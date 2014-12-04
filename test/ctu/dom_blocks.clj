(ns ctu.dom-blocks
  (require [ctu.core :refer :all]
           [ctu.seq-set :as ss] ))


; == interaction fictive sur la formulation du type de problème au système ==

; > (new-interaction)
; I create a new interaction (:id = 1)
; which :type of interaction ? (:def-problem-type, :def-problem ...)
; useful informations you can set about (interaction :id 1) are a ':name' and a ':description'.
; > (set :about 1 :subject :def-a-problem-type :name "def block-world" :description "define the block world domain")


; definir les concepts du :problem-type
; objects   = [:table]
; types     = [:Cube, :Place]
; :Cube is :primitive
; :Place is (or (a :Cube) :table)
; relations = [:Position]
; :Position, :representation = [(a :Cube) :on (a :Place)]
; state-description = (set-of :Position)
; state-constraints = (fn [?S] (every? P (All Cube)), (= 1 (card (filter-match [?C :on ?] Position(S))))
; operators = [:Move-op]
; :Move-op, :representation = [:move (a :Cube ?C) :on (a :Place ?P2)]
; :Move-op, :pre-state-pattern = ~[? :on ?C], [?C :on ?P1], ~[? :on ?P2]
; :Move-op, :post-conditions = ~[?C :on ?P1], [?C :on ?P2]


;; define the block world domain
; (set-ctxt)
;  (declare le Problem-domain Block-world-pb)
;  (set-ctxt Block-world-pb)
;   (declare le Object table)
;   (declare les Types [Cube Place Position Operator])
;   (set-ctxt Place)
;    (set domain (or (a Cube) table))
;   (set-ctxt [Block-world-pb, Position])
;    (set pattern [(a Cube) :on (a Place)])
;   (set-ctxt [Block-world-pb, State])
;    (set domain (set-of Position))
;    (set ...)
;   (set-ctxt [Block-world-pb, Operator])
;    (set pattern [:move (a Cube ?C) :on (a Place ?P2)])
;    (set pre-state-pattern [[nil :on ?C], [?C :on ?P1], [nil :on ?P2]]
;

;; define a new block world problem
; (set-ctxt top)
;  (declare le Block-world-pb BW1)
;  (set-ctxt BW1)
;   (declare les Cube [A B C])
;   (set the-start-state (les Position [[A :on B], [B :on C], [C :on table]])
;   (set the-goal-state  (les Position [[C :on B], [B :on A], [A :on table]])


;; General Domain
; or, [], a, set-of, les
; objects-of, types-of, primitive, relations, representation, def, pattern

;; Problem Domain
; State, state-description, state-constraints, operator, :pre-state-pattern, :post-conditions, start-state, goal-state

;; Block World Problem Domain
; Cube, table, Place, Position, Move-op


; Regle R1 :
(=>
 (and [isa ?S State]
      [isa ?C Cube] )
 (= 1 (card ((filter-kb [sur ?C ?]) (kb ?S)))) )

(declare (the Cube A))

; R1 se decline en R1-1:
(=>
 [isa ?S State]
 (= 1 (card ((filter-kb [sur A ?]) (kb ?S)))) )

(declare (the State S1))

; R1-1 se décline en R1-1-1 :
(= 1 (card ((filter-kb [sur A ?]) (kb S1))))

; R1 se décline en R1-2 :
(=>
 [isa ?C Cube]
 (= 1 (card ((filter-kb [sur ?C ?]) (kb S1)))) )

->
((partial = 1) (card ((filter-kb [sur ?C ?]) (kb S1))))
->
((comp (partial = 1) card (filter-kb [sur ?C ?]) kb) S1)


; si on demandait à simplifier maintenant :
(simplify S1)

; comme (kb S1) => :empty-set
(= 1 (card ((filter-kb [sur A ?]) :empty-set)))
; comme ((a filter-kb) :empty-set) => :empty-set
(= 1 (card :empty-set))
; comme (card :empty-set) => 0
(= 1 0)
; comme (= x y) avec x != y
false

; si on demandait à simplifier maintenant :
; now (kb S1) => {[sur A :table]}
(add S1 [sur A :table])

(simplify S1)
(= 1 (card ((filter-kb [sur A ?]) {[sur A :table]})))
(= 1 (card {[sur A :table]}))
(= 1 1)
true










































