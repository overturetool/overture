package org.overture.pog.utility;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.node.INode;

/**
 * A class to create unique names, for use during PO generation. The module/class and location are set during
 * construction from the node passed in (though must be a PExp or PDefinition to do this).
 * 
 * @author Nick Battle
 */
public class UniqueNameGenerator
{
	protected final ILexLocation location;
	protected final String module;
	protected int counter = 0;

	public UniqueNameGenerator(INode node)
	{

		if (node instanceof PExp)
		{
			location = ((PExp) node).getLocation();
			module = handleLocation(location);
		} else if (node instanceof PDefinition)
		{
			location = ((PDefinition) node).getLocation();
			module = handleLocation(location);
		} else
		{
			location = null;
			module = null;
		}
	}

	public ILexNameToken getUnique(String name)
	{
		return new LexNameToken(module, name + ++counter, location);
	}

	// guard against null locations
	private String handleLocation(ILexLocation location)
	{

		if (location != null)
		{
			return location.getModule();
		} else
		{
			return null;
		}

	}
}
