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

package org.overturetool.vdmj.modules;

import java.io.Serializable;

import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.definitions.RenamedDefinition;
import org.overturetool.vdmj.definitions.StateDefinition;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.lex.LexIdentifierToken;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ContextException;
import org.overturetool.vdmj.runtime.StateContext;
import org.overturetool.vdmj.statements.Statement;
import org.overturetool.vdmj.typechecker.ModuleEnvironment;

/**
 * A class holding all the details for one module.
 */

public class Module implements Serializable
{
	private static final long serialVersionUID = 1L;

	/** The module name. */
	public final LexIdentifierToken name;
	/** A list of import declarations. */
	public final ModuleImports imports;
	/** A list of export declarations. */
	public final ModuleExports exports;
	/** A list of definitions created in the module. */
	public final DefinitionList defs;

	/** Those definitions which are exported. */
	public DefinitionList exportdefs;
	/** Definitions of imported objects from other modules. */
	public DefinitionList importdefs;
	/** True if the module was loaded from an object file. */
	public boolean loaded = false;
	/** The default module number for flat definitions. */
	public static int defNumber = 1;

	/**
	 * Create a module from the given name and definitions.
	 */

	public Module(LexIdentifierToken name,
		ModuleImports imports,
		ModuleExports exports,
		DefinitionList defs)
	{
		this.name = name;
		this.imports = imports;
		this.exports = exports;
		this.defs = defs;

		exportdefs = new DefinitionList();	// By default, export nothing
		importdefs = new DefinitionList();	// and import nothing
	}

	/**
	 * Create a module with a default name from the given definitions.
	 */

	public Module(DefinitionList defs)
	{
		if (defs.isEmpty())
		{
			this.name =	new LexIdentifierToken("DEFAULT", false, new LexLocation());
		}
		else
		{
    		this.name = nextName(defs.get(0).location);
    		defNumber++;
 		}

		this.imports = null;
		this.exports = null;
		this.defs = defs;

		exportdefs = new DefinitionList();	// Export nothing
		importdefs = new DefinitionList();	// and import nothing
	}

	/**
	 * Create a module called DEFAULT with no definitions.
	 */

	public Module()
	{
		this(new DefinitionList());
	}

	/**
	 * Generate the next default module name. Default names are the
	 * string "DEFAULT" plus a number starting at 1. A specification
	 * comprising three flat files would be parsed into three modules
	 * called DEFAULT1, DEFAULT2 and DEFAULT3.
	 *
	 * @param location	The textual location of the name
	 * @return	The next default module name.
	 */

	public static LexIdentifierToken nextName(LexLocation location)
	{
		return new LexIdentifierToken("DEFAULT" + defNumber, false, location);
	}

	/**
	 * Generate the exportdefs list of definitions. The exports list of
	 * export declarations is processed by searching the defs list of
	 * locally defined objects. The exportdefs field is populated with
	 * the result.
	 */

	public void processExports()
	{
		if (exports != null)
		{
			exportdefs.addAll(exports.getDefinitions(defs));
		}
	}

	/**
	 * Generate the importdefs list of definitions. The imports list of
	 * import declarations is processed by searching the module list passed
	 * in. The importdefs field is populated with the result.
	 */

	public void processImports(ModuleList allModules)
	{
		if (imports != null)
		{
			importdefs.clear();
			importdefs.addAll(imports.getDefinitions(allModules));
		}
	}

	/**
	 * Type check the imports, compared to their export definitions.
	 */

	public void typeCheckImports()
	{
		if (imports != null)
		{
			imports.typeCheck(new ModuleEnvironment(this));
		}
	}

	/**
	 * Return the module's state context, if any. Modules which define
	 * state produce a {@link Context} object that contains the state field
	 * values. This is independent of the initial context.
	 *
	 * @return	The state context, or null.
	 */

	public Context getStateContext()
	{
		StateDefinition sdef = defs.findStateDefinition();

		if (sdef != null)
		{
			return sdef.getStateContext();
		}

		return null;
	}

	/**
	 * Initialize the system for execution from this module. The initial
	 * {@link Context} is created, and populated with name/value pairs from the
	 * local definitions and the imported definitions. If state is defined
	 * by the module, this is also initialized, creating the state Context.
	 *
	 * @return True if initialized OK.
	 */

	public ContextException initialize(StateContext initialContext)
	{
		ContextException trouble = null;

		for (Definition d: importdefs)
		{
			if (d instanceof RenamedDefinition)
			{
				try
				{
					initialContext.put(d.getNamedValues(initialContext));
				}
				catch (ContextException e)
				{
					trouble = e;	// Carry on...
				}
			}
		}

		for (Definition d: defs)
		{
			try
			{
				initialContext.put(d.getNamedValues(initialContext));
			}
			catch (ContextException e)
			{
				trouble = e;	// Carry on...
			}
		}

		StateDefinition sdef = defs.findStateDefinition();

		if (sdef != null)
		{
			sdef.initState(initialContext);
		}

		return trouble;
	}

	/**
	 * Find the first {@link Statement} in the module that starts on a given line.
	 *
	 * @param lineno The line number to search for.
	 * @return	The first {@link Statement} on that line, or null.
	 */

	public Statement findStatement(int lineno)
	{
		return defs.findStatement(lineno);
	}

	/**
	 * Find the first {@link Expression} in the module that starts on a given line.
	 *
	 * @param lineno The line number to search for.
	 * @return	The first {@link Expression} on that line, or null.
	 */

	public Expression findExpression(int lineno)
	{
		return defs.findExpression(lineno);
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("module " + name.name + "\n");

		if (imports != null)
		{
			sb.append("\nimports\n\n");
			sb.append(imports.toString());
		}

		if (exports != null)
		{
			sb.append("\nexports\n\n");
			sb.append(exports.toString());
		}
		else
		{
			sb.append("\nexports all\n\n");
		}

		if (defs != null)
		{
			sb.append("\ndefinitions\n\n");

			for (Definition def: defs)
			{
				sb.append(def.toString() + "\n");
			}
		}

		sb.append("\nend " + name.name + "\n");

		return sb.toString();
	}

	public ProofObligationList getProofObligations()
	{
		return defs.getProofObligations(new POContextStack());
	}
}
