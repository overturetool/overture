package org.overture.refactoring;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.definitions.AClassClassDefinition;
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
import org.overture.ast.expressions.AMapCompMapExp;
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
import org.overture.ast.statements.AIdentifierStateDesignator;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.AFieldField;
import org.overture.codegen.analysis.vdm.DefinitionInfo;
import org.overture.codegen.analysis.vdm.IdDesignatorOccurencesCollector;
import org.overture.codegen.analysis.vdm.IdOccurencesCollector;
import org.overture.codegen.analysis.vdm.NameCollector;
import org.overture.codegen.analysis.vdm.Renaming;
import org.overture.codegen.analysis.vdm.VarOccurencesCollector;
import org.overture.codegen.ir.TempVarNameGen;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class RefactoringRenameCollector  extends DepthFirstAnalysisAdaptor
{
	private ITypeCheckerAssistantFactory af;

	private PDefinition enclosingDef;
	private Map<AIdentifierStateDesignator, PDefinition> idDefs;
	private Stack<ILexNameToken> localDefsInScope;
	private int enclosingCounter;

	private Set<Renaming> renamings;
	private Set<String> namesToAvoid;
	private TempVarNameGen nameGen;

	private Logger log = Logger.getLogger(this.getClass().getSimpleName());

	public RefactoringRenameCollector(ITypeCheckerAssistantFactory af,
			Map<AIdentifierStateDesignator, PDefinition> idDefs)
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
	public void caseAModuleModules(AModuleModules node) throws AnalysisException
	{
		if (enclosingDef != null)
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
	public void caseILexNameToken(ILexNameToken node) throws AnalysisException
	{
		// No need to visit names
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

	// Note that this methods is intended to work both for SL modules and PP/RT classes
	private void visitModuleDefs(List<PDefinition> defs, INode module)
			throws AnalysisException
	{
		DefinitionInfo defInfo = getStateDefs(defs, module);

		if (defInfo != null)
		{
			addLocalDefs(defInfo);
			handleExecutables(defs);
			removeLocalDefs(defInfo);
		} else
		{
			handleExecutables(defs);
		}
	}

	private void handleExecutables(List<PDefinition> defs)
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

	private DefinitionInfo getStateDefs(List<PDefinition> defs, INode module)
	{
		if (module instanceof AModuleModules)
		{
			List<PDefinition> fieldDefs = new LinkedList<PDefinition>();

			AStateDefinition stateDef = getStateDef(defs);

			if (stateDef != null)
			{
				fieldDefs.addAll(findFieldDefs(stateDef.getStateDefs(), stateDef.getFields()));
			}

			for (PDefinition def : defs)
			{
				if (def instanceof AValueDefinition)
				{
					fieldDefs.add(def);
				}
			}

			return new DefinitionInfo(fieldDefs, af);
		} else if (module instanceof SClassDefinition)
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
		} else
		{
			log.error("Expected module or class definition. Got: " + module);
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

	private List<PDefinition> findFieldDefs(List<PDefinition> stateDefs,
			List<AFieldField> fields)
	{
		List<PDefinition> fieldDefs = new LinkedList<PDefinition>();

		for (PDefinition d : stateDefs)
		{
			for (AFieldField f : fields)
			{
				if (f.getTagname().equals(d.getName()))
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

	private void addLocalDefs(DefinitionInfo defInfo)
	{
		List<PDefinition> allLocalDefs = defInfo.getAllLocalDefs();

		for (PDefinition localDef : allLocalDefs)
		{
			if (!contains(localDef))
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

			for (PDefinition localDef : localDefs)
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

	public void openScope(INode parentNode, List<PDefinition> localDefs,
			INode defScope) throws AnalysisException
	{
		for (PDefinition localDef : localDefs)
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

	private void findRenamings(PDefinition localDefToRename, INode parentNode,
			INode defScope) throws AnalysisException
	{
		ILexNameToken localDefName = getName(localDefToRename);

		if (localDefName == null)
		{
			return;
		}
		//TODO pass name
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

		for (AIdentifierStateDesignator id : idStateDesignators)
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

	private Set<AVariableExp> collectVarOccurences(ILexLocation defLoc,
			INode defScope) throws AnalysisException
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

	private Set<AIdentifierPattern> collectIdOccurences(ILexNameToken name,
			INode parent) throws AnalysisException
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

}
