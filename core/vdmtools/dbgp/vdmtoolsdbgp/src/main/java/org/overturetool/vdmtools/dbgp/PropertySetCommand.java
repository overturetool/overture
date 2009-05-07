/**
 * 
 */
package org.overturetool.vdmtools.dbgp;


final class PropertySetCommand extends CommandResponse {
	PropertySetCommand() {
	}

	@Override
	protected String parseAndExecute(DBGPCommand command) throws DBGPException {
		String name = command.getOption(DBGPOptionType.N).value;
		int num = Integer.parseInt(command.getOption(DBGPOptionType.D).value);
		String value = Base64Helper.decodeString(command.data);
		System.out.println("name: " + name + " num " + num + " value: " + value);
		
		return "<response " +
					"command=\"property_set\" " + 
					"transaction_id=\"" + command.getOption(DBGPOptionType.TRANSACTION_ID).value + "\" " +
					"success=\"0\" " + 
				">" + 
				"</response>";
	}
}