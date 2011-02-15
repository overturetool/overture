package org.overture.ide.core.propertytester;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.overture.ide.core.resources.IVdmProject;

public class VdmResourcePropertyTester extends PropertyTester
{

	private static final String PROPERTY = "dialect";

	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue)
	{
		if (receiver instanceof IVdmProject)
		{
			if (property.equalsIgnoreCase(PROPERTY))
			{
				return ((IVdmProject) receiver).getDialect().name()
						.equalsIgnoreCase(expectedValue.toString());
			}
		} else if (receiver instanceof IFile)
		{
			IFile file = (IFile) receiver;

			IVdmProject vdmProject = (IVdmProject) file.getProject()
					.getAdapter(IVdmProject.class);

			if (vdmProject != null)
			{

				if (property.equalsIgnoreCase(PROPERTY))
				{
					return vdmProject.getDialect().name().equalsIgnoreCase(
							expectedValue.toString());
				}
			}
		} else if (receiver instanceof IFolder)
		{
			IFolder file = (IFolder) receiver;
			IVdmProject vdmProject = (IVdmProject) file.getProject()
					.getAdapter(IVdmProject.class);

			if (vdmProject != null)
			{

				if (property.equalsIgnoreCase(PROPERTY))
				{
					return vdmProject.getDialect().name().equalsIgnoreCase(
							expectedValue.toString());
				}
			}
		}

		return false;
	}

}
