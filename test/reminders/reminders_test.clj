(ns reminders.reminders-test
  (:require [reminders.reminders :as reminders]
            [clojure.test :refer :all]
            [reminders.fixtures :as fixtures]))

(use-fixtures :each fixtures/isolate-refs)


(deftest unix->iso-test
  (is (= (reminders/unix->iso 0) "1970-01-01T12:00:00+0000"))
  (is (= (reminders/unix->iso 946684800) "2000-01-01T12:00:00+0000")))

(deftest create-test
  (testing "User can create a reminder"
    (let [reminder {:description "First reminder" :scheduled_time 1490952963}]
      (is (= reminder (select-keys (reminders/create! reminder) [:description :scheduled_time]))))))

(deftest list-single-test
  (let [time 1490952963
        iso-time (reminders/unix->iso time)
        reminder {:description "First reminder" :scheduled_time time}
        id (:id (reminders/create! reminder))]
    (testing "User can get a single reminder"
      (is (= reminder (select-keys (reminders/list-reminder {:id id}) [:description :scheduled_time]))))
    (testing "User can get reminder with date in ISO8601 format"
      (is (= (assoc reminder :scheduled_time iso-time)
             (select-keys (reminders/list-reminder {:id id :date_format :iso8601})
                          [:description :scheduled_time]))))))

(deftest list-all-test
  (let [time-1 1490952963
        iso-time-1 (reminders/unix->iso time-1)
        reminder-1 {:description "First reminder" :scheduled_time time-1}
        time-2 1536044781
        iso-time-2 (reminders/unix->iso time-2)
        reminder-2 {:description "First reminder" :scheduled_time time-2}
        _ (reminders/create! reminder-1)
        _ (reminders/create! reminder-2)]
    (testing "User can get all the reminders"
      (is (= 2 (count (reminders/list-all))))
      (is (= reminder-1 (->
                         (reminders/list-all)
                         first
                         (select-keys [:description :scheduled_time])))))
    (testing "User can get reminder with date in ISO8601 format"
      (is (= 2 (count (reminders/list-all {:date_format :iso8601}))))
      (is (= (assoc reminder-1 :scheduled_time iso-time-1)
             (->
              (reminders/list-all {:date_format :iso8601})
              first
              (select-keys [:description :scheduled_time])))))))

(deftest update-test
  (testing "User can update a reminder"
    (let [reminder {:description "First reminder" :scheduled_time 1490952963}
          id (:id (reminders/create! reminder))
          new-description "Last reminder"]
      (is (= new-description (:description (reminders/update! {:id id :description new-description})))))))

(deftest delete-test
  (testing "User can delete a reminder"
    (let [reminder {:description "First reminder" :scheduled_time 1490952963}
          id (:id (reminders/create! reminder))
          _ (reminders/delete! {:id id})]
      (is (= 0 (count (reminders/list-all)))))))
