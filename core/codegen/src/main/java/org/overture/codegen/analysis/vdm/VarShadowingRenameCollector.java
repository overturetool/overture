package org.overture.codegen.analysis.vdm;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.ANamedTraceDefinition;
import org.overture.ast.definitions.ASystemClassDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SFunctionDefinition;
import org.overture.ast.definitions.SOperationDefinition;
import org.overture.ast.definitions.traces.ALetBeStBindingTraceDefinition;
import org.overture.ast.expressions.ALambdaExp;
import org.overture.ast.expressions.ALetBeStExp;
import org.overture.ast.expressions.ALetDefExp;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.node.INode;
import org.overture.ast.statements.ABlockSimpleBlockStm;
import org.overture.ast.statements.ALetBeStStm;
import org.overture.ast.statements.ALetStm;
import org.overture.ast.statements.PStm;
import org.overture.codegen.ir.TempVarNameGen;
import org.overture.codegen.logging.Logger;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.definition.AExplicitFunctionDefinitionAssistantTC;

public class VarShadowingRenameCollector extends DepthFirstAnalysisAdaptor
{
	private PDefinition enclosingDef = null;
	private Stack<PDefinition> defsInScope;
	private List<Renaming> renamings;
	private int enclosingCounter;
	private List<String> namesToAvoid;

	private ITypeCheckerAssistantFactory af;

	private TempVarNameGen nameGen;

	public VarShadowingRenameCollector(ITypeCheckerAssistantFactory af)
	{
		this.enclosingDef = null;
		this.defsInScope = new Stack<PDefinition>();
		this.renamings = new LinkedList<Renaming>();
		this.enclosingCounter = 0;
		this.namesToAvoid = new LinkedList<String>();
		this.af = af;
		this.nameGen = new TempVarNameGen();
	}

	@Override
	public void caseAClassClassDefinition(AClassClassDefinition node)
			throws AnalysisException
	{
		if (enclosingDef != null)
		{
			return;
		}

		visitClassDefs(node.getDefinitions());
	}

	@Override
	public void caseASystemClassDefinition(ASystemClassDefinition node)
			throws AnalysisException
	{
		if (enclosingDef != null)
		{
			return;
		}

		visitClassDefs(node.getDefinitions());
	}

	@Override
	public void caseAExplicitOperationDefinition(
			AExplicitOperationDefinition node) throws AnalysisException
	{
		if (!proceed(node))
		{
			return;
		}

		List<PDefinition> defs = collectDefs(node.getParamDefinitions());

		openScope(defs, node);

		node.getBody().apply(this);

		endScope(defs);
	}

	@Override
	public void caseAExplicitFunctionDefinition(AExplicitFunctionDefinition node)
			throws AnalysisException
	{
		if (!proceed(node))
		{
			return;
		}

		List<PDefinition> defs = collectDefs(getParamDefs(node));

		openScope(defs, node);

		node.getBody().apply(this);

		endScope(defs);
	}

	@Override
	public void caseABlockSimpleBlockStm(ABlockSimpleBlockStm node)
			throws AnalysisException
	{
		if (!proceed(node))
		{
			return;
		}

		List<PDefinition> defs = collectDefs(node.getAssignmentDefs());

		openScope(defs, node);

		visitDefs(node.getAssignmentDefs());
		visitStms(node.getStatements());

		endScope(defs);
	}

	@Override
	public void caseALetDefExp(ALetDefExp node) throws AnalysisException
	{
		if (!proceed(node))
		{
			return;
		}

		List<PDefinition> defs = collectDefs(node.getLocalDefs());

		openScope(defs, node);

		visitDefs(node.getLocalDefs());
		node.getExpression().apply(this);

		endScope(defs);
	}

	@Override
	public void caseALetStm(ALetStm node) throws AnalysisException
	{
		if (!proceed(node))
		{
			return;
		}

		List<PDefinition> defs = collectDefs(node.getLocalDefs());

		openScope(defs, node);

		visitDefs(node.getLocalDefs());
		node.getStatement().apply(this);

		endScope(defs);
	}

	@Override
	public void caseALetBeStExp(ALetBeStExp node) throws AnalysisException
	{
		if (!proceed(node))
		{
			return;
		}

		List<PDefinition> defs = collectDefs(node.getDef().getDefs());

		openScope(defs, node);

		node.getDef().apply(this);

		if (node.getSuchThat() != null)
		{
			node.getSuchThat().apply(this);
		}

		node.getValue().apply(this);

		endScope(defs);
	}

	@Override
	public void caseALetBeStStm(ALetBeStStm node) throws AnalysisException
	{
		if (!proceed(node))
		{
			return;
		}

		List<PDefinition> defs = collectDefs(node.getDef().getDefs());
		openScope(defs, node);

		node.getDef().apply(this);

		if (node.getSuchThat() != null)
		{
			node.getSuchThat().apply(this);
		}

		node.getStatement().apply(this);

		endScope(defs);
	}

	@Override
	public void caseALetBeStBindingTraceDefinition(
			ALetBeStBindingTraceDefinition node) throws AnalysisException
	{
		if (!proceed(node))
		{
			return;
		}

		List<PDefinition> defs = collectDefs(node.getDef().getDefs());

		openScope(defs, node);

		node.getDef().apply(this);

		if (node.getStexp() != null)
		{
			node.getStexp().apply(this);
		}

		node.getBody().apply(this);

		endScope(defs);
	}

	@Override
	public void caseALambdaExp(ALambdaExp node) throws AnalysisException
	{
		if (!proceed(node))
		{
			return;
		}

		List<PDefinition> defs = collectDefs(node.getParamDefinitions());

		openScope(defs, node);

		node.getExpression().apply(this);

		endScope(defs);
	}

	@Override
	public void caseILexNameToken(ILexNameToken node) throws AnalysisException
	{
		// No need to visit names
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

	private void visitClassDefs(List<PDefinition> defs)
			throws AnalysisException
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

	public List<Renaming> getRenamings()
	{
		LinkedList<Renaming> renameCopies = new LinkedList<Renaming>(renamings);
		Collections.sort(renameCopies);

		return renameCopies;
	}

	private List<PDefinition> getParamDefs(AExplicitFunctionDefinition node)
	{
		AExplicitFunctionDefinitionAssistantTC funcAssistant = this.af.createAExplicitFunctionDefinitionAssistant();
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
			}
		}

		if (def == null)
		{
			Logger.getLog().printError("Expected operation, function or named trace definition in HiddenVarRenamer. Got: "
					+ enclosingDef);
		}

		return enclosingDef == def;
	}

	public void openScope(List<? extends PDefinition> defs, INode defScope)
			throws AnalysisException
	{
		for (int i = 0; i < defs.size(); i++)
		{

			PDefinition d = defs.get(i);
			if (contains(d))
			{
				findRenamings(d, defScope, defs.subList(0, i));
			} else
			{
				defsInScope.add(d);
			}

		}
	}

	public void endScope(List<? extends PDefinition> defs)
	{
		this.defsInScope.removeAll(defs);
	}

	private List<PDefinition> collectDefs(List<? extends PDefinition> defs)
	{
		List<PDefinition> collectedDefs = new LinkedList<PDefinition>();
		for (PDefinition d : defs)
		{
			collectedDefs.addAll(af.createPDefinitionAssistant().getDefinitions(d));
		}
		return collectedDefs;
	}

	private void findRenamings(PDefinition defToRename, INode defScope,
			List<? extends PDefinition> defsOutsideScope)
			throws AnalysisException
	{
		ILexNameToken defName = getName(defToRename);

		if (defName == null)
		{
			return;
		}

		String newName = computeNewName(defName.getName());

		if (!contains(defName.getLocation()))
		{
			renamings.add(new Renaming(defName.getLocation(), defName.getName(), newName));
		}

		Set<AVariableExp> occurences = collectVarOccurences(defToRename, defScope, defsOutsideScope);
		
		for (AVariableExp varExp : occurences)
		{
			registerRenaming(varExp.getName(), newName);
		}
	}

	private void registerRenaming(ILexNameToken name, String newName)
	{
		if (!contains(name.getLocation()))
		{
			renamings.add(new Renaming(name.getLocation(), name.getName(), newName));
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

	private Set<AVariableExp> collectVarOccurences(PDefinition def,
			INode defScope, List<? extends PDefinition> defsOutsideScope)
			throws AnalysisException
	{
		VarOccurencesCollector collector = new VarOccurencesCollector(def, defsOutsideScope);

		defScope.apply(collector);

		return collector.getVars();
	}

	private boolean contains(PDefinition defToCheck)
	{
		ILexNameToken nameToCheck = getName(defToCheck);

		if (nameToCheck != null)
		{
			for (PDefinition d : defsInScope)
			{
				ILexNameToken currentDefName = getName(d);

				if (currentDefName != null
						&& nameToCheck.getName().equals(currentDefName.getName()))
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
			Logger.getLog().printErrorln("Could not find name for definition : "
					+ def + " in VarShadowingRenamer");
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
