package com.lausdahl.ast.creator.extend;

import java.util.Map.Entry;
import java.util.Set;

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
        Environment result = Environment.getEmptyInstance("extended");
        
        result.setDefaultPackages(base.getDefaultPackage());
        result.setAnalysisPackages(base.getAnalysisPackage());
        // result.setAnalysisPackages(ext.getAnalysisPackage());
        // result.setDefaultPackages(ext.getDefaultPackage());
        
        // Copy over all class definitions
        for (IClassDefinition cdef : base.getClasses())
          result.getClasses().add(cdef);
        
        // Copy over all interfaces
        for (IInterfaceDefinition idef : base.getInterfaces())
          result.getInterfaces().add(idef);
        
        // Copy classToType mapping
        Set<Entry<IClassDefinition, ClassType>> c2t = base.classToType
            .entrySet();
        for (Entry<IClassDefinition, ClassType> e : c2t)
          result.classToType.put(e.getKey(), e.getValue());
        
        // Copy treenodes to interfaces mapping
        Set<Entry<IInterfaceDefinition, IInterfaceDefinition>> tn2i = base.treeNodeInterfaces
            .entrySet();
        for (Entry<IInterfaceDefinition, IInterfaceDefinition> e : tn2i)
          result.treeNodeInterfaces.put(e.getKey(), e.getValue());
        
        // Extend alternatives
        
        // Extend w. fields
        return result;
      }
  }
