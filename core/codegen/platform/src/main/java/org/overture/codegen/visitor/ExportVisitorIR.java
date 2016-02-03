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
import org.overture.codegen.cgast.SDeclCG;
import org.overture.codegen.cgast.SExportCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.declarations.AAllExportCG;
import org.overture.codegen.cgast.declarations.AFunctionExportCG;
import org.overture.codegen.cgast.declarations.AOperationExportCG;
import org.overture.codegen.cgast.declarations.ATypeExportCG;
import org.overture.codegen.cgast.declarations.AValueExportCG;
import org.overture.codegen.cgast.name.ATokenNameCG;
import org.overture.codegen.ir.IRInfo;

public class ExportVisitorCG extends AbstractVisitorCG<IRInfo, SExportCG>
{
	@Override
	public SExportCG caseAAllExport(AAllExport node, IRInfo question)
			throws AnalysisException
	{
		return addDecl(node, new AAllExportCG(), question);
	}

	@Override
	public SExportCG caseAFunctionExport(AFunctionExport node, IRInfo question)
			throws AnalysisException
	{
		AFunctionExportCG funcExportCg = new AFunctionExportCG();
		
		funcExportCg.setNameList(consNames(node.getNameList()));
		funcExportCg.setExportType(consExportType(node.getExportType(), question));
		
		return addDecl(node, funcExportCg, question);
	}

	@Override
	public SExportCG caseAOperationExport(AOperationExport node, IRInfo question)
			throws AnalysisException
	{
		AOperationExportCG opExportCg = new AOperationExportCG();
		
		opExportCg.setNameList(consNames(node.getNameList()));
		opExportCg.setExportType(consExportType(node.getExportType(), question));
		
		return addDecl(node, opExportCg, question);
	}

	@Override
	public SExportCG caseATypeExport(ATypeExport node, IRInfo question)
			throws AnalysisException
	{
		ATypeExportCG typeExportCg = new ATypeExportCG();
		
		typeExportCg.setName(node.getName() != null ? node.getName().getName() : null);
		typeExportCg.setStruct(node.getStruct());
		
		return addDecl(node, typeExportCg, question);
	}

	@Override
	public SExportCG caseAValueExport(AValueExport node, IRInfo question)
			throws AnalysisException
	{
		AValueExportCG valueExportCg = new AValueExportCG();
		
		valueExportCg.setNameList(consNames(node.getNameList()));
		valueExportCg.setExportType(consExportType(node.getExportType(), question));
		
		return addDecl(node, valueExportCg, question);
	}

	private STypeCG consExportType(PType type, IRInfo question) throws AnalysisException
	{
		if(type != null)
		{
			return type.apply(question.getTypeVisitor(), question);
		}
		else
		{
			return null;
		}
	}
	
	private List<ATokenNameCG> consNames(List<ILexNameToken> vdmNames)
	{
		List<ATokenNameCG> namesCg = new LinkedList<ATokenNameCG>();
		
		for(ILexNameToken vdmName : vdmNames)
		{
			ATokenNameCG nextNameCg = new ATokenNameCG();
			nextNameCg.setName(vdmName.getName());
			
			namesCg.add(nextNameCg);
		}
		
		return namesCg;
	}
	
	private SExportCG addDecl(PExport vdmImport, SExportCG irImport,
			IRInfo question) throws AnalysisException
	{
		for(PDefinition defItem : vdmImport.getDefinition())
		{
			SDeclCG declItemCg = defItem.apply(question.getDeclVisitor(), question);
			
			if(declItemCg != null)
			{
				irImport.getDecl().add(declItemCg);
			}
			else
			{
				return null;
			}
		}
		
		return irImport;
	}
}
