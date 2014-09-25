;;;; File repl/paip/gps1_repl.clj

(ns paip.gps2-repl
  (:require [ctu.core :refer :all]
            [paip.core :refer :all]
            [paip.gps2 :refer :all]
            [clojure.tools.trace :refer :all]
            [clojure.repl :refer :all] ))


;;; 4.11 GPS Version 2: A More General Problem Solver

(debug :gps)

((timeout-fn gps 500)
      [:son-at-home :car-needs-battery :have-money :have-phone-book]
      [:son-at-school]
      +school-ops+ )

((timeout-fn gps 500)
      [:son-at-home :car-works]
      [:son-at-school]
      +school-ops+ )

((timeout-fn gps 500)
      [:son-at-home :car-needs-battery :have-money :have-phone-book]
      [:have-money :son-at-school]
      +school-ops+ )

((timeout-fn gps 500)
      [:son-at-home :car-needs-battery :have-money :have-phone-book]
      [:son-at-school :have-money]
      +school-ops+ )

((timeout-fn gps 500)
      [:son-at-home :car-needs-battery :have-money]
      [:son-at-school]
      +school-ops+ )

((timeout-fn gps 500)
      [:son-at-home]
      [:son-at-home]
      +school-ops+ )





























