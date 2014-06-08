(defproject zea "0.1.0-SNAPSHOT"
  :description ""
  :url "http://github.com/ilshad/zea"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/core.async "0.1.303.0-886421-alpha"]
                 [org.clojure/algo.monads "0.1.5"]
                 [http-kit "2.1.16"]]
  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[com.datomic/datomic-pro "0.9.4815"]]}})
