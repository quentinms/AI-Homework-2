import java.io.*;
import java.net.*;
import java.util.Date;

public class Client {

	public static void main(String[] pArgs) 
	{
	    boolean lStandalone;
	
		if(pArgs.length<2)
		{
			System.out.println("usage: java Client host port [gamekey]");
		}
	
		Player lPlayer=new Player();
		
		try
		{
			Socket lSocket=new Socket(pArgs[0],Integer.parseInt(pArgs[1]));
			PrintWriter lOut=new PrintWriter(lSocket.getOutputStream());
			BufferedReader lIn=new BufferedReader(new InputStreamReader(lSocket.getInputStream()));
	
		    if(pArgs.length==2)
		    {
		        lStandalone=true;
		        lOut.println("MODE STANDALONE");
            }
		    else
		    {
		        lStandalone=false;
		        lOut.println("MODE GAME "+pArgs[2]);
            }
            lOut.flush();
	
		    //receive the answer
		    boolean lFirst; //will be true if we play first
		    Date lTime; //time when initialization must be done
		    String lLine=lIn.readLine();
		    String[] lTokens=lLine.split("[ ]+");
		    lFirst=Integer.parseInt(lTokens[1])!=0;
		    if(lStandalone)
    		    lTime=new Date(new Date().getTime()+19000);
            else
    		    lTime=new Date(Long.parseLong(lTokens[0])/1000);
	
		    lPlayer.Initialize(lFirst,lTime);
	
		    lOut.println("INIT");
		    lOut.flush();
		    
		    Board lBoard=new Board();
		    
		    while(true)
		    {
		        lSocket.setSoTimeout(1);
		        while(true)
		        {
		            lLine=null;
		            try
		            {
		                lLine=lIn.readLine();
		            }
		            catch(Throwable t)
		            {
		            }
		            
		            if(lLine!=null) break;
		            
		            if(!lPlayer.Idle(lBoard))
		                lSocket.setSoTimeout(0);
		        }
		        int lSpace=lLine.indexOf(' ');
      		    if(lStandalone)
        		    lTime=new Date(new Date().getTime()+9000);
                else
        		    lTime=new Date(Long.parseLong(lLine.substring(0,lSpace))/1000);
		        Move lMove=new Move(lLine.substring(lSpace+1));
		        
		        if(lMove.IsEOG())
		        {
		            if(lMove.Length()!=0)
		            {
		                if(lMove.At(0)==1)
		                    System.out.println("YOU WIN");
		                else if(lMove.At(0)==2)
		                    System.out.println("YOU LOSE");
		                else if(lMove.At(0)==3)
		                    System.out.println("DRAW");
                        else
                            System.out.println("INVALID GAME");
		            }
		            return;
                }
	
		        lBoard.DoMove(lMove);
	
		        lMove=lPlayer.Play(lBoard,lTime);
		        
		        lOut.println(lMove.ToString());
		        lOut.flush();
		        
		        if(lMove.IsEOG())
		            return;
		        
		        lBoard.DoMove(lMove);
		    }
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
	}
}
