(ns zea.core)

(defprotocol IConfig
  (config [this]
    "Return map - config with default values."))

(defprotocol ILifecycle
  (start [this]
    "Start component and return an its updated version.")
  (stop [this]
    "Shut down component and return its updated version."))

(defprotocol IResponse
  (response [this request]
    "Take Ring request, return Ring response."))

(defprotocol ITraversable
  (key [this]
    "Returns keyword - component name within the application.")
  (path [this]
    "Returns full path to this component within the application."))

(defprotocol IRoute
  (route [this request]
    "Based on request data, return path to some component.")
  (handler [this]
    "Return Ring handler function that uses 'route' internally
     to dispatch request to another components."))

(defn component
  "Return actual component."
  [app path])
