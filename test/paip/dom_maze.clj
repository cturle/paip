(ns paip.dom-maze)

(declare make-maze-ops make-maze-op)

(defn make-maze-ops
"Make maze ops in both directions"
  [[C1 C2]]
  [(make-maze-op C1 C2) (make-maze-op C2 C1)] )

(defn make-maze-op
"Make an operator to move between two places"
  [HERE THERE]
  {:action    [:move, :from HERE, :to THERE]
   :preconds  [[:at HERE]]
   :add-list  [[:at THERE]]
   :del-list  [[:at HERE]] })


(def +available-ops+
  (mapcat make-maze-ops
  '[[1 2]   [2 3]   [3 4]   [4 9]   [9 14]  [9 8]   [8 7] [7 12] [12 13]
    [12 11] [11 6]  [11 16] [16 17] [17 22] [21 22] [22 23]
    [23 18] [23 24] [24 19] [19 20] [20 15] [15 10] [10 5] [20 25] ]))




