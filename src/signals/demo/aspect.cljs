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
                  :title "Stelle das Signal auf Fahrt"
                  :active? (= :proceed current-aspect)
                  :type "success"} "Fahrt")
       ($ :div.btn-group
          (when (:sh1? main-state)
            ($ button {:on-click #(set-state! {:aspect (if sh1-active? :stop :stop+sh1)})
                       :title (if sh1-active?
                                "Deaktiviere das Sh1/Ra12"
                                "Stelle das Signal auf Halt mit Sh1 bzw. Ra12 aktiv")
                       :active? sh1-active?
                       :type "light"} "Sh1/Ra12"))
          ($ button {:on-click #(set-state! {:aspect :stop})
                     :title "Stelle das Signal auf Halt"
                     :active? (stop-aspect? current-aspect)
                     :type "danger"} "Halt")
          (when (:zs1? main-state)
            ($ button {:on-click #(set-state! {:aspect (if zs1-active? :stop :stop+zs1)})
                       :title (if zs1-active?
                                "Deaktiviere das Zs1"
                                "Stelle das Signal auf Halt und aktiviere das Zs1")
                       :active? zs1-active?
                       :type "danger"} "Zs1"))
          (when (:zs7? main-state)
            ($ button {:on-click #(set-state! {:aspect (if zs7-active? :stop :stop+zs7)})
                       :title (if zs7-active?
                                "Deaktiviere das Zs7"
                                "Stelle das Signal auf Halt und aktiviere das Zs7")
                       :active? zs7-active?
                       :type "danger"} "Zs7")))
       (when (and (:indicator? main-state)
                  (not= :hv-semaphore (:system state)))
         ($ :div
            ($ button {:on-click #(set-state! {:aspect :off})
                       :title "Betrieblich abschalten"
                       :active? (= :off current-aspect)
                       :type "light"}
               "Aus"))))))
