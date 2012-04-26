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

package org.overturetool.vdmjV2.syntax;

import java.util.List;
import java.util.Vector;

import org.overture.ast.expressions.PExp;
import org.overture.ast.patterns.ABooleanPattern;
import org.overture.ast.patterns.ACharacterPattern;
import org.overture.ast.patterns.AConcatenationPattern;
import org.overture.ast.patterns.AExpressionPattern;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.AIgnorePattern;
import org.overture.ast.patterns.AIntegerPattern;
import org.overture.ast.patterns.ANilPattern;
import org.overture.ast.patterns.AQuotePattern;
import org.overture.ast.patterns.ARealPattern;
import org.overture.ast.patterns.ARecordPattern;
import org.overture.ast.patterns.ASeqPattern;
import org.overture.ast.patterns.ASetPattern;
import org.overture.ast.patterns.AStringPattern;
import org.overture.ast.patterns.ATuplePattern;
import org.overture.ast.patterns.AUnionPattern;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.AUnresolvedType;
import org.overturetool.vdmjV2.lex.LexBooleanToken;
import org.overturetool.vdmjV2.lex.LexCharacterToken;
import org.overturetool.vdmjV2.lex.LexException;
import org.overturetool.vdmjV2.lex.LexIdentifierToken;
import org.overturetool.vdmjV2.lex.LexIntegerToken;
import org.overturetool.vdmjV2.lex.LexNameToken;
import org.overturetool.vdmjV2.lex.LexQuoteToken;
import org.overturetool.vdmjV2.lex.LexRealToken;
import org.overturetool.vdmjV2.lex.LexStringToken;
import org.overturetool.vdmjV2.lex.LexToken;
import org.overturetool.vdmjV2.lex.LexTokenReader;
import org.overturetool.vdmjV2.lex.VDMToken;

/**
 * A syntax analyser to parse pattern definitions.
 */

public class PatternReader extends SyntaxReader
{
	public PatternReader(LexTokenReader reader)
	{
		super(reader);
	}

	public PPattern readPattern() throws ParserException, LexException
	{
		PPattern pattern = readSimplePattern();

		while (lastToken().is(VDMToken.UNION)
				|| lastToken().is(VDMToken.CONCATENATE))
		{
			LexToken token = lastToken();

			switch (token.type)
			{
				case UNION:
					nextToken();
					pattern = new AUnionPattern(token.location, null, false, pattern, readPattern());
					// pattern = new UnionPattern(pattern, token.location, readPattern());
					break;

				case CONCATENATE:
					nextToken();
					pattern = new AConcatenationPattern(token.location, null, false, pattern, readPattern());
					// pattern = new ConcatenationPattern(pattern, token.location, readPattern());
					break;
			}
		}

		return pattern;
	}

	private PPattern readSimplePattern() throws ParserException, LexException
	{
		PPattern pattern = null;
		LexToken token = lastToken();
		boolean rdtok = true;

		switch (token.type)
		{
			case NUMBER:
				pattern = new AIntegerPattern(token.location, null, false, (LexIntegerToken) token);
				// pattern = new IntegerPattern((LexIntegerToken)token);
				break;

			case REALNUMBER:
				pattern = new ARealPattern(token.location, null, false, (LexRealToken) token);
				// pattern = new RealPattern((LexRealToken)token);
				break;

			case CHARACTER:
				pattern = new ACharacterPattern(token.location, null, false, (LexCharacterToken) token);
				// pattern = new CharacterPattern((LexCharacterToken)token);
				break;

			case STRING:
				pattern = new AStringPattern(token.location, null, false, (LexStringToken) token);
				// pattern = new StringPattern((LexStringToken)token);
				break;

			case QUOTE:
				pattern = new AQuotePattern(token.location, null, false, (LexQuoteToken) token);
				// pattern = new QuotePattern((LexQuoteToken)token);
				break;

			case TRUE:
			case FALSE:
				pattern = new ABooleanPattern(token.location, null, false, (LexBooleanToken) token);
				// pattern = new ABooleanPattern(token.location,null,(LexBooleanToken)token);
				break;

			case NIL:
				pattern = new ANilPattern(token.location, null, false);
				// pattern = new NilPattern((LexKeywordToken)token);
				break;

			case BRA:
				nextToken();
				ExpressionReader expr = getExpressionReader();
				PExp exp = expr.readExpression();
				pattern = new AExpressionPattern(exp.getLocation(), null, false,exp );
				// pattern = new ExpressionPattern(expr.readExpression());
				checkFor(VDMToken.KET, 2180, "Mismatched brackets in pattern");
				rdtok = false;
				break;

			case SET_OPEN:
				if (nextToken().is(VDMToken.SET_CLOSE))
				{
					pattern = new ASetPattern(token.location, null, false, new Vector<PPattern>());
					// pattern = new SetPattern(token.location, new PatternList());
				} else
				{
					pattern = new ASetPattern(token.location, null, false, readPatternList());
					// pattern = new SetPattern(token.location, readPatternList());
					checkFor(VDMToken.SET_CLOSE, 2181, "Mismatched braces in pattern");
					rdtok = false;
				}
				break;

			case SEQ_OPEN:
				if (nextToken().is(VDMToken.SEQ_CLOSE))
				{
					pattern = new ASeqPattern(token.location, null, false, new Vector<PPattern>());
					// pattern = new SeqPattern(token.location, new PatternList());
				} else
				{
					pattern = new ASeqPattern(token.location, null, false, readPatternList());
					// pattern = new SeqPattern(token.location, readPatternList());
					checkFor(VDMToken.SEQ_CLOSE, 2182, "Mismatched square brackets in pattern");
					rdtok = false;
				}
				break;

			case NAME:
				throwMessage(2056, "Cannot use module'id name in patterns");
				break;

			case IDENTIFIER:
				LexIdentifierToken id = lastIdToken();

				if (id.name.startsWith("mk_"))
				{
					nextToken();

					if (id.name.equals("mk_"))
					{
						checkFor(VDMToken.BRA, 2183, "Expecting '(' after mk_ tuple");
						pattern = new ATuplePattern(token.location, null, false, readPatternList());
						// pattern = new TuplePattern(token.location, readPatternList());
						checkFor(VDMToken.KET, 2184, "Expecting ')' after mk_ tuple");
					} else
					{
						checkFor(VDMToken.BRA, 2185, "Expecting '(' after "
								+ id + " record");
						LexNameToken typename = null;
						int backtick = id.name.indexOf('`');

						if (backtick >= 0)
						{
							// Strange case of "mk_MOD`name"
							String mod = id.name.substring(3, backtick);
							String name = id.name.substring(backtick + 1);
							typename = new LexNameToken(mod, name, id.location);
						} else
						{
							// Regular case of "mk_Name"
							LexIdentifierToken type = new LexIdentifierToken(id.name.substring(3), false, id.location);
							typename = idToName(type);
						}

						if (lastToken().is(VDMToken.KET))
						{
							// An empty pattern list
							pattern = new ARecordPattern(token.location, null, false, typename, new Vector<PPattern>(), new AUnresolvedType(token.location, false, null, typename));
							// pattern = new RecordPattern(typename, new PatternList());
							nextToken();
						} else
						{
							pattern = new ARecordPattern(token.location, null, false, typename, readPatternList(), new AUnresolvedType(token.location, false, null, typename));
							// pattern = new RecordPattern(typename, readPatternList());
							checkFor(VDMToken.KET, 2186, "Expecting ')' after "
									+ id + " record");
						}
					}

					rdtok = false;
				} else
				{
					pattern = new AIdentifierPattern(token.location, null, false, idToName(id));
					// pattern = new IdentifierPattern(idToName(id));
				}
				break;

			case MINUS:
				pattern = new AIgnorePattern(token.location, null, false);
				// pattern = new IgnorePattern(token.location);
				break;

			default:
				throwMessage(2057, "Unexpected token in pattern");
		}

		if (rdtok)
			nextToken();
		return pattern;
	}

	public List<PPattern> readPatternList() throws ParserException,
			LexException
	{
		List<PPattern> list = new Vector<PPattern>();
		list.add(readPattern());

		while (ignore(VDMToken.COMMA))
		{
			list.add(readPattern());
		}

		return list;
	}
}
