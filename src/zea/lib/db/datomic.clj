(ns zea.lib.db.datomic
  (:require [zea.core :as zea]
            [datomic.api :as d]
            [clojure.java.io :as io]))

(defn datomic
  "Datomic component.

   Config:
     * :uri - database uri, default to 'datomic:mem://example'
     * :schema - file name with database schema, default to 'schema.edn'

   State:
     * :conn - database connection."
  [app]
  (reify

    zea/IConfig
    (config [_]
      {:uri "datomic:mem://zea"
       :schema "schema.edn"})

    zea/IState
    (start [e]
      (let [conf (zea/get-config e @app)
            filename (:schema conf)
            uri (:uri conf)
            created (d/create-database uri)
            conn (d/connect uri)]
        (when created
          @(d/transact conn
             (-> filename io/resource slurp read-string)))
        {:conn conn}))
    
    (stop [e]
      (d/release (:conn (zea/get-state e app)))
      nil)))
