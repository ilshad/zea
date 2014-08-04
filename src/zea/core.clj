(ns zea.core)

(defprotocol IConfig
  "Configurable ear."
  (config [e]
    "Returns config map with default values. This method is used
     to install configuration schema for this ear."))

(extend-protocol IConfig
  java.lang.Object
  (config [e] nil))

(defprotocol IState
  "Stateful ear."
  (start [e]
    "Starts this ear and returns initial state map.")
  (stop [e]
    "Stops this ear and returns updated version of its state map."))

(extend-protocol IState
  java.lang.Object
  (start [e] nil)
  (stop [e] nil))

(defprotocol IActor
  "Ear that behaves like an actor."
  (process [e message]
    "Process message."))

(extend-protocol IActor
  java.lang.Object
  (process [e message] nil))

(defprotocol IResponse
  "Ring handler."
  (response [e request]
    "Handle request and return response."))

(defn install!
  "Install ear into app."
  [app path constructor]
  (let [e (-> (constructor app) (vary-meta assoc :path path))]
    (-> app
        (swap! assoc-in (cons :source path) e)
        (swap! assoc-in (cons :config path) (config e))
        (swap! assoc-in (cons :state path) (start e)))))

(defn get-config
  "Get config for the ear."
  [e app]
  (get-in (:config @app) (:path (meta e))))

(defn get-state
  "Get actual state map of the ear."
  [e app]
  (get-in (:state @app) (:path (meta e))))

(defn f
  "Get ear."
  [path app]
  (get-in (:source @app) path))
