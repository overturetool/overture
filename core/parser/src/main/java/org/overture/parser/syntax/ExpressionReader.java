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

import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.overture.ast.annotations.PAnnotation;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.AAnnotatedUnaryExp;
import org.overture.ast.expressions.ACaseAlternative;
import org.overture.ast.expressions.ACasesExp;
import org.overture.ast.expressions.ADefExp;
import org.overture.ast.expressions.AElseIfExp;
import org.overture.ast.expressions.AEqualsBinaryExp;
import org.overture.ast.expressions.AExists1Exp;
import org.overture.ast.expressions.AExistsExp;
import org.overture.ast.expressions.AForAllExp;
import org.overture.ast.expressions.AIfExp;
import org.overture.ast.expressions.AIotaExp;
import org.overture.ast.expressions.AIsExp;
import org.overture.ast.expressions.AIsOfBaseClassExp;
import org.overture.ast.expressions.AIsOfClassExp;
import org.overture.ast.expressions.ALambdaExp;
import org.overture.ast.expressions.ALetBeStExp;
import org.overture.ast.expressions.ALetDefExp;
import org.overture.ast.expressions.AMapletExp;
import org.overture.ast.expressions.AMuExp;
import org.overture.ast.expressions.ANarrowExp;
import org.overture.ast.expressions.ANewExp;
import org.overture.ast.expressions.APreExp;
import org.overture.ast.expressions.ARecordModifier;
import org.overture.ast.expressions.ASameBaseClassExp;
import org.overture.ast.expressions.ASameClassExp;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.expressions.SMapExp;
import org.overture.ast.expressions.SSeqExp;
import org.overture.ast.expressions.SSetExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexCommentList;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.Dialect;
import org.overture.ast.lex.LexBooleanToken;
import org.overture.ast.lex.LexCharacterToken;
import org.overture.ast.lex.LexIdentifierToken;
import org.overture.ast.lex.LexIntegerToken;
import org.overture.ast.lex.LexKeywordToken;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.lex.LexQuoteToken;
import org.overture.ast.lex.LexRealToken;
import org.overture.ast.lex.LexStringToken;
import org.overture.ast.lex.LexToken;
import org.overture.ast.lex.VDMToken;
import org.overture.ast.node.NodeList;
import org.overture.ast.patterns.ATypeBind;
import org.overture.ast.patterns.PBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.AUnresolvedType;
import org.overture.ast.types.PType;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.parser.lex.LexException;
import org.overture.parser.lex.LexTokenReader;

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

	private PExp readConnectiveExpression() throws ParserException,
			LexException
	{
		PExp exp = readImpliesExpression();
		LexToken token = lastToken();

		if (token.is(VDMToken.EQUIVALENT))
		{
			nextToken();
			exp = AstFactory.newAEquivalentBooleanBinaryExp(exp, token, readConnectiveExpression());
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
			exp = AstFactory.newAImpliesBooleanBinaryExp(exp, token, readImpliesExpression());
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
			exp = AstFactory.newAOrBooleanBinaryExp(exp, token, readOrExpression());
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
			exp = AstFactory.newAAndBooleanBinaryExp(exp, token, readAndExpression());
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
			exp = AstFactory.newANotUnaryExp(token.location, readNotExpression());
		} else
		{
			exp = readRelationalExpression();
		}

		return exp;
	}

	// Relations Family...

	public AEqualsBinaryExp readDefEqualsExpression() throws ParserException,
			LexException
	{
		// This is an oddball parse for the "def" expression :-)

		PExp exp = readEvaluatorP1Expression();
		LexToken token = lastToken();

		if (readToken().is(VDMToken.EQUALS))
		{
			return AstFactory.newAEqualsBinaryExp(exp, token, readEvaluatorP1Expression());
		}

		throwMessage(2029, "Expecting <set bind> = <expression>");
		return null;
	}

	private PExp readRelationalExpression() throws ParserException,
			LexException
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
				} else
				{
					reader.pop();
				}
			} else
			{
				reader.pop();
			}
		} else if (token.is(VDMToken.IN))
		{
			// Check for "in set"
			reader.push();

			if (nextToken().is(VDMToken.SET))
			{
				token = new LexKeywordToken(VDMToken.INSET, token.location);
				reader.unpush();
			} else
			{
				reader.pop();
			}
		}

		// No grouping for relationals...
		switch (token.type)
		{
			case LT:
				nextToken();
				exp = AstFactory.newALessNumericBinaryExp(exp, token, readNotExpression());
				break;

			case LE:
				nextToken();
				exp = AstFactory.newALessEqualNumericBinaryExp(exp, token, readNotExpression());
				break;

			case GT:
				nextToken();
				exp = AstFactory.newAGreaterNumericBinaryExp(exp, token, readNotExpression());
				break;

			case GE:
				nextToken();
				exp = AstFactory.newAGreaterEqualNumericBinaryExp(exp, token, readNotExpression());
				break;

			case NE:
				nextToken();
				exp = AstFactory.newANotEqualBinaryExp(exp, token, readNotExpression());
				break;

			case EQUALS:
				nextToken();
				exp = AstFactory.newAEqualsBinaryExp(exp, token, readNotExpression());
				break;

			case SUBSET:
				nextToken();
				exp = AstFactory.newASubsetBinaryExp(exp, token, readNotExpression());
				break;

			case PSUBSET:
				nextToken();
				exp = AstFactory.newAProperSubsetBinaryExp(exp, token, readNotExpression());
				break;

			case INSET:
				nextToken();
				exp = AstFactory.newAInSetBinaryExp(exp, token, readNotExpression());
				break;

			case NOTINSET:
				nextToken();
				exp = AstFactory.newANotInSetBinaryExp(exp, token, readNotExpression());
				break;
			default:
				break;
		}

		return exp;
	}

	// Evaluator Family...

	private PExp readEvaluatorP1Expression() throws ParserException,
			LexException
	{
		PExp exp = readEvaluatorP2Expression();
		boolean more = true;

		while (more) // Left grouping
		{
			LexToken token = lastToken();

			switch (token.type)
			{
				case PLUS:
					nextToken();
					exp = AstFactory.newAPlusNumericBinaryExp(exp, token, readEvaluatorP2Expression());
					break;

				case MINUS:
					nextToken();
					exp = AstFactory.newASubstractNumericBinaryExp(exp, token, readEvaluatorP2Expression());
					break;

				case UNION:
					nextToken();
					exp = AstFactory.newASetUnionBinaryExp(exp, token, readEvaluatorP2Expression());
					break;

				case SETDIFF:
					nextToken();
					exp = AstFactory.newASetDifferenceBinaryExp(exp, token, readEvaluatorP2Expression());
					break;

				case MUNION:
					nextToken();
					exp = AstFactory.newAMapUnionBinaryExp(exp, token, readEvaluatorP2Expression());
					break;

				case PLUSPLUS:
					nextToken();
					exp = AstFactory.newAPlusPlusBinaryExp(exp, token, readEvaluatorP2Expression());
					break;

				case CONCATENATE:
					nextToken();
					exp = AstFactory.newASeqConcatBinaryExp(exp, token, readEvaluatorP2Expression());
					break;

				default:
					more = false;
					break;
			}
		}

		return exp;
	}

	private PExp readEvaluatorP2Expression() throws ParserException,
			LexException
	{
		PExp exp = readEvaluatorP3Expression();
		boolean more = true;

		while (more) // Left grouping
		{
			LexToken token = lastToken();

			switch (token.type)
			{
				case TIMES:
					nextToken();
					exp = AstFactory.newATimesNumericBinaryExp(exp, token, readEvaluatorP3Expression());
					break;

				case DIVIDE:
					nextToken();
					exp = AstFactory.newADivideNumericBinaryExp(exp, token, readEvaluatorP3Expression());
					break;

				case REM:
					nextToken();
					exp = AstFactory.newARemNumericBinaryExp(exp, token, readEvaluatorP3Expression());
					break;

				case MOD:
					nextToken();
					exp = AstFactory.newAModNumericBinaryExp(exp, token, readEvaluatorP3Expression());
					break;

				case DIV:
					nextToken();
					exp = AstFactory.newADivNumericBinaryExp(exp, token, readEvaluatorP3Expression());
					break;

				case INTER:
					nextToken();
					exp = AstFactory.newASetIntersectBinaryExp(exp, token, readEvaluatorP3Expression());
					break;

				default:
					more = false;
					break;
			}
		}

		return exp;
	}

	private PExp readEvaluatorP3Expression() throws ParserException,
			LexException
	{
		PExp exp = null;
		LexToken token = lastToken();

		if (token.is(VDMToken.INVERSE))
		{
			nextToken();
			// Unary, so recursion OK for left grouping
			exp = AstFactory.newAMapInverseUnaryExp(token.location, readEvaluatorP3Expression());
		} else
		{
			exp = readEvaluatorP4Expression();
		}

		return exp;
	}

	private PExp readEvaluatorP4Expression() throws ParserException,
			LexException
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
					exp = AstFactory.newADomainResToBinaryExp(exp, token, readEvaluatorP5Expression());
					break;

				case DOMRESBY:
					nextToken();
					exp = AstFactory.newADomainResByBinaryExp(exp, token, readEvaluatorP5Expression());
					break;

				default:
					more = false;
					break;
			}
		}

		return exp;
	}

	private PExp readEvaluatorP5Expression() throws ParserException,
			LexException
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
					exp = AstFactory.newARangeResToBinaryExp(exp, token, readEvaluatorP6Expression());
					break;

				case RANGERESBY:
					nextToken();
					exp = AstFactory.newARangeResByBinaryExp(exp, token, readEvaluatorP6Expression());
					break;

				default:
					more = false;
					break;
			}
		}

		return exp;
	}

	private PExp readEvaluatorP6Expression() throws ParserException,
			LexException
	{
		PExp exp = null;
		LexToken token = lastToken();
		ILexLocation location = token.location;

		// Unary operators, so recursion OK for left grouping
		switch (token.type)
		{
			case PLUS:
				nextToken();
				exp = AstFactory.newAUnaryPlusUnaryExp(location, readEvaluatorP6Expression());
				break;

			case MINUS:
				nextToken();
				exp = AstFactory.newAUnaryMinusUnaryExp(location, readEvaluatorP6Expression());
				break;

			case CARD:
				nextToken();
				exp = AstFactory.newACardinalityUnaryExp(location, readEvaluatorP6Expression());
				break;

			case DOM:
				nextToken();
				exp = AstFactory.newAMapDomainUnaryExp(location, readEvaluatorP6Expression());
				break;

			case LEN:
				nextToken();
				exp = AstFactory.newALenUnaryExp(location, readEvaluatorP6Expression());
				break;

			case POWER:
				nextToken();
				exp = AstFactory.newAPowerSetUnaryExp(location, readEvaluatorP6Expression());
				break;

			case RNG:
				nextToken();
				exp = AstFactory.newAMapRangeUnaryExp(location, readEvaluatorP6Expression());
				break;

			case ELEMS:
				nextToken();
				exp = AstFactory.newAElementsUnaryExp(location, readEvaluatorP6Expression());
				break;

			case ABS:
				nextToken();
				exp = AstFactory.newAAbsoluteUnaryExp(location, readEvaluatorP6Expression());
				break;

			case DINTER:
				nextToken();
				exp = AstFactory.newADistIntersectUnaryExp(location, readEvaluatorP6Expression());
				break;

			case MERGE:
				nextToken();
				exp = AstFactory.newADistMergeUnaryExp(location, readEvaluatorP6Expression());
				break;

			case HEAD:
				nextToken();
				exp = AstFactory.newAHeadUnaryExp(location, readEvaluatorP6Expression());
				break;

			case TAIL:
				nextToken();
				exp = AstFactory.newATailUnaryExp(location, readEvaluatorP6Expression());
				break;

			case REVERSE:
				if (Settings.release == Release.CLASSIC)
				{
					throwMessage(2291, "'reverse' not available in VDM classic");
				}

				nextToken();
				exp = AstFactory.newAReverseUnaryExp(location, readEvaluatorP6Expression());
				break;

			case FLOOR:
				nextToken();
				exp = AstFactory.newAFloorUnaryExp(location, readEvaluatorP6Expression());
				break;

			case DUNION:
				nextToken();
				exp = AstFactory.newADistUnionUnaryExp(location, readEvaluatorP6Expression());
				break;

			case DISTCONC:
				nextToken();
				exp = AstFactory.newADistConcatUnaryExp(location, readEvaluatorP6Expression());
				break;

			case INDS:
				nextToken();
				exp = AstFactory.newAIndicesUnaryExp(location, readEvaluatorP6Expression());
				break;

			default:
				exp = readApplicatorExpression();
				break;
		}

		return exp;
	}

	// Applicator Family. Left grouping(?)

	private PExp readApplicatorExpression() throws ParserException,
			LexException
	{
		PExp exp = readAnnotatedExpression();
		PAnnotation annotation = null;
		
		if (exp instanceof AAnnotatedUnaryExp)
		{
			// Remember and re-apply the annotation while exp is rewired.
			AAnnotatedUnaryExp ae = (AAnnotatedUnaryExp)exp;
			annotation = ae.getAnnotation();
			exp = ae.getExp();
		}

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
							AVariableExp ve = (AVariableExp) exp;
							String name = ve.getName().getName();

							if (name.startsWith("mk_"))
							{
								// a mk_TYPE() with no field values
								exp = readMkExpression(ve);
								break;
							}
						}

						exp = AstFactory.newAApplyExp(exp);
						nextToken();
					} else
					{
						if (exp instanceof AVariableExp)
						{
							AVariableExp ve = (AVariableExp) exp;
							String name = ve.getName().getName();

							if (name.equals("mu"))
							{
								exp = readMuExpression(ve);
								break;
							} else if (name.startsWith("mk_"))
							{
								exp = readMkExpression(ve);
								break;
							} else if (name.startsWith("is_"))
							{
								exp = readIsExpression(ve);
								break;
							} else if (name.equals("pre_"))
							{
								exp = readPreExpression(ve);
								break;
							} else if (name.equals("narrow_"))
							{
								if (Settings.release == Release.CLASSIC)
								{
									throwMessage(2303, "Narrow not available in VDM classic", ve.getName());
								} else
								{
									exp = readNarrowExpression(ve);
								}
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
								exp = AstFactory.newASubseqExp(exp, first, last);
								break;
							}

							reader.pop(); // Not a subsequence then...
						}

						// OK, so read a (list, of, arguments)...

						List<PExp> args = new NodeList<PExp>(null);
						args.add(first);

						while (ignore(VDMToken.COMMA))
						{
							args.add(readExpression());
						}

						checkFor(VDMToken.KET, 2122, "Expecting ')' after function args");
						exp = AstFactory.newAApplyExp(exp, args);
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
					exp = AstFactory.newAFuncInstatiationExp(exp, types);
					break;

				case POINT:
					// Field selection by name or number
					switch (nextToken().type)
					{
						case NAME:
							if (dialect != Dialect.VDM_SL)
							{
								exp = AstFactory.newAFieldExp(exp, lastNameToken());
							} else
							{
								throwMessage(2030, "Expecting simple field identifier");
							}
							break;

						case IDENTIFIER:
							exp = AstFactory.newAFieldExp(exp, lastIdToken());
							break;

						case HASH:
							if (nextToken().isNot(VDMToken.NUMBER))
							{
								throwMessage(2031, "Expecting field number after .#");
							}

							LexIntegerToken num = (LexIntegerToken) lastToken();
							exp = AstFactory.newAFieldNumberExp(exp, num);
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
			AVariableExp ve = (AVariableExp) exp;
			ve.setName(ve.getName().getExplicit(true));
		}

		// Combinator Family. Right grouping.
		LexToken token = lastToken();

		if (token.is(VDMToken.COMP))
		{
			nextToken();
			exp = AstFactory.newACompBinaryExp(exp, token, readApplicatorExpression());
		}
		else if (token.is(VDMToken.STARSTAR))
		{
			nextToken();
			exp = AstFactory.newAStarStarBinaryExp(exp, token, readEvaluatorP6Expression());
		}
		
		if (annotation != null)
		{
			// Re-apply annotation, now that exp has been rewired.
			exp = AstFactory.newAAnnotatedUnaryExp(exp.getLocation(), annotation, exp);
		}

		return exp;
	}

	private PExp readAnnotatedExpression() throws ParserException, LexException
	{
		ILexCommentList comments = getComments();
		List<PAnnotation> annotations = readAnnotations(comments);
		PExp body = null;

		if (!annotations.isEmpty())
		{
			if (lastToken().isNot(VDMToken.BRA) && isBracketed(annotations))
			{
				warning(5030, "Annotation is not followed by bracketed sub-expression", lastToken().location);
			}

			beforeAnnotations(this, annotations);
			body = readBasicExpression();
			afterAnnotations(this, annotations, body);

			Collections.reverse(annotations);	// Build the chain backwards
			
			for (PAnnotation annotation: annotations)
			{
				body = AstFactory.newAAnnotatedUnaryExp(annotation.getName().getLocation(), annotation, body);
			}
		}
		else
		{
			body = readBasicExpression();
		}
		
		body.setComments(comments);
		return body;
	}

	private boolean isBracketed(List<PAnnotation> annotations)
	{
		for (PAnnotation annotation: annotations)
		{
			if (annotation.getImpl().isBracketed())
			{
				return true;
			}
		}

		return false;
	}

	private PExp readBasicExpression() throws ParserException, LexException
	{
		LexToken token = lastToken();

		switch (token.type)
		{
			case NUMBER:
				nextToken();
				return AstFactory.newAIntLiteralExp((LexIntegerToken) token);

			case REALNUMBER:
				nextToken();
				return AstFactory.newARealLiteralExp((LexRealToken) token);

			case NAME:
				// Includes mk_ constructors
				LexNameToken name = lastNameToken();
				nextToken();
				return AstFactory.newAVariableExp(name);

			case IDENTIFIER:
				// Includes mk_ constructors
				// Note we can't use lastNameToken as this checks that we don't
				// use old~ names.
				LexNameToken id = new LexNameToken(reader.currentModule, (LexIdentifierToken) token);
				nextToken();
				return AstFactory.newAVariableExp(id);

			case STRING:
				nextToken();
				return AstFactory.newAStringLiteralExp((LexStringToken) token);

			case CHARACTER:
				nextToken();
				return AstFactory.newACharLiteralExp((LexCharacterToken) token);

			case QUOTE:
				nextToken();
				return AstFactory.newAQuoteLiteralExp((LexQuoteToken) token);

			case TRUE:
			case FALSE:
				nextToken();
				return AstFactory.newABooleanConstExp((LexBooleanToken) token);

			case UNDEFINED:
				nextToken();
				return AstFactory.newAUndefinedExp(token.location);
				// return new UndefinedExpression(token.location);

			case NIL:
				nextToken();
				return AstFactory.newANilExp(token.location);

			case THREADID:
				nextToken();
				return AstFactory.newAThreadIdExp(token.location);

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
				return AstFactory.newASelfExp(token.location);

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

			case REQ:
			case ACT:
			case FIN:
			case ACTIVE:
			case WAITING:
				return readHistoryExpression(token.location);

			case TIME:
				return readTimeExpression(token.location);

			default:
				throwMessage(2034, "Unexpected token in expression: " + token.type);
				return null;
		}
	}

	private PExp readTimeExpression(ILexLocation location) throws LexException
	{
		nextToken();
		return AstFactory.newATimeExp(location);
	}

	private AMuExp readMuExpression(AVariableExp ve) throws ParserException,
			LexException
	{
		List<ARecordModifier> args = new Vector<ARecordModifier>();
		PExp record = readExpression();

		do
		{
			checkFor(VDMToken.COMMA, 2128, "Expecting comma separated record modifiers");
			LexIdentifierToken id = readIdToken("Expecting <identifier> |-> <expression>", true);
			checkFor(VDMToken.MAPLET, 2129, "Expecting <identifier> |-> <expression>");
			args.add(AstFactory.newARecordModifier(id, readExpression()));
		} while (lastToken().is(VDMToken.COMMA));

		checkFor(VDMToken.KET, 2130, "Expecting ')' after mu maplets");
		return AstFactory.newAMuExp(ve.getLocation(), record, args);
	}

	private PExp readMkExpression(AVariableExp ve) throws ParserException,
			LexException
	{
		List<PExp> args = new NodeList<PExp>(null);

		if (lastToken().isNot(VDMToken.KET)) // NB. mk_T() is legal
		{
			args.add(readExpression());

			while (ignore(VDMToken.COMMA))
			{
				args.add(readExpression());
			}
		}

		checkFor(VDMToken.KET, 2131, "Expecting ')' after mk_ tuple");
		PExp exp = null;

		if (ve.getName().getName().equals("mk_"))
		{
			if (args.size() < 2)
			{
				throwMessage(2035, "Tuple must have >1 argument");
			}

			exp = AstFactory.newATupleExp(ve.getLocation(), args);
		} else
		{
			LexNameToken typename = getMkTypeName(ve.getName());
			VDMToken type = VDMToken.lookup(typename.name, Dialect.VDM_SL);

			if (type != null)
			{
				if (args.size() != 1)
				{
					throwMessage(2300, "mk_<type> must have a single argument");
				}

				PExp value = args.get(0);

				if (type == VDMToken.TOKEN)
				{
					exp = AstFactory.newAMkBasicExp(AstFactory.newATokenBasicType(ve.getLocation()), value);
				}
				else
				{
					throwMessage(2036, "Expecting mk_token");
				}
			} else
			{
				exp = AstFactory.newAMkTypeExp(typename, args);
			}
		}

		return exp;
	}

	private LexNameToken getMkTypeName(ILexNameToken mktoken)
			throws ParserException, LexException
	{
		String typename = mktoken.getName().substring(3); // mk_... or is_...
		String[] parts = typename.split("`");

		switch (parts.length)
		{
			case 1:
				return new LexNameToken(getCurrentModule(), parts[0], mktoken.getLocation());

			case 2:
				return new LexNameToken(parts[0], parts[1], mktoken.getLocation(), false, true);

			default:
				throwMessage(2037, "Malformed mk_<type> name " + typename);
		}

		return null;
	}

	private AIsExp readIsExpression(AVariableExp ve) throws ParserException,
			LexException
	{
		String name = ve.getName().getName();
		AIsExp exp = null;

		if (name.equals("is_"))
		{
			PExp test = readExpression();
			checkFor(VDMToken.COMMA, 2132, "Expecting is_(expression, type)");
			TypeReader tr = getTypeReader();
			PType type = tr.readType();

			if (type instanceof AUnresolvedType)
			{
				AUnresolvedType nt = (AUnresolvedType) type;
				exp = AstFactory.newAIsExp(ve.getLocation(), nt.getName(), test);
			} else
			{
				exp = AstFactory.newAIsExp(ve.getLocation(), type, test);
			}
		} else
		{
			LexNameToken typename = getMkTypeName(ve.getName());
			VDMToken type = VDMToken.lookup(typename.name, Dialect.VDM_SL);

			if (type != null)
			{
				switch (type)
				{
					case BOOL:
						exp = AstFactory.newAIsExp(ve.getLocation(), AstFactory.newABooleanBasicType(ve.getLocation()), readExpression());
						break;

					case NAT:
						exp = AstFactory.newAIsExp(ve.getLocation(), AstFactory.newANatNumericBasicType(ve.getLocation()), readExpression());
						break;

					case NAT1:
						exp = AstFactory.newAIsExp(ve.getLocation(), AstFactory.newANatOneNumericBasicType(ve.getLocation()), readExpression());
						break;

					case INT:
						exp = AstFactory.newAIsExp(ve.getLocation(), AstFactory.newAIntNumericBasicType(ve.getLocation()), readExpression());
						break;

					case RAT:
						exp = AstFactory.newAIsExp(ve.getLocation(), AstFactory.newARationalNumericBasicType(ve.getLocation()), readExpression());
						break;

					case REAL:
						exp = AstFactory.newAIsExp(ve.getLocation(), AstFactory.newARealNumericBasicType(ve.getLocation()), readExpression());
						break;

					case CHAR:
						exp = AstFactory.newAIsExp(ve.getLocation(), AstFactory.newACharBasicType(ve.getLocation()), readExpression());
						break;

					case TOKEN:
						exp = AstFactory.newAIsExp(ve.getLocation(), AstFactory.newATokenBasicType(ve.getLocation()), readExpression());
						break;

					default:
						throwMessage(2038, "Expecting is_<type>");
				}
			} else
			{
				exp = AstFactory.newAIsExp(ve.getLocation(), typename, readExpression());
			}
		}

		checkFor(VDMToken.KET, 2133, "Expecting ')' after is_ expression");
		return exp;
	}

	private PExp readNarrowExpression(AVariableExp ve) throws ParserException,
			LexException
	{
		ANarrowExp exp = null;

		PExp test = readExpression();
		checkFor(VDMToken.COMMA, 2301, "Expecting narrow_(expression, type)");
		TypeReader tr = getTypeReader();
		PType type = tr.readType();

		if (type instanceof AUnresolvedType)
		{
			AUnresolvedType nt = (AUnresolvedType) type;
			exp = AstFactory.newANarrowExpression(ve.getLocation(), nt.getName(), test);
		} else
		{
			exp = AstFactory.newANarrowExpression(ve.getLocation(), type, test);
		}

		checkFor(VDMToken.KET, 2302, "Expecting ')' after narrow_ expression");

		return exp;
	}

	private APreExp readPreExpression(AVariableExp ve) throws ParserException,
			LexException
	{
		List<PExp> args = new Vector<PExp>();
		PExp function = readExpression();

		while (ignore(VDMToken.COMMA))
		{
			args.add(readExpression());
		}

		checkFor(VDMToken.KET, 2134, "Expecting pre_(function [,args])");

		return AstFactory.newAPreExp(ve.getLocation(), function, args);
	}

	private PExp readSetOrMapExpression(ILexLocation start)
			throws ParserException, LexException
	{
		LexToken token = lastToken();

		if (token.is(VDMToken.SET_CLOSE))
		{
			nextToken();
			return AstFactory.newASetEnumSetExp(start); // empty set
		} else if (token.is(VDMToken.MAPLET))
		{
			nextToken();
			checkFor(VDMToken.SET_CLOSE, 2135, "Expecting '}' in empty map");
			return AstFactory.newAMapEnumMapExp(start); // empty map
		}

		PExp first = readExpression();
		token = lastToken();

		if (token.is(VDMToken.MAPLET))
		{
			nextToken();
			AMapletExp maplet = AstFactory.newAMapletExp(first, token, readExpression());
			return readMapExpression(start, maplet);
		} else
		{
			return readSetExpression(start, first);
		}
	}

	private SSetExp readSetExpression(ILexLocation start, PExp first)
			throws ParserException, LexException
	{
		SSetExp result = null;

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
			result = AstFactory.newASetCompSetExp(start, first, bindings, exp);
		} else
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
					return AstFactory.newASetRangeSetExp(start, first, end);
				}

				reader.pop(); // Not a set range then...
			}

			List<PExp> members = new NodeList<PExp>(null);
			members.add(first);

			while (ignore(VDMToken.COMMA))
			{
				members.add(readExpression());
			}

			checkFor(VDMToken.SET_CLOSE, 2139, "Expecting '}' after set enumeration");
			result = AstFactory.newASetEnumSetExp(start, members);
		}

		return result;
	}

	private SMapExp readMapExpression(ILexLocation start, AMapletExp first)
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
			result = AstFactory.newAMapCompMapExp(start, first, bindings, exp);
		} else
		{
			List<AMapletExp> members = new Vector<AMapletExp>();
			members.add(first);

			while (ignore(VDMToken.COMMA))
			{
				PExp member = readExpression();
				LexToken token = lastToken();

				if (token.is(VDMToken.MAPLET))
				{
					nextToken();
					AMapletExp maplet = AstFactory.newAMapletExp(member, token, readExpression());
					members.add(maplet);
				} else
				{
					throwMessage(2039, "Expecting maplet in map enumeration");
				}
			}

			checkFor(VDMToken.SET_CLOSE, 2141, "Expecting '}' after map enumeration");
			result = AstFactory.newAMapEnumMapExp(start, members);
		}

		return result;
	}

	private SSeqExp readSeqExpression(ILexLocation start)
			throws ParserException, LexException
	{
		if (lastToken().is(VDMToken.SEQ_CLOSE))
		{
			nextToken();
			return AstFactory.newASeqEnumSeqExp(start);
		}

		SSeqExp result = null;
		PExp first = readExpression();

		if (lastToken().is(VDMToken.PIPE))
		{
			nextToken();
			BindReader br = getBindReader();
			PBind setbind = br.readBind();
			PExp exp = null;

			if (lastToken().is(VDMToken.AMPERSAND))
			{
				nextToken();
				exp = readExpression();
			}

			checkFor(VDMToken.SEQ_CLOSE, 2142, "Expecting ']' after list comprehension");
			result = AstFactory.newASeqCompSeqExp(start, first, setbind, exp);
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
			result = AstFactory.newASeqEnumSeqExp(start, members);
		}

		return result;
	}

	private AIfExp readIfExpression(ILexLocation start) throws ParserException,
			LexException
	{
		PExp exp = readExpression();
		checkFor(VDMToken.THEN, 2144, "Missing 'then'");
		PExp thenExp = readExpression();
		List<AElseIfExp> elseList = new Vector<AElseIfExp>();

		while (lastToken().is(VDMToken.ELSEIF))
		{
			nextToken();
			elseList.add(readElseIfExpression(lastToken().location));
		}

		PExp elseExp = null;

		if (lastToken().is(VDMToken.ELSE))
		{
			nextToken();
			elseExp = readConnectiveExpression(); // Precedence < maplet?
		} else
		{
			throwMessage(2040, "Expecting 'else' in 'if' expression");
		}

		return AstFactory.newAIfExp(start, exp, thenExp, elseList, elseExp);
	}

	private AElseIfExp readElseIfExpression(ILexLocation start)
			throws ParserException, LexException
	{
		PExp exp = readExpression();
		checkFor(VDMToken.THEN, 2145, "Missing 'then' after 'elseif'");
		PExp thenExp = readExpression();
		return AstFactory.newAElseIfExp(start, exp, thenExp);
	}

	private ACasesExp readCasesExpression(ILexLocation start)
			throws ParserException, LexException
	{
		PExp exp = readExpression();
		checkFor(VDMToken.COLON, 2146, "Expecting ':' after cases expression");

		List<ACaseAlternative> cases = new Vector<ACaseAlternative>();
		PExp others = null;
		cases.addAll(readCaseAlternatives(exp));

		while (lastToken().is(VDMToken.COMMA))
		{
			if (nextToken().is(VDMToken.OTHERS))
			{
				nextToken();
				checkFor(VDMToken.ARROW, 2147, "Expecting '->' after others");
				others = readExpression();
				break;
			} else
			{
				cases.addAll(readCaseAlternatives(exp));
			}
		}

		checkFor(VDMToken.END, 2148, "Expecting ', case alternative' or 'end' after cases");
		return AstFactory.newACasesExp(start, exp, cases, others);
	}

	private List<ACaseAlternative> readCaseAlternatives(PExp exp)
			throws ParserException, LexException
	{
		List<ACaseAlternative> alts = new Vector<ACaseAlternative>();
		List<PPattern> plist = getPatternReader().readPatternList();
		checkFor(VDMToken.ARROW, 2149, "Expecting '->' after case pattern list");
		PExp then = readExpression();

		for (PPattern p : plist)
		{
			alts.add(AstFactory.newACaseAlternative(exp.clone(), p.clone(), then.clone()));
		}

		return alts;
	}

	private PExp readLetExpression(ILexLocation start) throws ParserException,
			LexException
	{
		ParserException letDefError = null;

		try
		{
			reader.push();
			ALetDefExp exp = readLetDefExpression(start);
			reader.unpush();
			return exp;
		} catch (ParserException e)
		{
			e.adjustDepth(reader.getTokensRead());
			reader.pop();
			letDefError = e;
		}

		try
		{
			reader.push();
			ALetBeStExp exp = readLetBeStExpression(start);
			reader.unpush();
			return exp;
		} catch (ParserException e)
		{
			e.adjustDepth(reader.getTokensRead());
			reader.pop();
			throw e.deeperThan(letDefError) ? e : letDefError;
		}
	}

	private ALetDefExp readLetDefExpression(ILexLocation start)
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
		return AstFactory.newALetDefExp(start, localDefs, readConnectiveExpression());
	}

	private ALetBeStExp readLetBeStExpression(ILexLocation start)
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
		return AstFactory.newALetBeStExp(start, bind, stexp, readConnectiveExpression());
	}

	private AForAllExp readForAllExpression(ILexLocation start)
			throws ParserException, LexException
	{
		List<PMultipleBind> bindList = getBindReader().readBindList();
		checkFor(VDMToken.AMPERSAND, 2153, "Expecting '&' after bind list in forall");
		return AstFactory.newAForAllExp(start, bindList, readExpression());
	}

	private AExistsExp readExistsExpression(ILexLocation start)
			throws ParserException, LexException
	{
		List<PMultipleBind> bindList = getBindReader().readBindList();
		checkFor(VDMToken.AMPERSAND, 2154, "Expecting '&' after bind list in exists");
		return AstFactory.newAExistsExp(start, bindList, readExpression());
	}

	private AExists1Exp readExists1Expression(ILexLocation start)
			throws ParserException, LexException
	{
		PBind bind = getBindReader().readBind();
		checkFor(VDMToken.AMPERSAND, 2155, "Expecting '&' after single bind in exists1");
		return AstFactory.newAExists1Exp(start, bind, readExpression());
	}

	private AIotaExp readIotaExpression(ILexLocation start)
			throws ParserException, LexException
	{
		PBind bind = getBindReader().readBind();
		checkFor(VDMToken.AMPERSAND, 2156, "Expecting '&' after single bind in iota");
		return AstFactory.newAIotaExp(start, bind, readExpression());
	}

	private ALambdaExp readLambdaExpression(ILexLocation start)
			throws ParserException, LexException
	{
		List<ATypeBind> bindList = getBindReader().readTypeBindList();
		checkFor(VDMToken.AMPERSAND, 2157, "Expecting '&' after bind list in lambda");
		return AstFactory.newALambdaExp(start, bindList, readExpression());
	}

	private ADefExp readDefExpression(ILexLocation start)
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
		return AstFactory.newADefExp(start, equalsDefs, readExpression());
	}

	private ANewExp readNewExpression(ILexLocation start)
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
		return AstFactory.newANewExp(start, name, args);
	}

	private AIsOfBaseClassExp readIsOfBaseExpression(ILexLocation start)
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

		ILexNameToken classname = ((AVariableExp) args.get(0)).getName();

		if (classname.getOld())
		{
			throwMessage(2295, "Can't use old name here", classname);
		}

		return AstFactory.newAIsOfBaseClassExp(start, classname, args.get(1));
	}

	private AIsOfClassExp readIsOfClassExpression(ILexLocation start)
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

		ILexNameToken classname = ((AVariableExp) args.get(0)).getName();

		if (classname.getOld())
		{
			throwMessage(2295, "Can't use old name here", classname);
		}

		return AstFactory.newAIsOfClassExp(start, classname, args.get(1));
	}

	private ASameBaseClassExp readSameBaseExpression(ILexLocation start)
			throws ParserException, LexException
	{
		checkFor(VDMToken.BRA, 2164, "Expecting '(' after 'samebaseclass'");
		List<PExp> args = readExpressionList();
		checkFor(VDMToken.KET, 2165, "Expecting ')' after 'samebaseclass' args");

		if (args.size() != 2)
		{
			throwMessage(2045, "Expecting two expressions in 'samebaseclass'");
		}

		return AstFactory.newASameBaseClassExp(start, args);
	}

	private ASameClassExp readSameClassExpression(ILexLocation start)
			throws ParserException, LexException
	{
		checkFor(VDMToken.BRA, 2166, "Expecting '(' after 'sameclass'");
		List<PExp> args = readExpressionList();
		checkFor(VDMToken.KET, 2167, "Expecting ')' after 'sameclass' args");

		if (args.size() != 2)
		{
			throwMessage(2046, "Expecting two expressions in 'sameclass'");
		}

		return AstFactory.newASameClassExp(start, args);
	}

	private boolean inPerExpression = false;

	public PExp readPerExpression() throws ParserException, LexException
	{
		inPerExpression = true;
		PExp e = readExpression();
		inPerExpression = false;
		return e;
	}

	private PExp readHistoryExpression(ILexLocation location)
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
				return AstFactory.newAHistoryExp(location, op, opnames);

			default:
				throwMessage(2048, "Expecting #act, #active, #fin, #req or #waiting");
				return null;
		}
	}
}
