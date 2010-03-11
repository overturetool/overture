//package org.overture.ide.builders.vdmj;
//
//import org.eclipse.core.runtime.CoreException;
//import org.eclipse.core.runtime.IStatus;
//import org.overture.ide.core.builder.AbstractVdmBuilder;
//
//
//public class VdmPpBuilder extends AbstractVdmBuilder
//{
//
//	public VdmPpBuilder() {
//		// TODO Auto-generated constructor stub
//	}
//
//	@SuppressWarnings("unchecked")
//	@Override
//	public IStatus buileModelElements(IVdmProject project, RootNode rooList)
//	{
//		System.out.println("Building");
//		Settings.dialect = Dialect.VDM_PP;
//		ClassList classes=null;
//		try
//		{
//			Settings.release = project.getLanguageVersion();
//			Settings.dynamictypechecks = project.hasDynamictypechecks();
//			Settings.invchecks = project.hasInvchecks();
//			Settings.postchecks = project.hasPostchecks();
//			Settings.prechecks = project.hasPrechecks();
//		} catch (CoreException e1)
//		{
//			e1.printStackTrace();
//		}
//		try {
//			 classes = rooList.getClassList();
//		} catch (NotAllowedException e) {
//			
//			e.printStackTrace();
//		}
//
//		return buileModelElements(project.getProject());
//	}
//
//	
//
//	@Override
//	public String getNatureId()
//	{
//		// TODO Auto-generated method stub
//		return "org.overture.ide.vdmpp.core.nature";
//	}
//
//}
