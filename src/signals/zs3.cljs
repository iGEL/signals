(ns signals.zs3
  (:require
   [preo.core :as p]
   [signals.display :as display]
   [signals.helper :refer [stop-aspect?]]
   [signals.spec :as spec]
   [signals.zs3.sign :as zs3.sign]
   [uix.core :refer [$ defui]]))

(defui zs3 [{{{:keys [zs3 speed-limit aspect]} :main
              :as signal} :signal}]
  {:pre [(p/arg! ::spec/signal signal)]}
  (case zs3
    :display ($ :g {:transform "translate(8,19)"}
                ($ display/view {:color :white
                                 :value (when (and (not (stop-aspect? aspect))
                                                   speed-limit)
                                          (/ speed-limit 10))}))
    :sign ($ zs3.sign/view {:position :main
                            :value (/ speed-limit 10)})
    nil))

(defui zs3v [{{{:keys [zs3v speed-limit]
                distant-aspect :aspect} :distant
               {main-aspect :aspect} :main
               :as signal} :signal}]
  {:pre [(p/arg! ::spec/signal signal)]}
  (case zs3v
    :display ($ :g {:transform "translate(8,19)"}
                ($ display/view {:color :yellow
                                 :value (when (and (not (stop-aspect? distant-aspect))
                                                   (not (stop-aspect? main-aspect))
                                                   speed-limit)
                                          (/ speed-limit 10))}))
    :sign ($ zs3.sign/view {:position :distant
                            :value (/ speed-limit 10)})
    nil))
