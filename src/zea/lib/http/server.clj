(ns zea.lib.http.server
  (:require [zea.core :as zea]
            [org.httpkit.server :refer [run-server]]))

(defn server
  "HTTP Server component based on HTTP Kit (http://http-kit.org).

   Config:
     * :ip - which IP to bind, default to 0.0.0.0
     * :port - which port listens incoming request, default to 8090
     * :thread - how many threads to compute response, default to 4
     * :queue-size - max requests queued waiting for threadpool
                     to compute response before reject, default to 20K
     * :max-body - length limit for request body in bytes, default to 8M
     * :max-ws - max websocket message size, default to 4M
     * :max-line - length limit for HTTP inital line and per header,
                   default to 4K
     * :route-path - application path to routing component (which
                     implements IRoute). Default to [:route]

   State:
     * :stop - function that stops the server.
  "
  [app]
  (reify

    zea/IConfig
    (config [_]
      {:ip "0.0.0.0"
       :port 8090
       :thread 4
       :queue-size 20480
       :max-body 8388608
       :max-ws 4194304
       :max-line 4096
       :route-path [:route]})

    zea/ILifecycle
    (start [this]
      (let [route (zea/component app (-> this zea/config :route-path))]
        (assoc this :stop (run-server (zea/handler route) (zea/config this)))))

    (stop [this]
      ((:stop this) :timeout 100)
      (dissoc this :stop))))
