package expression.parser;

import java.util.Map;
import java.util.Set;

public class Parameters {
    protected static int maxLevel = 3;
    protected static Map<String, Integer> PRIORITIES = Map.of(
            "min", 3, "max", 3,
            "+", 2, "-", 2,
            "*", 1, "/", 1
    );

    protected static Set<String> OPERATORS = Set.of(
            "+", "-", "*", "/", "("
    );

    protected static Set<String> VARIABLES = Set.of(
            "x", "y", "z"
    );
}
