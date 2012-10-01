package com.lausdahl.ast.creator.extend;

import java.util.Map.Entry;
import java.util.Set;

import com.lausdahl.ast.creator.definitions.BaseClassDefinition;
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
    
    public Environment extend(Environment ext)
      {
        Environment result = Environment.getInstance("extended");
        
//        result.setDefaultPackages(base.getDefaultPackage());
//        result.setAnalysisPackages(base.getAnalysisPackage());
        
        result.setAnalysisPackages(ext.getAnalysisPackage());
        result.setDefaultPackages(ext.getDefaultPackage());
        
        // Copy over all class definitions
        for (IClassDefinition cdef : base.getClasses())
          result.getClasses().add(cdef);
        
        for(IClassDefinition cdef : ext.getClasses())
        	result.getClasses().add(cdef);
        
        // Copy over all interfaces
        for (IInterfaceDefinition idef : base.getInterfaces())
          result.getInterfaces().add(idef);
        
        for(IInterfaceDefinition idef : ext.getInterfaces())
        	result.getInterfaces().add(idef);
        
        // Copy classToType mapping
        Set<Entry<IClassDefinition, ClassType>> c2t = base.classToType
            .entrySet();
        for (Entry<IClassDefinition, ClassType> e : c2t)
          result.classToType.put(e.getKey(), e.getValue());
        
        c2t = ext.classToType.entrySet();
        for(Entry<IClassDefinition, ClassType> e : c2t)
        	result.classToType.put(e.getKey(), e.getValue());
        
        // Copy treenodes to interfaces mapping
        Set<Entry<IInterfaceDefinition, IInterfaceDefinition>> tn2i = base.treeNodeInterfaces
            .entrySet();
        for (Entry<IInterfaceDefinition, IInterfaceDefinition> e : tn2i)
        {
        	if (e.getKey() == base.node) continue;
        	if (e.getKey() == base.token) continue;
            result.treeNodeInterfaces.put(e.getKey(), e.getValue());
        }
        
        tn2i = ext.treeNodeInterfaces.entrySet();
        for(Entry<IInterfaceDefinition, IInterfaceDefinition> e : tn2i)
        {
        	if (e.getKey() == base.node) continue;
        	if (e.getKey() == base.token) continue;
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
