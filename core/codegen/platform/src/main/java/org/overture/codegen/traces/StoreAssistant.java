package org.overture.codegen.traces;

import java.util.HashMap;
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
import org.overture.codegen.cgast.types.AObjectTypeCG;
import org.overture.codegen.cgast.types.AUnionTypeCG;
import org.overture.codegen.cgast.types.AUnknownTypeCG;
import org.overture.codegen.trans.assistants.TransAssistantCG;

public class StoreAssistant
{
	private TraceNames tracePrefixes;
	private Map<String, String> idConstNameMap;
	private TransAssistantCG transAssistant;
	
	public StoreAssistant(TraceNames tracePrefixes, TransAssistantCG transAssistant)
	{
		super();
		this.tracePrefixes = tracePrefixes;
		this.idConstNameMap = new HashMap<String, String>();
		this.transAssistant = transAssistant;
	}
	
	public Map<String, String> getIdConstNameMap()
	{
		return idConstNameMap;
	}

	public void appendStoreRegStms(ABlockStmCG declBlock, String varName, String idConstName, boolean staticReg)
	{
		// Passing the variable type as the unknown type is not very accurate.
		// However, it simplifies the store registration.
		declBlock.getStatements().add(consStoreRegistration(idConstName, new AUnknownTypeCG(), varName, staticReg));
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
	
	public ACallObjectExpStmCG consStoreRegistration(String idConstName, STypeCG varType, String varName, boolean staticReg)
	{
		AIdentifierVarExpCG idVar = transAssistant.getInfo().getExpAssistant().consIdVar(varName, varType);

		return consStoreRegistration(idConstName, idVar, staticReg);
	}

	public ACallObjectExpStmCG consStoreRegistration(String idConstName, SExpCG subject, boolean staticReg)
	{
		AIdentifierVarExpCG idVarExp = transAssistant.getInfo().getExpAssistant().consIdVar(idConstName, new ANatNumericBasicTypeCG());
		AClassTypeCG storageType = transAssistant.consClassType(tracePrefixes.storeClassName());
		String storeVarName = tracePrefixes.storeVarName();
		
		String method = staticReg ? tracePrefixes.storeStaticRegistrationMethodName()
				: tracePrefixes.storeRegistrationMethodName();
		
		return transAssistant.consInstanceCallStm(storageType, storeVarName, method, idVarExp, subject);
	}
	
	public SExpCG consStoreLookup(AIdentifierVarExpCG node)
	{
		return consStoreLookup(node, false);
	}
	
	public SExpCG consStoreLookup(AIdentifierVarExpCG node, boolean noCast)
	{
		AClassTypeCG storeType = transAssistant.consClassType(tracePrefixes.storeClassName());
		
		AIdentifierVarExpCG idArg = transAssistant.getInfo().getExpAssistant().consIdVar(idConstNameMap.get(node.getName()), 
				new ANatNumericBasicTypeCG());
		
		SExpCG call = transAssistant.consInstanceCall(storeType, tracePrefixes.storeVarName(), 
				node.getType(), tracePrefixes.storeGetValueMethodName(), idArg);
		
		if (noCast || node.getType() instanceof AUnionTypeCG || node.getType() instanceof AUnknownTypeCG
				|| node.getType() instanceof AObjectTypeCG)
		{
			return call;
		} else
		{
			ACastUnaryExpCG cast = new ACastUnaryExpCG();
			cast.setType(node.getType().clone());
			cast.setExp(call);

			return cast;
		}
	}
}
