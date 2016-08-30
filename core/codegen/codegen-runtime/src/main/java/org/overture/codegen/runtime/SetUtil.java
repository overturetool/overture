/*
 * #%~
 * VDM Code Generator Runtime
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.codegen.runtime;

public class SetUtil
{
	public static VDMSet set()
	{
		return new VDMSet();
	}

	@SuppressWarnings("unchecked")
	public static VDMSet set(Object... elements)
	{
		if (elements == null)
		{
			throw new IllegalArgumentException("Cannot instantiate set from null");
		}

		VDMSet set = set();

		for (Object element : elements)
		{
			set.add(element);
		}

		return set;
	}

	public static boolean inSet(Object elem, Object set)
	{
		validateSet(set, "'in set'");

		VDMSet vdmSet = (VDMSet) set;

		return vdmSet.contains(elem);
	}

	@SuppressWarnings("unchecked")
	public static boolean subset(Object left, Object right)
	{
		validateSets(left, right, "subset");

		VDMSet leftSet = (VDMSet) left;
		VDMSet rightSet = (VDMSet) right;

		return rightSet.containsAll(leftSet);
	}

	@SuppressWarnings("unchecked")
	public static VDMSet union(Object left, Object right)
	{
		validateSets(left, right, "set union");

		VDMSet leftSet = (VDMSet) left;
		VDMSet rightSet = (VDMSet) right;

		VDMSet result = new VDMSet();

		result.addAll(leftSet);
		result.addAll(rightSet);

		return result;
	}

	@SuppressWarnings("unchecked")
	public static VDMSet dunion(Object setOfSets)
	{
		final String DUNION = "distributed union";

		validateSet(setOfSets, DUNION);

		VDMSet vdmSetOfSets = (VDMSet) setOfSets;

		VDMSet result = set();

		for (Object set : vdmSetOfSets)
		{
			validateSet(set, DUNION);

			VDMSet vdmSet = (VDMSet) set;
			result.addAll(vdmSet);
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public static VDMSet dinter(Object setOfSets)
	{
		final String DINTER = "distributed intersection";

		validateSet(setOfSets, DINTER);

		VDMSet vdmSetOfSets = (VDMSet) setOfSets;

		VDMSet result = dunion(vdmSetOfSets);

		for (Object set : vdmSetOfSets)
		{
			validateSet(set, DINTER);

			VDMSet vdmSet = (VDMSet) set;
			result.retainAll(vdmSet);
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public static VDMSet diff(Object left, Object right)
	{
		validateSets(left, right, "set difference");

		VDMSet setLeft = (VDMSet) left;
		VDMSet setRight = (VDMSet) right;

		VDMSet result = new VDMSet();

		result.addAll(setLeft);
		result.removeAll(setRight);

		return result;
	}

	@SuppressWarnings("unchecked")
	public static boolean psubset(Object left, Object right)
	{
		validateSets(left, right, "proper subset");

		VDMSet setLeft = (VDMSet) left;
		VDMSet setRight = (VDMSet) right;

		return setLeft.size() < setRight.size()
				&& setRight.containsAll(setLeft);
	}

	@SuppressWarnings("unchecked")
	public static VDMSet intersect(Object left, Object right)
	{
		validateSets(left, right, "set intersection");

		VDMSet setLeft = (VDMSet) left;
		VDMSet setRight = (VDMSet) right;

		VDMSet result = new VDMSet();

		result.addAll(setLeft);
		result.retainAll(setRight);

		return result;
	}

	@SuppressWarnings("unchecked")
	public static VDMSet powerset(Object originalSet)
	{

		validateSet(originalSet, "power set");

		VDMSet vdmOriginalSet = (VDMSet) originalSet;

		VDMSet sets = SetUtil.set();

		if (vdmOriginalSet.isEmpty())
		{
			sets.add(SetUtil.set());
			return sets;
		}

		VDMSeq seq = SeqUtil.seq();
		seq.addAll(vdmOriginalSet);

		Object firstElement = seq.get(0);
		VDMSet rest = SetUtil.set();
		rest.addAll(seq.subList(1, seq.size()));

		VDMSet powerSets = powerset(rest);
		Object[] powerSetsArray = powerSets.toArray();

		for (int i = 0; i < powerSets.size(); i++)
		{
			Object obj = powerSetsArray[i];
			if (!(obj instanceof VDMSet))
			{
				throw new IllegalArgumentException("Powerset operation is only applicable to sets. Got: "
						+ obj);
			}

			VDMSet set = (VDMSet) obj;

			VDMSet newSet = SetUtil.set();
			newSet.add(firstElement);
			newSet.addAll(set);
			sets.add(newSet);
			sets.add(set);
		}

		return sets;
	}

	@SuppressWarnings("unchecked")
	public static VDMSet range(Object first, Object last)
	{
		Utils.validateNumbers(first, last, "set range");

		Number firstNumber = (Number) first;
		Number lastNumber = (Number) last;

		long from = (long) Math.ceil(firstNumber.doubleValue());
		long to = (long) Math.floor(lastNumber.doubleValue());

		VDMSet result = new VDMSet();

		for (long i = from; i <= to; i++)
		{
			result.add(i);
		}

		return result;
	}

	static void validateSet(Object arg, String operator)
	{
		if (!(arg instanceof VDMSet))
		{
			throw new IllegalArgumentException(operator
					+ " is only supported for " + VDMSet.class.getName()
					+ ". Got " + arg);
		}
	}

	private static void validateSets(Object left, Object right, String operator)
	{
		if (!(left instanceof VDMSet) || !(right instanceof VDMSet))
		{
			throw new IllegalArgumentException(operator
					+ " is only supported for " + VDMSet.class.getName()
					+ ". Got " + left + " and " + right);
		}
	}
}
