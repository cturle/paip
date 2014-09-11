(ns ctu.heuristic-search-repl
  (:require [clojure.tools.trace :refer :all]
            [clojure.repl :refer :all]
            [clojure.inspector :refer :all]
            [ctu.core :refer :all]
            [ctu.heuristic-search :refer :all]
            [ctu.heuristic-search-test :refer :all]))


(def C (binding [*context* (atom (assoc-in +CTXT+ [:PB :pred] #(= 5 (cget % :value))))]
         ((timeout-fn solve-by-heuristic-search 2000) :PB)
         *context* ))


; with GUI
(inspect-tree (deref C))






