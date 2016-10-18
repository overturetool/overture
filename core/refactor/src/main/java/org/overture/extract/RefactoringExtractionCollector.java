package org.overture.extract;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.ANamedTraceDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.ASystemClassDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SFunctionDefinition;
import org.overture.ast.definitions.SOperationDefinition;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.ast.util.modules.CombinedDefaultModule;
import org.overture.codegen.analysis.vdm.NameCollector;
import org.overture.codegen.ir.TempVarNameGen;

public class RefactoringExtractionCollector  extends DepthFirstAnalysisAdaptor
{

	private PDefinition enclosingDef;
	private int enclosingCounter;

	private Set<String> namesToAvoid;
	private TempVarNameGen nameGen;
	private AModuleModules currentModule;
	private Logger log = Logger.getLogger(this.getClass().getSimpleName());
	private int from;
	private int to;
	private String extractedName;
	
	private List<INode> visitedOperations;
	private AExplicitOperationDefinition extractedOperation;
	private boolean replaceDuplicates;
	
	public RefactoringExtractionCollector()
	{
		this.enclosingDef = null;
		this.enclosingCounter = 0;
		this.visitedOperations = new ArrayList<INode>();
		this.namesToAvoid = new HashSet<String>();
		this.nameGen = new TempVarNameGen();
		this.currentModule = null;
		this.extractedOperation = null;
		ExtractionLog.clearExtractions();
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
			visitModuleDefs(node.getDefs(), node);
		}
	}

	@Override
	public void caseAClassClassDefinition(AClassClassDefinition node)
			throws AnalysisException
	{
		if (enclosingDef != null)
		{
			return;
		}

		visitModuleDefs(node.getDefinitions(), node);
	}

	@Override
	public void caseASystemClassDefinition(ASystemClassDefinition node)
			throws AnalysisException
	{
		if (enclosingDef != null)
		{
			return;
		}

		visitModuleDefs(node.getDefinitions(), node);
	}

	// For operations and functions it works as a single pattern
	// Thus f(1,mk_(2,2),5) will fail
	// public f : nat * (nat * nat) * nat -> nat
	// f (b,mk_(b,b), a) == b;

	
	@Override
	public void caseAExplicitOperationDefinition(
			AExplicitOperationDefinition node) throws AnalysisException
	{
		if (!proceed(node))
		{
			return;
		}
		if(extractedOperation == null){
			BodyOccurrenceCollector bodyCollector = new BodyOccurrenceCollector(node, currentModule, from, to, extractedName);
			node.getBody().apply(bodyCollector);
			if(bodyCollector.getToOperation() != null){
				extractedOperation = bodyCollector.getToOperation();
				currentModule.apply(THIS);
			}
		} 
		if(replaceDuplicates){
			if(extractedOperation != null && !visitedOperations.contains(node)){
				DuplicateOccurrenceCollector dubCollector = new DuplicateOccurrenceCollector(node, extractedOperation, from, to, extractedName, currentModule);
				node.getBody().apply(dubCollector);
				visitedOperations.add(node);
			}
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

	
	public String computeNewName(String original)
	{
		String prefix = original + "_";
		String newNameSuggestion = nameGen.nextVarName(prefix);

		while (namesToAvoid.contains(newNameSuggestion))
		{
			newNameSuggestion = nameGen.nextVarName(prefix);
		}

		namesToAvoid.add(newNameSuggestion);

		return newNameSuggestion;
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
				setNamesToAvoid(def); //TODO could be removed
				this.nameGen = new TempVarNameGen();

				def.apply(this);
			}
		}
	}

	private void setNamesToAvoid(PDefinition def) throws AnalysisException
	{
		NameCollector collector = new NameCollector();
		def.apply(collector);
		namesToAvoid = collector.namesToAvoid();
	}

	public void init(boolean clearExtracions)
	{
		this.enclosingDef = null;
		this.enclosingCounter = 0;
		this.namesToAvoid.clear();
		this.nameGen = new TempVarNameGen();
		if(clearExtracions){
			ExtractionLog.clearExtractions();
		}
	}

	public Set<Extraction> getExtractions()
	{
		return ExtractionLog.getExtractions();
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
