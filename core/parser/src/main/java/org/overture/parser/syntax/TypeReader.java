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
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.lex.LexIdentifierToken;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.lex.LexQuoteToken;
import org.overture.ast.lex.LexToken;
import org.overture.ast.lex.VDMToken;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.AVoidType;
import org.overture.ast.types.PField;
import org.overture.ast.types.PType;
import org.overture.parser.lex.LexException;
import org.overture.parser.lex.LexTokenReader;

/**
 * A syntax analyser to parse type expressions.
 */

public class TypeReader extends SyntaxReader
{
	public TypeReader(LexTokenReader reader)
	{
		super(reader);
	}

	public PType readType() throws ParserException, LexException
	{
		PType type = readUnionType();

		if (lastToken().is(VDMToken.ARROW)
				|| lastToken().is(VDMToken.TOTAL_FUNCTION))
		{
			LexToken token = lastToken();
			nextToken();
			PType result = readType();

			if (result instanceof AVoidType)
			{
				throwMessage(2070, "Function type cannot return void type");
			}

			type = AstFactory.newAFunctionType(token.location, token.is(VDMToken.ARROW), productExpand(type), result);
		}

		return type;
	}

	private PType readUnionType() throws ParserException, LexException
	{
		PType type = readProductType();

		while (lastToken().type == VDMToken.PIPE)
		{
			LexToken token = lastToken();
			nextToken();
			type = AstFactory.newAUnionType(token.location, type, readProductType());
		}

		return type;
	}

	private PType readProductType() throws ParserException, LexException
	{
		LexToken token = lastToken();
		PType type = readComposeType();
		List<PType> productList = new ArrayList<PType>();
		productList.add(type);

		while (lastToken().type == VDMToken.TIMES)
		{
			nextToken();
			productList.add(readComposeType());
		}

		if (productList.size() == 1)
		{
			return type;
		}

		return AstFactory.newAProductType(token.location, productList);
	}

	private PType readComposeType() throws ParserException, LexException
	{
		PType type = null;

		if (lastToken().is(VDMToken.COMPOSE))
		{
			nextToken();
			LexIdentifierToken id = readIdToken("Compose not followed by record identifier");
			checkFor(VDMToken.OF, 2249, "Missing 'of' in compose type");
			ARecordInvariantType rtype = AstFactory.newARecordInvariantType(idToName(id), readFieldList());
			rtype.setComposed(true);
			checkFor(VDMToken.END, 2250, "Missing 'end' in compose type");
			type = rtype;
		} else
		{
			type = readMapType();
		}

		return type;
	}

	public List<AFieldField> readFieldList() throws ParserException,
			LexException
	{
		List<AFieldField> list = new ArrayList<AFieldField>();

		while (lastToken().isNot(VDMToken.END)
				&& lastToken().isNot(VDMToken.SEMICOLON)
				&& lastToken().isNot(VDMToken.INV))
		{
			reader.push();
			LexToken tag = lastToken();
			LexToken separator = nextToken();

			if (separator.is(VDMToken.COLON))
			{
				if (tag.isNot(VDMToken.IDENTIFIER))
				{
					throwMessage(2071, "Expecting field identifier before ':'");
				}

				nextToken();
				LexIdentifierToken tagid = (LexIdentifierToken) tag;

				if (tagid.isOld())
				{
					throwMessage(2295, "Can't use old name here", tag);
				}

				LexNameToken tagname = idToName(tagid);
				list.add(AstFactory.newAFieldField(tagname, tagid.getName(), readType(), false));
				reader.unpush();
			} else if (separator.is(VDMToken.EQABST))
			{
				if (tag.isNot(VDMToken.IDENTIFIER))
				{
					throwMessage(2072, "Expecting field name before ':-'");
				}

				nextToken();
				LexIdentifierToken tagid = (LexIdentifierToken) tag;

				if (tagid.isOld())
				{
					throwMessage(2295, "Can't use old name here", tag);
				}

				LexNameToken tagname = idToName(tagid);
				list.add(AstFactory.newAFieldField(tagname, tagid.getName(), readType(), true));
				reader.unpush();
			} else
			// Anonymous field or end of fields
			{
				try
				{
					reader.retry();
					String anon = Integer.toString(list.size() + 1);
					PType ftype = readType();
					LexNameToken tagname = new LexNameToken(getCurrentModule(), anon, ftype.getLocation());
					list.add(AstFactory.newAFieldField(tagname, anon, ftype, false));
					reader.unpush();
				} catch (Exception e)
				{
					// End? EOF? Or badly formed type, fails elsewhere...
					reader.pop();
					break;
				}
			}
		}

		for (PField f1 : list)
		{
			for (PField f2 : list)
			{
				if (f1 != f2
						&& ((AFieldField) f1).getTag().equals(((AFieldField) f2).getTag()))
				{
					throwMessage(2073, "Duplicate field names in record type");
				}
			}
		}

		return list;
	}

	private PType readMapType() throws ParserException, LexException
	{
		PType type = null;
		LexToken token = lastToken();

		switch (token.type)
		{
			case MAP:
				nextToken();
				type = readType(); // Effectively bracketed by 'to'
				checkFor(VDMToken.TO, 2251, "Expecting 'to' in map type");
				type = AstFactory.newAMapMapType(token.location, type, readComposeType());
				break;

			case INMAP:
				nextToken();
				type = readType(); // Effectively bracketed by 'to'
				checkFor(VDMToken.TO, 2252, "Expecting 'to' in inmap type");
				type = AstFactory.newAInMapMapType(token.location, type, readComposeType());
				break;

			default:
				type = readSetSeqType();
				break;
		}

		return type;
	}

	private PType readSetSeqType() throws ParserException, LexException
	{
		PType type = null;
		LexToken token = lastToken();

		switch (token.type)
		{
			case SET:
				nextToken();
				checkFor(VDMToken.OF, 2253, "Expecting 'of' after set");
				type = AstFactory.newASetType(token.location, readComposeType());
				break;

			case SEQ:
				nextToken();
				checkFor(VDMToken.OF, 2254, "Expecting 'of' after seq");
				type = AstFactory.newASeqSeqType(token.location, readComposeType());
				break;

			case SEQ1:
				nextToken();
				checkFor(VDMToken.OF, 2255, "Expecting 'of' after seq1");
				type = AstFactory.newASeq1SeqType(token.location, readComposeType());
				break;

			default:
				type = readBasicType();
				break;
		}

		return type;
	}

	private PType readBasicType() throws ParserException, LexException
	{
		PType type = null;
		LexToken token = lastToken();
		ILexLocation location = token.location;

		switch (token.type)
		{
			case NAT:
				type = AstFactory.newANatNumericBasicType(location);
				nextToken();
				break;

			case NAT1:
				type = AstFactory.newANatOneNumericBasicType(location);
				nextToken();
				break;

			case BOOL:
				type = AstFactory.newABooleanBasicType(location);
				nextToken();
				break;

			case REAL:
				type = AstFactory.newARealNumericBasicType(location);
				nextToken();
				break;

			case INT:
				type = AstFactory.newAIntNumericBasicType(location);
				nextToken();
				break;

			case RAT:
				type = AstFactory.newARationalNumericBasicType(location);
				nextToken();
				break;

			case CHAR:
				type = AstFactory.newACharBasicType(location);
				nextToken();
				break;

			case TOKEN:
				type = AstFactory.newATokenBasicType(location);
				nextToken();
				break;

			case QUOTE:
				type = AstFactory.newAQuoteType((LexQuoteToken) token);
				nextToken();
				break;

			case BRA:
				if (nextToken().is(VDMToken.KET))
				{
					type = AstFactory.newAVoidType(location);
					nextToken();
				} else
				{
					type = AstFactory.newABracketType(location, readType());
					checkFor(VDMToken.KET, 2256, "Bracket mismatch");
				}
				break;

			case SEQ_OPEN:
				nextToken();
				type = AstFactory.newAOptionalType(location, readType());
				checkFor(VDMToken.SEQ_CLOSE, 2257, "Missing close bracket after optional type");
				break;

			case IDENTIFIER:
				LexIdentifierToken id = (LexIdentifierToken) token;
				type = AstFactory.newAUnresolvedType(idToName(id));
				nextToken();
				break;

			case NAME:
				type = AstFactory.newAUnresolvedType((LexNameToken) token);
				nextToken();
				break;

			case AT:
				nextToken();
				type = AstFactory.newAParameterType(idToName(readIdToken("Invalid type parameter")));
				break;

			case QMARK:
				nextToken();
				type = AstFactory.newAUnknownType(location); // Not strictly VDM :-)
				break;

			default:
				throwMessage(2074, "Unexpected token in type expression");
		}

		return type;
	}

	public AOperationType readOperationType() throws ParserException,
			LexException
	{
		PType paramtype = readType();
		LexToken arrow = lastToken();
		checkFor(VDMToken.OPDEF, 2258, "Expecting '==>' in explicit operation type");
		PType resulttype = readType();
		return AstFactory.newAOperationType(arrow.location, productExpand(paramtype), resulttype);
	}

	private List<PType> productExpand(PType parameters)
	{
		List<PType> types = new ArrayList<PType>();

		if (parameters instanceof AProductType)
		{
			// Expand unbracketed product types
			AProductType pt = (AProductType) parameters;
			types.addAll(pt.getTypes());
		} else if (parameters instanceof AVoidType)
		{
			// No type
		} else
		{
			// One parameter, including bracketed product types
			types.add(parameters);
		}

		return types;
	}
}
