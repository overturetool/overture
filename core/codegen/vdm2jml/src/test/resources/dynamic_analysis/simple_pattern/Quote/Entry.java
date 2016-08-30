package project;

import java.util.*;
import org.overture.codegen.runtime.*;
import org.overture.codegen.vdm2jml.runtime.*;

@SuppressWarnings("all")
//@ nullable_by_default

final public class Entry {
  /*@ public ghost static boolean invChecksOn = true; @*/

  private Entry() {}

  public static Object Run() {

    {
      final project.quotes.AQuote ignorePattern_1 = f();
      //@ assert Utils.is_(ignorePattern_1,project.quotes.AQuote.class);

      /* skip */
    }

    IO.println("Done! Expected no violations");
    return 0L;
  }
  /*@ pure @*/

  public static project.quotes.AQuote f() {

    final project.quotes.AQuote quotePattern_1 = project.quotes.AQuote.getInstance();
    //@ assert Utils.is_(quotePattern_1,project.quotes.AQuote.class);

    Boolean success_1 = Utils.equals(quotePattern_1, project.quotes.AQuote.getInstance());
    //@ assert Utils.is_bool(success_1);

    if (!(success_1)) {
      throw new RuntimeException("Quote pattern match failed");
    }

    project.quotes.AQuote ret_1 = project.quotes.AQuote.getInstance();
    //@ assert Utils.is_(ret_1,project.quotes.AQuote.class);

    return ret_1;
  }

  public String toString() {

    return "Entry{}";
  }
}
