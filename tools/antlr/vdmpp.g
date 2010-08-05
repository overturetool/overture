grammar vdmpp;
options{
	backtrack=true;
}

tokens{
	LEX_ACT='#act';
	LEX_ACTIVE='#active';
	LEX_FIN='#fin';
	LEX_REQ='#req';
	LEX_WAITING='#waiting';
	LEX_ABS='abs';
	LEX_ALL='all';
	LEX_ALWAYS='always';
	LEX_AND='and';
	LEX_ATOMIC='atomic';
	LEX_ASYNC='async';
	LEX_BE='be';
	LEX_BOOL='bool';
	LEX_BY='by';
	LEX_CARD='card';
	LEX_CASES='cases';
	LEX_CHAR='char';
	LEX_CLASS='class';
	LEX_COMP='comp';
	LEX_COMPOSE='compose';
	LEX_CONC='conc';
	LEX_CYCLES='cycles';
	LEX_DCL='dcl';
	LEX_DEF='def';
	LEX_DINTER='dinter';
	LEX_ARITHMETIC_INTEGER_DIVISION='div';
	LEX_DO='do';
	LEX_DOM='dom';
	LEX_DUNION='dunion';
	LEX_DURATION='duration';
	LEX_ELEMS='elems';
	LEX_ELSE='else';
	LEX_ELSEIF='elseif';
	LEX_END='end';
	LEX_ERROR='error';
	LEX_ERRS='errs';
	LEX_EXISTS='exists';
	LEX_EXISTS1='exists1';
	LEX_EXIT='exit';
	LEX_EXT='ext';
	LEX_BOOL_FALSE='false';
	LEX_FLOOR='floor';
	LEX_FOR='for';
	LEX_FORALL='forall';
	LEX_FROM='from';
	LEX_FUNCTIONS='functions';
	LEX_HD='hd';
	LEX_IF='if';
	LEX_IN='in';
	LEX_INDS='inds';
	LEX_INMAP='inmap';
	LEX_INSTANCE='instance';
	LEX_INT='int';
	LEX_SET_INTERSECTION='inter';
	LEX_INF='inv';
	LEX_INVERSE='inverse';
	LEX_IOTA='iota';
	LEX_IS='is';
	LEX_IS_='is_';
	LEX_ISOFBASECLASS='isofbaseclass';
	LEX_ISOFCLASS='isofclass';
	LEX_LAMBDA='lambda';
	LEX_LEN='len';
	LEX_LET='let';
	LEX_MAP='map';
	LEX_DMERGE='merge';
	LEX_MK_='mk_';
	LEX_MOD='mod';
	LEX_MU='mu';
	LEX_MAP_MERGE='munion';
	LEX_MUTEX='mutex';
	LEX_NAT='nat';
	LEX_NATONE='nat1';
	LEX_NEW='new';
	LEX_NIL='nil';
	LEX_NOT='not';
	LEX_OF='of';
	LEX_OPERATIONS='operations';
	LEX_OR='or';
	LEX_OTHERS='others';
	LEX_PER='per';
	LEX_PERIODIC='periodic';
	LEX_POST='post';
	LEX_POWER='power';
	LEX_PRE='pre';
	LEX_PRECONDAPPLY='pre_';
	LEX_PRIVATE='private';
	LEX_PROTECTED='protected';
	LEX_PROPER_SUBSET='psubset';
	LEX_PUBLIC='public';
	LEX_RAT='rat';
	LEX_RD='rd';
	LEX_REAL='real';
	LEX_REM='rem';
	LEX_RESPONSIBILITY='responsibility';
	LEX_RETURN='return';
	LEX_RNG='rng';
	LEX_SAMEBASECLASS='samebaseclass';
	LEX_SAMECLASS='sameclass';
	LEX_SELF='self';
	LEX_SEQ='seq';
	LEX_SEQ1='seq1';
	LEX_SET='set';
	LEX_SKIP='skip';
	LEX_SPECIFIED='specified';
	LEX_SPORADIC='sporadic';
	LEX_ST='st';
	LEX_START='start';
	LEX_STARTLIST='startlist';
	LEX_STATIC='static';
	LEX_SUBCLASS='subclass';
	LEX_SUBSET='subset';
	LEX_SYNC='sync';
	LEX_SYSTEM='system;
	LEX_THEN='then';
	LEX_THREAD='thread';
	LEX_THREADID='threadid';
	LEX_TIME='time';
	LEX_TIXE='tixe';
	LEX_TL='tl';
	LEX_TO='to';
	LEX_TOKEN='token';
	LEX_TRACES='traces';
	LEX_TRAP='trap';
	LEX_BOOL_TRUE='true';
	LEX_TYPES='types';
	LEX_UNDEFINED='undefined';
	LEX_SET_UNION='union';
	LEX_VALUES='values';
	LEX_VARIABLES='variables';
	LEX_WHILE='while';
	LEX_WITH='with';
	LEX_WR='wr';
	LEX_YET='yet'; 

		
	DCOLON='::';
	ASSIGN=':=';
	COLON=':';
	SCOLON=';';
	EQ='=';
	LB='[';
	RB=']';
	LP='(';
	RP=')';
	BAR='|';
}

fragment
LOWER	:	'a'..'z';
fragment
UPPER	:	'A'..'Z';
fragment
CHAR	:	(LOWER|UPPER|'_');
DIGIT 	:	'0'..'9';
IDENT	:	CHAR (CHAR|DIGIT)*;
QUOTE	:	'<'(DIGIT|UPPER|'_')+ '>';
STRING 	:	'"' (~('\\'|'"') )* '"';
WS	:	('\n' | '\r' | '\t' | ' ')+ { skip(); };

start	:	document EOF {System.out.println("Document parsed");}
	;

document:	classDefinitionList
	|	{System.out.println("empty document");}	
	;

classDefinitionList
	:	classDefinition classDefinition*
	;

classDefinition 
	:	LEX_CLASS nameStart=IDENT LEX_END nameEnd=IDENT 
	{ System.out.println(nameStart.getText());}
	| LEX_CLASS IDENT definitionList LEX_END IDENT
	| LEX_CLASS IDENT LEX_IS LEX_SUBCLASS LEX_OF IDENT LEX_END IDENT
	| LEX_CLASS IDENT LEX_IS LEX_SUBCLASS LEX_OF IDENT definitionList LEX_END IDENT
	;
	
definitionList
	: definitionBlock definitionBlock*
	;

definitionBlock 
	:	valueDefinitions
	|	instanceVariableDefinitions
	|	typeDefinitions
	;
	
valueDefinitions
	:	LEX_VALUES
	|	LEX_VALUES valueDefinitionList SCOLON?
	;

valueDefinitionList
	:	valueDefinition (SCOLON valueDefinition)*
	;
	
valueDefinition
	:	IDENT EQ STRING
	;
	
instanceVariableDefinitions
	:	LEX_INSTANCE LEX_VARIABLES
	|	LEX_INSTANCE LEX_VARIABLES instanceVariableDefinitionList SCOLON?
	;
	
instanceVariableDefinitionList
	:	instanceVariable (SCOLON instanceVariable)*
	;
	
instanceVariable
	:	IDENT COLON type (ASSIGN STRING)?
	;
	
typeDefinitions	
	: LEX_TYPES 
	| LEX_TYPES typeDefinitionList SCOLON?
	;
	
typeDefinitionList
	:	typeDefinition (SCOLON typeDefinition)*;

typeDefinition 
	:	IDENT EQ type
	|	IDENT DCOLON
	|	IDENT DCOLON fieldList
	;

fieldList
	:	IDENT COLON type (IDENT COLON type)*
	;

	
type 	: IDENT 
	| QUOTE 
	| LB type RB 
	| LP type RP 
	| LEX_SEQ LEX_OF type 
	| LEX_SET LEX_OF type 
	| LEX_MAP type LEX_TO type
	|	(IDENT 
		| QUOTE 
		| LB type RB 
		| LP type RP 
		| LEX_SEQ LEX_OF type 
		| LEX_SET LEX_OF type 
		| LEX_MAP type LEX_TO type 
		) BAR type
	;


	




	
	 