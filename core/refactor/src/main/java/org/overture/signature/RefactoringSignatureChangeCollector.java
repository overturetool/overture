package org.overture.signature;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;
import java.util.function.Consumer;

import org.apache.log4j.Logger;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.ALocalDefinition;
import org.overture.ast.definitions.ANamedTraceDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.ASystemClassDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SFunctionDefinition;
import org.overture.ast.definitions.SOperationDefinition;
import org.overture.ast.definitions.traces.ALetBeStBindingTraceDefinition;
import org.overture.ast.expressions.ACaseAlternative;
import org.overture.ast.expressions.ACasesExp;
import org.overture.ast.expressions.AExistsExp;
import org.overture.ast.expressions.AForAllExp;
import org.overture.ast.expressions.ALambdaExp;
import org.overture.ast.expressions.ALetBeStExp;
import org.overture.ast.expressions.ALetDefExp;
import org.overture.ast.expressions.AMapCompMapExp;
import org.overture.ast.expressions.ASetCompSetExp;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.statements.ABlockSimpleBlockStm;
import org.overture.ast.statements.ACallStm;
import org.overture.ast.statements.ACaseAlternativeStm;
import org.overture.ast.statements.ACasesStm;
import org.overture.ast.statements.AForPatternBindStm;
import org.overture.ast.statements.AIdentifierStateDesignator;
import org.overture.ast.statements.ALetBeStStm;
import org.overture.ast.statements.ALetStm;
import org.overture.ast.statements.ATixeStm;
import org.overture.ast.statements.ATixeStmtAlternative;
import org.overture.ast.statements.ATrapStm;
import org.overture.ast.statements.PStm;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.PType;
import org.overture.ast.util.modules.CombinedDefaultModule;
import org.overture.codegen.analysis.vdm.DefinitionInfo;
import org.overture.codegen.analysis.vdm.NameCollector;
import org.overture.codegen.analysis.vdm.Renaming;
import org.overture.codegen.analysis.vdm.VarOccurencesCollector;
import org.overture.codegen.ir.TempVarNameGen;
import org.overture.extract.BodyOccurrenceCollector;
import org.overture.rename.CallOccurenceRenamer;
import org.overture.rename.RenameObject;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.definition.SFunctionDefinitionAssistantTC;

public class RefactoringSignatureChangeCollector extends DepthFirstAnalysisAdaptor {
	private ITypeCheckerAssistantFactory af;

	private PDefinition enclosingDef;
	private Map<AIdentifierStateDesignator, PDefinition> idDefs;
	private Stack<ILexNameToken> localDefsInScope;
	private int enclosingCounter;

	private Set<SignatureChange> signatureChanges;
	private Set<String> namesToAvoid;
	private TempVarNameGen nameGen;
	private AModuleModules currentModule;
	private Logger log = Logger.getLogger(this.getClass().getSimpleName());
	private String[] parameters;
	private String changeType;
	private int from;
	private int to;
	private String paramType;
	private String paramName;
	
	public RefactoringSignatureChangeCollector(ITypeCheckerAssistantFactory af,
			Map<AIdentifierStateDesignator, PDefinition> idDefs)
	{
		this.af = af;

		this.enclosingDef = null;
		this.idDefs = idDefs;
		this.localDefsInScope = new Stack<ILexNameToken>();
		this.enclosingCounter = 0;

		this.signatureChanges = new HashSet<SignatureChange>();
		this.namesToAvoid = new HashSet<String>();
		this.nameGen = new TempVarNameGen();
		this.currentModule = null;
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
		
		if(compareNodeLocation(node.getLocation())){
			if(changeType.equals("rmv")){
				findParametersToRemove(node,node.parent(),node.parent());
			}
			if(changeType.equals("add")){
				PDefinition newParam = node.getParamDefinitions().getFirst().clone();
				LexNameToken parName = new LexNameToken(node.getName().getModule(),paramName,null);
				newParam.setName(parName);
				
				//Set type of parameter
				ABooleanBasicType paramType = new ABooleanBasicType();
				if(paramType instanceof PType){
					newParam.setType((PType)paramType);
				}
				//Add parameter to node definitions
				node.getParamDefinitions().add(newParam);
				
				//Add parameter to node patterns
				AIdentifierPattern pat = new AIdentifierPattern();
				pat.setName(parName);
				node.getParameterPatterns().add(pat);
				
				//Add parameter to node type
				PType nodeType = node.getType();
				if(nodeType instanceof AOperationType){
					((AOperationType) nodeType).getParameters().add(paramType);
				}
				
				//Update calls
				signatureChangeCallOccurences(node.getLocation(), node.parent(), this::registerSignatureChange);
				
				
				System.out.println("test");
			}
		}
		
		
		
		
		//TODO operation
		//BodyOccurrenceCollector bodyCollector = new BodyOccurrenceCollector(node, currentModule, from, to, signatureName);
		//node.getBody().apply(bodyCollector);
		
		//RemoveOccurrenceCollector removeCollector = new RemoveOccurrenceCollector(node, currentModule, loc);
		
		
	}
	
	private Set<ACallStm> signatureChangeCallOccurences(ILexLocation defLoc, INode defScope,
			Consumer<SignatureChangeObject> function) throws AnalysisException
	{
		CallOccurrenceSignatureChanger collector = new CallOccurrenceSignatureChanger(defLoc, function);
		defScope.apply(collector);
		return collector.getCalls();
	}

	private void registerSignatureChange(RenameObject reObj)
	{
		if (!contains(reObj.name.getLocation()))
		{
			LexNameToken token = new LexNameToken(reObj.name.getModule(), reObj.newName, reObj.name.getLocation());
			signatureChanges.add(new SignatureChange(reObj.name.getLocation(), reObj.name.getName(), reObj.newName, reObj.name.getModule(), reObj.name.getModule()));
			reObj.function.accept(token);
		}
	}
	
	private boolean contains(ILexLocation loc)
	{
		for (SignatureChange r : signatureChanges)
		{
			if (r.getLoc().equals(loc))
			{
				return true;
			}
		}

		return false;
	}
		
	private void findParametersToRemove(AExplicitOperationDefinition opToRemoveParamFrom, INode parentNode,
			INode defScope) throws AnalysisException {
		if (opToRemoveParamFrom.getName() == null)
		{
			return;
		}
		
		if(parameters.length >= 3){
			
			//Check this 
			DefinitionInfo defInfo = new DefinitionInfo(opToRemoveParamFrom.getParamDefinitions(), af);
			openScope(defInfo, opToRemoveParamFrom);
			opToRemoveParamFrom.getBody().apply(this);
			endScope(defInfo);
						
			
//			renameVarOccurences(localDefToRemove.getLocation(), defScope, this::registerRenaming, newName);
//			renameIdDesignatorOccurrences(localDefToRemove.getLocation(), defScope, this::registerRenaming, newName);	
//			renameCallOccurences(localDefToRemove.getLocation(), defScope, this::registerRenaming, newName);
//			
//			if (!contains(localDefToRemove.getName().getLocation()))
//			{
//				registerRenaming(new RenameObject(localDefToRemove.getName(), newName, localDefToRemove::setName));	
//				localDefToRemove.toString();
//			}
//			renameIdOccurences(localDefName, parentNode, this::registerRenaming, newName);
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

		DefinitionInfo defInfo = new DefinitionInfo(getParamDefs(node), af);

		openScope(defInfo, node);

		node.getBody().apply(this);

		endScope(defInfo);
	}

	@Override
	public void caseABlockSimpleBlockStm(ABlockSimpleBlockStm node)
			throws AnalysisException
	{
		if (!proceed(node))
		{
			return;
		}

		DefinitionInfo defInfo = new DefinitionInfo(node.getAssignmentDefs(), af);

		visitDefs(defInfo.getNodeDefs());

		openScope(defInfo, node);

		visitStms(node.getStatements());

		endScope(defInfo);
	}

	@Override
	public void caseALetDefExp(ALetDefExp node) throws AnalysisException
	{
		if (!proceed(node))
		{
			return;
		}

		DefinitionInfo defInfo = new DefinitionInfo(node.getLocalDefs(), af);

		visitDefs(defInfo.getNodeDefs());

		openScope(defInfo, node);

		node.getExpression().apply(this);

		endScope(defInfo);
	}

	@Override
	public void caseALetStm(ALetStm node) throws AnalysisException
	{
		if (!proceed(node))
		{
			return;
		}

		DefinitionInfo defInfo = new DefinitionInfo(node.getLocalDefs(), af);

		visitDefs(defInfo.getNodeDefs());

		openScope(defInfo, node);

		node.getStatement().apply(this);

		endScope(defInfo);
	}

	@Override
	public void caseALetBeStExp(ALetBeStExp node) throws AnalysisException
	{
		if (!proceed(node))
		{
			return;
		}

		node.getDef().apply(this);

		DefinitionInfo defInfo = new DefinitionInfo(node.getDef().getDefs(), af);

		openScope(defInfo, node);

		if (node.getSuchThat() != null)
		{
			node.getSuchThat().apply(this);
		}

		node.getValue().apply(this);

		endScope(defInfo);
	}

	/*
	 * Exists1 needs no treatment it uses only a bind
	 */

	@Override
	public void caseAForAllExp(AForAllExp node) throws AnalysisException
	{
		if (!proceed(node))
		{
			return;
		}

		handleMultipleBindConstruct(node, node.getBindList(), null, node.getPredicate());
	}

	@Override
	public void caseAExistsExp(AExistsExp node) throws AnalysisException
	{
		if (!proceed(node))
		{
			return;
		}

		handleMultipleBindConstruct(node, node.getBindList(), null, node.getPredicate());
	}

	/*
	 * Sequence comp needs no treatment it uses only a bind
	 */

	@Override
	public void caseASetCompSetExp(ASetCompSetExp node) throws AnalysisException
	{
		if (!proceed(node))
		{
			return;
		}

		handleMultipleBindConstruct(node, node.getBindings(), node.getFirst(), node.getPredicate());
	}

	@Override
	public void caseAMapCompMapExp(AMapCompMapExp node) throws AnalysisException
	{
		if (!proceed(node))
		{
			return;
		}

		handleMultipleBindConstruct(node, node.getBindings(), node.getFirst(), node.getPredicate());
	}

	@Override
	public void caseALetBeStStm(ALetBeStStm node) throws AnalysisException
	{
		if (!proceed(node))
		{
			return;
		}

		node.getDef().apply(this);

		DefinitionInfo defInfo = new DefinitionInfo(node.getDef().getDefs(), af);

		openScope(defInfo, node);

		if (node.getSuchThat() != null)
		{
			node.getSuchThat().apply(this);
		}

		node.getStatement().apply(this);

		endScope(defInfo);
	}

	@Override
	public void caseALetBeStBindingTraceDefinition(
			ALetBeStBindingTraceDefinition node) throws AnalysisException
	{
		if (!proceed(node))
		{
			return;
		}

		node.getDef().apply(this);

		DefinitionInfo defInfo = new DefinitionInfo(node.getDef().getDefs(), af);

		openScope(defInfo, node);

		if (node.getStexp() != null)
		{
			node.getStexp().apply(this);
		}

		node.getBody().apply(this);

		endScope(defInfo);
	}

	@Override
	public void caseALambdaExp(ALambdaExp node) throws AnalysisException
	{
		if (!proceed(node))
		{
			return;
		}

		DefinitionInfo defInfo = new DefinitionInfo(node.getParamDefinitions(), af);

		openScope(defInfo, node);

		node.getExpression().apply(this);

		endScope(defInfo);
	}

	@Override
	public void caseATixeStm(ATixeStm node) throws AnalysisException
	{
		if (node.getBody() != null)
		{
			node.getBody().apply(this);
		}

		// The trap alternatives will be responsible for opening/ending the scope
		for (ATixeStmtAlternative trap : node.getTraps())
		{
			trap.apply(this);
		}
	}

	@Override
	public void caseATixeStmtAlternative(ATixeStmtAlternative node)
			throws AnalysisException
	{
		openScope(node.getPatternBind(), node.getPatternBind().getDefs(), node.getStatement());

		node.getStatement().apply(this);

		// End scope
		for (PDefinition def : node.getPatternBind().getDefs())
		{
			removeLocalDefFromScope(def);
		}
	}

	@Override
	public void caseATrapStm(ATrapStm node) throws AnalysisException
	{
		if (!proceed(node))
		{
			return;
		}

		if (node.getBody() != null)
		{
			node.getBody().apply(this);
		}

		openScope(node.getPatternBind().getPattern(), node.getPatternBind().getDefs(), node.getWith());

		if (node.getWith() != null)
		{
			node.getWith().apply(this);
		}

		for (PDefinition def : node.getPatternBind().getDefs())
		{
			removeLocalDefFromScope(def);
		}
	}

	@Override
	public void caseAForPatternBindStm(AForPatternBindStm node)
			throws AnalysisException
	{
		if (!proceed(node))
		{
			return;
		}

		if (node.getExp() != null)
		{
			node.getExp().apply(this);
		}

		openScope(node.getPatternBind().getPattern(), node.getPatternBind().getDefs(), node.getStatement());

		node.getStatement().apply(this);

		for (PDefinition def : node.getPatternBind().getDefs())
		{
			removeLocalDefFromScope(def);
		}
	}

	@Override
	public void caseACasesStm(ACasesStm node) throws AnalysisException
	{
		if (!proceed(node))
		{
			return;
		}

		handleCaseNode(node.getExp(), node.getCases(), node.getOthers());
	}

	@Override
	public void caseACasesExp(ACasesExp node) throws AnalysisException
	{
		if (!proceed(node))
		{
			return;
		}

		handleCaseNode(node.getExpression(), node.getCases(), node.getOthers());
	}

	@Override
	public void caseACaseAlternativeStm(ACaseAlternativeStm node)
			throws AnalysisException
	{
		if (!proceed(node))
		{
			return;
		}

		handleCase(node.getDefs(), node.getPattern(), node.getResult());
	}

	@Override
	public void caseACaseAlternative(ACaseAlternative node)
			throws AnalysisException
	{
		if (!proceed(node))
		{
			return;
		}

		handleCase(node.getDefs(), node.getPattern(), node.getResult());
	}

	@Override
	public void caseILexNameToken(ILexNameToken node) throws AnalysisException
	{
		// No need to visit names
	}

	private void handleCaseNode(PExp cond, List<? extends INode> cases,
			INode others) throws AnalysisException
	{
		if (cond != null)
		{
			cond.apply(this);
		}

		// The cases will be responsible for opening/ending the scope
		for (INode c : cases)
		{
			c.apply(this);
		}

		if (others != null)
		{
			others.apply(this);
		}
	}

	private void handleCase(LinkedList<PDefinition> localDefs, PPattern pattern,
			INode result) throws AnalysisException
	{
		// Do not visit the conditional exp (cexp)
		openScope(pattern, localDefs, result);

		result.apply(this);

		// End scope
		for (PDefinition def : localDefs)
		{
			removeLocalDefFromScope(def);
		}
	}

	private void handleMultipleBindConstruct(INode node,
			LinkedList<PMultipleBind> bindings, PExp first, PExp pred)
			throws AnalysisException
	{

		DefinitionInfo defInfo = new DefinitionInfo(getMultipleBindDefs(bindings), af);

		openScope(defInfo, node);

		if (first != null)
		{
			first.apply(this);
		}

		if (pred != null)
		{
			pred.apply(this);
		}

		endScope(defInfo);
	}

	private List<PDefinition> getMultipleBindDefs(List<PMultipleBind> bindings)
	{
		List<PDefinition> defs = new Vector<PDefinition>();

		for (PMultipleBind mb : bindings)
		{
			for (PPattern pattern : mb.getPlist())
			{
				defs.addAll(af.createPPatternAssistant().getDefinitions(pattern, af.createPMultipleBindAssistant().getPossibleType(mb), NameScope.LOCAL));
			}
		}

		return defs;
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
				setNamesToAvoid(def);
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

	public void init(boolean clearSignatureChanges)
	{
		this.enclosingDef = null;
		this.enclosingCounter = 0;
		this.namesToAvoid.clear();
		this.nameGen = new TempVarNameGen();

		if (signatureChanges != null && clearSignatureChanges)
		{
			signatureChanges.clear();
		}
	}

	public Set<SignatureChange> getSignatureChanges()
	{
		return signatureChanges;
	}

	private List<PDefinition> getParamDefs(AExplicitFunctionDefinition node)
	{
		SFunctionDefinitionAssistantTC funcAssistant = this.af.createSFunctionDefinitionAssistant();
		List<List<PDefinition>> paramDefs = funcAssistant.getParamDefinitions(node, node.getType(), node.getParamPatternList(), node.getLocation());

		List<PDefinition> paramDefFlattened = new LinkedList<PDefinition>();

		for (List<PDefinition> list : paramDefs)
		{
			paramDefFlattened.addAll(list);
		}

		return paramDefFlattened;
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

	public void openScope(DefinitionInfo defInfo, INode defScope)
			throws AnalysisException
	{
		List<? extends PDefinition> nodeDefs = defInfo.getNodeDefs();

		for (int i = 0; i < nodeDefs.size(); i++)
		{
			PDefinition parentDef = nodeDefs.get(i);

			List<? extends PDefinition> localDefs = defInfo.getLocalDefs(parentDef);

			for (PDefinition localDef : localDefs) // check if it matches position
			{
				if(compareNodeLocation(localDef.getLocation()) || checkVarOccurences(localDef.getLocation(), defScope)){
					
				}
			}
		}
	}

	public void openScope(INode parentNode, List<PDefinition> localDefs,
			INode defScope) throws AnalysisException
	{
		for (PDefinition localDef : localDefs)
		{
			if(compareNodeLocation(localDef.getLocation()) || checkVarOccurences(localDef.getLocation(), defScope)){
				
			}
		}
	}

	public void endScope(DefinitionInfo defInfo)
	{
		this.localDefsInScope.removeAll(defInfo.getAllLocalDefNames());
	}

	public void removeLocalDefFromScope(PDefinition localDef)
	{
		localDefsInScope.remove(localDef.getName());
	}

		private boolean checkVarOccurences(ILexLocation defLoc,
			INode defScope) throws AnalysisException
	{
		VarOccurencesCollector collector = new VarOccurencesCollector(defLoc);
		
		defScope.apply(collector);
		Set<AVariableExp> setOfVars = collector.getVars();
		
		for(Iterator<AVariableExp> i = setOfVars.iterator(); i.hasNext(); ) {
			AVariableExp item = i.next();
			if(compareNodeLocation(item.getLocation())){
				return true;
			}
		}
		
		return false;
	}

	private void visitDefs(List<? extends PDefinition> defs)
			throws AnalysisException
	{
		for (PDefinition def : defs)
		{
			def.apply(this);
		}
	}

	private void visitStms(List<? extends PStm> stms) throws AnalysisException
	{
		for (PStm stm : stms)
		{
			stm.apply(this);
		}
	}
	
	private boolean compareNodeLocation(ILexLocation newNode){

		//System.out.println("Pos " + newNode.getStartLine() + ": " + newNode.getStartPos());

		if(parameters.length >= 3){
			if(newNode.getStartLine() == from){
				return true;
			}
		}
		return false;
	}
	
	public void setRefactoringParameters(String[] parameters) {
		if(parameters.length >= 3){
			this.parameters = parameters;
			this.changeType = parameters[0];
			if(this.changeType.equals("rmv")){
				this.from = Integer.parseInt(parameters[1]);
				this.to = Integer.parseInt(parameters[2]);
			}
			if(this.changeType.equals("add")){
				this.from = Integer.parseInt(parameters[1]);
				this.paramType = parameters[2];
				this.paramName = parameters[3];
			}
		}
	}
	
	public void addToNodeCurrentModule(PDefinition node){
		System.out.println("Sized:" + currentModule.getDefs().size());

		currentModule.getDefs().add(node);
		
		System.out.println("Sizea:" + currentModule.getDefs().size());
	}
}
