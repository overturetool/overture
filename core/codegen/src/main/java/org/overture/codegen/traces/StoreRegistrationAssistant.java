package org.overture.codegen.traces;

import java.util.Map;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.declarations.AVarDeclCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.ACallObjectExpStmCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.ANatNumericBasicTypeCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.trans.assistants.TransAssistantCG;

public class StoreRegistrationAssistant
{
	private IRInfo info;
	private TraceNames tracePrefixes;
	private Map<String, String> idConstNameMap;
	private TransAssistantCG transAssistant;
	
	public StoreRegistrationAssistant(IRInfo info, TraceNames tracePrefixes,
			Map<String, String> idConstNameMap, TransAssistantCG transAssistant)
	{
		super();
		this.info = info;
		this.tracePrefixes = tracePrefixes;
		this.idConstNameMap = idConstNameMap;
		this.transAssistant = transAssistant;
	}

	public void appendStoreRegStms(ABlockStmCG declBlock, STypeCG varType, String varName)
	{
		String idConstName = idConstNameMap.get(varName);
		
		if(idConstName == null)
		{
			idConstName = info.getTempVarNameGen().nextVarName(tracePrefixes.idConstNamePrefix());
		}
		
		declBlock.getStatements().add(transAssistant.wrap(consIdConstDecl(idConstName)));
		declBlock.getStatements().add(consStoreRegistration(idConstName, varType, varName));
		idConstNameMap.put(varName, idConstName);
	}

	public AVarDeclCG consIdConstDecl(String idConstName)
	{
		ANatNumericBasicTypeCG intType = new ANatNumericBasicTypeCG();
		AClassTypeCG idGenType = transAssistant.consClassType(tracePrefixes.idGeneratorClassName());
		SExpCG init = transAssistant.consInstanceCall(idGenType, tracePrefixes.idGeneratorVarName(),
				intType, tracePrefixes.idGeneratorIncrementMethodName());
		
		AVarDeclCG idConstDecl = transAssistant.consDecl(idConstName, intType.clone(), init);
		idConstDecl.setFinal(true);
		
		return idConstDecl;
	}
	
	public ACallObjectExpStmCG consStoreRegistration(String idConstName, STypeCG varType, String varName)
	{
		AClassTypeCG storageType = transAssistant.consClassType(tracePrefixes.storeClassName());
		String storeVarName = tracePrefixes.storeVarName();
		AIdentifierVarExpCG idVarExp = transAssistant.consIdentifierVar(idConstName, new ANatNumericBasicTypeCG());
		
		return transAssistant.consInstanceCallStm(storageType, storeVarName, tracePrefixes.storeRegisterMethodName(),
				idVarExp, transAssistant.consIdentifierVar(varName, varType));
	}
}
