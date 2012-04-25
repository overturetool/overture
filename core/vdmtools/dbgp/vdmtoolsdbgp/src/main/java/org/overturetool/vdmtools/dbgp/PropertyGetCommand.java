/**
 * 
 */
package org.overturetool.vdmtools.dbgp;


final class PropertyGetCommand extends CommandResponse {
	PropertyGetCommand() {
	
	}

	@Override
	protected String parseAndExecute(DBGPCommand command) throws DBGPException {
		String longName = command.getOption(DBGPOptionType.N).value;
		String depth =  command.getOption(DBGPOptionType.D).value;
		
		return "<response command=\"property_get\" " + 
					"transaction_id=\"" + command.getOption(DBGPOptionType.TRANSACTION_ID).value + "\">" + 
					getProperty(depth, longName) + 
				"</response>\r\n" + "";
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