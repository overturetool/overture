package org.overture.ide.builders.vdmj;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.overturetool.vdmj.definitions.BUSClassDefinition;
import org.overturetool.vdmj.definitions.CPUClassDefinition;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.syntax.ParserException;
import org.overture.ide.vdmrt.core.*;
/***
 * VDM RT builder
 * 
 * @author kela
 *<extension<br>
 *        point="org.overture.ide.builder"><br>
 *     <builder<br>
 *           class="org.overture.ide.builders.vdmj.BuilderRt"><br>
 *     </builder><br>
 *  </extension><br>
 */
public class BuilderRt extends VdmjBuilder {
	@SuppressWarnings("unchecked")
	@Override
	public IStatus buileModelElements(IProject project, List modelElements) {
		ClassList modules = new ClassList();

		for (Object classDefinition : modelElements) {
			if (classDefinition instanceof ClassDefinition)
			{
				modules.add((ClassDefinition) classDefinition);
			}
		}
		try {
			modules.add(new CPUClassDefinition());
			modules.add(new BUSClassDefinition());
		} catch (ParserException e) {
			e.printStackTrace();
		} catch (LexException e) {
			e.printStackTrace();
		}
		IEclipseVdmj eclipseType = new EclipseVdmjRt(modules);
		return buileModelElements(project, eclipseType);
	}

	@Override
	public String getNatureId() {
		return VdmRtProjectNature.VDM_RT_NATURE;
	}

}
