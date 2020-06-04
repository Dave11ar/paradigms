map_build([], []).
map_build([(Key, Value) | Tail], Result) :- map_build(Tail, TailTree), map_put(TailTree, Key, Value, Result).

map_cut(TreeMap, Key, L, M, R) :- split(TreeMap, L, Tmp, Key), split(Tmp, M, R, Key + 1).

map_put(TreeMap, Key, Value, Result) :-
    map_cut(TreeMap, Key, L, M, R),
    rand_int(1000000, Prior),
    merge(L, node(Key, Value, Prior, [], []), Tmp), merge(Tmp, R, Result).

map_remove(TreeMap, Key, Result) :- map_cut(TreeMap, Key, L, M, R), merge(L, R, Result).

map_get(node(Key, Val, _, _, _), Key, Val).
map_get(TreeMap, Key, Value) :-
    TreeMap = node(Key1, Val1, Prior1, Left1, Right1),
    ((Key > Key1, map_get(Right1, Key, Value));
    (Key < Key1, map_get(Left1, Key, Value))).

merge(Tree, [], Tree) :- !.
merge([], Tree, Tree) :- !.
merge(L, R, Tree) :-
    L = node(Key1, Val1, Prior1, Left1, Right1),
    R = node(Key2, Val2, Prior2, Left2, Right2),
    (Prior1 > Prior2, merge(Right1, R, Right), Tree = node(Key1, Val1, Prior1, Left1, Right);
    Prior1 =< Prior2, merge(L, Left2, Left),Tree = node(Key2, Val2, Prior2, Left, Right2)).

split([], [], [], Key) :- !.
split(Tree, L, R, Key) :-
    Tree = node(KeyT, ValT, PriorT, LeftT, RightT),
    (Key > KeyT, split(RightT, Left, R, Key), L = node(KeyT, ValT, PriorT, LeftT, Left);
    Key =< KeyT, split(LeftT, L, Right, Key), R = node(KeyT, ValT, PriorT, Right, RightT)).
