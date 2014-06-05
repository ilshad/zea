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
      {:uri "datomic:mem://example"
       :schema "schema.edn"})

    zea/ILifecycle
    (start [this]
      (let [key (zea/key this)
            uri (-> app :config key :uri)
            created (d/create-database uri)
            conn (d/connect uri)]
        (when created
          @(d/transact conn (-> app :resources key :schema
                                io/resource slurp read-string)))
        (assoc this :conn conn)))

    (stop [this]
      (d/release (:conn this))
      (dissoc this :conn))))
