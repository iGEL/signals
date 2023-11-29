(ns signals.main
  (:require
   [signals.hl :as hl]
   [signals.hv-light :as hv-light]
   [signals.hv-semaphore :as hv-semaphore]
   [signals.ks :as ks]
   [signals.ne :as ne]
   [signals.signal :as signal]
   [signals.zs3 :refer [zs3 zs3v]]
   [uix.core :as uix :refer [$ defui]]
   [uix.dom :as uix.dom]))

(defui radial-gradient [{:keys [id stop-color1 stop-color2]}]
  ($ :radialGradient {:id id}
     ($ :stop {:stopColor stop-color1
               :offset "0.05"})
     ($ :stop {:stopColor stop-color2
               :offset "0.9"})))

(defui defs []
  ($ :defs
     ($ radial-gradient {:id "green-gradient"
                         :stop-color1 "#33ff6d"
                         :stop-color2 "#00bd4a"})
     ($ radial-gradient {:id "red-gradient"
                         :stop-color1 "#ff3763"
                         :stop-color2 "#da012a"})
     ($ radial-gradient {:id "orange-gradient"
                         :stop-color1 "#ffc955"
                         :stop-color2 "#fc8e00"})
     ($ radial-gradient {:id "yellow-gradient"
                         :stop-color1 "#ffe060"
                         :stop-color2 "#fac412"})
     ($ radial-gradient {:id "white-gradient"
                         :stop-color1 "#fffaef"
                         :stop-color2 "#ebe6d8"})))

(defui signal [{:keys [signal]}]
  ($ :svg {:version "1.1"
           :viewBox "0 0 140 600"
           :width "200"
           :height "600"}
     ($ defs)
     (case (:system signal)
       :ks ($ :<>
              ($ :g {:transform "translate(16,0)"})
              ($ zs3 {:signal signal})
              ($ :g {:transform "translate(19,65)"}
                 ($ ks/view {:signal signal}))
              ($ :g {:transform "translate(16,168)"}
                 ($ zs3v {:signal signal}))
              (when (and (= :distant (:type signal))
                         (not (= :repeater (-> signal :distant :distant-addition))))
                ($ :g {:transform "translate(33,500)"}
                   ($ ne/ne2))))
       :hl ($ hl/view {:signal signal})
       :hv-light ($ :<>
                    ($ :g {:transform "translate(19,0)"}
                       ($ zs3 {:signal signal}))
                    ($ :g {:transform "translate(3,65)"}
                       ($ hv-light/view {:signal signal}))
                    ($ :g {:transform "translate(19,350)"}
                       ($ zs3v {:signal signal}))
                    (when (and (= :distant (:type signal))
                               (not (= :repeater (-> signal :distant :distant-addition))))
                      ($ :g {:transform "translate(33,500)"}
                         ($ ne/ne2))))
       :hv-semaphore ($ :<>
                        ($ :g {:transform "translate(19,0)"}
                           ($ zs3 {:signal signal}))
                        ($ :g {:transform "translate(3,65)"}
                           ($ hv-semaphore/view {:signal signal}))
                        ($ :g {:transform "translate(19,350)"}
                           ($ zs3v {:signal signal}))
                        (when (and (= :distant (:type signal))
                                   (not (= :repeater (-> signal :distant :distant-addition))))
                          ($ :g {:transform "translate(33,500)"}
                             ($ ne/ne2)))))))

(defui button [{:keys [on-click active? children disabled? type title]
                :or {type "primary"}}]
  ($ :<>
     ($ :button.btn.btn-sm
        {:class [(str "btn-outline-" type) (when active? "active")]
         :disabled disabled?
         :on-click on-click
         :title title}
        children)
     " "))

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

(defn- speed-limit-available? [state limit]
  (case (:system state)
    :ks (ks/speed-limit-available? state limit)
    :hl (hl/speed-limit-available? state limit)
    :hv-light (hv-light/speed-limit-available? state limit)
    :hv-semaphore (hv-light/speed-limit-available? state limit)))

(defui speed-limit-btn [{:keys [speed-limit set-state! state]}]
  ($ button {:on-click #(set-state! :speed-limit speed-limit)
             :type (if (or (nil? speed-limit)
                           (> speed-limit 60))
                     "success"
                     "warning")
             :active? (= speed-limit (-> state :main :speed-limit))
             :disabled? (not (speed-limit-available? state speed-limit))}
     (if speed-limit speed-limit "∞")))

(defui speed-limit-btns [{:keys [set-state! state]}]
  ($ :<>
     (when (#{:hv-light :hv-semaphore} (:system state))
       ($ :div
          (let [active? (-> state :main :slow-speed-lights seq)]
            ($ button {:on-click #(set-state! :slow-speed-lights (if active? [] [40]))
                       :type "info"
                       :active? active?}
               "Langsamfahrt"))))
     ($ :div.btn-group
        ($ button {:on-click #(set-state! :zs3 nil)
                   :type "info"
                   :active? (nil? (-> state :main :zs3))} "Kein")
        (if (= :hl (:system state))
          (let [slow-speed-lights (-> state :main :slow-speed-lights)
                active-40? (some #{40} slow-speed-lights)
                active-60? (some #{60} slow-speed-lights)
                active-100? (some #{100} slow-speed-lights)]
            ($ :<>
               ($ button {:on-click (fn []
                                      (set-state! :slow-speed-lights (if active-40?
                                                                       (filterv #(not= 40 %) slow-speed-lights)
                                                                       (conj slow-speed-lights 40))))
                          :type "info"
                          :active? active-40?}
                  "40")
               ($ button {:on-click (fn []
                                      (set-state! :slow-speed-lights (if active-60?
                                                                       (filterv #(not= 60 %) slow-speed-lights)
                                                                       (conj slow-speed-lights 60))))
                          :type "info"
                          :active? active-60?}
                  "60")
               ($ button {:on-click (fn []
                                      (set-state! :slow-speed-lights (if active-100?
                                                                       (filterv #(not= 100 %) slow-speed-lights)
                                                                       (conj slow-speed-lights 100))))
                          :type "info"
                          :active? active-100?}
                  "100")))
          ($ :<>
             ($ button {:on-click (fn []
                                    (set-state! :zs3 :sign)
                                    (when-not (-> state :main :speed-limit)
                                      (set-state! :speed-limit 60)))
                        :type "info"
                        :active? (= :sign (-> state :main :zs3))} "Tafel")
             ($ button {:on-click #(set-state! :zs3 :display)
                        :type "info"
                        :active? (= :display (-> state :main :zs3))} "Lichtsignal"))))
     ($ :div
        ($ :div.btn-group
           ($ speed-limit-btn {:speed-limit nil :set-state! set-state! :state state})
           ($ speed-limit-btn {:speed-limit 150 :set-state! set-state! :state state})
           ($ speed-limit-btn {:speed-limit 140 :set-state! set-state! :state state})
           ($ speed-limit-btn {:speed-limit 130 :set-state! set-state! :state state})
           ($ speed-limit-btn {:speed-limit 120 :set-state! set-state! :state state})))
     ($ :div
        ($ :div.btn-group
           ($ speed-limit-btn {:speed-limit 110 :set-state! set-state! :state state})
           ($ speed-limit-btn {:speed-limit 100 :set-state! set-state! :state state})
           ($ speed-limit-btn {:speed-limit 90 :set-state! set-state! :state state})
           ($ speed-limit-btn {:speed-limit 80 :set-state! set-state! :state state})
           ($ speed-limit-btn {:speed-limit 70 :set-state! set-state! :state state})))

     ($ :div
        ($ :div.btn-group
           ($ speed-limit-btn {:speed-limit 60 :set-state! set-state! :state state})
           ($ speed-limit-btn {:speed-limit 50 :set-state! set-state! :state state})
           ($ speed-limit-btn {:speed-limit 40 :set-state! set-state! :state state})
           ($ speed-limit-btn {:speed-limit 30 :set-state! set-state! :state state})
           ($ speed-limit-btn {:speed-limit 20 :set-state! set-state! :state state})
           ($ speed-limit-btn {:speed-limit 10 :set-state! set-state! :state state})))))

(defui demo []
  (let [[distant set-distant!] (uix/use-state (signal/distant {:system :ks}))
        [repeater set-repeater!] (uix/use-state (signal/distant {:system :ks
                                                                 :distant-addition :repeater}))
        [combination set-combination!] (uix/use-state (signal/combination {:system :ks}))
        set-combination-aspect! (fn [aspect]
                                  (set-combination! (assoc-in combination [:main :aspect] aspect))
                                  (set-repeater! (assoc-in repeater [:distant :aspect] aspect))
                                  (set-distant! (assoc-in distant [:distant :aspect] aspect)))
        [main set-main!] (uix/use-state (signal/main {:system :ks}))
        set-main-aspect! (fn [aspect]
                           (set-main! (assoc-in main [:main :aspect] aspect))
                           (set-combination! (assoc-in combination [:distant :aspect] aspect)))]
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
                (when (= :hv-semaphore (:system main))
                  ($ :div.alert.alert-warning {:role "alert"}
                     "Positionierung der einzelnen Signale und Animationen sind noch unfertig"))
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
                           :active? (= :hl (:system main))} "Hl")))
          ($ :tr
             ($ :th "Begriff")
             ($ :td)
             ($ :td)
             (let [combination-main (:main combination)
                   current-aspect (:aspect combination-main)]
               ($ :td
                  ($ button {:on-click #(set-combination-aspect! :proceed)
                             :active? (= :proceed current-aspect)
                             :type "success"} "Fahrt")
                  ($ :div.btn-group
                     ($ button {:on-click #(set-combination-aspect! :stop)
                                :active? (signal/stop-aspect? current-aspect)
                                :type "danger"} "Halt")
                     (when (:sh1? combination-main)
                       ($ button {:on-click #(set-combination-aspect! (if (= :stop+sh1 current-aspect)
                                                                        :stop
                                                                        :stop+sh1))
                                  :disabled? (not (signal/stop-aspect? current-aspect))
                                  :active? (= :stop+sh1 current-aspect)} "Sh1/Ra12"))
                     (when (:zs1? combination-main)
                       ($ button {:on-click #(set-combination-aspect! (if (= :stop+zs1 current-aspect)
                                                                        :stop
                                                                        :stop+zs1))
                                  :disabled? (not (signal/stop-aspect? current-aspect))
                                  :active? (= :stop+zs1 current-aspect)} "Zs1"))
                     (when (:zs7? combination-main)
                       ($ button {:on-click #(set-combination-aspect! (if (= :stop+zs7 current-aspect)
                                                                        :stop
                                                                        :stop+zs7))
                                  :disabled? (not (signal/stop-aspect? current-aspect))
                                  :active? (= :stop+zs7 current-aspect)} "Zs7")))))
             (let [main-state (:main main)
                   current-aspect (:aspect main-state)]
               ($ :td
                  ($ button {:on-click #(set-main-aspect! :proceed)
                             :active? (= :proceed current-aspect)
                             :type "success"} "Fahrt")
                  ($ :div.btn-group

                     ($ button {:on-click #(set-main-aspect! :stop)
                                :active? (signal/stop-aspect? current-aspect)
                                :type "danger"} "Halt")
                     (when (:sh1? main-state)
                       ($ button {:on-click #(set-main-aspect! (if (= :stop+sh1 current-aspect)
                                                                 :stop
                                                                 :stop+sh1))
                                  :disabled? (not (signal/stop-aspect? current-aspect))
                                  :active? (= :stop+sh1 current-aspect)} "Sh1/Ra12"))
                     (when (:zs1? main-state)
                       ($ button {:on-click #(set-main-aspect! (if (= :stop+zs1 current-aspect)
                                                                 :stop
                                                                 :stop+zs1))
                                  :disabled? (not (signal/stop-aspect? current-aspect))
                                  :active? (= :stop+zs1 current-aspect)} "Zs1"))
                     (when (:zs7? main-state)
                       ($ button {:on-click #(set-main-aspect! (if (= :stop+zs7 current-aspect)
                                                                 :stop
                                                                 :stop+zs7))
                                  :disabled? (not (signal/stop-aspect? current-aspect))
                                  :active? (= :stop+zs7 current-aspect)} "Zs7"))))))
          ($ :tr
             ($ :th "Features")
             ($ :td)
             ($ :td)
             ($ :td
                ($ feature-btns {:set-state! set-combination! :state combination})
                ($ :div
                   ($ shortened-break-path-btn {:set-state! set-distant! :state distant})))
             ($ :td
                ($ feature-btns {:set-state! set-main! :state main})
                ($ :div
                   ($ shortened-break-path-btn {:set-state! set-combination! :state combination}))))
          ($ :tr
             ($ :th "Zs3")
             ($ :td)
             ($ :td)
             ($ :td
                ($ speed-limit-btns {:set-state! (fn [attr val]
                                                   (set-combination! (assoc-in combination [:main attr] val))
                                                   (set-repeater! (assoc-in repeater [:distant (if (= attr :zs3) :zs3v attr)] val))
                                                   (set-distant! (assoc-in distant [:distant (if (= attr :zs3) :zs3v attr)] val)))
                                     :state combination}))
             ($ :td
                ($ speed-limit-btns {:set-state! (fn [attr val]
                                                   (set-main! (assoc-in main [:main attr] val))
                                                   (set-combination! (assoc-in combination [:distant (if (= attr :zs3) :zs3v attr)] val)))
                                     :state main})))))))

(defn render []
  (uix.dom/render ($ demo) (.getElementById js/document "app")))

(defn ^:dev/after-load reload []
  (render))

(defn ^:export init []
  (render))
