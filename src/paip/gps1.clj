;;;; File src/paip/gps1.clj: First version of GPS (General Problem Solver)
;;;  from Paradigms of Artificial Intelligence Programming

(ns paip.gps1
  (:require [clojure.set :as set]
            [ctu.core :refer :all]
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


;;; DATA

;; map are used instead of p-list
;; clojure set are used to represent set

(def +school-ops+
  #{{:action   :drive-son-to-school
     :preconds #{:son-at-home :car-works}
     :add-list #{:son-at-school}
     :del-list #{:son-at-home}
     }
    {:action   :shop-installs-battery
     :preconds #{:car-needs-battery :shop-knows-problem :shop-has-money}
     :add-list #{:car-works}
     }
   {:action   :tell-shop-problem
    :preconds #{:in-communication-with-shop}
    :add-list #{:shop-knows-problem}
    }
   {:action   :telephone-shop
    :preconds #{:know-phone-number}
    :add-list #{:in-communication-with-shop}
    }
   {:action   :look-up-number
    :preconds #{:have-phone-book}
    :add-list #{:know-phone-number}
    }
   {:action   :give-shop-money
    :preconds #{:have-money}
    :add-list #{:shop-has-money}
    :del-list #{:have-money}
    } })


;;; INTERFACE

;; Operator accessors
;; not needed but helps to read the code

(defn op-name
  "get the name of an Operator OP"
  [OP]
  (get OP :action) )

(defn pre*
  "get preconditions of Operator OP"
  [OP]
  (get OP :preconds #{}))

(defn add*
  "get conditions to add after applying Operator OP"
  [OP]
  (get OP :add-list #{}))

(defn del*
  "get conditions to remove after applying Operator OP"
  [OP]
  (get OP :del-list #{}))


;;; IMPLEMENTATION

(def ^:dynamic *current-state*
  "The current state: a set of conditions."
  #{} )

(def ^:dynamic *available-ops*
  "A set of available operators."
  #{} )

(declare achieve appropriate? apply-op)

(defn gps
  "General Problem Solver: achieve all goals G* using operators O* from starting conditions SC*."
  [SC* G* O*]
  (binding [*current-state* SC*
            *available-ops* O*]
    (if (every? achieve G*) :solved) ))


(defn achieve
  "A goal G is achieved if it already holds,
  or if there is an appropriate op for it that is applicable."
  [G]
  (or (contains? *current-state* G)
      (some apply-op (filter #(appropriate? G %) *available-ops*)) ))


(defn appropriate?
  "An OP is appropriate to a goal G if G is in OP add list."
  [G OP]
  (contains? (add* OP) G) )


(defn apply-op
  "Print a message and update State if OP is applicable."
  [OP]
  (when (Thread/interrupted)
    (throw (ex-info "interruption in apply-op")) )
  (when (every? achieve (pre* OP))
    (dprintln (list 'executing (op-name OP)))
    (set! *current-state* (set/union (set/difference *current-state* (del* OP)) (add* OP)))
    true ))




