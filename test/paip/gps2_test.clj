;;;; File test/paip/gps1_test.clj

(ns paip.gps2-test
  (:require [clojure.test :refer :all]
            [ctu.core :refer :all]
            [paip.core :refer :all]
            [paip.gps2 :refer :all]
            [paip.dom-bananas], [paip.dom-maze], [paip.dom-blocks] ))

(undebug)

; (executing?-tests)
(deftest executing?-tests
  (testing "cas nominal"
    (is (executing? [:executing :op-name]))
    (is (not (executing? :op-name))) ))

; (convert-op-tests)
(deftest convert-op-tests
  (testing "cas nominal"
    (let [NOP (convert-op {:action :op-name, :add-list #{:cond1}})]
      (is (= {:action :op-name, :add-list #{:cond1, [:executing :op-name]}} NOP))
      (is (= NOP (convert-op NOP))) )))

; (op-tests)
(deftest op-tests
  (testing "cas nominal"
    (let [OP (op :op-name {:add-list #{:cond1}})]
      (is (= {:action :op-name, :add-list #{:cond1, [:executing :op-name]}} OP)) )))

; (gps-tests)
(deftest gps-tests
  (let [GPS (timeout-fn gps 500)]
    (testing "cases of the book"
      (is (= '([:start] [:executing :look-up-number] [:executing :telephone-shop] [:executing :tell-shop-problem]
               [:executing :give-shop-money] [:executing :shop-installs-battery] [:executing :drive-son-to-school])
             (GPS [:son-at-home :car-needs-battery :have-money :have-phone-book]
                  [:son-at-school]
                  +paip-school-ops+ ))))
    (testing "CASE : one direct operator"
      (is (= '([:start] [:executing :drive-son-to-school])
             (GPS [:son-at-home :car-works]
                  [:son-at-school]
                  +paip-school-ops+ ))))
    (testing "CASE : avoids leaping before it looks."
      (is (nil? (GPS [:son-at-home :car-needs-battery :have-money :have-phone-book]
                     [:have-money :son-at-school]
                     +paip-school-ops+ )))
      (is (nil? (GPS [:son-at-home :car-needs-battery :have-money :have-phone-book]
                     [:son-at-school :have-money]
                     +paip-school-ops+ ))))
    (testing "CASE : missing pre-condition or missing operator"
      (is (nil? (GPS [:son-at-home :car-needs-battery :have-money]
                     [:son-at-school]
                     +paip-school-ops+ ))))
    (testing "CASE : goal already in state"
      (is (= '([:start])
             (GPS [:son-at-home]
                  [:son-at-home]
                  +paip-school-ops+ ))))
    (testing "CASE : monkey and bananas"
      (is (= '([:start] [:executing :push-chair-from-door-to-middle-room] [:executing :climb-on-chair]
               [:executing :drop-ball] [:executing :grasp-bananas] [:executing :eat-bananas])
             (GPS [:at-door :on-floor :has-ball :hungry :chair-at-door]
                  [:not-hungry]
                  (set (map convert-op paip.dom-bananas/+available-ops+)) )))) ))

; (gps-chap4-13-tests)
(deftest gps-chap4-13-tests
  (let [GPS (timeout-fn gps-chap4-13 500)]
    (testing "CASE : maze"
      (is (= '([:start] [:executing [:move :from 1 :to 2]] [:executing [:move :from 2 :to 3]] [:executing [:move :from 3 :to 4]]
               [:executing [:move :from 4 :to 9]] [:executing [:move :from 9 :to 8]] [:executing [:move :from 8 :to 7]]
               [:executing [:move :from 7 :to 12]] [:executing [:move :from 12 :to 11]] [:executing [:move :from 11 :to 16]]
               [:executing [:move :from 16 :to 17]] [:executing [:move :from 17 :to 22]] [:executing [:move :from 22 :to 23]]
               [:executing [:move :from 23 :to 24]] [:executing [:move :from 24 :to 19]] [:executing [:move :from 19 :to 20]]
               [:executing [:move :from 20 :to 25]] )
             (GPS [[:at 1]]
                  #{[:at 25]}
                  (set (map convert-op paip.dom-maze/+available-ops+)) ))))
      ))

; (destination-tests)
(deftest destination-tests
  (testing "CASE : nominal"
    (is (= 2 (destination [:executing [:move :from 1 :to 2]]))) ))

; (find-path-tests)
(deftest find-path-tests
  (use-ops (set (map convert-op paip.dom-maze/+available-ops+)))
  (testing "CASE : nominal"
    (is (= [1 2 3 4 9 8 7 12 11 16 17 22 23 24 19 20 25]
           ((timeout-fn find-path 500) 1 25) ))
    (is (= [1]
           ((timeout-fn find-path 500) 1 1) ))
    (is (= ((timeout-fn find-path 500) 1 25)
           (reverse ((timeout-fn find-path 500) 25 1)) ))))



; (run-tests)

