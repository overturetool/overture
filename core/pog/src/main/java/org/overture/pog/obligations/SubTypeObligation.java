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

package org.overture.pog.obligations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.expressions.AApplyExp;
import org.overture.ast.expressions.ANotYetSpecifiedExp;
import org.overture.ast.expressions.ASubclassResponsibilityExp;
import org.overture.ast.expressions.ATupleExp;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.APatternListTypePair;
import org.overture.ast.patterns.ATuplePattern;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.patterns.assistants.PPatternAssistantTC;
import org.overture.ast.types.PType;
import org.overturetool.vdmj.lex.LexNameToken;

public class SubTypeObligation extends ProofObligation
{
	public SubTypeObligation(
		PExp exp, PType etype, PType atype, POContextStack ctxt)
	{
		super(exp.getLocation(), POType.SUB_TYPE, ctxt);
		value = ctxt.getObligation(oneType(false, exp, etype, atype));
		return;
	}

	public SubTypeObligation(
		AExplicitFunctionDefinition func, PType etype, PType atype, POContextStack ctxt)
	{
		super(func.getLocation(), POType.SUB_TYPE, ctxt);

		PExp body = null;

		if (func.getBody() instanceof ANotYetSpecifiedExp ||
			func.getBody() instanceof ASubclassResponsibilityExp)
		{
			// We have to say "f(a)" because we have no body

			PExp root = new AVariableExp(null, func.getName().getLocation(), func.getName());
			List<PExp> args = new ArrayList<PExp>();

			for (PPattern p: func.getParamPatternList().get(0))
			{
				args.add(PPatternAssistantTC.getMatchingExpression(p));
			}
			body = new AApplyExp(null, root.getLocation(), root, args);
						
		}
		else
		{
			body = func.getBody();
		}

		value = ctxt.getObligation(oneType(false, body, etype, atype));
	}

	public SubTypeObligation(
		AImplicitFunctionDefinition func, PType etype, PType atype, POContextStack ctxt)
	{
		super(func.getLocation(), POType.SUB_TYPE, ctxt);

		PExp body = null;

		if (func.getBody() instanceof ANotYetSpecifiedExp ||
			func.getBody() instanceof ASubclassResponsibilityExp)
		{
			// We have to say "f(a)" because we have no body

			PExp root = new AVariableExp(null, func.getName().getLocation(), func.getName());
			List<PExp> args = new ArrayList<PExp>();

			for (APatternListTypePair pltp: func.getParamPatterns())
			{
				for (PPattern p: pltp.getPatterns())
				{
					args.add(PPatternAssistantTC.getMatchingExpression(p));
				}
			}

			body = new AApplyExp(null, root.getLocation(), root, args);
		}
		else
		{
			body = func.getBody();
		}

		value = ctxt.getObligation(oneType(false, body, etype, atype));
	}

	public SubTypeObligation(
		AExplicitOperationDefinition def, PType actualResult, POContextStack ctxt)
	{
		super(def.getLocation(), POType.SUB_TYPE, ctxt);

		AVariableExp result = new AVariableExp(null, 
				def.getLocation(), 
				new LexNameToken(def.getName().module, "RESULT", def.getLocation())); 
			
			

		value = ctxt.getObligation(
			oneType(false, result, def.getType().getResult(), actualResult));
	}

	public SubTypeObligation(
		AImplicitOperationDefinition def, PType actualResult, POContextStack ctxt)
	{
		super(def.getLocation(), POType.SUB_TYPE, ctxt);
		PExp result = null;

		if (def.getResult().getPattern() instanceof AIdentifierPattern)
		{
			AIdentifierPattern ip = (AIdentifierPattern)def.getResult().getPattern();
			result = new AVariableExp(null,ip.getName().getLocation(),ip.getName());
		}
		else
		{
			ATuplePattern tp = (ATuplePattern)def.getResult().getPattern();
			List<PExp> args = new ArrayList<PExp>();

			for (PPattern p: tp.getPlist())
			{
				AIdentifierPattern ip = (AIdentifierPattern)p;
				args.add(new AVariableExp(null,ip.getName().getLocation(),ip.getName()));
			}

			result = new ATupleExp(null, def.getLocation(), args);
		}

		value = ctxt.getObligation(
			oneType(false, result, def.getType().getResult(), actualResult));
	}

	private String oneType(boolean rec, PExp exp, PType etype, PType atype)
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

		etype = etype.deBracket();

		if (etype instanceof UnionType)
		{
			UnionType ut = (UnionType)etype;
			TypeSet possibles = new TypeSet();

			for (PType pos: ut.types)
			{
				if (atype == null || TypeComparator.compatible(pos, atype))
				{
					possibles.add(pos);
				}
			}

			prefix = "";

			for (PType poss: possibles)
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

//				This needs to be put back if/when we change the inv_R signature to take
//    			the record fields as arguments, rather than one R value.
//				if (exp instanceof MkTypeExpression)
//				{
//					MkTypeExpression mk = (MkTypeExpression)exp;
//					sb.append(Utils.listToString(mk.args));
//				}
//				else
				{
					sb.append(exp);
				}

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

					if (rt.fields.size() == mk.args.size())
					{
    					Iterator<Field> fit = rt.fields.iterator();
    					Iterator<PType> ait = mk.argTypes.iterator();

    					for (PExp e: mk.args)
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
				Iterator<PType> it = seq.types.iterator();

				for (PExp m: seq.members)
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
				PType itype = new NaturalOneType(exp.location);
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
				Iterator<PType> dit = seq.domtypes.iterator();
				Iterator<PType> rit = seq.rngtypes.iterator();
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
				Iterator<PType> it = set.types.iterator();
				prefix = "";

				for (PExp m: set.members)
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
				PType itype = new IntegerType(exp.location);
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
				Iterator<PType> eit = pt.types.iterator();
				Iterator<PType> ait = te.types.iterator();
				prefix = "";

				for (PExp e: te.args)
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

    			if (atype instanceof NumericType)
    			{
    				NumericType ant = (NumericType)atype;

    				if (ant.getWeight() > nt.getWeight())
    				{
            			if (nt instanceof NaturalOneType)
            			{
          					sb.append(exp);
           					sb.append(" > 0");
            			}
            			else if (nt instanceof NaturalType)
            			{
           					sb.append(exp);
           					sb.append(" >= 0");
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

	private void addIs(StringBuilder sb, PExp exp, PType type)
	{
		sb.append("is_(");
		sb.append(exp);
		sb.append(", ");
		sb.append(type);
		sb.append(")");
	}
}
