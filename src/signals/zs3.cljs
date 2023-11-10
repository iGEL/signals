(ns signals.zs3
  (:require
   [preo.core :as p]
   [signals.display :as display]
   [signals.signal :as signal]
   [uix.core :refer [$ defui]]))

(defui zs3 [{{{:keys [zs3 speed-limit aspect]} :main
              :as signal} :signal}]
  {:pre [(p/arg! ::signal/signal signal)]}
  (case zs3
    :display ($ :g {:transform "translate(8,19)"}
                ($ display/view {:color :white
                                 :value (when (and (not (signal/stop-aspect? aspect))
                                                   speed-limit)
                                          (/ speed-limit 10))}))
    nil))

(defui zs3v [{{{:keys [zs3v speed-limit]
                distant-aspect :aspect} :distant
               {main-aspect :aspect} :main
               :as signal} :signal}]
  {:pre [(p/arg! ::signal/signal signal)]}
  (case zs3v
    :display ($ :g {:transform "translate(8,19)"}
                ($ display/view {:color :yellow
                                 :value (when (and (not (signal/stop-aspect? distant-aspect))
                                                   (not (signal/stop-aspect? main-aspect))
                                                   speed-limit)
                                          (/ speed-limit 10))}))
    nil))
