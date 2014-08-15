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

;
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




