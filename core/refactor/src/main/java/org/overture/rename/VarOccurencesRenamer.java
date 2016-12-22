package org.overture.rename;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.intf.lex.ILexLocation;

public class VarOccurencesRenamer extends DepthFirstAnalysisAdaptor
{
	private ILexLocation defLoc;
	private Set<AVariableExp> varOccurences;
	private Consumer<RenameObject> function;
	private String newName;
	public VarOccurencesRenamer(ILexLocation defLoc, Consumer<RenameObject> f, String newName)
	{
		this.defLoc = defLoc;
		this.varOccurences = new HashSet<AVariableExp>();
		this.function = f;
		this.newName = newName;
	}

	public Set<AVariableExp> getVars()
	{
		return varOccurences;
	}

	@Override
	public void caseAVariableExp(AVariableExp node) throws AnalysisException
	{
		if (node.getVardef() == null)
		{
			return;
		}

		if (node.getVardef().getLocation().equals(defLoc))
		{
			function.accept(new RenameObject(node.getName(), newName, node::setName));
			node.setOriginal(newName);
			varOccurences.add(node);
		}
	}
}
