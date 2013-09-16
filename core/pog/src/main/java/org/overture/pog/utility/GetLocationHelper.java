package org.overture.pog.utility;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.node.INode;

public class GetLocationHelper
{
	
	public static ILexLocation findLocation(INode node){
		GetLocationVisitor visitor = new GetLocationVisitor();
		ILexLocation r;
		try
		{
			r = node.apply(visitor);
		} catch (AnalysisException e)
		{
			r= null;
		}
		
		return r;
	}
	
}
