package org.overture.typecheck.visitors;

import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.assistants.PDefinitionAssistantTC;
import org.overture.ast.definitions.assistants.PDefinitionListAssistantTC;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.modules.AAllImport;
import org.overture.ast.modules.AFunctionValueImport;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.modules.AOperationValueImport;
import org.overture.ast.modules.ATypeImport;
import org.overture.ast.modules.SValueImport;
import org.overture.ast.types.PType;
import org.overture.ast.types.SInvariantType;
import org.overture.ast.types.assistants.PTypeAssistantTC;
import org.overture.typecheck.FlatCheckedEnvironment;
import org.overture.typecheck.TypeCheckInfo;
import org.overture.typecheck.TypeCheckerErrors;
import org.overture.typecheck.TypeComparator;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.NameScope;

public class TypeCheckerImportsVisitor extends
		QuestionAnswerAdaptor<TypeCheckInfo, PType> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6883311293059829368L;
	final private QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor;
	
	
	public TypeCheckerImportsVisitor(TypeCheckVisitor typeCheckVisitor) {
		this.rootVisitor = typeCheckVisitor;
	}

	@Override
	public PType caseAAllImport(AAllImport node, TypeCheckInfo question) {
		return null; // Implicitly OK.
	}
	
	@Override
	public PType caseATypeImport(ATypeImport node, TypeCheckInfo question) {
		if (node.getDef() != null && node.getFrom() != null)
		{
			PDefinition def = node.getDef();
			LexNameToken name = node.getName();
			AModuleModules from = node.getFrom();
			def.setType((SInvariantType)PTypeAssistantTC.typeResolve(PDefinitionAssistantTC.getType(def),null,rootVisitor,question));
			PDefinition expdef = PDefinitionListAssistantTC.findType(from.getExportdefs(),name, null);

			if (expdef != null)
			{
				PType exptype = PTypeAssistantTC.typeResolve(expdef.getType(),null,rootVisitor,question);

				if (!TypeComparator.compatible(def.getType(), exptype))
				{
					TypeCheckerErrors.report(3192, "Type import of " + name + " does not match export from " + from.getName(),node.getLocation(),node);
					TypeCheckerErrors.detail2("Import", def.getType().toString() //TODO: .toDetailedString()
							, "Export", exptype.toString()); //TODO: .toDetailedString());
				}				
			}
		}
		return null;
	}
	
	@Override
	public PType defaultSValueImport(SValueImport node, TypeCheckInfo question) {
		PType type = node.getImportType();
		AModuleModules from = node.getFrom();
		LexNameToken name = node.getName();
		
		if (type != null && from != null)
		{
			type = PTypeAssistantTC.typeResolve(type, null, rootVisitor, question);
			PDefinition expdef = PDefinitionListAssistantTC.findName(from.getExportdefs(), name, NameScope.NAMES);

			if (expdef != null)
			{
    			PType exptype = PTypeAssistantTC.typeResolve(expdef.getType(), null, rootVisitor, question);

    			if (!TypeComparator.compatible(type, exptype))
    			{
    				TypeCheckerErrors.report(3194, "Type of value import " + name + " does not match export from " + from.getName(),node.getLocation(),node);
    				TypeCheckerErrors.detail2("Import", type.toString(), //TODO: .toDetailedString(), 
    						"Export", exptype.toString()); //TODO: .toDetailedString());
    			}
			}
		}
		return null;
	}
		
	
	@Override
	public PType caseAFunctionValueImport(AFunctionValueImport node,
			TypeCheckInfo question) {
		//TODO: This might need to be made in another way
		if (node.getTypeParams().size() == 0)
		{
			defaultSValueImport(node, question);
		}
		else
		{
    		List<PDefinition> defs = new Vector<PDefinition>();

    		for (LexNameToken pname: node.getTypeParams())
    		{
    			PDefinition p = 
    					AstFactory.newALocalDefinition(pname.location, pname, NameScope.NAMES, AstFactory.newAParameterType(pname));

    			PDefinitionAssistantTC.markUsed(p);
    			defs.add(p);
    		}

    		FlatCheckedEnvironment params =	new FlatCheckedEnvironment(
    			defs, question.env, NameScope.NAMES);

    		defaultSValueImport(node, new TypeCheckInfo(params,question.scope,question.qualifiers));
		}
		return null;
	}
	
	@Override
	public PType caseAOperationValueImport(AOperationValueImport node,
			TypeCheckInfo question) {
		return defaultSValueImport(node, question);
	}
}
