package multiThreadedExample;

public abstract class AbstractFileWorker extends Thread
{
	private Thread t;
	private String dirName;
	private String unzipDir;
	private String saveLocation;
	
	AbstractFileWorker(String dirName, String unzipDir,String saveLocation)
	{
		this.dirName = dirName;
		this.unzipDir = unzipDir;
		this.saveLocation = saveLocation;
	}
}