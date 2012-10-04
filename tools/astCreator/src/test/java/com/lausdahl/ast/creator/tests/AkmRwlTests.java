package com.lausdahl.ast.creator.tests;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.lausdahl.ast.creator.Main;

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
                            + "      = {and}" + "      " + "      | {or} \n"
                            + "      ;\n" + "Aspect Declaration\n";
    
    private String src2 = "Packages\n"
                            + "base eu.compassresearch.ast.node;\n"
                            + "analysis eu.compassresearch.ast.analysis;\n"
                            + "Tokens\n"
                            + "\n"
                            + "Abstract Syntax Tree\n"
                            + "cml {-> package='eu.compassresearch.ast.expressions'}\n"
                            + "    =   {binary} [left]:exp [op]:binop [right]:exp\n"
                            + "    ;\n" + "Aspect Declaration\n";
    
    /*
    public void testBasic() throws Exception
      {
        System.out.println(new File(".").getAbsolutePath());
        File output = new File(
            FilePathUtil.getPlatformPath("target/testData/simple/srcsrc2"));
        
        ByteArrayInputStream input1 = new ByteArrayInputStream(src.getBytes());
        ByteArrayInputStream input2 = new ByteArrayInputStream(src2.getBytes());
        
        Main.create(input1, input2, output, "Cml", false);
        
      }
    */
    
    public void testCmlAndOvt() throws Exception
    {
    	File output = new File("../../testdata/generatedCode");
    	InputStream cmlAST = getClass().getResourceAsStream("/extend2/cml_merge.ast");
    	InputStream ovtAST = getClass().getResourceAsStream("/extend2/ovt_merge.ast");
    	InputStream ovt = getClass().getResourceAsStream("/overtureII.astv2");
    	InputStream cml = getClass().getResourceAsStream("/ucml.ast");
    	Assert.assertNotNull("Ast file 1 cannot be found", cmlAST);
    	Assert.assertNotNull("Ast file 2 cannot be found", ovtAST);
    	Main.create(ovt, cml,output, "Cml", false );
    	// Main.create(ovt, output, true, false);
    }
    
  }
