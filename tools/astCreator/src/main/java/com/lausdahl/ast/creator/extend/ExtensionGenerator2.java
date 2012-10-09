package com.lausdahl.ast.creator.extend;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.lausdahl.ast.creator.AstCreatorException;
import com.lausdahl.ast.creator.definitions.BaseClassDefinition;
import com.lausdahl.ast.creator.definitions.ExternalJavaClassDefinition;
import com.lausdahl.ast.creator.definitions.Field;
import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.definitions.IClassDefinition.ClassType;
import com.lausdahl.ast.creator.definitions.IInterfaceDefinition;
import com.lausdahl.ast.creator.definitions.InterfaceDefinition;
import com.lausdahl.ast.creator.definitions.PredefinedClassDefinition;
import com.lausdahl.ast.creator.env.Environment;
import com.lausdahl.ast.creator.java.definitions.JavaName;
import com.lausdahl.ast.creator.methods.KindMethod;
import com.lausdahl.ast.creator.methods.Method;
import com.lausdahl.ast.creator.methods.Method.Argument;
import com.lausdahl.ast.creator.methods.visitors.AnalysisAcceptMethod;
import com.lausdahl.ast.creator.methods.visitors.AnalysisUtil;
import com.lausdahl.ast.creator.methods.visitors.adaptor.analysis.AnalysisAdaptorCaseMethod;
import com.lausdahl.ast.creator.utils.ClassFactory;

public class ExtensionGenerator2
{

	private final Environment base;

	public ExtensionGenerator2(Environment base)
	{
		this.base = base;
	}



	private static boolean isTemplateClass(IInterfaceDefinition def, Environment ext)
	{
		if (ext.node == def) return true;
		if (ext.iNode == def) return true;
		if (ext.token == def) return true;
		if (ext.iToken == def) return true;
		if (ext.analysisException == def) return true;
		if (ext.externalNode == def) return true;
		if (ext.graphNodeList == def) return true;
		if (ext.graphNodeListList== def) return true;
		if (ext.nodeList == def) return true;
		if (ext.nodeListList == def) return true;
		if (ext.getTaggedDef(ext.TAG_IAnalysis) == def) return true;
		if (ext.getTaggedDef(ext.TAG_IAnswer) == def) return true;
		if (ext.getTaggedDef(ext.TAG_IQuestion) == def) return true;
		if (ext.getTaggedDef(ext.TAG_IQuestionAnswer) == def) return true;
		return false;
	}

	// 1) Include all class definition and interfaces from the base tree
	// - Create function: void addBaseAstClassAndInterfaces(result, baseEnv);
	private static void addBaseAstClassesAndInterfaces(Environment result, Environment baseEnv)
	{
		// Copy over all class definitions
		for (IClassDefinition cdef : baseEnv.getClasses())
			result.getClasses().add(cdef);

		// Copy over all interfaces from base
		for (IInterfaceDefinition idef : baseEnv.getInterfaces())
			result.getInterfaces().add(idef);
	}

	// 2) Include all classes from the extension tree we will not generate 
	//    an extend-version of or take from the base tree. E.g. PExpBase 
	//    from the ext env should not be copied over as we are going to 
	//    replace that with PCmlExpBase in the example
	//
	// NB! The root tree nodes and utility classes (NodeList, GraphList etc...) 
	// are the nodes to be taken from the base tree.
	//
	// - Create function: boolean willGenerateExtensionFor(iDef,baseEnv)
	// - Create function: boolean isUtilityOrTemplateClass(iDef, env)
	// - Create function: void includeClassesFromExtension(result, extEnv)
	private static boolean willGenerateExtentionFor(IInterfaceDefinition iDef, Environment base)
	{
		return base.lookUpType(iDef.getName().getName()) != null && !isUtilityOrTemplateClass(iDef, base);
	}


	private static boolean isUtilityOrTemplateClass(IInterfaceDefinition def, Environment env)
	{
		return isTemplateClass(def, env) || (def instanceof PredefinedClassDefinition);
	}

	private static void includeClassesFromExtension(Environment result, Environment ext, Environment base)
	{
		for(IClassDefinition cDef : ext.getClasses())
		{
			if (!willGenerateExtentionFor(cDef, base))
				if (!isUtilityOrTemplateClass(cDef, ext))
					result.getClasses().add(cDef);
		}		
	}


	// 3) Include all interfaces from the extension that are not going to be extended
	// - Create function: void includeInterfacesFromExtension(result, extEnv);
	private static void includeInterfacesFromExtension(Environment result, Environment extEnv, Environment base)
	{
		for(IInterfaceDefinition iDef : extEnv.getInterfaces())
			if (!willGenerateExtentionFor(iDef, base))
				result.getInterfaces().add(iDef);

	}

	private static JavaName makeExtensionJavaName(IInterfaceDefinition baseProduction, Environment ext, Environment base)
	{
		String cmlPackage = computeExtensionPackageName(ext, base, baseProduction);
		String newNameStr = ext.getName()+ baseProduction.getName().getRawName();
		JavaName newName = new JavaName(ext.getDefaultPackage(), newNameStr);
		newName.setPrefix(baseProduction.getName().getPrefix());
		newName.setPostfix(baseProduction.getName().getPostfix());
		newName.setPackageName(cmlPackage);
		return newName;
	}

	private static String computeExtensionPackageName(Environment ext, Environment base, IInterfaceDefinition baseProduction)
	{
		String cmlPackage = ext.getAstPackage() + baseProduction.getName().getPackageName().replace(base.getAstPackage(), "");
		return cmlPackage;
	}

	// 4) Generate interfaces and base classes for the interfaces to be extended in the ext env
	// - Create function: Map<String, IInterfaceDefinition> generateExtensionInterfaces(extEnv, baseEnv)
	// - Create function: Map<String, IClassDefinition> generateExtensionClasses(extEnv, baseEnv)
	private static Map<String, IInterfaceDefinition> generateExtensionInterfaces(Environment result, Environment ext, Environment base)
	{
		Map<String, IInterfaceDefinition> replacementMap = new HashMap<String, IInterfaceDefinition>();
		for(IInterfaceDefinition iDef : ext.getInterfaces())
		{
			if (willGenerateExtentionFor(iDef, base))
			{
				// Lookup base production in the base environment, e.g. the one to extend 
				IInterfaceDefinition baseProduction = base.lookUpType(iDef.getName().getName());

				// Create the new Cml Production as a sub-interface of the base production
				JavaName newName = makeExtensionJavaName(baseProduction, ext, base);
				InterfaceDefinition extProduction = new InterfaceDefinition(newName);
				extProduction.supers.add(baseProduction);

				// Update mapping, in the result environment the new extension 
				// node is a sub-interface for the baseProduction node. 
				result.addCommonTreeInterface(extProduction, baseProduction);

				// Add the newly generated interface to the interface list in the result env.
				result.getInterfaces().add(extProduction);

				// Alternatives and base classes in ext current has Production 
				// and SubProduction nodes from the ext environment that should 
				// not exist as super Def and in their interface-lists. The same 
				// is true for method and field definition inside each 
				// IInterface- and IClassDefinition.
				//
				// The replacement map will help us later to fix this by mapping the 
				// wrongly generated names to the extended names that we wish to use. 
				// 
				// Example:
				// 
				// PExp is extended with an alternative AFatBraceExp
				// 
				// We generate: PCmlExp, PCmlExpBase which shall become the parent-hierarchy 
				// for AFatBraceExp. However, the AFatBraceExp has PExp and PExpBase from the 
				// ext environment is its parent hierarchy currently. The replacement map   
				// will help us fix this later.
				replacementMap.put(baseProduction.getName().getName(), extProduction);				
			}
		}
		return replacementMap;
	}

	private static Map<String, IClassDefinition> generateExtensionClasses(Environment result, Environment ext, Environment base, Map<String, IInterfaceDefinition> replacementMap)
	{
		Map<String, IClassDefinition> classReplacementMap = new HashMap<String, IClassDefinition>();
		// Generate the base classes
		for(Entry<String, IInterfaceDefinition> e : replacementMap.entrySet())
		{
			final IInterfaceDefinition baseProduction = base.lookUpType(e.getKey());

			// Lookup the base production base class e.g. PExpBase that will be the super class 
			// for out to be created PCmlExpBase. Examplified of course
			String baseProductionBaseName = baseProduction.getName().getPrefix() + baseProduction.getName().getRawName()+"Base";
			IClassDefinition baseProductionBase = base.lookUp(baseProductionBaseName);

			IInterfaceDefinition extProduction = e.getValue();

			// In our running example let us Create the PCmlExpBase class
			String cmlPackage = computeExtensionPackageName(ext, base, baseProductionBase);
			JavaName newName = makeExtensionJavaName(baseProductionBase, ext, base);
			IClassDefinition extensionProductionBase = ClassFactory.create(cmlPackage, newName.getRawName(), baseProductionBase, base.classToType.get(baseProductionBase), result);
			extensionProductionBase.addInterface(extProduction);

			class LocalKindMethod extends KindMethod {

				public LocalKindMethod(IClassDefinition c,
						boolean isAbstractKind) {
					super(c, isAbstractKind);


				}

				@Override
				public String getJavaSourceCode(Environment env) {
					StringBuilder result = new StringBuilder();
					String returnType = "E" + baseProduction.getName().getRawName();

					result.append("public "+returnType+" kind"+baseProduction.getName().getPrefix()+baseProduction.getName().getRawName()+"()");
					result.append("{ throw new RuntimeException(\"Using the kind method is kind of deprecated ;).\"); }");
					return result.toString();
				}

			}


			// Add kindMethod that throw RuntimeException
			Method kindMethod = new LocalKindMethod(baseProductionBase, false);
			extensionProductionBase.addMethod(kindMethod);
			kindMethod.returnType = "E"+baseProduction.getName().getRawName();


			// Add mapping from the extensionProductionBase production to the extProduction
			result.treeNodeInterfaces.put(extensionProductionBase, extProduction);

			// This is actually a bit ugly (baseProductionBase should come from the extension environment)
			classReplacementMap.put(baseProductionBase.getName().getName(), extensionProductionBase);
		}
		return classReplacementMap;
	}

	/*
	 * UpdateFieldsWithUnresolvedTypes
	 * -------------------------------
	 * 
	 * We allow the extension environment (ext) to be created with unresolved 
	 * types pointing into the base environment. Now it is time to resolve these.
	 * 
	 * For every definition in the result environment so far we check all fields if 
	 * they have an unresolvedtype. If so we look up in the base environment what 
	 * type it should have.
	 */
	private void updateFieldsWithUnresolvedTypes(Environment result,
			Environment ext, Environment base) throws AstCreatorException {
		// Path node, token, inode and itoken
		for(IInterfaceDefinition def : result.getAllDefinitions())
		{
			if (def instanceof BaseClassDefinition)
			{
				BaseClassDefinition bcdef = (BaseClassDefinition)def;

				for(Field f : bcdef.getFields())
				{
					if (f.type == null)
					{
						IInterfaceDefinition type =  base.lookupByTag(f.getUnresolvedType());
						if (type == null) type = ext.lookupByTag(f.getUnresolvedType());
						if (type != null)
						{
							f.type = type;
							if (type instanceof ExternalJavaClassDefinition)
							{
								ExternalJavaClassDefinition ejcd = (ExternalJavaClassDefinition)type;
								if (ejcd.getFields() != null && ejcd.getFields().size() > 0 && ejcd.getFields().get(0).isTokenField)
									f.isTokenField = true;
							}
						}
						else
							throw new AstCreatorException("The extension points to production: "+f.getUnresolvedType()+" in alternative "+def.getName().getName()+" which does not exists.",null,true);
					}
				}
			}

		}
	}


	/*
	 * Tree node to interface mappings from the base environment are copied blindly, 
	 * for the extension we copy over the ones that are only in the extension and make sure 
	 * that any extension-interface that are created are used instead of the base interface.
	 * 
	 */
	private void updateTreeNodeToInterface(Environment result, Environment ext,
			Environment base, Map<String, IInterfaceDefinition> iReplacementMap) {

		// Copy tree nodes to interfaces mapping
		Set<Entry<IInterfaceDefinition, IInterfaceDefinition>> tn2i = base.treeNodeInterfaces
				.entrySet();
		for (Entry<IInterfaceDefinition, IInterfaceDefinition> e : tn2i)
		{
			result.treeNodeInterfaces.put(e.getKey(), e.getValue());
		}

		tn2i = ext.treeNodeInterfaces.entrySet();
		for(Entry<IInterfaceDefinition, IInterfaceDefinition> e : tn2i)
		{
			// is it not a templated type
			if (!isTemplateClass(e.getKey(), ext))
			{
				// do we have a replacement e.g. PExp replace for PExtExp 
				if(iReplacementMap.containsKey(e.getValue().getName().getName()))
					// yes! okay lookup the replacement and use that one instead
					result.treeNodeInterfaces.put(e.getKey(), iReplacementMap.get(e.getValue().getName().getName()));
				else
					// no! fine we take whatever was created for the extension environment
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
	private void updateSuperDefinitions(Environment result, Environment ext,
			Map<String, IClassDefinition> cReplacementMap) {
		for(IClassDefinition cdef : ext.getClasses())
		{
			IClassDefinition superDef = cdef.getSuperDef();
			if (superDef != null){

				// Update super to be newly generated production if necessary
				IClassDefinition replacementSuperDef = cReplacementMap.get(superDef.getName().getName());
				if (replacementSuperDef != null)
					cdef.setSuper(replacementSuperDef);

				// Update super to the result-ast if necessary
				if (superDef == ext.node)
					cdef.setSuper(result.node);

				// RWL: Hmm, node is not an interface and should not be present in supers, right?
				if (cdef.getSuperDefs().contains(base.node) || cdef.getSuperDefs().contains(ext.node))
				{
					cdef.getSuperDefs().remove(base.node);
					cdef.getSuperDefs().remove(ext.node);
					cdef.getSuperDefs().add(result.node);
				}
				
				if (cdef.getSuperDefs().contains(base.iNode) || cdef.getSuperDefs().contains(ext.iNode))
				{
					cdef.getSuperDefs().remove(base.iNode);
					cdef.getSuperDefs().remove(ext.iNode);
					cdef.getSuperDefs().add(result.iNode);
				}
			}
			
		}

		for (IInterfaceDefinition idef : ext.getInterfaces())
		{
			if (idef.getSuperDefs().contains(base.iNode) || idef.getSuperDefs().contains(ext.iNode))
			{
				idef.getSuperDefs().remove(base.iNode);
				idef.getSuperDefs().remove(ext.iNode);
				idef.getSuperDefs().add(result.iNode);
			}
			
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
		for(Entry<IClassDefinition, ClassType> e : c2t)
			//if (!isTemplateClass(e.getKey(), ext))
			if (!willGenerateExtentionFor(e.getKey(), base))
				result.classToType.put(e.getKey(), e.getValue());


	}




	/**
	 * Given an extension environment use the base environment in 
	 * this instance to resolve any unresolved types. 
	 * 
	 * The extension (ext parameter) will be destructively updated, the 
	 * environment will no longer function after this invocation. Also, the 
	 * result environment returned will be entangled with the base environment 
	 * in this instance.
	 * 
	 * @param ext - an extension environment 
	 * 
	 * @return A fresh environment entangled with ext and base. (Things are NOT cloned)
	 * 
	 * @throws AstCreatorException - An AstCreatorException is thrown if an unresolved symbol
	 *  in the extension environment cannot be resolved in the base environment of this instance.
	 */
	public Environment extend(Environment ext) throws AstCreatorException
	{
		Environment result = Environment.getFromBase(base, ext.getAnalysisPackage(), ext.getDefaultPackage());

		result.setTemplateAnalysisPackage(base.getAnalysisPackage());
		result.setTemplateDefaultPackage(base.getDefaultPackage());

		// 1 every class and interface from the base environment is added to result
		addBaseAstClassesAndInterfaces(result, base);

		// 2 include classes from the extension that we will need
		includeClassesFromExtension(result, ext, base);

		// 3 include interfaces from the extension that we will need
		includeInterfacesFromExtension(result, ext, base);

		// 4a generate new extension productions that is P and S interface
		Map<String, IInterfaceDefinition> iReplacementMap = generateExtensionInterfaces(result, ext, base);

		// 4b generate new base classes for the P and S interface generated in 4a
		Map<String, IClassDefinition> cReplacementMap = generateExtensionClasses(result, ext, base, iReplacementMap);

		// 5 Update classToType
		updateClassToType(result,ext,base);

		// 6 Update super definitions
		updateSuperDefinitions(result,ext,cReplacementMap);

		// 7 Update treeNode to interface mapping (that is the common implements relationship)
		updateTreeNodeToInterface(result, ext,base, iReplacementMap);

		// 8 Fields on nodes from the ext environment may have unresolved types
		updateFieldsWithUnresolvedTypes(result,ext,base);

		return result;
	}

	/**
	 * Create Analysis interfaces and adaptors for the extensions alone.
	 * 
	 * 
	 * @param extEnv
	 * @param result
	 */
	public static void runPostGeneration(Environment extEnv, Environment result)
	{

		createIAnalysisInterface(result, extEnv);

		
		// Look up the IAnalysis interface from the base 

		// Remove methods that are handling extension cases

		// Create Analysis for the extension adding only extension methods

		// 
	}

	private static abstract class MethodFactory {
		public abstract Method createCaseMethod(IClassDefinition cdef);
		public abstract void updateApplyMethod(IClassDefinition cdef, String newAnalysisName);
	}

	private static void createIAnalysisInterface(final Environment result, final Environment ext)
	{
		
		MethodFactory extMf = new MethodFactory() {

			@Override
			public Method createCaseMethod(IClassDefinition cdef) {
				AnalysisAdaptorCaseMethod aacm = new AnalysisAdaptorCaseMethod();
				aacm.setClassDefinition(cdef);
				return aacm;
				
			}

			@Override
			public void updateApplyMethod(final IClassDefinition cdef, final String newAnalysisName) {

				
				AnalysisAcceptMethod aam = null;
				for(Method m : cdef.getMethods())
				{
					if (m instanceof AnalysisAcceptMethod)
					{
						aam = (AnalysisAcceptMethod)m;
					}
				}
				
				if (aam != null)
				{
					aam.setPrivilegedBody("\t\t ( ("+newAnalysisName+") analysis).case" + AnalysisUtil.getCaseClass(result, cdef).getName().getName() + "(this);");
				}
				
			}

		};
		createAnalysisInterface(Arrays.asList(new String[0]), "Analysis", result.TAG_IAnalysis, extMf, ext, result);	
	}


	private static void createAnalysisInterface(List<String> genericArguments, String name, String tag, MethodFactory extMf,  Environment extEnv, Environment result) {

		// Create a extended analysis interface and add it to result
		JavaName jname = new JavaName(result.getDefaultPackage(), "I"+extEnv.getName()+name);
		InterfaceDefinition extNewDef = new InterfaceDefinition(jname);
		extNewDef.setTag(tag);
		extNewDef.setGenericArguments(genericArguments);
		result.addInterface(extNewDef);
		IInterfaceDefinition iAnalysis = result.lookUpType("IAnalysis");
		extNewDef.supers.add(iAnalysis);

		// Add methods for the analysis and apply functions for the classes
		for(IClassDefinition cdef : result.getClasses())
		{
			Environment env = null;
			IInterfaceDefinition newDef = null;
			MethodFactory mf = null;
			if (extEnv.classToType.containsKey(cdef))
			{
				mf = extMf;
				newDef = extNewDef;
				env = extEnv;
			}

			// Should it have an apply method and a case in the analysis?
			if (mf != null)
				switch(env.classToType.get(cdef))
				{
				case Alternative:
				case Token:
				{
					mf.updateApplyMethod(cdef, jname.getCanonicalName());

					Method caseMethod = mf.createCaseMethod(cdef);
					newDef.addMethod(caseMethod);
					
					// remove this case from IAnalysis
					List<Method> toBeRemoved = new LinkedList<Method>();
					for(Method m : iAnalysis.getMethods())
					{
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

	}
}
