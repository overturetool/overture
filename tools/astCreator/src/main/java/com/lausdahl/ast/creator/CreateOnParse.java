package com.lausdahl.ast.creator;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.CommonTree;

import com.lausdahl.ast.creator.definitions.BaseClassDefinition;
import com.lausdahl.ast.creator.definitions.CommonTreeClassDefinition;
import com.lausdahl.ast.creator.definitions.ExternalEnumJavaClassDefinition;
import com.lausdahl.ast.creator.definitions.ExternalJavaClassDefinition;
import com.lausdahl.ast.creator.definitions.Field;
import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.definitions.IClassDefinition.ClassType;
import com.lausdahl.ast.creator.methods.Method;
import com.lausdahl.ast.creator.methods.TokenConstructorMethod;
import com.lausdahl.ast.creator.parser.AstcLexer;
import com.lausdahl.ast.creator.parser.AstcParser;

public class CreateOnParse
{
	public Environment parse(String astFile, String defaultPackage)
			throws IOException, AstCreatorException
	{
		Environment env = new Environment(defaultPackage);

		ANTLRFileStream input = new ANTLRFileStream(astFile);
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

		if (parser.hasErrors() || parser.hasExceptions())
		{
			throw new AstCreatorException("Errors in AST input file", null, true);
		}

		try
		{
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
								CommonTree nameNode = (CommonTree) p.getChildren().get(0);
								String packageName = defaultPackage;

								IClassDefinition c = null;
								if (nameNode.getText().equals("#"))
								{
									for (IClassDefinition def : env.getClasses())
									{
										if (def instanceof CommonTreeClassDefinition
												&& ((CommonTreeClassDefinition) def).rawName.equals(nameNode.getChild(0).getText()))
										{
											c = (CommonTreeClassDefinition) def;
										}
									}
								}else if(nameNode.getText().equals("NODE"))
								{
									c = env.node;
								} else
								{
									c = new CommonTreeClassDefinition(nameNode.getText(), null, CommonTreeClassDefinition.ClassType.Production, env);
									c.setPackageName(defaultPackage);
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
														 packageName = oo.getChild(0).getText();
														c.setPackageName(packageName);
													}
												}
											}
										} else if (aa.getText() != null
												&& aa.getText().equals("ALTERNATIVE_SUB_ROOT"))
										{
											CommonTreeClassDefinition subAlternativeClassDef = new CommonTreeClassDefinition(aa.getChild(0).getText(), c, CommonTreeClassDefinition.ClassType.SubProduction, env);
											subAlternativeClassDef.setPackageName(defaultPackage);
										} else
										{
											exstractA(c, aa, env, packageName);
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

								CommonTreeClassDefinition c = null;
								if (!externalJavaType)
								{
									c = new CommonTreeClassDefinition(p.getText(), null, CommonTreeClassDefinition.ClassType.Token, env);
									c.setPackageName(defaultPackage + ".tokens");
								} else if (enumType)
								{
									c = new ExternalEnumJavaClassDefinition(p.getText(), null, CommonTreeClassDefinition.ClassType.Token, idT.getText(), env);
								} else
								{
									c = new ExternalJavaClassDefinition(p.getText(), null, CommonTreeClassDefinition.ClassType.Token, idT.getText(), nodeType, env);
								}

								c.imports.add(env.token);
								c.imports.add(env.node);
								Field f = new Field(env);
								f.name = "text";
								f.type = Environment.stringDef;
								f.isTokenField = true;
								c.addField(f);
								Method m = new TokenConstructorMethod(c, f, idT.getText(), env);
								c.methods.add(m);
								println("Token: " + p);
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
									CommonTree p = (CommonTree) toke;
									// String classDefName = "P"
									// + BaseClassDefinition.firstLetterUpper(p.getText());
									String classDefName = getNameFromAspectNode((CommonTree) p.getChild(0));
									IClassDefinition c = env.lookUp(classDefName);
									if (c == null)
									{
										System.err.println("Failed to lookup aspect addition with "
												+ p + classDefName);
										continue;
									}
									boolean firstName = true;
									if (p.getChildCount() > 0)
									{
										for (Object aspectDcl : p.getChildren())
										{
											if (firstName)
											{
												firstName = false;
												continue;
											}
											if (aspectDcl instanceof CommonTree)
											{
												CommonTree aspectDclT = (CommonTree) aspectDcl;
												String typeName = aspectDclT.getText();
												Field f = new Field(env);

												if (aspectDclT.getChildCount() > 0)
												{
													for (Object aspectDclName : aspectDclT.getChildren())
													{
														if (aspectDclName instanceof CommonTree)
														{
															CommonTree aspectDclNameT = (CommonTree) aspectDclName;
															f.name = aspectDclNameT.getText();
															break;
														}
													}
												}

												if (aspectDclT.getChildCount() > 1)
												{
													if (aspectDclT.getChild(1) != null)
													{
														String regex = aspectDclT.getChild(1).getText();
														if (regex.trim().equals("*"))
														{
															f.isList = true;
														}
														if (regex.trim().equals("**"))
														{
															f.isList = true;
															f.isDoubleList = true;
														}
													}
												}
												
												
												for (IClassDefinition cl : env.getClasses())
												{
													if (cl instanceof ExternalJavaClassDefinition
															&& ((ExternalJavaClassDefinition) cl).rawName.equals(typeName))
													{
														f.isTokenField = true;
														f.type = cl;// TODO
													}
												}
												if (f.type == null)
												{
													f.setType(typeName);
												}

												f.isAspect = true;
//												f.setType(aspectDclT.getText());
												c.addField(f);
											}
										}

									}

									println("Aspect Decleration: " + p);
								}
							}
						}
					}
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			throw new AstCreatorException("Exception in AST parser", e, true);
		}

		return env;
	}

	public static String getNameFromAspectNode(CommonTree p)
	{
		String topName = p.getText();
		if (p.getChildCount() > 0)
		{
			for (Object c : p.getChildren())
			{
				if (c instanceof CommonTree)
				{
					topName += ((CommonTree) c).getText();
				}
			}
		}

		String[] names = topName.split("->");

		List<String> nns = Arrays.asList(names);
		Collections.reverse(nns);

		String name = null;
		for (String s : nns)
		{
			if (name == null)
			{
				if (s.startsWith("#"))
				{
					name = "S";
				} else
				{
					name = "P";
				}
			}
			name += BaseClassDefinition.firstLetterUpper(s.replace("#", ""));
		}

		// String name = (topName.startsWith("#") ? "S" : "P");
		//
		// name += BaseClassDefinition.firstLetterUpper(topName.substring(topName.startsWith("#") ? 1
		// : 0));

		return name;
	}

	private static void exstractA(IClassDefinition superClass,
			CommonTree a, Environment env, String thisPackage)
	{
		// CommonTree nameNode = (CommonTree)a.getChildren().get(0);
		CommonTreeClassDefinition.ClassType type = ClassType.Alternative;
		if(superClass == env.node)
		{
			type = ClassType.Production;
		}
		CommonTreeClassDefinition c = new CommonTreeClassDefinition(a.getText(), superClass, type, env);
		c.setPackageName(thisPackage);
		if (a.getChildCount() > 0)
		{
			for (Object f : a.getChildren())
			{
				if (f instanceof CommonTree)
				{
					CommonTree fTree = (CommonTree) f;
					Field field = new Field(env);
					String typeName = fTree.getText();

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
								&& ((ExternalJavaClassDefinition) cl).rawName.equals(typeName))
						{
							field.isTokenField = true;
							field.type = cl;// TODO
						}
					}
					if (field.type == null)
					{
						field.setType(typeName);
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
