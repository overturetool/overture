package org.overturetool.VDM2JavaCG.VDM2Java;

import jp.co.csk.vdm.toolbox.VDM.*;

import java.util.*;

import java.util.Iterator;
import java.util.Vector;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import org.overturetool.VDM2JavaCG.main.*;
import org.overturetool.VDM2JavaCG.Utilities.CGCollections;
import org.overturetool.VDM2JavaCG.Utilities.Token;
import org.overturetool.VDM2JavaCG.ast.java.imp.*;
import org.overturetool.VDM2JavaCG.ast.java.itf.*;
import org.overturetool.VDM2JavaCG.main.ClassExstractorFromTexFiles;
import org.overturetool.vdmj.definitions.ValueDefinition;
import org.overturetool.vdmj.statements.IdentifierDesignator;

@SuppressWarnings("all")
public class JavaAstVisitor extends JavaVisitor
{

	public String result = null;

	private Long lvl = null;

	private String nl = null;

	private String tab = "        ";

	private boolean import_flag = false;

	final private List<Throwable> errors = new Vector<Throwable>();

	private void init_Visitor() throws CGException
	{
		try
		{

			result = new String();
			lvl = new Long(0);
			nl = new String("");
			errors.clear();
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public JavaAstVisitor() throws CGException
	{
		init_Visitor();
	}

	public void useNewLineSeparator(final Boolean useNewLine)
			throws CGException
	{
		if (useNewLine.booleanValue())
			nl = "\n";
		else
			nl = ("");
	}

	private void printNodeField(final IJavaNode pNode) throws Exception
	{
		pNode.accept((IJavaVisitor) this);
	}

	private void printBooleanField(final Boolean pval) throws CGException
	{

		String rhs_2 = null;
		if (pval.booleanValue())
			rhs_2 = new String("true");
		else
			rhs_2 = new String("false");
		result = UTIL.ConvertToString(UTIL.clone(rhs_2));
	}

	private void printIntField(final Long pval) throws CGException
	{

		result = pval.toString();
	}

	private void printDoubleField(final Double pval) throws CGException
	{

		result = pval.toString();
	}

	private void printCharField(final Character pval) throws CGException
	{

		String rhs_2 = null;
		rhs_2 = new String();
		rhs_2 = rhs_2 + pval;
		result = UTIL.ConvertToString(UTIL.clone(rhs_2));
	}

	private void printField(final Object fld) throws Exception
	{
		if (((fld instanceof Boolean)))
			printBooleanField((Boolean) fld);
		else if (((fld instanceof Character)))
			printCharField((Character) fld);
		else
		{
			if ((UTIL.IsInteger(fld) && ((Number) fld).intValue() >= 0))
				printIntField(UTIL.NumberToLong(fld));
			else
			{
				if ((UTIL.IsReal(fld)))
					printDoubleField(UTIL.NumberToReal(fld));
				else
				{

					Boolean cond_6 = null;
					cond_6 = new Boolean(fld instanceof IJavaNode);
					if (cond_6.booleanValue())
						printNodeField((IJavaNode) fld);
					else
					{
						printStringField(UTIL.ConvertToString(fld));
					}
				}
			}
		}
	}

	private void printStringField(final String str) throws CGException
	{

		String rhs_2 = null;
		String var1_3 = null;
		var1_3 = new String("\"").concat(str);
		rhs_2 = var1_3.concat(new String("\""));
		result = UTIL.ConvertToString(UTIL.clone(rhs_2));
	}

	private void printSeqofField(final Vector pval) throws Exception
	{

		String str = new String("");
		Long cnt = new Long(pval.size());
		while (((cnt.intValue()) > (new Long(0).intValue())))
		{

			Object tmpArg_v_7 = null;
			if ((1 <= new Long(new Long(new Long(pval.size()).intValue()
					- cnt.intValue()).intValue()
					+ new Long(1).intValue()).intValue())
					&& (new Long(new Long(new Long(pval.size()).intValue()
							- cnt.intValue()).intValue()
							+ new Long(1).intValue()).intValue() <= pval.size()))
				tmpArg_v_7 = pval.get(new Long(new Long(new Long(pval.size()).intValue()
						- cnt.intValue()).intValue()
						+ new Long(1).intValue()).intValue() - 1);
			else
				UTIL.RunTime("Run-Time Error:Illegal index");
			printField(tmpArg_v_7);
			String rhs_15 = null;
			rhs_15 = str.concat(result);
			str = UTIL.ConvertToString(UTIL.clone(rhs_15));
			if (((cnt.intValue()) > (new Long(1).intValue())))
			{

				String rhs_21 = null;
				rhs_21 = str.concat(new String(", "));
				str = UTIL.ConvertToString(UTIL.clone(rhs_21));
			}
			cnt = UTIL.NumberToLong(UTIL.clone(new Long(cnt.intValue()
					- new Long(1).intValue())));
		}
		result = UTIL.ConvertToString(UTIL.clone(str));
	}

	public void visitNode(final IJavaNode pNode)
	{
		pNode.accept((IJavaVisitor) this);
	}

	/*
	 * public void visitDocument (final IJavaDocument pcmp) throws Exception { String str = null; String var1_2 = null;
	 * String var2_4 = null; JavaClassDefinitions jcds = (JavaClassDefinitions) pcmp.getSpecification(); var2_4 =
	 * jcds.getClassList().get(0).getIdentifier().getName(); var1_2 = new String("--BEGIN FileName: ").concat(var2_4);
	 * str = var1_2.concat(nl); Boolean cond_6 = null; cond_6 = pcmp.isSpecification(); if (cond_6.booleanValue()) try {
	 * { IJavaSpecification tmpArg_v_8 = null; tmpArg_v_8 = (IJavaSpecification) pcmp.getSpecification();
	 * visitSpecification((IJavaSpecification) tmpArg_v_8); } } catch (Exception e) { 
	 * e.printStackTrace(); } String rhs_9 = null; String var1_10 = null; String var1_11 = null; var1_11 =
	 * str.concat(result); var1_10 = var1_11.concat(new String("--END FileName: ")); String var2_15 = null; var2_15 =
	 * jcds.getClassList().get(0).getIdentifier().getName(); rhs_9 = var1_10.concat(var2_15); result =
	 * UTIL.ConvertToString(UTIL.clone(rhs_9)); }
	 */

	/*
	 * public void visitSpecification (final IJavaSpecification jspec) { String str = nl; JavaClassDefinitions jcds =
	 * (JavaClassDefinitions) jspec; { List<IJavaClassDefinition> sq_2 = null; sq_2 = jcds.getClassList();
	 * IJavaClassDefinition node = null; for (Iterator enm_16 = sq_2.iterator(); enm_16.hasNext(); ) {
	 * IJavaClassDefinition elem_3 = (IJavaClassDefinition) enm_16.next(); node = (IJavaClassDefinition) elem_3; { try {
	 * printNodeField((IJavaNode) node); } catch (Exception e) {  e.printStackTrace();
	 * } String rhs_8 = null; String var1_9 = null; String var1_10 = null; var1_10 = str.concat(nl); var1_9 =
	 * var1_10.concat(result); rhs_8 = var1_9.concat(nl); str = UTIL.ConvertToString(UTIL.clone(rhs_8)); } } } result =
	 * UTIL.ConvertToString(UTIL.clone(str)); }
	 */

	public void visitClassDefinition(final IJavaClassDefinition pcmp)
	{
		try
		{
			String str = "";
			if (!pcmp.getNested())
			{
				str = "import org.overturetool.VDM2JavaCG.Utilities.*;".concat(nl).concat("import java.util.*;").concat(nl);
			}
			// ---------printing the imports------------
			if (!import_flag)
			{
				if (!vdm2java.imports.isEmpty())
				{
					int size = vdm2java.imports.size();
					for (int i = 0; i < size; i++)
					{
						str = str.concat(vdm2java.imports.get(i)).concat(nl);
					}
					str = str.concat(nl).concat(nl);
				}
				import_flag = true;
			}
			// ---------printing access information-------------
			IJavaAccessDefinition jAD = (IJavaAccessDefinition) pcmp.getAccessdefinition();
			printNodeField((IJavaNode) jAD);
			str = str.concat(result);

			// -------------printing the modifiers------------------
			IJavaModifier jm = (IJavaModifier) pcmp.getModifiers();
			printNodeField((IJavaNode) jm);
			str = str.concat(result);

			str = str.concat("class ").concat(pcmp.getIdentifier().getName());
			Boolean cond_4 = null;
			cond_4 = !(pcmp.getInheritanceClause().getImplementList().isEmpty())
					|| !(pcmp.getInheritanceClause().getExtends().isEmpty());
			if (cond_4.booleanValue())
			{
				IJavaInheritanceClause inh = null;
				inh = (IJavaInheritanceClause) pcmp.getInheritanceClause();
				printNodeField((IJavaNode) inh);
			} else
				result = "";

			str = str.concat(result).concat(" {").concat(nl);

			IJavaDefinitionList jdl = pcmp.getBody();
			IJavaDefinition db = null;
			for (Iterator enm = jdl.getDefinitionList().iterator(); enm.hasNext();)
			{

				db = (IJavaDefinition) enm.next();
				{

					printNodeField((IJavaNode) db);
					String tmp_str = null;
					tmp_str = str.concat(nl).concat(result);
					str = tmp_str;

				}
			}
			if (!pcmp.getNested())
			{
				str = str.concat("public boolean equals (Object obj) {").concat(nl).concat(tab).concat("if (!(obj instanceof ").concat(pcmp.getIdentifier().getName()).concat("))");
				str = str.concat(nl).concat(tab).concat("  ").concat("return false;").concat(nl).concat(tab);
				str = str.concat("else").concat(nl).concat(tab).concat("  ").concat("return true; }").concat(nl);
			}
			str = str.concat("}");
			result = str;
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitInstanceVariableDefinition(
			IJavaInstanceVariableDefinition jivd)
	{
		try
		{
			String str = "";
			// ----------printing access modifiers---------------------------
			IJavaAccessDefinition jad = (IJavaAccessDefinition) jivd.getAccess();
			printNodeField((IJavaNode) jad);
			str = str.concat(result).concat(" ");

			IJavaModifier jm = (IJavaModifier) jivd.getModifiers();
			printNodeField((IJavaNode) jm);
			str = str.concat(result);

			// -------------------printing variable type and name ---------------------------
			IJavaType vt = (IJavaType) jivd.getType();

			printNodeField((IJavaNode) vt);

			str = str.concat(result).concat(" ").concat(jivd.getName().getName());
			// ----------------------printing initialization if any------------------
			if (jivd.getInitialized())
			{
				printNodeField((IJavaNode) jivd.getExpression());
				str = str.concat(" ").concat("=").concat(" ").concat(result);
			}
			result = str.concat(";");
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitInterfaceDefinition(IJavaInterfaceDefinition pNode)
	{
		try
		{
			String str = "";

			// ---------printing access information-------------
			IJavaAccessDefinition jAD = (IJavaAccessDefinition) pNode.getAccessdefinition();
			printNodeField((IJavaNode) jAD);
			str = str.concat(result);

			// -------------printing the modifiers------------------
			IJavaModifier jm = (IJavaModifier) pNode.getModifiers();
			printNodeField((IJavaNode) jm);
			str = str.concat(result);

			str = str.concat("interface ").concat(pNode.getIdentifier().getName());
			Boolean cond_4 = null;
			cond_4 = !pNode.getInheritanceClause().getExtends().isEmpty();
			if (cond_4.booleanValue())
			{
				IJavaInheritanceClause tmpArg_v_7 = null;
				tmpArg_v_7 = (IJavaInheritanceClause) pNode.getInheritanceClause();
				printNodeField((IJavaNode) tmpArg_v_7);
			} else
				result = UTIL.ConvertToString(UTIL.clone(new String("")));

			str = str.concat(result).concat(" {");
			String str1 = "";
			if (!pNode.getBody().getDefinitionList().isEmpty())
			{
				IJavaDefinitionList jdl = pNode.getBody();
				IJavaDefinition db = null;
				for (Iterator enm = jdl.getDefinitionList().iterator(); enm.hasNext();)
				{

					db = (IJavaDefinition) enm.next();
					{

						printNodeField((IJavaNode) db);
						String rhs_19 = null;
						String var1_20 = null;
						var1_20 = str1.concat(nl);
						rhs_19 = var1_20.concat(result);
						str1 = UTIL.ConvertToString(UTIL.clone(rhs_19));

					}
				}
			}
			str = str.concat("}").concat(nl).concat(str1);
			result = str;
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitEmptyDefinition(final IJavaEmptyDefinition je)
	{
		result = "";
	}

	public void visitInheritanceClause(final IJavaInheritanceClause pcmp)
	{
		try
		{
			String str = new String("");
			List<IJavaIdentifier> impl_list = pcmp.getImplementList();
			List<IJavaIdentifier> extend = pcmp.getExtends();
			Long length = new Long(impl_list.size());
			Long i = new Long(1);
			if (!impl_list.isEmpty())
				str = str.concat(" implements ");
			while (((i.intValue()) <= (length.intValue())))
			{

				String rhs_6 = null;
				String var2_8 = null;
				if ((1 <= i.intValue()) && (i.intValue() <= impl_list.size()))
					var2_8 = impl_list.get(i.intValue() - 1).getName();
				else
					UTIL.RunTime("Run-Time Error:Illegal index");
				rhs_6 = str.concat(var2_8);
				str = UTIL.ConvertToString(UTIL.clone(rhs_6));
				i = UTIL.NumberToLong(UTIL.clone(new Long(i.intValue()
						+ new Long(1).intValue())));
				if (((i.intValue()) <= (length.intValue())))
				{

					String rhs_17 = null;
					rhs_17 = str.concat(new String(" ,"));
					str = UTIL.ConvertToString(UTIL.clone(rhs_17));
				}
			}
			if (!extend.isEmpty())
			{
				str = str.concat(" extends ").concat(extend.get(0).getName());
			}
			result = str;
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitAccessDefinition(final IJavaAccessDefinition pcmp)
	{
		try
		{
			// System.out.println("what?!");
			String str = new String("");
			Boolean cond_2 = null;
			IJavaScope js = null;
			js = (IJavaScope) pcmp.getScope();
			printNodeField((IJavaNode) js);

			str = str.concat(result);

			cond_2 = pcmp.getStaticAccsess();
			if (cond_2.booleanValue())
				str = str.concat(" static");
			result = str.concat(" ");
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitIdentifier(final IJavaIdentifier pcmp)
	{

		String str = new String("");
		String list = new String(pcmp.getName());
		list = str.concat(list);
		result = UTIL.ConvertToString(UTIL.clone(list));

	}

	public void visitScope(final IJavaScope pNode)
	{
		try
		{
			boolean succ_2 = true;

			if (!pNode.isPUBLIC())
				succ_2 = false;
			if (succ_2)
				result = "public";
			else
			{

				succ_2 = true;
				if (!pNode.isPRIVATE())
					succ_2 = false;
				if (succ_2)
					result = "private";
				else
				{

					succ_2 = true;
					if (!pNode.isPROTECTED())
						succ_2 = false;
					if (succ_2)
						result = "protected";

					else
					{

						UTIL.RunTime("Run-Time Error:Can not evaluate an error statement");
					}
				}
			}
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitPattern(final IJavaPattern pNode)
	{
		pNode.accept((IJavaVisitor) this);
	}

	public void visitBinaryExpression(IJavaBinaryExpression pNode)
	{
		try
		{
			String str = "";
			printNodeField((IJavaNode) pNode.getLhsExpression());
			str = str.concat(result);

			printNodeField((IJavaNode) pNode.getOperator());
			str = str.concat(" ").concat(result);

			printNodeField((IJavaNode) pNode.getRhsExpression());
			str = str.concat(result);
			result = str;
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitImplicationExpression(IJavaImplicationExpression pNode)
	{
		try
		{
			String str = "Utils.implication(";
			printNodeField((IJavaNode) pNode.getLeft());
			str = str.concat(result).concat(", ");

			printNodeField((IJavaNode) pNode.getRight());
			str = str.concat(result).concat(")");
			result = str;
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitBiimplicationExpression(IJavaBiimplicationExpression pNode)
	{
		try
		{
			String str = "Utils.biimplication(";
			printNodeField((IJavaNode) pNode.getLeft());
			str = str.concat(result).concat(", ");

			printNodeField((IJavaNode) pNode.getRight());
			str = str.concat(result).concat(")");
			result = str;
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitIntDivExpression(IJavaIntDivExpression pNode)
	{
		try
		{
			String str = "Utils.div(";
			printNodeField((IJavaNode) pNode.getLeft());
			str = str.concat(result).concat(", ");

			printNodeField((IJavaNode) pNode.getRight());
			str = str.concat(result).concat(")");
			result = str;
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitRemainderExpression(IJavaRemainderExpression pNode)
	{
		try
		{
			String str = "Utils.rem(";
			printNodeField((IJavaNode) pNode.getLeft());
			str = str.concat(result).concat(", ");

			printNodeField((IJavaNode) pNode.getRight());
			str = str.concat(result).concat(")");
			result = str;
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitModulusExpression(IJavaModulusExpression pNode)
	{
		try
		{
			String str = "Utils.mod(";
			printNodeField((IJavaNode) pNode.getLeft());
			str = str.concat(result).concat(", ");

			printNodeField((IJavaNode) pNode.getRight());
			str = str.concat(result).concat(")");
			result = str;
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitStarStarExpression(IJavaStarStarExpression pNode)
	{
		try
		{
			String str = "Utils.StarStar(";
			printNodeField((IJavaNode) pNode.getLeft());
			str = str.concat(result).concat(", ");

			printNodeField((IJavaNode) pNode.getRight());
			str = str.concat(result).concat(")");
			result = str;
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitCompositionExpression(IJavaCompositionExpression pNode)
	{
		try
		{
			String str = "CGCollections.comp(";
			printNodeField((IJavaNode) pNode.getLeft());
			str = str.concat(result).concat(", ");

			printNodeField((IJavaNode) pNode.getRight());
			str = str.concat(result).concat(")");
			result = str;
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitMapUnionExpression(IJavaMapUnionExpression pNode)
	{
		try
		{
			String str = "CGCollections.munion(";
			printNodeField((IJavaNode) pNode.getLeft());
			str = str.concat(result).concat(", ");

			printNodeField((IJavaNode) pNode.getRight());
			str = str.concat(result).concat(")");
			result = str;
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitDomainResByExpression(IJavaDomainResByExpression pNode)
	{
		try
		{
			String str = "CGCollections.DomBy(";
			printNodeField((IJavaNode) pNode.getLeft());
			str = str.concat(result).concat(", ");

			printNodeField((IJavaNode) pNode.getRight());
			str = str.concat(result).concat(")");
			result = str;
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitRangeResByExpression(IJavaRangeResByExpression pNode)
	{
		try
		{
			String str = "CGCollections.RangeBy(";
			printNodeField((IJavaNode) pNode.getLeft());
			str = str.concat(result).concat(", ");

			printNodeField((IJavaNode) pNode.getRight());
			str = str.concat(result).concat(")");
			result = str;
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitDomainResToExpression(IJavaDomainResToExpression pNode)
	{
		try
		{
			String str = "CGCollections.DomTo(";
			printNodeField((IJavaNode) pNode.getLeft());
			str = str.concat(result).concat(", ");

			printNodeField((IJavaNode) pNode.getRight());
			str = str.concat(result).concat(")");
			result = str;
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitRangeResToExpression(IJavaRangeResToExpression pNode)
	{
		try
		{
			String str = "CGCollections.RangeTo(";
			printNodeField((IJavaNode) pNode.getLeft());
			str = str.concat(result).concat(", ");

			printNodeField((IJavaNode) pNode.getRight());
			str = str.concat(result).concat(")");
			result = str;
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitLengthExpression(IJavaLengthExpression pNode)
	{
		try
		{
			String str = "";
			printNodeField((IJavaNode) pNode.getSeqName());
			str = str.concat(result).concat(".size()");
			result = str;
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitElementsExpression(IJavaElementsExpression pNode)
	{
		try
		{
			String str = "CGCollections.elems(";
			printNodeField((IJavaNode) pNode.getSeqname());
			str = str.concat(result).concat(")");
			result = str;
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitIndexesExpression(IJavaIndexesExpression pNode)
	{
		try
		{
			String str = "CGCollections.indexes(";
			printNodeField((IJavaNode) pNode.getSeqName());
			str = str.concat(result).concat(")");
			result = str;
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitDistConcat(IJavaDistConcat pNode)
	{
		try
		{
			String str = "CGCollections.conc(";
			printNodeField((IJavaNode) pNode.getSeqName());
			str = str.concat(result).concat(")");
			result = str;
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitSeqCompExpression(final IJavaSeqCompExpression jsc)
	{
		try
		{
			String str = "CGCollections.SeqComprehension(";
			printNodeField((IJavaNode) ((JavaInSetExpression) jsc.getPredicate()).getOwner());
			str = str.concat(result).concat(",");
			printNodeField((IJavaNode) ((JavaSetBind) jsc.getBindlist().get(0)).getSetName());
			str = str.concat(result);
			result = str.concat(")");
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitSubVectorExpression(final IJavaSubVectorExpression jsc)
	{
		try
		{
			String str = "CGCollections.SubSequence(";
			printNodeField((IJavaNode) jsc.getVecName());
			str = str.concat(result).concat(", ");
			printNodeField((IJavaNode) jsc.getFrom());
			str = str.concat(result).concat("-1, ");
			printNodeField((IJavaNode) jsc.getTo());
			str = str.concat(result);
			result = str.concat("-1)");
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitVectorApplication(IJavaVectorApplication pNode)
	{
		try
		{
			String str = "";
			printNodeField((IJavaNode) pNode.getName());
			str = str.concat(result).concat(".elementAt(");

			printNodeField((IJavaNode) pNode.getAt());
			str = str.concat(result).concat("-1)");
			result = str;
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitMapApplication(IJavaMapApplication pNode)
	{
		try
		{
			String str = "";
			printNodeField((IJavaNode) pNode.getName());
			str = str.concat(result).concat(".get(");

			printNodeField((IJavaNode) pNode.getKey());
			str = str.concat(result).concat(")");
			result = str;
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitPlusPlusExpression(IJavaPlusPlusExpression pNode)
	{
		try
		{
			String str = "Utils.PlusPlus(";
			printNodeField((IJavaNode) pNode.getLeft());
			str = str.concat(result).concat(", ");

			printNodeField((IJavaNode) pNode.getRight());
			str = str.concat(result).concat(")");
			result = str;
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitVectorConcatExpression(IJavaVectorConcatExpression pNode)
	{
		try
		{
			String str = "CGCollections.concatenation(";
			printNodeField((IJavaNode) pNode.getLeft());
			str = str.concat(result).concat(", ");
			printNodeField((IJavaNode) pNode.getRight());
			str = str.concat(result).concat(")");
			result = str;
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitUnaryExpression(IJavaUnaryExpression pNode)
	{
		try
		{
			String str = "";
			printNodeField((IJavaNode) pNode.getOperator());
			str = str.concat(result).concat("(");

			printNodeField((IJavaNode) pNode.getExpression());
			str = str.concat(result).concat(")");
			result = str;
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitEqualsExpression(IJavaEqualsExpression pNode)
	{
		try
		{
			String str = "(";
			printNodeField((IJavaNode) pNode.getLhsExpression());
			str = str.concat(result).concat(")");

			printNodeField((IJavaNode) pNode.getOperator());
			str = str.concat(result).concat("(");

			printNodeField((IJavaNode) pNode.getRhsExpression());
			str = str.concat(result).concat(")");
			result = str;
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitLiteral(final IJavaLiteral pNode)
	{
		pNode.accept((IJavaVisitor) this);
	}

	public void visitType(final IJavaType pNode)
	{
		pNode.accept((IJavaVisitor) this);
	}

	public void visitPatternIdentifier(final IJavaPatternIdentifier pcmp)
	{

		String str = null;
		String var1_2 = null;
		var1_2 = pcmp.getIdentifier().getName();
		str = var1_2.concat(new String(""));
		result = UTIL.ConvertToString(UTIL.clone(str));
	}

	public void visitApplyExpression(IJavaApplyExpression pNode)
	{
		try
		{
			String str = "";
			printNodeField((IJavaNode) pNode.getExpression());
			str = str.concat(result).concat("(");
			int size = pNode.getExpressionList().size();
			for (int i = 0; i < size; i++)
			{
				printNodeField((IJavaNode) pNode.getExpressionList().get(i));
				str = str.concat(result);
				if ((i + 1) < size)
					str = str.concat(", ");
			}
			str = str.concat(")");
			result = str;
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitSymbolicLiteralExpression(
			final IJavaSymbolicLiteralExpression pcmp)
	{
		try
		{
			String str = new String("");
			IJavaLiteral tmpArg_v_3 = null;
			tmpArg_v_3 = (IJavaLiteral) pcmp.getLiteral();
			printNodeField((IJavaNode) tmpArg_v_3);
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitVectorEnumExpression(IJavaVectorEnumExpression pNode)
	{
		try
		{
			if (pNode.getElements().isEmpty())
			{
				result = "new Vector()";
			} else
			{
				String str = "CGCollections.SeqEnumeration(";
				int size = pNode.getElements().size();
				for (int i = 0; i < size; i++)
				{
					printNodeField((IJavaNode) pNode.getElements().get(i));
					str = str.concat(result);
					if (i < size - 1)
					{
						str = str.concat(", ");
					}
				}
				result = str.concat(")");
			}
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitStringLiteralExpression(IJavaStringLiteralExpression jsl)
	{
		String str = "";
		result = str.concat("\"").concat(jsl.getString()).concat("\"");
	}

	public void visitTextLiteral(final IJavaTextLiteral pcmp)
	{

		String str = null;
		str = pcmp.getVal();
		String rhs_2 = null;
		String var1_3 = null;
		var1_3 = new String("\"").concat(str);
		rhs_2 = var1_3.concat(new String("\""));
		result = UTIL.ConvertToString(UTIL.clone(rhs_2));
	}

	public void visitQuoteLiteralExpression(IJavaQuoteLiteralExpression jsl)
	{
		String str = "(new Quote(".concat(jsl.getLiteral()).concat("))");
		result = str;
	}

	public void visitTokenExpression(IJavaTokenExpression jsl)
	{
		try
		{
			String str = "(new Token(";
			printNodeField((IJavaNode) jsl.getValue());
			str = str.concat(result).concat("))");
			result = str;
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitCharacterLiteral(final IJavaCharacterLiteral pcmp)
	{

		String str = new String("\'").concat(pcmp.getVal().toString()).concat(new String("\'"));
		result = str;
	}

	public void visitCharType(final IJavaCharType var_1_1)
	{

		result = new String("Character");
	}

	public void visitQuoteType(final IJavaQuoteType q)
	{

		result = q.getLiterral();
	}

	public void visitTokenType(final IJavaTokenType t)
	{

		result = "Token";
	}

	public void visitStringType(IJavaStringType pNode)
	{
		result = "String";
	}

	public void visitName(final IJavaName pcmp)
	{

		String str = new String("");
		Boolean flag = !pcmp.getIdentifier().getName().isEmpty();
		if (flag.booleanValue())
		{

			String name = null;
			name = pcmp.getIdentifier().getName();
			str = str.concat(name);
		}
		result = str;

	}

	public void visitIntType(final IJavaIntType ji)
	{

		result = "Integer";
	}

	public void visitClassType(final IJavaClassType jst)
	{

		result = jst.getName();
	}

	public void visitBooleanType(final IJavaBooleanType var_1_1)
	{

		result = "Boolean";
	}

	public void visitGenMap(IJavaGenMap mt)
	{
		try
		{
			String str = "HashMap<";
			printNodeField((IJavaNode) mt.getKey());
			str = str.concat(result).concat(",");
			printNodeField((IJavaNode) mt.getValue());
			str = str.concat(result).concat(">");
			result = str;
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitBiMap(IJavaBiMap mt)
	{
		try
		{
			String str = "BiMap<";
			printNodeField((IJavaNode) mt.getKey());
			str = str.concat(result).concat(",");
			printNodeField((IJavaNode) mt.getValue());
			str = str.concat(result).concat(">");
			result = str;
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitBinaryOperator(final IJavaBinaryOperator pNode)
	{
		String str = null;
		if (pNode.isGT())
			str = ">";
		if (pNode.isLT())
			str = "<";
		if (pNode.isEQEQ())
			str = "==";
		if (pNode.isEQ())
			str = "=";
		if (pNode.isAND())
			str = "&&";
		if (pNode.isDIV())
			str = "/";
		if (pNode.isDIVIDE())
			str = "/";
		if (pNode.isGE())
			str = ">=";
		if (pNode.isLE())
			str = "<=";
		if (pNode.isMINUS())
			str = "-";
		if (pNode.isMOD())
			str = "%";
		if (pNode.isMULTIPLY())
			str = "*";
		if (pNode.isNE())
			str = "!=";
		if (pNode.isOR())
			str = "||";
		if (pNode.isPLUS())
			str = "+";
		if (pNode.isREM())
			str = "%";
		result = str.concat(" ");
	}

	public void visitUnaryOperator(final IJavaUnaryOperator pNode)
	{
		String str = null;
		if (pNode.isMINUS())
			str = "-";
		if (pNode.isPLUS())
			str = "+";
		if (pNode.isNOT())
			str = "!";
		if (pNode.isFLOOR())
			str = "(Integer) Math.floor";
		if (pNode.isABSOLUTE())
			str = "Math.abs";
		result = str;
	}

	public void visitHeadExpression(final IJavaHeadExpression pNode)
	{
		try
		{
			String str = "";
			printNodeField((IJavaNode) pNode.getExpression());
			str = result.concat(".firstElement()");
			result = str;
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitTailExpression(final IJavaTailExpression pNode)
	{
		try
		{
			String str = "";
			printNodeField((IJavaNode) pNode.getExpression());
			str = str.concat("new Vector(").concat(result).concat(".subList(1, ").concat(result).concat(".size()))");
			result = str;
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitCardinalityExpression(final IJavaCardinalityExpression jc)
	{
		try
		{
			String str = "";
			printNodeField((IJavaNode) jc.getName());
			str = str.concat(result).concat(".size()");
			result = str;
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitBinaryObjectOperator(final IJavaBinaryObjectOperator pNode)
	{
		String str = null;
		if (pNode.isEQUALS())
			str = ".equals";
		result = str;
	}

	public void visitDoubleType(final IJavaDoubleType var_1_1)
	{

		String str = new String("Double");
		result = str;
	}

	public void visitUnresolvedType(final IJavaUnresolvedType jut)
	{
		result = jut.getTypename().getName();
	}

	public void visitVectorType(IJavaVectorType pNode)
	{
		try
		{
			String str = "Vector<";
			printNodeField((IJavaNode) pNode.getType());
			/*
			 * if (result.equals(new String("int"))) { result = "Integer"; } if (result.equals(new String("boolean"))) {
			 * result = "Boolean"; } if (result.equals(new String("double"))) { result = "Double"; } if
			 * (result.equals(new String("char"))) { result = "Character"; } if (result.equals(new String("float"))) {
			 * result = "Float"; } if (result.equals(new String("short"))) { result = "Short"; } if (result.equals(new
			 * String("byte"))) { result = "Byte"; } if (result.equals(new String("long"))) { result = "Long"; }
			 */
			str = str.concat(result).concat(">");
			result = str;
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitSetType(IJavaSetType st)
	{
		try
		{
			String str = "HashSet<";
			printNodeField((IJavaNode) st.getType());
			str = str.concat(result).concat(">");
			result = str;
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitSetCompExpression(final IJavaSetCompExpression jsc)
	{
		try
		{
			String str = "CGCollections.SetComprehension(";
			printNodeField((IJavaNode) ((JavaInSetExpression) jsc.getPredicate()).getOwner());
			str = str.concat(result).concat(",");
			printNodeField((IJavaNode) ((JavaSetBind) jsc.getBindlist().get(0)).getSetName());
			str = str.concat(result);
			result = str.concat(")");
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitSetEnumExpression(final IJavaSetEnumExpression jse)
	{
		try
		{
			String str = "CGCollections.SetEnumeration(";
			for (int i = 0; i < jse.getArgs().size(); i++)
			{
				printNodeField((IJavaNode) jse.getArgs().get(i));
				if (!(i == (jse.getArgs().size() - 1)))
					str = str.concat(result).concat(",");
				else
					str = str.concat(result);
			}
			result = str.concat(")");
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitSetDifferenceExpression(
			final IJavaSetDifferenceExpression jsd)
	{
		try
		{
			String str = "CGCollections.difference(";
			printNodeField((IJavaNode) jsd.getLeft());
			str = str.concat(result).concat(",");
			printNodeField((IJavaNode) jsd.getRight());
			str = str.concat(result);
			result = str.concat(")");
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitSetIntersectExpression(
			final IJavaSetIntersectExpression jsi)
	{
		try
		{
			String str = "CGCollections.inter(";
			printNodeField((IJavaNode) jsi.getLeft());
			str = str.concat(result).concat(",");
			printNodeField((IJavaNode) jsi.getRight());
			str = str.concat(result);
			result = str.concat(")");
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitSetRangeExpression(final IJavaSetRangeExpression jsr)
	{
		try
		{
			String str = "CGCollections.SetRange(";
			printNodeField((IJavaNode) jsr.getHead());
			str = str.concat(result).concat(",");
			printNodeField((IJavaNode) jsr.getTail());
			str = str.concat(result);
			result = str.concat(")");
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitSetUnionExpression(final IJavaSetUnionExpression jsu)
	{
		try
		{
			String str = "CGCollections.union(";
			printNodeField((IJavaNode) jsu.getLeft());
			str = str.concat(result).concat(",");
			printNodeField((IJavaNode) jsu.getRight());
			str = str.concat(result);
			result = str.concat(")");
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitInSetExpression(final IJavaInSetExpression jis)
	{
		try
		{
			String str = "";
			printNodeField((IJavaNode) jis.getOwner());
			str = str.concat(result).concat(".contains(");
			printNodeField((IJavaNode) jis.getElm());
			str = str.concat(result).concat(")");
			result = str;
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitNotInSetExpression(final IJavaNotInSetExpression jnis)
	{
		try
		{
			String str = "!";
			printNodeField((IJavaNode) jnis.getOwner());
			str = str.concat(result).concat(".contains(");
			printNodeField((IJavaNode) jnis.getElm());
			str = str.concat(result).concat(")");
			result = str;
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitPowerSetExpression(final IJavaPowerSetExpression jps)
	{
		try
		{
			String str = "CGCollections.power(";
			printNodeField((IJavaNode) jps.getSetname());
			str = str.concat(result).concat(")");
			result = str;
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitSubSetExpression(IJavaSubSetExpression jss)
	{
		try
		{
			String str = "";
			printNodeField((IJavaNode) jss.getLeft());
			str = str.concat(result).concat(".containsAll(");
			printNodeField((IJavaNode) jss.getRight());
			str = str.concat(result);
			result = str.concat(")");
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitProperSubsetExpression(IJavaProperSubsetExpression jpss)
	{
		try
		{
			String str = "CGCollections.psubset(";
			printNodeField((IJavaNode) jpss.getLeft());
			str = str.concat(result).concat(",");
			printNodeField((IJavaNode) jpss.getRight());
			str = str.concat(result);
			result = str.concat(")");
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitDistUnionExpression(IJavaDistUnionExpression jdu)
	{
		try
		{
			String str = "CGCollections.dunion(";
			printNodeField((IJavaNode) jdu.getSetname());
			str = str.concat(result);
			result = str.concat(")");
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitDistIntersectionExpression(
			IJavaDistIntersectionExpression jdi)
	{
		try
		{
			String str = "CGCollections.dinter(";
			printNodeField((IJavaNode) jdi.getSetname());
			str = str.concat(result);
			result = str.concat(")");
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	// ---------Map Expressions--------------------------------

	public void visitDistMergeExpression(IJavaDistMergeExpression jdu)
	{
		try
		{
			String str = "CGCollections.merge(";
			printNodeField((IJavaNode) jdu.getMapname());
			str = str.concat(result);
			result = str.concat(")");
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitMapRangeExpression(IJavaMapRangeExpression jdu)
	{
		try
		{
			String str = "CGCollections.range(";
			printNodeField((IJavaNode) jdu.getMapname());
			str = str.concat(result);
			result = str.concat(")");
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitMapDomainExpression(IJavaMapDomainExpression jdu)
	{
		try
		{
			String str = "";
			printNodeField((IJavaNode) jdu.getMapname());
			str = str.concat(result).concat(".keySet()");
			result = str;
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitMapInverseExpression(IJavaMapInverseExpression jdu)
	{
		try
		{
			String str = "";
			printNodeField((IJavaNode) jdu.getMapname());
			str = str.concat(result).concat(".inverse()");
			result = str;
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitMapletExpression(IJavaMapletExpression jme)
	{
		try
		{
			String str = "CGCollections.MapLet(";
			printNodeField((IJavaNode) jme.getLeft());
			str = str.concat(result).concat(", ");
			printNodeField((IJavaNode) jme.getRight());
			str = str.concat(result);
			result = str.concat(")");
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitMapEnumExpression(IJavaMapEnumExpression jmee)
	{
		try
		{
			String str = "CGCollections.MapEnumeration(";
			for (int i = 0; i < jmee.getArgs().size(); i++)
			{
				printNodeField((IJavaNode) jmee.getArgs().get(i));
				if (!(i == (jmee.getArgs().size() - 1)))
					str = str.concat(result).concat(",");
				else
					str = str.concat(result);
			}
			result = str.concat(")");
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitNewExpression(IJavaNewExpression jne)
	{
		try
		{
			String str = "new ";
			printNodeField((IJavaNode) jne.getClassName());
			str = str.concat(result).concat("(");
			if (!jne.getExpressions().isEmpty())
			{
				for (int i = 0; i < jne.getExpressions().size(); i++)
				{
					printNodeField((IJavaNode) jne.getExpressions().get(i));
					str = str.concat(result);
					if (i < (jne.getExpressions().size() - 1))
						str = str.concat(", ");
				}
			}
			result = str.concat(")");
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitThisExpression(IJavaThisExpression jne)
	{

		result = "this";
	}

	public void visitIsExpression(final IJavaIsExpression pcmp)
	{
		try
		{
			String str = new String("");
			printNodeField((IJavaNode) pcmp.getInstanceName());

			str = str.concat(result).concat(" instanceof ");

			printNodeField((IJavaNode) pcmp.getClassName());

			result = str.concat(result);
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitSameClassMembership(final IJavaSameClassMembership pcmp)
	{
		try
		{
			String str = new String("");
			printNodeField((IJavaNode) pcmp.getLeft());

			str = str.concat(result).concat(".getClass()").concat(".equals(");

			printNodeField((IJavaNode) pcmp.getRight());

			result = str.concat(result).concat(".getClass())");
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitIsBasicTypeExpression(final IJavaIsBasicTypeExpression pcmp)
	{
		try
		{
			String str = new String("Utils.Is(");
			printNodeField((IJavaNode) pcmp.getValue());

			str = str.concat(result).concat(", ");

			printNodeField((IJavaNode) pcmp.getType());

			result = str.concat("\"").concat(result).concat("\")");
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitNumericLiteral(final IJavaNumericLiteral pcmp)
	{
		try
		{
			String str = new String("");
			Long tmpArg_v_3 = null;
			tmpArg_v_3 = pcmp.getVal();
			printIntField(tmpArg_v_3);
			str = str.concat(result);
			result = str;
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitRealLiteral(final IJavaRealLiteral pcmp)
	{
		try
		{
			String str = new String("");
			Double tmpArg_v_3 = null;
			tmpArg_v_3 = pcmp.getVal();
			printDoubleField(tmpArg_v_3);
			str = UTIL.ConvertToString(UTIL.clone(result));
			result = UTIL.ConvertToString(UTIL.clone(str));
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitQuoteLiteral(final IJavaQuoteLiteral pcmp)
	{
		try
		{
			String str = new String("<");
			String tmpArg_v_3 = null;
			tmpArg_v_3 = pcmp.getVal();
			printStringField(tmpArg_v_3);
			String rhs_4 = null;
			String var1_5 = null;
			String var2_7 = null;
			int from_11 = (int) Math.max(new Long(2).doubleValue() - 1, 0);
			int to_12 = (int) Math.min(new Long(new Long(result.length()).intValue()
					- new Long(1).intValue()).doubleValue(), result.length());
			if (from_11 > to_12)
				var2_7 = new String();
			else
				var2_7 = new String(result.substring(from_11, to_12));
			var1_5 = str.concat(var2_7);
			rhs_4 = var1_5.concat(new String(">"));
			str = UTIL.ConvertToString(UTIL.clone(rhs_4));
			result = UTIL.ConvertToString(UTIL.clone(str));
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitBooleanLiteral(final IJavaBooleanLiteral pcmp)
	{
		try
		{
			String str = "";
			printBooleanField(pcmp.getVal());
			str = str.concat(result);
			result = str;
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitNilLiteral(final IJavaNilLiteral var_1_1)
	{
		result = new String(" null ");
	}

	public void visitReturnStatement(final IJavaReturnStatement jrs)
	{
		try
		{
			String str = "";
			if (jrs.hasExpression())
			{
				IJavaExpression je = jrs.getExpression();
				printNodeField((IJavaNode) je);

				str = str.concat("return ").concat(result).concat(";");
				result = str;
			}
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitAssignStatement(final IJavaAssignStatement jas)
	{
		try
		{
			String str = "";
			printNodeField((IJavaNode) jas.getStateDesignator());
			str = str.concat(result).concat(" = ");
			printNodeField((IJavaNode) jas.getExpression());
			str = str.concat(result);
			result = str.concat(";");
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitStateDesignatorName(final IJavaStateDesignatorName jsdn)
	{
		result = jsdn.getName().getIdentifier().getName();
	}

	public void visitFieldReference(final IJavaFieldReference jas)
	{
		try
		{
			String str = "";
			printNodeField((IJavaNode) jas.getStateDesignator());
			str = str.concat(result).concat(".").concat(jas.getIdentifier().getName());
			result = str;
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitMapOrSequenceReference(
			final IJavaMapOrSequenceReference jas)
	{
		try
		{
			String str = "";
			printNodeField((IJavaNode) jas.getStateDesignator());
			str = str.concat(result).concat("(");
			printNodeField((IJavaNode) jas.getExpression());
			// ------needed to be improved to be able to determine the Sequence reference and subtract one(because in
			// Java
			// Vector starts from 0)---
			str = str.concat(result).concat(")");
			result = str;
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitEmptyStatement(IJavaEmptyStatement pNode)
	{
		result = "";
	}

	public void visitNotYetSpecifiedStatement(
			IJavaNotYetSpecifiedStatement pNode)
	{
		result = "throw new InternalError(\"This method is not yet specified!\");";
	}

	public void visitBlockStatement(final IJavaBlockStatement jbs)
	{
		try
		{
			String str = "{";
			for (int i = 0; i < jbs.getStatements().size(); i++)
			{
				printNodeField((IJavaNode) jbs.getStatements().get(i));
				str = str.concat(result).concat(nl);
			}
			result = str.concat("}").concat(nl);
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitAtomicStatement(final IJavaAtomicStatement jbs)
	{
		try
		{
			String str = "{";
			for (int i = 0; i < jbs.getStatements().size(); i++)
			{
				printNodeField((IJavaNode) jbs.getStatements().get(i));
				str = str.concat(result).concat(nl);
			}
			result = str.concat("}").concat(nl);
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitErrorStatement(final IJavaErrorStatement pcmp)
	{
		result = "throw new InternalError (\"The result of the statement is undefined!\");";
	}

	public void visitContinueStatement(final IJavaContinueStatement pcmp)
	{
		result = "continue;";
	}

	public void visitCallStatement(final IJavaCallStatement jbs)
	{
		try
		{
			String str = jbs.getName().getName();
			str = str.concat("(");
			for (int i = 0; i < jbs.getArgs().size(); i++)
			{
				printNodeField((IJavaNode) jbs.getArgs().get(i));
				str = str.concat(result);
				if (i < jbs.getArgs().size() - 1)
					str = str.concat(",");
			}
			result = str.concat(");");
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitCallObjectStatement(final IJavaCallObjectStatement jbs)
	{
		try
		{
			String str = "";
			printNodeField((IJavaNode) jbs.getObjectDesignator());
			str = str.concat(result).concat(".").concat(jbs.getName().getIdentifier().getName()).concat("(");
			for (int i = 0; i < jbs.getExpressionList().size(); i++)
			{
				printNodeField((IJavaNode) jbs.getExpressionList().get(i));
				str = str.concat(result);
				if (i < jbs.getExpressionList().size() - 1)
					str = str.concat(",");
			}
			result = str.concat(");");
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitNameDesignator(final IJavaNameDesignator jsdn)
	{
		result = jsdn.getName().getName();
	}

	public void visitThisDesignator(final IJavaThisDesignator jsdn)
	{
		try
		{
			String str = "";
			printNodeField((IJavaNode) jsdn.getThis());
			str = result;
			result = str;
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitNewDesignator(final IJavaNewDesignator jsdn)
	{
		try
		{
			String str = "";
			printNodeField((IJavaNode) jsdn.getNew());
			str = result;
			result = str;
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitObjectFieldReference(final IJavaObjectFieldReference jas)
	{
		try
		{
			String str = "";
			printNodeField((IJavaNode) jas.getObjectDesignator());
			str = str.concat(result).concat(".").concat(jas.getName().getIdentifier().getName());

			result = str;
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitObjectApply(final IJavaObjectApply jbs)
	{
		try
		{
			String str = "";
			printNodeField((IJavaNode) jbs.getObjectDesignator());
			str = str.concat(result).concat("(");
			for (int i = 0; i < jbs.getExpressionList().size(); i++)
			{
				printNodeField((IJavaNode) jbs.getExpressionList().get(i));
				str = str.concat(result);
				if (i < jbs.getExpressionList().size() - 1)
					str = str.concat(",");
			}
			result = str.concat(")");
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitWhileLoop(final IJavaWhileLoop wl)
	{
		try
		{
			String str = "while(";
			printNodeField((IJavaNode) wl.getCondition());
			str = str.concat(result).concat(") {").concat(nl).concat(tab);
			printNodeField((IJavaNode) wl.getStatement());
			str = str.concat(result).concat("}");

			result = str.concat(nl);
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitSetForLoop(final IJavaSetForLoop wl)
	{
		try
		{
			String str = "for(Iterator<Integer> i = ";
			printNodeField((IJavaNode) wl.getSet());
			str = str.concat(result).concat(".iterator(); i.hasNext();)").concat(" {").concat(nl).concat(tab);
			str = str.concat(wl.getVarName()).concat(" = i.next();").concat(nl).concat(tab);
			printNodeField((IJavaNode) wl.getStatement());
			str = str.concat(result).concat("}");
			result = str.concat(nl);
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitSeqForLoop(final IJavaSeqForLoop wl)
	{
		try
		{
			String str = "";
			if (!wl.getReverse())
			{
				str = "for(int ".concat(wl.getVarName()).concat(" = 0; ").concat(wl.getVarName()).concat(" < ");
				printNodeField((IJavaNode) wl.getSeq());
				str = str.concat(result).concat(".size(); ").concat(wl.getVarName()).concat("++)");
			} else
			{
				str = "for(int ".concat(wl.getVarName()).concat(" = ");
				printNodeField((IJavaNode) wl.getSeq());
				str = str.concat(result).concat(".size(); ").concat(wl.getVarName()).concat(" > 0; ").concat(wl.getVarName()).concat("--)");
			}

			str = str.concat(" {").concat(nl).concat(tab);
			printNodeField((IJavaNode) wl.getStatement());
			str = str.concat(result).concat("}");
			result = str.concat(nl);
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitIndexForLoop(final IJavaIndexForLoop wl)
	{
		try
		{
			String str = "";
			str = "for(int ".concat(wl.getVarName()).concat(" = ");
			printNodeField((IJavaNode) wl.getFrom());
			str = str.concat(result).concat(";").concat(wl.getVarName()).concat(" <= ");
			printNodeField((IJavaNode) wl.getTo());

			str = str.concat(result).concat("; ").concat(wl.getVarName()).concat(" + ");
			printNodeField((IJavaNode) wl.getStep());
			str = str.concat(result).concat(")").concat(" {").concat(nl).concat(tab);

			printNodeField((IJavaNode) wl.getStatement());
			str = str.concat(result).concat("}");
			result = str.concat(nl);
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitAssignmentDefinition(final IJavaAssignmentDefinition pcmp)
	{
		try
		{
			String str = "";
			printNodeField((IJavaNode) pcmp.getType());
			str = str.concat(result).concat(" ");
			str = str.concat(pcmp.getIdentifier().getName()).concat(" = ");
			printNodeField((IJavaNode) pcmp.getExpression());
			str = str.concat(result).concat(";");
			result = str;
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitMethodBody(final IJavaMethodBody pcmp)
	{
		try
		{
			String str = new String("{").concat(nl).concat(tab);
			// -------printing Precondition------------
			if (!(pcmp.getPrecondition() instanceof IJavaEmptyExpression))
			{
				printNodeField((IJavaNode) pcmp.getPrecondition());
				str = str.concat("if (").concat(result).concat(")").concat(nl).concat(tab).concat(tab).concat("throw new InternalError(\"Precondition is not satisfied!\");");
			}
			// --------printing the statement----------
			Boolean flag = null;
			flag = !(pcmp.getStatement().isEmpty());
			if (flag.booleanValue())
			{

				int max_st = pcmp.getStatement().size();
				for (int i = 0; i < max_st; i++)
				{

					IJavaStatement js = (IJavaStatement) pcmp.getStatement().get(i);
					printNodeField((IJavaNode) js);
				}

				str = str.concat(result).concat(nl).concat(tab);

			} else
			{
				result = "";
				str = str.concat(result).concat(nl).concat(tab);
			}
			if (pcmp.getImplicit())
			{
				str = str.concat("throw new InternalError(\"This method is only defined implicitly!\");").concat(nl);
			}
			// -----------Printing postcondition----------------------------------------------
			if (!(pcmp.getPostcondition() == ""))
			{
				str = str.concat("//-------postcondition '").concat(pcmp.getPostcondition()).concat("' is ignored-------------------------------").concat(nl);
					StatusLog.warn("Postcondition is ignored in method '"
							+ ((JavaMethodDefinition) pcmp.getParent()).getIdentifier().getName()
							+ "'");
			}
			str = str.concat(tab).concat(new String("}"));
			result = str;
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitMethodDefinition(final IJavaMethodDefinition pcmp)
	{
		try
		{
			String str = new String("");
			// String str_static = new String("");
			// String str_init = new String("");
			// -------printing static and non-static initializers---------
			/*
			 * if (!init_flag) { if (!Initializer_list.isEmpty()) { for(int i = 0; i <Initializer_list.size(); i++) { if
			 * (Initializer_list.get(i) instanceof IJavaStatic_Init) { IJavaExpression JE = ((JavaStatic_Init)
			 * Initializer_list.get(i)).getBody(); try { printNodeField((IJavaNode) JE); } catch (Exception e) {  e.printStackTrace(); } str_static = str_static.concat(result); } } if
			 * (!(str_static == "")) { str = "static {".concat(str_static).concat(" }").concat(nl); } for (int i = 0; i
			 * <Initializer_list.size(); i++) { if (Initializer_list.get(i) instanceof IJavaInit) { IJavaExpression je =
			 * ((JavaInit) Initializer_list.get(i)).getBody(); try { printNodeField((IJavaNode)je); } catch (Exception
			 * e) { e.printStackTrace(); } str_init = str_init.concat(result); } } if
			 * (!(str_init == "")) { str =
			 * ("private void Initialize() {").concat(nl).concat("tab").concat(str_init).concat(" }").concat(nl); }
			 * init_flag = true; } }
			 */
			IJavaAccessDefinition jad = null;

			// -----------print access field--------------------
			jad = pcmp.getAccess();
			printNodeField((IJavaNode) jad);

			str = str.concat(result);

			// ------------print modifiers-------------------------
			IJavaModifier jm = pcmp.getModifiers();
			printNodeField((IJavaNode) jm);
			str = str.concat(result);

			// ----------print return-type field-------------------

			if (!pcmp.getIsConstructor())
			{
				String rt1 = "";
				IJavaType rt = pcmp.getReturntype();
				printNodeField((IJavaNode) rt);
				if (rt instanceof JavaVoidType)
					rt1 = str.concat("void").concat(" ");
				else
					rt1 = str.concat(result).concat(" ");
				str = rt1;
			}

			// ******print print method identifier--------------------
			IJavaIdentifier ji = pcmp.getIdentifier();
			printNodeField((IJavaNode) ji);
			String ji1 = str.concat(result);
			str = ji1;

			// *********Print ParameterList---------------
			List<IJavaBind> jb = pcmp.getParameterList();
			for (int i = 0; i < jb.size(); i++)
			{
				printNodeField((IJavaNode) jb.get(i));
			}
			String jb1 = str.concat(result).concat(" ");
			str = jb1;

			// *******print Method Body-----------------
			IJavaMethodBody jmb = null;
			jmb = pcmp.getBody();
			if (!jmb.getSubclassResponsibility())
			{
				printNodeField((IJavaNode) jmb);
			} else
				result = ";";
			result = str.concat(result).concat("\n");
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitModifier(IJavaModifier pNode)
	{
		String str = "";
		if (pNode.getAbstract())
			str = str.concat("abstract").concat(" ");
		if (pNode.getFinal())
			str = str.concat("final").concat(" ");
		result = str;
	}

	public void visitIfExpression(IJavaIfExpression pNode)
	{
		try
		{
			String str = "((";
			printNodeField((IJavaNode) pNode.getIfExpression());
			str = str.concat(result).concat(") ? ");

			IJavaExpression ijs = pNode.getThenExpression();
			printNodeField((IJavaNode) ijs);
			str = str.concat(result).concat(" : ");

			if (!pNode.getElseifExpressionList().isEmpty())
			{
				int size = pNode.getElseifExpressionList().size();
				IJavaElseIfExpression jeis;
				for (int i = 0; i < size; i++)
				{
					jeis = pNode.getElseifExpressionList().get(i);
					printNodeField((IJavaNode) jeis);
					str = str.concat(result).concat(" : ");
				}
			}

			if (!(pNode.getElseExpression() instanceof IJavaEmptyExpression))
			{
				IJavaExpression js = pNode.getElseExpression();
				printNodeField((IJavaNode) js);
				str = str.concat(result).concat(")");
				for (int i = 0; i < pNode.getElseifExpressionList().size(); i++)
					str = str.concat(")");
			}
			result = str;
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitElseIfExpression(IJavaElseIfExpression jeis)
	{
		try
		{
			String str = "((";
			printNodeField((IJavaNode) jeis.getElseifExpression());
			str = str.concat(result).concat(")").concat(" ? ");
			printNodeField((IJavaNode) jeis.getThenExpression());
			str = str.concat(result).concat(" : ");
			result = str;
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitIfStatement(IJavaIfStatement pNode)
	{
		try
		{
			String str = "if".concat("(");
			printNodeField((IJavaNode) pNode.getExpression());
			str = str.concat(result).concat(")").concat(nl).concat(tab).concat("  ").concat("{ ");

			IJavaStatement ijs = pNode.getThenStatement();
			printNodeField((IJavaNode) ijs);
			str = str.concat(result).concat(" }").concat(nl);

			if (!pNode.getElselist().isEmpty())
			{
				int size = pNode.getElselist().size();
				IJavaElseIfStatement jeis;
				for (int i = 0; i < size; i++)
				{
					jeis = pNode.getElselist().get(i);
					printNodeField((IJavaNode) jeis);
					str = str.concat(result).concat(nl);
				}
			}

			if (!(pNode.getElseStatement() instanceof IJavaEmptyStatement))
			{
				IJavaStatement js = pNode.getElseStatement();
				printNodeField((IJavaNode) js);
				str = str.concat(tab).concat("else").concat(nl).concat(tab).concat("  ").concat("{ ").concat(result).concat(" }");
			}
			result = str;
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitElseIfStatement(IJavaElseIfStatement jeis)
	{
		try
		{
			String str = tab.concat("else if(");
			printNodeField((IJavaNode) jeis.getElseifExpression());
			str = str.concat(result).concat(")").concat(nl).concat(tab).concat("  { ");
			printNodeField((IJavaNode) jeis.getThenStatement());
			str = str.concat(result).concat(" }");
			result = str;
		} catch (Exception e)
		{
			errors.add(e);
		}
	}

	public void visitTypeBind(final IJavaTypeBind jtb)
	{
		try
		{
			String str = new String("(");

			// *******print parameter type
			IJavaType jt = null;
			if (!jtb.getTypeList().isEmpty()
					&& !jtb.getPatternPattern().isEmpty())
			{
				int list_size = jtb.getTypeList().size();
				for (int i = 0; i < list_size; i++)
				{
					jt = jtb.getTypeList().get(i);
					printNodeField((IJavaNode) jt);
					str = str.concat(result).concat(" ");
					result = str;

					// ----------------print parameter pattern--------------

					List<IJavaPattern> jp = null;
					jp = jtb.getPatternPattern();
					printNodeField((IJavaNode) jp.get(i));
					str = str.concat(result);
					if ((i + 1) < list_size)
					{
						str = str.concat(", ");
					}
				}
			}
			str = str.concat(")");
			result = UTIL.ConvertToString(UTIL.clone(str));

			// result = UTIL.ConvertToString(UTIL.clone(new String("NOT YET SUPPORTED")));
		} catch (Exception e)
		{
			errors.add(e);
		}
	}
}