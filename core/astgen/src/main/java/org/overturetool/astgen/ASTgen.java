/*******************************************************************************
 *
 *	Copyright (c) 2009 Fujitsu Services Ltd.
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

package org.overturetool.astgen;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;


import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.DefinitionSet;
import org.overturetool.vdmj.definitions.TypeDefinition;
import org.overturetool.vdmj.definitions.ValueDefinition;
import org.overturetool.vdmj.expressions.StringLiteralExpression;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.patterns.IdentifierPattern;
import org.overturetool.vdmj.syntax.ClassReader;
import org.overturetool.vdmj.typechecker.ClassTypeChecker;
import org.overturetool.vdmj.typechecker.TypeChecker;
import org.overturetool.vdmj.types.CharacterType;
import org.overturetool.vdmj.types.NamedType;
import org.overturetool.vdmj.types.QuoteType;
import org.overturetool.vdmj.types.RecordType;
import org.overturetool.vdmj.types.SeqType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.UnionType;

public class ASTgen
{
	private static LanguageWriter writer;
	private static ClassList classes;
	private static AbstractTree tree;
	private static DefinitionSet done;

	public static void main(String[] args)
	{
		String language = null;
		Kind kind = Kind.INTF;
		File directory = null;
		List<File> grammars = new Vector<File>();
		List<String> names = new Vector<String>();

		Iterator<String> argit = Arrays.asList(args).iterator();

		while (argit.hasNext())
		{
			String arg = argit.next();

			if (arg.equals("-lang"))
			{
				if (argit.hasNext())
				{
					language = argit.next();
				}
				else
				{
					usage();
				}
			}
			else if (arg.equals("-kind"))
			{
				if (argit.hasNext())
				{
					kind = Kind.valueOf(argit.next().toUpperCase());
				}
				else
				{
					usage();
				}
			}
			else if (arg.equals("-out"))
			{
				if (argit.hasNext())
				{
					directory = new File(argit.next());
				}
				else
				{
					usage();
				}
			}
			else if (arg.equals("-class"))
			{
				if (argit.hasNext())
				{
					names.add(argit.next());
				}
				else
				{
					usage();
				}
			}
			else if (arg.startsWith("-"))
			{
				usage();
			}
			else
			{
				grammars.add(new File(arg));
			}
		}

		if ((!language.equals("java") && !language.equals("vdm")) ||
			directory == null || grammars.isEmpty() || names.isEmpty())
		{
			usage();
		}

		int errs = generate(language, kind, directory, grammars, names);

		System.out.println(errs > 0 ? "Errors occurred" : "OK");
	}

	/**
	 * Generates the source from one or more AST classes
	 * @param language java or vdm
	 * @param kind INTF for interfaces/abstracts or IMPL for implementation
	 * @param directory -out
	 * @param grammars files
	 * @param names -class
	 * @return 0 if ok
	 */
	public static int generate(String language, Kind kind, File directory,
			List<File> grammars, List<String> names)
	{
		parse(grammars);
		AbstractTreeList trees = check(names, kind);

		TypeChecker tc = new ClassTypeChecker(classes);
		tc.typeCheck();

		if (TypeChecker.getErrorCount() > 0)
		{
			TypeChecker.printErrors(new PrintWriter(System.err, true));
			System.exit(5);
		}

		int errs = processTrees(language, directory, trees);
		return errs;
	}

	private static void usage()
	{
		System.err.println("Usage: ASTgen -lang <java|vdm> -kind <intf|impl> -out <dir> { -class <name> } <grammar files>");
		System.exit(1);
	}

	private static void parse(List<File> grammars)
	{
		classes = new ClassList();
		int errs = 0;

		for (File file: grammars)
		{
			LexTokenReader ltr = new LexTokenReader(file, Dialect.VDM_PP);
			ClassReader syn = new ClassReader(ltr);
			classes.addAll(syn.readClasses());

    		if (syn.getErrorCount() > 0)
    		{
    			syn.printErrors(new PrintWriter(System.err, true));
    			errs++;
    		}
		}

		if (errs > 0)
		{
			System.exit(2);
		}
	}

	private static AbstractTreeList check(List<String> names, Kind kind)
	{
		AbstractTreeList trees = new AbstractTreeList();
		int errs = 0;

		for (ClassDefinition cls: classes)
		{
			if (cls.supernames.size() > 1)
			{
				System.err.println("Only one superclass allowed " + cls.location);
				errs++;
			}

			String pkg = null;

    		for (Definition d: cls.definitions)
    		{
    			if (d instanceof TypeDefinition)
    			{
        			TypeDefinition td = (TypeDefinition)d;

        			if (td.invPattern != null)
        			{
        				System.err.println("No type invariants allowed " + td.invPattern.location);
        				errs++;
        			}
    			}
    			else if (d instanceof ValueDefinition)
    			{
    				ValueDefinition vd = (ValueDefinition)d;

    				if (vd.pattern instanceof IdentifierPattern)
    				{
    					IdentifierPattern p = (IdentifierPattern)vd.pattern;

    					if (p.name.name.equals("package"))
    					{
    						if (vd.exp instanceof StringLiteralExpression)
    						{
    							StringLiteralExpression s = (StringLiteralExpression)vd.exp;
    							pkg = s.value.value;
    						}
    					}
    					else
    					{
    	    				System.err.println("Only 'package' value allowed " + vd.location);
    	    				errs++;
    					}
    				}
    				else
    				{
        				System.err.println("Only name=value patterns allowed " + vd.location);
        				errs++;
    				}
    			}
    			else
    			{
    				System.err.println("Only type definitions allowed " + d.location);
    				errs++;
    			}
    		}

    		if (pkg == null)
    		{
				System.err.println("Class does not defined a package " + cls.location);
				errs++;
    		}
    		else
    		{
    			trees.add(new AbstractTree(cls, pkg, kind));
    		}
		}

		trees.setDerived();

		for (String name: names)
		{
			AbstractTree found = trees.find(name);

			if (found == null)
			{
				System.err.println("Class " + name + " not in grammar file");
				errs++;
			}
			else
			{
				found.process = true;
			}
		}

		if (errs > 0)
		{
			System.exit(4);
		}

		return trees;
	}

	private static int processTrees(
		String language, File directory, AbstractTreeList trees)
	{
		int errs = 0;

		for (AbstractTree ast: trees)
		{
			if (ast.process)
			{
	    		tree = ast;
	    		done = new DefinitionSet();
	    		writer = LanguageWriter.factory(language, ast.kind);
	    		writer.setDetails(directory, ast);

	    		try
	    		{
	    			writer.createAbstractInterface(ast.getNodeName(), null);
	    		}
	    		catch (IOException e)
	    		{
	    			System.err.println("Exception: " + e.getMessage());
	    			errs++;
	    		}

	    		for (TypeDefinition td: ast.getTypeDefinitions())
	    		{
	    			if (!done.contains(td))
	    			{
	           			try
	    				{
	    					errs += processDefinition(td, ast.getNodeName());
	    				}
	    				catch (IOException e)
	    				{
	    					System.err.println("Exception: " + e.getMessage());
	    					errs++;
	    				}
	    			}
	    		}
			}
		}

		return errs;
	}

	private static int processDefinition(TypeDefinition def, LexNameToken parent)
		throws IOException
	{
		int errs = 0;

		if (!done.contains(def))
		{
			done.add(def);

			if (def.type instanceof NamedType)
    		{
    			NamedType nt = (NamedType)def.type;
    			errs += processNamedType(nt, parent);
    		}
    		else if (def.type instanceof RecordType)
    		{
    			RecordType rt = (RecordType)def.type;
    			errs += processRecordType(rt, parent);
    		}
    		else
    		{
    			System.err.println("Type should be named or record " + def.location);
    			errs++;
    		}
		}

		return errs;
	}

	private static int processNamedType(NamedType nt, LexNameToken parent)
		throws IOException
	{
		int errs = 0;

		if (nt.type instanceof NamedType)
		{
			NamedType st = (NamedType)nt.type;
			errs = processSimpleName(st, parent);
		}
		else if (nt.type instanceof UnionType)
		{
			UnionType ut = (UnionType)nt.type;
			errs = processUnionType(nt.typename, ut, parent);
		}
		else if (nt.type instanceof SeqType)
		{
			SeqType seq = (SeqType)nt.type;

			if (!(seq.seqof instanceof CharacterType))
			{
	    		System.err.println("Can only be seq of char " + nt.location);
	    		return 1;
			}
		}

		return errs;
	}

	private static int processUnionType(
		LexNameToken name, UnionType ut, LexNameToken parent)
		throws IOException
	{
		int quotes = 0;

		for (Type t: ut.types)
		{
			if (t instanceof QuoteType)
			{
				quotes++;
			}
		}

		if (quotes > 0)
		{
			if (quotes != ut.types.size())
			{
	    		System.err.println("All elements must be quote types " + ut.location);
	    		return 1;
			}
			else
			{
				writer.createQuoteEnumeration(name, ut.types, parent);
				return 0;
			}
		}
		else
		{
    		writer.createAbstractInterface(name, parent);

    		int errs = 0;

    		for (Type t: ut.types)
    		{
    			TypeDefinition def = tree.findDefinition(t);
    			errs += processDefinition(def, name);
    		}

    		return errs;
		}
	}

	private static int processSimpleName(NamedType nt, LexNameToken parent)
		throws IOException
	{
		writer.createAbstractInterface(nt.typename, parent);
		TypeDefinition def = tree.findDefinition(nt.type);
		return processDefinition(def, nt.typename);
	}

	private static int processRecordType(RecordType rt, LexNameToken parent)
		throws IOException
	{
		writer.createRecordInterface(rt, parent);
		return 0;
	}
}
