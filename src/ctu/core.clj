(ns ctu.core)

; Return = {:all-pre-ok? Boolean, :out Any, :bad-pre Any}

(defn reduce-pre
  "[([Any, Any] -> Any) F, ([Any, Any] -> Boolean) F-PRE?] -> ([Any INIT, List L] -> Return R) F2
  (:out R) = as (reduce F INIT L) if each intermediate call to F-PRE? returns true.
  (:all-pre-ok? R) = (every? [TV (intermediate-calls to F-PRE?)] (true? TV)"
  [F F-PRE?]
  (fn [INIT L]
    (loop [X INIT, L L]
      (if (empty? L) {:all-pre-ok? true, :out X}
        (let [Y (first L)]
          (if (not (F-PRE? X Y)) {:all-pre-ok? false, :bad-pre [X, Y]}
            (recur (F X Y) (rest L)) ))))))




