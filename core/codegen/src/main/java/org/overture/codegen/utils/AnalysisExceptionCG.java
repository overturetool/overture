package org.overture.codegen.utils;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.intf.lex.ILexLocation;

public class AnalysisExceptionCG extends AnalysisException
{
	private static final long serialVersionUID = 2717706867333516092L;
	
	private ILexLocation location;

	public AnalysisExceptionCG(String message, ILexLocation location)
	{
		super(message);
		this.location = location;
	}

	public AnalysisExceptionCG(String message)
	{
		super(message);
		this.location = null;
	}

	public ILexLocation getLocation()
	{
		return location;
	}
}
