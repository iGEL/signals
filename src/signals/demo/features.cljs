(ns signals.demo.features
  (:require
   [signals.bootstrap :refer [button]]
   [uix.core :as uix :refer [$ defui]]))

(defui shortened-break-path-btn [{:keys [set-state! state]}]
  (let [active? (-> state :distant :distant-addition (= :shortened-break-path))]
    ($ button {:on-click #(set-state! (assoc-in state [:distant :distant-addition]
                                                (when-not active? :shortened-break-path)))
               :active? active?
               :type "info"
               :title "Bremsweg um mehr als 5% kürzer als der Regelabstand"}
       "Kurzer Bremsweg")))

(defui feature-btns [{:keys [set-state! state]}]
  ($ :div.btn-group
     ($ button {:on-click #(set-state! (update-in state [:main :sh1?] not))
                :type "info"
                :active? (-> state :main :sh1?)} "Sh1/Ra12")
     ($ button {:on-click #(set-state! (update-in state [:main :zs1?] not))
                :type "info"
                :active? (-> state :main :zs1?)} "Zs1")
     (when-not (= :hl (:system state))
       ($ button {:on-click #(set-state! (update-in state [:main :zs7?] not))
                  :type "info"
                  :active? (-> state :main :zs7?)} "Zs7"))))

(defui speed-limit-config-btns [{:keys [set-state! state]}]
  (let [slow-speed-lights (-> state :main :slow-speed-lights seq)
        active-40? (some #{40} slow-speed-lights)
        active-60? (some #{60} slow-speed-lights)
        active-100? (some #{100} slow-speed-lights)
        zs3-sign-active? (= :sign (-> state :main :zs3))
        zs3-display-active? (= :display (-> state :main :zs3))]
    ($ :<>
       (when (#{:hv-light :hv-semaphore} (:system state))
         ($ :div
            ($ button {:on-click #(set-state! {:slow-speed-lights (if active-40? [] [40])})
                       :type "info"
                       :active? active-40?}
               "Langsamfahrt")))
       ($ :div.btn-group
          (if (= :hl (:system state))
            ($ :<>
               ($ button {:on-click (fn []
                                      (set-state! {:slow-speed-lights (if active-100?
                                                                        (filterv #(not= 100 %) slow-speed-lights)
                                                                        (vec (conj slow-speed-lights 100)))}))
                          :type "info"
                          :active? active-100?}
                  "100")
               ($ button {:on-click (fn []
                                      (set-state! {:slow-speed-lights (if active-60?
                                                                        (filterv #(not= 60 %) slow-speed-lights)
                                                                        (vec (conj slow-speed-lights 60)))}))
                          :type "info"
                          :active? active-60?}
                  "60")
               ($ button {:on-click (fn []
                                      (set-state! {:slow-speed-lights (if active-40?
                                                                        (filterv #(not= 40 %) slow-speed-lights)
                                                                        (vec (conj slow-speed-lights 40)))}))
                          :type "info"
                          :active? active-40?}
                  "40"))
            ($ :<>
               ($ button {:on-click (fn []
                                      (set-state! {:zs3 (if zs3-sign-active? nil :sign)
                                                   :speed-limit (or (-> state :main :speed-limit)
                                                                    60)}))
                          :type "info"
                          :active? zs3-sign-active?} "Zs3 Tafel")
               ($ button {:on-click #(set-state! {:zs3 (if zs3-display-active? nil :display)})
                          :type "info"
                          :active? zs3-display-active?} "Zs3 Lichtsignal")))))))

(defui base [{:keys [main set-main! distant set-distant! set-both!]}]
  ($ :<>
     ($ feature-btns {:set-state! set-main! :state main})
     ($ :div
        ($ shortened-break-path-btn {:set-state! set-distant! :state distant}))
     ($ :div
        ($ speed-limit-config-btns {:set-state! set-both! :state main}))))
