(ns zea.core)

(defprotocol IConfig
  (config [e]))

(defprotocol IState
  (start [e])
  (stop [e]))

(defprotocol IActor
  (process [e message]))

(defprotocol IResponse
  (response [e request]))

(extend-protocol IConfig
  java.lang.Object
  (config [e] nil))

(extend-protocol IState
  java.lang.Object
  (start [e] nil)
  (stop [e] nil))

(extend-protocol IActor
  java.lang.Object
  (process [e message] nil))

(def ^:private sources-path :src)
(def ^:private config-path :cfg)
(def ^:private state-path :var)

(defn install! [app path constructor]
  (let [e (-> (constructor app) (vary-meta assoc :path path))]
    (swap! app assoc-in (cons sources-path path) e)
    (swap! app assoc-in (cons config-path path) (config e))
    (swap! app assoc-in (cons state-path path) (start e))))

(defn get-config
  ([e app]    (get-in (config-path @app) (-> e meta :path)))
  ([e app kw] (kw (get-config e app))))

(defn get-state
  ([e app]    (get-in (state-path @app) (-> e meta :path)))
  ([e app kw] (kw (get-state e app))))

(defn get-ear [path app] (get-in (sources-path @app) path))
