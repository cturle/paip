;;;; File test/paip/gps1_test.clj

(ns paip.gps1-test
  (:require [clojure.test :refer :all]
            [paip.core :refer :all]
            [paip.gps1 :refer :all]
            [paip.dom-school :as school]
            ))



; (op-accessors-tests)
(deftest op-accessors-tests
  (let [OP (first school/+available-ops+)]
    (testing "Operation accessors"
      (is (= :drive-son-to-school      (op-name OP)))
      (is (= [:son-at-home :car-works] (pre* OP)))
      (is (= [:son-at-school]          (add* OP)))
      (is (= [:son-at-home]            (del* OP)))
      )))

; (gps-tests)
(deftest gps-tests
  (testing "chap 4.4 Stage 4: Test"
    (is (= :solved
           (gps [:son-at-home :car-needs-battery :have-money :have-phone-book]
                [:son-at-school]
                school/+available-ops+ )))
    (is (nil? (gps [:son-at-home :car-needs-battery :have-money]
                   [:son-at-school]
                   school/+available-ops+ )))
    (is (= :solved
           (gps [:son-at-home :car-works]
                [:son-at-school]
                school/+available-ops+ )))
    ))



; (run-tests)




