(ns signals.hv-semaphore
  (:require
   [cljs.spec.alpha :as s]
   [preo.core :as p]
   [signals.helper :refer [stop-aspect?]]
   [signals.hv-light :as hv-light]
   [signals.lamp :as lamp]
   [signals.spec :as spec]
   [uix.core :refer [$ defui]]))

(s/def :distant/disk #{:vertical :horizontal})
(s/def :distant/arm (s/nilable #{:vertical :inclined}))
(s/def :distant/right-lights #{:vertical :inclined}) ;; The left lights always correspond to the disk
(s/def :distant/shortened-break-path? boolean?)
(s/def ::distant (s/nilable (s/keys :req-un [:distant/arm :distant/disk :distant/right-lights :distant/shortened-break-path?])))

(s/def :main/top-arm #{:horizontal :inclined})
(s/def :main/lower-arm (s/nilable #{:vertical :inclined}))
(s/def :main/sh1 (s/nilable #{:horizontal :inclined}))
(s/def :main/zs1 ::lamp/state)
(s/def :main/zs7 ::lamp/state)
(s/def ::main (s/nilable (s/keys :req-un [:main/top-arm :main/lower-arm :main/sh1 :main/zs1 :main/zs7])))

(s/def ::arms (s/keys :req-un [::distant ::main]))

(defn arms [{{main-aspect :aspect
              main-slow-speed-lights :slow-speed-lights
              main-speed-limit :speed-limit
              sh1? :sh1?
              zs1? :zs1?
              zs7? :zs7?} :main
             {distant-aspect :aspect
              distant-slow-speed-lights :slow-speed-lights
              distant-speed-limit :speed-limit
              distant-addition :distant-addition} :distant
             signal-type :type
             :as signal}]
  {:pre [(p/arg! ::spec/signal signal)]
   :post [(p/ret! ::arms %)]}
  {:main (when-not (= :distant signal-type)
           {:top-arm (if (stop-aspect? main-aspect) :horizontal :inclined)
            :lower-arm (cond
                         (not (some #{40} main-slow-speed-lights)) nil
                         (and (not (stop-aspect? main-aspect))
                              main-speed-limit
                              (>= 60 main-speed-limit)) :inclined
                         :else :vertical)
            :sh1 (cond
                   (not sh1?) nil
                   (#{:proceed :stop+sh1} main-aspect) :inclined
                   :else :horizontal)
            :zs1 (cond
                   (not zs1?) nil
                   (= :stop+zs1 main-aspect) :on
                   :else :off)
            :zs7 (cond
                   (not zs7?) nil
                   (= :stop+zs7 main-aspect) :on
                   :else :off)})
   :distant (when (and (not= :main signal-type)
                       (not= :repeater distant-addition))
              (let [has-slow-speed? (some #{40} distant-slow-speed-lights)
                    slow-speed? (and has-slow-speed?
                                     (not (stop-aspect? main-aspect))
                                     (not (stop-aspect? distant-aspect))
                                     distant-speed-limit
                                     (>= 60 distant-speed-limit))]
                {:disk (if (or (stop-aspect? main-aspect)
                               (stop-aspect? distant-aspect)
                               slow-speed?) :vertical
                           :horizontal)
                 :arm (cond
                        (not has-slow-speed?) nil
                        slow-speed? :inclined
                        :else :vertical)
                 :right-lights (if (and (not (stop-aspect? main-aspect))
                                        (not (stop-aspect? distant-aspect)))
                                 :inclined
                                 :vertical)
                 :shortened-break-path? (= :shortened-break-path distant-addition)}))})

(defui main-lights [{:keys [position top-color bottom-color]}]
  (let [colors {:red "#e64e54"
                :green "#2cecf5"
                :yellow "#db9b6e"}]
    ($ :g.semaphoreAnimate.main-lights {:class (if (= :inclined position)
                                                 "inclined"
                                                 "horizontal")}
       ($ :g {:transform "rotate(33.832)"}
          ($ :circle.ral9007 {:cx 25.785
                              :cy 28.549
                              :r 9.874})
          ($ :circle.ral9007 {:cx 25.785
                              :cy 49.448
                              :r 9.874})
          ($ :path.ral9007 {:d "M15.912 28.549h19.747v20.9H15.912z"})
          ($ :path.ral9007 {:d "M-21.741 41.036H-5.32v4.913h-16.421z"
                            :transform "rotate(-33.832)"})
          (when top-color
            ($ :circle {:cx 25.785
                        :cy 28.549
                        :r 7.152
                        :fill (colors top-color)}))
          ($ :circle {:cx 25.785
                      :cy 49.446
                      :r 7.152
                      :fill (colors bottom-color)})))))

(defui main-arm [{:keys [position]}]
  ($ :g.semaphoreAnimate.main-arm {:class (name position)}
     ($ :g {:transform "translate(-11,-11)"}
        ($ :rect.ral3002 {:width 133
                          :height 16
                          :x -20
                          :y 3.2})
        ($ :rect.ral9002 {:width 125
                          :height 6.8
                          :x -15.2
                          :y 7.8})
        ($ :circle.ral3002 {:cx 125
                            :cy 11
                            :r 16})
        ($ :circle.ral9002 {:cx 125
                            :cy 11
                            :r 10})
        ($ :circle.screw {:cx 11
                          :cy 11
                          :r 1.8}))))

(defui main-pole []
  (let [grid-cross ($ :<>
                      ($ :path {:transform "rotate(45)"
                                :d "M295.29 299.044h17.644v2.474H295.29z"})
                      ($ :path {:transform "rotate(-45)"
                                :d "M-309.103 302.875h17.644v2.474h-17.644z"}))]
    ($ :<>
       ($ :g.ral6011
          ($ :rect {:x 3.6
                    :y -13.4
                    :width 14.3
                    :height 2.4})
          ($ :rect {:x 3.6
                    :y -11.2
                    :width 2.5
                    :height 29})
          ($ :rect {:x 15.4
                    :y -11.2
                    :width 2.5
                    :height 29})
          ($ :rect {:x 3.6
                    :y 12
                    :width 14.3
                    :height 19})
          ($ :rect {:x 3.6
                    :y 390
                    :width 2.5
                    :height 110})
          ($ :rect {:x 15.4
                    :y 390
                    :width 2.5
                    :height 110})
          ($ :g {:transform "translate(8.1)"} grid-cross)
          ($ :g {:transform "translate(8.1 16)"} grid-cross)
          ($ :g {:transform "translate(8.1 32)"} grid-cross)
          ($ :g {:transform "translate(8.1 48)"} grid-cross)
          ($ :g {:transform "translate(8.1 64)"} grid-cross))
       ($ :rect.ral3002 {:x 3.6
                         :y 31
                         :width 14.3
                         :height 360})
       ($ :rect.ral9002 {:x 3.6
                         :y 103
                         :width 14.3
                         :height 72})
       ($ :rect.ral9002 {:x 3.6
                         :y 247
                         :width 14.3
                         :height 72}))))

(defui sign []
  ($ :rect.ral9002 {:x 3.6
                    :y 398.5
                    :width 14.3
                    :height 22}))

(defui view [{:keys [signal]}]
  {:pre [(p/arg! ::spec/signal signal)]}
  (let [{{:keys [top-arm lower-arm sh1]
          :as main} :main
         {:keys [disk arm right-lights]
          :as distant} :distant} (arms signal)]
    ($ :g {:transform "translate(0,20)"}
       (when main
         ($ :<>
            ($ main-lights {:position top-arm
                            :top-color :red
                            :bottom-color :green})
            (when lower-arm
              ($ :g {:transform "translate(0 155)"}
                 ($ main-lights {:position lower-arm
                                 :bottom-color :yellow})))
            ($ main-pole)
            ($ sign)
            ($ main-arm {:position top-arm})
            (when lower-arm
              ($ :g {:transform "translate(0 153)"}
                 ($ main-arm {:position lower-arm})))
            ($ :g {:transform (if lower-arm "translate(-7 225)" "translate(-7 100)")}
               ($ hv-light/zs1+zs7-view main))))

       (when distant
         ($ :<>
            ($ :rect.ral6011 {:width 7.4
                              :height 185
                              :x 62
                              :y 259})
            ($ :g {:transform "translate(60 278)"}
               ($ main-lights {:position right-lights
                               :top-color :yellow
                               :bottom-color :green}))
            ($ :g {:transform "translate(71 400) rotate(180)"}
               ($ main-lights {:position (if (= :horizontal disk) :inclined :horizontal)
                               :top-color :yellow
                               :bottom-color :green}))
            ($ :g.semaphoreAnimate {:style {:transform (str "translate(66px,259px) " (when (= :horizontal disk) "rotateX(80deg)"))}}
               ($ :circle.ral9002 {:x 0 :y 0
                                   :r 37.6})
               ($ :circle.ral9005 {:x 0 :y 0
                                   :r 32.6})
               ($ :circle.ral2000 {:x 0 :y 0
                                   :r 28}))
            (when arm
              ($ :g.semaphoreAnimate {:style {:transform (str "translate(66px,358px) " (when (= :inclined arm) "rotate(-45deg)"))}}
                 ($ :path.ral9002 {:d "m -9,-55 18,0 0,100 -9,9 -9,-9 z"})
                 ($ :path.ral2000 {:d "m -5,-51 10,0 0,94 -5,5 -5,-5 z"
                                   :style {:stroke "#0e0e10"
                                           :stroke-width 2
                                           :stroke-linecap "butt"
                                           :stroke-linejoin "miter"
                                           :stroke-miterlimit 4}})
                 ($ :circle.screw {:x 0 :y 0
                                   :r 1.8})))))
       (when sh1
         ($ :g
            ($ :rect.ral9005 {:x -17
                              :y 443
                              :width 56
                              :height 56})
            ($ :circle.ral9002 {:cx 10 :cy 470.5 :r 20.5})
            ($ :rect.ral9005.semaphoreAnimate {:style {:transform (str "translate(11px,471px)" (when (= :inclined sh1) " rotate(-45deg)"))}
                                               :x -23
                                               :y -7.5
                                               :width 46
                                               :height 15}))))))
