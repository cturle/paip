;;;; File src/paip/gps1.clj: First version of GPS (General Problem Solver)
;;;  from Paradigms of Artificial Intelligence Programming

(ns paip.gps1
  (:require [ctu.core :refer :all]
            [ctu.seq-set :as ss]
            [paip.core :refer :all]) )


; === § 4.1 => §4.4 : GPS1 from Book

;;; Conceptual Representation

; Condition =
;  Keyword
; Operator =
;  [the Keyword op-name, set-of Condition pre*, set-of Condition add*, set-of Condition del*]
; apply-op-pre =
;  [set-of Condition, Operator] -> boolean
; apply-op =
;  [set-of Condition, Operator] -> set-of Condition
; gps =
;  [set-of Condition init-state, set-of Condition goal-state, set-of Operation] -> (or nil :solved)


;;; INTERFACE

;; Operator accessors
;; not needed but it helps to read the code

(defn op-name
  "get the name of an Operator OP"
  [OP]
  (get OP :action) )

(defn pre*
  "get preconditions of Operator OP"
  [OP]
  (get OP :preconds []))

(defn add*
  "get conditions to add after applying Operator OP"
  [OP]
  (get OP :add-list []))

(defn del*
  "get conditions to remove after applying Operator OP"
  [OP]
  (get OP :del-list []))


;;; IMPLEMENTATION

(def ^:dynamic *current-state*
  "The current state: a set of conditions."
  [] )

(def ^:dynamic *available-ops*
  "A set of available operators."
  [] )

(declare achieve appropriate? apply-op)

(defn gps
  "General Problem Solver: achieve all goals G* using operators O* from starting conditions SC*.
   Backward chaining with depth-first operator selection."
  [SC* G* O*]
  (binding [*current-state* SC*
            *available-ops* O*]
    (if (every? achieve G*) :solved) ))


(defn achieve
  "A goal G is achieved if it already holds,
  or if there is an appropriate op for it that is applicable."
  [G]
  (or (ss/contains? *current-state* G)
      (some apply-op (filter #(appropriate? G %) *available-ops*)) ))


(defn appropriate?
  "An OP is appropriate to a goal G if G is in OP add list."
  [G OP]
  (ss/contains? (add* OP) G) )


(defn apply-op
  "Print a message and update State if OP is applicable."
  [OP]
  (when (Thread/interrupted)
    (throw (ex-info "interruption in apply-op")) )
  (when (every? achieve (pre* OP))
    (dprintln (list 'executing (op-name OP)))
    (set! *current-state* (ss/union (ss/difference *current-state* (del* OP)) (add* OP)))
    true ))




