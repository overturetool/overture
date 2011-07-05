package org.overturetool.vdmj.lex;


public abstract class LexIdentifierToken extends LexToken{

	public LexIdentifierToken(LexLocation location, VDMToken type) {
		super(location, type);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1770683334150366111L;

	public abstract LexNameToken getClassName();

	public abstract boolean isOld();

	public abstract String getName();

	public abstract LexLocation getLocation();

}