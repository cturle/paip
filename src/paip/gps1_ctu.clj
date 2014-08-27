;;;; File gps1_ctu.clj: First version of GPS (General Problem Solver)
; Christophe Turle implementation from the same specification

(ns paip.gps1-ctu
  (:require [clojure.set :as set]
            [ctu.core :refer :all] ))

;;; === § 4.1 => §4.4 : GPS1 from CTU
;;; GPS isa conceptual representation of problems and a 'means-ends' implementation to solve these problems


;;; Conceptual Representation of Problems
; Condition = Keyword
; Operation = [the Keyword op-name, set-of Condition pre*, set-of Condition add*, set-of Condition del*]
; apply-op = [set-of Condition, Operation] -> set-of Condition
; gps = [set-of Condition init-state, set-of Condition goal-state, set-of Operation] -> list-of Operation

(def ^:dynamic *context* {})

;;; Operation accessors

(defn op-name
  "get the name of an Operation OP"
  [OP]
  (get (get *context* OP) :action) )

(defn pre*
  "get preconditions of Operation OP"
  [OP]
  (get (get *context* OP) :preconds #{}))

(defn add*
  "get conditions to add after applying Operation OP"
  [OP]
  (get (get *context* OP) :add-list #{}))

(defn del*
  "get conditions to remove after applying Operation OP"
  [OP]
  (get (get *context* OP) :del-list #{}))

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
(declare new-gps-problem solved? elaborate-state choose-next-State-Operator apply-State-Operator)

(defn gps-post?
  [IS GS OP*1 OP*2]
  (or
   (false? OP*2)
   (and (every? #(contains? OP*1 %) OP*2)
        (let [R ((reduce-pre apply-op apply-op-pre?) IS OP*2)]
          (and (:all-pre-ok? RET)
               (set/superset? (:out R) GS) )))))


(defn gps
  "General Problem Solver :
  [set-of Condition IC*, set-of Condition GC*, set-of Operation OP*1] -> (or False, list-of Operation) OP*2
  post : (or (false? OP*2)
             (and (every? [OPi OP*2] (set/contains? OP*1 OPi))
                  (set/superset? (reduce apply-op IC* OP*2) GC*) ))"
  [IC* GC* OP*1]

)


;  (loop [P (new-gps-problem IC* GC* OP*1)]
;    (if (solved? P) P
;      (let [[P NS NOP] (choose-next-State-Operator P)]
;        (if (nil? NS) P
;            (recur (apply-State-Operator P NS NOP)) )))))


; (def P (new-gps-problem #{:son-at-home :car-needs-battery :have-money :have-phone-book} #{:son-at-school} School-ops))
(defn new-gps-problem
  "create a new gps from problem with set of init Conditions IC*, set of goal Conditions GC* and set of domain operations OP*"
  [IC* GC* OP*]
  (let [IS-solved (set/superset? IC* GC*)
        IS-name   (gensym "State")
        IS-value  {:cond* IC*
                   :prev-state nil}
        P0        {:init-cond* IC*
                   :goal-cond* GC*
                   :domain-op* OP*
                   :initial-state  IS-name
                   :state*         #{IS-name}
                   :solving-state* (if IS-solved #{IS-name} #{})
                   IS-name     IS-value }
        P1         (add-operation* P0 OP*)]
    P1
    )
  )

(defn add-operation*-post?
  "[GPS-Problem P1, set-of Operation OP*] -> GPS-Problem P2"
  [P1 OP* P2]
  (let [NOP* (set/difference (set (keys P2)) (set (keys P1)))]
    (and (= NOP* (set/difference (op* P2) (op* P1)))
         (= OP*  (set (for [NOP NOP*] (get P2 NOP)))) ))
  )

; (solved? P)
(defn solved?
  "returns true if problem is solved, false otherwise."
  [P]
  (not (empty? (get P :solving-state*)))
  )

(defn choose-next-State-Operator
  "GPS-Problem P1 -> GPS-Problem P2
  "
  [P1]

  )


