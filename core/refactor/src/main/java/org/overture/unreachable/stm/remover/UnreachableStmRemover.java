package org.overture.unreachable.stm.remover;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.definitions.AAssignmentDefinition;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.node.INode;
import org.overture.ast.statements.ABlockSimpleBlockStm;
import org.overture.ast.statements.ALetStm;
import org.overture.ast.statements.PStm;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.AVoidType;
import org.overture.ast.types.PType;

public class UnreachableStmRemover extends DepthFirstAnalysisAdaptor
{
	private List<Removal> allRemovals = new LinkedList<Removal>();
	private OccurrenceCollector collector = new OccurrenceCollector();
	
	@Override
	public void caseABlockSimpleBlockStm(ABlockSimpleBlockStm node)
			throws AnalysisException
	{
		List<Integer> unreachStmIndices = new LinkedList<Integer>();

		boolean notreached = false;

		for (int i = 0; i < node.getStatements().size(); i++)
		{
			PStm stmt = node.getStatements().get(i);
			stmt.apply(this);
			PType stype = stmt.getType();

			if (notreached)
			{
				if(stmt.getLocation() != null){
					allRemovals.add(new Removal(stmt.getLocation(), stmt.toString()));
				} else{
					allRemovals.add(new Removal(node.getLocation(), stmt.toString()));
				}
				unreachStmIndices.add(i);
			} else
			{
				notreached = true;

				if (stype instanceof AUnionType)
				{
					AUnionType ust = (AUnionType) stype;

					for (PType t : ust.getTypes())
					{
						if (t instanceof AVoidType || t instanceof AUnknownType)
						{
							notreached = false;
						}
					}
				} else
				{
					if (stype == null || stype instanceof AVoidType
							|| stype instanceof AUnknownType)
					{
						notreached = false;
					}
				}
			}
		}

		// Go backwards to not corrupt the stored indices
		for (int i = unreachStmIndices.size() - 1; i >= 0; i--)
		{
			node.getStatements().remove(unreachStmIndices.get(i).intValue());
		}
		//TODO Check warning
		LinkedList<AAssignmentDefinition> assignmentDefs = (LinkedList<AAssignmentDefinition>) node.getAssignmentDefs().clone();
		
		for (int i = 0; i < node.getAssignmentDefs().size(); i++) {
			AAssignmentDefinition def = node.getAssignmentDefs().get(i);
			collector.init(def.getLocation());
			node.apply(collector);
			if(!collector.isFoundUsage()){
				allRemovals.add(new Removal(node.getLocation(), node.toString()));
				assignmentDefs.remove(def);
			}
		}
		node.setAssignmentDefs(assignmentDefs);
		
	}
	
	public List<Removal> getAllRemovals()
	{
		return allRemovals;
	}

	@Override
	public void caseAValueDefinition(AValueDefinition node) throws AnalysisException {
		
		collector.init(node.getLocation());
		node.parent().apply(collector);

		if(!collector.isFoundUsage()){
			if(node.parent() instanceof ALetStm){
				ALetStm parent = (ALetStm) node.parent();
				parent.getLocalDefs().remove(node);
				if(parent.getLocalDefs().size() < 1){
					
					if(parent.parent() instanceof ABlockSimpleBlockStm){
						ABlockSimpleBlockStm grandparent = (ABlockSimpleBlockStm) parent.parent();
						grandparent.getStatements().remove(parent);
						grandparent.getStatements().add(parent.getStatement());
					}
					if(parent.parent() instanceof AExplicitOperationDefinition){
						AExplicitOperationDefinition grandparent = (AExplicitOperationDefinition) parent.parent();
						grandparent.setBody(parent.getStatement());
					}
				}
			}
		} 
	}
}
