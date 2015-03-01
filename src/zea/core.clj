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

(def ^:private path-src :src)
(def ^:private path-cfg :cfg)
(def ^:private path-var :var)

(defn install [app path constructor]
  (let [e (-> (constructor app) (vary-meta assoc :path path))]
    (swap! app assoc-in (cons path-src path) e)
    (swap! app assoc-in (cons path-cfg path) (config e))
    (swap! app assoc-in (cons path-var path) (start e))))

(defn cfg
  ([e app]    (get-in (path-cfg @app) (-> e meta :path)))
  ([e app kw] (kw (cfg e app))))

(defn state
  ([e app]    (get-in (path-var @app) (-> e meta :path)))
  ([e app kw] (kw (state e app))))

(defn ear [path app] (get-in (path-src @app) path))
