(ns signals.ks-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [signals.ks :as ks]
   [signals.signal :as signal]))

(deftest main-test
  (testing "stop shows hp0"
    (is (= {:top-white nil
            :red :on
            :green :off
            :yellow nil
            :center-white nil
            :zs7 nil
            :bottom-white nil}
           (-> (signal/main {:aspect :stop
                             :system :ks})
               ks/lights))))

  (testing "proceed shows ks1"
    (is (= {:top-white nil
            :red :off
            :green :on
            :yellow nil
            :center-white nil
            :zs7 nil
            :bottom-white nil}
           (-> (signal/main {:aspect :proceed
                             :system :ks})
               ks/lights))))

  (testing "sh1"
    (testing "stop+sh1 without sh1 shows hp0"
      (is (= {:top-white nil
              :red :on
              :green :off
              :yellow nil
              :center-white nil
              :zs7 nil
              :bottom-white nil}
             (-> (signal/main {:aspect :stop+sh1
                               :system :ks})
                 ks/lights))))

    (testing "stop+sh1 with sh1 shows hp0 and sh1"
      (is (= {:top-white nil
              :red :on
              :green :off
              :yellow nil
              :center-white :on
              :zs7 nil
              :bottom-white :on}
             (-> (signal/main {:aspect :stop+sh1
                               :sh1? true
                               :system :ks})
                 ks/lights))))

    (testing "proceed with sh1 shows ks1"
      (is (= {:top-white nil
              :red :off
              :green :on
              :yellow nil
              :center-white :off
              :zs7 nil
              :bottom-white :off}
             (-> (signal/main {:aspect :proceed
                               :sh1? true
                               :system :ks})
                 ks/lights)))))

  (testing "zs1"
    (testing "stop+zs1 without zs1 shows hp0"
      (is (= {:top-white nil
              :red :on
              :green :off
              :yellow nil
              :center-white nil
              :zs7 nil
              :bottom-white nil}
             (-> (signal/main {:aspect :stop+zs1
                               :system :ks})
                 ks/lights))))

    (testing "stop+zs1 with zs1 shows hp0 and zs1"
      (is (= {:top-white nil
              :red :on
              :green :off
              :yellow nil
              :center-white :blinking
              :zs7 nil
              :bottom-white nil}
             (-> (signal/main {:aspect :stop+zs1
                               :zs1? true
                               :system :ks})
                 ks/lights))))

    (testing "stop+zs1 with sh1 + zs1 shows hp0 and zs1"
      (is (= {:top-white nil
              :red :on
              :green :off
              :yellow nil
              :center-white :blinking
              :zs7 nil
              :bottom-white :off}
             (-> (signal/main {:aspect :stop+zs1
                               :sh1? true
                               :zs1? true
                               :system :ks})
                 ks/lights))))

    (testing "proceed with zs1 shows ks1"
      (is (= {:top-white nil
              :red :off
              :green :on
              :yellow nil
              :center-white :off
              :zs7 nil
              :bottom-white nil}
             (-> (signal/main {:aspect :proceed
                               :zs1? true
                               :system :ks})
                 ks/lights)))))

  (testing "zs3"
    (testing "stop with zs3 shows hp0"
      (is (= {:top-white nil
              :red :on
              :green :off
              :yellow nil
              :center-white nil
              :zs7 nil
              :bottom-white nil}
             (-> (signal/main {:aspect :stop
                               :zs3 :sign
                               :speed-limit 10
                               :system :ks})
                 ks/lights))))

    (testing "proceed with zs3 and speed limit shows ks1"
      (is (= {:top-white nil
              :red :off
              :green :on
              :yellow nil
              :center-white nil
              :zs7 nil
              :bottom-white nil}
             (-> (signal/main {:aspect :proceed
                               :speed-limit 10
                               :zs3 :display
                               :system :ks})
                 ks/lights)))))

  (testing "zs7"
    (testing "stop+zs7 without zs7 shows hp0"
      (is (= {:top-white nil
              :red :on
              :green :off
              :yellow nil
              :center-white nil
              :zs7 nil
              :bottom-white nil}
             (-> (signal/main {:aspect :stop+zs7
                               :system :ks})
                 ks/lights))))

    (testing "stop+zs7 with zs7 shows hp0 and zs7"
      (is (= {:top-white nil
              :red :on
              :green :off
              :yellow nil
              :center-white nil
              :zs7 :on
              :bottom-white nil}
             (-> (signal/main {:aspect :stop+zs7
                               :zs7? true
                               :system :ks})
                 ks/lights))))

    (testing "proceed with zs7 shows ks1"
      (is (= {:top-white nil
              :red :off
              :green :on
              :yellow nil
              :center-white nil
              :zs7 :off
              :bottom-white nil}
             (-> (signal/main {:aspect :proceed
                               :zs7? true
                               :system :ks})
                 ks/lights))))))

(deftest distant-test
  (testing "stop expected shows ks2"
    (is (= {:top-white nil
            :red nil
            :green :off
            :yellow :on
            :center-white nil
            :zs7 nil
            :bottom-white nil}
           (-> (signal/distant {:aspect :stop
                                :system :ks})
               ks/lights))))

  (testing "proceed expected shows ks1"
    (is (= {:top-white nil
            :red nil
            :green :on
            :yellow :off
            :center-white nil
            :zs7 nil
            :bottom-white nil}
           (-> (signal/distant {:aspect :proceed
                                :system :ks})
               ks/lights))))

  (testing "shortened break path"
    (testing "stop expected with shortened break path shows ks2 plus top white"
      (is (= {:top-white :on
              :red nil
              :green :off
              :yellow :on
              :center-white nil
              :zs7 nil
              :bottom-white nil}
             (-> (signal/distant {:aspect :stop
                                  :distant-addition :shortened-break-path
                                  :system :ks})
                 ks/lights))))

    (testing "proceed expected with shortened break path shows ks1"
      (is (= {:top-white :off
              :red nil
              :green :on
              :yellow :off
              :center-white nil
              :zs7 nil
              :bottom-white nil}
             (-> (signal/distant {:aspect :proceed
                                  :distant-addition :shortened-break-path
                                  :system :ks})
                 ks/lights))))

    (testing "proceed expected with shortened break path & speed limit, but no zs3v shows ks1"
      (is (= {:top-white :off
              :red nil
              :green :on
              :yellow :off
              :center-white nil
              :zs7 nil
              :bottom-white nil}
             (-> (signal/distant {:aspect :proceed
                                  :speed-limit 10
                                  :distant-addition :shortened-break-path
                                  :system :ks})
                 ks/lights))))

    (testing "proceed expected with shortened break path & zs3v, but no speed limit shows ks1"
      (is (= {:top-white :off
              :red nil
              :green :on
              :yellow :off
              :center-white nil
              :zs7 nil
              :bottom-white nil}
             (-> (signal/distant {:aspect :proceed
                                  :zs3v :display
                                  :distant-addition :shortened-break-path
                                  :system :ks})
                 ks/lights))))

    (testing "proceed expected with shortened break path + zs3v and speed limit shows ks1 (blinking) and top white"
      (is (= {:top-white :on
              :red nil
              :green :blinking
              :yellow :off
              :center-white nil
              :zs7 nil
              :bottom-white nil}
             (-> (signal/distant {:aspect :proceed
                                  :speed-limit 10
                                  :zs3v :display
                                  :distant-addition :shortened-break-path
                                  :system :ks})
                 ks/lights)))))

  (testing "repeater"
    (testing "stop expected with repeater shows ks2 plus bottom white"
      (is (= {:top-white nil
              :red nil
              :green :off
              :yellow :on
              :center-white nil
              :zs7 nil
              :bottom-white :on}
             (-> (signal/distant {:aspect :stop
                                  :distant-addition :repeater
                                  :system :ks})
                 ks/lights))))

    (testing "proceed expected with repeater shows ks1"
      (is (= {:top-white nil
              :red nil
              :green :on
              :yellow :off
              :center-white nil
              :zs7 nil
              :bottom-white :off}
             (-> (signal/distant {:aspect :proceed
                                  :distant-addition :repeater
                                  :system :ks})
                 ks/lights))))

    (testing "proceed expected with repeater & speed limit, but no zs3v shows ks1"
      (is (= {:top-white nil
              :red nil
              :green :on
              :yellow :off
              :center-white nil
              :zs7 nil
              :bottom-white :off}
             (-> (signal/distant {:aspect :proceed
                                  :speed-limit 10
                                  :distant-addition :repeater
                                  :system :ks})
                 ks/lights))))

    (testing "proceed expected with repeater & zs3v, but no speed limit shows ks1"
      (is (= {:top-white nil
              :red nil
              :green :on
              :yellow :off
              :center-white nil
              :zs7 nil
              :bottom-white :off}
             (-> (signal/distant {:aspect :proceed
                                  :zs3v :display
                                  :distant-addition :repeater
                                  :system :ks})
                 ks/lights))))

    (testing "proceed expected with repeater + zs3v and speed limit shows ks1 (blinking) and bottom white"
      (is (= {:top-white nil
              :red nil
              :green :blinking
              :yellow :off
              :center-white nil
              :zs7 nil
              :bottom-white :on}
             (-> (signal/distant {:aspect :proceed
                                  :speed-limit 10
                                  :zs3v :display
                                  :distant-addition :repeater
                                  :system :ks})
                 ks/lights)))))

  (testing "sh1"
    (testing "stop+sh1 expected shows ks2"
      (is (= {:top-white nil
              :red nil
              :green :off
              :yellow :on
              :center-white nil
              :zs7 nil
              :bottom-white nil}
             (-> (signal/distant {:aspect :stop+sh1
                                  :system :ks})
                 ks/lights)))))

  (testing "zs1"
    (testing "stop+zs1 expected shows ks2"
      (is (= {:top-white nil
              :red nil
              :green :off
              :yellow :on
              :center-white nil
              :zs7 nil
              :bottom-white nil}
             (-> (signal/distant {:aspect :stop+zs1
                                  :system :ks})
                 ks/lights)))))

  (testing "zs3v"
    (testing "stop expected + zs3v shows ks2"
      (is (= {:top-white nil
              :red nil
              :green :off
              :yellow :on
              :center-white nil
              :zs7 nil
              :bottom-white nil}
             (-> (signal/distant {:aspect :stop
                                  :speed-limit 10
                                  :zs3v :display
                                  :system :ks})
                 ks/lights))))

    (testing "proceed expected with speed limit, but no zs3v shows ks1"
      (is (= {:top-white nil
              :red nil
              :green :on
              :yellow :off
              :center-white nil
              :zs7 nil
              :bottom-white nil}
             (-> (signal/distant {:aspect :proceed
                                  :speed-limit 10
                                  :system :ks})
                 ks/lights))))

    (testing "proceed expected + zs3v, but no speed limit shows ks1"
      (is (= {:top-white nil
              :red nil
              :green :on
              :yellow :off
              :center-white nil
              :zs7 nil
              :bottom-white nil}
             (-> (signal/distant {:aspect :proceed
                                  :zs3v :display
                                  :system :ks})
                 ks/lights))))

    (testing "proceed expected + zs3v and speed limit shows ks1 (blinking)"
      (is (= {:top-white nil
              :red nil
              :green :blinking
              :yellow :off
              :center-white nil
              :zs7 nil
              :bottom-white nil}
             (-> (signal/distant {:aspect :proceed
                                  :speed-limit 10
                                  :zs3v :display
                                  :system :ks})
                 ks/lights)))))

  (testing "zs7"
    (testing "stop+zs7 expected shows ks2"
      (is (= {:top-white nil
              :red nil
              :green :off
              :yellow :on
              :center-white nil
              :zs7 nil
              :bottom-white nil}
             (-> (signal/distant {:aspect :stop+zs7
                                  :system :ks})
                 ks/lights))))))

(deftest combination-test
  (testing "stop shows hp0"
    (is (= {:top-white nil
            :red :on
            :green :off
            :yellow :off
            :center-white nil
            :zs7 nil
            :bottom-white nil}
           (-> (signal/combination {:distant {:aspect :stop}
                                    :main {:aspect :stop}
                                    :system :ks})
               ks/lights)))

    (is (= {:top-white nil
            :red :on
            :green :off
            :yellow :off
            :center-white nil
            :zs7 nil
            :bottom-white nil}
           (-> (signal/combination {:distant {:aspect :proceed}
                                    :main {:aspect :stop}
                                    :system :ks})
               ks/lights))))

  (testing "shortened break path"
    (testing "stop with shortened break path shows hp0"
      (is (= {:top-white :off
              :red :on
              :green :off
              :yellow :off
              :center-white nil
              :zs7 nil
              :bottom-white nil}
             (-> (signal/combination {:distant {:aspect :stop
                                                :distant-addition :shortened-break-path}
                                      :main {:aspect :stop}
                                      :system :ks})
                 ks/lights))))

    (testing "proceed + stop expected with shortened break path shows ks2 with top white"
      (is (= {:top-white :on
              :red :off
              :green :off
              :yellow :on
              :center-white nil
              :zs7 nil
              :bottom-white nil}
             (-> (signal/combination {:distant {:aspect :stop
                                                :distant-addition :shortened-break-path}
                                      :main {:aspect :proceed}
                                      :system :ks})
                 ks/lights))))

    (testing "proceed + proceed expected with shortened break path shows ks1"
      (is (= {:top-white :off
              :red :off
              :green :on
              :yellow :off
              :center-white nil
              :zs7 nil
              :bottom-white nil}
             (-> (signal/combination {:distant {:aspect :proceed
                                                :distant-addition :shortened-break-path}
                                      :main {:aspect :proceed}
                                      :system :ks})
                 ks/lights))))

    (testing "proceed + proceed expected + shortened break path + zs3v but no speed-limit shows ks1"
      (is (= {:top-white :off
              :red :off
              :green :on
              :yellow :off
              :center-white nil
              :zs7 nil
              :bottom-white nil}
             (-> (signal/combination {:distant {:aspect :proceed
                                                :zs3v :display
                                                :distant-addition :shortened-break-path}
                                      :main {:aspect :proceed}
                                      :system :ks})
                 ks/lights))))

    (testing "proceed + proceed expected + speed-limit but no zs3v shows ks1"
      (is (= {:top-white :off
              :red :off
              :green :on
              :yellow :off
              :center-white nil
              :zs7 nil
              :bottom-white nil}
             (-> (signal/combination {:distant {:aspect :proceed
                                                :speed-limit 10
                                                :distant-addition :shortened-break-path}
                                      :main {:aspect :proceed}
                                      :system :ks})
                 ks/lights))))

    (testing "proceed + proceed expected + zs3 & speed-limit shows ks1 (blinking) & top white"
      (is (= {:top-white :on
              :red :off
              :green :blinking
              :yellow :off
              :center-white nil
              :zs7 nil
              :bottom-white nil}
             (-> (signal/combination {:distant {:aspect :proceed
                                                :zs3v :sign
                                                :speed-limit 10
                                                :distant-addition :shortened-break-path}
                                      :main {:aspect :proceed}
                                      :system :ks})
                 ks/lights)))))

  (testing "proceed + stop expected shows ks2"
    (is (= {:top-white nil
            :red :off
            :green :off
            :yellow :on
            :center-white nil
            :zs7 nil
            :bottom-white nil}
           (-> (signal/combination {:distant {:aspect :stop}
                                    :main {:aspect :proceed}
                                    :system :ks})
               ks/lights))))

  (testing "proceed + proceed expected shows ks1"
    (is (= {:top-white nil
            :red :off
            :green :on
            :yellow :off
            :center-white nil
            :zs7 nil
            :bottom-white nil}
           (-> (signal/combination {:distant {:aspect :proceed}
                                    :main {:aspect :proceed}
                                    :system :ks})
               ks/lights))))

  (testing "sh1"
    (testing "stop with sh1 shows hp0"
      (is (= {:top-white nil
              :red :on
              :green :off
              :yellow :off
              :center-white :off
              :zs7 nil
              :bottom-white :off}
             (-> (signal/combination {:distant {:aspect :stop}
                                      :main {:aspect :stop
                                             :sh1? true}
                                      :system :ks})
                 ks/lights))))

    (testing "stop+sh1 without sh1 shows hp0"
      (is (= {:top-white nil
              :red :on
              :green :off
              :yellow :off
              :center-white nil
              :zs7 nil
              :bottom-white nil}
             (-> (signal/combination {:distant {:aspect :stop}
                                      :main {:aspect :stop+sh1}
                                      :system :ks})
                 ks/lights))))

    (testing "stop+sh1 with sh1 shows hp0 and sh1"
      (is (= {:top-white nil
              :red :on
              :green :off
              :yellow :off
              :center-white :on
              :zs7 nil
              :bottom-white :on}
             (-> (signal/combination {:distant {:aspect :stop}
                                      :main {:aspect :stop+sh1
                                             :sh1? true}
                                      :system :ks})
                 ks/lights))))

    (testing "proceed + stop+sh1 expected shows ks2"
      (is (= {:top-white nil
              :red :off
              :green :off
              :yellow :on
              :center-white nil
              :zs7 nil
              :bottom-white nil}
             (-> (signal/combination {:distant {:aspect :stop+sh1}
                                      :main {:aspect :proceed}
                                      :system :ks})
                 ks/lights)))))

  (testing "zs1"
    (testing "stop with zs1 shows hp0"
      (is (= {:top-white nil
              :red :on
              :green :off
              :yellow :off
              :center-white nil
              :zs7 nil
              :bottom-white :off}
             (-> (signal/combination {:distant {:aspect :stop}
                                      :main {:aspect :stop
                                             :zs1? true}
                                      :system :ks})
                 ks/lights))))

    (testing "stop+zs1 without zs1 shows hp0"
      (is (= {:top-white nil
              :red :on
              :green :off
              :yellow :off
              :center-white nil
              :zs7 nil
              :bottom-white nil}
             (-> (signal/combination {:distant {:aspect :stop}
                                      :main {:aspect :stop+zs1}
                                      :system :ks})
                 ks/lights))))

    (testing "stop+zs1 with zs1 shows hp0 and zs1"
      (is (= {:top-white nil
              :red :on
              :green :off
              :yellow :off
              :center-white nil
              :zs7 nil
              :bottom-white :blinking}
             (-> (signal/combination {:distant {:aspect :stop}
                                      :main {:aspect :stop+zs1
                                             :zs1? true}
                                      :system :ks})
                 ks/lights))))

    (testing "stop+zs1 with sh1 & zs1 shows hp0 and zs1"
      (is (= {:top-white nil
              :red :on
              :green :off
              :yellow :off
              :center-white :off
              :zs7 nil
              :bottom-white :blinking}
             (-> (signal/combination {:distant {:aspect :stop}
                                      :main {:aspect :stop+zs1
                                             :sh1? true
                                             :zs1? true}
                                      :system :ks})
                 ks/lights))))

    (testing "proceed + stop+zs1 expected shows ks2"
      (is (= {:top-white nil
              :red :off
              :green :off
              :yellow :on
              :center-white nil
              :zs7 nil
              :bottom-white nil}
             (-> (signal/combination {:distant {:aspect :stop+zs1}
                                      :main {:aspect :proceed}
                                      :system :ks})
                 ks/lights)))))

  (testing "zs3"
    (testing "stop + zs3 and speed limit shows hp0"
      (is (= {:top-white nil
              :red :on
              :green :off
              :yellow :off
              :center-white nil
              :zs7 nil
              :bottom-white nil}
             (-> (signal/combination {:distant {:aspect :stop}
                                      :main {:aspect :stop
                                             :speed-limit 10
                                             :zs3 :sign}
                                      :system :ks})
                 ks/lights))))

    (testing "proceed + zs3 & speed-limit + stop expected shows ks2"
      (is (= {:top-white nil
              :red :off
              :green :off
              :yellow :on
              :center-white nil
              :zs7 nil
              :bottom-white nil}
             (-> (signal/combination {:distant {:aspect :stop
                                                :zs3v :sign
                                                :speed-limit 10}
                                      :main {:aspect :proceed}
                                      :system :ks})
                 ks/lights))))

    (testing "proceed + zs3 & speed-limit + proceed expected shows ks1"
      (is (= {:top-white nil
              :red :off
              :green :on
              :yellow :off
              :center-white nil
              :zs7 nil
              :bottom-white nil}
             (-> (signal/combination {:distant {:aspect :proceed}
                                      :main {:aspect :proceed
                                             :zs3 :display
                                             :speed-limit 10}
                                      :system :ks})
                 ks/lights)))))

  (testing "zs3v"
    (testing "stop + zs3v and speed limit shows hp0"
      (is (= {:top-white nil
              :red :on
              :green :off
              :yellow :off
              :center-white nil
              :zs7 nil
              :bottom-white nil}
             (-> (signal/combination {:distant {:aspect :stop}
                                      :speed-limit 10
                                      :zs3v :sign
                                      :main {:aspect :stop}
                                      :system :ks})
                 ks/lights))))

    (testing "proceed + stop expected + zs3v & speed-limit shows ks2"
      (is (= {:top-white nil
              :red :off
              :green :off
              :yellow :on
              :center-white nil
              :zs7 nil
              :bottom-white nil}
             (-> (signal/combination {:distant {:aspect :stop}
                                      :main {:aspect :proceed
                                             :zs3 :sign
                                             :speed-limit 10}
                                      :system :ks})
                 ks/lights))))

    (testing "proceed + proceed expected + zs3v but no speed-limit shows ks1"
      (is (= {:top-white nil
              :red :off
              :green :on
              :yellow :off
              :center-white nil
              :zs7 nil
              :bottom-white nil}
             (-> (signal/combination {:distant {:aspect :proceed
                                                :zs3v :display}
                                      :main {:aspect :proceed}
                                      :system :ks})
                 ks/lights))))

    (testing "proceed + proceed expected + speed-limit but no zs3v shows ks1"
      (is (= {:top-white nil
              :red :off
              :green :on
              :yellow :off
              :center-white nil
              :zs7 nil
              :bottom-white nil}
             (-> (signal/combination {:distant {:aspect :proceed
                                                :speed-limit 10}
                                      :main {:aspect :proceed}
                                      :system :ks})
                 ks/lights))))

    (testing "proceed + proceed expected + zs3 & speed-limit shows ks1 (blinking)"
      (is (= {:top-white nil
              :red :off
              :green :blinking
              :yellow :off
              :center-white nil
              :zs7 nil
              :bottom-white nil}
             (-> (signal/combination {:distant {:aspect :proceed
                                                :zs3v :sign
                                                :speed-limit 10}
                                      :main {:aspect :proceed}
                                      :system :ks})
                 ks/lights)))))

  (testing "zs7"
    (testing "stop with zs7 shows hp0"
      (is (= {:top-white nil
              :red :on
              :green :off
              :yellow :off
              :center-white nil
              :zs7 :off
              :bottom-white nil}
             (-> (signal/combination {:distant {:aspect :stop}
                                      :main {:aspect :stop
                                             :zs7? true}
                                      :system :ks})
                 ks/lights))))

    (testing "stop+zs7 without zs7 shows hp0"
      (is (= {:top-white nil
              :red :on
              :green :off
              :yellow :off
              :center-white nil
              :zs7 nil
              :bottom-white nil}
             (-> (signal/combination {:distant {:aspect :stop}
                                      :main {:aspect :stop+zs7}
                                      :system :ks})
                 ks/lights))))

    (testing "stop+zs7 with zs7 shows hp0 and zs7"
      (is (= {:top-white nil
              :red :on
              :green :off
              :yellow :off
              :center-white nil
              :zs7 :on
              :bottom-white nil}
             (-> (signal/combination {:distant {:aspect :stop}
                                      :main {:aspect :stop+zs7
                                             :zs7? true}
                                      :system :ks})
                 ks/lights))))

    (testing "proceed + stop+zs7 expected shows ks2"
      (is (= {:top-white nil
              :red :off
              :green :off
              :yellow :on
              :center-white nil
              :zs7 nil
              :bottom-white nil}
             (-> (signal/combination {:distant {:aspect :stop+zs7}
                                      :main {:aspect :proceed}
                                      :system :ks})
                 ks/lights))))))

(deftest speed-limit-available?
  (testing "without Zs3"
    (is (ks/speed-limit-available?
         (signal/main {:main {}
                       :system :ks})
         nil))

    (is (ks/speed-limit-available?
         (signal/combination {:main {}
                              :system :ks})
         nil))

    (is (not (ks/speed-limit-available?
              (signal/main {:main {}
                            :system :ks})
              100)))

    (is (not (ks/speed-limit-available?
              (signal/combination {:main {}
                                   :system :ks})
              100))))

  (testing "with Zs3"
    (is (ks/speed-limit-available?
         (signal/main {:main {:zs3 :display}
                       :system :ks})
         nil))

    (is (ks/speed-limit-available?
         (signal/combination {:main {:zs3 :sign}
                              :system :ks})
         nil))

    (is (ks/speed-limit-available?
         (signal/main {:zs3 :sign
                       :system :ks})
         100))

    (is (ks/speed-limit-available?
         (signal/combination {:main {:zs3 :display}
                              :system :ks})
         100))))
