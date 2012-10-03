package com.lausdahl.ast.creator.extend;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.lausdahl.ast.creator.definitions.BaseClassDefinition;
import com.lausdahl.ast.creator.definitions.ExternalJavaClassDefinition;
import com.lausdahl.ast.creator.definitions.Field;
import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.definitions.IClassDefinition.ClassType;
import com.lausdahl.ast.creator.definitions.IInterfaceDefinition;
import com.lausdahl.ast.creator.env.Environment;

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

	public Environment extend(Environment ext)
	{
		Environment result = Environment.getFromBase(base, ext.getAnalysisPackage(), ext.getDefaultPackage());

		result.setTemplateAnalysisPackage(base.getAnalysisPackage());
		result.setTemplateDefaultPackage(base.getDefaultPackage());
		
		// Copy over all class definitions
		for (IClassDefinition cdef : base.getClasses())
			result.getClasses().add(cdef);

		for(IClassDefinition cdef : ext.getClasses())
			if (!isTemplateClass(cdef, ext))
				result.getClasses().add(cdef);

		// Copy over all interfaces
		for (IInterfaceDefinition idef : base.getInterfaces())
			result.getInterfaces().add(idef);

		for(IInterfaceDefinition idef : ext.getInterfaces())
			if (!isTemplateClass(idef, ext))
				result.getInterfaces().add(idef);

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

		tn2i = ext.treeNodeInterfaces.entrySet();
		for(Entry<IInterfaceDefinition, IInterfaceDefinition> e : tn2i)
		{
			if (!isTemplateClass(e.getKey(), ext))			
				result.treeNodeInterfaces.put(e.getKey(), e.getValue());        	
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
						int a;
						
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
