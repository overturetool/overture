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
import org.overturetool.vdmj.lex.LexIdentifierToken;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.lex.LexQuoteToken;
import org.overturetool.vdmj.lex.LexToken;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.lex.Token;
import org.overturetool.vdmj.types.BooleanType;
import org.overturetool.vdmj.types.BracketType;
import org.overturetool.vdmj.types.CharacterType;
import org.overturetool.vdmj.types.Field;
import org.overturetool.vdmj.types.FunctionType;
import org.overturetool.vdmj.types.InMapType;
import org.overturetool.vdmj.types.IntegerType;
import org.overturetool.vdmj.types.MapType;
import org.overturetool.vdmj.types.NaturalOneType;
import org.overturetool.vdmj.types.NaturalType;
import org.overturetool.vdmj.types.OperationType;
import org.overturetool.vdmj.types.OptionalType;
import org.overturetool.vdmj.types.ParameterType;
import org.overturetool.vdmj.types.ProductType;
import org.overturetool.vdmj.types.QuoteType;
import org.overturetool.vdmj.types.RationalType;
import org.overturetool.vdmj.types.RealType;
import org.overturetool.vdmj.types.RecordType;
import org.overturetool.vdmj.types.Seq1Type;
import org.overturetool.vdmj.types.SeqType;
import org.overturetool.vdmj.types.SetType;
import org.overturetool.vdmj.types.TokenType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.types.UnionType;
import org.overturetool.vdmj.types.UnknownType;
import org.overturetool.vdmj.types.UnresolvedType;
import org.overturetool.vdmj.types.VoidType;


/**
 * A syntax analyser to parse type expressions.
 */

public class TypeReader extends SyntaxReader
{
	public TypeReader(LexTokenReader reader)
	{
		super(reader);
	}

	public Type readType()
		throws ParserException, LexException
	{
		Type type = readUnionType();

		if (lastToken().is(Token.ARROW) ||
			lastToken().is(Token.TOTAL_FUNCTION))
		{
			LexToken token = lastToken();
			nextToken();
			Type result = readType();

			if (result instanceof VoidType)
			{
				throwMessage(2070, "Function type cannot return void type");
			}

			type = new FunctionType(token.location,
				token.is(Token.ARROW), productExpand(type), result);
		}

		return type;
	}

	private Type readUnionType()
		throws ParserException, LexException
	{
		Type type = readComposeType();

		while (lastToken().type == Token.PIPE)
		{
			LexToken token = lastToken();
			nextToken();
			type = new UnionType(token.location, type, readComposeType());
		}

		return type;
	}

	private Type readComposeType()
		throws ParserException, LexException
	{
		Type type = null;

		if (lastToken().is(Token.COMPOSE))
		{
			nextToken();
			LexIdentifierToken id = readIdToken("Compose not followed by record identifier");
			checkFor(Token.OF, 2249, "Missing 'of' in compose type");
			type = new RecordType(idToName(id), readFieldList());
			checkFor(Token.END, 2250, "Missing 'end' in compose type");
		}
		else
		{
			type = readProductType();
		}

		return type;
	}

	public List<Field> readFieldList()
		throws ParserException, LexException
	{
		List<Field> list = new Vector<Field>();

		while (lastToken().isNot(Token.END) &&
			   lastToken().isNot(Token.SEMICOLON) &&
			   lastToken().isNot(Token.INV))
		{
			reader.push();
			LexToken tag = lastToken();
			LexToken separator = nextToken();

			if (separator.is(Token.COLON))
			{
				if (tag.isNot(Token.IDENTIFIER))
				{
					throwMessage(2071, "Expecting field identifier before ':'");
				}

				nextToken();
				LexIdentifierToken tagid = (LexIdentifierToken)tag;
				LexNameToken tagname = idToName(tagid);
				list.add(new Field(tagname, tagid.name, readType(), false));
				reader.unpush();
			}
			else if (separator.is(Token.EQABST))
			{
				if (tag.isNot(Token.IDENTIFIER))
				{
					throwMessage(2072, "Expecting field name before ':-'");
				}

				nextToken();
				LexIdentifierToken tagid = (LexIdentifierToken)tag;
				LexNameToken tagname = idToName(tagid);
				list.add(new Field(tagname, tagid.name, readType(), true));
				reader.unpush();
			}
			else	// Anonymous field or end of fields
			{
				try
				{
					reader.retry();
					String anon = Integer.toString(list.size() + 1);
					Type ftype = readType();
					LexNameToken tagname = new LexNameToken(
						getCurrentModule(), anon, ftype.location);
					list.add(new Field(tagname, anon, ftype, false));
					reader.unpush();
				}
				catch (Exception e)
				{
					// End? EOF? Or badly formed type, fails elsewhere...
					reader.pop();
					break;
				}
			}
		}

		for (Field f1: list)
		{
			for (Field f2: list)
			{
				if (f1 != f2 && f1.tag.equals(f2.tag))
				{
					throwMessage(2073, "Duplicate field names in record type");
				}
			}
		}

		return list;
	}

	private Type readProductType()
		throws ParserException, LexException
	{
		LexToken token = lastToken();
		Type type = readMapType();
		TypeList productList = new TypeList(type);

		while (lastToken().type == Token.TIMES)
		{
			nextToken();
			productList.add(readMapType());
		}

		if (productList.size() == 1)
		{
			return type;
		}

		return new ProductType(token.location, productList);
	}

	private Type readMapType()
		throws ParserException, LexException
	{
		Type type = null;
		LexToken token = lastToken();

		switch (token.type)
		{
			case MAP:
				nextToken();
				type = readType();	// Effectively bracketed by 'to'
				checkFor(Token.TO, 2251, "Expecting 'to' in map type");
				type = new MapType(token.location, type, readMapType());
				break;

			case INMAP:
				nextToken();
				type = readType();	// Effectively bracketed by 'to'
				checkFor(Token.TO, 2252, "Expecting 'to' in inmap type");
				type = new InMapType(token.location, type, readMapType());
				break;

			default:
				type = readSetSeqType();
				break;
		}

		return type;
	}

	private Type readSetSeqType()
		throws ParserException, LexException
	{
		Type type = null;
		LexToken token = lastToken();

		switch (token.type)
		{
			case SET:
				nextToken();
				checkFor(Token.OF, 2253, "Expecting 'of' after set");
				type = new SetType(token.location, readMapType());
				break;

			case SEQ:
				nextToken();
				checkFor(Token.OF, 2254, "Expecting 'of' after seq");
				type = new SeqType(token.location, readMapType());
				break;

			case SEQ1:
				nextToken();
				checkFor(Token.OF, 2255, "Expecting 'of' after seq1");
				type = new Seq1Type(token.location, readMapType());
				break;

			default:
				type = readBasicType();
				break;
		}

		return type;
	}

	private Type readBasicType()
		throws ParserException, LexException
	{
		Type type = null;
		LexToken token = lastToken();
		LexLocation location = token.location;

		switch (token.type)
		{
			case NAT:
				type = new NaturalType(location);
				nextToken();
				break;

			case NAT1:
				type = new NaturalOneType(location);
				nextToken();
				break;

			case BOOL:
				type = new BooleanType(location);
				nextToken();
				break;

			case REAL:
				type = new RealType(location);
				nextToken();
				break;

			case INT:
				type = new IntegerType(location);
				nextToken();
				break;

			case RAT:
				type = new RationalType(location);
				nextToken();
				break;

			case CHAR:
				type = new CharacterType(location);
				nextToken();
				break;

			case TOKEN:
				type = new TokenType(location);
				nextToken();
				break;

			case QUOTE:
				type = new QuoteType((LexQuoteToken)token);
				nextToken();
				break;

			case BRA:
				if (nextToken().is(Token.KET))
				{
					type = new VoidType(location);
					nextToken();
				}
				else
				{
					type = new BracketType(location, readType());
					checkFor(Token.KET, 2256, "Bracket mismatch");
				}
				break;

			case SEQ_OPEN:
				nextToken();
				type = new OptionalType(location, readType());
				checkFor(Token.SEQ_CLOSE, 2257, "Missing close bracket after optional type");
				break;

			case NIL:
				type = new VoidType(location);
				nextToken();
				break;

			case IDENTIFIER:
				LexIdentifierToken id = (LexIdentifierToken)token;
				type = new UnresolvedType(idToName(id));
				nextToken();
				break;

			case NAME:
				type = new UnresolvedType((LexNameToken)token);
				nextToken();
				break;

			case AT:
				nextToken();
				type = new ParameterType(
						idToName(readIdToken("Invalid type parameter")));
				break;

			case QMARK:
				nextToken();
				type = new UnknownType(location);	// Not strictly VDM :-)
				break;

			default:
				throwMessage(2074, "Unexpected token in type expression");
		}

		return type;
	}

	public OperationType readOperationType()
		throws ParserException, LexException
	{
		Type paramtype = readType();
		LexToken arrow = lastToken();
		checkFor(Token.OPDEF, 2258, "Expecting '==>' in explicit operation type");
		Type resulttype = readType();
		return new OperationType(arrow.location, productExpand(paramtype), resulttype);
	}

	private TypeList productExpand(Type parameters)
	{
		TypeList types = new TypeList();

		if (parameters instanceof ProductType)
		{
			// Expand unbracketed product types
			ProductType pt = (ProductType)parameters;
			types.addAll(pt.types);
		}
		else if (parameters instanceof VoidType)
		{
			// No type
		}
		else
		{
			// One parameter, including bracketed product types
			types.add(parameters);
		}

		return types;
	}
}
