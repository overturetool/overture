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

public enum POType
{
	MAP_APPLY("map apply"),
	FUNC_APPLY("function apply"),
	SEQ_APPLY("sequence apply"),
	FUNC_POST_CONDITION("post condition"),
	FUNC_SATISFIABILITY("function satisfiability"),
	FUNC_PATTERNS("function parameter patterns"),
	LET_BE_EXISTS("let be st existence"),
	UNIQUE_EXISTENCE("unique existence binding"),
	FUNC_ITERATION("function iteration"),
	MAP_ITERATION("map iteration"),
	FUNC_COMPOSE("function compose"),
	MAP_COMPOSE("map compose"),
	NON_EMPTY_SET("non-empty set"),
	NON_EMPTY_SEQ("non-empty sequence"),
	NON_ZERO("non-zero"),
	FINITE_MAP("finite map"),
	FINITE_SET("finite set"),
	MAP_COMPATIBLE("map compatible"),
	MAP_SEQ_OF_COMPATIBLE("map sequence compatible"),
	MAP_SET_OF_COMPATIBLE("map set compatible"),
	SEQ_MODIFICATION("sequence modification"),
	TUPLE_SELECT("tuple selection"),
	VALUE_BINDING("value binding"),
	SUB_TYPE("subtype"),
	CASES_EXHAUSTIVE("cases exhaustive"),
	INVARIANT("type invariant"),
	RECURSIVE("recursive function"),
	STATE_INVARIANT("state invariant"),
	WHILE_LOOP("while loop termination"),
	OP_POST_CONDITION("operation post condition"),
	OPERATION_PATTERNS("operation parameter patterns"),
	OP_SATISFIABILITY("operation satifiability");

	private String kind;

	POType(String kind)
	{
		this.kind = kind;
	}

	@Override
	public String toString()
	{
		return kind;
	}
}
