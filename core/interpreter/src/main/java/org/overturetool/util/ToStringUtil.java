package org.overturetool.util;

import java.util.List;
import java.util.Vector;

import org.overture.interpreter.ast.definitions.AEqualsDefinitionInterpreter;
import org.overture.interpreter.ast.definitions.AExplicitFunctionDefinitionInterpreter;
import org.overture.interpreter.ast.definitions.AExplicitOperationDefinitionInterpreter;
import org.overture.interpreter.ast.definitions.AImplicitFunctionDefinitionInterpreter;
import org.overture.interpreter.ast.definitions.AImplicitOperationDefinitionInterpreter;
import org.overture.interpreter.ast.definitions.AImportedDefinitionInterpreter;
import org.overture.interpreter.ast.definitions.AInheritedDefinitionInterpreter;
import org.overture.interpreter.ast.definitions.AMultiBindListDefinitionInterpreter;
import org.overture.interpreter.ast.definitions.ARenamedDefinitionInterpreter;
import org.overture.interpreter.ast.definitions.AThreadDefinitionInterpreter;
import org.overture.interpreter.ast.definitions.AValueDefinitionInterpreter;
import org.overture.interpreter.ast.definitions.PDefinitionInterpreter;
import org.overture.interpreter.ast.definitions.SClassDefinitionInterpreter;

import org.overture.interpreter.ast.patterns.APatternListTypePairInterpreter;
import org.overture.interpreter.ast.patterns.PPatternInterpreter;
import org.overture.interpreter.ast.statements.ABlockSimpleBlockStmInterpreter;
import org.overture.interpreter.ast.statements.ACaseAlternativeStmInterpreter;
import org.overture.interpreter.ast.statements.ACasesStmInterpreter;
import org.overture.interpreter.ast.statements.AElseIfStmInterpreter;
import org.overture.interpreter.ast.statements.AIfStmInterpreter;
import org.overture.interpreter.ast.statements.ANonDeterministicSimpleBlockStmInterpreter;
import org.overture.interpreter.ast.statements.PStmInterpreter;
import org.overture.interpreter.ast.statements.SSimpleBlockStmInterpreter;
import org.overture.interpreter.ast.node.NodeListInterpreter;

import org.overturetool.interpreter.vdmj.lex.LexLocation;
import org.overturetool.interpreter.vdmj.lex.LexNameList;
import org.overturetool.interpreter.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.util.Utils;

public class ToStringUtil
{
	public static String getExplicitFunctionString(AExplicitFunctionDefinitionInterpreter d)
	{
		StringBuilder params = new StringBuilder();

		for (List<PPatternInterpreter> plist: d.getParamPatternList())
		{
			params.append("(" + Utils.listToString(plist) + ")");
		}

		return d.getAccess() + d.getName().name +
				(d.getTypeParams().isEmpty() ? ": " : "[" + getTypeListString(d.getTypeParams()) + "]: ") + d.getType() +
				"\n\t" + d.getName().name + params + " ==\n" + d.getBody() +
				(d.getPrecondition() == null ? "" : "\n\tpre " + d.getPrecondition()) +
				(d.getPostcondition() == null ? "" : "\n\tpost " + d.getPostcondition());
	}
	
	public static String getImplicitFunctionString(AImplicitFunctionDefinitionInterpreter d)
	{
		return	d.getAccess() + " " +	d.getName().name +
		(d.getTypeParams().isEmpty() ? "" : "[" + getTypeListString(d.getTypeParams()) + "]") +
		Utils.listToString("(", getString(d.getParamPatterns()), ", ", ")") + d.getResult()+
		(d.getBody() == null ? "" : " ==\n\t" + d.getBody()) +
		(d.getPrecondition() == null ? "" : "\n\tpre " + d.getPrecondition()) +
		(d.getPostcondition() == null ? "" : "\n\tpost " + d.getPostcondition());
	}
	
	private static List<String> getString(List<APatternListTypePairInterpreter> node)
	{
		List<String> list = new Vector<String>();
		for (APatternListTypePairInterpreter pl : node)
		{
			list.add( "(" + getStringPattern(pl.getPatterns()) + ":" + pl.getType() + ")");	
		}
		return list;
	}
	private static String getStringPattern(List<PPatternInterpreter> patterns)
	{
		return Utils.listToString(patterns);
	}



	private static String getTypeListString(List<LexNameToken> typeParams)
	{
		return "(" + Utils.listToString(typeParams) + ")";
	}

	public static String getExplicitOperationString(AExplicitOperationDefinitionInterpreter d)
	{
		return  d.getName() + " " + d.getType() +
		"\n\t" + d.getName() + "(" + Utils.listToString(d.getParameterPatterns()) + ")" +
		(d.getBody() == null ? "" : " ==\n" + d.getBody()) +
		(d.getPrecondition() == null ? "" : "\n\tpre " + d.getPrecondition()) +
		(d.getPostcondition()== null ? "" : "\n\tpost " + d.getPostcondition());
	}
	
	public static String getImplicitOperationString(AImplicitOperationDefinitionInterpreter d)
	{
		return	d.getName() + Utils.listToString("(", d.getParameterPatterns(), ", ", ")") +
		(d.getResult() == null ? "" : " " + d.getResult()) +
		(d.getExternals().isEmpty() ? "" : "\n\text " + d.getExternals()) +
		(d.getPrecondition() == null ? "" : "\n\tpre " + d.getPrecondition()) +
		(d.getPostcondition() == null ? "" : "\n\tpost " + d.getPostcondition()) +
		(d.getErrors().isEmpty() ? "" : "\n\terrs " + d.getErrors());
	}
	

	public static String getDefinitionListString(
			NodeListInterpreter<PDefinitionInterpreter> _definitions)
	{
		StringBuilder sb = new StringBuilder();

		for (PDefinitionInterpreter d : _definitions)
		{
			if(d.getAccess()!=null)
			{
			sb.append(d.getAccess());
			sb.append(" ");
			}
			sb.append(d.kindPDefinitionInterpreter() + " " + getVariableNames(d) + ":"
					+ d.getType());
			sb.append("\n");
		}

		return sb.toString();
	}

	private static LexNameList getVariableNames(List<? extends PDefinitionInterpreter> list)
	{
		LexNameList variableNames = new LexNameList();

		for (PDefinitionInterpreter dd : list)
		{
			variableNames.addAll(getVariableNames(dd));
		}

		return variableNames;
	}

	private static LexNameList getVariableNames(PDefinitionInterpreter d)
	{
		switch (d.kindPDefinitionInterpreter())
		{

			case CLASS:
				if (d instanceof SClassDefinitionInterpreter)
				{
					return getVariableNames(((SClassDefinitionInterpreter) d).getDefinitions());
				}
				assert false : "Error in class getVariableNames";
				break;

			case EQUALS:
				if (d instanceof AEqualsDefinitionInterpreter)
				{
					return ((AEqualsDefinitionInterpreter) d).getDefs() == null ? new LexNameList()
							: getVariableNames(((AEqualsDefinitionInterpreter) d).getDefs());
				}
				assert false : "Error in equals getVariableNames";
				break;

			case EXTERNAL:
				// return state.getVariableNames();
				// TODO
				return new LexNameList(new LexNameToken("Not implemented", "Not implemented", new LexLocation()));

			case IMPORTED:
				if (d instanceof AImportedDefinitionInterpreter)
				{
					return getVariableNames(((AImportedDefinitionInterpreter) d).getDef());
				}
				assert false : "Error in imported getVariableNames";
				break;
			case INHERITED:
				if (d instanceof AInheritedDefinitionInterpreter)
				{
					LexNameList names = new LexNameList();
					// checkSuperDefinition();//TODO
					AInheritedDefinitionInterpreter t = (AInheritedDefinitionInterpreter) d;
					for (LexNameToken vn : getVariableNames(t.getSuperdef()))
					{
						names.add(vn.getModifiedName(t.getName().module));
					}

					return names;
				}
				assert false : "Error in inherited getVariableNames";
				break;

			case MULTIBINDLIST:
				if (d instanceof AMultiBindListDefinitionInterpreter)
				{
					return ((AMultiBindListDefinitionInterpreter) d).getDefs() == null ? new LexNameList()
							: getVariableNames(((AMultiBindListDefinitionInterpreter) d).getDefs());
				}
				break;
			case MUTEXSYNC:
			case NAMEDTRACE:
			case PERSYNC:
				return new LexNameList();
			case RENAMED:
				if (d instanceof ARenamedDefinitionInterpreter)
				{
					LexNameList both = new LexNameList(d.getName());
					both.add(((ARenamedDefinitionInterpreter) d).getDef().getName());
					return both;
				}
				assert false : "Error in renamed getVariableNames";

			case STATE:
				// return statedefs.getVariableNames();
				// TODO
				return new LexNameList(new LexNameToken("Not implemented", "Not implemented", new LexLocation()));
			case THREAD:
				if (d instanceof AThreadDefinitionInterpreter)
				{
					if(((AThreadDefinitionInterpreter) d).getOperationDef() !=null)//Differnt from VDMJ
					{
					return new LexNameList(((AThreadDefinitionInterpreter) d).getOperationDef().getName());
					}else
					{
						return null;
					}
				}
				assert false : "Error in thread getVariableNames";
				break;
			case TYPE:
				return new LexNameList(d.getName());
			case UNTYPED:
				assert false : "Can't get variables of untyped definition?";
				return null;

			case VALUE:
				if (d instanceof AValueDefinitionInterpreter)
				{
					// return ((AValueDefinition) d).getPattern()
					// TODO
					return new LexNameList(new LexNameToken("Not implemented", "Not implemented", new LexLocation()));
				}
				// return pattern.getVariableNames();
				break;

			default:
				return new LexNameList(d.getName());

		}
		return null;
	}
public static String getCasesString(ACasesStmInterpreter stm)
{
	StringBuilder sb = new StringBuilder();
	sb.append("cases " + stm.getExp() + " :\n");

	for (ACaseAlternativeStmInterpreter csa: stm.getCases())
	{
		sb.append("  ");
		sb.append(csa.toString());
	}

	if (stm.getOthers() != null)
	{
		sb.append("  others -> ");
		sb.append(stm.getOthers().toString());
	}

	sb.append("esac");
	return sb.toString();
}


public static String getIfString(AIfStmInterpreter node)
{
	StringBuilder sb = new StringBuilder();
	sb.append("if " + node.getIfExp() + "\nthen\n" + node.getThenStm());

	for (AElseIfStmInterpreter s: node.getElseIf())
	{
		sb.append(s.toString());
	}

	if (node.getElseStm() != null)
	{
		sb.append("else\n");
		sb.append(node.getElseStm().toString());
	}

	return sb.toString();
}
public static String getSimpleBlockString(SSimpleBlockStmInterpreter node)
{
	StringBuilder sb = new StringBuilder();
	String sep = "";

	for (PStmInterpreter s: node.getStatements())
	{
		sb.append(sep);
		sb.append(s.toString());
		sep = ";\n";
	}

	sb.append("\n");
	return sb.toString();
}

public static String getBlockSimpleBlockString(ABlockSimpleBlockStmInterpreter node)
{
	StringBuilder sb = new StringBuilder();
	sb.append("(\n");

	for (PDefinitionInterpreter d: node.getAssignmentDefs())
	{
		sb.append(d);
		sb.append("\n");
	}

	sb.append("\n");
	sb.append(getSimpleBlockString(node));
	sb.append(")");
	return sb.toString();
}

public static String getNonDeterministicSimpleBlockString(ANonDeterministicSimpleBlockStmInterpreter node)
{
	StringBuilder sb = new StringBuilder();
	sb.append("||(\n");
	sb.append(getSimpleBlockString(node));
	sb.append(")");
	return sb.toString();
}
}
