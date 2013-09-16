package org.overture.tools.astcreator.extend;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.overture.tools.astcreator.AstCreatorException;
import org.overture.tools.astcreator.definitions.BaseClassDefinition;
import org.overture.tools.astcreator.definitions.ExternalJavaClassDefinition;
import org.overture.tools.astcreator.definitions.Field;
import org.overture.tools.astcreator.definitions.IClassDefinition;
import org.overture.tools.astcreator.definitions.IClassDefinition.ClassType;
import org.overture.tools.astcreator.definitions.IInterfaceDefinition;
import org.overture.tools.astcreator.definitions.InterfaceDefinition;
import org.overture.tools.astcreator.definitions.PredefinedClassDefinition;
import org.overture.tools.astcreator.env.Environment;
import org.overture.tools.astcreator.java.definitions.JavaName;
import org.overture.tools.astcreator.methods.KindMethod;
import org.overture.tools.astcreator.methods.Method;
import org.overture.tools.astcreator.methods.analysis.depthfirst.AnalysisDepthFirstAdaptorCaseMethod;
import org.overture.tools.astcreator.methods.visitors.AnalysisAcceptMethod;
import org.overture.tools.astcreator.methods.visitors.AnalysisUtil;
import org.overture.tools.astcreator.methods.visitors.AnswerAcceptMethod;
import org.overture.tools.astcreator.methods.visitors.QuestionAcceptMethod;
import org.overture.tools.astcreator.methods.visitors.QuestionAnswerAcceptMethod;
import org.overture.tools.astcreator.methods.visitors.adaptor.analysis.AnalysisAdaptorCaseMethod;
import org.overture.tools.astcreator.methods.visitors.adaptor.analysis.AnalysisAdaptorDefaultMethod;
import org.overture.tools.astcreator.methods.visitors.adaptor.answer.AnswerAdaptorCaseMethod;
import org.overture.tools.astcreator.methods.visitors.adaptor.answer.AnswerAdaptorDefaultMethod;
import org.overture.tools.astcreator.methods.visitors.adaptor.question.QuestionAdaptorCaseMethod;
import org.overture.tools.astcreator.methods.visitors.adaptor.question.QuestionAdaptorDefaultMethod;
import org.overture.tools.astcreator.methods.visitors.adaptor.questionanswer.QuestionAnswerAdaptorCaseMethod;
import org.overture.tools.astcreator.methods.visitors.adaptor.questionanswer.QuestionAnswerAdaptorDefaultMethod;
import org.overture.tools.astcreator.utils.ClassFactory;

public class ExtensionGenerator2 {

	private static final String COMPASS_JAVA_DOC_STRING = "*\n* Extensions by the COMPASS Project\n* @author Rasmus Winther Lauritsen, Anders Kaels Malmos\n";
	private final Environment base;

	public ExtensionGenerator2(Environment base) {
		this.base = base;
	}

	private static boolean isTemplateClass(IInterfaceDefinition def,
			Environment ext) {
		if (ext.node == def)
			return true;
		if (ext.iNode == def)
			return true;
		if (ext.token == def)
			return true;
		if (ext.iToken == def)
			return true;
		if (ext.analysisException == def)
			return true;
		if (ext.externalNode == def)
			return true;
		if (ext.graphNodeList == def)
			return true;
		if (ext.graphNodeListList == def)
			return true;
		if (ext.nodeList == def)
			return true;
		if (ext.nodeListList == def)
			return true;
		if (ext.getTaggedDef(ext.TAG_IAnalysis) == def)
			return true;
		if (ext.getTaggedDef(ext.TAG_IAnswer) == def)
			return true;
		if (ext.getTaggedDef(ext.TAG_IQuestion) == def)
			return true;
		if (ext.getTaggedDef(ext.TAG_IQuestionAnswer) == def)
			return true;
		return false;
	}

	// 1) Include all class definition and interfaces from the base tree
	// - Create function: void addBaseAstClassAndInterfaces(result, baseEnv);
	private static void addBaseAstClassesAndInterfaces(Environment result,
			Environment baseEnv) {
		// Copy over all class definitions
		for (IClassDefinition cdef : baseEnv.getClasses())
			if (!isUtilityOrTemplateClass(cdef, baseEnv))
				result.getClasses().add(cdef);

		// Copy over all interfaces from base
		for (IInterfaceDefinition idef : baseEnv.getInterfaces())
			result.getInterfaces().add(idef);
	}

	// 2) Include all classes from the extension tree we will not generate
	// an extend-version of or take from the base tree. E.g. PExpBase
	// from the ext env should not be copied over as we are going to
	// replace that with PCmlExpBase in the example
	//
	// NB! The root tree nodes and utility classes (NodeList, GraphList etc...)
	// are the nodes to be taken from the base tree.
	//
	// - Create function: boolean willGenerateExtensionFor(iDef,baseEnv)
	// - Create function: boolean isUtilityOrTemplateClass(iDef, env)
	// - Create function: void includeClassesFromExtension(result, extEnv)
	private static boolean willGenerateExtensionFor(IInterfaceDefinition iDef,
			Environment base) {
		IInterfaceDefinition overtureEquivalent = base.lookUpType(iDef
				.getName().getName());
		boolean hasSameTag = overtureEquivalent == null ? false : iDef
				.getName().getTag()
				.equals(overtureEquivalent.getName().getTag());
		return hasSameTag && !isUtilityOrTemplateClass(iDef, base);
	}

	private static boolean isUtilityOrTemplateClass(IInterfaceDefinition def,
			Environment env) {
		return isTemplateClass(def, env)
				|| (def instanceof PredefinedClassDefinition);
	}

	private static void includeClassesFromExtension(Environment result,
			Environment ext, Environment base) {
		for (IClassDefinition cDef : ext.getClasses()) {
			if (!willGenerateExtensionFor(cDef, base)) {
				if (!isUtilityOrTemplateClass(cDef, ext))
					result.getClasses().add(cDef);
			}
		}
	}

	// 3) Include all interfaces from the extension that are not going to be
	// extended
	// - Create function: void includeInterfacesFromExtension(result, extEnv);
	private static void includeInterfacesFromExtension(Environment result,
			Environment extEnv, Environment base) {
		for (IInterfaceDefinition iDef : extEnv.getInterfaces())
			if (!willGenerateExtensionFor(iDef, base))
				result.getInterfaces().add(iDef);

	}

	private static JavaName makeExtensionJavaName(
			IInterfaceDefinition baseProduction, Environment ext,
			Environment base, String extensionTargetPackage) {
		String newNameStr = ext.getName()
				+ baseProduction.getName().getRawName();
		JavaName newName = new JavaName(ext.getDefaultPackage(), newNameStr);
		newName.setPrefix(baseProduction.getName().getPrefix());
		newName.setPostfix(baseProduction.getName().getPostfix());
		newName.setPackageName(extensionTargetPackage);
		newName.setTag(baseProduction.getName().getTag());
		return newName;
	}

	// private static String computeExtensionPackageName(Environment ext,
	// Environment base, IInterfaceDefinition baseProduction) {
	// String cmlPackage = ext.getAstPackage()
	// + baseProduction.getName().getPackageName()
	// .replace(base.getAstPackage(), "");
	// return cmlPackage;
	// }

	// 4) Generate interfaces and base classes for the interfaces to be extended
	// in the ext env
	// - Create function: Map<String, IInterfaceDefinition>
	// generateExtensionInterfaces(extEnv, baseEnv)
	// - Create function: Map<String, IClassDefinition>
	// generateExtensionClasses(extEnv, baseEnv)
	private static Map<String, IInterfaceDefinition> generateExtensionInterfaces(
			Environment result, Environment ext, Environment base) {
		Map<String, IInterfaceDefinition> replacementMap = new HashMap<String, IInterfaceDefinition>();
		for (IInterfaceDefinition iDef : ext.getInterfaces()) {
			if (willGenerateExtensionFor(iDef, base)) {
				// Lookup base production in the base environment, e.g. the one
				// to extend
				IInterfaceDefinition baseProduction = base.lookUpType(iDef
						.getName().getName());

				// Create the new Cml Production as a sub-interface of the base
				// production
				JavaName newName = makeExtensionJavaName(baseProduction, ext,
						base, iDef.getName().getPackageName());
				InterfaceDefinition extProduction = new InterfaceDefinition(
						newName, result.getAstPackage());
				extProduction.supers.add(baseProduction);

				// Update mapping, in the result environment the new extension
				// node is a sub-interface for the baseProduction node.
				result.addCommonTreeInterface(extProduction, baseProduction);

				// Add the newly generated interface to the interface list in
				// the result env.
				result.getInterfaces().add(extProduction);

				// Alternatives and base classes in ext current has Production
				// and SubProduction nodes from the ext environment that should
				// not exist as super Def and in their interface-lists. The same
				// is true for method and field definition inside each
				// IInterface- and IClassDefinition.
				//
				// The replacement map will help us later to fix this by mapping
				// the
				// wrongly generated names to the extended names that we wish to
				// use.
				//
				// Example:
				//
				// PExp is extended with an alternative AFatBraceExp
				//
				// We generate: PCmlExp, PCmlExpBase which shall become the
				// parent-hierarchy
				// for AFatBraceExp. However, the AFatBraceExp has PExp and
				// PExpBase from the
				// ext environment is its parent hierarchy currently. The
				// replacement map
				// will help us fix this later.
				replacementMap.put(baseProduction.getName().getName(),
						extProduction);
			}
		}
		return replacementMap;
	}

	private static Map<String, IClassDefinition> generateExtensionClasses(
			Environment result, Environment ext, Environment base,
			Map<String, IInterfaceDefinition> replacementMap) {
		Map<String, IClassDefinition> classReplacementMap = new HashMap<String, IClassDefinition>();
		final String extentionName = ext.getName();
		// Generate the base classes
		for (Entry<String, IInterfaceDefinition> e : replacementMap.entrySet()) {
			final IInterfaceDefinition baseProduction = base.lookUpType(e
					.getKey());
			
			// Lookup the base production base class e.g. PExpBase that will be
			// the super class
			// for out to be created PCmlExpBase. Examplified of course
			IClassDefinition baseProductionBase = null;
			String baseProductionBaseName1 = null;
			String baseProductionBaseName2 = null;

			IInterfaceDefinition c = baseProduction;
			while (c.getSuperDefs().size() > 0) {
				IInterfaceDefinition tmp = c.getSuperDefs().iterator().next();
				if (tmp instanceof PredefinedClassDefinition)
					break;
				c = tmp;
			}
			JavaName basePName = baseProduction.getName();
			baseProductionBaseName1 = basePName.getPrefix()
					+ basePName.getRawName() + basePName.getPostfix();
			baseProductionBaseName2 = baseProductionBaseName1.replace(c
					.getName().getRawName(), "Base");
			baseProductionBaseName1 += "Base";

			IInterfaceDefinition extProduction = e.getValue();
			baseProductionBase = base.lookUp(baseProductionBaseName1);
			if (baseProductionBase == null)
				baseProductionBase = base.lookUp(baseProductionBaseName2);

			if (baseProductionBase == null)
				System.out.println("Crap!");

			// In our running example let us Create the PCmlExpBase class
			String cmlPackage = e.getValue().getName().getPackageName();
			JavaName newName = makeExtensionJavaName(baseProductionBase, ext,
					base, cmlPackage);
			IClassDefinition extensionProductionBase = ClassFactory.create(
					cmlPackage, newName.getRawName(), baseProductionBase,
					base.classToType.get(baseProductionBase), result);
			extensionProductionBase.setIsExtTree(true);
			extensionProductionBase.getName().setTag(
					baseProductionBase.getName().getTag());
			extensionProductionBase.addInterface(extProduction);
			class LocalKindMethod extends KindMethod {

				public LocalKindMethod(IClassDefinition c,
						boolean isAbstractKind) {
					super(c, isAbstractKind);

				}

				@Override
				public String getJavaSourceCode(Environment env) {
					StringBuilder result = new StringBuilder();

					returnType = "String";
					name = "kind" + baseProduction.getName().getPrefix() + baseProduction.getName().getRawName();

					result.append("public " + returnType + " " + name + "()");
					//result.append("{ throw new RuntimeException(\"Using the kind method is deprecated.\"); }");
					String nodeNameString = "\"" + extentionName + baseProduction.getName().getRawName() + "\"";
					result.append("{ return " + name +"; }\n");
					result.append("public static final " + returnType + " " + name + " = " + nodeNameString +";\n");
					return result.toString();
				}

			}

			if (!"S".equals(newName.getPrefix())) {
				// Add kindMethod that throw RuntimeException
				Method kindMethod = new LocalKindMethod(baseProductionBase,
						false);
				extensionProductionBase.addMethod(kindMethod);
			}
			// Add mapping from the extensionProductionBase production to the
			// extProduction
			result.treeNodeInterfaces.put(extensionProductionBase,
					extProduction);

			// This is actually a bit ugly (baseProductionBase should come from
			// the extension environment)
			classReplacementMap.put(baseProductionBase.getName().getName(),
					extensionProductionBase);
		}
		return classReplacementMap;
	}

	/*
	 * UpdateFieldsWithUnresolvedTypes
	 * 
	 * -------------------------------
	 * 
	 * We allow the extension environment (ext) to be created with unresolved
	 * types pointing into the base environment. Now it is time to resolve
	 * these.
	 * 
	 * For every definition in the result environment so far we check all fields
	 * if they have an unresolvedtype. If so we look up in the base environment
	 * what type it should have.
	 */
	private void updateFieldsWithUnresolvedTypes(Environment result,
			Environment ext, Environment base) throws AstCreatorException {
		// Path node, token, inode and itoken
		for (IInterfaceDefinition def : result.getAllDefinitions()) {

			if (def instanceof InterfaceDefinition)
				((InterfaceDefinition) def)
						.setExtJavaDoc(COMPASS_JAVA_DOC_STRING);

			if (def instanceof BaseClassDefinition) {
				BaseClassDefinition bcdef = (BaseClassDefinition) def;
				for (Field f : bcdef.getFields()) {
					String rawTypeToResolved = f.getUnresolvedType();
					IInterfaceDefinition type = null;
					if (f.type != null)
						type = f.type;
					else {
						type = result.lookupTagPath(rawTypeToResolved, false);
						if (type == null)
						    {
							System.out.println(""+rawTypeToResolved+" cannot be found"); 
							System.exit(-1);
						    }
						if (result.treeNodeInterfaces.containsKey(type))
							type = result.treeNodeInterfaces.get(type);
					}
					if (result.treeNodeInterfaces.containsKey(type))
						type = result.treeNodeInterfaces.get(type);

					if (type != null) {
						f.type = type;
						if (type instanceof ExternalJavaClassDefinition) {
							ExternalJavaClassDefinition ejcd = (ExternalJavaClassDefinition) type;
							if (ejcd.getFields() != null
									&& ejcd.getFields().size() > 0
									&& ejcd.getFields().get(0).isTokenField)
								f.isTokenField = true;
						}
					} else
						throw new AstCreatorException("Field '" + f.toString()
								+ "' has unresolved type: \""
								+ f.getUnresolvedType() + "\" in alternative "
								+ def.getName().getName() + ".", null, true);
				}
			}

		}
	}

	/*
	 * Tree node to interface mappings from the base environment are copied
	 * blindly, for the extension we copy over the ones that are only in the
	 * extension and make sure that any extension-interface that are created are
	 * used instead of the base interface.
	 */
	private void updateTreeNodeToInterface(Environment result, Environment ext,
			Environment base, Map<String, IInterfaceDefinition> iReplacementMap) {

		// Copy tree nodes to interfaces mapping
		Set<Entry<IInterfaceDefinition, IInterfaceDefinition>> tn2i = base.treeNodeInterfaces
				.entrySet();
		for (Entry<IInterfaceDefinition, IInterfaceDefinition> e : tn2i) {
			result.treeNodeInterfaces.put(e.getKey(), e.getValue());
		}

		tn2i = ext.treeNodeInterfaces.entrySet();
		for (Entry<IInterfaceDefinition, IInterfaceDefinition> e : tn2i) {
			// is it not a templated type
			if (!isTemplateClass(e.getKey(), ext)) {
				// do we have a replacement e.g. PExp replace for PExtExp
				if (iReplacementMap.containsKey(e.getValue().getName()
						.getName()))
					// yes! okay lookup the replacement and use that one instead
					result.treeNodeInterfaces.put(
							e.getKey(),
							iReplacementMap.get(e.getValue().getName()
									.getName()));
				else
					// no! fine we take whatever was created for the extension
					// environment
					result.treeNodeInterfaces.put(e.getKey(), e.getValue());
			}
		}

	}

	// Do super replacements, typically for ext nodes pointing to e.g.
	// PExpBase as super should be replaced with
	// PExtExpBase if an extension were made for the Exp production.
	//
	// Also any top-level nodes taken from the ext-tree should have their
	// super def to ext.node updated to result.node
	//
	//
	private void updateSuperDefinitions(Environment result, Environment ext,
			Map<String, IClassDefinition> cReplacementMap,
			Map<String, IInterfaceDefinition> iReplacementMap) {
		for (IClassDefinition cdef : ext.getClasses()) {
			IClassDefinition superDef = cdef.getSuperDef();
			if (superDef != null) {

				// Update super to be newly generated production if necessary
				IClassDefinition replacementSuperDef = cReplacementMap
						.get(superDef.getName().getName());
				if (replacementSuperDef != null)
					cdef.setSuper(replacementSuperDef);

				// Update super to the result-ast if necessary
				if (superDef == ext.node)
					cdef.setSuper(result.node);

				// RWL: Hmm, node is not an interface and should not be present
				// in supers, right?
				if (cdef.getSuperDefs().contains(base.node)
						|| cdef.getSuperDefs().contains(ext.node)) {
					cdef.getSuperDefs().remove(base.node);
					cdef.getSuperDefs().remove(ext.node);
					cdef.getSuperDefs().add(result.node);
				}

				if (cdef.getSuperDefs().contains(base.iNode)
						|| cdef.getSuperDefs().contains(ext.iNode)) {
					cdef.getSuperDefs().remove(base.iNode);
					cdef.getSuperDefs().remove(ext.iNode);
					cdef.getSuperDefs().add(result.iNode);
				}

				// Replace supers
				List<IInterfaceDefinition> tbr = new LinkedList<IInterfaceDefinition>();
				List<IInterfaceDefinition> tba = new LinkedList<IInterfaceDefinition>();
				for (IInterfaceDefinition idef : cdef.getSuperDefs())
					if (cReplacementMap.containsKey(idef.getName().getName())) {
						IClassDefinition newDef = cReplacementMap.get(idef
								.getName().getName());
						tbr.add(idef);
						tba.add(newDef);
					}
				cdef.getSuperDefs().removeAll(tbr);
				cdef.getSuperDefs().addAll(tba);

			}

		}

		for (IInterfaceDefinition idef : ext.getInterfaces()) {
			if (idef.getSuperDefs().contains(base.iNode)
					|| idef.getSuperDefs().contains(ext.iNode)) {
				idef.getSuperDefs().remove(base.iNode);
				idef.getSuperDefs().remove(ext.iNode);
				idef.getSuperDefs().add(result.iNode);
			}

			List<IInterfaceDefinition> tbr = new LinkedList<IInterfaceDefinition>();
			List<IInterfaceDefinition> tba = new LinkedList<IInterfaceDefinition>();
			for (IInterfaceDefinition sidef : idef.getSuperDefs()) {
				if (iReplacementMap.containsKey(sidef.getName().getName())) {
					IInterfaceDefinition replacement = iReplacementMap
							.get(sidef.getName().getName());
					tbr.add(sidef);
					tba.add(replacement);
				}
			}
			idef.getSuperDefs().removeAll(tbr);
			idef.getSuperDefs().addAll(tba);
		}

	}

	private void updateClassToType(Environment result, Environment ext,
			Environment base) {

		// Copy classToType mapping
		Set<Entry<IClassDefinition, ClassType>> c2t = base.classToType
				.entrySet();
		for (Entry<IClassDefinition, ClassType> e : c2t)
			result.classToType.put(e.getKey(), e.getValue());

		// Copy over the class to type entries that are not already handled
		c2t = ext.classToType.entrySet();
		for (Entry<IClassDefinition, ClassType> e : c2t)
			// if (!isTemplateClass(e.getKey(), ext))
			if (!willGenerateExtensionFor(e.getKey(), base))
				result.classToType.put(e.getKey(), e.getValue());

	}

	/**
	 * Given an extension environment use the base environment in this instance
	 * to resolve any unresolved types.
	 * 
	 * The extension (ext parameter) will be destructively updated, the
	 * environment will no longer function after this invocation. Also, the
	 * result environment returned will be entangled with the base environment
	 * in this instance.
	 * 
	 * @param ext
	 *            - an extension environment
	 * 
	 * @return A fresh environment entangled with ext and base. (Things are NOT
	 *         cloned)
	 * 
	 * @throws AstCreatorException
	 *             - An AstCreatorException is thrown if an unresolved symbol in
	 *             the extension environment cannot be resolved in the base
	 *             environment of this instance.
	 */
	public Environment extend(Environment ext) throws AstCreatorException {
		Environment result = Environment.getFromBase(base,
				ext.getAnalysisPackage(), ext.getDefaultPackage());

		result.setTemplateAnalysisPackage(base.getAnalysisPackage());
		result.setTemplateDefaultPackage(base.getDefaultPackage());

		System.out.println("***********************************************");
		System.out.println("AST Creator Extensions");
		System.out.println("***********************************************");

		// 1 every class and interface from the base environment is added to
		// result
		addBaseAstClassesAndInterfaces(result, base);

		// 2 include classes from the extension that we will need
		includeClassesFromExtension(result, ext, base);

		// 3 include interfaces from the extension that we will need
		includeInterfacesFromExtension(result, ext, base);

		// 4a generate new extension productions that is P and S interface
		Map<String, IInterfaceDefinition> iReplacementMap = generateExtensionInterfaces(
				result, ext, base);

		// 4b generate new base classes for the P and S interface generated in
		// 4a
		Map<String, IClassDefinition> cReplacementMap = generateExtensionClasses(
				result, ext, base, iReplacementMap);

		// 5 Update classToType
		updateClassToType(result, ext, base);

		// 6 Update super definitions
		updateSuperDefinitions(result, ext, cReplacementMap, iReplacementMap);

		// 7 Update treeNode to interface mapping (that is the common implements
		// relationship)
		updateTreeNodeToInterface(result, ext, base, iReplacementMap);

		// 8 Fields on nodes from the ext environment may have unresolved types
		updateFieldsWithUnresolvedTypes(result, ext, base);

		return result;
	}

	/**
	 * Create Analysis interfaces and adaptors for the extensions alone.
	 * 
	 * It is a precondition that the Generator.generator method has been run on
	 * the given result environment. The extEnv remains as it were after
	 * invoking the extend method.
	 * 
	 * @param extEnv
	 *            - the extension environment
	 * @param result
	 *            - an extended environment for which analysis classes should be
	 *            generated.
	 * 
	 *            Notice: The analysis created will have cases for the classes
	 *            that exists in both result and extEnv such that the case maps
	 *            to either an Alternative or a Token in the extended
	 *            environment.
	 */
	public void runPostGeneration(Environment extEnv, Environment result) {

		createIAnalysisInterface(result, extEnv);

		createIAnswerInterface(result, extEnv);

		createIQuestionInterface(result, extEnv);

		createIQuestionAnswerInterface(result, extEnv);

		createDepthFirstAdaptor(result, extEnv);
	}

	private void createIQuestionAnswerInterface(final Environment result,
			Environment extEnv) {

		List<String> genericArguments = new LinkedList<String>();
		genericArguments.add("Q");
		genericArguments.add("A");
		String name = "QuestionAnswer";
		String tag = result.TAG_IQuestionAnswer;
		MethodFactory extMf = new MethodFactory() {

			@Override
			public Method createCaseMethod(IClassDefinition cdef) {
				QuestionAnswerAdaptorCaseMethod qaacm = new QuestionAnswerAdaptorCaseMethod();
				qaacm.setClassDefinition(cdef);
				return qaacm;
			}

			@Override
			public void updateApplyMethod(IClassDefinition cdef,
					String newAnalysisName) {

				QuestionAnswerAcceptMethod qaam = findMethodType(
						QuestionAnswerAcceptMethod.class, cdef);
				if (qaam != null)
					qaam.setPrivilegedBody("\t\treturn (A)(("
							+ newAnalysisName
							+ "<Q,A>)caller).case"
							+ AnalysisUtil.getCaseClass(result, cdef).getName()
									.getName() + "(this, question);");

			}

			@Override
			public Method createDefaultCaseMethod(IClassDefinition cdef) {
				QuestionAnswerAdaptorDefaultMethod qaadm = new QuestionAnswerAdaptorDefaultMethod();
				qaadm.setClassDefinition(cdef);
				return qaadm;
			}

		};
		createAnalysisInterface(genericArguments, name, tag, extMf, extEnv,
				result, base);
	}

	/*
	 * Create the Question interface e.g.
	 * eu.compassresearch.ast.analysis.intf.IQuestion for the extension.
	 */
	private void createIQuestionInterface(final Environment result,
			Environment extEnv) {

		List<String> genericArguments = new LinkedList<String>();
		genericArguments.add("Q");
		String name = "Question";
		String tag = result.TAG_IQuestion;
		MethodFactory extMf = new MethodFactory() {

			@Override
			public void updateApplyMethod(IClassDefinition cdef,
					String newAnalysisName) {
				QuestionAcceptMethod qam = findMethodType(
						QuestionAcceptMethod.class, cdef);
				if (qam != null)
					qam.setPrivilegedBody("\t\t(("
							+ newAnalysisName
							+ "<Q>)caller).case"
							+ AnalysisUtil.getCaseClass(result, cdef).getName()
									.getName() + "(this, question);");
			}

			@Override
			public Method createCaseMethod(IClassDefinition cdef) {
				QuestionAdaptorCaseMethod caseM = new QuestionAdaptorCaseMethod();
				caseM.setClassDefinition(cdef);
				return caseM;
			}

			@Override
			public Method createDefaultCaseMethod(IClassDefinition cdef) {
				QuestionAdaptorDefaultMethod qadm = new QuestionAdaptorDefaultMethod();
				qadm.setClassDefinition(cdef);
				return qadm;
			}
		};
		createAnalysisInterface(genericArguments, name, tag, extMf, extEnv,
				result, base);
	}

	/*
	 * Create the IAnswer interface for the extension.
	 */
	private void createIAnswerInterface(final Environment result,
			Environment extEnv) {

		List<String> genericArguments = new LinkedList<String>();
		genericArguments.add("A");
		String name = "Answer";
		String tag = result.TAG_IAnswer;
		MethodFactory extMf = new MethodFactory() {

			@Override
			public void updateApplyMethod(IClassDefinition cdef,
					String newAnalysisName) {
				AnswerAcceptMethod aam = findMethodType(
						AnswerAcceptMethod.class, cdef);
				if (aam != null)
					aam.setPrivilegedBody("\t\treturn (A)(("
							+ newAnalysisName
							+ "<A>)caller).case"
							+ AnalysisUtil.getCaseClass(result, cdef).getName()
									.getName() + "(this);");
			}

			@Override
			public Method createCaseMethod(IClassDefinition cdef) {
				AnswerAdaptorCaseMethod caseM = new AnswerAdaptorCaseMethod();
				caseM.setClassDefinition(cdef);
				return caseM;
			}

			@Override
			public Method createDefaultCaseMethod(IClassDefinition cdef) {
				AnswerAdaptorDefaultMethod aadm = new AnswerAdaptorDefaultMethod();
				aadm.setClassDefinition(cdef);
				return aadm;
			}
		};
		createAnalysisInterface(genericArguments, name, tag, extMf, extEnv,
				result, base);
	}

	/*
	 * Helper class adding an extra level of indirection for Method creation.
	 */
	private static abstract class MethodFactory {
		public abstract Method createCaseMethod(IClassDefinition cdef);

		public abstract void updateApplyMethod(IClassDefinition cdef,
				String newAnalysisName);

		public abstract Method createDefaultCaseMethod(IClassDefinition cdef);
	}

	/*
	 * Find the first occurrence in the list of methods for cdef that is of
	 * class c.
	 * 
	 * Returns null if no method is found
	 * 
	 * Throws NullPointerException it either argument is null.
	 */
	private static <T extends Method> T findMethodType(Class<T> c,
			IClassDefinition cdef) {
		for (Method m : cdef.getMethods())
			if (c.isInstance(m))
				return c.cast(m);
		return null;
	}

	/*
	 * Create the IExtAnalysis interface.
	 */
	private void createIAnalysisInterface(final Environment result,
			final Environment ext) {

		MethodFactory extMf = new MethodFactory() {

			@Override
			public Method createCaseMethod(IClassDefinition cdef) {
				AnalysisAdaptorCaseMethod aacm = new AnalysisAdaptorCaseMethod();
				aacm.setClassDefinition(cdef);
				return aacm;

			}

			@Override
			public void updateApplyMethod(final IClassDefinition cdef,
					final String newAnalysisName) {
				AnalysisAcceptMethod aam = findMethodType(
						AnalysisAcceptMethod.class, cdef);
				if (aam != null)
					aam.setPrivilegedBody("\t\t ( ("
							+ newAnalysisName
							+ ") analysis).case"
							+ AnalysisUtil.getCaseClass(result, cdef).getName()
									.getName() + "(this);");
			}

			@Override
			public Method createDefaultCaseMethod(IClassDefinition cdef) {
				AnalysisAdaptorDefaultMethod aadm = new AnalysisAdaptorDefaultMethod();
				aadm.setClassDefinition(cdef);
				return aadm;
			}

		};
		createAnalysisInterface(Arrays.asList(new String[0]), "Analysis",
				result.TAG_IAnalysis, extMf, ext, result, base);
	}

	/***
	 * Get the list of classes from classList that are in env. If a class has
	 * been replaced due to extending base classes the replaced class will be
	 * included.
	 * 
	 * @param classList
	 * @param extensionName
	 *            Name of the extension, which helps to identify replaced
	 *            classes
	 * @param env
	 *            Environment to check for the existence
	 * @return
	 */
	private static List<IClassDefinition> getExtClasses(
			List<IClassDefinition> classList, String extensionName,
			Environment env) {
		List<IClassDefinition> classes = new LinkedList<IClassDefinition>();

		for (IClassDefinition c : classList) {
			// Check for the existing of the classdef directly.
			if (env.isTreeNode(c)) {
				classes.add(c);
			}
			// else if it does not exist in env, check if the class has been
			// replaced and add this instead.
			else {

				String newName = c.getName().getPrefix() + extensionName
						+ c.getName().getRawName() + c.getName().getPostfix();

				IClassDefinition newC = env.lookUp(newName);

				if (null != newC)
					classes.add(newC);
			}
		}
		return classes;
	}

	/***
	 * Creates the DepthFistAnalysisAdaptor for the extension
	 * 
	 * @param result
	 *            The environment of the result of merging the base with the
	 *            extension environment
	 * @param extEnv
	 *            The extension environment only
	 */
	private static void createDepthFirstAdaptor(Environment result,
			Environment extEnv) {

		// Create a extended depth first analysis adapter and add it to extEnv
		JavaName jname = new JavaName(result.getAstPackage() + ".analysis",
				"DepthFirstAnalysis" + extEnv.getName() + "Adaptor");
		IClassDefinition extAdaptor = ClassFactory.createCustom(jname, result);

		// Find the base DepthFirstAnalysisAdaptor and set it as super to the
		// newly created one
		IClassDefinition baseAdaptor = result
				.lookUp("DepthFirstAnalysisAdaptor");
		extAdaptor.setSuper(baseAdaptor);
		
		// Find the interface I<extensions name>Analysis interface and implement it
		IInterfaceDefinition extensionAnalysisInterface = result
				.lookUpInterface("I"+ extEnv.getName()+"Analysis");
		extAdaptor.addInterface(extensionAnalysisInterface);
				
		// Find the base visitedNode field
		// FIXME "".get(0) is not the best way of getting the field :)
		Field queue = baseAdaptor.getFields().get(0);

		// The inherited field type also need to be imported because it is used
		// in the constructor
		((InterfaceDefinition) extAdaptor).imports.add(queue.type);

		/**
		 * Go Through all the classes in the extension and add the corresponding
		 * methods in the extended analysis and remove them in the base analysis
		 */
		for (IClassDefinition c : getExtClasses(result.getClasses(),
				extEnv.getName(), result)) {
			switch (result.classToType.get(c)) {

			case Custom:
				break;
			case Production:
			case SubProduction: {
				AnalysisAdaptorDefaultMethod mIn = new AnalysisAdaptorDefaultMethod(
						c);
				mIn.setDefaultPostfix("In");
				mIn.setClassDefinition(c);

				extAdaptor.addMethod(mIn);
				AnalysisAdaptorDefaultMethod mOut = new AnalysisAdaptorDefaultMethod(
						c);
				mOut.setDefaultPostfix("Out");
				mOut.setClassDefinition(c);

				extAdaptor.addMethod(mOut);

			}
				break;
			case Alternative:
			case Token: {

				// IClassDefinition superDef = c.getSuperDef();
				
				AnalysisDepthFirstAdaptorCaseMethod m =new AnalysisDepthFirstAdaptorCaseMethod();  
				m.setVisitedNodesField(queue);
				m.setClassDefinition(c);
				extAdaptor.addMethod(m);

			}
				break;
			case Unknown:
				break;

			}

			AnalysisAdaptorCaseMethod mIn = new AnalysisAdaptorCaseMethod(c);
			mIn.setMethodNamePrefix("in");
			mIn.setDefaultPostfix("In");
			mIn.setClassDefinition(c);
			// mIn.setEnvironment(source);
			extAdaptor.addMethod(mIn);

			AnalysisAdaptorCaseMethod mOut = new AnalysisAdaptorCaseMethod(c);
			mOut.setMethodNamePrefix("out");
			mOut.setDefaultPostfix("Out");
			mOut.setClassDefinition(c);
			// mOut.setEnvironment(source);
			extAdaptor.addMethod(mOut);

			// Remove the case for the existing adaptor
			List<Method> toBeRemoved = new LinkedList<Method>();
			for (Method method : baseAdaptor.getMethods()) {
				if (method.classDefinition == c) {
					toBeRemoved.add(method);
				}
			}
			baseAdaptor.getMethods().removeAll(toBeRemoved);

		}

		// {
		// AnalysisAdaptorDefaultNodeMethod mOut = new
		// AnalysisAdaptorDefaultNodeMethod();
		// mOut.setDefaultPostfix("Out");
		// // mOut.setEnvironment(source);
		// extAdaptor.addMethod(mOut);
		//
		// AnalysisAdaptorDefaultNodeMethod mIn = new
		// AnalysisAdaptorDefaultNodeMethod();
		// mIn.setDefaultPostfix("In");
		// // mIn.setEnvironment(source);
		// extAdaptor.addMethod(mIn);
		// }
		//
		// {
		// AnalysisAdaptorDefaultTokenMethod mOut = new
		// AnalysisAdaptorDefaultTokenMethod();
		// mOut.setDefaultPostfix("Out");
		// // mOut.setEnvironment(source);
		// extAdaptor.addMethod(mOut);
		//
		// AnalysisAdaptorDefaultTokenMethod mIn = new
		// AnalysisAdaptorDefaultTokenMethod();
		// mIn.setDefaultPostfix("In");
		// // mIn.setEnvironment(source);
		// extAdaptor.addMethod(mIn);
		// }
		//
		// // FIXME adaptor.getImports().addAll(source.getAllDefinitions());
	}

	private static void createAnalysisInterface(List<String> genericArguments,
			String name, String tag, MethodFactory extMf, Environment extEnv,
			Environment result, Environment base) {

		// Create a extended analysis interface and add it to result
		JavaName jname = new JavaName(
				result.getAstPackage() + ".analysis.intf", "I"
						+ extEnv.getName() + name);
		InterfaceDefinition extNewDef = new InterfaceDefinition(jname,
				result.getAstPackage());
		extNewDef.setTag(tag);
		extNewDef.setGenericArguments(genericArguments);
		result.addInterface(extNewDef);
		IInterfaceDefinition iAnalysis = result.lookUpType("I" + name);
		// extNewDef.supers.add(iAnalysis);
		extNewDef.setExtJavaDoc(COMPASS_JAVA_DOC_STRING);

		extNewDef.setIsExtTree(true);
		extNewDef.setIsBaseTree(false);
		
		// Add methods for the analysis and apply functions for the classes
		for (IClassDefinition cdef : result.getClasses()) {
			Environment env = null;
			IInterfaceDefinition newDef = null;
			MethodFactory mf = null;
			if (!base.classToType.containsKey(cdef)
					&& result.classToType.containsKey(cdef)) {
				mf = extMf;
				newDef = extNewDef;
				env = result;
			}

			// Should it have an apply method and a case in the analysis?
			if (mf != null)
				switch (env.classToType.get(cdef)) {
				case Alternative:
				case Token: {
					mf.updateApplyMethod(cdef, jname.getCanonicalName());

					Method caseMethod = mf.createCaseMethod(cdef);
					newDef.addMethod(caseMethod);

					// remove this case from IAnalysis
					List<Method> toBeRemoved = new LinkedList<Method>();
					for (Method m : iAnalysis.getMethods()) {
						if (m.classDefinition == cdef)
							toBeRemoved.add(m);
					}
					iAnalysis.getMethods().removeAll(toBeRemoved);

				}
					break;
				default:
					break;
				}
		}

		// Find existing adaptor class
		String adaptorName = name + "Adaptor";
		IClassDefinition answerClass = result.lookUp(adaptorName);

		// Create adaptor class
		IClassDefinition extAnswerClass = ClassFactory.createCustom(
				new JavaName(result.getAstPackage() + ".analysis", name
						+ extEnv.getName() + "Adaptor"), result);
		extAnswerClass.setGenericArguments(extNewDef.getGenericArguments());
		extAnswerClass.addInterface(extNewDef);
		extAnswerClass.setSuper(answerClass);

		for (IClassDefinition cdef : result.getClasses()) {
			if (result.classToType.containsKey(cdef)
					&& !base.classToType.containsKey(cdef)) {
				switch (result.classToType.get(cdef)) {
				case Alternative:
				case Token:

					Method caseMethod = extMf.createCaseMethod(cdef);
					extAnswerClass.addMethod(caseMethod);

					// Remove the case for the existing adaptor
					List<Method> toBeRemoved = new LinkedList<Method>();
					for (Method m : answerClass.getMethods()) {
						if (m.classDefinition == cdef) {
							toBeRemoved.add(m);
						}
					}
					answerClass.getMethods().removeAll(toBeRemoved);
					break;
				case Production:
				case SubProduction:

					Method defaultCase = extMf.createDefaultCaseMethod(cdef);
					defaultCase.setClassDefinition(cdef);
					extAnswerClass.addMethod(defaultCase);

					// Remove the case for the existing adaptor
					toBeRemoved = new LinkedList<Method>();
					for (Method m : answerClass.getMethods()) {
						if (m.classDefinition == cdef) {
							toBeRemoved.add(m);
						}
					}
					answerClass.getMethods().removeAll(toBeRemoved);
					break;
				default:
					break;
				}
			}
		}

	}
}
