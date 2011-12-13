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
package org.overture.ide.ui.internal.viewsupport;

import org.eclipse.core.resources.IResource;



/**
 * Can be added to a ProblemMarkerManager to get notified about problem
 * marker changes. Used to update error ticks.
 */
public interface IProblemChangedListener {

	/**
	 * Called when problems changed. This call is posted in an aynch exec, therefore passed
	 * resources must not exist.
	 * @param changedResources  A set with elements of type <code>IResource</code> that
	 * describe the resources that had an problem change.
	 * @param isMarkerChange If set to <code>true</code>, the change was a marker change, if
	 * <code>false</code>, the change came from an annotation model modification.
	 */
	void problemsChanged(IResource[] changedResources, boolean isMarkerChange);

}
