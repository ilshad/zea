(ns zea.core)

(defprotocol IConfig
  (config [e]))

(defprotocol IState
  (start [e])
  (stop [e]))

(defprotocol IResponse
  (response [e request]))

(defn install [constructor path app]
  (let [e (-> (constructor app) (vary-meta assoc :path path))]
    (swap! app assoc-in (cons :src path) e)
    (when (satisfies? IConfig e)
      (swap! app assoc-in (cons :cfg path) (config e)))
    (when (satisfies? IState e)
      (swap! app assoc-in (cons :var path) (start e)))))

(defn cfg
  ([e app]    (get-in (:cfg @app) (-> e meta :path)))
  ([e app kw] (kw (cfg e app))))

(defn state
  ([e app]    (get-in (:var @app) (-> e meta :path)))
  ([e app kw] (kw (state e app))))

(defn ear [path app]
  (get-in (:src @app) path))

(defn restart [path app]
  (let [e (ear path app)]
    (swap! app assoc-in (cons :cfg path) (config e))))
