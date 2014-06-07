(ns zea.test.lib.http.test-route
  (:require [clojure.test :refer :all]
            [zea.lib.http.route :refer :all]))

(def route-map
  {"/"              [:http :root]
   "*"              [:http :not-found]
   "/foo/bar"       [:http :foo-bar]
   "/foo/:id/bar"   [:http :foo-?-bar]
   "/foo/bar/:id"   [:http :foo-bar-?]
   "/foo/:id"       [:http :foo-?]})

(deftest test-compiled-route-map
  (is (= (compiled-route-map route-map)
         [[["*"] [:http :not-found]]
          [["foo" :id] [:http :foo-?]]
          [["foo" "bar"] [:http :foo-bar]]
          [[] [:http :root]]
          [["foo" "bar" :id] [:http :foo-bar-?]]
          [["foo" :id "bar"] [:http :foo-?-bar]]])))

(deftest test-matcher
  (is (= (matcher (lexer "/foo/bar/42")
                  (compiled-route-map route-map))
         nil)))

(matcher (lexer "/foo/bar/42")
         (compiled-route-map route-map))
