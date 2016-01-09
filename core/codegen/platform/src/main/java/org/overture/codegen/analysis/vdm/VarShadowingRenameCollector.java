package org.overture.codegen.analysis.vdm;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

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
import org.overture.ast.definitions.SClassDefinition;
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
import org.overture.ast.lex.LexNameList;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.statements.ABlockSimpleBlockStm;
import org.overture.ast.statements.ACaseAlternativeStm;
import org.overture.ast.statements.ACasesStm;
import org.overture.ast.statements.AForAllStm;
import org.overture.ast.statements.AForIndexStm;
import org.overture.ast.statements.AForPatternBindStm;
import org.overture.ast.statements.AIdentifierStateDesignator;
import org.overture.ast.statements.ALetBeStStm;
import org.overture.ast.statements.ALetStm;
import org.overture.ast.statements.ATixeStm;
import org.overture.ast.statements.ATixeStmtAlternative;
import org.overture.ast.statements.ATrapStm;
import org.overture.ast.statements.PStm;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.PType;
import org.overture.codegen.ir.TempVarNameGen;
import org.overture.codegen.logging.Logger;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.definition.SFunctionDefinitionAssistantTC;

/**
 * This analysis is used to compute new names for variables that shadow other variables. A renaming is suggested if a
 * definition hides another variable or causes a duplicate definition. In addition to renaming the definition itself,
 * occurrences of the definition must also be renamed. Occurrences include both variable expressions (e.g. return a),
 * identifier state designators (e.g. x := 5) as well as identifier patterns (in mk_(a,a) the first 'a' is a definition
 * whereas the second 'a' is an identifier pattern occurrence of the first 'a') When computed, the renamings can be
 * applied in order to get rid of warnings related to hidden variables and duplicate definitions. The current version of
 * this analysis only considers renamings in operations and functions. This is sufficient if the VDM AST is code
 * generated to Java since Java allows hiding of class fields.
 * 
 * @author pvj
 */
public class VarShadowingRenameCollector extends DepthFirstAnalysisAdaptor
{
	private ITypeCheckerAssistantFactory af;

	private PDefinition enclosingDef;
	private Map<AIdentifierStateDesignator, PDefinition> idDefs;
	private Stack<ILexNameToken> localDefsInScope;
	private int enclosingCounter;
	
	private Set<Renaming> renamings;
	private Set<String> namesToAvoid;
	private TempVarNameGen nameGen;
	
	public VarShadowingRenameCollector(ITypeCheckerAssistantFactory af, Map<AIdentifierStateDesignator, PDefinition> idDefs)
	{
		this.af = af;

		this.enclosingDef = null;
		this.idDefs = idDefs;
		this.localDefsInScope = new Stack<ILexNameToken>();
		this.enclosingCounter = 0;

		this.renamings = new HashSet<Renaming>();
		this.namesToAvoid = new HashSet<String>();
		this.nameGen = new TempVarNameGen();
	}

	@Override
	public void caseAModuleModules(AModuleModules node)
			throws AnalysisException
	{
		if(enclosingDef != null)
		{
			return;
		}

		visitModuleDefs(node.getDefs(), node);
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
	// public f :  nat * (nat * nat) * nat -> nat
	// f (b,mk_(b,b), a) == b;

	
	@Override
	public void caseAExplicitOperationDefinition(
			AExplicitOperationDefinition node) throws AnalysisException
	{
		if (!proceed(node))
		{
			return;
		}
		
		DefinitionInfo defInfo = new DefinitionInfo(node.getParamDefinitions(), af);
		
		openScope(defInfo, node);

		node.getBody().apply(this);

		endScope(defInfo);
	}

	@Override
	public void caseAExplicitFunctionDefinition(AExplicitFunctionDefinition node)
			throws AnalysisException
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
		
		handleMultipleBindConstruct(node, node.getBindList(), null,node.getPredicate());
	}
	
	
	/*
	 * Sequence comp needs no treatment it uses only a bind
	 */
	
	@Override
	public void caseASetCompSetExp(ASetCompSetExp node)
			throws AnalysisException
	{
		if (!proceed(node))
		{
			return;
		}
		
		handleMultipleBindConstruct(node, node.getBindings(), node.getFirst(), node.getPredicate());
	}
	
	@Override
	public void caseAMapCompMapExp(AMapCompMapExp node)
			throws AnalysisException
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
		if(node.getBody() != null)
		{
			node.getBody().apply(this);
		}
		
		// The trap alternatives will be responsible for opening/ending the scope
		for(ATixeStmtAlternative trap : node.getTraps())
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
		
		//End scope
		for(PDefinition def : node.getPatternBind().getDefs())
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
		
		if(node.getWith() != null)
		{
			node.getWith().apply(this);
		}

		for(PDefinition def : node.getPatternBind().getDefs())
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
		
		if(node.getExp() != null)
		{
			node.getExp().apply(this);
		}
		
		openScope(node.getPatternBind().getPattern(), node.getPatternBind().getDefs(), node.getStatement());
		
		node.getStatement().apply(this);
		
		for(PDefinition def : node.getPatternBind().getDefs())
		{
			removeLocalDefFromScope(def);
		}
	}
	
	@Override
	public void caseAForAllStm(AForAllStm node) throws AnalysisException
	{
		if (!proceed(node))
		{
			return;
		}

		if(node.getSet() != null)
		{
			node.getSet().apply(this);
		}

		PType possibleType = af.createPPatternAssistant().getPossibleType(node.getPattern());
		List<PDefinition> defs = af.createPPatternAssistant().getDefinitions(node.getPattern(), possibleType, NameScope.LOCAL);
		
		for(PDefinition d : defs)
		{
			openLoop(d.getName(), node.getPattern(), node.getStatement());
		}

		node.getStatement().apply(this);
		
		for(PDefinition def : defs)
		{
			removeLocalDefFromScope(def);
		}
	}
	
	@Override
	public void caseAForIndexStm(AForIndexStm node) throws AnalysisException
	{
		if (!proceed(node))
		{
			return;
		}

		if(node.getFrom() != null)
		{
			node.getFrom().apply(this);
		}
		
		if(node.getTo() != null)
		{
			node.getTo().apply(this);
		}
		
		if(node.getBy() != null)
		{
			node.getBy().apply(this);
		}

		ILexNameToken var = node.getVar();

		openLoop(var, null, node.getStatement());
		
		node.getStatement().apply(this);
		
		localDefsInScope.remove(var);
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
	
	private void handleCaseNode(PExp cond, List<? extends INode> cases, INode others) throws AnalysisException
	{
		if(cond != null)
		{
			cond.apply(this);
		}
		
		// The cases will be responsible for opening/ending the scope
		for(INode c : cases)
		{
			c.apply(this);
		}
		
		if (others != null)
		{
			others.apply(this);
		}
	}

	private void openLoop(ILexNameToken var, INode varParent, PStm body)
			throws AnalysisException
	{
		if(!contains(var))
		{
			localDefsInScope.add(var);			
		}
		else
		{
			String newName = computeNewName(var.getName());
			
			registerRenaming(var, newName);
			
			if(varParent != null)
			{
				Set<AIdentifierPattern> idPatterns = collectIdOccurences(var, varParent);
				
				for(AIdentifierPattern id : idPatterns)
				{
					registerRenaming(id.getName(), newName);
				}
			}
			
			Set<AVariableExp> vars = collectVarOccurences(var.getLocation(), body);

			for (AVariableExp varExp : vars)
			{
				registerRenaming(varExp.getName(), newName);
			}
			
			Set<AIdentifierStateDesignator> idStateDesignators = collectIdDesignatorOccurrences(var.getLocation(), body);
			
			for(AIdentifierStateDesignator id : idStateDesignators)
			{
				registerRenaming(id.getName(), newName);
			}
		}
	}
	
	private void handleCase(LinkedList<PDefinition> localDefs, PPattern pattern, INode result) throws AnalysisException
	{
		// Do not visit the conditional exp (cexp)
		openScope(pattern, localDefs, result);
		
		result.apply(this);
		
		//End scope
		for(PDefinition def : localDefs)
		{
			removeLocalDefFromScope(def);
		}
	}
	
	private void handleMultipleBindConstruct(INode node, LinkedList<PMultipleBind> bindings, PExp first, PExp pred) throws AnalysisException
	{
		
		DefinitionInfo defInfo = new DefinitionInfo(getMultipleBindDefs(bindings), af);
		
		openScope(defInfo, node);
		
		if (first != null)
		{
			first.apply(this);
		}
		
		if(pred != null)
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

	// Note that this methods is intended to work both for SL modules and PP/RT classes
	private void visitModuleDefs(List<PDefinition> defs, INode module)
			throws AnalysisException
	{
		DefinitionInfo defInfo = getStateDefs(defs, module);
		
		if(defInfo != null)
		{
			addLocalDefs(defInfo);
			handleExecutables(defs);
			removeLocalDefs(defInfo);
		}
		else
		{
			handleExecutables(defs);
		}
	}

	private void handleExecutables(List<PDefinition> defs) throws AnalysisException
	{
		for (PDefinition def : defs)
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

	private DefinitionInfo getStateDefs(List<PDefinition> defs, INode module)
	{
		if(module instanceof AModuleModules)
		{
			List<PDefinition> fieldDefs = new LinkedList<PDefinition>();

			AStateDefinition stateDef = getStateDef(defs);
			
			if(stateDef != null)
			{
				fieldDefs.addAll(findFieldDefs(stateDef.getStateDefs(), stateDef.getFields()));
			}
			
			for(PDefinition def : defs)
			{
				if(def instanceof AValueDefinition)
				{
					fieldDefs.add(def);
				}
			}

			return new DefinitionInfo(fieldDefs, af);
		}
		else if(module instanceof SClassDefinition)
		{
			SClassDefinition classDef = (SClassDefinition) module;
			List<PDefinition> allDefs = new LinkedList<PDefinition>();

			LinkedList<PDefinition> enclosedDefs = classDef.getDefinitions();
			LinkedList<PDefinition> inheritedDefs = classDef.getAllInheritedDefinitions();

			allDefs.addAll(enclosedDefs);
			allDefs.addAll(inheritedDefs);
			
			List<PDefinition> fields = new LinkedList<PDefinition>();
			
			for (PDefinition def : allDefs)
			{
				if (def instanceof AInstanceVariableDefinition
						|| def instanceof AValueDefinition)
				{
					fields.add(def);
				}
			}
			
			return new DefinitionInfo(fields, af);
		}
		else
		{
			Logger.getLog().printErrorln("Expected module or class definition. Got: " + module + " in '" + this.getClass().getSimpleName() +"'");
			return null;
		}
	}
	
	private AStateDefinition getStateDef(List<PDefinition> defs)
	{
		for (PDefinition def : defs)
		{
			if (def instanceof AStateDefinition)
			{
				return (AStateDefinition) def;
			}
		}
		
		return null;
	}

	private List<PDefinition> findFieldDefs(List<PDefinition> stateDefs, List<AFieldField> fields)
	{
		List<PDefinition> fieldDefs = new LinkedList<PDefinition>();
		
		for(PDefinition d : stateDefs)
		{
			for(AFieldField f : fields)
			{
				if(f.getTagname().equals(d.getName()))
				{
					fieldDefs.add(d);
					break;
				}
			}
		}
		return fieldDefs;
	}

	private void setNamesToAvoid(PDefinition def) throws AnalysisException
	{
		NameCollector collector = new NameCollector();
		def.apply(collector);
		namesToAvoid = collector.namesToAvoid();
	}

	public void init(boolean clearRenamings)
	{
		this.enclosingDef = null;
		this.enclosingCounter = 0;
		this.namesToAvoid.clear();
		this.nameGen = new TempVarNameGen();

		if (renamings != null && clearRenamings)
		{
			renamings.clear();
		}
	}

	public Set<Renaming> getRenamings()
	{
		return renamings;
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
				
				if(def == null)
				{
					def = node.getAncestor(AValueDefinition.class);
					
					if(def == null)
					{
						def = node.getAncestor(AInstanceVariableDefinition.class);
						
						if(def == null)
						{
							def = node.getAncestor(ATypeDefinition.class);
							
							if(def == null)
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
			Logger.getLog().printError("Got unexpected definition in '" + this.getClass().getSimpleName() + "'. Got: "
					+ enclosingDef);
		}

		return enclosingDef == def;
	}
	
	private void addLocalDefs(DefinitionInfo defInfo)
	{
		List<PDefinition> allLocalDefs = defInfo.getAllLocalDefs();
		
		for(PDefinition localDef : allLocalDefs)
		{
			if(!contains(localDef))
			{
				localDefsInScope.add(localDef.getName());
			}
		}
	}
	
	private void removeLocalDefs(DefinitionInfo defInfo)
	{
		localDefsInScope.removeAll(defInfo.getAllLocalDefNames());
	}

	public void openScope(DefinitionInfo defInfo, INode defScope)
			throws AnalysisException
	{
		List<? extends PDefinition> nodeDefs = defInfo.getNodeDefs();
		
		for (int i = 0; i < nodeDefs.size(); i++)
		{
			PDefinition parentDef = nodeDefs.get(i);
			
			List<? extends PDefinition> localDefs = defInfo.getLocalDefs(parentDef);
			
			for(PDefinition localDef : localDefs)
			{
				if (contains(localDef))
				{
					findRenamings(localDef, parentDef, defScope);
				} else
				{
					localDefsInScope.add(localDef.getName());
				}
			}
		}
	}
	
	public void openScope(INode parentNode, List<PDefinition> localDefs, INode defScope) throws AnalysisException
	{
		for(PDefinition localDef : localDefs)
		{
			if (contains(localDef))
			{
				findRenamings(localDef, parentNode, defScope);
			} else
			{
				localDefsInScope.add(localDef.getName());
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

	private void findRenamings(PDefinition localDefToRename, INode parentNode, INode defScope)
			throws AnalysisException
	{
		ILexNameToken localDefName = getName(localDefToRename);

		if (localDefName == null)
		{
			return;
		}

		String newName = computeNewName(localDefName.getName());

		if (!contains(localDefName.getLocation()))
		{
			registerRenaming(localDefName, newName);
		}

		Set<AVariableExp> vars = collectVarOccurences(localDefToRename.getLocation(), defScope);

		for (AVariableExp varExp : vars)
		{
			registerRenaming(varExp.getName(), newName);
		}
		
		Set<AIdentifierStateDesignator> idStateDesignators = collectIdDesignatorOccurrences(localDefToRename.getLocation(), defScope);
		
		for(AIdentifierStateDesignator id : idStateDesignators)
		{
			registerRenaming(id.getName(), newName);
		}

		Set<AIdentifierPattern> idPatterns = collectIdOccurences(localDefName, parentNode);

		for (AIdentifierPattern id : idPatterns)
		{
			registerRenaming(id.getName(), newName);
		}
	}

	private void registerRenaming(ILexNameToken name, String newName)
	{
		if (!contains(name.getLocation()))
		{
			renamings.add(new Renaming(name.getLocation(), name.getName(), newName, name.getModule(), name.getModule()));
		}
	}

	private boolean contains(ILexLocation loc)
	{
		for (Renaming r : renamings)
		{
			if (r.getLoc().equals(loc))
			{
				return true;
			}
		}

		return false;
	}

	private Set<AVariableExp> collectVarOccurences(ILexLocation defLoc, INode defScope)
			throws AnalysisException
	{
		VarOccurencesCollector collector = new VarOccurencesCollector(defLoc);

		defScope.apply(collector);

		return collector.getVars();
	}
	
	private Set<AIdentifierStateDesignator> collectIdDesignatorOccurrences(
			ILexLocation defLoc, INode defScope) throws AnalysisException
	{
		IdDesignatorOccurencesCollector collector = new IdDesignatorOccurencesCollector(defLoc, idDefs);

		defScope.apply(collector);

		return collector.getIds();
	}
	
	private Set<AIdentifierPattern> collectIdOccurences(ILexNameToken name, INode parent) throws AnalysisException
	{
		IdOccurencesCollector collector = new IdOccurencesCollector(name, parent);
		
		parent.apply(collector);
		
		return collector.getIdOccurences();
	}

	private boolean contains(PDefinition defToCheck)
	{
		ILexNameToken nameToCheck = getName(defToCheck);

		return contains(nameToCheck);
	}

	private boolean contains(ILexNameToken nameToCheck)
	{
		// Can be null if we try to find the name for the ignore pattern
		if (nameToCheck != null)
		{
			for (ILexNameToken name : localDefsInScope)
			{
				if (name != null
						&& nameToCheck.getName().equals(name.getName()))
				{
					return true;
				}
			}
		}

		return false;
	}

	private ILexNameToken getName(PDefinition def)
	{
		LexNameList varNames = af.createPDefinitionAssistant().getVariableNames(def);

		if (varNames.isEmpty())
		{
			// Can be empty if we try to find the name for the ignore pattern
			return null;
		} else
		{
			return varNames.firstElement();
		}
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
}
