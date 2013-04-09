package org.overture.ide.debug.utils;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.ast.statements.PStm;

public class ExecutableAnalysis extends DepthFirstAnalysisAdaptor
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5353696074294132014L;
	private final int searchLine;
	
	private String module = null;
	
	private ExecutableAnalysis(int searchLine, String module)
	{
		this.searchLine = searchLine;
		this.module = module;
	}
	
	public static boolean isExecutable(INode node, int line, boolean findModule)
	{
		
		String nodeModule = findModule ? searchForModule(node) : null;
				
		ExecutableAnalysis analysis = new ExecutableAnalysis(line, nodeModule);

		try
		{
			node.apply(analysis);
		} catch (ExecutableAnalysisException e)
		{
			return e.isExecutable();
			
		} catch (AnalysisException e)
		{
			e.printStackTrace();
			return false;
		}

		return false;
	}
	
	private static String searchForModule(INode node)
	{
		
		String nodeModule = null;
		
		SClassDefinition classDef = node.getAncestor(SClassDefinition.class); 
		
		if(classDef != null)
		{
			nodeModule = classDef.getLocation().module;
		}
		else
		{
			AModuleModules slModule = node.getAncestor(AModuleModules.class);
			
			if(slModule != null)
			{
				nodeModule = slModule.getName().name;
			}
		}
		
		return nodeModule;
	}
	
	private boolean isValidModule(PStm node){
		
		return module == null || (node.getLocation().startLine == searchLine &&  module.equals(node.getLocation().module));
	}
	
	private boolean isValidModule(PExp node){
		
		return module == null || (node.getLocation().startLine == searchLine &&  module.equals(node.getLocation().module));
	}

	public void defaultInPStm(PStm node) throws ExecutableAnalysisException
	{
		if (isValidModule(node))
		{
			throw new ExecutableAnalysisException(true);
		}
	}

	public void defaultInPExp(PExp node) throws ExecutableAnalysisException
	{
		if (isValidModule(node))
		{
			throw new ExecutableAnalysisException(true);
		}
	}
}
