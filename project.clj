(defproject reminders "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [clj-time "0.14.4"]
                 [bidi "2.1.3"]
                 [http-kit "2.2.0"]
                 [org.clojure/data.json "0.2.6"]
                 [ring/ring-core "1.7.0-RC2"]]
  :main ^:skip-aot reminders.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
