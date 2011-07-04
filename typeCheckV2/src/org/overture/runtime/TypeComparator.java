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

package org.overture.runtime;

import java.util.List;
import java.util.Vector;

import org.overture.ast.types.ABracketType;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AMapType;
import org.overture.ast.types.ANamedInvariantType;

import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AParameterType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.ASeq1Type;

import org.overture.ast.types.ASeqType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.AUndefinedType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.AUnresolvedType;
import org.overture.ast.types.AVoidReturnType;
import org.overture.ast.types.AVoidType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SNumericBasicType;



/**
 * A class for static type checking comparisons.
 */

public class TypeComparator
{
	/**
	 * A vector of type pairs that have already been compared. This is to
	 * allow recursive type definitions to be compared without infinite
	 * regress.
	 */

	private static Vector<TypePair> done = new Vector<TypePair>(256);

	/**
	 * A result value for comparison of types. The "Maybe" value is needed so
	 * that the fact that a type's subtypes are being actively compared in
	 * a recursive call can be recorded. For example, if a MySetType contains
	 * references to MySetType in its element's type, the comparison of those
	 * types will see the "Maybe" result of the original call and not recurse.
	 * That will not fail the lower level comparison, but the overall "yes"
	 * will not be recorded until the recursive calls return (assuming there
	 * are no "no" votes of course).
	 */

	private static enum Result { Yes, No, Maybe }

	private static class TypePair
	{
		public PType a;
		public PType b;
		public Result result;

		public TypePair(PType a, PType b)
		{
			this.a = a;
			this.b = b;
			this.result = Result.Maybe;
		}

		@Override
		public boolean equals(Object other)
		{
			if (other instanceof TypePair)
			{
				TypePair to = (TypePair)other;
				return a == to.a && b == to.b;
			}

			return false;
		}

		@Override
		public int hashCode()
		{
			return a.hashCode() + b.hashCode();
		}
	}

	/**
	 * Test whether the two types are compatible. This means that, at runtime,
	 * it is possible that the two types are the same, or sufficiently similar
	 * that the "from" value can be assigned to the "to" value.
	 *
	 * @param to
	 * @param from
	 * @return True if types "a" and "b" are compatible.
	 */

	public synchronized static boolean compatible(PType to, PType from)
	{
		done.clear();
		return searchCompatible(to, from, false) == Result.Yes;
	}

	public synchronized static boolean compatible(PType to, PType from, boolean paramOnly)
	{
		done.clear();
		return searchCompatible(to, from, paramOnly) == Result.Yes;
	}

	/**
	 * Compare two type lists for placewise compatibility.
	 *
	 * @param to
	 * @param from
	 * @return True if all types compatible.
	 */

	public synchronized static boolean compatible(List<PType> to, List<PType> from)
	{
		done.clear();
		return allCompatible(to, from, false) == Result.Yes;
	}

	/**
	 * Compare two type lists for placewise compatibility. This is used
	 * to check ordered lists of types such as those in a ProductType or
	 * parameters to a function or operation.
	 *
	 * @param to
	 * @param from
	 * @return Yes or No.
	 */

	private static Result allCompatible(List<PType> to, List<PType> from, boolean paramOnly)
	{
		if (to.size() != from.size())
		{
			return Result.No;
		}
		else
		{
			for (int i=0; i<to.size(); i++)
			{
				if (searchCompatible(to.get(i), from.get(i), paramOnly) == Result.No)
				{
					return Result.No;
				}
			}
		}

		return Result.Yes;
	}

	/**
	 * Search the {@link #done} vector for an existing comparison of two
	 * types before either returning the previous result, or making a new
	 * comparison and adding that result to the vector.
	 *
	 * @param to
	 * @param from
	 * @return Yes or No.
	 */

	private static Result searchCompatible(PType to, PType from, boolean paramOnly)
	{
		TypePair pair = new TypePair(to, from);
		int i = done.indexOf(pair);

		if (i >= 0)
		{
			return done.get(i).result;		// May be "Maybe".
		}
		else
		{
			done.add(pair);
		}

		// The pair.result is "Maybe" until this call returns.
		pair.result = test(to, from, paramOnly);

		return pair.result;
	}

	/**
	 * The main implementation of the compatibility checker. If "a" and "b" are
	 * the same object the result is "yes"; if either is an {@link UnknownType},
	 * we are dealing with earlier parser errors and so "yes" is returned to
	 * avoid too many errors; if either is a {@link ParameterType} the result is
	 * also "yes" on the grounds that the type cannot be tested at compile time.
	 *
	 * If either type is a {@link BracketType} or a {@link NamedType} the types
	 * are reduced to their underlying type before proceeding; if either is an
	 * {@link OptionalType} and the other is optional also, the result is
	 * "yes", otherwise the underlying type of the optional type is taken before
	 * proceeding; the last two steps are repeated until the types will not
	 * reduce further.
	 *
	 * To compare the reduced types, if "a" is a union type, then all the
	 * component types of "a" are compared to "b" (or b's components, if it too
	 * is a union type) until a match is found; otherwise basic type comparisons
	 * are made, involving any subtypes - for example, if they are both sets,
	 * then the result depends on whether their "set of" subtypes are
	 * compatible, by a recursive call. Similarly with maps and sequences,
	 * function/operation parameter types, and record field types. Lastly, a
	 * simple {@link org.overturetool.vdmj.types.Type#equals} operation is
	 * performed on two basic types to decide the result.
	 *
	 * @param to
	 * @param from
	 * @param paramOnly
	 * @return Yes or No.
	 */

	private static Result test(PType to, PType from, boolean paramOnly)
	{
		if (to instanceof AUnresolvedType)
		{
			throw new TypeCheckException("Unknown type: " + to, to.getLocation());
		}

		if (from instanceof AUnresolvedType)
		{
			throw new TypeCheckException("Unknown type: " + from, from.getLocation());
		}

		if (to == from)
		{
			return Result.Yes;	// Same object!
		}

		if (to instanceof AUnknownType || from instanceof AUnknownType)
		{
			return Result.Yes;	// Hmmm... too many errors otherwise
		}

		if (to instanceof AUndefinedType || from instanceof AUndefinedType)
		{
			return Result.Yes;	// Not defined "yet"...?
		}

		if (to instanceof AParameterType || from instanceof AParameterType)
		{
			return Result.Yes;	// Runtime checked...
		}


		// Obtain the fundamental type of BracketTypes, NamedTypes and
		// OptionalTypes.

		boolean resolved = false;

		while (!resolved)
		{
    		if (to instanceof ABracketType)
    		{
    			to = ((ABracketType)to).getType();
    			continue;
    		}

    		if (from instanceof ABracketType)
    		{
    			from = ((ABracketType)from).getType();
    			continue;
    		}

    		if (to instanceof ANamedInvariantType)
    		{
    			to = ((ANamedInvariantType)to).getType();
    			continue;
    		}

    		if (from instanceof ANamedInvariantType)
    		{
    			from = ((ANamedInvariantType)from).getType();
    			continue;
    		}

    		if (to instanceof AOptionalType)
    		{
    			if (from instanceof AOptionalType)
    			{
    				return Result.Yes;
    			}

    			to = ((AOptionalType)to).getType();
    			continue;
    		}

    		if (from instanceof AOptionalType)
    		{
    			// Can't assign nil to a non-optional type? This should maybe
    			// generate a warning here?

    			if (to instanceof AOptionalType)
    			{
    				return Result.Yes;
    			}

    			from = ((AOptionalType)from).getType();
    			continue;
    		}

    		resolved = true;
		}

		// OK... so we have fully resolved the basic types...

		if (to instanceof AUnionType)
		{
			AUnionType ua = (AUnionType)to;

			for (PType ta: ua.getTypes())
			{
				if (searchCompatible(ta, from, paramOnly) == Result.Yes)
				{
					return Result.Yes;
				}
			}
		}
		else
		{
			if (from instanceof AUnionType)
			{
				AUnionType ub = (AUnionType)from;

				for (PType tb: ub.getTypes())
				{
					if (searchCompatible(to, tb, paramOnly) == Result.Yes)
					{
						return Result.Yes;
					}
				}
			}
			else if (to instanceof SNumericBasicType)
			{
				return (from instanceof SNumericBasicType) ? Result.Yes : Result.No;
			}
			else if (to instanceof AProductType)
			{
				if (!(from instanceof AProductType))
				{
					return Result.No;
				}

				List<PType> ta = ((AProductType)to).getTypes();
				List<PType> tb = ((AProductType)from).getTypes();
				return allCompatible(ta, tb, paramOnly);
			}
			else if (to instanceof AMapType)
			{
				if (!(from instanceof AMapType))
				{
					return Result.No;
				}

				AMapType ma = (AMapType)to;
				AMapType mb = (AMapType)from;

				return (ma.getEmpty() || mb.getEmpty() ||
					(searchCompatible(ma.getFrom(), mb.getFrom(), paramOnly) == Result.Yes &&
					 searchCompatible(ma.getTo(), mb.getTo(), paramOnly) == Result.Yes)) ?
							Result.Yes : Result.No;
			}
			else if (to instanceof ASetType)
			{
				if (!(from instanceof ASetType))
				{
					return Result.No;
				}

				ASetType sa = (ASetType)to;
				ASetType sb = (ASetType)from;

				return (sa.getEmpty() || sb.getEmpty() ||
						searchCompatible(sa.getSetof(), sb.getSetof(), paramOnly) == Result.Yes) ?
							Result.Yes : Result.No;
			}
			else if (to instanceof ASeqType)	// Includes seq1
			{
				if (!(from instanceof ASeqType))
				{
					return Result.No;
				}

				ASeqType sa = (ASeqType)to;
				ASeqType sb = (ASeqType)from;

				if (to instanceof ASeq1Type && sb.getEmpty())
				{
					return Result.No;
				}

				return (sa.getEmpty() || sb.getEmpty() ||
						searchCompatible(sa.getSeqof(), sb.getSeqof(), paramOnly) == Result.Yes) ?
							Result.Yes : Result.No;
			}
			else if (to instanceof AFunctionType)
			{
				if (!(from instanceof AFunctionType))
				{
					return Result.No;
				}

				AFunctionType fa = (AFunctionType)to;
				AFunctionType fb = (AFunctionType)from;

				return (allCompatible(fa.getParameters(), fb.getParameters(), paramOnly) == Result.Yes &&
						(paramOnly ||
						 searchCompatible(fa.getResult(), fb.getResult(), paramOnly) == Result.Yes)) ?
							Result.Yes : Result.No;
			}
			else if (to instanceof AOperationType)
			{
				if (!(from instanceof AOperationType))
				{
					return Result.No;
				}

				AOperationType fa = (AOperationType)to;
				AOperationType fb = (AOperationType)from;

				return (allCompatible(fa.getParameters(), fb.getParameters(), paramOnly) == Result.Yes &&
						(paramOnly ||
						 searchCompatible(fa.getResult(), fb.getResult(), paramOnly) == Result.Yes)) ?
							Result.Yes : Result.No;
			}
			else if (to instanceof ARecordInvariantType)
			{
				if (!(from instanceof ARecordInvariantType))
				{
					return Result.No;
				}

				ARecordInvariantType rf = (ARecordInvariantType)from;
				ARecordInvariantType rt = (ARecordInvariantType)to;

				return rf.equals(rt) ? Result.Yes : Result.No;
			}
			else if (to instanceof AClassType)
			{
				if (!(from instanceof AClassType))
				{
					return Result.No;
				}

				AClassType cfrom = (AClassType)from;
				AClassType cto = (AClassType)to;

				// VDMTools doesn't seem to worry about sub/super type
				// assignments. This was "cfrom.equals(cto)".

				if (HelperType.hasSupertype(cfrom,cto) || HelperType.hasSupertype(cto,cfrom))
				{
					return Result.Yes;
				}
			}
			else if (from instanceof AVoidReturnType)
			{
				if (to instanceof AVoidType || to instanceof AVoidReturnType)
				{
					return Result.Yes;
				}
				else
				{
					return Result.No;
				}
			}
			else if (to instanceof AVoidReturnType)
			{
				if (from instanceof AVoidType || from instanceof AVoidReturnType)
				{
					return Result.Yes;
				}
				else
				{
					return Result.No;
				}
			}
			else
			{
				return to.equals(from) ? Result.Yes : Result.No;
			}
		}

		return Result.No;
	}

	/**
	 * Test whether one type is a subtype of another.
	 *
	 * @param sub
	 * @param sup
	 * @return True if sub is a subtype of sup.
	 */

	public synchronized static boolean isSubType(PType sub, PType sup)
	{
		done.clear();
		return searchSubType(sub, sup) == Result.Yes;
	}

	/**
	 * Compare two type lists for placewise subtype compatibility. This is used
	 * to check ordered lists of types such as those in a ProductType or
	 * parameters to a function or operation.
	 *
	 * @param sub
	 * @param sup
	 * @return Yes or No.
	 */

	private static Result allSubTypes(List<PType> sub, List<PType> sup)
	{
		if (sub.size() != sup.size())
		{
			return Result.No;
		}
		else
		{
			for (int i=0; i<sub.size(); i++)
			{
				if (searchSubType(sub.get(i), sup.get(i)) == Result.No)
				{
					return Result.No;
				}
			}
		}

		return Result.Yes;
	}

	/**
	 * Search the {@link #done} vector for an existing subtype comparison of two
	 * types before either returning the previous result, or making a new
	 * comparison and adding that result to the vector.
	 *
	 * @param sub
	 * @param sup
	 * @return Yes or No, if sub is a subtype of sup.
	 */

	private static Result searchSubType(PType sub, PType sup)
	{
		TypePair pair = new TypePair(sub, sup);
		int i = done.indexOf(pair);

		if (i >= 0)
		{
			return done.get(i).result;		// May be "Maybe".
		}
		else
		{
			done.add(pair);
		}

		// The pair.result is "Maybe" until this call returns.
		pair.result = subtest(sub, sup);

		return pair.result;
	}

	/**
	 * The main implementation of the subtype checker. If "a" and "b" are
	 * the same object the result is "yes"; if either is an {@link UnknownType},
	 * we are dealing with earlier parser errors and so "yes" is returned to
	 * avoid too many errors; if either is a {@link ParameterType} the result is
	 * also "yes" on the grounds that the type cannot be tested at compile time.
	 *
	 * If either type is a {@link BracketType} or a {@link NamedType} the types
	 * are reduced to their underlying type before proceeding; if either is an
	 * {@link OptionalType} and the other is optional also, the result is
	 * "yes", otherwise the underlying type of the optional type is taken before
	 * proceeding; the last two steps are repeated until the types will not
	 * reduce further.
	 *
	 * To compare the reduced types, if "a" is a union type, then all the
	 * component types of "a" are compared to "b" (or b's components, if it too
	 * is a union type); otherwise basic type comparisons
	 * are made, involving any subtypes - for example, if they are both sets,
	 * then the result depends on whether their "set of" subtypes are
	 * subtypes, by a recursive call. Similarly with maps and sequences,
	 * function/operation parameter types, and record field types. Lastly, a
	 * simple {@link org.overturetool.vdmj.types.Type#equals} operation is
	 * performed on two basic types to decide the result.
	 *
	 * @param sub
	 * @param sup
	 * @return Yes or No.
	 */

	private static Result subtest(PType sub, PType sup)
	{
		if (sub instanceof AUnresolvedType)
		{
			throw new TypeCheckException("Unknown type: " + sub, sub.getLocation());
		}

		if (sup instanceof AUnresolvedType)
		{
			throw new TypeCheckException("Unknown type: " + sup, sup.getLocation());
		}

		if (sub instanceof AUnknownType || sup instanceof AUnknownType)
		{
			return Result.Yes;	// Hmmm... too many errors otherwise
		}

		if (sub instanceof AParameterType || sup instanceof AParameterType)
		{
			return Result.Yes;	// Runtime checked...
		}

		if (sub instanceof AUndefinedType || sup instanceof AUndefinedType)
		{
			return Result.Yes;	// Usually uninitialized variables etc.
		}

		// Obtain the fundamental type of BracketTypes, NamedTypes and
		// OptionalTypes.

		boolean resolved = false;

		while (!resolved)
		{
    		if (sub instanceof ABracketType)
    		{
    			sub = ((ABracketType)sub).getType();
    			continue;
    		}

    		if (sup instanceof ABracketType)
    		{
    			sup = ((ABracketType)sup).getType();
    			continue;
    		}

    		if (sub instanceof ANamedInvariantType)
    		{
    			ANamedInvariantType nt = (ANamedInvariantType)sub;

       			if (nt.getInvdef() == null)
       			{
        			sub = nt.getType();
        			continue;
        		}
    		}

    		if (sup instanceof ANamedInvariantType)
    		{
    			ANamedInvariantType nt = (ANamedInvariantType)sup;

       			if (nt.getInvdef() == null)
       			{
        			sup = nt.getType();
        			continue;
        		}
    		}

    		if (sub instanceof AOptionalType && sup instanceof AOptionalType)
    		{
       			sub = ((AOptionalType)sub).getType();
       			sup = ((AOptionalType)sup).getType();
    			continue;
    		}

    		resolved = true;
		}

		if (sub instanceof AUnknownType || sup instanceof AUnknownType)
		{
			return Result.Yes;		// Hmmm... too many errors otherwise
		}

		if (sub == sup)
		{
			return Result.Yes;		// Same object!
		}

		// OK... so we have fully resolved the basic types...

		if (sub instanceof AUnionType)
		{
			AUnionType subu = (AUnionType)sub;

			for (PType suba: subu.getTypes())
			{
				if (searchSubType(suba, sup) == Result.No)
				{
					return Result.No;
				}
			}

			return Result.Yes;	// Must be all of them
		}
		else
		{
			if (sup instanceof AUnionType)
			{
				AUnionType supu = (AUnionType)sup;

				for (PType supt: supu.getTypes())
				{
					if (searchSubType(sub, supt) == Result.Yes)
					{
						return Result.Yes;	// Can be any of them
					}
				}

				return Result.No;
			}
			else if (sub instanceof ANamedInvariantType)
    		{
				ANamedInvariantType subn = (ANamedInvariantType)sub;
				return searchSubType(subn.getType(), sup);
			}
			else if (sup instanceof AOptionalType)
    		{
				// Supertype includes a nil value, and the subtype is not
				// optional (stripped above), so we test the optional's type.

				AOptionalType op = (AOptionalType)sup;
				return searchSubType(sub, op.getType());
			}
			else if (sub instanceof SNumericBasicType)
			{
				if (sup instanceof SNumericBasicType)
				{
					SNumericBasicType subn = (SNumericBasicType)sub;
					SNumericBasicType supn = (SNumericBasicType)sup;

					return (HelperNumericBasicType.getWeight(subn) <= HelperNumericBasicType.getWeight(supn)) ?
						Result.Yes : Result.No;
				}
			}
			else if (sub instanceof AProductType)
			{
				if (!(sup instanceof AProductType))
				{
					return Result.No;
				}

				List<PType> subl = ((AProductType)sub).getTypes();
				List<PType> supl = ((AProductType)sup).getTypes();

				return allSubTypes(subl, supl);
			}
			else if (sub instanceof AMapType)
			{
				if (!(sup instanceof AMapType))
				{
					return Result.No;
				}

				AMapType subm = (AMapType)sub;
				AMapType supm = (AMapType)sup;

				return (subm.getEmpty() || supm.getEmpty() ||
					(searchSubType(subm.getFrom(), supm.getFrom()) == Result.Yes &&
					 searchSubType(subm.getTo(), supm.getTo()) == Result.Yes)) ?
							Result.Yes : Result.No;
			}
			else if (sub instanceof ASetType)
			{
				if (!(sup instanceof ASetType))
				{
					return Result.No;
				}

				ASetType subs = (ASetType)sub;
				ASetType sups = (ASetType)sup;

				return (subs.getEmpty() || sups.getEmpty() ||
						searchSubType(subs.getSetof(), sups.getSetof()) == Result.Yes) ?
							Result.Yes : Result.No;
			}
			else if (sub instanceof ASeqType)	// Includes seq1
			{
				if (!(sup instanceof ASeqType))
				{
					return Result.No;
				}

				ASeqType subs = (ASeqType)sub;
				ASeqType sups = (ASeqType)sup;

				if (subs.getEmpty() || sups.getEmpty())
				{
					return Result.Yes;
				}

				if (searchSubType(subs.getSeqof(), sups.getSeqof()) == Result.Yes)
				{
					if (!(sub instanceof ASeq1Type) &&
						 (sup instanceof ASeq1Type))
					{
						return Result.No;
					}
					
					return Result.Yes;
				}
				else
				{
					return Result.No;
				}
			}
			else if (sub instanceof AFunctionType)
			{
				if (!(sup instanceof AFunctionType))
				{
					return Result.No;
				}

				AFunctionType subf = (AFunctionType)sub;
				AFunctionType supf = (AFunctionType)sup;

				return (allSubTypes(subf.getParameters(), supf.getParameters()) == Result.Yes &&
						searchSubType(subf.getResult(), supf.getResult()) == Result.Yes) ?
							Result.Yes : Result.No;
			}
			else if (sub instanceof AOperationType)
			{
				if (!(sup instanceof AOperationType))
				{
					return Result.No;
				}

				AOperationType subo = (AOperationType)sub;
				AOperationType supo = (AOperationType)sup;

				return (allSubTypes(subo.getParameters(), supo.getParameters()) == Result.Yes &&
						searchSubType(subo.getResult(), supo.getResult()) == Result.Yes) ?
							Result.Yes : Result.No;
			}
			else if (sub instanceof ARecordInvariantType)
			{
				if (!(sup instanceof ARecordInvariantType))
				{
					return Result.No;
				}

				ARecordInvariantType subr = (ARecordInvariantType)sub;
				ARecordInvariantType supr = (ARecordInvariantType)sup;

				return subr.equals(supr) ? Result.Yes : Result.No;
			}
			else if (sub instanceof AClassType)
			{
				if (!(sup instanceof AClassType))
				{
					return Result.No;
				}

				AClassType supc = (AClassType)sup;
				AClassType subc = (AClassType)sub;

				if (HelperType.hasSupertype(subc,supc))
				{
					return Result.Yes;
				}
			}
			else
			{
				return sub.equals(sup) ? Result.Yes : Result.No;
			}
		}

		return Result.No;
	}
}
