package org.overture.codegen.vdm2jml.trans;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.statements.ACallStm;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.PType;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.declarations.ACatchClauseDeclIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.expressions.SVarExpIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.statements.AMetaStmIR;
import org.overture.codegen.ir.statements.APlainCallStmIR;
import org.overture.codegen.ir.statements.AReturnStmIR;
import org.overture.codegen.ir.statements.ATryStmIR;
import org.overture.codegen.ir.types.AExternalTypeIR;
import org.overture.codegen.ir.SourceNode;
import org.overture.codegen.traces.StoreAssistant;
import org.overture.codegen.traces.TraceStmBuilder;
import org.overture.codegen.traces.TracesTrans;

public class JmlTraceStmBuilder extends TraceStmBuilder
{
	private static final String ASSERTION_ERROR_TYPE = "AssertionError";
	private static final String ASSERTION_ERROR_PARAM = "e";
	
	private List<TcExpInfo> tcExpInfo;
	
	public JmlTraceStmBuilder(TracesTrans traceTrans, String traceEnclosingClass, StoreAssistant storeAssist, List<TcExpInfo> tcExpInfo)
	{
		super(traceTrans, traceEnclosingClass, storeAssist);
		this.tcExpInfo = tcExpInfo;
	}
	
	private AMetaStmIR consTypeCheckExp(SVarExpIR arg, STypeIR formalParamType, String traceEnclosingClass,
			StoreAssistant storeAssistant)
	{
		/**
		 * Don't do anything with 'tc' yet. Later it will be replaced with a proper dynamic type check
		 */
		AMetaStmIR tc = new AMetaStmIR();

		tcExpInfo.add(new TcExpInfo(arg.getName(), formalParamType, tc, traceEnclosingClass));

		return tc;
	}
	
	@Override
	public AMethodDeclIR consTypeCheckMethod(SStmIR stm)
	{
		/**
		 * We don't need to consider the 'ACallObjectExpStmIR' since it only appears in the IR if we code generate a PP
		 * or RT model
		 */
		if(!(stm instanceof APlainCallStmIR))
		{
			return null;
		}
		
		APlainCallStmIR call = (APlainCallStmIR) stm;
		
		if(call.getArgs().isEmpty())
		{
			// Nothing to type check
			return null;
		}
		
		List<STypeIR> argTypes = null;
		SourceNode source = call.getSourceNode();
		if (source != null)
		{
			org.overture.ast.node.INode vdmNode = source.getVdmNode();

			if (vdmNode instanceof ACallStm)
			{
				ACallStm callStm = (ACallStm) vdmNode;
				PDefinition def = callStm.getRootdef();
				
				PType type = def.getType();
				
				List<PType> vdmArgTypes = null;
				if(type instanceof AOperationType)
				{
					vdmArgTypes = ((AOperationType) type).getParameters();;
				}
				else if(type instanceof AFunctionType)
				{
					vdmArgTypes = ((AFunctionType) type).getParameters();
				}
				
				if(vdmArgTypes != null)
				{
					argTypes = new LinkedList<>();
					for(PType t : vdmArgTypes)
					{
						try
						{
							argTypes.add(t.apply(traceTrans.getTransAssist().getInfo().getTypeVisitor(), traceTrans.getTransAssist().getInfo()));
						} catch (org.overture.ast.analysis.AnalysisException e)
						{
							argTypes = null;
							e.printStackTrace();
						}
					}
				}
			}
		}
		
		if(argTypes == null)
		{
			log.error("Could not find argument types for call statement: " + call);
			return null;
		}
		
		if(argTypes.size() != call.getArgs().size())
		{
			log.error("Argument types and arguments do not match");
			return null;
		}
		
		ABlockStmIR methodBody = new ABlockStmIR();
		
		ATryStmIR tryStm = new ATryStmIR();
		methodBody.getStatements().add(tryStm);
		
		ABlockStmIR tryBody = new ABlockStmIR();
		tryStm.setStm(tryBody);
		tryStm.getCatchClauses().add(consTcFailHandling());
		
		// Construct a body on the form
		// try {
		//   //@ azzert tc(arg1));
		//   ...
		//   //@ azzert tc(argN));
		// } catch(AssertionError e) { return false; }
		// return true;
		
		for(int i = 0; i < call.getArgs().size(); i++)
		{
			SExpIR a = call.getArgs().get(i);
			
			if(a instanceof SVarExpIR)
			{
				AMetaStmIR tc = consTypeCheckExp((SVarExpIR) a, argTypes.get(i), traceEnclosingClass, storeAssistant);
				tryBody.getStatements().add(tc);
			}
			else
			{
				log.error("Expected argument to be a variable expression by now. Got: " + a);
			}
		}
		
		// If we make it to end of the method it means that the arguments type checked successfully
		AReturnStmIR retTrue = new AReturnStmIR();
		retTrue.setExp(traceTrans.getTransAssist().getInfo().getExpAssistant().consBoolLiteral(true));
		methodBody.getStatements().add(retTrue);
		
		AMethodDeclIR typeCheckMethod = initPredDecl(traceTrans.getTracePrefixes().callStmIsTypeCorrectNamePrefix());
		typeCheckMethod.setBody(methodBody);
		
		return typeCheckMethod;
	}

	private ACatchClauseDeclIR consTcFailHandling()
	{
		AExternalTypeIR externalType = new AExternalTypeIR();
		externalType.setName(ASSERTION_ERROR_TYPE);

		ACatchClauseDeclIR catchClause = new ACatchClauseDeclIR();
		catchClause.setType(externalType);
		catchClause.setName(ASSERTION_ERROR_PARAM);
		
		AReturnStmIR retFalse = new AReturnStmIR();
		retFalse.setExp(traceTrans.getTransAssist().getInfo().getExpAssistant().consBoolLiteral(false));
		catchClause.setStm(retFalse);
		
		return catchClause;
	}
}
