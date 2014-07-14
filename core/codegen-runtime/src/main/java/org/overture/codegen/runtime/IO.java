package org.overture.codegen.runtime;

import java.io.File;
import java.util.List;


public class IO {
	
	private static final String NOT_SUPPORTED_MSG = "Operation is currently not supported";
	
    public static <p> boolean writeval(p val) {
        
    	String text = val.toString();
    	
    	System.out.print(text);
    	System.out.flush();
    	
    	return true;
    }

    public static <p> boolean fwriteval(String filename, p val, int fdir) {

    	throw new UnsupportedOperationException(NOT_SUPPORTED_MSG);
    }
    
    public static <p> boolean fwriteval(VDMSeq filename, p val, int fdir) {

    	throw new UnsupportedOperationException(NOT_SUPPORTED_MSG);
    }

    public static <p> Tuple freadval(String filename) {
        
    	throw new UnsupportedOperationException(NOT_SUPPORTED_MSG);
    }
    
    public static <p> Tuple freadval(VDMSeq filename) {
        
    	throw new UnsupportedOperationException(NOT_SUPPORTED_MSG);
    }

	protected static File getFile(String fval)
	{
		throw new UnsupportedOperationException(NOT_SUPPORTED_MSG);
	}
    
	protected static File getFile(VDMSeq fval)
	{
		throw new UnsupportedOperationException(NOT_SUPPORTED_MSG);
	}
	
    public boolean echo(String text) {
    	throw new UnsupportedOperationException(NOT_SUPPORTED_MSG);
    }
    
    public boolean echo(VDMSeq text) {
    	throw new UnsupportedOperationException(NOT_SUPPORTED_MSG);
    }

    public boolean fecho(String filename, String text, Integer fdir) {
    	throw new UnsupportedOperationException(NOT_SUPPORTED_MSG);
    }
    
    public boolean fecho(VDMSeq filename, VDMSeq text, Integer fdir) {
    	throw new UnsupportedOperationException(NOT_SUPPORTED_MSG);
    }

    public String ferror() {
    	throw new UnsupportedOperationException(NOT_SUPPORTED_MSG);
    }

    public static void print(Object arg) {
    	
		System.out.printf("%s", arg);
		System.out.flush();
    }

    public static void println(Object arg) {
    	
    	System.out.printf("%s", arg);
    	System.out.printf("%s", "\n");
    	System.out.flush();

    }

    public static void printf(String format, List<Object> args) {
        
		System.out.printf(format, args.toArray());
		System.out.flush();
    }
    
    public static void printf(VDMSeq seq, List<Object> args) {
		
    	System.out.printf(seq.toString(), args.toArray());
		System.out.flush();
    }
}
