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