package org.overture.codegen.visitor;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.modules.AAllExport;
import org.overture.ast.modules.AFunctionExport;
import org.overture.ast.modules.AOperationExport;
import org.overture.ast.modules.ATypeExport;
import org.overture.ast.modules.AValueExport;
import org.overture.ast.modules.PExport;
import org.overture.ast.types.PType;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.SDeclIR;
import org.overture.codegen.ir.SExportIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.declarations.AAllExportIR;
import org.overture.codegen.ir.declarations.AFunctionExportIR;
import org.overture.codegen.ir.declarations.AOperationExportIR;
import org.overture.codegen.ir.declarations.ATypeExportIR;
import org.overture.codegen.ir.declarations.AValueExportIR;
import org.overture.codegen.ir.name.ATokenNameIR;

public class ExportVisitorIR extends AbstractVisitorIR<IRInfo, SExportIR>
{
	@Override
	public SExportIR caseAAllExport(AAllExport node, IRInfo question)
			throws AnalysisException
	{
		return addDecl(node, new AAllExportIR(), question);
	}

	@Override
	public SExportIR caseAFunctionExport(AFunctionExport node, IRInfo question)
			throws AnalysisException
	{
		AFunctionExportIR funcExportCg = new AFunctionExportIR();

		funcExportCg.setNameList(consNames(node.getNameList()));
		funcExportCg.setExportType(consExportType(node.getExportType(), question));

		return addDecl(node, funcExportCg, question);
	}

	@Override
	public SExportIR caseAOperationExport(AOperationExport node,
			IRInfo question) throws AnalysisException
	{
		AOperationExportIR opExportCg = new AOperationExportIR();

		opExportCg.setNameList(consNames(node.getNameList()));
		opExportCg.setExportType(consExportType(node.getExportType(), question));

		return addDecl(node, opExportCg, question);
	}

	@Override
	public SExportIR caseATypeExport(ATypeExport node, IRInfo question)
			throws AnalysisException
	{
		ATypeExportIR typeExportCg = new ATypeExportIR();

		typeExportCg.setName(node.getName() != null ? node.getName().getName()
				: null);
		typeExportCg.setStruct(node.getStruct());

		return addDecl(node, typeExportCg, question);
	}

	@Override
	public SExportIR caseAValueExport(AValueExport node, IRInfo question)
			throws AnalysisException
	{
		AValueExportIR valueExportCg = new AValueExportIR();

		valueExportCg.setNameList(consNames(node.getNameList()));
		valueExportCg.setExportType(consExportType(node.getExportType(), question));

		return addDecl(node, valueExportCg, question);
	}

	private STypeIR consExportType(PType type, IRInfo question)
			throws AnalysisException
	{
		if (type != null)
		{
			return type.apply(question.getTypeVisitor(), question);
		} else
		{
			return null;
		}
	}

	private List<ATokenNameIR> consNames(List<ILexNameToken> vdmNames)
	{
		List<ATokenNameIR> namesCg = new LinkedList<ATokenNameIR>();

		for (ILexNameToken vdmName : vdmNames)
		{
			ATokenNameIR nextNameCg = new ATokenNameIR();
			nextNameCg.setName(vdmName.getName());

			namesCg.add(nextNameCg);
		}

		return namesCg;
	}

	private SExportIR addDecl(PExport vdmImport, SExportIR irImport,
			IRInfo question) throws AnalysisException
	{
		for (PDefinition defItem : vdmImport.getDefinition())
		{
			SDeclIR declItemCg = defItem.apply(question.getDeclVisitor(), question);

			if (declItemCg != null)
			{
				irImport.getDecl().add(declItemCg);
			} else
			{
				return null;
			}
		}

		return irImport;
	}
}
