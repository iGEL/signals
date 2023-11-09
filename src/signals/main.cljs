(ns signals.main
  (:require
   [signals.ks :as ks]
   [signals.signal :as signal]
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
     ($ ks/view {:signal signal})))

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
             (let [combination-main (:main combination)
                   shortened-break-path-active? (-> distant :distant :distant-addition (= :shortened-break-path))]
               ($ :td
                  ($ :div.btn-group
                     ($ button {:on-click #(set-combination! (update-in combination [:main :sh1?] not))
                                :type "info"
                                :active? (:sh1? combination-main)} "Sh1/Ra12")
                     ($ button {:on-click #(set-combination! (update-in combination [:main :zs1?] not))
                                :type "info"
                                :active? (:zs1? combination-main)} "Zs1")
                     ($ button {:on-click #(set-combination! (update-in combination [:main :zs7?] not))
                                :type "info"
                                :active? (:zs7? combination-main)} "Zs7"))
                  ($ :div
                     ($ button {:on-click #(set-distant! (assoc-in distant [:distant :distant-addition] (when-not shortened-break-path-active? :shortened-break-path)))
                                :active? shortened-break-path-active?
                                :type "info"
                                :title "Bremsweg um mehr als 5% kürzer als der Regelabstand"}
                        "Kurzer Bremsweg"))))
             (let [main-state (:main main)
                   shortened-break-path-active? (-> combination :distant :distant-addition (= :shortened-break-path))]
               ($ :td
                  ($ :div.btn-group
                     ($ button {:on-click #(set-main! (update-in main [:main :sh1?] not))
                                :type "info"
                                :active? (:sh1? main-state)} "Sh1/Ra12")
                     ($ button {:on-click #(set-main! (update-in main [:main :zs1?] not))
                                :type "info"
                                :active? (:zs1? main-state)} "Zs1")
                     ($ button {:on-click #(set-main! (update-in main [:main :zs7?] not))
                                :type "info"
                                :active? (:zs7? main-state)} "Zs7"))
                  ($ :div
                     ($ button {:on-click #(set-combination! (assoc-in combination [:distant :distant-addition] (when-not shortened-break-path-active? :shortened-break-path)))
                                :active? shortened-break-path-active?
                                :type "info"
                                :title "Bremsweg um mehr als 5% kürzer als der Regelabstand"}
                        "Kurzer Bremsweg"))))))
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
