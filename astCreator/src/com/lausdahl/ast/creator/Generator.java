package com.lausdahl.ast.creator;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import com.lausdahl.ast.creator.definitions.CommonTreeClassDefinition;
import com.lausdahl.ast.creator.definitions.CustomClassDefinition;
import com.lausdahl.ast.creator.definitions.EnumDefinition;
import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.definitions.IClassDefinition.ClassType;
import com.lausdahl.ast.creator.definitions.InterfaceDefinition;
import com.lausdahl.ast.creator.methods.Method;
import com.lausdahl.ast.creator.methods.analysis.AnalysisAcceptMethod;
import com.lausdahl.ast.creator.methods.analysis.AnalysisCaseMethod;
import com.lausdahl.ast.creator.methods.analysis.AnswerAcceptMethod;
import com.lausdahl.ast.creator.methods.analysis.AnswerCaseMethod;
import com.lausdahl.ast.creator.methods.analysis.QuestionAcceptMethod;
import com.lausdahl.ast.creator.methods.analysis.QuestionAnswerAcceptMethod;
import com.lausdahl.ast.creator.methods.analysis.QuestionAnswerCaseMethod;
import com.lausdahl.ast.creator.methods.analysis.QuestionCaseMethod;

public class Generator
{
	

	public Environment generate(String inputFile,String defaultPackage, String analysisPackageName) throws IOException,
			InstantiationException, IllegalAccessException
	{
		Environment env = new CreateOnParse().parse(inputFile,defaultPackage);

		createNodeEnum(env,defaultPackage);
		createProductionEnums(env,defaultPackage);

		createAnalysis(env,analysisPackageName);
		createAnswer(env,analysisPackageName);
		createQuestion(env,analysisPackageName);
		createQuestionAnswer(env,analysisPackageName);

		// SourceFileWriter.write(outputFolder, env1);
		return env;
	}

	private static void createNodeEnum(Environment env,String packageName)
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

	private static void createProductionEnums(Environment env, String packageName)
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
				}else if (c.getType() == ClassType.SubProduction)
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

	private static void createAnalysis(Environment env, String analysisPackageName)
			throws InstantiationException, IllegalAccessException
	{
		extendedVisitor("Analysis", AnalysisAcceptMethod.class, AnalysisCaseMethod.class, env,analysisPackageName,env.TAG_IAnalysis);
	}

	private static void createAnswer(Environment env, String analysisPackageName)
			throws InstantiationException, IllegalAccessException
	{
		extendedVisitor("Answer<A>", AnswerAcceptMethod.class, AnswerCaseMethod.class, env,analysisPackageName,env.TAG_IAnswer);
	}

	private static void createQuestion(Environment env, String analysisPackageName)
			throws InstantiationException, IllegalAccessException
	{
		extendedVisitor("Question<Q>", QuestionAcceptMethod.class, QuestionCaseMethod.class, env,analysisPackageName,env.TAG_IQuestion);
	}

	private static void createQuestionAnswer(Environment env, String analysisPackageName)
			throws InstantiationException, IllegalAccessException
	{
		extendedVisitor("QuestionAnswer<Q, A>", QuestionAnswerAcceptMethod.class, QuestionAnswerCaseMethod.class, env,analysisPackageName,env.TAG_IQuestionAnswer);
	}

	@SuppressWarnings("rawtypes")
	public static void extendedVisitor(String intfName, Class accept,
			Class caseM, Environment env, String analysisPackageName,String tag) throws InstantiationException,
			IllegalAccessException
	{
		InterfaceDefinition answerIntf = new InterfaceDefinition(intfName);
		answerIntf.setTag(tag);
		answerIntf.setPackageName(analysisPackageName);
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

		String tmpName = answerIntf.getName().substring(1);
		if (tmpName.contains("<"))
		{
			tmpName = tmpName.substring(0, tmpName.indexOf('<')) + "Adaptor"
					+ answerIntf.getName().substring(tmpName.indexOf('<') + 1);
		} else
		{
			tmpName += "Adaptor";
		}

		CustomClassDefinition answerClass = new CustomClassDefinition(tmpName, env);
		answerClass.setPackageName(analysisPackageName);
		answerClass.interfaces.add(answerIntf.getName());
		answerClass.methods.addAll(answerIntf.methods);
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

}
