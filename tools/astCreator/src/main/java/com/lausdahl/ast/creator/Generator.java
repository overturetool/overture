package com.lausdahl.ast.creator;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import com.lausdahl.ast.creator.definitions.CommonTreeClassDefinition;
import com.lausdahl.ast.creator.definitions.CustomClassDefinition;
import com.lausdahl.ast.creator.definitions.EnumDefinition;
import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.definitions.IClassDefinition.ClassType;
import com.lausdahl.ast.creator.definitions.IInterfaceDefinition;
import com.lausdahl.ast.creator.definitions.InterfaceDefinition;
import com.lausdahl.ast.creator.methods.Method;
import com.lausdahl.ast.creator.methods.analysis.AnalysisAcceptMethod;
import com.lausdahl.ast.creator.methods.analysis.AnswerAcceptMethod;
import com.lausdahl.ast.creator.methods.analysis.QuestionAcceptMethod;
import com.lausdahl.ast.creator.methods.analysis.QuestionAnswerAcceptMethod;
import com.lausdahl.ast.creator.methods.analysis.adopter.AnalysisAdaptorCaseMethod;
import com.lausdahl.ast.creator.methods.analysis.adopter.AnalysisAdaptorDefaultMethod;
import com.lausdahl.ast.creator.methods.analysis.adopter.AnalysisAdaptorDefaultNodeMethod;
import com.lausdahl.ast.creator.methods.analysis.adopter.AnalysisAdaptorDefaultTokenMethod;
import com.lausdahl.ast.creator.methods.analysis.adopter.AnswerAdaptorCaseMethod;
import com.lausdahl.ast.creator.methods.analysis.adopter.AnswerAdaptorDefaultMethod;
import com.lausdahl.ast.creator.methods.analysis.adopter.AnswerAdaptorDefaultNodeMethod;
import com.lausdahl.ast.creator.methods.analysis.adopter.AnswerAdaptorDefaultTokenMethod;
import com.lausdahl.ast.creator.methods.analysis.adopter.QuestionAdaptorCaseMethod;
import com.lausdahl.ast.creator.methods.analysis.adopter.QuestionAdaptorDefaultMethod;
import com.lausdahl.ast.creator.methods.analysis.adopter.QuestionAdaptorDefaultNodeMethod;
import com.lausdahl.ast.creator.methods.analysis.adopter.QuestionAdaptorDefaultTokenMethod;
import com.lausdahl.ast.creator.methods.analysis.adopter.QuestionAnswerAdaptorCaseMethod;
import com.lausdahl.ast.creator.methods.analysis.adopter.QuestionAnswerAdaptorDefaultMethod;
import com.lausdahl.ast.creator.methods.analysis.adopter.QuestionAnswerAdaptorDefaultNodeMethod;
import com.lausdahl.ast.creator.methods.analysis.adopter.QuestionAnswerAdaptorDefaultTokenMethod;
import com.lausdahl.ast.creator.methods.analysis.depthfirst.DepthFirstCaseMethod;

public class Generator
{

	public Environment generate(String inputFile, String defaultPackage,
			String analysisPackageName) throws IOException,
			InstantiationException, IllegalAccessException
	{
		Environment env = new CreateOnParse().parse(inputFile, defaultPackage);
		
		new ToStringAddOnReader().readAndAdd(inputFile+".tostring", env);

		System.out.println("Generating enumerations...");
		createNodeEnum(env, defaultPackage);
		createProductionEnums(env, defaultPackage);

		System.out.println("Generating analysis visitors...");
		System.out.print("Analysis...");
		createAnalysis(env, analysisPackageName);
		// createAnalysisAdaptor(env,analysisPackageName);
		System.out.print("Answer...");
		createAnswer(env, analysisPackageName);
		System.out.print("Question...");
		createQuestion(env, analysisPackageName);
		System.out.print("Question-Answer...");
		createQuestionAnswer(env, analysisPackageName);
		System.out.print("Depth-First...");
		createdepthFirstAdaptor(env, analysisPackageName);
		System.out.println();
		// SourceFileWriter.write(outputFolder, env1);
		return env;
	}

	private static void createNodeEnum(Environment env, String packageName)
	{
		EnumDefinition eDef = new EnumDefinition("NodeEnum");
		eDef.setPackageName(packageName);
		env.addClass(eDef);
		eDef.elements.add("TOKEN");
		for (IClassDefinition d : env.getClasses())
		{
			if (d instanceof CommonTreeClassDefinition)
			{
				CommonTreeClassDefinition c = (CommonTreeClassDefinition) d;

				if (c.getType() == ClassType.Production)
				{
					eDef.elements.add(c.getEnumName());
				}
			}
		}
	}

	private static void createProductionEnums(Environment env,
			String packageName)
	{
		List<EnumDefinition> enums = new Vector<EnumDefinition>();

		for (IClassDefinition d : env.getClasses())
		{
			if (d instanceof CommonTreeClassDefinition)
			{
				CommonTreeClassDefinition c = (CommonTreeClassDefinition) d;
				if (c.getType() == ClassType.Production)
				{
					EnumDefinition eDef = new EnumDefinition(c.getEnumTypeName());
					eDef.setPackageName(c.getPackageName());
					enums.add(eDef);

					for (CommonTreeClassDefinition sub : getClasses(env.getSubClasses(c)))
					{
						eDef.elements.add(sub.getEnumName());
					}
				} else if (c.getType() == ClassType.SubProduction)
				{
					EnumDefinition eDef = new EnumDefinition(c.getEnumTypeName());
					eDef.setPackageName(c.getPackageName());
					enums.add(eDef);

					for (CommonTreeClassDefinition sub : getClasses(env.getSubClasses(c)))
					{
						eDef.elements.add(sub.getEnumName());
					}
				}
			}
		}

		for (EnumDefinition enumDefinition : enums)
		{
			env.addClass(enumDefinition);
		}
	}

	private static void createAnalysis(Environment env,
			String analysisPackageName) throws InstantiationException,
			IllegalAccessException
	{

		extendedVisitor("Analysis", new Vector<IInterfaceDefinition>(), AnalysisAcceptMethod.class, AnalysisAdaptorCaseMethod.class, AnalysisAdaptorDefaultMethod.class, AnalysisAdaptorDefaultNodeMethod.class, AnalysisAdaptorDefaultTokenMethod.class, env, analysisPackageName, env.TAG_IAnalysis);
	}

	private static void createAnswer(Environment env, String analysisPackageName)
			throws InstantiationException, IllegalAccessException
	{
		List<IInterfaceDefinition> genericArguments = new Vector<IInterfaceDefinition>();
		genericArguments.add(Environment.A);
		extendedVisitor("Answer", genericArguments, AnswerAcceptMethod.class, AnswerAdaptorCaseMethod.class, AnswerAdaptorDefaultMethod.class,AnswerAdaptorDefaultNodeMethod.class, AnswerAdaptorDefaultTokenMethod.class, env, analysisPackageName, env.TAG_IAnswer);
	}

	private static void createQuestion(Environment env,
			String analysisPackageName) throws InstantiationException,
			IllegalAccessException
	{
		List<IInterfaceDefinition> genericArguments = new Vector<IInterfaceDefinition>();
		genericArguments.add(Environment.Q);
		extendedVisitor("Question", genericArguments, QuestionAcceptMethod.class, QuestionAdaptorCaseMethod.class, QuestionAdaptorDefaultMethod.class,QuestionAdaptorDefaultNodeMethod.class, QuestionAdaptorDefaultTokenMethod.class, env, analysisPackageName, env.TAG_IQuestion);
	}

	private static void createQuestionAnswer(Environment env,
			String analysisPackageName) throws InstantiationException,
			IllegalAccessException
	{
		List<IInterfaceDefinition> genericArguments = new Vector<IInterfaceDefinition>();
		genericArguments.add(Environment.Q);
		genericArguments.add(Environment.A);
		extendedVisitor("QuestionAnswer", genericArguments, QuestionAnswerAcceptMethod.class, QuestionAnswerAdaptorCaseMethod.class, QuestionAnswerAdaptorDefaultMethod.class,QuestionAnswerAdaptorDefaultNodeMethod.class, QuestionAnswerAdaptorDefaultTokenMethod.class, env, analysisPackageName, env.TAG_IQuestionAnswer);
	}

	@SuppressWarnings("rawtypes")
	public static void extendedVisitor(String intfName,
			List<IInterfaceDefinition> genericArguments, Class accept,
			Class caseM, Class defaultCase, Class defaultNodeMethod,
			Class defaultTokenMethod, Environment env,
			String analysisPackageName, String tag)
			throws InstantiationException, IllegalAccessException
	{
		InterfaceDefinition answerIntf = new InterfaceDefinition(intfName);
		answerIntf.setTag(tag);
		answerIntf.setPackageName(analysisPackageName);
		answerIntf.setGenericArguments(genericArguments.toArray(new IInterfaceDefinition[0]));
		env.addInterface(answerIntf);

		for (CommonTreeClassDefinition c : getClasses(env.getClasses()))
		{
			if (c.getType() == IClassDefinition.ClassType.Production)
			{
				continue;
			}
			Method m = (Method) accept.newInstance();
			m.setClassDefinition(c);
			m.setEnvironment(env);
			c.methods.add(m);

			m = (Method) caseM.newInstance();
			m.setClassDefinition(c);
			m.setEnvironment(env);
			answerIntf.methods.add(m);

		}

		CustomClassDefinition answerClass = new CustomClassDefinition(answerIntf.getSignatureName().substring(1)
				+ "Adaptor", env);
		answerClass.setPackageName(analysisPackageName);
		answerClass.setGenericArguments(answerIntf.getGenericArguments());
		answerClass.interfaces.add(answerIntf);
		// answerClass.methods.addAll(answerIntf.methods);

		for (IClassDefinition c : env.getClasses())
		{
			if (c instanceof CommonTreeClassDefinition)
			{
				CommonTreeClassDefinition cd = (CommonTreeClassDefinition) c;
				switch (cd.getType())
				{
					case Alternative:
					case Token:
					{
						Method m = (Method) caseM.newInstance();
						m.setClassDefinition(c);
						m.setEnvironment(env);
						answerClass.methods.add(m);
					}
						break;
					case SubProduction:
					{
						Method m = (Method) caseM.newInstance();
						m.setClassDefinition(c);
						m.setEnvironment(env);
						answerClass.methods.add(m);
					}
					case Production:
					//case SubProduction:
					{
						// answerClass.methods.add(new AnalysisAdaptorDefaultMethod(c,env));
						Method m = (Method) defaultCase.newInstance();
						m.setClassDefinition(c);
						m.setEnvironment(env);
						answerClass.methods.add(m);
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
		m.setEnvironment(env);
		answerClass.methods.add(m);

		m = (Method) defaultTokenMethod.newInstance();
		m.setEnvironment(env);
		answerClass.methods.add(m);
		//
		// answerClass.methods.add(new AnalysisAdaptorDefaultNodeMethod(env));
		// answerClass.methods.add(new AnalysisAdaptorDefaultTokenMethod(env));

		env.addClass(answerClass);
	}

	public static List<CommonTreeClassDefinition> getClasses(
			List<IClassDefinition> classList)
	{
		List<CommonTreeClassDefinition> classes = new Vector<CommonTreeClassDefinition>();
		for (IClassDefinition c : classList)
		{
			if (c instanceof CommonTreeClassDefinition)
			{
				classes.add((CommonTreeClassDefinition) c);
			}
		}
		return classes;
	}

//	private void createAnalysisAdaptor(Environment env,
//			String analysisPackageName)
//	{
//		CustomClassDefinition answerClass = new CustomClassDefinition("AnalysisAdaptor", env);
//		answerClass.setPackageName(analysisPackageName);
//		answerClass.interfaces.add(env.getTaggedDef(env.TAG_IAnalysis));
//		answerClass.methods.add(new AnalysisAdaptorDefaultNodeMethod(env));
//		answerClass.methods.add(new AnalysisAdaptorDefaultTokenMethod(env));
//
//		env.addClass(answerClass);
//		for (IClassDefinition c : env.getClasses())
//		{
//			if (c instanceof CommonTreeClassDefinition)
//			{
//				CommonTreeClassDefinition cd = (CommonTreeClassDefinition) c;
//				switch (cd.getType())
//				{
//					case Alternative:
//					case Token:
//						answerClass.methods.add(new AnalysisAdaptorCaseMethod(c, env));
//						break;
//
//					case Production:
//					case SubProduction:
//						answerClass.methods.add(new AnalysisAdaptorDefaultMethod(c, env));
//						break;
//
//					case Custom:
//						break;
//					case Unknown:
//						break;
//
//				}
//			}
//		}
//	}
	private void createdepthFirstAdaptor(Environment source, String defaultPackage) 
	{
		CustomClassDefinition adaptor = new CustomClassDefinition("DepthFirstAnalysisAdaptor", source);
		adaptor.setAnnotation("@SuppressWarnings(\"unused\")");
		adaptor.setPackageName(defaultPackage);
//		copyAdaptor.interfaces.add(source.getTaggedDef(destination.TAG_IAnswer).getSignatureName()+"<"+destination.node.getSignatureName()+">");
		adaptor.interfaces.add(source.getTaggedDef(source.TAG_IAnalysis));

		for (CommonTreeClassDefinition c : Generator.getClasses(source.getClasses()))
		{
			if (c.getType() == IClassDefinition.ClassType.Production /*|| c.getType()==ClassType.SubProduction*/)
			{
				continue;
			}
			
			Method m = new DepthFirstCaseMethod(c, source);
			m.setClassDefinition(c);
			m.setEnvironment(source);
			adaptor.methods.add(m);

		}
		
//		copyAdaptor.methods.add(new CopyNode2ExtendedNodeListHelper(source,destination));
		adaptor.imports.addAll(source.getAllDefinitions());
//		copyAdaptor.imports.addAll(destination.getAllDefinitions());
		
		source.addClass(adaptor);
	}
}
