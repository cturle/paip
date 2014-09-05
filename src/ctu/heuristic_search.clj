(ns ctu.heuristic-search
  (:require [ctu.core :refer :all]
            [ctu.graph :refer [find-path]]) )

(def +context+ (atom {}))

(declare init-context search-status? choose-next-node-and-operator apply-operator-on-node)

(defn solve-by-heuristic-search
  "solve a Search-Space-Problem PB with heuristic search.
   PB -> boolean
  Returns false if no solution is found.
  Returns true if a solution N1 is found.
   In this case, (= (cget PB :solution) N1) and (= (cget-v PB :op-v) OP*)
   where OP* are the operations to apply in order from N0 to reach N1.
   N0 is the initial Node : (= N0 :start-node (:gen-def (:graph PB)))
  Used algorithm is :
   (while solution-not-found
      choose-next-node-and-operator
      apply-operator-on-node )"
  [PB]
  (loop [State :init, Args {}]
    (reset! +context+ *context*) ; just to get infos after the run.
    (case State
      :init    (do (init-context PB)
                   (recur :check Args) )
      :check   (do (if-let [N (get-solution PB)]
                     (recur :path (assoc Args :solution N))
                     (recur :choose Args) ))
      :choose  (do (let [R (choose-next-node-and-operator PB)]
                     (if-not (:success R)
                       false
                       (recur :apply (merge Args (select-keys R [:operator :node]))) )))
      :apply   (do (let [R (apply-operator-on-node PB (:operator Args) (:node Args))]
                     (if-not (:success R)
                       false
                       (recur :check Args) )))
      :path    (do (let [G       (cget PB :graph)
                         P       (find-path G (cget (cget G :gen-def) :start-node) (:solution Args))
                         NPBPPS  (assoc (cget PB) :solution (:solution Args), :op-v P)]
                     (set! *context* (assoc *context* PB NPBPPs))
                     true )))))

(defn init-context
  "Add elaboration informations from the init state."
  [PB]
  (let [G   (cget PB :graph)
        N0  (cget (cget G :gen-def) :start-node)
        NG  (merge (cget G)  {:node* #{N0}})
        NPB (merge (cget PB) {:op-apply-v []}) ]
    (set! *context* (merge *context* {PB NPB, G NG}))
    true ))

(defn get-solution
  "Returns one node N tq ((:pred PB) N) else returns nil"
  [PB]
  (first (for [N (cget* (cget PB :graph) :node*) :when ((cget PB :pred) N)]
           N )))

(declare potential-node-operator-v)

(defn choose-next-node-and-operator
  "Returns {:success true, :node N, :operator OP} where N, OP are the best candidate to find a solution of PB
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
  (let [N   (gensym "Node"),
        NN  (merge NPPs {:isa :Node})
        NG  (update-in (cget G) [:node*] conj N)
        NC  (assoc *context* G NG, N NN)]
    (set! *context* NC)
    N ))

(defn add-op-apply
  "[Problem PB, OpApplyProperties OAPPs] -> Op-Apply OA
  OA is a new Op-Apply "
  [PB OAPPs]
  (let [OA  (gensym "Op-Apply")
        NOA (merge OAPPs {:isa :Op-Apply})
        NPB (update-in (cget PB) [:op-apply-v] conj OA) ]
    (set! *context* (merge *context* {PB NPB, OA NOA}))
    OA ))


(comment
  (let [CTXT1  (zipmap (repeatedly gensym) OP*1)
        [N0, D, G, PB]  (take 4 (repeatedly gensym))
        CTXT2 {PB   {:isa    :Find-Node-of-Generative-Graph-Problem
                     :graph  G
                     :pred   #(set/subset? GC* (cget* % :cond*))
                    }
               G    {:isa      :Graph
                     :gen-def  D
                    }
               D    {:isa            :Graph-Generative-Definition
                     :start-node     N0
                     :domain-op*     (set (keys CTXT1))
                     :apply-op-pre?  #(apply-op-pre? (cget* %1 :cond*) (cget %2))
                     :apply-op       #({:isa    :Node
                                        :cond*  (apply-op (cget* %1 :cond*) (cget %2))
                                       })
                    }
               N0   {:isa    :Node
                     :cond*  IC*
                    }
               }]

)
