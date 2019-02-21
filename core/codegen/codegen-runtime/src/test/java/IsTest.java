import org.junit.Assert;
import org.junit.Test;
import org.overture.codegen.runtime.Utils;

public class IsTest {

    @Test
    public void isStr()
    {
        Assert.assertTrue(Utils.is_("hej", Utils.STRING));
    }

    @Test
    public void isNotStr()
    {
        Assert.assertFalse(Utils.is_(null, Utils.STRING));
    }
}
