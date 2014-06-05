(ns zea.lib.http.route
  (:require [zea.core :as zea]
            [clojure.string :as string]))

(defn lexer [pattern]
  (->> (string/split pattern #"/")
       (filter (complement empty?))
       (mapv #(if-let [[_ v] (re-find #"^:(\w+)" %)] (keyword v) %))))

(defn route
  "Default routing component for Zea.

   Config:
     * :route-map - route map where keys are component paths
                    and values are patterns. Default to
                    {[:http :root] '/'
                     [:http :not-found] '*'}"
  [app]
  (reify
    zea/IConfig
    (config [_]
      {:route-map {[:http :root] "/"
                   [:http :not-found] "*"}})
    zea/IRoute
    (route [this request]
      (let [key (zea/key this)
            route-map (-> app :config key :route-map)
            matcher (-> request :url parse-url compile-matcher)]
        (matcher route-map)))
    (handler [this]
      (fn [request]
        (zea/component app (zea/route this request))))))
