package org.overture.ast.intf.lex;

import org.overture.ast.lex.VDMToken;
import org.overture.ast.node.INode;

public interface ILexToken extends INode, Comparable<INode>
{



	ILexLocation getLocation();

	VDMToken getType();

	/**
	 * Test whether this token is a given basic type.
	 *
	 * @param ttype	The type to test.
	 * @return	True if this is of that type.
	 */

	public boolean is(VDMToken ttype);

	/**
	 * Test whether this token is not a given basic type.
	 *
	 * @param ttype	The type to test.
	 * @return	True if this is not of that type.
	 */

	public boolean isNot(VDMToken ttype);


	ILexToken clone();







	

}
