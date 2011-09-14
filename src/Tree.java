//import java.util.Date;
//import java.util.Vector;
//
//public class Tree {
//
//	Node head;
//	int depth;
//	static Vector<Node> lastGeneration;
//
//	public Tree(Board initialBoard, byte firstPlayer) {
//		lastGeneration = new Vector<Node>();
//		head = new Node(initialBoard, firstPlayer);
//		lastGeneration.add(head);
//		depth = 0;
//	}
//
//	public Move chooseBestMove() {
//		//int gr = alphaBetaPrunning(head, Integer.MIN_VALUE, Integer.MAX_VALUE);
//		int gr=miniMax(head);
//		System.out.println("Chosen move is: " + gr);
//		return head.possibleMoves.elementAt(find(gr));
//	}
//
//	public int find(int negaNumber) {
//		boolean found = false;
//		int i = -1;
//		while (!found && i < head.children.length - 1) {
//			i++;
//			if (head.children[i].algoNumber == negaNumber) {
//				found = true;
//			}
//		}
//
//		return i;
//	}
//
//	public int alphaBetaPrunning(Node n, int alpha, int beta) {
//
//		int val = 0;
//		if (n.children == null)
//			return n.gradeNode();
//		else {
//			if (n.player == Board.CELL_OWN) {
//				val = Integer.MIN_VALUE;
//				for (Node cn : n.children) {
//					val = Math.max(alphaBetaPrunning(cn, alpha, beta), val);
//					if (val > alpha) {
//						alpha = val;
//					}
//					if (val >= beta) {
//						cn.algoNumber = val;
//						return val;
//					}
//					alpha = Math.max(alpha, val);
//				}
//			} else {
//				val = Integer.MAX_VALUE;
//				for (Node cn : n.children) {
//					val = Math.min(val, alphaBetaPrunning(cn, alpha, beta));
//					if (alpha >= val) {
//						cn.algoNumber = val;
//						return val;
//					}
//					beta = Math.min(val, beta);
//				}
//			}
//			n.algoNumber = val;
//			return val;
//		}
//
//	}
//	
//	public int miniMax(Node n) {
//
//		if (n.children == null)
//			return n.gradeNode();
//		else if (n.player == Board.CELL_OWN) {
//			int val = Integer.MIN_VALUE;
//			for (Node child : n.children) {
//				val = Math.max(val, miniMax(child));
//			}
//			n.algoNumber = val;
//			return val;
//		} else {
//			int val = Integer.MAX_VALUE;
//			for (Node child : n.children) {
//				val = Math.min(val, miniMax(child));
//			}
//			n.algoNumber = val;
//			return val;
//		}
//
//	}
//
//	public Move findMove(Move move) {
//		for (Move m : head.possibleMoves) {
//			if (m.ToString().equals(move.ToString())) {
//				return m;
//
//			}
//
//		}
//		return null;
//	}
//
//	public void changeHead(Move move) {
//		int index = head.possibleMoves.indexOf(move);
//		head = head.children[index];
//		lastGeneration = new Vector<Node>();
//		findLastGeneration(lastGeneration, head);
//		depth--;
//		// TODO
//		System.gc();
//	}
//
//	public void findLastGeneration(Vector<Node> lastGen, Node node) {
//		for (Node n : node.children) {
//			if (n.children == null) {
//				lastGen.add(n);
//			} else {
//				findLastGeneration(lastGen, n);
//			}
//		}
//	}
//
//	@SuppressWarnings("unchecked")
//	public void continueGeneratingTree() {
//		Vector<Node> previousGeneration = (Vector<Node>) lastGeneration.clone();
//		for (Node n : previousGeneration) {
//			lastGeneration.remove(n);
//			n.createChildren();
//		}
//		depth++;
//	}
//	
//	
//	public int gradeBoard(Board b) {
//			int gr = 0;
//			for (int r = 7; r >= 0; r--) {
//				for (int c = 7; c >= 0; c--) {
//					if ((b.At(r, c) & Board.CELL_OWN) != 0) {
//						if ((b.At(r, c) & Board.CELL_KING) != 0)
//							gr += KING_POINTS;
//						else
//							gr += PEON_POINTS;
//					} else if ((b.At(r, c) & Board.CELL_OTHER) != 0) {
//						if ((b.At(r, c) & Board.CELL_KING) != 0)
//							gr -= KING_POINTS;
//						else
//							gr -= PEON_POINTS;
//					}
//				}
//			}
//			return gr;
//
//	}
//	public static final byte inverse = new Byte("3");
//	private static final int JUMP_POINTS=50;
//	private static final int KING_POINTS=25;
//	private static final int PEON_POINTS=100;
//
//}
//
//class Node2{
//	
//	private static final int JUMP_POINTS=50;
//	private static final int KING_POINTS=25;
//	private static final int PEON_POINTS=100;
//
//	public static final byte inverse = new Byte("3");
//
//	Board currentBoard;
//	Node[] children;
//	Vector<Move> possibleMoves;
//	int algoNumber;
//	int grade;
//	int grMod;
//	byte player;
//
//	
//	
//	public Node(Node parentNode, Move move, byte who) {
//		player = who;
//		possibleMoves = new Vector<Move>();
//		grade = Integer.MIN_VALUE;
//		algoNumber = 0;
//		grMod = 0;
//		currentBoard = new Board(parentNode.currentBoard, move);
//
//		if (move.IsJump()) {
//			grMod += JUMP_POINTS * Math.pow(-1, player) * (move.Length() - 1);
//		}
//
//		
//		
//		currentBoard.FindPossibleMoves(possibleMoves, player);
//		createChildren();
//		// currentBoard.PrintNoColor();
//
//	}
//
//	public Node(Board initialBoard, byte firstplayer) {
//
//		currentBoard = initialBoard;
//		possibleMoves = new Vector<Move>();
//		grade = Integer.MIN_VALUE;
//		player = firstplayer;
//
//		currentBoard.FindPossibleMoves(possibleMoves, player);
//
//		// currentBoard.PrintNoColor();
//
//	}
//
//	public void createChildren() {
//
//		children = new Node[possibleMoves.size()];
//
//		for (int i = 0; i < children.length; i++) {
//			children[i] = new Node(this, possibleMoves.elementAt(i),
//					(byte) (player ^ inverse));
//			Tree.lastGeneration.add(children[i]);
//		}
//
//	}
//
//	public int gradeNode() {
//		if (grade != Integer.MIN_VALUE) {
//			return grade;
//		} else {
//			int gr = 0;
//			for (int r = 7; r >= 0; r--) {
//				for (int c = 7; c >= 0; c--) {
//					if ((currentBoard.At(r, c) & Board.CELL_OWN) != 0) {
//						if ((currentBoard.At(r, c) & Board.CELL_KING) != 0)
//							gr += KING_POINTS;
//						else
//							gr += PEON_POINTS;
//					} else if ((currentBoard.At(r, c) & Board.CELL_OTHER) != 0) {
//						if ((currentBoard.At(r, c) & Board.CELL_KING) != 0)
//							gr -= KING_POINTS;
//						else
//							gr -= PEON_POINTS;
//					}
//				}
//			}
//			gr += grMod;
//			grade = gr;
//			return gr;
//		}
//
//	}
//}
