import java.util.Vector;

public class Tree {

	static int MAX_DEPTH = 2;
	Node head;
	static Vector<Node> lastGeneration;
	static Vector<Node> previousGeneration;

	public Tree(Board initialBoard, byte firstPlayer) {
		lastGeneration=new Vector<Node>();
		head = new Node(initialBoard, firstPlayer);
		lastGeneration.add(head);	
	}

	public Move chooseBestMove() {
		return head.possibleMoves.elementAt(find(negaScout(head, Integer.MAX_VALUE, Integer.MIN_VALUE)));
	}
	
	public int find(int negaNumber){
		boolean found=false;
		int i =-1;
		while(!found){
			i++;
			if(head.children[i].negaScoutNumber==negaNumber){found=true;}
		}
		
		return i;
	}
	
	public int negaScout(Node n, int alpha, int beta){
		if (n.children == null) {
			return n.negaScoutNumber;
		} else {
			//int best = Integer.MIN_VALUE;
			int best=beta;
			for (Node cn : n.children) {
				//int val = -negaScout(cn, -alpha, -beta);
				cn.negaScoutNumber=-negaScout(cn, -alpha, -beta);
				if (cn.negaScoutNumber > best) {
					best = cn.negaScoutNumber;
					if (best > alpha) {
						alpha = best;
						if (alpha >= beta)
							return best;
					}
				}
			}
			return best;
		}
	}
	
	public void changeHead(Move move){
		int index = head.possibleMoves.indexOf(move);
		head=head.children[index];
		lastGeneration=new Vector<Node>();
		findLastGeneration(lastGeneration);
	}

	public void findLastGeneration(Vector<Node> lastGen){
		for(Node n : head.children){
			if(n.children==null){lastGen.add(n);}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void continueGeneratingTree(){
		previousGeneration=(Vector<Node>) lastGeneration.clone();
		for(Node n : previousGeneration){
			lastGeneration.remove(n);
			n.createChildren();
		}
	}

}

class Node {
	
	public static final byte inverse = new Byte("3");

	Node parent;
	Board currentBoard;
	Node[] children;
	Move incomingMove;
	Vector<Move> possibleMoves;
	int numberOfPossibleMoves;
	int negaScoutNumber;
	int grade;
	int gradeModifier;
	byte player;
	int depth;

	public Node(Node parentNode, Move move, byte who) {
		parent = parentNode;
		player = who;
		possibleMoves = new Vector<Move>();
		gradeModifier=0;
		grade=parentNode.grade;
		negaScoutNumber=0;
		
		incomingMove=move;
		currentBoard = new Board(parent.currentBoard, move);
		
		if(move.IsJump()){gradeModifier+=move.Length()-1;}
		if(move.IsEOG()){gradeModifier+=100;}
		
		grade+=(Math.pow(-1,player+1))*gradeModifier;
		negaScoutNumber=grade;
		
		currentBoard.FindPossibleMoves(possibleMoves, player);
		depth = parent.depth + 1;

		//currentBoard.PrintNoColor();
		
	}

	public Node(Board initialBoard, byte firstplayer) {

		parent = null;
		currentBoard = initialBoard;
		possibleMoves = new Vector<Move>();
		depth=0;
		grade=0;
		player=firstplayer;
		
		currentBoard.FindPossibleMoves(possibleMoves, player);
		
		//currentBoard.PrintNoColor();
		
	}
	
	public void createChildren(){
		
			numberOfPossibleMoves = possibleMoves.size();
			children = new Node[numberOfPossibleMoves];
			
			
			for (int i = 0; i < children.length; i++) {
				children[i] = new Node(this, possibleMoves.elementAt(i), (byte)(player ^ inverse));
				Tree.lastGeneration.add(children[i]);
			}			
	
	}

}
