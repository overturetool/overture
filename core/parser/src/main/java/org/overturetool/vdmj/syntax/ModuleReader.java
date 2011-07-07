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

package org.overturetool.vdmj.syntax;

import java.io.File;
import java.util.List;
import java.util.Vector;

import org.overture.ast.modules.AModuleModules;
import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.definitions.TypeDefinition;
import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.lex.LexIdentifierToken;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.lex.LexStringToken;
import org.overturetool.vdmj.lex.LexToken;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.lex.VDMToken;
import org.overturetool.vdmj.messages.LocatedException;
import org.overturetool.vdmj.modules.DLModule;
import org.overturetool.vdmj.modules.Export;
import org.overturetool.vdmj.modules.ExportAll;
import org.overturetool.vdmj.modules.ExportedFunction;
import org.overturetool.vdmj.modules.ExportedOperation;
import org.overturetool.vdmj.modules.ExportedType;
import org.overturetool.vdmj.modules.ExportedValue;
import org.overturetool.vdmj.modules.Import;
import org.overturetool.vdmj.modules.ImportAll;
import org.overturetool.vdmj.modules.ImportFromModule;
import org.overturetool.vdmj.modules.ImportedFunction;
import org.overturetool.vdmj.modules.ImportedOperation;
import org.overturetool.vdmj.modules.ImportedType;
import org.overturetool.vdmj.modules.ImportedValue;
import org.overturetool.vdmj.modules.Module;
import org.overturetool.vdmj.modules.ModuleExports;
import org.overturetool.vdmj.modules.ModuleImports;
import org.overturetool.vdmj.modules.ModuleList;
import org.overturetool.vdmj.types.FunctionType;
import org.overturetool.vdmj.types.Type;



/**
 * A syntax analyser to parse modules.
 */

public class ModuleReader extends SyntaxReader
{
	public ModuleReader(LexTokenReader reader)
	{
		super(reader);
	}

	public List<AModuleModules> readModules()
	{
		List<AModuleModules> modules = new Vector<AModuleModules>();

		try
		{
			if (lastToken().is(VDMToken.EOF))
			{
				return modules;		// The file is empty
			}

    		if (lastToken().isNot(VDMToken.MODULE) &&
    			!DefinitionReader.newSection(lastToken()))
    		{
    			warning(5015,
    				"LaTeX source should start with %comment, \\document, \\section or \\subsection",
    				lastToken().location);
    		}

    		while (lastToken().isNot(VDMToken.EOF) && lastToken().isNot(VDMToken.END))
    		{
    			switch (lastToken().type)
    			{
    				case MODULE:
    					modules.add(readModule());
    					break;

    				case DLMODULE:
    					modules.add(readDLModule());
    					break;

    				case IDENTIFIER:
    					LexIdentifierToken id = lastIdToken();

    					if (id.name.equals("class"))
    					{
    						throwMessage(2260, "Module starts with 'class' instead of 'module'");
    					}
    					// else fall through to a flat definition...

    				default:
    					modules.add(readFlatModule());
    					break;
    			}
    		}
    	}
    	catch (LocatedException e)
    	{
    		VDMToken[] end = new VDMToken[0];
    		report(e, end, end);
    	}

		return modules;
	}

	public static ImportFromModule importAll(LexIdentifierToken from)
	{
		List<List<Import>> types = new Vector<List<Import>>();
		LexNameToken all = new LexNameToken(from.name, "all", from.location);
		List<Import> impAll = new Vector<Import>();
		impAll.add(new ImportAll(all));
		types.add(impAll);
		return new ImportFromModule(from, types);
	}

	private AModuleModules readFlatModule() throws ParserException, LexException
	{
		File file = lastToken().location.file;
		setCurrentModule("DEFAULT");
		DefinitionList definitions = getDefinitionReader().readDefinitions();
		return new AModuleModules(file, definitions);
	}

	private AModuleModules readModule() throws ParserException, LexException
	{
		LexIdentifierToken name = new LexIdentifierToken("?", false, lastToken().location);
		ModuleImports imports = null;
		ModuleExports exports = null;

		try
		{
			setCurrentModule("");
			checkFor(VDMToken.MODULE, 2170, "Expecting 'module' at module start");
			name = readIdToken("Expecting identifier after 'module'");
			setCurrentModule(name.name);

			if (lastToken().is(VDMToken.IMPORTS))
			{
				imports = readImports(name);
			}

			if (lastToken().is(VDMToken.EXPORTS))
			{
				exports = readExports();
			}

			// Be forgiving about the ordering...

			if (imports == null && lastToken().is(VDMToken.IMPORTS))
			{
				imports = readImports(name);
			}
		}
		catch (LocatedException e)
		{
			VDMToken[] after = { VDMToken.DEFINITIONS };
			VDMToken[] upto = { VDMToken.END };
			report(e, after, upto);
		}

		DefinitionList defs = null;

		if (lastToken().is(VDMToken.DEFINITIONS))
		{
			nextToken();
			defs = getDefinitionReader().readDefinitions();
		}
		else
		{
			defs = new DefinitionList();
		}

		checkFor(VDMToken.END, 2171, "Expecting 'end' after module definitions");
		LexIdentifierToken endname =
			readIdToken("Expecting 'end <name>' after module definitions");

		if (name != null &&	!name.equals(endname))
		{
			throwMessage(2049, "Expecting 'end " + name.name + "'");
		}

		LexLocation.addSpan(idToName(name), lastToken());
		return new AModuleModules(name, imports, exports, defs);
	}

	private AModuleModules readDLModule() throws ParserException, LexException
	{
		LexIdentifierToken name = new LexIdentifierToken("?", false, lastToken().location);
		ModuleImports imports = null;
		ModuleExports exports = null;
		LexStringToken library = null;

		try
		{
			checkFor(VDMToken.DLMODULE, 2172, "Expecting 'dlmodule' at module start");
			name = readIdToken("Expecting identifier after 'dlmodule'");
			setCurrentModule(name.name);

			if (lastToken().is(VDMToken.IMPORTS))
			{
				imports = readImports(name);
			}

			if (lastToken().is(VDMToken.EXPORTS))
			{
				exports = readExports();
			}

			if (lastToken().is(VDMToken.USELIB))
			{
				if (nextToken().is(VDMToken.STRING))
				{
					library = (LexStringToken)lastToken();
					nextToken();
				}
				else
				{
					throwMessage(2050, "Expecting library name after 'uselib'");
				}
			}
		}
		catch (LocatedException e)
		{
			VDMToken[] after = {};
			VDMToken[] upto = { VDMToken.END };
			report(e, after, upto);
		}

		checkFor(VDMToken.END, 2173, "Expecting 'end' after dlmodule definitions");
		LexIdentifierToken endname =
			readIdToken("Expecting 'end <name>' after dlmodule definitions");

		if (name != null &&	!name.equals(endname))
		{
			throwMessage(2051, "Expecting 'end " + name.name + "'");
		}

		return new DLModule(name, imports, exports, library);
	}

	private ModuleExports readExports() throws ParserException, LexException
	{
		checkFor(VDMToken.EXPORTS, 2174, "Malformed imports? Expecting 'exports' section");
		return new ModuleExports(readExportsFromModule());
	}

	private List<List<Export>> readExportsFromModule()
		throws ParserException, LexException
	{
		List<List<Export>> types = new Vector<List<Export>>();

		if (lastToken().is(VDMToken.ALL))
		{
			LexNameToken all = new LexNameToken(getCurrentModule(), "all", lastToken().location);
			List<Export> expAll = new Vector<Export>();
			expAll.add(new ExportAll(all.location));
			types.add(expAll);
			nextToken();
			return types;
		}

		types.add(readExportsOfOneType());

		while (newType())
		{
			types.add(readExportsOfOneType());
		}

		return types;
	}

	private List<Export> readExportsOfOneType()
		throws ParserException, LexException
	{
		switch (lastToken().type)
		{
			case TYPES:
				nextToken();
				return readExportedTypes();

			case VALUES:
				nextToken();
				return readExportedValues();

			case FUNCTIONS:
				nextToken();
				return readExportedFunctions();

			case OPERATIONS:
				nextToken();
				return readExportedOperations();
		}

		throwMessage(2052, "Expecting 'all', 'types', 'values', 'functions' or 'operations'");
		return null;
	}

	private List<Export> readExportedTypes()
		throws ParserException, LexException
	{
		List<Export> list = new Vector<Export>();
		list.add(readExportedType());

		while (lastToken().isNot(VDMToken.DEFINITIONS) &&
			   lastToken().isNot(VDMToken.USELIB) && !newType())
		{
			list.add(readExportedType());
		}

		return list;
	}

	private ExportedType readExportedType()
		throws ParserException, LexException
	{
		boolean struct = lastToken().is(VDMToken.STRUCT);
		if (struct) nextToken();
		LexNameToken name = readNameToken("Expecting exported type name");
		ignore(VDMToken.SEMICOLON);
		return new ExportedType(name, struct);
	}

	private List<Export> readExportedValues()
		throws ParserException, LexException
	{
		List<Export> list = new Vector<Export>();
		list.add(readExportedValue());

		while (lastToken().isNot(VDMToken.DEFINITIONS) &&
			   lastToken().isNot(VDMToken.USELIB) && !newType())
		{
			list.add(readExportedValue());
		}

		return list;
	}

	private ExportedValue readExportedValue()
		throws ParserException, LexException
	{
		LexToken token = lastToken();
		LexNameList nameList = readIdList();
		checkFor(VDMToken.COLON, 2175, "Expecting ':' after export name");
		Type type = getTypeReader().readType();
		ignore(VDMToken.SEMICOLON);
		return new ExportedValue(token.location, nameList, type);
	}

	private List<Export> readExportedFunctions()
		throws ParserException, LexException
	{
		List<Export> list = new Vector<Export>();
		list.add(readExportedFunction());

		while (lastToken().is(VDMToken.IDENTIFIER) || lastToken().is(VDMToken.NAME))
		{
			list.add(readExportedFunction());
		}

		return list;
	}

	private ExportedFunction readExportedFunction()
		throws ParserException, LexException
	{
		LexToken token = lastToken();
		LexNameList nameList = readIdList();
		checkFor(VDMToken.COLON, 2176, "Expecting ':' after export name");
		LexToken tloc = lastToken();
		Type type = getTypeReader().readType();

		if (!(type instanceof FunctionType))
		{
			throwMessage(2053, "Exported function is not a function type", tloc);
		}

		ignore(VDMToken.SEMICOLON);
		return new ExportedFunction(token.location, nameList, type);
	}

	private List<Export> readExportedOperations()
		throws ParserException, LexException
	{
		List<Export> list = new Vector<Export>();
		list.add(readExportedOperation());

		while (lastToken().is(VDMToken.IDENTIFIER) || lastToken().is(VDMToken.NAME))
		{
			list.add(readExportedOperation());
		}

		return list;
	}

	private ExportedOperation readExportedOperation()
		throws ParserException, LexException
	{
		LexToken token = lastToken();
		LexNameList nameList = readIdList();
		checkFor(VDMToken.COLON, 2177, "Expecting ':' after export name");
		Type type = getTypeReader().readOperationType();
		ignore(VDMToken.SEMICOLON);
		return new ExportedOperation(token.location, nameList, type);
	}

	private LexNameList readIdList()
		throws ParserException, LexException
	{
		LexNameList list = new LexNameList();
		list.add(readNameToken("Expecting name list"));
		ignoreTypeParams();

		while (ignore(VDMToken.COMMA))
		{
			list.add(readNameToken("Expecting name list"));
			ignoreTypeParams();
		}

		return list;
	}

	private ModuleImports readImports(LexIdentifierToken name)
		throws ParserException, LexException
	{
		checkFor(VDMToken.IMPORTS, 2178, "Expecting 'imports'");
		List<ImportFromModule> imports = new Vector<ImportFromModule>();
		imports.add(readImportDefinition());

		while (ignore(VDMToken.COMMA))
		{
			imports.add(readImportDefinition());
		}

		return new ModuleImports(name, imports);
	}

	private ImportFromModule readImportDefinition()
		throws ParserException, LexException
	{
		checkFor(VDMToken.FROM, 2179, "Expecting 'from' in import definition");
		LexIdentifierToken from = readIdToken("Expecting module identifier after 'from'");
		return new ImportFromModule(from, readImportsFromModule(from));
	}

	private List<List<Import>> readImportsFromModule(LexIdentifierToken from)
		throws ParserException, LexException
	{
		List<List<Import>> types = new Vector<List<Import>>();

		if (lastToken().is(VDMToken.ALL))
		{
			LexNameToken all = new LexNameToken(getCurrentModule(), "all", lastToken().location);
			List<Import> impAll = new Vector<Import>();
			impAll.add(new ImportAll(all));
			types.add(impAll);
			nextToken();
			return types;
		}

		types.add(readImportsOfOneType(from));

		while (newType())
		{
			types.add(readImportsOfOneType(from));
		}

		return types;
	}

	private List<Import> readImportsOfOneType(LexIdentifierToken from)
		throws ParserException, LexException
	{
		switch (lastToken().type)
		{
			case TYPES:
				nextToken();
				return readImportedTypes(from);

			case VALUES:
				nextToken();
				return readImportedValues(from);

			case FUNCTIONS:
				nextToken();
				return readImportedFunctions(from);

			case OPERATIONS:
				nextToken();
				return readImportedOperations(from);
		}

		throwMessage(2054, "Expecting types, values, functions or operations");
		return null;
	}

	private List<Import> readImportedTypes(LexIdentifierToken from)
		throws ParserException, LexException
	{
		List<Import> list = new Vector<Import>();
		list.add(readImportedType(from));

		while (lastToken().is(VDMToken.IDENTIFIER) || lastToken().is(VDMToken.NAME))
		{
			list.add(readImportedType(from));
		}

		return list;
	}

	private ImportedType readImportedType(LexIdentifierToken from)
		throws ParserException, LexException
	{
		String savedModule = getCurrentModule();

		try
		{
			reader.push();
			setCurrentModule(from.name);	// So names are from "from" in...
			TypeDefinition def = getDefinitionReader().readTypeDefinition();
			setCurrentModule(savedModule);	// and restore
			reader.unpush();

			LexNameToken renamed = null;

			if (ignore(VDMToken.RENAMED))
			{
				renamed = readNameToken("Expected renamed type name");
			}

			ignore(VDMToken.SEMICOLON);
			return new ImportedType(def, renamed);
		}
		catch (ParserException e)
		{
			reader.pop();
			setCurrentModule(savedModule);
		}

		LexNameToken name = readNameToken("Expecting imported type name");
		LexNameToken defname = getDefName(from, name);
		LexNameToken renamed = null;

		if (ignore(VDMToken.RENAMED))
		{
			renamed = readNameToken("Expected renamed type name");
		}

		ignore(VDMToken.SEMICOLON);
		return new ImportedType(defname, renamed);
	}

	private List<Import> readImportedValues(LexIdentifierToken from)
		throws ParserException, LexException
	{
		List<Import> list = new Vector<Import>();
		list.add(readImportedValue(from));

		while (lastToken().is(VDMToken.IDENTIFIER) || lastToken().is(VDMToken.NAME))
		{
			list.add(readImportedValue(from));
		}

		return list;
	}

	private ImportedValue readImportedValue(LexIdentifierToken from)
		throws ParserException, LexException
	{
		LexNameToken name = readNameToken("Expecting imported value name");
		LexNameToken defname = getDefName(from, name);
		Type type = null;

		if (lastToken().is(VDMToken.COLON))
		{
			nextToken();
			type = getTypeReader().readType();
		}

		LexNameToken renamed = null;

		if (ignore(VDMToken.RENAMED))
		{
			renamed = readNameToken("Expected renamed value name");
		}

		ignore(VDMToken.SEMICOLON);
		return new ImportedValue(defname, type, renamed);
	}

	private List<Import> readImportedFunctions(LexIdentifierToken from)
		throws ParserException, LexException
	{
		List<Import> list = new Vector<Import>();
		list.add(readImportedFunction(from));

		while (lastToken().is(VDMToken.IDENTIFIER) || lastToken().is(VDMToken.NAME))
		{
			list.add(readImportedFunction(from));
		}

		return list;
	}

	private ImportedFunction readImportedFunction(LexIdentifierToken from)
		throws ParserException, LexException
	{
		LexNameToken name =	readNameToken("Expecting imported function name");
		LexNameToken defname = getDefName(from, name);
		LexNameList typeParams = getDefinitionReader().readTypeParams();

		Type type = null;

		if (lastToken().is(VDMToken.COLON))
		{
			nextToken();
			LexToken tloc = lastToken();
			type = getTypeReader().readType();

			if (!(type instanceof FunctionType))
			{
				throwMessage(2055, "Imported function is not a function type", tloc);
			}
		}

		LexNameToken renamed = null;

		if (ignore(VDMToken.RENAMED))
		{
			renamed = readNameToken("Expected renamed function name");
		}

		ignore(VDMToken.SEMICOLON);
		return new ImportedFunction(defname, type, typeParams, renamed);
	}

	private List<Import> readImportedOperations(LexIdentifierToken from)
		throws ParserException, LexException
	{
		List<Import> list = new Vector<Import>();
		list.add(readImportedOperation(from));

		while (lastToken().is(VDMToken.IDENTIFIER) || lastToken().is(VDMToken.NAME))
		{
			list.add(readImportedOperation(from));
		}

		return list;
	}

	private ImportedOperation readImportedOperation(LexIdentifierToken from)
		throws ParserException, LexException
	{
		LexNameToken name = readNameToken("Expecting imported operation name");
		LexNameToken defname = getDefName(from, name);
		Type type = null;

		if (lastToken().is(VDMToken.COLON))
		{
			nextToken();
			type = getTypeReader().readOperationType();
		}

		LexNameToken renamed = null;

		if (ignore(VDMToken.RENAMED))
		{
			renamed = readNameToken("Expected renamed operation name");
		}

		ignore(VDMToken.SEMICOLON);
		return new ImportedOperation(defname, type, renamed);
	}

	private boolean newType() throws LexException
	{
		switch (lastToken().type)
		{
			case TYPES:
			case VALUES:
			case FUNCTIONS:
			case OPERATIONS:
			case EOF:
				return true;
		}

		return false;
	}

	private LexNameToken getDefName(LexIdentifierToken impmod, LexNameToken name)
	{
    	if (name.module.equals(getCurrentModule()))		//ie. it was an id
    	{
    		return new LexNameToken(impmod.name, name.name, name.location);
    	}

    	return name;
	}

	private void ignoreTypeParams() throws LexException
	{
		if (lastToken().is(VDMToken.SEQ_OPEN))
		{
			while (!ignore(VDMToken.SEQ_CLOSE))
			{
				nextToken();
			}
		}
	}
}
