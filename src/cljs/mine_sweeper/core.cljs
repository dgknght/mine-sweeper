(ns mine-sweeper.core
  (:require [clojure.string :as string]
            [reagent.core :as r]
            [mine-sweeper.game :as ms]))

(defonce app-state
  (r/atom {}))

(defn- handle-click
  [e index state]
  (let [f (if (.-ctrlKey e)
            ms/flag
            ms/dig)]
    (swap! state
           update-in
           [:game]
           f
           index)))

(defn- cell-class
  [cell result]
  (string/join " "
               (->> [:dug :exploded]
                    (filter #(% cell))
                    (map name)
                    (concat
                      ["cell"
                       result]))))

(defn- game-cell
  [cell state]
  (let [index (-> cell meta :index)
        result (r/cursor state [:game :result])]
    ^{:key (str "game-cell-" index)}
    [:div (cond-> {:class (cell-class cell @result)}
            (nil? @result) (assoc :on-click #(handle-click % index state)))
     (cond
       (:dug cell)      (:adjacent cell)
       (:flagged cell)  "F"
       (:exploded cell) "*")]))

(defn- game-row
  [row state]
  ^{:key (str "game-row-" (:index (meta (first row))))}
  [:div.d-flex
   (doall (map #(game-cell % state) row))])

(defn- render-board
  [state]
  (let [game (r/cursor state [:game])]
    (fn []
      (when @game
        [:div
         (doall (map #(game-row % state) (:board @game)))]))))

(defn- render-result
  [state]
  (let [result (r/cursor state [:game :result])]
    (fn []
      (when @result
        [:div.result {:class @result}
         (if (= :lose @result)
           "You Lose!"
           "You Win!")]))))

(defn- page
  [state]
  (fn []
    [:div.container
     [:h1.mt-5 "Mine Sweeper"]
     [render-board state]
     [render-result state]
     [:div.mt-3
      [:button.btn.btn-primary {:on-click #(swap! state
                                                  assoc
                                                  :game
                                                  (ms/create {:width 5
                                                              :height 5}))}
       "New Game"]]]))

(defn dev-setup []
  (when ^boolean js/goog.DEBUG
    (enable-console-print!)
    (println "dev mode")))

(defn reload []
  (r/render [page app-state]
                  (.getElementById js/document "app")))

(defn ^:export main []
  (dev-setup)
  (reload))
