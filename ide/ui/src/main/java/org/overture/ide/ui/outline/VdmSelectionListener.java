package org.overture.ide.ui.outline;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.overture.ide.ui.VdmUIPlugin;

public class VdmSelectionListener implements ISelectionChangedListener {

	public void selectionChanged(SelectionChangedEvent event) {
		VdmUIPlugin.println("VdmSelectionListener - Selected Object: ");
		VdmUIPlugin.println(event.getSelection().toString());
		
	}

}
