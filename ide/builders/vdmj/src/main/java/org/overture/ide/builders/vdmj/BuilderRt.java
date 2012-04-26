package org.overture.ide.builders.vdmj;

import org.overture.ast.definitions.ABusClassDefinition;
import org.overture.ast.definitions.ACpuClassDefinition;
import org.overturetool.vdmjV2.Settings;
import org.overturetool.vdmjV2.lex.Dialect;
import org.overturetool.vdmjV2.messages.InternalException;

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
	public ExitStatus typeCheck()
	{
		try
		{
			classes.add(new ACpuClassDefinition());
  			classes.add(new ABusClassDefinition());
		}
		catch (Exception e)
		{
			throw new InternalException(11, "CPU or BUS creation failure");
		}

		return super.typeCheck();
	}

}
