package org.overture.codegen.cgen;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.expressions.AVariableExp;
import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AClassHeaderDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AFormalParamLocalParamCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.expressions.ABoolLiteralExpCG;
import org.overture.codegen.cgast.expressions.AEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.ANotEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.ANotUnaryExpCG;
import org.overture.codegen.cgast.expressions.SBinaryExpCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.SMapTypeCG;
import org.overture.codegen.cgast.types.SSeqTypeCG;
import org.overture.codegen.cgast.types.SSetTypeCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.merging.MergeVisitor;
import org.overture.codegen.merging.TemplateCallable;
import org.overture.codegen.merging.TemplateManager;
import org.overture.codegen.merging.TemplateStructure;
import org.overture.codegen.trans.TempVarPrefixes;
import org.overture.codegen.utils.GeneralUtils;

public class CFormat
{

	private MergeVisitor mergeVisitor;
	private IRInfo info;
	private int number = 0;
	
	private long nextClassId = 0;
	private Map<String,Long> classIds = new HashMap<String,Long>();
	
	public String getClassId(String name)
	{
		if(classIds.containsKey(name))
			return classIds.get(name).toString();;
		
		Long id = nextClassId;
		classIds.put(name, id);
		nextClassId++;
		return id.toString();
		
	}

	public static final String UTILS_FILE = "Utils";
	
	public String getNumber()
	{
		number = number + 1;

		return Integer.toString(number - 1);
	}

	public CFormat(TempVarPrefixes varPrefixes, IRInfo info)
	{
		TemplateManager templateManager = new TemplateManager(new TemplateStructure("MyTemplates"));
		TemplateCallable[] templateCallables = new TemplateCallable[] {
				new TemplateCallable("CFormat", this) };
		this.mergeVisitor = new MergeVisitor(templateManager, templateCallables);
		this.info = info;
	}

	public String format(INode node) throws AnalysisException
	{
		StringWriter writer = new StringWriter();
		node.apply(mergeVisitor, writer);

		return writer.toString();
	}

	public boolean isSeqType(SExpCG exp)
	{
		return info.getAssistantManager().getTypeAssistant().isSeqType(exp);
	}

	public Boolean isClassType(AFormalParamLocalParamCG fp)
	{
		return fp.getTag() == "class";
	}

	public String getEnclosingClass(AFormalParamLocalParamCG fp)
	{
		return fp.getAncestor(AClassDeclCG.class).getName().toString()
				+ "CLASS";
	}

	public String format(SExpCG exp, boolean leftChild) throws AnalysisException
	{
		String formattedExp = format(exp);

		CPrecedence precedence = new CPrecedence();

		INode parent = exp.parent();

		if (!(parent instanceof SExpCG))
		{
			return formattedExp;
		}

		boolean isolate = precedence.mustIsolate((SExpCG) parent, exp, leftChild);

		return isolate ? "(" + formattedExp + ")" : formattedExp;
	}

	public MergeVisitor GetMergeVisitor()
	{
		return mergeVisitor;
	}

	public String formatOperationBody(SStmCG body) throws AnalysisException
	{
		String NEWLINE = "\n";
		if (body == null)
		{
			return ";";
		}

		StringWriter generatedBody = new StringWriter();

		generatedBody.append("{" + NEWLINE + NEWLINE);
		generatedBody.append(handleOpBody(body));
		generatedBody.append(NEWLINE + "}");

		return generatedBody.toString();
	}

	private String handleOpBody(SStmCG body) throws AnalysisException
	{
		AMethodDeclCG method = body.getAncestor(AMethodDeclCG.class);

		if (method == null)
		{
			Logger.getLog().printErrorln("Could not find enclosing method when formatting operation body. Got: "
					+ body);
		} else if (method.getAsync() != null && method.getAsync())
		{
			return "new VDMThread(){ " + "\tpublic void run() {" + "\t "
					+ format(body) + "\t} " + "}.start();";
		}

		return format(body);
	}

	public String format(List<AFormalParamLocalParamCG> params)
			throws AnalysisException
	{
		StringWriter writer = new StringWriter();

		if (params.size() <= 0)
		{
			return "";
		}

		AFormalParamLocalParamCG firstParam = params.get(0);
		writer.append(format(firstParam));

		for (int i = 1; i < params.size(); i++)
		{
			AFormalParamLocalParamCG param = params.get(i);
			writer.append(", ");
			writer.append(format(param));
		}
		return writer.toString();
	}

	public boolean isClass(INode node)
	{
		return node != null && node instanceof AClassDeclCG;
	}

	public String getClassNameId(AIdentifierVarExpCG id)
	{

		org.overture.ast.node.INode vdm = id.getSourceNode().getVdmNode();

		if (vdm instanceof AVariableExp
				&& ((AVariableExp) vdm).getVardef() instanceof AInstanceVariableDefinition)
		{
			AVariableExp var = ((AVariableExp) vdm);
			String cl = var.getVardef().getClassDefinition().getName().getName();
			String fieldName = var.getName().getName().toString();
			String bcl = cl;
			return "GET_FIELD_PTR(" + cl + "," + bcl + "," + "this" + ","
					+ fieldName + ")";
		}

		return id.getName().toString();
	}

	public String formatArgs(List<? extends SExpCG> exps)
			throws AnalysisException
	{
		StringWriter writer = new StringWriter();

		if (exps.size() <= 0)
		{
			return "";
		}

		SExpCG firstExp = exps.get(0);
		writer.append(format(firstExp));

		for (int i = 1; i < exps.size(); i++)
		{
			SExpCG exp = exps.get(i);
			writer.append(", " + format(exp));
		}

		return writer.toString();
	}

	public List<AMethodDeclCG> getMethodsByAccess(List<AMethodDeclCG> methods,
			String access)
	{
		LinkedList<AMethodDeclCG> matches = new LinkedList<AMethodDeclCG>();

		for (AMethodDeclCG m : methods)
		{
			if (m.getAccess().equals(access))
			{
				matches.add(m);
			}
		}

		return matches;
	}

	public boolean isNull(INode node)
	{
		return node == null;
	}

	public String escapeChar(char c)
	{
		return GeneralUtils.isEscapeSequence(c)
				? StringEscapeUtils.escapeJavaScript(c + "") : c + "";
	}

	public List<AFieldDeclCG> getFieldsByAccess(List<AFieldDeclCG> fields,
			String access)
	{
		LinkedList<AFieldDeclCG> matches = new LinkedList<AFieldDeclCG>();

		for (AFieldDeclCG f : fields)
		{
			if (f.getAccess().equals(access))
			{
				matches.add(f);
			}
		}

		return matches;
	}

	public String formatEqualsBinaryExp(AEqualsBinaryExpCG node)
			throws AnalysisException
	{
		STypeCG leftNodeType = node.getLeft().getType();

		if (leftNodeType instanceof SSeqTypeCG || leftNodeType instanceof SSetTypeCG || leftNodeType instanceof SMapTypeCG)
		{
			return handleCollectionComparison(node);
		}
		else
		{
			return handleEquals(node);
		}
	}

	public String formatNotEqualsBinaryExp(ANotEqualsBinaryExpCG node)
			throws AnalysisException
	{
		ANotUnaryExpCG transformed = transNotEquals(node);
		return formatNotUnary(transformed.getExp());
	}
	
	public String formatNotUnary(SExpCG exp) throws AnalysisException
	{
		String formattedExp = format(exp, false);

		boolean doNotWrap = exp instanceof ABoolLiteralExpCG
				|| formattedExp.startsWith("(") && formattedExp.endsWith(")");

		return doNotWrap ? "!" + formattedExp : "!(" + formattedExp + ")";
	}
	
	private ANotUnaryExpCG transNotEquals(ANotEqualsBinaryExpCG notEqual)
	{
		ANotUnaryExpCG notUnary = new ANotUnaryExpCG();
		notUnary.setType(new ABoolBasicTypeCG());

		AEqualsBinaryExpCG equal = new AEqualsBinaryExpCG();
		equal.setType(new ABoolBasicTypeCG());
		equal.setLeft(notEqual.getLeft().clone());
		equal.setRight(notEqual.getRight().clone());

		notUnary.setExp(equal);

		// Replace the "notEqual" expression with the transformed expression
		INode parent = notEqual.parent();

		// It may be the case that the parent is null if we execute e.g. [1] <> [1] in isolation
		if (parent != null)
		{
			parent.replaceChild(notEqual, notUnary);
			notEqual.parent(null);
		}

		return notUnary;
	}

	private String handleEquals(AEqualsBinaryExpCG valueType)
			throws AnalysisException
	{
		return String.format("%s.equals(%s, %s)", UTILS_FILE, format(valueType.getLeft()), format(valueType.getRight()));
	}

	private String handleCollectionComparison(SBinaryExpCG node) throws AnalysisException
	{
		// In VDM the types of the equals are compatible when the AST passes the type check
		SExpCG leftNode = node.getLeft();
		SExpCG rightNode = node.getRight();

		final String EMPTY = ".isEmpty()";

		if (isEmptyCollection(leftNode.getType()))
		{
			return format(node.getRight()) + EMPTY;
		} else if (isEmptyCollection(rightNode.getType()))
		{
			return format(node.getLeft()) + EMPTY;
		}

		return UTILS_FILE + ".equals(" + format(node.getLeft()) + ", "
				+ format(node.getRight()) + ")";
	}
	
	private boolean isEmptyCollection(STypeCG type)
	{
		if (type instanceof SSeqTypeCG)
		{
			SSeqTypeCG seq = (SSeqTypeCG) type;

			return seq.getEmpty();
		} else if (type instanceof SSetTypeCG)
		{
			SSetTypeCG set = (SSetTypeCG) type;

			return set.getEmpty();
		} else if (type instanceof SMapTypeCG)
		{
			SMapTypeCG map = (SMapTypeCG) type;

			return map.getEmpty();
		}

		return false;
	}
	
	public String getTVPtype()
	{
		return "TVP";
	}

	public String getIncludeClassName(AClassDeclCG cl)
	{
		return "\"" + cl.getName().toString() + ".h\"";
	}

	public String getClassName(AClassDeclCG cl)
	{
		return cl.getName().toString();
	}

	public String getClassHeaderName(AClassHeaderDeclCG ch)
	{
		return ch.getName().toString();
	}
}