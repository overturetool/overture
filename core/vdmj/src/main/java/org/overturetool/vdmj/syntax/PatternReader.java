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

import org.overturetool.vdmj.lex.LexBooleanToken;
import org.overturetool.vdmj.lex.LexCharacterToken;
import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.lex.LexIdentifierToken;
import org.overturetool.vdmj.lex.LexIntegerToken;
import org.overturetool.vdmj.lex.LexKeywordToken;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.lex.LexQuoteToken;
import org.overturetool.vdmj.lex.LexRealToken;
import org.overturetool.vdmj.lex.LexStringToken;
import org.overturetool.vdmj.lex.LexToken;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.lex.Token;
import org.overturetool.vdmj.patterns.BooleanPattern;
import org.overturetool.vdmj.patterns.CharacterPattern;
import org.overturetool.vdmj.patterns.ConcatenationPattern;
import org.overturetool.vdmj.patterns.ExpressionPattern;
import org.overturetool.vdmj.patterns.IdentifierPattern;
import org.overturetool.vdmj.patterns.IgnorePattern;
import org.overturetool.vdmj.patterns.IntegerPattern;
import org.overturetool.vdmj.patterns.NilPattern;
import org.overturetool.vdmj.patterns.Pattern;
import org.overturetool.vdmj.patterns.PatternList;
import org.overturetool.vdmj.patterns.QuotePattern;
import org.overturetool.vdmj.patterns.RealPattern;
import org.overturetool.vdmj.patterns.RecordPattern;
import org.overturetool.vdmj.patterns.SeqPattern;
import org.overturetool.vdmj.patterns.SetPattern;
import org.overturetool.vdmj.patterns.StringPattern;
import org.overturetool.vdmj.patterns.TuplePattern;
import org.overturetool.vdmj.patterns.UnionPattern;

/**
 * A syntax analyser to parse pattern definitions.
 */

public class PatternReader extends SyntaxReader
{
	public PatternReader(LexTokenReader reader)
	{
		super(reader);
	}

	public Pattern readPattern() throws ParserException, LexException
	{
		Pattern pattern = readSimplePattern();

		while (lastToken().is(Token.UNION) || lastToken().is(Token.CONCATENATE))
		{
			LexToken token = lastToken();

			switch (token.type)
			{
				case UNION:
					nextToken();
					pattern = new UnionPattern(pattern, token.location, readPattern());
					break;

				case CONCATENATE:
					nextToken();
					pattern = new ConcatenationPattern(pattern, token.location, readPattern());
					break;
			}
		}

		return pattern;
	}

	private Pattern readSimplePattern() throws ParserException, LexException
	{
		Pattern pattern = null;
		LexToken token = lastToken();
		boolean rdtok = true;

		switch (token.type)
		{
			case NUMBER:
				pattern = new IntegerPattern((LexIntegerToken)token);
				break;

			case REALNUMBER:
				pattern = new RealPattern((LexRealToken)token);
				break;

			case CHARACTER:
				pattern = new CharacterPattern((LexCharacterToken)token);
				break;

			case STRING:
				pattern = new StringPattern((LexStringToken)token);
				break;

			case QUOTE:
				pattern = new QuotePattern((LexQuoteToken)token);
				break;

			case TRUE:
			case FALSE:
				pattern = new BooleanPattern((LexBooleanToken)token);
				break;

			case NIL:
				pattern = new NilPattern((LexKeywordToken)token);
				break;

			case BRA:
				nextToken();
				ExpressionReader expr = getExpressionReader();
				pattern = new ExpressionPattern(expr.readExpression());
				checkFor(Token.KET, 2180, "Mismatched brackets in pattern");
				rdtok = false;
				break;

			case SET_OPEN:
				if (nextToken().is(Token.SET_CLOSE))
				{
					pattern = new SetPattern(token.location, new PatternList());
				}
				else
				{
					pattern = new SetPattern(token.location, readPatternList());
					checkFor(Token.SET_CLOSE, 2181, "Mismatched braces in pattern");
					rdtok = false;
				}
				break;

			case SEQ_OPEN:
				if (nextToken().is(Token.SEQ_CLOSE))
				{
					pattern = new SeqPattern(token.location, new PatternList());
				}
				else
				{
					pattern = new SeqPattern(token.location, readPatternList());
					checkFor(Token.SEQ_CLOSE, 2182, "Mismatched square brackets in pattern");
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
						checkFor(Token.BRA, 2183, "Expecting '(' after mk_ tuple");
						pattern = new TuplePattern(token.location, readPatternList());
						checkFor(Token.KET, 2184, "Expecting ')' after mk_ tuple");
					}
					else
					{
						checkFor(Token.BRA, 2185, "Expecting '(' after " + id + " record");
						LexNameToken typename = null;
						int backtick = id.name.indexOf('`');

						if (backtick >= 0)
						{
							// Strange case of "mk_MOD`name"
							String mod = id.name.substring(3, backtick);
							String name = id.name.substring(backtick + 1);
							typename = new LexNameToken(mod, name, id.location);
						}
						else
						{
							// Regular case of "mk_Name"
							LexIdentifierToken type = new LexIdentifierToken(
								id.name.substring(3), false, id.location);
							typename = idToName(type);
						}

						if (lastToken().is(Token.KET))
						{
							// An empty pattern list
							pattern = new RecordPattern(typename, new PatternList());
							nextToken();
						}
						else
						{
							pattern = new RecordPattern(typename, readPatternList());
							checkFor(Token.KET, 2186, "Expecting ')' after " + id + " record");
						}
					}

					rdtok = false;
				}
				else
				{
					pattern = new IdentifierPattern(idToName(id));
				}
				break;

			case MINUS:
				pattern = new IgnorePattern(token.location);
				break;

			default:
				throwMessage(2057, "Unexpected token in pattern");
		}

		if (rdtok) nextToken();
		return pattern;
	}

	public PatternList readPatternList() throws ParserException, LexException
	{
		PatternList list = new PatternList();
		list.add(readPattern());

		while (ignore(Token.COMMA))
		{
			list.add(readPattern());
		}

		return list;
	}
}
