import java.util.Random;

public class MATH
{

	public static final double pi = 3.141592653589793;
	private static Random random = new Random();
	private static long seed = 0;
	
	public static double sin(double v)
	{
		return Math.sin(v);
	}

	public static double cos(double v)
	{
		return Math.cos(v);
	}

	public static double tan(double a)
	{
		return Math.tan(a);
	}

	public static double cot(double a)
	{
		return 1/Math.tan(a);
	}

	public static double asin(double a)
	{
		return Math.asin(a);
	}

	public static double acos(double a)
	{
		return Math.acos(a);
	}

	public static double atan(double v)
	{
		return Math.atan(v);
	}

	public static double acot(double a)
	{
		return atan(1 / a);
	}

	public static double sqrt(double a)
	{

		return Math.sqrt(a);
	}

	public static double pi_f()
	{
		return Math.PI;
	}

	public static void srand(long a)
	{
		MATH.srand2(a);
	}

	public static long rand(long a)
	{
		long lv = a;

		if (seed == -1)
		{
			return lv;
		}
		else if (lv == 0)
		{
			return 0;
		}
		else
		{
			return Math.abs(random.nextLong() % lv);
		}
	}

	public static long srand2(long a)
	{
		seed = a;
		random.setSeed(seed);
		return seed;
	}

	public static double exp(double a)
	{

		return Math.exp(a);
	}

	public static double ln(double a)
	{

		return Math.log(a);
	}

	public static double log(double a)
	{

		return Math.log10(a);
	}

	public static long fac(long a)
	{
		return (a < 1) ? 1 : a * fac(a-1);
	}

}