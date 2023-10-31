(ns signals.signal
  (:require
   [cljs.spec.alpha :as s]
   [preo.core :as p]))

;; Aspects
;; 0 = stop (red), nil = unlimited (green), other = limited speed
(s/def ::speed-limit (s/nilable (s/and int? #(not (neg? %)))))
(s/def ::stop-override (s/nilable #{:zs1 :zs7 :sh1}))

;; Configuration
(s/def ::sh1? boolean?)
(s/def ::zs1? boolean?)
(s/def ::zs3? boolean?)
(s/def ::zs3v? boolean?)
(s/def ::zs7? boolean?)
;; Does the Hv have a yellow light/second arm or does Hl have yellow light, green or yellow strips?
(s/def ::slow-speed-lights (s/coll-of int? :distinct true :kind vector?))
(s/def ::distant-addition (s/nilable #{:repeater :shortened-break-path}))

(s/def ::main (s/keys :req-un [::speed-limit ::stop-override ::slow-speed-lights ::sh1? ::zs1? ::zs3? ::zs7?]))
(s/def ::distant (s/keys :req-un [::speed-limit ::distant-addition ::slow-speed-lights ::zs3v?]))

(s/def ::type #{:distant :main :combination})
(s/def ::system #{:ks})
(s/def ::signal (s/keys :req-un [::type ::system]
                        :opt-un [::distant ::main]))

(defn main
  "Constructor for a main signal"
  [{:keys [speed-limit stop-override sh1? zs1? zs3? zs7? system]
    :or {speed-limit 0
         sh1? false zs1? false zs3? false zs7? false}}]
  {:post [(p/ret! ::signal %)]}
  {:system system
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
  [{:keys [speed-limit distant-addition zs3v? system]
    :or {speed-limit 0
         zs3v? false}}]
  {:post [(p/ret! ::signal %)]}
  {:system system
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
          sh1? false zs1? false zs3? false zs7? false}} :main
    system :system}]
  {:post [(p/ret! ::signal %)]}
  {:system system
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
