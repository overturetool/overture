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

import org.overture.ast.node.tokens.TBool;
import org.overture.ast.node.tokens.TChar;
import org.overture.ast.node.tokens.TStringLiteral;
import org.overture.ast.node.tokens.TTokenLiteral;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.ABracketType;
import org.overture.ast.types.ACharBasicType;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AInMapType;
import org.overture.ast.types.AIntNumericBasicType;
import org.overture.ast.types.AMapType;
import org.overture.ast.types.ANatNumericBasicType;
import org.overture.ast.types.ANatOneNumericBasicType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AParameterType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.AQuoteType;
import org.overture.ast.types.ARationalNumericBasicType;
import org.overture.ast.types.ARealNumericBasicType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.ASeq1Type;
import org.overture.ast.types.ASeqType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.ATokenBasicType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.AUnresolvedType;
import org.overture.ast.types.AVoidType;
import org.overture.ast.types.PField;
import org.overture.ast.types.PType;
import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.lex.LexIdentifierToken;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.lex.LexQuoteToken;
import org.overturetool.vdmj.lex.LexToken;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.lex.VDMToken;




/**
 * A syntax analyser to parse type expressions.
 */

public class TypeReader extends SyntaxReader
{
	public TypeReader(LexTokenReader reader)
	{
		super(reader);
	}

	public PType readType()
		throws ParserException, LexException
	{
		PType type = readUnionType();

		if (lastToken().is(VDMToken.ARROW) ||
			lastToken().is(VDMToken.TOTAL_FUNCTION))
		{
			LexToken token = lastToken();
			nextToken();
			PType result = readType();

			if (result instanceof AVoidType)
			{
				throwMessage(2070, "Function type cannot return void type");
			}

			type = new AFunctionType(token.location,
				token.is(VDMToken.ARROW), productExpand(type), result);
		}

		return type;
	}

	private PType readUnionType()
		throws ParserException, LexException
	{
		PType type = readComposeType();

		while (lastToken().type == VDMToken.PIPE)
		{
			LexToken token = lastToken();
			nextToken();
			List<PType> list = new Vector<PType>();
			list.add(type);
			list.add(readComposeType());
			type = new AUnionType(token.location, list);
		}

		return type;
	}

	private PType readComposeType()
		throws ParserException, LexException
	{
		PType type = null;

		if (lastToken().is(VDMToken.COMPOSE))
		{
			nextToken();
			LexIdentifierToken id = readIdToken("Compose not followed by record identifier");
			checkFor(VDMToken.OF, 2249, "Missing 'of' in compose type");
			type = new ARecordInvariantType(id.location,idToName(id), readFieldList(),null);
			checkFor(VDMToken.END, 2250, "Missing 'end' in compose type");
		}
		else
		{
			type = readProductType();
		}

		return type;
	}

	public List<PField> readFieldList()
		throws ParserException, LexException
	{
		List<PField> list = new Vector<PField>();

		while (lastToken().isNot(VDMToken.END) &&
			   lastToken().isNot(VDMToken.SEMICOLON) &&
			   lastToken().isNot(VDMToken.INV))
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
				LexIdentifierToken tagid = (LexIdentifierToken)tag;

				if (tagid.isOld())
				{
					throwMessage(2295, "Can't use old name here", tag);
				}
				
				LexNameToken tagname = idToName(tagid);
				list.add(new AFieldField(null,null,null,tagname, tagid.getName(), readType(), false));
				reader.unpush();
			}
			else if (separator.is(VDMToken.EQABST))
			{
				if (tag.isNot(VDMToken.IDENTIFIER))
				{
					throwMessage(2072, "Expecting field name before ':-'");
				}

				nextToken();
				LexIdentifierToken tagid = (LexIdentifierToken)tag;

				if (tagid.isOld())
				{
					throwMessage(2295, "Can't use old name here", tag);
				}

				LexNameToken tagname = idToName(tagid);
				list.add(new AFieldField(null,null,null,tagname, tagid.getName(), readType(), true));
				reader.unpush();
			}
			else	// Anonymous field or end of fields
			{
				try
				{
					reader.retry();
					String anon = Integer.toString(list.size() + 1);
					PType ftype = readType();
					LexNameToken tagname = new LexNameToken(
						getCurrentModule(), anon, ftype.getLocation());
					list.add(new AFieldField(null,null,null,tagname, anon, ftype, false));
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

		for (PField f1: list)
		{
			for (PField f2: list)
			{
				if (f1 != f2 && ((AFieldField)f1).getTag().equals(((AFieldField)f2).getTag()))//TODO unsafe cast
				{
					throwMessage(2073, "Duplicate field names in record type");
				}
			}
		}

		return list;
	}

	private PType readProductType()
		throws ParserException, LexException
	{
		LexToken token = lastToken();
		PType type = readMapType();
		List<PType> productList = new Vector<PType>();
		productList.add(type);

		while (lastToken().type == VDMToken.TIMES)
		{
			nextToken();
			productList.add(readMapType());
		}

		if (productList.size() == 1)
		{
			return type;
		}

		return new AProductType(token.location, productList);
	}

	private PType readMapType()
		throws ParserException, LexException
	{
		PType type = null;
		LexToken token = lastToken();

		switch (token.type)
		{
			case MAP:
				nextToken();
				type = readType();	// Effectively bracketed by 'to'
				checkFor(VDMToken.TO, 2251, "Expecting 'to' in map type");
				type = new AMapType(token.location, type, readMapType(),false);
				break;

			case INMAP:
				nextToken();
				type = readType();	// Effectively bracketed by 'to'
				checkFor(VDMToken.TO, 2252, "Expecting 'to' in inmap type");
				type = new AInMapType(token.location, type, readMapType());
				break;

			default:
				type = readSetSeqType();
				break;
		}

		return type;
	}

	private PType readSetSeqType()
		throws ParserException, LexException
	{
		PType type = null;
		LexToken token = lastToken();

		switch (token.type)
		{
			case SET:
				nextToken();
				checkFor(VDMToken.OF, 2253, "Expecting 'of' after set");
				type = new ASetType(token.location, readMapType(),false);
				break;

			case SEQ:
				nextToken();
				checkFor(VDMToken.OF, 2254, "Expecting 'of' after seq");
				type = new ASeqType(token.location, readMapType(),false);
				break;

			case SEQ1:
				nextToken();
				checkFor(VDMToken.OF, 2255, "Expecting 'of' after seq1");
				type = new ASeq1Type(token.location, readMapType(),false);
				break;

			default:
				type = readBasicType();
				break;
		}

		return type;
	}

	private PType readBasicType()
		throws ParserException, LexException
	{
		PType type = null;
		LexToken token = lastToken();
		LexLocation location = token.location;

		switch (token.type)
		{
			case NAT:
				type = new ANatNumericBasicType(location);
				nextToken();
				break;

			case NAT1:
				type = new ANatOneNumericBasicType(location);
				nextToken();
				break;

			case BOOL:
				type = new ABooleanBasicType(location);
				nextToken();
				break;

			case REAL:
				type = new ARealNumericBasicType(location);
				nextToken();
				break;

			case INT:
				type = new AIntNumericBasicType(location);
				nextToken();
				break;

			case RAT:
				type = new ARationalNumericBasicType(location);
				nextToken();
				break;

			case CHAR:
				type = new ACharBasicType(location);
				nextToken();
				break;

			case TOKEN:
				type = new ATokenBasicType(location);
				nextToken();
				break;

			case QUOTE:
				type = new AQuoteType(location, new TStringLiteral( ((LexQuoteToken)token).value));//(LexQuoteToken)token);
				nextToken();
				break;

			case BRA:
				if (nextToken().is(VDMToken.KET))
				{
					type = new AVoidType(location);
					nextToken();
				}
				else
				{
					type = new ABracketType(location, readType());
					checkFor(VDMToken.KET, 2256, "Bracket mismatch");
				}
				break;

			case SEQ_OPEN:
				nextToken();
				type = new AOptionalType(location, readType());
				checkFor(VDMToken.SEQ_CLOSE, 2257, "Missing close bracket after optional type");
				break;

			case NIL:
				type = new AVoidType(location);
				nextToken();
				break;

			case IDENTIFIER:
				LexIdentifierToken id = (LexIdentifierToken)token;
				type = new AUnresolvedType(location,idToName(id));
				nextToken();
				break;

			case NAME:
				type = new AUnresolvedType(location,(LexNameToken)token);
				nextToken();
				break;

			case AT:
				nextToken();
				type = new AParameterType(location,
						idToName(readIdToken("Invalid type parameter")));
				break;

			case QMARK:
				nextToken();
				type = new AUnknownType(location);	// Not strictly VDM :-)
				break;

			default:
				throwMessage(2074, "Unexpected token in type expression");
		}

		return type;
	}

	public AOperationType readOperationType()
		throws ParserException, LexException
	{
		PType paramtype = readType();
		LexToken arrow = lastToken();
		checkFor(VDMToken.OPDEF, 2258, "Expecting '==>' in explicit operation type");
		PType resulttype = readType();
		return new AOperationType(arrow.location, productExpand(paramtype), resulttype);
	}

	private List<PType> productExpand(PType parameters)
	{
		List<PType> types = new Vector<PType>();

		if (parameters instanceof AProductType)
		{
			// Expand unbracketed product types
			AProductType pt = (AProductType)parameters;
			types.addAll(pt.getTypes());
		}
		else if (parameters instanceof AVoidType)
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
