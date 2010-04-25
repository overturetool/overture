package org.overture.ide.debug.ui.launchconfigurations;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.eclipse.jface.operation.IRunnableContext;
import org.overturetool.vdmj.ast.IAstNode;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.ExplicitFunctionDefinition;
import org.overturetool.vdmj.definitions.ExplicitOperationDefinition;
import org.overturetool.vdmj.lex.Token;
import org.overturetool.vdmj.modules.Module;
import org.overturetool.vdmj.patterns.PatternList;
import org.overturetool.vdmj.statements.SubclassResponsibilityStatement;

public class MethodSearchEngine
{
	public final static int MAIN_ONLY = 1;
	public final static int EXPLICIT_FUNCTION = 2;
	public final static int EXPLICIT_OPERATION = 4;
	public final static int STATIC = 8;
	public final static int PUBLIC = 16;

	public IAstNode[] searchMainMethods(IRunnableContext context,
			Object[] nodes, int constraints)
	{
		boolean onlyMain = (constraints & MAIN_ONLY) == MAIN_ONLY;
		boolean onlyExplicitFunction = (constraints & EXPLICIT_FUNCTION) == EXPLICIT_FUNCTION;
		boolean onlyExplicitOperation = (constraints & EXPLICIT_OPERATION) == EXPLICIT_OPERATION;
		boolean onlyStatic = (constraints & STATIC) == STATIC;
		boolean onlyPublicAccess = (constraints & PUBLIC) == PUBLIC;

		final String MAIN_NAME = "main";

		List<IAstNode> matched = new Vector<IAstNode>();

		for (int i = 0; i < nodes.length; i++)
		{
			Object iAstNode = nodes[i];
			boolean accept = false;

			if (onlyExplicitOperation
					&& iAstNode instanceof ExplicitOperationDefinition)
			{
				ExplicitOperationDefinition exop = (ExplicitOperationDefinition) iAstNode;
				if (isConstructor(exop))
				{
					continue;//is constructor
				}

				if (onlyStatic && !exop.isStatic())
				{
					continue;
				}

				if (!exop.isStatic() && exop.classDefinition != null)
				{
					// check for empty constructor
					boolean ok = false;
					boolean constructorFound = false;
					for (Object def : exop.classDefinition.getDefinitions())
					{
						if (def instanceof ExplicitOperationDefinition)
						{
							ExplicitOperationDefinition ctor = (ExplicitOperationDefinition) def;
							if (isConstructor(ctor))
							{
								continue;
							}
							if (ctor.getName().equalsIgnoreCase(exop.classDefinition.getName()))
							{
								constructorFound = true;
							}
							if (ctor.body instanceof SubclassResponsibilityStatement)
							{
								// abstract class instantiation impossible
								ok = false;
								break;
							}
							if ( ctor.parameterPatterns != null
									&& ctor.parameterPatterns.size() > 0)
							{
								ok = true;
								break;
							}
						}

					}
					if (!ok && constructorFound)
					{
						continue;
					}
				}
				if (onlyMain
						&& !exop.getName().toLowerCase().equals(MAIN_NAME))
				{
					continue;
				}
				if (onlyPublicAccess
						&& exop.accessSpecifier.access != Token.PUBLIC)
				{
					continue;
				}

				if ( exop.parameterPatterns != null
						&& exop.parameterPatterns.size() > 0)
				{
					continue;
				}
				accept = true;

			}
			if (onlyExplicitFunction
					&& iAstNode instanceof ExplicitFunctionDefinition)
			{
				ExplicitFunctionDefinition exfu = (ExplicitFunctionDefinition) iAstNode;
				if (onlyStatic && !exfu.isStatic())
				{
					continue;
				}
				if (onlyMain
						&& !exfu.getName().toLowerCase().equals(MAIN_NAME))
				{
					continue;
				}
				if (onlyPublicAccess
						&& exfu.accessSpecifier.access != Token.PUBLIC)
				{
					continue;
				}

				if (exfu.paramPatternList != null
						&& (exfu.paramPatternList.size() > 0 && exfu.paramPatternList.get(0) instanceof PatternList && ((PatternList)exfu.paramPatternList.get(0)).size()>0))
				{
					continue;
				}
				accept = true;

			}

			if (accept && iAstNode instanceof IAstNode)
			{
				matched.add((IAstNode) iAstNode);
			}

			if (iAstNode instanceof ClassDefinition)
			{
				Object[] elements = ((ClassDefinition) iAstNode).getDefinitions().toArray();
				matched.addAll(Arrays.asList(searchMainMethods(context, elements, constraints)));
			}
			if (iAstNode instanceof Module)
			{
				Object[] elements = ((Module) iAstNode).defs.toArray();
				matched.addAll(Arrays.asList(searchMainMethods(context, elements, constraints)));
			}
		}
		return matched.toArray(new IAstNode[matched.size()]);
	}
	
	
	public static boolean isConstructor(ExplicitOperationDefinition op)
	{
		if (op.isConstructor||op.classDefinition!=null && op.getName().equalsIgnoreCase(op.classDefinition.location.module))
		{
			return true;
		}
		return false;
	}
}