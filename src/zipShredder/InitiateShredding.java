package zipShredder;

public class InitiateShredding
{
	public static void main(String[] args) 
	
	{	
		/**
	     * Initiate fileWorker class
	     * @param dirName = dir containing plz's
	     * @param unzipDir = dir to unzip plz's
	     * @param saveLocation = dir to store updated plz's
	     */
		
		long startTime = System.nanoTime();
		
		String dirName = "/Users/dcallif/Documents/Client Stuff/Excel Industries/working content directory/mediaXmls/";
		String unzipDir = "/Users/dcallif/Documents/Client Stuff/Excel Industries/media/unzip/";
		String saveLocation = "/Users/dcallif/Documents/Client Stuff/Excel Industries/media/updated/";
		
		new FileWorker(dirName, unzipDir, saveLocation, false);
		
		long endTime = System.nanoTime();

		long duration = (endTime - startTime) / 1000000;  //divide by 1000000 to get milliseconds.
		System.out.println( "Total execution time: " + duration + "ms" );
	}
}