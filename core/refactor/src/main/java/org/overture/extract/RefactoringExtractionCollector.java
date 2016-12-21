package org.overture.extract;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.ANamedTraceDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SFunctionDefinition;
import org.overture.ast.definitions.SOperationDefinition;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.ast.util.modules.CombinedDefaultModule;
import org.overture.refactoring.RefactoringLogger;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class RefactoringExtractionCollector  extends DepthFirstAnalysisAdaptor
{
	private PDefinition enclosingDef;
	private int enclosingCounter;
	private Set<String> namesToAvoid;
	private AModuleModules currentModule;
	private Logger log = Logger.getLogger(this.getClass().getSimpleName());
	private int from;
	private int to;
	private String extractedName;
	private List<INode> visitedOperations;
	private AExplicitOperationDefinition extractedOperation;
	private boolean replaceDuplicates;
	private RefactoringLogger<Extraction> refactoringLogger;
	private NameCollector nameCollector;
	
	public RefactoringExtractionCollector(ITypeCheckerAssistantFactory af)
	{
		this.enclosingDef = null;
		this.enclosingCounter = 0;
		this.visitedOperations = new ArrayList<INode>();
		this.namesToAvoid = new HashSet<String>();
		this.currentModule = null;
		this.extractedOperation = null;
		refactoringLogger = new RefactoringLogger<Extraction>(); 
		nameCollector = new NameCollector(af);
	}

	@Override
	public void caseAModuleModules(AModuleModules node) throws AnalysisException
	{
		if (enclosingDef != null)
		{
			return;
		}
		
		if(node instanceof CombinedDefaultModule)
		{
			for(AModuleModules m : ((CombinedDefaultModule) node).getModules())
			{
				m.apply(THIS);
			}
		}
		else 
		{
			currentModule = node;
			node.apply(nameCollector);
			visitModuleDefs(node.getDefs(), node);
		}
	}

	@Override
	public void caseAExplicitOperationDefinition(
			AExplicitOperationDefinition node) throws AnalysisException
	{
		if (!proceed(node))
		{
			return;
		}
		
		performExtraction(node);
	}

	private void performExtraction(AExplicitOperationDefinition node) throws AnalysisException {
		if(extractedOperation == null){
			
			if(!nameCollector.checkNameNotInUse(extractedName)){
				
				BodyOccurrenceCollector bodyCollector = new BodyOccurrenceCollector(node, currentModule, from, to, extractedName, refactoringLogger);
				node.getBody().apply(bodyCollector);
				if(bodyCollector.getToOperation() != null){
					extractedOperation = bodyCollector.getToOperation();
					currentModule.apply(THIS);
				}
			 
				if(replaceDuplicates){
					if(extractedOperation != null && !visitedOperations.contains(node)){
						DuplicateOccurrenceCollector dubCollector = new DuplicateOccurrenceCollector(node, extractedOperation, from, to, extractedName, currentModule, refactoringLogger);
						node.getBody().apply(dubCollector);
						visitedOperations.add(node);
					}
				}
			}
		} else {
			refactoringLogger.addWarning("Name in use!");
		}
	}
		
	@Override
	public void caseAExplicitFunctionDefinition(
			AExplicitFunctionDefinition node) throws AnalysisException
	{
		if (!proceed(node))
		{
			return;
		}
	}

	
	private void visitModuleDefs(List<PDefinition> defs, INode module)
			throws AnalysisException
	{
		handleExecutables(defs);
	}
	
	private void handleExecutables(List<PDefinition> defs)
			throws AnalysisException
	{
		for (PDefinition def : new LinkedList<>(defs))
		{
			if (def instanceof SOperationDefinition
					|| def instanceof SFunctionDefinition
					|| def instanceof ANamedTraceDefinition)
			{
				enclosingDef = def;
				enclosingCounter = 0;

				def.apply(this);
			}
		}
	}

	public void init(boolean clearExtracions)
	{
		this.enclosingDef = null;
		this.enclosingCounter = 0;
		this.namesToAvoid.clear();
		if(clearExtracions){
			refactoringLogger.clear();
			nameCollector.init();
		}
	}

	public Set<Extraction> getExtractions()
	{
		return refactoringLogger.get();
	}

	private boolean proceed(INode node)
	{
		if (node == enclosingDef)
		{
			enclosingCounter++;
		}

		if (enclosingCounter > 1)
		{
			// To protect against recursion
			return false;
		}

		PDefinition def = node.getAncestor(SOperationDefinition.class);

		if (def == null)
		{
			def = node.getAncestor(SFunctionDefinition.class);

			if (def == null)
			{
				def = node.getAncestor(ANamedTraceDefinition.class);

				if (def == null)
				{
					def = node.getAncestor(AValueDefinition.class);

					if (def == null)
					{
						def = node.getAncestor(AInstanceVariableDefinition.class);

						if (def == null)
						{
							def = node.getAncestor(ATypeDefinition.class);

							if (def == null)
							{
								def = node.getAncestor(AStateDefinition.class);
							}
						}
					}
				}
			}
		}

		if (def == null)
		{
			log.error("Got unexpected definition: " + enclosingDef);
		}

		return enclosingDef == def;
	}
	
	public void setRefactoringParameters(String[] parameters) {
		if(parameters.length >= 3){
			this.from = Integer.parseInt(parameters[0]);
			this.to = Integer.parseInt(parameters[1]);
			this.extractedName = parameters[2];
			this.replaceDuplicates = false;
		}
		if(parameters.length >= 4){
			this.replaceDuplicates = true;
		}
	}
}
