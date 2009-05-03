/**
 * 
 */
package org.overturetool.vdmtools.dbgp;

import org.overturetool.vdmtools.VDMToolsProject;
import org.overturetool.vdmtools.interpreter.Interpreter.DebugState;

final class EvalCommand extends CommandResponse {

	private StackManager stackManager;
	private VDMToolsProject vdmToolsProject;
	
	EvalCommand() {
		stackManager = StackManager.getInstance();
		vdmToolsProject = VDMToolsProject.getInstance();
	}

	@Override
	protected String parseAndExecute(DBGPCommand command) throws DBGPException {
		String value = command.data;
//		if (command.data != null){
//			value = Base64Helper.decodeString(command.getOption(DBGPOptionType.DATA).value);
//		}
		if (value.length() == 0)
			value = "self";
		if (stackManager.getDebugState() != DebugState.BREAKPOINT)
		{
			throw new DBGPException(DBGPErrorCode.NOT_AVAILABLE, command.toString());
		}

//		if (value == null) {
//			String response = 
//				"<response " +
//					"command=\"eval\" " + 
//					"transaction_id=\"" + command.getOption(DBGPOptionType.TRANSACTION_ID).value  + "\" " +
//					"success=\"1\" " + ">" + 
//						dummyProperty(0) + 
//				"</response>";
//			return response;
//		}
		
		
		try
		{
			String expression = command.data;
			String evaluatedExpression = vdmToolsProject.GetInterpreter().EvalExpressionToString(expression); 
			if (evaluatedExpression.equals("")){
				evaluatedExpression = "no legal expression";
			}
			String response = 
				"<response command=\"eval\"" + 
					" transaction_id=\"" + command.getOption(DBGPOptionType.TRANSACTION_ID).value + "\" " +
					"success=\"1\" " + 
				">" + 
					getProperty("evalExpression", evaluatedExpression) + 
				"</response>";
			return response;
			
		}
		catch (Exception e) {
			String response = 
				"<response command=\"eval\"" + 
					" transaction_id=\"" + command.getOption(DBGPOptionType.TRANSACTION_ID).value + "\" " +
					"success=\"1\" " + 
				">" + 
					getProperty("evalExpression", e.getMessage()) + 
				"</response>";
			return response;
		}
	}
	

	private String getProperty(String name,String evaluatedResponse)
	{
		String response = 
			"<property " +
				"name=\"" + name + "\" " +
				"fullname=\""+ name +"\" " +
				"type=\"String\" " + 
//				"classname=\"name_of_object_class\" " +
				"constant=\"0\" " + 
				"children=\"0\" " +
				"size=\"" + evaluatedResponse.getBytes().length +  "\" " +
//				"page=\"0\" " + 
//				"pagesize=\"{NUM}\" " +
//				"address=\"{NUM}\" " +
//				"key=\"language_dependent_key\" " +
				"encoding=\"base64\" "+
//				"numchildren=\"0\"" +
			 ">" +
			 "<![CDATA[" + 
					Base64Helper.encodeString(evaluatedResponse) +
			 "]]>" +
			 "</property>";

		return response;
	}
}