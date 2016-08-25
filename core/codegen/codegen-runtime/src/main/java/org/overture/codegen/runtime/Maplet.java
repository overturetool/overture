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

public class Maplet
{
	private Object left;
	private Object right;

	public Maplet(Object left, Object right)
	{
		super();
		this.left = left;
		this.right = right;
	}

	public Object getLeft()
	{
		return left;
	}

	public Object getRight()
	{
		return right;
	}

	@Override
	public int hashCode()
	{
		int hash = 0;

		if (left != null)
		{
			hash += left.hashCode();
		}

		if (right != null)
		{
			hash += right.hashCode();
		}

		return hash;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null)
		{
			return false;
		}

		if (this == obj)
		{
			return true;
		}

		if (!(obj instanceof Maplet))
		{
			return false;
		}

		final Maplet other = (Maplet) obj;

		if (this.left == null && other.left != null
				|| this.left != null && !this.left.equals(other.left))
		{
			return false;
		}

		if (this.right == null && other.right != null
				|| this.right != null && !this.right.equals(other.right))
		{
			return false;
		}

		return true;
	}

	@Override
	public String toString()
	{
		return "{" + Utils.toString(left) + " |-> " + Utils.toString(right)
				+ "}";
	}
}
