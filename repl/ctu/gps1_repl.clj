(ns ctu.gps1-repl
  (:require [clojure.tools.trace :refer :all]
            [clojure.repl :refer :all]
            [clojure.inspector :refer :all]
            [ctu.core :refer :all]
            [ctu.display :refer :all]
            [ctu.heuristic-search :refer :all]
            [ctu.gps1 :refer :all]
            [ctu.gps1-test :refer :all]
            [paip.gps1 :refer [op-name pre* add* del*]]
            [paip.dom-bananas], [paip.dom-maze] ))

; we define a global context here.
(def C (atom nil))

(let [GPS    (timeout-fn gps 3000)
      IS     [:son-at-home :car-needs-battery :have-money :have-phone-book]
      GS     [:son-at-school]
      OP*1   +school-ops+ ]
  (GPS IS GS OP*1 C) )

; some results :
; [:give-shop-money :look-up-number :telephone-shop :tell-shop-problem :shop-installs-battery :drive-son-to-school]
; [:look-up-number :telephone-shop :tell-shop-problem :give-shop-money :shop-installs-battery :drive-son-to-school]

; with GUI
(inspect-tree (deref C))

; 4.7 : The Clobbered Sibling Goal Problem

(let [GPS (timeout-fn gps 3000)
      IS     #{:son-at-home :car-works :have-money}
      GS     #{:have-money :son-at-school}
      OP*1   +school-ops+ ]
  (GPS IS GS OP*1 C) )


; below we see that this implementation works out of the box. It is because algorithm is more general and so
; it is simpler. We then have less errors. The drawback is that it is not optimized. We will see later that
; optimization will be handled by the system itself and not by developpers.

(let [GPS (timeout-fn gps 3000)
      IS     #{:son-at-home :car-needs-battery :have-money :have-phone-book}
      GS     #{:have-money :son-at-school}
      OP*1   +school-ops+ ]
  (GPS IS GS OP*1 C) )

(inspect-tree (deref C))


; 4.12 The New Domain Problem: Monkey and Bananas

(let [GPS (timeout-fn gps 3000)
      IS     #{:at-door :on-floor :has-ball :hungry :chair-at-door}
      GS     #{:not-hungry}
      OP*1   paip.dom-bananas/+available-ops+ ]
  (GPS IS GS OP*1 C) )

(inspect-tree (deref C))


; 4.13 The Maze Searching Domain

(let [GPS (timeout-fn gps 1000)
      IS     #{[:at 1]}
      GS     #{[:at 25]}
      OP*1   paip.dom-maze/+available-ops+ ]
  (GPS IS GS OP*1 C) )

(inspect-tree (deref C))

(declare destination)

(defn find-path
"Search a maze for a path from start to end."
  [START END]
  (let [IS      #{[:at START]}
        GS      #{[:at END]}
        OP*1    paip.dom-maze/+available-ops+ ]
    (if-let [results (gps IS GS OP*1 C)]
      (cons START (map destination results)) )))

(defn destination
"Find the Y in [:executing [:move :from X :to Y]]"
  [ACTION]
  (nth ACTION 4) )

((timeout-fn find-path 1000) 1 25)
((timeout-fn find-path 1000) 1 1)
(= ((timeout-fn find-path 1000) 1 25) (reverse ((timeout-fn find-path 1000) 25 1)))


; 4·14 The Blocks World Domain

(let [GPS (timeout-fn gps 1000)
      IS     #{[:a :on :table] [:b :on :table] [:space :on :a] [:space :on :b] [:space :on :table]}
      GS     #{[:a :on :b] [:b :on :table]}
      OP*1   (paip.dom-blocks/make-block-ops #{:a :b}) ]
  (GPS IS GS OP*1 C) )

(inspect-tree (deref C))

(let [GPS (timeout-fn gps 1000)
      IS     #{[:a :on :b] [:b :on :table] [:space :on :a] [:space :on :table]}
      GS     #{[:b :on :a]}
      OP*1   (paip.dom-blocks/make-block-ops #{:a :b}) ]
  (GPS IS GS OP*1 C) )

; returns nil !
; why ? because it timed out.
; why ? because inappropriate operators are choosen.
; Ex : [:move c :from a :to b] to achieve the goal [c :on b]
(let [GPS (timeout-fn gps 1000)
      IS     #{[:a :on :b] [:b :on :c] [:c :on :table] [:space :on :a] [:space :on :table]}
      GS     #{[:b :on :a] [:c :on :b]}
      OP*1   (paip.dom-blocks/make-block-ops #{:a :b :c}) ]
  (GPS IS GS OP*1 C) )

; if you try with a time out of 10 minutes, it's ok.
(let [GPS (timeout-fn gps (* 10 60 1000))
      IS     #{[:a :on :b] [:b :on :c] [:c :on :table] [:space :on :a] [:space :on :table]}
      GS     #{[:b :on :a] [:c :on :b]}
      OP*1   (paip.dom-blocks/make-block-ops #{:a :b :c}) ]
  (GPS IS GS OP*1 C) )

; => 1181 operators are considered to find the solution
(binding [*context* C] (count (cget-v (get-pb-id) :op-apply-v)))

; you may inspect them
(binding [*context* C] (inspect-tree (map op-apply-view (op-apply-v (get-pb-id)))))

; a first problem : the goal should be full represented to have less redondant states.
(let [GPS (timeout-fn gps (* 60 1000))
      IS     #{[:space :on :a] [:a :on :b] [:b :on :c] [:c :on :table] [:space :on :table]}
      GS     #{[:space :on :c] [:c :on :b] [:b :on :a] [:a :on :table] [:space :on :table]}
      OP*1   (paip.dom-blocks/make-block-ops #{:a :b :c}) ]
  (GPS IS GS OP*1 C) )


; forward search
; only coherent states are tested, so search space is smaller and computation finishes within time limit.
(let [GPS (timeout-fn gps-forward 1000)
      IS     #{[:a :on :b] [:b :on :c] [:c :on :table] [:space :on :a] [:space :on :table]}
      GS     #{[:b :on :a] [:c :on :b]}
      OP*1   (paip.dom-blocks/make-block-ops #{:a :b :c}) ]
  (GPS IS GS OP*1 C) )




