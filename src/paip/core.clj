(ns paip.core
  (require [clojure.set :as set]
           [clojure.pprint :as pp]) )


(defn starts-with
"Is L a list whose first element is X ?"
  [L X]
  (and (coll? L) (= (first L) X)) )

;============================================================================

(def +dbg-id*+
"Identifiers used by dbg"
  #{} )


(defn debug
"Start dbg output on the given set of ids ID*."
  [& ID*]
  (def +dbg-id*+ (set/union +dbg-id*+ (set ID*))) )

(defn undebug
"Stop dbg on the set of ids ID*. With no ids, stop dbg altogether."
  ([]
    (def +dbg-id*+ #{}))
  ([& ID*]
    (def +dbg-id*+ (set/difference +dbg-id*+ (set ID*))) ))


(defn dbg
"Print debugging info if debug id ID has been specified (with debug).
 Printing is done with cl-format on *err* stream.
 Return nil."
  [ID format-string & args]
  (when (contains? +dbg-id*+ ID)
    (let [F (str "~&" format-string)]
      (apply pp/cl-format *err* F args)
      nil )))

(defn dbg-indent
"Print indented debugging info if debug id ID has been specified (with debug).
 Printing is done with cl-format on *err* stream.
 Return nil."
  [ID indent format-string & args]
  (when (contains? +dbg-id*+ ID)
    (let [F (str "~&" "~v{~C~}" format-string)]
      (apply pp/cl-format *err* F (list* indent (repeat \|) args))
      nil )))
