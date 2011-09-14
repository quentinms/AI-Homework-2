import java.util.Vector;

public class Node {

	Board currentBoard;
	int algoValue;
	Move prevMove;
	Node parent;
	int grade;
	byte player;
	String name;

	public static final byte inverse = new Byte("3");
	// private static final int JUMP_POINTS=50;
	private static final int KING_POINTS = 14;
	private static final int PEON_POINTS = 10;

	Node(Board pBoard, Move pMove, byte player, Node parent) {
		currentBoard = pBoard;
		prevMove = pMove;
		this.player = player;
		this.parent = parent;
		grade = 0;
	}

	Node(int value, String nam) {
		algoValue = value;
		name = nam;
	}

	public int gradeNode() {
		int gr = 0;
		int other = 0;
		for (int r = 7; r >= 0; r--) {
			for (int c = 7; c >= 0; c--) {
				if ((currentBoard.At(r, c) & Board.CELL_OWN) != 0) {
					if ((currentBoard.At(r, c) & Board.CELL_KING) != 0)
						gr += KING_POINTS;
					else
						gr += PEON_POINTS;
				} else if ((currentBoard.At(r, c) & Board.CELL_OTHER) != 0) {
					other++;
					if ((currentBoard.At(r, c) & Board.CELL_KING) != 0)
						gr -= KING_POINTS;
					else
						gr -= PEON_POINTS;
				}
			}
		}
		if (other == 0)
			grade += 1000;
		grade = gr;
		return gr;

	}

	public Node alphaBetaPruning(int depth, Node alpha, Node beta,
			byte player) {

		/*
		 function alphabeta(node, depth, α, β, Player)         
    if  depth = 0 or node is a terminal node
        return the heuristic value of node
    if  Player = MaxPlayer
        for each child of node
            α := max(α, alphabeta(child, depth-1, α, β, not(Player) ))     
            if β ≤ α
                break                             (* Beta cut-off *)
        return α
    else
        for each child of node
            β := min(β, alphabeta(child, depth-1, α, β, not(Player) ))     
            if β ≤ α
                break                             (* Alpha cut-off *)
        return beta
		 */
		if (depth == 0)
				return returnNode(this);
			//byte bPlayer = n.player ? Board.CELL_OWN : Board.CELL_OTHER;
			if (player==Board.CELL_OWN) {
				Vector<Move> possibleMoves = new Vector<Move>();
			currentBoard.FindPossibleMoves(possibleMoves, player);
				if (possibleMoves.size()==0)
					return returnNode(this);
				for (Move pMove : possibleMoves) {
					Node alphabeta = new Node(new Board(currentBoard, pMove),pMove,
							(byte)(player^inverse), this).alphaBetaPruning(depth - 1, alpha,
							beta,(byte)(player^inverse));
					alpha = (alpha.algoValue >= alphabeta.algoValue) ? alpha
							: alphabeta;
					if (beta.algoValue <= alpha.algoValue)
						break;
				}
				return alpha;
			} else {
				Vector<Move> possibleMoves = new Vector<Move>();
				currentBoard.FindPossibleMoves(possibleMoves, player);
				if (possibleMoves.size()==0)
					return returnNode(this);
				for (Move pMove : possibleMoves) {
					Node alphabeta = new Node(new Board(currentBoard, pMove),pMove,
							(byte)(player^inverse), this).alphaBetaPruning(depth - 1, alpha,
							beta,(byte)(player^inverse));
					beta = (beta.algoValue <= alphabeta.algoValue) ? beta
							: alphabeta;
					if (beta.algoValue <= alpha.algoValue)
						break;
				}
				return beta;
			}
		}
	

	Node returnNode(Node n) {
		n.gradeNode();
		n.algoValue = n.grade;
		return n;

	}

	Move findOriginalMove(Node n) {
		if (n.parent == null) {
			System.out.println("blu");
			return n.prevMove;
		} else if (n.parent.parent == null) {
			return n.prevMove;
		} else {
			return findOriginalMove(n.parent);
		}
	}

}
