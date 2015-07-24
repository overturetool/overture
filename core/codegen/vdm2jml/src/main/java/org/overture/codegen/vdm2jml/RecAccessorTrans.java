package org.overture.codegen.vdm2jml;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AFormalParamLocalParamCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.ARecordDeclCG;
import org.overture.codegen.cgast.expressions.AApplyExpCG;
import org.overture.codegen.cgast.expressions.AFieldExpCG;
import org.overture.codegen.cgast.statements.AAssignToExpStmCG;
import org.overture.codegen.cgast.statements.ACallObjectExpStmCG;
import org.overture.codegen.cgast.statements.AReturnStmCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.AMethodTypeCG;
import org.overture.codegen.cgast.types.ARecordTypeCG;
import org.overture.codegen.cgast.types.AVoidTypeCG;
import org.overture.codegen.ir.IRConstants;

public class RecAccessorTrans extends DepthFirstAnalysisAdaptor {

	private static final String VALID = "valid";
	private static final String GET = "get_";
	private static final String SET = "set_";

	private JmlGenerator jmlGen;

	public RecAccessorTrans(JmlGenerator jmlGen) {
		this.jmlGen = jmlGen;
	}

	// private void privatizeFields(ARecordDeclCG node)
	// {
	// for (AFieldDeclCG f : node.getFields())
	// {
	// f.setAccess(IRConstants.PRIVATE);
	// }
	// }

	@Override
	public void caseARecordDeclCG(ARecordDeclCG node) throws AnalysisException {
		// TODO: Privatise record fields?
		node.getMethods().addAll(consAccessors(node));
		node.getMethods().add(consValidMethod());
	}

	@Override
	public void caseAAssignToExpStmCG(AAssignToExpStmCG node) throws AnalysisException {
		if (node.getTarget() instanceof AFieldExpCG) {

			AFieldExpCG target = (AFieldExpCG) node.getTarget();

			if (target.getObject().getType() instanceof ARecordTypeCG) {

				ACallObjectExpStmCG setCall = new ACallObjectExpStmCG();
				setCall.setFieldName(consSetCallName(target.getMemberName()));
				setCall.getArgs().add(node.getExp().clone());
				setCall.setType(new AVoidTypeCG());
				setCall.setObj(target.getObject().clone());
				setCall.setSourceNode(node.getSourceNode());

				jmlGen.getJavaGen().getTransAssistant().replaceNodeWith(node, setCall);

				setCall.getObj().apply(this);
				setCall.getArgs().getFirst().apply(this);
			}
		} else {
			node.getTarget().apply(this);
			node.getExp().apply(this);
		}
	}

	@Override
	public void caseAFieldExpCG(AFieldExpCG node) throws AnalysisException {

		node.getObject().apply(this);

		if (node.getObject().getType() instanceof ARecordTypeCG) {
			AMethodTypeCG getterType = new AMethodTypeCG();
			getterType.setResult(node.getType().clone());

			AFieldExpCG getterField = node.clone();
			getterField.setType(getterType);
			getterField.setMemberName(consGetCallName(node.getMemberName()));

			AApplyExpCG getCall = new AApplyExpCG();
			getCall.setRoot(getterField);
			getCall.setType(node.getType().clone());
			getCall.setSourceNode(node.getSourceNode());

			jmlGen.getJavaGen().getTransAssistant().replaceNodeWith(node, getCall);
		}
	}

	private List<AMethodDeclCG> consAccessors(ARecordDeclCG node) {

		List<AMethodDeclCG> accessors = new LinkedList<AMethodDeclCG>();

		for (AFieldDeclCG f : node.getFields()) {
			accessors.add(consGetter(f));
			accessors.add(consSetter(f));
		}

		return accessors;
	}

	private AMethodDeclCG consSetter(AFieldDeclCG f) {
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

	private AMethodDeclCG consGetter(AFieldDeclCG f) {
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
		validMethod.setName(VALID);
		validMethod.setStatic(false);

		AMethodTypeCG methodType = new AMethodTypeCG();
		methodType.setResult(new ABoolBasicTypeCG());
		validMethod.setMethodType(methodType);
		
		jmlGen.getAnnotator().makePure(validMethod);
		
		AReturnStmCG body = new AReturnStmCG();
		body.setExp(jmlGen.getJavaGen().getInfo().getExpAssistant().consBoolLiteral(true));
		
		validMethod.setBody(body);
		
		return validMethod;
	}
	
	public String consGetCallName(String fieldName) {
		return GET + fieldName;
	}

	public String consSetCallName(String fieldName) {
		return SET + fieldName;
	}

	private String consParamName(AFieldDeclCG f) {
		return "_" + f.getName();
	}
}
