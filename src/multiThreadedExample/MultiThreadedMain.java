package multiThreadedExample;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MultiThreadedMain
{
	/**
	 * Initiate fileWorker class
	 * @param dirName = dir containing plz's
	 * @param unzipDir = dir to unzip plz's
	 * @param saveLocation = dir to store updated plz's
	 */
	
	static String dirName = "C://Users//Daniel//Desktop//New folder//";
	static String unzipDir = "C://Users//Daniel//Desktop//New folder//UnzippedFiles//";
	static String saveLocation = "C://Users//Daniel//Desktop//New folder//UpdatedPages//";
	static File[] files = new File(dirName).listFiles();
	 


	private String plzDir, outputDir;

	public void addResponse( String msg, boolean success)
	{

	}


	public MultiThreadedMain(String plzDir, String outputDir, String unzipDir) 
	{
		this.plzDir = plzDir;
		this.outputDir = outputDir;
		this.unzipDir = unzipDir;
	}

	private void doIt() throws InterruptedException
	{
		ExecutorService threadPoolExecutorService = Executors.newFixedThreadPool(15);

		File baseDir = new File(plzDir);
		for( File file : baseDir.listFiles())
		{
			MultiThreadedFileWorker mtfw = new MultiThreadedFileWorker(file, unzipDir, outputDir, this);
			threadPoolExecutorService.submit(mtfw);
		}

		threadPoolExecutorService.shutdown();
		threadPoolExecutorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);

	}


	public static void main(String[] args) throws InterruptedException 
	{
		MultiThreadedMain mtm = new MultiThreadedMain(dirName, saveLocation, unzipDir);
		mtm.doIt();
	}
}