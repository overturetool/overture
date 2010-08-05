import java.io.*;
import org.antlr.runtime.*;
import org.antlr.runtime.debug.DebugEventSocketProxy;


public class __Test__ {

    public static void main(String args[]) throws Exception {
        vdmppLexer lex = new vdmppLexer(new ANTLRFileStream("/Users/ari/Documents/antlr/input", "UTF8"));
        CommonTokenStream tokens = new CommonTokenStream(lex);

        vdmppParser g = new vdmppParser(tokens, 49100, null);
        try {
            g.start();
        } catch (RecognitionException e) {
            e.printStackTrace();
        }
    }
}