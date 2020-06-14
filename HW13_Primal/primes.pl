init(MAXN) :-
    M is MAXN + 1,
    erat_mem(2, M).

erat_table(0).
erat_table(1).

erat_mem(N, MAXN) :-
    N * N < MAXN,
    N1 is N + 1, not erat_mem(N1, MAXN),
    not erat_table(N),
    N2 is N * N, erat_cycle(N2, MAXN, N).

erat_cycle(N, MAXN, HOD) :-
    N < MAXN,
    assert(erat_table(N)),
    M is N + HOD,
    erat_cycle(M, MAXN, HOD).

prime(N) :- not erat_table(N), !.
composite(N) :- erat_table(N), !.

divisor(N, A, A) :- K is div(N, A), N =:= K * A, prime(A), !.
divisor(N, A, D) :- D =< N, D1 is D + 1, divisor(N, A, D1), !.

find_divisors(1, [], _).
find_divisors(N, [N], _) :- prime(N), !.

find_divisors(N, [A | R], P) :- divisor(N, A, P), N1 is N / A, find_divisors(N1, R, A), !.
prime_divisors(N, D) :- not is_list(D), find_divisors(N, D, 2), !.

ordered([]).
ordered([_]).
ordered([X,Y|Z]) :- X =< Y , ordered([Y|Z]), !.

do_mul(1, []).
do_mul(N, [A | R]) :- do_mul(T, R), N is A * T, !.
prime_divisors(N, D) :- is_list(D), ordered(D), do_mul(N, D), !.

get_syst(0, [], _) :- !.
get_syst(N, [A | R], K) :- N < K, A is N, N1 is 0, get_syst(N1, R, K), !.
get_syst(N, [A | R], K) :- N1 is div(N, K), A is mod(N, K), get_syst(N1, R, K), !.

prime_palindrome(N, K) :- prime(N), get_syst(N, P1, K), reverse(P1, P2), P1 = P2.
