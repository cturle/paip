(ns ctu.core-test
  (:require [clojure.test :refer :all]
            [ctu.core :refer :all]))


(def +CTXT1+ {:c1 {:val* #{1 2}}})

(deftest cget-tests
  (binding [*context* +CTXT1+]
    (testing "cget"
      (is (= (cget :c1) {:val* #{1 2}}))
      (is (try (cget :c2) false (catch Exception e true)))
      )))

(deftest cget*-tests
  (binding [*context* +CTXT1+]
    (testing "cget*"
      (is (= (cget* :c1 :val*) #{1 2}))
      (is (= (cget* :c1 :val*-unknown) #{}))
      (is (try (cget* :c2 :val*) false (catch Exception e true)))
      )))

(defn div-pre? [X Y] (and (number? X)
                          (number? Y)
                          (not= Y 0) ))

(deftest reduce-pre-tests
  (testing "reduce-pre"
    (is (= 1 (:out ((reduce-pre / div-pre?) 8 [1 2 4]))))
    (is (:all-pre-ok? ((reduce-pre / div-pre?) 8 [1 2 4])))
    (is (not (:all-pre-ok? ((reduce-pre / div-pre?) 8 [1 0 4]))))
    (is (= [8 0] (:bad-pre ((reduce-pre / div-pre?) 8 [1 0 4]))))
    (is (not (:all-pre-ok? ((reduce-pre / div-pre?) 8 [1 :x 4]))))
    (is (= [8 :x] (:bad-pre ((reduce-pre / div-pre?) 8 [1 :x 4]))))
    ))


(comment
  (run-tests)
  )


