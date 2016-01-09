package org.overture.codegen.analysis.vdm;

import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.node.INode;

class RenameAnalysis extends DepthFirstAnalysisAdaptor
{
	private Set<Renaming> renamings;
	
	public RenameAnalysis(Set<Renaming> renamings)
	{
		this.renamings = renamings;
	}
	
	@Override
	public void caseAInstanceVariableDefinition(AInstanceVariableDefinition node)
			throws AnalysisException
	{
		handleNameToken(node, node.getName());
		handleNameToken(node, node.getOldname());
		
		node.getExpression().apply(this);
		node.getType().apply(this);
	}
	
	@Override
	public void caseILexNameToken(ILexNameToken node)
			throws AnalysisException
	{
		handleNameToken(node.parent(), node);
	}

	private void handleNameToken(INode parent, ILexNameToken node)
	{
		for (Renaming r : renamings)
		{
			if (node.getLocation().equals(r.getLoc()))
			{
				parent.replaceChild(node, consLexNameToken(node, r.getNewName(), r.getNewModule()));
				break;
			}
		}
	}

	private LexNameToken consLexNameToken(ILexNameToken defName,
			String newName, String newModule)
	{
		LexNameToken newLexName = new LexNameToken(newModule, newName, defName.getLocation(), defName.getOld(), defName.getExplicit());
		newLexName.setTypeQualifier(defName.getTypeQualifier());
		
		return newLexName;
	}
}
