(defproject mine-sweeper "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.9.946"]
                 [reagent "0.7.0"]
                 [ring/ring-core "1.8.2"]
                 [ring/ring-jetty-adapter "1.8.2"]
                 [hiccup "1.0.5"]]

  :dev-dependencies [[lein/ring-devel "1.8.2"]]

  :min-lein-version "2.5.3"

  :source-paths ["src/clj" "src/cljc"]

  :plugins [[lein-cljsbuild "1.1.4"]
            [lein-ring "0.12.5"]]

  :clean-targets ^{:protect false} ["resources/public/js"
                                    "target"]

  :ring {:handler mine-sweeper.web/app}

  :figwheel {:css-dirs ["resources/public/css"]}

  :profiles
  {:dev
   {:dependencies []

    :plugins      [[lein-figwheel "0.5.15"]]}}

  :cljsbuild
  {:builds
   [{:id           "dev"
     :source-paths ["src/cljs"]
     :figwheel     {:on-jsload "mine-sweeper.core/reload"}
     :compiler     {:main                 mine-sweeper.core
                    :optimizations        :none
                    :output-to            "resources/public/js/app.js"
                    :output-dir           "resources/public/js/dev"
                    :asset-path           "js/dev"
                    :source-map-timestamp true}}

    {:id           "min"
     :source-paths ["src/cljs"]
     :compiler     {:main            mine-sweeper.core
                    :optimizations   :advanced
                    :output-to       "resources/public/js/app.js"
                    :output-dir      "resources/public/js/min"
                    :elide-asserts   true
                    :closure-defines {goog.DEBUG false}
                    :pretty-print    false}}]})
