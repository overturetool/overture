package org.overture.rename;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.AApplyExp;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.expressions.PExpBase;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.node.INode;
import org.overture.ast.statements.ACallStm;
import org.overture.ast.types.PType;

public class ApplyOccurenceRenamer extends DepthFirstAnalysisAdaptor {
	private ILexLocation defLoc;
	private Set<AApplyExp> applyOccurences;
	private Consumer<RenameObject> function;
	private String newName;
	
	public ApplyOccurenceRenamer(ILexLocation defLoc, Consumer<RenameObject> f, String newName)
	{
		this.defLoc = defLoc;
		this.applyOccurences = new HashSet<AApplyExp>();
		this.function = f;
		this.newName = newName;
	}

	public Set<AApplyExp> getApplications()
	{
		return applyOccurences;
	}
	
	@Override
	public void caseAApplyExp(AApplyExp node) throws AnalysisException
	{
		if (node.getRoot() == null)
		{
			return;
		}
		AValueDefinition nodeToRename = node.getAncestor(AValueDefinition.class);
		AVariableExp ancestor = node.getRoot().getAncestor(AVariableExp.class);
		PDefinition operation = ancestor.getVardef();
		
		if (operation.getLocation().equals(defLoc))
		{
			//function.accept(new RenameObject(nodeToRename.getName(), newName, nodeToRename::setName));
			applyOccurences.add(node);
		}
	}
}	
