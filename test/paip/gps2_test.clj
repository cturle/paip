;;;; File test/paip/gps1_test.clj

(ns paip.gps2-test
  (:require [clojure.test :refer :all]
            [ctu.core :refer :all]
            [paip.core :refer :all]
            [paip.gps1 :refer [op-name, add*, del*, pre*]]
            [paip.gps2 :refer :all]
            [paip.dom-school], [paip.dom-bananas], [paip.dom-maze], [paip.dom-blocks :refer [make-block-ops]] ))


; (executing?-tests)
(deftest executing?-tests
  (testing "cas nominal"
    (is (executing? [:executing :op-name]))
    (is (not (executing? :op-name))) ))

; (convert-op-tests)
(deftest convert-op-tests
  (testing "cas nominal"
    (let [NOP (convert-op {:action :op-name, :add-list [:cond1]})]
      (is (= {:action :op-name, :add-list [[:executing :op-name], :cond1]} NOP))
      (is (= NOP (convert-op NOP))) )))

; (op-tests)
(deftest op-tests
  (testing "cas nominal"
    (let [OP (op :op-name {:add-list [:cond1]})]
      (is (= {:action :op-name, :add-list [[:executing :op-name], :cond1]} OP)) )))

; (gps-tests)
(deftest gps-tests
  (let [GPS          (timeout-fn gps 500)
        SCHOOL-OP*   (map convert-op paip.dom-school/+available-ops+)
        BANANAS-OP*  (map convert-op paip.dom-bananas/+available-ops+) ]
    (testing "cases of the book"
      (is (= '([:start] [:executing :look-up-number] [:executing :telephone-shop] [:executing :tell-shop-problem]
               [:executing :give-shop-money] [:executing :shop-installs-battery] [:executing :drive-son-to-school])
             (GPS [:son-at-home :car-needs-battery :have-money :have-phone-book]
                  [:son-at-school]
                  SCHOOL-OP* ))))
    (testing "CASE : one direct operator"
      (is (= '([:start] [:executing :drive-son-to-school])
             (GPS [:son-at-home :car-works]
                  [:son-at-school]
                  SCHOOL-OP* ))))
    (testing "CASE : avoids leaping before it looks."
      (is (nil? (GPS [:son-at-home :car-needs-battery :have-money :have-phone-book]
                     [:have-money :son-at-school]
                     SCHOOL-OP* )))
      (is (nil? (GPS [:son-at-home :car-needs-battery :have-money :have-phone-book]
                     [:son-at-school :have-money]
                     SCHOOL-OP* ))))
    (testing "CASE : missing pre-condition or missing operator"
      (is (nil? (GPS [:son-at-home :car-needs-battery :have-money]
                     [:son-at-school]
                     SCHOOL-OP* ))))
    (testing "CASE : goal already in state"
      (is (= '([:start])
             (GPS [:son-at-home]
                  [:son-at-home]
                  SCHOOL-OP* ))))
    (testing "CASE : monkey and bananas"
      (is (= '([:start] [:executing :push-chair-from-door-to-middle-room] [:executing :climb-on-chair]
               [:executing :drop-ball] [:executing :grasp-bananas] [:executing :eat-bananas])
             (GPS [:at-door :on-floor :has-ball :hungry :chair-at-door]
                  [:not-hungry]
                  BANANAS-OP* )))) ))

; (gps-chap4-13-tests)
(deftest gps-chap4-13-tests
  (let [GPS         (timeout-fn gps-chap4-13 500)
        MAZE-OP*    (map convert-op paip.dom-maze/+available-ops+) ]
    (testing "CASE : maze domain"
      (is (= [[:start]
              [:executing [:move :from 1 :to 2]]   [:executing [:move :from 2 :to 3]]   [:executing [:move :from 3 :to 4]]
              [:executing [:move :from 4 :to 9]]   [:executing [:move :from 9 :to 8]]   [:executing [:move :from 8 :to 7]]
              [:executing [:move :from 7 :to 12]]  [:executing [:move :from 12 :to 11]] [:executing [:move :from 11 :to 16]]
              [:executing [:move :from 16 :to 17]] [:executing [:move :from 17 :to 22]] [:executing [:move :from 22 :to 23]]
              [:executing [:move :from 23 :to 24]] [:executing [:move :from 24 :to 19]] [:executing [:move :from 19 :to 20]]
              [:executing [:move :from 20 :to 25]] ]
             (GPS [[:at 1]]
                  [[:at 25]]
                  MAZE-OP* ))))
    (testing "CASE : blocks domain"
      (is (= [[:start] [:executing [:move :a :from :table :to :b]]]
             (GPS [[:a :on :table] [:b :on :table] [:space :on :a] [:space :on :b] [:space :on :table]]
                  [[:a :on :b] [:b :on :table]]
                  (map convert-op (paip.dom-blocks/make-block-ops [:a :b])) )))
      (is (= [[:start] [:executing [:move :a :from :b :to :table]] [:executing [:move :b :from :table :to :a]]]
             (GPS [[:a :on :b] [:b :on :table] [:space :on :a] [:space :on :table]]
                  [[:b :on :a]]
                  (map convert-op (paip.dom-blocks/make-block-ops [:a :b])) )))
      (is (= [[:start] [:executing [:move :a :from :b :to :table]] [:executing [:move :b :from :c :to :a]]
              [:executing [:move :c :from :table :to :b]] ]
             (GPS [[:a :on :b] [:b :on :c] [:c :on :table] [:space :on :a] [:space :on :table]]
                  [[:b :on :a] [:c :on :b]]
                  (map convert-op (paip.dom-blocks/make-block-ops [:a :b :c])) ))))
      ))

; (destination-tests)
(deftest destination-tests
  (testing "CASE : nominal"
    (is (= 2 (destination [:executing [:move :from 1 :to 2]]))) ))

; (find-path-tests)
(deftest find-path-tests
  (use-ops (map convert-op paip.dom-maze/+available-ops+))
  (testing "CASE : nominal"
    (is (= [1 2 3 4 9 8 7 12 11 16 17 22 23 24 19 20 25]
           ((timeout-fn find-path 500) 1 25) ))
    (is (= [1]
           ((timeout-fn find-path 500) 1 1) ))
    (is (= ((timeout-fn find-path 500) 1 25)
           (reverse ((timeout-fn find-path 500) 25 1)) ))))

; (make-block-ops-tests)
(deftest make-block-ops-tests
  (testing "CASE : nominal"
    (is (= [[:move :b :from :a :to :table] [:move :b :from :table :to :a] [:move :a :from :b :to :table]
            [:move :a :from :table :to :b] ]
           (map op-name (make-block-ops [:a :b])) ))))


; (run-tests)

