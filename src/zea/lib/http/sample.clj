(ns zea.lib.http.sample
  (:require [zea.core :as zea]))

(defn hello
  "View component"
  [app]
  (reify
    
    zea/IResponse
    (response [_ _]
      (str "Hello world!"))))

(defn hello-configurable
  "Configurable view component. Adds [`component_path...` :greeting]
   parameter into app's config, with default value 'Salut'"
  [app]
  (reify
    
    zea/IConfig
    (config [_]
      {:greeting "Salut"})
    
    zea/IResponse
    (response [c _]
      (str (:greeting (zea/get-config c @app)) " world!"))))

(defn hello-stateful
  "View component with state."
  [app]
  (reify
    
    zea/ILifecycle
    (start [c]
      {:counter (atom 0)})
    
    zea/IResponse
    (response [c request]
      (let [counter (:counter (zea/get-state c app))]
        (swap! counter inc)
        (str "Hello, " @counter "st")))))
