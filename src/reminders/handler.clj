(ns reminders.handler
  (:require
   [reminders.reminders :as reminders]
   [reminders.util :as util]
   [reminders.spec :as spec]
   [clojure.spec.alpha :as s]))

;; Assuming request body confirms the spec, will check fo rthe spec later
;; Need to parse schedule_time
(defn create! [request]
  (let [reminder (reminders/create! (:body request))]
    {:status 201
     :body reminder}))

;; Should validate route params in a middleware
(defn update! [request]
  (let [id (Integer. (get-in request [:route-params :id]))
        reminder (reminders/update! (assoc (:body request)
                                           :id id))]
    {:status 200
     :body reminder}))

(defn delete! [request]
  (let [id (Integer. (get-in request [:route-params :id]))]
    (do
      (reminders/delete! {:id id})
      {:status 200
       :body {:deleted true}})))

(defn list-reminder [{:keys [query-params route-params]}]
  (let [id (Integer. (get route-params :id))
        date_format (keyword (get query-params "date_format"))]
    (if-let [reminder (if date_format
                        (reminders/list-reminder {:id id
                                                  :date_format date_format})
                        (reminders/list-reminder {:id id}))]
      {:status 200
       :body reminder}
      {:status 404
       :body "Reminder not found"})))

(defn list-all [{:keys [query-params]}]
  (if-let [date_format (keyword (get query-params "date_format"))]
    {:status 200
     :body (reminders/list-all {:date_format date_format})}
    {:status 200
     :body (reminders/list-all)}))
