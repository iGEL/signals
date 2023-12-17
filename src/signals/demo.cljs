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
                                (set-combination! (update combination :distant merge attributes)))]
    ($ :table.table
       ($ :thead
          ($ :tr
             ($ :th)
             ($ :th "Vorsignal")
             ($ :th "Wiederholer")
             ($ :th "Mehrabschnittssignal")
             ($ :th "Hauptsignal")))
       ($ :tbody
          ($ :tr
             ($ :th)
             ($ :td ($ signal {:signal distant}))
             ($ :td ($ signal {:signal repeater}))
             ($ :td ($ signal {:signal combination}))
             ($ :td ($ signal {:signal main}))))
       ($ :tfoot
          ($ :tr
             ($ :th "System")
             ($ :td {:col-span 4}
                ($ :div
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
                              :active? (= :hl (:system main))} "Hl"))
                (when (= :hv-semaphore (:system main))
                  ($ :div.alert.alert-warning {:role "alert"}
                     "Positionierung der einzelnen Signale und Animationen müssen noch überarbeitet werden."))))
          ($ :tr
             ($ :th "Begriff")
             ($ :td)
             ($ :td)
             ($ :td
                ($ aspect/buttons {:set-state! set-combination+distants!
                                   :state combination}))
             ($ :td
                ($ aspect/buttons {:set-state! set-main+combination!
                                   :state main})))
          ($ :tr
             ($ :th "Features")
             ($ :td)
             ($ :td)
             ($ :td
                ($ features/base {:set-main! set-combination!
                                  :set-distant! set-distant!
                                  :set-both! set-combination+distants!
                                  :main combination
                                  :distant distant}))
             ($ :td
                ($ features/base {:set-main! set-main!
                                  :set-distant! set-combination!
                                  :set-both! set-main+combination!
                                  :main main
                                  :distant combination})))
          ($ :tr
             ($ :th "Geschwindigkeits-"
                ($ :br)
                "begrenzung")
             ($ :td)
             ($ :td)
             ($ :td
                ($ speed-limit/buttons {:set-state! set-combination+distants!
                                        :state combination}))
             ($ :td
                ($ speed-limit/buttons {:set-state! set-main+combination!
                                        :state main})))
          ($ :tr
             ($ :td {:col-span 5}
                ($ bug-report/base {:state {:distant distant
                                            :repeater repeater
                                            :combination combination
                                            :main main}})))))))
