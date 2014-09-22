(ns paip.core-test
  (:require [clojure.test :refer :all]
            [paip.core :refer :all]))


; (debug-id*-test)
(deftest debug-id*-test
  (testing "init debug ids"
    (undebug)
    (is (empty? +dbg-id*+)) )
  (testing "add debug ids"
    (debug :dbg)
    (is (= #{:dbg} +dbg-id*+))
    (debug :dbg2 :dbg3)
    (is (= #{:dbg :dbg2 :dbg3} +dbg-id*+)) )
  (testing "remove debug ids"
    (undebug :dbg2)
    (is (= #{:dbg :dbg3} +dbg-id*+))
    (undebug)
    (is (empty? +dbg-id*+)) ))


(comment
  (run-tests)
  )
