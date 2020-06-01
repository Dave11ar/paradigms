;rewiew
(defn size-equals? [args] (every? #(and (vector? %)
                                        (= (count (first args))
                                           (count %)))
                                  args))

(defn isNumbers? [args] (every? number? args))

(defn isVector? [arg] (and (vector? arg)
                           (every? number? arg)))

(defn isMatrix? [args] (and (vector? args)
                            (size-equals? args)
                            (every? isVector? args)))

(defn isTensor? [tensor] (or (every? number? tensor)
                             (and (vector? tensor)
                                  (size-equals? tensor)
                                  (every? isTensor? (apply mapv vector tensor)))))

(defn abstract-coord [check-type operation]
  (letfn [(recur-oper [& args]
            {:pre [(or (isNumbers? args) (size-equals? args))] }
            (if (every? number? args)(apply operation args)
                                     (apply mapv recur-oper args)))]
    (fn [& args]
      {:pre [(every? check-type args)]}
      (apply recur-oper args))))

(comment ":NOTE: you can pass `opeeration` implicitly, now it looks like copy-paste")
(defn create-v [operation] (abstract-coord isVector? operation))
(defn create-m [operation] (abstract-coord isMatrix? operation))
(defn create-t [operation] (abstract-coord isTensor? operation))

(def v+ (create-v +))
(def v- (create-v -))
(def v* (create-v *))

(def m+ (create-m +))
(def m- (create-m -))
(def m* (create-m *))

(def t+ (create-t +))
(def t- (create-t -))
(def t* (create-t *))

(defn scalar [& vectors]
  {:pre [(= (count vectors) 2)]}
  (apply + (apply v* vectors)))

(defn vect [& vectors]
  {:pre [(and (= (count (first vectors)) 3) (size-equals? vectors))]}
    (reduce #(letfn [(subV [i j] (- (* (nth %1 i) (nth %2 j)) (* (nth %1 j) (nth %2 i))))]
               [(subV 1 2), (subV 2 0), (subV 0 1)])
            vectors))


(defn abstract-scalar [check-type operation]
  (fn [v & scalars]
    {:pre [(and (check-type v) (isNumbers? scalars))]}
    (let [s (apply * scalars)]
      (mapv #(operation % s) v))))

(def v*s (abstract-scalar isVector? *))
(def m*s (abstract-scalar isMatrix? v*s))

(defn m*v [matrix vec]
  {:pre [(and (isMatrix? matrix) (isVector? vec))]}
  (mapv #(apply + (v* % vec)) matrix))

(defn transpose [matrix]
  {:pre [(isMatrix? matrix)]}
  (apply mapv vector matrix))

(defn m*m [& matrices]
  {:pre [(every? isMatrix? matrices)]}
  (reduce (fn [a b] (mapv #(m*v (transpose b) %) a)) matrices))
