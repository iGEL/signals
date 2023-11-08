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
                 ks/lights))))))
