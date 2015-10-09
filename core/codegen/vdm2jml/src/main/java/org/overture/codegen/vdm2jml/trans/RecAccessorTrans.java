package org.overture.codegen.vdm2jml.trans;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AFormalParamLocalParamCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.ARecordDeclCG;
import org.overture.codegen.cgast.declarations.AVarDeclCG;
import org.overture.codegen.cgast.expressions.AApplyExpCG;
import org.overture.codegen.cgast.expressions.AFieldExpCG;
import org.overture.codegen.cgast.expressions.AMapSeqGetExpCG;
import org.overture.codegen.cgast.statements.AAssignToExpStmCG;
import org.overture.codegen.cgast.statements.ACallObjectExpStmCG;
import org.overture.codegen.cgast.statements.AMapSeqUpdateStmCG;
import org.overture.codegen.cgast.statements.AReturnStmCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.AMethodTypeCG;
import org.overture.codegen.cgast.types.ARecordTypeCG;
import org.overture.codegen.cgast.types.AVoidTypeCG;
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

	// private void privatizeFields(ARecordDeclCG node)
	// {
	// for (AFieldDeclCG f : node.getFields())
	// {
	// f.setAccess(IRConstants.PRIVATE);
	// }
	// }

	@Override
	public void caseARecordDeclCG(ARecordDeclCG node) throws AnalysisException
	{
		// TODO: Privatise record fields?
		List<AMethodDeclCG> accessors = consAccessors(node);
		registerAccessors(accessors);
		node.getMethods().addAll(accessors);
		node.getMethods().add(consValidMethod());
	}

	@Override
	public void caseAAssignToExpStmCG(AAssignToExpStmCG node)
			throws AnalysisException
	{
		if (node.getTarget() instanceof AFieldExpCG)
		{
			AFieldExpCG target = (AFieldExpCG) node.getTarget();

			if (target.getObject().getType() instanceof ARecordTypeCG)
			{
				ACallObjectExpStmCG setCall = new ACallObjectExpStmCG();
				setCall.setFieldName(consSetCallName(target.getMemberName()));
				setCall.getArgs().add(node.getExp().clone());
				setCall.setType(new AVoidTypeCG());
				setCall.setObj(target.getObject().clone());
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
	public void caseAFieldExpCG(AFieldExpCG node) throws AnalysisException
	{
		node.getObject().apply(this);

		if (node.getObject().getType() instanceof ARecordTypeCG && !(node.parent() instanceof AApplyExpCG))
		{
			AMethodTypeCG getterType = new AMethodTypeCG();
			getterType.setResult(node.getType().clone());

			AFieldExpCG getterField = node.clone();
			getterField.setType(getterType);

			AApplyExpCG getCall = new AApplyExpCG();
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

	private AApplyExpCG makeCopy(AApplyExpCG getCall)
	{
		AApplyExpCG copyCall = jmlGen.getJavaGen().getJavaFormat().getJavaFormatAssistant().consUtilCopyCall();
		copyCall.getArgs().add(getCall);
		return copyCall;
	}

	private boolean cloneFieldRead(AFieldExpCG node)
	{
		AVarDeclCG decl = node.getAncestor(AVarDeclCG.class);
		
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
				&& valSem.usesStructuralEquivalence(node.getType());
	}

	private boolean isObjOfFieldExp(AFieldExpCG node)
	{
		return node.parent() instanceof AFieldExpCG
				&& ((AFieldExpCG) node.parent()).getObject() == node;
	}

	private boolean isColOfMapSeq(AFieldExpCG node)
	{
		return (node.parent() instanceof AMapSeqGetExpCG
				&& ((AMapSeqGetExpCG) node.parent()).getCol() == node)
				|| (node.parent() instanceof AMapSeqUpdateStmCG
						&& ((AMapSeqUpdateStmCG) node.parent()).getCol() == node);
	}

	private List<AMethodDeclCG> consAccessors(ARecordDeclCG node)
	{
		List<AMethodDeclCG> accessors = new LinkedList<AMethodDeclCG>();

		for (AFieldDeclCG f : node.getFields())
		{
			accessors.add(consGetter(f));
			accessors.add(consSetter(f));
		}

		return accessors;
	}

	private AMethodDeclCG consSetter(AFieldDeclCG f)
	{
		AMethodDeclCG setter = new AMethodDeclCG();

		setter.setAbstract(false);
		setter.setAccess(IRConstants.PUBLIC);
		setter.setAsync(false);
		setter.setImplicit(false);
		setter.setIsConstructor(false);
		setter.setName(consSetCallName(f.getName()));
		setter.setStatic(false);
		setter.setSourceNode(f.getSourceNode());

		String paramName = consParamName(f);

		AFormalParamLocalParamCG param = new AFormalParamLocalParamCG();
		param.setType(f.getType().clone());
		param.setPattern(jmlGen.getJavaGen().getInfo().getPatternAssistant().consIdPattern(paramName));
		setter.getFormalParams().add(param);

		AMethodTypeCG methodType = new AMethodTypeCG();
		methodType.setResult(new AVoidTypeCG());
		methodType.getParams().add(f.getType().clone());
		setter.setMethodType(methodType);

		AAssignToExpStmCG fieldUpdate = new AAssignToExpStmCG();
		fieldUpdate.setTarget(jmlGen.getJavaGen().getInfo().getExpAssistant().consIdVar(f.getName(), f.getType().clone()));
		fieldUpdate.setExp(jmlGen.getJavaGen().getInfo().getExpAssistant().consIdVar(paramName, f.getType().clone()));

		setter.setBody(fieldUpdate);

		return setter;
	}

	private AMethodDeclCG consGetter(AFieldDeclCG f)
	{
		AMethodDeclCG getter = new AMethodDeclCG();
		getter.setAbstract(false);
		getter.setAccess(IRConstants.PUBLIC);
		getter.setAsync(false);
		getter.setImplicit(false);
		getter.setIsConstructor(false);
		getter.setName(consGetCallName(f.getName()));
		getter.setStatic(false);
		getter.setSourceNode(f.getSourceNode());

		AMethodTypeCG methodType = new AMethodTypeCG();
		methodType.setResult(f.getType().clone());
		getter.setMethodType(methodType);

		AReturnStmCG returnField = new AReturnStmCG();
		returnField.setExp(jmlGen.getJavaGen().getInfo().getExpAssistant().consIdVar(f.getName(), f.getType().clone()));
		getter.setBody(returnField);

		jmlGen.getAnnotator().makePure(getter);
		
		return getter;
	}

	public AMethodDeclCG consValidMethod()
	{
		AMethodDeclCG validMethod = new AMethodDeclCG();

		validMethod.setAbstract(false);
		validMethod.setAccess(IRConstants.PUBLIC);
		validMethod.setAsync(false);
		validMethod.setImplicit(false);
		validMethod.setIsConstructor(false);
		validMethod.setName(JmlGenerator.REC_VALID_METHOD_NAMEVALID);
		validMethod.setStatic(false);

		AMethodTypeCG methodType = new AMethodTypeCG();
		methodType.setResult(new ABoolBasicTypeCG());
		validMethod.setMethodType(methodType);

		AReturnStmCG body = new AReturnStmCG();
		body.setExp(jmlGen.getJavaGen().getInfo().getExpAssistant().consBoolLiteral(true));

		validMethod.setBody(body);
		
		jmlGen.getAnnotator().makePure(validMethod);

		return validMethod;
	}

	private void registerAccessors(List<AMethodDeclCG> accessors)
	{
		for (AMethodDeclCG a : accessors)
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

	private String consParamName(AFieldDeclCG f)
	{
		return "_" + f.getName();
	}

	public RecClassInfo getRecInfo()
	{
		return recInfo;
	}
}
