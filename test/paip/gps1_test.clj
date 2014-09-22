;;;; File test/paip/gps1_test.clj

(ns paip.gps1-ctu-repl
  (:require [clojure.test :refer :all]
            [paip.core :refer :all]
            [paip.gps1 :refer :all] ))



; (op-accessors-tests)
(deftest op-accessors-tests
  (let [OP (first +school-ops+)]
    (testing "Operation accessors"
      (is (= :drive-son-to-school       (op-name OP)))
      (is (= #{:son-at-home :car-works} (pre* OP)))
      (is (= #{:son-at-school}          (add* OP)))
      (is (= #{:son-at-home}            (del* OP)))
      )))


; (run-tests)




