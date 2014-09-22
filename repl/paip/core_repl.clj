(ns paip.core-repl
  (require [paip.core :refer :all]
           [clojure.pprint :refer :all]))


(undebug)
(dbg :dbg "ne doit pas s'afficher")
(debug :dbg)
(dbg :dbg  ":dbg doit s'afficher")
(dbg :dbg1 ":dbg1 ne doit pas s'afficher")
(dbg-indent :dbg  5 ":dbg doit s'afficher avec une indentation de 5 caractères")
(dbg-indent :dbg1 5 ":dbg1 ne doit pas s'afficher")
(undebug)
(dbg-indent :dbg  5 ":dbg ne doit plus s'afficher")

