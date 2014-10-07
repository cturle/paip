(ns ctu.seq-set-test
  (:require [clojure.test :refer :all]
            [ctu.seq-set :refer :all]
            ))


; (member?-tests)
(deftest member?-tests
  (testing "CASE : returns X"
    (is (= :x1 (member? [:x1 :x2] :x1)))
    (is (= :x2 (member? [:x1 :x2] :x2)))
    (is (= :x1 (member? '(:x1 :x2) :x1)))
    (is (= :x1 (member? #{:x1 :x2} :x1)))
   )
  (testing "CASE : returns nil"
    (is (nil? (member? [:x1 :x2] :x3)))
    (is (nil? (member? '(:x1 :x2) :x3)))
    (is (nil? (member? #{:x1 :x2} :x3)))
    (is (nil? (member? [] :x1)))
    (is (nil? (member? '() :x1)))
    (is (nil? (member? #{} :x1)))
    (is (nil? (member? nil :x1)))
    ))

; (union-tests)
(deftest union-tests
  (is (= [] (union [] [])))
  (is (= [:x1] (union [:x1] [])))
  (is (= [:x1 :x2] (union [:x1] [:x2])))
  (is (= [:x1] (union [:x1] [:x1])))
  (is (= [:x1 :x2] (union [:x1 :x2] [:x1])))
  (is (= [:x1 :x2 :x3] (union [:x1 :x2] [:x1 :x3])))
  )

; (intersection-tests)
(deftest intersection-tests
  (is (= [] (intersection [] [])))
  (is (= [] (intersection [:x1] [])))
  (is (= [] (intersection [:x1] [:x2])))
  (is (= [:x1] (intersection [:x1] [:x1])))
  (is (= [:x1] (intersection [:x1 :x2] [:x1])))
  (is (= [:x1] (intersection [:x1 :x2] [:x1 :x3])))
  (is (= [:x1 :x3] (intersection [:x1 :x2 :x3] [:x1 :x3])))
  )

; (difference-tests)
(deftest difference-tests
  (is (= [] (difference [] [])))
  (is (= [:x1] (difference [:x1] [])))
  (is (= [:x1] (difference [:x1] [:x2])))
  (is (= [] (difference [:x1] [:x1])))
  (is (= [:x2] (difference [:x1 :x2] [:x1])))
  (is (= [:x2] (difference [:x1 :x2] [:x1 :x3])))
  (is (= [:x2] (difference [:x1 :x2 :x3] [:x1 :x3])))
  )

; (subset?-tests)
(deftest subset?-tests
  (testing "CASE : true"
    (is (subset? [] []))
    (is (subset? [:x1] [:x1]))
    (is (subset? [:x1 :x2] [:x1 :x2 :x3]))
    (is (subset? [:x1 :x2] [:x2 :x3 :x1]))
  )
  (testing "CASE : false"
    (is (not (subset? [:x1] [])))
    (is (not (subset? [:x1] [:x2])))
    (is (not (subset? [:x1 :x2] [:x1])))
    (is (not (subset? [:x1 :x2] [:x1 :x3])))
  ))

; (superset?-tests)
(deftest superset?-tests
  (testing "CASE : true"
    (is (superset? [] []))
    (is (superset? [:x1] [:x1]))
    (is (superset? [:x1 :x2 :x3] [:x1 :x2]))
    (is (superset? [:x1 :x2 :x3] [:x2 :x3]))
  )
  (testing "CASE : false"
    (is (not (superset? [] [:x1])))
    (is (not (superset? [:x1] [:x2])))
    (is (not (superset? [:x1] [:x1 :x2])))
    (is (not (superset? [:x1 :x2] [:x1 :x3])))
  ))


; (run-tests)


















