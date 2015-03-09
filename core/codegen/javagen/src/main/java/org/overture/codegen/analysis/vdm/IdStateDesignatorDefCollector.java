package org.overture.codegen.analysis.vdm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AAssignmentDefinition;
import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.AThreadDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.node.INode;
import org.overture.ast.statements.ABlockSimpleBlockStm;
import org.overture.ast.statements.AIdentifierStateDesignator;
import org.overture.ast.statements.PStm;

/**
 * Computes the definitions of identifier state designators
 * 
 * @author pvj
 *
 */
public class IdStateDesignatorDefCollector extends VdmAnalysis
{
	private List<PDefinition> defsInScope;
	private Map<AIdentifierStateDesignator, PDefinition> idDefs;
	private Set<INode> visited;
	
	public IdStateDesignatorDefCollector(INode topNode)
	{
		super(topNode);
		this.defsInScope = new LinkedList<PDefinition>();
		this.idDefs = new HashMap<>();
		this.visited = new HashSet<>();
	}
	
	public static Map<AIdentifierStateDesignator, PDefinition> getIdDefs(List<SClassDefinition> classes) throws AnalysisException
	{
		Map<AIdentifierStateDesignator, PDefinition> allDefs = new HashMap<>();
		
		for(SClassDefinition clazz : classes)
		{
			IdStateDesignatorDefCollector collector = new IdStateDesignatorDefCollector(clazz);
			clazz.apply(collector);
			allDefs.putAll(collector.idDefs);
		}
		
		return allDefs;
	}
	
	@Override
	public void caseAClassClassDefinition(AClassClassDefinition node)
			throws AnalysisException
	{
		if(!proceed(node))
		{
			return;
		}
		
		// Instance variables and values are visible to all operations
		for(int i = 0; i < node.getDefinitions().size(); i++)
		{
			PDefinition def = node.getDefinitions().get(i);
			
			while(def instanceof AInheritedDefinition)
			{
				def = ((AInheritedDefinition) def).getSuperdef();
			}
			
			if(def instanceof AInstanceVariableDefinition || def instanceof AValueDefinition)
			{
				defsInScope.add(def);
			}
		}
		
		for(PDefinition def : node.getDefinitions())
		{
			// Check only explicit operations or threads within the enclosing class
			if(def instanceof AExplicitOperationDefinition || def instanceof AThreadDefinition)
			{
				def.apply(this);
			}
		}
	}
	
	@Override
	public void caseABlockSimpleBlockStm(ABlockSimpleBlockStm node)
			throws AnalysisException
	{
		if(!proceed(node))
		{
			return;
		}
		
		int adds = 0;
		for(AAssignmentDefinition d : node.getAssignmentDefs())
		{
			defsInScope.add(d);
			adds++;
		}
		
		for(PStm stm : node.getStatements())
		{
			stm.apply(this);
		}
		
		for(int i = 0; i < adds; i++)
		{
			defsInScope.remove(defsInScope.size() - 1);
		}
	}
	
	@Override
	public void caseAIdentifierStateDesignator(AIdentifierStateDesignator node)
			throws AnalysisException
	{
		if(!proceed(node))
		{
			return;
		}
		
		for(int i = defsInScope.size() - 1; i >= 0; i--)
		{
			PDefinition nextDef = defsInScope.get(i);
			
			if(node.getName().equals(nextDef.getName()))
			{
				this.idDefs.put(node, nextDef);
				break;
			}
		}
	}
	
	@Override
	protected boolean proceed(INode node)
	{
		if(visited.contains(node))
		{
			return false;
		}
		else
		{
			visited.add(node);
			return super.proceed(node);
		}
	}
}
