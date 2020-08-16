(ns mine-sweeper.game-test
  (:require [clojure.test :refer [deftest is]]
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
