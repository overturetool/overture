package org.overture.codegen.analysis.vdm;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.intf.lex.ILexNameToken;

public class NameCollector extends DepthFirstAnalysisAdaptor
{
	private List<String> names;

	public NameCollector()
	{
		this.names = new LinkedList<String>();
	}
	
	public List<String> namesToAvoid()
	{
		return names;
	}

	@Override
	public void inILexNameToken(ILexNameToken node) throws AnalysisException
	{
		String name = node.getName();
		
		if (!names.contains(name))
		{
			names.add(name);
		}
	}
}
