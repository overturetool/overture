/*
 * #%~
 * The Overture Abstract Syntax Tree
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.ast.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.AEqualsDefinition;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AExternalDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.AImportedDefinition;
import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.ast.definitions.AMultiBindListDefinition;
import org.overture.ast.definitions.AMutexSyncDefinition;
import org.overture.ast.definitions.ANamedTraceDefinition;
import org.overture.ast.definitions.APerSyncDefinition;
import org.overture.ast.definitions.ARenamedDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.AThreadDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.AUntypedDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.node.NodeList;
import org.overture.ast.patterns.APatternListTypePair;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.statements.ABlockSimpleBlockStm;
import org.overture.ast.statements.ACaseAlternativeStm;
import org.overture.ast.statements.ACasesStm;
import org.overture.ast.statements.AElseIfStm;
import org.overture.ast.statements.AIfStm;
import org.overture.ast.statements.ANonDeterministicSimpleBlockStm;
import org.overture.ast.statements.PStm;
import org.overture.ast.statements.SSimpleBlockStm;
import org.overture.ast.typechecker.NameScope;

public class ToStringUtil
{
	public static String getExplicitFunctionString(AExplicitFunctionDefinition d)
	{
		StringBuilder params = new StringBuilder();

		for (List<PPattern> plist : d.getParamPatternList())
		{
			params.append("(" + Utils.listToString(plist) + ")");
		}

		String accessStr = d.getAccess().toString();
		if (d.getNameScope() == NameScope.LOCAL)
		{
			accessStr = "";
		}

		return accessStr
				+ d.getName().getName()
				+ (d.getTypeParams().isEmpty() ? ": " : "["
						+ getTypeListString(d.getTypeParams()) + "]: ")
				+ d.getType()
				+ "\n\t"
				+ d.getName().getName()
				+ params
				+ " ==\n"
				+ d.getBody()
				+ (d.getPrecondition() == null ? "" : "\n\tpre "
						+ d.getPrecondition())
				+ (d.getPostcondition() == null ? "" : "\n\tpost "
						+ d.getPostcondition());
	}

	public static String getImplicitFunctionString(AImplicitFunctionDefinition d)
	{
		return d.getAccess()
				+ " "
				+ d.getName().getName()
				+ (d.getTypeParams().isEmpty() ? "" : "["
						+ getTypeListString(d.getTypeParams()) + "]")
				+ Utils.listToString("(", getString(d.getParamPatterns()), ", ", ")")
				+ d.getResult()
				+ (d.getBody() == null ? "" : " ==\n\t" + d.getBody())
				+ (d.getPrecondition() == null ? "" : "\n\tpre "
						+ d.getPrecondition())
				+ (d.getPostcondition() == null ? "" : "\n\tpost "
						+ d.getPostcondition());
	}

	private static List<String> getString(List<APatternListTypePair> node)
	{
		List<String> list = new ArrayList<String>();
		for (APatternListTypePair pl : node)
		{
			list.add("(" + getStringPattern(pl.getPatterns()) + ":"
					+ pl.getType() + ")");
		}
		return list;
	}

	private static String getStringPattern(List<PPattern> patterns)
	{
		return Utils.listToString(patterns);
	}

	private static String getTypeListString(List<ILexNameToken> typeParams)
	{
		return "(" + Utils.listToString(typeParams) + ")";
	}

	public static String getExplicitOperationString(
			AExplicitOperationDefinition d)
	{
		return d.getName()
				+ " "
				+ d.getType()
				+ "\n\t"
				+ d.getName()
				+ "("
				+ Utils.listToString(d.getParameterPatterns())
				+ ")"
				+ (d.getBody() == null ? "" : " ==\n" + d.getBody())
				+ (d.getPrecondition() == null ? "" : "\n\tpre "
						+ d.getPrecondition())
				+ (d.getPostcondition() == null ? "" : "\n\tpost "
						+ d.getPostcondition());
	}

	public static String getImplicitOperationString(
			AImplicitOperationDefinition d)
	{
		return d.getName()
				+ Utils.listToString("(", d.getParameterPatterns(), ", ", ")")
				+ (d.getResult() == null ? "" : " " + d.getResult())
				+ (d.getExternals().isEmpty() ? "" : "\n\text "
						+ d.getExternals())
				+ (d.getPrecondition() == null ? "" : "\n\tpre "
						+ d.getPrecondition())
				+ (d.getPostcondition() == null ? "" : "\n\tpost "
						+ d.getPostcondition())
				+ (d.getErrors().isEmpty() ? "" : "\n\terrs " + d.getErrors());
	}

	public static String getDefinitionListString(
			NodeList<? extends PDefinition> _definitions)
	{
		StringBuilder sb = new StringBuilder();

		for (PDefinition d : _definitions)
		{
			if (d.getAccess() != null)
			{
				sb.append(d.getAccess());
				sb.append(" ");
			}
			// sb.append(d.getClass().getName() + " " + getVariableNames(d) + ":" + d.getType());
			sb.append(d.toString());
			sb.append("\n");
		}

		return sb.toString();
	}

	private static LexNameList getVariableNames(List<? extends PDefinition> list)
	{
		LexNameList variableNames = new LexNameList();

		for (PDefinition dd : list)
		{
			variableNames.addAll(getVariableNames(dd));
		}

		return variableNames;
	}

	private static LexNameList getVariableNames(PDefinition d)
	{
		if (d instanceof SClassDefinition)
		{
			if (d instanceof SClassDefinition)
			{
				return getVariableNames(((SClassDefinition) d).getDefinitions());
			}
			assert false : "Error in class getVariableNames";
		} else if (d instanceof AEqualsDefinition)
		{
			if (d instanceof AEqualsDefinition)
			{
				return ((AEqualsDefinition) d).getDefs() == null ? new LexNameList()
						: getVariableNames(((AEqualsDefinition) d).getDefs());
			}
			assert false : "Error in equals getVariableNames";
		} else if (d instanceof AExternalDefinition)
		{
			// return state.getVariableNames();
			// TODO
			return new LexNameList(new LexNameToken("Not implemented", "Not implemented", new LexLocation()));
		} else if (d instanceof AImportedDefinition)
		{
			if (d instanceof AImportedDefinition)
			{
				return getVariableNames(((AImportedDefinition) d).getDef());
			}
			assert false : "Error in imported getVariableNames";
		} else if (d instanceof AInheritedDefinition)
		{
			if (d instanceof AInheritedDefinition)
			{
				LexNameList names = new LexNameList();
				// checkSuperDefinition();//TODO
				AInheritedDefinition t = (AInheritedDefinition) d;
				for (ILexNameToken vn : getVariableNames(t.getSuperdef()))
				{
					names.add(vn.getModifiedName(t.getName().getModule()));
				}

				return names;
			}
			assert false : "Error in inherited getVariableNames";
		} else if (d instanceof AMultiBindListDefinition)
		{
			if (d instanceof AMultiBindListDefinition)
			{
				return ((AMultiBindListDefinition) d).getDefs() == null ? new LexNameList()
						: getVariableNames(((AMultiBindListDefinition) d).getDefs());
			}
		} else if (d instanceof AMutexSyncDefinition
				|| d instanceof ANamedTraceDefinition
				|| d instanceof APerSyncDefinition)
		{
			return new LexNameList();
		} else if (d instanceof ARenamedDefinition)
		{
			if (d instanceof ARenamedDefinition)
			{
				LexNameList both = new LexNameList(d.getName());
				both.add(((ARenamedDefinition) d).getDef().getName());
				return both;
			}
			assert false : "Error in renamed getVariableNames";
		} else if (d instanceof AStateDefinition)
		{
			// return statedefs.getVariableNames();
			// TODO
			return new LexNameList(new LexNameToken("Not implemented", "Not implemented", new LexLocation()));
		} else if (d instanceof AThreadDefinition)
		{
			if (d instanceof AThreadDefinition)
			{
				if (((AThreadDefinition) d).getOperationDef() != null)// Differnt from VDMJ
				{
					return new LexNameList(((AThreadDefinition) d).getOperationDef().getName());
				} else
				{
					return null;
				}
			}
			assert false : "Error in thread getVariableNames";
		} else if (d instanceof ATypeDefinition)
		{
			return new LexNameList(d.getName());
		} else if (d instanceof AUntypedDefinition)
		{
			assert false : "Can't get variables of untyped definition?";
		} else if (d instanceof AValueDefinition)
		{
			if (d instanceof AValueDefinition)
			{
				// return ((AValueDefinition) d).getPattern()
				// TODO
				return new LexNameList(new LexNameToken("Not implemented", "Not implemented", new LexLocation()));
			}
		} else
		{
			return new LexNameList(d.getName());
		}
		return null;
	}

	public static String getCasesString(ACasesStm stm)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("cases " + stm.getExp() + " :\n");

		for (ACaseAlternativeStm csa : stm.getCases())
		{
			sb.append("  ");
			sb.append(csa.toString());
		}

		if (stm.getOthers() != null)
		{
			sb.append("  others -> ");
			sb.append(stm.getOthers().toString());
		}

		sb.append(" end");
		return sb.toString();
	}

	public static String getIfString(AIfStm node)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("if " + node.getIfExp() + "\nthen\n" + node.getThenStm());

		for (AElseIfStm s : node.getElseIf())
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

	public static String getSimpleBlockString(SSimpleBlockStm node)
	{
		StringBuilder sb = new StringBuilder();
		String sep = "";

		for (PStm s : node.getStatements())
		{
			sb.append(sep);
			sb.append(s.toString());
			sep = ";\n";
		}

		sb.append("\n");
		return sb.toString();
	}

	public static String getBlockSimpleBlockString(ABlockSimpleBlockStm node)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("(\n");

		for (PDefinition d : node.getAssignmentDefs())
		{
			sb.append(d);
			sb.append("\n");
		}

		sb.append("\n");
		sb.append(getSimpleBlockString(node));
		sb.append(")");
		return sb.toString();
	}

	public static String getNonDeterministicSimpleBlockString(
			ANonDeterministicSimpleBlockStm node)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("||(\n");
		sb.append(getSimpleBlockString(node));
		sb.append(")");
		return sb.toString();
	}
}
