package org.overture.codegen.traces;

import java.util.Map;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.declarations.AVarDeclCG;
import org.overture.codegen.cgast.expressions.ACastUnaryExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.ACallObjectExpStmCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.ANatNumericBasicTypeCG;
import org.overture.codegen.cgast.types.AUnknownTypeCG;
import org.overture.codegen.trans.assistants.TransAssistantCG;

public class StoreAssistant
{
	private TraceNames tracePrefixes;
	private Map<String, String> idConstNameMap;
	private TransAssistantCG transAssistant;
	
	public StoreAssistant(TraceNames tracePrefixes,
			Map<String, String> idConstNameMap, TransAssistantCG transAssistant)
	{
		super();
		this.tracePrefixes = tracePrefixes;
		this.idConstNameMap = idConstNameMap;
		this.transAssistant = transAssistant;
	}

	public void appendStoreRegStms(ABlockStmCG declBlock, String varName, String idConstName)
	{
		// Passing the variable type as the unknown type is not very accurate.
		// However, it simplifies the store registration.
		declBlock.getStatements().add(consStoreRegistration(idConstName, new AUnknownTypeCG(), varName));
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
	
	public ACastUnaryExpCG consStoreLookup(AIdentifierVarExpCG node)
	{
		AClassTypeCG storeType = transAssistant.consClassType(tracePrefixes.storeClassName());
		
		AIdentifierVarExpCG idArg = transAssistant.consIdentifierVar(idConstNameMap.get(node.getName()), 
				new ANatNumericBasicTypeCG());
		
		SExpCG call = transAssistant.consInstanceCall(storeType, tracePrefixes.storeVarName(), 
				node.getType(), tracePrefixes.storeGetValueMethodName(), idArg);
		
		ACastUnaryExpCG cast = new ACastUnaryExpCG();
		cast.setType(node.getType().clone());
		cast.setExp(call);
		
		return cast;
	}
}
