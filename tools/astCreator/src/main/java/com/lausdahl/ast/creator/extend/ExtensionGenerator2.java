package com.lausdahl.ast.creator.extend;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
			IInterfaceDefinition baseProduction = base.lookUpType(e.getKey());

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
			
			// Add mapping from the extensionProductionBase production to the extProduction
			result.treeNodeInterfaces.put(extensionProductionBase, extProduction);
			
			// This is actually a bit ugly (baseProductionBase should come from the extension environment)
			classReplacementMap.put(baseProductionBase.getName().getName(), extensionProductionBase);
		}
		return classReplacementMap;
	}
	
	// 5) Patch up the classToType relation:
	//     * Copy all from the base tree
	//     * Copy from the ext tree classes 
	//       that are not already taken from the base tree (leaving out template classes as well)
	
	// 6) Patch up the treeNodeInterface relation
	//     * Copy all from the base tree
	//     * Take from extension tree those interfaces we have not extended (leaving out template classes as well) 

	
	// 7) Update all super defs to point to the right root 
	
	public Environment extend(Environment ext)
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

		// Copy classToType mapping
		Set<Entry<IClassDefinition, ClassType>> c2t = base.classToType
				.entrySet();
		for (Entry<IClassDefinition, ClassType> e : c2t)
			result.classToType.put(e.getKey(), e.getValue());

		c2t = ext.classToType.entrySet();
		for(Entry<IClassDefinition, ClassType> e : c2t)
			if (!isTemplateClass(e.getKey(), ext))
				result.classToType.put(e.getKey(), e.getValue());

		// Copy treenodes to interfaces mapping
		Set<Entry<IInterfaceDefinition, IInterfaceDefinition>> tn2i = base.treeNodeInterfaces
				.entrySet();
		for (Entry<IInterfaceDefinition, IInterfaceDefinition> e : tn2i)
		{
			result.treeNodeInterfaces.put(e.getKey(), e.getValue());
		}


		// Do super replacements, typically for ext nodes pointing to e.g. 
		// PExpBase as super should be replaced with
		// PExtExpBase if an extension were made for the Exp production. 
		for(IClassDefinition cdef : ext.getClasses())
		{
			IClassDefinition superDef = cdef.getSuperDef();
			if (superDef != null){
				IClassDefinition replacementSuperDef = cReplacementMap.get(superDef.getName().getName());
				if (replacementSuperDef != null)
					cdef.setSuper(replacementSuperDef);
			}
		}
		
		tn2i = ext.treeNodeInterfaces.entrySet();
		for(Entry<IInterfaceDefinition, IInterfaceDefinition> e : tn2i)
		{
			if (!isTemplateClass(e.getKey(), ext))
			{
				if(iReplacementMap.containsKey(e.getValue().getName().getName()))
					result.treeNodeInterfaces.put(e.getKey(), iReplacementMap.get(e.getValue().getName().getName()));
				else
					result.treeNodeInterfaces.put(e.getKey(), e.getValue());
			}
		}

		// Path node, token, inode and itoken
		for(IInterfaceDefinition def : result.getAllDefinitions())
		{
			if (def instanceof BaseClassDefinition)
			{
				BaseClassDefinition bcdef = (BaseClassDefinition)def;
				if (bcdef.getSuperDef() == base.node || bcdef.getSuperDef() == ext.node)
					bcdef.setSuper(result.node);

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
							System.out.println("Shit man I cannot lookup unresolved type: "+f.getUnresolvedType());
					}

				}
			}

			if (def.getSuperDefs().contains(base.node) || def.getSuperDefs().contains(ext.node))
			{
				def.getSuperDefs().remove(base.node);
				def.getSuperDefs().remove(ext.node);
				def.getSuperDefs().add(result.node);
			}

			if (def.getSuperDefs().contains(base.iNode)|| def.getSuperDefs().contains(ext.iNode))
			{
				def.getSuperDefs().remove(base.iNode);
				def.getSuperDefs().remove(ext.iNode);
				def.getSuperDefs().add(result.iNode);
			}
		}

		return result;
	}
}
