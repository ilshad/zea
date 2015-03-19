(ns zea.tools.pprint
  (:require [zea.core :as zea]
            [clojure.pprint :as p]
            [clojure.string :as string]))

(defn- format-class [object]
  (let [[ns s] (-> object class .getName str (string/split #"\$"))]
    (str ns "/" s)))

(defn- print-unit [unit]
  ((p/formatter-out "#zea/unit ~w")
   (format-class unit)))

(defn- dispatch [x]
  (if (zea/unit? x)
    (print-unit x)
    (p/simple-dispatch x)))

(defn pprint [app]
  (p/with-pprint-dispatch dispatch
    (p/pprint @app)))
