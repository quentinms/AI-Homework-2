import java.util.Vector;


public class Board {
    static final int cSquares=32;			///< 32 valid squares
    static final int cPlayerPieces=12;		///< 12 pieces per player

    static final byte CELL_EMPTY=0;			///< the cell is empty
    static final byte CELL_OWN=(1<<0);		///< the cell belongs to us (the one playing)
    static final byte CELL_OTHER=(1<<1);	///< the cell belongs to the other player
    static final byte CELL_KING=(1<<2);		///< the cell contains a king 
    static final byte CELL_INVALID=(1<<3);  ///< the cell is invalid 
    
    ///initializes the board to the starting position
    Board()
    {
    	mCell=new byte[cSquares];
    	
        for(int i=0;i<cPlayerPieces;i++)
        {
            mCell[i]=CELL_OWN;
            mCell[cSquares-1-i]=CELL_OTHER;
        }
        for(int i=cPlayerPieces;i<cSquares-cPlayerPieces;i++)
        {
            mCell[i]=CELL_EMPTY;
        }
    }
    
    
    ///constructs a board which is the result of applying move \p pMove to board \p pRH
    
    /// \param pBoard the starting board position
    /// \param pMove the movement to perform
    ///
    /// \sa DoMove()
    Board(Board pBoard,Move pMove)
    {
    	mCell=pBoard.mCell.clone();
    	DoMove(pMove);
    }

    ///returns the content of a cell in the board.

    ///Cells are numbered as follows:
    ///
    ///    31    30    29    28
    /// 27    26    25    24
    ///    23    22    21    20
    /// 19    18    17    16
    ///    15    14    13    12
    /// 11    10     9     8
    ///    7      6     5     4
    ///  3     2     1     0
    ///
    /// this function returns a byte representing the contents of the cell
    ///
    /// For example, to check if cell 3 contains an own piece, you would check 
    ///
    ///   (lBoard.At(3)&CELL_OWN)!=0
    ///
    /// to check if it is a piece of the other player,
    ///
    ///   (lBoard.At(3)&CELL_OTHER)!=0
    ///
    /// and to check if it is aking, you would check if
    ///
    ///   (lBoard.At(3)&CELL_KING)!=0
    ///
    byte At(int pPos)
    {
        return mCell[pPos];
    }
    
    ///returns the content of a cell in the board.

    ///Rows are numbered (0 to 7) from the lower row in the board, 
    ///as seen by the player this program is playing.
    ///
    ///Columns are numbered starting from the right (also 0 to 7).
    ///
    ///Cells corresponding to white squares in the board, which will
    ///never contain a piece, always return CELL_INVALID
    ///
    ///If the cell falls outside of the board, return CELL_INVALID
    byte At(int pR,int pC)
    {
        if(pR<0||pR>7||pC<0||pC>7) return CELL_INVALID;
        if((pR&1)==(pC&1)) return CELL_INVALID;
        return mCell[pR*4+(pC>>1)];
    }

    ///private version of above function (allows modifying cells)
    private void Set(int pR,int pC,byte pValue)
    {
    	mCell[pR*4+(pC>>1)]=pValue;
    }

    ///returns the row corresponding to a cell index
    static int CellToRow(int pCell)
    {
        return (pCell>>2);
    }
    
    ///returns the col corresponding to a cell index
    static int CellToCol(int pCell)
    {
        int lC=(pCell&3)<<1;
        if((pCell&4)==0)
            lC++;
        return lC;
    }
    
    ///returns the cell corresponding to a row and col
    
    ///It doesn't check if it corresponds to a black square in the board,
    ///or if it falls within the board.
    ///
    ///If it doesn't, the result is undefined, and the program is likely
    ///to crash
    static int RowColToCell(int pRow,int pCol)
    {
        return (pRow*4+(pCol>>1));
    }

    ///tries to make a jump from a certain position of the board
    
    /// \param pMoves a vector where the valid moves will be inserted
    /// \param pOther the \ref ECell code corresponding to the player
    /// who is not making the move
    /// \param pR the row of the cell we are moving from
    /// \param pC the col
    /// \param pKing true if the moving piece is a king
    /// \param pBuffer a buffer where the list of jump positions is 
    /// inserted (for multiple jumps)
    /// \param pDepth the number of multiple jumps before this attempt
    private boolean TryJump(Vector<Move> pMoves,byte pOther,int pR,int pC,
                 boolean pKing,byte[] pBuffer,int pDepth)
    {
        pBuffer[pDepth]=(byte)RowColToCell(pR,pC);
        boolean lFound=false;

        //try capturing forward
        if(pOther==CELL_OTHER||pKing)
        {
            //try capturing right
            if((At(pR+1,pC-1)&pOther)!=0&&At(pR+2,pC-2)==CELL_EMPTY)
            {
                lFound=true;
                byte lOldValue=At(pR+1,pC-1);
                Set(pR+1,pC-1,CELL_EMPTY);
                TryJump(pMoves,pOther,pR+2,pC-2,pKing,pBuffer,pDepth+1);
                Set(pR+1,pC-1,lOldValue);
            }
            //try capturing left
            if((At(pR+1,pC+1)&pOther)!=0&&At(pR+2,pC+2)==CELL_EMPTY)
            {
                lFound=true;
                byte lOldValue=At(pR+1,pC+1);
                Set(pR+1,pC+1,CELL_EMPTY);
                TryJump(pMoves,pOther,pR+2,pC+2,pKing,pBuffer,pDepth+1);
                Set(pR+1,pC+1,lOldValue);
            }
        }
        //try capturing backwards
        if(pOther==CELL_OWN||pKing)
        {
            //try capturing right
            if((At(pR-1,pC-1)&pOther)!=0&&At(pR-2,pC-2)==CELL_EMPTY)
            {
                lFound=true;
                byte lOldValue=At(pR-1,pC-1);
                Set(pR-1,pC-1,CELL_EMPTY);
                TryJump(pMoves,pOther,pR-2,pC-2,pKing,pBuffer,pDepth+1);
                Set(pR-1,pC-1,lOldValue);
            }
            //try capturing left
            if((At(pR-1,pC+1)&pOther)!=0&&At(pR-2,pC+2)==CELL_EMPTY)
            {
                lFound=true;
                byte lOldValue=At(pR-1,pC+1);
                Set(pR-1,pC+1,CELL_EMPTY);
                TryJump(pMoves,pOther,pR-2,pC+2,pKing,pBuffer,pDepth+1);
                Set(pR-1,pC+1,lOldValue);
            }
        }
        
        if(!lFound&&pDepth>0)
        {
            pMoves.add(new Move(pDepth+1,pBuffer));
        }

        return lFound;
    }

    ///tries to make a move from a certain position
    
    /// \param pMoves vector where the valid moves will be inserted
    /// \param pCell the cell where the move is tried from
    /// \param pOther the \ref ECell code corresponding to the player
    /// who is not making the move
    /// \param pKing true if the piece is a king
    private void TryMove(Vector<Move> pMoves,int pCell,int pOther,boolean pKing)
    {
        int lR=CellToRow(pCell);
        int lC=CellToCol(pCell);
        //try moving forward
        if(pOther==CELL_OTHER||pKing)
        {
            //try moving right
            if(At(lR+1,lC-1)==CELL_EMPTY)
                pMoves.add(new Move(pCell,RowColToCell(lR+1,lC-1)));
            //try moving left
            if(At(lR+1,lC+1)==CELL_EMPTY)
                pMoves.add(new Move(pCell,RowColToCell(lR+1,lC+1)));
        }
        //try moving backwards
        if(pOther==CELL_OWN||pKing)
        {
            //try moving right
            if(At(lR-1,lC-1)==CELL_EMPTY)
                pMoves.add(new Move(pCell,RowColToCell(lR-1,lC-1)));
            //try moving left
            if(At(lR-1,lC+1)==CELL_EMPTY)
                pMoves.add(new Move(pCell,RowColToCell(lR-1,lC+1)));
        }
    }

    /// returns a list of all valid moves for \p pWho
    
    /// \param pMoves a vector where the list of moves will be appended
    /// \param pWho the \ref ECell code (CELL_OWN or CELL_OTHER) of the
    /// player making the move
    void FindPossibleMoves(Vector<Move> pMoves,byte pWho)
    {
        pMoves.clear();
    
        assert(pWho==CELL_OWN||pWho==CELL_OTHER);

        byte lOther=(byte)(pWho^(CELL_OWN|CELL_OTHER));
    
        boolean lFound=false;
        int[] lPieces=new int[cPlayerPieces];
        byte[] lMoveBuffer=new byte[cPlayerPieces];
        int lNumPieces=0;
        for(int i=0;i<cSquares;i++)
        {
            //if it belongs to the player making the move
            if((At(i)&pWho)!=0)
            {
                boolean lIsKing=(At(i)&CELL_KING)!=0;

                if(TryJump(pMoves,lOther,CellToRow(i),CellToCol(i),lIsKing,
                                     lMoveBuffer,0))
                {
                    lFound=true;
                }
                else if(!lFound)
                {
                    lPieces[lNumPieces++]=i;
                }
            }
        }

        if(!lFound)
        {
            for(int k=0;k<lNumPieces;k++)
            {
                int lCell=lPieces[k];
                boolean lIsKing=(At(lCell)&CELL_KING)!=0;
                TryMove(pMoves,lCell,lOther,lIsKing);
            }
        }        
    }
    
    ///transforms the board by performing a move

    ///it doesn't check that the move is valid, so you should only use
    ///it with moves returned by FindPossibleMoves 
    
    /// \param pMove the move to perform
    void DoMove(Move pMove)
    {
        if(pMove.IsJump())
        {
            int lSR=CellToRow(pMove.At(0));
            int lSC=CellToCol(pMove.At(0));
        
            for(int i=1;i<pMove.Length();i++)
            {
                int lDR=CellToRow(pMove.At(i));
                int lDC=CellToCol(pMove.At(i));
                
                mCell[pMove.At(i)]=mCell[pMove.At(i-1)];
                mCell[pMove.At(i-1)]=CELL_EMPTY;

                if((lDR==7&&(mCell[pMove.At(i)]&CELL_OWN)!=0)||
                   (lDR==0&&(mCell[pMove.At(i)]&CELL_OTHER)!=0))
                    mCell[pMove.At(i)]|=CELL_KING;
        
                ///now we have to remove the other one
                if(lDR>lSR)
                {
                    if(lDC>lSC)
                        mCell[RowColToCell(lDR-1,lDC-1)]=CELL_EMPTY;
                    else
                        mCell[RowColToCell(lDR-1,lDC+1)]=CELL_EMPTY;
                }
                else
                {
                    if(lDC>lSC)
                        mCell[RowColToCell(lDR+1,lDC-1)]=CELL_EMPTY;
                    else
                        mCell[RowColToCell(lDR+1,lDC+1)]=CELL_EMPTY;
                }
                
                lSR=lDR;
                lSC=lDC;
            }
        }
        else if(pMove.IsNormal())
        {
            int lDR=CellToRow(pMove.At(1));
            mCell[pMove.At(1)]=mCell[pMove.At(0)];
            mCell[pMove.At(0)]=CELL_EMPTY;

            if((lDR==7&&(mCell[pMove.At(1)]&CELL_OWN)!=0)||
               (lDR==0&&(mCell[pMove.At(1)]&CELL_OTHER)!=0))
                mCell[pMove.At(1)]|=CELL_KING;
        }
    }

    ///prints the board
    
    ///Useful for debug purposes. Don't call it in the final version.
    void Print()
    {
        for(int r=7;r>=0;r--)
        {
            for(int c=7;c>=0;c--)
            {
                if(c%2==r%2) //white
                {
                    System.out.print((char)27+"[47m  "+(char)27+"[0m");
                }
                else if((At(r,c)&CELL_OWN)!=0)
                {
                    if((At(r,c)&CELL_KING)!=0)
                        System.out.print((char)27+"[37;40m##"+(char)27+"[0m");
                    else
                        System.out.print((char)27+"[37;40m()"+(char)27+"[0m");
                }
                else if((At(r,c)&CELL_OTHER)!=0)
                {
                    if((At(r,c)&CELL_KING)!=0)
                        System.out.print((char)27+"[31;40m##"+(char)27+"[0m");
                    else
                        System.out.print((char)27+"[31;40m()"+(char)27+"[0m");
                }
                else
                {
                    System.out.print((char)27+"[37;40m  "+(char)27+"[0m");
                }
            }
            System.out.print("\n");
        }
        System.out.print("\n");
    }

    ///prints the board (no color version)
    
    ///Useful for debug purposes. Don't call it in the final version.
    void PrintNoColor()
    {
        System.out.println("----------------");
    
        for(int r=7;r>=0;r--)
        {
            for(int c=7;c>=0;c--)
            {
                if(c%2==r%2) //white
                {
                    System.out.print("  ");
                }
                else if((At(r,c)&CELL_OWN)!=0)
                {
                    if((At(r,c)&CELL_KING)!=0)
                        System.out.print("WW");
                    else
                        System.out.print("ww");
                }
                else if((At(r,c)&CELL_OTHER)!=0)
                {
                    if((At(r,c)&CELL_KING)!=0)
                        System.out.print("RR");
                    else
                        System.out.print("rr");
                }
                else
                {
                    System.out.print("  ");
                }
            }
            System.out.print("\n");
        }
        System.out.println("----------------");
    }

    byte[] mCell;
}
