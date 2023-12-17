(ns signals.main
  (:require
   [signals.demo :refer [demo]]
   [uix.core :as uix :refer [$]]
   [uix.dom :as uix.dom]))

(defonce root (uix.dom/create-root (js/document.getElementById "app")))

(defn render []
  (uix.dom/render-root ($ demo) root))

(defn ^:export init []
  (render))
