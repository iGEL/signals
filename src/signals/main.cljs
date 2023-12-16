(ns signals.main
  (:require
   [clojure.pprint :refer [pprint]]
   [clojure.string :as str]
   [signals.bootstrap :refer [button modal]]
   [signals.helper :refer [stop-aspect?]]
   [signals.hl :as hl]
   [signals.hv-light :as hv-light]
   [signals.ks :as ks]
   [signals.signal :as signal]
   [uix.core :as uix :refer [$ defui]]
   [uix.dom :as uix.dom]))

(defui signal [{:keys [signal]}]
  ($ :svg {:version "1.1"
           :viewBox "0 0 140 600"
           :width "200"
           :height "600"}
     ($ signal/defs)
     ($ signal/signal {:signal signal})))

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
  (when (speed-limit-available? state speed-limit)
    ($ button {:on-click #(set-state! :speed-limit speed-limit)
               :type (if (or (nil? speed-limit)
                             (> speed-limit 60))
                       "success"
                       "warning")
               :active? (= speed-limit (-> state :main :speed-limit))}
       (if speed-limit speed-limit "∞"))))

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
             ($ button {:on-click #(set-state! :zs3 nil)
                        :type "info"
                        :active? (nil? (-> state :main :zs3))} "Kein")
             ($ button {:on-click (fn []
                                    (set-state! :zs3 :sign)
                                    (when-not (-> state :main :speed-limit)
                                      (set-state! :speed-limit 60)))
                        :type "info"
                        :active? (= :sign (-> state :main :zs3))} "Tafel")
             ($ button {:on-click #(set-state! :zs3 :display)
                        :type "info"
                        :active? (= :display (-> state :main :zs3))} "Lichtsignal"))))
     (let [speeds (->> [nil 150 140 130 120 110 100 90 80 70 60 50 40 30 20 10]
                       (filter
                        (partial speed-limit-available? state))
                       (reduce ; Group speeds into groups with up to 12 characters.
                        (fn [prev speed]
                           ; Add the item to the last group, if the str length
                           ; of the items is 12 or less letters, otherwise open
                           ; a new group
                          (let [current-group (last prev)
                                current-group-length (->> current-group
                                                          (map str)
                                                          str/join
                                                          count
                                                          (+ (count (str speed))))]
                            (if (< current-group-length 13)
                              (conj (-> prev butlast vec)
                                    (conj current-group speed))
                              (conj prev [speed]))))
                        [[]]))]
       (doall
        (map-indexed (fn [group-idx group]
                       ($ :div {:key (str "speed-limit-group-" group-idx)}
                          ($ :div.btn-group
                             (map-indexed (fn [speed-idx speed]
                                            ($ speed-limit-btn {:key (str "speed-limit-btn-" speed-idx)
                                                                :speed-limit speed
                                                                :set-state! set-state!
                                                                :state state}))
                                          group))))
                     speeds)))))

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
             (let [combination-main (:main combination)
                   current-aspect (:aspect combination-main)]
               ($ :td
                  ($ button {:on-click #(set-combination-aspect! :proceed)
                             :active? (= :proceed current-aspect)
                             :type "success"} "Fahrt")
                  ($ :div.btn-group
                     ($ button {:on-click #(set-combination-aspect! :stop)
                                :active? (stop-aspect? current-aspect)
                                :type "danger"} "Halt")
                     (when (:sh1? combination-main)
                       ($ button {:on-click #(set-combination-aspect! (if (= :stop+sh1 current-aspect)
                                                                        :stop
                                                                        :stop+sh1))
                                  :disabled? (not (stop-aspect? current-aspect))
                                  :active? (= :stop+sh1 current-aspect)} "Sh1/Ra12"))
                     (when (:zs1? combination-main)
                       ($ button {:on-click #(set-combination-aspect! (if (= :stop+zs1 current-aspect)
                                                                        :stop
                                                                        :stop+zs1))
                                  :disabled? (not (stop-aspect? current-aspect))
                                  :active? (= :stop+zs1 current-aspect)} "Zs1"))
                     (when (:zs7? combination-main)
                       ($ button {:on-click #(set-combination-aspect! (if (= :stop+zs7 current-aspect)
                                                                        :stop
                                                                        :stop+zs7))
                                  :disabled? (not (stop-aspect? current-aspect))
                                  :active? (= :stop+zs7 current-aspect)} "Zs7")))))
             (let [main-state (:main main)
                   current-aspect (:aspect main-state)]
               ($ :td
                  ($ button {:on-click #(set-main-aspect! :proceed)
                             :active? (= :proceed current-aspect)
                             :type "success"} "Fahrt")
                  ($ :div.btn-group

                     ($ button {:on-click #(set-main-aspect! :stop)
                                :active? (stop-aspect? current-aspect)
                                :type "danger"} "Halt")
                     (when (:sh1? main-state)
                       ($ button {:on-click #(set-main-aspect! (if (= :stop+sh1 current-aspect)
                                                                 :stop
                                                                 :stop+sh1))
                                  :disabled? (not (stop-aspect? current-aspect))
                                  :active? (= :stop+sh1 current-aspect)} "Sh1/Ra12"))
                     (when (:zs1? main-state)
                       ($ button {:on-click #(set-main-aspect! (if (= :stop+zs1 current-aspect)
                                                                 :stop
                                                                 :stop+zs1))
                                  :disabled? (not (stop-aspect? current-aspect))
                                  :active? (= :stop+zs1 current-aspect)} "Zs1"))
                     (when (:zs7? main-state)
                       ($ button {:on-click #(set-main-aspect! (if (= :stop+zs7 current-aspect)
                                                                 :stop
                                                                 :stop+zs7))
                                  :disabled? (not (stop-aspect? current-aspect))
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
             ($ :th "Geschwindigkeits-"
                ($ :br)
                "begrenzung")
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
                                     :state main})))
          ($ :tr
             (let [[modal-open? set-modal-open!] (uix/use-state false)
                   [copied? set-copied!] (uix/use-state false)
                   close-modal (fn []
                                 (set-copied! false)
                                 (set-modal-open! false))
                   state (with-out-str
                           (pprint {:distant distant
                                    :repeater repeater
                                    :combination combination
                                    :main main}))
                   copy (fn []
                          (-> js/navigator
                              .-clipboard
                              (.writeText state)
                              (.then #(set-copied! true))))
                   download #(let [a (js/document.createElement "a")
                                   content (js/Blob. [state] (clj->js {:type "plain/text"}))]
                               (.setAttribute a "href" (js/URL.createObjectURL content))
                               (.setAttribute a "download" "state.txt")
                               (.click a))]
               ($ :td {:col-span 5}
                  ($ modal {:open? modal-open?
                            :close close-modal
                            :header "🐛 Fehler melden"
                            :footer ($ :div
                                       ($ button {:on-click close-modal} "Schließen"))}
                     ($ :div
                        "Wenn du einen Fehler gefunden hast, bitte melde ihn mir. Dazu hast du diese Möglichkeiten:"
                        ($ :ul
                           ($ :li
                              "Erstelle einen "
                              ($ :a {:href "https://github.com/iGEL/signals/issues/new"
                                     :target "_blank"} "Issue auf GitHub")
                              " (GitHub account erforderlich)")
                           ($ :li
                              "Sende mir eine E-Mail an "
                              ($ :a {:href "mailto:igel@igels.net"} "igel@igels.net"))
                           ($ :li
                              "Schreib mir auf Mastodon an "
                              ($ :a {:href "https://mastodon.online/@iGEL"
                                     :target "_blank"} "@iGEL@mastodon.online")))
                        ($ :div
                           ($ :strong "Wichtig:"))
                        "Bitte sende mir den State der Signale, damit ich den Fehler einfach nachvollziehen kann."
                        ($ :ol
                           ($ :li "Wenn der Fehler nicht schon sichtbar ist, schließe das Fenster jetzt und bringe die Signale in den fehlerhaften Zustand.")
                           ($ :li
                              "Klicke auf "
                              ($ button {:on-click copy}
                                 (if copied? "kopiert ✅" "kopieren"))
                              " oder "
                              ($ button {:on-click download} "herunterladen")
                              " und sende mir dies mit der Beschreibung des korrekten Zustands zu."))
                        ($ :div
                           ($ :strong "Danke! ❤️"))))
                  ($ button {:type "danger"
                             :on-click #(set-modal-open! true)}
                     "🐛 Fehler melden"))))))))

(defonce root (uix.dom/create-root (js/document.getElementById "app")))

(defn render []
  (uix.dom/render-root ($ demo) root))

(defn ^:export init []
  (render))
