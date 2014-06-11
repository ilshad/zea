(ns zea.core
  (:require [clojure.core.async :refer [go-loop chan <! >!]]
            [clojure.algo.monads :refer :all]))

(defprotocol IConfig
  "Configurable component."
  (config [c]
    "Returns config map with default values. This method is used
     to install configuration schema for this component."))

(defprotocol ILifecycle
  "Stateful component."
  (start [c]
    "Starts this component and returns initial state map.")
  (stop [c]
    "Stops this component and returns updated version of its state map."))

(defprotocol IActor
  "Actor model."
  (behavior [c message]
    "Process message."))

(extend-protocol IConfig
  java.lang.Object
  (config [c] nil))

(extend-protocol ILifecycle
  java.lang.Object
  (start [c] nil)
  (stop [c] nil))

(extend-protocol IActor
  java.lang.Object
  (behavior [c] nil))

(defprotocol IHandler
  "Ring handler constructor."
  (handler [c]
    "Return Ring handler function."))

(defprotocol IResponse
  "Ring handler."
  (response [c request]
    "Handle request and return response."))

(defprotocol IRoute
  "HTTP routing."
  (route [c request]
    "Look at request data and return associative data structure:
       * :path - app path (vector) to some component,
       * :params - routing params (map)."))

(defn get-config
  "Get config for the component."
  [c app]
  (get-in (:config app) (:path (meta c))))

(defn get-state
  "Get actual state map of the component."
  [c app]
  (get-in (:state app) (:path (meta c))))

(defn install!
  "Install component into map which contains whole application."
  [app path create-component]
  (let [c (-> (create-component app)
              (vary-meta assoc :path path))]
    (-> app
        (swap! assoc-in (cons :main path) c)
        (swap! assoc-in (cons :config path) (config c))
        (swap! assoc-in (cons :state path) (start c)))))

(defmacro compose-state [& body]
  `(with-monad
     state-m
     (m-seq [~@body])))
