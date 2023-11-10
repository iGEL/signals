(ns signals.display
  (:require
   [cljs.math :refer [floor]]
   [signals.display.matrix :refer [matrix]]
   [signals.lamp :refer [lamp]]
   [uix.core :refer [$ defui]]))

(defn- center [rows]
  (let [width (-> rows first count)
        pad (-> (- 9 width)
                (/ 2)
                floor)]
    (map (fn [row]
           (concat (repeat pad false)
                   row
                   (repeat (- 9 (count row) pad) false)))
         rows)))

(defui view [{:keys [value color]}]
  ($ :g
     ($ :rect {:width 54
               :height 44
               :x 0
               :y 0
               :style {:fill "black"
                       :stroke "none"}})
     ($ :g
        (map-indexed
         (fn [y row]
           (map-indexed
            (fn [x bit]
              ($ lamp {:key (str "display" x "-" y)
                       :color color
                       :state (if bit :on :off)
                       :size :tiny
                       :x (+ (* x 5) 7)
                       :y (+ (* y 5) 7)}))
            row))
         (-> value matrix center)))))
