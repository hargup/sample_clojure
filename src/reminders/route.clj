(ns reminders.route
  (:require [reminders.handler :as handler]
            [reminders.util :as util]))

(defn routes []
  ["/reminders" {"" {:get handler/list-all
                     :post handler/create!}
                 ["/" [#"[0-9]+" :id]] {:get handler/list-reminder
                                        :put handler/update!
                                        :delete handler/delete!}
                 true (util/not-found-response)}])
