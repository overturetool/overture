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

import java.util.List;
import java.util.Vector;
import java.util.Arrays;

import org.overturetool.vdmj.Release;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.definitions.AccessSpecifier;
import org.overturetool.vdmj.definitions.AssignmentDefinition;
import org.overturetool.vdmj.definitions.ClassInvariantDefinition;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.definitions.EqualsDefinition;
import org.overturetool.vdmj.definitions.ExplicitFunctionDefinition;
import org.overturetool.vdmj.definitions.ExplicitOperationDefinition;
import org.overturetool.vdmj.definitions.ImplicitFunctionDefinition;
import org.overturetool.vdmj.definitions.ImplicitOperationDefinition;
import org.overturetool.vdmj.definitions.InstanceVariableDefinition;
import org.overturetool.vdmj.definitions.MutexSyncDefinition;
import org.overturetool.vdmj.definitions.PerSyncDefinition;
import org.overturetool.vdmj.definitions.StateDefinition;
import org.overturetool.vdmj.definitions.ThreadDefinition;
import org.overturetool.vdmj.definitions.NamedTraceDefinition;
import org.overturetool.vdmj.definitions.TypeDefinition;
import org.overturetool.vdmj.definitions.ValueDefinition;
import org.overturetool.vdmj.expressions.EqualsExpression;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.expressions.ExpressionList;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.lex.LexIdentifierToken;
import org.overturetool.vdmj.lex.LexIntegerToken;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.lex.LexToken;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.lex.Token;
import org.overturetool.vdmj.messages.LocatedException;
import org.overturetool.vdmj.patterns.IdentifierPattern;
import org.overturetool.vdmj.patterns.MultipleBind;
import org.overturetool.vdmj.patterns.Pattern;
import org.overturetool.vdmj.patterns.PatternList;
import org.overturetool.vdmj.patterns.SetBind;
import org.overturetool.vdmj.patterns.TuplePattern;
import org.overturetool.vdmj.patterns.TypeBind;
import org.overturetool.vdmj.statements.CallObjectStatement;
import org.overturetool.vdmj.statements.ErrorCase;
import org.overturetool.vdmj.statements.ExternalClause;
import org.overturetool.vdmj.statements.SpecificationStatement;
import org.overturetool.vdmj.statements.Statement;
import org.overturetool.vdmj.traces.TraceApplyExpression;
import org.overturetool.vdmj.traces.TraceLetDefBinding;
import org.overturetool.vdmj.traces.TraceLetBeStBinding;
import org.overturetool.vdmj.traces.TraceBracketedExpression;
import org.overturetool.vdmj.traces.TraceCoreDefinition;
import org.overturetool.vdmj.traces.TraceDefinitionTerm;
import org.overturetool.vdmj.traces.TraceDefinition;
import org.overturetool.vdmj.traces.TraceRepeatDefinition;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.Field;
import org.overturetool.vdmj.types.FunctionType;
import org.overturetool.vdmj.types.InvariantType;
import org.overturetool.vdmj.types.NamedType;
import org.overturetool.vdmj.types.OperationType;
import org.overturetool.vdmj.types.PatternListTypePair;
import org.overturetool.vdmj.types.PatternTypePair;
import org.overturetool.vdmj.types.ProductType;
import org.overturetool.vdmj.types.RecordType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.types.UnresolvedType;


/**
 * A syntax analyser to parse definitions.
 */

public class DefinitionReader extends SyntaxReader
{
	private static final long MAX_TIMES = 5;

	public DefinitionReader(LexTokenReader reader)
	{
		super(reader);
	}

	private static Token[] sectionArray =
	{
		Token.TYPES,
		Token.FUNCTIONS,
		Token.STATE,
		Token.VALUES,
		Token.OPERATIONS,
		Token.INSTANCE,
		Token.THREAD,
		Token.SYNC,
		Token.TRACES,
		Token.END,
		Token.EOF
	};

	private static Token[] afterArray =
	{
		Token.SEMICOLON
	};

	private static List<Token> sectionList = Arrays.asList(sectionArray);

	private boolean newSection() throws LexException
	{
		return sectionList.contains(lastToken().type);
	}

	public DefinitionList readDefinitions() throws ParserException, LexException
	{
		DefinitionList list = new DefinitionList();
		boolean threadDone = false;

		while (lastToken().isNot(Token.EOF) && lastToken().isNot(Token.END))
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
					try
    				{
    					nextToken();
        				list.add(readStateDefinition());

        				if (!newSection())
        				{
        					checkFor(Token.SEMICOLON,
        						2080, "Missing ';' after state definition");
        				}
    				}
    				catch (LocatedException e)
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
					list.addAll(readInstanceVariables());
					break;

				case TRACES:
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
					}
					else
					{
						throwMessage(2011, "Only one thread clause permitted per class");
					}

					try
    				{
    					nextToken();
        				list.add(readThreadDefinition());

        				if (!newSection())
        				{
        					checkFor(Token.SEMICOLON,
        						2085, "Missing ';' after thread definition");
        				}
    				}
    				catch (LocatedException e)
    				{
    					report(e, afterArray, sectionArray);
    				}
					break;

				case SYNC:
					list.addAll(readSyncs());
					break;

				case EOF:
					break;

				default:
					try
					{
						throwMessage(2013, "Expected 'operations', 'state', 'functions', 'types' or 'values'");
					}
    				catch (LocatedException e)
    				{
    					report(e, afterArray, sectionArray);
    				}
			}
		}

		return list;
	}

	private AccessSpecifier readAccessSpecifier(boolean async)
		throws LexException, ParserException
	{
		if (dialect == Dialect.VDM_SL)
		{
			return AccessSpecifier.DEFAULT;
		}

		// Defaults
		boolean isStatic = false;
		boolean isAsync = false;
		Token access = Token.PRIVATE;

		boolean more = true;

		while (more)
		{
			switch (lastToken().type)
			{
				case ASYNC:
					if (async)
					{
						isAsync = true;
						nextToken();
					}
					else
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
				case PRIVATE:
				case PROTECTED:
					access = lastToken().type;
					nextToken();
					break;

				default:
					more = false;
					break;
			}
		}

		return new AccessSpecifier(isStatic, isAsync, access);
	}

	public TypeDefinition readTypeDefinition() throws ParserException, LexException
	{
		LexIdentifierToken id = readIdToken("Expecting new type identifier");
		TypeReader tr = getTypeReader();
		InvariantType invtype = null;

		switch (lastToken().type)
		{
			case EQUALS:
				nextToken();
				NamedType nt = new NamedType(idToName(id), tr.readType());

				if (nt.type instanceof UnresolvedType &&
					((UnresolvedType)nt.type).typename.equals(nt.typename))
				{
					throwMessage(2014, "Recursive type declaration");
				}

				invtype = nt;
				break;

			case COLONCOLON:
				nextToken();
				invtype = new RecordType(idToName(id), tr.readFieldList());
				break;

			default:
				throwMessage(2015, "Expecting =<type> or ::<field list>");
		}

		Pattern invPattern = null;
		Expression invExpression = null;

		if (lastToken().is(Token.INV))
		{
			nextToken();
			invPattern =  getPatternReader().readPattern();
			checkFor(Token.EQUALSEQUALS, 2087, "Expecting '==' after pattern in invariant");
			invExpression = getExpressionReader().readExpression();
		}

		return new TypeDefinition(idToName(id), invtype, invPattern, invExpression);
	}

	private DefinitionList readTypes() throws LexException, ParserException
	{
		checkFor(Token.TYPES, 2013, "Expected 'types'");
		DefinitionList list = new DefinitionList();

		while (!newSection())
		{
			try
			{
				AccessSpecifier access = readAccessSpecifier(false);
				TypeDefinition def = readTypeDefinition();

				// Force all type defs (invs) to be static
				def.setAccessSpecifier(access.getStatic(true));
				list.add(def);

				if (!newSection())
				{
					checkFor(Token.SEMICOLON,
						2078, "Missing ';' after type definition");
				}
			}
			catch (LocatedException e)
			{
				report(e, afterArray, sectionArray);
			}
		}

		return list;
	}

	private DefinitionList readValues() throws LexException, ParserException
	{
		checkFor(Token.VALUES, 2013, "Expected 'values'");
		DefinitionList list = new DefinitionList();

		while (!newSection())
		{
			try
			{
				AccessSpecifier access = readAccessSpecifier(false);
				Definition def = readValueDefinition(NameScope.GLOBAL);

				// Force all values to be static
				def.setAccessSpecifier(access.getStatic(true));
				list.add(def);

				if (!newSection())
				{
					checkFor(Token.SEMICOLON,
						2081, "Missing ';' after value definition");
				}
			}
			catch (LocatedException e)
			{
				report(e, afterArray, sectionArray);
			}
		}

		return list;
	}

	private DefinitionList readFunctions() throws LexException, ParserException
	{
		checkFor(Token.FUNCTIONS, 2013, "Expected 'functions'");
		DefinitionList list = new DefinitionList();

		while (!newSection())
		{
			try
			{
				AccessSpecifier access = readAccessSpecifier(false);
				Definition def = readFunctionDefinition(NameScope.GLOBAL);

				if (Settings.release == Release.VDM_10)
				{
					// Force all functions to be static for VDM-10
					def.setAccessSpecifier(access.getStatic(true));
				}
				else
				{
					def.setAccessSpecifier(access);
				}

				list.add(def);

				if (!newSection())
				{
					checkFor(Token.SEMICOLON,
						2079, "Missing ';' after function definition");
				}
			}
			catch (LocatedException e)
			{
				report(e, afterArray, sectionArray);
			}
		}

		return list;
	}

	public DefinitionList readOperations() throws LexException, ParserException
	{
		checkFor(Token.OPERATIONS, 2013, "Expected 'operations'");
		DefinitionList list = new DefinitionList();

		while (!newSection())
		{
			try
			{
				AccessSpecifier access = readAccessSpecifier(dialect == Dialect.VDM_RT);
				Definition def = readOperationDefinition();
				def.setAccessSpecifier(access);
				list.add(def);

				if (!newSection())
				{
					checkFor(Token.SEMICOLON,
						2082, "Missing ';' after operation definition");
				}
			}
			catch (LocatedException e)
			{
				report(e, afterArray, sectionArray);
			}
		}

		return list;
	}

	public DefinitionList readInstanceVariables() throws LexException, ParserException
	{
		if (dialect == Dialect.VDM_SL)
		{
			throwMessage(2009, "Can't have instance variables in VDM-SL");
		}

		checkFor(Token.INSTANCE, 2083, "Expected 'instance variables'");
		checkFor(Token.VARIABLES, 2083, "Expecting 'instance variables'");
		DefinitionList list = new DefinitionList();

		while (!newSection())
		{
			try
			{
				Definition def = readInstanceVariableDefinition();
				list.add(def);

				if (!newSection())
				{
					checkFor(Token.SEMICOLON,
						2084, "Missing ';' after instance variable definition");
				}
			}
			catch (LocatedException e)
			{
				report(e, afterArray, sectionArray);
			}
		}

		return list;
	}

	private DefinitionList readTraces() throws LexException, ParserException
	{
		if (dialect == Dialect.VDM_SL)
		{
			throwMessage(2262, "Can't have traces in VDM-SL");
		}

		checkFor(Token.TRACES, 2013, "Expected 'traces'");
		DefinitionList list = new DefinitionList();

		while (!newSection())
		{
			try
			{
				Definition def = readNamedTraceDefinition();
				list.add(def);

				if (!newSection())
				{
					ignore(Token.SEMICOLON);	// Optional?
				}
			}
			catch (LocatedException e)
			{
				report(e, afterArray, sectionArray);
			}
		}

		return list;
	}

	private DefinitionList readSyncs() throws LexException, ParserException
	{
		if (dialect == Dialect.VDM_SL)
		{
			throwMessage(2012, "Can't have a sync clause in VDM-SL");
		}

		checkFor(Token.SYNC, 2013, "Expected 'sync'");
		DefinitionList list = new DefinitionList();

		while (!newSection())
		{
			try
			{
				Definition def = readPermissionPredicateDefinition();
				list.add(def);

				if (!newSection())
				{
					checkFor(Token.SEMICOLON,
						2086, "Missing ';' after sync definition");
				}
			}
			catch (LocatedException e)
			{
				report(e, afterArray, sectionArray);
			}
		}

		return list;
	}

	private Definition readFunctionDefinition(NameScope scope)
		throws ParserException, LexException
	{
		Definition def = null;
		LexIdentifierToken funcName = readIdToken("Expecting new function identifier");

		if (funcName.name.startsWith("mk_"))
		{
			throwMessage(2016, "Function name cannot start with 'mk_'");
		}

		LexNameList typeParams = null;

		if (lastToken().is(Token.SEQ_OPEN))
		{
			typeParams = new LexNameList();
			nextToken();
			checkFor(Token.AT, 2088, "Expecting '@' before type parameter");
			LexIdentifierToken tid = readIdToken("Expecting '@identifier' in type parameter list");
			typeParams.add(idToName(tid));

			while (ignore(Token.COMMA))
			{
				checkFor(Token.AT, 2089, "Expecting '@' before type parameter");
				tid = readIdToken("Expecting '@identifier' in type parameter list");
				typeParams.add(idToName(tid));
			}

			checkFor(Token.SEQ_CLOSE, 2090, "Expecting ']' after type parameters");
		}

		if (lastToken().is(Token.COLON))
		{
			def = readExplicitFunctionDefinition(funcName, scope, typeParams);
		}
		else if (lastToken().is(Token.BRA))
		{
			def = readImplicitFunctionDefinition(funcName, scope, typeParams);
		}
		else
		{
			throwMessage(2017, "Expecting ':' or '(' after name in function definition");
		}

		LexLocation.addSpan(idToName(funcName), lastToken());
		return def;
	}

	private Definition readExplicitFunctionDefinition(
		LexIdentifierToken funcName, NameScope scope, LexNameList typeParams)
		throws ParserException, LexException
	{
		// Explicit function definition, like "f: int->bool f(x) == true"

		nextToken();
		Type t = getTypeReader().readType();

		if (!(t instanceof FunctionType))
		{
			throwMessage(2018, "Function type is not a -> or +> function");
		}

		FunctionType type = (FunctionType)t;

		LexIdentifierToken name =
			readIdToken("Expecting identifier after function type in definition");

		if (!name.equals(funcName))
		{
			throwMessage(2019, "Expecting identifier " + funcName.name + " after type in definition");
		}

		if (lastToken().isNot(Token.BRA))
		{
			throwMessage(2020, "Expecting '(' after function name");
		}

		List<PatternList> parameters = new Vector<PatternList>();

		while (lastToken().is(Token.BRA))
		{
			if (nextToken().isNot(Token.KET))
			{
    			parameters.add(getPatternReader().readPatternList());
    			checkFor(Token.KET, 2091, "Expecting ')' after function parameters");
    		}
    		else
    		{
    			parameters.add(new PatternList());	// empty "()"
    			nextToken();
    		}
		}

		checkFor(Token.EQUALSEQUALS, 2092, "Expecting '==' after parameters");
		ExpressionReader expr = getExpressionReader();
		Expression body = expr.readExpression();
		Expression precondition = null;
		Expression postcondition = null;
		LexIdentifierToken measure = null;

		if (lastToken().is(Token.PRE))
		{
			nextToken();
			precondition = expr.readExpression();
		}

		if (lastToken().is(Token.POST))
		{
			nextToken();
			postcondition = expr.readExpression();
		}

		if (lastToken().is(Token.MEASURE))
		{
			nextToken();
			measure = readIdToken("Expecting identifier after 'measure'");
		}

		return new ExplicitFunctionDefinition(
			idToName(funcName), scope, typeParams,
			type, parameters, body, precondition, postcondition,
			false, measure);
	}

	private Definition readImplicitFunctionDefinition(
		LexIdentifierToken funcName, NameScope scope, LexNameList typeParams)
		throws ParserException, LexException
	{
		// Implicit, like g(x: int) y: bool pre exp post exp

		nextToken();

		PatternReader pr = getPatternReader();
		TypeReader tr = getTypeReader();
		List<PatternListTypePair> parameterPatterns = new Vector<PatternListTypePair>();

		while (lastToken().isNot(Token.KET))
		{
			PatternList pl = pr.readPatternList();
			checkFor(Token.COLON, 2093, "Missing colon after pattern/type parameter");
			parameterPatterns.add(new PatternListTypePair(pl, tr.readType()));
			ignore(Token.COMMA);
		}

		LexToken firstResult = nextToken();
   		PatternList resultNames = new PatternList();
   		TypeList resultTypes = new TypeList();

   		do
   		{
   			LexIdentifierToken rname = readIdToken("Expecting result identifier");
   	   		resultNames.add(new IdentifierPattern(idToName(rname)));
   	   		checkFor(Token.COLON, 2094, "Missing colon in identifier/type return value");
   	   		resultTypes.add(tr.readType());
   		}
   		while (ignore(Token.COMMA));

   		if (lastToken().is(Token.IDENTIFIER))
		{
			throwMessage(2261, "Missing comma between return types?");
		}

   		PatternTypePair resultPattern = null;

   		if (resultNames.size() > 1)
   		{
   			resultPattern = new PatternTypePair(
   	   			new TuplePattern(firstResult.location, resultNames),
 	   			new ProductType(firstResult.location, resultTypes));
   		}
   		else
   		{
   			resultPattern = new PatternTypePair(
   	   			resultNames.get(0), resultTypes.get(0));
   		}

		ExpressionReader expr = getExpressionReader();
		Expression body = null;
		Expression precondition = null;
		Expression postcondition = null;
		LexIdentifierToken measure = null;

		if (lastToken().is(Token.EQUALSEQUALS))		// extended implicit function
		{
			nextToken();
			body = expr.readExpression();
		}

		if (lastToken().is(Token.PRE))
		{
			nextToken();
			precondition = expr.readExpression();
		}

		if (body == null)	// Mandatory for standard implicit functions
		{
			checkFor(Token.POST, 2095, "Implicit function must have post condition");
			postcondition = expr.readExpression();
		}
		else
		{
			if (lastToken().is(Token.POST))
			{
				nextToken();
				postcondition = expr.readExpression();
			}
		}

		if (lastToken().is(Token.MEASURE))
		{
			nextToken();
			measure = readIdToken("Expecting identifier after 'measure'");
		}

		return new ImplicitFunctionDefinition(
			idToName(funcName), scope, typeParams, parameterPatterns, resultPattern,
			body, precondition, postcondition, measure);
	}

	public Definition readLocalDefinition(NameScope scope)
		throws ParserException, LexException
	{
		ParserException funcDefError = null;

    	try
    	{
        	reader.push();
        	Definition def = readFunctionDefinition(scope);
    		reader.unpush();
    		return def;
    	}
    	catch (ParserException e)		// Not a function then...
    	{
    		reader.pop();
			e.adjustDepth(reader.getTokensRead());
    		funcDefError = e;
    	}

		try
		{
        	reader.push();
        	Definition def = readValueDefinition(scope);
    		reader.unpush();
    		return def;
		}
		catch (ParserException e)
		{
    		reader.pop();
			e.adjustDepth(reader.getTokensRead());
			throw e.deeperThan(funcDefError) ? e : funcDefError;
		}
	}

	public Definition readValueDefinition(NameScope scope)
		throws ParserException, LexException
	{
       	// Should be <pattern>[:<type>]=<expression>

    	Pattern p = getPatternReader().readPattern();
    	Type type = null;

    	if (lastToken().is(Token.COLON))
    	{
    		nextToken();
    		type = getTypeReader().readType();
    	}

 		checkFor(Token.EQUALS, 2096, "Expecting <pattern>[:<type>]=<exp>");
		return new ValueDefinition(
			p, scope, type, getExpressionReader().readExpression());
	}

	private Definition readStateDefinition() throws ParserException, LexException
	{
		LexIdentifierToken name = readIdToken("Expecting identifier after 'state' definition");
		checkFor(Token.OF, 2097, "Expecting 'of' after state name");
		List<Field> fieldList = getTypeReader().readFieldList();

		Expression invExpression = null;
		Expression initExpression = null;
		Pattern invPattern = null;
		Pattern initPattern = null;

		if (lastToken().is(Token.INV))
		{
			nextToken();
			invPattern = getPatternReader().readPattern();
			checkFor(Token.EQUALSEQUALS, 2098, "Expecting '==' after pattern in invariant");
			invExpression = getExpressionReader().readExpression();
		}

		if (lastToken().is(Token.INIT))
		{
			nextToken();
			initPattern = getPatternReader().readPattern();
			checkFor(Token.EQUALSEQUALS, 2099, "Expecting '==' after pattern in initializer");
			initExpression = getExpressionReader().readExpression();
		}

		checkFor(Token.END, 2100, "Expecting 'end' after state definition");
		return new StateDefinition(idToName(name), fieldList,
			invPattern, invExpression, initPattern, initExpression);
	}

	private Definition readOperationDefinition()
		throws ParserException, LexException
	{
		Definition def = null;
		LexIdentifierToken funcName = readIdToken("Expecting new operation identifier");

		if (lastToken().is(Token.COLON))
		{
			def = readExplicitOperationDefinition(funcName);
		}
		else if (lastToken().is(Token.BRA))
		{
			def = readImplicitOperationDefinition(funcName);
		}
		else if (lastToken().is(Token.SEQ_OPEN))
		{
			throwMessage(2059, "Operations cannot have [@T] type parameters");
		}
		else
		{
			throwMessage(2021, "Expecting ':' or '(' after name in operation definition");
		}

		LexLocation.addSpan(idToName(funcName), lastToken());
		return def;
	}

	private Definition readExplicitOperationDefinition(LexIdentifierToken funcName)
		throws ParserException, LexException
	{
		// Like "f: int ==> bool f(x) == <statement>"

		nextToken();
		OperationType type = getTypeReader().readOperationType();

		LexIdentifierToken name =
			readIdToken("Expecting operation identifier after type in definition");

		if (!name.equals(funcName))
		{
			throwMessage(2022, "Expecting name " + funcName.name + " after type in definition");
		}

		if (lastToken().isNot(Token.BRA))
		{
			throwMessage(2023, "Expecting '(' after operation name");
		}

		PatternList parameters = null;

		if (nextToken().isNot(Token.KET))
		{
			parameters = getPatternReader().readPatternList();
			checkFor(Token.KET, 2101, "Expecting ')' after operation parameters");
		}
		else
		{
			parameters = new PatternList();		// empty "()"
			nextToken();
		}

		checkFor(Token.EQUALSEQUALS, 2102, "Expecting '==' after parameters");
		Statement body = getStatementReader().readStatement();
		Expression precondition = null;
		Expression postcondition = null;

		if (lastToken().is(Token.PRE))
		{
			nextToken();
			precondition = getExpressionReader().readExpression();
		}

		if (lastToken().is(Token.POST))
		{
			nextToken();
			postcondition = getExpressionReader().readExpression();
		}

		ExplicitOperationDefinition def = new ExplicitOperationDefinition(
			idToName(funcName), type,
			parameters, precondition, postcondition, body);

		return def;
	}

	private Definition readImplicitOperationDefinition(LexIdentifierToken funcName)
		throws ParserException, LexException
	{
		// Like g(x: int) [y: bool]? ext rd fred[:int] pre exp post exp

		nextToken();
		PatternReader pr = getPatternReader();
		TypeReader tr = getTypeReader();
		List<PatternListTypePair> parameterPatterns = new Vector<PatternListTypePair>();

		while (lastToken().isNot(Token.KET))
		{
			PatternList pl = pr.readPatternList();
			checkFor(Token.COLON, 2103, "Missing colon after pattern/type parameter");
			parameterPatterns.add(new PatternListTypePair(pl, tr.readType()));
			ignore(Token.COMMA);
		}

		LexToken firstResult = nextToken();
   		PatternTypePair resultPattern = null;

		if (firstResult.is(Token.IDENTIFIER))
		{
			PatternList resultNames = new PatternList();
			TypeList resultTypes = new TypeList();

			do
			{
				LexIdentifierToken rname = readIdToken("Expecting result identifier");
				resultNames.add(new IdentifierPattern(idToName(rname)));
				checkFor(Token.COLON, 2104, "Missing colon in identifier/type return value");
				resultTypes.add(tr.readType());
			}
			while (ignore(Token.COMMA));

			if (lastToken().is(Token.IDENTIFIER))
			{
				throwMessage(2261, "Missing comma between return types?");
			}

			if (resultNames.size() > 1)
			{
				resultPattern = new PatternTypePair(
					new TuplePattern(firstResult.location, resultNames),
					new ProductType(firstResult.location, resultTypes));
			}
			else
			{
				resultPattern = new PatternTypePair(
					resultNames.get(0), resultTypes.get(0));
			}
		}

		Statement body = null;

		if (lastToken().is(Token.EQUALSEQUALS))		// extended implicit operation
		{
			nextToken();
			body = getStatementReader().readStatement();
		}

		SpecificationStatement spec = readSpecification(funcName.location, body == null);

		ImplicitOperationDefinition def = new ImplicitOperationDefinition(
			idToName(funcName), parameterPatterns, resultPattern, body, spec);

		return def;
	}

	public SpecificationStatement readSpecification(
		LexLocation location, boolean postMandatory)
		throws ParserException, LexException
	{
		List<ExternalClause> externals = null;

		if (lastToken().is(Token.EXTERNAL))
		{
			externals = new Vector<ExternalClause>();
			nextToken();

			while (lastToken().is(Token.READ) || lastToken().is(Token.WRITE))
			{
				externals.add(readExternal());
			}

			if (externals.isEmpty())
			{
				throwMessage(2024, "Expecting external declarations after 'ext'");
			}
		}

		ExpressionReader expr = getExpressionReader();
		Expression precondition = null;
		Expression postcondition = null;

		if (lastToken().is(Token.PRE))
		{
			nextToken();
			precondition = expr.readExpression();
		}

		if (postMandatory)	// Mandatory for standard implicit operations
		{
			checkFor(Token.POST, 2105, "Implicit operation must define a post condition");
			postcondition = expr.readExpression();
		}
		else
		{
			if (lastToken().is(Token.POST))
			{
				nextToken();
				postcondition = expr.readExpression();
			}
		}

		List<ErrorCase> errors = null;

		if (lastToken().is(Token.ERRS))
		{
			errors = new Vector<ErrorCase>();
			nextToken();

			while (lastToken() instanceof LexIdentifierToken)
			{
				LexIdentifierToken name = readIdToken("Expecting error identifier");
				checkFor(Token.COLON, 2106, "Expecting ':' after name in errs clause");
				Expression left = expr.readExpression();
				checkFor(Token.ARROW, 2107, "Expecting '->' in errs clause");
				Expression right = expr.readExpression();
				errors.add(new ErrorCase(name, left, right));
			}

			if (errors.isEmpty())
			{
				throwMessage(2025, "Expecting <name>: exp->exp in errs clause");
			}
		}

		return new SpecificationStatement(location,
						externals, precondition, postcondition, errors);
	}

	private ExternalClause readExternal() throws ParserException, LexException
	{
		LexToken mode = lastToken();

		if (mode.isNot(Token.READ) && mode.isNot(Token.WRITE))
		{
			throwMessage(2026, "Expecting 'rd' or 'wr' after 'ext'");
		}

		LexNameList names = new LexNameList();
		nextToken();
		names.add(readNameToken("Expecting name in external clause"));

		while (ignore(Token.COMMA))
		{
			names.add(readNameToken("Expecting name in external clause"));
		}

		Type type = null;

		if (lastToken().is(Token.COLON))
		{
			nextToken();
			type = getTypeReader().readType();
		}

		return new ExternalClause(mode, names, type);
	}

	public EqualsDefinition readEqualsDefinition()
		throws ParserException, LexException
	{
       	// The grammar here says the form of the definition should be
		// "def" <patternBind>=<expression> "in" <expression>, but since
		// a set bind is "s in set S" that naively parses as
		// "s in set (S = <expression>)". Talking to PGL, we have to
		// make a special parse here. It is one of three forms:
		//
		//	"def" <pattern> "=" <expression> "in" ...
		//	"def" <type bind> "=" <expression> "in" ...
		//	"def" <pattern> "in set" <equals-expression> "in" ...
		//
		// and the "=" is unpicked from the left and right of the equals
		// expression in the third case.

		LexLocation location = lastToken().location;
		ParserException equalsDefError = null;

    	try	// "def" <pattern> "=" <expression> "in" ...
    	{
        	reader.push();
    		Pattern pattern = getPatternReader().readPattern();
     		checkFor(Token.EQUALS, 2108, "Expecting <pattern>=<exp>");
     		Expression test = getExpressionReader().readExpression();
    		reader.unpush();

     		return new EqualsDefinition(location, pattern, test);
    	}
    	catch (ParserException e)
    	{
    		reader.pop();
			e.adjustDepth(reader.getTokensRead());
    		equalsDefError = e;
    	}

		try	// "def" <type bind> "=" <expression> "in" ...
		{
        	reader.push();
    		TypeBind typebind = getBindReader().readTypeBind();
     		checkFor(Token.EQUALS, 2109, "Expecting <type bind>=<exp>");
     		Expression test = getExpressionReader().readExpression();
    		reader.unpush();

     		return new EqualsDefinition(location, typebind, test);
		}
		catch (ParserException e)
		{
    		reader.pop();
			e.adjustDepth(reader.getTokensRead());
			equalsDefError = e.deeperThan(equalsDefError) ? e : equalsDefError;
		}

		try	// "def" <pattern> "in set" <equals-expression> "in" ...
		{
        	reader.push();
    		Pattern pattern = getPatternReader().readPattern();
     		checkFor(Token.IN, 2110, "Expecting <pattern> in set <set exp>");
     		checkFor(Token.SET, 2111, "Expecting <pattern> in set <set exp>");
     		EqualsExpression test = getExpressionReader().readDefEqualsExpression();
     		SetBind setbind = new SetBind(pattern, test.left);
     		reader.unpush();

     		return new EqualsDefinition(location, setbind, test.right);
		}
		catch (ParserException e)
		{
    		reader.pop();
			e.adjustDepth(reader.getTokensRead());
			throw e.deeperThan(equalsDefError) ? e : equalsDefError;
		}
 	}

	private Definition readInstanceVariableDefinition()
		throws ParserException, LexException
    {
		LexToken token = lastToken();

		if (token.is(Token.INV))
		{
			nextToken();
			Expression exp = getExpressionReader().readExpression();
			String str = getCurrentModule();
			LexNameToken className = new LexNameToken(str, str, token.location);
			return new ClassInvariantDefinition(
				className.getInvName(token.location), exp);
		}
		else
		{
			AccessSpecifier access = readAccessSpecifier(false);
			AssignmentDefinition def = getStatementReader().readAssignmentDefinition();
			InstanceVariableDefinition ivd =
				new InstanceVariableDefinition(def.name, def.type, def.expression);
			ivd.setAccessSpecifier(access);
			return ivd;
		}
    }

	private Definition readThreadDefinition() throws LexException, ParserException
	{
		LexToken token = lastToken();

		if (token.is(Token.PERIODIC))
		{
			nextToken();
			checkFor(Token.BRA, 2112, "Expecting '(' after periodic");
			ExpressionList args = getExpressionReader().readExpressionList();
			checkFor(Token.KET, 2113, "Expecting ')' after period arguments");
			checkFor(Token.BRA, 2114, "Expecting '(' after periodic(...)");
			LexNameToken name = readNameToken("Expecting (name) after periodic(...)");
			checkFor(Token.KET, 2115, "Expecting (name) after periodic(...)");

			return new ThreadDefinition(name, args);
		}
		else
		{
			Statement stmt = getStatementReader().readStatement();
			return new ThreadDefinition(stmt);
		}
	}

	private Definition readPermissionPredicateDefinition()
		throws LexException, ParserException
	{
		LexToken token = lastToken();

		switch (token.type)
		{
			case PER:
				nextToken();
				LexNameToken name = readNameToken("Expecting name after 'per'");
				checkFor(Token.IMPLIES, 2116, "Expecting <name> => <exp>");
				Expression exp = getExpressionReader().readPerExpression();
				return new PerSyncDefinition(token.location, name, exp);

			case MUTEX:
				nextToken();
				checkFor(Token.BRA, 2117, "Expecting '(' after mutex");
				LexNameList opnames = new LexNameList();

				switch (lastToken().type)
				{
					case ALL:
						nextToken();
						checkFor(Token.KET, 2118, "Expecting ')' after 'all'");
						break;

					default:
						LexNameToken op = readNameToken("Expecting a name");
						opnames.add(op);

						while (ignore(Token.COMMA))
						{
							op = readNameToken("Expecting a name");
							opnames.add(op);
						}

						checkFor(Token.KET, 2119, "Expecting ')'");
						break;
				}

				return new MutexSyncDefinition(token.location, opnames);

			default:
				throwMessage(2028, "Expecting 'per' or 'mutex'");
				return null;
		}
	}

	private Definition readNamedTraceDefinition()
		throws ParserException, LexException
	{
		LexLocation start = lastToken().location;
		List<String> names = readTraceIdentifierList();
		checkFor(Token.COLON, 2264, "Expecting ':' after trace name(s)");
		List<TraceDefinitionTerm> traces = readTraceDefinitionList();

		return new NamedTraceDefinition(start, names, traces);
	}

	private List<String> readTraceIdentifierList()
		throws ParserException, LexException
	{
		List<String> names = new Vector<String>();
		names.add(readIdToken("Expecting trace identifier").name);

		while (lastToken().is(Token.DIVIDE))
		{
			nextToken();
			names.add(readIdToken("Expecting trace identifier").name);
		}

		return names;
	}

	private List<TraceDefinitionTerm> readTraceDefinitionList()
		throws LexException, ParserException
	{
		List<TraceDefinitionTerm> list = new Vector<TraceDefinitionTerm>();
		list.add(readTraceDefinitionTerm());

		while (lastToken().is(Token.SEMICOLON))
		{
			try
			{
				reader.push();
				nextToken();
				list.add(readTraceDefinitionTerm());
				reader.unpush();
			}
	    	catch (ParserException e)
	    	{
	    		reader.pop();
				break;
	    	}
		}

		return list;
	}

	private TraceDefinitionTerm readTraceDefinitionTerm()
		throws LexException, ParserException
	{
		TraceDefinitionTerm term = new TraceDefinitionTerm();
		term.add(readTraceDefinition());

		while (lastToken().is(Token.PIPE))
		{
			nextToken();
			term.add(readTraceDefinition());
		}

		return term;
	}

	private TraceDefinition readTraceDefinition()
		throws LexException, ParserException
	{
		if (lastToken().is(Token.LET))
		{
			return readTraceBinding();
		}
		else
		{
			return readTraceRepeat();
		}
	}

	private TraceDefinition readTraceRepeat()
		throws ParserException, LexException
	{
       	TraceCoreDefinition core = readCoreTraceDefinition();

       	long from = 1;
       	long to = 1;
       	LexToken token = lastToken();

       	switch (token.type)
		{
			case TIMES:
				from = 0;
				to = MAX_TIMES;
				nextToken();
				break;

			case PLUS:
				from = 1;
				to = MAX_TIMES;
				nextToken();
				break;

			case QMARK:
				from = 0;
				to = 1;
				nextToken();
				break;

			case SET_OPEN:
				if (nextToken().isNot(Token.NUMBER))
				{
					throwMessage(2266, "Expecting '{n}' or '{n1, n2}' after trace definition");
				}

				LexIntegerToken lit = (LexIntegerToken)lastToken();
				from = lit.value;
				to = lit.value;

				switch (nextToken().type)
				{
					case COMMA:
						if (nextToken().isNot(Token.NUMBER))
						{
							throwMessage(2265, "Expecting '{n1, n2}' after trace definition");
						}

						lit = (LexIntegerToken)readToken();
						to = lit.value;
						checkFor(Token.SET_CLOSE, 2265, "Expecting '{n1, n2}' after trace definition");
						break;

					case SET_CLOSE:
						nextToken();
						break;

					default:
						throwMessage(2266, "Expecting '{n}' or '{n1, n2}' after trace definition");
				}
				break;
		}

       	return new TraceRepeatDefinition(token.location, core, from, to);
	}

	private TraceDefinition readTraceBinding()
		throws ParserException, LexException
	{
		checkFor(Token.LET, 2230, "Expecting 'let'");
		ParserException letDefError = null;

		try
		{
			reader.push();
			TraceDefinition def = readLetDefBinding();
			reader.unpush();
			return def;
		}
		catch (ParserException e)
		{
			reader.pop();
			e.adjustDepth(reader.getTokensRead());
			letDefError = e;
		}

		try
		{
			reader.push();
			TraceDefinition def = readLetBeStBinding();
			reader.unpush();
			return def;
		}
		catch (ParserException e)
		{
			reader.pop();
			e.adjustDepth(reader.getTokensRead());
			throw e.deeperThan(letDefError) ? e : letDefError;
		}
	}

	private TraceDefinition readLetDefBinding()
		throws ParserException, LexException
	{
		List<ValueDefinition> localDefs = new Vector<ValueDefinition>();
		LexToken start = lastToken();

		while (lastToken().isNot(Token.IN))
		{
			Definition def = readLocalDefinition(NameScope.LOCAL);

			if (!(def instanceof ValueDefinition))
			{
				throwMessage(2270, "Only value definitions allowed in traces");
			}

			localDefs.add((ValueDefinition)def);
			ignore(Token.COMMA);
		}

		checkFor(Token.IN, 2231, "Expecting 'in' after local definitions");
		TraceDefinition body = readTraceDefinition();

		return new TraceLetDefBinding(start.location, localDefs, body);
	}

	private TraceDefinition readLetBeStBinding()
		throws ParserException, LexException
	{
		LexToken start = lastToken();
		MultipleBind bind = getBindReader().readMultipleBind();
		Expression stexp = null;

		if (lastToken().is(Token.BE))
		{
			nextToken();
			checkFor(Token.ST, 2232, "Expecting 'st' after 'be' in let statement");
			stexp = getExpressionReader().readExpression();
		}

		checkFor(Token.IN, 2233, "Expecting 'in' after bind in let statement");
		TraceDefinition body = readTraceDefinition();

		return new TraceLetBeStBinding(start.location, bind, stexp, body);
	}

	private TraceCoreDefinition readCoreTraceDefinition()
		throws ParserException, LexException
	{
		LexToken token = lastToken();

		switch (token.type)
		{
			case IDENTIFIER:
				StatementReader sr = getStatementReader();
				Statement stmt = sr.readStatement();

				if (!(stmt instanceof CallObjectStatement))
				{
					throwMessage(2267,
						"Expecting 'id.id(args)' or '(trace definitions)'", token);
				}

				return new TraceApplyExpression((CallObjectStatement)stmt);

			case BRA:
				nextToken();
				List<TraceDefinitionTerm> list = readTraceDefinitionList();
				checkFor(Token.KET, 2269, "Expecting '(trace definitions)'");
				return new TraceBracketedExpression(token.location, list);

			default:
				throwMessage(2267, "Expecting 'id.id(args)' or '(trace definitions)'");
				return null;
		}
	}
}
