package expression.parser;

import java.util.Map;
import java.util.Set;

public class Parameters {
    protected static int maxLevel = 2;
    protected static Map<String, Integer> PRIORITIES = Map.of(
            "+", 2, "-", 2,
            "*", 1, "/", 1
    );

    protected static Set<String> VARIABLES = Set.of(
            "x", "y", "z"
    );
}
