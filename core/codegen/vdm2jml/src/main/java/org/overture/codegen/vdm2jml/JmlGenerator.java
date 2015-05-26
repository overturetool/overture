package org.overture.codegen.vdm2jml;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SFunctionDefinition;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.util.ClonableString;
import org.overture.codegen.analysis.violations.UnsupportedModelingException;
import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.SDeclCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AFormalParamLocalParamCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.AModuleDeclCG;
import org.overture.codegen.cgast.declarations.ANamedTypeDeclCG;
import org.overture.codegen.cgast.declarations.ARecordDeclCG;
import org.overture.codegen.cgast.declarations.AStateDeclCG;
import org.overture.codegen.cgast.declarations.ATypeDeclCG;
import org.overture.codegen.cgast.declarations.AVarDeclCG;
import org.overture.codegen.cgast.expressions.ACastUnaryExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.AIfStmCG;
import org.overture.codegen.cgast.types.AUnknownTypeCG;
import org.overture.codegen.ir.IRConstants;
import org.overture.codegen.ir.IREventObserver;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.IRSettings;
import org.overture.codegen.ir.IRStatus;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.vdm2java.JavaCodeGen;
import org.overture.codegen.vdm2java.JavaSettings;

public class JmlGenerator implements IREventObserver
{
	public static final String GEN_INV_METHOD_PARAM_NAME = "elem";
	public static final String INV_METHOD_REPLACEMENT_NAME_PREFIX = "check_"; 
	public static final String JML_OR = " || ";
	public static final String JML_AND = " && ";
	public static final String JML_PUBLIC = "public";
	public static final String JML_INSTANCE_INV_ANNOTATION = "instance invariant";
	public static final String JML_STATIC_INV_ANNOTATION = "static invariant";
	public static final String JML_REQ_ANNOTATION = "requires";
	public static final String JML_ENS_ANNOTATION = "ensures";
	public static final String JML_ASSERT_ANNOTATION = "assert";
	public static final String JML_INV_PREFIX = "inv_";
	public static final String JML_OLD_PREFIX = "\\old";
	public static final String JML_SPEC_PUBLIC = "/*@ spec_public @*/";
	public static final String JML_PURE = "/*@ pure @*/";
	public static final String JML_HELPER = "/*@ helper @*/";
	public static final String JML_RESULT = "\\result";
	public static final String REPORT_CALL = "report";
	public static final String JML_NULLABLE = "//@ nullable;";
	
	private JavaCodeGen javaGen;
	private JmlSettings jmlSettings;
	private Map<String, List<ClonableString>> classInvInfo;
	private List<NamedTypeInfo> typeInfoList;
	private JmlGenUtil util;
	private JmlAnnotationHelper annotator;
	
	public JmlGenerator()
	{
		this.javaGen = new JavaCodeGen();
		this.jmlSettings = new JmlSettings();
		this.classInvInfo = new HashedMap<String, List<ClonableString>>();
		
		// Named invariant type info will be derived (later) from the VDM-SL AST
		this.typeInfoList = null;
		this.util = new JmlGenUtil(this);
		this.annotator = new JmlAnnotationHelper(this);
	}
	
	public GeneratedData generateJml(List<AModuleModules> ast)
			throws AnalysisException, UnsupportedModelingException
	{
		computeNamedTypeInvInfo(ast);
		
		javaGen.register(this);

		return javaGen.generateJavaFromVdmModules(ast);
	}

	@Override
	public List<IRStatus<INode>> initialIRConstructed(
			List<IRStatus<INode>> ast, IRInfo info)
	{
		// In the initial version of the IR the top level containers are both modules and classes
		for (IRStatus<AClassDeclCG> status : IRStatus.extract(ast, AClassDeclCG.class))
		{
			computeClassInvInfo(status.getIrNode());
		}

		for (IRStatus<AModuleDeclCG> status : IRStatus.extract(ast, AModuleDeclCG.class))
		{
			computeModuleInvInfo(status.getIrNode());
		}

		return ast;
	}

	@Override
	public List<IRStatus<INode>> finalIRConstructed(List<IRStatus<INode>> ast,
			IRInfo info)
	{
		// In the final version of the IR, received by the Java code generator, all
		// top level containers are classes

		annotateRecsWithInvs(ast);
		
		// Functions are JML pure so we will annotate them as so.
		// Note that @pure is a JML modifier so this annotation should go last
		// to prevent the error: "no modifiers are allowed prior to a lightweight
		// specification case"
		
		// All the record methods are JML pure
		annotator.makeRecMethodsPure(ast);
		
		List<IRStatus<INode>> newAst = new LinkedList<IRStatus<INode>>(ast);

		// To circumvent a problem with OpenJML. See documentation of makeRecsOuterClasses
		newAst.addAll(util.makeRecsOuterClasses(ast));
		
		// Also extract classes that are records
		for(IRStatus<AClassDeclCG> status : IRStatus.extract(newAst, AClassDeclCG.class))
		{
			// Make declarations nullable
			annotator.makeNullable(status.getIrNode());
		}
		
		// Only extract from 'ast' to not get the record classes
		for (IRStatus<AClassDeclCG> status : IRStatus.extract(ast, AClassDeclCG.class))
		{
			AClassDeclCG clazz = status.getIrNode();

			if(info.getDeclAssistant().isLibraryName(clazz.getName()))
			{
				continue;
			}
			
			if(clazz.getInvariant() != null)
			{
				// Now that the static invariant is public the invariant function
				// must also be made public
				annotator.makeCondPublic(clazz.getInvariant());

				// Now make the invariant function a pure helper so we can invoke it from
				// the invariant annotation and avoid runtime errors and JML warnings
				annotator.makeHelper(clazz.getInvariant());
				annotator.makePure(clazz.getInvariant());
				injectReportCalls(clazz.getInvariant());
			}

			for (AFieldDeclCG f : clazz.getFields())
			{
				// Make fields JML @spec_public so they can be passed to post conditions.
				// However, we'll avoid doing it for public fields as this does not make sense
				if(!f.getAccess().equals(IRConstants.PUBLIC))
				{
					annotator.makeSpecPublic(f);
				}
			}

			List<ClonableString> inv = classInvInfo.get(status.getIrNodeName());

			if (inv != null)
			{
				clazz.setMetaData(inv);
			}

			// Named type invariant functions will potentially also have to be
			// accessed by other modules
			annotator.makeNamedTypeInvFuncsPublic(clazz);
			
			// In order for a value to be compatible with a named invariant type
			// two conditions must be met:
			//
			// 1) The type of the value must match one of the leaf types of the
			//    named invariant type, and secondly
			// 
			// 2) the value must meet the invariant predicate 
			//
			// Wrt. 1) a dynamic type check has to be added to the invariant method
			adjustNamedTypeInvFuncs(clazz);

			// Note that the methods contained in clazz.getMethod() include
			// pre post and invariant methods
			for (AMethodDeclCG m : clazz.getMethods())
			{
				if (m.getPreCond() != null)
				{
					// We need to make pre and post conditions functions public in order to
					// be able to call them in the @requires and @ensures clauses, respectively.
					annotator.makeCondPublic(m.getPreCond());
					annotator.appendMetaData(m, consMethodCond(m.getPreCond(), m.getFormalParams(), JML_REQ_ANNOTATION));
					injectReportCalls(m.getPreCond());
				}

				if (m.getPostCond() != null)
				{
					annotator.makeCondPublic(m.getPostCond());
					annotator.appendMetaData(m, consMethodCond(m.getPostCond(), m.getFormalParams(), JML_ENS_ANNOTATION));
					injectReportCalls(m.getPostCond());
				}

				// Some methods such as those in record classes
				// will not have a source node. Thus this check.
				if (m.getSourceNode() != null)
				{
					if (m.getSourceNode().getVdmNode() instanceof SFunctionDefinition)
					{
						annotator.makePure(m);
					}
				}
			}
		}
		
		addModuleStateInvAssertions(newAst);
		addNamedTypeInvariantAssertions(newAst);

		// Return back the modified AST to the Java code generator
		return newAst;
	}
	
	private void computeNamedTypeInvInfo(List<AModuleModules> ast) throws AnalysisException
	{
		NamedTypeInvDepCalculator depCalc = new NamedTypeInvDepCalculator();
		
		for (AModuleModules m : ast)
		{
			if (!javaGen.getInfo().getDeclAssistant().isLibrary(m))
			{
				m.apply(depCalc);
			}
		}
		
		this.typeInfoList = depCalc.getTypeDataList();
	}

	private void addNamedTypeInvariantAssertions(List<IRStatus<INode>> newAst)
	{
		NamedTypeInvariantTransformation assertTr = new NamedTypeInvariantTransformation(this);
		
		for (IRStatus<AClassDeclCG> status : IRStatus.extract(newAst, AClassDeclCG.class))
		{
			AClassDeclCG clazz = status.getIrNode();

			if (!this.javaGen.getInfo().getDeclAssistant().isLibraryName(clazz.getName()))
			{
				try
				{
					this.javaGen.getIRGenerator().applyPartialTransformation(status, assertTr);
				} catch (org.overture.codegen.cgast.analysis.AnalysisException e)
				{
					Logger.getLog().printErrorln("Unexpected problem occured when applying transformation in '"
							+ this.getClass().getSimpleName() + "'");
					e.printStackTrace();
				}
			}
		}
	}

	private void annotateRecsWithInvs(List<IRStatus<INode>> ast)
	{
		List<ARecordDeclCG> recs = util.getRecords(ast);
		
		for(ARecordDeclCG r : recs)
		{
			List<String> args = util.getRecFieldNames(r);
			
			if(r.getInvariant() != null)
			{
				// The record invariant is an instance invariant and the invariant method
				// is updated such that it passes the record fields (rather than the record instance)
				// to the invariant method. See the changeRecInvMethod for more information about
				// this. Also note that there is no need for the record invariant method to guard against
				// the record being null (because the invariant is specified as an instance invariant).
				changeRecInvMethod(r);
				
				// Must be public otherwise we can't access it from the invariant
				annotator.makeCondPublic(r.getInvariant());
				// Must be a helper since we use this function from the invariant
				annotator.makeHelper(r.getInvariant());
				
				annotator.makePure(r.getInvariant());
				
				// Add the instance invariant to the record
				// Make it public so we can access the record fields from the invariant clause
				annotator.appendMetaData(r, annotator.consAnno("public " + JML_INSTANCE_INV_ANNOTATION, JML_INV_PREFIX
						+ r.getName(), args));
				
				injectReportCalls(r.getInvariant());
			}
		}
	}

	/**
	 * The record invariant method is generated such that it takes the record instance as an argument, e.g.
	 * inv_Rec(recToCheck). When I try to invoke it from the instance invariant clause as //@ invariant inv_Rec(this);
	 * it crashes on a stackoverflow where it keeps calling the invariant check. Adjusting the invariant method such
	 * that it instead takes the fields as arguments does, however, work. This is exactly what this method does. After
	 * calling this method a call to the invariant method from the invariant will take the following form:
	 * //@ public instance invariant inv_Rec(field1,field2);
	 * 
	 * @param rec The record for which we will change the invariant method
	 */
	private void changeRecInvMethod(ARecordDeclCG rec)
	{
		try
		{
			rec.getInvariant().apply(new RecInvTransformation(javaGen, rec));
		} catch (org.overture.codegen.cgast.analysis.AnalysisException e)
		{
			Logger.getLog().printErrorln("Problems transforming the invariant method of a record in '"
					+ this.getClass().getSimpleName() + "'");
			e.printStackTrace();
		}
	}

	private void addModuleStateInvAssertions(List<IRStatus<INode>> ast)
	{
		// In addition to specifying the static invariant we must assert the invariant
		// every time we change the state. Remember that an invariant of a module can not
		// depend on the state of another module since the invariant is a function and
		// functions cannot access state.
		
		ModuleStateInvTransformation assertTr = new ModuleStateInvTransformation(this);
		
		for (IRStatus<AClassDeclCG> status : IRStatus.extract(ast, AClassDeclCG.class))
		{
			AClassDeclCG clazz = status.getIrNode();

			if (!this.javaGen.getInfo().getDeclAssistant().isLibraryName(clazz.getName()))
			{
				try
				{
					this.javaGen.getIRGenerator().applyPartialTransformation(status, assertTr);
				} catch (org.overture.codegen.cgast.analysis.AnalysisException e)
				{
					Logger.getLog().printErrorln("Unexpected problem occured when applying transformation in '"
							+ this.getClass().getSimpleName() + "'");
					e.printStackTrace();
				}
			}
		}
	}

	private List<ClonableString> consMethodCond(SDeclCG decl,
			List<AFormalParamLocalParamCG> parentMethodParams, String jmlAnno)
	{
		if (decl instanceof AMethodDeclCG)
		{
			AMethodDeclCG cond = (AMethodDeclCG) decl;

			List<String> fieldNames = new LinkedList<String>();

			// The arguments of a function or an operation are passed to the pre/post condition
			int i;
			for (i = 0; i < parentMethodParams.size(); i++)
			{
				fieldNames.add(util.getMethodCondArgName(parentMethodParams.get(i).getPattern()));
			}

			// Now compute the remaining argument names which (possibly)
			// include the RESULT and the old state passed
			for (; i < cond.getFormalParams().size(); i++)
			{
				fieldNames.add(util.getMethodCondArgName(cond.getFormalParams().get(i).getPattern()));
			}

			return annotator.consAnno(jmlAnno, cond.getName(), fieldNames);

		} else if (decl != null)
		{
			Logger.getLog().printErrorln("Expected pre/post condition to be a method declaration at this point. Got: "
					+ decl + " in '" + this.getClass().getSimpleName() + "'");
		}

		return null;
	}

	private void computeModuleInvInfo(AModuleDeclCG module)
	{
		for (SDeclCG decl : module.getDecls())
		{
			if (decl instanceof AStateDeclCG)
			{
				AStateDeclCG state = (AStateDeclCG) decl;
				if (state.getInvDecl() != null)
				{
					List<String> fieldNames = new LinkedList<String>();
					fieldNames.add(state.getName());

					// The static invariant requires that either the state of the module is uninitialized
					// or inv_St(St) holds.
					//
					//@ public static invariant St == null || inv_St(St);
					classInvInfo.put(module.getName(), annotator.consAnno("public " + JML_STATIC_INV_ANNOTATION,
							String.format("%s != null", state.getName())  + " ==> " +
							JML_INV_PREFIX + state.getName(), fieldNames));
					//
					// Note that the invariant is public. Otherwise we would get the error
					// 'An identifier with public visibility may not be used in a invariant clause with private '
					// .. because the state field is private.
					//
					// Without the St == null check the initialization of the state field, i.e.
					// St = new my.pack.Mtypes.St(<args>) would try to check that inv_St(St) holds
					// before the state of the module is initialized but that will cause a null
					// pointer exception.
					//
					// If OpenJML did allow the state record to be an inner class (which it does not
					// due to a bug) then the constructor of the state class could have been made private,
					// inv_St(St) would not be checked during initialization of
					// the module. If that was possible, then the null check could have been omitted.
				}
			}
		}
	}

	/**
	 * Computes class invariant information for a class under the assumption that that the dialect is VDMPP
	 * 
	 * @param clazz
	 *            The class to compute class invariant information for
	 */
	private void computeClassInvInfo(AClassDeclCG clazz)
	{
		if (clazz.getInvariant() != null)
		{
			List<String> fieldNames = new LinkedList<String>();
			for (AFieldDeclCG field : clazz.getFields())
			{
				if (!field.getFinal() && !field.getVolatile())
				{
					fieldNames.add(field.getName());
				}
			}

			classInvInfo.put(clazz.getName(), annotator.consAnno(JML_STATIC_INV_ANNOTATION, JML_INV_PREFIX
					+ clazz.getName(), fieldNames));
		}
	}
	
	public void adjustNamedTypeInvFuncs(AClassDeclCG clazz)
	{
		for (ATypeDeclCG typeDecl : clazz.getTypeDecls())
		{
			if (typeDecl.getDecl() instanceof ANamedTypeDeclCG)
			{
				ANamedTypeDeclCG namedTypeDecl = (ANamedTypeDeclCG) typeDecl.getDecl();

				AMethodDeclCG method = util.getInvMethod(typeDecl);

				boolean invMethodIsGen = false;
				
				if (method == null)
				{
					method = util.genInvMethod(clazz, namedTypeDecl);
					typeDecl.setInv(method);
					invMethodIsGen = true;
				}
				
				AFormalParamLocalParamCG invParam = util.getInvFormalParam(method);
				AFormalParamLocalParamCG invParamCopy = invParam.clone();
				
				invParam.setType(new AUnknownTypeCG());
				
				String paramName = util.getName(invParam);
				
				if(paramName == null)
				{
					continue;
				}
				
				invParam.setPattern(util.consInvParamReplacementId(clazz, paramName));
				
				// Invariant methods are really functions so we'll annotate them as pure
				annotator.makePure(method);

				AIfStmCG dynTypeCheck = util.consDynamicTypeCheck(method, namedTypeDecl);
				
				ABlockStmCG declStmBlock = new ABlockStmCG();
				
				if(!invMethodIsGen)
				{
					AIdentifierVarExpCG paramVar = util.getInvParamVar(method);
					
					if(paramVar == null)
					{
						continue;
					}
					
					ACastUnaryExpCG cast = new ACastUnaryExpCG();
					cast.setType(invParamCopy.getType().clone());
					cast.setExp(paramVar.clone());
					
					AVarDeclCG decl = javaGen.getInfo().getDeclAssistant().consLocalVarDecl(invParamCopy.getType(), invParamCopy.getPattern(), cast);
					declStmBlock.setScoped(false);
					declStmBlock.getLocalDefs().add(decl);
				}
				
				if(dynTypeCheck == null)
				{
					continue;
				}
				
				SStmCG body = method.getBody();

				ABlockStmCG repBlock = new ABlockStmCG();
				javaGen.getTransformationAssistant().replaceNodeWith(body, repBlock);

				repBlock.getStatements().add(dynTypeCheck);
				repBlock.getStatements().add(declStmBlock);
				repBlock.getStatements().add(body);
			}
		}
	}

	public void injectReportCalls(SDeclCG cond)
	{
		if(!getJmlSettings().injectReportCalls())
		{
			return;
		}
		
		if (cond instanceof AMethodDeclCG)
		{
			final AMethodDeclCG method = (AMethodDeclCG) cond;

			try
			{
				method.apply(new ReportInjector(this, method));
			} catch (org.overture.codegen.cgast.analysis.AnalysisException e)
			{
				Logger.getLog().printErrorln("Problem encountered when injecting report calls in '"
						+ this.getClass().getSimpleName()
						+ " ' "
						+ e.getMessage());
				e.printStackTrace();
			}
		} else
		{
			Logger.getLog().printErrorln("Expected condition to be a method declaration at this point. Got: "
					+ cond + " in '" + this.getClass().getSimpleName() + "'");
		}
	}

	public IRSettings getIrSettings()
	{
		return javaGen.getSettings();
	}

	public void setIrSettings(IRSettings irSettings)
	{
		javaGen.setSettings(irSettings);
	}

	public JavaSettings getJavaSettings()
	{
		return javaGen.getJavaSettings();
	}

	public void setJavaSettings(JavaSettings javaSettings)
	{
		javaGen.setJavaSettings(javaSettings);
	}
	
	public JmlAnnotationHelper getAnnotator()
	{
		return annotator;
	}
	
	public JavaCodeGen getJavaGen()
	{
		return javaGen;
	}
	
	public JmlGenUtil getUtil()
	{
		return util;
	}
	
	public List<NamedTypeInfo> getTypeInfoList()
	{
		return typeInfoList;
	}

	public JmlSettings getJmlSettings()
	{
		return jmlSettings;
	}
}
