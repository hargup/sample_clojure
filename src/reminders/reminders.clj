(ns reminders.reminders
  (:require [reminders.spec :as spec]
            [clojure.spec.alpha :as s]
            [clj-time.format :as f]
            [clj-time.core :as t]
            [clj-time.coerce :as c]))

(defonce reminders (ref {} :validator #(s/valid? ::spec/reminders %)))

(def iso-formatter (f/formatter "YYYY-MM-dd'T'hh:mm:ssZ"))

(defn unix->iso [timestamp]
  ;; timestamp is second from epoch
  ;; clj-time expects milliseconds from epoch
  (f/unparse iso-formatter (c/from-long (* 1000 timestamp))))

(defn create! [{:keys [description scheduled_time]}]
  (dosync
   (let [id (inc (count @reminders))
         new-reminder {:id id
                   :description description
                   :scheduled_time scheduled_time}]
     (alter reminders assoc id new-reminder)
     new-reminder)))

(defn update! [params]
  (dosync
   (when-let [old-reminder (get @reminders (:id params))]
     (let [updated-reminder (merge old-reminder params)]
       (alter reminders assoc (:id params) updated-reminder)
       updated-reminder))))

(defn delete! [{:keys [id]}]
  (dosync
   (alter reminders dissoc id)))

(defn list-reminder [{:keys [id date_format] :or {date_format :unix}}]
  (let [reminder (get @reminders id)]
    (condp = date_format
      :unix reminder
      :iso8601 (update reminder :scheduled_time unix->iso)
      :default (throw (Exception. "Illegal date format")))))

(defn list-all
  ([] (list-all {}))
  ([{:keys [date_format] :or {date_format :unix}}]
   (let [all-reminders (vals @reminders)]
     (condp = date_format
       :unix all-reminders
       :iso8601 (map #(update % :scheduled_time unix->iso) all-reminders)
       :default (throw (Exception. "Illegal date format"))))))
