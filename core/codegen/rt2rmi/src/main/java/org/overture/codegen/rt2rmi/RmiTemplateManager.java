package org.overture.codegen.rt2rmi;

import org.overture.cgrmi.extast.declarations.AClientInstanceDeclIR;
import org.overture.cgrmi.extast.declarations.ACpuDeploymentDeclIR;
import org.overture.cgrmi.extast.declarations.ARMIServerDeclIR;
import org.overture.cgrmi.extast.declarations.ARMIregistryDeclIR;
import org.overture.cgrmi.extast.declarations.ARemoteContractDeclIR;
import org.overture.cgrmi.extast.declarations.ARemoteContractImplDeclIR;
import org.overture.cgrmi.extast.declarations.ARemoteInstanceDeclIR;
import org.overture.cgrmi.extast.declarations.ASynchTokenDeclIR;
import org.overture.cgrmi.extast.declarations.ASynchTokenInterfaceDeclIR;
import org.overture.codegen.vdm2java.JavaTemplateManager;

public class RmiTemplateManager extends JavaTemplateManager
{

	public RmiTemplateManager(String root)
	{
		super(root);
	}
	
	@Override
	protected void initNodeTemplateFileNames()
	{
		super.initNodeTemplateFileNames();

	}
}
