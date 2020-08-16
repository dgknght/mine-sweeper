(ns mine-sweeper.game)

(defn create
  "Create a new game with a board of the specified width and height"
  [{:keys [height
           width
           density]
    :or {height 5
         width 5
         density 0.25}}]

  (let [cell-count (* width height)
        mine-indices (->> (range cell-count)
                          (random-sample 0.7)
                          (take (Math/ceil (* density cell-count)))
                          set)]
    {:board (->> (range cell-count)
                 (map (fn [i]
                        (cond-> {}
                          (contains? mine-indices i)
                          (assoc :mine true))))
                 (partition width))}))

(defn dig
  "Dig up a cell to see if there is a mine there. Digging up a
  cell with a mine will end the game. Otherwise, the cell is opened"
  [g [x y]]
  (let [cell (get-in g [:board y x])]
    (if (:mine cell)
      (-> g
          (assoc :result :lose)
          (update-in [:board y x] #(-> %
                                       (dissoc :mine)
                                       (assoc :exploded true))))
      (assoc-in g [:board y x :dug] true))))
