package org.overture.codegen.vdm2jml;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.overture.ast.util.ClonableString;
import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.IRConstants;
import org.overture.codegen.ir.IRStatus;
import org.overture.codegen.ir.PIR;
import org.overture.codegen.ir.SPatternIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.VdmNodeInfo;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.declarations.AFormalParamLocalParamIR;
import org.overture.codegen.ir.declarations.AInterfaceDeclIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.ANamedTypeDeclIR;
import org.overture.codegen.ir.declarations.ARecordDeclIR;
import org.overture.codegen.ir.declarations.ATypeDeclIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.ir.statements.AReturnStmIR;
import org.overture.codegen.ir.types.ABoolBasicTypeIR;
import org.overture.codegen.ir.types.AExternalTypeIR;
import org.overture.codegen.ir.types.AMethodTypeIR;
import org.overture.codegen.vdm2java.JavaCodeGen;
import org.overture.codegen.vdm2java.JavaCodeGenUtil;
import org.overture.codegen.vdm2java.JavaFormat;
import org.overture.codegen.vdm2jml.data.RecClassInfo;
import org.overture.codegen.vdm2jml.util.NameGen;

public class JmlGenUtil
{
	private JmlGenerator jmlGen;

	private Logger log = Logger.getLogger(this.getClass().getName());

	public JmlGenUtil(JmlGenerator jmlGen)
	{
		this.jmlGen = jmlGen;
	}

	public AIdentifierVarExpIR getInvParamVar(AMethodDeclIR invMethod)
	{
		AFormalParamLocalParamIR formalParam = getInvFormalParam(invMethod);

		if (formalParam == null)
		{
			return null;
		}

		String paramName = getName(formalParam.getPattern());

		if (paramName == null)
		{
			return null;
		}

		STypeIR paramType = formalParam.getType().clone();

		return jmlGen.getJavaGen().getInfo().getExpAssistant().consIdVar(paramName, paramType);
	}

	public String getName(SPatternIR id)
	{
		if (!(id instanceof AIdentifierPatternIR))
		{
			log.error("Expected identifier pattern "
					+ "to be an identifier pattern at this point. Got: " + id);
			return null;
		}

		return ((AIdentifierPatternIR) id).getName();
	}

	public AFormalParamLocalParamIR getInvFormalParam(AMethodDeclIR invMethod)
	{
		if (invMethod.getFormalParams().size() == 1)
		{
			return invMethod.getFormalParams().get(0);
		} else
		{
			log.error("Expected only a single formal parameter "
					+ "for named invariant type method " + invMethod.getName()
					+ " but got " + invMethod.getFormalParams().size());

			if (!invMethod.getFormalParams().isEmpty())
			{
				return invMethod.getFormalParams().get(0);
			} else
			{
				return null;
			}
		}
	}

	public AMethodDeclIR getInvMethod(ATypeDeclIR typeDecl)
	{
		if (typeDecl.getInv() instanceof AMethodDeclIR)
		{
			return (AMethodDeclIR) typeDecl.getInv();
		} else if (typeDecl.getInv() != null)
		{
			log.error("Expected named type invariant function "
					+ "to be a method declaration at this point. Got: "
					+ typeDecl.getDecl());
		}

		return null;
	}

	public List<AMethodDeclIR> getNamedTypeInvMethods(ADefaultClassDeclIR clazz)
	{
		List<AMethodDeclIR> invDecls = new LinkedList<AMethodDeclIR>();

		for (ATypeDeclIR typeDecl : clazz.getTypeDecls())
		{
			if (typeDecl.getDecl() instanceof ANamedTypeDeclIR)
			{
				AMethodDeclIR m = getInvMethod(typeDecl);

				if (m != null)
				{
					invDecls.add(m);
				}
			}
		}

		return invDecls;
	}

	public List<String> getRecFieldNames(ARecordDeclIR r)
	{
		List<String> args = new LinkedList<String>();

		for (AFieldDeclIR f : r.getFields())
		{
			args.add(f.getName());
		}
		return args;
	}

	public List<ARecordDeclIR> getRecords(List<IRStatus<PIR>> ast)
	{
		List<ARecordDeclIR> records = new LinkedList<ARecordDeclIR>();

		for (IRStatus<ADefaultClassDeclIR> classStatus : IRStatus.extract(ast, ADefaultClassDeclIR.class))
		{
			for (ATypeDeclIR typeDecl : classStatus.getIrNode().getTypeDecls())
			{
				if (typeDecl.getDecl() instanceof ARecordDeclIR)
				{
					records.add((ARecordDeclIR) typeDecl.getDecl());
				}
			}
		}

		return records;
	}

	public String getMethodCondArgName(SPatternIR pattern)
	{
		// By now all patterns should be identifier patterns
		if (pattern instanceof AIdentifierPatternIR)
		{
			String paramName = ((AIdentifierPatternIR) pattern).getName();

			if (jmlGen.getJavaGen().getInfo().getExpAssistant().isOld(paramName))
			{
				paramName = toJmlOldExp(paramName);
			} else if (jmlGen.getJavaGen().getInfo().getExpAssistant().isResult(paramName))
			{
				// The type checker prohibits use of 'RESULT' as name of a user specified identifier
				paramName = JmlGenerator.JML_RESULT;
			}

			return paramName;

		} else
		{
			log.error("Expected formal parameter pattern to be an indentifier pattern. Got: "
					+ pattern);

			return "UNKNOWN";
		}
	}

	public String toJmlOldExp(String paramName)
	{
		// Convert old name to current name (e.g. _St to St)
		String currentArg = jmlGen.getJavaGen().getInfo().getExpAssistant().oldNameToCurrentName(paramName);

		// Note that invoking the copy method on the state should be okay
		// because the state should never be a null pointer
		currentArg += ".copy()";

		// Convert current name to JML old expression (e.g. \old(St)
		return String.format("%s(%s)", JmlGenerator.JML_OLD_PREFIX, currentArg);
	}

	/**
	 * This change to the IR is really only to circumvent a bug in OpenJML with loading of inner classes. The only thing
	 * that the Java code generator uses inner classes for is records. If this problem gets fixed the workaround
	 * introduced by this method should be removed.
	 * 
	 * @param ast
	 *            The IR passed by the Java code generator after the transformation process
	 * @param recInfo
	 *            Since the new record classes are deep copies we need to update the record info too
	 */
	public List<IRStatus<PIR>> makeRecsOuterClasses(List<IRStatus<PIR>> ast,
			RecClassInfo recInfo)
	{
		List<IRStatus<PIR>> extraClasses = new LinkedList<IRStatus<PIR>>();

		for (IRStatus<ADefaultClassDeclIR> status : IRStatus.extract(ast, ADefaultClassDeclIR.class))
		{
			ADefaultClassDeclIR clazz = status.getIrNode();

			List<ARecordDeclIR> recDecls = new LinkedList<ARecordDeclIR>();

			for (ATypeDeclIR d : clazz.getTypeDecls())
			{
				if (d.getDecl() instanceof ARecordDeclIR)
				{
					recDecls.add((ARecordDeclIR) d.getDecl());
				}
			}

			// Note that we do not remove the type declarations (or records) from the class.

			// For each of the records we will make a top-level class
			for (ARecordDeclIR recDecl : recDecls)
			{
				ADefaultClassDeclIR recClass = new ADefaultClassDeclIR();

				recInfo.registerRecClass(recClass);

				recClass.setMetaData(recDecl.getMetaData());
				recClass.setAbstract(false);
				recClass.setAccess(IRConstants.PUBLIC);
				recClass.setSourceNode(recDecl.getSourceNode());
				recClass.setStatic(false);
				recClass.setName(recDecl.getName());

				if (recDecl.getInvariant() != null)
				{
					recClass.setInvariant(recDecl.getInvariant().clone());
				}

				AInterfaceDeclIR recInterface = new AInterfaceDeclIR();
				recInterface.setPackage("org.overture.codegen.runtime");

				final String RECORD_NAME = "Record";
				recInterface.setName(RECORD_NAME);
				AExternalTypeIR recInterfaceType = new AExternalTypeIR();
				recInterfaceType.setName(RECORD_NAME);
				AMethodTypeIR copyMethodType = new AMethodTypeIR();
				copyMethodType.setResult(recInterfaceType);

				AMethodDeclIR copyMethod = jmlGen.getJavaGen().getJavaFormat().getRecCreator().consCopySignature(copyMethodType);
				copyMethod.setAbstract(true);

				recInterface.getMethodSignatures().add(copyMethod);

				recClass.getInterfaces().add(recInterface);

				// Copy the record methods to the class
				List<AMethodDeclIR> methods = new LinkedList<AMethodDeclIR>();
				for (AMethodDeclIR m : recDecl.getMethods())
				{
					AMethodDeclIR newMethod = m.clone();
					methods.add(newMethod);
					recInfo.updateAccessor(m, newMethod);
				}
				recClass.setMethods(methods);

				// Copy the record fields to the class
				List<AFieldDeclIR> fields = new LinkedList<AFieldDeclIR>();
				for (AFieldDeclIR f : recDecl.getFields())
				{
					AFieldDeclIR newField = f.clone();
					fields.add(newField);
					recInfo.register(newField);
				}
				recClass.setFields(fields);

				// Put the record classes of module M in package <userpackage>.<modulename>types
				// Examples: my.pack.Mtypes
				if (JavaCodeGenUtil.isValidJavaPackage(jmlGen.getJavaSettings().getJavaRootPackage()))
				{
					String recPackage = consRecPackage(clazz.getName(), jmlGen.getJavaSettings().getJavaRootPackage());
					recClass.setPackage(recPackage);
				} else
				{
					recClass.setPackage(clazz.getName()
							+ JavaFormat.TYPE_DECL_PACKAGE_SUFFIX);
				}

				List<ClonableString> imports = new LinkedList<>();
				imports.add(new ClonableString(JavaCodeGen.JAVA_UTIL));
				imports.add(new ClonableString(JavaCodeGen.RUNTIME_IMPORT));
				recClass.setDependencies(imports);

				extraClasses.add(new IRStatus<PIR>(recClass.getSourceNode().getVdmNode(), recClass.getName(), recClass, new HashSet<VdmNodeInfo>()));
			}
		}

		return extraClasses;
	}

	public static String consRecPackage(String defClass, String javaRootPackage)
	{
		String recPackage = "";

		if (JavaCodeGenUtil.isValidJavaPackage(javaRootPackage))
		{
			recPackage += javaRootPackage + ".";
		}

		recPackage += defClass + JavaFormat.TYPE_DECL_PACKAGE_SUFFIX;

		return recPackage;
	}

	public AMethodDeclIR genInvMethod(ADefaultClassDeclIR clazz,
			ANamedTypeDeclIR namedTypeDecl)
	{
		AReturnStmIR body = new AReturnStmIR();
		body.setExp(jmlGen.getJavaGen().getInfo().getExpAssistant().consBoolLiteral(true));

		STypeIR paramType = namedTypeDecl.getType();

		AMethodTypeIR invMethodType = new AMethodTypeIR();
		invMethodType.setResult(new ABoolBasicTypeIR());
		invMethodType.getParams().add(paramType.clone());

		String formalParamName = new NameGen(clazz).getName(JmlGenerator.GEN_INV_METHOD_PARAM_NAME);

		AFormalParamLocalParamIR formalParam = new AFormalParamLocalParamIR();
		formalParam.setType(paramType.clone());
		formalParam.setPattern(jmlGen.getJavaGen().getInfo().getPatternAssistant().consIdPattern(formalParamName));

		AMethodDeclIR method = new AMethodDeclIR();
		method.setImplicit(false);
		method.setAbstract(false);
		method.setAccess(IRConstants.PUBLIC);
		method.setAsync(false);
		method.setBody(body);
		method.getFormalParams().add(formalParam);
		method.setIsConstructor(false);
		method.setMethodType(invMethodType);
		method.setName("inv_" + namedTypeDecl.getName());
		method.setStatic(true);

		return method;
	}

	public AIdentifierPatternIR consInvParamReplacementId(
			ADefaultClassDeclIR encClass, String originalParamName)
	{
		NameGen nameGen = new NameGen(encClass);
		nameGen.addName(originalParamName);

		String newParamName = nameGen.getName(JmlGenerator.INV_METHOD_REPLACEMENT_NAME_PREFIX
				+ originalParamName);

		return jmlGen.getJavaGen().getInfo().getPatternAssistant().consIdPattern(newParamName);
	}

	public ADefaultClassDeclIR getEnclosingClass(INode node)
	{
		ADefaultClassDeclIR enclosingClass = node.getAncestor(ADefaultClassDeclIR.class);

		if (enclosingClass != null)
		{
			return enclosingClass;
		} else
		{
			log.error("Could not find enclosing class of node: " + node);
			return null;
		}
	}

	public AMethodDeclIR getEnclosingMethod(INode node)
	{
		AMethodDeclIR enclosingMethod = node.getAncestor(AMethodDeclIR.class);

		if (enclosingMethod != null)
		{
			return enclosingMethod;
		} else
		{
			log.error("Could not find enclosing method of node: " + node);
			return null;
		}
	}

	/**
	 * There are problems with OpenJML when you invoke named type invariant methods across classes. Until these bugs are
	 * fixed the workaround is simply to make sure that all generated classes have a local copy of a named invariant
	 * method. TODO: Currently invariant method are named on the form "module_typename" although this does not truly
	 * garuantee uniqueness. For example if module A defines type B_C the invariant method name is A_B_C. However if
	 * module A_B defines type C then the invariant method will also be named A_B_C. So something needs to be done about
	 * this.
	 * 
	 * @param newAst
	 */
	public void distributeNamedTypeInvs(List<IRStatus<PIR>> newAst)
	{
		// Collect all named type invariants
		List<ATypeDeclIR> allNamedTypeInvTypeDecls = new LinkedList<ATypeDeclIR>();
		for (IRStatus<ADefaultClassDeclIR> status : IRStatus.extract(newAst, ADefaultClassDeclIR.class))
		{
			ADefaultClassDeclIR clazz = status.getIrNode();

			if (jmlGen.getJavaGen().getInfo().getDeclAssistant().isLibraryName(clazz.getName()))
			{
				continue;
			}

			for (ATypeDeclIR typeDecl : clazz.getTypeDecls())
			{
				if (typeDecl.getDecl() instanceof ANamedTypeDeclIR)
				{
					allNamedTypeInvTypeDecls.add(typeDecl);
				}
			}
		}

		for (IRStatus<ADefaultClassDeclIR> status : IRStatus.extract(newAst, ADefaultClassDeclIR.class))
		{
			ADefaultClassDeclIR clazz = status.getIrNode();

			if (jmlGen.getJavaGen().getInfo().getDeclAssistant().isLibraryName(clazz.getName()))
			{
				continue;
			}

			List<ATypeDeclIR> classTypeDecls = new LinkedList<ATypeDeclIR>(clazz.getTypeDecls());

			for (ATypeDeclIR namedTypeInv : allNamedTypeInvTypeDecls)
			{
				if (!classTypeDecls.contains(namedTypeInv))
				{
					classTypeDecls.add(namedTypeInv.clone());
				}
			}

			clazz.setTypeDecls(classTypeDecls);
		}
	}
}
