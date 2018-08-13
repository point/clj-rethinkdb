(defproject com.apa512/rethinkdb "1.0.0-SNAPSHOT3"
  :description "RethinkDB client"
  :url "http://github.com/apa512/clj-rethinkdb"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :codox {:metadata {:doc/format :markdown}
          :source-uri "https://github.com/apa512/clj-rethinkdb/blob/master/{filepath}#L{line}"}
  :global-vars {*warn-on-reflection* true}
  :plugins [[lein-codox "0.9.5"]
            [test2junit "1.1.2"]
            [lein-cljsbuild "1.1.3"]
            [lein-doo "0.1.7"]]
  :test2junit-output-dir ~(or (System/getenv "CIRCLE_TEST_REPORTS") "target/test2junit")
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.10.339" :scope "provided"]
                 [org.clojure/core.async "0.4.474"]
                 [org.clojure/tools.logging "0.4.0"]
                 [aleph "0.4.6"]
                 [gloss "0.2.6"]
                 [cheshire "5.8.0"]
                 [rethinkdb-protobuf "2.3.0"]
                 [com.google.protobuf/protobuf-java "3.6.1"]
                 [clj-time "0.14.4"]]
  :profiles {:dev {:resource-paths ["test-resources"]
                   :dependencies [[ch.qos.logback/logback-classic "1.1.7"]]}}
  :clean-targets ^{:protect false} [:target-path "out"]
  :cljsbuild {:builds [{:id "test"
                        :source-paths ["src" "test"]
                        :compiler     {:output-to     "target/clj-rethinkdb.js"
                                       :main rethinkdb.test-runner
                                       :optimizations :whitespace}}
                       {:id "advanced"
                        :source-paths ["src"]
                        :compiler {:output-to "target/clj-rethinkdb-adv.js"
                                   :optimizations :advanced}
                        :warning-handlers [(fn [warning-type env extra]
                                             (when (warning-type cljs.analyzer/*cljs-warnings*)
                                               (when-let [s (cljs.analyzer/error-message warning-type extra)]
                                                 (binding [*out* *err*]
                                                   (println "WARNING:" (cljs.analyzer/message env s)))
                                                 (System/exit 1))))]}]}
  :jvm-opts ["-Xmx512m"]
  :deploy-repositories [["releases" :clojars]
                        ["snapshots" :clojars]])
