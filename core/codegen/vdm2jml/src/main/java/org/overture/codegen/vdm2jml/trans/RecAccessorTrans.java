package org.overture.codegen.vdm2jml.trans;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.declarations.AFormalParamLocalParamIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.ARecordDeclIR;
import org.overture.codegen.ir.declarations.AVarDeclIR;
import org.overture.codegen.ir.expressions.AApplyExpIR;
import org.overture.codegen.ir.expressions.AFieldExpIR;
import org.overture.codegen.ir.expressions.AMapSeqGetExpIR;
import org.overture.codegen.ir.statements.AAssignToExpStmIR;
import org.overture.codegen.ir.statements.ACallObjectExpStmIR;
import org.overture.codegen.ir.statements.AMapSeqUpdateStmIR;
import org.overture.codegen.ir.statements.AReturnStmIR;
import org.overture.codegen.ir.types.ABoolBasicTypeIR;
import org.overture.codegen.ir.types.AMethodTypeIR;
import org.overture.codegen.ir.types.ARecordTypeIR;
import org.overture.codegen.ir.types.AVoidTypeIR;
import org.overture.codegen.ir.IRConstants;
import org.overture.codegen.vdm2java.JavaValueSemantics;
import org.overture.codegen.vdm2jml.JmlGenerator;
import org.overture.codegen.vdm2jml.data.RecClassInfo;

public class RecAccessorTrans extends DepthFirstAnalysisAdaptor
{
	public static final String GET_PREFIX = "get_";
	public static final String SET_PREFIX = "set_";

	private JmlGenerator jmlGen;
	private RecClassInfo recInfo;

	private boolean inTarget = false;

	public RecAccessorTrans(JmlGenerator jmlGen)
	{
		this.jmlGen = jmlGen;
		this.recInfo = new RecClassInfo();
	}

	// private void privatizeFields(ARecordDeclIR node)
	// {
	// for (AFieldDeclIR f : node.getFields())
	// {
	// f.setAccess(IRConstants.PRIVATE);
	// }
	// }

	@Override
	public void caseARecordDeclIR(ARecordDeclIR node) throws AnalysisException
	{
		// TODO: Privatise record fields?
		List<AMethodDeclIR> accessors = consAccessors(node);
		registerAccessors(accessors);
		node.getMethods().addAll(accessors);

		if(!this.jmlGen.getJmlSettings().genInvariantFor())
		{
			node.getMethods().add(consValidMethod());
		}
	}

	@Override
	public void caseAAssignToExpStmIR(AAssignToExpStmIR node)
			throws AnalysisException
	{
		if (node.getTarget() instanceof AFieldExpIR)
		{
			AFieldExpIR target = (AFieldExpIR) node.getTarget();

			if (target.getObject().getType() instanceof ARecordTypeIR)
			{
				ACallObjectExpStmIR setCall = new ACallObjectExpStmIR();
				setCall.setFieldName(consSetCallName(target.getMemberName()));
				setCall.getArgs().add(node.getExp().clone());
				setCall.setType(new AVoidTypeIR());

				SExpIR obj = target.getObject().clone();
				jmlGen.getJavaGen().getJavaFormat().getValueSemantics().addCloneFreeNode(obj);
				
				setCall.setObj(obj);
				setCall.setSourceNode(node.getSourceNode());

				/**
				 * Replacing the assignment statement with a setter call makes the setter call the new state designator
				 * owner
				 */
				jmlGen.getStateDesInfo().replaceStateDesOwner(node, setCall);
				
				jmlGen.getJavaGen().getTransAssistant().replaceNodeWith(node, setCall);

				inTarget = true;
				setCall.getObj().apply(this);
				inTarget = false;

				setCall.getArgs().getFirst().apply(this);
			}
		} else
		{
			inTarget = true;
			node.getTarget().apply(this);
			inTarget = false;
			node.getExp().apply(this);
		}
	}

	@Override
	public void caseAFieldExpIR(AFieldExpIR node) throws AnalysisException
	{
		node.getObject().apply(this);

		if (node.getObject().getType() instanceof ARecordTypeIR && !(node.parent() instanceof AApplyExpIR))
		{
			AMethodTypeIR getterType = new AMethodTypeIR();
			getterType.setResult(node.getType().clone());

			AFieldExpIR getterField = node.clone();
			getterField.setType(getterType);

			AApplyExpIR getCall = new AApplyExpIR();
			getCall.setRoot(getterField);
			getCall.setType(node.getType().clone());
			getCall.setSourceNode(node.getSourceNode());
			getterField.setMemberName(consGetCallName(node.getMemberName()));

			/**
			 * The getters added to the record classes do not copy object references representing values. Therefore we
			 * need to take it into account when we do the field read call
			 */
			if (cloneFieldRead(node))
			{
				getCall = makeCopy(getCall);
			}

			jmlGen.getJavaGen().getTransAssistant().replaceNodeWith(node, getCall);
		}
	}

	private AApplyExpIR makeCopy(AApplyExpIR getCall)
	{
		AApplyExpIR copyCall = jmlGen.getJavaGen().getJavaFormat().getJavaFormatAssistant().consUtilCopyCall();
		copyCall.getArgs().add(getCall);
		return copyCall;
	}

	private boolean cloneFieldRead(AFieldExpIR node)
	{
		AVarDeclIR decl = node.getAncestor(AVarDeclIR.class);
		
		/*
		 * Normalized state designators do not need cloning
		 */
		if (decl != null && jmlGen.getStateDesInfo().isStateDesDecl(decl))
		{
			return false;
		}
		
		if(jmlGen.getJavaGen().getJavaFormat().getValueSemantics().isCloneFree(node))
		{
			return false;
		}
		
		JavaValueSemantics valSem = jmlGen.getJavaGen().getJavaFormat().getValueSemantics();
		
		return !inTarget && !isObjOfFieldExp(node) && !isColOfMapSeq(node)
				&& valSem.mayBeValueType(node.getType());
	}

	private boolean isObjOfFieldExp(AFieldExpIR node)
	{
		return node.parent() instanceof AFieldExpIR
				&& ((AFieldExpIR) node.parent()).getObject() == node;
	}

	private boolean isColOfMapSeq(AFieldExpIR node)
	{
		return (node.parent() instanceof AMapSeqGetExpIR
				&& ((AMapSeqGetExpIR) node.parent()).getCol() == node)
				|| (node.parent() instanceof AMapSeqUpdateStmIR
						&& ((AMapSeqUpdateStmIR) node.parent()).getCol() == node);
	}

	private List<AMethodDeclIR> consAccessors(ARecordDeclIR node)
	{
		List<AMethodDeclIR> accessors = new LinkedList<AMethodDeclIR>();

		for (AFieldDeclIR f : node.getFields())
		{
			accessors.add(consGetter(f));
			accessors.add(consSetter(f));
		}

		return accessors;
	}

	private AMethodDeclIR consSetter(AFieldDeclIR f)
	{
		AMethodDeclIR setter = new AMethodDeclIR();

		setter.setAbstract(false);
		setter.setAccess(IRConstants.PUBLIC);
		setter.setAsync(false);
		setter.setImplicit(false);
		setter.setIsConstructor(false);
		setter.setName(consSetCallName(f.getName()));
		setter.setStatic(false);
		setter.setSourceNode(f.getSourceNode());

		String paramName = consParamName(f);

		AFormalParamLocalParamIR param = new AFormalParamLocalParamIR();
		param.setType(f.getType().clone());
		param.setPattern(jmlGen.getJavaGen().getInfo().getPatternAssistant().consIdPattern(paramName));
		setter.getFormalParams().add(param);

		AMethodTypeIR methodType = new AMethodTypeIR();
		methodType.setResult(new AVoidTypeIR());
		methodType.getParams().add(f.getType().clone());
		setter.setMethodType(methodType);

		AAssignToExpStmIR fieldUpdate = new AAssignToExpStmIR();
		fieldUpdate.setTarget(jmlGen.getJavaGen().getInfo().getExpAssistant().consIdVar(f.getName(), f.getType().clone()));
		fieldUpdate.setExp(jmlGen.getJavaGen().getInfo().getExpAssistant().consIdVar(paramName, f.getType().clone()));

		setter.setBody(fieldUpdate);

		return setter;
	}

	private AMethodDeclIR consGetter(AFieldDeclIR f)
	{
		AMethodDeclIR getter = new AMethodDeclIR();
		getter.setAbstract(false);
		getter.setAccess(IRConstants.PUBLIC);
		getter.setAsync(false);
		getter.setImplicit(false);
		getter.setIsConstructor(false);
		getter.setName(consGetCallName(f.getName()));
		getter.setStatic(false);
		getter.setSourceNode(f.getSourceNode());

		AMethodTypeIR methodType = new AMethodTypeIR();
		methodType.setResult(f.getType().clone());
		getter.setMethodType(methodType);

		AReturnStmIR returnField = new AReturnStmIR();
		returnField.setExp(jmlGen.getJavaGen().getInfo().getExpAssistant().consIdVar(f.getName(), f.getType().clone()));
		getter.setBody(returnField);

		jmlGen.getAnnotator().makePure(getter);
		
		return getter;
	}

	public AMethodDeclIR consValidMethod()
	{
		AMethodDeclIR validMethod = new AMethodDeclIR();

		validMethod.setAbstract(false);
		validMethod.setAccess(IRConstants.PUBLIC);
		validMethod.setAsync(false);
		validMethod.setImplicit(false);
		validMethod.setIsConstructor(false);
		validMethod.setName(JmlGenerator.REC_VALID_METHOD_NAMEVALID);
		validMethod.setStatic(false);

		AMethodTypeIR methodType = new AMethodTypeIR();
		methodType.setResult(new ABoolBasicTypeIR());
		validMethod.setMethodType(methodType);

		AReturnStmIR body = new AReturnStmIR();
		body.setExp(jmlGen.getJavaGen().getInfo().getExpAssistant().consBoolLiteral(true));

		validMethod.setBody(body);
		
		jmlGen.getAnnotator().makePure(validMethod);

		return validMethod;
	}

	private void registerAccessors(List<AMethodDeclIR> accessors)
	{
		for (AMethodDeclIR a : accessors)
		{
			recInfo.register(a);
		}
	}

	public String consGetCallName(String fieldName)
	{
		return GET_PREFIX + fieldName;
	}

	public String consSetCallName(String fieldName)
	{
		return SET_PREFIX + fieldName;
	}

	private String consParamName(AFieldDeclIR f)
	{
		return "_" + f.getName();
	}

	public RecClassInfo getRecInfo()
	{
		return recInfo;
	}
}
