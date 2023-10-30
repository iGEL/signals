(ns signals.ks-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [signals.ks :as ks]))

(deftest main-test
  (testing "stop"
    (is (= {:top-white nil
            :red :on
            :green :off
            :yellow nil
            :center-white nil
            :zs7 nil
            :bottom-white nil}
           (-> (ks/main {:speed-limit 0})
               ks/lights))))

  (testing "proceed"
    (is (= {:top-white nil
            :red :off
            :green :on
            :yellow nil
            :center-white nil
            :zs7 nil
            :bottom-white nil}
           (-> (ks/main {:speed-limit nil})
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
           (-> (ks/distant {:speed-limit 0})
               ks/lights))))

  (testing "proceed expected"
    (is (= {:top-white nil
            :red nil
            :green :on
            :yellow :off
            :center-white nil
            :zs7 nil
            :bottom-white nil}
           (-> (ks/distant {:speed-limit nil})
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
           (-> (ks/combination {:distant {:speed-limit 0}
                                :main {:speed-limit 0}})
               ks/lights)))

    (is (= {:top-white nil
            :red :on
            :green :off
            :yellow :off
            :center-white nil
            :zs7 nil
            :bottom-white nil}
           (-> (ks/combination {:distant {:speed-limit nil}
                                :main {:speed-limit 0}})
               ks/lights))))

  (testing "proceed + stop expected"
    (is (= {:top-white nil
            :red :off
            :green :off
            :yellow :on
            :center-white nil
            :zs7 nil
            :bottom-white nil}
           (-> (ks/combination {:distant {:speed-limit 0}
                                :main {:speed-limit nil}})
               ks/lights))))

  (testing "proceed + proceed expected"
    (is (= {:top-white nil
            :red :off
            :green :on
            :yellow :off
            :center-white nil
            :zs7 nil
            :bottom-white nil}
           (-> (ks/combination {:distant {:speed-limit nil}
                                :main {:speed-limit nil}})
               ks/lights)))))
