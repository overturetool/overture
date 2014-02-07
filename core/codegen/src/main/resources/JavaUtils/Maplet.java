
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
	public int hashCode()
	{
		int hash = 0;
		
		if(left != null)
			hash += left.hashCode();
		
		if(right != null)
			hash += right.hashCode();
		
		return hash;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj == null)
			return false;
		
		if(this == obj)
			return true;
		
		if(!(obj instanceof Maplet))
			return false;
		
		final Maplet other = (Maplet) obj;
		
		if ((this.left == null && other.left != null)
				|| (this.left != null && !this.left.equals(other.left)))
		{
			return false;
		}
		
		if ((this.right == null && other.right != null)
				|| (this.right != null && !this.right.equals(other.right)))
		{
			return false;
		}
		
		return true;
	}
	
	@Override
	public String toString()
	{
		return "{" + left != null ? left.toString() : null + " |-> " + right != null ? right.toString() : null + "}";
	}
}
