// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 29-07-2009 15:19:47
// Home Page: http://members.fortunecity.com/neshkov/dj.html http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   AstMain.java

package org.overturetool.tools.astgen.vdm;

import java.io.PrintStream;

// Referenced classes of package nl.marcelverhoef.vdm.ast:
//            AstParser, wfCheckVisitor, AstDefinitions, codegenVisitor
@SuppressWarnings("all")
public class AstMain
{

    public AstMain()
    {
    }

    public static void main(String args[])
    {
        if(args.length != 1)
        {
            System.out.println("please supply a file name!");
            return;
        }
        try
        {
            AstParser ast = new AstParser(args[0]);
            System.out.println("Start parse");
            ast.yyparse();
            System.out.println("Parsing finished");
            if(ast.errors == 0)
            {
                wfCheckVisitor wVisit = new wfCheckVisitor();
                ast.theAst.accept(wVisit);
                if(wfCheckVisitor.errors.intValue() == 0)
                {
                    codegenVisitor cgVisit = new codegenVisitor();
                    cgVisit.setLong();
                    ast.theAst.accept(cgVisit);
                }
            }
            System.out.println("Done");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}