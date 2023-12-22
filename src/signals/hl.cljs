(ns signals.hl
  (:require
   [cljs.spec.alpha :as s]
   [clojure.set :refer [intersection]]
   [preo.core :as p]
   [signals.helper :refer [stop-aspect?]]
   [signals.lamp :as lamp :refer [lamp]]
   [signals.spec :as spec]
   [uix.core :refer [$ defui]]))

(s/def ::top-yellow ::lamp/state)
(s/def ::top-green ::lamp/state)
(s/def ::top-white ::lamp/state)
(s/def ::red ::lamp/state)
(s/def ::bottom-white ::lamp/state)
(s/def ::bottom-yellow ::lamp/state)
(s/def ::replacement-red ::lamp/state)
(s/def ::green-stripe ::lamp/state)
(s/def ::yellow-stripe ::lamp/state)
(s/def ::shortened-break-path? boolean?)

(s/def ::lights (s/keys :req-un [::top-yellow ::top-green ::top-white ::red ::bottom-white ::bottom-yellow ::replacement-red ::green-stripe ::yellow-stripe ::shortened-break-path?]))

(defn lights [{{main-aspect* :aspect
                main-slow-speed-lights :slow-speed-lights
                main-speed-limit :speed-limit
                sh1? :sh1?
                zs1? :zs1?
                main-indicator? :indicator?} :main
               {distant-aspect* :aspect
                distant-slow-speed-lights :slow-speed-lights
                distant-speed-limit :speed-limit
                distant-addition :distant-addition
                distant-indicator? :indicator?} :distant
               signal-type :type
               :as signal}]
  {:pre [(p/arg! ::spec/signal signal)]
   :post [(p/ret! ::lights %)]}
  (let [main-aspect (if (or (= :dark main-aspect*)
                            (and (= :off main-aspect*)
                                 (not main-indicator?)))
                      :stop
                      main-aspect*)
        distant-aspect (if (or (= :dark distant-aspect*)
                               (and (= :off distant-aspect*)
                                    (not distant-indicator?)))
                         :stop
                         distant-aspect*)
        distant? (= :distant signal-type)
        main? (= :main signal-type)
        distant-40-or-60-limit? (and (some #{40 60} distant-slow-speed-lights)
                                     (#{40 60} distant-speed-limit))]
    {:top-yellow (cond
                   main? nil
                   (= :off main-aspect) :off
                   (= :off distant-aspect) :off
                   (stop-aspect? main-aspect) :off
                   (stop-aspect? distant-aspect) :on
                   distant-40-or-60-limit? :blinking
                   :else :off)
     :top-green (cond
                  (or (stop-aspect? main-aspect)
                      (stop-aspect? distant-aspect)
                      distant-40-or-60-limit?
                      (= :off main-aspect)
                      (= :off distant-aspect)) :off
                  (and (some #{100} distant-slow-speed-lights)
                       (= 100 distant-speed-limit)) :blinking
                  :else :on)
     :top-white (cond
                  (not sh1?) nil
                  (= :stop+sh1 main-aspect) :on
                  :else :off)
     :red (cond
            distant? nil
            (= :off main-aspect) :off
            (stop-aspect? main-aspect) :on
            :else :off)
     :bottom-white (cond
                     (not (or sh1? zs1? main-indicator? distant-indicator?)) nil
                     (= :off main-aspect) :on
                     (= :off distant-aspect) :on
                     (and sh1? (= :stop+sh1 main-aspect)) :on
                     (and zs1? (= :stop+zs1 main-aspect)) :blinking
                     :else :off)
     :bottom-yellow (cond
                      (or distant?
                          (not (some #{40 60 100} main-slow-speed-lights))) nil
                      (and (#{40 60 100} main-speed-limit)
                           (not (stop-aspect? main-aspect))) :on
                      :else :off)
     :replacement-red (when-not distant? :off)
     :green-stripe (cond
                     (or distant?
                         (not (some #{100} main-slow-speed-lights))) nil
                     (and (= 100 main-speed-limit)
                          (not (stop-aspect? main-aspect))) :on
                     :else :off)
     :yellow-stripe (cond
                      (or distant?
                          (not (some #{60} main-slow-speed-lights))) nil
                      (and (= 60 main-speed-limit)
                           (not (stop-aspect? main-aspect))) :on
                      :else :off)
     :shortened-break-path? (= :shortened-break-path distant-addition)}))

(defui stripe-lamp [{:keys [x color yellow-stripe green-stripe]}]
  ($ lamp {:color color
           :state (if (= :green color)
                    green-stripe
                    yellow-stripe)
           :size :small
           :x x
           :y (if (and (= :green color)
                       yellow-stripe)
                200
                227)}))

(defui view [{:keys [signal]}]
  {:pre [(p/arg! ::spec/signal signal)]}
  (let [{:keys [top-yellow top-green top-white red bottom-white bottom-yellow replacement-red
                yellow-stripe green-stripe]} (lights signal)]
    ($ :g.hlSignal
       ($ :path {:d (if red
                      "M 0,9 8,0 62,0 l 9,9 0,175 -72,0 z"
                      "M -0,9 8,0 62,0 l 9,9 0,122 -72,0 z")})
       ($ lamp {:color :yellow
                :state top-yellow
                :size :big
                :x 20
                :y 30})
       ($ lamp {:color :green
                :state top-green
                :size :big
                :x 50
                :y 30})
       ($ lamp {:color :white
                :state top-white
                :size :small
                :x 53
                :y 63})
       ($ lamp {:color :red
                :state red
                :size :big
                :x 35
                :y 82})
       ($ lamp {:color :white
                :state bottom-white
                :size :small
                :x 14
                :y 99})
       ($ lamp {:color :yellow
                :state bottom-yellow
                :size :big
                :x 20
                :y 136})
       ($ lamp {:color :red
                :state replacement-red
                :size :big
                :x 50
                :y 136})
       (when (or yellow-stripe green-stripe)
         (let [green {:color :green
                      :yellow-stripe yellow-stripe
                      :green-stripe green-stripe}
               yellow {:color :yellow
                       :yellow-stripe yellow-stripe
                       :green-stripe green-stripe}]
           ($ :<>
              ($ :rect {:height 62
                        :width 71
                        :x 0
                        :y 185})
              ($ stripe-lamp (merge green {:x 8}))
              ($ stripe-lamp (merge green {:x 27}))
              ($ stripe-lamp (merge green {:x 45}))
              ($ stripe-lamp (merge green {:x 63}))
              ($ stripe-lamp (merge yellow {:x 8}))
              ($ stripe-lamp (merge yellow {:x 27}))
              ($ stripe-lamp (merge yellow {:x 45}))
              ($ stripe-lamp (merge yellow {:x 63}))))))))

(defn speed-limit-available? [{{slow-speed-lights :slow-speed-lights} :main
                               :as signal} limit]
  {:pre [(p/arg! ::spec/signal signal)]}
  (let [speeds (-> slow-speed-lights
                   set
                   (intersection #{40 60 100}))]
    (or (nil? limit)
        (speeds limit))))
