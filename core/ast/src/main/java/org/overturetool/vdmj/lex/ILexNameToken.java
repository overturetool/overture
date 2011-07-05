package org.overturetool.vdmj.lex;

import java.util.List;

import org.overture.ast.node.Node;
import org.overture.ast.types.PType;

public abstract class ILexNameToken extends Node{

	public abstract ILexIdentifierToken getIdentifier();

	public abstract ILexNameToken getExplicit(boolean ex);

	public abstract ILexNameToken getOldName();

	public abstract String getName();

	public abstract ILexNameToken getPreName(LexLocation l);

	public abstract ILexNameToken getPostName(LexLocation l);

	public abstract ILexNameToken getInvName(LexLocation l);

	public abstract ILexNameToken getInitName(LexLocation l);

	public abstract ILexNameToken getModifiedName(String classname);

	public abstract ILexNameToken getSelfName();

	public abstract ILexNameToken getThreadName();

	public abstract ILexNameToken getPerName(LexLocation loc);

	public abstract ILexNameToken getClassName();

	public abstract void setTypeQualifier(List<PType> types);

	public abstract boolean equals(Object other);

	public abstract boolean matches(ILexNameToken other);
	
	public abstract LexLocation getLocation();

	


}