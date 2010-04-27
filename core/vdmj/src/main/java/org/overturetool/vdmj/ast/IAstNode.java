package org.overturetool.vdmj.ast;

import org.overturetool.vdmj.lex.LexLocation;


public interface IAstNode
{
	/**
	 * Gets the name of the node
	 * @return the name
	 */
	public String getName();
	
	/**
	 * Gets the lex location of the node
	 * @return the lex location
	 */
	public LexLocation getLocation();
	
}
