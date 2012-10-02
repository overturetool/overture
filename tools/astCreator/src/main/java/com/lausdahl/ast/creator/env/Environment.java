package com.lausdahl.ast.creator.env;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import com.lausdahl.ast.creator.ToStringAddOn;
import com.lausdahl.ast.creator.definitions.AnalysisExceptionDefinition;
import com.lausdahl.ast.creator.definitions.BaseClassDefinition;
import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.definitions.IClassDefinition.ClassType;
import com.lausdahl.ast.creator.definitions.IInterfaceDefinition;
import com.lausdahl.ast.creator.definitions.InterfaceDefinition;
import com.lausdahl.ast.creator.definitions.PredefinedClassDefinition;
import com.lausdahl.ast.creator.java.definitions.JavaName;

public class Environment extends BaseEnvironment
  {
    public PredefinedClassDefinition                       iNode;
    public InterfaceDefinition                             iToken;
    
    public final String                                          TAG_IAnalysis       = "IAnalysis";
    public final String                                          TAG_IAnswer         = "IAnswer";
    public final String                                          TAG_IQuestion       = "IQuestion";
    public final String                                          TAG_IQuestionAnswer = "IQuestionAnswer";
    public BaseClassDefinition                             analysisException;
    
    private final List<ToStringAddOn>                            toStringAddOn       = new Vector<ToStringAddOn>();
    
    public final Map<IInterfaceDefinition, IInterfaceDefinition> treeNodeInterfaces  = new Hashtable<IInterfaceDefinition, IInterfaceDefinition>();
    
    public final Map<IClassDefinition, ClassType>                classToType         = new Hashtable<IClassDefinition, ClassType>();
    
    private String                                               analysisPackage     = "org.overture.ast.analysis";
    
    private String templateAnalysisPackage;
    
    public String getTemplateDefaultPackage() {
		return templateDefaultPackage;
	}

	public void setTemplateDefaultPackage(String templateDefaultPackage) {
		this.templateDefaultPackage = templateDefaultPackage;
	}

	public String getTemplateAnalysisPackage() {
		return templateAnalysisPackage;
	}

	public void setTemplateAnalysisPackage(String templateAnalysisPackage) {
		this.templateAnalysisPackage = templateAnalysisPackage;
	}

	private Environment(String name)
      {
        super(name);
      }
    
    public static Environment getFromBase(Environment base, String extAnalysisPackage, String extDefaultPackage)
    {
    	Environment res = new Environment("extended_"+base.name);
    	res.setAnalysisPackages(extAnalysisPackage);
    	res.setDefaultPackages(extDefaultPackage);
        res.iNode = base.iNode;
        res.iToken = base.iToken;
        res.node = base.node;
        res.token = base.token;
        res.iToken.supers.add(res.iNode);
        res.node.addInterface(res.iNode);
        res.token.addInterface(res.iToken);
        res.analysisException = base.analysisException;
        res.addCommonTreeInterface(base.node, base.iNode);
        res.addCommonTreeInterface(base.token, base.iToken);
        res.addClass(base.analysisException);
        return res;
    }
    
    public static Environment getEmptyInstance(String name)
      {
        Environment res = new Environment(name);
        return res;
      }
    
    public static Environment getInstance(String name)
      {
        Environment res = new Environment(name);
        res.iNode = new PredefinedClassDefinition(res.defaultPackage, "INode");
        res.iToken = new PredefinedClassDefinition(res.defaultPackage, "IToken");
        // iToken.addInterface(iNode);
        res.iToken.supers.add(res.iNode);
        res.node.addInterface(res.iNode);
        res.token.addInterface(res.iToken);
        res.analysisException = new AnalysisExceptionDefinition(res.analysisPackage,
            "AnalysisException", res);
        res.addCommonTreeInterface(res.node, res.iNode);
        res.addCommonTreeInterface(res.token, res.iToken); 
        res.addClass(res.analysisException);
        return res;
      }
    
    public List<IInterfaceDefinition> getInterfaces()
      {
        return super.interfaces;
      }
    
    /**
     * Sets the analysis package for this environment.
     * 
     * Also all interface and class definitions are updated with the package,
     * only if they are in the old package name.
     * 
     * @param analysisPackage
     */
    public void setAnalysisPackages(String analysisPackage)
      {
        String oldPackage = this.analysisPackage;
        this.templateAnalysisPackage = this.analysisPackage = analysisPackage;
        for (IClassDefinition c : classes)
          {
            if (c.getName().getPackageName().equals(oldPackage))
              {
                c.getName().setPackageName(analysisPackage);
              }
          }
        
        for (IInterfaceDefinition c : interfaces)
          {
            if (c.getName().getPackageName().equals(oldPackage))
              {
                c.getName().setPackageName(analysisPackage);
              }
          }
      }
    
    public String getAnalysisPackage()
      {
        return this.analysisPackage;
      }
    
    public IInterfaceDefinition lookupByTag(String tag)
    {
    	int a;
    	for(IInterfaceDefinition idef : classes)
    	{
    		if (tag.equals(idef.getTag()))
    			return idef;
    		JavaName jn = idef.getName();
    		if (jn != null && tag.equals(jn.getTag()))
    			return idef;
    	}
    	return null;
    }
    
    public IInterfaceDefinition lookUpType(String name)
      {
        IInterfaceDefinition res = lookUpInterface(name);
        if (res == null)
          {
            return lookUp(name);
          }
        return res;
      }
    
    public IClassDefinition lookUp(String name)
      {
        for (IClassDefinition cl : classes)
          {
            if (isClassNamesEqual(cl.getName().getName(), name))
              {
                return cl;
              }
          }
        return null;
      }
    
    public IInterfaceDefinition lookUpInterface(String name)
      {
        for (IInterfaceDefinition cl : interfaces)
          {
            if (isClassNamesEqual(cl.getName().getName(), name))
              {
                return cl;
              }
          }
        return null;
      }
    
    private boolean isClassNamesEqual(String a, String b)
      {
        if (a.contains("<"))
          {
            a = a.substring(0, a.indexOf('<'));
          }
        if (b.contains("<"))
          {
            b = b.substring(0, b.indexOf('<'));
          }
        return a.equals(b);
      }
    
    public boolean isSuperTo(IClassDefinition superClass,
        IClassDefinition selectedClass)
      {
        return !selectedClass.equals(superClass) && selectedClass.hasSuper()
            && selectedClass.getSuperDef().equals(superClass);
      }
    
    public IClassDefinition getSuperClass(IClassDefinition selectedClass)
      {
        for (IClassDefinition cl : classes)
          {
            if (isSuperTo(cl, selectedClass))
              {
                return cl;
              }
          }
        return null;
      }
    
    public boolean isSuperTo(IInterfaceDefinition superClass,
        IInterfaceDefinition selectedClass)
      {
        Set<IInterfaceDefinition> selectedSupers = new HashSet<IInterfaceDefinition>();
        if (selectedClass instanceof InterfaceDefinition)
          {
            selectedSupers.addAll(((InterfaceDefinition) selectedClass).supers);
          } else if (selectedClass instanceof IClassDefinition)
          {
            selectedSupers.addAll(((IClassDefinition) selectedClass)
                .getInterfaces());
            selectedSupers
                .add(((IClassDefinition) selectedClass).getSuperDef());
          }
        
        for (IInterfaceDefinition intf : selectedSupers)
          {
            if (superClass.getName().equals(intf.getName())
                || isSuperTo(superClass, intf))
              {
                return true;
              }
          }
        return false;
      }
    
    public List<IClassDefinition> getSubClasses(IClassDefinition c)
      {
        List<IClassDefinition> subclasses = new Vector<IClassDefinition>();
        for (IClassDefinition sub : classes)
          {
            // if (sub.getSuperClassDefinition() != null
            // && sub.getSuperClassDefinition().equals(c))
            // {
            if (isSuperTo(c, sub))
              {
                subclasses.add(sub);
              }
          }
        return subclasses;
      }
    
    public IInterfaceDefinition getTaggedDef(String tag)
      {
        for (IInterfaceDefinition def : interfaces)
          {
            if (def.getTag().equals(tag))
            
              {
                return def;
              }
          }
        return null;
      }
    
    public void addToStringAddOn(ToStringAddOn addon)
      {
        toStringAddOn.add(addon);
      }
    
    public List<ToStringAddOn> getToStringAddOns()
      {
        return this.toStringAddOn;
      }
    
    public void addCommonTreeInterface(IInterfaceDefinition source,
        IInterfaceDefinition intf)
      {
        addInterface(intf);
        treeNodeInterfaces.put(source, intf);
      }
    
    public IInterfaceDefinition getInterfaceForCommonTreeNode(
        IInterfaceDefinition node)
      {
        if (treeNodeInterfaces.containsKey(node))
          {
            return treeNodeInterfaces.get(node);
          }
        
        System.out.println("Error getting interface for common tree node:\n\t"
            + node.getName()
            + "\nThe mapping is as defined as follows in environment \""
            + this.name + "\": ");
        Set<Entry<IInterfaceDefinition, IInterfaceDefinition>> l = treeNodeInterfaces
            .entrySet();
        for (Entry<IInterfaceDefinition, IInterfaceDefinition> e : l)
          {
            System.out.println("\t" + e.getKey().getName() + " ==> "
                + e.getValue().getName());
          }
        
        return null;
      }
    
    public boolean isTreeNode(IClassDefinition c)
      {
        if (classToType.containsKey(c))
          {
            switch (classToType.get(c))
              {
                case Alternative:
                case Production:
                case SubProduction:
                case Token:
                  return true;
              }
          }
        return false;
      }
    
    public String getInheritanceToString()
      {
        StringBuilder sb = new StringBuilder();
        sb.append("* \n* " + name + "\n*\n");
        for (IInterfaceDefinition cl : interfaces)
          {
            sb.append(pad("interface " + cl.getName().getName(), 40)
                + pad(cl.getName().getPackageName(), 35)
                + inheritanceString(cl) + "\n");
            
          }
        for (IClassDefinition cl : classes)
          {
            sb.append(pad("class " + cl.getName().getName(), 40)
                + pad(cl.getName().getPackageName(), 35)
                + inheritanceString(cl) + "\n");
            
          }
        return sb.toString();
      }
    
    private String inheritanceString(IInterfaceDefinition def)
      {
        if (def instanceof IClassDefinition)
          {
            String tmp = "";
            if (!((IClassDefinition) def).getInterfaces().isEmpty())
              {
                tmp += "(";
                for (Iterator<IInterfaceDefinition> itr = ((IClassDefinition) def)
                    .getInterfaces().iterator(); itr.hasNext();)
                  {
                    IInterfaceDefinition type = itr.next();
                    tmp += type.getName().getName();
                  }
                tmp += ")";
              }
            if (((IClassDefinition) def).hasSuper())
              {
                return (tmp.length() > 0 ? ": " + tmp : "")
                    + " <- "
                    + ((IClassDefinition) def).getSuperDef().getName()
                        .getName()
                    + inheritanceString(((IClassDefinition) def).getSuperDef());
              }
            return tmp.length() > 0 ? ": " + tmp : "";
          }
        
        if (def instanceof InterfaceDefinition)
          {
            if (!((InterfaceDefinition) def).supers.isEmpty())
              {
                String tmp = "";
                for (IInterfaceDefinition intf : ((InterfaceDefinition) def).supers)
                  {
                    tmp += " <- " + intf.getName().getName();
                  }
                return tmp;
                
              }
            return "";
          }
        
        return "";
      }
    
  }
