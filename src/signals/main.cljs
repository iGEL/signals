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
    ($ :table {:border 1}
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
             ($ :td
                ($ :button {:on-click #(set-combination-aspect! :stop)} "Halt")
                ($ :button {:on-click #(set-combination-aspect! :proceed)} "Fahrt")
                ($ :button {:on-click #(set-combination-aspect! :stop+sh1)
                            :disabled (not (-> combination :main :sh1?))} "Halt + Sh1")
                ($ :button {:on-click #(set-combination-aspect! :stop+zs1)
                            :disabled (not (-> combination :main :zs1?))} "Halt + Zs1")
                ($ :button {:on-click #(set-combination-aspect! :stop+zs7)
                            :disabled (not (-> combination :main :zs7?))} "Halt + Zs7"))
             ($ :td
                ($ :button {:on-click #(set-main-aspect! :stop)} "Halt")
                ($ :button {:on-click #(set-main-aspect! :proceed)} "Fahrt")
                ($ :button {:on-click #(set-main-aspect! :stop+sh1)
                            :disabled (not (-> main :main :sh1?))} "Halt + Sh1")
                ($ :button {:on-click #(set-main-aspect! :stop+zs1)
                            :disabled (not (-> main :main :zs1?))} "Halt + Zs1")
                ($ :button {:on-click #(set-main-aspect! :stop+zs7)
                            :disabled (not (-> main :main :zs7?))} "Halt + Zs7")))
          ($ :tr
             ($ :td "Features")
             ($ :td
                ($ :button {:on-click #(set-distant! (assoc-in distant [:distant :distant-addition] (when-not (-> distant :distant :distant-addition (= :shortened-break-path))
                                                                                                      :shortened-break-path)))} "Bremsweg >5% verkürzt"))
             ($ :td)
             ($ :td
                ($ :button {:on-click #(set-combination! (update-in combination [:main :sh1?] not))} "Sh1")
                ($ :button {:on-click #(set-combination! (update-in combination [:main :zs1?] not))} "Zs1")
                ($ :button {:on-click #(set-combination! (update-in combination [:main :zs7?] not))} "Zs7")
                ($ :button {:on-click #(set-combination! (assoc-in combination [:distant :distant-addition] (when-not (-> combination :distant :distant-addition (= :shortened-break-path))
                                                                                                              :shortened-break-path)))} "Bremsweg >5% verkürzt"))
             ($ :td
                ($ :button {:on-click #(set-main! (update-in main [:main :sh1?] not))} "Sh1")
                ($ :button {:on-click #(set-main! (update-in main [:main :zs1?] not))} "Zs1")
                ($ :button {:on-click #(set-main! (update-in main [:main :zs7?] not))} "Zs7"))))
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
