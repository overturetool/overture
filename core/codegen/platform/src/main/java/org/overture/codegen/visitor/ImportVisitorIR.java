package org.overture.codegen.visitor;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.modules.AAllImport;
import org.overture.ast.modules.AFunctionValueImport;
import org.overture.ast.modules.AOperationValueImport;
import org.overture.ast.modules.ATypeImport;
import org.overture.ast.modules.AValueValueImport;
import org.overture.ast.modules.PImport;
import org.overture.ast.modules.SValueImport;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.SDeclIR;
import org.overture.codegen.ir.SImportIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.declarations.AAllImportIR;
import org.overture.codegen.ir.declarations.AFunctionValueImportIR;
import org.overture.codegen.ir.declarations.AOperationValueImportIR;
import org.overture.codegen.ir.declarations.ATypeDeclIR;
import org.overture.codegen.ir.declarations.ATypeImportIR;
import org.overture.codegen.ir.declarations.AValueValueImportIR;
import org.overture.codegen.ir.declarations.SValueImportIR;
import org.overture.codegen.ir.name.ATokenNameIR;

public class ImportVisitorIR extends AbstractVisitorIR<IRInfo, SImportIR>
{
	@Override
	public SImportIR caseAAllImport(AAllImport node, IRInfo question)
			throws AnalysisException
	{
		return initImport(node, new AAllImportIR());
	}

	@Override
	public SImportIR caseATypeImport(ATypeImport node, IRInfo question)
			throws AnalysisException
	{
		ATypeImportIR typeImportCg = new ATypeImportIR();

		initImport(node, typeImportCg);

		SDeclIR typeDeclCg = node.getDef() != null
				? node.getDef().apply(question.getDeclVisitor(), question)
				: null;

		if (typeDeclCg instanceof ATypeDeclIR)
		{
			typeImportCg.setDecl((ATypeDeclIR) typeDeclCg);
		}

		return typeImportCg;
	}

	@Override
	public SImportIR caseAValueValueImport(AValueValueImport node,
			IRInfo question) throws AnalysisException
	{
		return initValueImport(node, new AValueValueImportIR(), question);
	}

	@Override
	public SImportIR caseAFunctionValueImport(AFunctionValueImport node,
			IRInfo question) throws AnalysisException
	{
		AFunctionValueImportIR funcImportCg = new AFunctionValueImportIR();
		initValueImport(node, funcImportCg, question);

		for (ILexNameToken typeParam : node.getTypeParams())
		{
			ATokenNameIR nameCg = new ATokenNameIR();
			nameCg.setName(typeParam.getName());
			funcImportCg.getTypeParams().add(nameCg);
		}

		return funcImportCg;
	}

	@Override
	public SImportIR caseAOperationValueImport(AOperationValueImport node,
			IRInfo question) throws AnalysisException
	{
		return initValueImport(node, new AOperationValueImportIR(), question);
	}

	private SImportIR initValueImport(SValueImport vdmImport,
			SValueImportIR irImport, IRInfo question) throws AnalysisException
	{
		initImport(vdmImport, irImport);

		STypeIR importTypeCg = vdmImport.getImportType() != null
				? vdmImport.getImportType().apply(question.getTypeVisitor(), question)
				: null;

		irImport.setImportType(importTypeCg);

		return irImport;
	}

	private SImportIR initImport(PImport vdmImport, SImportIR irImport)
	{
		String name = vdmImport.getName() != null
				? vdmImport.getName().getName() : null;
		String renamed = vdmImport.getRenamed() != null
				? vdmImport.getRenamed().getName() : null;
		String fromModuleName = vdmImport.getFrom() != null
				? vdmImport.getFrom().getName().getName() : null;

		irImport.setName(name);
		irImport.setRenamed(renamed);
		irImport.setFromModuleName(fromModuleName);

		return irImport;
	}
}
