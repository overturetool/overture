
public class Maplet
{
	private Object left;
	private Object right;
	
	public Maplet(Object left, Object right)
	{
		super();
		this.left = left;
		this.right = right;
	}

	public Object getLeft()
	{
		return left;
	}

	public Object getRight()
	{
		return right;
	}
	
	@Override
	public String toString()
	{
		return "{" + left != null ? left.toString() : null + " |-> " + right != null ? right.toString() : null + "}";
	}
}
