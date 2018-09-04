(ns reminders.util)

(defn forbidden-response []
  {:status 403
   :body {:error "Forbidden"}})

(defn bad-request-response [error]
  {:status 401
   :body {:error error}})

(defn not-found-response []
  {:status 404
   :body {:error "Resource not found"}})
