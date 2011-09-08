import java.util.Arrays;

///encapsulates a move

///in general, you should regard this as an opaque structure that you obtain
///from FindPossibleMoves, and provide to DoMove.
///
///The functions IsNormal() and Is Jump() can however be useful.
///
///The rest of the interface you can probably ignore it.
public class Move {

	static final int MOVE_JUMP=1;   ///< a simple jump (numbers above that will represent multiple jumps)
	static final int MOVE_NORMAL=0; ///< a normal move
	static final int MOVE_EOG=-1;   ///< end of game
	static final int MOVE_BOG=-2;   ///< beginning of game
	static final int MOVE_NULL=-3;   ///< a null move

    ///constructs a special type move
    
    ///\param pType should be one of MOVE_EOF or MOVE_BOG
    Move(int pType)
    {
    	mType=pType;
    }

    ///constructs a normal move (not a jump)
    
    ///\param p1 the source square
    ///\param p2 the destination square
    Move(int p1,int p2)
    {
    	mType=MOVE_NORMAL;
    	mData=new byte[2];
    	mData[0]=(byte)p1;
    	mData[1]=(byte)p2;
    }

    ///constructs a jump move
    
    ///\param pData a series of squares that form the sequence of jumps
    ///\param pLen the number of squares in pData
    Move(int pLen,byte[] pData)
    {
    	mType=pLen-1;
    	mData=Arrays.copyOfRange(pData,0,pLen);
    }
	    
    ///reconstructs the move from a string
	    
    ///\param pString a string, which should have been previously generated
    ///by ToString(), or obtained from the server
    Move(String pString)
    {
    	String[] lTokens=pString.split("[ ]+");

    	mType=Integer.parseInt(lTokens[0]);
    	
        int lLen=0;
        
        if(mType==MOVE_NORMAL)
            lLen=2;
        else if(mType>0)
            lLen=mType+1;
        else if(mType==MOVE_EOG)
            lLen=1;
            
        if(lLen>12||mType<-3)
        {
            mType=MOVE_NULL;
            return;
        }
        
        mData=new byte[lLen];
            
        for(int i=0;i<lLen;i++)
        {
            int lCell=Integer.parseInt(lTokens[i+1]);
            if(lCell<0||lCell>31)
            {
                mType=MOVE_NULL;
                break;
            }
            
            mData[i]=(byte)lCell;
        }
    }

    ///returns true if the movement is null or invalid
    boolean IsNull() 			 {	 return (mType==MOVE_NULL); }
    ///returns true if the movement marks beginning of game
    boolean IsBOG()            {   return (mType==MOVE_BOG);   }
    ///returns true if the movement marks end of game
    boolean IsEOG()            {   return (mType==MOVE_EOG);   }
    ///returns true if the movement is a jump
    boolean IsJump() 			 {	 return (mType>0);			}
    ///returns true if the movement is a normal move
    boolean IsNormal() 		 {	 return (mType==MOVE_NORMAL);	}

    ///returns the type of the move
    int GetType() 			 {	 return mType;				}
	    
    ///returns (for normal moves and jumps) the number of squares
	int Length()         {   return mData.length;    }
    ///returns the pNth square in the sequence
    byte At(int pN)      {   return mData[pN];    }

    ///converts the move to a string so that it can be sent to the server
    String ToString()
    {
    	StringBuffer lBuffer=new StringBuffer();

    	lBuffer.append((int)mType);
        for(int i=0;i<mData.length;i++)
        {
        	lBuffer.append(' ');
        	lBuffer.append((int)mData[i]);
        }
        
        return lBuffer.toString();
    }

    private int mType;
	private byte[] mData;
}
