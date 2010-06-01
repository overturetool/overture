package org.overture.ide.debug.core.model.eval;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.model.IDebugElement;

public class InspectEvaluatedVdmExpression extends EvaluatedVdmExpression
		implements IDebugEventSetListener {

	public InspectEvaluatedVdmExpression(IVdmEvaluationResult result) {
		super(result);

		DebugPlugin.getDefault().addDebugEventListener(this);
	}

	public void handleDebugEvents(DebugEvent[] events) {
		for (int i = 0; i < events.length; i++) {
			DebugEvent event = events[i];
			switch (event.getKind()) {
			case DebugEvent.TERMINATE:
				if (event.getSource().equals(getDebugTarget())) {
					DebugPlugin.getDefault().getExpressionManager()
							.removeExpression(this);
				}
				break;
			case DebugEvent.SUSPEND:
				if (event.getDetail() != DebugEvent.EVALUATION_IMPLICIT) {
					if (event.getSource() instanceof IDebugElement) {
						IDebugElement source = (IDebugElement) event
								.getSource();
						if (source.getDebugTarget().equals(getDebugTarget())) {
							DebugPlugin.getDefault().fireDebugEventSet(
									new DebugEvent[] { new DebugEvent(this,
											DebugEvent.CHANGE,
											DebugEvent.CONTENT) });
						}
					}
				}
				break;
			}
		}
	}

	public void dispose() {
		super.dispose();

		DebugPlugin.getDefault().removeDebugEventListener(this);
	}
}
