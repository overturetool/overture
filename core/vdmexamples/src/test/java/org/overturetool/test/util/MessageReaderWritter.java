package org.overturetool.test.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.overturetool.test.framework.examples.IMessage;
import org.overturetool.test.framework.examples.Message;



public class MessageReaderWritter {

	enum MsgType {Warning,Error};
	
	public static String WARNING_LABEL = "WARNING"; 
	public static String ERROR_LABEL = "ERROR";
	
	Set<IMessage> errors = null; 
	Set<IMessage> warnings = null;
	
	public MessageReaderWritter() {
		errors = new HashSet<IMessage>();
		warnings = new HashSet<IMessage>();
	}
	
	public void setWarningsAndErrors(List<IMessage> errors, List<IMessage> warnings)
	{
		this.errors.clear();
		this.warnings.clear();
		this.errors.addAll(errors);
		this.warnings.addAll(warnings);
	}
	
	public Set<IMessage> getErrors() {
		return errors;
	}
	
	public Set<IMessage> getWarnings() {
		return warnings;
	}
	
	public boolean readFromFile(File file)
	{
		errors.clear();
		warnings.clear();
				
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			String line;
			while((line = in.readLine()) != null)
			{
				MsgType type = null;
				if(line.startsWith(ERROR_LABEL))
				{
					type = MsgType.Error;
					line = line.substring(ERROR_LABEL.length() + 1);
				}
				else if(line.startsWith(WARNING_LABEL))
				{
					type = MsgType.Warning;
					line = line.substring(WARNING_LABEL.length() + 1);
				}
				else
				{
					return false;
				}
								
				String[] splitLine = line.split(":");
				
				if(splitLine.length != 3)
				{
					return false;
				}
				
				int number = Integer.parseInt(splitLine[0]);
				
				String[] position = splitLine[1].split(",");
				
				if(position.length != 2)
				{
					return false;
				}
				
				int startLine = Integer.parseInt(position[0]);
				int startCol = Integer.parseInt(position[1]);
				
				String message = splitLine[2];
				
				IMessage msg = new Message(number, startLine, startCol, message);
					
				switch (type) {
				case Error:
					errors.add(msg);
					break;
				case Warning:
					warnings.add(msg);
					break;				
				}
					
			}
			
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		
		
		
		return true;
	}
	
	
	

	public boolean writeToFile(String pathname)
	{
		File file = new File(pathname);
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(file));			
			for (IMessage warning : warnings) {
				StringBuffer sb = new StringBuffer();
				sb.append(WARNING_LABEL);
				sb.append(":");
				sb.append(warning.getNumber());
				sb.append(":");
				sb.append(warning.getLine());
				sb.append(",");
				sb.append(warning.getCol());
				sb.append(":");
				sb.append(warning.getMessage());
				out.write(sb.toString());
				out.newLine();
			}
			
			for (IMessage error : errors) {
				StringBuffer sb = new StringBuffer();
				sb.append(ERROR_LABEL);
				sb.append(":");
				sb.append(error.getNumber());
				sb.append(":");
				sb.append(error.getLine());
				sb.append(",");
				sb.append(error.getCol());
				sb.append(":");
				sb.append(error.getMessage());
				out.write(sb.toString());
				out.newLine();
			}
			
			out.flush();
			out.close();
		} catch (IOException e) {
			return false;
		}
		
		
		return true;
	}
	
}
