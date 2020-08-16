(ns mine-sweeper.game-test
  (:require [clojure.test :refer [deftest is testing]]
            [mine-sweeper.game :as game]))

(deftest create-a-game
  (let [{:keys [board]} (game/create {:width 4
                                      :height 3
                                      :density 0.33})]
    (is (= 3 (count board))
        "A board is created with the specified number of rows (height)")
    (is (apply = 4 (map count board))
        "A board is created with the specified number of columns (width)")
    (is (= 4 (->> (apply concat board)
                  (filter :mine)
                  (count)))
        "The number of mines is equal to the density times the cell count")))

(def ^:private adjacency-tests
  [{:options {:width 3
              :height 3
              :mine-indices [0]}
    :expected [nil 1   nil
               1   1   nil
               nil nil nil]}
   {:options {:width 3
              :height 3
              :mine-indices [1]}
    :expected [1   nil 1
               1   1   1
               nil nil nil]}
   {:options {:width 3
              :height 3
              :mine-indices [2]}
    :expected [nil 1   nil 
               nil 1   1
               nil nil nil]}])

(deftest set-adjacencies
  (doseq [{:keys [options expected]} adjacency-tests]
    (let [{:keys [board]} (game/create options)]
      (testing (format "%s x %s with mines at %s"
                       (:width options)
                       (:height options)
                       (:mine-indices options))
        (is (= expected
               (map :adjacent (apply concat board)))
            "The adjacent values are correct")))))

(deftest dig-up-a-cell
  (is (= {:board [[{:dug true} {}]
                  [{} {}]]}
         (game/dig {:board [[{} {}]
                            [{} {}]]}
                   [0 0]))
      "Digging up a cell without a mine does not end the game")
  (is (= {:board [[{:exploded true} {}]
                  [{} {}]]
          :result :lose}
         (game/dig {:board [[{:mine true} {}]
                            [{} {}]]}
                   [0 0]))
      "Digging up a cell with a mine causes the mine to explode"))
