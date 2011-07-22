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

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.AAssignmentDefinition;
import org.overture.ast.definitions.AClassInvariantDefinition;
import org.overture.ast.definitions.AEqualsDefinition;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.ALocalDefinition;
import org.overture.ast.definitions.AMutexSyncDefinition;
import org.overture.ast.definitions.ANamedTraceDefinition;
import org.overture.ast.definitions.APerSyncDefinition;
import org.overture.ast.definitions.APrivateAccess;
import org.overture.ast.definitions.AProtectedAccess;
import org.overture.ast.definitions.APublicAccess;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.AThreadDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PAccess;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.traces.AApplyExpressionTraceCoreDefinition;
import org.overture.ast.definitions.traces.ABracketedExpressionTraceCoreDefinition;
import org.overture.ast.definitions.traces.AConcurrentExpressionTraceCoreDefinition;
import org.overture.ast.definitions.traces.ALetBeStBindingTraceDefinition;
import org.overture.ast.definitions.traces.ALetDefBindingTraceDefinition;
import org.overture.ast.definitions.traces.ARepeatTraceDefinition;
import org.overture.ast.definitions.traces.PTraceCoreDefinition;
import org.overture.ast.definitions.traces.PTraceDefinition;
import org.overture.ast.expressions.AEqualsBinaryExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.node.NodeList;
import org.overture.ast.node.tokens.TAsync;
import org.overture.ast.node.tokens.TStatic;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.APatternListTypePair;
import org.overture.ast.patterns.APatternTypePair;
import org.overture.ast.patterns.ASetBind;
import org.overture.ast.patterns.ATuplePattern;
import org.overture.ast.patterns.ATypeBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.statements.ACallObjectStm;
import org.overture.ast.statements.ACallStm;
import org.overture.ast.statements.AErrorCase;
import org.overture.ast.statements.AExternalClause;
import org.overture.ast.statements.APeriodicStm;
import org.overture.ast.statements.ASpecificationStm;
import org.overture.ast.statements.PStm;
import org.overture.ast.types.AAccessSpecifierAccessSpecifier;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.AUnresolvedType;
import org.overture.ast.types.AVoidType;
import org.overture.ast.types.PAccessSpecifier;
import org.overture.ast.types.PType;
import org.overture.ast.types.SInvariantType;
import org.overturetool.vdmj.Release;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.config.Properties;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.lex.LexIdentifierToken;
import org.overturetool.vdmj.lex.LexIntegerToken;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.lex.LexToken;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.lex.VDMToken;
import org.overturetool.vdmj.messages.LocatedException;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.util.Utils;



/**
 * A syntax analyser to parse definitions.
 */

public class DefinitionReader extends SyntaxReader
{
	private AAccessSpecifierAccessSpecifier getDefaultAccess()
	{
		return new AAccessSpecifierAccessSpecifier(new APrivateAccess(), null, null);
	}
	
	public List<PType> getTypeList(APatternListTypePair node)
	{
		List<PType> list = new Vector<PType>();

		for (int i=0; i<node.getPatterns().size(); i++)
		{
			PType type =(PType) node.getType().clone();//Use clone since we don't want to make a switch for all types.
			type.parent(null);//Create new type not in the tree yet.
			list.add(type);
		}

		return list;
	}
	
	public DefinitionReader(LexTokenReader reader)
	{
		super(reader);
	}

	private static VDMToken[] sectionArray =
	{
		VDMToken.TYPES,
		VDMToken.FUNCTIONS,
		VDMToken.STATE,
		VDMToken.VALUES,
		VDMToken.OPERATIONS,
		VDMToken.INSTANCE,
		VDMToken.THREAD,
		VDMToken.SYNC,
		VDMToken.TRACES,
		VDMToken.END,
		VDMToken.EOF
	};

	private static VDMToken[] afterArray =
	{
		VDMToken.SEMICOLON
	};

	private static List<VDMToken> sectionList = Arrays.asList(sectionArray);

	private boolean newSection() throws LexException
	{
		return newSection(lastToken());
	}
	
	public static boolean newSection(LexToken tok)
	{
		return sectionList.contains(tok.type);
	}

	public List<PDefinition> readDefinitions() throws ParserException, LexException
	{
		List<PDefinition> list = new Vector<PDefinition>();
		boolean threadDone = false;

		while (lastToken().isNot(VDMToken.EOF) && lastToken().isNot(VDMToken.END))
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
        					checkFor(VDMToken.SEMICOLON,
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
					if (dialect == Dialect.VDM_SL)
					{
						throwMessage(2009, "Can't have instance variables in VDM-SL");
					}

					list.addAll(readInstanceVariables());
					break;

				case TRACES:
					if (dialect == Dialect.VDM_SL &&
						Settings.release != Release.VDM_10)
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
        					checkFor(VDMToken.SEMICOLON,
        						2085, "Missing ';' after thread definition");
        				}
    				}
    				catch (LocatedException e)
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
					}
    				catch (LocatedException e)
    				{
    					report(e, afterArray, sectionArray);
    				}
			}
		}

		return list;
	}

	private AAccessSpecifierAccessSpecifier readAccessSpecifier(boolean async)
		throws LexException, ParserException
	{
		if (dialect == Dialect.VDM_SL)
		{
			return new AAccessSpecifierAccessSpecifier(
					new APrivateAccess(),null,null);
			//return AccessSpecifier.DEFAULT;
		}

		// Defaults
//		boolean isStatic = false;
//		boolean isAsync = false;
		TStatic isStatic = null;
		TAsync isAsync = null;
		//VDMToken access = VDMToken.PRIVATE;
		PAccess access = new APrivateAccess();	
				
		boolean more = true;

		while (more)
		{
			switch (lastToken().type)
			{
				case ASYNC:
					if (async)
					{
						isAsync = new TAsync();
						nextToken();
					}
					else
					{
						throwMessage(2278, "Async only permitted for operations");
						more = false;
					}
					break;

				case STATIC:
					isStatic = new TStatic();
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
					//access = lastToken().type;
					access = new AProtectedAccess();
					nextToken();
					break;

				default:
					more = false;
					break;
			}
		}

		return new AAccessSpecifierAccessSpecifier(access,isStatic, isAsync);
	}

	public ATypeDefinition readTypeDefinition() throws ParserException, LexException
	{
		LexIdentifierToken id = readIdToken("Expecting new type identifier");
		TypeReader tr = getTypeReader();
		SInvariantType invtype = null;

		switch (lastToken().type)
		{
			case EQUALS:
				nextToken();
//				NamedType nt = new NamedType(idToName(id), tr.readType());
				PType type = tr.readType();
				ANamedInvariantType nt = 
					new ANamedInvariantType(id.location,false,idToName(id), type);

				if (type instanceof AUnresolvedType &&
					((AUnresolvedType)type).getTypename().equals(idToName(id)))
				{
					throwMessage(2014, "Recursive type declaration");
				}

				invtype = nt;
				break;

			case COLONCOLON:
				nextToken();
				invtype = new ARecordInvariantType(id.location, false, idToName(id), tr.readFieldList());
				break;

			default:
				throwMessage(2015, "Expecting =<type> or ::<field list>");
		}

		PPattern invPattern = null;
		PExp invExpression = null;

		if (lastToken().is(VDMToken.INV))
		{
			nextToken();
			invPattern =  getPatternReader().readPattern();
			checkFor(VDMToken.EQUALSEQUALS, 2087, "Expecting '==' after pattern in invariant");
			invExpression = getExpressionReader().readExpression();
		}

		return new ATypeDefinition(id.location,idToName(id),null, 
				null,null,null,null,invtype,invPattern,invExpression,null,false);
		
		//return new TypeDefinition(idToName(id), invtype, invPattern, invExpression);
	}

	private List<PDefinition> readTypes() throws LexException, ParserException
	{
		checkFor(VDMToken.TYPES, 2013, "Expected 'types'");
		List<PDefinition> list = new Vector<PDefinition>();

		while (!newSection())
		{
			try
			{
				AAccessSpecifierAccessSpecifier access = readAccessSpecifier(false);
				access.setStatic(new TStatic());
				ATypeDefinition def = readTypeDefinition();
				
				// Force all type defs (invs) to be static
				def.setAccess(access);
				list.add(def);

				if (!newSection())
				{
					checkFor(VDMToken.SEMICOLON,
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

	private List<PDefinition> readValues() throws LexException, ParserException
	{
		checkFor(VDMToken.VALUES, 2013, "Expected 'values'");
		List<PDefinition> list = new Vector<PDefinition>();

		while (!newSection())
		{
			try
			{
				AAccessSpecifierAccessSpecifier access = readAccessSpecifier(false);
				access.setStatic(new TStatic());
				PDefinition def = readValueDefinition(NameScope.GLOBAL);

				// Force all values to be static
				def.setAccess(access);
				list.add(def);

				if (!newSection())
				{
					checkFor(VDMToken.SEMICOLON,
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

	private List<PDefinition> readFunctions() throws LexException, ParserException
	{
		checkFor(VDMToken.FUNCTIONS, 2013, "Expected 'functions'");
		List<PDefinition> list = new Vector<PDefinition>();

		while (!newSection())
		{
			try
			{
				AAccessSpecifierAccessSpecifier access = readAccessSpecifier(false);
				PDefinition def = readFunctionDefinition(NameScope.GLOBAL);

				if (Settings.release == Release.VDM_10)
				{
					// Force all functions to be static for VDM-10
					access.setStatic(new TStatic());
					def.setAccess(access);
				}
				else
				{
					def.setAccess(access);
				}

				list.add(def);

				if (!newSection())
				{
					checkFor(VDMToken.SEMICOLON,
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

	public List<PDefinition> readOperations() throws LexException, ParserException
	{
		checkFor(VDMToken.OPERATIONS, 2013, "Expected 'operations'");
		List<PDefinition> list = new Vector<PDefinition>();

		while (!newSection())
		{
			try
			{
				AAccessSpecifierAccessSpecifier access = readAccessSpecifier(dialect == Dialect.VDM_RT);
				PDefinition def = readOperationDefinition();
				def.setAccess(access);
				list.add(def);

				if (!newSection())
				{
					checkFor(VDMToken.SEMICOLON,
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

	public List<PDefinition> readInstanceVariables() throws LexException, ParserException
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
					checkFor(VDMToken.SEMICOLON,
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
					ignore(VDMToken.SEMICOLON);	// Optional?
				}
			}
			catch (LocatedException e)
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
					checkFor(VDMToken.SEMICOLON,
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
		}
		else if (lastToken().is(VDMToken.BRA))
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

	private PDefinition readExplicitFunctionDefinition(
		LexIdentifierToken funcName, NameScope scope, List<LexNameToken> typeParams)
		throws ParserException, LexException
	{
		// Explicit function definition, like "f: int->bool f(x) == true"

		nextToken();
		PType t = getTypeReader().readType();

		if (!(t instanceof AFunctionType))
		{
			throwMessage(2018, "Function type is not a -> or +> function");
		}

		AFunctionType type = (AFunctionType)t;

		LexIdentifierToken name =
			readIdToken("Expecting identifier after function type in definition");

		if (!name.equals(funcName))
		{
			throwMessage(2019, "Expecting identifier " + funcName.getName() + " after type in definition");
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
    		}
    		else
    		{
    			parameters.add(new Vector<PPattern>());	// empty "()"
    			nextToken();
    		}
		}

		checkFor(VDMToken.EQUALSEQUALS, 2092, "Expecting '==' after parameters");
		ExpressionReader expr = getExpressionReader();
		PExp body = expr.readExpression();
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

//		return new AExplicitFunctionDefinition(funcName.location,
//			idToName(funcName), scope,false,null,getDefaultAccess(),typeParams,
//			 parameters,type,body,precondition,postcondition,measure,
//			 null,null,null,null,false,false,0,null,null,false,false);
//		
		return new AExplicitFunctionDefinition(funcName.location, idToName(funcName), scope, 
				false, getDefaultAccess(), typeParams, parameters, type, body, precondition, postcondition, measure);
	}

	private PDefinition readImplicitFunctionDefinition(
		LexIdentifierToken funcName, NameScope scope, List<LexNameToken> typeParams)
		throws ParserException, LexException
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
			parameterPatterns.add(new APatternListTypePair(false,pl, tr.readType()));

			while (ignore(VDMToken.COMMA))
			{
				pl = pr.readPatternList();
				checkFor(VDMToken.COLON, 2093, "Missing colon after pattern/type parameter");
				parameterPatterns.add(new APatternListTypePair(false, pl, tr.readType()));
			}
		}

    	checkFor(VDMToken.KET, 2124, "Expecting ')' after parameters");

		LexToken firstResult = lastToken();
   		List<PPattern> resultNames = new Vector<PPattern>();
   		List<PType> resultTypes = new Vector<PType>();

   		do
   		{
   			LexIdentifierToken rname = readIdToken("Expecting result identifier");
   	   		resultNames.add(new AIdentifierPattern(firstResult.location,
   	   				null,false, idToName(rname)));
   	   		checkFor(VDMToken.COLON, 2094, "Missing colon in identifier/type return value");
   	   		resultTypes.add(tr.readType());
   		}
   		while (ignore(VDMToken.COMMA));

   		if (lastToken().is(VDMToken.IDENTIFIER))
		{
			throwMessage(2261, "Missing comma between return types?");
		}

   		APatternTypePair resultPattern = null;

   		if (resultNames.size() > 1)
   		{
   			resultPattern = new APatternTypePair(false,
   	   			new ATuplePattern(firstResult.location,null,false,resultNames),
 	   			new AProductType(firstResult.location,false,null, resultTypes));
   		}
   		else
   		{
   			resultPattern = new APatternTypePair(false,
   	   			resultNames.get(0), resultTypes.get(0));
   		}

		ExpressionReader expr = getExpressionReader();
		PExp body = null;
		PExp precondition = null;
		PExp postcondition = null;
		LexNameToken measure = null;

		if (lastToken().is(VDMToken.EQUALSEQUALS))		// extended implicit function
		{
			nextToken();
			body = expr.readExpression();
		}

		if (lastToken().is(VDMToken.PRE))
		{
			nextToken();
			precondition = expr.readExpression();
		}

		if (body == null)	// Mandatory for standard implicit functions
		{
			checkFor(VDMToken.POST, 2095, "Implicit function must have post condition");
			postcondition = expr.readExpression();
		}
		else
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
		
		List<PType> ptypes = new NodeList<PType>(null);

		for (APatternListTypePair ptp: parameterPatterns)
		{
			ptypes.addAll(getTypeList(ptp));
		}

		// NB: implicit functions are always +> total, apparently
		AFunctionType functionType = new AFunctionType(funcName.location, false, null, false, ptypes, (PType) resultPattern.getType().clone());
		// functionType.setDefinitions(value) = new DefinitionList(this);

//		AImplicitFunctionDefinition functionDef = new AImplicitFunctionDefinition(funcName.location, idToName(funcName), scope, false, null, getDefaultAccess(), functionType, typeParams, parameterPatterns, resultPattern, body, precondition, postcondition, measure, null, null, null, false, false, 0, null, functionType);

		AImplicitFunctionDefinition functionDef = new AImplicitFunctionDefinition(funcName.location, idToName(funcName), 
				scope, false, getDefaultAccess(), typeParams, parameterPatterns, resultPattern, body, precondition, postcondition, measure, functionType);
		
		List<PDefinition> defs = new Vector<PDefinition>();
		defs.add(functionDef);
		functionType.setDefinitions(defs);
		return functionDef;
//		return new ImplicitFunctionDefinition(
//			idToName(funcName), scope, typeParams, parameterPatterns, resultPattern,
//			body, precondition, postcondition, measure);
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
    	}
    	catch (ParserException e)		// Not a function then...
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
		}
		catch (ParserException e)
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
 		return new AValueDefinition(lastToken().location,null,scope,false,
				null,null,type,p,getExpressionReader().readExpression(),null,null);
//		return new ValueDefinition(
//			p, scope, type, getExpressionReader().readExpression());
	}

	private PDefinition readStateDefinition() throws ParserException, LexException
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

		checkFor(VDMToken.END, 2100, "Expecting 'end' after state definition");
//		return new AStateDefinition(name.location,idToName(name),NameScope.STATE,null,null,null, 
//				null,fieldList,invPattern, invExpression,null, initPattern, initExpression, null,
//				null,null, null);
		
		ARecordInvariantType recordType = new ARecordInvariantType(name.location,false,idToName(name), fieldList);
		ALocalDefinition recordDefinition = new ALocalDefinition(name.location,  idToName(name), NameScope.STATE, true, getDefaultAccess(),recordType,null);
//		recordDefinition.markUsed();	// Can't be exported anyway
//		statedefs.add(recordDefinition);
		
		AStateDefinition stateDef = new AStateDefinition(name.location, idToName(name),NameScope.STATE, false, getDefaultAccess(), null, fieldList, invPattern, invExpression, null, initPattern, initExpression, null, recordDefinition, recordType);
		
		stateDef.getStateDefs().add(recordDefinition);
		
		for (AFieldField f : fieldList)
		{
			stateDef.getStateDefs().add(new ALocalDefinition(
					f.getTagname().location, f.getTagname(), NameScope.STATE, false,getDefaultAccess(),f.getType(),null));

				ALocalDefinition ld = new ALocalDefinition(f.getTagname().location,
					f.getTagname().getOldName(), NameScope.OLDSTATE, true, getDefaultAccess(),f.getType(),null);

//				ld.markUsed();		// Else we moan about unused ~x names
				stateDef.getStateDefs().add(ld);
		}
		
		return stateDef;
	}

	private PDefinition readOperationDefinition()
		throws ParserException, LexException
	{
		PDefinition def = null;
		LexIdentifierToken funcName = readIdToken("Expecting new operation identifier");

		if (lastToken().is(VDMToken.COLON))
		{
			def = readExplicitOperationDefinition(funcName);
		}
		else if (lastToken().is(VDMToken.BRA))
		{
			def = readImplicitOperationDefinition(funcName);
		}
		else if (lastToken().is(VDMToken.SEQ_OPEN))
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

	private PDefinition readExplicitOperationDefinition(LexIdentifierToken funcName)
		throws ParserException, LexException
	{
		// Like "f: int ==> bool f(x) == <statement>"

		nextToken();
		AOperationType type = getTypeReader().readOperationType();

		LexIdentifierToken name =
			readIdToken("Expecting operation identifier after type in definition");

		if (!name.equals(funcName))
		{
			throwMessage(2022, "Expecting name " + funcName.getName() + " after type in definition");
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
		}
		else
		{
			parameters = new Vector<PPattern>();		// empty "()"
			nextToken();
		}

		checkFor(VDMToken.EQUALSEQUALS, 2102, "Expecting '==' after parameters");
		PStm body = getStatementReader().readStatement();
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
//super(Pass.DEFS, name.location, name, NameScope.GLOBAL);
		
		
//		AExplicitOperationDefinition def = new AExplicitOperationDefinition(funcName.location,
//			idToName(funcName),NameScope.GLOBAL,false,null,getDefaultAccess(), type, parameters,body,
//			precondition,postcondition,type,null,null,null,null,null,false);
		
		AExplicitOperationDefinition def = new AExplicitOperationDefinition(funcName.location, idToName(funcName), 
				NameScope.GLOBAL, false, getDefaultAccess(), parameters, body, precondition, postcondition, type, null, false);

		return def;
	}

	private PDefinition readImplicitOperationDefinition(LexIdentifierToken funcName)
		throws ParserException, LexException
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
			parameterPatterns.add(new APatternListTypePair(false,pl, tr.readType()));

			while (ignore(VDMToken.COMMA))
			{
				pl = pr.readPatternList();
				checkFor(VDMToken.COLON, 2103, "Missing colon after pattern/type parameter");
				parameterPatterns.add(new APatternListTypePair(false,pl, tr.readType()));
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
				resultNames.add(new AIdentifierPattern(rname.location,null,false,idToName(rname)));
				checkFor(VDMToken.COLON, 2104, "Missing colon in identifier/type return value");
				resultTypes.add(tr.readType());
			}
			while (ignore(VDMToken.COMMA));

			if (lastToken().is(VDMToken.IDENTIFIER))
			{
				throwMessage(2261, "Missing comma between return types?");
			}

			if (resultNames.size() > 1)
			{
				resultPattern = new APatternTypePair(false,
					new ATuplePattern(firstResult.location,null,false,resultNames),
					new AProductType(firstResult.location,false,null, resultTypes));
			}
			else
			{
				resultPattern = new APatternTypePair(false,
					resultNames.get(0), resultTypes.get(0));
			}
		}

		PStm body = null;

		if (lastToken().is(VDMToken.EQUALSEQUALS))		// extended implicit operation
		{
			nextToken();
			body = getStatementReader().readStatement();
		}

		ASpecificationStm spec = readSpecification(funcName.location, body == null);
		
		List<PType> ptypes = new Vector<PType>();

		for (APatternListTypePair ptp: parameterPatterns)
		{
			ptypes.addAll(getTypeList(ptp));
		}
		AOperationType operationType = new AOperationType(funcName.location, false, null, ptypes, (resultPattern == null ? new AVoidType(funcName.location, false, null)
				: resultPattern.getType()));
		
//		AImplicitOperationDefinition def = new AImplicitOperationDefinition(funcName.location,
//			idToName(funcName),NameScope.GLOBAL,false,null,getDefaultAccess(),null, parameterPatterns, resultPattern,
//			body,spec.getExternals(),spec.getPrecondition(),spec.getPostcondition(),spec.getErrors(),operationType,null,null,null,null,null,false);
		
		AImplicitOperationDefinition def = new AImplicitOperationDefinition(funcName.location,
			idToName(funcName),NameScope.GLOBAL,false, getDefaultAccess(), parameterPatterns, resultPattern, body, spec.getExternals(), spec.getPrecondition(), 
			spec.getPostcondition(), spec.getErrors(), operationType, null, false);
		
		return def;
	}

	public ASpecificationStm readSpecification(
		LexLocation location, boolean postMandatory)
		throws ParserException, LexException
	{
		List<AExternalClause> externals = null;

		if (lastToken().is(VDMToken.EXTERNAL))
		{
			externals = new Vector<AExternalClause>();
			nextToken();

			while (lastToken().is(VDMToken.READ) || lastToken().is(VDMToken.WRITE))
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

		if (postMandatory)	// Mandatory for standard implicit operations
		{
			checkFor(VDMToken.POST, 2105, "Implicit operation must define a post condition");
			postcondition = expr.readExpression();
		}
		else
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
				errors.add(new AErrorCase(name, left, right));
			}

			if (errors.isEmpty())
			{
				throwMessage(2025, "Expecting <name>: exp->exp in errs clause");
			}
		}
		
		return new ASpecificationStm(location,externals, precondition, postcondition, errors);
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

		return new AExternalClause(mode, names, type);
	}

	public AEqualsDefinition readEqualsDefinition()
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
    		PPattern pattern = getPatternReader().readPattern();
     		checkFor(VDMToken.EQUALS, 2108, "Expecting <pattern>=<exp>");
     		PExp test = getExpressionReader().readExpression();
    		reader.unpush();

     		return new AEqualsDefinition(location, null,null,null,null,null,null,
     				pattern,null,null,test,null,null,null);
    	}
    	catch (ParserException e)
    	{
			e.adjustDepth(reader.getTokensRead());
    		reader.pop();
    		equalsDefError = e;
    	}

		try	// "def" <type bind> "=" <expression> "in" ...
		{
        	reader.push();
    		ATypeBind typebind = getBindReader().readTypeBind();
     		checkFor(VDMToken.EQUALS, 2109, "Expecting <type bind>=<exp>");
     		PExp test = getExpressionReader().readExpression();
    		reader.unpush();

     		return new AEqualsDefinition(location,null,null,null,null,null,null,
     				null,typebind,null, test,null,null,null);
		}
		catch (ParserException e)
		{
			e.adjustDepth(reader.getTokensRead());
    		reader.pop();
			equalsDefError = e.deeperThan(equalsDefError) ? e : equalsDefError;
		}

		try	// "def" <pattern> "in set" <equals-expression> "in" ...
		{
        	reader.push();
    		PPattern pattern = getPatternReader().readPattern();
     		checkFor(VDMToken.IN, 2110, "Expecting <pattern> in set <set exp>");
     		checkFor(VDMToken.SET, 2111, "Expecting <pattern> in set <set exp>");
     		AEqualsBinaryExp test = getExpressionReader().readDefEqualsExpression();
     		ASetBind setbind = new ASetBind(pattern.getLocation(),pattern, test.getLeft());
     		reader.unpush();
     		
     		return new AEqualsDefinition(location,null,null,null,null,null,null,pattern, null,
     				setbind, test,null,null,pattern.getDefinitions());
		}
		catch (ParserException e)
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
			return new AClassInvariantDefinition(token.location,
					className,null,null,null,null,null, exp);
//			return new ClassInvariantDefinition(
//				className.getInvName(token.location), exp);
		}
		else
		{
			AAccessSpecifierAccessSpecifier access = readAccessSpecifier(false);
			AAssignmentDefinition def = getStatementReader().readAssignmentDefinition();
			AInstanceVariableDefinition ivd =
				new AInstanceVariableDefinition(token.location, def.getName(),null,null,null,access, 
					null, def.getExpression(),def.getType(),null,null);
			ivd.setAccess(access);
			return ivd;
		}
    }

	private PDefinition readThreadDefinition() throws LexException, ParserException
	{
		LexToken token = lastToken();

		if (token.is(VDMToken.PERIODIC))
		{
			nextToken();
			checkFor(VDMToken.BRA, 2112, "Expecting '(' after periodic");
			List<PExp> args = getExpressionReader().readExpressionList();
			checkFor(VDMToken.KET, 2113, "Expecting ')' after period arguments");
			checkFor(VDMToken.BRA, 2114, "Expecting '(' after periodic(...)");
			LexNameToken name = readNameToken("Expecting (name) after periodic(...)");
			checkFor(VDMToken.KET, 2115, "Expecting (name) after periodic(...)");
			PStm statement = new APeriodicStm(token.location,name,args);
			return new AThreadDefinition(name.location,name,NameScope.GLOBAL,null,null,new AAccessSpecifierAccessSpecifier(new AProtectedAccess(),null,null),null,statement, LexNameToken.getThreadName(statement.getLocation()), null);
		}
		else
		{
			PStm stmt = getStatementReader().readStatement();
			return new AThreadDefinition(stmt.getLocation(),null,NameScope.GLOBAL,null,null,new AAccessSpecifierAccessSpecifier(new AProtectedAccess(),null,null),null,stmt, LexNameToken.getThreadName(stmt.getLocation()), null);
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
				return new APerSyncDefinition(
						token.location, name,null,null,null,null,null,null,exp);
				//return new PerSyncDefinition(token.location, name, exp);

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

				return new AMutexSyncDefinition(token.location,null,null,
												null,null,null,null, opnames);

			default:
				throwMessage(2028, "Expecting 'per' or 'mutex'");
				return null;
		}
	}

	private PDefinition readNamedTraceDefinition()
		throws ParserException, LexException
	{
		LexLocation start = lastToken().location;
		List<String> names = readTraceIdentifierList();
		checkFor(VDMToken.COLON, 2264, "Expecting ':' after trace name(s)");
		List<List<PTraceDefinition>> traces = readTraceDefinitionList();

//		return new NamedTraceDefinition(start, names, traces);
		//TODO
		LexNameToken name = new LexNameToken(
				start.module, Utils.listToString(names, "_"),start);
		PAccessSpecifier access = new AAccessSpecifierAccessSpecifier(new APublicAccess(), null, null);
		return new ANamedTraceDefinition(start, name, NameScope.GLOBAL, false, null, access , null, traces);
	}

	private List<String> readTraceIdentifierList()
		throws ParserException, LexException
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

	private List<List<PTraceDefinition>> readTraceDefinitionList()
		throws LexException, ParserException
	{
		List<List<PTraceDefinition>> list = new Vector<List<PTraceDefinition>>();
		list.add(readTraceDefinitionTerm());

		while (lastToken().is(VDMToken.SEMICOLON))
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

	private List<PTraceDefinition> readTraceDefinitionTerm()
		throws LexException, ParserException
	{
		List<PTraceDefinition> term = new Vector<PTraceDefinition>();
		term.add(readTraceDefinition());

		while (lastToken().is(VDMToken.PIPE))
		{
			nextToken();
			term.add(readTraceDefinition());
		}

		return term;
	}

	private PTraceDefinition readTraceDefinition()
		throws LexException, ParserException
	{
		if (lastToken().is(VDMToken.LET))
		{
			return readTraceBinding();
		}
		else
		{
			return readTraceRepeat();
		}
	}

	private PTraceDefinition readTraceRepeat()
		throws ParserException, LexException
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

				LexIntegerToken lit = (LexIntegerToken)lastToken();
				from = lit.value;
				to = lit.value;

				switch (nextToken().type)
				{
					case COMMA:
						if (nextToken().isNot(VDMToken.NUMBER))
						{
							throwMessage(2265, "Expecting '{n1, n2}' after trace definition");
						}

						lit = (LexIntegerToken)readToken();
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

       	return new ARepeatTraceDefinition(token.location, core, from, to);
	}

	private PTraceDefinition readTraceBinding()
		throws ParserException, LexException
	{
		checkFor(VDMToken.LET, 2230, "Expecting 'let'");
		ParserException letDefError = null;

		try
		{
			reader.push();
			PTraceDefinition def = readLetDefBinding();
			reader.unpush();
			return def;
		}
		catch (ParserException e)
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
		}
		catch (ParserException e)
		{
			e.adjustDepth(reader.getTokensRead());
			reader.pop();
			throw e.deeperThan(letDefError) ? e : letDefError;
		}
	}

	private PTraceDefinition readLetDefBinding()
		throws ParserException, LexException
	{
		List<AValueDefinition> localDefs = new Vector<AValueDefinition>();
		LexToken start = lastToken();

		PDefinition def = readLocalDefinition(NameScope.LOCAL);

		if (!(def instanceof AValueDefinition))
		{
			throwMessage(2270, "Only value definitions allowed in traces");
		}

		localDefs.add((AValueDefinition)def);

		while (ignore(VDMToken.COMMA))
		{
			def = readLocalDefinition(NameScope.LOCAL);

			if (!(def instanceof AValueDefinition))
			{
				throwMessage(2270, "Only value definitions allowed in traces");
			}

			localDefs.add((AValueDefinition)def);
		}

		checkFor(VDMToken.IN, 2231, "Expecting 'in' after local definitions");
		PTraceDefinition body = readTraceDefinition();

		return new ALetDefBindingTraceDefinition(start.location, localDefs, body);
	}

	private PTraceDefinition readLetBeStBinding()
		throws ParserException, LexException
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

		return new ALetBeStBindingTraceDefinition(start.location, bind, stexp, body, null);
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

				if (!(stmt instanceof ACallStm) &&
					!(stmt instanceof ACallObjectStm))
				{
					throwMessage(2267,
						"Expecting 'obj.op(args)' or 'op(args)'", token);
				}

				return new AApplyExpressionTraceCoreDefinition(stmt.getLocation(),stmt, getCurrentModule());

			case BRA:
				nextToken();
				List<List<PTraceDefinition>> list = readTraceDefinitionList();
				checkFor(VDMToken.KET, 2269, "Expecting '(trace definitions)'");
				return new ABracketedExpressionTraceCoreDefinition(token.location, list);

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
				return new AConcurrentExpressionTraceCoreDefinition(token.location, defs);

			default:
				throwMessage(2267, "Expecting 'obj.op(args)' or 'op(args)'", token);
				return null;
		}
	}
}
