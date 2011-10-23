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
package org.overture.ide.ui.navigator;

import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;
import org.eclipse.ui.navigator.WizardActionGroup;

public class VdmNewActionProvider extends CommonActionProvider {

	
	

	    private static final String NEW_MENU_NAME = "common.new.menu";//$NON-NLS-1$

	    private ActionFactory.IWorkbenchAction showDlgAction;

	    private WizardActionGroup newWizardActionGroup;

	    private boolean contribute = false;

	    @Override
	    public void init(ICommonActionExtensionSite anExtensionSite) {

	        if (anExtensionSite.getViewSite() instanceof ICommonViewerWorkbenchSite) {
	            IWorkbenchWindow window = ((ICommonViewerWorkbenchSite) anExtensionSite.getViewSite()).getWorkbenchWindow();
	            showDlgAction = ActionFactory.NEW.create(window);

	            newWizardActionGroup = new WizardActionGroup(window, PlatformUI.getWorkbench().getNewWizardRegistry(), WizardActionGroup.TYPE_NEW, anExtensionSite.getContentService());

	            contribute = true;
	        }
	    }

	    @Override
	    public void fillContextMenu(IMenuManager menu) {
	        IMenuManager submenu = new MenuManager(
	                "New",
	                NEW_MENU_NAME);
	        if(!contribute) {
	            return;
	        }

	        // fill the menu from the commonWizard contributions
	        newWizardActionGroup.setContext(getContext());
	        newWizardActionGroup.fillContextMenu(submenu);

	        submenu.add(new Separator(ICommonMenuConstants.GROUP_ADDITIONS));

	        // Add other ..
	        submenu.add(new Separator());
	        submenu.add(showDlgAction);

	        // append the submenu after the GROUP_NEW group.
	        menu.insertAfter(ICommonMenuConstants.GROUP_NEW, submenu);
	    }

	    @Override
	    public void dispose() {
	        if (showDlgAction!=null) {
	            showDlgAction.dispose();
	            showDlgAction = null;
	        }
	        super.dispose();
	    }

}
