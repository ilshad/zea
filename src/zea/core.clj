(ns zea.core)

(defprotocol IConfig
  "Configurable component."
  (config [c]
    "Return config map with default values. This method is used
     to install configuration schema for this component."))

(defprotocol ILifecycle
  "Stateful component."
  (start [c]
    "Start this component and return its updated version.")
  (stop [c]
    "Shut down this component and return its updated version."))

(defprotocol IHandler
  "Ring handler constructor."
  (handler [c]
    "Return Ring handler function."))

(defprotocol IResponse
  "Ring handler."
  (response [c request]
    "Take Ring request, return Ring response."))

(defprotocol IRoute
  "HTTP routing."
  (route [c request]
    " Look at request data and return map with keys:
       * :path - app path to some component,
       * :params - route params, map."))

(defn get-config [component app]
  (get-in (:config app) (:path (meta component))))

(defn- try-config [c]
  (try (config c) (catch java.lang.IllegalArgumentException e)))

(defn install! [app-atom path create-component]
  (let [c (-> (create-component app-atom)
              (vary-meta assoc :path path))]
    (swap! app-atom assoc-in path c)
    (when-let [cfg (try-config c)]
      (swap! app-atom assoc-in (into [:config] path) cfg))))
