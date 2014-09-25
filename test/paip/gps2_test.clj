;;;; File test/paip/gps1_test.clj

(ns paip.gps2-test
  (:require [clojure.test :refer :all]
            [ctu.core :refer :all]
            [paip.core :refer :all]
            [paip.gps2 :refer :all]
            [paip.dom-bananas] ))

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
                  +paip-school-ops+ )))
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
                  (map convert-op paip.dom-bananas/+available-ops+) ))))
      )))


; (run-tests)

