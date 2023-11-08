(ns signals.main
  (:require
   [signals.ks :as ks]
   [signals.signal :as signal]
   [uix.core :refer [$ defui]]
   [uix.dom :as dom]))

(defui radial-gradient [{:keys [id stop-color1 stop-color2]}]
  ($ :radialGradient {:id id}
     ($ :stop {:stopColor stop-color1
               :offset "0.05"})
     ($ :stop {:stopColor stop-color2
               :offset "0.9"})))

(defui defs []
  ($ :defs
     ($ radial-gradient {:id "green-gradient"
                         :stop-color1 "#33ff6d"
                         :stop-color2 "#00bd4a"})
     ($ radial-gradient {:id "red-gradient"
                         :stop-color1 "#ff3763"
                         :stop-color2 "#da012a"})
     ($ radial-gradient {:id "orange-gradient"
                         :stop-color1 "#ffc955"
                         :stop-color2 "#fc8e00"})
     ($ radial-gradient {:id "yellow-gradient"
                         :stop-color1 "#ffe060"
                         :stop-color2 "#fac412"})
     ($ radial-gradient {:id "white-gradient"
                         :stop-color1 "#fffaef"
                         :stop-color2 "#ebe6d8"})))

(defui signal []
  ($ :svg {:version "1.1"
           :viewBox "0 0 140 600"
           :width "200"
           :height "600"}
     ($ defs)
     ($ ks/view {:signal (signal/main {:speed-limit 10
                                       :aspect :stop+zs1
                                       :zs1? true
                                       :system :ks})})))

(defn render []
  (dom/render ($ signal) (.getElementById js/document "app")))

(defn ^:dev/after-load reload []
  (render))

(defn ^:export init []
  (render))
