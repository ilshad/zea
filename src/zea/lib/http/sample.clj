(ns zea.lib.http.sample
  (:require [zea.core :as zea]))

(defn hello
  "View component"
  [app]
  (reify
    
    zea/IResponse
    (response [_ _]
      {:body "Hello world!"})))

(defn hello-configurable
  "Configurable view component. Adds [`component_path...` :greeting]
   parameter into app's config, with default value 'Salut'"
  [app]
  (reify
    
    zea/IConfig
    (config [_]
      {:greeting "Salut"})
    
    zea/IResponse
    (response [e _]
      (str (zea/cfg e app :greeting) " world!"))))

(defn hello-stateful
  "View component with state."
  [app]
  (reify
    
    zea/IState
    (start [_]
      {:counter (atom 0)})
    
    zea/IResponse
    (response [e request]
      (let [counter (zea/state e app :counter)]
        (swap! counter inc)
        (str "Hello, " @counter "st")))))
