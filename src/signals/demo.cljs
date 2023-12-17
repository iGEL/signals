(ns signals.demo
  (:require
   [signals.bootstrap :refer [button]]
   [signals.demo.aspect :as aspect]
   [signals.demo.bug-report :as bug-report]
   [signals.demo.features :as features]
   [signals.demo.speed-limit :as speed-limit]
   [signals.signal :as signal]
   [uix.core :as uix :refer [$ defui]]))

(defui signal [{:keys [signal]}]
  ($ :svg {:version "1.1"
           :viewBox "0 0 140 600"
           :width "200"
           :height "600"}
     ($ signal/defs)
     ($ signal/signal {:signal signal})))

(defui demo []
  (let [[distant set-distant!] (uix/use-state (signal/distant {:system :ks}))
        [repeater set-repeater!] (uix/use-state (signal/distant {:system :ks
                                                                 :distant-addition :repeater}))
        [combination set-combination!] (uix/use-state (signal/combination {:system :ks}))
        [main set-main!] (uix/use-state (signal/main {:system :ks}))
        set-combination+distants! (fn [attributes]
                                    (set-combination! (update combination :main merge attributes))
                                    (set-repeater! (update repeater :distant merge attributes))
                                    (set-distant! (update distant :distant merge attributes)))
        set-main+combination! (fn [attributes]
                                (set-main! (update main :main merge attributes))
                                (set-combination! (update combination :distant merge attributes)))
        [show-usage-hint? set-show-usage-hint!] (uix/use-state true)]
    ($ :<>
       (when show-usage-hint?
         ($ :div.alert.alert-primary.alert-dismissible {:style {:max-width "1050px"}}
            "Unter den Hauptsignalen kannst du das jeweilige Signal konfigurieren. Die vorhergehenden Vorsignale zeigen den zu erwartenden Begriff an."
            ($ :button.btn-close {:type "button"
                                  :aria-label "Close"
                                  :on-click #(set-show-usage-hint! false)})))
       ($ :div.demo-container
          ($ :div.demo-control-label "Signal-System")
          ($ :div.demo-control-system
             ($ button {:on-click (fn []
                                    (set-distant! (assoc distant :system :ks))
                                    (set-repeater! (assoc repeater :system :ks))
                                    (set-combination! (assoc combination :system :ks))
                                    (set-main! (assoc main :system :ks)))
                        :active? (= :ks (:system main))} "Ks")
             ($ button {:on-click (fn []
                                    (set-distant! (assoc distant :system :hv-light))
                                    (set-repeater! (assoc repeater :system :hv-light))
                                    (set-combination! (assoc combination :system :hv-light))
                                    (set-main! (assoc main :system :hv-light)))
                        :active? (= :hv-light (:system main))} "H/V Lichtsignal")
             ($ button {:on-click (fn []
                                    (set-distant! (assoc distant :system :hv-semaphore))
                                    (set-repeater! (assoc repeater :system :hv-light))
                                    (set-combination! (assoc combination :system :hv-semaphore))
                                    (set-main! (assoc main :system :hv-semaphore)))
                        :active? (= :hv-semaphore (:system main))} "H/V Formsignal")
             ($ button {:on-click (fn []
                                    (set-distant! (assoc distant :system :hl))
                                    (set-repeater! (assoc repeater :system :hl))
                                    (set-combination! (assoc combination :system :hl))
                                    (set-main! (assoc main :system :hl)))
                        :active? (= :hl (:system main))} "Hl")
             ($ bug-report/base {:state {:distant distant
                                         :repeater repeater
                                         :combination combination
                                         :main main}})))
       ($ :div.demo-container
          ($ :div.demo-signal
             ($ :h3.demo-signal-label "Vorsignal")
             ($ :div ($ signal {:signal distant})))
          ($ :div.demo-signal
             ($ :h3.demo-signal-label "Wiederholer")
             ($ :div ($ signal {:signal repeater})))
          ($ :div.demo-signal
             ($ :h3.demo-signal-label "Mehrabschnitt")
             ($ :div ($ signal {:signal combination}))
             ($ :div
                ($ :div.demo-control-aspect
                   ($ aspect/buttons {:set-state! set-combination+distants!
                                      :state combination}))
                ($ :div.demo-control-speed
                   ($ speed-limit/buttons {:set-state! set-combination+distants!
                                           :state combination}))
                ($ :div.demo-control-features
                   ($ features/base {:set-main! set-combination!
                                     :set-distant! set-distant!
                                     :set-both! set-combination+distants!
                                     :main combination
                                     :distant distant}))))
          ($ :div.demo-signal
             ($ :h3.demo-signal-label "Hauptsignal")
             ($ :div ($ signal {:signal main}))
             ($ :div
                ($ :div.demo-control-aspect
                   ($ aspect/buttons {:set-state! set-main+combination!
                                      :state main}))
                ($ :div.demo-control-speed
                   ($ speed-limit/buttons {:set-state! set-main+combination!
                                           :state main}))
                ($ :div.demo-control-features
                   ($ features/base {:set-main! set-main!
                                     :set-distant! set-combination!
                                     :set-both! set-main+combination!
                                     :main main
                                     :distant combination}))))))))
