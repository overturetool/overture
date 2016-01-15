package org.overture.codegen.vdm2jml.trans;

import org.overture.ast.definitions.SFunctionDefinition;
import org.overture.ast.definitions.SOperationDefinition;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.node.INode;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.ADefaultClassDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AFormalParamLocalParamCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.ARecordDeclCG;
import org.overture.codegen.cgast.expressions.AExplicitVarExpCG;
import org.overture.codegen.cgast.expressions.AFieldExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.AMethodTypeCG;
import org.overture.codegen.ir.SourceNode;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.trans.assistants.TransAssistantCG;
import org.overture.codegen.vdm2java.JavaCodeGen;
import org.overture.codegen.vdm2java.JavaCodeGenUtil;

public class RecInvTransformation extends DepthFirstAnalysisAdaptor
{
	private JavaCodeGen javaGen;
	private String paramName;
	private ARecordDeclCG rec;

	public RecInvTransformation(JavaCodeGen javaGen, ARecordDeclCG rec)
			throws AnalysisException
	{
		this.javaGen = javaGen;
		this.rec = rec;
		changeRecInvSignature();
	}

	private void changeRecInvSignature() throws AnalysisException
	{
		if (!(rec.getInvariant() instanceof AMethodDeclCG))
		{
			Logger.getLog().printErrorln("Expected invariant to be a method declaration. Got: "
					+ rec.getInvariant() + " in '"
					+ this.getClass().getSimpleName() + "'");
			terminate();
		}

		AMethodDeclCG invMethod = (AMethodDeclCG) rec.getInvariant();

		if (invMethod.getFormalParams().size() != 1)
		{
			Logger.getLog().printErrorln("Expected invariant to take a single argument. Instead it takes "
					+ invMethod.getFormalParams().size() + " in '"
					+ this.getClass().getSimpleName() + "'");

			if (invMethod.getFormalParams().isEmpty())
			{
				terminate();
			}
		}

		AFormalParamLocalParamCG param = invMethod.getFormalParams().getFirst();

		if (!(param.getPattern() instanceof AIdentifierPatternCG))
		{
			Logger.getLog().printErrorln("Expected pattern of formal parameter to be an identifier pattern at this point. Got "
					+ param.getPattern() + " in '"
					+ this.getClass().getSimpleName() + "'");

			terminate();
		}

		// First update the signature of the invariant method to take the fields

		invMethod.setMethodType(null);
		invMethod.getFormalParams().clear();

		AMethodTypeCG newMethodType = new AMethodTypeCG();
		newMethodType.setResult(new ABoolBasicTypeCG());
		invMethod.setMethodType(newMethodType);

		for (AFieldDeclCG f : rec.getFields())
		{
			newMethodType.getParams().add(f.getType().clone());

			AFormalParamLocalParamCG nextParam = new AFormalParamLocalParamCG();
			nextParam.setPattern(javaGen.getInfo().getPatternAssistant().consIdPattern(consUniqueName(f.getName())));
			nextParam.setType(f.getType().clone());

			invMethod.getFormalParams().add(nextParam);
		}

		this.paramName = ((AIdentifierPatternCG) param.getPattern()).getName();
	}

	private void terminate() throws AnalysisException
	{
		throw new AnalysisException("Invalid record invariant - transformation cannot be applied. See error log.");
	}

	@Override
	public void caseAExplicitVarExpCG(AExplicitVarExpCG node)
			throws AnalysisException
	{
		String pack = javaGen.getJavaSettings().getJavaRootPackage();

		if (JavaCodeGenUtil.isValidJavaPackage(pack))
		{
			STypeCG type = node.getClassType();

			if (type instanceof AClassTypeCG)
			{
				AClassTypeCG classType = (AClassTypeCG) type;
				classType.setName(pack + "." + classType.getName());
			} else
			{
				Logger.getLog().printErrorln("Expected type of explicit variable to be a class type at this point in '"
						+ this.getClass().getSimpleName() + "'. Got: " + type);
			}
		}
	}

	@Override
	public void caseAIdentifierVarExpCG(AIdentifierVarExpCG node)
			throws AnalysisException
	{
		if (node.parent() instanceof AFieldExpCG
				&& node.getName().equals(paramName))
		{
			AFieldExpCG field = (AFieldExpCG) node.parent();

			TransAssistantCG assistant = javaGen.getTransAssistant();
			AIdentifierVarExpCG replField = javaGen.getInfo().getExpAssistant().consIdVar(consUniqueName(field.getMemberName()), field.getType().clone());
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
						ADefaultClassDeclCG encClass = rec.getAncestor(ADefaultClassDeclCG.class);

						if (encClass != null)
						{
							String defClass = "";

							if (JavaCodeGenUtil.isValidJavaPackage(encClass.getPackage()))
							{
								defClass += encClass.getPackage() + ".";
							}

							defClass += encClass.getName();

							AExplicitVarExpCG func = new AExplicitVarExpCG();

							AClassTypeCG classType = new AClassTypeCG();
							classType.setName(defClass);

							func.setClassType(classType);
							func.setIsLambda(false);
							func.setIsLocal(false);
							func.setSourceNode(sourceNode);
							func.setName(node.getName());

							javaGen.getTransAssistant().replaceNodeWith(node, func);
						}
						else
						{
							Logger.getLog().printErrorln("Could not find enclosing class of record "
									+ rec.getName() + " in '"
									+ this.getClass().getSimpleName() + "'");
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
