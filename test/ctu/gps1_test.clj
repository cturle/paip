;;;; File test/ctu/gps1_test.clj

(ns ctu.gps1-test
  (:require [clojure.test :refer :all]
            [ctu.core :refer :all]
            [ctu.gps1 :refer :all]
            [paip.gps1 :refer [+school-ops+ op-name pre* add* del*]] ))

(def +GPS-PB1+ {:Op1 {:action   :drive-son-to-school
                      :preconds #{:son-at-home :car-works}
                      :add-list #{:son-at-school}
                      :del-list #{:son-at-home}
                      } })

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


; (gps-tests)
(deftest gps-tests
  (testing "POST"
    (binding [*context* (atom +GPS-PB1+)]
      (let [IS    #{:son-at-home :car-works :other}
            GS    #{:son-at-school}
            OP*1  #{(cget :Op1)}
            OP*2  [(op-name (cget :Op1))] ]
        (is (gps-post? IS GS OP*1 OP*2)) )
      (let [IS    #{:son-at-home :other}
            GS    #{:son-at-school}
            OP*1  #{(cget :Op1)}
            OP*2  [(op-name (cget :Op1))] ]
        (is (not (gps-post? IS GS OP*1 OP*2))) )))
  (let [gps-to (timeout-fn gps 3000)]
    (testing "CASE : backward solution"
      (let [IS     #{:son-at-home :car-needs-battery :have-money :have-phone-book}
            GS     #{:son-at-school}
            OP*1   +school-ops+
            OP*2   (gps-to IS GS OP*1) ]
        (when (is OP*2)
          (is (gps-post? IS GS OP*1 OP*2))) ))
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
        (when (is OP*2)
          (is (gps-post? IS GS OP*1 OP*2)) )))))


; (run-tests)


















