;;;; File repl/paip/gps1_repl.clj

(ns paip.gps1-repl
  (:require [ctu.core :refer :all]
            [paip.core :refer :all]
            [paip.gps1 :refer :all]
            [clojure.tools.trace :refer :all]
            [clojure.repl :refer :all] ))


;;; 4.4 Stage 4: Test

(gps #{:son-at-home :car-needs-battery :have-money :have-phone-book}
     #{:son-at-school}
     +school-ops+ )

(gps #{:son-at-home :car-needs-battery :have-money}
     #{:son-at-school}
     +school-ops+ )

(gps #{:son-at-home :car-works}
     #{:son-at-school}
     +school-ops+ )


;;; 4.7 The Clobbered Sibling Goal Problem

(gps #{:son-at-home :have-money :car-works}
     #{:have-money :son-at-school}
     +school-ops+ )

(gps #{:son-at-home :car-needs-battery :have-money :have-phone-book}
     #{:have-money :son-at-school}
     +school-ops+ )


;;; 4.8 The Leaping before You Look Problem

;; we have to give goals as a sequence to check order impact
(gps #{:son-at-home :car-needs-battery :have-money :have-phone-book}
     [:son-at-school :have-money]
     +school-ops+ )


;;; 4.9 The Recursive Subgoal Problem

(trace-vars achieve)

;; operators are now a list since order is important for this test
;; using timeout-fn (5 milli-seconds) prevents the stack overflow
;; check the console to see traces
((timeout-fn gps 5)
     #{:son-at-home :car-needs-battery :have-money}
     #{:son-at-school}
     (list* {:action :ask-phone-number, :preconds #{:in-communication-with-shop}, :add-list #{:know-phone-number}}
            +school-ops+ ))

(untrace-vars achieve)






























