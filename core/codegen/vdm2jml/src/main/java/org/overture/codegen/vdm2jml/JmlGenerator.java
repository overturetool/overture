package org.overture.codegen.vdm2jml;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SFunctionDefinition;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.util.ClonableString;
import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.SDeclCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.ADefaultClassDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AFormalParamLocalParamCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.AModuleDeclCG;
import org.overture.codegen.cgast.declarations.ANamedTypeDeclCG;
import org.overture.codegen.cgast.declarations.ARecordDeclCG;
import org.overture.codegen.cgast.declarations.ATypeDeclCG;
import org.overture.codegen.cgast.declarations.AVarDeclCG;
import org.overture.codegen.cgast.expressions.ACastUnaryExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.types.AUnknownTypeCG;
import org.overture.codegen.ir.IRConstants;
import org.overture.codegen.ir.IREventObserver;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.IRSettings;
import org.overture.codegen.ir.IRStatus;
import org.overture.codegen.ir.IrNodeInfo;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.trans.AssignStmTrans;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.vdm2java.IJavaQuoteEventObserver;
import org.overture.codegen.vdm2java.JavaCodeGen;
import org.overture.codegen.vdm2java.JavaCodeGenUtil;
import org.overture.codegen.vdm2java.JavaSettings;
import org.overture.codegen.vdm2jml.data.RecClassInfo;
import org.overture.codegen.vdm2jml.data.StateDesInfo;
import org.overture.codegen.vdm2jml.predgen.TypePredDecorator;
import org.overture.codegen.vdm2jml.predgen.info.NamedTypeInfo;
import org.overture.codegen.vdm2jml.predgen.info.NamedTypeInvDepCalculator;
import org.overture.codegen.vdm2jml.trans.RecAccessorTrans;
import org.overture.codegen.vdm2jml.trans.RecInvTransformation;
import org.overture.codegen.vdm2jml.trans.TargetNormaliserTrans;
import org.overture.codegen.vdm2jml.util.AnnotationSorter;
import org.overture.codegen.vdm2jml.util.IsValChecker;

import de.hunsicker.jalopy.storage.Convention;
import de.hunsicker.jalopy.storage.ConventionKeys;

public class JmlGenerator implements IREventObserver, IJavaQuoteEventObserver
{
	public static final String DEFAULT_JAVA_ROOT_PACKAGE = "project";
	public static final String GEN_INV_METHOD_PARAM_NAME = "elem";
	public static final String INV_PREFIX = "inv_";
	public static final String INV_METHOD_REPLACEMENT_NAME_PREFIX = "check_"; 
	
	public static final String JML_PUBLIC = "public";
	public static final String JML_STATIC_INV_ANNOTATION = "static invariant";
	public static final String JML_OR = " || ";
	public static final String JML_AND = " && ";
	public static final String JML_IMPLIES = " ==> ";
	public static final String JML_INSTANCE_INV_ANNOTATION = "instance invariant";
	public static final String JML_REQ_ANNOTATION = "requires";
	public static final String JML_ENS_ANNOTATION = "ensures";
	public static final String JML_ASSERT_ANNOTATION = "assert";
	public static final String JML_OLD_PREFIX = "\\old";
	public static final String JML_SPEC_PUBLIC = "/*@ spec_public @*/";
	public static final String JML_PURE = "/*@ pure @*/";
	public static final String JML_HELPER = "/*@ helper @*/";
	public static final String JML_RESULT = "\\result";
	public static final String JML_NULLABLE_BY_DEFAULT = "//@ nullable_by_default";
	
	public static final String INV_CHECKS_ON_GHOST_VAR_NAME = "invChecksOn";
	public static final String JML_INV_CHECKS_ON_DECL = "/*@ public ghost static boolean %s = true; @*/";
	public static final String JML_ENABLE_INV_CHECKS = "//@ set " + INV_CHECKS_ON_GHOST_VAR_NAME + " = true;";
	public static final String JML_DISABLE_INV_CHECKS = "//@ set " + INV_CHECKS_ON_GHOST_VAR_NAME + " = false;";
	
	public static final String REC_VALID_METHOD_NAMEVALID = "valid";
	public static final String REC_VALID_METHOD_CALL = REC_VALID_METHOD_NAMEVALID + "()"; 
	
	private JavaCodeGen javaGen;
	private List<NamedTypeInfo> typeInfoList;
	private JmlGenUtil util;
	private JmlAnnotationHelper annotator;
	
	private StateDesInfo stateDesInfo;
	
	// The class owning the invChecksOn flag
	private ADefaultClassDeclCG invChecksFlagOwner = null;
	
	public JmlGenerator()
	{
		this(new JavaCodeGen());
	}
	
	public JmlGenerator(JavaCodeGen javaGen)
	{
		this.javaGen = javaGen;
		
		// Named invariant type info will be derived (later) from the VDM-SL AST
		this.typeInfoList = null;
		this.util = new JmlGenUtil(this);
		this.annotator = new JmlAnnotationHelper(this);
		
		initSettings();

		addJmlTransformations();
	}

	private void addJmlTransformations()
	{
		TargetNormaliserTrans targetNormaliserTrans = new TargetNormaliserTrans(this);

		// Info structures are populated with data when the transformations are applied
		this.stateDesInfo = targetNormaliserTrans.getStateDesInfo();

		List<DepthFirstAnalysisAdaptor> series = this.javaGen.getTransSeries().getSeries();
		
		for (int i = 0; i < series.size(); i++)
		{
			// We'll add the transformations after the assignment transformation
			if (series.get(i).getClass().equals(AssignStmTrans.class))
			{
				int targetTransIdx = i + 1;

				if (targetTransIdx <= series.size())
				{
					series.add(targetTransIdx, targetNormaliserTrans);
				} else
				{
					Logger.getLog().printErrorln("Could not add transformations "
							+ " to Java transformation series in '"
							+ this.getClass().getSimpleName());
				}
				
				break;
			}
		}
	}
	
	private void initSettings()
	{
		IRSettings irSettings = getIrSettings();
		irSettings.setGeneratePreConds(true);
		irSettings.setGeneratePreCondChecks(false);
		irSettings.setGeneratePostConds(true);
		irSettings.setGeneratePostCondChecks(false);
		irSettings.setGenerateInvariants(true);
		
		JavaSettings javaSettings = getJavaSettings();
		javaSettings.setGenRecsAsInnerClasses(false);
		
		// Bugs in Jalopy requires a small tweak to the code formatting conventions.
		// Force Jalopy to not remove 'scope' braces
		Convention.getInstance().putBoolean(ConventionKeys.BRACE_REMOVE_BLOCK, false);
	}

	public GeneratedData generateJml(List<AModuleModules> ast)
			throws AnalysisException
	{
		if(!JavaCodeGenUtil.isValidJavaPackage(getJavaSettings().getJavaRootPackage()))
		{
			getJavaSettings().setJavaRootPackage(DEFAULT_JAVA_ROOT_PACKAGE);
		}
		
		computeNamedTypeInvInfo(ast);
		
		javaGen.registerIrObs(this);
		javaGen.registerJavaQuoteObs(this);

		return javaGen.generateJavaFromVdmModules(ast);
	}

	@Override
	public List<IRStatus<INode>> initialIRConstructed(
			List<IRStatus<INode>> ast, IRInfo info)
	{
		List<IRStatus<AModuleDeclCG>> modules = IRStatus.extract(ast, AModuleDeclCG.class);
		
		for(IRStatus<AModuleDeclCG> m : modules)
		{
			for(SDeclCG d : m.getIrNode().getDecls())
			{
				if(d instanceof AFieldDeclCG)
				{
					AFieldDeclCG f = (AFieldDeclCG) d;
					
					if(f.getInitial() != null && f.getFinal())
					{
						IsValChecker isVal = new IsValChecker();
						try
						{
							if(!f.getInitial().apply(isVal))
							{
								Set<IrNodeInfo> wrap = new HashSet<>();
								IrNodeInfo warning = new IrNodeInfo(f, "The JML generator only allows literal-based expressions "
										+ "to be used to initialise value definitions");
								// By requiring that there is no need to assert that it is not null
								wrap.add(warning);
								m.addTransformationWarnings(wrap);
							}
							
						} catch (org.overture.codegen.cgast.analysis.AnalysisException e)
						{
							e.printStackTrace();
						}
						
					}
				}
			}
		}
		
		return ast;
	}

	@Override
	public List<IRStatus<INode>> finalIRConstructed(List<IRStatus<INode>> ast,
			IRInfo info)
	{
		// In the final version of the IR, received by the Java code generator, all
		// top level containers are classes
		setInvChecksOnOwner(ast);
		annotateRecsWithInvs(ast);

		// All the record methods are JML pure
		annotator.makeRecMethodsPure(ast);
		
		/**
		 * Make records accessor-based (i.e. using setters and getters), which will force the record into a visible
		 * state when modified/read.
		 */
		RecClassInfo recInfo = makeRecStateAccessorBased(ast);
		
		List<IRStatus<INode>> newAst = new LinkedList<IRStatus<INode>>(ast);

		// To circumvent a problem with OpenJML. See documentation of makeRecsOuterClasses
		newAst.addAll(util.makeRecsOuterClasses(ast, recInfo));
		
		// Also extract classes that are records
		for(IRStatus<ADefaultClassDeclCG> status : IRStatus.extract(newAst, ADefaultClassDeclCG.class))
		{
			// VDM uses the type system to control whether 'nil' is allowed as a value so we'll
			// just annotate all classes as @nullable_by_default
			annotator.makeNullableByDefault(status.getIrNode());
		}
		
		// Only extract from 'ast' to not get the record classes
		for (IRStatus<ADefaultClassDeclCG> status : IRStatus.extract(ast, ADefaultClassDeclCG.class))
		{
			ADefaultClassDeclCG clazz = status.getIrNode();

			if(info.getDeclAssistant().isLibraryName(clazz.getName()))
			{
				continue;
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

			// Named type invariant functions will potentially also have to be
			// accessed by other modules
			annotator.makeNamedTypeInvFuncsPublic(clazz);
			
			// In order for a value to be compatible with a named invariant type
			// two conditions must be met:
			//
			// 1) The type of the value must match the domain type of the named
			// type invariant. T = <domainType>
			// 
			// 2) the value must meet the invariant predicate 
			adjustNamedTypeInvFuncs(status);

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
				}

				if (m.getPostCond() != null)
				{
					annotator.makeCondPublic(m.getPostCond());
					annotator.appendMetaData(m, consMethodCond(m.getPostCond(), m.getFormalParams(), JML_ENS_ANNOTATION));
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
		
		// Add assertions to check for violation of record and named type invariants
		addAssertions(newAst, stateDesInfo, recInfo);

		// Make sure that the JML annotations are ordered correctly
		sortAnnotations(newAst);

		// Make all nodes have a copy of each named type invariant method
		util.distributeNamedTypeInvs(newAst);
		
		// Return back the modified AST to the Java code generator
		return newAst;
	}

	private RecClassInfo makeRecStateAccessorBased(List<IRStatus<INode>> ast) {

		RecAccessorTrans recAccTr = new RecAccessorTrans(this);

		for (IRStatus<INode> status : ast) {
			try {
				javaGen.getIRGenerator().applyPartialTransformation(status, recAccTr);
			} catch (org.overture.codegen.cgast.analysis.AnalysisException e) {

				Logger.getLog().printErrorln(
						"Problems applying '" + RecAccessorTrans.class + "' to status " + status.getIrNodeName());
				e.printStackTrace();
			}
		}
		
		return recAccTr.getRecInfo();
	}

	private void sortAnnotations(List<IRStatus<INode>> newAst)
	{
		AnnotationSorter sorter = new AnnotationSorter();

		for (IRStatus<ADefaultClassDeclCG> status : IRStatus.extract(newAst, ADefaultClassDeclCG.class))
		{
			if (!javaGen.getInfo().getDeclAssistant().isLibraryName(status.getIrNode().getName()))
			{
				try
				{
					status.getIrNode().apply(sorter);
				} catch (org.overture.codegen.cgast.analysis.AnalysisException e)
				{
					Logger.getLog().printErrorln("Problems sorting JML annotations for node "
							+ status.getIrNode()
							+ " in '"
							+ this.getClass().getSimpleName() + "'");
					e.printStackTrace();
				}
			}
		}
	}

	private void computeNamedTypeInvInfo(List<AModuleModules> ast) throws AnalysisException
	{
		NamedTypeInvDepCalculator depCalc = new NamedTypeInvDepCalculator(this.getJavaGen().getInfo());
		
		for (AModuleModules m : ast)
		{
			if (!javaGen.getInfo().getDeclAssistant().isLibrary(m))
			{
				m.apply(depCalc);
			}
		}
		
		this.typeInfoList = depCalc.getTypeDataList();
	}

	private void addAssertions(List<IRStatus<INode>> newAst, StateDesInfo stateDesInfo, RecClassInfo recInfo)
	{
		TypePredDecorator assertTr = new TypePredDecorator(this, stateDesInfo, recInfo);
		
		for (IRStatus<ADefaultClassDeclCG> status : IRStatus.extract(newAst, ADefaultClassDeclCG.class))
		{
			ADefaultClassDeclCG clazz = status.getIrNode();

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
				annotator.addRecInv(r);
			}
		}
	}

	private void setInvChecksOnOwner(List<IRStatus<INode>> ast) {
		
		for (IRStatus<ADefaultClassDeclCG> status : IRStatus.extract(ast, ADefaultClassDeclCG.class))
		{
			if(invChecksFlagOwner == null)
			{
				invChecksFlagOwner = status.getIrNode();
				annotator.addInvCheckGhostVarDecl(invChecksFlagOwner);
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
	
	public void adjustNamedTypeInvFuncs(IRStatus<ADefaultClassDeclCG> status)
	{
		ADefaultClassDeclCG clazz = status.getIrNode();
		
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
				
				String defModule = namedTypeDecl.getName().getDefiningClass();
				String name = namedTypeDecl.getName().getName();
				method.setName(JmlGenerator.INV_PREFIX + defModule + "_" + name);
				
				AFormalParamLocalParamCG invParam = util.getInvFormalParam(method);
				AFormalParamLocalParamCG invParamCopy = invParam.clone();
				
				invParam.setType(new AUnknownTypeCG());
				
				String paramName = util.getName(invParam.getPattern());
				
				if(paramName == null)
				{
					continue;
				}
				
				invParam.setPattern(util.consInvParamReplacementId(clazz, paramName));
				
				// Invariant methods are really functions so we'll annotate them as pure
				annotator.makePure(method);
				
				// We'll also make it a helper so it can be called from invariants, which is needed for fields
				// Otherwise we would get errors on the form:
				// "Calling a pure non-helper method in an invariant may lead to unbounded recursive
				// invariant checks and stack-overflow: inv_C(java.lang.Object)"
				annotator.makeHelper(method);
				
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
				
				SStmCG body = method.getBody();

				ABlockStmCG repBlock = new ABlockStmCG();
				javaGen.getTransAssistant().replaceNodeWith(body, repBlock);

				repBlock.getStatements().add(declStmBlock);
				repBlock.getStatements().add(body);
			}
		}
	}
	
	public StateDesInfo normaliseTargets(List<IRStatus<INode>> newAst)
	{
		TargetNormaliserTrans normaliser = new TargetNormaliserTrans(this);
		for (IRStatus<INode> n : newAst)
		{
			try
			{
				javaGen.getIRGenerator().applyPartialTransformation(n, normaliser);
			} catch (org.overture.codegen.cgast.analysis.AnalysisException e)
			{
				Logger.getLog().printErrorln("Problem normalising state designators in '"
						+ this.getClass().getSimpleName() + "': "
						+ e.getMessage());
				e.printStackTrace();
			}
		}

		return normaliser.getStateDesInfo();
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

	public ADefaultClassDeclCG getInvChecksFlagOwner()
	{
		return invChecksFlagOwner;
	}
	
	public StateDesInfo getStateDesInfo()
	{
		return stateDesInfo;
	}

	@Override
	public void quoteClassesProduced(List<ADefaultClassDeclCG> quoteClasses)
	{
		for(ADefaultClassDeclCG qc : quoteClasses)
		{
			// Code generated quotes are represented as singletons and by default the instance
			// field is null. So we'll mark quote classes as nullable_by_default.
			//Example from class represented <A>: private static AQuote instance = null;
			annotator.makeNullableByDefault(qc);
		}
	}
}
