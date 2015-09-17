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

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.AAssignmentDefinition;
import org.overture.ast.definitions.AEqualsDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.APrivateAccess;
import org.overture.ast.definitions.AProtectedAccess;
import org.overture.ast.definitions.APublicAccess;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PAccess;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.traces.ATraceDefinitionTerm;
import org.overture.ast.definitions.traces.PTraceCoreDefinition;
import org.overture.ast.definitions.traces.PTraceDefinition;
import org.overture.ast.expressions.AEqualsBinaryExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.intf.lex.ILexToken;
import org.overture.ast.lex.Dialect;
import org.overture.ast.lex.LexIdentifierToken;
import org.overture.ast.lex.LexIntegerToken;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.lex.LexToken;
import org.overture.ast.lex.VDMToken;
import org.overture.ast.node.tokens.TStatic;
import org.overture.ast.patterns.APatternListTypePair;
import org.overture.ast.patterns.APatternTypePair;
import org.overture.ast.patterns.ASetBind;
import org.overture.ast.patterns.ATypeBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.statements.ACallObjectStm;
import org.overture.ast.statements.ACallStm;
import org.overture.ast.statements.AErrorCase;
import org.overture.ast.statements.AExternalClause;
import org.overture.ast.statements.ASpecificationStm;
import org.overture.ast.statements.PStm;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.AAccessSpecifierAccessSpecifier;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AUnresolvedType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SInvariantType;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.parser.config.Properties;
import org.overture.parser.lex.LexException;
import org.overture.parser.lex.LexTokenReader;
import org.overture.parser.messages.LocatedException;

/**
 * A syntax analyser to parse definitions.
 */

public class DefinitionReader extends SyntaxReader
{

	public DefinitionReader(LexTokenReader reader)
	{
		super(reader);
	}

	private static VDMToken[] sectionArray = { VDMToken.TYPES,
			VDMToken.FUNCTIONS, VDMToken.STATE, VDMToken.VALUES,
			VDMToken.OPERATIONS, VDMToken.INSTANCE, VDMToken.THREAD,
			VDMToken.SYNC, VDMToken.TRACES, VDMToken.END, VDMToken.EOF };

	private static VDMToken[] afterArray = { VDMToken.SEMICOLON };

	private static List<VDMToken> sectionList = Arrays.asList(sectionArray);

	private boolean newSection() throws LexException
	{
		return newSection(lastToken());
	}

	public static boolean newSection(LexToken tok)
	{
		return sectionList.contains(tok.type);
	}

	public List<PDefinition> readDefinitions() throws ParserException,
			LexException
	{
		List<PDefinition> list = new Vector<PDefinition>();
		boolean threadDone = false;

		while (lastToken().isNot(VDMToken.EOF)
				&& lastToken().isNot(VDMToken.END))
		{
			switch (lastToken().type)
			{
				case TYPES:
					list.addAll(readTypes());
					break;

				case FUNCTIONS:
					list.addAll(readFunctions());
					break;

				case STATE:
					if (dialect != Dialect.VDM_SL)
					{
						throwMessage(2277, "Can't have state in VDM++");
					}

					try
					{
						nextToken();
						list.add(readStateDefinition());

						if (!newSection())
						{
							checkFor(VDMToken.SEMICOLON, 2080, "Missing ';' after state definition");
						}
					} catch (LocatedException e)
					{
						report(e, afterArray, sectionArray);
					}
					break;

				case VALUES:
					list.addAll(readValues());
					break;

				case OPERATIONS:
					list.addAll(readOperations());
					break;

				case INSTANCE:
					if (dialect == Dialect.VDM_SL)
					{
						throwMessage(2009, "Can't have instance variables in VDM-SL");
					}

					list.addAll(readInstanceVariables());
					break;

				case TRACES:
					if (dialect == Dialect.VDM_SL
							&& Settings.release != Release.VDM_10)
					{
						throwMessage(2262, "Can't have traces in VDM-SL classic");
					}

					list.addAll(readTraces());
					break;

				case THREAD:
					if (dialect == Dialect.VDM_SL)
					{
						throwMessage(2010, "Can't have a thread clause in VDM-SL");
					}

					if (!threadDone)
					{
						threadDone = true;
					} else
					{
						throwMessage(2011, "Only one thread clause permitted per class");
					}

					try
					{
						nextToken();
						list.add(readThreadDefinition());

						if (!newSection())
						{
							checkFor(VDMToken.SEMICOLON, 2085, "Missing ';' after thread definition");
						}
					} catch (LocatedException e)
					{
						report(e, afterArray, sectionArray);
					}
					break;

				case SYNC:
					if (dialect == Dialect.VDM_SL)
					{
						throwMessage(2012, "Can't have a sync clause in VDM-SL");
					}

					list.addAll(readSyncs());
					break;

				case EOF:
					break;

				default:
					try
					{
						throwMessage(2013, "Expected 'operations', 'state', 'functions', 'types' or 'values'");
					} catch (LocatedException e)
					{
						report(e, afterArray, sectionArray);
					}
			}
		}

		return list;
	}

	private AAccessSpecifierAccessSpecifier readAccessSpecifier(boolean asyncOK, boolean pureOK)
			throws LexException, ParserException
	{
		if (dialect == Dialect.VDM_SL)
		{
			if (lastToken().is(VDMToken.PURE))
			{
				if (Settings.release == Release.CLASSIC)
				{
					throwMessage(2325, "Pure operations are not available in classic");
				}
				
				if (pureOK)
				{
					nextToken();
					return AstFactory.getDefaultAccessSpecifier();
				}
				else
				{
					throwMessage(2324, "Pure only permitted for operations");
				}
			}
			else
			{
				return AstFactory.getDefaultAccessSpecifier();
			}
		}

		// Defaults
		// boolean isStatic = false;
		// boolean isAsync = false;
		boolean isStatic = false;
		boolean isAsync = false;
		boolean isPure = false;
		// VDMToken access = VDMToken.PRIVATE;
		PAccess access = new APrivateAccess();

		boolean more = true;

		while (more)
		{
			switch (lastToken().type)
			{
				case ASYNC:
					if (asyncOK)
					{
						isAsync = true;
						nextToken();
					} else
					{
						throwMessage(2278, "Async only permitted for operations");
						more = false;
					}
					break;

				case STATIC:
					isStatic = true;
					nextToken();
					break;

				case PUBLIC:
					access = new APublicAccess();
					nextToken();
					break;
				case PRIVATE:
					access = new APrivateAccess();
					nextToken();
					break;
				case PROTECTED:
					// access = lastToken().type;
					access = new AProtectedAccess();
					nextToken();
					break;

				case PURE:
					if (Settings.release == Release.CLASSIC)
					{
						throwMessage(2325, "Pure operations are not available in classic");
					}

					if (pureOK)
					{
						isPure = true;
						nextToken();
					}
					else
					{
						throwMessage(2324, "Pure only permitted for operations");
					}
					break;

				default:
					more = false;
					break;
			}
		}

		return AstFactory.newAAccessSpecifierAccessSpecifier(access, isStatic, isAsync, isPure);
	}

	public ATypeDefinition readTypeDefinition() throws ParserException,
			LexException
	{
		LexIdentifierToken id = readIdToken("Expecting new type identifier");
		TypeReader tr = getTypeReader();
		SInvariantType invtype = null;

		switch (lastToken().type)
		{
			case EQUALS:
				nextToken();
				PType type = tr.readType();
				ANamedInvariantType nt = AstFactory.newANamedInvariantType(idToName(id), type);

				if (type instanceof AUnresolvedType
						&& ((AUnresolvedType) type).getName().equals(idToName(id)))
				{
					throwMessage(2014, "Recursive type declaration");
				}

				invtype = nt;
				break;

			case COLONCOLON:
				nextToken();
				invtype = AstFactory.newARecordInvariantType(idToName(id), tr.readFieldList());
				break;

			default:
				throwMessage(2015, "Expecting =<type> or ::<field list>");
		}

		PPattern invPattern = null;
		PExp invExpression = null;

		if (lastToken().is(VDMToken.INV))
		{
			nextToken();
			invPattern = getPatternReader().readPattern();
			checkFor(VDMToken.EQUALSEQUALS, 2087, "Expecting '==' after pattern in invariant");
			invExpression = getExpressionReader().readExpression();
		}

		return AstFactory.newATypeDefinition(idToName(id), invtype, invPattern, invExpression);

	}

	private List<PDefinition> readTypes() throws LexException, ParserException
	{
		checkFor(VDMToken.TYPES, 2013, "Expected 'types'");
		List<PDefinition> list = new Vector<PDefinition>();

		while (!newSection())
		{
			try
			{
				AAccessSpecifierAccessSpecifier access = readAccessSpecifier(false, false);
				access.setStatic(new TStatic());
				ATypeDefinition def = readTypeDefinition();

				// Force all type defs (invs) to be static
				def.setAccess(access);
				list.add(def);

				if (!newSection())
				{
					checkFor(VDMToken.SEMICOLON, 2078, "Missing ';' after type definition");
				}
			} catch (LocatedException e)
			{
				report(e, afterArray, sectionArray);
			}
		}

		return list;
	}

	private List<PDefinition> readValues() throws LexException, ParserException
	{
		checkFor(VDMToken.VALUES, 2013, "Expected 'values'");
		List<PDefinition> list = new Vector<PDefinition>();

		while (!newSection())
		{
			try
			{
				AAccessSpecifierAccessSpecifier access = readAccessSpecifier(false, false);
				access.setStatic(new TStatic());
				PDefinition def = readValueDefinition(NameScope.GLOBAL);

				// Force all values to be static
				def.setAccess(access);

				if (def instanceof AValueDefinition)
				{
					for (PDefinition pDefinition : ((AValueDefinition) def).getDefs())
					{
						pDefinition.setAccess(access.clone());
					}
				}

				list.add(def);

				if (!newSection())
				{
					checkFor(VDMToken.SEMICOLON, 2081, "Missing ';' after value definition");
				}
			} catch (LocatedException e)
			{
				report(e, afterArray, sectionArray);
			}
		}

		return list;
	}

	private List<PDefinition> readFunctions() throws LexException,
			ParserException
	{
		checkFor(VDMToken.FUNCTIONS, 2013, "Expected 'functions'");
		List<PDefinition> list = new Vector<PDefinition>();

		while (!newSection())
		{
			try
			{
				AAccessSpecifierAccessSpecifier access = readAccessSpecifier(false, false);
				PDefinition def = readFunctionDefinition(NameScope.GLOBAL);

				if (Settings.release == Release.VDM_10)
				{
					// Force all functions to be static for VDM-10
					access.setStatic(new TStatic());
					def.setAccess(access);
				} else
				{
					def.setAccess(access);
				}

				list.add(def);

				if (!newSection())
				{
					checkFor(VDMToken.SEMICOLON, 2079, "Missing ';' after function definition");
				}
			} catch (LocatedException e)
			{
				report(e, afterArray, sectionArray);
			}
		}

		return list;
	}

	public List<PDefinition> readOperations() throws LexException,
			ParserException
	{
		checkFor(VDMToken.OPERATIONS, 2013, "Expected 'operations'");
		List<PDefinition> list = new Vector<PDefinition>();

		while (!newSection())
		{
			try
			{
				AAccessSpecifierAccessSpecifier access = readAccessSpecifier(dialect == Dialect.VDM_RT, true);
				PDefinition def = readOperationDefinition();
				def.setAccess(access);
				((AOperationType)def.getType()).setPure(access.getPure());
				list.add(def);

				if (!newSection())
				{
					checkFor(VDMToken.SEMICOLON, 2082, "Missing ';' after operation definition");
				}
			} catch (LocatedException e)
			{
				report(e, afterArray, sectionArray);
			}
		}

		return list;
	}

	public List<PDefinition> readInstanceVariables() throws LexException,
			ParserException
	{
		checkFor(VDMToken.INSTANCE, 2083, "Expected 'instance variables'");
		checkFor(VDMToken.VARIABLES, 2083, "Expecting 'instance variables'");
		List<PDefinition> list = new Vector<PDefinition>();

		while (!newSection())
		{
			try
			{
				PDefinition def = readInstanceVariableDefinition();
				list.add(def);

				if (!newSection())
				{
					checkFor(VDMToken.SEMICOLON, 2084, "Missing ';' after instance variable definition");
				}
			} catch (LocatedException e)
			{
				report(e, afterArray, sectionArray);
			}
		}

		return list;
	}

	private List<PDefinition> readTraces() throws LexException, ParserException
	{
		checkFor(VDMToken.TRACES, 2013, "Expected 'traces'");
		List<PDefinition> list = new Vector<PDefinition>();

		while (!newSection())
		{
			try
			{
				PDefinition def = readNamedTraceDefinition();
				list.add(def);

				if (!newSection())
				{
					ignore(VDMToken.SEMICOLON); // Optional?
				}
			} catch (LocatedException e)
			{
				report(e, afterArray, sectionArray);
			}
		}

		return list;
	}

	private List<PDefinition> readSyncs() throws LexException, ParserException
	{
		checkFor(VDMToken.SYNC, 2013, "Expected 'sync'");
		List<PDefinition> list = new Vector<PDefinition>();

		while (!newSection())
		{
			try
			{
				PDefinition def = readPermissionPredicateDefinition();
				list.add(def);

				if (!newSection())
				{
					checkFor(VDMToken.SEMICOLON, 2086, "Missing ';' after sync definition");
				}
			} catch (LocatedException e)
			{
				report(e, afterArray, sectionArray);
			}
		}

		return list;
	}

	public LexNameList readTypeParams() throws LexException, ParserException
	{
		LexNameList typeParams = null;

		if (lastToken().is(VDMToken.SEQ_OPEN))
		{
			typeParams = new LexNameList();
			nextToken();
			checkFor(VDMToken.AT, 2088, "Expecting '@' before type parameter");
			LexIdentifierToken tid = readIdToken("Expecting '@identifier' in type parameter list");
			typeParams.add(idToName(tid));

			while (ignore(VDMToken.COMMA))
			{
				checkFor(VDMToken.AT, 2089, "Expecting '@' before type parameter");
				tid = readIdToken("Expecting '@identifier' in type parameter list");
				typeParams.add(idToName(tid));
			}

			checkFor(VDMToken.SEQ_CLOSE, 2090, "Expecting ']' after type parameters");
		}

		return typeParams;
	}

	private PDefinition readFunctionDefinition(NameScope scope)
			throws ParserException, LexException
	{
		PDefinition def = null;
		LexIdentifierToken funcName = readIdToken("Expecting new function identifier");

		if (funcName.getName().startsWith("mk_"))
		{
			throwMessage(2016, "Function name cannot start with 'mk_'");
		}

		LexNameList typeParams = readTypeParams();

		if (lastToken().is(VDMToken.COLON))
		{
			def = readExplicitFunctionDefinition(funcName, scope, typeParams);
		} else if (lastToken().is(VDMToken.BRA))
		{
			def = readImplicitFunctionDefinition(funcName, scope, typeParams);
		} else
		{
			throwMessage(2017, "Expecting ':' or '(' after name in function definition");
		}

		LexLocation.addSpan(idToName(funcName), lastToken());
		return def;
	}

	private PDefinition readExplicitFunctionDefinition(
			LexIdentifierToken funcName, NameScope scope,
			List<ILexNameToken> typeParams) throws ParserException,
			LexException
	{
		// Explicit function definition, like "f: int->bool f(x) == true"

		nextToken();
		PType t = getTypeReader().readType();

		if (!(t instanceof AFunctionType))
		{
			throwMessage(2018, "Function type is not a -> or +> function");
		}

		AFunctionType type = (AFunctionType) t;

		LexIdentifierToken name = readIdToken("Expecting identifier after function type in definition");

		if (!name.equals(funcName))
		{
			throwMessage(2019, "Expecting identifier " + funcName.getName()
					+ " after type in definition");
		}

		if (lastToken().isNot(VDMToken.BRA))
		{
			throwMessage(2020, "Expecting '(' after function name");
		}

		List<List<PPattern>> parameters = new Vector<List<PPattern>>();

		while (lastToken().is(VDMToken.BRA))
		{
			if (nextToken().isNot(VDMToken.KET))
			{
				parameters.add(getPatternReader().readPatternList());
				checkFor(VDMToken.KET, 2091, "Expecting ')' after function parameters");
			} else
			{
				parameters.add(new Vector<PPattern>()); // empty "()"
				nextToken();
			}
		}

		checkFor(VDMToken.EQUALSEQUALS, 2092, "Expecting '==' after parameters");
		ExpressionReader expr = getExpressionReader();
		PExp body = readFunctionBody();
		PExp precondition = null;
		PExp postcondition = null;
		LexNameToken measure = null;

		if (lastToken().is(VDMToken.PRE))
		{
			nextToken();
			precondition = expr.readExpression();
		}

		if (lastToken().is(VDMToken.POST))
		{
			nextToken();
			postcondition = expr.readExpression();
		}

		if (lastToken().is(VDMToken.MEASURE))
		{
			nextToken();
			measure = readNameToken("Expecting name after 'measure'");
		}

		return AstFactory.newAExplicitFunctionDefinition(idToName(funcName), scope, typeParams, type, parameters, body, precondition, postcondition, false, measure);
	}

	private PDefinition readImplicitFunctionDefinition(
			LexIdentifierToken funcName, NameScope scope,
			List<ILexNameToken> typeParams) throws ParserException,
			LexException
	{
		// Implicit, like g(x: int) y: bool pre exp post exp

		nextToken();

		PatternReader pr = getPatternReader();
		TypeReader tr = getTypeReader();
		List<APatternListTypePair> parameterPatterns = new Vector<APatternListTypePair>();

		if (lastToken().isNot(VDMToken.KET))
		{
			List<PPattern> pl = pr.readPatternList();
			checkFor(VDMToken.COLON, 2093, "Missing colon after pattern/type parameter");
			parameterPatterns.add(AstFactory.newAPatternListTypePair(pl, tr.readType()));

			while (ignore(VDMToken.COMMA))
			{
				pl = pr.readPatternList();
				checkFor(VDMToken.COLON, 2093, "Missing colon after pattern/type parameter");
				parameterPatterns.add(AstFactory.newAPatternListTypePair(pl, tr.readType()));
			}
		}

		checkFor(VDMToken.KET, 2124, "Expecting ')' after parameters");

		LexToken firstResult = lastToken();
		List<PPattern> resultNames = new Vector<PPattern>();
		List<PType> resultTypes = new Vector<PType>();

		do
		{
			LexIdentifierToken rname = readIdToken("Expecting result identifier");
			resultNames.add(AstFactory.newAIdentifierPattern(idToName(rname)));
			checkFor(VDMToken.COLON, 2094, "Missing colon in identifier/type return value");
			resultTypes.add(tr.readType());
		} while (ignore(VDMToken.COMMA));

		if (lastToken().is(VDMToken.IDENTIFIER))
		{
			throwMessage(2261, "Missing comma between return types?");
		}

		APatternTypePair resultPattern = null;

		if (resultNames.size() > 1)
		{
			resultPattern = AstFactory.newAPatternTypePair(AstFactory.newATuplePattern(firstResult.location, resultNames), AstFactory.newAProductType(firstResult.location, resultTypes));
		} else
		{
			resultPattern = AstFactory.newAPatternTypePair(resultNames.get(0), resultTypes.get(0));
		}

		ExpressionReader expr = getExpressionReader();
		PExp body = null;
		PExp precondition = null;
		PExp postcondition = null;
		LexNameToken measure = null;

		if (lastToken().is(VDMToken.EQUALSEQUALS)) // extended implicit function
		{
			nextToken();
			body = readFunctionBody();
		}

		if (lastToken().is(VDMToken.PRE))
		{
			nextToken();
			precondition = expr.readExpression();
		}

		if (body == null) // Mandatory for standard implicit functions
		{
			checkFor(VDMToken.POST, 2095, "Implicit function must have post condition");
			postcondition = expr.readExpression();
		} else
		{
			if (lastToken().is(VDMToken.POST))
			{
				nextToken();
				postcondition = expr.readExpression();
			}
		}

		if (lastToken().is(VDMToken.MEASURE))
		{
			nextToken();
			measure = readNameToken("Expecting name after 'measure'");
		}

		return AstFactory.newAImplicitFunctionDefinition(idToName(funcName), scope, typeParams, parameterPatterns, resultPattern, body, precondition, postcondition, measure);

	}

	public PDefinition readLocalDefinition(NameScope scope)
			throws ParserException, LexException
	{
		ParserException funcDefError = null;

		try
		{
			reader.push();
			PDefinition def = readFunctionDefinition(scope);
			reader.unpush();
			return def;
		} catch (ParserException e) // Not a function then...
		{
			e.adjustDepth(reader.getTokensRead());
			reader.pop();
			funcDefError = e;
		}

		try
		{
			reader.push();
			PDefinition def = readValueDefinition(scope);
			reader.unpush();
			return def;
		} catch (ParserException e)
		{
			e.adjustDepth(reader.getTokensRead());
			reader.pop();
			throw e.deeperThan(funcDefError) ? e : funcDefError;
		}
	}

	public PDefinition readValueDefinition(NameScope scope)
			throws ParserException, LexException
	{
		// Should be <pattern>[:<type>]=<expression>

		PPattern p = getPatternReader().readPattern();
		PType type = null;

		if (lastToken().is(VDMToken.COLON))
		{
			nextToken();
			type = getTypeReader().readType();
		}

		checkFor(VDMToken.EQUALS, 2096, "Expecting <pattern>[:<type>]=<exp>");

		return AstFactory.newAValueDefinition(p, scope, type, getExpressionReader().readExpression());
	}

	private PDefinition readStateDefinition() throws ParserException,
			LexException
	{
		LexIdentifierToken name = readIdToken("Expecting identifier after 'state' definition");
		checkFor(VDMToken.OF, 2097, "Expecting 'of' after state name");
		List<AFieldField> fieldList = getTypeReader().readFieldList();

		PExp invExpression = null;
		PExp initExpression = null;
		PPattern invPattern = null;
		PPattern initPattern = null;

		if (lastToken().is(VDMToken.INV))
		{
			nextToken();
			invPattern = getPatternReader().readPattern();
			checkFor(VDMToken.EQUALSEQUALS, 2098, "Expecting '==' after pattern in invariant");
			invExpression = getExpressionReader().readExpression();
		}

		if (lastToken().is(VDMToken.INIT))
		{
			nextToken();
			initPattern = getPatternReader().readPattern();
			checkFor(VDMToken.EQUALSEQUALS, 2099, "Expecting '==' after pattern in initializer");
			initExpression = getExpressionReader().readExpression();
		}

		// Be forgiving about the inv/init order
		if (lastToken().is(VDMToken.INV) && invExpression == null)
		{
			nextToken();
			invPattern = getPatternReader().readPattern();
			checkFor(VDMToken.EQUALSEQUALS, 2098, "Expecting '==' after pattern in invariant");
			invExpression = getExpressionReader().readExpression();
		}

		checkFor(VDMToken.END, 2100, "Expecting 'end' after state definition");

		return AstFactory.newAStateDefinition(idToName(name), fieldList, invPattern, invExpression, initPattern, initExpression);
	}

	private PDefinition readOperationDefinition() throws ParserException,
			LexException
	{
		PDefinition def = null;
		LexIdentifierToken funcName = readIdToken("Expecting new operation identifier");

		if (lastToken().is(VDMToken.COLON))
		{
			def = readExplicitOperationDefinition(funcName);
		} else if (lastToken().is(VDMToken.BRA))
		{
			def = readImplicitOperationDefinition(funcName);
		} else if (lastToken().is(VDMToken.SEQ_OPEN))
		{
			throwMessage(2059, "Operations cannot have [@T] type parameters");
		} else
		{
			throwMessage(2021, "Expecting ':' or '(' after name in operation definition");
		}

		LexLocation.addSpan(idToName(funcName), lastToken());
		return def;
	}

	private PDefinition readExplicitOperationDefinition(
			LexIdentifierToken funcName) throws ParserException, LexException
	{
		// Like "f: int ==> bool f(x) == <statement>"

		nextToken();
		AOperationType type = getTypeReader().readOperationType();

		LexIdentifierToken name = readIdToken("Expecting operation identifier after type in definition");

		if (!name.equals(funcName))
		{
			throwMessage(2022, "Expecting name " + funcName.getName()
					+ " after type in definition");
		}

		if (lastToken().isNot(VDMToken.BRA))
		{
			throwMessage(2023, "Expecting '(' after operation name");
		}

		List<PPattern> parameters = null;

		if (nextToken().isNot(VDMToken.KET))
		{
			parameters = getPatternReader().readPatternList();
			checkFor(VDMToken.KET, 2101, "Expecting ')' after operation parameters");
		} else
		{
			parameters = new Vector<PPattern>(); // empty "()"
			nextToken();
		}

		checkFor(VDMToken.EQUALSEQUALS, 2102, "Expecting '==' after parameters");
		PStm body = readOperationBody();
		PExp precondition = null;
		PExp postcondition = null;

		if (lastToken().is(VDMToken.PRE))
		{
			nextToken();
			precondition = getExpressionReader().readExpression();
		}

		if (lastToken().is(VDMToken.POST))
		{
			nextToken();
			postcondition = getExpressionReader().readExpression();
		}

		return AstFactory.newAExplicitOperationDefinition(idToName(funcName), type, parameters, precondition, postcondition, body);
	}

	private PDefinition readImplicitOperationDefinition(
			LexIdentifierToken funcName) throws ParserException, LexException
	{
		// Like g(x: int) [y: bool]? ext rd fred[:int] pre exp post exp

		nextToken();
		PatternReader pr = getPatternReader();
		TypeReader tr = getTypeReader();
		List<APatternListTypePair> parameterPatterns = new Vector<APatternListTypePair>();

		if (lastToken().isNot(VDMToken.KET))
		{
			List<PPattern> pl = pr.readPatternList();
			checkFor(VDMToken.COLON, 2103, "Missing colon after pattern/type parameter");
			parameterPatterns.add(AstFactory.newAPatternListTypePair(pl, tr.readType()));

			while (ignore(VDMToken.COMMA))
			{
				pl = pr.readPatternList();
				checkFor(VDMToken.COLON, 2103, "Missing colon after pattern/type parameter");
				parameterPatterns.add(AstFactory.newAPatternListTypePair(pl, tr.readType()));
			}
		}

		checkFor(VDMToken.KET, 2124, "Expecting ')' after args");

		LexToken firstResult = lastToken();
		APatternTypePair resultPattern = null;

		if (firstResult.is(VDMToken.IDENTIFIER))
		{
			List<PPattern> resultNames = new Vector<PPattern>();
			List<PType> resultTypes = new Vector<PType>();

			do
			{
				LexIdentifierToken rname = readIdToken("Expecting result identifier");
				resultNames.add(AstFactory.newAIdentifierPattern(idToName(rname)));
				checkFor(VDMToken.COLON, 2104, "Missing colon in identifier/type return value");
				resultTypes.add(tr.readType());
			} while (ignore(VDMToken.COMMA));

			if (lastToken().is(VDMToken.IDENTIFIER))
			{
				throwMessage(2261, "Missing comma between return types?");
			}

			if (resultNames.size() > 1)
			{
				resultPattern = AstFactory.newAPatternTypePair(AstFactory.newATuplePattern(firstResult.location, resultNames), AstFactory.newAProductType(firstResult.location, resultTypes));
			} else
			{
				resultPattern = AstFactory.newAPatternTypePair(resultNames.get(0), resultTypes.get(0));
			}
		}

		PStm body = null;

		if (lastToken().is(VDMToken.EQUALSEQUALS)) // extended implicit operation
		{
			nextToken();
			body = readOperationBody();
		}

		ASpecificationStm spec = readSpecification(funcName.location, body == null);

		AImplicitOperationDefinition def = AstFactory.newAImplicitOperationDefinition(idToName(funcName), parameterPatterns, resultPattern, body, spec);

		return def;
	}

	public ASpecificationStm readSpecification(ILexLocation location,
			boolean postMandatory) throws ParserException, LexException
	{
		List<AExternalClause> externals = null;

		if (lastToken().is(VDMToken.EXTERNAL))
		{
			externals = new Vector<AExternalClause>();
			nextToken();

			while (lastToken().is(VDMToken.READ)
					|| lastToken().is(VDMToken.WRITE))
			{
				externals.add(readExternal());
			}

			if (externals.isEmpty())
			{
				throwMessage(2024, "Expecting external declarations after 'ext'");
			}
		}

		ExpressionReader expr = getExpressionReader();
		PExp precondition = null;
		PExp postcondition = null;

		if (lastToken().is(VDMToken.PRE))
		{
			nextToken();
			precondition = expr.readExpression();
		}

		if (postMandatory) // Mandatory for standard implicit operations
		{
			checkFor(VDMToken.POST, 2105, "Implicit operation must define a post condition");
			postcondition = expr.readExpression();
		} else
		{
			if (lastToken().is(VDMToken.POST))
			{
				nextToken();
				postcondition = expr.readExpression();
			}
		}

		List<AErrorCase> errors = null;

		if (lastToken().is(VDMToken.ERRS))
		{
			errors = new Vector<AErrorCase>();
			nextToken();

			while (lastToken() instanceof LexIdentifierToken)
			{
				LexIdentifierToken name = readIdToken("Expecting error identifier");
				checkFor(VDMToken.COLON, 2106, "Expecting ':' after name in errs clause");
				PExp left = expr.readExpression();
				checkFor(VDMToken.ARROW, 2107, "Expecting '->' in errs clause");
				PExp right = expr.readExpression();
				errors.add(AstFactory.newAErrorCase(name, left, right));
			}

			if (errors.isEmpty())
			{
				throwMessage(2025, "Expecting <name>: exp->exp in errs clause");
			}
		}

		return AstFactory.newASpecificationStm(location, externals, precondition, postcondition, errors);
	}

	private AExternalClause readExternal() throws ParserException, LexException
	{
		LexToken mode = lastToken();

		if (mode.isNot(VDMToken.READ) && mode.isNot(VDMToken.WRITE))
		{
			throwMessage(2026, "Expecting 'rd' or 'wr' after 'ext'");
		}

		LexNameList names = new LexNameList();
		nextToken();
		names.add(readNameToken("Expecting name in external clause"));

		while (ignore(VDMToken.COMMA))
		{
			names.add(readNameToken("Expecting name in external clause"));
		}

		PType type = null;

		if (lastToken().is(VDMToken.COLON))
		{
			nextToken();
			type = getTypeReader().readType();
		}

		return AstFactory.newAExternalClause(mode, names, type);

	}

	public AEqualsDefinition readEqualsDefinition() throws ParserException,
			LexException
	{
		// The grammar here says the form of the definition should be
		// "def" <patternBind>=<expression> "in" <expression>, but since
		// a set bind is "s in set S" that naively parses as
		// "s in set (S = <expression>)". Talking to PGL, we have to
		// make a special parse here. It is one of three forms:
		//
		// "def" <pattern> "=" <expression> "in" ...
		// "def" <type bind> "=" <expression> "in" ...
		// "def" <pattern> "in set" <equals-expression> "in" ...
		//
		// and the "=" is unpicked from the left and right of the equals
		// expression in the third case.

		ILexLocation location = lastToken().location;
		ParserException equalsDefError = null;

		try
		// "def" <pattern> "=" <expression> "in" ...
		{
			reader.push();
			PPattern pattern = getPatternReader().readPattern();
			checkFor(VDMToken.EQUALS, 2108, "Expecting <pattern>=<exp>");
			PExp test = getExpressionReader().readExpression();
			reader.unpush();
			return AstFactory.newAEqualsDefinition(location, pattern, test);
		} catch (ParserException e)
		{
			e.adjustDepth(reader.getTokensRead());
			reader.pop();
			equalsDefError = e;
		}

		try
		// "def" <type bind> "=" <expression> "in" ...
		{
			reader.push();
			ATypeBind typebind = getBindReader().readTypeBind();
			checkFor(VDMToken.EQUALS, 2109, "Expecting <type bind>=<exp>");
			PExp test = getExpressionReader().readExpression();
			reader.unpush();
			return AstFactory.newAEqualsDefinition(location, typebind, test);
		} catch (ParserException e)
		{
			e.adjustDepth(reader.getTokensRead());
			reader.pop();
			equalsDefError = e.deeperThan(equalsDefError) ? e : equalsDefError;
		}

		try
		// "def" <pattern> "in set" <equals-expression> "in" ...
		{
			reader.push();
			PPattern pattern = getPatternReader().readPattern();
			checkFor(VDMToken.IN, 2110, "Expecting <pattern> in set <set exp>");
			checkFor(VDMToken.SET, 2111, "Expecting <pattern> in set <set exp>");
			AEqualsBinaryExp test = getExpressionReader().readDefEqualsExpression();
			ASetBind setbind = AstFactory.newASetBind(pattern, test.getLeft());
			reader.unpush();
			return AstFactory.newAEqualsDefinition(location, setbind, test.getRight());
			// TODO: why does this have patterns.getDefinitions() for defs?!
			// return new AEqualsDefinition(location, null, null, null, null, null, null, null, null, null, setbind,
			// test.getRight(), null, null, pattern.getDefinitions());
		} catch (ParserException e)
		{
			e.adjustDepth(reader.getTokensRead());
			reader.pop();
			throw e.deeperThan(equalsDefError) ? e : equalsDefError;
		}
	}

	private PDefinition readInstanceVariableDefinition()
			throws ParserException, LexException
	{
		LexToken token = lastToken();

		if (token.is(VDMToken.INV))
		{
			nextToken();
			PExp exp = getExpressionReader().readExpression();
			String str = getCurrentModule();
			LexNameToken className = new LexNameToken(str, str, token.location);
			return AstFactory.newAClassInvariantDefinition(className.getInvName(token.location), exp);
		} else
		{
			AAccessSpecifierAccessSpecifier access = readAccessSpecifier(false, false);
			AAssignmentDefinition def = getStatementReader().readAssignmentDefinition();
			AInstanceVariableDefinition ivd = AstFactory.newAInstanceVariableDefinition(def.getName(), def.getType(), def.getExpression());
			def.getType().parent(ivd);// the type of ivd is graph but we trough away the assignment
			ivd.setAccess(access);
			return ivd;
		}
	}

	private PDefinition readThreadDefinition() throws LexException,
			ParserException
	{
		LexToken token = lastToken();

		if (token.is(VDMToken.PERIODIC))
		{
			if (dialect != Dialect.VDM_RT)
			{
				throwMessage(2316, "Periodic threads only available in VDM-RT");
			}

			nextToken();
			checkFor(VDMToken.BRA, 2112, "Expecting '(' after periodic");
			List<PExp> args = getExpressionReader().readExpressionList();
			checkFor(VDMToken.KET, 2113, "Expecting ')' after periodic arguments");
			checkFor(VDMToken.BRA, 2114, "Expecting '(' after periodic(...)");
			LexNameToken name = readNameToken("Expecting (name) after periodic(...)");
			checkFor(VDMToken.KET, 2115, "Expecting (name) after periodic(...)");
			// PStm statement = AstFactory.newAPeriodicStm(token.location, name, args);
			return AstFactory.newPeriodicAThreadDefinition(name, args);
		} else if (token.is(VDMToken.SPORADIC))
		{
			if (dialect != Dialect.VDM_RT)
			{
				throwMessage(2317, "Sporadic threads only available in VDM-RT");
			}

			nextToken();
			checkFor(VDMToken.BRA, 2312, "Expecting '(' after sporadic");
			List<PExp> args = getExpressionReader().readExpressionList();
			checkFor(VDMToken.KET, 2313, "Expecting ')' after sporadic arguments");
			checkFor(VDMToken.BRA, 2314, "Expecting '(' after sporadic(...)");
			LexNameToken name = readNameToken("Expecting (name) after sporadic(...)");
			checkFor(VDMToken.KET, 2315, "Expecting (name) after sporadic(...)");
			return AstFactory.newSporadicAThreadDefinition(name, args);
		} else
		{
			PStm stmt = getStatementReader().readStatement();
			return AstFactory.newAThreadDefinition(stmt);
		}
	}

	private PDefinition readPermissionPredicateDefinition()
			throws LexException, ParserException
	{
		LexToken token = lastToken();

		switch (token.type)
		{
			case PER:
				nextToken();
				LexNameToken name = readNameToken("Expecting name after 'per'");
				checkFor(VDMToken.IMPLIES, 2116, "Expecting <name> => <exp>");
				PExp exp = getExpressionReader().readPerExpression();
				return AstFactory.newAPerSyncDefinition(token.location, name, exp);

			case MUTEX:
				nextToken();
				checkFor(VDMToken.BRA, 2117, "Expecting '(' after mutex");
				LexNameList opnames = new LexNameList();

				switch (lastToken().type)
				{
					case ALL:
						nextToken();
						checkFor(VDMToken.KET, 2118, "Expecting ')' after 'all'");
						break;

					default:
						LexNameToken op = readNameToken("Expecting a name");
						opnames.add(op);

						while (ignore(VDMToken.COMMA))
						{
							op = readNameToken("Expecting a name");
							opnames.add(op);
						}

						checkFor(VDMToken.KET, 2119, "Expecting ')'");
						break;
				}

				return AstFactory.newAMutexSyncDefinition(token.location, opnames);

			default:
				throwMessage(2028, "Expecting 'per' or 'mutex'");
				return null;
		}
	}

	private PDefinition readNamedTraceDefinition() throws ParserException,
			LexException
	{
		ILexLocation start = lastToken().location;
		List<String> names = readTraceIdentifierList();
		checkFor(VDMToken.COLON, 2264, "Expecting ':' after trace name(s)");
		List<ATraceDefinitionTerm> traces = readTraceDefinitionList();

		return AstFactory.newANamedTraceDefinition(start, names, traces);
	}

	private List<String> readTraceIdentifierList() throws ParserException,
			LexException
	{
		List<String> names = new Vector<String>();
		names.add(readIdToken("Expecting trace identifier").getName());

		while (lastToken().is(VDMToken.DIVIDE))
		{
			nextToken();
			names.add(readIdToken("Expecting trace identifier").getName());
		}

		return names;
	}

	private List<ATraceDefinitionTerm> readTraceDefinitionList()
			throws LexException, ParserException
	{
		List<ATraceDefinitionTerm> list = new Vector<ATraceDefinitionTerm>();
		list.add(readTraceDefinitionTerm());

		while (lastToken().is(VDMToken.SEMICOLON))
		{
			try
			{
				reader.push();
				nextToken();
				list.add(readTraceDefinitionTerm());
				reader.unpush();
			} catch (ParserException e)
			{
				reader.pop();
				break;
			}
		}

		return list;
	}

	private ATraceDefinitionTerm readTraceDefinitionTerm() throws LexException,
			ParserException
	{
		List<PTraceDefinition> term = new Vector<PTraceDefinition>();
		term.add(readTraceDefinition());

		while (lastToken().is(VDMToken.PIPE))
		{
			nextToken();
			term.add(readTraceDefinition());
		}

		return AstFactory.newATraceDefinitionTerm(term);
	}

	private PTraceDefinition readTraceDefinition() throws LexException,
			ParserException
	{
		if (lastToken().is(VDMToken.LET))
		{
			return readTraceBinding();
		} else
		{
			return readTraceRepeat();
		}
	}

	private PTraceDefinition readTraceRepeat() throws ParserException,
			LexException
	{
		PTraceCoreDefinition core = readCoreTraceDefinition();

		long from = 1;
		long to = 1;
		LexToken token = lastToken();

		switch (token.type)
		{
			case TIMES:
				from = 0;
				to = Properties.traces_max_repeats;
				nextToken();
				break;

			case PLUS:
				from = 1;
				to = Properties.traces_max_repeats;
				nextToken();
				break;

			case QMARK:
				from = 0;
				to = 1;
				nextToken();
				break;

			case SET_OPEN:
				if (nextToken().isNot(VDMToken.NUMBER))
				{
					throwMessage(2266, "Expecting '{n}' or '{n1, n2}' after trace definition");
				}

				LexIntegerToken lit = (LexIntegerToken) lastToken();
				from = lit.value;
				to = lit.value;

				switch (nextToken().type)
				{
					case COMMA:
						if (nextToken().isNot(VDMToken.NUMBER))
						{
							throwMessage(2265, "Expecting '{n1, n2}' after trace definition");
						}

						lit = (LexIntegerToken) readToken();
						to = lit.value;
						checkFor(VDMToken.SET_CLOSE, 2265, "Expecting '{n1, n2}' after trace definition");
						break;

					case SET_CLOSE:
						nextToken();
						break;

					default:
						throwMessage(2266, "Expecting '{n}' or '{n1, n2}' after trace definition");
				}
				break;
		}

		return AstFactory.newARepeatTraceDefinition(token.location, core, from, to);
	}

	private PTraceDefinition readTraceBinding() throws ParserException,
			LexException
	{
		checkFor(VDMToken.LET, 2230, "Expecting 'let'");
		ParserException letDefError = null;

		try
		{
			reader.push();
			PTraceDefinition def = readLetDefBinding();
			reader.unpush();
			return def;
		} catch (ParserException e)
		{
			e.adjustDepth(reader.getTokensRead());
			reader.pop();
			letDefError = e;
		}

		try
		{
			reader.push();
			PTraceDefinition def = readLetBeStBinding();
			reader.unpush();
			return def;
		} catch (ParserException e)
		{
			e.adjustDepth(reader.getTokensRead());
			reader.pop();
			throw e.deeperThan(letDefError) ? e : letDefError;
		}
	}

	private PTraceDefinition readLetDefBinding() throws ParserException,
			LexException
	{
		List<AValueDefinition> localDefs = new Vector<AValueDefinition>();
		LexToken start = lastToken();

		PDefinition def = readLocalDefinition(NameScope.LOCAL);

		if (!(def instanceof AValueDefinition))
		{
			throwMessage(2270, "Only value definitions allowed in traces");
		}

		localDefs.add((AValueDefinition) def);

		while (ignore(VDMToken.COMMA))
		{
			def = readLocalDefinition(NameScope.LOCAL);

			if (!(def instanceof AValueDefinition))
			{
				throwMessage(2270, "Only value definitions allowed in traces");
			}

			localDefs.add((AValueDefinition) def);
		}

		checkFor(VDMToken.IN, 2231, "Expecting 'in' after local definitions");
		PTraceDefinition body = readTraceDefinition();

		return AstFactory.newALetDefBindingTraceDefinition(start.location, localDefs, body);
	}

	private PTraceDefinition readLetBeStBinding() throws ParserException,
			LexException
	{
		LexToken start = lastToken();
		PMultipleBind bind = getBindReader().readMultipleBind();
		PExp stexp = null;

		if (lastToken().is(VDMToken.BE))
		{
			nextToken();
			checkFor(VDMToken.ST, 2232, "Expecting 'st' after 'be' in let statement");
			stexp = getExpressionReader().readExpression();
		}

		checkFor(VDMToken.IN, 2233, "Expecting 'in' after bind in let statement");
		PTraceDefinition body = readTraceDefinition();

		return AstFactory.newALetBeStBindingTraceDefinition(start.location, bind, stexp, body);
	}

	private PTraceCoreDefinition readCoreTraceDefinition()
			throws ParserException, LexException
	{
		LexToken token = lastToken();

		switch (token.type)
		{
			case IDENTIFIER:
			case NAME:
			case SELF:
				StatementReader sr = getStatementReader();
				PStm stmt = sr.readStatement();

				if (!(stmt instanceof ACallStm)
						&& !(stmt instanceof ACallObjectStm))
				{
					throwMessage(2267, "Expecting 'obj.op(args)' or 'op(args)'", token);
				}

				return AstFactory.newAApplyExpressionTraceCoreDefinition(stmt, getCurrentModule());

			case BRA:
				nextToken();
				List<ATraceDefinitionTerm> list = readTraceDefinitionList();
				checkFor(VDMToken.KET, 2269, "Expecting '(trace definitions)'");
				return AstFactory.newABracketedExpressionTraceCoreDefinition(token.location, list);

			case PIPEPIPE:
				nextToken();
				checkFor(VDMToken.BRA, 2292, "Expecting '|| (...)'");
				List<PTraceDefinition> defs = new Vector<PTraceDefinition>();
				defs.add(readTraceDefinition());
				checkFor(VDMToken.COMMA, 2293, "Expecting '|| (a, b {,...})'");
				defs.add(readTraceDefinition());

				while (lastToken().is(VDMToken.COMMA))
				{
					nextToken();
					defs.add(readTraceDefinition());
				}

				checkFor(VDMToken.KET, 2294, "Expecting ')' ending || clause");
				return AstFactory.newAConcurrentExpressionTraceCoreDefinition(token.location, defs);

			default:
				throwMessage(2267, "Expecting 'obj.op(args)' or 'op(args)'", token);
				return null;
		}
	}

	private PExp readFunctionBody() throws LexException, ParserException
	{
		ILexToken token = lastToken();

		if (token.is(VDMToken.IS))
		{
			switch (nextToken().type)
			{
				case NOT:
					nextToken();
					checkFor(VDMToken.YET, 2125, "Expecting 'is not yet specified'");
					checkFor(VDMToken.SPECIFIED, 2126, "Expecting 'is not yet specified'");
					return AstFactory.newANotYetSpecifiedExp(token.getLocation());

				case SUBCLASS:
					nextToken();
					checkFor(VDMToken.RESPONSIBILITY, 2127, "Expecting 'is subclass responsibility'");
					return AstFactory.newASubclassResponsibilityExp(token.getLocation());

				default:
					if (dialect == Dialect.VDM_PP)
					{
						throwMessage(2033, "Expecting 'is not yet specified' or 'is subclass responsibility'", token);
					} else
					{
						throwMessage(2033, "Expecting 'is not yet specified'", token);
					}
					return null;
			}
		} else
		{
			ExpressionReader expr = getExpressionReader();
			return expr.readExpression();
		}
	}

	private PStm readOperationBody() throws LexException, ParserException
	{
		ILexToken token = lastToken();

		if (token.is(VDMToken.IS))
		{
			switch (nextToken().type)
			{
				case NOT:
					nextToken();
					checkFor(VDMToken.YET, 2187, "Expecting 'is not yet specified");
					checkFor(VDMToken.SPECIFIED, 2188, "Expecting 'is not yet specified");
					return AstFactory.newANotYetSpecifiedStm(token.getLocation());

				case SUBCLASS:
					nextToken();
					checkFor(VDMToken.RESPONSIBILITY, 2189, "Expecting 'is subclass responsibility'");
					return AstFactory.newASubclassResponsibilityStm(token.getLocation());

				default:
					if (dialect == Dialect.VDM_PP)
					{
						throwMessage(2062, "Expecting 'is not yet specified' or 'is subclass responsibility'", token);
					} else
					{
						throwMessage(2062, "Expecting 'is not yet specified'", token);
					}
					return null;
			}
		} else
		{
			StatementReader stmt = getStatementReader();
			return stmt.readStatement();
		}
	}
}
