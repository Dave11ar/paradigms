(defn abstractFunc [f]
  (fn [& expr] (fn [cur_vars] (apply f (mapv #(% cur_vars) expr)))))

(defn constant [value] (constantly value))
(defn variable [cur_var] #(get % cur_var))
(def add (abstractFunc +))
(def subtract (abstractFunc -))
(def multiply (abstractFunc *))
(def divide (abstractFunc (fn [firs & expr]
                            (reduce #(/ (double %1) %2) firs expr))))
(def negate subtract)
(def avg (abstractFunc #(/ (double (apply + %&)) (count %&))))
(def med (abstractFunc #(nth (sort %&) (/ (count %&) 2))))

(def str-to-func {'+      add
                  '-      subtract
                  '*      multiply
                  '/      divide
                  'negate negate
                  'avg    avg
                  'med    med
                  })
