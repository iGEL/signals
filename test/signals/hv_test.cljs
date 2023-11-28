(ns signals.hv-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [signals.hv-light :as hv-light]
   [signals.hv-semaphore :as hv-semaphore]
   [signals.signal :as signal]))

(def no-hp {:main nil})
(def light-hp0 {:main {:green :off
                       :red :on
                       :yellow nil
                       :secondary-red nil
                       :sh1 nil
                       :zs1 nil
                       :zs7 nil}})

(def light-hp1 {:main {:green :on
                       :red :off
                       :yellow nil
                       :secondary-red nil
                       :sh1 nil
                       :zs1 nil
                       :zs7 nil}})

(def light-hp2 {:main {:green :on
                       :red :off
                       :yellow :on
                       :secondary-red nil
                       :sh1 nil
                       :zs1 nil
                       :zs7 nil}})

(def semaphore-hp0 {:main {:top-arm :horizontal
                           :lower-arm nil
                           :sh1 nil
                           :zs1 nil
                           :zs7 nil}})

(def semaphore-hp1 {:main {:top-arm :inclined
                           :lower-arm nil
                           :sh1 nil
                           :zs1 nil
                           :zs7 nil}})

(def semaphore-hp2 {:main {:top-arm :inclined
                           :lower-arm :inclined
                           :sh1 nil
                           :zs1 nil
                           :zs7 nil}})

(defn- add-to-main [signal new-attrs]
  (assoc signal :main (merge (:main signal)
                             new-attrs)))

(def no-vr {:distant nil})

(def light-vr0 {:distant {:top-green :off
                          :top-yellow :on
                          :white nil
                          :bottom-green :off
                          :bottom-yellow :on}})

(def light-vr1 {:distant {:top-green :on
                          :top-yellow :off
                          :white nil
                          :bottom-green :on
                          :bottom-yellow :off}})

(def light-vr2 {:distant {:top-green :on
                          :top-yellow :off
                          :white nil
                          :bottom-green :off
                          :bottom-yellow :on}})

(def light-vr-off {:distant {:top-green :off
                             :top-yellow :off
                             :white nil
                             :bottom-green :off
                             :bottom-yellow :off}})

(def semaphore-vr0 {:distant {:disk :vertical
                              :arm nil
                              :right-lights :vertical
                              :shortened-break-path? false}})

(def semaphore-vr1 {:distant {:disk :horizontal
                              :arm nil
                              :right-lights :inclined
                              :shortened-break-path? false}})

(def semaphore-vr2 {:distant {:disk :vertical
                              :arm :inclined
                              :right-lights :inclined
                              :shortened-break-path? false}})

(defn- add-to-distant [signal new-attrs]
  (assoc signal :distant (merge (:distant signal)
                                new-attrs)))

(defn- lights-or-arms [signal]
  (case (:system signal)
    :hv-light (hv-light/lights signal)
    :hv-semaphore (hv-semaphore/arms signal)))

(deftest hv-test
  (testing "no semaphore repeater"
    (is (= (merge no-hp no-vr)
           (-> (signal/distant {:aspect :stop
                                :distant-addition :repeater
                                :system :hv-semaphore})
               lights-or-arms))))

  (testing "stop"
    (testing "distant shows vr0"
      (is (= (merge no-hp light-vr0)
             (-> (signal/distant {:aspect :stop
                                  :system :hv-light})
                 lights-or-arms)))

      (is (= (merge no-hp semaphore-vr0)
             (-> (signal/distant {:aspect :stop
                                  :system :hv-semaphore})
                 lights-or-arms))))

    (testing "repeater shows vr0"
      (is (= (merge no-hp (add-to-distant light-vr0 {:white :on}))
             (-> (signal/distant {:aspect :stop
                                  :distant-addition :repeater
                                  :system :hv-light})
                 lights-or-arms))))

    (testing "main shows hp0"
      (is (= (merge light-hp0 no-vr)
             (-> (signal/main {:aspect :stop
                               :system :hv-light})
                 lights-or-arms)))

      (is (= (merge semaphore-hp0 no-vr)
             (-> (signal/main {:aspect :stop
                               :system :hv-semaphore})
                 lights-or-arms))))

    (testing "combination shows hp0"
      (is (= (merge light-hp0 light-vr-off)
             (-> (signal/combination {:main {:aspect :stop}
                                      :distant {:aspect :stop}
                                      :system :hv-light})
                 lights-or-arms)))

      (is (= (merge light-hp0 light-vr-off)
             (-> (signal/combination {:main {:aspect :stop}
                                      :distant {:aspect :proceed}
                                      :system :hv-light})
                 lights-or-arms)))

      (is (= (merge semaphore-hp0 semaphore-vr0)
             (-> (signal/combination {:main {:aspect :stop}
                                      :distant {:aspect :stop}
                                      :system :hv-semaphore})
                 lights-or-arms)))

      (is (= (merge semaphore-hp0 semaphore-vr0)
             (-> (signal/combination {:main {:aspect :stop}
                                      :distant {:aspect :proceed}
                                      :system :hv-semaphore})
                 lights-or-arms)))))

  (testing "stop+sh1"
    (testing "distant shows vr0"
      (is (= (merge no-hp light-vr0)
             (-> (signal/distant {:aspect :stop+sh1
                                  :system :hv-light})
                 lights-or-arms)))

      (is (= (merge no-hp semaphore-vr0)
             (-> (signal/distant {:aspect :stop+sh1
                                  :system :hv-semaphore})
                 lights-or-arms))))

    (testing "main shows hp0"
      (is (= (merge light-hp0 no-vr)
             (-> (signal/main {:aspect :stop+sh1
                               :system :hv-light})
                 lights-or-arms)))

      (is (= (merge semaphore-hp0 no-vr)
             (-> (signal/main {:aspect :stop+sh1
                               :system :hv-semaphore})
                 lights-or-arms))))

    (testing "combination shows hp0"
      (is (= (merge light-hp0 light-vr-off)
             (-> (signal/combination {:main {:aspect :stop+sh1}
                                      :distant {:aspect :stop+sh1}
                                      :system :hv-light})
                 lights-or-arms)))

      (is (= (merge semaphore-hp0 semaphore-vr0)
             (-> (signal/combination {:main {:aspect :stop+sh1}
                                      :distant {:aspect :stop+sh1}
                                      :system :hv-semaphore})
                 lights-or-arms)))))

  (testing "stop+zs1"
    (testing "distant shows vr0"
      (is (= (merge no-hp light-vr0)
             (-> (signal/distant {:aspect :stop+zs1
                                  :system :hv-light})
                 lights-or-arms)))

      (is (= (merge no-hp semaphore-vr0)
             (-> (signal/distant {:aspect :stop+zs1
                                  :system :hv-semaphore})
                 lights-or-arms))))

    (testing "main shows hp0"
      (is (= (merge light-hp0 no-vr)
             (-> (signal/main {:aspect :stop+zs1
                               :system :hv-light})
                 lights-or-arms)))

      (is (= (merge semaphore-hp0 no-vr)
             (-> (signal/main {:aspect :stop+zs1
                               :system :hv-semaphore})
                 lights-or-arms))))

    (testing "combination shows hp0"
      (is (= (merge light-hp0 light-vr-off)
             (-> (signal/combination {:main {:aspect :stop+zs1}
                                      :distant {:aspect :stop+zs1}
                                      :system :hv-light})
                 lights-or-arms)))

      (is (= (merge semaphore-hp0 semaphore-vr0)
             (-> (signal/combination {:main {:aspect :stop+zs1}
                                      :distant {:aspect :stop+zs1}
                                      :system :hv-semaphore})
                 lights-or-arms)))))

  (testing "stop+zs7"
    (testing "distant shows vr0"
      (is (= (merge no-hp light-vr0)
             (-> (signal/distant {:aspect :stop+zs7
                                  :system :hv-light})
                 lights-or-arms)))

      (is (= (merge no-hp semaphore-vr0)
             (-> (signal/distant {:aspect :stop+zs7
                                  :system :hv-semaphore})
                 lights-or-arms))))

    (testing "main shows hp0"
      (is (= (merge light-hp0 no-vr)
             (-> (signal/main {:aspect :stop+zs7
                               :system :hv-light})
                 lights-or-arms)))

      (is (= (merge semaphore-hp0 no-vr)
             (-> (signal/main {:aspect :stop+zs7
                               :system :hv-semaphore})
                 lights-or-arms))))

    (testing "combination shows hp0"
      (is (= (merge light-hp0 light-vr-off)
             (-> (signal/combination {:main {:aspect :stop+zs7}
                                      :distant {:aspect :stop+zs7}
                                      :system :hv-light})
                 lights-or-arms)))

      (is (= (merge semaphore-hp0 semaphore-vr0)
             (-> (signal/combination {:main {:aspect :stop+zs7}
                                      :distant {:aspect :stop+zs7}
                                      :system :hv-semaphore})
                 lights-or-arms)))))

  (testing "proceed"
    (testing "distant shows vr1"
      (is (= (merge no-hp light-vr1)
             (-> (signal/distant {:aspect :proceed
                                  :system :hv-light})
                 lights-or-arms)))

      (is (= (merge no-hp semaphore-vr1)
             (-> (signal/distant {:aspect :proceed
                                  :system :hv-semaphore})
                 lights-or-arms))))

    (testing "repeater shows vr1"
      (is (= (merge no-hp (add-to-distant light-vr1 {:white :on}))
             (-> (signal/distant {:aspect :proceed
                                  :distant-addition :repeater
                                  :system :hv-light})
                 lights-or-arms))))

    (testing "main shows hp1"
      (is (= (merge light-hp1 no-vr)
             (-> (signal/main {:aspect :proceed
                               :system :hv-light})
                 lights-or-arms)))

      (is (= (merge semaphore-hp1 no-vr)
             (-> (signal/main {:aspect :proceed
                               :system :hv-semaphore})
                 lights-or-arms))))

    (testing "combination"
      (testing "with stop expected shows hp1 and vr0"
        (is (= (merge light-hp1 light-vr0)
               (-> (signal/combination {:main {:aspect :proceed}
                                        :distant {:aspect :stop}
                                        :system :hv-light})
                   lights-or-arms)))

        (is (= (merge semaphore-hp1 semaphore-vr0)
               (-> (signal/combination {:main {:aspect :proceed}
                                        :distant {:aspect :stop}
                                        :system :hv-semaphore})
                   lights-or-arms))))

      (testing "with proceed expected shows hp1 and vr1"
        (is (= (merge semaphore-hp1 semaphore-vr1)
               (-> (signal/combination {:main {:aspect :proceed}
                                        :distant {:aspect :proceed}
                                        :system :hv-semaphore})
                   lights-or-arms))))))

  (testing "slow-speed-lights"
    (testing "stop"
      (testing "distant shows vr0"
        (is (= (merge no-hp light-vr0)
               (-> (signal/distant {:aspect :stop
                                    :slow-speed-lights [40]
                                    :speed-limit 40
                                    :system :hv-light})
                   lights-or-arms)))

        (is (= (merge no-hp (add-to-distant semaphore-vr0 {:arm :vertical}))
               (-> (signal/distant {:aspect :stop
                                    :slow-speed-lights [40]
                                    :speed-limit 40
                                    :system :hv-semaphore})
                   lights-or-arms))))

      (testing "repeater shows vr0"
        (is (= (merge no-hp (add-to-distant light-vr0 {:white :on}))
               (-> (signal/distant {:aspect :stop
                                    :slow-speed-lights [40]
                                    :speed-limit 40
                                    :distant-addition :repeater
                                    :system :hv-light})
                   lights-or-arms))))

      (testing "main shows hp0"
        (is (= (merge (add-to-main light-hp0 {:yellow :off}) no-vr)
               (-> (signal/main {:aspect :stop
                                 :slow-speed-lights [40]
                                 :speed-limit 40
                                 :system :hv-light})
                   lights-or-arms)))

        (is (= (merge (add-to-main semaphore-hp0 {:lower-arm :vertical}) no-vr)
               (-> (signal/main {:aspect :stop
                                 :slow-speed-lights [40]
                                 :speed-limit 40
                                 :system :hv-semaphore})
                   lights-or-arms))))

      (testing "combination shows hp0"
        (is (= (merge (add-to-main light-hp0 {:yellow :off}) light-vr-off)
               (-> (signal/combination {:main {:aspect :stop
                                               :slow-speed-lights [40]
                                               :speed-limit 40}
                                        :distant {:aspect :stop}
                                        :system :hv-light})
                   lights-or-arms)))

        (is (= (merge (add-to-main light-hp0 {:yellow :off}) light-vr-off)
               (-> (signal/combination {:main {:aspect :stop
                                               :slow-speed-lights [40]
                                               :speed-limit 40}
                                        :distant {:aspect :proceed}
                                        :system :hv-light})
                   lights-or-arms)))

        (is (= (merge (add-to-main semaphore-hp0 {:lower-arm :vertical})
                      semaphore-vr0)
               (-> (signal/combination {:main {:aspect :stop
                                               :slow-speed-lights [40]
                                               :speed-limit 40}
                                        :distant {:aspect :stop}
                                        :system :hv-semaphore})
                   lights-or-arms)))

        (is (= (merge (add-to-main semaphore-hp0 {:lower-arm :vertical})
                      (add-to-distant semaphore-vr0 {:arm :vertical}))
               (-> (signal/combination {:main {:aspect :stop
                                               :slow-speed-lights [40]
                                               :speed-limit 40}
                                        :distant {:aspect :proceed
                                                  :slow-speed-lights [40]
                                                  :speed-limit 40}
                                        :system :hv-semaphore})
                   lights-or-arms)))))

    (testing "proceed"
      (testing "distant shows vr1"
        (is (= (merge no-hp light-vr1)
               (-> (signal/distant {:aspect :proceed
                                    :slow-speed-lights [40]
                                    :system :hv-light})
                   lights-or-arms)))

        (is (= (merge no-hp (add-to-distant semaphore-vr1 {:arm :vertical}))
               (-> (signal/distant {:aspect :proceed
                                    :slow-speed-lights [40]
                                    :system :hv-semaphore})
                   lights-or-arms))))

      (testing "repeater shows vr1"
        (is (= (merge no-hp (add-to-distant light-vr1 {:white :on}))
               (-> (signal/distant {:aspect :proceed
                                    :slow-speed-lights [40]
                                    :distant-addition :repeater
                                    :system :hv-light})
                   lights-or-arms))))

      (testing "main shows hp1"
        (is (= (merge (add-to-main light-hp1 {:yellow :off})
                      no-vr)
               (-> (signal/main {:aspect :proceed
                                 :slow-speed-lights [40]
                                 :system :hv-light})
                   lights-or-arms)))

        (is (= (merge (add-to-main semaphore-hp1 {:lower-arm :vertical})
                      no-vr)
               (-> (signal/main {:aspect :proceed
                                 :slow-speed-lights [40]
                                 :system :hv-semaphore})
                   lights-or-arms))))

      (testing "combination"
        (testing "with stop expected shows hp1 & vr0"
          (is (= (merge (add-to-main light-hp1 {:yellow :off}) light-vr0)
                 (-> (signal/combination {:main {:aspect :proceed
                                                 :slow-speed-lights [40]}
                                          :distant {:aspect :stop
                                                    :slow-speed-lights [40]}
                                          :system :hv-light})
                     lights-or-arms)))

          (is (= (merge (add-to-main semaphore-hp1 {:lower-arm :vertical})
                        (add-to-distant semaphore-vr0 {:arm :vertical}))
                 (-> (signal/combination {:main {:aspect :proceed
                                                 :slow-speed-lights [40]}
                                          :distant {:aspect :stop
                                                    :slow-speed-lights [40]}
                                          :system :hv-semaphore})
                     lights-or-arms))))

        (testing "with proceed expected shows hp1 & vr1"
          (is (= (merge (add-to-main light-hp1 {:yellow :off}) light-vr1)
                 (-> (signal/combination {:main {:aspect :proceed
                                                 :slow-speed-lights [40]}
                                          :distant {:aspect :proceed
                                                    :slow-speed-lights [40]}
                                          :system :hv-light})
                     lights-or-arms)))

          (is (= (merge (add-to-main semaphore-hp1 {:lower-arm :vertical})
                        (add-to-distant semaphore-vr1 {:arm :vertical}))
                 (-> (signal/combination {:main {:aspect :proceed
                                                 :slow-speed-lights [40]}
                                          :distant {:aspect :proceed
                                                    :slow-speed-lights [40]}
                                          :system :hv-semaphore})
                     lights-or-arms)))))

      (testing "with a speed-limit above 60 km/h"
        (testing "distant shows vr1"
          (is (= (merge no-hp light-vr1)
                 (-> (signal/distant {:aspect :proceed
                                      :speed-limit 70
                                      :slow-speed-lights [40]
                                      :system :hv-light})
                     lights-or-arms)))

          (is (= (merge no-hp (add-to-distant semaphore-vr1 {:arm :vertical}))
                 (-> (signal/distant {:aspect :proceed
                                      :speed-limit 70
                                      :slow-speed-lights [40]
                                      :system :hv-semaphore})
                     lights-or-arms))))

        (testing "main shows hp1"
          (is (= (merge (add-to-main light-hp1 {:yellow :off}) no-vr)
                 (-> (signal/main {:aspect :proceed
                                   :speed-limit 70
                                   :slow-speed-lights [40]
                                   :system :hv-light})
                     lights-or-arms)))

          (is (= (merge (add-to-main semaphore-hp1 {:lower-arm :vertical})
                        no-vr)
                 (-> (signal/main {:aspect :proceed
                                   :speed-limit 70
                                   :slow-speed-lights [40]
                                   :system :hv-semaphore})
                     lights-or-arms))))

        (testing "combination shows hp1 & vr1"
          (is (= (merge (add-to-main light-hp1 {:yellow :off}) light-vr1)
                 (-> (signal/combination {:main {:aspect :proceed
                                                 :speed-limit 70
                                                 :slow-speed-lights [40]}
                                          :distant {:aspect :proceed
                                                    :speed-limit 70
                                                    :slow-speed-lights [40]}
                                          :system :hv-light})
                     lights-or-arms)))

          (is (= (merge (add-to-main semaphore-hp1 {:lower-arm :vertical})
                        (add-to-distant semaphore-vr1 {:arm :vertical}))
                 (-> (signal/combination {:main {:aspect :proceed
                                                 :speed-limit 70
                                                 :slow-speed-lights [40]}
                                          :distant {:aspect :proceed
                                                    :speed-limit 70
                                                    :slow-speed-lights [40]}
                                          :system :hv-semaphore})
                     lights-or-arms)))))

      (testing "with a speed-limit smaller or equal to 60 km/h"
        (testing "distant shows vr2"
          (is (= (merge no-hp light-vr2)
                 (-> (signal/distant {:aspect :proceed
                                      :speed-limit 60
                                      :slow-speed-lights [40]
                                      :system :hv-light})
                     lights-or-arms)))

          (is (= (merge no-hp semaphore-vr2)
                 (-> (signal/distant {:aspect :proceed
                                      :speed-limit 60
                                      :slow-speed-lights [40]
                                      :system :hv-semaphore})
                     lights-or-arms))))

        (testing "main shows hp2"
          (is (= (merge light-hp2 no-vr)
                 (-> (signal/main {:aspect :proceed
                                   :speed-limit 60
                                   :slow-speed-lights [40]
                                   :system :hv-light})
                     lights-or-arms)))

          (is (= (merge semaphore-hp2 no-vr)
                 (-> (signal/main {:aspect :proceed
                                   :speed-limit 60
                                   :slow-speed-lights [40]
                                   :system :hv-semaphore})
                     lights-or-arms))))

        (testing "combination shows hp2 & vr2"
          (is (= (merge light-hp2 light-vr2)
                 (-> (signal/combination {:main {:aspect :proceed
                                                 :speed-limit 60
                                                 :slow-speed-lights [40]}
                                          :distant {:aspect :proceed
                                                    :speed-limit 60
                                                    :slow-speed-lights [40]}
                                          :system :hv-light})
                     lights-or-arms)))

          (is (= (merge semaphore-hp2 semaphore-vr2)
                 (-> (signal/combination {:main {:aspect :proceed
                                                 :speed-limit 60
                                                 :slow-speed-lights [40]}
                                          :distant {:aspect :proceed
                                                    :speed-limit 60
                                                    :slow-speed-lights [40]}
                                          :system :hv-semaphore})
                     lights-or-arms)))))))

  (testing "shortened break path"
    (testing "stop"
      (testing "combination shows hp0 and no lights on the distant signal"
        (is (= (merge light-hp0 (add-to-distant light-vr-off {:white :off}))
               (-> (signal/combination {:main {:aspect :stop}
                                        :distant {:aspect :stop
                                                  :distant-addition :shortened-break-path}
                                        :system :hv-light})
                   lights-or-arms)))))

    (testing "stop expected"
      (testing "distant shows vr0 with white addition"
        (is (= (merge no-hp (add-to-distant light-vr0 {:white :on}))
               (-> (signal/distant {:aspect :stop
                                    :distant-addition :shortened-break-path
                                    :system :hv-light})
                   lights-or-arms)))

        (is (= (merge no-hp (add-to-distant semaphore-vr0 {:shortened-break-path? true}))
               (-> (signal/distant {:aspect :stop
                                    :distant-addition :shortened-break-path
                                    :system :hv-semaphore})
                   lights-or-arms))))

      (testing "combination shows hp1 and vr0 with white addition"
        (is (= (merge light-hp1 (add-to-distant light-vr0 {:white :on}))
               (-> (signal/combination {:main {:aspect :proceed}
                                        :distant {:aspect :stop
                                                  :distant-addition :shortened-break-path}
                                        :system :hv-light})
                   lights-or-arms)))

        (is (= (merge semaphore-hp1 (add-to-distant semaphore-vr0 {:shortened-break-path? true}))
               (-> (signal/combination {:main {:aspect :proceed}
                                        :distant {:aspect :stop
                                                  :distant-addition :shortened-break-path}
                                        :system :hv-semaphore})
                   lights-or-arms)))))

    (testing "proceed expected"
      (testing "distant shows vr1 with white addition"
        (is (= (merge no-hp (add-to-distant light-vr1 {:white :on}))
               (-> (signal/distant {:aspect :proceed
                                    :distant-addition :shortened-break-path
                                    :system :hv-light})
                   lights-or-arms)))

        (is (= (merge no-hp (add-to-distant semaphore-vr1 {:shortened-break-path? true}))
               (-> (signal/distant {:aspect :proceed
                                    :distant-addition :shortened-break-path
                                    :system :hv-semaphore})
                   lights-or-arms))))

      (testing "combination shows hp1 and vr1 with white addition"
        (is (= (merge light-hp1 (add-to-distant light-vr1 {:white :on}))
               (-> (signal/combination {:main {:aspect :proceed}
                                        :distant {:aspect :proceed
                                                  :distant-addition :shortened-break-path}
                                        :system :hv-light})
                   lights-or-arms)))

        (is (= (merge semaphore-hp1 (add-to-distant semaphore-vr1 {:shortened-break-path? true}))
               (-> (signal/combination {:main {:aspect :proceed}
                                        :distant {:aspect :proceed
                                                  :distant-addition :shortened-break-path}
                                        :system :hv-semaphore})
                   lights-or-arms))))))

  (testing "sh1"
    (testing "stop"
      (testing "main shows hp0"
        (is (= (merge (add-to-main light-hp0 {:yellow :off
                                              :sh1 :off
                                              :secondary-red :on})
                      no-vr)
               (-> (signal/main {:aspect :stop
                                 :slow-speed-lights [40]
                                 :sh1? true
                                 :system :hv-light})
                   lights-or-arms)))

        (is (= (merge (add-to-main semaphore-hp0 {:lower-arm :vertical
                                                  :sh1 :horizontal})
                      no-vr)
               (-> (signal/main {:aspect :stop
                                 :slow-speed-lights [40]
                                 :sh1? true
                                 :system :hv-semaphore})
                   lights-or-arms))))

      (testing "combination shows hp0"
        (is (= (merge (add-to-main light-hp0 {:yellow :off
                                              :sh1 :off
                                              :secondary-red :on})
                      light-vr-off)
               (-> (signal/combination {:main {:aspect :stop
                                               :slow-speed-lights [40]
                                               :sh1? true}
                                        :distant {:aspect :stop}
                                        :system :hv-light})
                   lights-or-arms)))

        (is (= (merge (add-to-main light-hp0 {:yellow :off
                                              :sh1 :off
                                              :secondary-red :on})
                      light-vr-off)
               (-> (signal/combination {:main {:aspect :stop
                                               :slow-speed-lights [40]
                                               :sh1? true}
                                        :distant {:aspect :proceed}
                                        :system :hv-light})
                   lights-or-arms)))

        (is (= (merge (add-to-main semaphore-hp0 {:lower-arm :vertical
                                                  :sh1 :horizontal})
                      semaphore-vr0)
               (-> (signal/combination {:main {:aspect :stop
                                               :slow-speed-lights [40]
                                               :sh1? true}
                                        :distant {:aspect :stop}
                                        :system :hv-semaphore})
                   lights-or-arms)))

        (is (= (merge (add-to-main semaphore-hp0 {:lower-arm :vertical
                                                  :sh1 :horizontal})
                      semaphore-vr0)
               (-> (signal/combination {:main {:aspect :stop
                                               :slow-speed-lights [40]
                                               :sh1? true}
                                        :distant {:aspect :proceed}
                                        :system :hv-semaphore})
                   lights-or-arms)))))

    (testing "stop + sh1"
      (testing "main shows hp0 + sh1"
        (is (= (merge (add-to-main light-hp0 {:yellow :off
                                              :sh1 :on
                                              :secondary-red :off})
                      no-vr)
               (-> (signal/main {:aspect :stop+sh1
                                 :slow-speed-lights [40]
                                 :sh1? true
                                 :system :hv-light})
                   lights-or-arms)))

        (is (= (merge (add-to-main semaphore-hp0 {:lower-arm :vertical
                                                  :sh1 :inclined})
                      no-vr)
               (-> (signal/main {:aspect :stop+sh1
                                 :slow-speed-lights [40]
                                 :sh1? true
                                 :system :hv-semaphore})
                   lights-or-arms))))

      (testing "combination shows hp0 + sh1"
        (is (= (merge (add-to-main light-hp0 {:yellow :off
                                              :sh1 :on
                                              :secondary-red :off})
                      light-vr-off)
               (-> (signal/combination {:main {:aspect :stop+sh1
                                               :slow-speed-lights [40]
                                               :sh1? true}
                                        :distant {:aspect :stop}
                                        :system :hv-light})
                   lights-or-arms)))

        (is (= (merge (add-to-main light-hp0 {:yellow :off
                                              :sh1 :on
                                              :secondary-red :off})
                      light-vr-off)
               (-> (signal/combination {:main {:aspect :stop+sh1
                                               :slow-speed-lights [40]
                                               :sh1? true}
                                        :distant {:aspect :proceed}
                                        :system :hv-light})
                   lights-or-arms)))

        (is (= (merge (add-to-main semaphore-hp0 {:lower-arm :vertical
                                                  :sh1 :inclined})
                      semaphore-vr0)
               (-> (signal/combination {:main {:aspect :stop+sh1
                                               :slow-speed-lights [40]
                                               :sh1? true}
                                        :distant {:aspect :stop}
                                        :system :hv-semaphore})
                   lights-or-arms)))

        (is (= (merge (add-to-main semaphore-hp0 {:lower-arm :vertical
                                                  :sh1 :inclined})
                      semaphore-vr0)
               (-> (signal/combination {:main {:aspect :stop+sh1
                                               :slow-speed-lights [40]
                                               :sh1? true}
                                        :distant {:aspect :proceed}
                                        :system :hv-semaphore})
                   lights-or-arms)))))

    (testing "proceed"
      (testing "main shows hp1"
        (is (= (merge (add-to-main light-hp1 {:yellow :off
                                              :sh1 :off
                                              :secondary-red :off})
                      no-vr)
               (-> (signal/main {:aspect :proceed
                                 :slow-speed-lights [40]
                                 :sh1? true
                                 :system :hv-light})
                   lights-or-arms)))

        (is (= (merge (add-to-main semaphore-hp1 {:lower-arm :vertical
                                                  :sh1 :inclined})
                      no-vr)
               (-> (signal/main {:aspect :proceed
                                 :slow-speed-lights [40]
                                 :sh1? true
                                 :system :hv-semaphore})
                   lights-or-arms))))

      (testing "combination"
        (testing "with stop expected shows hp1 & vr0"
          (is (= (merge (add-to-main light-hp1 {:yellow :off
                                                :sh1 :off
                                                :secondary-red :off})
                        light-vr0)
                 (-> (signal/combination {:main {:aspect :proceed
                                                 :slow-speed-lights [40]
                                                 :sh1? true}
                                          :distant {:aspect :stop
                                                    :slow-speed-lights [40]}
                                          :system :hv-light})
                     lights-or-arms)))

          (is (= (merge (add-to-main semaphore-hp1 {:lower-arm :vertical
                                                    :sh1 :inclined})
                        (add-to-distant semaphore-vr0 {:arm :vertical}))
                 (-> (signal/combination {:main {:aspect :proceed
                                                 :slow-speed-lights [40]
                                                 :sh1? true}
                                          :distant {:aspect :stop
                                                    :slow-speed-lights [40]}
                                          :system :hv-semaphore})
                     lights-or-arms)))))

      (testing "with a speed-limit smaller or equal to 60 km/h"
        (testing "main shows hp2"
          (is (= (merge (add-to-main light-hp2 {:sh1 :off
                                                :secondary-red :off})
                        no-vr)
                 (-> (signal/main {:aspect :proceed
                                   :speed-limit 60
                                   :slow-speed-lights [40]
                                   :sh1? true
                                   :system :hv-light})
                     lights-or-arms)))

          (is (= (merge (add-to-main semaphore-hp2 {:sh1 :inclined})
                        no-vr)
                 (-> (signal/main {:aspect :proceed
                                   :speed-limit 60
                                   :slow-speed-lights [40]
                                   :sh1? true
                                   :system :hv-semaphore})
                     lights-or-arms))))

        (testing "combination shows hp2 & vr2"
          (is (= (merge (add-to-main light-hp2 {:sh1 :off
                                                :secondary-red :off})
                        light-vr2)
                 (-> (signal/combination {:main {:aspect :proceed
                                                 :speed-limit 60
                                                 :slow-speed-lights [40]
                                                 :sh1? true}
                                          :distant {:aspect :proceed
                                                    :speed-limit 60
                                                    :slow-speed-lights [40]}
                                          :system :hv-light})
                     lights-or-arms)))

          (is (= (merge (add-to-main semaphore-hp2 {:sh1 :inclined})
                        semaphore-vr2)
                 (-> (signal/combination {:main {:aspect :proceed
                                                 :speed-limit 60
                                                 :slow-speed-lights [40]
                                                 :sh1? true}
                                          :distant {:aspect :proceed
                                                    :speed-limit 60
                                                    :slow-speed-lights [40]}
                                          :system :hv-semaphore})
                     lights-or-arms)))))))

  (testing "zs1"
    (testing "stop"
      (testing "main shows hp0"
        (is (= (merge (add-to-main light-hp0 {:yellow :off
                                              :zs1 :off})
                      no-vr)
               (-> (signal/main {:aspect :stop
                                 :slow-speed-lights [40]
                                 :zs1? true
                                 :system :hv-light})
                   lights-or-arms)))

        (is (= (merge (add-to-main semaphore-hp0 {:zs1 :off})
                      no-vr)
               (-> (signal/main {:aspect :stop
                                 :zs1? true
                                 :system :hv-semaphore})
                   lights-or-arms))))

      (testing "combination shows hp0"
        (is (= (merge (add-to-main light-hp0 {:yellow :off
                                              :zs1 :off})
                      light-vr-off)
               (-> (signal/combination {:main {:aspect :stop
                                               :slow-speed-lights [40]
                                               :zs1? true}
                                        :distant {:aspect :stop}
                                        :system :hv-light})
                   lights-or-arms)))

        (is (= (merge (add-to-main light-hp0 {:yellow :off
                                              :zs1 :off})
                      light-vr-off)
               (-> (signal/combination {:main {:aspect :stop
                                               :slow-speed-lights [40]
                                               :zs1? true}
                                        :distant {:aspect :proceed}
                                        :system :hv-light})
                   lights-or-arms)))

        (is (= (merge (add-to-main semaphore-hp0 {:lower-arm :vertical
                                                  :zs1 :off})
                      semaphore-vr0)
               (-> (signal/combination {:main {:aspect :stop
                                               :slow-speed-lights [40]
                                               :zs1? true}
                                        :distant {:aspect :stop}
                                        :system :hv-semaphore})
                   lights-or-arms)))

        (is (= (merge (add-to-main semaphore-hp0 {:zs1 :off})
                      semaphore-vr0)
               (-> (signal/combination {:main {:aspect :stop
                                               :zs1? true}
                                        :distant {:aspect :proceed}
                                        :system :hv-semaphore})
                   lights-or-arms)))))

    (testing "stop + zs1"
      (testing "main shows hp0 + zs1"
        (is (= (merge (add-to-main light-hp0 {:yellow :off
                                              :zs1 :on})
                      no-vr)
               (-> (signal/main {:aspect :stop+zs1
                                 :slow-speed-lights [40]
                                 :zs1? true
                                 :system :hv-light})
                   lights-or-arms)))

        (is (= (merge (add-to-main semaphore-hp0 {:zs1 :on})
                      no-vr)
               (-> (signal/main {:aspect :stop+zs1
                                 :zs1? true
                                 :system :hv-semaphore})
                   lights-or-arms))))

      (testing "combination shows hp0 + zs1"
        (is (= (merge (add-to-main light-hp0 {:yellow :off
                                              :zs1 :on})
                      light-vr-off)
               (-> (signal/combination {:main {:aspect :stop+zs1
                                               :slow-speed-lights [40]
                                               :zs1? true}
                                        :distant {:aspect :stop}
                                        :system :hv-light})
                   lights-or-arms)))

        (is (= (merge (add-to-main light-hp0 {:zs1 :on})
                      light-vr-off)
               (-> (signal/combination {:main {:aspect :stop+zs1
                                               :zs1? true}
                                        :distant {:aspect :proceed}
                                        :system :hv-light})
                   lights-or-arms)))

        (is (= (merge (add-to-main semaphore-hp0 {:lower-arm :vertical
                                                  :zs1 :on})
                      semaphore-vr0)
               (-> (signal/combination {:main {:aspect :stop+zs1
                                               :slow-speed-lights [40]
                                               :zs1? true}
                                        :distant {:aspect :stop}
                                        :system :hv-semaphore})
                   lights-or-arms)))

        (is (= (merge (add-to-main semaphore-hp0 {:zs1 :on})
                      semaphore-vr0)
               (-> (signal/combination {:main {:aspect :stop+zs1
                                               :zs1? true}
                                        :distant {:aspect :proceed}
                                        :system :hv-semaphore})
                   lights-or-arms)))))

    (testing "proceed"
      (testing "main shows hp1"
        (is (= (merge (add-to-main light-hp1 {:yellow :off
                                              :zs1 :off})
                      no-vr)
               (-> (signal/main {:aspect :proceed
                                 :slow-speed-lights [40]
                                 :zs1? true
                                 :system :hv-light})
                   lights-or-arms)))

        (is (= (merge (add-to-main semaphore-hp1 {:lower-arm :vertical
                                                  :zs1 :off})
                      no-vr)
               (-> (signal/main {:aspect :proceed
                                 :slow-speed-lights [40]
                                 :zs1? true
                                 :system :hv-semaphore})
                   lights-or-arms))))

      (testing "combination"
        (testing "with stop expected shows hp1 & vr0"
          (is (= (merge (add-to-main light-hp1 {:yellow :off
                                                :zs1 :off})
                        light-vr0)
                 (-> (signal/combination {:main {:aspect :proceed
                                                 :slow-speed-lights [40]
                                                 :zs1? true}
                                          :distant {:aspect :stop
                                                    :slow-speed-lights [40]}
                                          :system :hv-light})
                     lights-or-arms)))

          (is (= (merge (add-to-main semaphore-hp1 {:zs1 :off})
                        (add-to-distant semaphore-vr0 {:arm :vertical}))
                 (-> (signal/combination {:main {:aspect :proceed
                                                 :zs1? true}
                                          :distant {:aspect :stop
                                                    :slow-speed-lights [40]}
                                          :system :hv-semaphore})
                     lights-or-arms)))))))

  (testing "zs7"
    (testing "stop"
      (testing "main shows hp0"
        (is (= (merge (add-to-main light-hp0 {:yellow :off
                                              :zs7 :off})
                      no-vr)
               (-> (signal/main {:aspect :stop
                                 :slow-speed-lights [40]
                                 :zs7? true
                                 :system :hv-light})
                   lights-or-arms)))

        (is (= (merge (add-to-main semaphore-hp0 {:zs7 :off})
                      no-vr)
               (-> (signal/main {:aspect :stop
                                 :zs7? true
                                 :system :hv-semaphore})
                   lights-or-arms))))

      (testing "combination shows hp0"
        (is (= (merge (add-to-main light-hp0 {:yellow :off
                                              :zs7 :off})
                      light-vr-off)
               (-> (signal/combination {:main {:aspect :stop
                                               :slow-speed-lights [40]
                                               :zs7? true}
                                        :distant {:aspect :stop}
                                        :system :hv-light})
                   lights-or-arms)))

        (is (= (merge (add-to-main light-hp0 {:yellow :off
                                              :zs7 :off})
                      light-vr-off)
               (-> (signal/combination {:main {:aspect :stop
                                               :slow-speed-lights [40]
                                               :zs7? true}
                                        :distant {:aspect :proceed}
                                        :system :hv-light})
                   lights-or-arms)))

        (is (= (merge (add-to-main semaphore-hp0 {:lower-arm :vertical
                                                  :zs7 :off})
                      semaphore-vr0)
               (-> (signal/combination {:main {:aspect :stop
                                               :slow-speed-lights [40]
                                               :zs7? true}
                                        :distant {:aspect :stop}
                                        :system :hv-semaphore})
                   lights-or-arms)))

        (is (= (merge (add-to-main semaphore-hp0 {:zs7 :off})
                      semaphore-vr0)
               (-> (signal/combination {:main {:aspect :stop
                                               :zs7? true}
                                        :distant {:aspect :proceed}
                                        :system :hv-semaphore})
                   lights-or-arms)))))

    (testing "stop + zs7"
      (testing "main shows hp0 + zs7"
        (is (= (merge (add-to-main light-hp0 {:yellow :off
                                              :zs7 :on})
                      no-vr)
               (-> (signal/main {:aspect :stop+zs7
                                 :slow-speed-lights [40]
                                 :zs7? true
                                 :system :hv-light})
                   lights-or-arms)))

        (is (= (merge (add-to-main semaphore-hp0 {:zs7 :on})
                      no-vr)
               (-> (signal/main {:aspect :stop+zs7
                                 :zs7? true
                                 :system :hv-semaphore})
                   lights-or-arms))))

      (testing "combination shows hp0 + zs7"
        (is (= (merge (add-to-main light-hp0 {:yellow :off
                                              :zs7 :on})
                      light-vr-off)
               (-> (signal/combination {:main {:aspect :stop+zs7
                                               :slow-speed-lights [40]
                                               :zs7? true}
                                        :distant {:aspect :stop}
                                        :system :hv-light})
                   lights-or-arms)))

        (is (= (merge (add-to-main light-hp0 {:yellow :off
                                              :zs7 :on})
                      light-vr-off)
               (-> (signal/combination {:main {:aspect :stop+zs7
                                               :slow-speed-lights [40]
                                               :zs7? true}
                                        :distant {:aspect :proceed}
                                        :system :hv-light})
                   lights-or-arms)))

        (is (= (merge (add-to-main semaphore-hp0 {:lower-arm :vertical
                                                  :zs7 :on})
                      semaphore-vr0)
               (-> (signal/combination {:main {:aspect :stop+zs7
                                               :slow-speed-lights [40]
                                               :zs7? true}
                                        :distant {:aspect :stop}
                                        :system :hv-semaphore})
                   lights-or-arms)))

        (is (= (merge (add-to-main semaphore-hp0 {:zs7 :on})
                      semaphore-vr0)
               (-> (signal/combination {:main {:aspect :stop+zs7
                                               :zs7? true}
                                        :distant {:aspect :proceed}
                                        :system :hv-semaphore})
                   lights-or-arms)))))

    (testing "proceed"
      (testing "main shows hp1"
        (is (= (merge (add-to-main light-hp1 {:zs7 :off})
                      no-vr)
               (-> (signal/main {:aspect :proceed
                                 :zs7? true
                                 :system :hv-light})
                   lights-or-arms)))

        (is (= (merge (add-to-main semaphore-hp1 {:zs7 :off})
                      no-vr)
               (-> (signal/main {:aspect :proceed
                                 :zs7? true
                                 :system :hv-semaphore})
                   lights-or-arms))))

      (testing "combination"
        (testing "with stop expected shows hp1 & vr0"
          (is (= (merge (add-to-main light-hp1 {:yellow :off
                                                :zs7 :off})
                        light-vr0)
                 (-> (signal/combination {:main {:aspect :proceed
                                                 :slow-speed-lights [40]
                                                 :zs7? true}
                                          :distant {:aspect :stop
                                                    :slow-speed-lights [40]}
                                          :system :hv-light})
                     lights-or-arms)))

          (is (= (merge (add-to-main semaphore-hp1 {:lower-arm :vertical
                                                    :zs7 :off})
                        (add-to-distant semaphore-vr0 {:arm :vertical}))
                 (-> (signal/combination {:main {:aspect :proceed
                                                 :slow-speed-lights [40]
                                                 :zs7? true}
                                          :distant {:aspect :stop
                                                    :slow-speed-lights [40]}
                                          :system :hv-semaphore})
                     lights-or-arms))))))))

(deftest speed-limit-available?
  (testing "without Zs3 / slow-speed-light"
    (is (not (hv-light/speed-limit-available?
              (signal/main {:system :hv-light})
              nil)))

    (is (not (hv-light/speed-limit-available?
              (signal/main {:system :hv-light})
              40)))

    (is (not (hv-light/speed-limit-available?
              (signal/main {:system :hv-light})
              100))))

  (testing "with slow-speed-light"
    (is (hv-light/speed-limit-available?
         (signal/main {:system :hv-light
                       :slow-speed-lights [40]})
         nil))

    (is (hv-light/speed-limit-available?
         (signal/main {:system :hv-light
                       :slow-speed-lights [40]})
         40))

    (is (not (hv-light/speed-limit-available?
              (signal/main {:system :hv-light
                            :slow-speed-lights [40]})
              100))))

  (testing "with Zs3"
    (is (hv-light/speed-limit-available?
         (signal/main {:zs3 :display
                       :system :hv-light})
         nil))

    (is (hv-light/speed-limit-available?
         (signal/combination {:main {:zs3 :display}
                              :system :hv-light})
         nil))

    (is (not (hv-light/speed-limit-available?
              (signal/main {:zs3 :sign
                            :system :hv-light})
              nil)))

    (is (not (hv-light/speed-limit-available?
              (signal/combination {:main {:zs3 :sign}
                                   :system :hv-light})
              nil)))

    (is (hv-light/speed-limit-available?
         (signal/main {:zs3 :sign
                       :system :hv-light})
         100))

    (is (hv-light/speed-limit-available?
         (signal/combination {:main {:zs3 :display}
                              :system :hv-light})
         100))))
