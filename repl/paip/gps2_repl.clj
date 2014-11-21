;;;; File repl/paip/gps1_repl.clj

(ns paip.gps2-repl
  (:require [clojure.inspector :refer :all]
            [ctu.core :refer :all]
            [paip.core :refer :all]
            [paip.gps1 :refer [*available-ops* op-name add* del* pre*]]
            [paip.gps2 :refer :all]
            [paip.dom-school], [paip.dom-bananas], [paip.dom-maze], [paip.dom-blocks]
            [clojure.tools.trace :refer :all]
            [clojure.repl :refer :all] ))

;;; 4.11 GPS Version 2: A More General Problem Solver

(def +paip-school-ops+ (map convert-op paip.dom-school/+available-ops+))

((timeout-fn gps 500)
      [:son-at-home :car-needs-battery :have-money :have-phone-book]
      [:son-at-school]
      +paip-school-ops+ )

(debug :gps)

((timeout-fn gps 500)
      [:son-at-home :car-needs-battery :have-money :have-phone-book]
      [:son-at-school]
      +paip-school-ops+ )

(undebug)

((timeout-fn gps 500)
      [:son-at-home :car-works]
      [:son-at-school]
      +paip-school-ops+ )

((timeout-fn gps 500)
      [:son-at-home :car-needs-battery :have-money :have-phone-book]
      [:have-money :son-at-school]
      +paip-school-ops+ )

((timeout-fn gps 500)
      [:son-at-home :car-needs-battery :have-money :have-phone-book]
      [:son-at-school :have-money]
      +paip-school-ops+ )

((timeout-fn gps 500)
      [:son-at-home :car-needs-battery :have-money]
      [:son-at-school]
      +paip-school-ops+ )

((timeout-fn gps 500)
      [:son-at-home]
      [:son-at-home]
      +paip-school-ops+ )


;;; 4.12 The New Domain Problem: Monkey and Bananas

(def +paip-bananas-ops+ (map convert-op paip.dom-bananas/+available-ops+))

((timeout-fn gps 500)
      [:at-door :on-floor :has-ball :hungry :chair-at-door]
      [:not-hungry]
      +paip-bananas-ops+ )


;;; 4.13 The Maze Searching Domain

(def +paip-maze-ops+ (map convert-op paip.dom-maze/+available-ops+))

((timeout-fn gps 500) [[:at 1]] [[:at 25]] +paip-maze-ops+)

((timeout-fn gps-chap4-13 500) [[:at 1]] [[:at 25]] +paip-maze-ops+)


;;; 4.14 The Blocks World Domain

(use-ops (map convert-op (paip.dom-blocks/make-block-ops [:a :b])))

(inspect-tree @+current-used-ops+)

((timeout-fn gps-chap4-13 1000) [[:a :on :table] [:b :on :table] [:space :on :a] [:space :on :b] [:space :on :table]]
                                [[:a :on :b] [:b :on :table]] )

(debug :gps)

((timeout-fn gps-chap4-13 1000) [[:a :on :b] [:b :on :table] [:space :on :a] [:space :on :table]]
                                [[:b :on :a]] )

(undebug)

(use-ops (map convert-op (paip.dom-blocks/make-block-ops [:a :b :c])))

((timeout-fn gps-chap4-13 1000) [[:a :on :b] [:b :on :c] [:c :on :table] [:space :on :a] [:space :on :table]]
                                [[:b :on :a] [:c :on :b]] )


((timeout-fn gps-chap4-13 1000) [[:a :on :b] [:b :on :c] [:c :on :table] [:space :on :a] [:space :on :table]]
                                [[:c :on :b] [:b :on :a]] )


;;; p139

(binding [*achieve-all* achieve-all]
  ((timeout-fn gps-chap4-13 1000) [[:a :on :b] [:b :on :c] [:c :on :table] [:space :on :a] [:space :on :table]]
                                  [[:c :on :b] [:b :on :a]] ))

(binding [*achieve-all* achieve-all-with-orderings]
  ((timeout-fn gps-chap4-13 1000) [[:a :on :b] [:b :on :c] [:c :on :table] [:space :on :a] [:space :on :table]]
                                  [[:c :on :b] [:b :on :a]] ))

;;; p140 : the efficiency of solutions

(binding [*achieve-all* achieve-all-with-orderings]
  ((timeout-fn gps-chap4-13 1000) [[:c :on :a] [:a :on :table] [:b :on :table] [:space :on :c] [:space :on :b] [:space :on :table]]
                                  [[:c :on :table]] ))

;
(binding [*achieve-all* achieve-all-with-orderings]
  ((timeout-fn gps-chap4-13 1000) [[:c :on :a] [:a :on :table] [:b :on :table] [:space :on :c] [:space :on :b] [:space :on :table]]
                                  [[:c :on :table] [:a :on :b]] ))


(binding [*achieve-all*      achieve-all-with-orderings
          *appropriate-ops*  appropriate-ops-chap4-14 ]
  ((timeout-fn gps-chap4-13 1000) [[:c :on :a] [:a :on :table] [:b :on :table] [:space :on :c] [:space :on :b] [:space :on :table]]
                                  [[:c :on :table] [:a :on :b]] ))

(binding [*achieve-all*      achieve-all-with-orderings
          *appropriate-ops*  appropriate-ops-chap4-14 ]
  ((timeout-fn gps-chap4-13 1000) [[:a :on :b] [:b :on :c] [:c :on :table] [:space :on :a] [:space :on :table]]
                                  [[:b :on :a] [:c :on :b]] ))

(binding [*achieve-all*      achieve-all-with-orderings
          *appropriate-ops*  appropriate-ops-chap4-14 ]
  ((timeout-fn gps-chap4-13 1000) [[:a :on :b] [:b :on :c] [:c :on :table] [:space :on :a] [:space :on :table]]
                                  [[:c :on :b] [:b :on :a]] ))


; The Sussman Anomaly

(def start [[:c :on :a] [:a :on :table] [:b :on :table] [:space :on :c] [:space :on :b] [:space :on :table]])

(debug :gps)

(binding [*achieve-all*      achieve-all-with-orderings
          *appropriate-ops*  appropriate-ops-chap4-14 ]
  ((timeout-fn gps-chap4-13 1000) start
                                  [[:a :on :b] [:b :on :c]] ))

(binding [*achieve-all*      achieve-all-with-orderings
          *appropriate-ops*  appropriate-ops-chap4-14 ]
  ((timeout-fn gps-chap4-13 1000) start
                                  [[:b :on :c] [:a :on :b]] ))

(undebug)

; 4.16 The Not Looking after You Don't Leap Problem

(use-ops (add-front paip.dom-school/+available-ops+
                    {:action   :taxi-son-to-school
                     :preconds [:son-at-home :have-money]
                     :add-list [:son-at-school]
                     :del-list [:son-at-home :have-money] }))

(debug :gps)

(binding [*achieve-all*      achieve-all-with-orderings
          *appropriate-ops*  appropriate-ops-chap4-14 ]
  ((timeout-fn gps-chap4-13 1000) [:son-at-home :have-money :car-works]
                                  [:son-at-school :have-money] ))

; look at the console TAB to see trace

(undebug)




