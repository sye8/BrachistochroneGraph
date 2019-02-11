/**
 * 
 */

/**
 * @author yesifan
 *
 */
public class Vertex implements Comparable<Vertex>{
	
	public int x;
	public int y;
	public double dist;
	public boolean known;
	
	public Vertex(int x, int y){
		this.x = x;
		this.y = y;
		this.dist = Double.POSITIVE_INFINITY;
		this.known = false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Vertex o) {
		if((this.dist - o.dist) < 0){
			return -1;
		}else if((this.dist - o.dist) > 0){
			return 1;
		}else{
			return 0;
		}
	}
	
	public String toString(){
		return "(" + x + "," + y + ")";
	}

}
