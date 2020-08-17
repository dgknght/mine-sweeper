(ns mine-sweeper.core
  (:require [reagent.core :as r]
            [mine-sweeper.game :as ms]))

(defonce app-state
  (r/atom {}))

(defn- game-cell
  [cell state]
  (let [index (-> cell meta :index)]
    ^{:key (str "game-cell-" index)}
    [:div.cell {:class (when (:dug cell) "dug")
                :on-click #(swap! state update-in [:game] ms/dig index)}
     (when (:dug cell)
       (:adjacent cell))]))

(defn- game-row
  [row state]
  ^{:key (str "game-row-" (:index (meta (first row))))}
  [:div.d-flex
   (map #(game-cell % state) row)])

(defn- render-board
  [state]
  (let [game (r/cursor state [:game])]
    (fn []
      (when @game
        [:div
         (map #(game-row % state) (:board @game))]))))

(defn- page
  [state]
  (fn []
    [:div.container
     [:h1.mt-5 "Mine Sweeper"]
     [render-board state]
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
