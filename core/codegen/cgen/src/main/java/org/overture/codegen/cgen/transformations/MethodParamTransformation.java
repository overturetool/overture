package org.overture.codegen.cgen.transformations;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.node.INode;
import org.overture.ast.types.AIntNumericBasicType;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AFormalParamLocalParamCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.types.AIntNumericBasicTypeCG;
import org.overture.codegen.trans.assistants.TransAssistantCG;

public class MethodParamTransformation extends DepthFirstAnalysisAdaptor
{

	private TransAssistantCG transformationAssistant;

	public MethodParamTransformation(TransAssistantCG transformationAssistant)
	{
		this.transformationAssistant = transformationAssistant;
	}

	// @SuppressWarnings("unchecked")

	@Override
	public void caseAMethodDeclCG(AMethodDeclCG node) throws AnalysisException
	{
		if (!node.getIsConstructor())
		{
			LinkedList<AFormalParamLocalParamCG> formalPar = node.getFormalParams();

			LinkedList<AFormalParamLocalParamCG> f = new LinkedList<>();

			AFormalParamLocalParamCG cl = new AFormalParamLocalParamCG();
			AIdentifierPatternCG id = new AIdentifierPatternCG();

			AIntNumericBasicTypeCG ty = new AIntNumericBasicTypeCG();

			// Get enclosing class
			AClassDeclCG classname = node.getAncestor(AClassDeclCG.class);

			INode vdmclass = classname.getSourceNode().getVdmNode();
			/*
			 * if (vdmclass instanceof AClassClassDefinition) { AClassClassDefinition vdm = (AClassClassDefinition)
			 * vdmclass; System.out.println("For method: " + node.getName() + " in class: " + vdm.getName());
			 * LinkedList<PDefinition> in = vdm.getAllInheritedDefinitions(); for (PDefinition i : in) {
			 * System.out.println(i.getName()); } }
			 */
			// Create the special new parameter for each operation
			cl.setTag("class");
			id.setName("this"); // This one gets printed
			cl.setPattern(id);
			cl.setType(ty);

			f.add(cl);
			f.addAll((List<? extends AFormalParamLocalParamCG>) formalPar.clone());
			node.setFormalParams(f);

		}
	}

}
