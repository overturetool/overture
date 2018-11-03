package org.overture.codegen.trans;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.overture.codegen.ir.IRConstants;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.PIR;
import org.overture.codegen.ir.SDeclIR;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SImportIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.AAllImportIR;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.declarations.AFromModuleImportsIR;
import org.overture.codegen.ir.declarations.AFuncDeclIR;
import org.overture.codegen.ir.declarations.AFunctionValueImportIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.AModuleDeclIR;
import org.overture.codegen.ir.declarations.AModuleImportsIR;
import org.overture.codegen.ir.declarations.ANamedTraceDeclIR;
import org.overture.codegen.ir.declarations.AOperationValueImportIR;
import org.overture.codegen.ir.declarations.ARecordDeclIR;
import org.overture.codegen.ir.declarations.AStateDeclIR;
import org.overture.codegen.ir.declarations.ATypeDeclIR;
import org.overture.codegen.ir.declarations.ATypeImportIR;
import org.overture.codegen.ir.declarations.AValueValueImportIR;
import org.overture.codegen.ir.expressions.AEqualsBinaryExpIR;
import org.overture.codegen.ir.expressions.ANewExpIR;
import org.overture.codegen.ir.expressions.AUndefinedExpIR;
import org.overture.codegen.ir.name.ATypeNameIR;
import org.overture.codegen.ir.types.ARecordTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;

public class ModuleToClassTransformation extends DepthFirstAnalysisAdaptor
		implements ITotalTransformation
{
	private ADefaultClassDeclIR clazz = null;

	private IRInfo info;
	private TransAssistantIR transAssistant;
	private List<AModuleDeclIR> allModules;

	private Logger log = Logger.getLogger(this.getClass().getName());

	public ModuleToClassTransformation(IRInfo info,
			TransAssistantIR transAssistant, List<AModuleDeclIR> allModules)
	{
		this.info = info;
		this.transAssistant = transAssistant;
		this.allModules = allModules;
	}

	@Override
	public void caseAModuleDeclIR(AModuleDeclIR node) throws AnalysisException
	{
		clazz = new ADefaultClassDeclIR();
		clazz.setSourceNode(node.getSourceNode());
		clazz.setAccess(IRConstants.PUBLIC);
		clazz.setName(node.getName());

		// Prevent instantiation of the class
		AMethodDeclIR privConstructor = info.getDeclAssistant().consDefaultContructor(node.getName());
		privConstructor.setAccess(IRConstants.PRIVATE);
		clazz.getMethods().add(privConstructor);

		makeStateAccessExplicit(node);
		handleImports(node.getImport(), clazz);

		// Wrap declarations in a new list to avoid a concurrent modifications
		// exception when moving the module declarations to the class
		for (SDeclIR decl : new LinkedList<>(node.getDecls()))
		{
			if (decl instanceof AMethodDeclIR)
			{
				AMethodDeclIR method = (AMethodDeclIR) decl;
				method.setAccess(IRConstants.PUBLIC);
				method.setStatic(true);

				clazz.getMethods().add(method);

			} else if (decl instanceof AFuncDeclIR)
			{
				// Functions are static by definition
				AFuncDeclIR func = (AFuncDeclIR) decl;
				func.setAccess(IRConstants.PUBLIC);

				clazz.getFunctions().add(func);

			} else if (decl instanceof ATypeDeclIR)
			{
				ATypeDeclIR typeDecl = (ATypeDeclIR) decl;
				typeDecl.setAccess(IRConstants.PUBLIC);

				clazz.getTypeDecls().add(typeDecl);

			} else if (decl instanceof AStateDeclIR)
			{
				// Handle this as the last thing since it may depend on value definitions
				continue;
			} else if (decl instanceof ANamedTraceDeclIR)
			{
				clazz.getTraces().add((ANamedTraceDeclIR) decl);

			} else if (decl instanceof AFieldDeclIR)
			{
				AFieldDeclIR field = (AFieldDeclIR) decl;
				field.setAccess(IRConstants.PUBLIC);
				field.setStatic(true);

				clazz.getFields().add(field);
			} else
			{
				log.error("Got unexpected declaration: " + decl);
			}
		}

		AStateDeclIR stateDecl = getStateDecl(node);

		if (stateDecl != null)
		{
			ARecordDeclIR record = new ARecordDeclIR();
			record.setSourceNode(stateDecl.getSourceNode());
			record.setName(stateDecl.getName());

			if (stateDecl.getInvDecl() != null)
			{
				// The state invariant constrains the type of the state
				// see https://github.com/overturetool/overture/issues/459
				record.setInvariant(stateDecl.getInvDecl().clone());
				if(info.getSettings().addStateInvToModule()) {
					clazz.getFunctions().add(stateDecl.getInvDecl().clone());
				}
			}

			for (AFieldDeclIR field : stateDecl.getFields())
			{
				record.getFields().add(field.clone());
			}

			ATypeDeclIR typeDecl = new ATypeDeclIR();
			typeDecl.setAccess(IRConstants.PUBLIC);
			typeDecl.setDecl(record);

			clazz.getTypeDecls().add(typeDecl);

			ATypeNameIR typeName = new ATypeNameIR();
			typeName.setName(stateDecl.getName());
			typeName.setDefiningClass(clazz.getName());

			ARecordTypeIR stateType = new ARecordTypeIR();
			stateType.setName(typeName);

			// The state field can't be final since you are allow to assign to it in
			// VDM-SL, e.g. St := mk_St(...)
			clazz.getFields().add(transAssistant.consField(IRConstants.PRIVATE, stateType, stateDecl.getName(), getInitExp(stateDecl)));
		}

		info.removeModule(node.getName());
		info.addClass(clazz);
	}

	private void handleImports(final AModuleImportsIR moduleImports,
			final ADefaultClassDeclIR clazz) throws AnalysisException
	{
		// name = moduleImports.getName();

		if (moduleImports == null)
		{
			return;
		}

		for (AFromModuleImportsIR fromImports : moduleImports.getImports())
		{
			// String fromName = fromImports.getName();

			for (List<SImportIR> sig : fromImports.getSignatures())
			{
				for (SImportIR imp : sig)
				{
					// TODO Implement the import analysis cases
					imp.apply(new DepthFirstAnalysisAdaptor()
					{
						@Override
						public void caseAAllImportIR(AAllImportIR node)
								throws AnalysisException
						{
						}

						@Override
						public void caseATypeImportIR(ATypeImportIR node)
								throws AnalysisException
						{
						}

						@Override
						public void caseAFunctionValueImportIR(
								AFunctionValueImportIR node)
								throws AnalysisException
						{
						}

						@Override
						public void caseAOperationValueImportIR(
								AOperationValueImportIR node)
								throws AnalysisException
						{
						}

						@Override
						public void caseAValueValueImportIR(
								AValueValueImportIR node)
								throws AnalysisException
						{
							/*
							 * String renamed = node.getRenamed(); if (renamed != null) { //STypeIR impType =
							 * node.getImportType(); String from = node.getFromModuleName(); String name =
							 * node.getName(); AFieldDeclIR impFieldCopy = getValue(name, from).clone();
							 * impFieldCopy.setAccess(IRConstants.PUBLIC); impFieldCopy.setName(renamed);
							 * clazz.getFields().add(impFieldCopy);
							 * //clazz.getFields().add(transAssistant.consConstField(access, type, fromName, initExp));
							 * }
							 */
						}
					});
				}
			}
		}
	}

	private void makeStateAccessExplicit(final AModuleDeclIR module)
			throws AnalysisException
	{
		final AStateDeclIR stateDecl = getStateDecl(module);

		if (stateDecl == null)
		{
			// Nothing to do
			return;
		}

		module.apply(new SlStateAccessTrans(stateDecl, info, transAssistant));
	}

	public AStateDeclIR getStateDecl(AModuleDeclIR module)
	{
		for (SDeclIR decl : module.getDecls())
		{
			if (decl instanceof AStateDeclIR)
			{
				return (AStateDeclIR) decl;
			}
		}

		return null;
	}

	private SExpIR getInitExp(AStateDeclIR stateDecl)
	{
		if (stateDecl.getInitExp() instanceof AEqualsBinaryExpIR)
		{
			AEqualsBinaryExpIR eqExp = (AEqualsBinaryExpIR) stateDecl.getInitExp();

			return eqExp.getRight().clone();
		} else
		{
			ANewExpIR defaultRecInit = new ANewExpIR();
			defaultRecInit.setName(transAssistant.getTypeName(stateDecl));
			defaultRecInit.setType(transAssistant.getRecType(stateDecl));

			for (int i = 0; i < stateDecl.getFields().size(); i++)
			{
				defaultRecInit.getArgs().add(new AUndefinedExpIR());
			}

			return defaultRecInit;
		}
	}

	@SuppressWarnings("unused")
	private AFieldDeclIR getValue(String fieldName, String moduleName)
	{
		for (AModuleDeclIR module : allModules)
		{
			if (module.getName().equals(moduleName))
			{
				for (SDeclIR decl : module.getDecls())
				{
					if (decl instanceof AFieldDeclIR)
					{
						AFieldDeclIR fieldDecl = (AFieldDeclIR) decl;
						if (fieldDecl.getName().equals(fieldName))
						{
							return fieldDecl;
						}
					}
				}
			}
		}

		log.error("Could not find field " + fieldName + " in module "
				+ moduleName);

		return null;
	}

	@Override
	public PIR getResult()
	{
		return clazz;
	}
}
