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

import java.util.List;
import java.util.Vector;
import java.util.ArrayList;

import org.overture.ast.factory.AstFactory;
import org.overture.ast.lex.VDMToken;
import org.overture.ast.patterns.ADefPatternBind;
import org.overture.ast.patterns.ASetBind;
import org.overture.ast.patterns.ATypeBind;
import org.overture.ast.patterns.PBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.parser.lex.LexException;
import org.overture.parser.lex.LexTokenReader;

/**
 * A syntax analyser to parse set and type binds.
 */

public class BindReader extends SyntaxReader
{
	public BindReader(LexTokenReader reader)
	{
		super(reader);
	}

	public ADefPatternBind readPatternOrBind() throws ParserException,
			LexException
	{
		ParserException bindError = null;

		try
		{
			reader.push();
			PBind bind = readBind();
			reader.unpush();
			return AstFactory.newADefPatternBind(bind.getLocation(), bind);
		} catch (ParserException e)
		{
			e.adjustDepth(reader.getTokensRead());
			reader.pop();
			bindError = e;
		}

		try
		{
			reader.push();
			PPattern p = getPatternReader().readPattern();
			reader.unpush();
			return AstFactory.newADefPatternBind(p.getLocation(), p);
		} catch (ParserException e)
		{
			e.adjustDepth(reader.getTokensRead());
			reader.pop();
			throw e.deeperThan(bindError) ? e : bindError;
		}
	}

	public PBind readBind() throws ParserException, LexException
	{
		ParserException setBindError = null;

		try
		{
			reader.push();
			PBind bind = readSetBind();
			reader.unpush();
			return bind;
		} catch (ParserException e)
		{
			e.adjustDepth(reader.getTokensRead());
			reader.pop();
			setBindError = e;
		}

		try
		{
			reader.push();
			PBind bind = readTypeBind();
			reader.unpush();
			return bind;
		} catch (ParserException e)
		{
			e.adjustDepth(reader.getTokensRead());
			reader.pop();
			throw e.deeperThan(setBindError) ? e : setBindError;
		}
	}

	public ASetBind readSetBind() throws LexException, ParserException
	{
		PPattern pattern = getPatternReader().readPattern();
		ASetBind sb = null;

		if (lastToken().is(VDMToken.IN))
		{
			if (nextToken().is(VDMToken.SET))
			{
				nextToken();
				sb = AstFactory.newASetBind(pattern, getExpressionReader().readExpression());
			} else
			{
				throwMessage(2000, "Expecting 'in set' after pattern in set binding");
			}
		} else
		{
			throwMessage(2001, "Expecting 'in set' in set bind");
		}

		return sb;
	}

	public ATypeBind readTypeBind() throws LexException, ParserException
	{
		PPattern pattern = getPatternReader().readPattern();
		ATypeBind tb = null;

		if (lastToken().is(VDMToken.COLON))
		{
			nextToken();
			tb = AstFactory.newATypeBind(pattern, getTypeReader().readType());
		} else
		{
			throwMessage(2002, "Expecting ':' in type bind");
		}

		return tb;
	}

	public List<ATypeBind> readTypeBindList() throws ParserException,
			LexException
	{
		List<ATypeBind> list = new ArrayList<ATypeBind>();
		list.add(readTypeBind());

		while (ignore(VDMToken.COMMA))
		{
			list.add(readTypeBind());
		}

		return list;
	}

	public PMultipleBind readMultipleBind() throws LexException,
			ParserException
	{
		List<PPattern> plist = getPatternReader().readPatternList();
		PMultipleBind mb = null;

		switch (lastToken().type)
		{
			case IN:
				if (nextToken().is(VDMToken.SET))
				{
					nextToken();
					mb = AstFactory.newASetMultipleBind(plist, getExpressionReader().readExpression());
				} else
				{
					throwMessage(2003, "Expecting 'in set' after pattern in binding");
				}
				break;

			case COLON:
				nextToken();
				mb = AstFactory.newATypeMultipleBind(plist, getTypeReader().readType());
				break;

			default:
				throwMessage(2004, "Expecting 'in set' or ':' after patterns");
		}

		return mb;
	}

	public List<PMultipleBind> readBindList() throws ParserException,
			LexException
	{
		List<PMultipleBind> list = new ArrayList<PMultipleBind>();
		list.add(readMultipleBind());

		while (ignore(VDMToken.COMMA))
		{
			list.add(readMultipleBind());
		}

		return list;
	}
}
