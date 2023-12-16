(ns signals.bootstrap
  (:require
   [uix.core :as uix :refer [$ defui]]))

(defui modal [{:keys [children header footer open? close]}]
  (uix/use-effect
   (fn []
     (let [backdrop (js/document.querySelector ".modal-backdrop")]
       (cond
         (and open? (not backdrop))
         (let [new-backdrop (js/document.createElement "div")
               body (.-body js/document)]
           (-> new-backdrop
               .-classList
               (.add "modal-backdrop" "open" "show"))
           (set! (.-onclick new-backdrop) close)
           (.append body new-backdrop))
         (and (not open?) backdrop)
         (.remove backdrop))))
   [open? close])
  (when open?
    ($ :div.modal.fade.show {:tab-index "-1"
                             :style {:display "block"}
                             :aria-modal true
                             :role "dialog"}
       ($ :div.modal-dialog.modal-dialog-centered.modal-dialog-scrollable
          ($ :div.modal-content
             (when (or header close)
               ($ :div.modal-header
                  (when header
                    header)
                  (when close
                    ($ :button.btn-close {:type "button"
                                          :on-click close
                                          :aria-label "Close"}))))
             ($ :div.modal-body
                children)
             (when footer
               ($ :div.modal-footer
                  footer)))))))

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
