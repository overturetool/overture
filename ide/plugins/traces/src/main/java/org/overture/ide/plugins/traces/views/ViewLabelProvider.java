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
package org.overture.ide.plugins.traces.views;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.overture.ide.plugins.traces.OvertureTracesPlugin;
import org.overture.ide.plugins.traces.views.treeView.ClassTreeNode;
import org.overture.ide.plugins.traces.views.treeView.NotYetReadyTreeNode;
import org.overture.ide.plugins.traces.views.treeView.ProjectTreeNode;
import org.overture.ide.plugins.traces.views.treeView.TraceTestTreeNode;
import org.overture.ide.plugins.traces.views.treeView.TraceTreeNode;
import org.overturetool.vdmj.traces.Verdict;

public class ViewLabelProvider extends LabelProvider {

	@Override
	public String getText(Object obj) {
		return obj.toString();
	}

	@Override
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
			Verdict status = (((TraceTestTreeNode) obj).getStatus());
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

			if (((TraceTestTreeNode) obj).hasRunTimeError())
				imgPath = OvertureTracesPlugin.IMG_ERROR;

			return OvertureTracesPlugin.getImageDescriptor(imgPath).createImage();
		}
		String imageKey = IDE.SharedImages.IMG_OBJ_PROJECT;
		return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
	}
}