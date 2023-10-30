(ns signals.signal
  (:require
   [cljs.spec.alpha :as s]))

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
