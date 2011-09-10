import java.util.Vector;

public class Tree {

	Node head;
	static Vector<Node> lastGeneration;
	static Vector<Node> previousGeneration;

	public Tree(Board initialBoard, byte firstPlayer) {
		lastGeneration = new Vector<Node>();
		head = new Node(initialBoard, firstPlayer);
		lastGeneration.add(head);
	}

	public Move chooseBestMove() {
		return head.possibleMoves.elementAt(find(alphaBetaPrunning(head,
				Integer.MIN_VALUE, Integer.MAX_VALUE)));
	}

	public int find(int negaNumber) {
		boolean found = false;
		int i = -1;
		while (!found) {
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
				val = -Integer.MAX_VALUE;
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
		/*
		 * fonction ALPHABETA(P, alpha, beta) si P est une feuille alors
		 * retourner la valeur de P sinon si P est un nœud Min alors Val =
		 * infini pour tout fils Pi de P faire Val = Min(Val, ALPHABETA(Pi,
		 * alpha, beta)) si alpha ≥ Val alors retourner Val beta = Min(beta,
		 * Val) finpour sinon Val = -infini pour tout fils Pi de P faire Val =
		 * Max(Val, ALPHABETA(Pi, alpha, beta)) si Val ≥ beta alors retourner
		 * Val alpha = Max(alpha, Val) finpour finsi retourner Val finsi fin
		 */

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
		findLastGeneration(lastGeneration);
	}

	public void findLastGeneration(Vector<Node> lastGen) {
		for (Node n : head.children) {
			if (n.children == null) {
				lastGen.add(n);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void continueGeneratingTree() {
		previousGeneration = (Vector<Node>) lastGeneration.clone();
		for (Node n : previousGeneration) {
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
		gradeModifier = 0;
		grade = parentNode.grade;
		negaScoutNumber = 0;

		currentBoard = new Board(parent.currentBoard, move);

		if (move.IsJump()) {
			gradeModifier += move.Length() - 1;
		}
		if (move.IsEOG()) {
			gradeModifier += 100;
		}

		grade += (Math.pow(-1, player + 1)) * gradeModifier;
		negaScoutNumber = grade;

		currentBoard.FindPossibleMoves(possibleMoves, player);
		depth = parent.depth + 1;

		// currentBoard.PrintNoColor();

	}

	public Node(Board initialBoard, byte firstplayer) {

		parent = null;
		currentBoard = initialBoard;
		possibleMoves = new Vector<Move>();
		depth = 0;
		grade = 0;
		player = firstplayer;

		currentBoard.FindPossibleMoves(possibleMoves, player);

		// currentBoard.PrintNoColor();

	}

	public void createChildren() {

		numberOfPossibleMoves = possibleMoves.size();
		children = new Node[numberOfPossibleMoves];

		for (int i = 0; i < children.length; i++) {
			children[i] = new Node(this, possibleMoves.elementAt(i),
					(byte) (player ^ inverse));
			Tree.lastGeneration.add(children[i]);
		}

	}

}
