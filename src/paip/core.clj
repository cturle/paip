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


; ==== Chap 2.2 second part p61
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












































