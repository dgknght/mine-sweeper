(ns mine-sweeper.core
  (:require [clojure.string :as string]
            [reagent.core :as r]
            [mine-sweeper.bootstrap :as bs]
            [mine-sweeper.forms :as forms]
            [mine-sweeper.game :as ms]))

(defonce app-state
  (r/atom {:specs {:height 5
                   :width 5}}))

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

(defn- game-cell-content
  [cell {:keys [result]}]
  (cond
    (:dug cell)
    (:adjacent cell)

    (:flagged cell)
    (if (and result
             (:mine cell))
      (bs/icon :bomb)
      (bs/icon :flag-fill))

    (:exploded cell)
    "*"

    result
    (when (:mine cell) (bs/icon :bomb))))

(defn- game-cell
  [cell {:keys [result] :as game} state]
  (let [index (-> cell meta :index)]
    ^{:key (str "game-cell-" index)}
    [:div.d-flex.align-items-center.justify-content-center
     (cond-> {:class (cell-class cell result)}
       (nil? result) (assoc :on-click #(handle-click % index state)))
     (game-cell-content cell game)]))

(defn- game-row
  [row game state]
  ^{:key (str "game-row-" (:index (meta (first row))))}
  [:div.d-flex
   (doall (map #(game-cell % game state) row))])

(defn- render-board
  [state]
  (let [game (r/cursor state [:game])]
    (fn []
      (when @game
        [:div
         (doall (map #(game-row % @game state)
                     (:board @game)))]))))

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
  (let [specs (r/cursor state [:specs])]
    (fn []
      [:div.container
       [:h1.mt-5 "Mine Sweeper"]
       [render-board state]
       [render-result state]
       [:div.mt-3.row
        [:div.col-md-6
         [:form.row {:no-validate true
                     :on-submit (fn [e]
                                  (.preventDefault e)
                                  (swap! state
                                         assoc
                                         :game
                                         (ms/create @specs)))}
          [:div.col-sm-4
           [forms/integer-input specs [:width]]]
          [:div.col-sm-1.text-center "X"]
          [:div.col-sm-4
           [forms/integer-input specs [:height]]]
          [:div.col-sm-3
           [:button.btn.btn-primary
            {:type :submit}
            "New Game"]]]]]])))

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
