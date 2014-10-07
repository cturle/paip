(ns ctu.seq-set-test
  (:require [clojure.test :refer :all]
            [ctu.seq-set :as ss]
            ))


; (contains?-tests)
(deftest contains?-tests
  (testing "CASE : returns X"
    (is (= :x1 (ss/contains? [:x1 :x2] :x1)))
    (is (= :x2 (ss/contains? [:x1 :x2] :x2)))
    (is (= :x1 (ss/contains? '(:x1 :x2) :x1)))
    (is (= :x1 (ss/contains? #{:x1 :x2} :x1)))
   )
  (testing "CASE : returns nil"
    (is (nil? (ss/contains? [:x1 :x2] :x3)))
    (is (nil? (ss/contains? '(:x1 :x2) :x3)))
    (is (nil? (ss/contains? #{:x1 :x2} :x3)))
    (is (nil? (ss/contains? [] :x1)))
    (is (nil? (ss/contains? '() :x1)))
    (is (nil? (ss/contains? #{} :x1)))
    (is (nil? (ss/contains? nil :x1)))
    ))

; (conj-tests)
(deftest conj-tests
  (is (= [:x1] (ss/conj  [] :x1)))
  (is (= [:x1] (ss/conj '() :x1)))
  (is (= [:x1] (ss/conj nil :x1)))
  (is (= [:x1] (ss/conj  [:x1] :x1)))
  (is (= [:x1] (ss/conj '(:x1) :x1)))
  (is (= [:x1 :x2] (ss/conj  [:x1] :x2)))
  (is (= [:x2 :x1] (ss/conj '(:x1) :x2))) )

; (disj-tests)
(deftest disj-tests
  (is (= [] (ss/disj  [] :x1)))
  (is (= [] (ss/disj '() :x1)))
  (is (= [] (ss/disj nil :x1)))
  (is (= [] (ss/disj  [:x1] :x1)))
  (is (= [] (ss/disj '(:x1) :x1)))
  (is (= [:x1 :x3] (ss/disj  [:x1 :x2 :x3] :x2)))
  (is (= [:x1 :x3] (ss/disj '(:x1 :x2 :x3) :x2))) )

; (union-tests)
(deftest union-tests
  (is (= [] (ss/union [] [])))
  (is (= [:x1] (ss/union [:x1] [])))
  (is (= [:x1 :x2] (ss/union [:x1] [:x2])))
  (is (= [:x1] (ss/union [:x1] [:x1])))
  (is (= [:x1 :x2] (ss/union [:x1 :x2] [:x1])))
  (is (= [:x1 :x2 :x3] (ss/union [:x1 :x2] [:x1 :x3])))
  )

; (intersection-tests)
(deftest intersection-tests
  (is (= [] (ss/intersection [] [])))
  (is (= [] (ss/intersection [:x1] [])))
  (is (= [] (ss/intersection [:x1] [:x2])))
  (is (= [:x1] (ss/intersection [:x1] [:x1])))
  (is (= [:x1] (ss/intersection [:x1 :x2] [:x1])))
  (is (= [:x1] (ss/intersection [:x1 :x2] [:x1 :x3])))
  (is (= [:x1 :x3] (ss/intersection [:x1 :x2 :x3] [:x1 :x3])))
  )

; (difference-tests)
(deftest difference-tests
  (is (= [] (ss/difference [] [])))
  (is (= [:x1] (ss/difference [:x1] [])))
  (is (= [:x1] (ss/difference [:x1] [:x2])))
  (is (= [] (ss/difference [:x1] [:x1])))
  (is (= [:x2] (ss/difference [:x1 :x2] [:x1])))
  (is (= [:x2] (ss/difference [:x1 :x2] [:x1 :x3])))
  (is (= [:x2] (ss/difference [:x1 :x2 :x3] [:x1 :x3])))
  )

; (subset?-tests)
(deftest subset?-tests
  (testing "CASE : true"
    (is (ss/subset? [] []))
    (is (ss/subset? [:x1] [:x1]))
    (is (ss/subset? [:x1 :x2] [:x1 :x2 :x3]))
    (is (ss/subset? [:x1 :x2] [:x2 :x3 :x1]))
  )
  (testing "CASE : false"
    (is (not (ss/subset? [:x1] [])))
    (is (not (ss/subset? [:x1] [:x2])))
    (is (not (ss/subset? [:x1 :x2] [:x1])))
    (is (not (ss/subset? [:x1 :x2] [:x1 :x3])))
  ))

; (superset?-tests)
(deftest superset?-tests
  (testing "CASE : true"
    (is (ss/superset? [] []))
    (is (ss/superset? [:x1] [:x1]))
    (is (ss/superset? [:x1 :x2 :x3] [:x1 :x2]))
    (is (ss/superset? [:x1 :x2 :x3] [:x2 :x3]))
  )
  (testing "CASE : false"
    (is (not (ss/superset? [] [:x1])))
    (is (not (ss/superset? [:x1] [:x2])))
    (is (not (ss/superset? [:x1] [:x1 :x2])))
    (is (not (ss/superset? [:x1 :x2] [:x1 :x3])))
  ))


; (run-tests)


















