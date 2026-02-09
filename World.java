import java.util.HashMap;

class TimeLimitExceededException extends Exception{
	public TimeLimitExceededException(String message) {
		super(message);
	}
}

public class World {
	int timeTotal;
	int timePassed;
	Map map;
	
	
	private class Node{
		private String value;
		private Node[] next;
		
		public Node(String n) {
			this.value = n;
			this.next = new Node[4];
		}
		public boolean isInBound() {
			return this.value==null;
		}
		
		public Integer getVal() {
			return Integer.valueOf(value);
		}
		
		public Integer getMapVal() {
			if (value.length()<4) {
				return -1;
			}
			else {
				return Integer.valueOf(value);
			}
		}
		public Node getNext(int mode) {
			/*
			 * N,E,S,W
			 * 0,1,2,3
			 */
			return next[mode];
		}
		public void setNext(int mode, Node newNode) {
			next[mode]=newNode;
		}
		
	}
	private class Map{
		Node headCol;	// Goes Horizontal
		Node headRow;	// Goes Vertical
		public Map() {
			this.headRow = new Node("0");
			this.headCol = new Node("0");
		}
		
		private boolean isGreater(String s1, String s2) {
			return Integer.valueOf(s1)>Integer.valueOf(s2);
		}
		
		public Node traverseAxis(Node head, int val, int mode) {
			/*
			 * traverse the  Nodes for value val
			 * returns the node if found, 
			 * Insert node Val accordingly if it does not exists.
			 */

				// out of bound
			if (val>99) {
				return null;
			}
			
			// if exists, return the pointer
			Node pointer = head;
			while (pointer!=null) {
				if(pointer.getVal()==val) {
					return pointer;
				}
				// if not exists
				try {
					//middle insertion
					if(pointer.getNext(mode).getVal()>val) {
						Node newNode = new Node(String.valueOf(val));
						newNode.setNext(mode, pointer.getNext(mode));
						pointer.setNext(mode, newNode);
						return newNode;
					}
				} catch (Exception e) {
					// tail insertion
					Node newNode = new Node(String.valueOf(val));
					pointer.setNext(mode, newNode);
					return newNode;
				}
				pointer=pointer.getNext(mode);
			}
				return null;
		}
		
		public Node traverseMap(Node head, String val, int mode) {
			/*
			 * goes down or right into the map, if exists then return the node
			 * if not exists them return the node largest coordinate not larger than 
			 * target
			 * 
			 */
			Node pointer = head;
			// if already exists, return pointer
			while (pointer!=null) {
				if(pointer.getMapVal()==Integer.valueOf(val)) {
					return pointer;
				}
				// if not exists, return largest coordinate  smaller than target node
					if(pointer.getNext(mode)==null || pointer.getNext(mode).getMapVal()>Integer.valueOf(val)) {
						return pointer;
					}
				pointer=pointer.getNext(mode);
			}
				return null;

		}
		
		
		
		public Node generate(int row, int col) {
			/*
			 * go to the coordinate in bound
			 */
			Node colPointer = traverseAxis(headCol, col, 1);	// goes right
			Node rowPointer = traverseAxis(headRow, row, 2);	// goes down
			String coordinate = String.format("%02d", row)+String.format("%02d", col);
			
			// row and column both empty
			colPointer = traverseMap(colPointer, coordinate, 2);	//goes down into the map
			rowPointer = traverseMap(rowPointer, coordinate, 1);	// goes right into the map
			
			Node newNode = new Node(coordinate); 
			
			//insert new Node
			newNode.setNext(2, colPointer.getNext(2));
			newNode.setNext(1, rowPointer.getNext(1));
			colPointer.setNext(2, newNode);
			rowPointer.setNext(1, newNode);
			
			// set doubly references
			newNode.setNext(0, colPointer);
			newNode.setNext(3, rowPointer);
			return newNode;
		}
		
		public Node goTo(int row, int col) {
			
			Node colPointer = traverseAxis(headCol, col, 1);	// goes right
			String coordinate = String.format("%02d", row)+String.format("%02d", col);
			colPointer = traverseMap(colPointer, coordinate, 2);
			return colPointer;
		}
		private static final int[] DIRECTION = {-100, 1, 100, -1};
		public boolean hasNeighbor(Node node, int mode) {
			/*
			 * returns true if neighbor already exists
			 * returns false if neighbor does not exists
			 */
			
			if (node.getNext(mode)==null) {
				return false;
			}
			
			int val = node.getMapVal();
		    int row = val / 100;
		    int col = val % 100;
		    
		    //out of bound check
		    if (mode == 0 && row == 0) {
		    	return true;     // up
		    }
		    if (mode == 1 && col == 99) {
		    	return true;    // right
		    }
		    if (mode == 2 && row == 99) {
		    	return true;    // down
		    }
		    if (mode == 3 && col == 0) {
		    	return true;     // left
		    }
		    
			return (node.getNext(mode).getMapVal()==node.getVal()+DIRECTION[mode]);
			}
			
		
		public boolean isSearchComplete(Node node) {
			/*
			 * returns true if all in-bound neighbors have been searched
			 * returns false otherwise
			 */
			int neighborCount=4;
			int coordinate = node.getMapVal();
			int row = coordinate/100;
			int col = coordinate/100;
			
			if (row==99) {	// bottom bound
				neighborCount-=1;
			}
			if (col==99){
				neighborCount-=1;
			}
			
			for (int i=0; i<4; i++) {
				if (node.getNext(i)!=null) {
					neighborCount-=1;
				}
			}
			return neighborCount==0;
			
		}
		
		public Node generateNeighbor(Node node,int mode) {
			
			//if already exists, returns null
			if (hasNeighbor(node,mode)) {
				return null;}
			
			
			else {
			int neighborCoord= node.getMapVal() +DIRECTION[mode];
			
			int neighborRow = neighborCoord/100;
			int neighborCol = neighborCoord%100;
			if (neighborRow<100 && neighborCol<100) {	// if in bound
			Node newNeighbor = generate(neighborRow, neighborCol);
			node.setNext(mode, newNeighbor);
			return newNeighbor;
			}
			else { return null;}						// if out of bound, return null
			}
		}
		public String stringify(String mode) {
			StringBuilder ret=new StringBuilder();
			Node axisPointer;
			int axisDirection;
			int mapDirection;
			if(mode.equals("row"))
			{
				axisPointer=headRow;
				axisDirection=2;
				mapDirection=1;
			}
			else	
			{
				axisPointer=headCol;
				axisDirection=1;
				mapDirection=2;
			}
			while (axisPointer!=null) {
				Node mapPointer=axisPointer.getNext(mapDirection);
				while (mapPointer!=null) {
					String col = String.valueOf(mapPointer.getMapVal()/100);
					String row = String.valueOf(mapPointer.getMapVal()%100);
					if(!(col.equals("50")&&row.equals("50"))){
					ret.append(col).append(" ").append(row).append("\n\n");
					}
					mapPointer=mapPointer.getNext(mapDirection);
				}
				axisPointer=axisPointer.getNext(axisDirection);
			}
			
			return ret.toString();
		}
		}
		
	public World() {
		this.map = new Map();
		this.timePassed=0;
		this.timeTotal=0;
		map.generate(50, 50);
	}
	
	private void incrementTime(int n) throws TimeLimitExceededException{
		if (timePassed+n >16) {
			throw new TimeLimitExceededException("TLE");
		}
		else {
			timePassed+=n;
			timeTotal+=n;
				
		}
	}

	
	public boolean searchNeighbor(int row, int col) {
		/*
		 * Clever Code Is Bad Code
		 * 
		 * returns true if finished searching
		 * return false if return early 
		 */
		Node RS= map.goTo(row,col);
		boolean returnToRSFlag=false;
		for (int i=0; i<4; i++) {
			
		try {
				// go to adjacent square and search
			if (map.hasNeighbor(RS, i)) {
				continue;
			}else {
				if(returnToRSFlag==true) {
					if(timePassed<=12) {
					incrementTime(1);
					}
					else {
						return false;
					}
				}
				
				incrementTime(3);
				map.generateNeighbor(RS, i);
				returnToRSFlag=true;
				
			}
		} catch (TimeLimitExceededException e) {
			timePassed=0;
			return false;
		} 
		}
		return true;
	
	}
	public void searchRS(int row, int col) {
		int expectedCoord = row*100+col;
		Node RS=map.goTo(row,col);
		if (RS==null) {
			try {
				incrementTime(2);
				map.generate(row, col);
			} catch (TimeLimitExceededException e) {
				
				return;
		}
		}
			else if (RS.getMapVal()!=expectedCoord) {
			try {
				incrementTime(2);
				map.generate(row, col);
			} catch (TimeLimitExceededException e) {
				// TODO Auto-generated catch block
				return;
			}
		}
	}
	
	public void searchArea(int row, int col) {
		if(timePassed<=14) {
		searchRS(row,col);
		boolean timeResetFlag=searchNeighbor(row,col);
		if (timeResetFlag==false) { // true if finished searching all neighbor, false if stopped short
			timePassed=0;
			timeTotal+=8;
		}
		}
		
		else {
			timePassed=0;
			timeTotal+=8;
			
		}
	}
	
	
	public String stringify(String mode) {
		String msg= map.stringify(mode);
		int len=msg.length();
		if (len!=0) {
			msg=msg.substring(0,len-1);
		}
		return msg+"\n"+timeTotal;
	}
	
	
}




