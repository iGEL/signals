(ns signals.signal
  (:require
   [preo.core :as p]
   [signals.hl :as hl]
   [signals.hv-light :as hv-light]
   [signals.hv-semaphore :as hv-semaphore]
   [signals.ks :as ks]
   [signals.ne :as ne]
   [signals.spec :as spec]
   [signals.zs3 :refer [zs3 zs3v]]
   [uix.core :as uix :refer [$ defui]]))

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

(defui signal [{:keys [signal]}]
  (case (:system signal)
    :ks ($ :<>
           ($ :g {:transform "translate(16,0)"}
              ($ zs3 {:signal signal}))
           ($ :g {:transform "translate(19,65)"}
              ($ ks/view {:signal signal}))
           ($ :g {:transform "translate(16,168)"}
              ($ zs3v {:signal signal}))
           (when (and (= :distant (:type signal))
                      (not (= :repeater (-> signal :distant :distant-addition))))
             ($ :g {:transform "translate(33,500)"}
                ($ ne/ne2))))

    :hl ($ :<> ($ hl/view {:signal signal})
           (when (and (= :distant (:type signal))
                      (not (= :repeater (-> signal :distant :distant-addition))))
             ($ :g {:transform "translate(17,500)"}
                ($ ne/ne2 {:shortened-break-path? (-> signal hl/lights :shortened-break-path?)}))))
    :hv-light ($ :<>
                 ($ :g {:transform "translate(19,0)"}
                    ($ zs3 {:signal signal}))
                 ($ :g {:transform "translate(3,65)"}
                    ($ hv-light/view {:signal signal}))
                 ($ :g {:transform "translate(19,350)"}
                    ($ zs3v {:signal signal}))
                 (when (and (= :distant (:type signal))
                            (not (= :repeater (-> signal :distant :distant-addition))))
                   ($ :g {:transform "translate(33,500)"}
                      ($ ne/ne2))))
    :hv-semaphore ($ :<>
                     ($ :g {:transform "translate(19,0)"}
                        ($ zs3 {:signal signal}))
                     ($ :g {:transform "translate(3,65)"}
                        ($ hv-semaphore/view {:signal signal}))
                     ($ :g {:transform "translate(19,350)"}
                        ($ zs3v {:signal signal}))
                     (when (and (= :distant (:type signal))
                                (not (= :repeater (-> signal :distant :distant-addition))))
                       ($ :g {:transform "translate(33,500)"}
                          ($ ne/ne2 {:shortened-break-path? (-> signal hv-semaphore/arms :distant :shortened-break-path?)}))))))

(defn main
  "Constructor for a main signal"
  [{:keys [aspect speed-limit slow-speed-lights sh1? zs1? zs3 zs7? system]
    :or {aspect :stop sh1? false zs1? false zs3 nil zs7? false slow-speed-lights []}}]
  {:post [(p/ret! ::spec/signal %)]}
  {:system system
   :type :main
   :main {:aspect aspect
          :speed-limit speed-limit
          :slow-speed-lights slow-speed-lights
          :sh1? sh1?
          :zs1? zs1?
          :zs3 zs3
          :zs7? zs7?}})

(defn distant
  "Constructor for a distant signal"
  [{:keys [aspect speed-limit slow-speed-lights distant-addition zs3 system]
    :or {aspect :stop zs3 nil slow-speed-lights []}}]
  {:post [(p/ret! ::spec/signal %)]}
  {:system system
   :type :distant
   :distant {:aspect aspect
             :speed-limit speed-limit
             :distant-addition distant-addition
             :slow-speed-lights slow-speed-lights
             :zs3 zs3}})

(defn combination
  "Constructor for a combination of a main & distant signal"
  [{{distant-aspect :aspect
     distant-speed-limit :speed-limit
     distant-slow-speed-lights :slow-speed-lights
     zs3v :zs3
     :keys [distant-addition]
     :or {distant-aspect :stop distant-slow-speed-lights []}} :distant
    {main-aspect :aspect
     main-speed-limit :speed-limit
     main-slow-speed-lights :slow-speed-lights
     :keys [sh1? zs1? zs3 zs7?]
     :or {main-aspect :stop sh1? false zs1? false zs3 nil zs7? false main-slow-speed-lights []}} :main
    system :system}]
  {:post [(p/ret! ::spec/signal %)]}
  {:system system
   :type :combination
   :distant {:aspect distant-aspect
             :speed-limit distant-speed-limit
             :distant-addition distant-addition
             :slow-speed-lights distant-slow-speed-lights
             :zs3 zs3v}
   :main {:aspect main-aspect
          :speed-limit main-speed-limit
          :slow-speed-lights main-slow-speed-lights
          :sh1? sh1?
          :zs1? zs1?
          :zs3 zs3
          :zs7? zs7?}})
