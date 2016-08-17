package org.overture.codegen.analysis.vdm;

import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.intf.lex.ILexIdentifierToken;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexIdentifierToken;
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
	public void caseAInstanceVariableDefinition(
			AInstanceVariableDefinition node) throws AnalysisException
	{
		handleNameToken(node, node.getName());
		handleNameToken(node, node.getOldname());

		node.getExpression().apply(this);
		node.getType().apply(this);
	}

	@Override
	public void caseILexNameToken(ILexNameToken node) throws AnalysisException
	{
		handleNameToken(node.parent(), node);
	}

	@Override
	public void caseILexIdentifierToken(ILexIdentifierToken node)
			throws AnalysisException
	{
		handleLexIdToken(node.parent(), node);
	}

	private void handleNameToken(INode parent, ILexNameToken node)
	{
		Renaming r = findRenaming(node.getLocation());

		if (r != null)
		{
			parent.replaceChild(node, consLexNameToken(node, r.getNewName(), r.getNewModule()));
		}
	}

	private void handleLexIdToken(INode parent, ILexIdentifierToken node)
	{
		Renaming r = findRenaming(node.getLocation());

		if (r != null)
		{
			parent.replaceChild(node, consLexIdToken(node, r.getNewName()));
		}
	}

	private Renaming findRenaming(ILexLocation loc)
	{
		for (Renaming r : renamings)
		{
			if (loc.equals(r.getLoc()))
			{
				return r;
			}
		}

		return null;
	}

	private LexIdentifierToken consLexIdToken(ILexIdentifierToken defName,
			String newName)
	{
		return new LexIdentifierToken(newName, defName.getOld(), defName.getLocation());
	}

	private LexNameToken consLexNameToken(ILexNameToken defName, String newName,
			String newModule)
	{
		LexNameToken newLexName = new LexNameToken(newModule, newName, defName.getLocation(), defName.getOld(), defName.getExplicit());
		newLexName.setTypeQualifier(defName.getTypeQualifier());

		return newLexName;
	}
}
