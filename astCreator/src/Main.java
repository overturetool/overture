import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Vector;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;

import com.lausdahl.ast.creator.BaseClassDefinition;
import com.lausdahl.ast.creator.CommonTreeClassDefinition;
import com.lausdahl.ast.creator.CustomClassDefinition;
import com.lausdahl.ast.creator.EnumDefinition;
import com.lausdahl.ast.creator.Environment;
import com.lausdahl.ast.creator.ExternalJavaClassDefinition;
import com.lausdahl.ast.creator.Field;
import com.lausdahl.ast.creator.IClassDefinition;
import com.lausdahl.ast.creator.IClassDefinition.ClassType;
import com.lausdahl.ast.creator.IInterfaceDefinition;
import com.lausdahl.ast.creator.InterfaceDefinition;
import com.lausdahl.ast.creator.PredefinedClassDefinition;
import com.lausdahl.ast.creator.methods.AnalysisAcceptMethod;
import com.lausdahl.ast.creator.methods.AnalysisCaseMethod;
import com.lausdahl.ast.creator.methods.AnswerAcceptMethod;
import com.lausdahl.ast.creator.methods.AnswerCaseMethod;
import com.lausdahl.ast.creator.methods.Method;
import com.lausdahl.ast.creator.methods.ParentGetMethod;
import com.lausdahl.ast.creator.methods.ParentSetMethod;
import com.lausdahl.ast.creator.methods.QuestionAcceptMethod;
import com.lausdahl.ast.creator.methods.QuestionAnswerAcceptMethod;
import com.lausdahl.ast.creator.methods.QuestionAnswerCaseMethod;
import com.lausdahl.ast.creator.methods.QuestionCaseMethod;
import com.lausdahl.ast.creator.methods.TokenConstructorMethod;

public class Main
{
	private static final String ANALYSIS_PACKAGE_NAME = "org.overture.ast.analysis";
	private static File generated = new File("..\\ast\\src\\");
	static boolean create = true;

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException
	{

		if (create)
		{
			Environment env = new Environment();
			env.addClass(new PredefinedClassDefinition("generated.node", "Node"));
			env.addClass(new PredefinedClassDefinition("generated.node", "NodeList"));
			env.addClass(new PredefinedClassDefinition("generated.node", "Token"));
			env.addClass(new PredefinedClassDefinition("java.util", "List"));
			// ANTLRStringStream input = new ANTLRStringStream(data);
			ANTLRFileStream input = new ANTLRFileStream("test.txt");
			AstcLexer lexer = new AstcLexer(input);
			CommonTokenStream tokens = new CommonTokenStream(lexer);

			AstcParser parser = new AstcParser(tokens);

			try
			{
				AstcParser.root_return result = parser.root();
				CommonTree t = (CommonTree) result.getTree();

				show(t, 0);
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
									CommonTree p = (CommonTree) production;
									CommonTreeClassDefinition c = new CommonTreeClassDefinition(p, null, CommonTreeClassDefinition.ClassType.Production, env);

									for (Object a : p.getChildren())
									{
										if (a instanceof CommonTree)
										{
											CommonTree aa = (CommonTree) a;

											if (aa.getText() != null
													&& aa.getText().equals("->"))
											{
												for (Object o : aa.getChildren())
												{
													if (o instanceof CommonTree
															&& ((CommonTree) o).getChildCount() > 0)
													{
														CommonTree oo = (CommonTree) o;
														if (oo.getText().equals("package"))
														{
															String packageName = oo.getChild(0).getText();
															c.setPackageName(packageName);
														}
													}
												}
											} else
											{
												exstractA(c,aa, env);
											}
										}
									}
								}
							}
						} else if (node.getText().equals("Tokens"))
						{
							for (Object toke : node.getChildren())
							{
								if (toke instanceof CommonTree)
								{
									CommonTree p = (CommonTree) toke;
									CommonTree idT = null;
									boolean externalJavaType = false;
									if (p.getChildCount() > 0)
									{
										idT = (CommonTree) p.getChild(0);
										if (p.getChildCount() > 1)
										{
											if (p.getChild(0).getText().equals("java"))
											{
												externalJavaType = true;
												idT = (CommonTree) p.getChild(2);
											}
										}
										// for (Object id : p.getChildren())
										// {
										// if (id instanceof CommonTree)
										// {
										// idT = (CommonTree) id;
										//
										// // m.isConstructor = true;
										// // m.name = c.getName();
										// // m.returnType = "";
										// // m.body = "\t\t" + f.getName()
										// // + " = \""
										// // + idT.getText() + "\";";
										//
										// break;
										// }
										// }
									}

									CommonTreeClassDefinition c = null;
									if (!externalJavaType)
									{
										c = new CommonTreeClassDefinition(p, null, CommonTreeClassDefinition.ClassType.Token, env);
										c.setPackageName("org.overture.ast.tokens");
									} else
									{
										c = new ExternalJavaClassDefinition(p, null, CommonTreeClassDefinition.ClassType.Token, idT.getText(), env);
									}
									
									c.imports.add("generated.node.Token");
									c.imports.add("generated.node.Node");
									Field f = new Field(env);
									f.name = "text";
									f.type = "String";
									f.isTokenField = true;
									c.addField(f);
									Method m = new TokenConstructorMethod(c, f, idT.getText(),env);
									c.methods.add(m);
									System.out.println("Token: " + p);
								}
							}
						} else if (node.getText().equals("Aspect Declaration"))
						{
							for (Object toke : node.getChildren())
							{
								if (toke instanceof CommonTree)
								{
									CommonTree p = (CommonTree) toke;
									String classDefName = "P"+BaseClassDefinition.firstLetterUpper(p.getText());
									IClassDefinition c = env.lookUp(classDefName);

									if (p.getChildCount() > 0)
									{
										for (Object aspectDcl : p.getChildren())
										{
											if (aspectDcl instanceof CommonTree)
											{
												CommonTree aspectDclT = (CommonTree) aspectDcl;
												Field f = new Field(env);
												f.type = aspectDclT.getText();
												if (aspectDclT.getChildCount() > 0)
												{
													for (Object aspectDclName : p.getChildren())
													{
														if (aspectDclName instanceof CommonTree)
														{
															CommonTree aspectDclNameT = (CommonTree) aspectDclName;
															f.name = aspectDclNameT.getText();
														}
													}
												}
												f.isAspect = true;
												c.addField(f);
											}
										}

									}

									System.out.println("Aspect Decleration: "
											+ p);
								}
							}
						}
					}
				}

				System.out.println("\n\n--------------------------------------------------------------------");
				
				File generatedVdm = new File(new File(new File(generated, "vdm"), "generated"), "node");
				generated.mkdirs();
				generatedVdm.mkdirs();

				createNodeEnum(generated,env);
				createProductionEnums(generated,env);
				createBaseClasses(generated);
				createAnalysis(generated, generatedVdm,env);
				createAnswer(generated, generatedVdm,env);
				createQuestion(generated, generatedVdm,env);
				createQuestionAnswer(generated, generatedVdm,env);
				// createDelegateMethodStructure(generated, "TypeChecker", CaseTypeCheckMethod.class,
				// TypeCheckMethod.class);
				// createDelegateMethodStructure(generated, "Eval", CaseEvalMethod.class, EvalMethod.class);

				for (IClassDefinition classDefinition : env.getClasses())
				{
					// System.out.println(classDefinition.toString());
					write(generated, classDefinition);
					write(generatedVdm, classDefinition, false);
				}

				// System.out.println(((CommonToken)result.getStart()));
			} catch (RecognitionException e)
			{

				e.printStackTrace();
			} catch (InstantiationException e)
			{

				e.printStackTrace();
			} catch (IllegalAccessException e)
			{

				e.printStackTrace();
			}
		}

//		Test.run();
//		ExtendedTest.test();
	}

	private static void write(File generated, IInterfaceDefinition def,
			boolean writeJava)
	{
		try
		{
			String name = null;
			String content = "";
			File output = createFolder(generated, def.getPackageName());
			if (writeJava)
			{
				name = def.getName();
				content = def.getJavaSourceCode();
			} else
			{
				InterfaceDefinition.VDM = true;
				String tmp = Field.fieldPrefic;
				Field.fieldPrefic = "m_";
				content = def.getVdmSourceCode();
				Field.fieldPrefic = tmp;
				name = def.getName();
				InterfaceDefinition.VDM = false;
			}

			if (content == null || content.trim().length() == 0)
			{
				return;
			}

			FileWriter outFile = new FileWriter(new File(output, getFileName(name)
					+ (writeJava ? ".java" : ".vdmpp")));
			PrintWriter out = new PrintWriter(outFile);

			out.println(content);
			out.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private static File createFolder(File src, String packageName)
	{
		File output = null;
		for (String s : packageName.split("\\."))
		{
			if(output==null)
			{
			output = new File(src,s);
			}else
			{
				output = new File(output,s);
			}
		}
		
		if(output == null)
		{
			output = src;
		}
		output.mkdirs();
		return output;
	}

	private static void createNodeEnum(File generated, Environment env)
	{
		EnumDefinition eDef = new EnumDefinition("NodeEnum");
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
		write(generated, eDef);
	}

	private static void createProductionEnums(File generated, Environment env)
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
					enums.add(eDef);			
					
					
					//for (CommonTreeClassDefinition sub : CommonTreeClassDefinition.getSubClasses(c))
					for (CommonTreeClassDefinition sub : getClasses(env.getSubClasses(c)))
					{
						eDef.elements.add(sub.getEnumName());
					}
					write(generated, eDef);
				}
			}
		}
		
		for (EnumDefinition enumDefinition : enums)
		{
			env.addClass(enumDefinition);
		}
	}

	private static void createBaseClasses(File generated)
	{
		copy(generated, "Node.java");
		copy(generated, "Token.java");
		copy(generated, "NodeList.java");
		copy(generated, "ExternalNode.java");
	}

	private static void copy(File generated, String name)
	{
		File output = new File(new File(generated, "generated"), "node");
		
		InputStream fis = null;

		try
		{

			fis = Main.class.getResourceAsStream("/" + name);

			if (fis == null)
			{
				fis = new FileInputStream(name);

			}
			OutputStream out = new FileOutputStream(new File(output, name));
			byte[] buffer = new byte[4096];
			for (int n; (n = fis.read(buffer)) != -1;)
			{
				out.write(buffer, 0, n);
			}
			out.close();
			fis.close();

		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	private static void createAnalysis(File generated, File generatedVdm, Environment env)
			throws InstantiationException, IllegalAccessException
	{
		// InterfaceDefinition analysisIntf = new InterfaceDefinition();
		// analysisIntf.name = "Analysis";
		//
		// for (ClassDefinition c : ClassDefinition.classes)
		// {
		// if (c.type == IClassDefinition.ClassType.Production)
		// {
		// continue;
		// }
		// Method m = new AnalysisAcceptMethod();
		// m.setClassDefinition(c);
		// c.methods.add(m);
		//
		// m = new AnalysisCaseMethod();
		// m.setClassDefinition(c);
		// analysisIntf.methods.add(m);
		//
		// }
		// write(generated, analysisIntf.name, analysisIntf.toString());
		// CustomClassDefinition analysisClass = new CustomClassDefinition();
		// analysisClass.methods.addAll(analysisIntf.methods);
		// analysisClass.name = analysisIntf.name + "Adaptor";
		// write(generated, analysisClass.name, analysisClass.toString());
		extendedVisitor(generated, generatedVdm, "Analysis", AnalysisAcceptMethod.class, AnalysisCaseMethod.class,env);
	}

	private static void createAnswer(File generated, File generatedVdm, Environment env)
			throws InstantiationException, IllegalAccessException
	{
		extendedVisitor(generated, generatedVdm, "Answer<A>", AnswerAcceptMethod.class, AnswerCaseMethod.class,env);
	}

	private static void createQuestion(File generated, File generatedVdm, Environment env)
			throws InstantiationException, IllegalAccessException
	{
		extendedVisitor(generated, generatedVdm, "Question<Q>", QuestionAcceptMethod.class, QuestionCaseMethod.class,env);
	}

	private static void createQuestionAnswer(File generated, File generatedVdm, Environment env)
			throws InstantiationException, IllegalAccessException
	{
		extendedVisitor(generated, generatedVdm, "QuestionAnswer<Q, A>", QuestionAnswerAcceptMethod.class, QuestionAnswerCaseMethod.class,env);
	}

	@SuppressWarnings("rawtypes")
	public static void extendedVisitor(File generated, File generatedVdm,
			String intfName, Class accept, Class caseM,Environment env)
			throws InstantiationException, IllegalAccessException
	{
		InterfaceDefinition answerIntf = new InterfaceDefinition(intfName);
		answerIntf.setPackageName(ANALYSIS_PACKAGE_NAME);
		// answerIntf.getName() = intfName;
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
		write(generated, answerIntf);
		write(generatedVdm, answerIntf, false);

		String tmpName = answerIntf.getName().substring(1);
		if (tmpName.contains("<"))
		{
			tmpName = tmpName.substring(0, tmpName.indexOf('<')) + "Adaptor"
					+ answerIntf.getName().substring(tmpName.indexOf('<') + 1);
		} else
		{
			tmpName += "Adaptor";
		}

		CustomClassDefinition answerClass = new CustomClassDefinition(tmpName,env);
		answerClass.setPackageName(ANALYSIS_PACKAGE_NAME);
		answerClass.interfaces.add(answerIntf.getName());
		answerClass.methods.addAll(answerIntf.methods);
		// answerClass.name = tmpName;
		write(generated, answerClass);
		write(generatedVdm, answerClass, false);
	}
	
	public static List<CommonTreeClassDefinition> getClasses(List<IClassDefinition> classList)
	{
		List<CommonTreeClassDefinition> classes = new Vector<CommonTreeClassDefinition>();
		for (IClassDefinition c : classList)
		{
			if(c instanceof CommonTreeClassDefinition)
			{
				classes.add((CommonTreeClassDefinition)c);
			}
		}
		return classes;
	}

	private static String getFileName(String name)
	{
		if (name.contains("<"))
		{
			return name.substring(0, name.indexOf('<'));
		}

		return name;
	}

	private static void exstractA(CommonTreeClassDefinition superClass, CommonTree a, Environment env)
	{
		CommonTreeClassDefinition c = new CommonTreeClassDefinition(a, superClass, CommonTreeClassDefinition.ClassType.Alternative, env);

		if (a.getChildCount() > 0)
		{
			for (Object f : a.getChildren())
			{
				if (f instanceof CommonTree)
				{
					CommonTree fTree = (CommonTree) f;
					Field field = new Field(env);
					field.type = fTree.getText();
					if (fTree.getChild(0) != null)
					{
						field.name = fTree.getChild(0).getText();
					}
					if (fTree.getChildCount() > 1)
					{
						if (fTree.getChild(1) != null)
						{
							String regex = fTree.getChild(1).getText();
							if (regex.trim().equals("*"))
							{
								field.isList = true;
							}
						}
					}
					
					for (IClassDefinition cl : env.getClasses())
					{
						if(cl instanceof ExternalJavaClassDefinition && cl.getName().equals(field.getType()))
						{
							field.isTokenField = true;
							field.type = cl.getName();
						}
					}
					
					
					c.addField(field);

				}
			}
		}
	}

	public static void show(CommonTree token, int level)
	{
		String indent = "";
		for (int i = 0; i < level; i++)
		{
			indent += "  ";
		}

		System.out.println(indent + token.getText());
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
			System.out.println();
	}

	public static void write(File generated, IInterfaceDefinition def)
	{
		write(generated, def, true);
	}

	@SuppressWarnings("rawtypes")
	public static void createDelegateMethodStructure(File generated,
			String structureClassName, Class delegateMethod,
			Class additionToNodesMethod, Environment env)
			throws InstantiationException, IllegalAccessException
	{
		List<Field> fieldsInChecker = new Vector<Field>();
		List<CustomClassDefinition> checkers = new Vector<CustomClassDefinition>();

		for (CommonTreeClassDefinition c : getClasses(env.getClasses()))
		{
			if (c.getType() == CommonTreeClassDefinition.ClassType.Production)
			{
				Field f = new Field(env);
				f.name = c.getName();
				f.type = c.getName() + structureClassName;
				fieldsInChecker.add(f);

				CustomClassDefinition checker = new CustomClassDefinition(f.type,env);
				// checker.name = f.type;
				checker.tag = c.thisClass;

				Field parent = new Field(env);
				parent.name = "parent";
				parent.type = structureClassName;
				parent.isTokenField = true;
				checker.addField(parent);
				Method m = new ParentSetMethod(structureClassName, parent,env);
				// m.name = "parent";
				// m.arguments.add(new Method.Argument(structureClassName, "value"));
				// m.body = "\t\tthis." + parent.getName() + " = value;";
				checker.methods.add(m);

				Method m2 = new ParentGetMethod(structureClassName, parent,env);
				// m2.name = "parent";
				// m2.returnType = structureClassName;
				// m2.body = "\t\treturn " + parent.getName() + ";";
				checker.methods.add(m2);

				checkers.add(checker);
			}

			if (c.getType() == CommonTreeClassDefinition.ClassType.Alternative)
			{
				Method m = (Method) additionToNodesMethod.newInstance();
				m.setClassDefinition(c);
				c.methods.add(m);
			}
		}

		for (CommonTreeClassDefinition c : getClasses(env.getClasses()))
		{
			if (c.getType() != CommonTreeClassDefinition.ClassType.Production)
			{
				for (CustomClassDefinition customClassDefinition : checkers)
				{
					if (customClassDefinition.tag == c.superClass)
					{
						// CaseTypeCheckMethod method = new CaseTypeCheckMethod(c);
						Method m = (Method) delegateMethod.newInstance();
						m.setClassDefinition(c);
						customClassDefinition.methods.add(m);
					}
				}
			}
		}

		CustomClassDefinition c = new CustomClassDefinition(structureClassName,env);
		// c.name = structureClassName;
		// c.interfaceName = "INodeChecker";
		for (Field field : fieldsInChecker)
		{
			c.addField(field);
		}
		// c.fields = fieldsInChecker;

		write(generated, c);

		for (CustomClassDefinition customClassDefinition : checkers)
		{
			write(generated, customClassDefinition);
		}
	}
}
