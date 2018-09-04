(ns reminders.middleware
  (:require
   [clojure.string :as str]
   [clojure.data.json :as json]
   [clojure.spec.alpha :as s]
   [reminders.util :as util]))

(defn- parse-body [req-body]
  (let [body-text (-> req-body
                      (.bytes)
                      (slurp))]
    (prn "parsing")
    (prn body-text)
    (json/read-str body-text :key-fn keyword)))

(defn wrap-json-request [handler]
  (fn [request]
    (if-let [body (:body request)]
      (handler (assoc request :body (parse-body body)))
      (handler request))))

(defn wrap-json-response [handler]
  (fn [request]
    (let [response (handler request)]
      (merge response
             {:headers {"Content-Type" "application/json"}
              :body    (json/write-str (:body response))}))))

(defn validate-body [handler]
  (fn [request]
    (if-let [spec (:spec (meta handler))]
      (let [parsed-body (s/conform spec (:body request))]
        (if (= parsed-body ::s/invalid)
          (util/bad-request-response (s/explain-str parsed-body))
          (handler request)))
      (handler request))))

(defn wrap-log-request [handler]
  (fn [request]
    (prn "incoming request")
    (prn request)
    (prn "----")
    (handler request)))
