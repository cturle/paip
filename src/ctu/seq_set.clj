(ns ctu.seq-set
  (require [clojure.core :exclude [contains? conj disj]]) )

; === set functions on sequence ===
; why not to use clojure set directly ?
; => because this implementation preserves order across clojure set implementation.
;    this way, sets are determinist. cool for tests and check different orders.

(declare contains? disj conj union intersection difference subset? superset?)

(defn contains?
"Returns X if S contains X, else returns nil."
  [S X]
  (some #{X} S) )

(defn disj
  "Returns S in the same order without X"
  [S X]
  (remove (partial = X) S) )

(defn conj
  "Returns (clojure.core/conj S X) if (contains? S X), else returns S."
  [S X]
  (if (contains? S X) S (clojure.core/conj S X)) )

(defn union
"Returns set union of S1 and S2.
 Order = S1 then S2 elements not in S1"
  [S1 S2]
  (concat S1 (remove (set S1) S2)) )

(defn intersection
"Returns set intersection of S1 and S2.
 Order = S1 in S2"
  [S1 S2]
  (filter (set S2) S1) )

(defn difference
"Returns set difference of S1 minus S2.
 Order = S1 not in S2"
 [S1 S2]
 (remove (set S2) S1)
  )

(defn subset?
"Returns true if S1 is a subset of S2"
  [S1 S2]
  (every? (set S2) S1)
  )

(defn superset?
"Returns true if S1 is a superset of S2"
  [S1 S2]
  (every? (set S1) S2)
  )

























