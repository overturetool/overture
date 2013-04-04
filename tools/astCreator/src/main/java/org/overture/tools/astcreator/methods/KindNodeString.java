package org.overture.tools.astcreator.methods;

import org.overture.tools.astcreator.definitions.IClassDefinition;
import org.overture.tools.astcreator.env.Environment;
import org.overture.tools.astcreator.utils.NameUtil;

public class KindNodeString extends Method
  {
    public KindNodeString()
      {
        super(null);
      }
    
    public KindNodeString(IClassDefinition c)
      {
        super(c);
      }
    
    @Override
    protected void prepare(Environment env)
      {
    	isAbstract = true;
        name = "kindNode";
        body = "\""
        		+ NameUtil.getClassName(
        				classDefinition.getName().getRawName().startsWith("#")
        				? classDefinition.getName().getRawName().substring(1)
        				: classDefinition.getName().getRawName())
				+ "\"";
      }
        
	@Override
	public String getSignature(Environment env) {
		internalPrepare(env);
		return "\tpublic static final String " + name + " = " + body;
	}
	
    @Override
    protected void prepareVdm(Environment env)
      {
        skip = true;
      }
  }
