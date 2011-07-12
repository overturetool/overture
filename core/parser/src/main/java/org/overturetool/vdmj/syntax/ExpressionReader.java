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

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.*;
import org.overture.ast.node.NodeList;
import org.overture.ast.patterns.ASetBind;
import org.overture.ast.patterns.ATypeBind;
import org.overture.ast.patterns.PBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.ACharBasicType;
import org.overture.ast.types.AIntNumericBasicType;
import org.overture.ast.types.ANatNumericBasicType;
import org.overture.ast.types.ANatOneNumericBasicType;
import org.overture.ast.types.ARationalNumericBasicType;
import org.overture.ast.types.ARealNumericBasicType;
import org.overture.ast.types.ATokenBasicType;
import org.overture.ast.types.AUnresolvedType;
import org.overture.ast.types.PType;
import org.overturetool.vdmj.Release;
import org.overturetool.vdmj.Settings;
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
import org.overturetool.vdmj.lex.VDMToken;
import org.overturetool.vdmj.typechecker.NameScope;



/**
 * A syntax analyser to parse expressions.
 */

public class ExpressionReader extends SyntaxReader
{
	public ExpressionReader(LexTokenReader reader)
	{
		super(reader);
	}

	public List<PExp> readExpressionList() throws ParserException, LexException
	{
		List<PExp> list = new NodeList<PExp>(null);
		list.add(readExpression());

		while (ignore(VDMToken.COMMA))
		{
			list.add(readExpression());
		}

		return list;
	}

	// Constructor Family...

	public PExp readExpression() throws ParserException, LexException
	{
		return readConnectiveExpression();
	}

	// Connectives Family. All (recursive) right grouping...

	private PExp readConnectiveExpression()
		throws ParserException, LexException
	{
		PExp exp = readImpliesExpression();
		LexToken token = lastToken();

		if (token.is(VDMToken.EQUIVALENT))
		{
			nextToken();
//			exp = new ABinopExp(null, null, exp, new AEquivalentBinop(token.location), readConnectiveExpression());
			exp = new AEquivalentBooleanBinaryExp(null, null, exp, token, readConnectiveExpression());
//			exp = new AEquivalentBinop(exp, token, readConnectiveExpression());
		}

		return exp;
	}

	private PExp readImpliesExpression() throws ParserException, LexException
	{
		PExp exp = readOrExpression();
		LexToken token = lastToken();

		if (token.is(VDMToken.IMPLIES))
		{
			nextToken();
			exp = new AImpliesBooleanBinaryExp(null, null, exp, token, readImpliesExpression());
//			exp = new ImpliesExpression(exp, token, readImpliesExpression());
		}

		return exp;
	}


	private PExp readOrExpression() throws ParserException, LexException
	{
		PExp exp = readAndExpression();
		LexToken token = lastToken();

		if (token.is(VDMToken.OR))
		{
			nextToken();
			exp = new AOrBooleanBinaryExp(null, null, exp, token, readOrExpression());
//			exp = new OrExpression(exp, token, readOrExpression());
		}

		return exp;
	}

	private PExp readAndExpression() throws ParserException, LexException
	{
		PExp exp = readNotExpression();
		LexToken token = lastToken();

		if (token.is(VDMToken.AND))
		{
			nextToken();
			exp = new AAndBooleanBinaryExp(null, null, exp, token, readAndExpression());
//			exp = new AndExpression(exp, token, readAndExpression());
		}

		return exp;
	}

	private PExp readNotExpression() throws ParserException, LexException
	{
		PExp exp = null;
		LexToken token = lastToken();

		if (token.is(VDMToken.NOT))
		{
			nextToken();
			exp = new ANotUnaryExp(null, token.location, readNotExpression()); 
//			exp = new NotExpression(token.location, readNotExpression());
		}
		else
		{
			exp = readRelationalExpression();
		}

		return exp;
	}

	// Relations Family...

	public AEqualsBinaryExp readDefEqualsExpression()
		throws ParserException, LexException
	{
		// This is an oddball parse for the "def" expression :-)

		PExp exp = readEvaluatorP1Expression();
		LexToken token = lastToken();

		if (readToken().is(VDMToken.EQUALS))
		{
			return new AEqualsBinaryExp(null, null, exp, token, readEvaluatorP1Expression());
//			return new EqualsExpression(exp, token, readEvaluatorP1Expression());
		}

		throwMessage(2029, "Expecting <set bind> = <expression>");
		return null;
	}

	private PExp readRelationalExpression()
		throws ParserException, LexException
	{
		PExp exp = readEvaluatorP1Expression();
		LexToken token = lastToken();

		if (token.is(VDMToken.NOT))
		{
			// Check for "not in set"
			reader.push();

			if (nextToken().is(VDMToken.IN))
			{
				if (nextToken().is(VDMToken.SET))
				{
					token = new LexKeywordToken(VDMToken.NOTINSET, token.location);
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
		else if (token.is(VDMToken.IN))
		{
			// Check for "in set"
			reader.push();

			if (nextToken().is(VDMToken.SET))
			{
				token = new LexKeywordToken(VDMToken.INSET, token.location);
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
				
				exp = new ALessNumericBinaryExp(null, null, exp, token, readNotExpression());
				//exp = new LessExpression(exp, token, readNotExpression());
				break;

			case LE:
				nextToken();
				exp = exp = new ALessEqualNumericBinaryExp(null, null, exp, token, readNotExpression());
//				exp = new LessEqualExpression(exp, token, readNotExpression());
				break;

			case GT:
				nextToken();
				exp = new AGreaterNumericBinaryExp(null, null, exp, token, readNotExpression());
//				exp = new GreaterExpression(exp, token, readNotExpression());
				break;

			case GE:
				nextToken();
				exp = new AGreaterEqualNumericBinaryExp(null, null, exp, token, readNotExpression());
//				exp = new GreaterEqualExpression(exp, token, readNotExpression());
				break;

			case NE:
				nextToken();
				exp = new ANotEqualBinaryExp(null, null, exp, token, readNotExpression());
//				exp = new NotEqualExpression(exp, token, readNotExpression());
				break;

			case EQUALS:
				nextToken();
				exp = new AEqualsBinaryExp(null, null, exp, token, readNotExpression());
//				exp = new EqualsExpression(exp, token, readNotExpression());
				break;

			case SUBSET:
				nextToken();
				exp = new ASubsetBinaryExp(null, null, exp, token, readNotExpression());
//				exp = new SubsetExpression(exp, token, readNotExpression());
				break;

			case PSUBSET:
				nextToken();
				exp = new AProperSubsetBinaryExp(null, null, exp, token, readNotExpression());
//				exp = new ProperSubsetExpression(exp, token, readNotExpression());
				break;

			case INSET:
				nextToken();
				exp = new AInSetBinaryExp(null, null, exp, token, readNotExpression());
//				exp = new InSetExpression(exp, token, readNotExpression());
				break;

			case NOTINSET:
				nextToken();
				exp = new ANotInSetBinaryExp(null, null, exp, token, readNotExpression());
//				exp = new NotInSetExpression(exp, token, readNotExpression());
				break;
		}

		return exp;
	}

	// Evaluator Family...

	private PExp readEvaluatorP1Expression()
		throws ParserException, LexException
	{
		PExp exp = readEvaluatorP2Expression();
		boolean more = true;

		while (more)	// Left grouping
		{
			LexToken token = lastToken();

			switch (token.type)
			{
				case PLUS:
					nextToken();
					exp = new APlusNumericBinaryExp(null, null, exp, token, readEvaluatorP2Expression());
//					exp = new PlusExpression(exp, token, readEvaluatorP2Expression());
					break;

				case MINUS:
					nextToken();
					exp = new ASubstractNumericBinaryExp(null, null, exp, token, readEvaluatorP2Expression());
//					exp = new SubtractExpression(exp, token, readEvaluatorP2Expression());
					break;

				case UNION:
					nextToken();
					exp = new ASetUnionBinaryExp(null, null, exp, token, readEvaluatorP2Expression());
//					exp = new SetUnionExpression(exp, token, readEvaluatorP2Expression());
					break;

				case SETDIFF:
					nextToken();
					exp = new ASetDifferenceBinaryExp(null, null, exp, token, readEvaluatorP2Expression());
//					exp = new SetDifferenceExpression(exp, token, readEvaluatorP2Expression());
					break;

				case MUNION:
					nextToken();
					exp = new AMapUnionBinaryExp(null, null, exp, token, readEvaluatorP2Expression());
//					exp = new MapUnionExpression(exp, token, readEvaluatorP2Expression());
					break;

				case PLUSPLUS:
					nextToken();
					exp = new APlusPlusBinaryExp(null, null, exp, token, readEvaluatorP2Expression());
//					exp = new PlusPlusExpression(exp, token, readEvaluatorP2Expression());
					break;

				case CONCATENATE:
					nextToken();
					exp = new ASeqConcatBinaryExp(null, null, exp, token, readEvaluatorP2Expression());
//					exp = new SeqConcatExpression(exp, token, readEvaluatorP2Expression());
					break;

				default:
					more = false;
					break;
			}
		}

		return exp;
	}

	private PExp readEvaluatorP2Expression()
		throws ParserException, LexException
	{
		PExp exp = readEvaluatorP3Expression();
		boolean more = true;

		while (more)	// Left grouping
		{
			LexToken token = lastToken();

			switch (token.type)
			{
				case TIMES:
					nextToken();
					exp = new ATimesNumericBinaryExp(null, null, exp, token, readEvaluatorP3Expression());
//					exp = new TimesExpression(exp, token, readEvaluatorP3Expression());
					break;

				case DIVIDE:
					nextToken();
					exp = new ADivideNumericBinaryExp(null, null, exp, token, readEvaluatorP3Expression());
//					exp = new DivideExpression(exp, token, readEvaluatorP3Expression());
					break;

				case REM:
					nextToken();
					exp = new ARemNumericBinaryExp(null, null, exp, token, readEvaluatorP3Expression());
//					exp = new RemExpression(exp, token, readEvaluatorP3Expression());
					break;

				case MOD:
					nextToken();
					exp = new AModNumericBinaryExp(null, null, exp, token, readEvaluatorP3Expression());
//					exp = new ModExpression(exp, token, readEvaluatorP3Expression());
					break;

				case DIV:
					nextToken();
					exp = new ADivNumericBinaryExp(null, null, exp, token, readEvaluatorP3Expression());
//					exp = new DivExpression(exp, token, readEvaluatorP3Expression());
					break;

				case INTER:
					nextToken();
					exp = new ASetIntersectBinaryExp(null, null, exp, token, readEvaluatorP3Expression());
//					exp = new SetIntersectExpression(exp, token, readEvaluatorP3Expression());
					break;

				default:
					more = false;
					break;
			}
		}

		return exp;
	}

	private PExp readEvaluatorP3Expression()
		throws ParserException, LexException
	{
		PExp exp = null;
		LexToken token = lastToken();

		if (token.is(VDMToken.INVERSE))
		{
			nextToken();
			// Unary, so recursion OK for left grouping
			exp = new AMapInverseUnaryExp(null, token.location, readEvaluatorP3Expression());
//			exp = new MapInverseExpression(token.location, readEvaluatorP3Expression());
		}
		else
		{
			exp = readEvaluatorP4Expression();
		}

		return exp;
	}

	private PExp readEvaluatorP4Expression()
		throws ParserException, LexException
	{
		PExp exp = readEvaluatorP5Expression();
		boolean more = true;

		while (more)
		{
			LexToken token = lastToken();

			switch (token.type)
			{
				case DOMRESTO:
					nextToken();
					exp = new ADomainResToBinaryExp(null, null, exp, token, readEvaluatorP5Expression());
//					exp = new DomainResToExpression(exp, token, readEvaluatorP5Expression());
					break;

				case DOMRESBY:
					nextToken();
					exp = new ADomainResByBinaryExp(null, null, exp, token, readEvaluatorP5Expression());
//					exp = new DomainResByExpression(exp, token, readEvaluatorP5Expression());
					break;

				default:
					more = false;
					break;
			}
		}

		return exp;
	}

	private PExp readEvaluatorP5Expression()
		throws ParserException, LexException
	{
		PExp exp = readEvaluatorP6Expression();
		boolean more = true;

		while (more)
		{
			LexToken token = lastToken();

			switch (token.type)
			{
				case RANGERESTO:
					nextToken();
					exp = new ARangeResToBinaryExp(null, null, exp, token, readEvaluatorP6Expression());
//					exp = new RangeResToExpression(exp, token, readEvaluatorP6Expression());
					break;

				case RANGERESBY:
					nextToken();
					exp = new ARangeResByBinaryExp(null, null, exp, token, readEvaluatorP6Expression());
//					exp = new RangeResByExpression(exp, token, readEvaluatorP6Expression());
					break;

				default:
					more = false;
					break;
			}
		}

		return exp;
	}

	private PExp readEvaluatorP6Expression()
		throws ParserException, LexException
	{
		PExp exp = null;
		LexToken token = lastToken();
		LexLocation location = token.location;

		// Unary operators, so recursion OK for left grouping
		switch (token.type)
		{
			case PLUS:
				nextToken();
				exp = new AUnaryPlusUnaryExp(null, location, readEvaluatorP6Expression());
//				exp = new UnaryPlusExpression(location, readEvaluatorP6Expression());
				break;

			case MINUS:
				nextToken();
				exp = new AUnaryMinusUnaryExp(null, location, readEvaluatorP6Expression());
//				exp = new UnaryMinusExpression(location, readEvaluatorP6Expression());
				break;

			case CARD:
				nextToken();
				exp = new ACardinalityUnaryExp(null, location, readEvaluatorP6Expression());
//				exp = new CardinalityExpression(location, readEvaluatorP6Expression());
				break;

			case DOM:
				nextToken();
				exp = new AMapDomainUnaryExp(null, location, readEvaluatorP6Expression());
//				exp = new MapDomainExpression(location, readEvaluatorP6Expression());
				break;

			case LEN:
				nextToken();
				exp = new ALenUnaryExp(null, location, readEvaluatorP6Expression());
//				exp = new LenExpression(location, readEvaluatorP6Expression());
				break;

			case POWER:
				nextToken();
				exp = new APowerSetUnaryExp(null, location, readEvaluatorP6Expression());
//				exp = new PowerSetExpression(location, readEvaluatorP6Expression());
				break;

			case RNG:
				nextToken();
				exp = new AMapRangeUnaryExp(null, location, readEvaluatorP6Expression());
//				exp = new MapRangeExpression(location, readEvaluatorP6Expression());
				break;

			case ELEMS:
				nextToken();
				exp = new AElementsUnaryExp(null, location, readEvaluatorP6Expression());
//				exp = new ElementsExpression(location, readEvaluatorP6Expression());
				break;

			case ABS:
				nextToken();
				exp = new AAbsoluteUnaryExp(null, location, readEvaluatorP6Expression());
				//exp = new AbsoluteExpression(location, readEvaluatorP6Expression());
				break;

			case DINTER:
				nextToken();
				exp = new ADistIntersectUnaryExp(null, location, readEvaluatorP6Expression());
//				exp = new DistIntersectExpression(location, readEvaluatorP6Expression());
				break;

			case MERGE:
				nextToken();
				exp = new ADistMergeUnaryExp(null, location, readEvaluatorP6Expression());
//				exp = new DistMergeExpression(location, readEvaluatorP6Expression());
				break;

			case HEAD:
				nextToken();
				exp = new AHeadUnaryExp(null, location, readEvaluatorP6Expression());
//				exp = new HeadExpression(location, readEvaluatorP6Expression());
				break;

			case TAIL:
				nextToken();
				exp = new ATailUnaryExp(null, location, readEvaluatorP6Expression());
//				exp = new TailExpression(location, readEvaluatorP6Expression());
				break;

			case REVERSE:
				if (Settings.release == Release.CLASSIC)
				{
					throwMessage(2291, "'reverse' not available in VDM classic");
				}

				nextToken();
				exp = new AReverseUnaryExp(null, location, readEvaluatorP6Expression());
//				exp = new ReverseExpression(location, readEvaluatorP6Expression());
				break;

			case FLOOR:
				nextToken();
				exp = new AFloorUnaryExp(null, location, readEvaluatorP6Expression());
//				exp = new FloorExpression(location, readEvaluatorP6Expression());
				break;

			case DUNION:
				nextToken();
				exp = new ADistUnionUnaryExp(null, location, readEvaluatorP6Expression());
//				exp = new DistUnionExpression(location, readEvaluatorP6Expression());
				break;

			case DISTCONC:
				nextToken();
				exp = new ADistConcatUnaryExp(null, location, readEvaluatorP6Expression());
//				exp = new DistConcatExpression(location, readEvaluatorP6Expression());
				break;

			case INDS:
				nextToken();
				exp = new AIndicesUnaryExp(null, location, readEvaluatorP6Expression());
//				exp = new IndicesExpression(location, readEvaluatorP6Expression());
				break;

			default:
				exp = readApplicatorExpression();
				break;
		}

		return exp;
	}

	// Applicator Family. Left grouping(?)

	private PExp readApplicatorExpression()
		throws ParserException, LexException
	{
		PExp exp = readBasicExpression();
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

    				if (nextToken().is(VDMToken.KET))
    				{
    					if (exp instanceof AVariableExp)
    					{
    						AVariableExp ve = (AVariableExp)exp;
    						String name = ve.getName().name;
//    						String name = ve.getName().name;

        					if (name.startsWith("mk_"))
    						{
        						// a mk_TYPE() with no field values
    							exp = readMkExpression(ve);
    							break;
    						} 
        				}
    					
    					exp = new AApplyExp(null,exp.getLocation(), exp,  null, null, null);
//   					exp = new ApplyExpression(exp);
    					nextToken();
    				}
    				else
    				{
    					if (exp instanceof AVariableExp)
    					{
    						AVariableExp ve = (AVariableExp)exp;
    						String name = ve.getName().name;

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

    					PExp first = readExpression();

    					if (lastToken().is(VDMToken.COMMA))
    					{
    						reader.push();

    						if (nextToken().is(VDMToken.RANGE))
    						{
    							nextToken();
    							checkFor(VDMToken.COMMA, 2120, "Expecting 'e1,...,e2' in subsequence");
    							PExp last = readExpression();
    							checkFor(VDMToken.KET, 2121, "Expecting ')' after subsequence");
    							reader.unpush();
    							exp = new ASubseqExp(null, exp.getLocation(), exp, first, last);
//    							exp = new SubseqExpression(exp, first, last);
    							break;
    						}

    						reader.pop();	// Not a subsequence then...
    					}

    					// OK, so read a (list, of, arguments)...

						List<PExp> args = new NodeList<PExp>(null);
						args.add(first);

						while (ignore(VDMToken.COMMA))
						{
							args.add(readExpression());
						}

						checkFor(VDMToken.KET, 2122, "Expecting ')' after function args");
						exp = new AApplyExp(null, exp.getLocation(), exp, args, null, null);
						//exp = new ApplyExpression(exp, args);
    				}
    				break;

    			case SEQ_OPEN:
    				// Polymorphic function instantiation
    				List<PType> types = new Vector<PType>();
    				TypeReader tr = getTypeReader();

    				nextToken();
    				types.add(tr.readType());

    				while (ignore(VDMToken.COMMA))
    				{
    					types.add(tr.readType());
    				}

    				checkFor(VDMToken.SEQ_CLOSE, 2123, "Expecting ']' after function instantiation");
   					exp = new AFuncInstatiationExp(null, exp.getLocation(), exp, types, null, null);
    				//exp = new FuncInstantiationExpression(exp, types);
    				break;

    			case POINT:
    				// Field selection by name or number
    				switch (nextToken().type)
    				{
    					case NAME:
    						if (dialect != Dialect.VDM_SL)
    						{
    							exp = new AFieldExp(null, exp.getLocation(), exp, lastNameToken(), null);
//        						exp = new FieldExpression(exp, lastNameToken());
    						}
    						else
    						{
    							throwMessage(2030, "Expecting simple field identifier");
    						}
    						break;

    					case IDENTIFIER:
    						exp = new AFieldExp(null, exp.getLocation(), exp, null, lastIdToken());
//    						exp = new FieldExpression(exp, lastIdToken());
    						break;

    					case HASH:
    						if (nextToken().isNot(VDMToken.NUMBER))
    						{
    							throwMessage(2031, "Expecting field number after .#");
    						}

    						LexIntegerToken num = (LexIntegerToken)lastToken();
    						exp = new AFieldNumberExp(null, exp.getLocation(), exp, num);
//    						exp = new FieldNumberExpression(exp, num);
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

		if (exp instanceof AVariableExp)
		{
			AVariableExp ve = (AVariableExp)exp;
			ve.setName( ve.getName().getExplicit(true));
			//ve.setExplicit(true);
		}

		// Combinator Family. Right grouping.
		LexToken token = lastToken();

		if (token.is(VDMToken.COMP))
		{
			nextToken();
			return new ACompBinaryExp(null, null, exp, token, readApplicatorExpression());
//			return new CompExpression(exp, token, readApplicatorExpression());
		}

		if (token.is(VDMToken.STARSTAR))
		{
			nextToken();
			return new AStarStarBinaryExp(null, null, exp, token, readEvaluatorP6Expression());
//			return new StarStarExpression(exp, token, readEvaluatorP6Expression());
		}

		return exp;
	}

	private PExp readBasicExpression()
		throws ParserException, LexException
	{
		LexToken token = lastToken();
		
		switch (token.type)
		{
			case NUMBER:
				nextToken();
				return new AIntConstExp(null, token.location, (LexIntegerToken)token);
//				return new IntegerLiteralExpression((LexIntegerToken)token);

			case REALNUMBER:
				nextToken(); 
				return new ARealConstExp(null, token.location, (LexRealToken)token);
//				return new RealLiteralExpression((LexRealToken)token);

			case NAME:
				// Includes mk_ constructors
				LexNameToken name = lastNameToken();
				nextToken();
				return new AVariableExp(null, name.location, name);
//				return new VariableExpression(name);

			case IDENTIFIER:
				// Includes mk_ constructors
				// Note we can't use lastNameToken as this checks that we don't
				// use old~ names.
				LexNameToken id =
					new LexNameToken(reader.currentModule, (LexIdentifierToken)token);
				nextToken();
				return new AVariableExp(null, id.location, id);
//				return new VariableExpression(id);

			case STRING:
				nextToken();
				return new AStringConstExp(null, token.location, (LexStringToken)token);
//				return new StringLiteralExpression((LexStringToken)token);

			case CHARACTER:
				nextToken();
				return new ACharConstExp(null, token.location, (LexCharacterToken)token);
				//return new CharLiteralExpression((LexCharacterToken)token);

			case QUOTE:
				nextToken();
				return new AQuoteConstExp(null, token.location,(LexQuoteToken)token);
//				return new QuoteLiteralExpression((LexQuoteToken)token);

			case TRUE:
			case FALSE:
				nextToken();
				return new ABooleanConstExp(null, token.location, (LexBooleanToken)token);
//				return new BooleanLiteralExpression((LexBooleanToken)token);

			case UNDEFINED:
				nextToken();
				return new AUndefinedExp(null, token.location);
//				return new UndefinedExpression(token.location);

			case NIL:
				nextToken();
				return new ANilExp(null, token.location);
//				return new NilExpression(token.location);

			case THREADID:
				nextToken();
				return new AThreadIdExp(null, token.location);
//				return new ThreadIdExpression(token.location);

			case BRA:
				nextToken();
				PExp exp = readExpression();
				checkFor(VDMToken.KET, 2124, "Expecting ')'");
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
				return new ASelfExp(null, token.location, (LexNameToken)token);
//				return new SelfExpression(token.location);

			case IS:
				switch (nextToken().type)
				{
					case NOT:
						nextToken();
						checkFor(VDMToken.YET, 2125, "Expecting 'is not yet specified'");
						checkFor(VDMToken.SPECIFIED, 2126, "Expecting 'is not yet specified'");
						return new ANotYetSpecifiedExp(null, token.location);
//						return new NotYetSpecifiedExpression(token.location);

					case SUBCLASS:
						nextToken();
						checkFor(VDMToken.RESPONSIBILITY, 2127, "Expecting 'is subclass responsibility'");
						return new ASubclassResponsibilityExp(null, token.location);
//						return new SubclassResponsibilityExpression(token.location);
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

	private PExp readTimeExpression(LexLocation location) throws LexException
	{
		nextToken();
		return new ATimeExp(null, location);
//		return new TimeExpression(location);
	}

	private AMuExp readMuExpression(AVariableExp ve)
		throws ParserException, LexException
	{
		List<ARecordModifier> args = new Vector<ARecordModifier>();
		PExp record = readExpression();

		do
		{
			checkFor(VDMToken.COMMA, 2128, "Expecting comma separated record modifiers");
			LexIdentifierToken id = readIdToken("Expecting <identifier> |-> <expression>");
			checkFor(VDMToken.MAPLET, 2129, "Expecting <identifier> |-> <expression>");
			args.add(new ARecordModifier(id, readExpression()));
		}
		while (lastToken().is(VDMToken.COMMA));

		checkFor(VDMToken.KET, 2130, "Expecting ')' after mu maplets");
		return new AMuExp(null, ve.getLocation(), record, args);
		//return new MuExpression(ve.location, record, args);
	}

	private PExp readMkExpression(AVariableExp ve)
		throws ParserException, LexException
	{
		List<PExp> args = new NodeList<PExp>(null);

		if (lastToken().isNot(VDMToken.KET))	// NB. mk_T() is legal
		{
			args.add(readExpression());

			while (ignore(VDMToken.COMMA))
			{
				args.add(readExpression());
			}
		}

		checkFor(VDMToken.KET, 2131, "Expecting ')' after mk_ tuple");
		PExp exp = null;

		if (ve.getName().name.equals("mk_"))
		{
			if (args.size() < 2)
			{
				throwMessage(2035, "Tuple must have >1 argument");
			}

			exp = new ATupleExp(null, ve.getLocation(), args);
//			exp = new TupleExpression(ve.location, args);
		}
		else
		{
			LexNameToken typename = getMkTypeName(ve.getName());
			VDMToken type = VDMToken.lookup(typename.name, Dialect.VDM_SL);

			if (type != null)
			{
				PExp value = args.get(0);

				switch (type) //TODO is this right? Type information is lost? If so lose the switch
				{
					case BOOL:
						exp = new AMkBasicExp(new ABooleanBasicType(ve.getLocation(), false,null), ve.getLocation(), value);
//						exp = new MkBasicExpression(new BooleanType(ve.location), value);
						break;

					case NAT:
						exp = new AMkBasicExp(new ANatNumericBasicType(ve.getLocation(), false,null), ve.getLocation(), value);
//						exp = new MkBasicExpression(new NaturalType(ve.location), value);
						break;

					case NAT1:
						exp = new AMkBasicExp(new ANatOneNumericBasicType(ve.getLocation(), false,null), ve.getLocation(), value);
//						exp = new MkBasicExpression(new NaturalOneType(ve.location), value);
						break;

					case INT:
						exp = new AMkBasicExp(new AIntNumericBasicType(ve.getLocation(), false,null), ve.getLocation(), value);
//						exp = new MkBasicExpression(new IntegerType(ve.location), value);
						break;

					case RAT:
						exp = new AMkBasicExp(new ARationalNumericBasicType(ve.getLocation(), false,null), ve.getLocation(), value);
//						exp = new MkBasicExpression(new RationalType(ve.location), value);
						break;

					case REAL:
						exp = new AMkBasicExp(new ARealNumericBasicType(ve.getLocation(), false,null), ve.getLocation(), value);
//						exp = new MkBasicExpression(new RealType(ve.location), value);
						break;

					case CHAR:
						exp = new AMkBasicExp(new ACharBasicType(ve.getLocation(), false,null), ve.getLocation(), value);
//						exp = new MkBasicExpression(new CharacterType(ve.location), value);
						break;

					case TOKEN:
						exp = new AMkBasicExp(new ATokenBasicType(ve.getLocation(), false,null), ve.getLocation(), value);
//						exp = new MkBasicExpression(new TokenType(ve.location), value);
						break;

					default:
						throwMessage(2036, "Expecting mk_<type>");
				}
			}
			else
			{
				exp = new AMkTypeExp(null, ve.getLocation(), typename, args);
//				exp = new MkTypeExpression(typename, args);
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

	private AIsExp readIsExpression(AVariableExp ve)
		throws ParserException, LexException
	{
		String name = ve.getName().name;
		AIsExp exp = null;

		if (name.equals("is_"))
		{
			PExp test = readExpression();
			checkFor(VDMToken.COMMA, 2132, "Expecting is_(expression, type)");
			TypeReader tr = getTypeReader();
			PType type = tr.readType();

			if (type instanceof AUnresolvedType)
			{
				AUnresolvedType nt = (AUnresolvedType)type;
				exp = new AIsExp(null,nt.getLocation(), nt.getTypename(), null, test);
//				exp = new IsExpression(ve.location, nt.typename, test);
			}
			else
			{
				exp = new AIsExp(null, ve.getLocation(), null, type, test);
//				exp = new IsExpression(ve.location, type, test);
			}
		}
		else
		{
			LexNameToken typename = getMkTypeName(ve.getName());
			VDMToken type = VDMToken.lookup(typename.name, Dialect.VDM_SL);

			if (type != null)
			{
				switch (type)
				{
					case BOOL: 
						exp = new AIsExp(new ABooleanBasicType(ve.getLocation(), false,null), ve.getLocation(), typename, null, readExpression());
//						exp = new IsExpression(ve.location, new BooleanType(ve.location), readExpression());
						break;

					case NAT:
						exp = new AIsExp(new ANatNumericBasicType(ve.getLocation(), false,null), ve.getLocation(), typename, null, readExpression());
//						exp = new IsExpression(ve.location, new NaturalType(ve.location), readExpression());
						break;

					case NAT1:
						exp = new AIsExp(new ANatOneNumericBasicType(ve.getLocation(), false,null), ve.getLocation(), typename, null, readExpression());
//						exp = new IsExpression(ve.location, new NaturalOneType(ve.location), readExpression());
						break;

					case INT:
						exp = new AIsExp(new AIntNumericBasicType(ve.getLocation(), false,null), ve.getLocation(), typename, null, readExpression());
//						exp = new IsExpression(ve.location, new IntegerType(ve.location), readExpression());
						break;

					case RAT:
						exp = new AIsExp(new ARationalNumericBasicType(ve.getLocation(), false,null), ve.getLocation(), typename, null, readExpression());
//						exp = new IsExpression(ve.location, new RationalType(ve.location), readExpression());
						break;

					case REAL:
						exp = new AIsExp(new ARealNumericBasicType(ve.getLocation(), false,null), ve.getLocation(), typename, null, readExpression());
//						exp = new IsExpression(ve.location, new RealType(ve.location), readExpression());
						break;

					case CHAR:
						exp = new AIsExp(new ACharBasicType(ve.getLocation(), false,null), ve.getLocation(), typename, null, readExpression());
//						exp = new IsExpression(ve.location, new CharacterType(ve.location), readExpression());
						break;

					case TOKEN:
						exp = new AIsExp(new ATokenBasicType(ve.getLocation(), false,null), ve.getLocation(), typename, null, readExpression());
//						exp = new IsExpression(ve.location, new TokenType(ve.location), readExpression());
						break;

					default:
						throwMessage(2038, "Expecting is_<type>");
				}
			}
			else
			{
				exp = new AIsExp(null, ve.getLocation(), typename, null, readExpression());
//				exp = new IsExpression(ve.location, typename, readExpression());
			}
		}

		checkFor(VDMToken.KET, 2133, "Expecting ')' after is_ expression");
		return exp;
	}

	private APreExp readPreExpression(AVariableExp ve)
		throws ParserException, LexException
	{
		List<PExp> args = new Vector<PExp>(); 
		PExp function = readExpression();

		while (ignore(VDMToken.COMMA))
		{
			args.add(readExpression());
		}

		checkFor(VDMToken.KET, 2134, "Expecting pre_(function [,args])");

		return new APreExp(null, ve.getLocation(), function, args);
//		return new PreExpression(ve.location, function, args);
	}

	private PExp readSetOrMapExpression(LexLocation start)
		throws ParserException, LexException
	{
		LexToken token = lastToken();

		if (token.is(VDMToken.SET_CLOSE))
		{
			nextToken();
			return new ASetEnumSetExp(null, token.location, null); //TODO
//			return new SetEnumExpression(start);		// empty set
		}
		else if (token.is(VDMToken.MAPLET))
		{
			nextToken();
			checkFor(VDMToken.SET_CLOSE, 2135, "Expecting '}' in empty map");
			return new AMapEnumMapExp(null,token.location, null);
//			return new MapEnumExpression(start);		// empty map
		}

		PExp first = readExpression();
		token = lastToken();

		if (token.is(VDMToken.MAPLET))
		{
			nextToken();
			AMapletExp maplet = new AMapletExp(null, token.location, first, readExpression());
			//MapletExpression maplet = new MapletExpression(first, token, readExpression());
			return readMapExpression(start, maplet);
		}
		else
		{
			return readSetExpression(start, first);
		}
	}

//	private SetExpression readSetExpression(LexLocation start, PExp first)
	private SSetExp readSetExpression(LexLocation start, PExp first)
		throws ParserException, LexException
	{
		SSetExp result = null;
//		SetExpression result = null;

		if (lastToken().is(VDMToken.PIPE))
		{
			nextToken();
			BindReader br = getBindReader();
			List<PMultipleBind> bindings = br.readBindList();
			PExp exp = null;

			if (lastToken().is(VDMToken.AMPERSAND))
			{
				nextToken();
				exp = readExpression();
			}

			checkFor(VDMToken.SET_CLOSE, 2136, "Expecting '}' after set comprehension");
			result = new ASetCompSetExp(null, exp.getLocation(), first, bindings, exp);
//			result = new SetCompExpression(start, first, bindings, exp);
		}
		else
		{
			if (lastToken().is(VDMToken.COMMA))
			{
				reader.push();

				if (nextToken().is(VDMToken.RANGE))
				{
					nextToken();
					checkFor(VDMToken.COMMA, 2137, "Expecting 'e1,...,e2' in set range");
					PExp end = readExpression();
					checkFor(VDMToken.SET_CLOSE, 2138, "Expecting '}' after set range");
					reader.unpush();
					return new ASetRangeSetExp(null, start, first, end); //TODO inheritance issue?
//					return new SetRangeExpression(start, first, end);
				}

				reader.pop();	// Not a set range then...
			}

			List<PExp>  members = new NodeList<PExp>(null);
			members.add(first);

			while (ignore(VDMToken.COMMA))
			{
				members.add(readExpression());
			}

			checkFor(VDMToken.SET_CLOSE, 2139, "Expecting '}' after set enumeration");
			result = new ASetEnumSetExp(null, start, members);
//			result = new SetEnumExpression(start, members);
		}

		return result;
	}

	//private MapExpression readMapExpression(LexLocation start, MapletExpression first)
	private SMapExp readMapExpression(LexLocation start, AMapletExp first)
		throws ParserException, LexException
	{
		SMapExp result = null;

		if (lastToken().is(VDMToken.PIPE))
		{
			nextToken();
			BindReader br = getBindReader();
			List<PMultipleBind> bindings = br.readBindList();
			PExp exp = null;

			if (lastToken().is(VDMToken.AMPERSAND))
			{
				nextToken();
				exp = readExpression();
			}

			checkFor(VDMToken.SET_CLOSE, 2140, "Expecting '}' after map comprehension");
			result = new AMapCompMapExp(null, start, first, bindings, exp); //TODO bindings should be list
//			result = new MapCompExpression(start, first, bindings, exp);
		}
		else
		{
			List<AMapletExp> members = new Vector<AMapletExp>();
			//List<MapletExpression> members = new Vector<MapletExpression>();
			members.add(first);

			while (ignore(VDMToken.COMMA))
			{
				PExp member = readExpression();
				LexToken token = lastToken();

				if (token.is(VDMToken.MAPLET))
				{
					nextToken();
					AMapletExp maplet = new AMapletExp(null, token.location, member, readExpression());
					//MapletExpression maplet = new MapletExpression(member, token, readExpression());
					members.add(maplet);
				}
				else
				{
					throwMessage(2039, "Expecting maplet in map enumeration");
				}
			}

			checkFor(VDMToken.SET_CLOSE, 2141, "Expecting '}' after map enumeration");
			result = new AMapEnumMapExp(null, start, members);
//			result = new MapEnumExpression(start, members);
		}

		return result;
	}

	//private SeqExpression readSeqExpression(LexLocation start)
	private SSeqExp readSeqExpression(LexLocation start)
		throws ParserException, LexException
	{
		if (lastToken().is(VDMToken.SEQ_CLOSE))
		{
			nextToken();
			return new ASeqEnumSeqExp(null, start, null);
//			return new SeqEnumExpression(start);		// empty list
		}

		SSeqExp result = null;
		//SeqExpression result = null;
		PExp first = readExpression();

		if (lastToken().is(VDMToken.PIPE))
		{
			nextToken();
			BindReader br = getBindReader();
			ASetBind setbind = br.readSetBind();
//			PSet setbind = br.readSetBind();
			PExp exp = null;

			if (lastToken().is(VDMToken.AMPERSAND))
			{
				nextToken();
				exp = readExpression();
			}

			checkFor(VDMToken.SEQ_CLOSE, 2142, "Expecting ']' after list comprehension");
			result = new ASeqCompSeqExp(null, start, first, setbind, exp);
//			result = new SeqCompExpression(start, first, setbind, exp);
		}
		else
		{
			List<PExp> members = new NodeList<PExp>(null);
			members.add(first);

			while (ignore(VDMToken.COMMA))
			{
				members.add(readExpression());
			}

			checkFor(VDMToken.SEQ_CLOSE, 2143, "Expecting ']' after list enumeration");
			result = new ASeqEnumSeqExp(null, start, members);
//			result = new SeqEnumExpression(start, members);
		}

		return result;
	}

	private AIfExp readIfExpression(LexLocation start)
		throws ParserException, LexException
	{
		PExp exp = readExpression();
		checkFor(VDMToken.THEN, 2144, "Missing 'then'");
		PExp thenExp = readExpression();
		List<AElseIfExp> elseList = new Vector<AElseIfExp>();
//		List<ElseIfExpression> elseList = new Vector<ElseIfExpression>();

		while (lastToken().is(VDMToken.ELSEIF))
		{
			nextToken();
			elseList.add(readElseIfExpression(lastToken().location));
		}

		PExp elseExp = null;

		if (lastToken().is(VDMToken.ELSE))
		{
			nextToken();
			elseExp = readConnectiveExpression();	// Precedence < maplet?
		}
		else
		{
			throwMessage(2040, "Expecting 'else' in 'if' expression");
		}

		return new AIfExp(null, exp.getLocation(), exp, thenExp, elseList, elseExp);
//		return new IfExpression(start, exp, thenExp, elseList, elseExp);
	}


	private AElseIfExp readElseIfExpression(LexLocation start)
		throws ParserException, LexException
	{
		PExp exp = readExpression();
		checkFor(VDMToken.THEN, 2145, "Missing 'then' after 'elseif'");
		PExp thenExp = readExpression();
		return new AElseIfExp(null, exp.getLocation(), exp, thenExp);
		//return new ElseIfExpression(start, exp, thenExp);
	}

	private ACasesExp readCasesExpression(LexLocation start)
		throws ParserException, LexException
	{
		PExp exp = readExpression();
		checkFor(VDMToken.COLON, 2146, "Expecting ':' after cases expression");

		List<ACaseAlternative> cases = new Vector<ACaseAlternative>();
//		List<CaseAlternative> cases = new Vector<CaseAlternative>();
		PExp others = null;
		cases.addAll(readCaseAlternatives(exp));

		while (ignore(VDMToken.COMMA))
		{
			if (lastToken().is(VDMToken.OTHERS))
			{
				break;
			}

			cases.addAll(readCaseAlternatives(exp));
		}

		if (lastToken().is(VDMToken.OTHERS))
		{
			nextToken();
			checkFor(VDMToken.ARROW, 2147, "Expecting '->' after others");
			others = readExpression();
		}

		checkFor(VDMToken.END, 2148, "Expecting 'end' after cases");
		return new ACasesExp(null, exp.getLocation(), exp, cases, others);
//		return new CasesExpression(start, exp, cases, others);
	}

	private List<ACaseAlternative> readCaseAlternatives(PExp exp)
		throws ParserException, LexException
	{
		List<ACaseAlternative> alts = new Vector<ACaseAlternative>();
		List<PPattern> plist = getPatternReader().readPatternList();
//		PatternList plist = getPatternReader().readPatternList();
		checkFor(VDMToken.ARROW, 2149, "Expecting '->' after case pattern list");
		PExp then = readExpression();

		for (PPattern p: plist) 
		{
			alts.add(new ACaseAlternative(null, exp, p, then,null,null)); 
			//alts.add(new CaseAlternative(exp, p, then));
		}

		return alts;
	}

	private PExp readLetExpression(LexLocation start)
		throws ParserException, LexException
	{
		ParserException letDefError = null;

		try
		{
			reader.push();
			ALetDefExp exp = readLetDefExpression(start);
//			LetDefExpression exp = readLetDefExpression(start);
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
			ALetBeStExp exp = readLetBeStExpression(start);
//			LetBeStExpression exp = readLetBeStExpression(start);
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

	private ALetDefExp readLetDefExpression(LexLocation start)
		throws ParserException, LexException
	{
		DefinitionReader dr = getDefinitionReader();
		
		List<PDefinition> localDefs = new Vector<PDefinition>();

		localDefs.add(dr.readLocalDefinition(NameScope.LOCAL));

		while (ignore(VDMToken.COMMA))
		{
			localDefs.add(dr.readLocalDefinition(NameScope.LOCAL));
		}

		checkFor(VDMToken.IN, 2150, "Expecting 'in' after local definitions");
		// Note we read a Connective expression for the body, so that |->
		// terminates the parse.
		return new ALetDefExp(null, start,localDefs, readConnectiveExpression());
//		return new LetDefExpression(start, localDefs, readConnectiveExpression());
	}

	private ALetBeStExp readLetBeStExpression(LexLocation start)
		throws ParserException, LexException
	{
		PMultipleBind bind = getBindReader().readMultipleBind();
		PExp stexp = null;

		if (lastToken().is(VDMToken.BE))
		{
			nextToken();
			checkFor(VDMToken.ST, 2151, "Expecting 'st' after 'be' in let expression");
			stexp = readExpression();
		}

		checkFor(VDMToken.IN, 2152, "Expecting 'in' after bind in let expression");
		// Note we read a Connective expression for the body, so that |->
		// terminates the parse.
		return new ALetBeStExp(null, start, bind, stexp, readConnectiveExpression());
//		return new LetBeStExpression(start, bind, stexp, readConnectiveExpression());
	}

	private AForAllExp readForAllExpression(LexLocation start)
		throws ParserException, LexException
	{
		List<PMultipleBind> bindList = getBindReader().readBindList();
		checkFor(VDMToken.AMPERSAND, 2153, "Expecting '&' after bind list in forall");
		return new AForAllExp(null, start, bindList, readExpression()); 
//		return new ForAllExpression(start, bindList, readExpression());
	}

	private AExistsExp readExistsExpression(LexLocation start)
		throws ParserException, LexException
	{
		List<PMultipleBind> bindList = getBindReader().readBindList();
		checkFor(VDMToken.AMPERSAND, 2154, "Expecting '&' after bind list in exists");
		return new AExistsExp(null, start, bindList, readExpression());
//		return new ExistsExpression(start, bindList, readExpression());
	}

	private AExists1Exp readExists1Expression(LexLocation start)
		throws ParserException, LexException
	{
		PBind bind = getBindReader().readBind();
//		Bind bind = getBindReader().readBind();
		checkFor(VDMToken.AMPERSAND, 2155, "Expecting '&' after single bind in exists1");
		return new AExists1Exp(null, start, bind, readExpression(),null);
//		return new Exists1Expression(start, bind, readExpression());
	}

	private AIotaExp readIotaExpression(LexLocation start)
		throws ParserException, LexException
	{
		PBind bind = getBindReader().readBind();
//		Bind bind = getBindReader().readBind();
		checkFor(VDMToken.AMPERSAND, 2156, "Expecting '&' after single bind in iota");
		return new AIotaExp(null, start, bind, readExpression());
//		return new IotaExpression(start, bind, readExpression());
	}

	private ALambdaExp readLambdaExpression(LexLocation start)
		throws ParserException, LexException
	{
		List<ATypeBind> bindList = getBindReader().readTypeBindList();
		checkFor(VDMToken.AMPERSAND, 2157, "Expecting '&' after bind list in lambda");
		return new ALambdaExp(null, start, bindList, readExpression()); 
//		return new LambdaExpression(start, bindList, readExpression());
	}

	private ADefExp readDefExpression(LexLocation start)
		throws ParserException, LexException
	{
		DefinitionReader dr = getDefinitionReader();
		List<PDefinition> equalsDefs = new Vector<PDefinition>();

		while (lastToken().isNot(VDMToken.IN))
		{
			equalsDefs.add(dr.readEqualsDefinition());
			ignore(VDMToken.SEMICOLON);
		}

		checkFor(VDMToken.IN, 2158, "Expecting 'in' after equals definitions");
		return new ADefExp(null, start, equalsDefs, readExpression());
//		return new DefExpression(start, equalsDefs, readExpression());
	}

	private ANewExp readNewExpression(LexLocation start)
		throws ParserException, LexException
	{
		LexIdentifierToken name = readIdToken("Expecting class name after 'new'");
		checkFor(VDMToken.BRA, 2159, "Expecting '(' after new class name");

    	List<PExp> args = new NodeList<PExp>(null);
    	ExpressionReader er = getExpressionReader();

    	if (lastToken().isNot(VDMToken.KET))
    	{
    		args.add(er.readExpression());

    		while (ignore(VDMToken.COMMA))
    		{
        		args.add(er.readExpression());
    		}
    	}

    	checkFor(VDMToken.KET, 2124, "Expecting ')' after constructor args");
    	return new ANewExp(null, start, name, args);
//    	return new NewExpression(start, name, args);
    }

	private AIsOfBaseClassExp readIsOfBaseExpression(LexLocation start)
		throws ParserException, LexException
	{
		checkFor(VDMToken.BRA, 2160, "Expecting '(' after 'isofbase'");
    	List<PExp> args = readExpressionList();
    	checkFor(VDMToken.KET, 2161, "Expecting ')' after 'isofbase' args");

    	if (args.size() != 2)
    	{
    		throwMessage(2041, "Expecting two arguments for 'isofbase'");
    	}

    	if (!(args.get(0) instanceof AVariableExp))
    	{
    		throwMessage(2042, "Expecting (<class>,<exp>) arguments for 'isofbase'");
    	}

    	LexNameToken classname = ((AVariableExp)args.get(0)).getName();

		if (classname.old)
		{
			throwMessage(2295, "Can't use old name here", classname);
		}

		return new AIsOfBaseClassExp(null, start, classname, args.get(1));
//		return new IsOfBaseClassExpression(start, classname, args.get(1));
    }

	private AIsOfClassExp readIsOfClassExpression(LexLocation start)
		throws ParserException, LexException
	{
		checkFor(VDMToken.BRA, 2162, "Expecting '(' after 'isofclass'");
    	List<PExp> args = readExpressionList();
    	checkFor(VDMToken.KET, 2163, "Expecting ')' after 'isofclass' args");

    	if (args.size() != 2)
    	{
    		throwMessage(2043, "Expecting two arguments for 'isofclass'");
    	}

    	if (!(args.get(0) instanceof AVariableExp))
    	{
    		throwMessage(2044, "Expecting (<class>,<exp>) arguments for 'isofclass'");
    	}

    	LexNameToken classname = ((AVariableExp)args.get(0)).getName();

		if (classname.old)
		{
			throwMessage(2295, "Can't use old name here", classname);
		}

		return new AIsOfClassExp(null, start, classname, args.get(1));
//		return new IsOfClassExpression(start, classname, args.get(1));
    }

	private ASameBaseClassExp readSameBaseExpression(LexLocation start)
		throws ParserException, LexException
	{
		checkFor(VDMToken.BRA, 2164, "Expecting '(' after 'samebaseclass'");
    	List<PExp> args = readExpressionList();
    	checkFor(VDMToken.KET, 2165, "Expecting ')' after 'samebaseclass' args");

    	if (args.size() != 2)
    	{
    		throwMessage(2045, "Expecting two expressions in 'samebaseclass'");
    	}

    	return new ASameBaseClassExp(null, start, args.get(0), args.get(1));
//    	return new SameBaseClassExpression(start, args);
    }

	private ASameClassExp readSameClassExpression(LexLocation start)
		throws ParserException, LexException
	{
		checkFor(VDMToken.BRA, 2166, "Expecting '(' after 'sameclass'");
    	List<PExp> args = readExpressionList();
    	checkFor(VDMToken.KET, 2167, "Expecting ')' after 'sameclass' args");

    	if (args.size() != 2)
    	{
    		throwMessage(2046, "Expecting two expressions in 'sameclass'");
    	}
    	
    	return new ASameClassExp(null, start, args.get(0), args.get(1));
//    	return new SameClassExpression(start, args);
    }

	private boolean inPerExpression = false;

	public PExp readPerExpression() throws ParserException, LexException
	{
		inPerExpression = true;
		PExp e = readExpression();
		inPerExpression = false;
		return e;
	}

	private PExp readHistoryExpression(LexLocation location)
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
				checkFor(VDMToken.BRA, 2168, "Expecting " + s + "(name(s))");

				LexNameList opnames = new LexNameList();
				LexNameToken opname = readNameToken("Expecting a name");
				opnames.add(opname);

				while (ignore(VDMToken.COMMA))
				{
					opname = readNameToken("Expecting " + s + "(name(s))");
					opnames.add(opname);
				}

				checkFor(VDMToken.KET, 2169, "Expecting " + s + "(name(s))");
				return new AHistoryExp(null, op.location, op, opnames);
//				return new HistoryExpression(location, op.type, opnames);

			default:
				throwMessage(2048, "Expecting #act, #active, #fin, #req or #waiting");
				return null;
		}
	}
}
