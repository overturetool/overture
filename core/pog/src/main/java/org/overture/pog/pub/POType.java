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

package org.overture.pog.pub;

public enum POType
{
	CASES_EXHAUSTIVE("cases exhaustive"), EXT("extension"), FINITE_MAP(
			"finite map"), FINITE_SET("finite set"), FUNC_APPLY(
			"legal function application"), FUNC_COMPOSE("function compose"), FUNC_ITERATION(
			"function iteration"), FUNC_PATTERNS("function parameter patterns"), FUNC_POST_CONDITION(
			"function establishes postcondition"), FUNC_SATISFIABILITY(
			"function postcondition satisfiable"), LET_BE_EXISTS(
			"let be st existence"), MAP_APPLY("legal map application"), MAP_COMPATIBLE(
			"map compatible"), MAP_COMPOSE("map compose"), MAP_INJ_ENUM(
			"enumeration map injectivity"), MAP_INJ_COMP(
			"comprehension map injectivity"), MAP_INVERSE("map inverse"), MAP_ITERATION(
			"map iteration"), NON_EMPTY_SEQ("non-empty sequence"), NON_EMPTY_SET(
			"non-empty set"), NON_ZERO("non-zero"), OP_CALL("operation call"), OP_POST_CONDITION(
			"operation establishes postcondition"), OP_SATISFIABILITY(
			"operation postcondition satisfiable"), OPERATION_PATTERNS(
			"operation parameter patterns"), RECURSIVE("recursive function"), SEQ_APPLY(
			"legal sequence application"), SEQ_MODIFICATION(
			"sequence modification"), STATE_INV("state invariant holds"), STATE_INV_INIT(
			"state invariant initialized"), STATE_INV_SAT(
			"state invariant satisfiable"), TYPE_COMP("type compatibility"), TYPE_INV_SAT(
			"type invariant satisfiable"), TUPLE_SELECT("tuple selection"), UNIQUE_EXISTENCE(
			"unique existence binding"),
			ORDERED("ordered"),
			STRICT_ORDER("strict order relation"),
			EQUIV_REL( "equivalence relation"),
			VALUE_BINDING("value binding"),
			WHILE_LOOP( "while loop termination"),
			SET_MEMBER("set membership"),
			SEQ_MEMBER("sequence membership");

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
