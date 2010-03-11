package org.overture.ide.builders.vdmj;

import org.eclipse.core.runtime.IStatus;
import org.overture.ide.core.ast.RootNode;
import org.overture.ide.core.builder.BuildParcitipant;
import org.overture.ide.core.utility.IVdmProject;

public class VdmPpBuilder extends BuildParcitipant
{

	public VdmPpBuilder() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public IStatus buileModelElements(IVdmProject project, RootNode rooList)
	{
		System.out.println("Building");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getContentTypeId()
	{
		// TODO Auto-generated method stub
		return "org.overture.ide.vdmpp.content-type";
	}

	@Override
	public String getNatureId()
	{
		// TODO Auto-generated method stub
		return "org.overture.ide.vdmpp.core.nature";
	}

}
