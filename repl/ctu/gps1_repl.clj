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

; some results :
; [:give-shop-money :look-up-number :telephone-shop :tell-shop-problem :shop-installs-battery :drive-son-to-school]
; [:look-up-number :telephone-shop :tell-shop-problem :give-shop-money :shop-installs-battery :drive-son-to-school]

; with GUI
(inspect-tree (deref C))

; 4.7 : The Clobbered Sibling Goal Problem

(let [gps-to (timeout-fn gps 3000)
      IS     #{:son-at-home :car-works :have-money}
      GS     #{:have-money :son-at-school}
      OP*1   +school-ops+ ]
  (gps-to IS GS OP*1 C) )


; below we see that this implementation works out of the box. It is because algorithm is more general and so
; it is simpler. We then have less errors. The drawback is that it is not optimized. We will see later that
; optimization will be handled by the system itself and not by developpers.

(let [gps-to (timeout-fn gps 3000)
      IS     #{:son-at-home :car-needs-battery :have-money :have-phone-book}
      GS     #{:have-money :son-at-school}
      OP*1   +school-ops+ ]
  (gps-to IS GS OP*1 C) )

(inspect-tree (deref C))







