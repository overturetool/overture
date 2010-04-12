package org.overture.ide.core.propertytester;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.core.resources.VdmProject;


public class VdmResourcePropertyTester extends PropertyTester
{

	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue)
	{
		if(receiver instanceof IVdmProject)
		{
			if(property.equalsIgnoreCase("dialect") )
			{
				return ((IVdmProject) receiver).getDialect().name().equalsIgnoreCase(expectedValue.toString());
			}
		}else if(receiver instanceof IFile)
		{
			IFile file = (IFile) receiver;
			
			if(VdmProject.isVdmProject(file.getProject()))
			{
			
			if(property.equalsIgnoreCase("dialect"))
			{
				return VdmProject.createProject(file.getProject()).getDialect().name().equalsIgnoreCase(expectedValue.toString());
			}
			}
		}else if(receiver instanceof IFolder)
		{
			IFolder file = (IFolder) receiver;
			
			if(VdmProject.isVdmProject(file.getProject()))
			{
			
			if(property.equalsIgnoreCase("dialect"))
			{
				return VdmProject.createProject(file.getProject()).getDialect().name().equalsIgnoreCase(expectedValue.toString());
			}
			}
		}
		
		return false;
	}

}
