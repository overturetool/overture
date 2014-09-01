/*
 * #%~
 * org.overture.ide.ui
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
package org.overture.ide.ui.outline;


import org.eclipse.jface.action.Action;
import org.eclipse.ui.PlatformUI;

public class MemberFilterAction extends Action {

	private int fFilterProperty;
	private MemberFilterActionGroup fFilterActionGroup;

	public MemberFilterAction(MemberFilterActionGroup actionGroup, String title, int property, String contextHelpId, boolean initValue) {
		super(title);
		fFilterActionGroup= actionGroup;
		fFilterProperty= property;

		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, contextHelpId);

		setChecked(initValue);
	}

	/**
	 * Returns this action's filter property.
	 * @return returns the property
	 */
	public int getFilterProperty() {
		return fFilterProperty;
	}

	/*
	 * @see Action#actionPerformed
	 */
	public void run() {
		fFilterActionGroup.setMemberFilter(fFilterProperty, isChecked());
	}

}
