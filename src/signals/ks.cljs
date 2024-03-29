(ns signals.ks
  (:require
   [cljs.spec.alpha :as s]
   [preo.core :as p]
   [signals.helper :refer [stop-aspect?]]
   [signals.lamp :as lamp :refer [lamp]]
   [signals.spec :as signal]
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
  [{{main-aspect* :aspect
     sh1? :sh1?
     zs1? :zs1?
     zs7? :zs7?
     main-indicator? :indicator?} :main
    {distant-aspect* :aspect
     distant-speed-limit :speed-limit
     distant-addition :distant-addition
     zs3 :zs3
     distant-indicator? :indicator?} :distant
    signal-type :type
    :as signal}]
  {:pre [(p/arg! ::signal/signal signal)]
   :post [(p/ret! ::lights %)]}
  (let [main-aspect (if (and (= :off main-aspect*)
                             (not main-indicator?))
                      :stop
                      main-aspect*)
        distant-aspect (if (and (= :off distant-aspect*)
                                (not distant-indicator?))
                         :stop
                         distant-aspect*)]
    {:top-white (cond
                  (= :shortened-break-path distant-addition)
                  (cond
                    (stop-aspect? main-aspect) :off
                    (or (stop-aspect? distant-aspect)
                        (and distant-speed-limit zs3)) :on
                    :else :off)

                  distant-indicator?
                  (if (= :off distant-aspect) :on :off)

                  main-indicator?
                  (if (= :off main-aspect) :on :off))
     :red (cond
            (= :distant signal-type) nil
            (stop-aspect? main-aspect) :on
            :else :off)
     :green (cond
              (or (stop-aspect? main-aspect)
                  (stop-aspect? distant-aspect)
                  (#{:dark :off} main-aspect)
                  (#{:dark :off} distant-aspect)) :off
              (and distant-speed-limit zs3) :blinking
              :else :on)
     :yellow (cond
               (= :main signal-type) nil
               (or (stop-aspect? main-aspect)
                   (#{:dark :off} main-aspect)) :off
               (stop-aspect? distant-aspect) :on
               :else :off)
     :center-white (cond
                     (and (not sh1?)
                          (or (not zs1?)
                              (not= :main signal-type))) nil
                     (= :stop+sh1 main-aspect) :on
                     (and (= :main signal-type)
                          (= :stop+zs1 main-aspect)) :blinking
                     :else :off)
     :zs7 (cond
            (not zs7?) nil
            (= :stop+zs7 main-aspect) :on
            :else :off)
     :bottom-white (if (= :distant signal-type)
                     (cond
                       (not= :repeater distant-addition) nil
                       (or (stop-aspect? distant-aspect)
                           (and distant-speed-limit zs3)) :on
                       :else :off)
                     (cond
                       (and (not sh1?)
                            (or (not zs1?)
                                (not= :combination signal-type))) nil
                       (= :stop+sh1 main-aspect) :on
                       (and (= :combination signal-type)
                            (= :stop+zs1 main-aspect)) :blinking
                       :else :off))}))

(defui view [{:keys [signal]}]
  {:pre [(p/arg! ::signal/signal signal)]}
  (let [{:keys [top-white red green yellow center-white zs7 bottom-white]} (lights signal)]
    ($ :g
       ($ :rect {:width 64
                 :height 120
                 :x 0
                 :y 0
                 :style {:fill "black"
                         :stroke "none"}})
       ($ lamp {:color :white
                :state top-white
                :size :small
                :x 16.5
                :y 14.5})
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
                :y 57.3})
       ($ lamp {:color :yellow
                :state zs7
                :size :small
                :x 21.5
                :y 81})
       ($ lamp {:color :white
                :state center-white
                :size :small
                :x 32
                :y 81})
       ($ lamp {:color :yellow
                :state zs7
                :size :small
                :x 42.5
                :y 81})
       ($ lamp {:color :yellow
                :state zs7
                :size :small
                :x 32
                :y 98.7})
       ($ lamp {:color :white
                :state bottom-white
                :size :small
                :x 11.5
                :y 98.7}))))

(defn speed-limit-available? [{{:keys [zs3]} :main
                               :as signal}
                              limit]
  {:pre [(p/arg! ::signal/signal signal)]}
  (case zs3
    :display true
    :sign limit
    (nil? limit)))
