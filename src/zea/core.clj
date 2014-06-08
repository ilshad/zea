(ns zea.core)

(defprotocol IConfig
  (setup [c]
    "Return map - config with default values.
     Used to install configuration schema for this component."))

(defprotocol ILifecycle
  (start [c]
    "Start this component and return its updated version.")
  (stop [c]
    "Shut down this component and return its updated version."))

(defprotocol IHandler
  (handler [c]
    "Return Ring handler function."))

(defprotocol IRoute
  (route [c request]
    "Look at request data and return map with keys:
     * :path - app path to some component,
     * :params - route params, map."))

(defprotocol IResponse
  (response [c request]
    "Take Ring request, return Ring response."))

(defn config
  "Return config of component."
  [component app]
  (get-in (:config app) (:path (meta component))))

(defn install!
  "Install component into app."
  [app-atom path create-component]
  (let [c (-> (create-component app-atom)
              (vary-meta assoc :path path))]
    (swap! app-atom assoc-in path c)
    (swap! app-atom assoc-in (into [:config] path) (setup c))))
