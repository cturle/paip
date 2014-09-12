;;;; File ctu/gps1_test.clj

(ns ctu.gps1-test
  (:require [clojure.test :refer :all]
            [ctu.core :refer :all]
            [ctu.gps1 :refer :all]))

(def +GPS-PB1+ {:Op1 {:action   :drive-son-to-school
                      :preconds #{:son-at-home :car-works}
                      :add-list #{:son-at-school}
                      :del-list #{:son-at-home}
                      } })

; (op-accessors-tests)
(deftest op-accessors-tests
  (binding [*context* (atom +GPS-PB1+)]
    (testing "Operation accessors"
      (is (= :drive-son-to-school       (op-name (cget :Op1))))
      (is (= #{:son-at-home :car-works} (pre* (cget :Op1))))
      (is (= #{:son-at-school}          (add* (cget :Op1))))
      (is (= #{:son-at-home}            (del* (cget :Op1))))
      )))


; (apply-op-tests)
(deftest apply-op-tests
  (binding [*context* (atom +GPS-PB1+)]
    (testing "apply-op-pre?"
      (let [C*1  #{:son-at-home :car-works :other}
            OP   :Op1]
        (is (apply-op-pre? C*1 (cget OP))) )
      (let [C*1  #{:son-at-home}
            OP   :Op1]
        (is (not (apply-op-pre? C*1 (cget OP))) ))
    (testing "apply-op-post?"
      (let [C*1  #{:son-at-home :car-works :other}
            OP   :Op1
            C*2  #{:car-works :other :son-at-school} ]
        (is (apply-op-post? C*1 (cget OP) C*2)) )
      (let [C*1  #{:son-at-home :car-works :other}
            OP   :Op1
            C*2  #{:car-works :other} ]
        (is (not (apply-op-post? C*1 (cget OP) C*2))) ))
    (testing "apply-op function"
      (let [C*1  #{:son-at-home :car-works :other}
            OP   :Op1
            C*2  (apply-op C*1 (cget OP)) ]
        (is (apply-op-post? C*1 (cget OP) C*2)) ))
      )))


(def +school-ops+
  #{{:action   :drive-son-to-school
     :preconds #{:son-at-home :car-works}
     :add-list #{:son-at-school}
     :del-list #{:son-at-home}
     }
    {:action   :shop-installs-battery
     :preconds #{:car-needs-battery :shop-knows-problem :shop-has-money}
     :add-list #{:car-works}
     }
   {:action   :tell-shop-problem
    :preconds #{:in-communication-with-shop}
    :add-list #{:shop-knows-problem}
    }
   {:action   :telephone-shop
    :preconds #{:know-phone-number}
    :add-list #{:in-communication-with-shop}
    }
   {:action   :look-up-number
    :preconds #{:have-phone-book}
    :add-list #{:know-phone-number}
    }
   {:action   :give-shop-money
    :preconds #{:have-money}
    :add-list #{:shop-has-money}
    :del-list #{:have-money}
    } })


; (gps-tests)
(deftest gps-tests
  (testing "POST"
    (binding [*context* (atom +GPS-PB1+)]
      (let [IS    #{:son-at-home :car-works :other}
            GS    #{:son-at-school}
            OP*1  #{(cget :Op1)}
            OP*2  [(cget :Op1)] ]
        (is (gps-post? IS GS OP*1 OP*2)) )
      (let [IS    #{:son-at-home :other}
            GS    #{:son-at-school}
            OP*1  #{(cget :Op1)}
            OP*2  [(cget :Op1)] ]
        (is (not (gps-post? IS GS OP*1 OP*2))) )))
  (let [gps-to (timeout-fn gps 3000)]
    (testing "CASE : backward solution"
      (let [IS     #{:son-at-home :car-needs-battery :have-money :have-phone-book}
            GS     #{:son-at-school}
            OP*1   +school-ops+
            OP*2   (gps-to IS GS OP*1) ]
        (is OP*2)
        (is (gps-post? IS GS OP*1 OP*2)) ))
    (testing "CASE : no solution"
      (let [IS    #{:son-at-home :car-needs-battery :have-money}
            GS    #{:son-at-school}
            OP*1  +school-ops+
            OP*2  (gps-to IS GS OP*1) ]
        (is (false? OP*2)) ))
    (testing "CASE : one step solution"
      (let [IS    #{:son-at-home :car-works}
            GS    #{:son-at-school}
            OP*1  +school-ops+
            OP*2  (gps-to IS GS OP*1) ]
        (is OP*2)
        (is (gps-post? IS GS OP*1 OP*2)) ))))


(comment
   (run-tests)
  )


















