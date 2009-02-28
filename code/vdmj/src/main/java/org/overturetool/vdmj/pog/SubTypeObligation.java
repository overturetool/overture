/*******************************************************************************
 *
 *	Copyright (C) 2008 Fujitsu Services Ltd.
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

package org.overturetool.vdmj.pog;

import java.util.Iterator;

import org.overturetool.vdmj.definitions.ExplicitOperationDefinition;
import org.overturetool.vdmj.definitions.ImplicitOperationDefinition;
import org.overturetool.vdmj.expressions.BooleanLiteralExpression;
import org.overturetool.vdmj.expressions.CharLiteralExpression;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.expressions.ExpressionList;
import org.overturetool.vdmj.expressions.IntegerLiteralExpression;
import org.overturetool.vdmj.expressions.MapEnumExpression;
import org.overturetool.vdmj.expressions.MapletExpression;
import org.overturetool.vdmj.expressions.MkTypeExpression;
import org.overturetool.vdmj.expressions.RealLiteralExpression;
import org.overturetool.vdmj.expressions.SeqEnumExpression;
import org.overturetool.vdmj.expressions.SetEnumExpression;
import org.overturetool.vdmj.expressions.SetRangeExpression;
import org.overturetool.vdmj.expressions.SubseqExpression;
import org.overturetool.vdmj.expressions.TupleExpression;
import org.overturetool.vdmj.expressions.VariableExpression;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.patterns.IdentifierPattern;
import org.overturetool.vdmj.patterns.Pattern;
import org.overturetool.vdmj.patterns.TuplePattern;
import org.overturetool.vdmj.typechecker.TypeComparator;
import org.overturetool.vdmj.types.BasicType;
import org.overturetool.vdmj.types.BooleanType;
import org.overturetool.vdmj.types.BracketType;
import org.overturetool.vdmj.types.CharacterType;
import org.overturetool.vdmj.types.Field;
import org.overturetool.vdmj.types.IntegerType;
import org.overturetool.vdmj.types.InvariantType;
import org.overturetool.vdmj.types.MapType;
import org.overturetool.vdmj.types.NamedType;
import org.overturetool.vdmj.types.NaturalOneType;
import org.overturetool.vdmj.types.NaturalType;
import org.overturetool.vdmj.types.NumericType;
import org.overturetool.vdmj.types.OptionalType;
import org.overturetool.vdmj.types.ProductType;
import org.overturetool.vdmj.types.RecordType;
import org.overturetool.vdmj.types.Seq1Type;
import org.overturetool.vdmj.types.SeqType;
import org.overturetool.vdmj.types.SetType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeSet;
import org.overturetool.vdmj.types.UnionType;

public class SubTypeObligation extends ProofObligation
{
	public SubTypeObligation(
		Expression exp, Type etype, Type atype, POContextStack ctxt)
	{
		super(exp.location, POType.SUB_TYPE, ctxt);
		value = ctxt.getObligation(oneType(false, exp, etype, atype));
		return;
	}

	public SubTypeObligation(
		ExplicitOperationDefinition def, Type actualResult, POContextStack ctxt)
	{
		super(def.location, POType.SUB_TYPE, ctxt);

		VariableExpression result = new VariableExpression(
			new LexNameToken(def.name.module, "RESULT", def.location));

		value = ctxt.getObligation(
			oneType(false, result, def.type.result, actualResult));
	}

	public SubTypeObligation(
		ImplicitOperationDefinition def, Type actualResult, POContextStack ctxt)
	{
		super(def.location, POType.SUB_TYPE, ctxt);
		Expression result = null;

		if (def.result.pattern instanceof IdentifierPattern)
		{
			IdentifierPattern ip = (IdentifierPattern)def.result.pattern;
			result = new VariableExpression(ip.name);
		}
		else
		{
			TuplePattern tp = (TuplePattern)def.result.pattern;
			ExpressionList args = new ExpressionList();

			for (Pattern p: tp.plist)
			{
				IdentifierPattern ip = (IdentifierPattern)p;
				args.add(new VariableExpression(ip.name));
			}

			result = new TupleExpression(def.location, args);
		}

		value = ctxt.getObligation(
			oneType(false, result, def.type.result, actualResult));
	}

	private String oneType(boolean rec, Expression exp, Type etype, Type atype)
	{
		if (atype != null && rec)
		{
			if (TypeComparator.isSubType(atype, etype))
			{
				return "";		// A sub comparison is OK without checks
			}
		}

		StringBuilder sb = new StringBuilder();
		String prefix = "";
		boolean resolved = false;

		while (!resolved)
		{
    		if (etype instanceof BracketType)
    		{
    			etype = ((BracketType)etype).type;
    			continue;
    		}

    		if (etype instanceof OptionalType)
    		{
    			etype = ((OptionalType)etype).type;
    			continue;
    		}

    		resolved = true;
		}

		if (etype instanceof UnionType)
		{
			UnionType ut = (UnionType)etype;
			TypeSet possibles = new TypeSet();

			for (Type pos: ut.types)
			{
				if (atype == null || TypeComparator.compatible(pos, atype))
				{
					possibles.add(pos);
				}
			}

			prefix = "";

			for (Type poss: possibles)
			{
				String s = oneType(true, exp, poss, null);

				sb.append(prefix);
				sb.append("(");
				addIs(sb, exp, poss);

				if (s.length() > 0 &&
					!s.startsWith("is_(") && !s.startsWith("(is_("))
				{
					sb.append(" and ");
					sb.append(s);
				}

				sb.append(")");
				prefix = " or\n";
			}
		}
		else if (etype instanceof InvariantType)
		{
			InvariantType et = (InvariantType)etype;
			prefix = "";

			if (et.invdef != null)
			{
    			sb.append(et.invdef.name.name);
    			sb.append("(");
    			sb.append(exp);
    			sb.append(")");
    			prefix = " and ";
			}

			if (etype instanceof NamedType)
			{
				NamedType nt = (NamedType)etype;

				if (atype instanceof NamedType)
				{
					atype = ((NamedType)atype).type;
				}
				else
				{
					atype = null;
				}

				String s = oneType(true, exp, nt.type, atype);

				if (s.length() > 0)
				{
					sb.append(prefix);
					sb.append("(");
					sb.append(s);
					sb.append(")");
				}
			}
			else if (etype instanceof RecordType)
			{
				if (exp instanceof MkTypeExpression)
				{
					RecordType rt = (RecordType)etype;
					MkTypeExpression mk = (MkTypeExpression)exp;
					Iterator<Field> fit = rt.fields.iterator();
					Iterator<Type> ait = mk.argTypes.iterator();

					for (Expression e: mk.args)
					{
						String s = oneType(true, e, fit.next().type, ait.next());

						if (s.length() > 0)
						{
							sb.append(prefix);
							sb.append("(");
							sb.append(s);
							sb.append(")");
							prefix = "\nand ";
						}
					}
				}
				else
				{
					sb.append(prefix);
					addIs(sb, exp, etype);
				}
			}
			else
			{
				sb.append(prefix);
				addIs(sb, exp, etype);
			}
		}
		else if (etype instanceof SeqType)
		{
			prefix = "";

			if (etype instanceof Seq1Type)
			{
    			sb.append(exp);
    			sb.append(" <> []");
    			prefix = " and ";
			}

			if (exp instanceof SeqEnumExpression)
			{
				SeqType stype = (SeqType)etype;
				SeqEnumExpression seq = (SeqEnumExpression)exp;
				Iterator<Type> it = seq.types.iterator();

				for (Expression m: seq.members)
				{
					String s = oneType(true, m, stype.seqof, it.next());

					if (s.length() > 0)
					{
						sb.append(prefix);
						sb.append("(");
						sb.append(s);
						sb.append(")");
						prefix = "\nand ";
					}
				}
			}
			else if (exp instanceof SubseqExpression)
			{
				SubseqExpression subseq = (SubseqExpression)exp;
				Type itype = new NaturalOneType(exp.location);
				String s = oneType(true, subseq.from, itype, subseq.ftype);

				if (s.length() > 0)
				{
					sb.append("(");
					sb.append(s);
					sb.append(")");
					sb.append(" and ");
				}

				s = oneType(true, subseq.to, itype, subseq.ttype);

				if (s.length() > 0)
				{
					sb.append("(");
					sb.append(s);
					sb.append(")");
					sb.append(" and ");
				}

				sb.append(subseq.to);
				sb.append(" <= len ");
				sb.append(subseq.seq);

				sb.append(" and ");
				addIs(sb, exp, etype);		// Like set range does
			}
			else
			{
				sb = new StringBuilder();	// remove any "x <> []"
				addIs(sb, exp, etype);
			}
		}
		else if (etype instanceof MapType)
		{
			if (exp instanceof MapEnumExpression)
			{
				MapType mtype = (MapType)etype;
				MapEnumExpression seq = (MapEnumExpression)exp;
				Iterator<Type> dit = seq.domtypes.iterator();
				Iterator<Type> rit = seq.rngtypes.iterator();
				prefix = "";

				for (MapletExpression m: seq.members)
				{
					String s = oneType(true, m.left, mtype.from, dit.next());

					if (s.length() > 0)
					{
						sb.append(prefix);
						sb.append("(");
						sb.append(s);
						sb.append(")");
						prefix = "\nand ";
					}

					s = oneType(true, m.right, mtype.to, rit.next());

					if (s.length() > 0)
					{
						sb.append(prefix);
						sb.append("(");
						sb.append(s);
						sb.append(")");
						prefix = "\nand ";
					}
				}
			}
			else
			{
				addIs(sb, exp, etype);
			}
		}
		else if (etype instanceof SetType)
		{
			if (exp instanceof SetEnumExpression)
			{
				SetType stype = (SetType)etype;
				SetEnumExpression set = (SetEnumExpression)exp;
				Iterator<Type> it = set.types.iterator();
				prefix = "";

				for (Expression m: set.members)
				{
					String s = oneType(true, m, stype.setof, it.next());

					if (s.length() > 0)
					{
						sb.append(prefix);
						sb.append("(");
						sb.append(s);
						sb.append(")");
						prefix = "\nand ";
					}
				}

				sb.append("\nand ");
			}
			else if (exp instanceof SetRangeExpression)
			{
				SetType stype = (SetType)etype;
				SetRangeExpression range = (SetRangeExpression)exp;
				Type itype = new IntegerType(exp.location);
				prefix = "";

				String s = oneType(true, range.first, itype, range.ftype);

				if (s.length() > 0)
				{
					sb.append(prefix);
					sb.append("(");
					sb.append(s);
					sb.append(")");
					prefix = "\nand ";
				}

				s = oneType(true, range.first, stype.setof, range.ftype);

				if (s.length() > 0)
				{
					sb.append(prefix);
					sb.append("(");
					sb.append(s);
					sb.append(")");
					prefix = "\nand ";
				}

				s = oneType(true, range.last, itype, range.ltype);

				if (s.length() > 0)
				{
					sb.append(prefix);
					sb.append("(");
					sb.append(s);
					sb.append(")");
					prefix = "\nand ";
				}

				s = oneType(true, range.last, stype.setof, range.ltype);

				if (s.length() > 0)
				{
					sb.append(prefix);
					sb.append("(");
					sb.append(s);
					sb.append(")");
					prefix = "\nand ";
				}
			}

			sb.append(prefix);
			addIs(sb, exp, etype);
		}
		else if (etype instanceof ProductType)
		{
			if (exp instanceof TupleExpression)
			{
				ProductType pt = (ProductType)etype;
				TupleExpression te = (TupleExpression)exp;
				Iterator<Type> eit = pt.types.iterator();
				Iterator<Type> ait = te.types.iterator();
				prefix = "";

				for (Expression e: te.args)
				{
					String s = oneType(true, e, eit.next(), ait.next());

					if (s.length() > 0)
					{
						sb.append(prefix);
						sb.append("(");
						sb.append(s);
						sb.append(")");
						prefix = " and ";
					}
				}
			}
			else
			{
				addIs(sb, exp, etype);
			}
		}
		else if (etype instanceof BasicType)
		{
    		if (etype instanceof NumericType)
    		{
    			NumericType nt = (NumericType)etype;
    			double signum = -1;

    			if (rec && exp instanceof IntegerLiteralExpression)
    			{
    				IntegerLiteralExpression lit = (IntegerLiteralExpression)exp;
    				signum = Math.signum(lit.value.value);
    			}
    			else if (rec && exp instanceof RealLiteralExpression)
    			{
    				RealLiteralExpression lit = (RealLiteralExpression)exp;

    				if (Math.round(lit.value.value) == lit.value.value)
    				{
    					signum = Math.signum(lit.value.value);
    				}
    			}

    			if (nt instanceof NaturalOneType)
    			{
    				if (signum <= 0)
    				{
    					sb.append(exp);
    					sb.append(" > 0");
    				}
    			}
    			else if (nt instanceof NaturalType)
    			{
    				if (signum < 0)
    				{
    					sb.append(exp);
    					sb.append(" >= 0");
    				}
    			}
    			else
    			{
        			sb.append("is_");
        			sb.append(nt);
        			sb.append("(");
        			sb.append(exp);
        			sb.append(")");
    			}
    		}
    		else if (etype instanceof BooleanType)
    		{
    			if (!(exp instanceof BooleanLiteralExpression))
    			{
        			addIs(sb, exp, etype);
    			}
    		}
    		else if (etype instanceof CharacterType)
    		{
    			if (!(exp instanceof CharLiteralExpression))
    			{
        			addIs(sb, exp, etype);
    			}
    		}
    		else
    		{
    			addIs(sb, exp, etype);
    		}
		}
		else
		{
			addIs(sb, exp, etype);
		}

		return sb.toString();
	}

	private void addIs(StringBuilder sb, Expression exp, Type type)
	{
		sb.append("is_(");
		sb.append(exp);
		sb.append(", ");
		sb.append(type);
		sb.append(")");
	}
}
