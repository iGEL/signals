(ns signals.ks-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [signals.ks :as ks]
   [signals.signal :as signal]))

(deftest main-test
  (testing "stop"
    (is (= {:top-white nil
            :red :on
            :green :off
            :yellow nil
            :center-white nil
            :zs7 nil
            :bottom-white nil}
           (-> (signal/main {:speed-limit 0
                             :system :ks})
               ks/lights))))

  (testing "proceed"
    (is (= {:top-white nil
            :red :off
            :green :on
            :yellow nil
            :center-white nil
            :zs7 nil
            :bottom-white nil}
           (-> (signal/main {:speed-limit nil
                             :system :ks})
               ks/lights)))))

(deftest distant-test
  (testing "stop expected"
    (is (= {:top-white nil
            :red nil
            :green :off
            :yellow :on
            :center-white nil
            :zs7 nil
            :bottom-white nil}
           (-> (signal/distant {:speed-limit 0
                                :system :ks})
               ks/lights))))

  (testing "proceed expected"
    (is (= {:top-white nil
            :red nil
            :green :on
            :yellow :off
            :center-white nil
            :zs7 nil
            :bottom-white nil}
           (-> (signal/distant {:speed-limit nil
                                :system :ks})
               ks/lights)))))

(deftest combination-test
  (testing "stop"
    (is (= {:top-white nil
            :red :on
            :green :off
            :yellow :off
            :center-white nil
            :zs7 nil
            :bottom-white nil}
           (-> (signal/combination {:distant {:speed-limit 0}
                                    :main {:speed-limit 0}
                                    :system :ks})
               ks/lights)))

    (is (= {:top-white nil
            :red :on
            :green :off
            :yellow :off
            :center-white nil
            :zs7 nil
            :bottom-white nil}
           (-> (signal/combination {:distant {:speed-limit nil}
                                    :main {:speed-limit 0}
                                    :system :ks})
               ks/lights))))

  (testing "proceed + stop expected"
    (is (= {:top-white nil
            :red :off
            :green :off
            :yellow :on
            :center-white nil
            :zs7 nil
            :bottom-white nil}
           (-> (signal/combination {:distant {:speed-limit 0}
                                    :main {:speed-limit nil}
                                    :system :ks})
               ks/lights))))

  (testing "proceed + proceed expected"
    (is (= {:top-white nil
            :red :off
            :green :on
            :yellow :off
            :center-white nil
            :zs7 nil
            :bottom-white nil}
           (-> (signal/combination {:distant {:speed-limit nil}
                                    :main {:speed-limit nil}
                                    :system :ks})
               ks/lights)))))
