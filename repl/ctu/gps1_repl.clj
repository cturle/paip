(ns ctu.gps1-repl
  (:require [clojure.tools.trace :refer :all]
            [clojure.repl :refer :all]
            [clojure.inspector :refer :all]
            [ctu.core :refer :all]
            [ctu.gps1 :refer :all]
            [ctu.gps1-test :refer :all]))


(def C (atom nil))

(let [gps-to (timeout-fn gps 3000)
      IS     #{:son-at-home :car-needs-battery :have-money :have-phone-book}
      GS     #{:son-at-school}
      OP*1   +school-ops+ ]
  (gps-to IS GS OP*1 C) )


; with GUI
(inspect-tree (deref C))






