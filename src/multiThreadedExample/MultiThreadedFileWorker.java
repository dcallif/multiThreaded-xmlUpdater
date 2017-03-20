package multiThreadedExample;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * This class reads in all files in a specified dir, un-archives them, 
 * edits the XML file to change the tenantKey node, 
 * and re-archives them into a plz
 */
public class MultiThreadedFileWorker implements Runnable
{
	File unzipDir;
	File updatedDir;
	File currentFile;
	MultiThreadedMain main;
	String outputDir;
	String unzipPath;
	String updatedPath;

	@Override
	public void run() 
	{
		//Creates object that is the unzipPath folder
		unzipDir = new File(unzipPath);
		//Creates object that is the updatedPath folder (where we are storing the files)
		updatedDir = new File(outputDir);

		unzipFile(currentFile);

		// Get File Name
		String fileName = currentFile.getName(); // Has .plz on it
		fileName = fileName.substring(0, fileName.lastIndexOf('.')); // remove extension

		//Getting XML file inside the unzipped directory
		File xmlFile = new File(unzipDir.getAbsolutePath() + File.separator + fileName + ".xml");
		try 
		{
			//Convert XML file to an XML document which is an object
			Document doc = getXMLDocument(xmlFile);

			if (doc != null)
			{
				//Updates object doc with new XML node xlms
				updateXML(doc);
				//Writes object doc to xmlFile. Overwrites original xml file.
				writeXMLDocument(doc, xmlFile);
				zipFile(currentFile);
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}

		// Remove Files from Unzip Dir
		for (File f : unzipDir.listFiles())
		{
			f.delete();
		}

		//main.addResponse("Success", true);
	}

	//Constructor 
	public MultiThreadedFileWorker(File currentFile, String unzipPath, String updatedPath, MultiThreadedMain main)
	{
		this.currentFile = currentFile;
		this.unzipPath = unzipPath;
		this.updatedPath = updatedPath;
		this.main = main;
		
		run();
	}

	//Unzip all files in unzipDir
	public void unzipFile(File file)
	{
		if (file.exists())
		{
			System.out.println("Updated file: " + file.getName());

			try 
			{
				ZipFile zipFile = new ZipFile(file);
				zipFile.extractAll(unzipDir.getAbsolutePath());
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			} 

		}
	}

	//Zips all files in unzipDir
	public void zipFile(File file)
	{
		try 
		{
			File newFile = new File(updatedDir.getAbsolutePath() + File.separator + file.getName());

			ZipFile zipFile = new ZipFile(newFile);

			for (File f : unzipDir.listFiles())
			{
				zipFile.addFile(f, new ZipParameters());
			}

		} 

		catch (Exception e) 
		{
			e.printStackTrace();
		} 
	}

	//Retrieves XML file
	public Document getXMLDocument(File file)
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		Document doc = null;

		try 
		{
			db = dbf.newDocumentBuilder();
			doc = db.parse(file);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		} 

		return doc;
	}

	//Updates tenantKey node in source XML
	public void updateXML(Document xmlDocument)
	{
		Node page = xmlDocument.getFirstChild();

		// update tenantKey attribute
		NamedNodeMap attr = page.getAttributes();
		Node nodeAttr = attr.getNamedItem("tenantKey");
		nodeAttr.setTextContent("UPDATED");
	}

	//Writes updated XML file
	public void writeXMLDocument(Document xmlDocument, File xmlFile)
	{
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		try 
		{
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(xmlDocument);
			StreamResult result =  new StreamResult(xmlFile);
			transformer.transform(source, result);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
}
