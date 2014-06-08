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
  "Return config map for this component."
  [c app]
  (get-in (:config app) (:path (meta c))))

(defn install
  "Install component into app."
  [app path create-component]
  (let [c (vary-meta (create-component app) assoc :path path)]
    (-> app
        (assoc-in path nil)
        (assoc-in path c)
        (assoc-in (into [:config] path) (setup c)))))

(defn install!
  "Shorhand for app in atom."
  [app-atom path create-component]
  (swap! app-atom install path create-component))
