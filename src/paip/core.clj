(ns paip.core)

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
  (cond (sequential? phrase)    (mapcat generate-3 phrase)
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
    Mix-Phrase   [:or Article Verb-Phrase]
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
  "A grammar for a trivial subset of English. ':and' means take them all in sequence. ':or' means take one of them.
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

(defn generate-6
  "generate an Explicit-Grammar+ phrase with :nothing"
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
  (reset! Grammar Bigger-Grammar)
  (use 'clojure.tools.trace)
  (trace-vars generate-6)
  (generate-6 'PP*)
  (generate-6 'Sentence)
  (generate-6 :nothing)
)





























