(ns zea.core)

(defprotocol IConfig
  "Configurable component."
  (config [c]
    "Returns config map with default values. This method is used
     to install configuration schema for this component."))

(defprotocol ILifecycle
  "Stateful component."
  (start [c]
    "Starts this component and returns initial state map.")
  (stop [c]
    "Stops this component and returns updated version of its state map."))

(defprotocol IActor
  "Actor model."
  (behavior [c message]
    "Process message."))

(extend-protocol IConfig
  java.lang.Object
  (config [c] nil))

(extend-protocol ILifecycle
  java.lang.Object
  (start [c] nil)
  (stop [c] nil))

(extend-protocol IActor
  java.lang.Object
  (behavior [c] nil))

(defprotocol IResponse
  "Ring handler."
  (response [c request]
    "Handle request and return response."))

(defn install!
  "Install component into app."
  [app path constructor]
  (let [c (-> (constructor app) (vary-meta assoc :path path))]
    (-> app
        (swap! assoc-in (cons :components path) c)
        (swap! assoc-in (cons :config path) (config c))
        (swap! assoc-in (cons :state path) (start c)))))

(defn get-config
  "Get config for the component."
  [c app]
  (get-in (:config @app) (:path (meta c))))

(defn get-state
  "Get actual state map of the component."
  [c app]
  (get-in (:state @app) (:path (meta c))))

(defn get-component
  "Find component"
  [path app]
  (get-in (:component @app) path))
