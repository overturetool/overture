package org.overturetool.vdmj.lex;

import java.util.List;

import org.overture.ast.node.Node;
import org.overture.ast.types.PType;

public abstract class LexNameToken extends LexToken{

	public LexNameToken(LexLocation location, VDMToken type) {
		super(location, type);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -6021528487608461917L;

	public abstract LexIdentifierToken getIdentifier();

	public abstract LexNameToken getExplicit(boolean ex);

	public abstract LexNameToken getOldName();

	public abstract String getName();

	public abstract LexNameToken getPreName(LexLocation l);

	public abstract LexNameToken getPostName(LexLocation l);

	public abstract LexNameToken getInvName(LexLocation l);

	public abstract LexNameToken getInitName(LexLocation l);

	public abstract LexNameToken getModifiedName(String classname);

	public abstract LexNameToken getSelfName();

	public abstract LexNameToken getThreadName();

	public abstract LexNameToken getPerName(LexLocation loc);

	public abstract LexNameToken getClassName();

	public abstract void setTypeQualifier(List<PType> types);

	public abstract boolean equals(Object other);

	public abstract boolean matches(LexNameToken other);
	
	public abstract LexLocation getLocation();

	public abstract String getModule();

	public abstract List<PType> getTypeQualifier();

	public abstract boolean isOld();

	


}