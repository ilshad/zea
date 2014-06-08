(ns zea.core)

(defprotocol IConfig
  "Configurable component."
  (config [c]
    "Returns config map with default values. This method is used
     to install configuration schema for this component."))

(defprotocol ILifecycle
  "Stateful component."
  (start [c]
    "Starts this component and returns initial state in map.")
  (stop [c state-map]
    "Stops down this component and returns updated version of
     its state map."))

(defprotocol IHandler
  "Ring handler constructor."
  (handler [c]
    "Return Ring handler function."))

(defprotocol IResponse
  "Ring handler."
  (response [c request]
    "Handle request and return response."))

(defprotocol IRoute
  "HTTP routing."
  (route [c request]
    "Look at request data and return associative data structure:
       * :path - app path (vector) to some component,
       * :params - routing params (map)."))

(defn get-config [c app]
  (get-in (:config app) (:path (meta c))))

(defn get-state [c app]
  (get-in (:state app) (:path (meta c))))

(defn- try-config [c]
  (try (config c) (catch java.lang.IllegalArgumentException e)))

(defn install! [app-atom path create-component]
  (let [c (-> (create-component app-atom)
              (vary-meta assoc :path path))]
    (swap! app-atom assoc-in path c)
    (when-let [cfg (try-config c)]
      (swap! app-atom assoc-in (into [:config] path) cfg))))

