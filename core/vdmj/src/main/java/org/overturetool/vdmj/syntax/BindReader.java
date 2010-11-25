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

import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.lex.Token;
import org.overturetool.vdmj.patterns.Bind;
import org.overturetool.vdmj.patterns.MultipleBind;
import org.overturetool.vdmj.patterns.MultipleSetBind;
import org.overturetool.vdmj.patterns.MultipleTypeBind;
import org.overturetool.vdmj.patterns.Pattern;
import org.overturetool.vdmj.patterns.PatternBind;
import org.overturetool.vdmj.patterns.PatternList;
import org.overturetool.vdmj.patterns.SetBind;
import org.overturetool.vdmj.patterns.TypeBind;


/**
 * A syntax analyser to parse set and type binds.
 */

public class BindReader extends SyntaxReader
{
	public BindReader(LexTokenReader reader)
	{
		super(reader);
	}

	public PatternBind readPatternOrBind() throws ParserException, LexException
	{
		ParserException bindError = null;

		try
		{
			reader.push();
			Bind bind = readBind();
			reader.unpush();
			return new PatternBind(bind.location, bind);
		}
		catch (ParserException e)
		{
			e.adjustDepth(reader.getTokensRead());
			reader.pop();
			bindError = e;
		}

		try
		{
			reader.push();
			Pattern p = getPatternReader().readPattern();
			reader.unpush();
			return new PatternBind(p.location, p);
		}
		catch (ParserException e)
		{
			e.adjustDepth(reader.getTokensRead());
			reader.pop();
			throw e.deeperThan(bindError) ? e : bindError;
		}
	}

	public Bind readBind() throws ParserException, LexException
	{
		ParserException setBindError = null;

		try
		{
			reader.push();
			Bind bind = readSetBind();
			reader.unpush();
			return bind;
		}
		catch (ParserException e)
		{
			e.adjustDepth(reader.getTokensRead());
    		reader.pop();
    		setBindError = e;
		}

		try
		{
			reader.push();
			Bind bind = readTypeBind();
			reader.unpush();
			return bind;
		}
		catch (ParserException e)
		{
			e.adjustDepth(reader.getTokensRead());
    		reader.pop();
			throw e.deeperThan(setBindError) ? e : setBindError;
		}
	}

	public SetBind readSetBind() throws LexException, ParserException
	{
		Pattern pattern = getPatternReader().readPattern();
		SetBind sb = null;

		if (lastToken().is(Token.IN))
		{
			if (nextToken().is(Token.SET))
			{
				nextToken();
				sb = new SetBind(pattern, getExpressionReader().readExpression());
			}
			else
			{
				throwMessage(2000, "Expecting 'in set' after pattern in set binding");
			}
		}
		else
		{
			throwMessage(2001, "Expecting 'in set' in set bind");
		}

		return sb;
	}

	public TypeBind readTypeBind() throws LexException, ParserException
	{
		Pattern pattern = getPatternReader().readPattern();
		TypeBind tb = null;

		if (lastToken().is(Token.COLON))
		{
			nextToken();
			tb = new TypeBind(pattern, getTypeReader().readType());
		}
		else
		{
			throwMessage(2002, "Expecting ':' in type bind");
		}

		return tb;
	}

	public List<TypeBind> readTypeBindList() throws ParserException, LexException
	{
		List<TypeBind> list = new Vector<TypeBind>();
		list.add(readTypeBind());

		while (ignore(Token.COMMA))
		{
			list.add(readTypeBind());
		}

		return list;
	}

	public MultipleBind readMultipleBind() throws LexException, ParserException
	{
		PatternList plist = getPatternReader().readPatternList();
		MultipleBind mb = null;

		switch (lastToken().type)
		{
			case IN:
				if (nextToken().is(Token.SET))
				{
					nextToken();
					mb = new MultipleSetBind(
							plist, getExpressionReader().readExpression());
				}
				else
				{
					throwMessage(2003, "Expecting 'in set' after pattern in binding");
				}
				break;

			case COLON:
				nextToken();
				mb = new MultipleTypeBind(plist, getTypeReader().readType());
				break;

			default:
				throwMessage(2004, "Expecting 'in set' or ':' after patterns");
		}

		return mb;
	}

	public List<MultipleBind> readBindList() throws ParserException, LexException
	{
		List<MultipleBind> list = new Vector<MultipleBind>();
		list.add(readMultipleBind());

		while (ignore(Token.COMMA))
		{
			list.add(readMultipleBind());
		}

		return list;
	}
}
