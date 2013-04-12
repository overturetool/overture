package org.overture.ast.intf.lex;

import org.overture.ast.lex.LexLocation;
import org.overture.ast.lex.LexNameToken;

public interface ILexIdentifierToken extends ILexToken
{


	public LexNameToken getClassName();

	public boolean getOld();

	
	



	public boolean isOld();

	public String getName();

	
	public LexLocation getLocation();


	
	
	

}
