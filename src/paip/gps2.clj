(ns paip.gps2
  (require [clojure.set :as set]
           [ctu.core  :refer :all]
           [paip.core :refer :all]
           [paip.gps1 :as v1 :refer [op-name add* del* pre*, *available-ops*]] ))


;;; ==============================

(defn executing?
"Is x of the form: (:executing ...) ?"
  [X]
  (starts-with X :executing) )

(defn convert-op
"Make op conform to the (:executing op-name) convention."
  [OP]
  (if (some executing? (add* OP))
    OP
    (update-in OP [:add-list] conj [:executing (op-name OP)]) ))

; Norvig names it 'op'
(defn mk-op
"Make a new operator that obeys the (:executing op-name) convention."
  [OP-PP*]
  (convert-op OP-PP*) )


;;; ==============================

(def +school-ops+ (set (map convert-op v1/+school-ops+)))

;;; ==============================

; *ops* : re-using v1/*available-ops*

(declare achieve-all achieve apply-op)

(defn gps
"General Problem Solver: from state S (init conditions), achieve goals G* (final conditions) using operators OP*."
  [S G* OP*]
  (binding [*available-ops* OP*]
    (when-let [AA (achieve-all (cons [:start] S) G* '())]
      (filter coll? AA) )))

;;; ==============================

(defn achieve-all
"Achieve each goal, and make sure they still hold at the end."
 [S G* G-STACK]
 ; very imperative but stick to original definition
 (with-local-vars [CURRENT-STATE S]
   (if (and (every? #(var-set CURRENT-STATE (achieve @CURRENT-STATE % G-STACK)) G*)
            (set/subset? (set G*) (set @CURRENT-STATE)) )
        @CURRENT-STATE )))

(defn achieve
"A goal G is achieved from state S if it already holds, or if there is an appropriate op for it that is applicable."
  [S G G-STACK]
  (dbg-indent :gps (count G-STACK) "Goal: ~a" G)
  (cond (some #(= G %) S)
          (do (dbg-indent :gps (count G-STACK) "OK: Goal in current state")
              S )
        (some #(= G %) G-STACK)
          (do (dbg-indent :gps (count G-STACK) "CANCEL : Goal in stack")
              nil )
        true
          (if-let [NS (some #(apply-op S G % G-STACK)
                            (filter #(v1/appropriate? G %) *available-ops*) )]
            NS
            (dbg-indent :gps (count G-STACK) "CANCEL : No operator found") )))

;;; ==============================

; member-equal not needed :default behavior of contains?

;;; ==============================

(defn apply-op
"Return a new, transformed state if OP is applicable."
  [S G OP G-STACK]
  ; allow timeout-fn to interrupt Thread
  (when (Thread/interrupted)
    (throw (ex-info "interruption in apply-op")) )
  (dbg-indent :gps (count G-STACK) "Consider: ~a" (op-name OP))
  (let [NS (achieve-all S (pre* OP) (cons G G-STACK))]
    (when-not (nil? NS)
      ;; Return an updated state
      (dbg-indent :gps (count G-STACK) "Action: ~a" (op-name OP))
      (concat (remove #(contains? (del* OP) %) NS) (add* OP)) )))


;;; ==============================

; no use of 'use'









