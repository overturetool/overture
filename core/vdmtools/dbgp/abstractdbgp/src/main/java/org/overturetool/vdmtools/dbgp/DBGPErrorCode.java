/*******************************************************************************
 *
 *	Copyright (c) 2009 Fujitsu Services Ltd.
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

package org.overturetool.vdmtools.dbgp;

public enum DBGPErrorCode
{
	NONE(0),
	PARSE(1),
	DUPLICATE_ARGS(2),
	INVALID_OPTIONS(3),
	UNIMPLEMENTED(4),
	NOT_AVAILABLE(5),

	CANT_OPEN(100),
	STREAM_REDIRECT_FAILED(101),

	CANT_SET_BREAKPOINT(200),
	BREAKPOINT_TYPE_UNSUPPORTED(201),
	INVALID_BREAKPOINT(202),
	NO_CODE_AT_BREAKPOINT(203),
	INVALID_BREAKPOINT_STATE(204),
	NO_SUCH_BREAKPOINT(205),
	EVALUATION_ERROR(206),
	INVALID_EXPRESION(207),

	CANT_GET_PROPERTY(300),
	INVALID_STACK_DEPTH(301),
	INVALID_CONTEXT(302),

	INVALID_ENCODING(900),
	INTERNAL_ERROR(998),
	UNKNOWN_ERROR(999);

	public int value;

	DBGPErrorCode(int v)
	{
		value = v;
	}
}
