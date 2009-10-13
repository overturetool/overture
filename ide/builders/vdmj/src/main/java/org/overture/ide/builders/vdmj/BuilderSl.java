package org.overture.ide.builders.vdmj;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.overture.ide.vdmsl.core.VdmSlProjectNature;
import org.overturetool.vdmj.modules.Module;
import org.overturetool.vdmj.modules.ModuleList;

/***
 * VDM SL builder
 * 
 * @author kela
 *<extension<br>
 *        point="org.overture.ide.builder"><br>
 *     <builder<br>
 *           class="org.overture.ide.builders.vdmj.BuilderSl"><br>
 *     </builder><br>
 *  </extension><br>
 */
public class BuilderSl extends VdmjBuilder {
	@SuppressWarnings("unchecked")
	@Override
	public IStatus buileModelElements(IProject project, List modelElements) {
		ModuleList modules = new ModuleList();

		for (Object classDefinition : modelElements) {
			if (classDefinition instanceof Module)
				modules.add((Module) classDefinition);
		}
		IEclipseVdmj eclipseType = new EclipseVdmjSl(modules);
		return buileModelElements(project, eclipseType);
	}

	@Override
	public String getNatureId() {
		return VdmSlProjectNature.VDM_SL_NATURE;
	}

}
