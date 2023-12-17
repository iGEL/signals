(ns signals.demo.bug-report
  (:require
   [clojure.pprint :refer [pprint]]
   [signals.bootstrap :as bootstrap]
   [uix.core :as uix :refer [$ defui]]))

(defui modal [{:keys [open? close state]}]
  (let [[copied? set-copied!] (uix/use-state false)
        close-modal (fn []
                      (set-copied! false)
                      (close))
        pprinted-state (with-out-str (pprint state))
        copy (fn []
               (-> js/navigator
                   .-clipboard
                   (.writeText pprinted-state)
                   (.then #(set-copied! true))))
        download #(let [a (js/document.createElement "a")
                        content (js/Blob. [pprinted-state] (clj->js {:type "plain/text"}))]
                    (.setAttribute a "href" (js/URL.createObjectURL content))
                    (.setAttribute a "download" "state.txt")
                    (.click a))]
    ($ bootstrap/modal {:open? open?
                        :close close-modal
                        :header "🐛 Fehler melden"
                        :footer ($ :div
                                   ($ bootstrap/button {:on-click close-modal} "Schließen"))}
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
                ($ bootstrap/button {:on-click copy}
                   (if copied? "kopiert ✅" "kopieren"))
                " oder "
                ($ bootstrap/button {:on-click download} "herunterladen")
                " und sende mir dies mit der Beschreibung des korrekten Zustands zu."))
          ($ :div
             ($ :strong "Danke! ❤️"))))))

(defui base [{:keys [state]}]
  (let [[modal-open? set-modal-open!] (uix/use-state false)]
    ($ :<>
       ($ modal {:open? modal-open?
                 :close #(set-modal-open! false)
                 :state state})
       ($ bootstrap/button {:type "danger"
                            :on-click #(set-modal-open! true)}
          "🐛 Fehler melden"))))
