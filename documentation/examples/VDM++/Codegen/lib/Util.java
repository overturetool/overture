package codegen;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import org.overturetool.vdmj.messages.Console;
import org.overturetool.vdmj.values.BooleanValue;
import org.overturetool.vdmj.values.CharacterValue;
import org.overturetool.vdmj.values.IntegerValue;
import org.overturetool.vdmj.values.QuoteValue;
import org.overturetool.vdmj.values.SeqValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;

/**
 *
 * @author spand
 */
public class Util implements Serializable {

    private static final String PROGRAMS_DIR = "Programs";
    private static final String GENERATED_DIR = "generated"+File.separatorChar+"java";

    public Value iToS(Value val){
        return new SeqValue(val.toString());
    }

    public Value showType(Value val){
        Console.out.println(val.toString());
        Console.out.println(val.getClass().getName());
        return val;
    }

    public Value getSimpleNames(){
        try {
        File programsDir = new File(PROGRAMS_DIR);
        String[] programs = programsDir.list(new FilenameFilter() {

            public boolean accept(File dir, String name) {
                return name.endsWith(".simple");
            }
        });
        
        ValueList list = new ValueList(programs.length);
        for (int i = 0; i < programs.length; i++){
            list.add(i, new SeqValue(programs[i].replace(".simple", "")));
        }
        
        Value result = new SeqValue(list);
        return result;
        } catch (NullPointerException e){
            Console.out.println("Null");
            return new SeqValue("");
        }
    }

    public Value parseSimpleProgram(Value filename) throws Exception {
        return new Simple.ASTParse().Parse(new QuoteValue("PP"), new SeqValue(PROGRAMS_DIR+File.separatorChar+stringOf(filename)+".simple"));
    }

    // Not tested
    public Value writeProgram(Value filename, Value contents){
        // Create the dir for generated files
        File generatedDir = new File(GENERATED_DIR);
        if (!generatedDir.exists()){
            boolean res = generatedDir.mkdirs();
            if (!res){
                Console.err.println("Couldnt create generated dir");
                return new BooleanValue(false);
            }
        }
        
        String strPath = GENERATED_DIR+File.separatorChar+stringOf(filename)+".java";
        String strContents = stringOf(contents);

        File outFile = new File(strPath);
        OutputStreamWriter fos = null;
        try {
            fos = new OutputStreamWriter(new FileOutputStream(outFile, false), "UTF-8");
            fos.write(strContents);
            return new BooleanValue(true);
        } catch (IOException e){
            Console.out.println(e.getMessage());
            return new BooleanValue(false);
        } finally {
            if (fos != null){
                try {
                    fos.close();
                } catch (IOException e){
                    
                }
            }
        }
    }

    public Value compileProgram(Value path){
        Value result;
        String strPath = stringOf(path)+".java";
        ProcessBuilder pb = new ProcessBuilder("javac", strPath).directory(new File(GENERATED_DIR));
        try {
            Process p = pb.start();
            int retCode = p.waitFor();
            closeProcess(p);
            result = new BooleanValue(true);
        } catch (IOException e){
            Console.err.println(e.getMessage());
            result = new BooleanValue(false);
        } catch (InterruptedException e){
            Console.err.println(e.getMessage());
            result = new BooleanValue(false);
        }
        
        return result;
    }

    public Value runProgram(Value path){
        Value result;
        String strPath = stringOf(path);
        ProcessBuilder pb = new ProcessBuilder("java", strPath).directory(new File(GENERATED_DIR));
        try {
            Process p = pb.start();
            int retCode = p.waitFor();
            closeProcess(p);
            result = new IntegerValue(retCode);
        } catch (IOException e){
            Console.err.println(e.getMessage());
            result = new BooleanValue(false);
        } catch (InterruptedException e){
            Console.err.println(e.getMessage());
            result = new BooleanValue(false);
        }
        return result;
    }


    // We need this because the toString of the Value converts special
    // characters back to their quoted form.
    private static String stringOf(Value val) {
        StringBuilder s = new StringBuilder();
        val = val.deref();

        if (val instanceof SeqValue) {
            SeqValue sv = (SeqValue) val;

            for (Value v : sv.values) {
                v = v.deref();

                if (v instanceof CharacterValue) {
                    CharacterValue cv = (CharacterValue) v;
                    s.append(cv.unicode);
                } else {
                    s.append("?");
                }
            }

            return s.toString();
        } else {
            return val.toString();
        }
    }

    private static void closeProcess(Process p){
        closeIgnoringException(p.getInputStream());
        closeIgnoringException(p.getOutputStream());
        closeIgnoringException(p.getErrorStream());
        p.destroy();
    }

    private static void closeIgnoringException(Closeable c){
        try {
            if (c != null){
                c.close();
            }
        } catch (IOException e){
            
        }
    }
}
