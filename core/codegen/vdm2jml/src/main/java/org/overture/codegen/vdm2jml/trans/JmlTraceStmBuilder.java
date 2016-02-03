package org.overture.codegen.vdm2jml.trans;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.statements.ACallStm;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.PType;
import org.overture.codegen.ir.SExpCG;
import org.overture.codegen.ir.SStmCG;
import org.overture.codegen.ir.STypeCG;
import org.overture.codegen.ir.declarations.ACatchClauseDeclCG;
import org.overture.codegen.ir.declarations.AMethodDeclCG;
import org.overture.codegen.ir.expressions.SVarExpCG;
import org.overture.codegen.ir.statements.ABlockStmCG;
import org.overture.codegen.ir.statements.AMetaStmCG;
import org.overture.codegen.ir.statements.APlainCallStmCG;
import org.overture.codegen.ir.statements.AReturnStmCG;
import org.overture.codegen.ir.statements.ATryStmCG;
import org.overture.codegen.ir.types.AExternalTypeCG;
import org.overture.codegen.ir.SourceNode;
import org.overture.codegen.logging.Logger;
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
	
	private AMetaStmCG consTypeCheckExp(SVarExpCG arg, STypeCG formalParamType, String traceEnclosingClass,
			StoreAssistant storeAssistant)
	{
		/**
		 * Don't do anything with 'tc' yet. Later it will be replaced with a proper dynamic type check
		 */
		AMetaStmCG tc = new AMetaStmCG();

		tcExpInfo.add(new TcExpInfo(arg.getName(), formalParamType, tc, traceEnclosingClass));

		return tc;
	}
	
	@Override
	public AMethodDeclCG consTypeCheckMethod(SStmCG stm)
	{
		/**
		 * We don't need to consider the 'ACallObjectExpStmCG' since it only appears in the IR if we code generate a PP
		 * or RT model
		 */
		if(!(stm instanceof APlainCallStmCG))
		{
			return null;
		}
		
		APlainCallStmCG call = (APlainCallStmCG) stm;
		
		if(call.getArgs().isEmpty())
		{
			// Nothing to type check
			return null;
		}
		
		List<STypeCG> argTypes = null;
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
			Logger.getLog().printErrorln("Could not find argument types for call statement " + call + " in '" + this.getClass().getSimpleName() + "'");
			return null;
		}
		
		if(argTypes.size() != call.getArgs().size())
		{
			Logger.getLog().printErrorln("Argument types and arguments do not match in '" + this.getClass().getSimpleName() + "'");
			return null;
		}
		
		ABlockStmCG methodBody = new ABlockStmCG();
		
		ATryStmCG tryStm = new ATryStmCG();
		methodBody.getStatements().add(tryStm);
		
		ABlockStmCG tryBody = new ABlockStmCG();
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
			SExpCG a = call.getArgs().get(i);
			
			if(a instanceof SVarExpCG)
			{
				AMetaStmCG tc = consTypeCheckExp((SVarExpCG) a, argTypes.get(i), traceEnclosingClass, storeAssistant);
				tryBody.getStatements().add(tc);
			}
			else
			{
				Logger.getLog().printError("Expected argument to be a variable expression by now. Got: " + a);
			}
		}
		
		// If we make it to end of the method it means that the arguments type checked successfully
		AReturnStmCG retTrue = new AReturnStmCG();
		retTrue.setExp(traceTrans.getTransAssist().getInfo().getExpAssistant().consBoolLiteral(true));
		methodBody.getStatements().add(retTrue);
		
		AMethodDeclCG typeCheckMethod = initPredDecl(traceTrans.getTracePrefixes().callStmIsTypeCorrectNamePrefix());
		typeCheckMethod.setBody(methodBody);
		
		return typeCheckMethod;
	}

	private ACatchClauseDeclCG consTcFailHandling()
	{
		AExternalTypeCG externalType = new AExternalTypeCG();
		externalType.setName(ASSERTION_ERROR_TYPE);

		ACatchClauseDeclCG catchClause = new ACatchClauseDeclCG();
		catchClause.setType(externalType);
		catchClause.setName(ASSERTION_ERROR_PARAM);
		
		AReturnStmCG retFalse = new AReturnStmCG();
		retFalse.setExp(traceTrans.getTransAssist().getInfo().getExpAssistant().consBoolLiteral(false));
		catchClause.setStm(retFalse);
		
		return catchClause;
	}
}
