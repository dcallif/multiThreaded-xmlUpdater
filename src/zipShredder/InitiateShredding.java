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
		
		String dirName = "/Users/dcallif/Documents/Client Stuff/Atlas Copco CR/source plzs/";
		String unzipDir = "/Users/dcallif/Documents/Client Stuff/Atlas Copco CR/unzip/";
		String saveLocation = "/Users/dcallif/Documents/Client Stuff/Atlas Copco CR/updated plzs/";
		
		new FileWorker(dirName, unzipDir, saveLocation, true);
		
		long endTime = System.nanoTime();

		long duration = (endTime - startTime) / 1000000;  //divide by 1000000 to get milliseconds.
		System.out.println( "Total execution time: " + duration + "ms" );
	}
}