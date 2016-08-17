package org.overture.codegen.traces;

import java.util.HashMap;
import java.util.Map;

import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.declarations.AVarDeclIR;
import org.overture.codegen.ir.expressions.ACastUnaryExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.statements.ACallObjectExpStmIR;
import org.overture.codegen.ir.types.AClassTypeIR;
import org.overture.codegen.ir.types.ANatNumericBasicTypeIR;
import org.overture.codegen.ir.types.AObjectTypeIR;
import org.overture.codegen.ir.types.AUnionTypeIR;
import org.overture.codegen.ir.types.AUnknownTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;

public class StoreAssistant
{
	private TraceNames tracePrefixes;
	private Map<String, String> idConstNameMap;
	private TransAssistantIR transAssistant;

	public StoreAssistant(TraceNames tracePrefixes,
			TransAssistantIR transAssistant)
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

	public void appendStoreRegStms(ABlockStmIR declBlock, String varName,
			String idConstName, boolean staticReg)
	{
		// Passing the variable type as the unknown type is not very accurate.
		// However, it simplifies the store registration.
		declBlock.getStatements().add(consStoreRegistration(idConstName, new AUnknownTypeIR(), varName, staticReg));
		idConstNameMap.put(varName, idConstName);
	}

	public AVarDeclIR consIdConstDecl(String idConstName)
	{
		ANatNumericBasicTypeIR intType = new ANatNumericBasicTypeIR();
		AClassTypeIR idGenType = transAssistant.consClassType(tracePrefixes.idGeneratorClassName());
		SExpIR init = transAssistant.consInstanceCall(idGenType, tracePrefixes.idGeneratorVarName(), intType, tracePrefixes.idGeneratorIncrementMethodName());

		AVarDeclIR idConstDecl = transAssistant.consDecl(idConstName, intType.clone(), init);
		idConstDecl.setFinal(true);

		return idConstDecl;
	}

	public ACallObjectExpStmIR consStoreRegistration(String idConstName,
			STypeIR varType, String varName, boolean staticReg)
	{
		AIdentifierVarExpIR idVar = transAssistant.getInfo().getExpAssistant().consIdVar(varName, varType);

		return consStoreRegistration(idConstName, idVar, staticReg);
	}

	public ACallObjectExpStmIR consStoreRegistration(String idConstName,
			SExpIR subject, boolean staticReg)
	{
		AIdentifierVarExpIR idVarExp = transAssistant.getInfo().getExpAssistant().consIdVar(idConstName, new ANatNumericBasicTypeIR());
		AClassTypeIR storageType = transAssistant.consClassType(tracePrefixes.storeClassName());
		String storeVarName = tracePrefixes.storeVarName();

		String method = staticReg
				? tracePrefixes.storeStaticRegistrationMethodName()
				: tracePrefixes.storeRegistrationMethodName();

		return transAssistant.consInstanceCallStm(storageType, storeVarName, method, idVarExp, subject);
	}

	public SExpIR consStoreLookup(AIdentifierVarExpIR node)
	{
		return consStoreLookup(node, false);
	}

	public SExpIR consStoreLookup(AIdentifierVarExpIR node, boolean noCast)
	{
		AClassTypeIR storeType = transAssistant.consClassType(tracePrefixes.storeClassName());

		AIdentifierVarExpIR idArg = transAssistant.getInfo().getExpAssistant().consIdVar(idConstNameMap.get(node.getName()), new ANatNumericBasicTypeIR());

		SExpIR call = transAssistant.consInstanceCall(storeType, tracePrefixes.storeVarName(), node.getType(), tracePrefixes.storeGetValueMethodName(), idArg);

		if (noCast || node.getType() instanceof AUnionTypeIR
				|| node.getType() instanceof AUnknownTypeIR
				|| node.getType() instanceof AObjectTypeIR)
		{
			return call;
		} else
		{
			ACastUnaryExpIR cast = new ACastUnaryExpIR();
			cast.setType(node.getType().clone());
			cast.setExp(call);

			return cast;
		}
	}
}
