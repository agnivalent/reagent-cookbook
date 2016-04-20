(defproject file-upload "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.122"]
                 [reagent "0.5.1"]
                 [ring "1.4.0"]
                 [ring/ring-defaults "0.2.0"]
                 [ring/ring-json "0.4.0"]
                 [ring-middleware-format "0.7.0"]
                 [compojure "1.5.0"]
                 [cljs-ajax "0.5.3"]]

  :source-paths ["src/clj"]

  :main file-upload.handler

  :plugins [[lein-cljsbuild "1.0.6"]
            [lein-figwheel "0.5.2"]
            [lein-ring "0.9.7"]]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target" "test/js"]

  :ring {:handler file-upload.handler/app}

  :cljsbuild {:builds [{:id "prod"
                        :source-paths ["src/cljs"]
                        :compiler {:output-to "resources/public/js/compiled/app.js"
                                   :optimizations :advanced
                                   :externs ["react/externs/react.js"]
                                   :pretty-print false}}]})
