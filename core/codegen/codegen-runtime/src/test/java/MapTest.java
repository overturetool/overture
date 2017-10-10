import org.junit.Assert;
import org.junit.Test;
import org.overture.codegen.runtime.MapUtil;
import org.overture.codegen.runtime.Maplet;
import org.overture.codegen.runtime.VDMMap;

public class MapTest {

    @Test(expected = IllegalArgumentException.class)
    public void mapCompositionInvalidInput()
    {
        VDMMap left = MapUtil.map(new Maplet(1L, 2L));
        VDMMap right = MapUtil.map(new Maplet(1L, 2L));

        MapUtil.comp(left, right);
    }

    @Test
    public void mapCompositionSimpleInput()
    {
        VDMMap left = MapUtil.map(new Maplet(2L, 3L));
        VDMMap right = MapUtil.map(new Maplet(1L, 2L));

        VDMMap actualRes = MapUtil.comp(left, right);
        VDMMap expectedRes = MapUtil.map(new Maplet(1L, 3L));

        Assert.assertEquals("Got unexpected map composition result", expectedRes, actualRes);
    }

    @Test
    public void mapCompositionNats()
    {
        VDMMap left = MapUtil.map(new Maplet(2L, 20L), new Maplet(4L, 40L), new Maplet(6L, 60L));
        VDMMap right = MapUtil.map(new Maplet(1L, 2L), new Maplet(3L, 4L), new Maplet(5L, 6L));

        VDMMap actualRes = MapUtil.comp(left, right);
        VDMMap expectedRes = MapUtil.map(new Maplet(1L, 20L), new Maplet(3L, 40L), new Maplet(5L, 60L));

        Assert.assertEquals("Got unexpected map composition result", expectedRes, actualRes);

    }
}
