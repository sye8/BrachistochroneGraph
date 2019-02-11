import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import javax.imageio.ImageIO;


/**
 * @author yesifan
 *
 */
public class Graph {

	public int n; // Dividing in n parts
	public int s; // Furtherest neighbor to search, default to be 1
	
	private Vertex [][] vertices;   // Storing all vertices
	private Vertex [][] path;       // Store the shortest path found using Dijkstra's Algorithm
	
	private double g;
	
	/**
	 * The default constructor. n = 100, s = 1
	 */
	public Graph(){
		this.n = 100; // Default to 100		
		this.vertices = new Vertex[101][101];
		for(int i = 0; i < 101; i++){
			for(int j = 0; j < 101; j++){
				vertices[i][j] = new Vertex(i,j);
			}
		}
		this.path = new Vertex[101][101];		
		this.g = 9.81 * 100;
		this.s = 1;
	}
	
	/**
	 * Constructor taking n divisions
	 * 
	 * @param size The size of the graph, representing n divisions
	 */
	public Graph(int size){
		this.n = size;
		this.vertices = new Vertex[n+1][n+1];
		for(int i = 0; i < n+1; i++){
			for(int j = 0; j < n+1; j++){
				vertices[i][j] = new Vertex(i,j);
			}
		}
		this.path = new Vertex[n+1][n+1];
		this.g = 9.81 * this.n;
		this.s = 1;
	}
	
	/**
	 * Constructor taking n divisions and s furtherest connecting neighbor
	 * 
	 * @param size The size of the graph, representing n divisions
	 * @param slope The biggest slope, representing the futherest connecting neighbor
	 */
	public Graph(int size, int slope){
		this.n = size;
		this.vertices = new Vertex[n+1][n+1];
		for(int i = 0; i < n+1; i++){
			for(int j = 0; j < n+1; j++){
				vertices[i][j] = new Vertex(i,j);
			}
		}
		this.path = new Vertex[n+1][n+1];
		this.g = 9.81 * this.n;
		this.s = slope;
	}
	
	/**
	 * Given a vertex, return all the other vertices that are reachable from it
	 * 
	 * @param v Vertex
	 * @return A List of Vertex
	 */
	public Set<Vertex> getNeighbors(Vertex v){
		
		Set<Vertex> neighbors = new HashSet<Vertex>();
		
		for(int i = 1; i <= s; i++){
			for(int j = 1; j <= s; j++){
				if(v.x + i <= n){
					neighbors.add(vertices[v.x+i][v.y]);
					if(v.y + j <= n){
						neighbors.add(vertices[v.x+i][v.y+j]);
						neighbors.add(vertices[v.x][v.y+j]);
					}
					if(v.y - j >= 0){
						neighbors.add(vertices[v.x+i][v.y-j]);
						neighbors.add(vertices[v.x][v.y-j]);
					}
				}
				if(v.x - i >= 0){
					neighbors.add(vertices[v.x-i][v.y]);
					if(v.y + j <= n){
						neighbors.add(vertices[v.x-i][v.y+j]);
					}
					if(v.y - j >= 0){
						neighbors.add(vertices[v.x-i][v.y-j]);
					}
				}
			}
		}
		
		return neighbors;	
	}
	
	/**
	 * Given vertex v and w, calculate the time taken for an object to slide from v to w.
	 * Assume frictionless, m = 1kg.
	 * t = 2d/(vv+vw)
	 * 
	 * @param v Starting Vertex
	 * @param w Ending Vertex
	 * @return The time taken to slide from v to w
	 */
	public double cost(Vertex v, Vertex w){
		double vv = vAtVertex(v);
		double vw = vAtVertex(w);
		double dX = w.x - v.x;
		double dY = w.y - v.y;
		double d = Math.sqrt(dX*dX + dY*dY);
		
		return 2*d/(vv+vw);
	}
	
	/**
	 * Given vertex v and a pair of coordinates wX, wY, calculate the time taken for an object to slide from v to w.
	 * Assume frictionless, m = 1kg.
	 * t = 2d/vv+vw
	 * 
	 * @param v Starting Vertex
	 * @param wX Ending point x axis
	 * @param wY Ending point y axis
	 * @return
	 */
	public double cost(Vertex v, int wX, int wY){
		double vv = vAtVertex(v);
		double vw = vAtVertex(wX, wY);
		double dX = wX - v.x;
		double dY = wY - v.y;
		double d = Math.sqrt(dX*dX + dY*dY);
		
		return 2*d/(vv+vw);
	}
	
	/**
	 * Given a vertex v, using conservation of energy, calculate the velocity at that vertex
	 * Assume frictionless, m = 1kg.
	 * 0.5mv^2 = mgy
	 * 
	 * @param v Vertex
	 * @return Velocity at Vertex v
	 */
	public double vAtVertex(Vertex v){
		return Math.sqrt(2 * g * v.y);
	}
	
	/**
	 * Given a coordinate (x,y), using conservation of energy, calculate the velocity at that vertex
	 * Assume frictionless, m = 1kg.
	 * 0.5mv^2 = mgy
	 * 
	 * @param x x coordinate
	 * @param y y coordinate 
	 * @return Velocity at (x,y)
	 */
	public double vAtVertex(int x, int y){
		return Math.sqrt(2 * g * y);
	}
	
	/**
	 * Use Dijkstra's Algorithm to search from (0,1) to (1,0) for shortest path
	 * 
	 * @return The vertex at (1,0)
	 */
	public void dijkstra(){
		
		// Initialize with start vertex
		vertices[0][n].dist = 0; // Set distance at start vertex to 0
		PriorityQueue<Vertex> queue = new PriorityQueue<Vertex>();
		queue.add(vertices[0][n]);
		
		while(!queue.isEmpty()){
			Vertex v = queue.poll();
			v.known = true;
			Set<Vertex> neighbors = getNeighbors(v);
			for(Vertex w: neighbors){
				if(!w.known){
					double cvw = cost(v,w);
					if(v.dist + cvw < w.dist){
						w.dist = v.dist + cvw;
						path[w.x][w.y] = v;
						queue.add(w);
					}
				}
			}
		}
	}
	
	/**
	 * Calculate the total time taken to slide from (0,1) to (1,0)
	 * This is to be run after calling Dijkstra
	 * 
	 * @return Total time taken to slide from (0,1) to (1,0). If Dijkstra has not been run, return NaN
	 */
	public double totalPathTime(){
		if(path[n][0] != null){
			double totalTime = 0;
			int x = n; int y = 0;
			while(path[x][y] != null){
				totalTime += cost(path[x][y], x, y);
				Vertex v = path[x][y];
				x = v.x;
				y = v.y;			
			}
			return totalTime;
		}
		return Double.NaN;
	}
	
	/**
	 * Paint the shortest path found
	 * Must be run after Dijkstra
	 * 
	 * @param g2d The Graphics to draw with
	 */
	public void paintPath(Graphics2D g2d){
		if(path[n][0] != null){
			int x = n; int y = 0;
			while(path[x][y] != null){	
				Vertex v = path[x][y];
				g2d.drawLine(1000 - (1000/n)*x, (1000/n)*y, 1000 - (1000/n)*v.x, (1000/n)*v.y);
				x = v.x;
				y = v.y;			
			}
		}
	}
	
	/**
	 * Paint the actual optimal path
	 * 
	 * @param g The Graphics to draw with
	 */
	public static void paintCycloid(Graphics g){
		
	}
	
	/**
	 * Run the Brachistochrome Problem at n
	 * 
	 * @param n Subdivisions
	 * @param s Furtherest neighbor
	 */
	public static Graph run(int n, int s){
		System.out.println("n = " + n);
		System.out.println("s = " + s);
		Graph g = new Graph(n,s);
		double startTime = System.currentTimeMillis();
		g.dijkstra();
		double endTime = System.currentTimeMillis();
		System.out.println("Search Time: " + (endTime - startTime)/1000 + "s.");
		System.out.println("Time to slide from (0,1) to (1,0): " + g.totalPathTime());
		System.out.println();
		return g;
	}
	
	/**
	 * Paint the results to a PNG image
	 * 
	 * @param n A list of n (divisions) to draw
	 * @param s A list of s (slopes) to draw
	 * @throws IOException 
	 */
	public static void paint(int[] n, int[] s) throws IOException{
		BufferedImage img = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = (Graphics2D)img.getGraphics();
		
		// Draw Background
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, 1000, 1000);
		
		// Draw diagonal
		g2d.setStroke(new BasicStroke(2));
		g2d.setColor(Color.BLACK);
		g2d.drawLine(0, 0, 1000, 1000);
		
		// Draw actual Brachistochrone optimal solution
		g2d.setColor(Color.GREEN);
		paintCycloid(g2d);
		
		// Draw the path we found for each n
		g2d.setColor(Color.BLUE);
		Color[] colors = {Color.BLUE, Color.CYAN, Color.MAGENTA, Color.ORANGE, Color.PINK};
		for(int i: n){
			for(int j : s){
				g2d.setColor(colors[j%5]);
				Graph toDraw = run(i,j);
				toDraw.paintPath(g2d);
			}
		}
		
		// Save Image
		g2d.dispose();
		ImageIO.write(img, "png", new File("Brachistochone.png"));
		System.out.println("Image Saved As: Brachistochone.png\n");
	}
	
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		int[] divisions = {500};
		int[] slopes = {1,2,3,4,5};
		paint(divisions, slopes);
	}

}
