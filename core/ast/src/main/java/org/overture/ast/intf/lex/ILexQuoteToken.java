package org.overture.ast.intf.lex;

import org.overture.ast.lex.LexQuoteToken;

public interface ILexQuoteToken extends ILexToken
{

	String getValue();
	
	LexQuoteToken clone();
	
	String toString();
}
