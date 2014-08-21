(ns paip.core
    (:require [clojure.math.combinatorics :as combo]))

; ==== Chap 2.1

; Sentence => Noun-Phrase + Verb-Phrase
; Noun-Phrase => Article + Noun
; Verb-Phrase => Verb + Noun-Phrase
; Article => the, a, . . .
; Noun => man, ball, woman, table. . .
; Verb => hit, took, saw, liked. . .

; ==== Chap 2.2 first part

(declare sentence noun-phrase verb-phrase article noun verb)
(declare one-of)

(defn sentence []
  (concat (noun-phrase) (verb-phrase)) )

(defn noun-phrase []
  (concat (article) (noun)) )

(defn verb-phrase []
  (concat (verb) (noun-phrase)) )

(defn article []
  (one-of '[the a]) )

(defn noun []
  (one-of '[man ball woman table]) )

(defn verb []
  (one-of '[hit took saw liked]) )

(defn one-of [L]
  [(rand-nth L)] )


; utilisation
(comment
  (sentence)
  (noun-phrase)
  (verb-phrase)

  (use 'clojure.tools.trace)
  (trace-ns 'paip.core)
  (sentence)
  ; look at the light table console

  )


; ==== Chap 2.2 second part
; Sentence-2 => Noun-Phrase-2 + Verb-Phrase-2
; Noun-Phrase-2 => Article + Adj* + Noun + PP*
; Verb-Phrase-2 => Verb + Noun-Phrase-2
; Adj*=> 0, Adj + Adj*
; PP*=> 0, PP + PP*
; PP=> Prep + Noun-Phrase
; Adj=> big, little, blue, green, . . .
; Prep => to, in, by, with, . . .


(declare sentence-2 verb-phrase-2 noun-phrase-2 adj* pp* pp adj prep)

(defn sentence-2 []
  (concat (noun-phrase-2) (verb-phrase-2)) )

(defn verb-phrase-2 []
  (concat (verb) (noun-phrase-2)) )

(defn noun-phrase-2 []
  (concat (article) (adj*) (noun) (pp*)) )

(defn adj* []
  (if (rand-nth [true false])
    []
    (concat (adj) (adj*)) ))

(defn pp* []
  (if (rand-nth [true false])
    []
    (concat (pp) (pp*)) ))

(defn pp []
  (concat (prep) (noun-phrase-2)) )

(defn adj []
  (one-of '[big little blue green]) )

(defn prep []
  (one-of '[to in by with]) )



; utilisation
(comment
  (sentence-2)
  )


; ==== Chap 2.3

(def Simple-Grammar
  "A grammar for a trivial subset of English."
  '{Sentence     [[Noun-Phrase Verb-Phrase]]
    Noun-Phrase  [[Article Noun]]
    Verb-Phrase  [[Verb Noun-Phrase]]
    Article      [the a]
    Noun         [man ball woman table]
    Verb         [hit took saw liked]
    })

(def Grammar
  "The grammar used by generate. Initially, this is
  Simple-Grammar, but we can switch to other grammars."
  (atom Simple-Grammar) )

(defn rewrites [category]
  (get @Grammar category false) )

(defn generate
  "Generate a random sentence or phrase"
  [phrase]
  (cond (sequential? phrase)  (mapcat generate phrase)
        (rewrites phrase)     (generate (rand-nth (rewrites phrase)))
        :else                 [phrase] ))

(comment
  (use 'clojure.tools.trace)
  (trace-ns 'paip.core)
  (generate 'Sentence)
  (generate 'Sentence)
  (generate 'Noun-Phrase)
  (generate 'Verb-Phrase)
)


(defn generate-2
  "Generate a random sentence or phrase using let"
   [phrase]
  (if (sequential? phrase)
    (mapcat generate-2 phrase)
    (let [choices (rewrites phrase)]
      (if choices
        (generate-2 (rand-nth choices))
        [phrase] ))))


(comment
  (trace-vars generate-2)
  (generate-2 'Sentence)
  (generate-2 'Sentence)
)


; === exercice 2.1

(defn generate-3
  "Generate a random sentence using cond calling rewrites only once"
   [phrase]
  (let [choices (rewrites phrase)]
    (cond (sequential? phrase)  (mapcat generate-3 phrase)
          choices               (generate-3 (rand-nth choices))
          :else                 [phrase] )))

(comment
  (trace-vars generate-3)
  (generate-3 'Sentence)
)


; === exercice 2.2

(declare non-terminal?)

(defn generate-4
 "Write a version of generate that explicitly differentiates between terminal symbols (those with no rewrite rules)
 and nonterminal symbols"
  [phrase]
  (cond (sequential? phrase)    (mapcat generate-4 phrase)
        (non-terminal? phrase)  (generate-4 (rand-nth (rewrites phrase)))
        :else                   [phrase] ))

(defn non-terminal? [phrase]
  (if (rewrites phrase) true false) )

(comment
  (trace-vars generate-4)
  (generate-4 'Sentence)
)


; === personal exercice

(def Explicit-Grammar
  "A grammar for a trivial subset of English. ':and' means take them all in sequence. ':or' means take one of them.
   You can even mix terminal and non-terminal symbols."
  '{Sentence     [:and Noun-Phrase Verb-Phrase]
    Noun-Phrase  [:and Article Noun]
    Verb-Phrase  [:and Verb Noun-Phrase]
    Article      [:or the a]
    Noun         [:or man ball woman table]
    Verb         [:or hit took saw liked]
    })


(declare one-of-2)

(defn generate-5
  "generate an Explicit-Grammar phrase"
  [phrase]
  (if (sequential? phrase)
    (case (first phrase)
      :and (mapcat generate-5 (rest phrase))
      :or  (generate-5 (one-of-2 (rest phrase))) )
    (if-let [rewrite (rewrites phrase)]
      (generate-5 rewrite)
      [phrase] )))


(defn one-of-2
  "one-of really should return only one value and not a list of that value."
  [L]
  (rand-nth L) )


(comment
  (reset! Grammar Explicit-Grammar)
  (generate-5 'Sentence)
  (generate-5 'Verb-Phrase)
  (generate-5 'Article)
  (generate-5 'Mix-Phrase)
  )

; === Chap 2.5

(def Bigger-Grammar
  "A grammar for a bigger subset of English."
  '{Sentence     [[Noun-Phrase Verb-Phrase]]
    ; notice below that '[Name]' is used instead of 'Name'. This implicitly tag Name as non-terminal.
    ; the program will be easier BUT users have to know this trick.
    ; check next part on how to solve this (PB1)
    Noun-Phrase  [[Article Adj* Noun PP*] [Name] [Pronoun]]
    Verb-Phrase  [[Verb Noun-Phrase PP*]]
    PP*          [[] [PP PP*]]
    Adj*         [[] [Adj Adj*]]
    PP           [[Prep Noun-Phrase]]
    ; here 'to' and not '[to]' because 'to' should be handled as terminal.
    Prep         [to in by with on]
    Adj          [big little blue green adiabatic]
    Article      [the a]
    Name         [Pat Kim Lee Terry Robin]
    Noun         [man ball woman table]
    Verb         [hit took saw liked]
    Pronoun      [he she it these those that]
    })

(comment
  (reset! Grammar Bigger-Grammar)
  (trace-vars generate-4)
  (generate-4 'Sentence)
  (generate-4 'PP*)
  (generate-4 'Article)
  )


; === with explicit grammar

(def Bigger-Explicit-Grammar
  "A grammar for a bigger subset of English. ':and' means take them all in sequence. ':or' means take one of them.
   You can even mix terminal and non-terminal symbols."
  '{Sentence     [:and Noun-Phrase Verb-Phrase]
    Noun-Phrase  [:or [:and Article Adj* Noun PP*] Name Pronoun]
    Verb-Phrase  [:and Verb Noun-Phrase PP*]
    PP*          [:or :nothing [:and PP PP*]]
    Adj*         [:or :nothing [:and Adj Adj*]]
    PP           [:and Prep Noun-Phrase]
    Prep         [:or to in by with on]
    Adj          [:or big little blue green adiabatic]
    Article      [:or the a]
    Name         [:or Pat Kim Lee Terry Robin]
    Noun         [:or man ball woman table]
    Verb         [:or hit took saw liked]
    Pronoun      [:or he she it these those that]
    })

(def Test-Grammar
  ""
  '{Sentence     [:or Article Article]
    Article      [:or the a]
    })

(defn generate-6
  "generate a Bigger-Explicit-Grammar phrase."
  [phrase]
  (if (sequential? phrase)
    (case (first phrase)
      :and (mapcat generate-6 (rest phrase))
      :or  (generate-6 (one-of-2 (rest phrase))) )
    (if (= phrase :nothing)
      []
      (if-let [rewrite (rewrites phrase)]
        (generate-6 rewrite)
        [phrase] ))))

(comment
  (use 'clojure.tools.trace)
  (trace-vars generate-6)
  (reset! Grammar Test-Grammar)
  (generate-6 'Sentence)
  (reset! Grammar Bigger-Explicit-Grammar)
  (generate-6 'PP*)
  (generate-6 :nothing)
  (generate-6 'Sentence)
)

; === Chap 2.6

(defn generate-tree [phrase]
"Generate a random sentence or phrase, with a complete parse tree."
  (cond (sequential? phrase) (map generate-tree phrase)
        (rewrites phrase)    (cons phrase (generate-tree (rand-nth (rewrites phrase))))
        true                 (list phrase) ))

(comment
  (reset! Grammar Bigger-Grammar)
  (use 'clojure.tools.trace)
  (trace-vars generate-tree)
  (generate-tree 'Sentence)
 )

; with explicit-grammar

; since the syntax is more explicit, it is simpler to change the code.
; We just need to change the non-terminal part. Instead of returning generated values, we have to add the non-terminal name.
; this is done by the cons. Then, don't forget that we must return values and not a value. So we 'list' the value.

(defn generate-tree-2
  "Generate a random sentence or phrase, with a complete parse tree, from an explicit grammar."
  [phrase]
  (if (sequential? phrase)
    (case (first phrase)
      :and (mapcat generate-tree-2 (rest phrase))
      :or  (generate-tree-2 (one-of-2 (rest phrase))) )
    (if (= phrase :nothing)
      []
      (if-let [rewrite (rewrites phrase)]
        (list (cons phrase (generate-tree-2 rewrite)))
        [phrase] ))))

(comment
  (use 'clojure.tools.trace)
  (trace-vars generate-tree-2)
  (reset! Grammar Test-Grammar)
  (first (generate-tree-2 'Sentence))
  (reset! Grammar Bigger-Explicit-Grammar)
  (generate-tree-2 :nothing)
  (generate-tree-2 'Noun-Phrase)
  (generate-tree-2 'PP)
  (generate-tree-2 'PP*)
  (first (generate-tree-2 'Sentence))
)

; === generate all possible rewrites of a phrase

(declare combine-all)

(defn generate-all
  "Generate a list of all possible expansions of this phrase."
  [phrase]
  (cond (= phrase [])          (list nil)
        (sequential? phrase)   (combine-all (generate-all (first phrase))
                                            (generate-all (rest phrase)) )
        (rewrites phrase)      (mapcat generate-all (rewrites phrase))
        true                   (list (list phrase)) ))

(defn combine-all
"Return a list of lists formed by appending a y to an x.
E.g., (combine-all '((a) (b)) '((1) (2)))
-> ((A 1) (B 1) (A 2) (B 2))."
  [xlist ylist]
  (for [y ylist, x xlist] (concat x y)) )

(comment
  (combine-all '((a) (b)) '((1) (2)))
  (reset! Grammar Simple-Grammar)
  (generate-all 'Article)
  (generate-all 'Noun)
  (generate-all 'Noun-Phrase)
  (count (generate-all 'Sentence))
  (reset! Grammar '{Test [[Article] [Article]], Article [a the]})
  ; note the doublon in the below : ((a) (the) (a) (the))
  (generate-all 'Test)
  ; note that we can't call Test rewriting : ((a a) (the a) (a the) (the the))
  (generate-all '[[Article] [Article]])
  )

; === §2.6 : generate-all / expressive grammar

; to better understand the program, we will clarify the terminology by giving an other semantic.
; New Semantic Try to view the grammar as the definition of set-of-phrase.
; in fact generate-all can be viewed as the fonction 'extension-of' which returns the extension of a set-of-phrase SP
; given by intension.


(declare merge-phrase merge-phrase-L cartesian-product-L)

(defn extension-of
 "returns the extension of a set-of-phrase SP given by intension.
  phrases are represented by list of word. Ex: [a cat in the dark]
  extension of a set-of-phrase are represented by a set of phrases. Ex: #{[a cat in the dark] [a dog in the light]}
  set-of-phrase are represented intensionaly by :
  - :nothing : the set of empty phrase. (extension-of :nothing) = #{[]}.
  - a terminal : the set of phrase with only the terminal as word. Ex: (extension-of 'bag) = #{[bag]}
  - a non-terminal : the set of phrase of its rewrite.
  - (:or & ?List-of-set-of-phrase) : union of set-of-phrase.
    Ex: (extension-of '(:or bag ball)) = union of #{[bag]} #{[ball]} = #{[bag] [ball]}
  - (:and & ?List-of-set-of-phrase)  :
    {(merge-phrase-L ?List-of-phrase) | ?List-of-phrase e (cartesian-product-L ?List-of-set-of-phrase)}"

  [SP]
  (if (sequential? SP)
    (case (first SP)
      ; more closer of the definition
      :and (set (for [?List-of-phrase (cartesian-product-L (map extension-of (rest SP)))]
                   (merge-phrase-L ?List-of-phrase) ))
      ; more compact :
      ; :and (set (map merge-phrase-L (cartesian-product-L (map extension-of (rest SP)))))
      :or  (apply clojure.set/union (map extension-of (rest SP))) )
    (if (= SP :nothing)
      #{[]}
      (if-let [rewrite (rewrites SP)]
        (extension-of rewrite)
        #{[SP]} ))))


; (merge-phrase '[the] '[ball])
(def merge-phrase
  "merge-phrase merge two phrases P1 and P2. Ex: (merge-phrase '[the] '[ball]) = [the ball]"
  concat)

; (merge-phrase-L '[[the] [ball] [] [is red]])
(defn merge-phrase-L
  "merge-phrase-L is a list-argument-generalisation of merge-phrase. This means that while merge-phrase takes 2 arguments
   merge-phrase-L can take a list of argument. so (merge-phrase-L ?L) = (reduce merge-phrase ?L)
   (merge-phrase-L '[[the] [ball] [] [is red]]) = [the ball is red]"
  [LP]
  (reduce merge-phrase LP))

; (cartesian-product-L '[#{x1 x2} #{y1 y2} #{z1 z2}])
(defn cartesian-product-L
  "returns the cartesian product of a list-of-set ?LS"
  [LS]
  (set (apply combo/cartesian-product LS)) )


(comment
  (reset! Grammar Explicit-Grammar)
  (extension-of 'Article)
  (extension-of 'Noun)
  (extension-of 'Noun-Phrase)
  (count (extension-of 'Sentence))
  ; note below that no doublon are generated and that we can call rewriting directly : #{[the] [a]}
  (extension-of '[:or Article Article])
  )


; ===  Exercice 2.4

; (cross-product + [1 2 3] [10 20 30])
; (cross-product list '[a b c d e f g h] [1 2 3 4 5 6 7 8])
(defn cross-product
  "Return a list of all (F x y) values."
  [F xlist ylist]
  (for [x xlist, y ylist] (F x y)))












