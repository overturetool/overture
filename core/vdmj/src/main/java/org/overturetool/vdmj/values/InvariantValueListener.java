/*******************************************************************************
 *
 *	Copyright (C) 2011 Fujitsu Services Ltd.
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

package org.overturetool.vdmj.values;

import java.io.Serializable;

import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ContextException;
import org.overturetool.vdmj.runtime.ValueException;

public class InvariantValueListener implements ValueListener, Serializable
{
    private static final long serialVersionUID = 1L;
	private UpdatableValue root = null;

	public InvariantValueListener()
	{
		this.root = null;
	}

	// We have to set the root separately from the constructor as the tree of
	// updatable values has to be created with the listener before we know
	// the objref of the object just created. See the getUpdatable method of
	// InvariantValue.
	
	public void setValue(UpdatableValue value)
	{
		this.root = value;		// Always an updatable InvariantValue
	}

	public void changedValue(LexLocation location, Value value, Context ctxt)
	{
		// InvariantValueListeners are created at every InvariantValue point (with
		// an inv function) in a structure, but the simplest level is actually
		// covered by the convertTo call in the arguments to "set". So to avoid
		// another unnecessary inv check, we also test whether root = value, which
		// is true for these simplest levels. The instanceof test is added for
		// safety, but all non-simple listeners should have InstanceValue roots.
		
		if (root != null && root.value != value &&
			(root.value instanceof InvariantValue) && Settings.invchecks)
		{
    		try
    		{
    			InvariantValue ival = (InvariantValue) root.value;	// Safe
    			ival.checkInvariant(ctxt);
    		}
    		catch (ValueException e)
    		{
    			throw new ContextException(e, location);
    		}
		}
	}
}
