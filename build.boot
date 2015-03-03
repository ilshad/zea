(set-env!
  :source-paths #{"src"}
  :dependencies '[[http-kit "2.1.16"]
                  [prismatic/plumbing "0.3.7"]
                  [prismatic/schema "0.3.7"]
                  [com.datomic/datomic-free "0.9.5078"]
                  [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                  [org.clojure/algo.monads "0.1.5"]])
