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

package org.overture.parser.syntax;

import java.io.File;
import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexIdentifierToken;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.lex.LexToken;
import org.overture.ast.lex.VDMToken;
import org.overture.ast.modules.AFromModuleImports;
import org.overture.ast.modules.AFunctionExport;
import org.overture.ast.modules.AFunctionValueImport;
import org.overture.ast.modules.AModuleExports;
import org.overture.ast.modules.AModuleImports;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.modules.AOperationExport;
import org.overture.ast.modules.AOperationValueImport;
import org.overture.ast.modules.ATypeExport;
import org.overture.ast.modules.ATypeImport;
import org.overture.ast.modules.AValueExport;
import org.overture.ast.modules.PExport;
import org.overture.ast.modules.PImport;
import org.overture.ast.modules.SValueImport;
import org.overture.ast.types.PType;
import org.overture.ast.util.ClonableFile;
import org.overture.ast.util.modules.ModuleList;
import org.overture.config.Settings;
import org.overture.parser.lex.LexException;
import org.overture.parser.lex.LexTokenReader;
import org.overture.parser.messages.LocatedException;

/**
 * A syntax analyser to parse modules.
 */

public class ModuleReader extends SyntaxReader
{
	public ModuleReader(LexTokenReader reader)
	{
		super(reader);
	}

	public ModuleList readModules()
	{
		ModuleList modules = new ModuleList();

		try
		{
			if (lastToken().is(VDMToken.EOF))
			{
				return modules; // The file is empty
			}

			if (lastToken().isNot(VDMToken.MODULE)
					&& !DefinitionReader.newSection(lastToken()))
			{
				warning(5015, "LaTeX source should start with %comment, \\document, \\section or \\subsection", lastToken().location);
			}

			while (lastToken().isNot(VDMToken.EOF)
					&& lastToken().isNot(VDMToken.END))
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

						if (id.getName().equals("class"))
						{
							throwMessage(2260, "Module starts with 'class' instead of 'module'");
						}
						// else fall through to a flat definition...

					default:
						modules.add(readFlatModule());
						break;
				}
			}
		} catch (LocatedException e)
		{
			VDMToken[] end = new VDMToken[0];
			report(e, end, end);
		}

		return modules;
	}

	public static AFromModuleImports importAll(LexIdentifierToken from)
	{
		List<List<PImport>> types = new Vector<List<PImport>>();
		LexNameToken all = new LexNameToken(from.getName(), "all", from.location);
		List<PImport> impAll = new Vector<PImport>();
		impAll.add(AstFactory.newAAllImport(all));
		types.add(impAll);
		return AstFactory.newAFromModuleImports(from, types);
	}

	private AModuleModules readFlatModule() throws ParserException,
			LexException
	{
		File file = lastToken().location.getFile();
		setCurrentModule("DEFAULT");
		List<PDefinition> definitions = getDefinitionReader().readDefinitions();
		checkFor(VDMToken.EOF, 2318, "Unexpected token after flat definitions");
		return AstFactory.newAModuleModules(file, definitions);
	}

	private AModuleModules readModule() throws ParserException, LexException
	{
		LexIdentifierToken name = new LexIdentifierToken("?", false, lastToken().location);
		AModuleImports imports = null;
		AModuleExports exports = null;

		try
		{
			setCurrentModule("");
			checkFor(VDMToken.MODULE, 2170, "Expecting 'module' at module start");
			name = readIdToken("Expecting identifier after 'module'");
			setCurrentModule(name.getName());

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
				if (Settings.strict)
				{
					warning(5024, "Strict: order should be imports then exports", lastToken().location);
				}
				
				imports = readImports(name);
			}
			
			if (Settings.strict && exports == null)
			{
				warning(5025, "Strict: expecting 'exports all' clause", lastToken().location);
			}
		}
		catch (LocatedException e)
		{
			VDMToken[] after = { VDMToken.DEFINITIONS };
			VDMToken[] upto = { VDMToken.END };
			report(e, after, upto);
		}

		List<PDefinition> defs = null;

		if (lastToken().is(VDMToken.DEFINITIONS))
		{
			nextToken();
			defs = getDefinitionReader().readDefinitions();
		} else
		{
			defs = new Vector<PDefinition>();
		}

		checkFor(VDMToken.END, 2171, "Expecting 'end' after module definitions");
		LexIdentifierToken endname = readIdToken("Expecting 'end <name>' after module definitions");

		if (name != null && !name.equals(endname))
		{
			throwMessage(2049, "Expecting 'end " + name.getName() + "'");
		}

		LexLocation.addSpan(idToName(name), lastToken());
		return AstFactory.newAModuleModules(name, imports, exports, defs);
	}

	private AModuleModules readDLModule() throws ParserException, LexException
	{
		LexIdentifierToken name = new LexIdentifierToken("?", false, lastToken().location);
		// FIXME dlmodules not implemented
		AModuleImports imports = null;
		AModuleExports exports = null;
		// LexStringToken library = null;

		try
		{
			checkFor(VDMToken.DLMODULE, 2172, "Expecting 'dlmodule' at module start");
			name = readIdToken("Expecting identifier after 'dlmodule'");
			setCurrentModule(name.getName());

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
					/* library = (LexStringToken) */lastToken();
					nextToken();
				} else
				{
					throwMessage(2050, "Expecting library name after 'uselib'");
				}
			}
		} catch (LocatedException e)
		{
			VDMToken[] after = {};
			VDMToken[] upto = { VDMToken.END };
			report(e, after, upto);
		}

		checkFor(VDMToken.END, 2173, "Expecting 'end' after dlmodule definitions");
		LexIdentifierToken endname = readIdToken("Expecting 'end <name>' after dlmodule definitions");

		if (name != null && !name.equals(endname))
		{
			throwMessage(2051, "Expecting 'end " + name.getName() + "'");
		}

		// return new DLModule(name, imports, exports, library);
		List<ClonableFile> files = new Vector<ClonableFile>();
		files.add(new ClonableFile(name.location.getFile()));

		AModuleModules module = AstFactory.newAModuleModules(name, imports, exports, null);
		module.setFiles(files);
		module.setIsDLModule(true);
		return module;
	}

	private AModuleExports readExports() throws ParserException, LexException
	{
		checkFor(VDMToken.EXPORTS, 2174, "Malformed imports? Expecting 'exports' section");
		return AstFactory.newAModuleExports(readExportsFromModule());
	}

	private List<List<PExport>> readExportsFromModule() throws ParserException,
			LexException
	{
		List<List<PExport>> types = new Vector<List<PExport>>();

		if (lastToken().is(VDMToken.ALL))
		{
			LexNameToken all = new LexNameToken(getCurrentModule(), "all", lastToken().location);
			List<PExport> expAll = new Vector<PExport>();
			expAll.add(AstFactory.newAAllExport(all.location));
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

	private List<PExport> readExportsOfOneType() throws ParserException,
			LexException
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
				
			default:
				throwMessage(2052, "Expecting 'all', 'types', 'values', 'functions' or 'operations'");
				return null;
		}
	}

	private List<PExport> readExportedTypes() throws ParserException,
			LexException
	{
		List<PExport> list = new Vector<PExport>();
		list.add(readExportedType());
		boolean semi = ignore(VDMToken.SEMICOLON);

		while (lastToken().isNot(VDMToken.DEFINITIONS)
				&& lastToken().isNot(VDMToken.USELIB) && !newType())
		{
			if (!semi && Settings.strict)
			{
				warning(5022, "Strict: expecting semi-colon between exports", lastToken().location);
			}

			list.add(readExportedType());
			semi = ignore(VDMToken.SEMICOLON);
		}

		return list;
	}

	private ATypeExport readExportedType() throws ParserException, LexException
	{
		boolean struct = lastToken().is(VDMToken.STRUCT);
		if (struct)
		{
			nextToken();
		}
		LexNameToken name = readNameToken("Expecting exported type name");
		return AstFactory.newATypeExport(name, struct);
	}

	private List<PExport> readExportedValues() throws ParserException,
			LexException
	{
		List<PExport> list = new Vector<PExport>();
		list.add(readExportedValue());
		boolean semi = ignore(VDMToken.SEMICOLON);

		while (lastToken().isNot(VDMToken.DEFINITIONS)
				&& lastToken().isNot(VDMToken.USELIB) && !newType())
		{
			if (!semi && Settings.strict)
			{
				warning(5022, "Strict: expecting semi-colon between exports", lastToken().location);
			}

			list.add(readExportedValue());
			semi = ignore(VDMToken.SEMICOLON);
		}

		return list;
	}

	private AValueExport readExportedValue() throws ParserException,
			LexException
	{
		LexToken token = lastToken();
		List<ILexNameToken> nameList = readIdList();
		checkFor(VDMToken.COLON, 2175, "Expecting ':' after export name");
		PType type = getTypeReader().readType();
		return AstFactory.newAValueExport(token.location, nameList, type);
	}

	private List<PExport> readExportedFunctions() throws ParserException,
			LexException
	{
		List<PExport> list = new Vector<PExport>();
		list.add(readExportedFunction());
		boolean semi = ignore(VDMToken.SEMICOLON);

		while (lastToken().is(VDMToken.IDENTIFIER)
				|| lastToken().is(VDMToken.NAME))
		{
			if (!semi && Settings.strict)
			{
				warning(5022, "Strict: expecting semi-colon between exports", lastToken().location);
			}

			list.add(readExportedFunction());
			semi = ignore(VDMToken.SEMICOLON);
		}

		return list;
	}

	private AFunctionExport readExportedFunction() throws ParserException,
			LexException
	{
		LexToken token = lastToken();
		List<ILexNameToken> nameList = readIdList();
		List<ILexNameToken> typeParams = ignoreTypeParams();
		checkFor(VDMToken.COLON, 2176, "Expecting ':' after export name");
		PType type = getTypeReader().readType();
		return AstFactory.newAFunctionExport(token.location, nameList, type, typeParams);
	}

	private List<PExport> readExportedOperations() throws ParserException,
			LexException
	{
		List<PExport> list = new Vector<PExport>();
		list.add(readExportedOperation());
		boolean semi = ignore(VDMToken.SEMICOLON);

		while (lastToken().is(VDMToken.IDENTIFIER)
				|| lastToken().is(VDMToken.NAME))
		{
			if (!semi && Settings.strict)
			{
				warning(5022, "Strict: expecting semi-colon between exports", lastToken().location);
			}

			list.add(readExportedOperation());
			semi = ignore(VDMToken.SEMICOLON);
		}

		return list;
	}

	private AOperationExport readExportedOperation() throws ParserException,
			LexException
	{
		LexToken token = lastToken();
		List<ILexNameToken> nameList = readIdList();
		checkFor(VDMToken.COLON, 2177, "Expecting ':' after export name");
		PType type = getTypeReader().readOperationType();
		return AstFactory.newAOperationExport(token.location, nameList, type);
	}

	private List<ILexNameToken> readIdList() throws ParserException,
			LexException
	{
		List<ILexNameToken> list = new Vector<ILexNameToken>();
		list.add(readNameToken("Expecting name list"));

		while (ignore(VDMToken.COMMA))
		{
			list.add(readNameToken("Expecting name list"));
		}

		return list;
	}

	private AModuleImports readImports(LexIdentifierToken name)
			throws ParserException, LexException
	{
		checkFor(VDMToken.IMPORTS, 2178, "Expecting 'imports'");
		List<AFromModuleImports> imports = new Vector<AFromModuleImports>();
		imports.add(readImportDefinition());

		while (ignore(VDMToken.COMMA))
		{
			imports.add(readImportDefinition());
		}

		return AstFactory.newAModuleImports(name, imports);
	}

	private AFromModuleImports readImportDefinition() throws ParserException,
			LexException
	{
		checkFor(VDMToken.FROM, 2179, "Expecting 'from' in import definition");
		LexIdentifierToken from = readIdToken("Expecting module identifier after 'from'");
		return AstFactory.newAFromModuleImports(from, readImportsFromModule(from));
	}

	private List<List<PImport>> readImportsFromModule(LexIdentifierToken from)
			throws ParserException, LexException
	{
		List<List<PImport>> types = new Vector<List<PImport>>();

		if (lastToken().is(VDMToken.ALL))
		{
			LexNameToken all = new LexNameToken(getCurrentModule(), "all", lastToken().location);
			List<PImport> impAll = new Vector<PImport>();
			impAll.add(AstFactory.newAAllImport(all));
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

	private List<PImport> readImportsOfOneType(LexIdentifierToken from)
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
				
			default:
				throwMessage(2054, "Expecting types, values, functions or operations");
				return null;
		}
	}

	private List<PImport> readImportedTypes(LexIdentifierToken from)
			throws ParserException, LexException
	{
		List<PImport> list = new Vector<PImport>();
		list.add(readImportedType(from));
		boolean semi = ignore(VDMToken.SEMICOLON);

		while (lastToken().is(VDMToken.IDENTIFIER)
				|| lastToken().is(VDMToken.NAME))
		{
			if (!semi && Settings.strict)
			{
				warning(5023, "Strict: expecting semi-colon between imports", lastToken().location);
			}

			list.add(readImportedType(from));
			semi = ignore(VDMToken.SEMICOLON);
		}

		return list;
	}

	private ATypeImport readImportedType(LexIdentifierToken from)
			throws ParserException, LexException
	{
		String savedModule = getCurrentModule();

		try
		{
			reader.push();
			setCurrentModule(from.getName()); // So names are from "from" in...
			ATypeDefinition def = getDefinitionReader().readTypeDefinition();
			setCurrentModule(savedModule); // and restore
			reader.unpush();

			LexNameToken renamed = null;

			if (ignore(VDMToken.RENAMED))
			{
				renamed = readNameToken("Expected renamed type name");
			}

			return AstFactory.newATypeImport(def, renamed);
		} catch (ParserException e)
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

		return AstFactory.newATypeImport(defname, renamed);
	}

	private List<PImport> readImportedValues(LexIdentifierToken from)
			throws ParserException, LexException
	{
		List<PImport> list = new Vector<PImport>();
		list.add(readImportedValue(from));
		boolean semi = ignore(VDMToken.SEMICOLON);

		while (lastToken().is(VDMToken.IDENTIFIER)
				|| lastToken().is(VDMToken.NAME))
		{
			if (!semi && Settings.strict)
			{
				warning(5023, "Strict: expecting semi-colon between imports", lastToken().location);
			}

			list.add(readImportedValue(from));
			semi = ignore(VDMToken.SEMICOLON);
		}

		return list;
	}

	private SValueImport readImportedValue(LexIdentifierToken from)
			throws ParserException, LexException
	{
		LexNameToken name = readNameToken("Expecting imported value name");
		LexNameToken defname = getDefName(from, name);
		PType type = null;

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

		return AstFactory.newAValueValueImport(defname, type, renamed);
	}

	private List<PImport> readImportedFunctions(LexIdentifierToken from)
			throws ParserException, LexException
	{
		List<PImport> list = new Vector<PImport>();
		list.add(readImportedFunction(from));
		boolean semi = ignore(VDMToken.SEMICOLON);

		while (lastToken().is(VDMToken.IDENTIFIER)
				|| lastToken().is(VDMToken.NAME))
		{
			if (!semi && Settings.strict)
			{
				warning(5023, "Strict: expecting semi-colon between imports", lastToken().location);
			}

			list.add(readImportedFunction(from));
			semi = ignore(VDMToken.SEMICOLON);
		}

		return list;
	}

	private AFunctionValueImport readImportedFunction(LexIdentifierToken from)
			throws ParserException, LexException
	{
		LexNameToken name = readNameToken("Expecting imported function name");
		LexNameToken defname = getDefName(from, name);
		LexNameList typeParams = getDefinitionReader().readTypeParams();

		PType type = null;

		if (lastToken().is(VDMToken.COLON))
		{
			nextToken();
			type = getTypeReader().readType();
		}

		LexNameToken renamed = null;

		if (ignore(VDMToken.RENAMED))
		{
			renamed = readNameToken("Expected renamed function name");
		}

		return AstFactory.newAFunctionValueImport(defname, type, typeParams, renamed);
	}

	private List<PImport> readImportedOperations(LexIdentifierToken from)
			throws ParserException, LexException
	{
		List<PImport> list = new Vector<PImport>();
		list.add(readImportedOperation(from));
		boolean semi = ignore(VDMToken.SEMICOLON);

		while (lastToken().is(VDMToken.IDENTIFIER)
				|| lastToken().is(VDMToken.NAME))
		{
			if (!semi && Settings.strict)
			{
				warning(5023, "Strict: expecting semi-colon between imports", lastToken().location);
			}

			list.add(readImportedOperation(from));
			semi = ignore(VDMToken.SEMICOLON);
		}

		return list;
	}

	private AOperationValueImport readImportedOperation(LexIdentifierToken from)
			throws ParserException, LexException
	{
		LexNameToken name = readNameToken("Expecting imported operation name");
		LexNameToken defname = getDefName(from, name);
		PType type = null;

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

		return AstFactory.newAOperationValueImport(defname, type, renamed);
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
				
			default:
				return false;
		}
	}

	private LexNameToken getDefName(LexIdentifierToken impmod, LexNameToken name)
	{
		if (name.module.equals(getCurrentModule())) // ie. it was an id
		{
			return new LexNameToken(impmod.getName(), name.name, name.location);
		}

		return name;
	}

	private LexNameList ignoreTypeParams() throws LexException, ParserException
	{
		if (lastToken().is(VDMToken.SEQ_OPEN))
		{
			return getDefinitionReader().readTypeParams();
		}
		else
		{
			return null;
		}
	}
}
