(ns signals.hl-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [signals.hl :as hl]
   [signals.signal :as signal]))

(deftest hl-test
  (testing "stop"
    (testing "distant shows Hl10"
      (is (= {:top-yellow :on
              :top-green :off
              :top-white nil
              :red nil
              :bottom-white nil
              :bottom-yellow nil
              :replacement-red nil
              :green-stripe nil
              :yellow-stripe nil
              :shortened-break-path? false}
             (-> (signal/distant {:aspect :stop
                                  :system :hl})
                 hl/lights))))

    (testing "combination shows Hp0"
      (is (= {:top-yellow :off
              :top-green :off
              :top-white nil
              :red :on
              :bottom-white nil
              :bottom-yellow nil
              :replacement-red :off
              :green-stripe nil
              :yellow-stripe nil
              :shortened-break-path? false}
             (-> (signal/combination {:distant {:aspect :stop}
                                      :main {:aspect :stop}
                                      :system :hl})
                 hl/lights)))

      (is (= {:top-yellow :off
              :top-green :off
              :top-white nil
              :red :on
              :bottom-white nil
              :bottom-yellow nil
              :replacement-red :off
              :green-stripe nil
              :yellow-stripe nil
              :shortened-break-path? false}
             (-> (signal/combination {:distant {:aspect :proceed}
                                      :main {:aspect :stop}
                                      :system :hl})
                 hl/lights))))

    (testing "main shows Hp0"
      (is (= {:top-yellow nil
              :top-green :off
              :top-white nil
              :red :on
              :bottom-white nil
              :bottom-yellow nil
              :replacement-red :off
              :green-stripe nil
              :yellow-stripe nil
              :shortened-break-path? false}
             (-> (signal/main {:aspect :stop
                               :system :hl})
                 hl/lights)))))

  (testing "stop+sh1"
    (testing "distant shows Hl10"
      (is (= {:top-yellow :on
              :top-green :off
              :top-white nil
              :red nil
              :bottom-white nil
              :bottom-yellow nil
              :replacement-red nil
              :green-stripe nil
              :yellow-stripe nil
              :shortened-break-path? false}
             (-> (signal/distant {:aspect :stop+sh1
                                  :system :hl})
                 hl/lights))))

    (testing "combination shows Hp0"
      (is (= {:top-yellow :off
              :top-green :off
              :top-white nil
              :red :on
              :bottom-white nil
              :bottom-yellow nil
              :replacement-red :off
              :green-stripe nil
              :yellow-stripe nil
              :shortened-break-path? false}
             (-> (signal/combination {:distant {:aspect :stop}
                                      :main {:aspect :stop+sh1}
                                      :system :hl})
                 hl/lights))))

    (testing "main shows Hp0"
      (is (= {:top-yellow nil
              :top-green :off
              :top-white nil
              :red :on
              :bottom-white nil
              :bottom-yellow nil
              :replacement-red :off
              :green-stripe nil
              :yellow-stripe nil
              :shortened-break-path? false}
             (-> (signal/main {:aspect :stop+sh1
                               :system :hl})
                 hl/lights)))))

  (testing "stop+zs1"
    (testing "distant shows Hl10"
      (is (= {:top-yellow :on
              :top-green :off
              :top-white nil
              :red nil
              :bottom-white nil
              :bottom-yellow nil
              :replacement-red nil
              :green-stripe nil
              :yellow-stripe nil
              :shortened-break-path? false}
             (-> (signal/distant {:aspect :stop+zs1
                                  :system :hl})
                 hl/lights))))

    (testing "combination shows Hp0"
      (is (= {:top-yellow :off
              :top-green :off
              :top-white nil
              :red :on
              :bottom-white nil
              :bottom-yellow nil
              :replacement-red :off
              :green-stripe nil
              :yellow-stripe nil
              :shortened-break-path? false}
             (-> (signal/combination {:distant {:aspect :stop}
                                      :main {:aspect :stop+zs1}
                                      :system :hl})
                 hl/lights))))

    (testing "main shows Hp0"
      (is (= {:top-yellow nil
              :top-green :off
              :top-white nil
              :red :on
              :bottom-white nil
              :bottom-yellow nil
              :replacement-red :off
              :green-stripe nil
              :yellow-stripe nil
              :shortened-break-path? false}
             (-> (signal/main {:aspect :stop+zs1
                               :system :hl})
                 hl/lights)))))

  (testing "stop+zs7"
    (testing "distant shows Hl10"
      (is (= {:top-yellow :on
              :top-green :off
              :top-white nil
              :red nil
              :bottom-white nil
              :bottom-yellow nil
              :replacement-red nil
              :green-stripe nil
              :yellow-stripe nil
              :shortened-break-path? false}
             (-> (signal/distant {:aspect :stop+zs7
                                  :system :hl})
                 hl/lights))))

    (testing "combination shows Hp0"
      (is (= {:top-yellow :off
              :top-green :off
              :top-white nil
              :red :on
              :bottom-white nil
              :bottom-yellow nil
              :replacement-red :off
              :green-stripe nil
              :yellow-stripe nil
              :shortened-break-path? false}
             (-> (signal/combination {:distant {:aspect :stop}
                                      :main {:aspect :stop+zs7}
                                      :system :hl})
                 hl/lights))))

    (testing "main shows Hp0"
      (is (= {:top-yellow nil
              :top-green :off
              :top-white nil
              :red :on
              :bottom-white nil
              :bottom-yellow nil
              :replacement-red :off
              :green-stripe nil
              :yellow-stripe nil
              :shortened-break-path? false}
             (-> (signal/main {:aspect :stop+zs7
                               :system :hl})
                 hl/lights)))))

  (testing "proceed"
    (testing "distant shows Hl1"
      (is (= {:top-yellow :off
              :top-green :on
              :top-white nil
              :red nil
              :bottom-white nil
              :bottom-yellow nil
              :replacement-red nil
              :green-stripe nil
              :yellow-stripe nil
              :shortened-break-path? false}
             (-> (signal/distant {:aspect :proceed
                                  :system :hl})
                 hl/lights))))

    (testing "combination"
      (testing "with stop expected shows Hl10"
        (is (= {:top-yellow :on
                :top-green :off
                :top-white nil
                :red :off
                :bottom-white nil
                :bottom-yellow nil
                :replacement-red :off
                :green-stripe nil
                :yellow-stripe nil
                :shortened-break-path? false}
               (-> (signal/combination {:distant {:aspect :stop}
                                        :main {:aspect :proceed}
                                        :system :hl})
                   hl/lights))))

      (testing "with proceed expected shows Hl1"
        (is (= {:top-yellow :off
                :top-green :on
                :top-white nil
                :red :off
                :bottom-white nil
                :bottom-yellow nil
                :replacement-red :off
                :green-stripe nil
                :yellow-stripe nil
                :shortened-break-path? false}
               (-> (signal/combination {:distant {:aspect :proceed}
                                        :main {:aspect :proceed}
                                        :system :hl})
                   hl/lights)))))

    (testing "main shows Hl1"
      (is (= {:top-yellow nil
              :top-green :on
              :top-white nil
              :red :off
              :bottom-white nil
              :bottom-yellow nil
              :replacement-red :off
              :green-stripe nil
              :yellow-stripe nil
              :shortened-break-path? false}
             (-> (signal/main {:aspect :proceed
                               :system :hl})
                 hl/lights)))))

  (testing "shortened break path"
    (testing "stop"
      (testing "distant shows Hl10 with marked Ne2"
        (is (= {:top-yellow :on
                :top-green :off
                :top-white nil
                :red nil
                :bottom-white nil
                :bottom-yellow nil
                :replacement-red nil
                :green-stripe nil
                :yellow-stripe nil
                :shortened-break-path? true}
               (-> (signal/distant {:aspect :stop
                                    :distant-addition :shortened-break-path
                                    :system :hl})
                   hl/lights))))

      (testing "combination shows Hp0"
        (is (= {:top-yellow :off
                :top-green :off
                :top-white nil
                :red :on
                :bottom-white nil
                :bottom-yellow nil
                :replacement-red :off
                :green-stripe nil
                :yellow-stripe nil
                :shortened-break-path? true}
               (-> (signal/combination {:distant {:aspect :stop
                                                  :distant-addition :shortened-break-path}
                                        :main {:aspect :stop}
                                        :system :hl})
                   hl/lights))))))

  (testing "sh1"
    (testing "stop"
      (testing "combination shows Hp0"
        (is (= {:top-yellow :off
                :top-green :off
                :top-white :off
                :red :on
                :bottom-white :off
                :bottom-yellow nil
                :replacement-red :off
                :green-stripe nil
                :yellow-stripe nil
                :shortened-break-path? false}
               (-> (signal/combination {:distant {:aspect :stop}
                                        :main {:aspect :stop
                                               :sh1? true}
                                        :system :hl})
                   hl/lights))))

      (testing "main show Hp0"
        (is (= {:top-yellow nil
                :top-green :off
                :top-white :off
                :red :on
                :bottom-white :off
                :bottom-yellow nil
                :replacement-red :off
                :green-stripe nil
                :yellow-stripe nil
                :shortened-break-path? false}
               (-> (signal/main {:aspect :stop
                                 :sh1? true
                                 :system :hl})
                   hl/lights)))))

    (testing "stop+sh1"
      (testing "combination shows Hp0 and sh1"
        (is (= {:top-yellow :off
                :top-green :off
                :top-white :on
                :red :on
                :bottom-white :on
                :bottom-yellow nil
                :replacement-red :off
                :green-stripe nil
                :yellow-stripe nil
                :shortened-break-path? false}
               (-> (signal/combination {:distant {:aspect :stop}
                                        :main {:aspect :stop+sh1
                                               :sh1? true}
                                        :system :hl})
                   hl/lights))))

      (testing "main shows Hp0 and Sh1"
        (is (= {:top-yellow nil
                :top-green :off
                :top-white :on
                :red :on
                :bottom-white :on
                :bottom-yellow nil
                :replacement-red :off
                :green-stripe nil
                :yellow-stripe nil
                :shortened-break-path? false}
               (-> (signal/main {:aspect :stop+sh1
                                 :sh1? true
                                 :system :hl})
                   hl/lights)))))

    (testing "proceed"
      (testing "with stop+sh1 expected shows Hl10"
        (is (= {:top-yellow :on
                :top-green :off
                :top-white :off
                :red :off
                :bottom-white :off
                :bottom-yellow nil
                :replacement-red :off
                :green-stripe nil
                :yellow-stripe nil
                :shortened-break-path? false}
               (-> (signal/combination {:distant {:aspect :stop+sh1}
                                        :main {:aspect :proceed
                                               :sh1? true}
                                        :system :hl})
                   hl/lights))))

      (testing "main shows Hl1"
        (is (= {:top-yellow nil
                :top-green :on
                :top-white :off
                :red :off
                :bottom-white :off
                :bottom-yellow nil
                :replacement-red :off
                :green-stripe nil
                :yellow-stripe nil
                :shortened-break-path? false}
               (-> (signal/main {:aspect :proceed
                                 :sh1? true
                                 :system :hl})
                   hl/lights))))))

  (testing "zs1"
    (testing "stop"
      (testing "combination shows Hp0"
        (is (= {:top-yellow :off
                :top-green :off
                :top-white nil
                :red :on
                :bottom-white :off
                :bottom-yellow nil
                :replacement-red :off
                :green-stripe nil
                :yellow-stripe nil
                :shortened-break-path? false}
               (-> (signal/combination {:distant {:aspect :stop}
                                        :main {:aspect :stop
                                               :zs1? true}
                                        :system :hl})
                   hl/lights))))

      (testing "main shows Hp0"
        (is (= {:top-yellow nil
                :top-green :off
                :top-white nil
                :red :on
                :bottom-white :off
                :bottom-yellow nil
                :replacement-red :off
                :green-stripe nil
                :yellow-stripe nil
                :shortened-break-path? false}
               (-> (signal/main {:aspect :stop
                                 :zs1? true
                                 :system :hl})
                   hl/lights)))))

    (testing "stop+zs1"
      (testing "combination shows Hp0 and Zs1"
        (is (= {:top-yellow :off
                :top-green :off
                :top-white nil
                :red :on
                :bottom-white :blinking
                :bottom-yellow nil
                :replacement-red :off
                :green-stripe nil
                :yellow-stripe nil
                :shortened-break-path? false}
               (-> (signal/combination {:distant {:aspect :stop}
                                        :main {:aspect :stop+zs1
                                               :zs1? true}
                                        :system :hl})
                   hl/lights))))

      (testing "main shows Hp0 & Zs1"
        (is (= {:top-yellow nil
                :top-green :off
                :top-white nil
                :red :on
                :bottom-white :blinking
                :bottom-yellow nil
                :replacement-red :off
                :green-stripe nil
                :yellow-stripe nil
                :shortened-break-path? false}
               (-> (signal/main {:aspect :stop+zs1
                                 :zs1? true
                                 :system :hl})
                   hl/lights))))

      (testing "in combination with sh1"
        (testing "combination shows Hp0 and Zs1"
          (is (= {:top-yellow :off
                  :top-green :off
                  :top-white :off
                  :red :on
                  :bottom-white :blinking
                  :bottom-yellow nil
                  :replacement-red :off
                  :green-stripe nil
                  :yellow-stripe nil
                  :shortened-break-path? false}
                 (-> (signal/combination {:distant {:aspect :stop}
                                          :main {:aspect :stop+zs1
                                                 :sh1? true
                                                 :zs1? true}
                                          :system :hl})
                     hl/lights))))

        (testing "main shows Hp0 and Zs1"
          (is (= {:top-yellow nil
                  :top-green :off
                  :top-white :off
                  :red :on
                  :bottom-white :blinking
                  :bottom-yellow nil
                  :replacement-red :off
                  :green-stripe nil
                  :yellow-stripe nil
                  :shortened-break-path? false}
                 (-> (signal/main {:aspect :stop+zs1
                                   :sh1? true
                                   :zs1? true
                                   :system :hl})
                     hl/lights))))))

    (testing "proceed"
      (testing "combination with stop+zs1 expected shows Hl10"
        (is (= {:top-yellow :on
                :top-green :off
                :top-white nil
                :red :off
                :bottom-white :off
                :bottom-yellow nil
                :replacement-red :off
                :green-stripe nil
                :yellow-stripe nil
                :shortened-break-path? false}
               (-> (signal/combination {:distant {:aspect :stop+zs1}
                                        :main {:aspect :proceed
                                               :zs1? true}
                                        :system :hl})
                   hl/lights))))

      (testing "main shows Hl1"
        (is (= {:top-yellow nil
                :top-green :on
                :top-white nil
                :red :off
                :bottom-white :off
                :bottom-yellow nil
                :replacement-red :off
                :green-stripe nil
                :yellow-stripe nil
                :shortened-break-path? false}
               (-> (signal/main {:aspect :proceed
                                 :zs1? true
                                 :system :hl})
                   hl/lights))))))

  (testing "slow-speed-lights"
    (testing "with 40"
      (testing "stop"
        (testing "distant shows Hl10"
          (is (= {:top-yellow :on
                  :top-green :off
                  :top-white nil
                  :red nil
                  :bottom-white nil
                  :bottom-yellow nil
                  :replacement-red nil
                  :green-stripe nil
                  :yellow-stripe nil
                  :shortened-break-path? false}
                 (-> (signal/distant {:aspect :stop
                                      :slow-speed-lights [40]
                                      :speed-limit 40
                                      :system :hl})
                     hl/lights))))

        (testing "combination shows hp0"
          (is (= {:top-yellow :off
                  :top-green :off
                  :top-white nil
                  :red :on
                  :bottom-white nil
                  :bottom-yellow :off
                  :replacement-red :off
                  :green-stripe nil
                  :yellow-stripe nil
                  :shortened-break-path? false}
                 (-> (signal/combination {:main {:aspect :stop
                                                 :slow-speed-lights [40]
                                                 :speed-limit 40}
                                          :distant {:aspect :stop}
                                          :system :hl})
                     hl/lights)))

          (is (= {:top-yellow :off
                  :top-green :off
                  :top-white nil
                  :red :on
                  :bottom-white nil
                  :bottom-yellow :off
                  :replacement-red :off
                  :green-stripe nil
                  :yellow-stripe nil
                  :shortened-break-path? false}
                 (-> (signal/combination {:main {:aspect :stop
                                                 :slow-speed-lights [40]
                                                 :speed-limit 40}
                                          :distant {:aspect :proceed}
                                          :system :hl})
                     hl/lights))))

        (testing "main shows Hp0"
          (is (= {:top-yellow nil
                  :top-green :off
                  :top-white nil
                  :red :on
                  :bottom-white nil
                  :bottom-yellow :off
                  :replacement-red :off
                  :green-stripe nil
                  :yellow-stripe nil
                  :shortened-break-path? false}
                 (-> (signal/main {:aspect :stop
                                   :slow-speed-lights [40]
                                   :speed-limit 40
                                   :system :hl})
                     hl/lights)))))

      (testing "proceed"
        (testing "distant shows Hl1"
          (is (= {:top-yellow :off
                  :top-green :on
                  :top-white nil
                  :red nil
                  :bottom-white nil
                  :bottom-yellow nil
                  :replacement-red nil
                  :green-stripe nil
                  :yellow-stripe nil
                  :shortened-break-path? false}
                 (-> (signal/distant {:aspect :proceed
                                      :slow-speed-lights [40]
                                      :system :hl})
                     hl/lights))))

        (testing "combination"
          (testing "with stop expected shows Hl10"
            (is (= {:top-yellow :on
                    :top-green :off
                    :top-white nil
                    :red :off
                    :bottom-white nil
                    :bottom-yellow :off
                    :replacement-red :off
                    :green-stripe nil
                    :yellow-stripe nil
                    :shortened-break-path? false}
                   (-> (signal/combination {:main {:aspect :proceed
                                                   :slow-speed-lights [40]}
                                            :distant {:aspect :stop
                                                      :slow-speed-lights [40]}
                                            :system :hl})
                       hl/lights))))

          (testing "with proceed expected shows Hl1"
            (is (= {:top-yellow :off
                    :top-green :on
                    :top-white nil
                    :red :off
                    :bottom-white nil
                    :bottom-yellow :off
                    :replacement-red :off
                    :green-stripe nil
                    :yellow-stripe nil
                    :shortened-break-path? false}
                   (-> (signal/combination {:main {:aspect :proceed
                                                   :slow-speed-lights [40]}
                                            :distant {:aspect :proceed
                                                      :slow-speed-lights [40]}
                                            :system :hl})
                       hl/lights)))))

        (testing "main shows Hl1"
          (is (= {:top-yellow nil
                  :top-green :on
                  :top-white nil
                  :red :off
                  :bottom-white nil
                  :bottom-yellow :off
                  :replacement-red :off
                  :green-stripe nil
                  :yellow-stripe nil
                  :shortened-break-path? false}
                 (-> (signal/main {:aspect :proceed
                                   :slow-speed-lights [40]
                                   :system :hl})
                     hl/lights))))

        (testing "with a speed-limit above 40 km/h"
          (testing "distant shows Hl1"
            (is (= {:top-yellow :off
                    :top-green :on
                    :top-white nil
                    :red nil
                    :bottom-white nil
                    :bottom-yellow nil
                    :replacement-red nil
                    :green-stripe nil
                    :yellow-stripe nil
                    :shortened-break-path? false}
                   (-> (signal/distant {:aspect :proceed
                                        :speed-limit 50
                                        :slow-speed-lights [40]
                                        :system :hl})
                       hl/lights))))

          (testing "combination shows Hl1"
            (is (= {:top-yellow :off
                    :top-green :on
                    :top-white nil
                    :red :off
                    :bottom-white nil
                    :bottom-yellow :off
                    :replacement-red :off
                    :green-stripe nil
                    :yellow-stripe nil
                    :shortened-break-path? false}
                   (-> (signal/combination {:main {:aspect :proceed
                                                   :speed-limit 50
                                                   :slow-speed-lights [40]}
                                            :distant {:aspect :proceed
                                                      :speed-limit 50
                                                      :slow-speed-lights [40]}
                                            :system :hl})
                       hl/lights))))

          (testing "main shows Hl1"
            (is (= {:top-yellow nil
                    :top-green :on
                    :top-white nil
                    :red :off
                    :bottom-white nil
                    :bottom-yellow :off
                    :replacement-red :off
                    :green-stripe nil
                    :yellow-stripe nil
                    :shortened-break-path? false}
                   (-> (signal/main {:aspect :proceed
                                     :speed-limit 50
                                     :slow-speed-lights [40]
                                     :system :hl})
                       hl/lights)))))

        (testing "with a speed-limit smaller or equal to 40 km/h"
          (testing "distant shows Hl7"
            (is (= {:top-yellow :blinking
                    :top-green :off
                    :top-white nil
                    :red nil
                    :bottom-white nil
                    :bottom-yellow nil
                    :replacement-red nil
                    :green-stripe nil
                    :yellow-stripe nil
                    :shortened-break-path? false}
                   (-> (signal/distant {:aspect :proceed
                                        :speed-limit 40
                                        :slow-speed-lights [40]
                                        :system :hl})
                       hl/lights))))

          (testing "combination shows Hl9a"
            (is (= {:top-yellow :blinking
                    :top-green :off
                    :top-white nil
                    :red :off
                    :bottom-white nil
                    :bottom-yellow :on
                    :replacement-red :off
                    :green-stripe nil
                    :yellow-stripe nil
                    :shortened-break-path? false}
                   (-> (signal/combination {:main {:aspect :proceed
                                                   :speed-limit 40
                                                   :slow-speed-lights [40]}
                                            :distant {:aspect :proceed
                                                      :speed-limit 40
                                                      :slow-speed-lights [40]}
                                            :system :hl})
                       hl/lights))))

          (testing "main shows Hl3a"
            (is (= {:top-yellow nil
                    :top-green :on
                    :top-white nil
                    :red :off
                    :bottom-white nil
                    :bottom-yellow :on
                    :replacement-red :off
                    :green-stripe nil
                    :yellow-stripe nil
                    :shortened-break-path? false}
                   (-> (signal/main {:aspect :proceed
                                     :speed-limit 40
                                     :slow-speed-lights [40]
                                     :system :hl})
                       hl/lights)))))))

    (testing "with 60"
      (testing "stop"
        (testing "distant shows Hl10"
          (is (= {:top-yellow :on
                  :top-green :off
                  :top-white nil
                  :red nil
                  :bottom-white nil
                  :bottom-yellow nil
                  :replacement-red nil
                  :green-stripe nil
                  :yellow-stripe nil
                  :shortened-break-path? false}
                 (-> (signal/distant {:aspect :stop
                                      :slow-speed-lights [40 60]
                                      :speed-limit 60
                                      :system :hl})
                     hl/lights))))

        (testing "combination shows hp0"
          (is (= {:top-yellow :off
                  :top-green :off
                  :top-white nil
                  :red :on
                  :bottom-white nil
                  :bottom-yellow :off
                  :replacement-red :off
                  :green-stripe nil
                  :yellow-stripe :off
                  :shortened-break-path? false}
                 (-> (signal/combination {:main {:aspect :stop
                                                 :slow-speed-lights [40 60]
                                                 :speed-limit 60}
                                          :distant {:aspect :stop}
                                          :system :hl})
                     hl/lights)))

          (is (= {:top-yellow :off
                  :top-green :off
                  :top-white nil
                  :red :on
                  :bottom-white nil
                  :bottom-yellow :off
                  :replacement-red :off
                  :green-stripe nil
                  :yellow-stripe :off
                  :shortened-break-path? false}
                 (-> (signal/combination {:main {:aspect :stop
                                                 :slow-speed-lights [40 60]
                                                 :speed-limit 60}
                                          :distant {:aspect :proceed}
                                          :system :hl})
                     hl/lights))))

        (testing "main shows Hp0"
          (is (= {:top-yellow nil
                  :top-green :off
                  :top-white nil
                  :red :on
                  :bottom-white nil
                  :bottom-yellow :off
                  :replacement-red :off
                  :green-stripe nil
                  :yellow-stripe :off
                  :shortened-break-path? false}
                 (-> (signal/main {:aspect :stop
                                   :slow-speed-lights [40 60]
                                   :speed-limit 60
                                   :system :hl})
                     hl/lights)))))

      (testing "proceed"
        (testing "distant shows Hl1"
          (is (= {:top-yellow :off
                  :top-green :on
                  :top-white nil
                  :red nil
                  :bottom-white nil
                  :bottom-yellow nil
                  :replacement-red nil
                  :green-stripe nil
                  :yellow-stripe nil
                  :shortened-break-path? false}
                 (-> (signal/distant {:aspect :proceed
                                      :slow-speed-lights [40 60]
                                      :system :hl})
                     hl/lights))))

        (testing "combination"
          (testing "with stop expected shows Hl10"
            (is (= {:top-yellow :on
                    :top-green :off
                    :top-white nil
                    :red :off
                    :bottom-white nil
                    :bottom-yellow :off
                    :replacement-red :off
                    :green-stripe nil
                    :yellow-stripe :off
                    :shortened-break-path? false}
                   (-> (signal/combination {:main {:aspect :proceed
                                                   :slow-speed-lights [40 60]}
                                            :distant {:aspect :stop
                                                      :slow-speed-lights [40 60]}
                                            :system :hl})
                       hl/lights))))

          (testing "with proceed expected shows Hl1"
            (is (= {:top-yellow :off
                    :top-green :on
                    :top-white nil
                    :red :off
                    :bottom-white nil
                    :bottom-yellow :off
                    :replacement-red :off
                    :green-stripe nil
                    :yellow-stripe :off
                    :shortened-break-path? false}
                   (-> (signal/combination {:main {:aspect :proceed
                                                   :slow-speed-lights [40 60]}
                                            :distant {:aspect :proceed
                                                      :slow-speed-lights [40 60]}
                                            :system :hl})
                       hl/lights)))))

        (testing "main shows Hl1"
          (is (= {:top-yellow nil
                  :top-green :on
                  :top-white nil
                  :red :off
                  :bottom-white nil
                  :bottom-yellow :off
                  :replacement-red :off
                  :green-stripe nil
                  :yellow-stripe :off
                  :shortened-break-path? false}
                 (-> (signal/main {:aspect :proceed
                                   :slow-speed-lights [40 60]
                                   :system :hl})
                     hl/lights))))

        (testing "with a speed-limit above 60 km/h"
          (testing "distant shows Hl1"
            (is (= {:top-yellow :off
                    :top-green :on
                    :top-white nil
                    :red nil
                    :bottom-white nil
                    :bottom-yellow nil
                    :replacement-red nil
                    :green-stripe nil
                    :yellow-stripe nil
                    :shortened-break-path? false}
                   (-> (signal/distant {:aspect :proceed
                                        :speed-limit 70
                                        :slow-speed-lights [40 60]
                                        :system :hl})
                       hl/lights))))

          (testing "combination shows Hl1"
            (is (= {:top-yellow :off
                    :top-green :on
                    :top-white nil
                    :red :off
                    :bottom-white nil
                    :bottom-yellow :off
                    :replacement-red :off
                    :green-stripe nil
                    :yellow-stripe :off
                    :shortened-break-path? false}
                   (-> (signal/combination {:main {:aspect :proceed
                                                   :speed-limit 70
                                                   :slow-speed-lights [40 60]}
                                            :distant {:aspect :proceed
                                                      :speed-limit 70
                                                      :slow-speed-lights [40 60]}
                                            :system :hl})
                       hl/lights))))

          (testing "main shows Hl1"
            (is (= {:top-yellow nil
                    :top-green :on
                    :top-white nil
                    :red :off
                    :bottom-white nil
                    :bottom-yellow :off
                    :replacement-red :off
                    :green-stripe nil
                    :yellow-stripe :off
                    :shortened-break-path? false}
                   (-> (signal/main {:aspect :proceed
                                     :speed-limit 70
                                     :slow-speed-lights [40 60]
                                     :system :hl})
                       hl/lights)))))

        (testing "with a speed-limit smaller or equal to 60 km/h"
          (testing "distant shows Hl7"
            (is (= {:top-yellow :blinking
                    :top-green :off
                    :top-white nil
                    :red nil
                    :bottom-white nil
                    :bottom-yellow nil
                    :replacement-red nil
                    :green-stripe nil
                    :yellow-stripe nil
                    :shortened-break-path? false}
                   (-> (signal/distant {:aspect :proceed
                                        :speed-limit 60
                                        :slow-speed-lights [40 60]
                                        :system :hl})
                       hl/lights))))

          (testing "combination shows Hl9b"
            (is (= {:top-yellow :blinking
                    :top-green :off
                    :top-white nil
                    :red :off
                    :bottom-white nil
                    :bottom-yellow :on
                    :replacement-red :off
                    :green-stripe nil
                    :yellow-stripe :on
                    :shortened-break-path? false}
                   (-> (signal/combination {:main {:aspect :proceed
                                                   :speed-limit 60
                                                   :slow-speed-lights [40 60]}
                                            :distant {:aspect :proceed
                                                      :speed-limit 60
                                                      :slow-speed-lights [40 60]}
                                            :system :hl})
                       hl/lights))))

          (testing "main shows Hl3b"
            (is (= {:top-yellow nil
                    :top-green :on
                    :top-white nil
                    :red :off
                    :bottom-white nil
                    :bottom-yellow :on
                    :replacement-red :off
                    :green-stripe nil
                    :yellow-stripe :on
                    :shortened-break-path? false}
                   (-> (signal/main {:aspect :proceed
                                     :speed-limit 60
                                     :slow-speed-lights [40 60]
                                     :system :hl})
                       hl/lights)))))))

    (testing "with 100"
      (testing "stop"
        (testing "distant shows Hl10"
          (is (= {:top-yellow :on
                  :top-green :off
                  :top-white nil
                  :red nil
                  :bottom-white nil
                  :bottom-yellow nil
                  :replacement-red nil
                  :green-stripe nil
                  :yellow-stripe nil
                  :shortened-break-path? false}
                 (-> (signal/distant {:aspect :stop
                                      :slow-speed-lights [40 100]
                                      :speed-limit 100
                                      :system :hl})
                     hl/lights))))

        (testing "combination shows hp0"
          (is (= {:top-yellow :off
                  :top-green :off
                  :top-white nil
                  :red :on
                  :bottom-white nil
                  :bottom-yellow :off
                  :replacement-red :off
                  :green-stripe :off
                  :yellow-stripe nil
                  :shortened-break-path? false}
                 (-> (signal/combination {:main {:aspect :stop
                                                 :slow-speed-lights [40 100]
                                                 :speed-limit 100}
                                          :distant {:aspect :stop}
                                          :system :hl})
                     hl/lights)))

          (is (= {:top-yellow :off
                  :top-green :off
                  :top-white nil
                  :red :on
                  :bottom-white nil
                  :bottom-yellow :off
                  :replacement-red :off
                  :green-stripe :off
                  :yellow-stripe nil
                  :shortened-break-path? false}
                 (-> (signal/combination {:main {:aspect :stop
                                                 :slow-speed-lights [40 100]
                                                 :speed-limit 100}
                                          :distant {:aspect :proceed}
                                          :system :hl})
                     hl/lights))))

        (testing "main shows Hp0"
          (is (= {:top-yellow nil
                  :top-green :off
                  :top-white nil
                  :red :on
                  :bottom-white nil
                  :bottom-yellow :off
                  :replacement-red :off
                  :green-stripe :off
                  :yellow-stripe nil
                  :shortened-break-path? false}
                 (-> (signal/main {:aspect :stop
                                   :slow-speed-lights [40 100]
                                   :speed-limit 100
                                   :system :hl})
                     hl/lights)))))

      (testing "proceed"
        (testing "distant shows Hl1"
          (is (= {:top-yellow :off
                  :top-green :on
                  :top-white nil
                  :red nil
                  :bottom-white nil
                  :bottom-yellow nil
                  :replacement-red nil
                  :green-stripe nil
                  :yellow-stripe nil
                  :shortened-break-path? false}
                 (-> (signal/distant {:aspect :proceed
                                      :slow-speed-lights [40 100]
                                      :system :hl})
                     hl/lights))))

        (testing "combination"
          (testing "with stop expected shows Hl10"
            (is (= {:top-yellow :on
                    :top-green :off
                    :top-white nil
                    :red :off
                    :bottom-white nil
                    :bottom-yellow :off
                    :replacement-red :off
                    :green-stripe :off
                    :yellow-stripe nil
                    :shortened-break-path? false}
                   (-> (signal/combination {:main {:aspect :proceed
                                                   :slow-speed-lights [40 100]}
                                            :distant {:aspect :stop
                                                      :slow-speed-lights [40 100]}
                                            :system :hl})
                       hl/lights))))

          (testing "with proceed expected shows Hl1"
            (is (= {:top-yellow :off
                    :top-green :on
                    :top-white nil
                    :red :off
                    :bottom-white nil
                    :bottom-yellow :off
                    :replacement-red :off
                    :green-stripe :off
                    :yellow-stripe nil
                    :shortened-break-path? false}
                   (-> (signal/combination {:main {:aspect :proceed
                                                   :slow-speed-lights [40 100]}
                                            :distant {:aspect :proceed
                                                      :slow-speed-lights [40 100]}
                                            :system :hl})
                       hl/lights)))))

        (testing "main shows Hl1"
          (is (= {:top-yellow nil
                  :top-green :on
                  :top-white nil
                  :red :off
                  :bottom-white nil
                  :bottom-yellow :off
                  :replacement-red :off
                  :green-stripe :off
                  :yellow-stripe nil
                  :shortened-break-path? false}
                 (-> (signal/main {:aspect :proceed
                                   :slow-speed-lights [40 100]
                                   :system :hl})
                     hl/lights))))

        (testing "with a speed-limit above 100 km/h"
          (testing "distant shows Hl1"
            (is (= {:top-yellow :off
                    :top-green :on
                    :top-white nil
                    :red nil
                    :bottom-white nil
                    :bottom-yellow nil
                    :replacement-red nil
                    :green-stripe nil
                    :yellow-stripe nil
                    :shortened-break-path? false}
                   (-> (signal/distant {:aspect :proceed
                                        :speed-limit 110
                                        :slow-speed-lights [40 100]
                                        :system :hl})
                       hl/lights))))

          (testing "combination shows Hl1"
            (is (= {:top-yellow :off
                    :top-green :on
                    :top-white nil
                    :red :off
                    :bottom-white nil
                    :bottom-yellow :off
                    :replacement-red :off
                    :green-stripe :off
                    :yellow-stripe nil
                    :shortened-break-path? false}
                   (-> (signal/combination {:main {:aspect :proceed
                                                   :speed-limit 110
                                                   :slow-speed-lights [40 100]}
                                            :distant {:aspect :proceed
                                                      :speed-limit 110
                                                      :slow-speed-lights [40 100]}
                                            :system :hl})
                       hl/lights))))

          (testing "main shows Hl1"
            (is (= {:top-yellow nil
                    :top-green :on
                    :top-white nil
                    :red :off
                    :bottom-white nil
                    :bottom-yellow :off
                    :replacement-red :off
                    :green-stripe :off
                    :yellow-stripe nil
                    :shortened-break-path? false}
                   (-> (signal/main {:aspect :proceed
                                     :speed-limit 110
                                     :slow-speed-lights [40 100]
                                     :system :hl})
                       hl/lights)))))

        (testing "with a speed-limit smaller or equal to 100 km/h"
          (testing "distant shows Hl4"
            (is (= {:top-yellow :off
                    :top-green :blinking
                    :top-white nil
                    :red nil
                    :bottom-white nil
                    :bottom-yellow nil
                    :replacement-red nil
                    :green-stripe nil
                    :yellow-stripe nil
                    :shortened-break-path? false}
                   (-> (signal/distant {:aspect :proceed
                                        :speed-limit 100
                                        :slow-speed-lights [40 100]
                                        :system :hl})
                       hl/lights))))

          (testing "combination shows Hl5"
            (is (= {:top-yellow :off
                    :top-green :blinking
                    :top-white nil
                    :red :off
                    :bottom-white nil
                    :bottom-yellow :on
                    :replacement-red :off
                    :green-stripe :on
                    :yellow-stripe nil
                    :shortened-break-path? false}
                   (-> (signal/combination {:main {:aspect :proceed
                                                   :speed-limit 100
                                                   :slow-speed-lights [40 100]}
                                            :distant {:aspect :proceed
                                                      :speed-limit 100
                                                      :slow-speed-lights [40 100]}
                                            :system :hl})
                       hl/lights))))

          (testing "main shows Hl2"
            (is (= {:top-yellow nil
                    :top-green :on
                    :top-white nil
                    :red :off
                    :bottom-white nil
                    :bottom-yellow :on
                    :replacement-red :off
                    :green-stripe :on
                    :yellow-stripe nil
                    :shortened-break-path? false}
                   (-> (signal/main {:aspect :proceed
                                     :speed-limit 100
                                     :slow-speed-lights [40 100]
                                     :system :hl})
                       hl/lights)))))))))

(deftest speed-limit-available?
  (testing "without slow-speed-lights"
    (is (not (hl/speed-limit-available?
              (signal/main {:system :hl})
              nil)))

    (is (not (hl/speed-limit-available?
              (signal/combination {:main {}
                                   :system :hl})
              nil)))

    (is (not (hl/speed-limit-available?
              (signal/main {:system :hl})
              40)))

    (is (not (hl/speed-limit-available?
              (signal/combination {:main {}
                                   :system :hl})
              40)))

    (is (not (hl/speed-limit-available?
              (signal/main {:system :hl})
              60)))

    (is (not (hl/speed-limit-available?
              (signal/main {:system :hl})
              100))))

  (testing "with slow-speed-lights=[40]"
    (is (hl/speed-limit-available?
         (signal/main {:slow-speed-lights [40]
                       :system :hl})
         nil))

    (is (hl/speed-limit-available?
         (signal/combination {:main {:slow-speed-lights [40]}
                              :system :hl})
         nil))

    (is (hl/speed-limit-available?
         (signal/main {:slow-speed-lights [40]
                       :system :hl})
         40))

    (is (hl/speed-limit-available?
         (signal/combination {:main {:slow-speed-lights [40]}
                              :system :hl})
         40))

    (is (not (hl/speed-limit-available?
              (signal/main {:system :hl
                            :slow-speed-lights [40]})
              60)))

    (is (not (hl/speed-limit-available?
              (signal/main {:system :hl
                            :slow-speed-lights [40]})
              100))))

  (testing "with slow-speed-lights=[40 60]"
    (is (hl/speed-limit-available?
         (signal/main {:slow-speed-lights [40 60]
                       :system :hl})
         nil))

    (is (hl/speed-limit-available?
         (signal/combination {:main {:slow-speed-lights [40 60]}
                              :system :hl})
         nil))

    (is (hl/speed-limit-available?
         (signal/main {:slow-speed-lights [40 60]
                       :system :hl})
         40))

    (is (hl/speed-limit-available?
         (signal/combination {:main {:slow-speed-lights [40 60]}
                              :system :hl})
         40))

    (is (hl/speed-limit-available?
         (signal/main {:system :hl
                       :slow-speed-lights [40 60]})
         60))

    (is (not (hl/speed-limit-available?
              (signal/main {:system :hl
                            :slow-speed-lights [40 60]})
              100))))

  (testing "with slow-speed-lights=[40 100]"
    (is (hl/speed-limit-available?
         (signal/main {:slow-speed-lights [40 100]
                       :system :hl})
         nil))

    (is (hl/speed-limit-available?
         (signal/combination {:main {:slow-speed-lights [40 100]}
                              :system :hl})
         nil))

    (is (hl/speed-limit-available?
         (signal/main {:slow-speed-lights [40 100]
                       :system :hl})
         40))

    (is (hl/speed-limit-available?
         (signal/combination {:main {:slow-speed-lights [40 100]}
                              :system :hl})
         40))

    (is (not (hl/speed-limit-available?
              (signal/main {:system :hl
                            :slow-speed-lights [40 100]})
              60)))

    (is (hl/speed-limit-available?
         (signal/main {:system :hl
                       :slow-speed-lights [40 100]})
         100))))
