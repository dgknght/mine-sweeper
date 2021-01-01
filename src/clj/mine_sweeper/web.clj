(ns mine-sweeper.web
  (:require [ring.util.response :as res]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [hiccup.page :refer [html5
                                 include-css
                                 include-js]]))

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
         [:script {:type "javascript"} "mine_sweeper.core.main();"]])
      res/response
      (res/content-type "text/html")))

(def app
  (-> welcome
      wrap-content-type
      (wrap-resource "public")))
