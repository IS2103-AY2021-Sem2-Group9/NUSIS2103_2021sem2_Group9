package util.thread;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;



public class RunnableNotification implements Runnable
{
    private Thread thread;
    private Boolean result;

    
    
    public RunnableNotification() 
    {
    }

    
    
    public RunnableNotification(Boolean result)
    {
        this.result = result;
    }
    
    
    
    public void run()
    {
       try
       {
           while(true)
           {
               Thread.sleep(1000);

               if(result != null) 
               {

                   if(result)
                   {
                       System.out.println("[SERVER] Appointment reminder notification email actually sent successfully!\n");
                   }
                   else
                   {
                       System.out.println("[SERVER] Appointment reminder notification email was NOT sent!\n");
                   }

                   break;
               }
           }
       }
       catch(InterruptedException ex)
       {
           System.out.println("[SERVER] Error actually sending checkout notification email: " + ex.getMessage() + "!\n");
       }
    }
    
    
    
    public void start()
    {
        if (thread == null) 
        {
            thread = new Thread (this, "thread1");
            thread.start();
        }
    }
}