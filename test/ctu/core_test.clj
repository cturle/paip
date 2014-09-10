(ns ctu.core-test
  (:require [clojure.test :refer :all]
            [ctu.core :refer :all]))


(def +CTXT1+ {:c1 {:val* #{1 2}, :val 3}})

; (cget-tests)
(deftest cget-tests
  (binding [*context* (atom +CTXT1+)]
    (testing "CASE : cget with one argument"
      (is (= (cget :c1) (get +CTXT1+ :c1)))
      (is (thrown? Exception (cget :c2))) )
    (testing "CASE : cget with two arguments"
      (is (= (cget :c1 :val) 3))
      (is (nil? (cget :c1 :val2)))
      (is (thrown? Exception (cget :c2 :val))) )))


(deftest cget*-tests
  (binding [*context* (atom +CTXT1+)]
    (testing "cget*"
      (is (= (cget* :c1 :val*) #{1 2}))
      (is (let [R (cget* :c1 :val*-unknown)] (and (set? R) (= #{} R))))
      (is (thrown? Exception (cget* :c2 :val*)))
      )))

(deftest cget-v-tests
  (binding [*context* (atom +CTXT1+)]
    (testing "cget-v"
      (is (= (cget-v :c1 :val*) #{1 2}))
      (is (let [R (cget-v :c1 :val*-unknown)] (and (vector? R) (= [] R))))
      (is (thrown? Exception (cget-v :c2 :val*)))
      )))


(def ^:dynamic *V* 0)

; (timeout-fn-tests)
(deftest timeout-fn-tests
  (let [F (timeout-fn (fn [T] (. Thread (sleep T)) T) 1000)]
    (testing "CASE : return value"
      (is (= 500 (F 500))) )
    (testing "CASE : interruption"
      (is (nil? (F 3000))) ))
  (testing "CASE : bindings are preserved"
    (let [F (timeout-fn (fn [] *V*) 1000)]
      (binding [*V* 2]
        (is (= 2 (F))) )))
  )


(declare div-pre?)

; (reduce-pre-tests)
(deftest reduce-pre-tests
  (testing "(reduce-pre / div-pre?)"
    (let [F (timeout-fn (reduce-pre / div-pre?) 1000)]
      (is (= 1 (:out (F 8 [1 2 4]))))
      (is (:all-pre-ok? (F 8 [1 2 4])))
      (is (not (:all-pre-ok? (F 8 [1 0 4]))))
      (is (= [8 0] (:bad-pre (F 8 [1 0 4]))))
      (is (not (:all-pre-ok? (F 8 [1 :x 4]))))
      (is (= [8 :x] (:bad-pre (F 8 [1 :x 4]))))
    )))

(defn div-pre? [X Y] (and (number? X)
                          (number? Y)
                          (not= Y 0) ))


; (choice-v-tests)
(deftest choice-v-tests
  (testing "CASE : nominal, F = choice-v"
    (let [F (timeout-fn choice-v 1000)]
      (is (= [inc inc] (F 1 (fn [S] inc) (fn [S C] (C S)) (fn [S] (= 3 S))))) )))


(comment
  (run-tests)
  )

















