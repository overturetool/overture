/**
 * 
 */
package org.overture.codegen.trans.conc;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.ir.IRConstants;
import org.overture.codegen.ir.IRGeneratedTag;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.declarations.AFormalParamLocalParamIR;
import org.overture.codegen.ir.declarations.AInterfaceDeclIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.APersyncDeclIR;
import org.overture.codegen.ir.declarations.SClassDeclIR;
import org.overture.codegen.ir.expressions.ABoolLiteralExpIR;
import org.overture.codegen.ir.expressions.ACastUnaryExpIR;
import org.overture.codegen.ir.expressions.AEqualsBinaryExpIR;
import org.overture.codegen.ir.expressions.AFieldExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.expressions.AIntLiteralExpIR;
import org.overture.codegen.ir.expressions.ANewExpIR;
import org.overture.codegen.ir.expressions.ASelfExpIR;
import org.overture.codegen.ir.name.ATokenNameIR;
import org.overture.codegen.ir.name.ATypeNameIR;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.ir.statements.AAssignToExpStmIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.statements.AElseIfStmIR;
import org.overture.codegen.ir.statements.AIfStmIR;
import org.overture.codegen.ir.statements.APlainCallStmIR;
import org.overture.codegen.ir.statements.AReturnStmIR;
import org.overture.codegen.ir.statements.ATryStmIR;
import org.overture.codegen.ir.types.ABoolBasicTypeIR;
import org.overture.codegen.ir.types.AClassTypeIR;
import org.overture.codegen.ir.types.AExternalTypeIR;
import org.overture.codegen.ir.types.AIntNumericBasicTypeIR;
import org.overture.codegen.ir.types.AMethodTypeIR;
import org.overture.codegen.ir.types.AVoidTypeIR;

/**
 * @author gkanos
 */
public class MainClassConcTrans extends DepthFirstAnalysisAdaptor
{
	public static final String MULTIPLE_INHERITANCE_WARNING = "Generation of concurrency "
			+ "constructs does not support multiple inheritance";

	private IRInfo info;
	private ConcPrefixes concPrefixes;

	public MainClassConcTrans(IRInfo info, ConcPrefixes concPrefixes)
	{
		this.info = info;
		this.concPrefixes = concPrefixes;
	}

	@Override
	public void caseADefaultClassDeclIR(ADefaultClassDeclIR node)
			throws AnalysisException
	{
		if (!info.getSettings().generateConc())
		{
			return;
		}
		
		if(info.getDeclAssistant().parentIsTest(node))
		{
			return;
		}

		if (node.getSuperNames().size() > 1)
		{
			info.addTransformationWarning(node, MULTIPLE_INHERITANCE_WARNING);
			return;
		}

		if(info.getDeclAssistant().isFullyAbstract(info.getDeclAssistant().getSourceClass(node), info))
		{
			return;
		}
		
		for (AFieldDeclIR fieldIR : node.getFields())
		{
			if (!fieldIR.getFinal())
			{
				fieldIR.setVolatile(true);
			}
		}

		AInterfaceDeclIR interf = new AInterfaceDeclIR();
		interf.setName(concPrefixes.evalPpTypeName());

		node.getInterfaces().add(interf);

		AExternalTypeIR sentType = new AExternalTypeIR();
		sentType.setName(concPrefixes.sentinelClassName());
		AFieldDeclIR sentinelfld = new AFieldDeclIR();
		sentinelfld.setName(concPrefixes.sentinelInstanceName());
		sentinelfld.setType(sentType);
		sentinelfld.setAccess(IRConstants.PUBLIC);
		sentinelfld.setVolatile(true);
		sentinelfld.setStatic(false);

		node.getFields().add(sentinelfld);

		for (AMethodDeclIR methodIR : node.getMethods())
		{
			if (methodIR.getStatic() != null && !methodIR.getStatic()
					&& !isIRGenerated(methodIR))
			{
				if (!methodIR.getIsConstructor() && !methodIR.getAbstract())
				{
					ABlockStmIR bodyStm = new ABlockStmIR();

					APlainCallStmIR entering = new APlainCallStmIR();
					APlainCallStmIR leaving = new APlainCallStmIR();

					entering.setName(concPrefixes.enteringMethodName());
					AClassTypeIR sentinel = new AClassTypeIR();
					sentinel.setName(concPrefixes.sentinelInstanceName());

					entering.setClassType(sentinel);
					entering.setType(new AVoidTypeIR());

					AFieldExpIR field = new AFieldExpIR();
					field.setMemberName(methodIR.getName());

					ACastUnaryExpIR cast = new ACastUnaryExpIR();
					AIdentifierVarExpIR varSentinel = new AIdentifierVarExpIR();
					varSentinel.setIsLocal(true);
					varSentinel.setIsLambda(false);
					varSentinel.setName(concPrefixes.sentinelInstanceName());

					AExternalTypeIR etype = new AExternalTypeIR();
					etype.setName(node.getName()
							+ concPrefixes.sentinelClassPostFix());

					cast.setExp(varSentinel);
					cast.setType(etype);
					field.setObject(cast);

					entering.getArgs().add(field);

					leaving.setName(concPrefixes.leavingMethodName());
					leaving.setClassType(sentinel.clone());
					leaving.setType(new AVoidTypeIR());
					leaving.getArgs().add(field.clone());

					bodyStm.getStatements().add(entering);
					ATryStmIR trystm = new ATryStmIR();
					trystm.setStm(methodIR.getBody());
					trystm.setFinally(leaving);
					bodyStm.getStatements().add(trystm);

					methodIR.setBody(bodyStm);
				}
			}

			if (methodIR.getIsConstructor())
			{
				ABlockStmIR bodyConst = new ABlockStmIR();

				AAssignToExpStmIR stm = new AAssignToExpStmIR();

				AIdentifierVarExpIR field = new AIdentifierVarExpIR();

				field.setName(concPrefixes.sentinelInstanceName());
				field.setIsLocal(false);

				ANewExpIR newexp = new ANewExpIR();

				ATypeNameIR classtype = new ATypeNameIR();
				classtype.setName(node.getName()
						+ concPrefixes.sentinelClassPostFix());

				newexp.setName(classtype);
				newexp.getArgs().add(new ASelfExpIR());

				stm.setExp(newexp);
				stm.setTarget(field);

				bodyConst.getStatements().add(stm);
				bodyConst.getStatements().add(methodIR.getBody());

				methodIR.setBody(bodyConst);
			}
		}
		// declaration of the method.

		AIntNumericBasicTypeIR fnr = new AIntNumericBasicTypeIR();
		AIdentifierPatternIR identifier = new AIdentifierPatternIR();
		identifier.setName(concPrefixes.funcNumberParamName());
		AFormalParamLocalParamIR fnrloc = new AFormalParamLocalParamIR();
		fnrloc.setType(fnr);
		fnrloc.setPattern(identifier);
		AMethodTypeIR methType = new AMethodTypeIR();
		methType.setResult(new ABoolBasicTypeIR());

		AMethodDeclIR evaluatePPmethod = new AMethodDeclIR();
		evaluatePPmethod.setAccess(IRConstants.PUBLIC);
		evaluatePPmethod.setName(concPrefixes.evalPpMethodName());
		evaluatePPmethod.setImplicit(false);
		evaluatePPmethod.setMethodType(methType);
		evaluatePPmethod.setIsConstructor(false);

		evaluatePPmethod.getFormalParams().add(fnrloc);

		// Body of the method.
		if (node.getMethods().size() != 0)
		{

			// fixing the overloaded operation problem
			List<AMethodDeclIR> classuniqueMethods = new LinkedList<>();

			for (AMethodDeclIR m : node.getMethods())
			{
				classuniqueMethods.add(m.clone());
			}

			classuniqueMethods.clear();

			List<AMethodDeclIR> allMethods;

			if (!node.getSuperNames().isEmpty())
			{
				allMethods = info.getDeclAssistant().getAllMethods(node, info.getClasses());
			} else
			{
				allMethods = node.getMethods();
			}

			for (AMethodDeclIR method : allMethods)
			{
				if (!classuniqueMethods.contains(method))
				{
					classuniqueMethods.add(method.clone());
				}
			}

			AIfStmIR bodyif = new AIfStmIR();
			for (int i = 0; i < classuniqueMethods.size(); i++)
			{

				AIdentifierVarExpIR testVar = new AIdentifierVarExpIR();
				testVar.setType(new AIntNumericBasicTypeIR());
				testVar.setName(concPrefixes.funcNumberParamName());
				testVar.setIsLocal(true);

				if (i == 0)
				{
					AEqualsBinaryExpIR firstBranch = new AEqualsBinaryExpIR();

					AIntLiteralExpIR methNum = new AIntLiteralExpIR();
					methNum.setValue((long) i);

					firstBranch.setLeft(testVar);
					firstBranch.setRight(methNum);

					AReturnStmIR ret = new AReturnStmIR();
					ABoolLiteralExpIR boolret = new ABoolLiteralExpIR();
					boolret.setValue(true);
					ret.setExp(boolret);

					for (APersyncDeclIR per : node.getPerSyncs())
					{
						if (per.getOpname().equals(classuniqueMethods.get(i).getName()))
						{
							ret.setExp(per.getPred());
						}

					}

					bodyif.setIfExp(firstBranch);
					bodyif.setThenStm(ret);
				}

				else
				{
					AReturnStmIR ret = new AReturnStmIR();
					ABoolLiteralExpIR boolret = new ABoolLiteralExpIR();
					boolret.setValue(true);
					ret.setExp(boolret);

					for (APersyncDeclIR per : node.getPerSyncs())
					{
						if (per.getOpname().equals(classuniqueMethods.get(i).getName()))
						{
							ret.setExp(per.getPred());
						}
					}

					AElseIfStmIR newBranch = new AElseIfStmIR();

					AEqualsBinaryExpIR Branches = new AEqualsBinaryExpIR();

					AIntLiteralExpIR methNum = new AIntLiteralExpIR();
					methNum.setValue((long) i);

					Branches.setLeft(testVar);
					Branches.setRight(methNum);

					newBranch.setElseIf(Branches);
					newBranch.setThenStm(ret.clone());

					bodyif.getElseIf().add(newBranch);
				}
			}
			AReturnStmIR ret = new AReturnStmIR();

			ABoolLiteralExpIR defaultPer = new ABoolLiteralExpIR();
			defaultPer.setValue(true);

			ret.setExp(defaultPer);
			bodyif.setElseStm(ret.clone());

			evaluatePPmethod.setBody(bodyif);
		}

		node.getMethods().add(evaluatePPmethod);

		if (node.getThread() != null)
		{
			makeThread(node);
		}
	}

	private boolean isIRGenerated(AMethodDeclIR method)
	{
		return method.getTag() instanceof IRGeneratedTag;
	}

	private void makeThread(ADefaultClassDeclIR node)
	{
		SClassDeclIR threadClass = getThreadClass(node.getSuperNames(), node);

		threadClass.getSuperNames().clear();

		ATokenNameIR superName = new ATokenNameIR();
		superName.setName(concPrefixes.vdmThreadClassName());
		threadClass.getSuperNames().add(superName);
	}

	private SClassDeclIR getThreadClass(List<ATokenNameIR> superNames,
			SClassDeclIR classCg)
	{
		if (superNames.isEmpty()
				|| superNames.get(0).getName().equals(concPrefixes.vdmThreadClassName()))
		{
			return classCg;
		} else
		{
			SClassDeclIR superClass = null;

			for (SClassDeclIR c : info.getClasses())
			{
				if (c.getName().equals(superNames.get(0).getName()))
				{
					superClass = c;
					break;
				}
			}

			return getThreadClass(superClass.getSuperNames(), superClass);
		}
	}
}
