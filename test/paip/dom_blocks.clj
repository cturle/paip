(ns paip.dom-blocks
  (require [ctu.core :refer :all]
           [ctu.seq-set :as ss] ))

(declare wo-space-on-table)

(defn move-op
"Make an operator to move A from Β to C . "
  [A B C]
  {:action    [:move A :from B :to C]
   :preconds  [[:space :on A] [:space :on C] [A :on B]]
   :del-list  (wo-space-on-table [[A :on B] [:space :on C]])
   :add-list  (wo-space-on-table [[A :on C] [:space :on B]]) })

; implementation a little different from the book.
; much understandable i hope.
(defn wo-space-on-table [C*]
"hypothesis : infinite space on table. So remove all conditions about this, no add, no del, just keep the fact."
  (ss/disj C* [:space :on :table]) )


; not an efficient implementation since lots of Z are tested with X = Y.
; but it is simpler to understand the meaning of this function.
(defn ctu-make-block-ops [B*]
"returns all move-op from blocks B*"
  (for [X B*, Y (conj B* :table), Z (conj B* :table), :when (all-distinct? [X Y Z])]
       (move-op X Y Z) ))

; paip version
(defn make-block-ops [B*]
"returns all move-op from blocks B* in the same order as in paip book."
  (with-local-vars [OP* '()]
    (doseq [X B*, Y B* :when (not= X Y)]
      (doseq [Z B* :when (and (not= X Z) (not= Y Z))]
        (var-set OP* (conj @OP* (move-op X Y Z))) )
      (var-set OP* (conj @OP* (move-op X :table Y)))
      (var-set OP* (conj @OP* (move-op X Y :table))) )
    @OP* ))







