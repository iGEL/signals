(ns signals.lamp
  (:require
   [cljs.spec.alpha :as s]
   [clojure.string :as str]
   [preo.core :as p]
   [uix.core :refer [$ defui]]))

(s/def ::color (s/nilable #{:red :green :orange :yellow :white}))
(s/def ::state (s/nilable #{:blinking :on :off}))
(s/def ::size #{:big :small :tiny})
(s/def ::x number?)
(s/def ::y number?)
(s/def ::lamp (s/keys :req-un [::color ::state ::size ::x ::y]))

(defui lamp [{:keys [color state x y size] :as args}]
  {:pre [(p/arg! ::lamp args)]}
  (when state
    (let [classes (->> (case state
                         :blinking ["on" "blinking"]
                         :on ["on"]
                         :off ["off"])
                       (cons (name color))
                       (str/join " "))]
      ($ :circle {:cx x
                  :cy y
                  :r (case size
                       :big "7.5"
                       :small "3.5"
                       :tiny "2")
                  :class classes}))))
