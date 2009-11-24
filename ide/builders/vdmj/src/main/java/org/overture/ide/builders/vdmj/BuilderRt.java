package org.overture.ide.builders.vdmj;

import org.overture.ide.vdmrt.core.VdmRtCorePluginConstants;
import org.overture.ide.vdmrt.core.VdmRtProjectNature;
import org.overturetool.vdmj.ExitStatus;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.definitions.BUSClassDefinition;
import org.overturetool.vdmj.definitions.CPUClassDefinition;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.messages.InternalException;

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
	
	@Override
	public ExitStatus typeCheck()
	{
		try
		{
			classes.add(new CPUClassDefinition());
  			classes.add(new BUSClassDefinition());
		}
		catch (Exception e)
		{
			throw new InternalException(11, "CPU or BUS creation failure");
		}

		return super.typeCheck();
	}

}
