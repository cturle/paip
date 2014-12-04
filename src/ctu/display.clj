(ns ctu.display
  (:require [clojure.inspector :refer :all]
            [ctu.core :refer :all]
            [ctu.heuristic-search :refer :all]
            [ctu.gps1 :refer :all]
            [paip.gps1 :refer [op-name pre* add* del*]] ))


(declare node-view op-apply-view)

(defn display1
"display historic of choices of current *context*"
  [PB]
  (let [HISTORIC  (map op-apply-view (cget-v PB :op-apply-v))]
    (inspect-tree HISTORIC) ))

(defn node-view [N]
  (cget N) )

(defn op-apply-view [OA]
  {:from (cget (cget OA :node))
   :op   (cget (cget OA :op))
   :to   (cget (cget OA :out)) } )


(defn display2
"display list of op-apply which :node = N (default to start node) of current *context*"
  ([PB]
    (display2 PB (cget PB :start-node)) )
  ([PB N]
    (let [HISTORIC  (map op-apply-view (filter #(= N (cget % :node)) (cget-v PB :op-apply-v)))]
      (inspect-tree HISTORIC) )))



