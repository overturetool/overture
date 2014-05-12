package org.overture.interpreter.utilities.type;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.node.INode;
import org.overture.ast.types.AInMapMapType;
import org.overture.ast.types.AMapMapType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AParameterType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.AQuoteType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.SBasicType;
import org.overture.ast.types.SInvariantType;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.assistant.type.AInMapMapTypeAssistantInterpreter;
import org.overture.interpreter.assistant.type.AOptionalTypeAssistantInterpreter;
import org.overture.interpreter.assistant.type.AParameterTypeAssistantInterpreter;
import org.overture.interpreter.assistant.type.AProductTypeAssistantInterpreter;
import org.overture.interpreter.assistant.type.AQuoteTypeAssistantInterpreter;
import org.overture.interpreter.assistant.type.ASetTypeAssistantInterpreter;
import org.overture.interpreter.assistant.type.AUnionTypeAssistantInterpreter;
import org.overture.interpreter.assistant.type.SBasicTypeAssistantInterpreter;
import org.overture.interpreter.assistant.type.SInvariantTypeAssistantInterpreter;
import org.overture.interpreter.assistant.type.SMapTypeAssistantInterpreter;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.values.ValueList;

/***************************************
 * 
 * This class ...
 * 
 * @author gkanos
 *
 ****************************************/
public class AllValuesCollector extends QuestionAnswerAdaptor<Context, ValueList>
{
	protected IInterpreterAssistantFactory af;
	
	public AllValuesCollector(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}
	
	@Override
	public ValueList defaultSBasicType(SBasicType type, Context ctxt)
			throws AnalysisException
	{
		return SBasicTypeAssistantInterpreter.getAllValues(type, ctxt);
	}
	
	@Override
	public ValueList defaultSInvariantType(SInvariantType type, Context ctxt)
			throws AnalysisException
	{
		return SInvariantTypeAssistantInterpreter.getAllValues(type, ctxt);
	}
	
	@Override
	public ValueList caseAInMapMapType(AInMapMapType type, Context ctxt)
			throws AnalysisException
	{
		return AInMapMapTypeAssistantInterpreter.getAllValues(type, ctxt);
	}
	
	@Override
	public ValueList caseAMapMapType(AMapMapType type, Context ctxt)
			throws AnalysisException
	{
		return SMapTypeAssistantInterpreter.getAllValues((AMapMapType) type, ctxt);
	}
	
	@Override
	public ValueList caseAOptionalType(AOptionalType type, Context ctxt)
			throws AnalysisException
	{
		return AOptionalTypeAssistantInterpreter.getAllValues(type, ctxt);
	}
	
	@Override
	public ValueList caseAProductType(AProductType type, Context ctxt)
			throws AnalysisException
	{
		return AProductTypeAssistantInterpreter.getAllValues(type, ctxt);
	}
	
	@Override
	public ValueList caseAQuoteType(AQuoteType type, Context ctxt)
			throws AnalysisException
	{
		return AQuoteTypeAssistantInterpreter.getAllValues(type, ctxt);
	}
	
	@Override
	public ValueList caseASetType(ASetType type, Context ctxt)
			throws AnalysisException
	{
		return ASetTypeAssistantInterpreter.getAllValues(type, ctxt);
	}
	
	@Override
	public ValueList caseAUnionType(AUnionType type, Context ctxt)
			throws AnalysisException
	{
		return AUnionTypeAssistantInterpreter.getAllValues(type, ctxt);
	}
	
	@Override
	public ValueList caseAParameterType(AParameterType type, Context ctxt)
			throws AnalysisException
	{
		return AParameterTypeAssistantInterpreter.getAllValues(type, ctxt);
	}
//	} else if (type instanceof SMapType)
//	{
//		if (type instanceof AInMapMapType)
//		{
//			
//		} else if (type instanceof AMapMapType)
//		{
//			
//		}

	@Override
	public ValueList createNewReturnValue(INode node, Context question)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ValueList createNewReturnValue(Object node, Context question)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}



}
