(ns ctu.core)


(def ^:dynamic *context* {})

(defn cget
"Any CM -> (or nil Map). Returns the map attached to CM in the context, or nil if not found.
 Throw an exception if CM is not found."
 [CM]
 (if-let [M (get *context* CM)] M (throw (str "cget: concept not found : " CM))) )

(defn cget*
"[Any CM, Any K] -> Set
 Returns the value mapped to key K in the contextual map CM. #{} is returned as default-value.
 Throw an exception if CM is not found."
 [CM K]
 (get (cget CM) K #{}) )


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




