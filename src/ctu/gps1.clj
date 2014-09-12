;;;; File ctu/gps1.clj: First version of GPS (General Problem Solver)
; Christophe Turle implementation from the same specification

(ns ctu.gps1
  (:require [clojure.set :as set]
            [ctu.core :refer :all]
            [ctu.heuristic-search :refer [solve-by-heuristic-search]] ))

;;; === § 4.1 => §4.4 : GPS1 from CTU
;;; GPS isa conceptual representation of problems and a 'means-ends' implementation to solve these problems


;;; Conceptual Representation of Problems
; Condition = Keyword
; Operation = [the Keyword op-name, set-of Condition pre*, set-of Condition add*, set-of Condition del*]
; apply-op = [set-of Condition, Operation] -> set-of Condition
; gps = [set-of Condition init-state, set-of Condition goal-state, set-of Operation] -> list-of Operation


;;; Operation accessors

(defn op-name
  "get the name of an Operation OP"
  [OP]
  (get OP :action) )

(defn pre*
  "get preconditions of Operation OP"
  [OP]
  (get OP :preconds #{}))

(defn add*
  "get conditions to add after applying Operation OP"
  [OP]
  (get OP :add-list #{}))

(defn del*
  "get conditions to remove after applying Operation OP"
  [OP]
  (get OP :del-list #{}))

;;; apply-op : apply an Operation on a set of Condition

(defn apply-op-pre?
  [C*1 OP]
  (set/subset? (pre* OP) C*1) )

(defn apply-op-post?
  [C*1 OP C*2]
  (= C*2 (set/difference (set/union C*1 (add* OP)) (del* OP))) )

(defn apply-op
"apply-op : [set-of Condition C*1, Operation OP] -> set-of Condition C*2
 pre:  (set/subset? (pre* OP) C*1)
 post: (= C*2 (set/difference (set/union C*1 (add* OP)) (del* OP)))"
  [C*1 OP]
  (set/difference (set/union C*1 (add* OP)) (del* OP)) )


;;;; GPS is a 'means-ends' implementation to solve these problems


(defn gps-post?
  [IS GS OP*1 OP*2]
  (or
   (false? OP*2)
   (and (every? #(contains? OP*1 %) OP*2)
        (let [R ((reduce-pre apply-op apply-op-pre?) IS OP*2)]
          (and (:all-pre-ok? R)
               (set/superset? (:out R) GS) )))))


; (?PB :isa :Find-Node-of-Generative-Graph-Problem)
; => (one-of [?N (all-nodes (graph ?PB)) :when ((pred ?PB) ?N)] ?N)

;(declare new-gps-problem solved? elaborate-state choose-next-State-Operator apply-State-Operator)

(defn gps
  "General Problem Solver :
  [set-of Condition IC*, set-of Condition GC*, set-of Operation OP*1] -> (or False, list-of Operation) OP*2
  post : (or (false? OP*2)
             (and (every? [OPi OP*2] (set/contains? OP*1 OPi))
                  (set/superset? (reduce apply-op IC* OP*2) GC*) ))"
  ([IC* GC* OP*1]
    (gps IC* GC* OP*1 (atom nil)) )
  ([IC* GC* OP*1 CTXT]
    (let [CTXT1  (zipmap (repeatedly #(gensym "Operation")) OP*1)
          N0  (gensym "Node-")
          D   (gensym "Definition-")
          G   (gensym "Graph-")
          PB  (gensym "Problem-")
          CTXT2 {PB   {:isa    :Find-Node-of-Generative-Graph-Problem
                       :graph  G
                       :pred   #(set/subset? GC* (cget* % :cond*))
                      }
                 G    {:isa      :Graph
                       :gen-def  D
                      }
                 D    {:isa            :Graph-Generative-Definition
                       :start-node     N0
                       :domain-op*     (set (keys CTXT1))
                       :apply-op-pre?  #(apply-op-pre? (cget* %1 :cond*) (cget %2))
                       :apply-op       (fn [N OA]
                                          {:isa    :Node
                                           :cond*  (apply-op (cget* N :cond*) (cget OA))
                                          }
                                        )
                      }
                 N0   {:isa    :Node
                       :cond*  IC*
                     }
                 }]
    (reset! CTXT (merge CTXT1 CTXT2))
    (binding [*context* CTXT]
      (if-not (solve-by-heuristic-search PB) false
        (mapv cget (cget-v PB :op-v)) )))))


