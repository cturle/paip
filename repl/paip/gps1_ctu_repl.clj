;;;; File gps1_ctu_repl.clj

(ns paip.gps1-ctu-repl
  (:require [paip.gps1-ctu :refer :all]
            [clojure.tools.trace :refer :all]
            [clojure.repl :refer :all]
            [clojure.set :as set] ))



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


