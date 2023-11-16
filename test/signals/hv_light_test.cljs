(ns signals.hv-light-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [signals.hv-light :as hv]
   [signals.signal :as signal]))

(def hp0-no-yellow {:main {:green :off
                           :red :on
                           :yellow nil
                           :secondary-red nil
                           :sh1 nil
                           :zs1 nil
                           :zs7 nil}})

(def hp0-with-yellow {:main {:green :off
                             :red :on
                             :yellow :off
                             :secondary-red nil
                             :sh1 nil
                             :zs1 nil
                             :zs7 nil}})

(def hp1-no-yellow {:main {:green :on
                           :red :off
                           :yellow nil
                           :secondary-red nil
                           :sh1 nil
                           :zs1 nil
                           :zs7 nil}})

(def hp1-with-yellow {:main {:green :on
                             :red :off
                             :yellow :off
                             :secondary-red nil
                             :sh1 nil
                             :zs1 nil
                             :zs7 nil}})

(def hp2 {:main {:green :on
                 :red :off
                 :yellow :on
                 :secondary-red nil
                 :sh1 nil
                 :zs1 nil
                 :zs7 nil}})

(defn- add-to-main [signal new-attrs]
  (assoc signal :main (merge (:main signal)
                             new-attrs)))

(def vr0 {:distant {:top-green :off
                    :top-yellow :on
                    :white nil
                    :bottom-green :off
                    :bottom-yellow :on}})

(def vr1 {:distant {:top-green :on
                    :top-yellow :off
                    :white nil
                    :bottom-green :on
                    :bottom-yellow :off}})

(def vr2 {:distant {:top-green :on
                    :top-yellow :off
                    :white nil
                    :bottom-green :off
                    :bottom-yellow :on}})

(def vr-off {:distant {:top-green :off
                       :top-yellow :off
                       :white nil
                       :bottom-green :off
                       :bottom-yellow :off}})

(def no-hp {:main nil})
(def no-vr {:distant nil})
(defn- add-to-distant [signal new-attrs]
  (assoc signal :distant (merge (:distant signal)
                                new-attrs)))

(deftest hv-test
  (testing "stop"
    (testing "distant shows vr0"
      (is (= (merge no-hp vr0)
             (-> (signal/distant {:aspect :stop
                                  :system :hv-light})
                 hv/lights))))

    (testing "repeater shows vr0"
      (is (= (merge no-hp (add-to-distant vr0 {:white :on}))
             (-> (signal/distant {:aspect :stop
                                  :distant-addition :repeater
                                  :system :hv-light})
                 hv/lights))))

    (testing "main shows hp0"
      (is (= (merge hp0-no-yellow no-vr)
             (-> (signal/main {:aspect :stop
                               :system :hv-light})
                 hv/lights))))

    (testing "combination shows hp0"
      (is (= (merge hp0-no-yellow vr-off)
             (-> (signal/combination {:main {:aspect :stop}
                                      :distant {:aspect :stop}
                                      :system :hv-light})
                 hv/lights)))

      (is (= (merge hp0-no-yellow vr-off)
             (-> (signal/combination {:main {:aspect :stop}
                                      :distant {:aspect :proceed}
                                      :system :hv-light})
                 hv/lights)))))

  (testing "stop+sh1"
    (testing "distant shows vr0"
      (is (= (merge no-hp vr0)
             (-> (signal/distant {:aspect :stop+sh1
                                  :system :hv-light})
                 hv/lights))))

    (testing "main shows hp0"
      (is (= (merge hp0-no-yellow no-vr)
             (-> (signal/main {:aspect :stop+sh1
                               :system :hv-light})
                 hv/lights))))

    (testing "combination shows hp0"
      (is (= (merge hp0-no-yellow vr-off)
             (-> (signal/combination {:main {:aspect :stop+sh1}
                                      :distant {:aspect :stop+sh1}
                                      :system :hv-light})
                 hv/lights)))))

  (testing "stop+zs1"
    (testing "distant shows vr0"
      (is (= (merge no-hp vr0)
             (-> (signal/distant {:aspect :stop+zs1
                                  :system :hv-light})
                 hv/lights))))

    (testing "main shows hp0"
      (is (= (merge hp0-no-yellow no-vr)
             (-> (signal/main {:aspect :stop+zs1
                               :system :hv-light})
                 hv/lights))))

    (testing "combination shows hp0"
      (is (= (merge hp0-no-yellow vr-off)
             (-> (signal/combination {:main {:aspect :stop+zs1}
                                      :distant {:aspect :stop+zs1}
                                      :system :hv-light})
                 hv/lights)))))

  (testing "stop+zs7"
    (testing "distant shows vr0"
      (is (= (merge no-hp vr0)
             (-> (signal/distant {:aspect :stop+zs7
                                  :system :hv-light})
                 hv/lights))))

    (testing "main shows hp0"
      (is (= (merge hp0-no-yellow no-vr)
             (-> (signal/main {:aspect :stop+zs7
                               :system :hv-light})
                 hv/lights))))

    (testing "combination shows hp0"
      (is (= (merge hp0-no-yellow vr-off)
             (-> (signal/combination {:main {:aspect :stop+zs7}
                                      :distant {:aspect :stop+zs7}
                                      :system :hv-light})
                 hv/lights)))))

  (testing "proceed"
    (testing "distant shows vr1"
      (is (= (merge no-hp vr1)
             (-> (signal/distant {:aspect :proceed
                                  :system :hv-light})
                 hv/lights))))

    (testing "repeater shows vr1"
      (is (= (merge no-hp (add-to-distant vr1 {:white :on}))
             (-> (signal/distant {:aspect :proceed
                                  :distant-addition :repeater
                                  :system :hv-light})
                 hv/lights))))

    (testing "main shows hp1"
      (is (= (merge hp1-no-yellow no-vr)
             (-> (signal/main {:aspect :proceed
                               :system :hv-light})
                 hv/lights))))

    (testing "combination"
      (testing "with stop expected shows hp1 and vr0"
        (is (= (merge hp1-no-yellow vr0)
               (-> (signal/combination {:main {:aspect :proceed}
                                        :distant {:aspect :stop}
                                        :system :hv-light})
                   hv/lights))))

      (testing "with proceed expected shows hp1 and vr1"
        (is (= (merge hp1-no-yellow vr1)
               (-> (signal/combination {:main {:aspect :proceed}
                                        :distant {:aspect :proceed}
                                        :system :hv-light})
                   hv/lights))))))

  (testing "slow-speed-lights"
    (testing "stop"
      (testing "distant shows vr0"
        (is (= (merge no-hp vr0)
               (-> (signal/distant {:aspect :stop
                                    :slow-speed-lights [40]
                                    :system :hv-light})
                   hv/lights))))

      (testing "repeater shows vr0"
        (is (= (merge no-hp (add-to-distant vr0 {:white :on}))
               (-> (signal/distant {:aspect :stop
                                    :slow-speed-lights [40]
                                    :distant-addition :repeater
                                    :system :hv-light})
                   hv/lights))))

      (testing "main shows hp0"
        (is (= (merge hp0-with-yellow no-vr)
               (-> (signal/main {:aspect :stop
                                 :slow-speed-lights [40]
                                 :system :hv-light})
                   hv/lights))))

      (testing "combination shows hp0"
        (is (= (merge hp0-with-yellow vr-off)
               (-> (signal/combination {:main {:aspect :stop
                                               :slow-speed-lights [40]}
                                        :distant {:aspect :stop}
                                        :system :hv-light})
                   hv/lights)))

        (is (= (merge hp0-with-yellow vr-off)
               (-> (signal/combination {:main {:aspect :stop
                                               :slow-speed-lights [40]}
                                        :distant {:aspect :proceed}
                                        :system :hv-light})
                   hv/lights)))))

    (testing "proceed"
      (testing "distant shows vr1"
        (is (= (merge no-hp vr1)
               (-> (signal/distant {:aspect :proceed
                                    :slow-speed-lights [40]
                                    :system :hv-light})
                   hv/lights))))

      (testing "repeater shows vr1"
        (is (= (merge no-hp (add-to-distant vr1 {:white :on}))
               (-> (signal/distant {:aspect :proceed
                                    :slow-speed-lights [40]
                                    :distant-addition :repeater
                                    :system :hv-light})
                   hv/lights))))

      (testing "main shows hp1"
        (is (= (merge hp1-with-yellow no-vr)
               (-> (signal/main {:aspect :proceed
                                 :slow-speed-lights [40]
                                 :system :hv-light})
                   hv/lights))))

      (testing "combination"
        (testing "with stop expected shows hp1 & vr0"
          (is (= (merge hp1-with-yellow vr0)
                 (-> (signal/combination {:main {:aspect :proceed
                                                 :slow-speed-lights [40]}
                                          :distant {:aspect :stop
                                                    :slow-speed-lights [40]}
                                          :system :hv-light})
                     hv/lights))))

        (testing "with proceed expected shows hp1 & vr1"
          (is (= (merge hp1-with-yellow vr1)
                 (-> (signal/combination {:main {:aspect :proceed
                                                 :slow-speed-lights [40]}
                                          :distant {:aspect :proceed
                                                    :slow-speed-lights [40]}
                                          :system :hv-light})
                     hv/lights)))))

      (testing "with a speed-limit above 60 km/h"
        (testing "distant shows vr1"
          (is (= (merge no-hp vr1)
                 (-> (signal/distant {:aspect :proceed
                                      :speed-limit 70
                                      :slow-speed-lights [40]
                                      :system :hv-light})
                     hv/lights))))

        (testing "main shows hp1"
          (is (= (merge hp1-with-yellow no-vr)
                 (-> (signal/main {:aspect :proceed
                                   :speed-limit 70
                                   :slow-speed-lights [40]
                                   :system :hv-light})
                     hv/lights))))

        (testing "combination shows hp1 & vr1"
          (is (= (merge hp1-with-yellow vr1)
                 (-> (signal/combination {:main {:aspect :proceed
                                                 :speed-limit 70
                                                 :slow-speed-lights [40]}
                                          :distant {:aspect :proceed
                                                    :speed-limit 70
                                                    :slow-speed-lights [40]}
                                          :system :hv-light})
                     hv/lights)))))

      (testing "with a speed-limit smaller or equal to 60 km/h"
        (testing "distant shows vr2"
          (is (= (merge no-hp vr2)
                 (-> (signal/distant {:aspect :proceed
                                      :speed-limit 60
                                      :slow-speed-lights [40]
                                      :system :hv-light})
                     hv/lights))))

        (testing "main shows hp2"
          (is (= (merge hp2 no-vr)
                 (-> (signal/main {:aspect :proceed
                                   :speed-limit 60
                                   :slow-speed-lights [40]
                                   :system :hv-light})
                     hv/lights))))

        (testing "combination shows hp2 & vr2"
          (is (= (merge hp2 vr2)
                 (-> (signal/combination {:main {:aspect :proceed
                                                 :speed-limit 60
                                                 :slow-speed-lights [40]}
                                          :distant {:aspect :proceed
                                                    :speed-limit 60
                                                    :slow-speed-lights [40]}
                                          :system :hv-light})
                     hv/lights)))))))

  (testing "shortened break path"
    (testing "stop"
      (testing "combination shows hp0 and no lights on the distant signal"
        (is (= (merge hp0-no-yellow (add-to-distant vr-off {:white :off}))
               (-> (signal/combination {:main {:aspect :stop}
                                        :distant {:aspect :stop
                                                  :distant-addition :shortened-break-path}
                                        :system :hv-light})
                   hv/lights)))))

    (testing "stop expected"
      (testing "distant shows vr0 with white addition"
        (is (= (merge no-hp (add-to-distant vr0 {:white :on}))
               (-> (signal/distant {:aspect :stop
                                    :distant-addition :shortened-break-path
                                    :system :hv-light})
                   hv/lights))))

      (testing "combination shows hp1 and vr0 with white addition"
        (is (= (merge hp1-no-yellow (add-to-distant vr0 {:white :on}))
               (-> (signal/combination {:main {:aspect :proceed}
                                        :distant {:aspect :stop
                                                  :distant-addition :shortened-break-path}
                                        :system :hv-light})
                   hv/lights)))))

    (testing "proceed expected"
      (testing "distant shows vr1 with white addition"
        (is (= (merge no-hp (add-to-distant vr1 {:white :on}))
               (-> (signal/distant {:aspect :proceed
                                    :distant-addition :shortened-break-path
                                    :system :hv-light})
                   hv/lights))))

      (testing "combination shows hp1 and vr1 with white addition"
        (is (= (merge hp1-no-yellow (add-to-distant vr1 {:white :on}))
               (-> (signal/combination {:main {:aspect :proceed}
                                        :distant {:aspect :proceed
                                                  :distant-addition :shortened-break-path}
                                        :system :hv-light})
                   hv/lights))))))

  (testing "sh1"
    (testing "stop"
      (testing "main shows hp0"
        (is (= (merge (add-to-main hp0-with-yellow {:sh1 :off
                                                    :secondary-red :on})
                      no-vr)
               (-> (signal/main {:aspect :stop
                                 :slow-speed-lights [40]
                                 :sh1? true
                                 :system :hv-light})
                   hv/lights))))

      (testing "combination shows hp0"
        (is (= (merge (add-to-main hp0-with-yellow {:sh1 :off
                                                    :secondary-red :on})
                      vr-off)
               (-> (signal/combination {:main {:aspect :stop
                                               :slow-speed-lights [40]
                                               :sh1? true}
                                        :distant {:aspect :stop}
                                        :system :hv-light})
                   hv/lights)))

        (is (= (merge (add-to-main hp0-with-yellow {:sh1 :off
                                                    :secondary-red :on})
                      vr-off)
               (-> (signal/combination {:main {:aspect :stop
                                               :slow-speed-lights [40]
                                               :sh1? true}
                                        :distant {:aspect :proceed}
                                        :system :hv-light})
                   hv/lights)))))

    (testing "stop + sh1"
      (testing "main shows hp0 + sh1"
        (is (= (merge (add-to-main hp0-with-yellow {:sh1 :on
                                                    :secondary-red :off})
                      no-vr)
               (-> (signal/main {:aspect :stop+sh1
                                 :slow-speed-lights [40]
                                 :sh1? true
                                 :system :hv-light})
                   hv/lights))))

      (testing "combination shows hp0 + sh1"
        (is (= (merge (add-to-main hp0-with-yellow {:sh1 :on
                                                    :secondary-red :off})
                      vr-off)
               (-> (signal/combination {:main {:aspect :stop+sh1
                                               :slow-speed-lights [40]
                                               :sh1? true}
                                        :distant {:aspect :stop}
                                        :system :hv-light})
                   hv/lights)))

        (is (= (merge (add-to-main hp0-with-yellow {:sh1 :on
                                                    :secondary-red :off})
                      vr-off)
               (-> (signal/combination {:main {:aspect :stop+sh1
                                               :slow-speed-lights [40]
                                               :sh1? true}
                                        :distant {:aspect :proceed}
                                        :system :hv-light})
                   hv/lights)))))

    (testing "proceed"
      (testing "main shows hp1"
        (is (= (merge (add-to-main hp1-with-yellow {:sh1 :off
                                                    :secondary-red :off})
                      no-vr)
               (-> (signal/main {:aspect :proceed
                                 :slow-speed-lights [40]
                                 :sh1? true
                                 :system :hv-light})
                   hv/lights))))

      (testing "combination"
        (testing "with stop expected shows hp1 & vr0"
          (is (= (merge (add-to-main hp1-with-yellow {:sh1 :off
                                                      :secondary-red :off})
                        vr0)
                 (-> (signal/combination {:main {:aspect :proceed
                                                 :slow-speed-lights [40]
                                                 :sh1? true}
                                          :distant {:aspect :stop
                                                    :slow-speed-lights [40]}
                                          :system :hv-light})
                     hv/lights)))))

      (testing "with a speed-limit smaller or equal to 60 km/h"
        (testing "main shows hp2"
          (is (= (merge (add-to-main hp2 {:sh1 :off
                                          :secondary-red :off})
                        no-vr)
                 (-> (signal/main {:aspect :proceed
                                   :speed-limit 60
                                   :slow-speed-lights [40]
                                   :sh1? true
                                   :system :hv-light})
                     hv/lights))))

        (testing "combination shows hp2 & vr2"
          (is (= (merge (add-to-main hp2 {:sh1 :off
                                          :secondary-red :off})
                        vr2)
                 (-> (signal/combination {:main {:aspect :proceed
                                                 :speed-limit 60
                                                 :slow-speed-lights [40]
                                                 :sh1? true}
                                          :distant {:aspect :proceed
                                                    :speed-limit 60
                                                    :slow-speed-lights [40]}
                                          :system :hv-light})
                     hv/lights)))))))

  (testing "zs1"
    (testing "stop"
      (testing "main shows hp0"
        (is (= (merge (add-to-main hp0-with-yellow {:zs1 :off})
                      no-vr)
               (-> (signal/main {:aspect :stop
                                 :slow-speed-lights [40]
                                 :zs1? true
                                 :system :hv-light})
                   hv/lights))))

      (testing "combination shows hp0"
        (is (= (merge (add-to-main hp0-with-yellow {:zs1 :off})
                      vr-off)
               (-> (signal/combination {:main {:aspect :stop
                                               :slow-speed-lights [40]
                                               :zs1? true}
                                        :distant {:aspect :stop}
                                        :system :hv-light})
                   hv/lights)))

        (is (= (merge (add-to-main hp0-with-yellow {:zs1 :off})
                      vr-off)
               (-> (signal/combination {:main {:aspect :stop
                                               :slow-speed-lights [40]
                                               :zs1? true}
                                        :distant {:aspect :proceed}
                                        :system :hv-light})
                   hv/lights)))))

    (testing "stop + zs1"
      (testing "main shows hp0 + zs1"
        (is (= (merge (add-to-main hp0-with-yellow {:zs1 :on})
                      no-vr)
               (-> (signal/main {:aspect :stop+zs1
                                 :slow-speed-lights [40]
                                 :zs1? true
                                 :system :hv-light})
                   hv/lights))))

      (testing "combination shows hp0 + zs1"
        (is (= (merge (add-to-main hp0-with-yellow {:zs1 :on})
                      vr-off)
               (-> (signal/combination {:main {:aspect :stop+zs1
                                               :slow-speed-lights [40]
                                               :zs1? true}
                                        :distant {:aspect :stop}
                                        :system :hv-light})
                   hv/lights)))

        (is (= (merge (add-to-main hp0-with-yellow {:zs1 :on})
                      vr-off)
               (-> (signal/combination {:main {:aspect :stop+zs1
                                               :slow-speed-lights [40]
                                               :zs1? true}
                                        :distant {:aspect :proceed}
                                        :system :hv-light})
                   hv/lights)))))

    (testing "proceed"
      (testing "main shows hp1"
        (is (= (merge (add-to-main hp1-with-yellow {:zs1 :off})
                      no-vr)
               (-> (signal/main {:aspect :proceed
                                 :slow-speed-lights [40]
                                 :zs1? true
                                 :system :hv-light})
                   hv/lights))))

      (testing "combination"
        (testing "with stop expected shows hp1 & vr0"
          (is (= (merge (add-to-main hp1-with-yellow {:zs1 :off})
                        vr0)
                 (-> (signal/combination {:main {:aspect :proceed
                                                 :slow-speed-lights [40]
                                                 :zs1? true}
                                          :distant {:aspect :stop
                                                    :slow-speed-lights [40]}
                                          :system :hv-light})
                     hv/lights)))))))

  (testing "zs7"
    (testing "stop"
      (testing "main shows hp0"
        (is (= (merge (add-to-main hp0-with-yellow {:zs7 :off})
                      no-vr)
               (-> (signal/main {:aspect :stop
                                 :slow-speed-lights [40]
                                 :zs7? true
                                 :system :hv-light})
                   hv/lights))))

      (testing "combination shows hp0"
        (is (= (merge (add-to-main hp0-with-yellow {:zs7 :off})
                      vr-off)
               (-> (signal/combination {:main {:aspect :stop
                                               :slow-speed-lights [40]
                                               :zs7? true}
                                        :distant {:aspect :stop}
                                        :system :hv-light})
                   hv/lights)))

        (is (= (merge (add-to-main hp0-with-yellow {:zs7 :off})
                      vr-off)
               (-> (signal/combination {:main {:aspect :stop
                                               :slow-speed-lights [40]
                                               :zs7? true}
                                        :distant {:aspect :proceed}
                                        :system :hv-light})
                   hv/lights)))))

    (testing "stop + zs7"
      (testing "main shows hp0 + zs7"
        (is (= (merge (add-to-main hp0-with-yellow {:zs7 :on})
                      no-vr)
               (-> (signal/main {:aspect :stop+zs7
                                 :slow-speed-lights [40]
                                 :zs7? true
                                 :system :hv-light})
                   hv/lights))))

      (testing "combination shows hp0 + zs7"
        (is (= (merge (add-to-main hp0-with-yellow {:zs7 :on})
                      vr-off)
               (-> (signal/combination {:main {:aspect :stop+zs7
                                               :slow-speed-lights [40]
                                               :zs7? true}
                                        :distant {:aspect :stop}
                                        :system :hv-light})
                   hv/lights)))

        (is (= (merge (add-to-main hp0-with-yellow {:zs7 :on})
                      vr-off)
               (-> (signal/combination {:main {:aspect :stop+zs7
                                               :slow-speed-lights [40]
                                               :zs7? true}
                                        :distant {:aspect :proceed}
                                        :system :hv-light})
                   hv/lights)))))

    (testing "proceed"
      (testing "main shows hp1"
        (is (= (merge (add-to-main hp1-with-yellow {:zs7 :off})
                      no-vr)
               (-> (signal/main {:aspect :proceed
                                 :slow-speed-lights [40]
                                 :zs7? true
                                 :system :hv-light})
                   hv/lights))))

      (testing "combination"
        (testing "with stop expected shows hp1 & vr0"
          (is (= (merge (add-to-main hp1-with-yellow {:zs7 :off})
                        vr0)
                 (-> (signal/combination {:main {:aspect :proceed
                                                 :slow-speed-lights [40]
                                                 :zs7? true}
                                          :distant {:aspect :stop
                                                    :slow-speed-lights [40]}
                                          :system :hv-light})
                     hv/lights))))))))

(deftest speed-limit-available?
  (testing "without Zs3 / slow-speed-light"
    (is (not (hv/speed-limit-available?
              (signal/main {:system :hv-light})
              nil)))

    (is (not (hv/speed-limit-available?
              (signal/main {:system :hv-light})
              40)))

    (is (not (hv/speed-limit-available?
              (signal/main {:system :hv-light})
              100))))

  (testing "with slow-speed-light"
    (is (hv/speed-limit-available?
         (signal/main {:system :hv-light
                       :slow-speed-lights [40]})
         nil))

    (is (hv/speed-limit-available?
         (signal/main {:system :hv-light
                       :slow-speed-lights [40]})
         40))

    (is (not (hv/speed-limit-available?
              (signal/main {:system :hv-light
                            :slow-speed-lights [40]})
              100))))

  (testing "with Zs3"
    (is (hv/speed-limit-available?
         (signal/main {:zs3 :display
                       :system :hv-light})
         nil))

    (is (hv/speed-limit-available?
         (signal/combination {:main {:zs3 :display}
                              :system :hv-light})
         nil))

    (is (not (hv/speed-limit-available?
              (signal/main {:zs3 :sign
                            :system :hv-light})
              nil)))

    (is (not (hv/speed-limit-available?
              (signal/combination {:main {:zs3 :sign}
                                   :system :hv-light})
              nil)))

    (is (hv/speed-limit-available?
         (signal/main {:zs3 :sign
                       :system :hv-light})
         100))

    (is (hv/speed-limit-available?
         (signal/combination {:main {:zs3 :display}
                              :system :hv-light})
         100))))
