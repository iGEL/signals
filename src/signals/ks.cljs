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

(defn main
  "Constructor for a main signal"
  [{:keys [speed-limit stop-override sh1? zs1? zs3? zs7?]
    :or {speed-limit 0
         sh1? false zs1? false zs3? false zs7? false}}]
  {:post [(p/ret! ::signal/signal %)]}
  {:system :ks
   :type :main
   :main {:speed-limit speed-limit
          :stop-override stop-override
          :slow-speed-lights []
          :sh1? sh1?
          :zs1? zs1?
          :zs3? zs3?
          :zs7? zs7?}})

(defn distant
  "Constructor for a distant signal"
  [{:keys [speed-limit distant-addition zs3v?]
    :or {speed-limit 0
         zs3v? false}}]
  {:post [(p/ret! ::signal/signal %)]}
  {:system :ks
   :type :distant
   :distant {:speed-limit speed-limit
             :distant-addition distant-addition
             :slow-speed-lights []
             :zs3v? zs3v?}})

(defn combination
  "Constructor for a combination of a main & distant signal"
  [{{distant-speed-limit :speed-limit
     :keys [zs3v? distant-addition]
     :or {distant-speed-limit 0
          zs3v? false}} :distant
    {main-speed-limit :speed-limit
     :keys [stop-override sh1? zs1? zs3? zs7?]
     :or {main-speed-limit 0
          sh1? false zs1? false zs3? false zs7? false}} :main}]
  {:post [(p/ret! ::signal/signal %)]}
  {:system :ks
   :type :combination
   :distant {:speed-limit distant-speed-limit
             :distant-addition distant-addition
             :slow-speed-lights []
             :zs3v? zs3v?}
   :main {:speed-limit main-speed-limit
          :stop-override stop-override
          :slow-speed-lights []
          :sh1? sh1?
          :zs1? zs1?
          :zs3? zs3?
          :zs7? zs7?}})

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
