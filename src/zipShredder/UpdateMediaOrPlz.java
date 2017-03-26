package zipShredder;

/**
 * Initiate fileWorker class
 * @param dirName = dir containing plz's
 * @param unzipDir = dir to unzip plz's
 * @param saveLocation = dir to store updated plz's
 */
public class UpdateMediaOrPlz
{
	public static void main(String[] args) 
	{	
		UpdateMediaOrPlz update = new UpdateMediaOrPlz();
		update.updateMedia( "/Users/dcallif/Documents/Client Stuff/Atlas Copco CR/source plzs/","/Users/dcallif/Documents/Client Stuff/Atlas Copco CR/unzip/"
				,"/Users/dcallif/Documents/Client Stuff/Atlas Copco CR/updated plzs/" );
		update.updatePlzs( "/Users/dcallif/Documents/Client Stuff/Atlas Copco CR/source plzs/","/Users/dcallif/Documents/Client Stuff/Atlas Copco CR/unzip/"
			,"/Users/dcallif/Documents/Client Stuff/Atlas Copco CR/updated plzs/" );
	}
	public void updatePlzs(String dirName, String unzipDir, String saveLocation)
	{
		long startTime = System.nanoTime();

		new FileWorker( dirName, unzipDir, saveLocation, true );

		long endTime = System.nanoTime();

		long duration = (endTime - startTime) / 1000000000;  //divide by 1000000000 to get seconds.
		System.out.println( "Total execution time: " + duration + "ms" );
	}
	public void updateMedia(String dirName, String unzipDir, String saveLocation)
	{
		long startTime = System.nanoTime();

		new FileWorker( dirName, unzipDir, saveLocation, false );

		long endTime = System.nanoTime();

		long duration = (endTime - startTime) / 1000000000;  //divide by 1000000000 to get seconds.
		System.out.println( "Total execution time: " + duration + "ms" );
	}
}