package org.overturetool.eclipse.plugins.traces.views;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.overturetool.eclipse.plugins.traces.OvertureTracesPlugin;
import org.overturetool.eclipse.plugins.traces.views.treeView.ClassTreeNode;
import org.overturetool.eclipse.plugins.traces.views.treeView.NotYetReadyTreeNode;
import org.overturetool.eclipse.plugins.traces.views.treeView.ProjectTreeNode;
import org.overturetool.eclipse.plugins.traces.views.treeView.TraceTestTreeNode;
import org.overturetool.eclipse.plugins.traces.views.treeView.TraceTreeNode;
import org.overturetool.vdmj.traces.Verdict;

public class ViewLabelProvider extends LabelProvider {

	public String getText(Object obj) {
		return obj.toString();
	}

	public Image getImage(Object obj) {
		if (obj instanceof ProjectTreeNode) {
			String imageKey = IDE.SharedImages.IMG_OBJ_PROJECT;
			return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
		}
		if (obj instanceof ClassTreeNode) {
			return OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_TRACE_CLASS).createImage();
		}
		if (obj instanceof TraceTreeNode) {
			return OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_TRACE).createImage();
		}
		if (obj instanceof NotYetReadyTreeNode) {
			return OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_TRACE_TEST_CASE_UNKNOWN).createImage();
		}
//		if (obj instanceof TraceTestGroup) {
//			return OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_TRACE_TEST_CASE_UNKNOWN).createImage();
//		}
		if (obj instanceof TraceTestTreeNode) {
			String imgPath = OvertureTracesPlugin.IMG_TRACE_TEST_CASE_UNKNOWN;
			Verdict status = (((TraceTestTreeNode) obj).GetStatus());
			if (status == Verdict.PASSED)
				imgPath = OvertureTracesPlugin.IMG_TRACE_TEST_CASE_SUCCES;
			else if (status == null)
				imgPath = OvertureTracesPlugin.IMG_TRACE_TEST_CASE_UNKNOWN;
			else if (status == Verdict.INCONCLUSIVE)
				imgPath = OvertureTracesPlugin.IMG_TRACE_TEST_CASE_UNDETERMINED;
			else if (status == Verdict.FAILED)
				imgPath = OvertureTracesPlugin.IMG_TRACE_TEST_CASE_FAIL;
//			else if (status == TestResultType.ExpansionFaild)
//				imgPath = OvertureTracesPlugin.IMG_TRACE_TEST_CASE_EXPANSIN_FAIL;
			else if (status == Verdict.SKIPPED)
				imgPath = OvertureTracesPlugin.IMG_TRACE_TEST_CASE_SKIPPED;

			if (((TraceTestTreeNode) obj).HasRunTimeError())
				imgPath = OvertureTracesPlugin.IMG_ERROR;

			return OvertureTracesPlugin.getImageDescriptor(imgPath).createImage();
		}
		String imageKey = IDE.SharedImages.IMG_OBJ_PROJECT;
		return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
	}
}