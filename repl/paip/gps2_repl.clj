;;;; File repl/paip/gps1_repl.clj

(ns paip.gps2-repl
  (:require [ctu.core :refer :all]
            [paip.core :refer :all]
            [paip.gps2 :refer :all]
            [paip.dom-bananas]
            [clojure.tools.trace :refer :all]
            [clojure.repl :refer :all] ))


;;; 4.11 GPS Version 2: A More General Problem Solver

(debug :gps)

((timeout-fn gps 500)
      [:son-at-home :car-needs-battery :have-money :have-phone-book]
      #{:son-at-school}
      +paip-school-ops+ )

((timeout-fn gps 500)
      [:son-at-home :car-works]
      #{:son-at-school}
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
      #{:son-at-school}
      +paip-school-ops+ )

((timeout-fn gps 500)
      [:son-at-home]
      #{:son-at-home}
      +paip-school-ops+ )


;;; 4.12 The New Domain Problem: Monkey and Bananas

(def +paip-bananas-ops+ (set (map convert-op paip.dom-bananas/+available-ops+)))

((timeout-fn gps 500)
      [:at-door :on-floor :has-ball :hungry :chair-at-door]
      #{:not-hungry}
      +paip-bananas-ops+ )


























