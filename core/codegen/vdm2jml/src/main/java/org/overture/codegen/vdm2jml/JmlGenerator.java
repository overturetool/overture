package org.overture.codegen.vdm2jml;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SFunctionDefinition;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.util.ClonableString;
import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.PIR;
import org.overture.codegen.ir.SDeclIR;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.declarations.AFormalParamLocalParamIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.AModuleDeclIR;
import org.overture.codegen.ir.declarations.ANamedTypeDeclIR;
import org.overture.codegen.ir.declarations.ARecordDeclIR;
import org.overture.codegen.ir.declarations.ATypeDeclIR;
import org.overture.codegen.ir.declarations.AVarDeclIR;
import org.overture.codegen.ir.declarations.SClassDeclIR;
import org.overture.codegen.ir.expressions.ACastUnaryExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.types.AUnknownTypeIR;
import org.overture.codegen.ir.CodeGenBase;
import org.overture.codegen.ir.IRConstants;
import org.overture.codegen.ir.IREventObserver;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.IRSettings;
import org.overture.codegen.ir.IRStatus;
import org.overture.codegen.ir.IrNodeInfo;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.traces.TracesTrans;
import org.overture.codegen.trans.AssignStmTrans;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.trans.uniontypes.UnionTypeTrans;
import org.overture.codegen.trans.uniontypes.UnionTypeVarPrefixes;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.vdm2java.IJavaQuoteEventObserver;
import org.overture.codegen.vdm2java.JavaCodeGen;
import org.overture.codegen.vdm2java.JavaCodeGenUtil;
import org.overture.codegen.vdm2java.JavaSettings;
import org.overture.codegen.vdm2jml.data.RecClassInfo;
import org.overture.codegen.vdm2jml.data.StateDesInfo;
import org.overture.codegen.vdm2jml.predgen.TypePredDecorator;
import org.overture.codegen.vdm2jml.predgen.info.AbstractTypeInfo;
import org.overture.codegen.vdm2jml.predgen.info.NamedTypeInfo;
import org.overture.codegen.vdm2jml.predgen.info.NamedTypeInvDepCalculator;
import org.overture.codegen.vdm2jml.trans.JmlTraceTrans;
import org.overture.codegen.vdm2jml.trans.JmlUnionTypeTrans;
import org.overture.codegen.vdm2jml.trans.RecAccessorTrans;
import org.overture.codegen.vdm2jml.trans.RecInvTransformation;
import org.overture.codegen.vdm2jml.trans.TargetNormaliserTrans;
import org.overture.codegen.vdm2jml.trans.TcExpInfo;
import org.overture.codegen.vdm2jml.util.AnnotationSorter;
import org.overture.codegen.vdm2jml.util.IsValChecker;
import org.overture.codegen.vdm2jml.util.NameGen;

import de.hunsicker.jalopy.storage.Convention;
import de.hunsicker.jalopy.storage.ConventionKeys;

public class JmlGenerator implements IREventObserver, IJavaQuoteEventObserver
{
	private static final String VDM_JML_RUNTIME_IMPORT = "org.overture.codegen.vdm2jml.runtime.*";
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
	public static final String JML_SET_INV_CHECKS = "//@ set %s = %s;";

	public static final String JML_INVARIANT_FOR = "\\invariant_for";
	public static final String REC_VALID_METHOD_NAMEVALID = "valid";
	public static final String REC_VALID_METHOD_CALL = REC_VALID_METHOD_NAMEVALID + "()";
	
	public static final String JAVA_INSTANCEOF = " instanceof ";
	
	private JavaCodeGen javaGen;
	
	private JmlSettings jmlSettings;
	
	private List<NamedTypeInfo> typeInfoList;
	private JmlGenUtil util;
	private JmlAnnotationHelper annotator;
	
	private StateDesInfo stateDesInfo;
	
	// The class owning the invChecksOn flag
	private ADefaultClassDeclIR invChecksFlagOwner = null;
	
	private List<TcExpInfo> tcExpInfo;
	
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

		List<INode> cloneFreeNodes = javaGen.getJavaFormat().getValueSemantics().getCloneFreeNodes();
		
		// Replace the union type transformation
		for(int i = 0; i < series.size(); i++)
		{
			DepthFirstAnalysisAdaptor currentTr = series.get(i);
			
			if(currentTr instanceof UnionTypeTrans)
			{
				TransAssistantIR assist = javaGen.getTransAssistant();
				UnionTypeVarPrefixes varPrefixes = javaGen.getVarPrefixManager().getUnionTypePrefixes();
				
				JmlUnionTypeTrans newUnionTypeTr = new JmlUnionTypeTrans(assist, varPrefixes, cloneFreeNodes, stateDesInfo);
				
				series.set(i, newUnionTypeTr);
			}
			else if(currentTr instanceof TracesTrans)
			{
				TracesTrans orig = (TracesTrans) currentTr;
				JmlTraceTrans newTraceTrans = new JmlTraceTrans(orig.getTransAssist(), orig.getIteVarPrefixes(), orig.getTracePrefixes(), orig.getLangIterator(), orig.getToStringBuilder(), cloneFreeNodes);
				series.set(i, newTraceTrans);
				this.tcExpInfo = newTraceTrans.getTcExpInfo();
			}
		}
		
		// Now add the assignment transformation
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
		irSettings.setGenerateTraces(true);
		
		JavaSettings javaSettings = getJavaSettings();
		javaSettings.setGenRecsAsInnerClasses(false);
		
		// Bugs in Jalopy requires a small tweak to the code formatting conventions.
		// Force Jalopy to not remove 'scope' braces
		Convention.getInstance().putBoolean(ConventionKeys.BRACE_REMOVE_BLOCK, false);
		
		this.jmlSettings = new JmlSettings();
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

		return javaGen.generate(CodeGenBase.getNodes(ast));
	}

	@Override
	public List<IRStatus<PIR>> initialIRConstructed(
			List<IRStatus<PIR>> ast, IRInfo info)
	{
		List<IRStatus<AModuleDeclIR>> modules = IRStatus.extract(ast, AModuleDeclIR.class);
		
		for(IRStatus<AModuleDeclIR> m : modules)
		{
			for(SDeclIR d : m.getIrNode().getDecls())
			{
				if(d instanceof AFieldDeclIR)
				{
					AFieldDeclIR f = (AFieldDeclIR) d;
					
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
							
						} catch (org.overture.codegen.ir.analysis.AnalysisException e)
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
	public List<IRStatus<PIR>> finalIRConstructed(List<IRStatus<PIR>> ast,
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
		
		List<IRStatus<PIR>> newAst = new LinkedList<>(ast);

		// To circumvent a problem with OpenJML. See documentation of makeRecsOuterClasses
		newAst.addAll(util.makeRecsOuterClasses(ast, recInfo));
		
		// Also extract classes that are records
		for(IRStatus<ADefaultClassDeclIR> status : IRStatus.extract(newAst, ADefaultClassDeclIR.class))
		{
			ADefaultClassDeclIR clazz = status.getIrNode();
			
			// VDM uses the type system to control whether 'nil' is allowed as a value so we'll
			// just annotate all classes as @nullable_by_default
			annotator.makeNullableByDefault(clazz);

			// Make sure that the classes can access the VDM to JML runtime
			addVdmToJmlRuntimeImport(clazz);
		}
		
		// Only extract from 'ast' to not get the record classes
		for (IRStatus<ADefaultClassDeclIR> status : IRStatus.extract(ast, ADefaultClassDeclIR.class))
		{
			ADefaultClassDeclIR clazz = status.getIrNode();

			if(info.getDeclAssistant().isLibraryName(clazz.getName()))
			{
				continue;
			}
			
			for (AFieldDeclIR f : clazz.getFields())
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
			for (AMethodDeclIR m : clazz.getMethods())
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
		
		TypePredDecorator assertTr = new TypePredDecorator(this, stateDesInfo, recInfo);
		
		// Add assertions to check for violation of record and named type invariants
		addAssertions(newAst, assertTr);

		// Make sure that the JML annotations are ordered correctly
		sortAnnotations(newAst);

		// Make all nodes have a copy of each named type invariant method
		util.distributeNamedTypeInvs(newAst);
		
		// Type check trace tests
		tcTraceTest(assertTr);
		
		// Return back the modified AST to the Java code generator
		return newAst;
	}

	private void tcTraceTest(TypePredDecorator assertTr)
	{
		for(TcExpInfo currentInfo : tcExpInfo)
		{
			AbstractTypeInfo typeInfo = assertTr.getTypePredUtil().findTypeInfo(currentInfo.getFormalParamType());

			String enclosingClass = currentInfo.getTraceEnclosingClass();
			String javaRootPackage = getJavaSettings().getJavaRootPackage();
			
			SClassDeclIR clazz = getJavaGen().getInfo().getDeclAssistant().findClass(getJavaGen().getInfo().getClasses(), enclosingClass);
			NameGen nameGen = new NameGen(clazz);
			String expRef = currentInfo.getExpRef();
			String checkStr = typeInfo.consCheckExp(enclosingClass, javaRootPackage, expRef, nameGen);

			currentInfo.getTypeCheck().setMetaData(annotator.consMetaData("//@ " + JML_ASSERT_ANNOTATION + " " +  checkStr + ";"));
		}
	}

	private void addVdmToJmlRuntimeImport(ADefaultClassDeclIR clazz)
	{
		String vdmJmlRuntimeImport = VDM_JML_RUNTIME_IMPORT;
		List<ClonableString> allImports = new LinkedList<>();
		allImports.addAll(clazz.getDependencies());
		allImports.add(new ClonableString(vdmJmlRuntimeImport));
		clazz.setDependencies(allImports);
	}

	private RecClassInfo makeRecStateAccessorBased(List<IRStatus<PIR>> ast) {

		RecAccessorTrans recAccTr = new RecAccessorTrans(this);

		for (IRStatus<PIR> status : ast) {
			try {
				javaGen.getIRGenerator().applyPartialTransformation(status, recAccTr);
			} catch (org.overture.codegen.ir.analysis.AnalysisException e) {

				Logger.getLog().printErrorln(
						"Problems applying '" + RecAccessorTrans.class + "' to status " + status.getIrNodeName());
				e.printStackTrace();
			}
		}
		
		return recAccTr.getRecInfo();
	}

	private void sortAnnotations(List<IRStatus<PIR>> newAst)
	{
		AnnotationSorter sorter = new AnnotationSorter();

		for (IRStatus<ADefaultClassDeclIR> status : IRStatus.extract(newAst, ADefaultClassDeclIR.class))
		{
			if (!javaGen.getInfo().getDeclAssistant().isLibraryName(status.getIrNode().getName()))
			{
				try
				{
					status.getIrNode().apply(sorter);
				} catch (org.overture.codegen.ir.analysis.AnalysisException e)
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

	private void addAssertions(List<IRStatus<PIR>> newAst, TypePredDecorator assertTr)
	{
		for (IRStatus<ADefaultClassDeclIR> status : IRStatus.extract(newAst, ADefaultClassDeclIR.class))
		{
			ADefaultClassDeclIR clazz = status.getIrNode();

			if (!this.javaGen.getInfo().getDeclAssistant().isLibraryName(clazz.getName()))
			{
				try
				{
					this.javaGen.getIRGenerator().applyPartialTransformation(status, assertTr);
				} catch (org.overture.codegen.ir.analysis.AnalysisException e)
				{
					Logger.getLog().printErrorln("Unexpected problem occured when applying transformation in '"
							+ this.getClass().getSimpleName() + "'");
					e.printStackTrace();
				}
			}
		}
	}

	private void annotateRecsWithInvs(List<IRStatus<PIR>> ast)
	{
		List<ARecordDeclIR> recs = util.getRecords(ast);
		
		for(ARecordDeclIR r : recs)
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

	private void setInvChecksOnOwner(List<IRStatus<PIR>> ast) {
		
		for (IRStatus<ADefaultClassDeclIR> status : IRStatus.extract(ast, ADefaultClassDeclIR.class))
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
	private void changeRecInvMethod(ARecordDeclIR rec)
	{
		try
		{
			rec.getInvariant().apply(new RecInvTransformation(javaGen, rec));
		} catch (org.overture.codegen.ir.analysis.AnalysisException e)
		{
			Logger.getLog().printErrorln("Problems transforming the invariant method of a record in '"
					+ this.getClass().getSimpleName() + "'");
			e.printStackTrace();
		}
	}

	private List<ClonableString> consMethodCond(SDeclIR decl,
			List<AFormalParamLocalParamIR> parentMethodParams, String jmlAnno)
	{
		if (decl instanceof AMethodDeclIR)
		{
			AMethodDeclIR cond = (AMethodDeclIR) decl;

			List<String> fieldNames = new LinkedList<>();

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
	
	public void adjustNamedTypeInvFuncs(IRStatus<ADefaultClassDeclIR> status)
	{
		ADefaultClassDeclIR clazz = status.getIrNode();
		
		for (ATypeDeclIR typeDecl : clazz.getTypeDecls())
		{
			if (typeDecl.getDecl() instanceof ANamedTypeDeclIR)
			{
				ANamedTypeDeclIR namedTypeDecl = (ANamedTypeDeclIR) typeDecl.getDecl();

				AMethodDeclIR method = util.getInvMethod(typeDecl);

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
				
				AFormalParamLocalParamIR invParam = util.getInvFormalParam(method);
				AFormalParamLocalParamIR invParamCopy = invParam.clone();
				
				invParam.setType(new AUnknownTypeIR());
				
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
				
				ABlockStmIR declStmBlock = new ABlockStmIR();
				
				if(!invMethodIsGen)
				{
					AIdentifierVarExpIR paramVar = util.getInvParamVar(method);
					
					if(paramVar == null)
					{
						continue;
					}
					
					ACastUnaryExpIR cast = new ACastUnaryExpIR();
					cast.setType(invParamCopy.getType().clone());
					cast.setExp(paramVar.clone());
					
					AVarDeclIR decl = javaGen.getInfo().getDeclAssistant().consLocalVarDecl(invParamCopy.getType(), invParamCopy.getPattern(), cast);
					declStmBlock.setScoped(false);
					declStmBlock.getLocalDefs().add(decl);
				}
				
				SStmIR body = method.getBody();

				ABlockStmIR repBlock = new ABlockStmIR();
				javaGen.getTransAssistant().replaceNodeWith(body, repBlock);

				repBlock.getStatements().add(declStmBlock);
				repBlock.getStatements().add(body);
			}
		}
	}
	
	public StateDesInfo normaliseTargets(List<IRStatus<PIR>> newAst)
	{
		TargetNormaliserTrans normaliser = new TargetNormaliserTrans(this);
		for (IRStatus<PIR> n : newAst)
		{
			try
			{
				javaGen.getIRGenerator().applyPartialTransformation(n, normaliser);
			} catch (org.overture.codegen.ir.analysis.AnalysisException e)
			{
				Logger.getLog().printErrorln("Problem normalising state designators in '"
						+ this.getClass().getSimpleName() + "': "
						+ e.getMessage());
				e.printStackTrace();
			}
		}

		return normaliser.getStateDesInfo();
	}
	
	@Override
	public void quoteClassesProduced(List<ADefaultClassDeclIR> quoteClasses)
	{
		for(ADefaultClassDeclIR qc : quoteClasses)
		{
			// Code generated quotes are represented as singletons and by default the instance
			// field is null. So we'll mark quote classes as nullable_by_default.
			//Example from class represented <A>: private static AQuote instance = null;
			annotator.makeNullableByDefault(qc);
			addVdmToJmlRuntimeImport(qc);
		}
	}
	
	public JmlSettings getJmlSettings()
	{
		return jmlSettings;
	}
	
	public void setJmlSettings(JmlSettings jmlSettings)
	{
		this.jmlSettings = jmlSettings;
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

	public ADefaultClassDeclIR getInvChecksFlagOwner()
	{
		return invChecksFlagOwner;
	}
	
	public StateDesInfo getStateDesInfo()
	{
		return stateDesInfo;
	}
}
