(ns reminders.core
  (:gen-class)
  (:require [org.httpkit.server :as httpkit]
            [reminders.route :refer [routes]]
            [reminders.middleware :as mw]
            [ring.middleware.params :refer [wrap-params]]
            [reminders.reminders :as reminders]
            [bidi.ring :refer [make-handler]]))

(defonce server (atom nil))

(defn handler []
  (->
   (routes)
   make-handler
   mw/wrap-log-request
   wrap-params
   mw/wrap-json-request
   mw/wrap-json-response))

(defn start-server! []
  (let [port 3636]
    (reset! server (httpkit/run-server (handler) {:port port}))
    (println "Server started at port: " port)))

(defn stop-server! []
  (when-not (nil? @server)
    (@server :timeout 100)
    (reset! server nil)))

(defn restart-server! []
  (stop-server!)
  (start-server!))

(defn -main [& args]
  (start-server!))
