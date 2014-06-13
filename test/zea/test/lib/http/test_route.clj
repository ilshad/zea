(ns zea.test.lib.http.test-route
  (:require [clojure.test :refer :all]
            [zea.lib.http.route :refer :all]))

(def route-map
  {"/"               {:get [:http :root]}
   "/foo"            {:get [:http :foo-get] :post [:http :foo-post]}
   "/foo/:id/bar"    {:get [:http :foo-?-bar]}
   "/foo/bar/:id"    {:get [:http :foo-bar-?]}
   "/foo/bar/:a/:b"  {:get [:http :foo-bar-?-?]}
   "/foo/:id"        {:get [:http :foo-?]}})

(deftest test-matcher
  (is (= (matcher (compiled-route-map route-map) :get "/")
         [[:http :root] {}]))
  (is (= (matcher (compiled-route-map route-map) :get "/foo")
         [[:http :foo-get] {}]))
  (is (= (matcher (compiled-route-map route-map) :post "/foo")
         [[:http :foo-post] {}]))
  (is (= (matcher (compiled-route-map route-map) :get "/foo/bar/42")
         [[:http :foo-bar-?] {:id "42"}]))
  (is (= (matcher (compiled-route-map route-map) :get "/foo/bar/42/77")
         [[:http :foo-bar-?-?] {:a "42" :b "77"}])))


