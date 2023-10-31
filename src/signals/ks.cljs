(ns signals.ks
  (:require
   [cljs.spec.alpha :as s]
   [preo.core :as p]
   [signals.lamp :as lamp :refer [lamp]]
   [signals.signal :as signal]
   [uix.core :refer [$ defui]]))

(s/def ::top-white ::lamp/state)
(s/def ::red ::lamp/state)
(s/def ::green ::lamp/state)
(s/def ::yellow ::lamp/state)
(s/def ::center-white ::lamp/state)
(s/def ::zs7 ::lamp/state)
(s/def ::bottom-white ::lamp/state)
(s/def ::lights (s/keys :req-un [::top-white ::red ::green ::yellow ::center-white ::zs7 ::bottom-white]))

(defn lights
  "Converts a signal definition into a list of lights and their state"
  [{{main-speed :speed-limit} :main
    {distant-speed :speed-limit} :distant
    signal-type :type
    :as signal}]
  {:pre [(p/arg! ::signal/signal signal)]
   :post [(p/ret! ::lights %)]}
  {:top-white nil
   :red (cond
          (= :distant signal-type) nil
          (zero? main-speed) :on
          :else :off)
   :green (cond
            (zero? main-speed) :off
            (zero? distant-speed) :off
            :else :on)
   :yellow (cond
             (= :main signal-type) nil
             (zero? main-speed) :off
             (zero? distant-speed) :on
             :else :off)
   :center-white nil
   :zs7 nil
   :bottom-white nil})

(defui view [{:keys [signal]}]
  {:pre [(p/arg! ::signal/signal signal)]}
  (let [{:keys [red green yellow]} (lights signal)]
    ($ :g
       ($ :rect {:width 64
                 :height 120
                 :x 0
                 :y 0
                 :style {:fill "black"
                         :stroke "none"}})
       ($ lamp {:color :red
                :state red
                :size :big
                :x 32
                :y 32})
       ($ lamp {:color :green
                :state green
                :size :big
                :x (if yellow 16.5 32)
                :y 57.3})
       ($ lamp {:color :yellow
                :state yellow
                :size :big
                :x 47.5
                :y 57.3}))))
