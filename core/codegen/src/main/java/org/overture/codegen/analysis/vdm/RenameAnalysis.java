package org.overture.codegen.analysis.vdm;

import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameToken;

class RenameAnalysis extends DepthFirstAnalysisAdaptor
{
	private List<Renaming> renamings;
	
	public RenameAnalysis(List<Renaming> renamings)
	{
		this.renamings = renamings;
	}
	
	@Override
	public void caseILexNameToken(ILexNameToken node)
			throws AnalysisException
	{
		for (Renaming r : renamings)
		{
			if (node.getLocation().equals(r.getLoc()))
			{
				node.parent().replaceChild(node, consLexNameToken(node, r.getNewName()));
			}
		}
	}

	private LexNameToken consLexNameToken(ILexNameToken defName,
			String newName)
	{
		LexNameToken newLexName = new LexNameToken(defName.getModule(), newName, defName.getLocation(), defName.getOld(), defName.getExplicit());
		newLexName.setTypeQualifier(defName.getTypeQualifier());
		
		return newLexName;
	}
}
