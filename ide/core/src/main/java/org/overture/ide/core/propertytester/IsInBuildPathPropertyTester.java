package org.overture.ide.core.propertytester;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.core.resources.ModelBuildPath;
/**
 * Property: IsInBuildPath
 * If expectedValue is true it will return TRUE if the receiver is in the path. If the
 * expectedValue is false it will return TRUE if the receiver is not in the path.
 * @author kela
 *
 */
public class IsInBuildPathPropertyTester extends PropertyTester
{
	final static String PROPERTY = "IsInBuildPath";

	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue)
	{
		IVdmProject project = null;
		boolean checkIfInSourcePath = true;
		if (expectedValue instanceof Boolean)
		{
			checkIfInSourcePath = (Boolean) expectedValue;
		}
		if (receiver instanceof IProject)
		{
			project = (IVdmProject) ((IProject) receiver).getAdapter(IVdmProject.class);

		} else if (receiver instanceof IVdmProject)
		{
			project = (IVdmProject) receiver;
		}

		if (project != null && property.equalsIgnoreCase(PROPERTY))
		{
			ModelBuildPath path = project.getModelBuildPath();

			if (checkIfInSourcePath)
			{
				return path.contains((IProject) project.getAdapter(IProject.class));
			} else
			{
				return !path.contains((IProject) project.getAdapter(IProject.class));
			}

		} else if (receiver instanceof IFolder)
		{
			IFolder file = (IFolder) receiver;

			project = (IVdmProject) file.getProject().getAdapter(IVdmProject.class);

			if (project != null)
			{
				ModelBuildPath path = project.getModelBuildPath();
				if (property.equalsIgnoreCase(PROPERTY))
				{
					if (checkIfInSourcePath)
					{
						return path.getModelSrcPaths().contains(file);
					} else
					{
						return !path.getModelSrcPaths().contains(file);
					}
				}
			}
		}
		return false;
	}

}
