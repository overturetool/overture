package org.overture.rename;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.codegen.analysis.vdm.VdmAnalysis;

public class IdOccurencesRenamer  extends VdmAnalysis
{
	private ILexNameToken name;
	private Set<AIdentifierPattern> idOccurences;
	private Consumer<RenameObject> function;
	private String newName;
	
	public IdOccurencesRenamer(ILexNameToken name, INode topNode, Consumer<RenameObject> f, String newName)
	{
		super(topNode);
		this.name = name;
		this.idOccurences = new HashSet<AIdentifierPattern>();
		this.function = f;
		this.newName = newName;
	}

	public Set<AIdentifierPattern> getIdOccurences()
	{
		return idOccurences;
	}

	@Override
	public void caseAIdentifierPattern(AIdentifierPattern node)
			throws AnalysisException
	{
		if (proceed(node))
		{
			if (node.getName().equals(name))
			{
				function.accept(new RenameObject(node.getName(), newName, node::setName));
				idOccurences.add(node);
			}
		}
	}
}
