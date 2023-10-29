(ns signals.main
  (:require
   [uix.core :refer [$ defui]]
   [uix.dom :as dom]))

(defui signal []
  ($ :div "Hello world"))

(defn render []
  (dom/render ($ signal) (.getElementById js/document "app")))

(defn ^:dev/after-load reload []
  (render))

(defn ^:export init []
  (render))
