(ns ctu.core)


(def +out+ *out*)
(defn dprintln [& more] (binding [*out* +out+] (apply println more)))
(defn dprint   [& more] (binding [*out* +out+] (apply print more)))

(def ^:dynamic *context* (atom {}))

(defn cget
"Any CM -> (or nil Map).
  Returns the map attached to CM in the Context *context*, or nil if not found.
 [Any CM, Any K] -> (or nil Any).
  Returns the value mapped to key K in the contextual map CM. nil is returned as default-value.
 Throw an exception if CM is not found."
 ([CM]
  (if-let [M (get (deref *context*) CM)] M (throw (Exception. (str "cget: concept not found : " CM)))) )
 ([CM K]
  (get (cget CM) K) ))

(defn cget*
"[Any CM, Any K] -> Set
 Returns the value mapped to key K in the contextual map CM. #{} is returned as default-value.
 Throw an exception if CM is not found."
 [CM K]
 (get (cget CM) K #{}) )

(defn cget-v
"[Any CM, Any K] -> Vector
 Returns the value mapped to key K in the contextual map CM. [] is returned as default-value.
 Throw an exception if CM is not found."
 [CM K]
 (get (cget CM) K []) )


; Return = {:all-pre-ok? Boolean, :out Any, :bad-pre Any}

(defn reduce-pre
  "[([Any, Any] -> Any) F, ([Any, Any] -> Boolean) F-PRE?] -> ([Any INIT, List L] -> Return R) F2
  (:out R) = as (reduce F INIT L) if each intermediate call to F-PRE? returns true.
  (:all-pre-ok? R) = (every? [TV (intermediate-calls to F-PRE?)] (true? TV)"
  [F F-PRE?]
  (fn [INIT L]
    (loop [X INIT, L L]
      (when (.. Thread currentThread isInterrupted)
        (throw (ex-info "interruption in reduce-pre" {:type :interruption, :X X :L L})) )
      (if (empty? L) {:all-pre-ok? true, :out X}
        (let [Y (first L)]
          (if (not (F-PRE? X Y)) {:all-pre-ok? false, :bad-pre [X, Y]}
            (recur (F X Y) (rest L)) ))))))


(defn choice-v
  "[State S0, (State -> Choice) S-C, ([State, Choice] -> State) SC-S, (State -> Boolean) FS?]
    -> vector-of Choice C-v
  Returns a sequence of choice from an initial State S0, a Choice chooser S-C, a new state builder SC-S and
  a final state predicate FS?.
  Interruptible."
  [S0, S-C, SC-S, FS?]
  (loop [S S0, C-v []]
    (when (.. Thread currentThread isInterrupted)
      (throw (ex-info "interruption in choice-v" {:type :interruption, :S S :C-v C-v})) )
    (if (FS? S)
      C-v
      (let [C  (S-C S)
            NS (SC-S S C)]
        (recur NS (conj C-v C)) ))))


(defn timeout-fn
  "[Function F, int TO] -> Function G.
   Returns a function G which is the same as F if it returns in less than TO milli-seconds else it returns nil.
   special variables are rebound when G is executed not defined."
  [F TO]
  (fn [& MORE]
    (let [FB (bound-fn* F)
          FU (future (apply FB MORE))
          R  (deref FU TO :timed-out) ]
      (if (= :timed-out R)
        (do (future-cancel FU) nil)
        R )
      )))





















