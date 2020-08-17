(ns mine-sweeper.game)

(defn- calc-mine-locations
  [cell-count density]
  (->> (range cell-count)
       (random-sample 0.7)
       (take (Math/ceil (* density cell-count)))
       set))

(defn- index->point
  [index {:keys [width]}]
  [(mod index width) (quot index width)])

(defn- point->index
  [[x y] {:keys [width]}]
  (+ (* y width) x))

(defn- valid-x?
  [[x _] {:keys [width]}]
  (and (not (neg? x))
       (< x width)))

(defn- valid-y?
  [[_ y] {:keys [height]}]
  (and (not (neg? y))
       (< y height)))

(defn- touching-indices
  [index options]
  (let [[x y] (index->point index options)]
    (->> (for [yy (take 3 (iterate inc (- y 1)))
               xx (take 3 (iterate inc (- x 1)))]
           [xx yy])
         (filter #(valid-x? % options))
         (filter #(valid-y? % options))
         (map #(point->index % options))
         (remove #(= index %)))))

(defn- append-adjacencies
  [mine-indices options cells]
  (->> mine-indices
       (mapcat #(touching-indices % options))
       frequencies
       (reduce #(assoc-in %1 [(first %2) :adjacent] (second %2))
               cells)))

(defn create
  "Create a new game with a board of the specified width and height"
  [{:keys [height
           width
           density
           mine-indices]
    :as options
    :or {height 5
         width 5
         density 0.25}}]

  (let [cell-count (* width height)
        mine-indices (or mine-indices
                         (calc-mine-locations cell-count density))]
    {:board (->> (range cell-count)
                 (mapv (fn [i]
                        (cond-> {}
                          (contains? mine-indices i)
                          (assoc :mine true))))
                 (append-adjacencies mine-indices options)
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
