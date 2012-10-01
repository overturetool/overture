package com.lausdahl.ast.creator;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.CommonTree;

import com.lausdahl.ast.creator.definitions.ExternalJavaClassDefinition;
import com.lausdahl.ast.creator.definitions.Field;
import com.lausdahl.ast.creator.definitions.Field.AccessSpecifier;
import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.definitions.IClassDefinition.ClassType;
import com.lausdahl.ast.creator.definitions.IInterfaceDefinition;
import com.lausdahl.ast.creator.env.Environment;
import com.lausdahl.ast.creator.methods.GetMethod;
import com.lausdahl.ast.creator.methods.Method;
import com.lausdahl.ast.creator.methods.SetMethod;
import com.lausdahl.ast.creator.methods.TokenConstructorMethod;
import com.lausdahl.ast.creator.parser.AstcLexer;
import com.lausdahl.ast.creator.parser.AstcParser;
import com.lausdahl.ast.creator.utils.ClassFactory;
import com.lausdahl.ast.creator.utils.NameUtil;

public class CreateOnParse
  {
    public Environment parse(InputStream astFile, String envName)
        throws IOException, AstCreatorException
    
      {
        Environment env = Environment.getInstance(envName);
        
        ANTLRInputStream input = new ANTLRInputStream(astFile);
        AstcLexer lexer = new AstcLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        
        AstcParser parser = new AstcParser(tokens);
        AstcParser.root_return result = null;
        try
          {
            result = parser.root();
          } catch (Exception e)
          {
            e.printStackTrace();
            throw new AstCreatorException("Exception in AST parser", e, true);
          }
        
        if (lexer.hasErrors() || lexer.hasExceptions())
          {
            throw new AstCreatorException("Errors in AST input file", null,
                true);
          }
        
        if (parser.hasErrors() || parser.hasExceptions())
          {
            throw new AstCreatorException("Errors in AST input file", null,
                true);
          }
        
        try
          {
            CommonTree t = (CommonTree) result.getTree();
            
            show(t, 0);
            processAst(env, t);
            generateInterfacesForPAndSNodes(env);
          } catch (Exception e)
          {
            e.printStackTrace();
            throw new AstCreatorException("Exception in AST parser", e, true);
          }
        
        return env;
      }
    
    private void generateInterfacesForPAndSNodes(Environment env)
      {
        for (Entry<IClassDefinition, ClassType> entry : env.classToType
            .entrySet())
          {
            switch (entry.getValue())
              {
              // case SubProduction:
                case Production:
                  ClassFactory.createInterface(entry.getKey(), env);
                  break;
              }
          }
        
        Set<IClassDefinition> rest = new HashSet<IClassDefinition>();
        for (Entry<IClassDefinition, ClassType> entry : env.classToType
            .entrySet())
          {// FIXME need to continou untill no false is returned such that all S
           // interfaces are made
            switch (entry.getValue())
              {
                case SubProduction:
                  // case Production:
                  if (!ClassFactory.createInterface(entry.getKey(), env))
                    {
                      // System.out.println("Faild to create interface for in first attempt: "
                      // + entry.getKey().getName());
                      rest.add(entry.getKey());
                    }
                  break;
              }
          }
        
        while (rest.size() > 0)
          {
            System.out.println("Retry Create interfaces - with " + rest.size()
                + " not yet created interfaces.");
            rest = createInterfaces(rest, env);
            
          }
        System.out.println("Done creating interfaces");
        
      }
    
    private Set<IClassDefinition> createInterfaces(
        Set<IClassDefinition> classes, Environment env)
      {
        Set<IClassDefinition> rest = new HashSet<IClassDefinition>();
        for (IClassDefinition c : classes)
          {
            if (!ClassFactory.createInterface(c, env))
              {
                rest.add(c);
              }
          }
        return rest;
        
      }
    
    protected void processAst(Environment env, CommonTree t)
      {
        for (Object root : t.getChildren())
          {
            if (root instanceof CommonTree)
              {
                CommonTree node = (CommonTree) root;
                if (node.getText().equals("Abstract Syntax Tree"))
                  {
                    for (Object production : node.getChildren())
                      {
                        if (production instanceof CommonTree)
                          {
                            createProductionSubProductionNode(env, production);
                          }
                      }
                  } else if (node.getText().equals("Tokens")
                    && node.getChildCount() > 0)
                  {
                    for (Object toke : node.getChildren())
                      {
                        if (toke instanceof CommonTree)
                          {
                            createTokenNode(env, toke);
                          }
                      }
                  } else if (node.getText().equals("Aspect Declaration"))
                  {
                    if (node.getChildren() != null)
                      {
                        for (Object toke : node.getChildren())
                          {
                            if (toke instanceof CommonTree)
                              {
                                createAspectField(toke, env);
                              }
                          }
                      }
                  } else if (node.getText() != null
                    && node.getText().equals("Packages"))
                  {
                    if (node.getChildren() != null)
                      {
                        for (Object toke : node.getChildren())
                          {
                            if (toke instanceof CommonTree)
                              {
                                createAndSetPackages(node, toke, env);
                              }
                          }
                      }
                  }
              }
          }
      }
    
    private void createAndSetPackages(CommonTree node, Object toke,
        Environment env)
      {
        CommonTree p = (CommonTree) toke;
        if (p.getText() != null && p.getText().equals("base")
            && node.getChildCount() > p.getChildIndex() + 1)
          {
            Object n = node.getChild(p.getChildIndex() + 1);
            if (n instanceof CommonTree)
              {
                env.setDefaultPackages(((CommonTree) n).getText());
                return;
              }
          } else if (p.getText() != null && p.getText().equals("analysis")
            && node.getChildCount() > p.getChildIndex() + 1)
          {
            Object n = node.getChild(p.getChildIndex() + 1);
            if (n instanceof CommonTree)
              {
                env.setAnalysisPackages(((CommonTree) n).getText());
                return;
              }
          }
      }
    
    protected void createAspectField(Object toke, Environment env)
      {
        CommonTree p = (CommonTree) toke;
        p = (CommonTree) p.getChild(0);
        
        String classDefName = getNameFromAspectNode((CommonTree) p.getChild(0));
        IClassDefinition c = env.lookUp(classDefName);
        if (c == null)
          {
            System.err.println("Failed to lookup aspect addition with " + p
                + classDefName);
            return;
          }
        
        for (int i = 1; i < p.getChildCount(); i++)
          {
            Field f = exstractField((CommonTree) p.getChild(i), env);
            f.isAspect = true;
            c.addField(f);
            addGetSetMethods(env, env.classToType.get(c), c, f);
          }
        println("Aspect Decleration: " + p);
      }
    
    protected void createTokenNode(Environment env, Object toke)
      {
        CommonTree p = (CommonTree) toke;
        CommonTree idT = null;
        boolean externalJavaType = false;
        boolean enumType = false;
        boolean nodeType = false;
        if (p.getChildCount() > 0)
          {
            idT = (CommonTree) p.getChild(0);
            if (p.getChildCount() > 1)
              {
                if (p.getChild(0).getText().equals("java"))
                  {
                    externalJavaType = true;
                    if (p.getChildCount() > 2
                        && p.getChild(2).getText().equals("enum"))
                      {
                        enumType = true;
                        idT = (CommonTree) p.getChild(4);
                      } else if (p.getChildCount() > 2
                        && p.getChild(2).getText().equals("node"))
                      {
                        nodeType = true;
                        idT = (CommonTree) p.getChild(4);
                      } else
                      {
                        idT = (CommonTree) p.getChild(2);
                      }
                  }
              }
          }
        
        IClassDefinition c = null;
        if (!externalJavaType)
          {
            
            c = ClassFactory.create(env.getDefaultPackage() + ".tokens",
                p.getText(), env.token, ClassType.Token, env);
            // c.setPackageName(env.getDefaultPackage() + ".tokens");
          } else if (enumType)
          {
            c = ClassFactory.createExternalJavaEnum(p.getText(),
                ClassType.Token, idT.getText(), env);
          } else
          {
            c = ClassFactory.createExternalJava(p.getText(), ClassType.Token,
                idT.getText(), nodeType, env);
          }
        
        // TODO
        // c.imports.add(env.token);
        // c.imports.add(env.iNode);
        Field f = new Field(env);
        f.name = "text";
        f.type = Environment.stringDef;
        f.isTokenField = true;
        c.addField(f);
        if (!externalJavaType)
          {
            addGetSetMethods(env, ClassType.Token, c, f);
          }
        Method m = new TokenConstructorMethod(c, f, idT.getText());
        c.addMethod(m);
        println("Token: " + p);
      }
    
    protected void createProductionSubProductionNode(Environment env,
        Object production)
      {
        CommonTree p = (CommonTree) production;
        CommonTree nameNode = (CommonTree) p.getChildren().get(0);
        String packageName = env.getDefaultPackage();
        
        IClassDefinition c = null;
        boolean useNonPackageSearchForSubNode = false;
        if (nameNode.getText().equals("#"))
          {
            // initial search with default package, this may be wrong
            for (IClassDefinition def : env.getClasses())
              {
                if (def.getName().getTag()
                    .equals("#" + nameNode.getChild(0).getText()))
                  {
                    c = def;
                    useNonPackageSearchForSubNode = true;
                    break;
                  }
              }
          }
        // else if (nameNode.getText().equals("NODE"))
        // {
        // c = env.node;
        // }
        else
          {
            c = ClassFactory.create(env.getDefaultPackage(),
                nameNode.getText(), env.node, ClassType.Production, env);
            // c.setPackageName(env.getDefaultPackage());
          }
        
        boolean foundNameNode = true;
        for (Object a : p.getChildren())
          {
            if (foundNameNode)
              {
                foundNameNode = false;
                continue;
              }
            if (a instanceof CommonTree)
              {
                CommonTree aa = (CommonTree) a;
                
                if (aa.getText() != null && aa.getText().equals("->"))
                  {
                    for (Object o : aa.getChildren())
                      {
                        if (o instanceof CommonTree
                            && ((CommonTree) o).getChildCount() > 0)
                          {
                            CommonTree oo = (CommonTree) o;
                            if (oo.getText().equals("package"))
                              {
                                packageName = oo.getChild(0).getText();
                                //
                                if (nameNode.getText().equals("#"))
                                  {
                                    // we need to search again since we have a
                                    // package to make a better search
                                    for (IClassDefinition def : env
                                        .getClasses())
                                      {
                                        if (def
                                            .getName()
                                            .getTag()
                                            .equals(
                                                "#"
                                                    + nameNode.getChild(0)
                                                        .getText())
                                            && def.getName().getPackageName()
                                                .equals(packageName))
                                          {
                                            c = def;
                                            useNonPackageSearchForSubNode = false;
                                            break;
                                          }
                                      }
                                  } else
                                  {
                                    //
                                    c.getName().setPackageName(packageName);
                                  }
                              }
                          }
                      }
                  } else if (aa.getText() != null
                    && aa.getText().equals("ALTERNATIVE_SUB_ROOT"))
                  {
                    
                    IClassDefinition subAlternativeClassDef = ClassFactory
                        .create(packageName, aa.getChild(0).getText(), c,
                            ClassType.SubProduction, env);
                    subAlternativeClassDef.getName().setTag(
                        "#" + aa.getChild(0).getText());
                  } else
                  {
                    exstractA(c, aa, env, packageName);
                  }
              }
          }
        if (useNonPackageSearchForSubNode)
          {
            System.out
                .println("WARNING: Using SubProduction class found without using package name. "
                    + c.getName().getName());
          }
      }
    
    public static String unfoldName(CommonTree p)
      {
        String topName = p.getText();
        if (p.getChildCount() > 0)
          {
            for (Object c : p.getChildren())
              {
                if (c instanceof CommonTree)
                  {
                    topName += unfoldName(((CommonTree) c));
                  }
              }
          }
        return topName;
      }
    
    public static String getNameFromAspectNode(CommonTree p)
      {
        
        String topName = unfoldName(p);
        
        String[] names = topName.split("->");
        
        List<String> nns = Arrays.asList(names);
        Collections.reverse(nns);
        
        ClassType type = ClassType.Unknown;
        String name = null;
        for (String s : nns)
          {
            if (name == null)
              {
                if (s.startsWith("#"))
                  {
                    type = ClassType.SubProduction;
                    name = "S";
                  } else
                  {
                    type = ClassType.Production;
                    name = "P";
                  }
              }
            
            name += NameUtil.firstLetterUpper(s.replace("#", ""));
          }
        
        switch (type)
          {
            case Production:
            case SubProduction:
              name += "Base";
              break;
          }
        // String name = (topName.startsWith("#") ? "S" : "P");
        //
        // name +=
        // BaseClassDefinition.firstLetterUpper(topName.substring(topName.startsWith("#")
        // ? 1
        // : 0));
        
        return name;
      }
    
    private static void exstractA(IClassDefinition superClass, CommonTree a,
        Environment env, String thisPackage)
      {
        // CommonTree nameNode = (CommonTree)a.getChildren().get(0);
        ClassType type = ClassType.Alternative;
        if (superClass == env.node)
          {
            type = ClassType.Production;
          }
        IClassDefinition c = ClassFactory.create(thisPackage, a.getText(),
            superClass, type, env);
        // c.setPackageName(thisPackage);
        if (a.getChildCount() > 0)
          {
            for (Object f : a.getChildren())
              {
                if (f instanceof CommonTree)
                  {
                    Field field = exstractField((CommonTree) f, env);
                    
                    c.addField(field);
                    addGetSetMethods(env, type, c, field);
                    
                  }
              }
          }
      }
    
    protected static void addGetSetMethods(Environment env, ClassType type,
        IClassDefinition c, Field field)
      {
        Method setM = new SetMethod(c, field);
        c.addMethod(setM);
        
        Method getM = new GetMethod(c, field);
        c.addMethod(getM);
        if (type == ClassType.Production || type == ClassType.SubProduction)
          {
            field.accessspecifier = AccessSpecifier.Protected;
            
            // update interface
            IInterfaceDefinition intf = env.getInterfaceForCommonTreeNode(c);
            if (intf != null)
              {
                intf.addMethod(setM);
                intf.addMethod(getM);
              }
          }
      }
    
    private static Field exstractField(CommonTree fTree, Environment env)
      {
        // CommonTree fTree = (CommonTree) f;
        Field field = new Field(env);
        String typeName = fTree.getText();
        
        int SYMBOL_POS = 0;
        int NAME_POS = 1;
        if (fTree.getChildCount() > 1 && fTree.getChild(NAME_POS) != null)
          {
            field.name = fTree.getChild(NAME_POS).getText();
            if (fTree.getChild(SYMBOL_POS) != null
                && fTree.getChild(SYMBOL_POS).getText().equals("("))
              {
                field.structureType = Field.StructureType.Graph;
              }
          }
        int REPEAT_POS = 2;
        if (fTree.getChildCount() > 2)
          {
            if (fTree.getChild(1) != null)
              {
                String regex = fTree.getChild(REPEAT_POS).getText();
                if (regex.trim().equals("*"))
                  {
                    field.isList = true;
                  }
                if (regex.trim().equals("**"))
                  {
                    field.isList = true;
                    field.isDoubleList = true;
                  }
              }
          }
        
        for (IClassDefinition cl : env.getClasses())
          {
            if (cl instanceof ExternalJavaClassDefinition
                && ((ExternalJavaClassDefinition) cl).getName().getTag()
                    .equals(typeName))
              {
                field.isTokenField = true;
                field.type = cl;// TODO
              }
          }
        if (field.type == null)
          {
            field.setType(typeName);
          }
        return field;
      }
    
    public static void show(CommonTree token, int level)
      {
        String indent = "";
        for (int i = 0; i < level; i++)
          {
            indent += "  ";
          }
        
        println(indent + token.getText());
        if (token.getChildCount() > 0)
          {
            for (Object chld : token.getChildren())
              {
                if (chld instanceof CommonTree)
                  {
                    show((CommonTree) chld, level + 1);
                  }
              }
          }
        if (level == 2)
          println();
      }
    
    private static void println(String text)
      {
        if (Main.test)
          {
            System.out.println(text);
          }
      }
    
    private static void println()
      {
        if (Main.test)
          {
            System.out.println();
          }
      }
  }
