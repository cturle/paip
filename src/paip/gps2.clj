(ns paip.gps2
  (require [ctu.core  :refer :all]
           [ctu.seq-set :as ss]
           [paip.core :refer :all]
           [paip.gps1 :as v1 :refer [*available-ops* op-name add* del* pre*]] ))


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
    (update-in OP [:add-list] add-front [:executing (op-name OP)]) ))

(defn op
"Make a new operator that obeys the (:executing op-name) convention."
  [OP-NAME OP-PP*]
  (convert-op (assoc OP-PP* :action OP-NAME)) )


;;; ==============================

; *ops* : defined in test or repl files

(declare achieve-all *appropriate-ops* apply-op)

(defn gps
"General Problem Solver: from state S (init conditions), achieve goals G* (final conditions) using operators OP*.
 Backward chaining with depth-first operator selection."
  [S G* OP*]
  (dbg :gps "START : GPS")
  (binding [*available-ops* OP*]
    (when-let [AA (achieve-all (add-front S [:start]) G* [])]
      (filter coll? AA) )))

;;; ==============================

(defn achieve-all
"Achieve each goal, and make sure they still hold at the end."
 [S G* G-STACK]
 ; very imperative but stick to original definition
 (with-local-vars [CURRENT-STATE S]
   (if (and (every? #(var-set CURRENT-STATE (achieve @CURRENT-STATE % G-STACK)) G*)
            (or (ss/subset? G* @CURRENT-STATE)
                (dbg-indent :gps (count G-STACK) "CANCEL : All goals no more achieved") ))
        @CURRENT-STATE )))

(defn achieve
"A goal G is achieved from state S if it already holds, or if there is an appropriate op for it that is applicable."
  [S G G-STACK]
  (dbg-indent :gps (count G-STACK) "Goal: ~a" G)
  (cond (ss/contains? S G)
          (do (dbg-indent :gps (count G-STACK) "OK: Goal in current state")
              S )
        (ss/contains? G-STACK G)
          (dbg-indent :gps (count G-STACK) "CANCEL : Goal in stack")
        true
          (or (some #(apply-op S G % G-STACK) (*appropriate-ops* G S))
              (dbg-indent :gps (count G-STACK) "CANCEL : No operator found") )))

(defn appropriate-ops [G S]
  (filter #(v1/appropriate? G %) *available-ops*) )

; to contextualize the 'achieve' function
(def ^:dynamic *appropriate-ops* appropriate-ops)


; member-equal not needed :default behavior of contains?

(defn apply-op
"Return a new, transformed state if OP is applicable."
  [S G OP G-STACK]
  ; allow timeout-fn to interrupt Thread
  (when (Thread/interrupted)
    (throw (ex-info "interruption in apply-op")) )
  (dbg-indent :gps (count G-STACK) "Consider: ~a" (op-name OP))
  (let [NS (achieve-all S (pre* OP) (add-front G-STACK G))]
    (when-not (nil? NS)
      ;; Return an updated state
      (dbg-indent :gps (count G-STACK) "Action: ~a" (op-name OP))
      (ss/union (ss/difference NS (del* OP)) (add* OP)) )))


;;; ==============================

(def +current-used-ops+ (atom []))

(defn use-ops [OP*]
  (reset! +current-used-ops+ OP*) )

(declare action?)

; to contextualize the 'achieve-all' function
(def ^:dynamic *achieve-all* achieve-all)

(defn gps-chap4-13
"General Problem Solver: from state S (init conditions), achieve goals G* (final conditions) using operators OP*."
  ; note that state is now required to be a sequence (and not a set) due to implementation choice.
  ([S G*]
   (gps-chap4-13 S G* @+current-used-ops+) )
  ([S G* OP*]
   (dbg :gps "START : GPS")
   (binding [*available-ops* OP*]
     (when-let [AA (*achieve-all* (add-front S [:start]) G* [])]
       (filter action? AA) ))))

(defn action?
"Is X something that is [:start] or [:executing ...] ?"
  [X]
  (or (= X [:start]) (executing? X)) )


(declare destination)

(defn find-path
"Search a maze for a path from start to end."
  [START END]
  (if-let [results (gps-chap4-13 [[:at START]] #{[:at END]})]
    (cons START (map destination (remove #(= [:start] %) results))) ))


(defn destination
"Find the Y in [:executing [:move :from X :to Y]]"
  [ACTION]
  (nth (second ACTION) 4) )

(declare orderings)

; to call 'achieve-all-with-orderings' instead of 'achieve-all' in gps-chap4-13,
; use (binding [*achieve-all* achieve-all-with-orderings] (gps-chap4-13 ...))
(defn achieve-all-with-orderings
"Achieve each goal, trying several orderings."
  [S G* G-STACK]
  (some #(achieve-all S % G-STACK) (orderings G*)) )


(defn orderings [L]
  (if (> (count L) 1)
    (list L (reverse L))
    (list L) ))

;;; p141

(defn appropriate-ops-chap4-14
"Return a list of appropriate operators for the goal G, sorted by the number of unfulfilled preconditions
 relative to the State S (ascending)."
  [G S]
  (sort-by (fn [OP] (count (filter #(not (ss/contains? S %)) (pre* OP))))
           (filter #(v1/appropriate? G %) *available-ops*) ))















