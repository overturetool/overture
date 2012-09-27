package com.lausdahl.ast.creator.tests;

import java.io.ByteArrayInputStream;
import java.io.File;

import junit.framework.TestCase;

public class AkmRwlTests extends TestCase
  {
    
    private String src  = "Packages\n"
                            + "base org.overture.ast.node;\n"
                            + "analysis org.overture.ast.analysis;\n"
                            + "Tokens\n"
                            + "\n"
                            + "Abstract Syntax Tree\n"
                            + "exp {-> package='org.overture.ast.expressions'}\n"
                            + "    =   {binary} [left]:exp [op]:binop [right]:exp\n"
                            + "    ;\n"
                            + "binop {-> package='org.overture.ast.expressions'}\n"
                            + "      = {and}" + "      | {or} \n" + "      ;\n"
                            + "Aspect Declaration\n";
    
    private String src2 = "Packages\n"
                            + "base eu.compassresearch.ast.node;\n"
                            + "analysis eu.compassresearch.ast.analysis;\n"
                            + "Tokens\n"
                            + "\n"
                            + "Abstract Syntax Tree\n"
                            + "cml <: exp {-> package='eu.compassresearch.ast.expressions'}\n"
                            + "    =   {binary} [left]:exp [op]:binop [right]:exp\n"
                            + "    ;\n" + "Aspect Declaration\n";
    
    public void testBasic() throws Exception
      {
        System.out.println(new File(".").getAbsolutePath());
        File output = new File(
            FilePathUtil.getPlatformPath("target/testData/simple"));
        
        ByteArrayInputStream input1 = new ByteArrayInputStream(src.getBytes());
        ByteArrayInputStream input2 = new ByteArrayInputStream(src2.getBytes());
        
        // Main.create(input1, input2, output, "Cml", false);
        
      }
    
  }
