(ns signals.spec
  (:require
   [cljs.spec.alpha :as s]))

;; Aspects
(s/def ::aspect #{:stop :proceed :stop+zs1 :stop+zs7 :stop+sh1})
(s/def ::speed-limit (s/nilable (s/and int? pos?)))

;; Configuration
(s/def ::sh1? boolean?)
(s/def ::zs1? boolean?)
(s/def ::zs3 (s/nilable #{:display :sign}))
(s/def ::zs7? boolean?)
;; Does the Hv have a yellow light/second arm or does Hl have yellow light, green or yellow strips?
(s/def ::slow-speed-lights (s/coll-of int? :distinct true :kind vector?))
(s/def ::distant-addition (s/nilable #{:repeater :shortened-break-path}))

(s/def ::main (s/keys :req-un [::aspect ::speed-limit ::slow-speed-lights ::sh1? ::zs1? ::zs3 ::zs7?]))
(s/def ::distant (s/keys :req-un [::aspect ::speed-limit ::distant-addition ::slow-speed-lights ::zs3]))

(s/def ::type #{:distant :main :combination})
(s/def ::system #{:ks :hl :hv-light :hv-semaphore})
(s/def ::signal (s/keys :req-un [::type ::system]
                        :opt-un [::distant ::main]))

