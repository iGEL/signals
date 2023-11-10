(ns signals.display.matrix)

(def letters
  (reduce-kv (fn [m key letter-matrix]
               (assoc m key (mapv (fn [row]
                                    (mapv #(= % \X) row))
                                  letter-matrix)))
             {}
             {nil [""
                   ""
                   ""
                   ""
                   ""
                   ""
                   ""]

              1 ["  X"
                 " XX"
                 "X X"
                 "  X"
                 "  X"
                 "  X"
                 "  X"]

              2 [" XXX "
                 "X   X"
                 "    X"
                 "   X "
                 "  X  "
                 " X   "
                 "XXXXX"]

              3 [" XXX "
                 "X   X"
                 "    X"
                 "   X "
                 "    X"
                 "X   X"
                 " XXX "]

              4 ["   X "
                 "  XX "
                 " X X "
                 "X  X "
                 "XXXXX"
                 "   X "
                 "   X "]

              5 ["XXXXX"
                 "X    "
                 "XXXX "
                 "    X"
                 "    X"
                 "X   X"
                 " XXX "]

              6 [" XXX "
                 "X    "
                 "X    "
                 "XXXX "
                 "X   X"
                 "X   X"
                 " XXX "]

              7 ["XXXXX"
                 "    X"
                 "   X "
                 "  X  "
                 " X   "
                 " X   "
                 " X   "]

              8 [" XXX "
                 "X   X"
                 "X   X"
                 " XXX "
                 "X   X"
                 "X   X"
                 " XXX "]

              0 [" XXX "
                 "X   X"
                 "X   X"
                 "X   X"
                 "X   X"
                 "X   X"
                 " XXX "]}))

(defn- append [l1 l2]
  (let [*l1 (letters l1)
        *l2 (letters l2)]
    (vec (map-indexed (fn [idx row]
                        (concat row [false] (get *l2 idx)))
                      *l1))))

(def matrix
  (merge letters
         {9 (map reverse
                 (reverse (letters 6)))
          10 (append 1 0)
          11 (append 1 1)
          12 (append 1 2)
          13 (append 1 3)
          14 (append 1 4)
          15 (append 1 5)}))
