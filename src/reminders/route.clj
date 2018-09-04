(ns reminders.route
  (:require [reminders.handler :as handler]))

(defn routes []
  ["/reminders" {"" {:get handler/list-all
                     :post handler/create!}
                 ["/" :id] {:get handler/list-reminder
                            :put handler/update!
                            :delete handler/delete!}}])
