(ns signals.main
  (:require
   [signals.ks :as ks]
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
     ($ zs3 {:signal signal})
     ($ :g {:transform "translate(3,65)"}
        ($ ks/view {:signal signal}))
     ($ :g {:transform "translate(0,168)"}
        ($ zs3v {:signal signal}))))

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

(defui speed-limit-btn [{:keys [set-state! speed-limit current]}]
  ($ button {:on-click #(set-state! :speed-limit speed-limit)
             :type (if (or (nil? speed-limit)
                           (> speed-limit 60))
                     "success"
                     "warning")
             :active? (= speed-limit (:speed-limit current))}
     (if speed-limit speed-limit "∞")))

(defui feature-btns [{:keys [set-state! state]}]
  ($ :div.btn-group
     ($ button {:on-click #(set-state! (update-in state [:main :sh1?] not))
                :type "info"
                :active? (-> state :main :sh1?)} "Sh1/Ra12")
     ($ button {:on-click #(set-state! (update-in state [:main :zs1?] not))
                :type "info"
                :active? (-> state :main :zs1?)} "Zs1")
     ($ button {:on-click #(set-state! (update-in state [:main :zs7?] not))
                :type "info"
                :active? (-> state :main :zs7?)} "Zs7")))

(defui speed-limit-btns [{:keys [set-state! state]}]
  ($ :<>
     ($ :div.btn-group
        ($ button {:on-click #(set-state! :zs3 nil)
                   :type "info"
                   :active? (nil? (:zs3 state))} "Kein")
        #_($ button {:on-click #(set-state! :zs3 :sign)
                     :type "info"
                     :active? (= :sign (:zs3 state))} "Tafel")
        ($ button {:on-click #(set-state! :zs3 :display)
                   :type "info"
                   :active? (= :display (:zs3 state))} "Lichtsignal"))
     ($ :div
        ($ :div.btn-group
           ($ speed-limit-btn {:set-state! set-state! :speed-limit nil :current state})
           ($ speed-limit-btn {:set-state! set-state! :speed-limit 150 :current state})
           ($ speed-limit-btn {:set-state! set-state! :speed-limit 140 :current state})
           ($ speed-limit-btn {:set-state! set-state! :speed-limit 130 :current state})
           ($ speed-limit-btn {:set-state! set-state! :speed-limit 120 :current state})))
     ($ :div
        ($ :div.btn-group
           ($ speed-limit-btn {:set-state! set-state! :speed-limit 110 :current state})
           ($ speed-limit-btn {:set-state! set-state! :speed-limit 100 :current state})
           ($ speed-limit-btn {:set-state! set-state! :speed-limit 90 :current state})
           ($ speed-limit-btn {:set-state! set-state! :speed-limit 80 :current state})
           ($ speed-limit-btn {:set-state! set-state! :speed-limit 70 :current state})))

     ($ :div
        ($ :div.btn-group
           ($ speed-limit-btn {:set-state! set-state! :speed-limit 60 :current state})
           ($ speed-limit-btn {:set-state! set-state! :speed-limit 50 :current state})
           ($ speed-limit-btn {:set-state! set-state! :speed-limit 40 :current state})
           ($ speed-limit-btn {:set-state! set-state! :speed-limit 30 :current state})
           ($ speed-limit-btn {:set-state! set-state! :speed-limit 20 :current state})
           ($ speed-limit-btn {:set-state! set-state! :speed-limit 10 :current state})))))

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
             ($ :th "Kombinationsignal")
             ($ :th "Hauptsignal"))
          ($ :tr
             ($ :td "Begriff")
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
             ($ :td "Features")
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
             ($ :td "Geschwindigkeit")
             ($ :td)
             ($ :td)
             ($ :td
                ($ speed-limit-btns {:set-state! (fn [attr val]
                                                   (set-combination! (assoc-in combination [:main attr] val))
                                                   (set-repeater! (assoc-in repeater [:distant (if (= attr :zs3) :zs3v attr)] val))
                                                   (set-distant! (assoc-in distant [:distant (if (= attr :zs3) :zs3v attr)] val)))
                                     :state (:main combination)}))
             ($ :td
                ($ speed-limit-btns {:set-state! (fn [attr val]
                                                   (set-main! (assoc-in main [:main attr] val))
                                                   (set-combination! (assoc-in combination [:distant (if (= attr :zs3) :zs3v attr)] val)))
                                     :state (:main main)}))))
       ($ :tbody
          ($ :tr
             ($ :td)
             ($ :td ($ signal {:signal distant}))
             ($ :td ($ signal {:signal repeater}))
             ($ :td ($ signal {:signal combination}))
             ($ :td ($ signal {:signal main})))))))

(defn render []
  (uix.dom/render ($ demo) (.getElementById js/document "app")))

(defn ^:dev/after-load reload []
  (render))

(defn ^:export init []
  (render))
