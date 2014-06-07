(ns zea.test.lib.http.test-route
  (:require [clojure.test :refer :all]
            [zea.lib.http.route :refer :all]))

(def route-map
  {"/"               [:http :root]
   "*"               [:http :not-found]
   "/foo/bar"        [:http :foo-bar]
   "/foo/:id/bar"    [:http :foo-?-bar]
   "/foo/bar/:id"    [:http :foo-bar-?]
   "/foo/bar/:a/:b"  [:http :foo-bar-?-?]
   "/foo/:id"        [:http :foo-?]})

(deftest test-matcher
  (is (= (matcher (compiled-route-map route-map) "/")
         {:path [:http :root], :params {}}))
  (is (= (matcher (compiled-route-map route-map) "/foo/bar/42")
         {:path [:http :foo-bar-?], :params {:id "42"}}))
  (is (= (matcher (compiled-route-map route-map) "/foo/bar/42/77")
         {:path [:http :foo-bar-?-?], :params {:a "42" :b "77"}})))


