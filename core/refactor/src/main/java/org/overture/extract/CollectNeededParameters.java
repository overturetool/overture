package org.overture.extract;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.node.INode;
import org.overture.ast.statements.ABlockSimpleBlockStm;
import org.overture.codegen.analysis.vdm.DefinitionInfo;
import org.overture.codegen.analysis.vdm.VarOccurencesCollector;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class CollectNeededParameters extends DepthFirstAnalysisAdaptor {

	private ITypeCheckerAssistantFactory af;
	private int from;
	private int to;
	private static List<PDefinition> neededParameters = new ArrayList<PDefinition>();

	public CollectNeededParameters(ITypeCheckerAssistantFactory af, int from, int to)
	{
		this.af = af;
		this.from = from;
		this.to = to;
	}
	
	public static List<PDefinition> openScope(DefinitionInfo defInfo, INode defScope, int from, int to)
			throws AnalysisException
	{
		
		List<? extends PDefinition> nodeDefs = defInfo.getNodeDefs();

		for (int i = 0; i < nodeDefs.size(); i++)
		{
			PDefinition parentDef = nodeDefs.get(i);

			List<? extends PDefinition> localDefs = defInfo.getLocalDefs(parentDef);

			for (int j = 0; j < localDefs.size(); j++) // check if it matches position
			{
				if(!ExtractUtil.isInRange(localDefs.get(j).getLocation(), from, to) && checkVarOccurences(localDefs.get(j).getLocation(), defScope, from, to)){
					
					PDefinition pDef = localDefs.get(j).clone();
					pDef.setLocation(null);
					pDef.setName(pDef.getName().clone());
					neededParameters.add(pDef);
				}
			}
		}
		
		return neededParameters;
	}
	
	@Override
	public void caseABlockSimpleBlockStm(ABlockSimpleBlockStm node) throws AnalysisException {
		DefinitionInfo defInfo = new DefinitionInfo(node.getAssignmentDefs(), af);
		CollectNeededParameters.openScope(defInfo, node, from, to);
	}
	
	
	private static boolean checkVarOccurences(ILexLocation defLoc,
			INode defScope, int from, int to) throws AnalysisException
	{
		VarOccurencesCollector collector = new VarOccurencesCollector(defLoc);
		defScope.apply(collector);
		Set<AVariableExp> setOfVars = collector.getVars();
		
		for(Iterator<AVariableExp> i = setOfVars.iterator(); i.hasNext(); ) {
			AVariableExp item = i.next();
			if(ExtractUtil.isInRange(item.getLocation(), from, to)){
				return true;
			}
		}
		return false;
	}
	
	public List<PDefinition> getNeededParameters(){
		return neededParameters;
	}
	
}
