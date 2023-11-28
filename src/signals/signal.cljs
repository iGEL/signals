(ns signals.signal
  (:require
   [cljs.spec.alpha :as s]
   [preo.core :as p]))

;; Aspects
(s/def ::aspect #{:stop :proceed :stop+zs1 :stop+zs7 :stop+sh1})
(s/def ::speed-limit (s/nilable (s/and int? pos?)))

;; Configuration
(s/def ::sh1? boolean?)
(s/def ::zs1? boolean?)
(s/def ::zs3 (s/nilable #{:display :sign}))
(s/def ::zs3v (s/nilable #{:display :sign}))
(s/def ::zs7? boolean?)
;; Does the Hv have a yellow light/second arm or does Hl have yellow light, green or yellow strips?
(s/def ::slow-speed-lights (s/coll-of int? :distinct true :kind vector?))
(s/def ::distant-addition (s/nilable #{:repeater :shortened-break-path}))

(s/def ::main (s/keys :req-un [::aspect ::speed-limit ::slow-speed-lights ::sh1? ::zs1? ::zs3 ::zs7?]))
(s/def ::distant (s/keys :req-un [::aspect ::speed-limit ::distant-addition ::slow-speed-lights ::zs3v]))

(s/def ::type #{:distant :main :combination})
(s/def ::system #{:ks :hv-light :hv-semaphore})
(s/def ::signal (s/keys :req-un [::type ::system]
                        :opt-un [::distant ::main]))

(defn stop-aspect?
  "Returns true when the given aspect is a stop aspect"
  [aspect]
  {:pre [(p/arg! (s/nilable ::aspect) aspect)]}
  (#{:stop :stop+zs1 :stop+zs7 :stop+sh1} aspect))

(defn main
  "Constructor for a main signal"
  [{:keys [aspect speed-limit slow-speed-lights sh1? zs1? zs3 zs7? system]
    :or {aspect :stop sh1? false zs1? false zs3 nil zs7? false slow-speed-lights []}}]
  {:post [(p/ret! ::signal %)]}
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
  [{:keys [aspect speed-limit slow-speed-lights distant-addition zs3v system]
    :or {aspect :stop zs3v nil slow-speed-lights []}}]
  {:post [(p/ret! ::signal %)]}
  {:system system
   :type :distant
   :distant {:aspect aspect
             :speed-limit speed-limit
             :distant-addition distant-addition
             :slow-speed-lights slow-speed-lights
             :zs3v zs3v}})

(defn combination
  "Constructor for a combination of a main & distant signal"
  [{{distant-aspect :aspect
     distant-speed-limit :speed-limit
     distant-slow-speed-lights :slow-speed-lights
     :keys [zs3v distant-addition]
     :or {distant-aspect :stop zs3v nil distant-slow-speed-lights []}} :distant
    {main-aspect :aspect
     main-speed-limit :speed-limit
     main-slow-speed-lights :slow-speed-lights
     :keys [sh1? zs1? zs3 zs7?]
     :or {main-aspect :stop sh1? false zs1? false zs3 nil zs7? false main-slow-speed-lights []}} :main
    system :system}]
  {:post [(p/ret! ::signal %)]}
  {:system system
   :type :combination
   :distant {:aspect distant-aspect
             :speed-limit distant-speed-limit
             :distant-addition distant-addition
             :slow-speed-lights distant-slow-speed-lights
             :zs3v zs3v}
   :main {:aspect main-aspect
          :speed-limit main-speed-limit
          :slow-speed-lights main-slow-speed-lights
          :sh1? sh1?
          :zs1? zs1?
          :zs3 zs3
          :zs7? zs7?}})
