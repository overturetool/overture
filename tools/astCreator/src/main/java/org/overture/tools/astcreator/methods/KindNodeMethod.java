package org.overture.tools.astcreator.methods;

import java.util.HashSet;
import java.util.Set;

import org.overture.tools.astcreator.definitions.IClassDefinition;
import org.overture.tools.astcreator.env.Environment;
import org.overture.tools.astcreator.utils.EnumUtil;

public class KindNodeMethod extends Method
  {
    public KindNodeMethod()
      {
        super(null);
      }
    
    public KindNodeMethod(IClassDefinition c)
      {
        super(c);
      }
    
    @Override
    protected void prepare(Environment env)
      {
        
//        StringBuilder sb = new StringBuilder();
        /*
         * sb.append("\t/**\n"); //
         * sb.append("\t * Returns the {@link NodeEnum"+
         * env.getInterfaceForCommonTreeNode
         * (classDefinition).getName().getPostfix()+"} corresponding to the\n");
         * sb.append("\t * type of this {@link " + env.iNode.getName().getName()
         * + "} node.\n"); sb.append("\t * @return the {@link NodeEnum" +
         * env.getInterfaceForCommonTreeNode(classDefinition).getName()
         * .getPostfix() + "} for this node\n"); sb.append("\t "); this.javaDoc
         * = sb.toString();
         */
        name = "kindNode";
        annotation = "@Override";
        returnType = "String";
        body = "\t\treturn kindNode;";
        
        // @Override public NodeEnum kindNode() {
        // return NodeEnum._BINOP;
        // }
      }
    
//    @Override
//    public Set<String> getRequiredImports(Environment env)
//      {
//        Set<String> imports = new HashSet<String>();
//        imports.addAll(super.getRequiredImports(env));
//        
//        imports.add(env.getTemplateDefaultPackage() + "."
//            + EnumUtil.getEnumTypeName(env.node, env));
//        
//        return imports;
//      }
    
    @Override
    protected void prepareVdm(Environment env)
      {
        skip = true;
      }
  }
