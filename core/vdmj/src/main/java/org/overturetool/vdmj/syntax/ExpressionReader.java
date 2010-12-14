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

import org.overturetool.vdmj.Release;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.expressions.*;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexBooleanToken;
import org.overturetool.vdmj.lex.LexCharacterToken;
import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.lex.LexIdentifierToken;
import org.overturetool.vdmj.lex.LexIntegerToken;
import org.overturetool.vdmj.lex.LexKeywordToken;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.lex.LexQuoteToken;
import org.overturetool.vdmj.lex.LexRealToken;
import org.overturetool.vdmj.lex.LexStringToken;
import org.overturetool.vdmj.lex.LexToken;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.lex.Token;
import org.overturetool.vdmj.patterns.Bind;
import org.overturetool.vdmj.patterns.MultipleBind;
import org.overturetool.vdmj.patterns.Pattern;
import org.overturetool.vdmj.patterns.PatternList;
import org.overturetool.vdmj.patterns.SetBind;
import org.overturetool.vdmj.patterns.TypeBind;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.BooleanType;
import org.overturetool.vdmj.types.CharacterType;
import org.overturetool.vdmj.types.IntegerType;
import org.overturetool.vdmj.types.NaturalOneType;
import org.overturetool.vdmj.types.NaturalType;
import org.overturetool.vdmj.types.RationalType;
import org.overturetool.vdmj.types.RealType;
import org.overturetool.vdmj.types.TokenType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.types.UnresolvedType;


/**
 * A syntax analyser to parse expressions.
 */

public class ExpressionReader extends SyntaxReader
{
	public ExpressionReader(LexTokenReader reader)
	{
		super(reader);
	}

	public ExpressionList readExpressionList() throws ParserException, LexException
	{
		ExpressionList list = new ExpressionList();
		list.add(readExpression());

		while (ignore(Token.COMMA))
		{
			list.add(readExpression());
		}

		return list;
	}

	// Constructor Family...

	public Expression readExpression() throws ParserException, LexException
	{
		Expression exp = readConnectiveExpression();
		LexToken token = lastToken();

		if (token.is(Token.MAPLET))
		{
			nextToken();
			Expression mapsTo = readExpression();
			exp = new MapletExpression(exp, token, mapsTo);
		}

		return exp;
	}

	// Connectives Family. All (recursive) right grouping...

	private Expression readConnectiveExpression()
		throws ParserException, LexException
	{
		Expression exp = readImpliesExpression();
		LexToken token = lastToken();

		if (token.is(Token.EQUIVALENT))
		{
			nextToken();
			exp = new EquivalentExpression(exp, token, readConnectiveExpression());
		}

		return exp;
	}

	private Expression readImpliesExpression() throws ParserException, LexException
	{
		Expression exp = readOrExpression();
		LexToken token = lastToken();

		if (token.is(Token.IMPLIES))
		{
			nextToken();
			exp = new ImpliesExpression(exp, token, readImpliesExpression());
		}

		return exp;
	}


	private Expression readOrExpression() throws ParserException, LexException
	{
		Expression exp = readAndExpression();
		LexToken token = lastToken();

		if (token.is(Token.OR))
		{
			nextToken();
			exp = new OrExpression(exp, token, readOrExpression());
		}

		return exp;
	}

	private Expression readAndExpression() throws ParserException, LexException
	{
		Expression exp = readNotExpression();
		LexToken token = lastToken();

		if (token.is(Token.AND))
		{
			nextToken();
			exp = new AndExpression(exp, token, readAndExpression());
		}

		return exp;
	}

	private Expression readNotExpression() throws ParserException, LexException
	{
		Expression exp = null;
		LexToken token = lastToken();

		if (token.is(Token.NOT))
		{
			nextToken();
			exp = new NotExpression(token.location, readNotExpression());
		}
		else
		{
			exp = readRelationalExpression();
		}

		return exp;
	}

	// Relations Family...

	public EqualsExpression readDefEqualsExpression()
		throws ParserException, LexException
	{
		// This is an oddball parse for the "def" expression :-)

		Expression exp = readEvaluatorP1Expression();
		LexToken token = lastToken();

		if (readToken().is(Token.EQUALS))
		{
			return new EqualsExpression(exp, token, readEvaluatorP1Expression());
		}

		throwMessage(2029, "Expecting <set bind> = <expression>");
		return null;
	}

	private Expression readRelationalExpression()
		throws ParserException, LexException
	{
		Expression exp = readEvaluatorP1Expression();
		LexToken token = lastToken();

		if (token.is(Token.NOT))
		{
			// Check for "not in set"
			reader.push();

			if (nextToken().is(Token.IN))
			{
				if (nextToken().is(Token.SET))
				{
					token = new LexKeywordToken(Token.NOTINSET, token.location);
					reader.unpush();
				}
				else
				{
					reader.pop();
				}
			}
			else
			{
				reader.pop();
			}
		}
		else if (token.is(Token.IN))
		{
			// Check for "in set"
			reader.push();

			if (nextToken().is(Token.SET))
			{
				token = new LexKeywordToken(Token.INSET, token.location);
				reader.unpush();
			}
			else
			{
				reader.pop();
			}
		}

		// No grouping for relationals...
		switch (token.type)
		{
			case LT:
				nextToken();
				exp = new LessExpression(exp, token, readNotExpression());
				break;

			case LE:
				nextToken();
				exp = new LessEqualExpression(exp, token, readNotExpression());
				break;

			case GT:
				nextToken();
				exp = new GreaterExpression(exp, token, readNotExpression());
				break;

			case GE:
				nextToken();
				exp = new GreaterEqualExpression(exp, token, readNotExpression());
				break;

			case NE:
				nextToken();
				exp = new NotEqualExpression(exp, token, readNotExpression());
				break;

			case EQUALS:
				nextToken();
				exp = new EqualsExpression(exp, token, readNotExpression());
				break;

			case SUBSET:
				nextToken();
				exp = new SubsetExpression(exp, token, readNotExpression());
				break;

			case PSUBSET:
				nextToken();
				exp = new ProperSubsetExpression(exp, token, readNotExpression());
				break;

			case INSET:
				nextToken();
				exp = new InSetExpression(exp, token, readNotExpression());
				break;

			case NOTINSET:
				nextToken();
				exp = new NotInSetExpression(exp, token, readNotExpression());
				break;
		}

		return exp;
	}

	// Evaluator Family...

	private Expression readEvaluatorP1Expression()
		throws ParserException, LexException
	{
		Expression exp = readEvaluatorP2Expression();
		boolean more = true;

		while (more)	// Left grouping
		{
			LexToken token = lastToken();

			switch (token.type)
			{
				case PLUS:
					nextToken();
					exp = new PlusExpression(exp, token, readEvaluatorP2Expression());
					break;

				case MINUS:
					nextToken();
					exp = new SubtractExpression(exp, token, readEvaluatorP2Expression());
					break;

				case UNION:
					nextToken();
					exp = new SetUnionExpression(exp, token, readEvaluatorP2Expression());
					break;

				case SETDIFF:
					nextToken();
					exp = new SetDifferenceExpression(exp, token, readEvaluatorP2Expression());
					break;

				case MUNION:
					nextToken();
					exp = new MapUnionExpression(exp, token, readEvaluatorP2Expression());
					break;

				case PLUSPLUS:
					nextToken();
					exp = new PlusPlusExpression(exp, token, readEvaluatorP2Expression());
					break;

				case CONCATENATE:
					nextToken();
					exp = new SeqConcatExpression(exp, token, readEvaluatorP2Expression());
					break;

				default:
					more = false;
					break;
			}
		}

		return exp;
	}

	private Expression readEvaluatorP2Expression()
		throws ParserException, LexException
	{
		Expression exp = readEvaluatorP3Expression();
		boolean more = true;

		while (more)	// Left grouping
		{
			LexToken token = lastToken();

			switch (token.type)
			{
				case TIMES:
					nextToken();
					exp = new TimesExpression(exp, token, readEvaluatorP3Expression());
					break;

				case DIVIDE:
					nextToken();
					exp = new DivideExpression(exp, token, readEvaluatorP3Expression());
					break;

				case REM:
					nextToken();
					exp = new RemExpression(exp, token, readEvaluatorP3Expression());
					break;

				case MOD:
					nextToken();
					exp = new ModExpression(exp, token, readEvaluatorP3Expression());
					break;

				case DIV:
					nextToken();
					exp = new DivExpression(exp, token, readEvaluatorP3Expression());
					break;

				case INTER:
					nextToken();
					exp = new SetIntersectExpression(exp, token, readEvaluatorP3Expression());
					break;

				default:
					more = false;
					break;
			}
		}

		return exp;
	}

	private Expression readEvaluatorP3Expression()
		throws ParserException, LexException
	{
		Expression exp = null;
		LexToken token = lastToken();

		if (token.is(Token.INVERSE))
		{
			nextToken();
			// Unary, so recursion OK for left grouping
			exp = new MapInverseExpression(token.location, readEvaluatorP3Expression());
		}
		else
		{
			exp = readEvaluatorP4Expression();
		}

		return exp;
	}

	private Expression readEvaluatorP4Expression()
		throws ParserException, LexException
	{
		Expression exp = readEvaluatorP5Expression();
		boolean more = true;

		while (more)
		{
			LexToken token = lastToken();

			switch (token.type)
			{
				case DOMRESTO:
					nextToken();
					exp = new DomainResToExpression(exp, token, readEvaluatorP5Expression());
					break;

				case DOMRESBY:
					nextToken();
					exp = new DomainResByExpression(exp, token, readEvaluatorP5Expression());
					break;

				default:
					more = false;
					break;
			}
		}

		return exp;
	}

	private Expression readEvaluatorP5Expression()
		throws ParserException, LexException
	{
		Expression exp = readEvaluatorP6Expression();
		boolean more = true;

		while (more)
		{
			LexToken token = lastToken();

			switch (token.type)
			{
				case RANGERESTO:
					nextToken();
					exp = new RangeResToExpression(exp, token, readEvaluatorP6Expression());
					break;

				case RANGERESBY:
					nextToken();
					exp = new RangeResByExpression(exp, token, readEvaluatorP6Expression());
					break;

				default:
					more = false;
					break;
			}
		}

		return exp;
	}

	private Expression readEvaluatorP6Expression()
		throws ParserException, LexException
	{
		Expression exp = null;
		LexToken token = lastToken();
		LexLocation location = token.location;

		// Unary operators, so recursion OK for left grouping
		switch (token.type)
		{
			case PLUS:
				nextToken();
				exp = new UnaryPlusExpression(location, readEvaluatorP6Expression());
				break;

			case MINUS:
				nextToken();
				exp = new UnaryMinusExpression(location, readEvaluatorP6Expression());
				break;

			case CARD:
				nextToken();
				exp = new CardinalityExpression(location, readEvaluatorP6Expression());
				break;

			case DOM:
				nextToken();
				exp = new MapDomainExpression(location, readEvaluatorP6Expression());
				break;

			case LEN:
				nextToken();
				exp = new LenExpression(location, readEvaluatorP6Expression());
				break;

			case POWER:
				nextToken();
				exp = new PowerSetExpression(location, readEvaluatorP6Expression());
				break;

			case RNG:
				nextToken();
				exp = new MapRangeExpression(location, readEvaluatorP6Expression());
				break;

			case ELEMS:
				nextToken();
				exp = new ElementsExpression(location, readEvaluatorP6Expression());
				break;

			case ABS:
				nextToken();
				exp = new AbsoluteExpression(location, readEvaluatorP6Expression());
				break;

			case DINTER:
				nextToken();
				exp = new DistIntersectExpression(location, readEvaluatorP6Expression());
				break;

			case MERGE:
				nextToken();
				exp = new DistMergeExpression(location, readEvaluatorP6Expression());
				break;

			case HEAD:
				nextToken();
				exp = new HeadExpression(location, readEvaluatorP6Expression());
				break;

			case TAIL:
				nextToken();
				exp = new TailExpression(location, readEvaluatorP6Expression());
				break;

			case REVERSE:
				if (Settings.release == Release.CLASSIC)
				{
					throwMessage(2291, "'reverse' not available in VDM classic");
				}

				nextToken();
				exp = new ReverseExpression(location, readEvaluatorP6Expression());
				break;

			case FLOOR:
				nextToken();
				exp = new FloorExpression(location, readEvaluatorP6Expression());
				break;

			case DUNION:
				nextToken();
				exp = new DistUnionExpression(location, readEvaluatorP6Expression());
				break;

			case DISTCONC:
				nextToken();
				exp = new DistConcatExpression(location, readEvaluatorP6Expression());
				break;

			case INDS:
				nextToken();
				exp = new IndicesExpression(location, readEvaluatorP6Expression());
				break;

			default:
				exp = readApplicatorExpression();
				break;
		}

		return exp;
	}

	// Applicator Family. Left grouping(?)

	private Expression readApplicatorExpression()
		throws ParserException, LexException
	{
		Expression exp = readBasicExpression();
		boolean more = true;

		while (more)
		{
			LexToken token = lastToken();

			switch (token.type)
    		{
    			case BRA:
    				// Either sequence(from, ..., to) or func(args) or map(key)
    				// or mk_*(), is_*(), mu(), pre_*(), post_*(),
    				// init_*() or inv_*()

    				if (nextToken().is(Token.KET))
    				{
    					if (exp instanceof VariableExpression)
    					{
    						VariableExpression ve = (VariableExpression)exp;
    						String name = ve.name.name;

        					if (name.startsWith("mk_"))
    						{
        						// a mk_TYPE() with no field values
    							exp = readMkExpression(ve);
    							break;
    						}
        				}

   						exp = new ApplyExpression(exp);
    					nextToken();
    				}
    				else
    				{
    					if (exp instanceof VariableExpression)
    					{
    						VariableExpression ve = (VariableExpression)exp;
    						String name = ve.name.name;

    						if (name.equals("mu"))
    						{
    							exp = readMuExpression(ve);
    							break;
    						}
    						else if (name.startsWith("mk_"))
    						{
    							exp = readMkExpression(ve);
    							break;
    						}
    						else if (name.startsWith("is_"))
    						{
    							exp = readIsExpression(ve);
    							break;
    						}
    						else if (name.equals("pre_"))
    						{
    							exp = readPreExpression(ve);
    							break;
    						}
     					}

    					// So we're a function/operation call, a list subsequence or
    					// a map index...

    					Expression first = readExpression();

    					if (lastToken().is(Token.COMMA))
    					{
    						reader.push();

    						if (nextToken().is(Token.RANGE))
    						{
    							nextToken();
    							checkFor(Token.COMMA, 2120, "Expecting 'e1,...,e2' in subsequence");
    							Expression last = readExpression();
    							checkFor(Token.KET, 2121, "Expecting ')' after subsequence");
    							reader.unpush();
    							exp = new SubseqExpression(exp, first, last);
    							break;
    						}

    						reader.pop();	// Not a subsequence then...
    					}

    					// OK, so read a (list, of, arguments)...

						ExpressionList args = new ExpressionList();
						args.add(first);

						while (ignore(Token.COMMA))
						{
							args.add(readExpression());
						}

						checkFor(Token.KET, 2122, "Expecting ')' after function args");
   						exp = new ApplyExpression(exp, args);
    				}
    				break;

    			case SEQ_OPEN:
    				// Polymorphic function instantiation
    				TypeList types = new TypeList();
    				TypeReader tr = getTypeReader();

    				nextToken();
    				types.add(tr.readType());

    				while (ignore(Token.COMMA))
    				{
    					types.add(tr.readType());
    				}

    				checkFor(Token.SEQ_CLOSE, 2123, "Expecting ']' after function instantiation");
   					exp = new FuncInstantiationExpression(exp, types);
    				break;

    			case POINT:
    				// Field selection by name or number
    				switch (nextToken().type)
    				{
    					case NAME:
    						if (dialect != Dialect.VDM_SL)
    						{
        						exp = new FieldExpression(exp, lastNameToken());
    						}
    						else
    						{
    							throwMessage(2030, "Expecting simple field identifier");
    						}
    						break;

    					case IDENTIFIER:
    						exp = new FieldExpression(exp, lastIdToken());
    						break;

    					case HASH:
    						if (nextToken().isNot(Token.NUMBER))
    						{
    							throwMessage(2031, "Expecting field number after .#");
    						}

    						LexIntegerToken num = (LexIntegerToken)lastToken();
    						exp = new FieldNumberExpression(exp, num);
    						break;

    					default:
    						throwMessage(2032, "Expecting field name");
    				}

    				nextToken();
    				break;

    			default:
    				more = false;
    				break;
    		}
		}

		// If we've collected as many applicators as we can, but we're still
		// just a variable, this is a bare variable expression. In VDM++, these
		// are always qualified (ie. x refers to C`x where it was declared, not
		// an overriding version lower down).

		if (exp instanceof VariableExpression)
		{
			VariableExpression ve = (VariableExpression)exp;
			ve.setExplicit(true);
		}

		// Combinator Family. Right grouping.
		LexToken token = lastToken();

		if (token.is(Token.COMP))
		{
			nextToken();
			return new CompExpression(exp, token, readApplicatorExpression());
		}

		if (token.is(Token.STARSTAR))
		{
			nextToken();
			return new StarStarExpression(exp, token, readEvaluatorP6Expression());
		}

		return exp;
	}

	private Expression readBasicExpression()
		throws ParserException, LexException
	{
		LexToken token = lastToken();

		switch (token.type)
		{
			case NUMBER:
				nextToken();
				return new IntegerLiteralExpression((LexIntegerToken)token);

			case REALNUMBER:
				nextToken();
				return new RealLiteralExpression((LexRealToken)token);

			case NAME:
				// Includes mk_ constructors
				LexNameToken name = lastNameToken();
				nextToken();
				return new VariableExpression(name);

			case IDENTIFIER:
				// Includes mk_ constructors
				// Note we can't use lastNameToken as this checks that we don't
				// use old~ names.
				LexNameToken id =
					new LexNameToken(reader.currentModule, (LexIdentifierToken)token);
				nextToken();
				return new VariableExpression(id);

			case STRING:
				nextToken();
				return new StringLiteralExpression((LexStringToken)token);

			case CHARACTER:
				nextToken();
				return new CharLiteralExpression((LexCharacterToken)token);

			case QUOTE:
				nextToken();
				return new QuoteLiteralExpression((LexQuoteToken)token);

			case TRUE:
			case FALSE:
				nextToken();
				return new BooleanLiteralExpression((LexBooleanToken)token);

			case UNDEFINED:
				nextToken();
				return new UndefinedExpression(token.location);

			case NIL:
				nextToken();
				return new NilExpression(token.location);

			case THREADID:
				nextToken();
				return new ThreadIdExpression(token.location);

			case BRA:
				nextToken();
				Expression exp = readExpression();
				checkFor(Token.KET, 2124, "Expecting ')'");
				return exp;

			case SET_OPEN:
				nextToken();
				return readSetOrMapExpression(token.location);

			case SEQ_OPEN:
				nextToken();
				return readSeqExpression(token.location);

			case FORALL:
				nextToken();
				return readForAllExpression(token.location);

			case EXISTS:
				nextToken();
				return readExistsExpression(token.location);

			case EXISTS1:
				nextToken();
				return readExists1Expression(token.location);

			case IOTA:
				nextToken();
				return readIotaExpression(token.location);

			case LAMBDA:
				nextToken();
				return readLambdaExpression(token.location);

			case IF:
				nextToken();
				return readIfExpression(token.location);

			case CASES:
				nextToken();
				return readCasesExpression(token.location);

			case LET:
				nextToken();
				return readLetExpression(token.location);

			case DEF:
				nextToken();
				return readDefExpression(token.location);

			case NEW:
				nextToken();
				return readNewExpression(token.location);

			case SELF:
				nextToken();
				return new SelfExpression(token.location);

			case IS:
				switch (nextToken().type)
				{
					case NOT:
						nextToken();
						checkFor(Token.YET, 2125, "Expecting 'is not yet specified'");
						checkFor(Token.SPECIFIED, 2126, "Expecting 'is not yet specified'");
						return new NotYetSpecifiedExpression(token.location);

					case SUBCLASS:
						nextToken();
						checkFor(Token.RESPONSIBILITY, 2127, "Expecting 'is subclass responsibility'");
						return new SubclassResponsibilityExpression(token.location);
				}

				throwMessage(2033, "Expected 'is not specified' or 'is subclass responsibility'");
				return null;

			case ISOFBASECLASS:
				nextToken();
				return readIsOfBaseExpression(token.location);

			case ISOFCLASS:
				nextToken();
				return readIsOfClassExpression(token.location);

			case SAMEBASECLASS:
				nextToken();
				return readSameBaseExpression(token.location);

			case SAMECLASS:
				nextToken();
				return readSameClassExpression(token.location);

			case REQ: case ACT: case FIN: case ACTIVE: case WAITING:
				return readHistoryExpression(token.location);

			case TIME:
				return readTimeExpression(token.location);

			default:
				throwMessage(2034, "Unexpected token in expression");
				return null;
		}
	}

	private Expression readTimeExpression(LexLocation location) throws LexException
	{
		nextToken();
		return new TimeExpression(location);
	}

	private MuExpression readMuExpression(VariableExpression ve)
		throws ParserException, LexException
	{
		List<RecordModifier> args = new Vector<RecordModifier>();
		Expression record = readExpression();

		do
		{
			checkFor(Token.COMMA, 2128, "Expecting comma separated record modifiers");
			LexIdentifierToken id = readIdToken("Expecting <identifier> |-> <expression>");
			checkFor(Token.MAPLET, 2129, "Expecting <identifier> |-> <expression>");
			args.add(new RecordModifier(id, readExpression()));
		}
		while (lastToken().is(Token.COMMA));

		checkFor(Token.KET, 2130, "Expecting ')' after mu maplets");
		return new MuExpression(ve.location, record, args);
	}

	private Expression readMkExpression(VariableExpression ve)
		throws ParserException, LexException
	{
		ExpressionList args = new ExpressionList();

		if (lastToken().isNot(Token.KET))	// NB. mk_T() is legal
		{
			args.add(readExpression());

			while (ignore(Token.COMMA))
			{
				args.add(readExpression());
			}
		}

		checkFor(Token.KET, 2131, "Expecting ')' after mk_ tuple");
		Expression exp = null;

		if (ve.name.name.equals("mk_"))
		{
			if (args.size() < 2)
			{
				throwMessage(2035, "Tuple must have >1 argument");
			}

			exp = new TupleExpression(ve.location, args);
		}
		else
		{
			LexNameToken typename = getMkTypeName(ve.name);
			Token type = Token.lookup(typename.name, Dialect.VDM_SL);

			if (type != null)
			{
				Expression value = args.get(0);

				switch (type)
				{
					case BOOL:
						exp = new MkBasicExpression(new BooleanType(ve.location), value);
						break;

					case NAT:
						exp = new MkBasicExpression(new NaturalType(ve.location), value);
						break;

					case NAT1:
						exp = new MkBasicExpression(new NaturalOneType(ve.location), value);
						break;

					case INT:
						exp = new MkBasicExpression(new IntegerType(ve.location), value);
						break;

					case RAT:
						exp = new MkBasicExpression(new RationalType(ve.location), value);
						break;

					case REAL:
						exp = new MkBasicExpression(new RealType(ve.location), value);
						break;

					case CHAR:
						exp = new MkBasicExpression(new CharacterType(ve.location), value);
						break;

					case TOKEN:
						exp = new MkBasicExpression(new TokenType(ve.location), value);
						break;

					default:
						throwMessage(2036, "Expecting mk_<type>");
				}
			}
			else
			{
				exp = new MkTypeExpression(typename, args);
			}
		}

		return exp;
	}

	private LexNameToken getMkTypeName(LexNameToken mktoken)
		throws ParserException, LexException
	{
		String typename = mktoken.name.substring(3);	// mk_... or is_...
		String[] parts = typename.split("`");

		switch (parts.length)
		{
			case 1:
				return new LexNameToken(getCurrentModule(), parts[0], mktoken.location);

			case 2:
				return new LexNameToken(parts[0], parts[1], mktoken.location, false, true);

			default:
				throwMessage(2037, "Malformed mk_<type> name " + typename);
		}

		return null;
	}

	private IsExpression readIsExpression(VariableExpression ve)
		throws ParserException, LexException
	{
		String name = ve.name.name;
		IsExpression exp = null;

		if (name.equals("is_"))
		{
			Expression test = readExpression();
			checkFor(Token.COMMA, 2132, "Expecting is_(expression, type)");
			TypeReader tr = getTypeReader();
			Type type = tr.readType();

			if (type instanceof UnresolvedType)
			{
				UnresolvedType nt = (UnresolvedType)type;
				exp = new IsExpression(nt.typename, test);
			}
			else
			{
				exp = new IsExpression(type, test);
			}
		}
		else
		{
			LexNameToken typename = getMkTypeName(ve.name);
			Token type = Token.lookup(typename.name, Dialect.VDM_SL);

			if (type != null)
			{
				switch (type)
				{
					case BOOL:
						exp = new IsExpression(new BooleanType(ve.location), readExpression());
						break;

					case NAT:
						exp = new IsExpression(new NaturalType(ve.location), readExpression());
						break;

					case NAT1:
						exp = new IsExpression(new NaturalOneType(ve.location), readExpression());
						break;

					case INT:
						exp = new IsExpression(new IntegerType(ve.location), readExpression());
						break;

					case RAT:
						exp = new IsExpression(new RationalType(ve.location), readExpression());
						break;

					case REAL:
						exp = new IsExpression(new RealType(ve.location), readExpression());
						break;

					case CHAR:
						exp = new IsExpression(new CharacterType(ve.location), readExpression());
						break;

					case TOKEN:
						exp = new IsExpression(new TokenType(ve.location), readExpression());
						break;

					default:
						throwMessage(2038, "Expecting is_<type>");
				}
			}
			else
			{
				exp = new IsExpression(typename, readExpression());
			}
		}

		checkFor(Token.KET, 2133, "Expecting ')' after is_ expression");
		return exp;
	}

	private PreExpression readPreExpression(VariableExpression ve)
		throws ParserException, LexException
	{
		ExpressionList args = new ExpressionList();
		Expression function = readExpression();

		while (ignore(Token.COMMA))
		{
			args.add(readExpression());
		}

		checkFor(Token.KET, 2134, "Expecting pre_(function [,args])");

		return new PreExpression(ve.location, function, args);
	}

	private Expression readSetOrMapExpression(LexLocation start)
		throws ParserException, LexException
	{
		LexToken token = lastToken();

		if (token.is(Token.SET_CLOSE))
		{
			nextToken();
			return new SetEnumExpression(start);		// empty set
		}
		else if (token.is(Token.MAPLET))
		{
			nextToken();
			checkFor(Token.SET_CLOSE, 2135, "Expecting '}' in empty map");
			return new MapEnumExpression(start);		// empty map
		}

		Expression first = readExpression();

		if (first instanceof MapletExpression)
		{
			return readMapExpression(start, (MapletExpression)first);
		}
		else
		{
			return readSetExpression(start, first);
		}
	}

	private SetExpression readSetExpression(LexLocation start, Expression first)
		throws ParserException, LexException
	{
		SetExpression result = null;

		if (lastToken().is(Token.PIPE))
		{
			nextToken();
			BindReader br = getBindReader();
			List<MultipleBind> bindings = br.readBindList();
			Expression exp = null;

			if (lastToken().is(Token.AMPERSAND))
			{
				nextToken();
				exp = readExpression();
			}

			checkFor(Token.SET_CLOSE, 2136, "Expecting '}' after set comprehension");
			result = new SetCompExpression(start, first, bindings, exp);
		}
		else
		{
			if (lastToken().is(Token.COMMA))
			{
				reader.push();

				if (nextToken().is(Token.RANGE))
				{
					nextToken();
					checkFor(Token.COMMA, 2137, "Expecting 'e1,...,e2' in set range");
					Expression end = readExpression();
					checkFor(Token.SET_CLOSE, 2138, "Expecting '}' after set range");
					reader.unpush();
					return new SetRangeExpression(start, first, end);
				}

				reader.pop();	// Not a set range then...
			}

			ExpressionList members = new ExpressionList();
			members.add(first);

			while (ignore(Token.COMMA))
			{
				members.add(readExpression());
			}

			checkFor(Token.SET_CLOSE, 2139, "Expecting '}' after set enumeration");
			result = new SetEnumExpression(start, members);
		}

		return result;
	}

	private MapExpression readMapExpression(LexLocation start, MapletExpression first)
		throws ParserException, LexException
	{
		MapExpression result = null;

		if (lastToken().is(Token.PIPE))
		{
			nextToken();
			BindReader br = getBindReader();
			List<MultipleBind> bindings = br.readBindList();
			Expression exp = null;

			if (lastToken().is(Token.AMPERSAND))
			{
				nextToken();
				exp = readExpression();
			}

			checkFor(Token.SET_CLOSE, 2140, "Expecting '}' after map comprehension");
			result = new MapCompExpression(start, first, bindings, exp);
		}
		else
		{
			List<MapletExpression> members = new Vector<MapletExpression>();
			members.add(first);

			while (ignore(Token.COMMA))
			{
				Expression member = readExpression();

				if (member instanceof MapletExpression)
				{
					members.add((MapletExpression)member);
				}
				else
				{
					throwMessage(2039, "Expecting maplet in map enumeration");
				}
			}

			checkFor(Token.SET_CLOSE, 2141, "Expecting '}' after map enumeration");
			result = new MapEnumExpression(start, members);
		}

		return result;
	}

	private SeqExpression readSeqExpression(LexLocation start)
		throws ParserException, LexException
	{
		if (lastToken().is(Token.SEQ_CLOSE))
		{
			nextToken();
			return new SeqEnumExpression(start);		// empty list
		}

		SeqExpression result = null;
		Expression first = readExpression();

		if (lastToken().is(Token.PIPE))
		{
			nextToken();
			BindReader br = getBindReader();
			SetBind setbind = br.readSetBind();
			Expression exp = null;

			if (lastToken().is(Token.AMPERSAND))
			{
				nextToken();
				exp = readExpression();
			}

			checkFor(Token.SEQ_CLOSE, 2142, "Expecting ']' after list comprehension");
			result = new SeqCompExpression(start, first, setbind, exp);
		}
		else
		{
			ExpressionList members = new ExpressionList();
			members.add(first);

			while (ignore(Token.COMMA))
			{
				members.add(readExpression());
			}

			checkFor(Token.SEQ_CLOSE, 2143, "Expecting ']' after list enumeration");
			result = new SeqEnumExpression(start, members);
		}

		return result;
	}

	private IfExpression readIfExpression(LexLocation start)
		throws ParserException, LexException
	{
		Expression exp = readExpression();
		checkFor(Token.THEN, 2144, "Missing 'then'");
		Expression thenExp = readExpression();
		List<ElseIfExpression> elseList = new Vector<ElseIfExpression>();

		while (lastToken().is(Token.ELSEIF))
		{
			nextToken();
			elseList.add(readElseIfExpression(lastToken().location));
		}

		Expression elseExp = null;

		if (lastToken().is(Token.ELSE))
		{
			nextToken();
			elseExp = readConnectiveExpression();	// Precedence < maplet?
		}
		else
		{
			throwMessage(2040, "Expecting 'else' in 'if' expression");
		}

		return new IfExpression(start, exp, thenExp, elseList, elseExp);
	}


	private ElseIfExpression readElseIfExpression(LexLocation start)
		throws ParserException, LexException
	{
		Expression exp = readExpression();
		checkFor(Token.THEN, 2145, "Missing 'then' after 'elseif'");
		Expression thenExp = readExpression();
		return new ElseIfExpression(start, exp, thenExp);
	}

	private CasesExpression readCasesExpression(LexLocation start)
		throws ParserException, LexException
	{
		Expression exp = readExpression();
		checkFor(Token.COLON, 2146, "Expecting ':' after cases expression");

		List<CaseAlternative> cases = new Vector<CaseAlternative>();
		Expression others = null;
		cases.addAll(readCaseAlternatives(exp));

		while (ignore(Token.COMMA))
		{
			if (lastToken().is(Token.OTHERS))
			{
				break;
			}

			cases.addAll(readCaseAlternatives(exp));
		}

		if (lastToken().is(Token.OTHERS))
		{
			nextToken();
			checkFor(Token.ARROW, 2147, "Expecting '->' after others");
			others = readExpression();
		}

		checkFor(Token.END, 2148, "Expecting 'end' after cases");
		return new CasesExpression(start, exp, cases, others);
	}

	private List<CaseAlternative> readCaseAlternatives(Expression exp)
		throws ParserException, LexException
	{
		List<CaseAlternative> alts = new Vector<CaseAlternative>();
		PatternList plist = getPatternReader().readPatternList();
		checkFor(Token.ARROW, 2149, "Expecting '->' after case pattern list");
		Expression then = readExpression();

		for (Pattern p: plist)
		{
			alts.add(new CaseAlternative(exp, p, then));
		}

		return alts;
	}

	private Expression readLetExpression(LexLocation start)
		throws ParserException, LexException
	{
		ParserException letDefError = null;

		try
		{
			reader.push();
			LetDefExpression exp = readLetDefExpression(start);
			reader.unpush();
			return exp;
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
			LetBeStExpression exp = readLetBeStExpression(start);
			reader.unpush();
			return exp;
		}
		catch (ParserException e)
		{
			e.adjustDepth(reader.getTokensRead());
			reader.pop();
			throw e.deeperThan(letDefError) ? e : letDefError;
		}
	}

	private LetDefExpression readLetDefExpression(LexLocation start)
		throws ParserException, LexException
	{
		DefinitionReader dr = getDefinitionReader();
		DefinitionList localDefs = new DefinitionList();

		localDefs.add(dr.readLocalDefinition(NameScope.LOCAL));

		while (ignore(Token.COMMA))
		{
			localDefs.add(dr.readLocalDefinition(NameScope.LOCAL));
		}

		checkFor(Token.IN, 2150, "Expecting 'in' after local definitions");
		// Note we read a Connective expression for the body, so that |->
		// terminates the parse.
		return new LetDefExpression(start, localDefs, readConnectiveExpression());
	}

	private LetBeStExpression readLetBeStExpression(LexLocation start)
		throws ParserException, LexException
	{
		MultipleBind bind = getBindReader().readMultipleBind();
		Expression stexp = null;

		if (lastToken().is(Token.BE))
		{
			nextToken();
			checkFor(Token.ST, 2151, "Expecting 'st' after 'be' in let expression");
			stexp = readExpression();
		}

		checkFor(Token.IN, 2152, "Expecting 'in' after bind in let expression");
		// Note we read a Connective expression for the body, so that |->
		// terminates the parse.
		return new LetBeStExpression(start, bind, stexp, readConnectiveExpression());
	}

	private ForAllExpression readForAllExpression(LexLocation start)
		throws ParserException, LexException
	{
		List<MultipleBind> bindList = getBindReader().readBindList();
		checkFor(Token.AMPERSAND, 2153, "Expecting '&' after bind list in forall");
		return new ForAllExpression(start, bindList, readExpression());
	}

	private ExistsExpression readExistsExpression(LexLocation start)
		throws ParserException, LexException
	{
		List<MultipleBind> bindList = getBindReader().readBindList();
		checkFor(Token.AMPERSAND, 2154, "Expecting '&' after bind list in exists");
		return new ExistsExpression(start, bindList, readExpression());
	}

	private Exists1Expression readExists1Expression(LexLocation start)
		throws ParserException, LexException
	{
		Bind bind = getBindReader().readBind();
		checkFor(Token.AMPERSAND, 2155, "Expecting '&' after single bind in exists1");
		return new Exists1Expression(start, bind, readExpression());
	}

	private IotaExpression readIotaExpression(LexLocation start)
		throws ParserException, LexException
	{
		Bind bind = getBindReader().readBind();
		checkFor(Token.AMPERSAND, 2156, "Expecting '&' after single bind in iota");
		return new IotaExpression(start, bind, readExpression());
	}

	private LambdaExpression readLambdaExpression(LexLocation start)
		throws ParserException, LexException
	{
		List<TypeBind> bindList = getBindReader().readTypeBindList();
		checkFor(Token.AMPERSAND, 2157, "Expecting '&' after bind list in lambda");
		return new LambdaExpression(start, bindList, readExpression());
	}

	private DefExpression readDefExpression(LexLocation start)
		throws ParserException, LexException
	{
		DefinitionReader dr = getDefinitionReader();
		DefinitionList equalsDefs = new DefinitionList();

		while (lastToken().isNot(Token.IN))
		{
			equalsDefs.add(dr.readEqualsDefinition());
			ignore(Token.SEMICOLON);
		}

		checkFor(Token.IN, 2158, "Expecting 'in' after equals definitions");
		return new DefExpression(start, equalsDefs, readExpression());
	}

	private NewExpression readNewExpression(LexLocation start)
		throws ParserException, LexException
	{
		LexIdentifierToken name = readIdToken("Expecting class name after 'new'");
		checkFor(Token.BRA, 2159, "Expecting '(' after new class name");

    	ExpressionList args = new ExpressionList();
    	ExpressionReader er = getExpressionReader();

    	if (lastToken().isNot(Token.KET))
    	{
    		args.add(er.readExpression());

    		while (ignore(Token.COMMA))
    		{
        		args.add(er.readExpression());
    		}
    	}

    	checkFor(Token.KET, 2124, "Expecting ')' after constructor args");
    	return new NewExpression(start, name, args);
    }

	private IsOfBaseClassExpression readIsOfBaseExpression(LexLocation start)
		throws ParserException, LexException
	{
		checkFor(Token.BRA, 2160, "Expecting '(' after 'isofbase'");
    	ExpressionList args = readExpressionList();
    	checkFor(Token.KET, 2161, "Expecting ')' after 'isofbase' args");

    	if (args.size() != 2)
    	{
    		throwMessage(2041, "Expecting two arguments for 'isofbase'");
    	}

    	if (!(args.get(0) instanceof VariableExpression))
    	{
    		throwMessage(2042, "Expecting (<class>,<exp>) arguments for 'isofbase'");
    	}

    	LexNameToken classname = ((VariableExpression)args.get(0)).name;

		if (classname.old)
		{
			throwMessage(2295, "Can't use old name here", classname);
		}

		return new IsOfBaseClassExpression(start, classname, args.get(1));
    }

	private IsOfClassExpression readIsOfClassExpression(LexLocation start)
		throws ParserException, LexException
	{
		checkFor(Token.BRA, 2162, "Expecting '(' after 'isofclass'");
    	ExpressionList args = readExpressionList();
    	checkFor(Token.KET, 2163, "Expecting ')' after 'isofclass' args");

    	if (args.size() != 2)
    	{
    		throwMessage(2043, "Expecting two arguments for 'isofclass'");
    	}

    	if (!(args.get(0) instanceof VariableExpression))
    	{
    		throwMessage(2044, "Expecting (<class>,<exp>) arguments for 'isofclass'");
    	}

    	LexNameToken classname = ((VariableExpression)args.get(0)).name;

		if (classname.old)
		{
			throwMessage(2295, "Can't use old name here", classname);
		}

		return new IsOfClassExpression(start, classname, args.get(1));
    }

	private SameBaseClassExpression readSameBaseExpression(LexLocation start)
		throws ParserException, LexException
	{
		checkFor(Token.BRA, 2164, "Expecting '(' after 'samebaseclass'");
    	ExpressionList args = readExpressionList();
    	checkFor(Token.KET, 2165, "Expecting ')' after 'samebaseclass' args");

    	if (args.size() != 2)
    	{
    		throwMessage(2045, "Expecting two expressions in 'samebaseclass'");
    	}

    	return new SameBaseClassExpression(start, args);
    }

	private SameClassExpression readSameClassExpression(LexLocation start)
		throws ParserException, LexException
	{
		checkFor(Token.BRA, 2166, "Expecting '(' after 'sameclass'");
    	ExpressionList args = readExpressionList();
    	checkFor(Token.KET, 2167, "Expecting ')' after 'sameclass' args");

    	if (args.size() != 2)
    	{
    		throwMessage(2046, "Expecting two expressions in 'sameclass'");
    	}

    	return new SameClassExpression(start, args);
    }

	private boolean inPerExpression = false;

	public Expression readPerExpression() throws ParserException, LexException
	{
		inPerExpression = true;
		Expression e = readExpression();
		inPerExpression = false;
		return e;
	}

	private Expression readHistoryExpression(LexLocation location)
		throws ParserException, LexException
	{
		if (!inPerExpression)
		{
			throwMessage(2047, "Can't use history expression here");
		}

		LexToken op = lastToken();
		String s = op.type.toString().toLowerCase();

		switch (op.type)
		{
			case ACT:
			case FIN:
			case ACTIVE:
			case REQ:
			case WAITING:
				nextToken();
				checkFor(Token.BRA, 2168, "Expecting " + s + "(name(s))");

				LexNameList opnames = new LexNameList();
				LexNameToken opname = readNameToken("Expecting a name");
				opnames.add(opname);

				while (ignore(Token.COMMA))
				{
					opname = readNameToken("Expecting " + s + "(name(s))");
					opnames.add(opname);
				}

				checkFor(Token.KET, 2169, "Expecting " + s + "(name(s))");
				return new HistoryExpression(location, op.type, opnames);

			default:
				throwMessage(2048, "Expecting #act, #active, #fin, #req or #waiting");
				return null;
		}
	}
}
