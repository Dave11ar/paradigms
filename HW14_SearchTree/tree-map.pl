map_build([], []).
map_build([(Key, Value) | Tail], Result) :- map_build(Tail, TailTree), map_put(TailTree, Key, Value, Result).

map_put(TreeMap, Key, Value, Result) :-
    split(TreeMap, L, R, Key),
    rand_int(1000000, Prior),
    merge(L, node(Key, Value, Prior, [], []), Tmp), merge(Tmp, R, Result).

map_remove(TreeMap, Key, Result) :- split(TreeMap, L, R, Key), merge(L, R, Result).

map_get(node(Key, Val, _, _, _), Key, Val).
map_get(TreeMap, Key, Value) :-
    TreeMap = node(Key1, _, _, Left1, Right1),
    ((Key > Key1, map_get(Right1, Key, Value));
    (Key < Key1, map_get(Left1, Key, Value))).

merge(Tree, [], Tree) :- !.
merge([], Tree, Tree) :- !.
merge(L, R, Tree) :-
    L = node(Key1, Val1, Prior1, Left1, Right1),
    R = node(Key2, Val2, Prior2, Left2, Right2),
    (Prior1 > Prior2, merge(Right1, R, Right), Tree = node(Key1, Val1, Prior1, Left1, Right);
    Prior1 =< Prior2, merge(L, Left2, Left), Tree = node(Key2, Val2, Prior2, Left, Right2)).

split([], [], [], _) :- !.
split(Tree, L, R, Key) :-
    Tree = node(KeyT, ValT, PriorT, LeftT, RightT),
    (Key > KeyT, split(RightT, Left, R, Key), L = node(KeyT, ValT, PriorT, LeftT, Left);
    Key < KeyT, split(LeftT, L, Right, Key), R = node(KeyT, ValT, PriorT, Right, RightT);
    Key = KeyT, L = LeftT, R = RightT).

%modif
map_ceilingKey(TreeMap, Key, CeilingKey) :-
    (map_get(TreeMap, Key, _), CeilingKey = Key), !;
    split(TreeMap, _, R, Key),
    map_left(R, CeilingKey).

map_left(node(CeilingKey, _, _, [], _), CeilingKey).
map_left(Tree, CeilingKey) :-
    Tree = node(_, _, _, LeftT, _),
    map_left(LeftT, CeilingKey).
