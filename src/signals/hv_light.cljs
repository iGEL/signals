(ns signals.hv-light
  (:require
   [cljs.spec.alpha :as s]
   [preo.core :as p]
   [signals.helper :refer [stop-aspect?]]
   [signals.lamp :as lamp]
   [signals.spec :as spec]
   [uix.core :refer [$ defui]]))

;; ---- Distant aspect
(s/def ::top-green ::lamp/state)
(s/def ::top-yellow ::lamp/state)
(s/def ::white ::lamp/state)
(s/def ::bottom-green ::lamp/state)
(s/def ::bottom-yellow ::lamp/state)
(s/def ::distant (s/nilable (s/keys :req-un [::top-green ::top-yellow ::white ::bottom-green ::bottom-yellow])))

;; ----- Main aspect
(s/def ::green ::lamp/state)
(s/def ::red ::lamp/state)
(s/def ::yellow ::lamp/state)
(s/def ::secondary-red ::lamp/state)
(s/def ::sh1 ::lamp/state)
(s/def ::zs1 ::lamp/state)
(s/def ::zs7 ::lamp/state)
(s/def ::main (s/nilable (s/keys :req-un [::green ::red ::yellow ::secondary-red ::sh1 ::zs1 ::zs7])))

(s/def ::lights (s/keys :req-un [::distant ::main]))

(defn lights [{{main-aspect* :aspect
                main-slow-speed-lights :slow-speed-lights
                main-speed-limit :speed-limit
                main-indicator? :indicator?
                sh1? :sh1?
                zs1? :zs1?
                zs7? :zs7?} :main
               {distant-aspect* :aspect
                distant-slow-speed-lights :slow-speed-lights
                distant-speed-limit :speed-limit
                distant-addition :distant-addition
                distant-indicator? :indicator?} :distant
               signal-type :type
               :as signal}]
  {:pre [(p/arg! ::spec/signal signal)]
   :post [(p/ret! ::lights %)]}
  (let [main-aspect (if (and (not main-indicator?)
                             (= :off main-aspect*)) :stop main-aspect*)
        distant-aspect (if (and (not distant-indicator?)
                                (= :off distant-aspect*)) :stop distant-aspect*)]
    {:main (when-not (= :distant signal-type)
             {:green (if (or (stop-aspect? main-aspect)
                             (#{:off :dark} main-aspect)) :off :on)
              :red (if (stop-aspect? main-aspect) :on :off)
              :yellow (let [has-light? (some #{40} main-slow-speed-lights)]
                        (cond
                          (not has-light?) nil
                          (and (not (stop-aspect? main-aspect))
                               (not (= :off main-aspect))
                               main-speed-limit (>= 60 main-speed-limit)) :on
                          :else :off))
              :secondary-red (cond
                               (not sh1?) nil
                               (= :stop+sh1 main-aspect) :off
                               (stop-aspect? main-aspect) :on
                               :else :off)
              :indicator (cond
                           (not main-indicator?) nil
                           (= :off main-aspect) :on
                           :else :off)
              :sh1 (cond
                     (not sh1?) nil
                     (= :stop+sh1 main-aspect) :on
                     :else :off)
              :zs1 (cond
                     (not zs1?) nil
                     (= :stop+zs1 main-aspect) :on
                     :else :off)
              :zs7 (cond
                     (not zs7?) nil
                     (= :stop+zs7 main-aspect) :on
                     :else :off)})
     :distant (when-not (= :main signal-type)
                (let [slow-speed? (and (some #{40} distant-slow-speed-lights)
                                       distant-speed-limit
                                       (>= 60 distant-speed-limit))]
                  {:top-green (cond
                                (stop-aspect? main-aspect) :off
                                (stop-aspect? distant-aspect) :off
                                (#{:off :dark} distant-aspect) :off
                                :else :on)
                   :top-yellow (cond
                                 (stop-aspect? main-aspect) :off
                                 (stop-aspect? distant-aspect) :on
                                 :else :off)
                   :white (cond
                            distant-addition (if (stop-aspect? main-aspect) :off :on)
                            distant-indicator? (if (= :off distant-aspect) :on :off))
                   :bottom-green (cond
                                   (stop-aspect? main-aspect) :off
                                   (stop-aspect? distant-aspect) :off
                                   (#{:off :dark} distant-aspect) :off
                                   slow-speed? :off
                                   :else :on)
                   :bottom-yellow (cond
                                    (stop-aspect? main-aspect) :off
                                    (stop-aspect? distant-aspect) :on
                                    slow-speed? :on
                                    :else :off)}))}))

(defui zs1+zs7-view [{:keys [zs1 zs7]}]
  (when (or zs1 zs7)
    ($ :<>
       ($ :rect {:width 35, :height 33, :x 0, :y 0})
       ($ lamp/lamp {:color :white
                     :state zs1
                     :size :small
                     :x 17
                     :y 10})
       ($ lamp/lamp {:color :white
                     :state zs1
                     :size :small
                     :x 8
                     :y 24})
       ($ lamp/lamp {:color :white
                     :state zs1
                     :size :small
                     :x 26
                     :y 24})
       ($ lamp/lamp {:color :yellow
                     :state zs7
                     :size :small
                     :x 8
                     :y 10})
       ($ lamp/lamp {:color :yellow
                     :state zs7
                     :size :small
                     :x 26
                     :y 10})
       ($ lamp/lamp {:color :yellow
                     :state zs7
                     :size :small
                     :x 17
                     :y 24}))))

(defui view [{:keys [signal]}]
  {:pre [(p/arg! ::spec/signal signal)]}
  (let [{{:keys [green red yellow secondary-red sh1 indicator] :as main} :main
         {:keys [top-green top-yellow white bottom-green bottom-yellow]} :distant} (lights signal)]
    ($ :<>
       (when main
         (if (-> signal :main :sh1?)
           ($ :<>
              ($ :rect {:width 58
                        :height 134
                        :x 22
                        :y 0
                        :style {:fill "black"
                                :stroke "none"}})
              ($ lamp/lamp {:color :green
                            :state green
                            :size :big
                            :x 38
                            :y 23})
              ($ lamp/lamp {:color :red
                            :state red
                            :size :big
                            :x 38
                            :y 44})
              ($ lamp/lamp {:color :red
                            :state secondary-red
                            :size :big
                            :x 65
                            :y 44})
              ($ lamp/lamp {:color :white
                            :state indicator
                            :size :small
                            :x 38
                            :y 68})
              ($ lamp/lamp {:color :white
                            :state sh1
                            :size :small
                            :x 66
                            :y 68})
              ($ lamp/lamp {:color :white
                            :state sh1
                            :size :small
                            :x 38
                            :y 92})
              ($ lamp/lamp {:color :yellow
                            :state yellow
                            :size :big
                            :x 38
                            :y 105})
              ($ :g {:transform (str "translate(34," (if sh1 136 147) ")")}
                 ($ zs1+zs7-view main)))
           ($ :<>
              ($ :rect {:width 65
                        :height 145
                        :x 18.8
                        :y 0
                        :style {:fill "black"
                                :stroke "none"}})
              ($ lamp/lamp {:color :red
                            :state red
                            :size :big
                            :x 43
                            :y 126})
              ($ lamp/lamp {:color :white
                            :state indicator
                            :size :small
                            :x 51.5
                            :y 68})
              ($ lamp/lamp {:color :green
                            :state green
                            :size :big
                            :x 60
                            :y (if yellow 24 126)})
              ($ lamp/lamp {:color :yellow
                            :state yellow
                            :size :big
                            :x 60
                            :y 126})

              ($ :g {:transform (str "translate(34," (if sh1 136 147) ")")}
                 ($ zs1+zs7-view main)))))
       (when (:distant signal)
         ($ :<>
            ($ :path {:d "m 0,256.82806 0,28.15532 16.4938603,15.11883 29.3079287,0 58.748797,-60.73876 0,-28.18838 -15.93946,-14.32876 -29.307928,0 z"})
            ($ lamp/lamp {:color :yellow
                          :state top-yellow
                          :size :big
                          :x 70
                          :y 222})
            ($ lamp/lamp {:color :green
                          :state top-green
                          :size :big
                          :x 89
                          :y 222})
            ($ lamp/lamp {:color :yellow
                          :state bottom-yellow
                          :size :big
                          :x 18
                          :y 275})
            ($ lamp/lamp {:color :green
                          :state bottom-green
                          :size :big
                          :x 37
                          :y 275})
            (when white
              ($ :<>
                 ($ :path {:d "m 8,211 a 15,15 0 0 0 0,21.2132 l 10.6065997,10.6066 10.6066,-10.6066 10.6066,-10.6066 -10.6066,-10.6066 a 15,15 0 0 0 -21.2131997,0 z"})
                 ($ lamp/lamp {:color :white
                               :state white
                               :size :small
                               :x 18
                               :y 222}))))))))

(defn speed-limit-available? [{{:keys [zs3 slow-speed-lights]} :main
                               :as signal} limit]
  {:pre [(p/arg! ::spec/signal signal)]}
  (cond
    (= :display zs3) true
    (= :sign zs3) limit
    (nil? limit) true
    (some #{40} slow-speed-lights) (= 40 limit)
    :else false))
