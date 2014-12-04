(ns ctu.heuristic-search-test
  (:require [clojure.test :refer :all]
            [ctu.core :refer :all]
            [ctu.heuristic-search :refer :all]))


(def +CTXT+  {:PB  {:isa    :Find-Node-of-Generative-Graph-Problem
                    :graph  :G
                    :pred   #(= 2 (cget % :value))
                   }
              :G   {:isa      :Graph
                    :gen-def  :D
                   }
              :D   {:isa            :Graph-Generative-Definition
                    :start-node     :N0
                    :domain-op*     [:A1 :A2]
                    :apply-op-pre?  (constantly true)
                    :apply-op       (fn [N OP] {:value  ((cget OP :fn) (cget N :value))})
                   }
              :N0  {:isa    :Node
                    :value  1
                   }
              :A1  {:isa    :Operator
                    :fn     inc
                   }
              :A2  {:isa    :Operator
                    :fn     dec
                   }
             })

; (init-context-tests)
(deftest init-context-tests
  (binding [*context* (atom +CTXT+)]
    (testing "CASE : adding init-node N0 to graph :node*"
      (is (init-context :PB))
      (is (= #{:N0} (cget :G :node*)))
      )))

; (get-solution-tests)
(deftest get-solution-tests
  (binding [*context* (atom +CTXT+)]
    (init-context :PB)
    (testing "CASE : none"
      (is (nil? (get-solution :PB))) )
    (testing "CASE : found"
      (let [R (apply-operator-on-node :PB :A1 :N0)
            N (get-solution :PB) ]
        (is N)
        (is (= N (cget (:op-apply R) :out)))
      ))))

; (potential-node-operator-v-tests)
(deftest potential-node-operator-v-tests
  (binding [*context* (atom +CTXT+)]
    (init-context :PB)
    (testing "CASE : nominal"
      (is (= #{[:N0 :A1] [:N0 :A2]} (set (potential-node-operator-v :PB))))
      )))

; (choose-next-node-and-operator-tests)
(deftest choose-next-node-and-operator-tests
  (binding [*context* (atom +CTXT+)]
    (init-context :PB)
    (testing "CASE : nominal"
      (let [{:keys [success node operator]} (choose-next-node-and-operator :PB)]
        (is success)
        (is (= :N0 node))
        (is (contains? (cget :D :domain-op*) operator)) ))
    (testing "CASE : node/operator not already applied"
      (add-op-apply :PB {:node :N0, :op :A1})
      (swap! *context* assoc-in [:D :apply-op-pre?] #(and (= %1 :N0) (= %2 :A1)))
      (let [R (choose-next-node-and-operator :PB)]
        (is (false? (:success R)) ))
      )))


; (find-node-tests)
(deftest find-node-tests
  (binding [*context* (atom +CTXT+)]
    (init-context :PB)
    (testing "CASE : nominal"
      (is (= :N0 (find-node :G {:value 1})))
      (is (false? (find-node :G {:value 2})))
      )))

; (add-node-tests)
(deftest add-node-tests
  (binding [*context* (atom +CTXT+)]
    (init-context :PB)
    (testing "CASE : nominal"
      (let [N (add-node :G {:value 2})]
        (is (= {:isa :Node, :ref N, :value 2} (cget N)))
        (is (contains? (cget* :G :node*) N))
      ))))

; (add-op-apply-tests)
(deftest add-op-apply-tests
  (binding [*context* (atom +CTXT+)]
    (init-context :PB)
    (testing "CASE : nominal"
      (let [OA (add-op-apply :PB {:node :N0, :op :A1, :out :N1})]
        (is (= {:isa :Op-Apply, :node :N0, :op :A1, :out :N1} (cget OA)))
        (is (= OA (peek (cget-v :PB :op-apply-v))))
      ))))

; (apply-operator-on-node-tests)
(deftest apply-operator-on-node-tests
  (binding [*context* (atom +CTXT+)]
    (init-context :PB)
      (testing "CASE : nominal"
        (let [R (apply-operator-on-node :PB :A1 :N0)]
          (is (get R :success))
          (let [OA (get R :op-apply)
                N1 (cget OA :out) ]
            (is (= 2 (cget N1 :value)))
            (let [R (apply-operator-on-node :PB :A2 N1)]
              (is (get R :success))
              (let [OA (get R :op-apply)
                    N0 (cget OA :out) ]
                (is (= :N0 N0)) )))))))


;(def +context+ (atom {}))

; (heuristic-search-tests)
(deftest heuristic-search-tests
  (let [SOLVE (timeout-fn solve-by-heuristic-search 1000)]
    (testing "CASE : 0 step from solution"
      (binding [*context* (atom (assoc-in +CTXT+ [:PB :pred] #(= 1 (cget % :value))))]
        (is (SOLVE :PB))
        (is (= :solved (cget :PB :state)))
        (is (= [] (cget-v :PB :op-v)))
        ))
    (testing "CASE : 1 step from solution"
      (binding [*context* (atom +CTXT+)]
        (is (SOLVE :PB))
        (is (= :solved (cget :PB :state)))
        (is (= [:A1] (cget-v :PB :op-v)))
        ))
    (testing "CASE : 4 step from solution"
      (binding [*context* (atom (assoc-in +CTXT+ [:PB :pred] #(= 5 (cget % :value))))]
        (is (SOLVE :PB))
        (is (= :solved (cget :PB :state)))
        (is (= [:A1 :A1 :A1 :A1] (cget-v :PB :op-v)))
       ))
    (testing "CASE : no solution"
      (binding [*context* (atom (assoc-in +CTXT+ [:D :apply-op-pre?] (constantly false)))]
        (is (not (SOLVE :PB)))
        (is (= :end-because-choose-failed (cget :PB :state)))
        ))
    ))


; (get-pb-id-tests)
(deftest get-pb-id-tests
  (testing "CASE : nominal"
    (binding [*context* (atom +CTXT+)]
      (is (= :PB (get-pb-id))) ))
  (testing "CASE : not found"
    (binding [*context* (atom (dissoc +CTXT+ :PB))]
      (is (nil? (get-pb-id ))) )))

; (run-tests)




















