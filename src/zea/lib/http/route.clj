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
      {:path path :params (into {} (extract-params template uri))}
      {:path nil  :params {}})))

(defn route
  "URL Routing component for Zea.

   Config:
     * :map - route-map: {string-pattern -> {request-method -> app-path}}

   State:
     * :map - 'compiled' version of route-map (some optimizations,
              less human readable)"
  [app]
  (reify

    zea/IConfig
    (setup [_]
      {:map {"/" {:get [:http :hello]}}})

    zea/ILifecycle
    (start [c]
      {:map (compiled-route-map (:map (zea/get-config c @app)))})

    zea/IRoute
    (route [c req]
      (matcher (:map (zea/get-state c @app))
               (:request-method req)
               (:uri req)))

    zea/IHandler
    (handler [c]
      (fn [req]
        (let [{:keys [path params]} (zea/route c req)]
          (zea/response (get-in @app path)
                        (update-in req [:params] (partial merge params))))))))
