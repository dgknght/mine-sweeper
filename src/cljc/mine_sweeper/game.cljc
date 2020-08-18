(ns mine-sweeper.game
  #_(:require #?(:clj [clojure.pprint :refer [pprint]])))

#_(defn- log
  [msg]
  #?(:cljs (.log js/console (prn-str msg))
     :clj (pprint msg)))

(defn- calc-mine-locations
  [cell-count density]
  (->> (range cell-count)
       (random-sample 0.7)
       (take (Math/ceil (* density cell-count)))
       set))

(defn index->point
  [index {:keys [width]}]
  [(mod index width) (quot index width)])

(defn point->index
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
       (reduce (fn [result [index adjacencies]]
                 (if (get-in result [index :mine])
                   result
                   (assoc-in result [index :adjacent] adjacencies)))
               cells)))

(defn- gridify
  [{:keys [width]} cells]
  (->> cells
       (partition width)
       (map vec)
       vec))

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
                        (cond-> (with-meta {} {:index i})
                          (contains? mine-indices i)
                          (assoc :mine true))))
                 (append-adjacencies mine-indices options)
                 (gridify options))
     :options options}))

(defn- check-for-win
  [{:keys [board] :as g}]
  (if (->> (apply concat board)
           (remove :mine)
           (remove :dug)
           empty?)
    (assoc g :result :win)
    g))

(defn dig
  "Dig up a cell to see if there is a mine there. Digging up a
  cell with a mine will end the game. Otherwise, the cell is opened"
  [g cell-id]
  (let [[x y] (if (sequential? cell-id)
                cell-id
                (index->point cell-id (:options g)))
        cell (get-in g [:board y x])]
    (if (:mine cell)
      (-> g
          (assoc :result :lose)
          (update-in [:board y x] #(-> %
                                       (dissoc :mine)
                                       (assoc :exploded true))))
      (-> g
          (assoc-in [:board y x :dug] true)
          check-for-win))))

(defn flag
  "Mark a cell indicating the player believes there is a bomb there."
  [g cell-id]
  (let [[x y] (if (sequential? cell-id)
                cell-id
                (index->point cell-id (:options g)))
        cell (get-in g [:board y x])]
    (if (:dug cell)
      g
      (assoc-in g [:board y x :flagged] true))))
