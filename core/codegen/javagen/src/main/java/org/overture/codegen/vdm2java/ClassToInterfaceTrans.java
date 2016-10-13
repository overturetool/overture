package org.overture.codegen.vdm2java;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.BooleanUtils;
import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.node.INode;
import org.overture.codegen.assistant.AssistantBase;
import org.overture.codegen.ir.IRGeneratedTag;
import org.overture.codegen.ir.PIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.ACpuClassDeclIR;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.declarations.AInterfaceDeclIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.ASystemClassDeclIR;
import org.overture.codegen.ir.declarations.SClassDeclIR;
import org.overture.codegen.ir.name.ATokenNameIR;
import org.overture.codegen.trans.ITotalTransformation;
import org.overture.codegen.trans.assistants.TransAssistantIR;

public class ClassToInterfaceTrans extends DepthFirstAnalysisAdaptor implements ITotalTransformation {

	private PIR result;

	private Map<String, Boolean> isFullyAbstract;
	private Map<String, AInterfaceDeclIR> interfaces;

	public ClassToInterfaceTrans(TransAssistantIR assist) {

		this.isFullyAbstract = new HashMap<>();
		this.interfaces = new HashMap<>();

		for (SClassDeclIR c : assist.getInfo().getClasses()) {
			INode vdmNode = AssistantBase.getVdmNode(c);

			// AVoid system as CPU classes
			if (vdmNode instanceof AClassClassDefinition) {
				SClassDefinition vdmClazz = (SClassDefinition) vdmNode;
				isFullyAbstract.put(c.getName(), assist.getInfo().getDeclAssistant().isFullyAbstract(vdmClazz, assist.getInfo()));
				interfaces.put(c.getName(), convertToInterface(c));
			}
			else
			{
				isFullyAbstract.put(c.getName(), false);
			}
			// This transformation is only helpful for IR classes that originate
			// from VDM classes. So we simply ignore classes that originate from
			// modules since SL does not support inheritance anyway.
		}
	}
	
	@Override
	public void caseASystemClassDeclIR(ASystemClassDeclIR node) throws AnalysisException {
		
		this.result = node;
	}
	
	@Override
	public void caseACpuClassDeclIR(ACpuClassDeclIR node) throws AnalysisException {
		
		this.result = node;
	}

	@Override
	public void caseADefaultClassDeclIR(ADefaultClassDeclIR node) throws AnalysisException {

		if (isFullyAbstract(node.getName())) {

			result = interfaces.get(node.getName());

		} else {
			// node remains a class
			result = node;

			List<ATokenNameIR> toMove = new LinkedList<>();
			for (ATokenNameIR s : node.getSuperNames()) {

				if (isFullyAbstract(s.getName())) {
					toMove.add(s);
				}
			}
			
			for (ATokenNameIR m : toMove) {
				node.getSuperNames().remove(m);
				node.getInterfaces().add(interfaces.get(m.getName()).clone());
			}
		}
	}

	private AInterfaceDeclIR convertToInterface(SClassDeclIR c) {
		List<AFieldDeclIR> clonedFields = new LinkedList<>();

		for (AFieldDeclIR f : c.getFields()) {
			clonedFields.add(f.clone());
		}

		List<AMethodDeclIR> clonedMethods = new LinkedList<>();

		for (AMethodDeclIR m : c.getMethods()) {
			if (!m.getIsConstructor() && !(m.getTag() instanceof IRGeneratedTag)) {
				clonedMethods.add(m.clone());
			}
		}

		AInterfaceDeclIR inter = new AInterfaceDeclIR();
		inter.setFields(clonedFields);
		inter.setMetaData(c.getMetaData());
		inter.setMethodSignatures(clonedMethods);
		inter.setName(c.getName());
		inter.setPackage(c.getPackage());
		inter.setSourceNode(c.getSourceNode());
		inter.setTag(c.getTag());

		for (ATokenNameIR n : c.getSuperNames()) {
			inter.getExtension().add(n.clone());
		}

		return inter;
	}
	
	private boolean isFullyAbstract(String name)
	{
		return BooleanUtils.isTrue(isFullyAbstract.get(name));
	}

	@Override
	public PIR getResult() {

		return result;
	}
}
