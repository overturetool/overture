/*
 * #%~
 * Combinatorial Testing
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
package org.overture.ide.plugins.combinatorialtesting.views;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.modules.AModuleModules;
import org.overture.ide.plugins.combinatorialtesting.OvertureTracesPlugin;
import org.overture.ide.plugins.combinatorialtesting.views.treeView.NotYetReadyTreeNode;
import org.overture.ide.plugins.combinatorialtesting.views.treeView.ProjectTreeNode;
import org.overture.ide.plugins.combinatorialtesting.views.treeView.TraceTestTreeNode;
import org.overture.ide.plugins.combinatorialtesting.views.treeView.TraceTreeNode;
import org.overture.interpreter.traces.Verdict;

public class ViewLabelProvider extends LabelProvider
{

	@Override
	public String getText(Object obj)
	{
		if (obj instanceof SClassDefinition)
		{
			return ((SClassDefinition) obj).getName().getName();
		} else if (obj instanceof AModuleModules)
		{
			return ((AModuleModules) obj).getName().getName();
		}
		return obj.toString();
	}

	@Override
	public Image getImage(Object obj)
	{
		if (obj instanceof ProjectTreeNode)
		{
			String imageKey = IDE.SharedImages.IMG_OBJ_PROJECT;
			return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
		}
		if (/* obj instanceof ClassTreeNode || */obj instanceof SClassDefinition
				|| obj instanceof AModuleModules)
		{
			return OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_TRACE_CLASS).createImage();
		}
		if (obj instanceof TraceTreeNode)
		{
			return OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_TRACE).createImage();
		}
		if (obj instanceof NotYetReadyTreeNode)
		{
			return OvertureTracesPlugin.getImageDescriptor(OvertureTracesPlugin.IMG_TRACE_TEST_CASE_UNKNOWN).createImage();
		}
		if (obj instanceof TraceTestTreeNode)
		{
			String imgPath = OvertureTracesPlugin.IMG_TRACE_TEST_CASE_UNKNOWN;
			Verdict status = ((TraceTestTreeNode) obj).getStatus();
			if (status == Verdict.PASSED)
			{
				imgPath = OvertureTracesPlugin.IMG_TRACE_TEST_CASE_SUCCES;
			} else if (status == null)
			{
				imgPath = OvertureTracesPlugin.IMG_TRACE_TEST_CASE_UNKNOWN;
			} else if (status == Verdict.INCONCLUSIVE)
			{
				imgPath = OvertureTracesPlugin.IMG_TRACE_TEST_CASE_UNDETERMINED;
			} else if (status == Verdict.FAILED)
			{
				imgPath = OvertureTracesPlugin.IMG_TRACE_TEST_CASE_FAIL;
			} else if (status == Verdict.SKIPPED)
			{
				imgPath = OvertureTracesPlugin.IMG_TRACE_TEST_CASE_SKIPPED;
			}

			if (((TraceTestTreeNode) obj).hasRunTimeError())
			{
				imgPath = OvertureTracesPlugin.IMG_ERROR;
			}

			return OvertureTracesPlugin.getImageDescriptor(imgPath).createImage();
		}
		String imageKey = IDE.SharedImages.IMG_OBJ_PROJECT;
		return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
	}
}
