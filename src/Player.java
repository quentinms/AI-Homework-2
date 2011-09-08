import java.util.Random;
import java.util.Date;
import java.util.Vector;

public class Player {
	
    ///called when waiting for the other player to move
    
    ///\param pBoard the current state of the board
    ///\return false if we don't want this function to be called again
    ///until next move, true otherwise
  	boolean Idle(Board pBoard)
	{
	    return false;
	}

    ///perform initialization of the player
    
    ///\param pFirst true if we will move first, false otherwise
    ///\param pDue time before which we must have returned. To check,
    ///for example, to check if we have less than 100 ms to return, we can check if
    ///new Date().getTime()+100>pDue.getTime()
    ///Times returned by getTime() are in milliseconds
	void Initialize(boolean pFirst,Date pDue)
	{
		mRandom=new Random();
	}
	    
    ///perform a move

    ///\param pBoard the current state of the board
    ///\param pDue time before which we must have returned
    ///\return the move we make
	Move Play(Board pBoard,Date pDue)
	{
	    //use the commented version if your system supports ANSI color (linux does)
	    //pBoard.Print();
	    pBoard.PrintNoColor();
	
		Vector<Move> lMoves=new Vector<Move>();
	    pBoard.FindPossibleMoves(lMoves,Board.CELL_OWN);
	    /*
	     * Here you should write your clever algorithms to get the best next move.
	     * This skeleton returns instead a random movement from the legal ones.
	     */
	    return lMoves.elementAt(mRandom.nextInt(lMoves.size()));
	}

	Random mRandom;
}
