;;;; File gps1.clj: First version of GPS (General Problem Solver)
;;;  Inspiration from Paradigms of Artificial Intelligence Programming

(ns paip.gsp1
  (:require [clojure.tools.trace :refer :all]
            [clojure.repl :refer :all] ))


; === § 4.1 => §4.4 : GPS1 from Book

(declare gps achieve appropriate? apply-op)

(def +state+
  "The current state: a set of conditions."
  (atom #{}) )

(def +ops+
  "A set of available operators."
  (atom #{}) )

(defn gps
  "General Problem Solver: achieve all goals G* using operators O* from starting conditions SC*."
  [SC* G* O*]
  (reset! +state+ SC*)
  (reset! +ops+ O*)
  (if (every? achieve G*) :solved) )


(defn achieve
  "A goal G is achieved if it already holds,
  or if there is an appropriate op for it that is applicable."
  [G]
  (or (contains? @+state+ G)
      (some apply-op
            (for [?op @+ops+ :when (appropriate? G ?op)] ?op) )))


(defn appropriate?
  "An OP is appropriate to a goal G if it is in its add list."
  [G OP]
  (contains? (get OP :add-list #{}) G) )


(defn apply-op
  "Print a message and update State if OP is applicable."
  [OP]
  (when (every? achieve (get OP :preconds #{}))
    (println (list 'executing (get OP :action)))
    (swap! +state+ clojure.set/difference (get OP :del-list #{}))
    (swap! +state+ clojure.set/union (get OP :add-list))
    true ))


;;; ==============================

(def School-ops
  #{{:action   :drive-son-to-school
     :preconds #{:son-at-home :car-works}
     :add-list #{:son-at-school}
     :del-list #{:son-at-home}
     }
    {:action   :shop-installs-battery
     :preconds #{:car-needs-battery :shop-knows-problem :shop-has-money}
     :add-list #{:car-works}
     }
   {:action   :tell-shop-problem
    :preconds #{:in-communication-with-shop}
    :add-list #{:shop-knows-problem}
    }
   {:action   :telephone-shop
    :preconds #{:know-phone-number}
    :add-list #{:in-communication-with-shop}
    }
   {:action   :look-up-number
    :preconds #{:have-phone-book}
    :add-list #{:know-phone-number}
    }
   {:action   :give-shop-money
    :preconds #{:have-money}
    :add-list #{:shop-has-money}
    :del-list #{:have-money}
    } })


(comment

  (gps #{:son-at-home :car-needs-battery :have-money :have-phone-book}
       #{:son-at-school}
       School-ops )

  (gps #{:son-at-home :car-needs-battery :have-money}
       #{:son-at-school}
       School-ops )

  (gps #{:son-at-home :car-works}
       #{:son-at-school}
       School-ops )
)


