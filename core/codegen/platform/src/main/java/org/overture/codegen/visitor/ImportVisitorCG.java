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
import org.overture.codegen.cgast.SDeclCG;
import org.overture.codegen.cgast.SImportCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.declarations.AAllImportCG;
import org.overture.codegen.cgast.declarations.AFunctionValueImportCG;
import org.overture.codegen.cgast.declarations.AOperationValueImportCG;
import org.overture.codegen.cgast.declarations.ATypeDeclCG;
import org.overture.codegen.cgast.declarations.ATypeImportCG;
import org.overture.codegen.cgast.declarations.AValueValueImportCG;
import org.overture.codegen.cgast.declarations.SValueImportCG;
import org.overture.codegen.cgast.name.ATokenNameCG;
import org.overture.codegen.ir.IRInfo;

public class ImportVisitorCG extends AbstractVisitorCG<IRInfo, SImportCG>
{
	@Override
	public SImportCG caseAAllImport(AAllImport node, IRInfo question)
			throws AnalysisException
	{
		return initImport(node, new AAllImportCG());
	}

	@Override
	public SImportCG caseATypeImport(ATypeImport node, IRInfo question)
			throws AnalysisException
	{
		ATypeImportCG typeImportCg = new ATypeImportCG();

		initImport(node, typeImportCg);

		SDeclCG typeDeclCg = node.getDef() != null ? node.getDef().apply(question.getDeclVisitor(), question)
				: null;

		if (typeDeclCg instanceof ATypeDeclCG)
		{
			typeImportCg.setDecl((ATypeDeclCG) typeDeclCg);
		}

		return typeImportCg;
	}

	@Override
	public SImportCG caseAValueValueImport(AValueValueImport node,
			IRInfo question) throws AnalysisException
	{
		return initValueImport(node, new AValueValueImportCG(), question);
	}

	@Override
	public SImportCG caseAFunctionValueImport(AFunctionValueImport node,
			IRInfo question) throws AnalysisException
	{
		AFunctionValueImportCG funcImportCg = new AFunctionValueImportCG();
		initValueImport(node, funcImportCg, question);

		for (ILexNameToken typeParam : node.getTypeParams())
		{
			ATokenNameCG nameCg = new ATokenNameCG();
			nameCg.setName(typeParam.getName());
			funcImportCg.getTypeParams().add(nameCg);
		}

		return funcImportCg;
	}

	@Override
	public SImportCG caseAOperationValueImport(AOperationValueImport node,
			IRInfo question) throws AnalysisException
	{
		return initValueImport(node, new AOperationValueImportCG(), question);
	}

	private SImportCG initValueImport(SValueImport vdmImport,
			SValueImportCG irImport, IRInfo question) throws AnalysisException
	{
		initImport(vdmImport, irImport);

		STypeCG importTypeCg = vdmImport.getImportType() != null ? vdmImport.getImportType().apply(question.getTypeVisitor(), question)
				: null;

		irImport.setImportType(importTypeCg);

		return irImport;
	}

	private SImportCG initImport(PImport vdmImport, SImportCG irImport)
	{
		String name = vdmImport.getName() != null ? vdmImport.getName().getName()
				: null;
		String renamed = vdmImport.getRenamed() != null ? vdmImport.getRenamed().getName()
				: null;
		String fromModuleName = vdmImport.getFrom() != null ? vdmImport.getFrom().getName().getName()
				: null;

		irImport.setName(name);
		irImport.setRenamed(renamed);
		irImport.setFromModuleName(fromModuleName);

		return irImport;
	}
}
