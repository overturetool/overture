package org.overture.ide.debug.ui.log;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class VdmDebugLogExecutionFilter extends ViewerFilter {

	String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	String[] ALLOWED_DATA = { "CREATE", "<init appid=", "run -i", "step_over", "step_into",
			"<response command=\"run\" status=\"break\"",
			"<response command=\"run\" status=\"stopped\"",
			"<response command=\"step_over\" status=\"break\"",
			"<response command=\"step_into\" status=\"break\""};

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof VdmDebugLogItem) {
			// return !((VdmDebugLogItem) element).getType().equals("Event");
			for (String allowedStart : ALLOWED_DATA) {
				if (((VdmDebugLogItem) element).getMessage()
						.replace(header, "").startsWith(allowedStart)) {
					return true;
				}
			}
		}
		return false;
	}

}
