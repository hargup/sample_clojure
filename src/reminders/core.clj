(ns reminders.core
  (:gen-class)
  (:require [org.httpkit.server :as httpkit]
            [reminders.route :refer [routes]]
            [reminders.middleware :as mw]
            [reminders.config :as config]
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
   mw/wrap-json-response
   (mw/wrap-rate-limit (config/api-rate))))

(defn start-server! []
  (do
    (reset! server (httpkit/run-server (handler) {:port (config/port)}))
    (println "Server started at port: " (config/port))))

(defn stop-server! []
  (when-not (nil? @server)
    (@server :timeout 100)
    (reset! server nil)))

(defn restart-server! []
  (stop-server!)
  (start-server!))

(defn -main [& args]
  (start-server!))
