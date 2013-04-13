package org.overture.ast.intf.lex;

import java.util.List;

import org.overture.ast.lex.LexLocation;
import org.overture.ast.types.PType;

public interface ILexNameToken extends ILexToken
{

	int compareTo(ILexNameToken o);

	ILexNameToken copy();

	ILexNameToken getClassName();

	boolean getExplicit();

	ILexNameToken clone();

	ILexNameToken getExplicit(boolean ex);

	ILexIdentifierToken getIdentifier();

	ILexNameToken getInitName(LexLocation l);

	ILexNameToken getInvName(LexLocation l);

	LexLocation getLocation();

	ILexNameToken getModifiedName(String classname);

	String getModule();

	String getName();

	ILexNameToken getNewName();

	boolean getOld();

	ILexNameToken getOldName();

	ILexNameToken getPerName(LexLocation loc);

	ILexNameToken getPostName(LexLocation l);

	ILexNameToken getPreName(LexLocation l);

	ILexNameToken getSelfName();

	String getSimpleName();

	ILexNameToken getThreadName();

	ILexNameToken getThreadName(LexLocation loc);

	List<PType> getTypeQualifier();

	List<PType> typeQualifier();

	boolean isOld();

	boolean matches(ILexNameToken other);

	void setTypeQualifier(List<PType> types);

}
