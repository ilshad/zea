(ns zea.lib.http.route
  (:require [zea.core :as zea]
            [clojure.string :as string]))

(defn- lexer [s]
  (filter (complement empty?) (string/split s #"/")))

(defn- parser [tokens]
  (mapv (fn [token]
          (if-let [[_ v] (re-find #"^:(\w+)" token)]
            (keyword v)
            token))
        tokens))

(defn- matcher [tokenized-uri routes]
  (reduce (fn [[index routes] segment]
            [(inc index)
             (filter (fn [[tokens path]]
                       (let [t (tokens index)]
                         (or (= segment t) (keyword? t))))
                     routes)])
          [0 routes]
          tokenized-uri))

(defn route
  "URL Routing component for Zea.

   Config:
     * :map - route-map where keys are patterns and values are
              component's paths.
              Default to {'/' [:http :root], '*' [:http :not-found]}

   State:
     * :map - 'compiled' version of route-map."
  [app]
  (reify

    zea/IConfig
    (setup [_]
      {:map {"/" [:http :root], "*" [:http :not-found]}})

    zea/ILifecycle
    (start [c]
      (assoc c :map (mapv (fn [[a b]] [(-> a lexer parser) b])
                          (:map (zea/config c app)))))
    
    (stop [c]
      (dissoc c :map))

    zea/IRoute
    (route [c req]
      (-> req :uri lexer (matcher (:map c)) last))

    zea/IHandler
    (handler [c]
      #(zea/component (zea/route c %) app))))
