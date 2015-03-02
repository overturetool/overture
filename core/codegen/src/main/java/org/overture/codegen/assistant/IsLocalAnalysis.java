package org.overture.codegen.assistant;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.cgast.SPatternCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.AVarDeclCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.AIdentifierStateDesignatorCG;
import org.overture.codegen.logging.Logger;

public class IsLocalAnalysis extends DepthFirstAnalysisAdaptor
{
	private AIdentifierStateDesignatorCG id;
	private List<String> names;
	private boolean isLocal;

	public IsLocalAnalysis(AIdentifierStateDesignatorCG id)
	{
		this.id = id;
		this.names = new LinkedList<String>();
		this.isLocal = false;
	}
	
	public boolean isLocal()
	{
		AClassDeclCG encClass = id.getAncestor(AClassDeclCG.class);
		
		if(encClass == null)
		{
			Logger.getLog().printErrorln("Could not find enclosing class for "
					+ "node " + id + " in '" + this.getClass().getSimpleName() + "'");
			return false;
		}
		else
		{
			try
			{
				encClass.apply(this);
			} catch (org.overture.codegen.cgast.analysis.AnalysisException e)
			{
				// Exceptions are used to terminate the analysis
			}
		}
		
		return isLocal;
	}
	
	@Override
	public void caseAClassDeclCG(AClassDeclCG node)
			throws org.overture.codegen.cgast.analysis.AnalysisException
	{
		// Only check the methods of the enclosing class
		for(AMethodDeclCG method : node.getMethods())
		{
			method.apply(this);
		}
		
		if(node.getThread() != null)
		{
			node.getThread().apply(this);
		}
	}
	
	
	@Override
	public void caseABlockStmCG(ABlockStmCG node)
			throws org.overture.codegen.cgast.analysis.AnalysisException
	{
		int adds = 0;
		for(AVarDeclCG d : node.getLocalDefs())
		{
			SPatternCG pattern = d.getPattern();
			if(pattern instanceof AIdentifierPatternCG)
			{
				AIdentifierPatternCG idPattern = (AIdentifierPatternCG) pattern;
				names.add(idPattern.getName());
				adds++;
			}
		}
		
		for(SStmCG stm : node.getStatements())
		{
			stm.apply(this);
		}
		

		for(int i = 0; i < adds; i++)
		{
			names.remove(names.size() - 1);
		}
	}
	
	@Override
	public void caseAIdentifierStateDesignatorCG(
			AIdentifierStateDesignatorCG node)
			throws org.overture.codegen.cgast.analysis.AnalysisException
	{
		if(node == id)
		{
			// If the name of the identifier state designator is in the list of local names
			// then it must be local
			isLocal = names.contains(node.getName());
			throw new org.overture.codegen.cgast.analysis.AnalysisException("Stop analysis");
		}
	}
}