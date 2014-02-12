(ns cryptick.core-test
  (:require [clojure.test :refer :all]
            [cryptick.core :refer :all]))

(deftest test-ticker-00 
  (testing "Calling ticker with invalid exchange key"
    (is (thrown-with-msg? Exception #"Invalid exchange specified: :nonexistant" @(ticker :nonexistant)))))

(deftest test-ticker-01 
  (testing "Calling ticker without currency pair, for exchange where pair is required"
    (is (thrown-with-msg? Exception #"Currency pair must be specified for :btce.*" @(ticker :btce)))))

(deftest test-ticker-havelock-00
  (testing "Havelock ticker without argument"
    (is (instance? java.util.Map @(ticker :havelock)))))

(deftest test-ticker-havelock-01
  (testing "Havelock ticker with argument"
    (is (instance? java.util.Map @(ticker :havelock "AM1")))))

; (deftest a-test
;   (testing "FIXME, I fail."
;     (is (= 0 1))))
