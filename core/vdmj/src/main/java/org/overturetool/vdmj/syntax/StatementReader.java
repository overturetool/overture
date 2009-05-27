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

import org.overturetool.vdmj.definitions.AssignmentDefinition;
import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.expressions.ExpressionList;
import org.overturetool.vdmj.expressions.UndefinedExpression;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.lex.LexIdentifierToken;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.lex.LexToken;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.lex.Token;
import org.overturetool.vdmj.patterns.MultipleBind;
import org.overturetool.vdmj.patterns.Pattern;
import org.overturetool.vdmj.patterns.PatternBind;
import org.overturetool.vdmj.patterns.PatternList;
import org.overturetool.vdmj.statements.AlwaysStatement;
import org.overturetool.vdmj.statements.AssignmentStatement;
import org.overturetool.vdmj.statements.AtomicStatement;
import org.overturetool.vdmj.statements.BlockStatement;
import org.overturetool.vdmj.statements.CallObjectStatement;
import org.overturetool.vdmj.statements.CallStatement;
import org.overturetool.vdmj.statements.CaseStmtAlternative;
import org.overturetool.vdmj.statements.CasesStatement;
import org.overturetool.vdmj.statements.CyclesStatement;
import org.overturetool.vdmj.statements.DefStatement;
import org.overturetool.vdmj.statements.DurationStatement;
import org.overturetool.vdmj.statements.ElseIfStatement;
import org.overturetool.vdmj.statements.ErrorStatement;
import org.overturetool.vdmj.statements.ExitStatement;
import org.overturetool.vdmj.statements.FieldDesignator;
import org.overturetool.vdmj.statements.ForAllStatement;
import org.overturetool.vdmj.statements.ForIndexStatement;
import org.overturetool.vdmj.statements.ForPatternBindStatement;
import org.overturetool.vdmj.statements.IdentifierDesignator;
import org.overturetool.vdmj.statements.IfStatement;
import org.overturetool.vdmj.statements.LetBeStStatement;
import org.overturetool.vdmj.statements.LetDefStatement;
import org.overturetool.vdmj.statements.MapSeqDesignator;
import org.overturetool.vdmj.statements.NonDeterministicStatement;
import org.overturetool.vdmj.statements.NotYetSpecifiedStatement;
import org.overturetool.vdmj.statements.ObjectApplyDesignator;
import org.overturetool.vdmj.statements.ObjectDesignator;
import org.overturetool.vdmj.statements.ObjectFieldDesignator;
import org.overturetool.vdmj.statements.ObjectIdentifierDesignator;
import org.overturetool.vdmj.statements.ObjectNewDesignator;
import org.overturetool.vdmj.statements.ObjectSelfDesignator;
import org.overturetool.vdmj.statements.ReturnStatement;
import org.overturetool.vdmj.statements.SkipStatement;
import org.overturetool.vdmj.statements.SpecificationStatement;
import org.overturetool.vdmj.statements.StartStatement;
import org.overturetool.vdmj.statements.StateDesignator;
import org.overturetool.vdmj.statements.Statement;
import org.overturetool.vdmj.statements.SubclassResponsibilityStatement;
import org.overturetool.vdmj.statements.TixeStatement;
import org.overturetool.vdmj.statements.TixeStmtAlternative;
import org.overturetool.vdmj.statements.TrapStatement;
import org.overturetool.vdmj.statements.WhileStatement;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.Type;


/**
 * A syntax analyser to parse statements.
 */

public class StatementReader extends SyntaxReader
{
	public StatementReader(LexTokenReader reader)
	{
		super(reader);
	}

	public Statement readStatement() throws ParserException, LexException
	{
		Statement stmt = null;
		LexToken token = lastToken();
		LexLocation location = token.location;

		switch (token.type)
		{
			case LET:
				stmt = readLetStatement(token);
				break;

			case RETURN:
				stmt = readReturnStatement(location);
				break;

			case BRA:
				stmt = readBlockStatement(location);
				break;

			case NAME:
			case IDENTIFIER:
			case NEW:
			case SELF:
				stmt = readAssignmentOrCallStatement(token);
				break;

			case IF:
				nextToken();	// to allow elseif to call it too
				stmt = readConditionalStatement(location);
				break;

			case CASES:
				stmt = readCasesStatement(location);
				break;

			case FOR:
				stmt = readForStatement(location);
				break;

			case WHILE:
				stmt = readWhileStatement(location);
				break;

			case PIPEPIPE:
				stmt = readNonDetStatement(location);
				break;

			case ALWAYS:
				stmt = readAlwaysStatement(location);
				break;

			case ATOMIC:
				stmt = readAtomicStatement(location);
				break;

			case TRAP:
				stmt = readTrapStatement(location);
				break;

			case TIXE:
				stmt = readTixeStatement(location);
				break;

			case DEF:
				stmt = readDefStatement(location);
				break;

			case EXIT:
				stmt = readExitStatement(location);
				break;

			case SEQ_OPEN:
				stmt = readSpecStatement(location);
				break;

			case ERROR:
				stmt = new ErrorStatement(location);
				nextToken();
				break;

			case SKIP:
				stmt = new SkipStatement(location);
				nextToken();
				break;

			case IS:
				switch (nextToken().type)
				{
					case NOT:
						nextToken();
						checkFor(Token.YET, 2187, "Expecting 'is not yet specified");
						checkFor(Token.SPECIFIED, 2188, "Expecting 'is not yet specified");
						stmt = new NotYetSpecifiedStatement(location);
						break;

					case SUBCLASS:
						nextToken();
						checkFor(Token.RESPONSIBILITY, 2189, "Expecting 'is subclass responsibility'");
						stmt = new SubclassResponsibilityStatement(location);
						break;

					default:
						throwMessage(2062, "Expected 'is not specified' or 'is subclass responsibility'");
				}
				break;

			case START:
				stmt = readStartStatement(location);
				break;

			case STARTLIST:
				stmt = readStartlistStatement(location);
				break;

			case CYCLES:
				stmt = readCyclesStatement(location);
				break;

			case DURATION:
				stmt = readDurationStatement(location);
				break;

			default:
				throwMessage(2063, "Unexpected token in statement");
		}

		return stmt;
	}

	private Statement readExitStatement(LexLocation token)
		throws ParserException, LexException
	{
		checkFor(Token.EXIT, 2190, "Expecting 'exit'");

		try
		{
			reader.push();
			Expression exp = getExpressionReader().readExpression();
			reader.unpush();
			return new ExitStatement(token, exp);
		}
		catch (ParserException e)
		{
			reader.pop();
		}

		return new ExitStatement(token);
	}

	private Statement readTixeStatement(LexLocation token)
		throws ParserException, LexException
	{
		checkFor(Token.TIXE, 2191, "Expecting 'tixe'");

		List<TixeStmtAlternative> traps = new Vector<TixeStmtAlternative>();
		BindReader br = getBindReader();
		checkFor(Token.SET_OPEN, 2192, "Expecting '{' after 'tixe'");

		while (lastToken().isNot(Token.SET_CLOSE))
		{
			PatternBind patternBind = br.readPatternOrBind();
			checkFor(Token.MAPLET, 2193, "Expecting '|->' after pattern bind");
			Statement result = readStatement();
			traps.add(new TixeStmtAlternative(patternBind, result));
			ignore(Token.COMMA);
		}

		nextToken();
		checkFor(Token.IN, 2194, "Expecting 'in' after tixe traps");
		Statement body = getStatementReader().readStatement();

		return new TixeStatement(token, traps, body);
	}

	private Statement readTrapStatement(LexLocation token)
		throws ParserException, LexException
	{
		checkFor(Token.TRAP, 2195, "Expecting 'trap'");
		PatternBind patternBind = getBindReader().readPatternOrBind();
		checkFor(Token.WITH, 2196, "Expecting 'with' in trap statement");
		Statement with = getStatementReader().readStatement();
		checkFor(Token.IN, 2197, "Expecting 'in' in trap statement");
		Statement body = getStatementReader().readStatement();
		return new TrapStatement(token, patternBind, with, body);
	}

	private Statement readAlwaysStatement(LexLocation token)
		throws ParserException, LexException
	{
		checkFor(Token.ALWAYS, 2198, "Expecting 'always'");
		Statement always = getStatementReader().readStatement();
		checkFor(Token.IN, 2199, "Expecting 'in' after 'always' statement");
		Statement body = getStatementReader().readStatement();
		return new AlwaysStatement(token, always, body);
	}

	private Statement readNonDetStatement(LexLocation token)
		throws ParserException, LexException
	{
		checkFor(Token.PIPEPIPE, 2200, "Expecting '||'");
		checkFor(Token.BRA, 2201, "Expecting '(' after '||'");
		NonDeterministicStatement block = new NonDeterministicStatement(token);
		block.add(readStatement());		// Must be one

		while (ignore(Token.COMMA))
		{
			block.add(readStatement());
		}

		checkFor(Token.KET, 2202, "Expecting ')' at end of '||' block");
		return block;
	}

	private Statement readAssignmentOrCallStatement(LexToken token)
		throws ParserException, LexException
	{
		ParserException assignError = null;
		Statement stmt = null;

		try
		{
			reader.push();
			stmt = readAssignmentStatement(token.location);
			reader.unpush();
			return stmt;
		}
		catch (ParserException e)
		{
			reader.pop();
			e.adjustDepth(reader.getTokensRead());
			assignError = e;
		}

		try
		{
			reader.push();
			stmt = readCallStatement();
			reader.unpush();
			return stmt;
		}
		catch (ParserException e)
		{
			reader.pop();
			e.adjustDepth(reader.getTokensRead());
			throw e.deeperThan(assignError) ? e : assignError;
		}
	}

	private Statement readAtomicStatement(LexLocation token)
		throws ParserException, LexException
	{
		checkFor(Token.ATOMIC, 2203, "Expecting 'atomic'");
		checkFor(Token.BRA, 2204, "Expecting '(' after 'atomic'");
		List<AssignmentStatement> assignments = new Vector<AssignmentStatement>();

		assignments.add(readAssignmentStatement(lastToken().location));

		while (ignore(Token.SEMICOLON))
		{
			assignments.add(readAssignmentStatement(lastToken().location));
		}

		checkFor(Token.KET, 2205, "Expecting ')' after atomic assignments");
		return new AtomicStatement(token, assignments);
	}

	private Statement readCallStatement()
		throws ParserException, LexException
	{
		if (dialect != Dialect.VDM_SL)
		{
			return readObjectCallStatement();
		}
		else
		{
			return readSimpleCallStatement();
		}
	}

	private Statement readSimpleCallStatement()
		throws ParserException, LexException
	{
		LexNameToken name =
			readNameToken("Expecting operation name in call statement");

		checkFor(Token.BRA, 2206, "Expecting '(' after call operation name");
		ExpressionList args = new ExpressionList();
		ExpressionReader er = getExpressionReader();

		while (lastToken().isNot(Token.KET))
		{
			args.add(er.readExpression());
			ignore(Token.COMMA);
		}

		nextToken();

		return new CallStatement(name, args);
	}

	private Statement readObjectCallStatement()
		throws ParserException, LexException
    {
		ObjectDesignator designator = readObjectDesignator();

		// All operation calls actually look like object apply designators,
		// since they end with <name>([args]). So we unpick the apply
		// designator to extract the operation name and args.

		if (!(designator instanceof ObjectApplyDesignator))
		{
			throwMessage(2064, "Expecting <object>.identifier(args) or name(args)");
		}

		ObjectApplyDesignator oad = (ObjectApplyDesignator)designator;
		ExpressionList args = oad.args;

		if (oad.object instanceof ObjectFieldDesignator)
		{
			ObjectFieldDesignator ofd = (ObjectFieldDesignator)oad.object;
    		return new CallObjectStatement(ofd.object, ofd.classname, ofd.fieldname, args);
		}
		else if (oad.object instanceof ObjectIdentifierDesignator)
		{
			ObjectIdentifierDesignator oid = (ObjectIdentifierDesignator)oad.object;
			return new CallStatement(oid.name, args);
		}
		else
		{
			throwMessage(2065, "Expecting <object>.name(args) or name(args)");
			return null;
		}
    }

	private ObjectDesignator readObjectDesignator()
		throws ParserException, LexException
	{
		ObjectDesignator des = readSimpleObjectDesignator();
		boolean done = false;

		while (!done)
		{
			switch (lastToken().type)
			{
				case POINT:
					LexToken field = nextToken();

					// If we just read a qualified name, we're dealing with
					// something like new A().X`op(), else it's the more usual
					// new A().op().

					String name = null;

					switch (field.type)
					{
						case IDENTIFIER:
							name = ((LexIdentifierToken)field).name;
							des = new ObjectFieldDesignator(des, null, name);
							break;

						case NAME:
							LexNameToken tok = (LexNameToken)field;
							des = new ObjectFieldDesignator(des, tok.module, tok.name);
							break;

						default:
							throwMessage(2066, "Expecting object field name");
					}

					nextToken();
					break;

				case BRA:
					nextToken();
			    	ExpressionReader er = getExpressionReader();
			    	ExpressionList args = new ExpressionList();

			    	while (lastToken().isNot(Token.KET))
			    	{
			    		args.add(er.readExpression());
			    		ignore(Token.COMMA);
			    	}

			    	nextToken();
					des = new ObjectApplyDesignator(des, args);
					break;

				default:
					done = true;
					break;
			}
		}

		return des;
	}

	private ObjectDesignator readSimpleObjectDesignator()
		throws LexException, ParserException
	{
		LexToken token = readToken();

		switch (token.type)
		{
			case SELF:
				return new ObjectSelfDesignator(token.location);

			case IDENTIFIER:
				return new ObjectIdentifierDesignator(idToName((LexIdentifierToken)token));

			case NAME:
				return new ObjectIdentifierDesignator((LexNameToken)token);

			case NEW:
				LexIdentifierToken name = readIdToken("Expecting class name after 'new'");
				checkFor(Token.BRA, 2207, "Expecting '(' after new class name");

		    	ExpressionList args = new ExpressionList();
		    	ExpressionReader er = getExpressionReader();

		    	while (lastToken().isNot(Token.KET))
		    	{
		    		args.add(er.readExpression());
		    		ignore(Token.COMMA);
		    	}

		    	nextToken();
				return new ObjectNewDesignator(name, args);

			default:
				throwMessage(2067, "Expecting 'self', 'new' or name in object designator");
				break;
		}

		return null;
	}

	private Statement readWhileStatement(LexLocation token)
		throws ParserException, LexException
	{
		checkFor(Token.WHILE, 2208, "Expecting 'while'");
		Expression exp = getExpressionReader().readExpression();
		checkFor(Token.DO, 2209, "Expecting 'do' after while expression");
		Statement body = getStatementReader().readStatement();
		return new WhileStatement(token, exp, body);
	}

	private Statement readForStatement(LexLocation token)
		throws ParserException, LexException
	{
		checkFor(Token.FOR, 2210, "Expecting 'for'");
		Statement forstmt = null;

		if (lastToken().is(Token.ALL))
		{
			nextToken();
			Pattern p = getPatternReader().readPattern();
			checkFor(Token.IN, 2211, "Expecting 'in set' after 'for all'");
			checkFor(Token.SET, 2212, "Expecting 'in set' after 'for all'");
			Expression set = getExpressionReader().readExpression();
			checkFor(Token.DO, 2213, "Expecting 'do' after for all expression");
			Statement body = getStatementReader().readStatement();
			return new ForAllStatement(token, p, set, body);
		}
		else
		{
			ParserException forIndexError = null;

			try
			{
				reader.push();
				forstmt = readForIndexStatement(token);
				reader.unpush();
				return forstmt;
			}
			catch (ParserException e)
			{
				reader.pop();
				e.adjustDepth(reader.getTokensRead());
				forIndexError = e;
			}

			try
			{
				reader.push();
				forstmt = readForPatternBindStatement(token);
				reader.unpush();
				return forstmt;
			}
			catch (ParserException e)
			{
				reader.pop();
				e.adjustDepth(reader.getTokensRead());
				throw e.deeperThan(forIndexError) ? e : forIndexError;
			}
		}
	}

	private Statement readForPatternBindStatement(LexLocation token)
		throws ParserException, LexException
	{
		PatternBind pb = getBindReader().readPatternOrBind();
		checkFor(Token.IN, 2214, "Expecting 'in' after pattern bind");
		boolean reverse = false; // was ignore(Token.REVERSE);
		Expression exp = getExpressionReader().readExpression();
		checkFor(Token.DO, 2215, "Expecting 'do' before loop statement");
		Statement body = getStatementReader().readStatement();
		return new ForPatternBindStatement(token, pb, reverse, exp, body);
	}

	private Statement readForIndexStatement(LexLocation token)
		throws ParserException, LexException
	{
		LexIdentifierToken var = readIdToken("Expecting variable identifier");
		checkFor(Token.EQUALS, 2216, "Expecting '=' after for variable");
		Expression from = getExpressionReader().readExpression();
		checkFor(Token.TO, 2217, "Expecting 'to' after from expression");
		Expression to = getExpressionReader().readExpression();
		Expression by = null;

		if (lastToken().is(Token.BY))
		{
			nextToken();
			by = getExpressionReader().readExpression();
		}

		checkFor(Token.DO, 2218, "Expecting 'do' before loop statement");
		Statement body = getStatementReader().readStatement();
		return new ForIndexStatement(token, idToName(var), from, to, by, body);
	}

	private Statement readConditionalStatement(LexLocation token)
		throws ParserException, LexException
	{
		Expression exp = getExpressionReader().readExpression();
		checkFor(Token.THEN, 2219, "Missing 'then'");
		Statement thenStmt = readStatement();
		List<ElseIfStatement> elseIfList = new Vector<ElseIfStatement>();

		while (lastToken().is(Token.ELSEIF))
		{
			LexToken elseif = lastToken();
			nextToken();
			elseIfList.add(readElseIfStatement(elseif.location));
		}

		Statement elseStmt = null;

		if (lastToken().is(Token.ELSE))
		{
			nextToken();
			elseStmt = readStatement();
		}

		return new IfStatement(token, exp, thenStmt, elseIfList, elseStmt);
	}

	private ElseIfStatement readElseIfStatement(LexLocation token)
		throws ParserException, LexException
	{
		Expression exp = getExpressionReader().readExpression();
		checkFor(Token.THEN, 2220, "Missing 'then' after 'elseif' expression");
		Statement thenStmt = readStatement();
		return new ElseIfStatement(token, exp, thenStmt);
	}

	private AssignmentStatement readAssignmentStatement(LexLocation token)
		throws ParserException, LexException
	{
		StateDesignator sd = readStateDesignator();
		checkFor(Token.ASSIGN, 2222, "Expecting ':=' in state assignment statement");
		return new AssignmentStatement(token, sd, getExpressionReader().readExpression());
	}

	private StateDesignator readStateDesignator()
		throws ParserException, LexException
	{
		LexNameToken name =
			readNameToken("Expecting name in assignment statement");

		StateDesignator sd = new IdentifierDesignator(name);

		while (lastToken().is(Token.POINT) || lastToken().is(Token.BRA))
		{
			if (lastToken().is(Token.POINT))
			{
				if (nextToken().isNot(Token.IDENTIFIER))
				{
					throwMessage(2068, "Expecting field identifier");
				}

				sd = new FieldDesignator(sd, lastIdToken());
				nextToken();
			}
			else
			{
				nextToken();
				Expression exp = getExpressionReader().readExpression();
				checkFor(Token.KET, 2223, "Expecting ')' after map/seq reference");
				sd = new MapSeqDesignator(sd, exp);
			}
		}

		return sd;
	}

	public Statement readBlockStatement(LexLocation token)
		throws ParserException, LexException
	{
		checkFor(Token.BRA, 2224, "Expecting statement block");
		BlockStatement block = new BlockStatement(token, readDclStatements());

		while (true)	// Loop for continue in exceptions
		{
			try
			{
				block.add(readStatement());

				while (!lastToken().is(Token.KET))
				{
					checkFor(Token.SEMICOLON, 2225, "Expecting ';' after statement");
    				block.add(readStatement());
    			}

				break;
			}
			catch (ParserException e)
			{
				if (lastToken().is(Token.KET) || lastToken().is(Token.EOF))
				{
					break;
				}

				Token[] after = { Token.SEMICOLON };
				Token[] upto = { Token.KET };
				report(e, after, upto);
				continue;
			}
		}

		checkFor(Token.KET, 2226, "Expecting ')' at end of statement block");
		return block;
	}

	private DefinitionList readDclStatements()
		throws ParserException, LexException
	{
		DefinitionList defs = new DefinitionList();

		while (lastToken().is(Token.DCL))
		{
			nextToken();
			defs.add(readAssignmentDefinition());

			while (ignore(Token.COMMA))
			{
				defs.add(readAssignmentDefinition());
			}

			checkFor(Token.SEMICOLON, 2227, "Expecting ';' after declarations");
		}

		return defs;
	}

	public AssignmentDefinition readAssignmentDefinition()
		throws ParserException, LexException
	{
		LexIdentifierToken name = readIdToken("Expecting variable identifier");
		checkFor(Token.COLON, 2228, "Expecting name:type in declaration");
		Type type = getTypeReader().readType();
		Expression exp = new UndefinedExpression(name.location);

		if (lastToken().is(Token.ASSIGN))
		{
			nextToken();
			exp = getExpressionReader().readExpression();
		}
		else if (lastToken().is(Token.EQUALSEQUALS) || lastToken().is(Token.EQUALS))
		{
			throwMessage(2069, "Expecting <identifier>:<type> := <expression>");
		}

		return new AssignmentDefinition(idToName(name), type, exp);
	}

	private Statement readReturnStatement(LexLocation token)
		throws ParserException, LexException
	{
		checkFor(Token.RETURN, 2229, "Expecting 'return'");

		try
		{
			reader.push();
			Expression exp = getExpressionReader().readExpression();
			reader.unpush();
			return new ReturnStatement(token, exp);
		}
		catch (ParserException e)
		{
			reader.pop();
			e.adjustDepth(reader.getTokensRead());
			return new ReturnStatement(token);
		}
	}

	private Statement readLetStatement(LexToken token)
		throws ParserException, LexException
	{
		checkFor(Token.LET, 2230, "Expecting 'let'");
		ParserException letDefError = null;

		try
		{
			reader.push();
			LetDefStatement stmt = readLetDefStatement(token.location);
			reader.unpush();
			return stmt;
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
			LetBeStStatement stmt = readLetBeStStatement(token.location);
			reader.unpush();
			return stmt;
		}
		catch (ParserException e)
		{
			reader.pop();
			e.adjustDepth(reader.getTokensRead());
			throw e.deeperThan(letDefError) ? e : letDefError;
		}
	}

	private LetDefStatement readLetDefStatement(LexLocation token)
		throws ParserException, LexException
	{
		DefinitionReader dr = getDefinitionReader();
		DefinitionList localDefs = new DefinitionList();

		while (lastToken().isNot(Token.IN))
		{
			localDefs.add(dr.readLocalDefinition(NameScope.LOCAL));
			ignore(Token.COMMA);
		}

		checkFor(Token.IN, 2231, "Expecting 'in' after local definitions");
		return new LetDefStatement(token, localDefs, readStatement());
	}

	private LetBeStStatement readLetBeStStatement(LexLocation token)
		throws ParserException, LexException
	{
		MultipleBind bind = getBindReader().readMultipleBind();
		Expression stexp = null;

		if (lastToken().is(Token.BE))
		{
			nextToken();
			checkFor(Token.ST, 2232, "Expecting 'st' after 'be' in let statement");
			stexp = getExpressionReader().readExpression();
		}

		checkFor(Token.IN, 2233, "Expecting 'in' after bind in let statement");
		return new LetBeStStatement(token, bind, stexp, readStatement());
	}

	private CasesStatement readCasesStatement(LexLocation token)
		throws ParserException, LexException
	{
		checkFor(Token.CASES, 2234, "Expecting 'cases'");
		Expression exp = getExpressionReader().readExpression();
		checkFor(Token.COLON, 2235, "Expecting ':' after cases expression");

		List<CaseStmtAlternative> cases = new Vector<CaseStmtAlternative>();
		PatternReader pr = getPatternReader();

		while (lastToken().isNot(Token.OTHERS) &&
			   lastToken().isNot(Token.END))
		{
			PatternList plist = pr.readPatternList();
			checkFor(Token.ARROW, 2236, "Expecting '->' after case pattern list");
			Statement result = readStatement();
			cases.add(new CaseStmtAlternative(plist, result));
			ignore(Token.COMMA);
		}

		Statement others = null;

		if (lastToken().is(Token.OTHERS))
		{
			nextToken();
			checkFor(Token.ARROW, 2237, "Expecting '->' after others");
			others = readStatement();
		}

		checkFor(Token.END, 2238, "Expecting 'end' after cases");
		return new CasesStatement(token, exp, cases, others);
	}

	private DefStatement readDefStatement(LexLocation token)
		throws ParserException, LexException
	{
		checkFor(Token.DEF, 2239, "Expecting 'def'");
		DefinitionReader dr = getDefinitionReader();
		DefinitionList equalsDefs = new DefinitionList();

		while (lastToken().isNot(Token.IN))
		{
			equalsDefs.add(dr.readEqualsDefinition());
			ignore(Token.SEMICOLON);
		}

		checkFor(Token.IN, 2240, "Expecting 'in' after equals definitions");
		return new DefStatement(token, equalsDefs, readStatement());
	}

	private SpecificationStatement readSpecStatement(LexLocation token)
		throws ParserException, LexException
	{
		checkFor(Token.SEQ_OPEN, 2241, "Expecting '['");
		DefinitionReader dr = getDefinitionReader();
		SpecificationStatement stmt = dr.readSpecification(token, false);
		checkFor(Token.SEQ_CLOSE, 2242, "Expecting ']' after specification statement");
		return stmt;
	}

	private Statement readStartStatement(LexLocation location)
		throws LexException, ParserException
	{
		checkFor(Token.START, 2243, "Expecting 'start'");
		checkFor(Token.BRA, 2244, "Expecting 'start('");
		Expression obj = getExpressionReader().readExpression();
		checkFor(Token.KET, 2245, "Expecting ')' after start object");
		return new StartStatement(location, obj);
	}

	private Statement readStartlistStatement(LexLocation location)
		throws LexException, ParserException
	{
		checkFor(Token.STARTLIST, 2246, "Expecting 'startlist'");
		checkFor(Token.BRA, 2247, "Expecting 'startlist('");
		Expression set = getExpressionReader().readExpression();
		checkFor(Token.KET, 2248, "Expecting ')' after startlist objects");
		return new StartStatement(location, set);
	}

	private Statement readDurationStatement(LexLocation location)
		throws LexException, ParserException
	{
		checkFor(Token.DURATION, 2271, "Expecting 'duration'");
		checkFor(Token.BRA, 2272, "Expecting 'duration('");
		Expression duration = getExpressionReader().readExpression();
		checkFor(Token.KET, 2273, "Expecting ')' after duration");
		Statement stmt = readStatement();
		return new DurationStatement(location, duration, stmt);
	}

	private Statement readCyclesStatement(LexLocation location)
		throws LexException, ParserException
	{
		checkFor(Token.CYCLES, 2274, "Expecting 'cycles'");
		checkFor(Token.BRA, 2275, "Expecting 'cycles('");
		Expression duration = getExpressionReader().readExpression();
		checkFor(Token.KET, 2276, "Expecting ')' after cycles");
		Statement stmt = readStatement();
		return new CyclesStatement(location, duration, stmt);
	}
}
