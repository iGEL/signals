(ns signals.demo.aspect
  (:require
   [signals.bootstrap :refer [button]]
   [signals.helper :refer [stop-aspect?]]
   [uix.core :as uix :refer [$ defui]]))

(defui buttons [{:keys [set-state! state]}]
  (let [main-state (:main state)
        current-aspect (:aspect main-state)
        sh1-active? (= :stop+sh1 current-aspect)
        zs1-active? (= :stop+zs1 current-aspect)
        zs7-active? (= :stop+zs7 current-aspect)]
    ($ :<>
       ($ button {:on-click #(set-state! {:aspect :proceed})
                  :active? (= :proceed current-aspect)
                  :type "success"} "Fahrt")
       ($ :div.btn-group
          ($ button {:on-click #(set-state! {:aspect :stop})
                     :active? (stop-aspect? current-aspect)
                     :type "danger"} "Halt")
          (when (:sh1? main-state)
            ($ button {:on-click #(set-state! {:aspect (if sh1-active? :stop :stop+sh1)})
                       :disabled? (not (stop-aspect? current-aspect))
                       :active? sh1-active?} "Sh1/Ra12"))
          (when (:zs1? main-state)
            ($ button {:on-click #(set-state! {:aspect (if zs1-active? :stop :stop+zs1)})
                       :disabled? (not (stop-aspect? current-aspect))
                       :active? zs1-active?} "Zs1"))
          (when (:zs7? main-state)
            ($ button {:on-click #(set-state! {:aspect (if zs7-active? :stop :stop+zs7)})
                       :disabled? (not (stop-aspect? current-aspect))
                       :active? zs7-active?} "Zs7"))))))
