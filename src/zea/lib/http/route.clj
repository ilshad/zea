(ns zea.lib.http.route
  (:require [zea.core :as zea]
            [clojure.string :as string]))

(defn- lexer [s]
  (filter
    (complement empty?)
    (string/split s #"/")))

(defn- parser [tokens]
  (mapv
    (fn [string]
      (if-let [[_ v] (re-find #"^:(\w+)" string)]
        (keyword v)
        string))
    tokens))

(defn- routes-by-length [routes length]
  (filter
    (fn [[template _]]
      (= (count template) length))
    routes))

(defn- routes-by-segment [routes index segment]
  (filter
    (fn [[template _]]
      (let [token (template index)]
        (or (= token segment) (keyword? token))))
    routes))

(defn- find-match [routes uri]
  (reduce
    (fn [[routes index] segment]
      [(routes-by-segment routes index segment) (inc index)])
    [(routes-by-length routes (count uri)) 0]
    uri))

(defn- extract-params [template uri]
  (filter
    (complement nil?)
    (map #(when (keyword? %1) [%1 %2])
      template
      uri)))

(defn compiled-route-map [route-map]
  (mapv
    (fn [[a b]]
      [(-> a lexer parser) b])
    route-map))

(defn matcher [routes method raw-uri]
  (let [uri (lexer raw-uri)
        [found index] (find-match routes uri)
        [template methods->paths] (first found)]
    (if-let [path (get methods->paths method)]
      [path (into {} (extract-params template uri))]
      [nil {}])))

(defn route
  "URL Routing component for Zea.

   Example of route map:
   {'/doc/:id' {:get [:doc :view] :post [:doc :update]}}
   where :id is a route param and it will be merged into :params map
   in the request. [:doc :view] and [:doc :update] is the app paths
   to the components.

   Config:
     * :map - route-map: {string-pattern -> {request-method -> app-path}}

   State:
     * :map - 'compiled' version of route-map (some optimizations,
              less human readable)"
  [app]
  (reify
    
    zea/IConfig
    (config [_]
      {:map {"/" {:get [:http :index]}}})
    
    zea/IState
    (start [e]
      {:map (compiled-route-map (:map (zea/cfg e app)))})
    
    zea/IResponse
    (response [e req]
      (let [[path params] (matcher (:map (zea/state e app))
                                   (:request-method req)
                                   (:uri req))]
        (zea/response (zea/ear path app)
          (update-in req [:params] (partial merge params)))))))
