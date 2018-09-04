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
    (prn "request")
    (prn request)
    (prn "----")
    (let [response (handler request)]
      (prn "response")
      (prn response)
      (prn "----")
      response)))

(defn wrap-rate-limit [handler max-bucket]
  (let [bucket (atom 0)]
    (fn [request]
      (if (> @bucket max-bucket)
        {:status 429
         :body {:error "Too many requests"}}
        (do (swap! bucket inc)
            (let [response (handler request)]
              (swap! bucket dec)
              response))))))

(defn- decode-base-64 [text]
  (String. (.decode (java.util.Base64/getDecoder) text)))

(defn- revstr [string]
  (apply str (reverse string)))

(defn- verify-user [username password]
  ;; In real cases the hash of the password
  ;; will be matched again the hash stored in the database
  (= username (revstr password)))

(defn wrap-auth [handler]
  (fn [request]
    (if-let [auth-header (get-in request [:headers "authorization"])]
      (let [[type token] (str/split auth-header #" ")
            decoded-token (decode-base-64 token)
            [username password] (str/split decoded-token #":")]
        (if (verify-user username password)
          (handler request)
          {:status 403
           :body {:error "Invalid username or password"}}))
      {:status 403
       :body {:error "No authorization token provided"}})))
