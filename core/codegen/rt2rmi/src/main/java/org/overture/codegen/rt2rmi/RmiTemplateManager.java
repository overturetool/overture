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
import org.overture.codegen.merging.TemplateManager;
import org.overture.codegen.merging.TemplateStructure;

public class RmiTemplateManager extends TemplateManager
{

	public RmiTemplateManager(TemplateStructure templateStructure,
			Class<?> classRef)
	{
		super(templateStructure, classRef);
		initRmiNodes();
	}

	private void initRmiNodes()
	{
		// RT specific
		nodeTemplateFileNames.put(ARemoteContractDeclCG.class, templateStructure.DECL_PATH
				+ "RemoteContract");
		
		nodeTemplateFileNames.put(ARemoteContractImplDeclCG.class, templateStructure.DECL_PATH
				+ "RemoteContractImpl");
		
		nodeTemplateFileNames.put(ACpuDeploymentDeclCG.class, templateStructure.DECL_PATH
				+ "CPUdeployment");

		nodeTemplateFileNames.put(ARemoteInstanceDeclCG.class, templateStructure.DECL_PATH
				+ "RemoteInstance");
		
		nodeTemplateFileNames.put(AClientInstanceDeclCG.class, templateStructure.DECL_PATH
				+ "ClientInstance");
		
		nodeTemplateFileNames.put(ARMIregistryDeclCG.class, templateStructure.DECL_PATH
				+ "RMIregistry");
		
		nodeTemplateFileNames.put(ARMIServerDeclCG.class, templateStructure.DECL_PATH 
				+ "RMI_Server");
		
		nodeTemplateFileNames.put(ASynchTokenDeclCG.class, templateStructure.DECL_PATH
				+ "SynchToken");
		
		nodeTemplateFileNames.put(ASynchTokenInterfaceDeclCG.class, templateStructure.DECL_PATH
				+ "SynchTokenInterface");	

	}
}
