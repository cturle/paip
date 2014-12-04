(ns ctu.heuristic-search
  (:require [ctu.core :refer :all]) )

; The problem space is searched to accomplish the task to construct the symbolic object that is desired.

; acts of deliberation :
; select a problem space to work in, to select a state within that problem space (if more than one is available),
; to select an operator of the space to apply to the selected state, and then to apply that operator to the state
; to take a step in the space.

(declare init-context get-solution search-status? choose-next-node-and-operator apply-operator-on-node)

(defn solve-by-heuristic-search
  "Contextual. Find-Node-of-Generative-Graph-Problem PB -> Boolean
   Solve PB.
   Returns false if no solution is found.
   Returns true if a solution N1 is found.
   In this case, (= (cget PB :solution) N1) and (= (cget-v PB :op-v) OP*)
   where OP* are the operations to apply in order from N0 to reach N1.
   where N0 is the initial Node. (= N0 (cget (cget (cget PB :graph) :gen-def) :start-node))
   Used algorithm is :
    (while solution-not-found
       choose-next-node-and-operator
       apply-operator-on-node )"
  [PB]
  (loop [State :init, Args {}]
    (when (Thread/interrupted)
      (throw (ex-info "interruption in solve-by-heuristic-search" {:type :interruption, :state State, :Args Args})) )
    (case State
      :init    (do (init-context PB)
                   (recur :check Args) )
      :check   (do (if-let [N (get-solution PB)]
                     (recur :path (assoc Args :solution N))
                     (recur :choose Args) ))
      :choose  (do (let [R (choose-next-node-and-operator PB)]
                     (if-not (:success R)
                       (let [nPBpps (assoc (cget PB) :state :end-because-choose-failed)]
                         (swap! *context* assoc PB nPBpps)
                         false )
                       (recur :apply (merge Args (select-keys R [:operator :node]))) )))
      :apply   (do (apply-operator-on-node PB (:operator Args) (:node Args))
                   (recur :check Args) )
      :path    (do (let [OA-v    (cget-v PB :op-apply-v)
                         N0      (cget PB :start-node)
                         Nn      (:solution Args)
                         RP      (choice-v Nn
                                           (fn [S]   (first (filter #(= (cget % :out) S) OA-v)))
                                           (fn [S C] (cget C :node))
                                           (fn [S]   (= S N0)) )
                         nPBpps  (assoc (cget PB) :op-v  (mapv #(cget % :op) (rseq RP))
                                                  :state :solved )]
                     (swap! *context* assoc PB nPBpps) )
                     true ))))


(defn init-context
  "Add elaboration informations from the init state."
  [PB]
  (let [G   (cget PB :graph)
        N0  (cget (cget G :gen-def) :start-node)
        NG  (merge (cget G)  {:node* #{N0}})
        NPB (merge (cget PB) {:op-apply-v [], :start-node N0, :state :searching}) ]
    (swap! *context* merge {PB NPB, G NG}) ))

(defn get-solution
  "Returns one node N s.t. ((:pred PB) N) else returns nil.
   Memorize solution in PB"
  [PB]
  (if-let [S (cget PB :solution)]
    S
    (let [S (first (for [N (cget* (cget PB :graph) :node*) :when ((cget PB :pred) N)]
                        N ))]
      (when S
        (swap! *context* assoc-in [PB :solution] S) )
        S )))


(declare potential-node-operator-v)

(defn choose-next-node-and-operator
  "Returns {:success true, :node N, :operator OP} where N, OP are the best candidate to apply next
   Returns {:success false} if no candidate can be found."
  [PB]
  (let [NO-v (potential-node-operator-v PB)]
    (if (empty? NO-v)
      {:success false}
      (let [[N, O] (first NO-v)]
        {:success true, :node N, :operator O} ))))


(defn potential-node-operator-v
  "PB -> list-of [Node N, Operator O].
   (set-of [N (node* (graph PB)), O (domain-op* (gen-def (graph PB)))
            :| (and ((apply-op-pre? (gen-def (graph PB))) N O)
                    (empty? (set-of [A (op-apply-v PB) :| (and (= (node A) N) (= (op A) O))] A)) )]
     [N, O] )"
  [PB]
  (let [G  (cget PB :graph)
        D  (cget G :gen-def)]
    (for [N (cget* G :node*), O (cget* D :domain-op*)
          :when (and ((cget D :apply-op-pre?) N O)
                     (empty? (for [A (cget-v PB :op-apply-v)
                                   :when (and (= (cget A :node) N) (= (cget A :op) O))]
                               A )))]
      [N, O] )))

(declare find-node add-node add-op-apply)

(defn apply-operator-on-node
  "Apply Operator O on Node N. Add the resulting Node in the graph if it does not already exist.
  Memorize this action."
  [PB O N]
  (let [G     (cget PB :graph)
        N2PPs ((cget (cget G :gen-def) :apply-op) N O)
        N2    (or (find-node G N2PPs) (add-node G N2PPs))
        OA    (add-op-apply PB {:node N, :op O, :out N2})]
    {:success true, :op-apply OA}
    ))

(defn find-node
  "[Graph G, NodeProperties NPPs] -> (or false Node N)
  Returns (one-of [N (node* G) :| (every [[PP V] NPPs] (= (cget N PP) V))] N) if it exists else it returns false."
  [G NPPs]
  (if-let [N (first (for [N (cget G :node*)
                          :when (every? (fn [[PP V]] (= (cget N PP) V)) NPPs)]
                      N ))]
    N
    false ))

(defn add-node
  "[Graph G, NodeProperties NPPs] -> Node N
  N is a new Node added to the Graph G."
  [G NPPs]
  (let [N   (gensym "Node-"),
        NN  (assoc NPPs :isa :Node, :ref N)
        NG  (update-in (cget G) [:node*] conj N) ]
    (swap! *context* assoc G NG, N NN)
    N ))

(defn add-op-apply
  "[Problem PB, OpApplyProperties OAPPs] -> Op-Apply OA
  OA is a new Op-Apply "
  [PB OAPPs]
  (let [OA  (gensym "OpApply-")
        NOA (merge OAPPs {:isa :Op-Apply, :ref OA})
        NPB (update-in (cget PB) [:op-apply-v] conj OA) ]
    (swap! *context* assoc PB NPB, OA NOA)
    OA ))

(defn op-apply-v [PB] (cget-v PB :op-apply-v))

(defn get-pb-id
"get the problem id of a current *context*."
  []
  (some (fn [[K V]] (when (= :Find-Node-of-Generative-Graph-Problem (get V :isa)) K)) @*context*) )

