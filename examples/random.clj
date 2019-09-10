#!/usr/bin/env ../bin/clojure-script

(dotimes [i 50]
  (print (repeat (rand-int 15) "."))
  (flush))

(println)

(System/exit 1)
