;;;; File repl/paip/gps1_repl.clj

(ns paip.gps1-repl
  (:require  [clojure.tools.trace :refer :all]
             [clojure.repl :refer :all]
             [ctu.core :refer :all]
             [paip.core :refer :all]
             [paip.gps1 :refer :all]
             [paip.dom-school :as school]
             ))


;;; 4.4 Stage 4: Test

(gps [:son-at-home :car-needs-battery :have-money :have-phone-book]
     [:son-at-school]
     school/+available-ops+ )

(gps [:son-at-home :car-needs-battery :have-money]
     [:son-at-school]
     school/+available-ops+ )

(gps [:son-at-home :car-works]
     [:son-at-school]
     school/+available-ops+ )


;;; 4.7 The Clobbered Sibling Goal Problem

(gps [:son-at-home :have-money :car-works]
     [:have-money :son-at-school]
     school/+available-ops+ )

(gps [:son-at-home :car-needs-battery :have-money :have-phone-book]
     [:have-money :son-at-school]
     school/+available-ops+ )


;;; 4.8 The Leaping before You Look Problem

(gps [:son-at-home :car-needs-battery :have-money :have-phone-book]
     [:son-at-school :have-money]
     school/+available-ops+ )


;;; 4.9 The Recursive Subgoal Problem

(trace-vars achieve)

;; using timeout-fn (15 milli-seconds) prevents the stack overflow. You may adjust this value.
;; check the console to see traces
((timeout-fn gps 5)
     [:son-at-home :car-needs-battery :have-money]
     [:son-at-school]
     (cons  {:action    :ask-phone-number
             :preconds  [:in-communication-with-shop]
             :add-list  [:know-phone-number] }
            school/+available-ops+ ))

(untrace-vars achieve)






























