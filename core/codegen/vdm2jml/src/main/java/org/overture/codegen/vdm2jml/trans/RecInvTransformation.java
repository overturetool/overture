package org.overture.codegen.vdm2jml.trans;

import org.apache.log4j.Logger;
import org.overture.ast.definitions.SFunctionDefinition;
import org.overture.ast.definitions.SOperationDefinition;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.node.INode;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.SourceNode;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.declarations.AFormalParamLocalParamIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.ARecordDeclIR;
import org.overture.codegen.ir.expressions.AExplicitVarExpIR;
import org.overture.codegen.ir.expressions.AFieldExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.ir.types.ABoolBasicTypeIR;
import org.overture.codegen.ir.types.AClassTypeIR;
import org.overture.codegen.ir.types.AMethodTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2java.JavaCodeGen;
import org.overture.codegen.vdm2java.JavaCodeGenUtil;

public class RecInvTransformation extends DepthFirstAnalysisAdaptor
{
	private JavaCodeGen javaGen;
	private String paramName;
	private ARecordDeclIR rec;

	private Logger log = Logger.getLogger(this.getClass().getName());

	public RecInvTransformation(JavaCodeGen javaGen, ARecordDeclIR rec)
			throws AnalysisException
	{
		this.javaGen = javaGen;
		this.rec = rec;
		changeRecInvSignature();
	}

	private void changeRecInvSignature() throws AnalysisException
	{
		if (!(rec.getInvariant() instanceof AMethodDeclIR))
		{
			log.error("Expected invariant to be a method declaration. Got: "
					+ rec.getInvariant());
			terminate();
		}

		AMethodDeclIR invMethod = (AMethodDeclIR) rec.getInvariant();

		if (invMethod.getFormalParams().size() != 1)
		{
			log.error("Expected invariant to take a single argument. Instead it takes "
					+ invMethod.getFormalParams().size());

			if (invMethod.getFormalParams().isEmpty())
			{
				terminate();
			}
		}

		AFormalParamLocalParamIR param = invMethod.getFormalParams().getFirst();

		if (!(param.getPattern() instanceof AIdentifierPatternIR))
		{
			log.error("Expected pattern of formal parameter to be an identifier pattern at this point. Got "
					+ param.getPattern());

			terminate();
		}

		// First update the signature of the invariant method to take the fields

		invMethod.setMethodType(null);
		invMethod.getFormalParams().clear();

		AMethodTypeIR newMethodType = new AMethodTypeIR();
		newMethodType.setResult(new ABoolBasicTypeIR());
		invMethod.setMethodType(newMethodType);

		for (AFieldDeclIR f : rec.getFields())
		{
			newMethodType.getParams().add(f.getType().clone());

			AFormalParamLocalParamIR nextParam = new AFormalParamLocalParamIR();
			nextParam.setPattern(javaGen.getInfo().getPatternAssistant().consIdPattern(consUniqueName(f.getName())));
			nextParam.setType(f.getType().clone());

			invMethod.getFormalParams().add(nextParam);
		}

		this.paramName = ((AIdentifierPatternIR) param.getPattern()).getName();
	}

	private void terminate() throws AnalysisException
	{
		throw new AnalysisException("Invalid record invariant - transformation cannot be applied. See error log.");
	}

	@Override
	public void caseAExplicitVarExpIR(AExplicitVarExpIR node)
			throws AnalysisException
	{
		String pack = javaGen.getJavaSettings().getJavaRootPackage();

		if (JavaCodeGenUtil.isValidJavaPackage(pack))
		{
			STypeIR type = node.getClassType();

			if (type instanceof AClassTypeIR)
			{
				AClassTypeIR classType = (AClassTypeIR) type;
				classType.setName(pack + "." + classType.getName());
			} else
			{
				log.error("Expected type of explicit variable to be a class type at this point. Got: "
						+ type);
			}
		}
	}

	@Override
	public void caseAIdentifierVarExpIR(AIdentifierVarExpIR node)
			throws AnalysisException
	{
		if (node.parent() instanceof AFieldExpIR
				&& node.getName().equals(paramName))
		{
			AFieldExpIR field = (AFieldExpIR) node.parent();

			TransAssistantIR assistant = javaGen.getTransAssistant();
			AIdentifierVarExpIR replField = javaGen.getInfo().getExpAssistant().consIdVar(consUniqueName(field.getMemberName()), field.getType().clone());
			assistant.replaceNodeWith(field, replField);
		} else
		{
			SourceNode sourceNode = node.getSourceNode();
			if (sourceNode != null)
			{
				INode vdmNode = sourceNode.getVdmNode();

				if (vdmNode instanceof AVariableExp)
				{
					AVariableExp varExp = (AVariableExp) vdmNode;

					if (varExp.getVardef() instanceof SFunctionDefinition
							|| varExp.getVardef() instanceof SOperationDefinition)
					{
						ADefaultClassDeclIR encClass = rec.getAncestor(ADefaultClassDeclIR.class);

						if (encClass != null)
						{
							String defClass = "";

							if (JavaCodeGenUtil.isValidJavaPackage(encClass.getPackage()))
							{
								defClass += encClass.getPackage() + ".";
							}

							defClass += encClass.getName();

							AExplicitVarExpIR func = new AExplicitVarExpIR();

							AClassTypeIR classType = new AClassTypeIR();
							classType.setName(defClass);

							func.setClassType(classType);
							func.setIsLambda(false);
							func.setIsLocal(false);
							func.setSourceNode(sourceNode);
							func.setName(node.getName());

							javaGen.getTransAssistant().replaceNodeWith(node, func);
						} else
						{
							log.error("Could not find enclosing class of record "
									+ rec.getName());
						}
					}
				}
			}
		}
	}

	/**
	 * TODO: Constructing names like this will work since names on the form _<name> cannot appear in a VDM model. What
	 * is not so nice about this approach is that it uses the naming conventions of an old name in the IR. Please note
	 * that since this method is used to construct names that appear inside an invariant no old name will ever appear. A
	 * better solution than the current one would be to pick a name that is not already used in the scope the name is
	 * constructed for.
	 */
	private String consUniqueName(String name)
	{
		return "_" + name;
	}
}
