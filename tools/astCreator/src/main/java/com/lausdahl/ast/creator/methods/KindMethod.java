package com.lausdahl.ast.creator.methods;

import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.env.Environment;
import com.lausdahl.ast.creator.utils.EnumUtil;

public class KindMethod extends Method
  {
    
    boolean isAbstractKind = false;
    
    public KindMethod(IClassDefinition c, boolean isAbstractKind
        )
      {
        super(c);
        this.isAbstractKind = isAbstractKind;
      }
    
    @Override
    protected void prepare(Environment env)
      {
        
        if (isAbstractKind)// (c.getType() == ClassType.Production)
          {
            this.isAbstract = true;
            this.name = "kind"
                + env.getInterfaceForCommonTreeNode(classDefinition).getName()
                    .getName();
            this.returnType = EnumUtil.getEnumTypeName(classDefinition, env);
          } else
          {
            IClassDefinition superClass = classDefinition.getSuperDef();
            if (env.isTreeNode(superClass))
              {
                String enumerationName = EnumUtil.getEnumTypeName(superClass,
                    env);
                this.name = "kind"
                    + env
                        .getInterfaceForCommonTreeNode(
                            classDefinition.getSuperDef()).getName().getName();
                
                // this.arguments.add(new Argument(f.getType(), "value"));
                this.returnType = enumerationName;
                this.annotation = "@Override";
                this.body = "\t\treturn " + enumerationName + "."
                    + EnumUtil.getEnumElementName(classDefinition) + ";";
              }
          }
        
        javaDoc = "\t/**\n";
        javaDoc += "\t * Returns the {@link " + this.returnType
            + "} corresponding to the\n";
        javaDoc += "\t * type of this {@link " + this.returnType + "} node.\n";
        javaDoc += "\t * @return the {@link " + this.returnType
            + "} for this node\n";
        javaDoc += "\t */";
      }
    
    @Override
    protected void prepareVdm(Environment env)
      {
        skip = true;
      }
  }
