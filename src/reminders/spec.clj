(ns reminders.spec
  (:require [clojure.spec.alpha :as s]))

(s/def ::id int?)

(s/def ::description string?)

(s/def ::scheduled_time int?)

(s/def ::reminder (s/keys :req-un [::id ::description ::scheduled_time]))

(s/def ::reminders (s/map-of ::id ::reminder))

(s/def ::create-params (s/keys :req-un [::description ::scheduled_time]))

(s/def ::update-params (s/keys :opt-un [::description ::scheduled_time]))
