package com.lausdahl.ast.creator;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.lausdahl.ast.creator.definitions.EnumDefinition;
import com.lausdahl.ast.creator.definitions.Field;
import com.lausdahl.ast.creator.definitions.IInterfaceDefinition;
import com.lausdahl.ast.creator.definitions.Field.AccessSpecifier;
import com.lausdahl.ast.creator.definitions.GenericArgumentedIInterfceDefinition;
import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.definitions.IClassDefinition.ClassType;
import com.lausdahl.ast.creator.definitions.InterfaceDefinition;
import com.lausdahl.ast.creator.definitions.PredefinedClassDefinition;
import com.lausdahl.ast.creator.env.BaseEnvironment;
import com.lausdahl.ast.creator.env.Environment;
import com.lausdahl.ast.creator.java.definitions.JavaName;
import com.lausdahl.ast.creator.methods.Method;
import com.lausdahl.ast.creator.methods.SetMethod;
import com.lausdahl.ast.creator.methods.analysis.depthfirst.AnalysisDepthFirstAdaptorCaseMethod;
import com.lausdahl.ast.creator.methods.visitors.AnalysisAcceptMethod;
import com.lausdahl.ast.creator.methods.visitors.AnswerAcceptMethod;
import com.lausdahl.ast.creator.methods.visitors.QuestionAcceptMethod;
import com.lausdahl.ast.creator.methods.visitors.QuestionAnswerAcceptMethod;
import com.lausdahl.ast.creator.methods.visitors.adaptor.analysis.AnalysisAdaptorCaseMethod;
import com.lausdahl.ast.creator.methods.visitors.adaptor.analysis.AnalysisAdaptorDefaultMethod;
import com.lausdahl.ast.creator.methods.visitors.adaptor.analysis.AnalysisAdaptorDefaultNodeMethod;
import com.lausdahl.ast.creator.methods.visitors.adaptor.analysis.AnalysisAdaptorDefaultTokenMethod;
import com.lausdahl.ast.creator.methods.visitors.adaptor.analysis.AnalysisMethodTemplate;
import com.lausdahl.ast.creator.methods.visitors.adaptor.analysis.CreateNewReturnValueMethod;
import com.lausdahl.ast.creator.methods.visitors.adaptor.analysis.MergeReturnMethod;
import com.lausdahl.ast.creator.methods.visitors.adaptor.answer.AnswerAdaptorCaseMethod;
import com.lausdahl.ast.creator.methods.visitors.adaptor.answer.AnswerAdaptorDefaultMethod;
import com.lausdahl.ast.creator.methods.visitors.adaptor.answer.AnswerAdaptorDefaultNodeMethod;
import com.lausdahl.ast.creator.methods.visitors.adaptor.answer.AnswerAdaptorDefaultTokenMethod;
import com.lausdahl.ast.creator.methods.visitors.adaptor.answer.AnswerDepthFirstAdaptorCaseMethod;
import com.lausdahl.ast.creator.methods.visitors.adaptor.question.QuestionAdaptorCaseMethod;
import com.lausdahl.ast.creator.methods.visitors.adaptor.question.QuestionAdaptorDefaultMethod;
import com.lausdahl.ast.creator.methods.visitors.adaptor.question.QuestionAdaptorDefaultNodeMethod;
import com.lausdahl.ast.creator.methods.visitors.adaptor.question.QuestionAdaptorDefaultTokenMethod;
import com.lausdahl.ast.creator.methods.visitors.adaptor.question.QuestionDepthFirstAdaptorCaseMethod;
import com.lausdahl.ast.creator.methods.visitors.adaptor.questionanswer.QuestionAnswerAdaptorCaseMethod;
import com.lausdahl.ast.creator.methods.visitors.adaptor.questionanswer.QuestionAnswerAdaptorDefaultMethod;
import com.lausdahl.ast.creator.methods.visitors.adaptor.questionanswer.QuestionAnswerAdaptorDefaultNodeMethod;
import com.lausdahl.ast.creator.methods.visitors.adaptor.questionanswer.QuestionAnswerAdaptorDefaultTokenMethod;
import com.lausdahl.ast.creator.methods.visitors.adaptor.questionanswer.QuestionAnswerDepthFirstAdaptorCaseMethod;
import com.lausdahl.ast.creator.utils.ClassFactory;
import com.lausdahl.ast.creator.utils.EnumUtil;

public class Generator {

	private boolean isBase = false;
	
	public Environment generate(InputStream toStringFile,
			InputStream inputFile, String envName, boolean doTypeHierarchyCheck, boolean isBase)
			throws IOException, AstCreatorException {
		Environment env = null;
		this.isBase = isBase;
		try {
			env = new CreateOnParse().parse(inputFile, envName);
			for(IInterfaceDefinition def : env.getAllDefinitions()) 
				def.setIsBaseTree(isBase);
			
			if (doTypeHierarchyCheck)
				for (IClassDefinition def : env.getClasses()) {
					def.checkFieldTypeHierarchy(env);
					
				}
		} catch (AstCreatorException e) {
			if (e.fatal) {
				throw e;
			} else {
				System.err.println(e.getMessage());
			}
		}

		if (toStringFile != null) {
			System.out.println("Generating toString add on...");
			try {
				ToStringAddOnReader reader = new ToStringAddOnReader();
				reader.readAndAdd(toStringFile, env);
			} catch (AstCreatorException e) {
				if (e.fatal) {
					throw e;
				} else {
					System.err.println(e.getMessage());
				}
			}
		}
		// SourceFileWriter.write(outputFolder, env1);
		return env;
	}

	public void runPostGeneration(Environment env, boolean isBaseTree)
			throws InstantiationException, IllegalAccessException {
		System.out.println("Generating enumerations...");
		createNodeEnum(env);
		createProductionEnums(env,isBaseTree);

		// System.out.println("Generating interfaces for nodes");
		// createInterfacesForNodePoints(env);

		System.out.println("Generating analysis visitors...");
		System.out.print("Analysis...");
		createAnalysis(env,isBaseTree);
		// createAnalysisAdaptor(env,analysisPackageName);
		System.out.print("Answer...");
		createAnswer(env,isBaseTree);
		System.out.print("Question...");
		createQuestion(env,isBaseTree);
		System.out.print("Question-Answer...");
		createQuestionAnswer(env,isBaseTree);
		System.out.print("Depth-First...");
		// createdepthFirstAdaptor(env);
		DepthFirstGeneratorConfig dconfig = new DepthFirstGeneratorConfig();
		dconfig.interfaceTag = env.TAG_IAnalysis;
		dconfig.type = "";
		dconfig.genericArguments = new Vector<String>();
		dconfig.defaultMethod = AnalysisAdaptorDefaultMethod.class;
		dconfig.depthfirstCase = AnalysisDepthFirstAdaptorCaseMethod.class;
		dconfig.caseM = AnalysisAdaptorCaseMethod.class;
		dconfig.defaultNode = AnalysisAdaptorDefaultNodeMethod.class;
		dconfig.defaultToken = AnalysisAdaptorDefaultTokenMethod.class;
		createdepthFirstAdaptor(env, dconfig,isBaseTree);

		dconfig.interfaceTag = env.TAG_IAnswer;
		dconfig.type = "Answer";
		dconfig.genericArguments = Arrays.asList(new String[] { "A" });
		dconfig.defaultMethod = AnswerAdaptorDefaultMethod.class;
		dconfig.depthfirstCase = AnswerDepthFirstAdaptorCaseMethod.class;
		dconfig.caseM = AnswerAdaptorCaseMethod.class;
		dconfig.defaultNode = AnswerAdaptorDefaultNodeMethod.class;
		dconfig.defaultToken = AnswerAdaptorDefaultTokenMethod.class;
		dconfig.returnType="A";
		createdepthFirstAdaptor(env, dconfig,isBaseTree);

		dconfig.interfaceTag = env.TAG_IQuestion;
		dconfig.type = "Question";
		dconfig.genericArguments = Arrays.asList(new String[] { "Q" });
		dconfig.defaultMethod = QuestionAdaptorDefaultMethod.class;
		dconfig.depthfirstCase = QuestionDepthFirstAdaptorCaseMethod.class;
		dconfig.caseM = QuestionAdaptorCaseMethod.class;
		dconfig.defaultNode = QuestionAdaptorDefaultNodeMethod.class;
		dconfig.defaultToken = QuestionAdaptorDefaultTokenMethod.class;
		dconfig.returnType=null;
		createdepthFirstAdaptor(env, dconfig,isBaseTree);

		dconfig.interfaceTag = env.TAG_IQuestionAnswer;
		dconfig.type = "QuestionAnswer";
		dconfig.genericArguments = Arrays.asList(new String[] { "Q", "A" });
		dconfig.defaultMethod = QuestionAnswerAdaptorDefaultMethod.class;
		dconfig.depthfirstCase = QuestionAnswerDepthFirstAdaptorCaseMethod.class;
		dconfig.caseM = QuestionAnswerAdaptorCaseMethod.class;
		dconfig.defaultNode = QuestionAnswerAdaptorDefaultNodeMethod.class;
		dconfig.defaultToken = QuestionAnswerAdaptorDefaultTokenMethod.class;
		dconfig.returnType="A";
		createdepthFirstAdaptor(env, dconfig,isBaseTree);

		System.out.println();

		// TODO test
		// createQuestionAnswerDepthFirst(env);
	}

	public void createInterfacesForNodePoints(Environment env) {
		Set<IClassDefinition> classes = new HashSet<IClassDefinition>();
		for (IClassDefinition c : env.getClasses()) {
			createInterfacesForNodePoints(env, classes, c);
		}
	}

	private Set<IClassDefinition> createInterfacesForNodePoints(
			Environment env, Set<IClassDefinition> processedClasses,
			IClassDefinition c) {
		if (processedClasses.contains(c)) {
			return processedClasses;
		}

		processedClasses.add(c);

		if (env.isTreeNode(c)) {
			// CommonTreeClassDefinition ct = (CommonTreeClassDefinition) c;
			switch (env.classToType.get(c)) {
			case Alternative:
				break;
			case Custom:
				break;
			case Production:
			case SubProduction:
				processedClasses.addAll(createInterfacesForNodePoints(env,
						processedClasses, c.getSuperDef()));
				InterfaceDefinition intf = new InterfaceDefinition(c.getName()
						.clone(), env.getAstPackage());
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

	private static void createNodeEnum(Environment env) {
		EnumDefinition eDef = new EnumDefinition(new JavaName(
				env.getTemplateDefaultPackage(), "NodeEnum"),
				env.getAstPackage());
		env.addClass(eDef);
		eDef.elements.add("TOKEN");
		eDef.elements.add("ExternalDefined");
		for (IClassDefinition d : env.getClasses()) {
			if (env.isTreeNode(d)) {
				if (env.classToType.get(d) == ClassType.Production) {
					eDef.elements.add(EnumUtil.getEnumElementName(d));
				}
			}
			
		}
	}

	private static void createProductionEnums(Environment env, boolean isBaseTree) {
		List<EnumDefinition> enums = new Vector<EnumDefinition>();

		for (IClassDefinition d : env.getClasses()) {
			if (env.isTreeNode(d)) {
				// CommonTreeClassDefinition c = (CommonTreeClassDefinition) d;
				switch (env.classToType.get(d)) {
				case Production:
				case SubProduction: {
					EnumDefinition eDef = new EnumDefinition(new JavaName(d
							.getName().getPackageName(), "",
							EnumUtil.getEnumTypeNameNoPostfix(d, env), ""),
							env.getAstPackage());
					if (d.isExtTree()) {
						eDef.setIsBaseTree(false);
					} else {d.setIsBaseTree(isBaseTree);}
					
					enums.add(eDef);

					for (IClassDefinition sub : getClasses(
							env.getSubClasses(d), env)) {
						eDef.elements.add(EnumUtil.getEnumElementName(sub));
					}
				}
					break;
				default:
					break;
				}

			}
		}

		for (EnumDefinition enumDefinition : enums) {
			env.addClass(enumDefinition);
		}
	}

	private static void createAnalysis(Environment env, boolean isBaseTree)
			throws InstantiationException, IllegalAccessException {

		extendedVisitor(isBaseTree,"Analysis", new Vector<String>(),
				AnalysisAcceptMethod.class, AnalysisAdaptorCaseMethod.class,
				AnalysisAdaptorDefaultMethod.class,
				AnalysisAdaptorDefaultNodeMethod.class,
				AnalysisAdaptorDefaultTokenMethod.class, env, env.TAG_IAnalysis);
	}

	private static void createAnswer(Environment env, boolean isBaseTree)
			throws InstantiationException, IllegalAccessException {
		List<String> genericArguments = new Vector<String>();
		genericArguments.add("A");
		extendedVisitor(isBaseTree,"Answer", genericArguments, AnswerAcceptMethod.class,
				AnswerAdaptorCaseMethod.class,
				AnswerAdaptorDefaultMethod.class,
				AnswerAdaptorDefaultNodeMethod.class,
				AnswerAdaptorDefaultTokenMethod.class, env, env.TAG_IAnswer);
	}

	private static void createQuestion(Environment env, boolean isBaseTree)
			throws InstantiationException, IllegalAccessException {
		List<String> genericArguments = new Vector<String>();
		genericArguments.add("Q");
		extendedVisitor(isBaseTree,"Question", genericArguments,
				QuestionAcceptMethod.class, QuestionAdaptorCaseMethod.class,
				QuestionAdaptorDefaultMethod.class,
				QuestionAdaptorDefaultNodeMethod.class,
				QuestionAdaptorDefaultTokenMethod.class, env, env.TAG_IQuestion);
	}

	private static void createQuestionAnswer(Environment env, boolean isBaseTree)
			throws InstantiationException, IllegalAccessException {
		List<String> genericArguments = new Vector<String>();
		genericArguments.add("Q");
		genericArguments.add("A");
		extendedVisitor(isBaseTree,"QuestionAnswer", genericArguments,
				QuestionAnswerAcceptMethod.class,
				QuestionAnswerAdaptorCaseMethod.class,
				QuestionAnswerAdaptorDefaultMethod.class,
				QuestionAnswerAdaptorDefaultNodeMethod.class,
				QuestionAnswerAdaptorDefaultTokenMethod.class, env,
				env.TAG_IQuestionAnswer);
	}

	public static void extendedVisitor(boolean isBaseTree, String intfName,
			List<String> genericArguments, Class<? extends Method> accept,
			Class<? extends Method> caseM, Class<? extends Method> defaultCase,
			Class<? extends Method> defaultNodeMethod,
			Class<? extends Method> defaultTokenMethod, Environment env,
                                           String tag) throws InstantiationException, IllegalAccessException 
    {
        InterfaceDefinition answerIntf = new InterfaceDefinition(new JavaName(
                                                                              env.getTemplateAnalysisPackage() + ".intf", "I" + intfName),
                                                                 env.getAstPackage());
        answerIntf.setIsBaseTree(isBaseTree);
            answerIntf.setTag(tag);
            answerIntf.setGenericArguments(genericArguments);
            env.addInterface(answerIntf);
            answerIntf.supers.add(BaseEnvironment.serializableDef);

            for (IClassDefinition c : getClasses(env.getClasses(), env)) {
                switch (env.classToType.get(c)) {
                case Alternative:
                case Token: {
                    Method m = Method.newMethod(accept,c);
                    c.addMethod(m);
                        
                    m = Method.newMethod(caseM, c);
                    answerIntf.methods.add(m);
                    break;
                }
                case Production:
                case SubProduction: {
                    break;
                } //case SubProduction
    			default:
    				break;
                }// switch
            } // for IClassDefinition c

            IClassDefinition answerClass = 
                ClassFactory.createCustom(new JavaName(
                                                       env.getTemplateAnalysisPackage(), 
                                                       intfName + "Adaptor"), env);
            answerClass.setIsBaseTree(isBaseTree);
            answerClass.setGenericArguments(answerIntf.getGenericArguments());
            answerClass.addInterface(answerIntf);

		for (IClassDefinition c : env.getClasses()) {
			if (env.isTreeNode(c)) {
				switch (env.classToType.get(c)) {
				case Alternative:
				case Token: {
                                    Method m =
                                        Method.newMethod(caseM,c);
                                    answerClass.addMethod(m);
				}
                                    break;
				case SubProduction:
				case Production: {
                                    Method m = Method.newMethod(defaultCase, c);
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

            Method m = Method.newMethod(defaultNodeMethod, null);
            answerClass.addMethod(m);
            m = Method.newMethod(defaultTokenMethod,null);
            answerClass.addMethod(m);
	}

	public static List<IClassDefinition> getClasses(
			List<IClassDefinition> classList, Environment env) {
		List<IClassDefinition> classes = new Vector<IClassDefinition>();
		for (IClassDefinition c : classList) {
			if (env.isTreeNode(c)) {
				classes.add(c);
			}
		}
		return classes;
	}

	private static class DepthFirstGeneratorConfig
	{
		public String interfaceTag;
		public String type;
		public List<String> genericArguments;
		public Class<? extends Method> defaultMethod;
		public Class<? extends Method> depthfirstCase;
		public Class<? extends Method> caseM;
		public Class<? extends Method> defaultNode;
		public Class<? extends Method> defaultToken;
		public String returnType = null;
	}

	private void createdepthFirstAdaptor(Environment source,
			final DepthFirstGeneratorConfig config, boolean isBaseTree)
			throws InstantiationException, IllegalAccessException
	{
		IClassDefinition adaptor = ClassFactory.createCustom(new JavaName(
				source.getTemplateAnalysisPackage(),
				"DepthFirstAnalysisAdaptor" + config.type ), source);
		adaptor.setIsBaseTree(isBaseTree);
                // adaptor.addInterface(source.getTaggedDef(source.TAG_IAnalysis));
		adaptor.addInterface(source.getTaggedDef(config.interfaceTag));
		adaptor.setGenericArguments(config.genericArguments);
		Field queue = new Field();
		queue.name = "visitedNodes";
		queue.accessspecifier = AccessSpecifier.Protected;
		queue.type = new GenericArgumentedIInterfceDefinition(BaseEnvironment.setDef, source.iNode.getName().getName());
		// TODO queue.setCustomInitializer("new java.util.LinkedList<"+source.iNode.getName().getName()+">()");
		queue.setCustomInitializer("new java.util.HashSet<"
				+ source.iNode.getName().getName() + ">()");
		adaptor.addField(queue);
		((InterfaceDefinition) adaptor).imports.add(queue.type);
		adaptor.addMethod(new SetMethod(adaptor, queue));
		adaptor.setAnnotation("@SuppressWarnings({\"rawtypes\",\"unchecked\"})");

		for (IClassDefinition c : Generator.getClasses(source.getClasses(),
				source)) {
			switch (source.classToType.get(c)) {

			case Custom:
				break;
			case Production:
			case SubProduction: {
                            AnalysisMethodTemplate mIn = (AnalysisMethodTemplate)
                                Method.newMethod(config.defaultMethod, c);
                            mIn.setDefaultPostfix("In");
                            adaptor.addMethod(mIn);
                            AnalysisMethodTemplate  mOut = (AnalysisMethodTemplate) 
                                Method.newMethod(config.defaultMethod,c);
				mOut.setDefaultPostfix("Out");
				adaptor.addMethod(mOut);
			}
                            break;
			case Alternative:
			case Token: {
                            AnalysisDepthFirstAdaptorCaseMethod m
                                = (AnalysisDepthFirstAdaptorCaseMethod)
                                Method.newMethod(config.depthfirstCase,c);
				m.setVisitedNodesField(queue);
                            adaptor.addMethod(m);
			}
                            break;
			case Unknown:
                            break;
			}

			AnalysisAdaptorCaseMethod mIn = 
                            (AnalysisAdaptorCaseMethod)
                            Method.newMethod(config.caseM,c);
			mIn.setMethodNamePrefix("in");
			mIn.setDefaultPostfix("In");
			adaptor.addMethod(mIn);

                            AnalysisAdaptorCaseMethod mOut = 
                                (AnalysisAdaptorCaseMethod)
                                Method.newMethod(config.caseM,c);
			mOut.setMethodNamePrefix("out");
			mOut.setDefaultPostfix("Out");
			adaptor.addMethod(mOut);
		}
		{
                    AnalysisAdaptorDefaultNodeMethod mOut = 
                        (AnalysisAdaptorDefaultNodeMethod)
                        Method.newMethod(config.defaultNode, null);
                    mOut.setDefaultPostfix("Out");
                    adaptor.addMethod(mOut);
                    
                    AnalysisAdaptorDefaultNodeMethod mIn = 
                        (AnalysisAdaptorDefaultNodeMethod)
                        Method.newMethod(config.defaultNode, null);
                    mIn.setDefaultPostfix("In");
                    adaptor.addMethod(mIn);
		}

		{
                    AnalysisAdaptorDefaultTokenMethod mOut = 
                        (AnalysisAdaptorDefaultTokenMethod)
                        Method.newMethod(config.defaultToken, null);
                    mOut.setDefaultPostfix("Out");
                    adaptor.addMethod(mOut);

                    AnalysisAdaptorDefaultTokenMethod mIn = 
                        (AnalysisAdaptorDefaultTokenMethod)
                        Method.newMethod(config.defaultToken, null);
                    mIn.setDefaultPostfix("In");
                    adaptor.addMethod(mIn);
		}

		if(config.returnType!=null)
		{
                    adaptor.addMethod(new MergeReturnMethod(config.returnType));
                    adaptor.addMethod(new CreateNewReturnValueMethod(source.iNode, config.returnType,config.genericArguments.size()>1));
                    adaptor.addMethod(new CreateNewReturnValueMethod(new PredefinedClassDefinition("","Object"), config.returnType,config.genericArguments.size()>1));
                    adaptor.setAbstract(true);
                }
                // FIXME adaptor.getImports().addAll(source.getAllDefinitions());
        }
}
