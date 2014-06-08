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

(defprotocol ITraversable
  (path [c]
    "Return app path to this component."))

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
  "Return actual config for this component."
  [c app]
  (get (:config app) (last (path c))))

(defn component
  "Return actual component."
  [path app]
  nil)
