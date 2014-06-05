(ns zea.lib.http.route
  (:require [zea.core :as zea]
            [clojure.string :as string]))

(defn lexer [pattern]
  (->> (string/split pattern #"/")
       (filter (complement empty?))
       (mapv #(if-let [[_ v] (re-find #"^:(\w+)" %)] (keyword v) %))))

(defn route
  "Simple routing component for Zea.

   Config:
     * :route-map - route map where keys are patterns and values are
                    component's paths. Default to {'/' [:http :root] 
                                                   '*' [:http :not-found]}

   State:
     * :route-map - complied version of route-map."
  [app]
  (reify

    zea/IConfig
    (setup [_]
      {:route-map {[:http :root] "/"
                   [:http :not-found] "*"}})

    zea/ILifecycle
    (start [c]
      (assoc c :route-map (map (fn [[a b]] [(lexer a) b])
                               (:route-map (zea/config c app)))))

    (stop [c]
      (dissoc c :route-map))
    
    zea/IRoute
    (route [c request]
      nil)

    zea/IHandler
    (handler [c]
      (fn [request]
        (zea/component (zea/route c request) app)))))
