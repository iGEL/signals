(ns signals.demo.speed-limit
  (:require
   [clojure.string :as str]
   [signals.bootstrap :refer [button]]
   [signals.hl :as hl]
   [signals.hv-light :as hv-light]
   [signals.ks :as ks]
   [uix.core :as uix :refer [$ defui]]))

(defn- speed-limit-available? [state limit]
  (case (:system state)
    :ks (ks/speed-limit-available? state limit)
    :hl (hl/speed-limit-available? state limit)
    :hv-light (hv-light/speed-limit-available? state limit)
    :hv-semaphore (hv-light/speed-limit-available? state limit)))

(defui speed-limit-btn [{:keys [speed-limit set-state! state]}]
  ($ button {:on-click #(set-state! {:speed-limit speed-limit})
             :title (if speed-limit
                      (str "Erlaube Geschwindigkeiten bis " speed-limit " km/h")
                      "Erlaube Fahrplangeschwindigkeit")
             :type (if (or (nil? speed-limit)
                           (> speed-limit 60))
                     "success"
                     "warning")
             :active? (= speed-limit (-> state :main :speed-limit))}
     (if speed-limit speed-limit "∞")))

(defn partition-by-str-length
  "Group speeds by string lengths. Each group can contain speeds with a
   total string length of up to 12 characters"
  [speeds]
  (reduce (fn [prev speed]
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
          [[]]
          speeds))

(defui buttons [{:keys [set-state! state]}]
  (let [speeds (->> [nil 150 140 130 120 110 100 90 80 70 60 50 40 30 20 10]
                    (filter (partial speed-limit-available? state))
                    partition-by-str-length)]
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
                  speeds))))
