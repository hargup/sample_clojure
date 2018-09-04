(ns reminders.fixtures
  (:require  [clojure.test :as t]
             [reminders.reminders :as reminders]))

(defn isolate-refs [f]
  (with-redefs [reminders/reminders (ref {})]
    (f)))
