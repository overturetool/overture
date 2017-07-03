import org.junit.Assert;
import org.junit.Test;
import org.overture.codegen.runtime.Tuple;
import org.overture.codegen.runtime.Utils;

public class TupleTest {

    @Test
    public void eq()
    {
        Tuple t1 = Tuple.mk_(true,false);
        Tuple t2 = Tuple.mk_(true,false);

        Assert.assertTrue("Expected tuples to be equal", Utils.equals(t1, t2));
    }

    @Test
    public void notEq()
    {
        Tuple t1 = Tuple.mk_(false,true);
        Tuple t2 = Tuple.mk_(true,false);

        Assert.assertFalse("Expected tuples to be different", Utils.equals(t1, t2));
    }

    @Test
    public void equalDifferentNumericTypes()
    {
        Tuple t1 = Tuple.mk_(1L,1L);
        Tuple t2 = Tuple.mk_(1.0,1.0);

        Assert.assertTrue("Expected tuples to be equal", Utils.equals(t1, t2));
    }
}
