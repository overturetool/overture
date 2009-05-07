package org.overturetool.vdmtools.dbgp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URI;
import java.net.URISyntaxException;


public class SourceCommand extends CommandResponse{

	@Override
	protected String parseAndExecute(DBGPCommand command) throws DBGPException {

		DBGPOption option = command.getOption(DBGPOptionType.B);
//		int begin = 1;
//
//		if (option != null)
//		{
//			begin = Integer.parseInt(option.value);
//		}
//
//		option = command.getOption(DBGPOptionType.E);
//		int end = 0;
//
//		if (option != null)
//		{
//			end = Integer.parseInt(option.value);
//		}

		option = command.getOption(DBGPOptionType.F);

		if (option == null)
		{
			throw new DBGPException(DBGPErrorCode.INVALID_OPTIONS, command.toString());
		}
		File file = null;
		try
		{
			file = new File(new URI(option.value));
		}
		catch (URISyntaxException e)
		{
			throw new DBGPException(DBGPErrorCode.INVALID_OPTIONS, command.toString());
		}

		try {
			
			BufferedReader input =  new BufferedReader(new FileReader(file));
			StringBuilder content = new StringBuilder();
			
			String line = null;
			while ((line = input.readLine()) != null) {
				content.append(line);
				content.append(System.getProperty("line.separator"));
			}

			String response = 
				"<response " +
					"command=\"source\" " +
					"success=\"1\" " +
					"transaction_id=\""+ command.getOption(DBGPOptionType.TRANSACTION_ID).value + "\"" +
				">" +
					Base64Helper.encodeString(content.toString()) + 
				"</response>";
			
			return response;
		} catch (Exception e) {
			throw new DBGPException(DBGPErrorCode.INVALID_OPTIONS, command.toString());
		}
	}

}
