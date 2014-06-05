(ns zea.core)

(defprotocol IConfig
  (config [this]
    "Return map - config with default values."))

(defprotocol ILifecycle
  (start [this]
    "Start component and return its updated version.")
  (stop [this]
    "Shut down component and return its updated version."))

(defprotocol IResponse
  (response [this request]
    "Take Ring request, return Ring response."))

(defprotocol ITraversable
  (path [this]
    "Returns full path to this component within the application."))

(defprotocol IRoute
  (route [this request]
    "Look at request data and return path to some component."))

(defprotocol IHandler
  (handler [this]
    "Return Ring handler function."))

(defn component
  "Return actual component."
  [app path])
