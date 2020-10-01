%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%% ========================================================================
%%	               TEAROOM STATE
%% ========================================================================
%%
%% ------------------------------------------------
%%                  TEATABLE STATE
%% ------------------------------------------------
%%
%% busy		= teatable already taken
%% dirty	= teatable free BUT dirty
%% clean	= teatable free AND clean
%%
%% ------------------------------------------------
%%                  BARMAN STATE
%% ------------------------------------------------
%%
%% idle						= barman has nothing to do
%% preparing(TEATABLE_ID)	= barman is preparing a drink for table TEATABLE_ID
%% ready(TEATABLE_ID)		= drink for table TEATABLE_ID is ready
%%
%% ------------------------------------------------
%%                  WAITER STATE
%% ------------------------------------------------
%%
%% idle						= barman has nothing to do
%% preparing(TEATABLE_ID)	= barman is preparing a drink for table TEATABLE_ID
%% ready(TEATABLE_ID)		= drink for table TEATABLE_ID is ready
%%
%%
%% ATTUALMENTE LASCIO LO STATO DEL WAITER E DEL BARMAN AI RISPETTIVI ATTORI
%% SE SI DECIDE DI METTERE TUTTO QUA POI WAITER E BARMAN DEVONO INDICARE A 
%% TEAROOM CHE IL LORO STATO E' CAMBIATO
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%




%%   FACTS   %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
teatable(1, clean).
teatable(2, clean).

bound(1, NO).
bound(2, NO).


%%   RULES   %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
numfreetables(N) :-
	findall( N,teatable( N,clean ), NList),
	%% stdout <- println( tearoomstate_numfreetables(NList) ),
	length(NList,N).
	
numbusytables(N) :-
	findall( N,teatable( N,busy ), NList),
	%% stdout <- println( tearoomstate_numfreetables(NList) ),
	length(NList,N).

numdirtytables(N) :-
	findall( N,teatable( N,dirty ), NList),
	%% stdout <- println( tearoomstate_numfreetables(NList) ),
	length(NList,N).

stateOfTeatables( [teatable1(V1),teatable2(V2)] ) :-
	teatable( 1, V1 ),
	teatable( 2, V2 ).

%% only a clean table can get busy	
setBusyTable(N)	 :-
	%% stdout <- println( tearoomstate_busyTable(N) ),
	retract( teatable( N, clean ) ),
	!,
	assert( teatable( N, busy ) ).
setBusyTable(N).	
	
%% only a dirty table can get clean	
setCleanTable(N)	 :-
	%% stdout <- println( tearoomkb_cleanTable(N) ),
	retract( teatable( N, dirty ) ),
	!,
	assert( teatable( N, clean ) ).
setCleanTable(N).	

%% only a busy table can get dirty	
setDirtyTable(N)	 :-
	%% stdout <- println( tearoomkb_cleanTable(N) ),
	retract( teatable( N, busy ) ),
	!,
	assert( teatable( N, dirty ) ).
setDirtyTable(N).

%% assign(X,Y) assign at table X the client Y
assign(X,Y)		:-
	retract( bound(X, _) ),
	!,
	assert( bound(X,Y) ).
assign(X,Y).


	