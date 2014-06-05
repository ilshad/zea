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
     * :handler - application path to handler (e.g. routing component),
                  default to [:route]

   State:
     * :stop - function that stops the server.
  "
  [app]
  (reify

    zea/IConfig
    (setup [_]
      {:ip "0.0.0.0"
       :port 8090
       :thread 4
       :queue-size 20480
       :max-body 8388608
       :max-ws 4194304
       :max-line 4096
       :handler [:route]})

    zea/ILifecycle
    (start [c]
      (let [conf (zea/config c app)
            handler (zea/component (:handler conf) app)
            stop (run-server (zea/handler handler) conf)]
        (assoc c :stop stop)))

    (stop [c]
      ((:stop c) :timeout 100)
      (dissoc c :stop))))
