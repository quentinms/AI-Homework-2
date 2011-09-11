import java.util.Vector;

public class Tree {

	Node head;
	int depth;
	static Vector<Node> lastGeneration;
	static Vector<Node> previousGeneration;

	public Tree(Board initialBoard, byte firstPlayer) {
		lastGeneration = new Vector<Node>();
		head = new Node(initialBoard, firstPlayer);
		lastGeneration.add(head);
		depth=0;
	}

	public Move chooseBestMove() {
		int gr=miniMax(head);
		System.out.println("Chosen move is: "+gr);
		return head.possibleMoves.elementAt(find(gr));
	}

	public int find(int negaNumber) {
		boolean found = false;
		int i = -1;
		while (!found && i < head.children.length - 1) {
			i++;
			if (head.children[i].negaScoutNumber == negaNumber) {
				found = true;
			}
		}

		return i;
	}

	public int negaScout(Node n, int alpha, int beta, int depth) {
		if (alpha > beta)
			System.out.println("zfh");
		if (n.children == null || depth == 0) {
			return n.negaScoutNumber;
		}
		int b = beta;
		for (Node cn : n.children) {
			cn.negaScoutNumber = -negaScout(cn, -b, -alpha, depth - 1);
			if (cn.negaScoutNumber > alpha && cn.negaScoutNumber < beta) {
				negaScout(cn, -beta, -alpha, depth - 1);
			}
			alpha = Math.max(alpha, cn.negaScoutNumber);
			if (alpha >= beta)
				return alpha;
			b = alpha + 1;
			return alpha;
		}
		return alpha;

	}

	public int alphaBetaPrunning(Node n, int alpha, int beta) {

		int val;
		if (alpha > beta)
			System.out.println("blublu");
		if (n.children == null)
			return n.negaScoutNumber;
		else {
			if (n.player == Board.CELL_OWN) {

				val = Integer.MAX_VALUE;
				for (Node cn : n.children) {
					val = Math.min(val, alphaBetaPrunning(cn, alpha, beta));
					if (alpha >= val) {
						return val;
					}
					beta = Math.min(beta, val);
				}
			} else {
				val = Integer.MIN_VALUE;
				for (Node cn : n.children) {
					val = Math.max(val, alphaBetaPrunning(cn, alpha, beta));
					if (val >= beta) {
						cn.negaScoutNumber = val;
						return val;
					}
					alpha = Math.max(alpha, val);
				}

			}
			n.negaScoutNumber = val;
			return val;
		}

	}

	public int miniMax(Node n) {

		/*
		 * if(max's turn) for each child score = alpha-beta(other
		 * player,child,alpha,beta) if score > alpha then alpha = score (we have
		 * found a better best move) if alpha >= beta then return alpha (cut
		 * off) return alpha (this is our best move) else (min's turn) for each
		 * child score = alpha-beta(other player,child,alpha,beta) if score <
		 * beta then beta = score (opponent has found a better worse move) if
		 * alpha >= beta then return beta (cut off
		 */

		if (n.children == null)
			return n.gradeNode();
		else if (n.player == Board.CELL_OWN) {
			int val = Integer.MIN_VALUE;
			for (Node child : n.children) {
				val = Math.max(val, miniMax(child));
			}
			n.negaScoutNumber = val;
			return val;
		} else {
			int val = Integer.MAX_VALUE;
			for (Node child : n.children) {
				val = Math.min(val, miniMax(child));
			}
			n.negaScoutNumber = val;
			return val;
		}

	}

	public Move findMove(Move move) {
		for (Move m : head.possibleMoves) {
			if (m.ToString().equals(move.ToString())) {
				return m;

			}

		}
		return null;
	}

	public void changeHead(Move move) {
		int index = head.possibleMoves.indexOf(move);
		head = head.children[index];
		lastGeneration = new Vector<Node>();
		findLastGeneration(lastGeneration,head);
		depth--;
		System.out.println("Changed head: "+lastGeneration.size());
	}

	public void findLastGeneration(Vector<Node> lastGen,Node node) {
		for (Node n : node.children) {
			if (n.children == null) {
				lastGen.add(n);
			}else{findLastGeneration(lastGen,n);}
		}
	}

	@SuppressWarnings("unchecked")
	public void continueGeneratingTree() {
		previousGeneration = (Vector<Node>) lastGeneration.clone();
		for (Node n : previousGeneration) {
			lastGeneration.remove(n);
			n.createChildren();
		}
		depth++;
		System.out.println("Current depth:"+depth+"|Current children:"+lastGeneration.size());
	}

}

class Node {

	public static final byte inverse = new Byte("3");

	Board currentBoard;
	Node[] children;
	Vector<Move> possibleMoves;
	int negaScoutNumber;
	int grade;
	byte player;

	public Node(Node parentNode, Move move, byte who) {
		player = who;
		possibleMoves = new Vector<Move>();
		grade = Integer.MIN_VALUE;
		negaScoutNumber = 0;

		currentBoard = new Board(parentNode.currentBoard, move);

		negaScoutNumber = grade;

		currentBoard.FindPossibleMoves(possibleMoves, player);

		// currentBoard.PrintNoColor();

	}

	public Node(Board initialBoard, byte firstplayer) {

		currentBoard = initialBoard;
		possibleMoves = new Vector<Move>();
		grade = Integer.MIN_VALUE;
		player = firstplayer;

		currentBoard.FindPossibleMoves(possibleMoves, player);

		// currentBoard.PrintNoColor();

	}

	public void createChildren() {

		children = new Node[possibleMoves.size()];

		for (int i = 0; i < children.length; i++) {
			children[i] = new Node(this, possibleMoves.elementAt(i),
					(byte) (player ^ inverse));
			Tree.lastGeneration.add(children[i]);
		}

	}

	public int gradeNode() {
		if (grade != Integer.MIN_VALUE) {
			return grade;
		} else {
			int gr=0;
			for(int r=7;r>=0;r--)
	        {
	            for(int c=7;c>=0;c--)
	            {
	               if((currentBoard.At(r,c)&Board.CELL_OWN)!=0)
	                {
	                    if((currentBoard.At(r,c)&Board.CELL_KING)!=0)
	                        gr+=50;
	                    else
	                        gr+=10;
	                }
	                else if((currentBoard.At(r,c)&Board.CELL_OTHER)!=0)
	                {
	                    if((currentBoard.At(r,c)&Board.CELL_KING)!=0)
	                        gr-=50;
	                    else
	                        gr-=10;
	                }
	            }
	        }
			//System.out.println("Grade is "+gr);
			grade=gr;
			return gr;
		}

	}

}
