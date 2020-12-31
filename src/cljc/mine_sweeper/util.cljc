(ns mine-sweeper.util
  (:require #?(:clj [clojure.pprint :refer [pprint]])))

(defn trace
  [msg]
  #?(:clj (pprint msg)
     :cljs (.log js/console (prn-str msg))))
