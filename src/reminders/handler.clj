(ns reminders.handler
  (:require
   [reminders.reminders :as reminders]
   [reminders.util :as util]
   [reminders.spec :as spec]
   [clojure.spec.alpha :as s]))

(defn create! [{:keys [body]}]
  (if (s/valid? ::spec/create-params body)
    (let [reminder (reminders/create! body)]
      {:status 201
       :body reminder})
    {:status 400
     :body {:error "Invalid arguments"}}))

(defn update! [{:keys [route-params body]}]
  (if (s/valid? ::spec/update-params body)
    (let [id (Integer. (get route-params :id))
          reminder (reminders/update! (assoc body :id id))]
      (if reminder
        {:status 200
         :body reminder}
        {:status 404
         :body "Reminder not found"}))
    {:status 400
     :body {:error "Invalid arguments"}}))

(defn delete! [{:keys [route-params]}]
  (let [id (Integer. (get route-params :id))]
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
       :body {:reminder reminder}}
      {:status 404
       :body "Reminder not found"})))

(defn list-all [{:keys [query-params]}]
  (if-let [date_format (keyword (get query-params "date_format"))]
    {:status 200
     :body {:reminders (reminders/list-all {:date_format date_format})}}
    {:status 200
     :body {:reminders (reminders/list-all)}}))
