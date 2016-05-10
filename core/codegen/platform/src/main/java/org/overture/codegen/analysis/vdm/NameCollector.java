package org.overture.codegen.analysis.vdm;

import java.util.HashSet;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.intf.lex.ILexNameToken;

public class NameCollector extends DepthFirstAnalysisAdaptor
{
	private Set<String> names;

	public NameCollector()
	{
		this.names = new HashSet<>();
	}
	
	public Set<String> namesToAvoid()
	{
		return names;
	}

	@Override
	public void inILexNameToken(ILexNameToken node) throws AnalysisException
	{
		names.add(node.getName());
	}
}
