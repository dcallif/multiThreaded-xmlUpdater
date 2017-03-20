package multiThreadedExample;

public class ThreadExample extends Thread
{
	private Thread t;
	private String threadName;
	
	ThreadExample(String name) 
	{
		threadName = name;
		System.out.println("Creating " + threadName);
	}
	
	public void run()
	{
		System.out.println("Running " + threadName);
		try
		{
			for(int i = 4; i > 0; i--)
			{
				System.out.println("Thread: " + threadName + ", " + i);
				//Makes thread sleep in millis
				Thread.sleep(50);
			}
		}
		catch (InterruptedException e)
		{
			System.out.println("Thread " + threadName + " interrupted.");
		}
		System.out.println("Thread " + threadName + " exiting.");
	}
	
	public void start()
	{
		System.out.println("Starting " + threadName);
		if(t == null)
		{
			t = new Thread(this, threadName);
			t.start();
		}
	}

	public static void main(String[] args) 
	{
		ThreadExample T1 = new ThreadExample( "Thread 1" );
		T1.start();
		
		ThreadExample T2 = new ThreadExample( "Thread 2" );
		T2.start();
		
	}

}
