;;;; File src/ctu/gps1.clj: First version of GPS (General Problem Solver)
; Christophe Turle implementation from the same specification

(ns ctu.gps1
  (:require [clojure.set :as set]
            [ctu.core :refer :all]
            [ctu.heuristic-search :refer [solve-by-heuristic-search]]
            [paip.gps1 :refer [op-name pre* add* del*]] ))

;;; === § 4.1 => §4.4 : GPS1 from CTU
;;; GPS isa conceptual representation of problems and a 'means-ends' implementation to solve these problems



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
  [IS GS OP*1 OPNv]
  (or
   (false? OPNv)
   (let [get-op-by-name (fn [N] (first (filter #(= N (op-name %)) OP*1)))
         OP*2           (mapv get-op-by-name OPNv)
         R              ((reduce-pre apply-op apply-op-pre?) IS OP*2) ]
      (and (:all-pre-ok? R)
           (set/superset? (:out R) GS) ))))


(defn gps
  "General Problem Solver :
  [set-of Condition IC*, set-of Condition GC*, set-of Operation OP*1] -> (or False, list-of Operation) OPNv
  post : (or (false? OPNv)
             (and (= OPNv (mapv op-name OP2*))
                  (isa OP2* vector-of Operation :| (every? [OPi OP*2] (set/contains? OP*1 OPi))
                                                   (set/superset? (reduce apply-op IC* OP*2) GC*) )))

  * utilisation d'une recherche dans un graphe générique depuis le but vers l'état initial.

  * avec 'u'=union, '/'=set-diff, '^'=intersection, '0'=empty-set, '<='=included-in
  set-rule SR1 : X=A/B => X<=A and X^B=0
  set-rule SR2 : C<=A and A^B=0 => C^B=0
  set-rule SR3 : A<=BuC => A/B<=C and A/C<=B

  * avec C*1=cond*(N1), C*2 idem mais pour N2, A=add*(op), D=del*(op), P=pre*(op)
  I*1 = borne-inf de C*1, I*2 borne-inf de C*2
   => (R1) : I*1<=C*1, (R2) : I*2<=C*2
  (op-apply N1 op N2)
   => (R3) : P<=C*1 and (R4) : C*2=(C*1uA)/D
  R4,SR1
   => (R5) : C*2<=(C*1uA) and (R6) : C*2^D=0
  R5,R2
   => (R7) : I*2<=(C*1uA)
  R7,SR3
   => (R8) : I*2/C*1<=A and (R9) : I*2/A<=C*1
  R2,R6,SR2
   => (R10) : I*2^D=0

  * Ce qui nous donne :
  apply-op-pre (R10) : vérifier I*2^D=0
  heuristique à inclure dans apply-op-pre : not(I*2^A=0)
  apply-op (R3,R9)   : I*1 = Pu(I*2/A) ; car I*1 = borne inf de C*1"
  ([IC* GC* OP*1]
    (gps IC* GC* OP*1 (atom nil)) )
  ([IC* GC* OP*1 CTXT]
    (let [CTXT1  (zipmap (repeatedly #(gensym "Operation")) OP*1)
          NG  (gensym "Node-")
          D   (gensym "Definition-")
          G   (gensym "Graph-")
          PB  (gensym "Problem-")
          CTXT2 {PB   {:isa    :Find-Node-of-Generative-Graph-Problem
                       :graph  G
                       :pred   #(set/subset? (cget* % :cond*-inf) IC*)
                      }
                 G    {:isa      :Graph
                       :gen-def  D
                      }
                 D    {:isa            :Graph-Generative-Definition
                       :start-node     NG
                       :domain-op*     (set (keys CTXT1))
                       :apply-op-pre?  (fn [N2 OA]
                                         (and      (empty? (set/intersection (cget* N2 :cond*-inf) (del* (cget OA))))
                                              (not (empty? (set/intersection (cget* N2 :cond*-inf) (add* (cget OA))))) ))
                       :apply-op       (fn [N2 OA]
                                          {:isa        :Node
                                           :cond*-inf  (set/union (pre* (cget OA))
                                                                  (set/difference (cget* N2 :cond*-inf) (add* (cget OA))) )
                                          }
                                        )
                      }
                 NG   {:isa        :Node
                       :cond*-inf  GC*
                     }
                 }]
    (reset! CTXT (merge CTXT1 CTXT2))
    (binding [*context* CTXT]
      (if-not (solve-by-heuristic-search PB) false
        (mapv (comp op-name cget) (rseq (cget-v PB :op-v))) )))))


(defn gps-forward
  "General Problem Solver :
  [set-of Condition IC*, set-of Condition GC*, set-of Operation OP*1] -> (or False, list-of Operation) OPNv
  post : (or (false? OPNv)
             (and (= OPNv (mapv op-name OP2*))
                  (isa OP2* vector-of Operation :| (every? [OPi OP*2] (set/contains? OP*1 OPi))
                                                   (set/superset? (reduce apply-op IC* OP*2) GC*) )))"
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
        (mapv (comp op-name cget) (cget-v PB :op-v)) )))))

