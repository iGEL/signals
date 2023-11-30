(ns signals.ne
  (:require
   [uix.core :refer [$ defui]]))

(defui ne2 [{:keys [shortened-break-path?]}]
  ($ :g
     (when shortened-break-path?
       ($ :path.ral9002 {:d "m1,-23 17,20 17,-20 z"
                         :style {:stroke "#0e0e10"
                                 :stroke-width 2}}))
     ($ :rect.ral9002 {:width 34
                       :height 54
                       :x 1
                       :y 0
                       :style {:stroke "#0e0e10"
                               :stroke-width 2
                               :stroke-miterlimit 4}})
     ($ :path.ral9005 {:d "m 0,55 18,-27.5 18,27.5 -7,0 -11,-17.5 -11,17.5 z"})
     ($ :path.ral9005 {:d "m 36,0 -18,27.5 -18,-27.5 7,0 11,17.5 11,-17.5 z"})))
