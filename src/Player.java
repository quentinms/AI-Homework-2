import java.util.Date;

public class Player {

	
	static int tour=1;
	
	Tree tree;

	// /called when waiting for the other player to move

	// /\param pBoard the current state of the board
	// /\return false if we don't want this function to be called again
	// /until next move, true otherwise
	boolean Idle(Board pBoard) {
		return false;
	}

	// /perform initialization of the player

	// /\param pFirst true if we will move first, false otherwise
	// /\param pDue time before which we must have returned. To check,
	// /for example, to check if we have less than 100 ms to return, we can
	// check if
	// /new Date().getTime()+100>pDue.getTime()
	// /Times returned by getTime() are in milliseconds
	void Initialize(boolean pFirst, Date pDue) {
		// mRandom=new Random();

		if (pFirst) {
			System.out.println("Play first!");
			tree = new Tree(new Board(), Board.CELL_OWN);
		} else {
			System.out.println("Play second!");
			tree = new Tree(new Board(), Board.CELL_OTHER);
		}
		tree.continueGeneratingTree();
		tree.continueGeneratingTree();
		tree.continueGeneratingTree();
		tree.continueGeneratingTree();
		tree.continueGeneratingTree();
		tree.continueGeneratingTree();
		System.out.println("blu");

	}

	// /perform a move

	// /\param pBoard the current state of the board
	// /\param pDue time before which we must have returned
	// /\return the move we make
	Move Play(Board pBoard, Date pDue, Move move) {
		// use the commented version if your system supports ANSI color (linux
		// does)
		// pBoard.Print();

		//pBoard.PrintNoColor();
		if(Debug.equalBoards(pBoard.mCell, new Board().mCell)){System.exit(0);}
		
		System.out.println(tour);tour++;
		
		Move nMove = tree.findMove(move);
		if (nMove != null) {
			tree.changeHead(nMove);
		}
		
		tree.continueGeneratingTree();
		tree.continueGeneratingTree();
		
		Move nextMove = tree.chooseBestMove();
		tree.changeHead(nextMove);

		return nextMove;

	}

}
