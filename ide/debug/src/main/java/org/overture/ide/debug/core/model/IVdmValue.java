/*******************************************************************************
 * Copyright (c) 2009, 2011 Overture Team and others.
 *
 * Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The Overture Tool web-site: http://overturetool.org/
 *******************************************************************************/
package org.overture.ide.debug.core.model;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.overture.ide.debug.core.model.eval.IVdmEvaluationCommand;

public interface IVdmValue extends IValue {
	String getInstanceId();

	IVdmType getType();

	String getEvalName();

	String getRawValue();

	/**
	 * Returns the physical memory address or <code>null</code> if it is not
	 * available.
	 */
	String getMemoryAddress();

	/**
	 * Returns the text that will be displayed in the 'details' pane of the
	 * 'Variables' view.
	 */
	String getDetailsString();

	IVariable getVariable(int offset) throws DebugException;

	IVdmEvaluationCommand createEvaluationCommand(String messageTemplate,
			IVdmThread thread);
}
