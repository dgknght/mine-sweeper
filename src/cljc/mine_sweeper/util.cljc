(ns mine-sweeper.util
  (:require #?(:clj [clojure.pprint :refer [pprint]])))

(defn trace
  [msg]
  #?(:clj (pprint msg)
     :cljs (.log js/console (prn-str msg))))

(defmulti presence
  (fn [value]
    (cond
      (string? value) :string)))

(defmethod presence :default
  [value]
  value)

(defmethod presence :string
  [value]
  (when-not (empty? value)
    value))
