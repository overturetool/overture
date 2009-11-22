package org.overture.ide.builders.vdmj;

import org.overture.ide.vdmrt.core.VdmRtCorePluginConstants;
import org.overture.ide.vdmrt.core.VdmRtProjectNature;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.lex.Dialect;

/***
 * VDM RT builder
 * 
 * @author kela <extension<br>
 *         point="org.overture.ide.builder"><br>
 *         <builder<br>
 *         class="org.overture.ide.builders.vdmj.BuilderRt"><br>
 *         </builder><br>
 *         </extension><br>
 */
public class BuilderRt extends BuilderPp {

	public BuilderRt() {
		super();
		Settings.dialect = Dialect.VDM_RT;
	}

	@Override
	public String getNatureId() {
		return VdmRtProjectNature.VDM_RT_NATURE;
	}

	@Override
	public String getContentTypeId() {
		return VdmRtCorePluginConstants.CONTENT_TYPE;
	}

}
