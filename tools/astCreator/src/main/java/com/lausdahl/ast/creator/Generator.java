package com.lausdahl.ast.creator;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.lausdahl.ast.creator.definitions.EnumDefinition;
import com.lausdahl.ast.creator.definitions.Field;
import com.lausdahl.ast.creator.definitions.Field.AccessSpecifier;
import com.lausdahl.ast.creator.definitions.GenericArgumentedIInterfceDefinition;
import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.definitions.IClassDefinition.ClassType;
import com.lausdahl.ast.creator.definitions.InterfaceDefinition;
import com.lausdahl.ast.creator.env.BaseEnvironment;
import com.lausdahl.ast.creator.env.Environment;
import com.lausdahl.ast.creator.java.definitions.JavaName;
import com.lausdahl.ast.creator.methods.Method;
import com.lausdahl.ast.creator.methods.SetMethod;
import com.lausdahl.ast.creator.methods.analysis.depthfirst.DepthFirstCaseMethod;
import com.lausdahl.ast.creator.methods.visitors.AnalysisAcceptMethod;
import com.lausdahl.ast.creator.methods.visitors.AnswerAcceptMethod;
import com.lausdahl.ast.creator.methods.visitors.QuestionAcceptMethod;
import com.lausdahl.ast.creator.methods.visitors.QuestionAnswerAcceptMethod;
import com.lausdahl.ast.creator.methods.visitors.adaptor.analysis.AnalysisAdaptorCaseMethod;
import com.lausdahl.ast.creator.methods.visitors.adaptor.analysis.AnalysisAdaptorDefaultMethod;
import com.lausdahl.ast.creator.methods.visitors.adaptor.analysis.AnalysisAdaptorDefaultNodeMethod;
import com.lausdahl.ast.creator.methods.visitors.adaptor.analysis.AnalysisAdaptorDefaultTokenMethod;
import com.lausdahl.ast.creator.methods.visitors.adaptor.answer.AnswerAdaptorCaseMethod;
import com.lausdahl.ast.creator.methods.visitors.adaptor.answer.AnswerAdaptorDefaultMethod;
import com.lausdahl.ast.creator.methods.visitors.adaptor.answer.AnswerAdaptorDefaultNodeMethod;
import com.lausdahl.ast.creator.methods.visitors.adaptor.answer.AnswerAdaptorDefaultTokenMethod;
import com.lausdahl.ast.creator.methods.visitors.adaptor.question.QuestionAdaptorCaseMethod;
import com.lausdahl.ast.creator.methods.visitors.adaptor.question.QuestionAdaptorDefaultMethod;
import com.lausdahl.ast.creator.methods.visitors.adaptor.question.QuestionAdaptorDefaultNodeMethod;
import com.lausdahl.ast.creator.methods.visitors.adaptor.question.QuestionAdaptorDefaultTokenMethod;
import com.lausdahl.ast.creator.methods.visitors.adaptor.questionanswer.QuestionAnswerAdaptorCaseMethod;
import com.lausdahl.ast.creator.methods.visitors.adaptor.questionanswer.QuestionAnswerAdaptorDefaultMethod;
import com.lausdahl.ast.creator.methods.visitors.adaptor.questionanswer.QuestionAnswerAdaptorDefaultNodeMethod;
import com.lausdahl.ast.creator.methods.visitors.adaptor.questionanswer.QuestionAnswerAdaptorDefaultTokenMethod;
import com.lausdahl.ast.creator.utils.ClassFactory;
import com.lausdahl.ast.creator.utils.EnumUtil;

public class Generator
  {
    
    public Environment generate(InputStream inputFile, String envName,
        boolean doTypeHierarchyCheck) throws IOException, AstCreatorException
      {
        Environment env = null;
        
        try
          {
            env = new CreateOnParse().parse(inputFile, envName);
            if (doTypeHierarchyCheck)
              for (IClassDefinition def : env.getClasses())
                {
                  def.checkFieldTypeHierarchy(env);
                }
          } catch (AstCreatorException e)
          {
            if (e.fatal)
              {
                throw e;
              } else
              {
                System.err.println(e.getMessage());
              }
          }
        
        try
          {
            ToStringAddOnReader reader = new ToStringAddOnReader();
            reader.readAndAdd(inputFile + ".tostring", env);
          } catch (AstCreatorException e)
          {
            if (e.fatal)
              {
                throw e;
              } else
              {
                System.err.println(e.getMessage());
              }
          }
        
        // SourceFileWriter.write(outputFolder, env1);
        return env;
      }
    
    public void runPostGeneration(Environment env)
        throws InstantiationException, IllegalAccessException
      {
        System.out.println("Generating enumerations...");
        createNodeEnum(env);
        createProductionEnums(env);
        
        // System.out.println("Generating interfaces for nodes");
        // createInterfacesForNodePoints(env);
        
        System.out.println("Generating analysis visitors...");
        System.out.print("Analysis...");
        createAnalysis(env);
        // createAnalysisAdaptor(env,analysisPackageName);
        System.out.print("Answer...");
        createAnswer(env);
        System.out.print("Question...");
        createQuestion(env);
        System.out.print("Question-Answer...");
        createQuestionAnswer(env);
        System.out.print("Depth-First...");
        createdepthFirstAdaptor(env);
        System.out.println();
      }
    
    public void createInterfacesForNodePoints(Environment env)
      {
        Set<IClassDefinition> classes = new HashSet<IClassDefinition>();
        for (IClassDefinition c : env.getClasses())
          {
            createInterfacesForNodePoints(env, classes, c);
          }
      }
    
    private Set<IClassDefinition> createInterfacesForNodePoints(
        Environment env, Set<IClassDefinition> processedClasses,
        IClassDefinition c)
      {
        if (processedClasses.contains(c))
          {
            return processedClasses;
          }
        
        processedClasses.add(c);
        
        if (env.isTreeNode(c))
          {
            // CommonTreeClassDefinition ct = (CommonTreeClassDefinition) c;
            switch (env.classToType.get(c))
              {
                case Alternative:
                  break;
                case Custom:
                  break;
                case Production:
                case SubProduction:
                  processedClasses.addAll(createInterfacesForNodePoints(env,
                      processedClasses, c.getSuperDef()));
                  InterfaceDefinition intf = new InterfaceDefinition(c
                      .getName().clone());
                  intf.methods.addAll(c.getMethods());
                  c.addInterface(intf);
                  intf.getName().setPackageName(
                      c.getName().getPackageName() + ".intf");
                  intf.getName().setPrefix("I" + intf.getName().getPrefix());
                  intf.filterMethodsIfInherited = true;
                  intf.supers.add(env.getInterfaceForCommonTreeNode(c
                      .getSuperDef()));
                  env.addCommonTreeInterface(c, intf);
                  break;
                case Token:
                  break;
                case Unknown:
                  break;
              }
          }
        
        return processedClasses;
      }
    
    private static void createNodeEnum(Environment env)
      {
        EnumDefinition eDef = new EnumDefinition(new JavaName(
            env.getTemplateDefaultPackage(), "NodeEnum"));
        env.addClass(eDef);
        eDef.elements.add("TOKEN");
        eDef.elements.add("ExternalDefined");
        for (IClassDefinition d : env.getClasses())
          {
            if (env.isTreeNode(d))
              {
                
                if (env.classToType.get(d) == ClassType.Production)
                  {
                    eDef.elements.add(EnumUtil.getEnumElementName(d));
                  }
              }
          }
      }
    
    private static void createProductionEnums(Environment env)
      {
        List<EnumDefinition> enums = new Vector<EnumDefinition>();
        
        for (IClassDefinition d : env.getClasses())
          {
            if (env.isTreeNode(d))
              {
                // CommonTreeClassDefinition c = (CommonTreeClassDefinition) d;
                switch (env.classToType.get(d))
                  {
                    case Production:
                    case SubProduction:
                      {
                        EnumDefinition eDef = new EnumDefinition(new JavaName(d
                            .getName().getPackageName(), "",
                            EnumUtil.getEnumTypeNameNoPostfix(d, env), ""/*
                                                                          * d .
                                                                          * getName
                                                                          * ( )
                                                                          * .
                                                                          * getPostfix
                                                                          * ( )
                                                                          */));
                        enums.add(eDef);
                        
                        for (IClassDefinition sub : getClasses(
                            env.getSubClasses(d), env))
                          {
                            eDef.elements.add(EnumUtil.getEnumElementName(sub));
                          }
                      }
                      break;
                  }
                
              }
          }
        
        for (EnumDefinition enumDefinition : enums)
          {
            env.addClass(enumDefinition);
          }
      }
    
    private static void createAnalysis(Environment env)
        throws InstantiationException, IllegalAccessException
      {
        
        extendedVisitor("Analysis", new Vector<String>(),
            AnalysisAcceptMethod.class, AnalysisAdaptorCaseMethod.class,
            AnalysisAdaptorDefaultMethod.class,
            AnalysisAdaptorDefaultNodeMethod.class,
            AnalysisAdaptorDefaultTokenMethod.class, env, env.TAG_IAnalysis);
      }
    
    private static void createAnswer(Environment env)
        throws InstantiationException, IllegalAccessException
      {
        List<String> genericArguments = new Vector<String>();
        genericArguments.add("A");
        extendedVisitor("Answer", genericArguments, AnswerAcceptMethod.class,
            AnswerAdaptorCaseMethod.class, AnswerAdaptorDefaultMethod.class,
            AnswerAdaptorDefaultNodeMethod.class,
            AnswerAdaptorDefaultTokenMethod.class, env, env.TAG_IAnswer);
      }
    
    private static void createQuestion(Environment env)
        throws InstantiationException, IllegalAccessException
      {
        List<String> genericArguments = new Vector<String>();
        genericArguments.add("Q");
        extendedVisitor("Question", genericArguments,
            QuestionAcceptMethod.class, QuestionAdaptorCaseMethod.class,
            QuestionAdaptorDefaultMethod.class,
            QuestionAdaptorDefaultNodeMethod.class,
            QuestionAdaptorDefaultTokenMethod.class, env, env.TAG_IQuestion);
      }
    
    private static void createQuestionAnswer(Environment env)
        throws InstantiationException, IllegalAccessException
      {
        List<String> genericArguments = new Vector<String>();
        genericArguments.add("Q");
        genericArguments.add("A");
        extendedVisitor("QuestionAnswer", genericArguments,
            QuestionAnswerAcceptMethod.class,
            QuestionAnswerAdaptorCaseMethod.class,
            QuestionAnswerAdaptorDefaultMethod.class,
            QuestionAnswerAdaptorDefaultNodeMethod.class,
            QuestionAnswerAdaptorDefaultTokenMethod.class, env,
            env.TAG_IQuestionAnswer);
      }
    
    @SuppressWarnings("rawtypes")
    public static void extendedVisitor(String intfName,
        List<String> genericArguments, Class accept, Class caseM,
        Class defaultCase, Class defaultNodeMethod, Class defaultTokenMethod,
        Environment env, String tag) throws InstantiationException,
        IllegalAccessException
      {
        InterfaceDefinition answerIntf = new InterfaceDefinition(new JavaName(
            env.getTemplateAnalysisPackage()+ ".intf", "I" + intfName));
        answerIntf.setTag(tag);
        answerIntf.setGenericArguments(genericArguments);
        env.addInterface(answerIntf);
        answerIntf.supers.add(BaseEnvironment.serializableDef);
        
        for (IClassDefinition c : getClasses(env.getClasses(), env))
          {
            // if (env.classToType.get(c) ==
            // IClassDefinition.ClassType.Production)
            // {
            // continue;
            // }
            // Method m = (Method) accept.newInstance();
            // m.setClassDefinition(c);
            // m.setEnvironment(env);
            // c.addMethod(m);
            //
            // m = (Method) caseM.newInstance();
            // m.setClassDefinition(c);
            // m.setEnvironment(env);
            // answerIntf.methods.add(m);
            
            switch (env.classToType.get(c))
              {
                case Alternative:
                case Token:
                  {
                    Method m = (Method) accept.newInstance();
                    m.setClassDefinition(c);
                    //m.setEnvironment(env);
                    c.addMethod(m);
                    
                    m = (Method) caseM.newInstance();
                    m.setClassDefinition(c);
                    //m.setEnvironment(env);
                    answerIntf.methods.add(m);
                    break;
                  }
                case Production:
                case SubProduction:
                  {
                    break;
                  }
              }
            
          }
        
        IClassDefinition answerClass = ClassFactory.createCustom(new JavaName(
            env.getTemplateAnalysisPackage(), intfName + "Adaptor"), env);
        answerClass.setGenericArguments(answerIntf.getGenericArguments());
        answerClass.addInterface(answerIntf);
        
        for (IClassDefinition c : env.getClasses())
          {
            if (env.isTreeNode(c))
              {
                switch (env.classToType.get(c))
                  {
                    case Alternative:
                    case Token:
                      {
                        Method m = (Method) caseM.newInstance();
                        m.setClassDefinition(c);
                        // m.setEnvironment(env);
                        answerClass.addMethod(m);
                      }
                      break;
                    case SubProduction:
                      // {
                      // Method m = (Method) caseM.newInstance();
                      // m.setClassDefinition(c);
                      // m.setEnvironment(env);
                      // answerClass.addMethod(m);
                      // }
                    case Production:
                      {
                        Method m = (Method) defaultCase.newInstance();
                        m.setClassDefinition(c);
                        // m.setEnvironment(env);
                        answerClass.addMethod(m);
                      }
                      break;
                    
                    case Custom:
                      break;
                    case Unknown:
                      break;
                  
                  }
              }
          }
        
        Method m = (Method) defaultNodeMethod.newInstance();
        // m.setEnvironment(env);
        answerClass.addMethod(m);
        
        m = (Method) defaultTokenMethod.newInstance();
        // m.setEnvironment(env);
        answerClass.addMethod(m);
      }
    
    public static List<IClassDefinition> getClasses(
        List<IClassDefinition> classList, Environment env)
      {
        List<IClassDefinition> classes = new Vector<IClassDefinition>();
        for (IClassDefinition c : classList)
          {
            if (env.isTreeNode(c))
              {
                classes.add(c);
              }
          }
        return classes;
      }
    
    private void createdepthFirstAdaptor(Environment source)
      {
        IClassDefinition adaptor = ClassFactory.createCustom(new JavaName(
            source.getAnalysisPackage(), "DepthFirstAnalysisAdaptor"), source);
        // adaptor.setAnnotation("@SuppressWarnings(\"unused\")");
        adaptor.addInterface(source.getTaggedDef(source.TAG_IAnalysis));
        Field queue = new Field();
        queue.name = "visitedNodes";
        queue.accessspecifier = AccessSpecifier.Protected;
        queue.type = new GenericArgumentedIInterfceDefinition(
            BaseEnvironment.setDef, source.iNode.getName().getName());
        // TODO
        // queue.setCustomInitializer("new java.util.LinkedList<"+source.iNode.getName().getName()+">()");
        queue.setCustomInitializer("new java.util.HashSet<"
            + source.iNode.getName().getName() + ">()");
        adaptor.addField(queue);
        ((InterfaceDefinition) adaptor).imports.add(queue.type);
        adaptor.addMethod(new SetMethod(adaptor, queue));
        adaptor
            .setAnnotation("@SuppressWarnings({\"rawtypes\",\"unchecked\"})");
        
        for (IClassDefinition c : Generator.getClasses(source.getClasses(),
            source))
          {
            // if (source.classToType.get(c) !=
            // IClassDefinition.ClassType.Production)
            // {
            // // continue;
            // Method m = new DepthFirstCaseMethod(c, source);
            // m.setClassDefinition(c);
            // m.setEnvironment(source);
            // adaptor.addMethod(m);
            // }
            
            switch (source.classToType.get(c))
              {
              
                case Custom:
                  break;
                case Production:
                case SubProduction:
                  {
                    AnalysisAdaptorDefaultMethod mIn = new AnalysisAdaptorDefaultMethod(
                        c);
                    mIn.setDefaultPostfix("In");
                    mIn.setClassDefinition(c);
           //         mIn.setEnvironment(source);
                    adaptor.addMethod(mIn);
                    
                    AnalysisAdaptorDefaultMethod mOut = new AnalysisAdaptorDefaultMethod(
                        c);
                    mOut.setDefaultPostfix("Out");
                    mOut.setClassDefinition(c);
             //       mOut.setEnvironment(source);
                    adaptor.addMethod(mOut);
                  }
                  break;
                case Alternative:
                case Token:
                  {
                    // continue;
                    Method m = new DepthFirstCaseMethod(c, queue);
                    m.setClassDefinition(c);
                    // m.setEnvironment(source);
                    adaptor.addMethod(m);
                  }
                  break;
                case Unknown:
                  break;
              
              }
            
            AnalysisAdaptorCaseMethod mIn = new AnalysisAdaptorCaseMethod(c);
            mIn.setMethodNamePrefix("in");
            mIn.setDefaultPostfix("In");
            mIn.setClassDefinition(c);
          //  mIn.setEnvironment(source);
            adaptor.addMethod(mIn);
            
            AnalysisAdaptorCaseMethod mOut = new AnalysisAdaptorCaseMethod(c);
            mOut.setMethodNamePrefix("out");
            mOut.setDefaultPostfix("Out");
            mOut.setClassDefinition(c);
//            mOut.setEnvironment(source);
            adaptor.addMethod(mOut);
            
          }
          
          {
            AnalysisAdaptorDefaultNodeMethod mOut = new AnalysisAdaptorDefaultNodeMethod();
            mOut.setDefaultPostfix("Out");
  //          mOut.setEnvironment(source);
            adaptor.addMethod(mOut);
            
            AnalysisAdaptorDefaultNodeMethod mIn = new AnalysisAdaptorDefaultNodeMethod();
            mIn.setDefaultPostfix("In");
    //        mIn.setEnvironment(source);
            adaptor.addMethod(mIn);
          }
          
          {
            AnalysisAdaptorDefaultTokenMethod mOut = new AnalysisAdaptorDefaultTokenMethod();
            mOut.setDefaultPostfix("Out");
      //      mOut.setEnvironment(source);
            adaptor.addMethod(mOut);
            
            AnalysisAdaptorDefaultTokenMethod mIn = new AnalysisAdaptorDefaultTokenMethod();
            mIn.setDefaultPostfix("In");
        //    mIn.setEnvironment(source);
            adaptor.addMethod(mIn);
          }
        
        // FIXME adaptor.getImports().addAll(source.getAllDefinitions());
      }
  }
