;;;; File gps1_ctu_test.clj

(ns paip.gps1-ctu-test
  (:require [clojure.test :refer :all]
            [paip.gps1-ctu :refer :all]))


(def +GPS-PB1+ {:Op1 {:action   :drive-son-to-school
                      :preconds #{:son-at-home :car-works}
                      :add-list #{:son-at-school}
                      :del-list #{:son-at-home}
                      } })

(deftest op-accessors-tests
  (binding [*context* +GPS-PB1+]
    (testing "Operation accessors"
      (is (= :drive-son-to-school       (op-name :Op1)))
      (is (= #{:son-at-home :car-works} (pre* :Op1)))
      (is (= #{:son-at-school}          (add* :Op1)))
      (is (= #{:son-at-home}            (del* :Op1)))

      )))

(deftest apply-op-tests
  (binding [*context* +GPS-PB1+]
    (testing "apply-op-pre?"
      (let [C*1  #{:son-at-home :car-works :other}
            OP   :Op1]
        (is (apply-op-pre? C*1 OP)) )
      (let [C*1  #{:son-at-home}
            OP   :Op1]
        (is (not (apply-op-pre? C*1 OP))) ))
    (testing "apply-op-post?"
      (let [C*1  #{:son-at-home :car-works :other}
            OP   :Op1
            C*2  #{:car-works :other :son-at-school} ]
        (is (apply-op-post? C*1 OP C*2)) )
      (let [C*1  #{:son-at-home :car-works :other}
            OP   :Op1
            C*2  #{:car-works :other} ]
        (is (not (apply-op-post? C*1 OP C*2))) ))
    (testing "apply-op function"
      (let [C*1  #{:son-at-home :car-works :other}
            OP   :Op1
            C*2  (apply-op C*1 OP) ]
        (is (apply-op-post? C*1 OP C*2)) ))
      ))


(deftest gps-tests
  (binding [*context* +GPS-PB1+]
    (testing "gps-post?"
      (let [IS    #{:son-at-home :car-works :other}
            GS    #{:son-at-school}
            OP*1  #{:Op1}
            OP*2  [:Op1] ]
        (is (gps-post? IS GS OP*1 OP*2)) )
      (let [IS    #{:son-at-home :other}
            GS    #{:son-at-school}
            OP*1  #{:Op1}
            OP*2  [:Op1] ]
        (is (not (gps-post? IS GS OP*1 OP*2))) ))
    ))

(comment
   (run-tests)
  )
























