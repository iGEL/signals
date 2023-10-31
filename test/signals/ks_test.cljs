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
           (-> (signal/main {:aspect :stop
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
           (-> (signal/main {:aspect :proceed
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
           (-> (signal/distant {:aspect :stop
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
           (-> (signal/distant {:aspect :proceed
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

  (testing "proceed + stop expected"
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

  (testing "proceed + proceed expected"
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
               ks/lights)))))
