(definterface IOperation
              (^Number evaluate [cur_vars])
              (^String toString [])
              (^String toStringInfix [])
              (diff [cur_var]))

(declare ZERO)
(declare ONE)

(deftype abstractTerminal[evaluate toString diff value]
         IOperation
         (evaluate [this cur_vars] (evaluate value cur_vars))
         (toString [this] (toString value))
         (toStringInfix [this] (toString value))
         (diff [this cur_var] (diff value cur_var)))

(defn createTerminal [evaluate toString diff]
      #(abstractTerminal. evaluate toString diff %))

(def Constant
  (createTerminal
    (fn [a b] a)
    #(format "%.1f" (double %))
    (fn [a b] ZERO)))

(def Variable
  (createTerminal
    #(%2 %1)
    (fn [a] a)
    (fn [a b] (if (= a b) ONE
                          ZERO))))

(def ZERO (Constant 0))
(def ONE (Constant 1))

(deftype abstractExpression [func sign dff args]
         IOperation
         (evaluate [this cur_vars] (apply func (mapv #(.evaluate % cur_vars) args)))
         (toString [this] (str "(" sign " " (clojure.string/join " " (mapv #(.toString %) args)) ")"))
         (toStringInfix [this] (if (= (count args) 1) (str sign "(" (.toStringInfix (first args)) ")")
                                                      (str "(" (clojure.string/join (str " " sign " ") (map #(.toStringInfix %) args)) ")")))
         (diff [this cur_var] (dff (vector args (mapv #(.diff % cur_var) args)))))

(defn createExpression [func sign dff]
      #(abstractExpression. func sign dff %&))

(defn simpleDiff [func]
      (letfn [(createDiff [func [args diffArgs]] (apply func diffArgs))]
             (partial createDiff func)))

(def Add
  (createExpression +
                    "+"
                    (simpleDiff #(apply Add %&))))
(def Subtract
  (createExpression -
                    "-"
                    (simpleDiff #(apply Subtract %&))))
(def Negate
  (createExpression -
                    "negate"
                    (simpleDiff #(apply Negate %&))))
(def Multiply
  (createExpression *
                    "*"
                    (fn [[args diffArgs]] (second
                                            (reduce (fn [[exprX exprDX] [x dx]]
                                                        (vector (Multiply exprX x)
                                                                (Add (Multiply exprX dx) (Multiply exprDX x))))
                                                    (mapv vector args diffArgs))))))
(def Divide
  (createExpression (fn [a & args] (/ (double a) (apply * args)))
                    "/"
                    (fn [[args diffArgs]] (Divide (second
                                                    (reduce (fn [[exprX exprDX] [x dx]]
                                                                (vector (Multiply exprX x)
                                                                        (Subtract (Multiply exprDX x) (Multiply exprX dx))))
                                                            (mapv vector args diffArgs)))
                                                  (Multiply (apply Multiply (rest args))
                                                            (apply Multiply (rest args)))))))
(def Sum
  (createExpression +
                    "sum"
                    (simpleDiff #(apply Sum %&))))
(def Avg
  (createExpression (fn [& args] (/ (apply + args) (count args)))
                    "avg"
                    (fn [[args diffArgs]] (Divide (apply Sum diffArgs)
                                                  (Constant (count args))))))
(def Pow
  (createExpression #(Math/pow %1 %2)
                    "**"
                    nil))
(def Log
  (createExpression #(/ (Math/log (Math/abs %2))
                        (Math/log (Math/abs %1)))
                    "//"
                    nil))

(def str-to-obj {'+      Add
                 '-      Subtract
                 '*      Multiply
                 '/      Divide
                 'negate Negate
                 'sum    Sum
                 'avg    Avg})

(defn evaluate [a cur_var] (.evaluate a cur_var))
(defn toString [a] (.toString a))
(defn toStringInfix [a] (.toStringInfix a))
(defn diff [a cur_var] (.diff a cur_var))

(defn parse [str-to-expr cur-const cur-variable]
      (fn [stringExpression]
          (letfn [(parseExpression [expression]
                                   (cond (number? expression) (cur-const expression)
                                         (symbol? expression) (cur-variable (str expression))
                                         :else (apply (str-to-expr (first expression))
                                                      (mapv parseExpression (rest expression)))))
                  ] (parseExpression (read-string stringExpression)))))

(def parseFunction (parse str-to-func constant variable))
(def parseObject (parse str-to-obj Constant Variable))
