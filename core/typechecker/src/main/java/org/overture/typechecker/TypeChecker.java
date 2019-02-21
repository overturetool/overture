/*******************************************************************************
 *
 *	Copyright (c) 2008 Fujitsu Services Ltd.
 *
 *	Author: Nick Battle
 *
 *	This file is part of VDMJ.
 *
 *	VDMJ is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	VDMJ is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with VDMJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

package org.overture.typechecker;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameSet;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.messages.InternalException;
import org.overture.parser.messages.VDMError;
import org.overture.parser.messages.VDMMessage;
import org.overture.parser.messages.VDMWarning;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.TypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.definition.PDefinitionAssistantTC;
import org.overture.typechecker.utilities.FreeVarInfo;

/**
 * The abstract root of all type checker classes.
 */

abstract public class TypeChecker
{
	private static boolean suppress=false;

	public interface IStatusListener
	{
		void report(VDMError error);

		void warning(VDMWarning warning);
	}

	private static List<VDMError> errors = new Vector<VDMError>();
	private static List<VDMWarning> warnings = new Vector<VDMWarning>();
	private static VDMMessage lastMessage = null;
	private static final int MAX = 200;

	final protected ITypeCheckerAssistantFactory assistantFactory;

	static List<IStatusListener> listners = new Vector<IStatusListener>();

	/**
	 * VDM-only constructor. <b>NOT</b> for use by extensions.
	 */
	public TypeChecker()
	{
		clearErrors();
		this.assistantFactory = new TypeCheckerAssistantFactory();
	}

	public TypeChecker(ITypeCheckerAssistantFactory factory)
	{
		clearErrors();
		this.assistantFactory = factory;
	}

	abstract public void typeCheck();

	/**
	 * Check for cyclic dependencies between the free variables that definitions depend
	 * on and the definition of those variables.
	 */
	protected void cyclicDependencyCheck(List<PDefinition> defs)
	{
		if (System.getProperty("skip.cyclic.check") != null)
		{
			return;		// For now, to allow us to skip if there are issues.
		}
		
		if (getErrorCount() > 0)
		{
			return;		// Can't really check everything until it's clean
		}
		
		Map<ILexNameToken, LexNameSet> dependencies = new HashMap<ILexNameToken, LexNameSet>();
		LexNameSet skip = new LexNameSet();
		PDefinitionAssistantTC assistant = assistantFactory.createPDefinitionAssistant();
		Environment globals = new FlatEnvironment(assistantFactory, defs, null);

    	for (PDefinition def: defs)
    	{
    		Environment env = new FlatEnvironment(assistantFactory, new Vector<PDefinition>());
    		FreeVarInfo empty = new FreeVarInfo(globals, env, false);
			LexNameSet freevars = assistant.getFreeVariables(def, empty);
			
			if (!freevars.isEmpty())
			{
    			for (ILexNameToken name: assistant.getVariableNames(def))
    			{
    				dependencies.put(nameFix(name.getExplicit(true)), nameFix(freevars));
    			}
			}
			
			// Skipped definition names occur in the cycle path, but are not checked
			// for cycles themselves, because they are not "initializable".
			
			if (assistant.isTypeDefinition(def) || assistant.isOperation(def))
			{
				if (def.getName() != null)
				{
					skip.add(nameFix(def.getName().getExplicit(true)));
				}
			}
    	}
    	
		for (ILexNameToken sought: dependencies.keySet())
		{
			if (!skip.contains(sought))
			{
    			Stack<ILexNameToken> stack = new Stack<ILexNameToken>();
    			stack.push(sought);
    			
    			if (reachable(sought, dependencies.get(sought), dependencies, stack))
    			{
    	    		report(3355, "Cyclic dependency detected for " + sought, sought.getLocation());
    	    		detail("Cycle", stack.toString());
    			}
    			
    			stack.pop();
			}
		}
	}
	
	/**
	 * We have to "fix" names to include equals and hashcode methods that take the type qualifier
	 * into account. This is so that we can distinguish (say) f(nat) and f(char). It also allows
	 * us to realise that f(nat1) and f(nat) are "the same".
	 */

	private LexNameSet nameFix(LexNameSet names)
	{
		LexNameSet result = new LexNameSet();
		
		for (ILexNameToken name: names)
		{
			result.add(nameFix(name));
		}
		
		return result;
	}

	private ILexNameToken nameFix(ILexNameToken name)
	{
		LexNameToken rv = new LexNameToken(name.getModule(), name.getName(), name.getLocation(), name.isOld(), name.getExplicit())
		{
			private static final long serialVersionUID = 1L;

			@Override
			public boolean equals(Object other)
			{
				if (super.equals(other))
				{
					LexNameToken lother = (LexNameToken)other;
					
					if (typeQualifier != null && lother.typeQualifier != null)
					{
						TypeComparator comp = new TypeComparator(assistantFactory);
						return comp.compatible(typeQualifier, lother.typeQualifier);
					}
					else
					{
						return true;
					}
				}
				else
				{
					return false;
				}
			}
			
			@Override
			public int hashCode()
			{
				return name.hashCode() + module.hashCode();
			}
		};
		
		rv.setTypeQualifier(name.getTypeQualifier());
		return rv;
	}

	/**
	 * Return true if the name sought is reachable via the next set of names passed using
	 * the dependency map. The stack passed records the path taken to find a cycle.
	 */
	private boolean reachable(ILexNameToken sought, LexNameSet nextset,
		Map<ILexNameToken, LexNameSet> dependencies, Stack<ILexNameToken> stack)
	{
		if (nextset == null)
		{
			return false;
		}
		
		if (nextset.contains(sought))
		{
			stack.push(sought);
			return true;
		}
		
		for (ILexNameToken nextname: nextset)
		{
			if (stack.contains(nextname))	// Been here before!
			{
				return false;
			}
			
			stack.push(nextname);
			
			if (reachable(sought, dependencies.get(nextname), dependencies, stack))
			{
				return true;
			}
			
			stack.pop();
		}
		
		return false;
    }
    
	public static void suppressErrors(boolean sup)
	{
		suppress =sup;
	}

	public static void report(int number, String problem, ILexLocation location)
	{
		if (suppress) return;
		VDMError error = new VDMError(number, problem, location);
		// System.out.println(error.toString());
		errors.add(error);
		lastMessage = error;

		for (IStatusListener listner : listners)
		{
			listner.report(error);
		}

		if (errors.size() >= MAX - 1)
		{
			errors.add(new VDMError(10, "Too many type checking errors", location));
			throw new InternalException(10, "Too many type checking errors");
		}
	}

	public static void warning(int number, String problem, ILexLocation location)
	{
		if (suppress) return;
		VDMWarning warning = new VDMWarning(number, problem, location);
		warnings.add(warning);
		lastMessage = warning;

		for (IStatusListener listner : listners)
		{
			listner.warning(warning);
		}
	}

	public static void detail(String tag, Object obj)
	{
		if (lastMessage != null)
		{
			lastMessage.add(tag + ": " + obj);
		}
	}

	public static void detail2(String tag1, Object obj1, String tag2,
			Object obj2)
	{
		detail(tag1, obj1);
		detail(tag2, obj2);
	}

	public static void clearErrors()
	{
		errors.clear();
		warnings.clear();
	}

	public static int getErrorCount()
	{
		return errors.size();
	}

	public static int getWarningCount()
	{
		return warnings.size();
	}

	public static List<VDMError> getErrors()
	{
		return errors;
	}

	public static List<VDMWarning> getWarnings()
	{
		return warnings;
	}

	public static void printErrors(PrintWriter out)
	{
		for (VDMError e : errors)
		{
			out.println(e.toString());
		}
	}

	public static void printWarnings(PrintWriter out)
	{
		for (VDMWarning w : warnings)
		{
			out.println(w.toString());
		}
	}

	public static void addStatusListner(IStatusListener listner)
	{
		listners.add(listner);
	}

	public static void removeStatusListner(IStatusListener listner)
	{
		listners.remove(listner);
	}
}
