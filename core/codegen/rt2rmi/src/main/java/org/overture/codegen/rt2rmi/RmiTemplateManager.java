package org.overture.codegen.rt2rmi;

import org.overture.cgrmi.extast.declarations.AClientInstanceDeclCG;
import org.overture.cgrmi.extast.declarations.ACpuDeploymentDeclCG;
import org.overture.cgrmi.extast.declarations.ARMIServerDeclCG;
import org.overture.cgrmi.extast.declarations.ARMIregistryDeclCG;
import org.overture.cgrmi.extast.declarations.ARemoteContractDeclCG;
import org.overture.cgrmi.extast.declarations.ARemoteContractImplDeclCG;
import org.overture.cgrmi.extast.declarations.ARemoteInstanceDeclCG;
import org.overture.cgrmi.extast.declarations.ASynchTokenDeclCG;
import org.overture.cgrmi.extast.declarations.ASynchTokenInterfaceDeclCG;
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
		
		setUserDefinedPath(ARemoteContractDeclCG.class, "RemoteContract");
		setUserDefinedPath(ARemoteContractImplDeclCG.class, "RemoteContractImpl");
		setUserDefinedPath(ARemoteContractImplDeclCG.class, "RemoteContractImpl");
		setUserDefinedPath(ACpuDeploymentDeclCG.class,"CPUdeployment");
		setUserDefinedPath(ARemoteInstanceDeclCG.class,"RemoteInstance");
		setUserDefinedPath(AClientInstanceDeclCG.class, "ClientInstance");
		setUserDefinedPath(ARMIregistryDeclCG.class,"RMIregistry");
		setUserDefinedPath(ARMIServerDeclCG.class, "RMI_Server");
		setUserDefinedPath(ASynchTokenDeclCG.class, "SynchToken");
		setUserDefinedPath(ASynchTokenInterfaceDeclCG.class,"SynchTokenInterface");	
	}
}
