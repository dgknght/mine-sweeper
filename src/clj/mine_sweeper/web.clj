(ns mine-sweeper.web
  (:require [clojure.pprint :refer [pprint]]
            [ring.util.response :as res]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.middleware.not-modified :refer [wrap-not-modified]]
            [ring.adapter.jetty :as jetty]
            [co.deps.ring-etag-middleware :as etag]
            [hiccup.page :refer [html5
                                 include-css
                                 include-js]]
            [hiccup.element :refer [javascript-tag]])
  (:gen-class :main true))

(defn- welcome
  [_req]
  (-> (html5
        [:head {:class "h-100"}
         [:meta {:charset "utf-8"}]
         [:meta {:name "viewport"
                 :content "width=device-width, initial-scale=1"}]
         (include-css "https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta1/dist/css/bootstrap.min.css"
                      "css/site.css")
         [:title "Dudes' Sweeper"]]
        [:body
         [:div#app "Loading..."]
         (include-js "https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta1/dist/js/bootstrap.bundle.min.js"
                     "js/app.js")
         (javascript-tag "mine_sweeper.core.main();")])
      res/response
      (res/content-type "text/html")))

(defn- wrap-logging
  [f]
  (fn [req]
    (let [res (f req)]
      (pprint {::req req
               ::res res})
      res)))

(defn- wrap-no-cache-header
  [handler]
  (fn  [req]
    (res/header  (handler req) "Cache-Control" "no-cache")))

(def app
  (-> welcome
      wrap-logging
      (wrap-resource "public")
      wrap-content-type
      wrap-no-cache-header
      etag/wrap-file-etag
      wrap-not-modified))

(defn -main
  [& _args]
  (let [port (or (first *command-line-args*) 8080)]
    (jetty/run-jetty app {:port port})))
