package org.overture.ast.intf.lex;

import java.util.List;

import org.overture.ast.lex.LexLocation;
import org.overture.ast.types.PType;

public interface ILexNameToken extends ILexIdentifierToken
{

	String getFullName();
	
	int compareTo(ILexNameToken o);

	ILexNameToken copy();

	ILexNameToken getClassName();

	boolean getExplicit();

	ILexNameToken clone();

	ILexNameToken getExplicit(boolean ex);

	ILexIdentifierToken getIdentifier();

	ILexNameToken getInitName(ILexLocation l);

	ILexNameToken getInvName(ILexLocation l);

	ILexLocation getLocation();

	ILexNameToken getModifiedName(String classname);

	String getModule();

	String getName();

	ILexNameToken getNewName();

	boolean getOld();

	ILexNameToken getOldName();

	ILexNameToken getPerName(ILexLocation loc);

	ILexNameToken getPostName(ILexLocation l);

	ILexNameToken getPreName(ILexLocation l);

	ILexNameToken getSelfName();

	String getSimpleName();

	ILexNameToken getThreadName();

	ILexNameToken getThreadName(ILexLocation loc);

	List<PType> getTypeQualifier();

	List<PType> typeQualifier();

	boolean isOld();

	boolean matches(ILexNameToken other);

	void setTypeQualifier(List<PType> types);

}
