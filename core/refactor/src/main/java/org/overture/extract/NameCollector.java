package org.overture.extract;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.ANamedTraceDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.definitions.SFunctionDefinition;
import org.overture.ast.definitions.SOperationDefinition;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.ast.types.AFieldField;
import org.overture.ast.util.modules.CombinedDefaultModule;
import org.overture.codegen.analysis.vdm.DefinitionInfo;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class NameCollector extends DepthFirstAnalysisAdaptor
{
	private PDefinition enclosingDef;
	private Logger log = Logger.getLogger(this.getClass().getSimpleName());
	private ITypeCheckerAssistantFactory af;
	private Stack<ILexNameToken> localDefsInScope;

	public NameCollector(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
		this.localDefsInScope = new Stack<ILexNameToken>();
		this.enclosingDef = null;
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
			visitModuleDefs(node.getDefs(), node);
		}
	}

	private void visitModuleDefs(List<PDefinition> defs, INode module)
			throws AnalysisException
	{
		DefinitionInfo defInfo = getStateDefs(defs, module);

		if (defInfo != null)
		{
			addLocalDefs(defInfo);
			handleExecutables(defs);
		} else
		{
			handleExecutables(defs);
		}
	}
	
	private void addLocalDefs(DefinitionInfo defInfo)
	{
		List<PDefinition> allLocalDefs = defInfo.getAllLocalDefs();

		for (PDefinition localDef : allLocalDefs)
		{
			addLocalDef(localDef);
		}
	}

	private void addLocalDef(PDefinition localDef) {
		if (!contains(localDef))
		{
			localDefsInScope.add(localDef.getName());
		}
	}
	
	public boolean checkNameNotInUse(String name){
		for(ILexNameToken item : localDefsInScope){
			if(item.getName().equals(name)){
				return true;
			}
		}
		return false;
	}
	
	private boolean contains(PDefinition defToCheck)
	{
		ILexNameToken nameToCheck = getName(defToCheck);
		return contains(nameToCheck);
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
				addLocalDef(def);
				def.apply(this);
			}
		}
	}

	public void init()
	{
		this.enclosingDef = null;
		this.localDefsInScope.clear();
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
}