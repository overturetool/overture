package com.lausdahl.ast.creator.extend;

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
        return base;
      }
    
  }
