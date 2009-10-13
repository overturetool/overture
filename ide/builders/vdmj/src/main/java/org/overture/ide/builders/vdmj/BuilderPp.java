package org.overture.ide.builders.vdmj;


import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.overture.ide.vdmpp.core.VdmPpProjectNature;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.ClassList;


/***
 * VDM PP builder
 * 
 * @author kela
 *<extension<br>
 *        point="org.overture.ide.builder"><br>
 *     <builder<br>
 *           class="org.overture.ide.builders.vdmj.BuilderPp"><br>
 *     </builder><br>
 *  </extension><br>
 */
public class BuilderPp extends VdmjBuilder
{
	@SuppressWarnings("unchecked")
	@Override
	public IStatus buileModelElements(IProject project, List modelElements)
	{
		ClassList modules = new ClassList();
		
		for (Object classDefinition : modelElements) {
			if(classDefinition instanceof ClassDefinition)
				modules.add((ClassDefinition) classDefinition);
		}
		
		
		IEclipseVdmj eclipseType = new EclipseVdmjPp(modules );
		
		return buileModelElements(project, eclipseType);
		
	}



	@Override
	public String getNatureId()
	{
		return VdmPpProjectNature.VDM_PP_NATURE;
	}

}
