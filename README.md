# PAIP

Clojure code of PAIP (Paradigms Of Artificial Intelligence Programming - Case Studies In Common Lisp, Peter Norvig)

Actually Chapter 2 and Chapter 4 (GPS1, GPS2) are implemented.

Chapters :

1. : Introduction to Lisp : *lisp specific, not implemented*.
2. : A Sample Lisp Program
    2.2 : A Straightforward Solution : **implemented**.
    2.3 : A Rule-Based Solution : **implemented**.
    2.4 : Two Paths to Follow : *no code*
    2.5 : Changing the Grammar without Changing the Program : **implemented**
    2.6 : Using the Same Data for Several Programs : **implemented**
3. : Overview of Lisp : *lisp specific*
4. : GPS: The General Problem Solver
    4.1 => 4.9 : GPS1 : **implemented**
    4.10 : The Lack of Intermediate Information Problem : **implemented**
    4.11 : GPS Version 2: A More General Problem Solver : **implemented**
    4.12 : The New Domain Problem: Monkey and Bananas : **implemented**
    4.13 : The Maze Searching Domain : **implemented**
    4.14 : The Blocks World Domain : **implemented**
    4.15 : Stage 5 Repeated: Analysis of Version 2 : *no code*
    4.16 : The Not Looking after You Don't Leap Problem : **implemented**
    4.17 => 4.21 : *no code*

Last updated : 2014-12-03

## Usage

Tools used are listed below. All in this project assume you are using them too :
- Light Table 0.6.7+ : code editor
- Leinengen 2.0+ : clojure project management
- git / github : source versionning / repository


## Running Chapter 2 programs

1. clone this repository
2. open repl/paip/chap2.clj
3. eval the buffer (ctrl+shift+enter)
4. eval lines in comment sections

## Running GPS1 (§4.1 => §4.9)

Code is in 'src/paip/gps1.clj', tests are in 'test/paip/gps1_test.clj' and repl session is in 'repl/paip/gps1_repl.clj'

1. clone this repository
2. test code
  2.1 open test/paip/gps1_test.clj
  2.2 eval the buffer (ctrl+shift+enter)
  2.3 select '(run-tests)' and ctrl-enter to run all tests. Check console tab to see results.
  2.4 you can also run lein test
3. play with code
  1 open repl/paip/gps1_repl.clj
  2 eval lines as you go

## Running GPS2 (§4.10 => §4.16)

like running GPS1 but with GPS2 files


## License

Copyright © 2014 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
