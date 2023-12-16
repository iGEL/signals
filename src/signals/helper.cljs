(ns signals.helper
  (:require
   [cljs.spec.alpha :as s]
   [preo.core :as p]
   [signals.spec :as spec]))

(defn stop-aspect?
  "Returns true when the given aspect is a stop aspect"
  [aspect]
  {:pre [(p/arg! (s/nilable ::spec/aspect) aspect)]}
  (#{:stop :stop+zs1 :stop+zs7 :stop+sh1} aspect))
