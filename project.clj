(defproject zea "0.1.0-SNAPSHOT"
  :description ""
  :url "http://github.com/ilshad/zea"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0-alpha4"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [org.clojure/algo.monads "0.1.5"]
                 [com.datomic/datomic-free "0.9.5078" :exclusions [joda-time]]
                 [http-kit "2.1.16"]]
  :profiles {:dev {:source-paths ["dev"]}})
