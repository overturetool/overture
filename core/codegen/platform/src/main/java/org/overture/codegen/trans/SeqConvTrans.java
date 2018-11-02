package org.overture.codegen.trans;

import org.apache.log4j.Logger;
import org.overture.ast.definitions.ARenamedDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.node.INode;
import org.overture.codegen.assistant.AssistantBase;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.AVarDeclIR;
import org.overture.codegen.ir.declarations.SClassDeclIR;
import org.overture.codegen.ir.expressions.*;
import org.overture.codegen.ir.statements.AAssignToExpStmIR;
import org.overture.codegen.ir.statements.AForAllStmIR;
import org.overture.codegen.ir.statements.AReturnStmIR;
import org.overture.codegen.ir.types.*;
import org.overture.codegen.trans.assistants.TransAssistantIR;

import java.util.LinkedList;
import java.util.List;

public class SeqConvTrans extends DepthFirstAnalysisAdaptor
{
	protected Logger log = Logger.getLogger(this.getClass().getName());

	private TransAssistantIR transformationAssistant;

	public SeqConvTrans(TransAssistantIR transformationAssistant)
	{
		this.transformationAssistant = transformationAssistant;
	}

	@Override
	public void caseAForAllStmIR(AForAllStmIR node) throws AnalysisException
	{
		if (node.getExp().getType() instanceof AStringTypeIR)
		{
			ASeqSeqTypeIR seqType = new ASeqSeqTypeIR();
			seqType.setEmpty(false);
			seqType.setSeq1(false);
			seqType.setOptional(false);
			seqType.setSeqOf(new ACharBasicTypeIR());

			correctExpToSeq(node.getExp(), seqType);
		}

		node.getBody().apply(this);
	}

	@Override
	public void inAFieldDeclIR(AFieldDeclIR node) throws AnalysisException
	{
		STypeIR nodeType = node.getType();
		SExpIR initial = node.getInitial();

		handleVarExp(nodeType, initial);

		if (initial == null)
		{
			return;
		}

		handleExp(initial, nodeType);
	}

	@Override
	public void inAFieldNumberExpIR(AFieldNumberExpIR node)
			throws AnalysisException
	{
		node.getTuple().apply(this);

		if (node.getType() instanceof AStringTypeIR)
		{
			correctExpToString(node);
		} else if (node.getType() instanceof SSeqTypeIR)
		{
			correctExpToSeq(node, node.getType());
		}
	}

	@Override
	public void inAVarDeclIR(AVarDeclIR node) throws AnalysisException
	{
		STypeIR nodeType = node.getType();
		SExpIR exp = node.getExp();

		handleVarExp(nodeType, exp);

		if (exp == null)
		{
			return;
		}

		handleExp(exp, nodeType);
	}

	@Override
	public void caseAAssignToExpStmIR(AAssignToExpStmIR node)
			throws AnalysisException
	{
		if (node.getExp() != null)
		{
			node.getExp().apply(this);
			handleExp(node.getExp(), node.getTarget().getType());
		}
	}

	private void handleExp(SExpIR exp, STypeIR nodeType)
	{
		if (exp.getType() instanceof AStringTypeIR
				&& nodeType instanceof SSeqTypeIR)
		{
			correctExpToSeq(exp, nodeType);
		} else if (exp.getType() instanceof SSeqTypeIR
				&& nodeType instanceof AStringTypeIR)
		{
			correctExpToString(exp);
		}
	}

	private void handleVarExp(STypeIR nodeType, SExpIR exp)
			throws AnalysisException
	{
		if (!(nodeType instanceof SSeqTypeIR))
		{
			return;
		}

		if (exp != null)
		{
			exp.apply(this);
		}
	}

	@Override
	public void caseAApplyExpIR(AApplyExpIR node) throws AnalysisException {
		super.caseAApplyExpIR(node);

		SExpIR root = node.getRoot();
		STypeIR type = root.getType();

		String moduleName;
		String fieldName;

		if(root instanceof AMethodInstantiationExpIR)
		{
			root = ((AMethodInstantiationExpIR) root).getFunc();
		}

		if(root instanceof AExplicitVarExpIR)
		{
			AExplicitVarExpIR expVar = (AExplicitVarExpIR) root;
			STypeIR classType = expVar.getClassType();

			if(classType instanceof AClassTypeIR)
			{
				moduleName = ((AClassTypeIR) classType).getName();
			}
			else
			{
				return;
			}
			fieldName = expVar.getName();
		}
		else if(root instanceof AIdentifierVarExpIR)
		{
			fieldName = ((AIdentifierVarExpIR) root).getName();

			INode var = AssistantBase.getVdmNode(node);

			if(var instanceof AVariableExp) {
				PDefinition def = ((AVariableExp) var).getVardef();

				if (def instanceof ARenamedDefinition) {
					ARenamedDefinition renamedDef = (ARenamedDefinition) def;

					moduleName = renamedDef.getDef().getName().getModule();
				}
				else
				{
					SClassDeclIR encClass = node.getAncestor(SClassDeclIR.class);
					if(encClass != null)
					{
						moduleName = encClass.getName();
					}
					else
					{
						log.warn("Could not find enclosing class for " + node);
						return;
					}

				}
			}
			else
			{
				SClassDeclIR encClass = node.getAncestor(SClassDeclIR.class);
				if(encClass != null)
				{
					moduleName = encClass.getName();
				}
				else
				{
					log.warn("Could not find enclosing class for " + node);
					return;
				}
			}
		}
		else if(root instanceof AFieldExpIR)
		{
			AFieldExpIR fieldExp = (AFieldExpIR) root;
			fieldName = fieldExp.getMemberName();

			STypeIR classType = ((AFieldExpIR) root).getObject().getType();

			if(classType instanceof AClassTypeIR)
			{
				moduleName = ((AClassTypeIR) classType).getName();
			}
			else
			{
				return;
			}
		}
		else
		{
			return;
		}

		if(!(type instanceof AMethodTypeIR))
		{
			return;
		}

		AMethodTypeIR rootType = (AMethodTypeIR) type;

		// All arguments -- except type arguments (polymorphic types)
		List<SExpIR> args = new LinkedList<>();
		for(int i = 0; i < rootType.getParams().size(); i++)
		{
			args.add(node.getArgs().get(i));
		}

		AMethodTypeIR methodType = transformationAssistant.getInfo().getTypeAssistant().
				getMethodType(transformationAssistant.getInfo(), moduleName, fieldName, args);

		if(methodType == null)
		{
			return;
		}

		for(int i = 0; i < methodType.getParams().size(); i ++)
		{
			STypeIR paramType = methodType.getParams().get(i);
			STypeIR argType = node.getArgs().get(i).getType();

			if (paramType instanceof AStringTypeIR
					&& argType instanceof SSeqTypeIR)
			{
				correctExpToString(node.getArgs().get(i));

			} else if (paramType instanceof SSeqTypeIR
					&& argType instanceof AStringTypeIR)
			{
				correctExpToSeq(node.getArgs().get(i), paramType);
			}
		}
	}

	@Override
	public void defaultInSBinaryExpIR(SBinaryExpIR node)
			throws AnalysisException
	{
		SExpIR left = node.getLeft();
		SExpIR right = node.getRight();

		left.apply(this);
		right.apply(this);

		if (left.getType() instanceof AStringTypeIR
				&& right.getType() instanceof AStringTypeIR)
		{
			node.setType(new AStringTypeIR());
			return;
		}

		if (node.getType() instanceof SSeqTypeIR
				|| node instanceof AEqualsBinaryExpIR
				|| node instanceof ANotEqualsBinaryExpIR)
		{
			if (left.getType() instanceof AStringTypeIR
					&& right.getType() instanceof SSeqTypeIR)
			{
				correctExpToString(right);
			} else if (right.getType() instanceof AStringTypeIR
					&& left.getType() instanceof SSeqTypeIR)
			{
				correctExpToString(left);
			} else
			{
				return;
			}

			node.setType(new AStringTypeIR());
		}
	}

	@Override
	public void inAReturnStmIR(AReturnStmIR node) throws AnalysisException
	{
		SExpIR exp = node.getExp();

		if (exp != null)
		{
			exp.apply(this);
		} else
		{
			return;
		}

		AMethodDeclIR method = node.getAncestor(AMethodDeclIR.class);
		AMethodTypeIR methodType = method.getMethodType();

		if (methodType.getResult() instanceof AStringTypeIR)
		{
			if (!(exp.getType() instanceof SSeqTypeIR))
			{
				return;
			}

			correctExpToString(exp);
			;
		} else if (methodType.getResult() instanceof SSeqTypeIR)
		{
			if (!(exp.getType() instanceof AStringTypeIR))
			{
				return;
			}

			correctExpToSeq(exp, exp.getType());
		}
	}

	private void correctExpToSeq(SExpIR toCorrect, STypeIR type)
	{
		AStringToSeqUnaryExpIR conversion = new AStringToSeqUnaryExpIR();

		transformationAssistant.replaceNodeWith(toCorrect, conversion);

		conversion.setType(type.clone());
		conversion.setExp(toCorrect);
	}

	private void correctExpToString(SExpIR toCorrect)
	{
		ASeqToStringUnaryExpIR conversion = new ASeqToStringUnaryExpIR();

		transformationAssistant.replaceNodeWith(toCorrect, conversion);

		conversion.setType(new AStringTypeIR());
		conversion.setExp(toCorrect);
	}
}
