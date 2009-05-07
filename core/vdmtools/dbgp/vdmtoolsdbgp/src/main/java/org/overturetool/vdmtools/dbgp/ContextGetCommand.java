/**
 * 
 */
package org.overturetool.vdmtools.dbgp;

//import java.util.HashMap;
//import java.util.HashSet;


final class ContextGetCommand extends CommandResponse {

//	private static final int LOCAL_CONTEXT_ID = 0;
//	private static final int GLOBAL_CONTEXT_ID = 1;
//	private static final int CLASS_CONTEXT_ID = 2;
	/**
	 * 
	 */

	/**
	 * @param debugger
	 */
	ContextGetCommand() {
	}

	@Override
	protected String parseAndExecute(DBGPCommand command) throws DBGPException {
		String properties = getProperties();
		try {
//			int level = Integer.parseInt(command.getOption(DBGPOptionType.D).value);
//			int context = -1;
//			String contextString = command.getOption(DBGPOptionType.C).value;
//			if (contextString != null) {
//				context = Integer.parseInt(contextString);
//			}
		}catch (Exception e) {
			
		}
		return "<response " +
					"command=\"context_get\"\r\n" + 
					"status=\"starting\" " + 
					"reason=\"ok\" " + 
					"transaction_id=\"" + command.getOption(DBGPOptionType.TRANSACTION_ID).value + 
				"\">\r\n" + 
					properties + 
				"</response>\r\n";
	}
	
	private String getProperties()
	{
//		int id = 0;
		String fullName = "TestData";
		String data_type = "String";
		String name_of_object_class = "Sort3";
		boolean hasChilds = false;
		int numC = 0;
		String vlEncoded = Base64Helper.encodeString("['TestData','']");
		
		return 
			"<property\r\n" + 
				"name=\"" + fullName + "\" " + 
				"fullname=\"" + fullName + "\" " + 
				"type=\"" + data_type + "\" " + 
				"classname=\"" + name_of_object_class + "\" " + 
				"constant=\"0\"\r\n" + 
				"children=\"" + (hasChilds ? 1 : 0) + "\" " + 
				"encoding=\"base64\" " + 
				"numchildren=\"" + numC + "\">" + 
					vlEncoded + 
			"</property>";
	}
}