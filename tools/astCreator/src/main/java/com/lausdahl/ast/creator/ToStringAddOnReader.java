package com.lausdahl.ast.creator;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.CommonTree;

import com.lausdahl.ast.creator.ToStringAddOn.ToStringPart.ToStringPartType;
import com.lausdahl.ast.creator.definitions.BaseClassDefinition;
import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.parser.AstcToStringLexer;
import com.lausdahl.ast.creator.parser.AstcToStringParser;
import com.lausdahl.ast.creator.parser.AstcToStringParser.root_return;

public class ToStringAddOnReader
{
	public void readAndAdd(String file, Environment env) throws IOException
	{
		if(!new File(file).exists())
		{
			return;
		}
		
		ANTLRFileStream input = new ANTLRFileStream(file);
		AstcToStringLexer lexer = new AstcToStringLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);

		AstcToStringParser parser = new AstcToStringParser(tokens);

		try
		{
			root_return result = parser.root();
			CommonTree t = (CommonTree) result.getTree();

			show(t, 0);

			for (Object root : t.getChildren())
			{
				if (root instanceof CommonTree)
				{
					CommonTree node = (CommonTree) root;
					if (node != null && node.getText() != null
							&& node.getText().equals("To String Extensions"))
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
										ToStringAddOn addon = new ToStringAddOn();
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
												if (((CommonTree) aspectDcl).getText().equals("="))
												{
													continue;
												}

												if (aspectDclT.getText().startsWith("\""))
												{
													ToStringAddOn.ToStringPart part = new ToStringAddOn.ToStringPart();
													part.type = ToStringPartType.String;
													part.content = aspectDclT.getText();
													addon.parts.add(part);
												} else if (aspectDclT.getText().startsWith("$"))
												{
													ToStringAddOn.ToStringPart part = new ToStringAddOn.ToStringPart();
													part.type = ToStringPartType.RawJava;
													part.content = aspectDclT.getText();
													addon.parts.add(part);
												}else if (aspectDclT.getText().equals("+"))
												{
													ToStringAddOn.ToStringPart part = new ToStringAddOn.ToStringPart();
													part.type = ToStringPartType.Plus;
													part.content = aspectDclT.getText();
													addon.parts.add(part);
												}else
												{
													ToStringAddOn.ToStringPart part = new ToStringAddOn.ToStringPart();
													part.type = ToStringPartType.Field;
													part.content = aspectDclT.getText();
													addon.parts.add(part);
												}

											}
										}
										c.addToStringAddOn(addon);
									}

									println("To String Extensions: " + p);
								}
							}
						}
					}
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}

//		for (IClassDefinition c : env.getClasses())
//		{
//			System.out.println(c);
//		}
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
					name = "A";
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

}
