package org.overture.ide.builders.vdmj;

import org.overture.ast.definitions.ABusClassDefinition;
import org.overture.ast.definitions.ACpuClassDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.ast.messages.InternalException;
import org.overture.config.Settings;

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
