; lein run -m examples.afterglow

(ns examples.afterglow
  (:require [launchpad :refer [get-launchpad]])
  (:gen-class))

(defn afterglow-dim
  [color]
  (mapv #(max 0 (- % 1)) color))

(defn get-in-2
  [state what where]
  (if (= what :grid)
    (let [[x y] where] (get-in state [what x y]))
    (get-in state [what where])))

(defn afterglow-helper
  [state [what where]]
  (.light state what where (afterglow-dim (get-in-2 state what where))))

(defn afterglow [state]
  "Dims the lights."
  (as-> state $
    (reduce afterglow-helper $
            (for [what [:top :side] index (range 8)] [what index]))
    (reduce afterglow-helper $
            (for [x (range 8) y (range 8)] [:grid [x y]]))))

(defn -main []
  (let [pad (get-launchpad)
        update-hz 10]
    (when-not pad
      (throw (RuntimeException. "Connect your launchpad!")))
    (.react pad (fn [state what where vel]
                  (when (= vel 127) (.light state what where [3 3]))))
    (while true
      (Thread/sleep (/ 1000 update-hz))
      (.update pad (afterglow (.state pad))))))
