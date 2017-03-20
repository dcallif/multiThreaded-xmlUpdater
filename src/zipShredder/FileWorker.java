package zipShredder;

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
import org.w3c.dom.NodeList;

/**
 * This class reads in all files in a specified dir, un-archives them, 
 * edits the XML file to change the tenantKey node, 
 * and re-archives them into a plz
 */
public class FileWorker
{
	File unzipDir;
	File updatedDir;
	File[] fileList;
	
	private final String supplierKey = "IEC-MODEL";
	private final String tenantKey = "IEC-MODEL";
	private final String userName = "digabitadmin@IEC-MODEL.com";

	//Constructor 
	public FileWorker(String directoryName, String unzipPath, String updatedPath, boolean isPlz)
	{
		if( isPlz == true )
		{
			//Creates object that is the unzipPath folder
			unzipDir = new File(unzipPath);
			//Creates object that is the updatedPath folder (where we are storing the files)
			updatedDir = new File(updatedPath);
			//Method that returns file array that is set on fileList object
			fileList = getFileList(directoryName);

			for (File file : fileList)
			{
				//If file is a directory stop
				if (file.isDirectory())
					break;

				unzipFile(file);

				// Get File Name
				String fileName = file.getName(); // Has .plz on it
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
						zipFile(file);
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
			}
		}
		else
		{
			//Creates object that is the unzipPath folder
			unzipDir = new File(unzipPath);
			//Creates object that is the updatedPath folder (where we are storing the files)
			updatedDir = new File(updatedPath);
			//Method that returns file array that is set on fileList object
			fileList = getFileList(directoryName);

			for (File file : fileList)
			{
				//If file is a directory stop
				if (file.isDirectory())
					break;

				// Get File Name
				String fileName = file.getName(); // Has .mdz on it
				fileName = fileName.substring(0, fileName.lastIndexOf('.')); // remove extension

				//Getting XML file inside the unzipped directory
				File xmlFile = new File(unzipDir.getAbsolutePath() + File.separator + fileName + ".xml");
				try 
				{
					//Convert XML file to an XML document which is an object
					Document doc = getXMLDocument(file);

					if (doc != null)
					{
						//Updates object doc with new XML node xlms
						updateMediaXML(doc);
						//Writes object doc to xmlFile. Overwrites original xml file.
						writeXMLDocument(doc, xmlFile);
					}
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
		}
	}

	public FileWorker() 
	{
	}

	//Retrieves array of files
	public File[] getFileList(String directoryName)
	{	 
		File directory = new File(directoryName);

		if (directory.exists())
		{
			return directory.listFiles();
		}

		return new File[0];
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

		//update tenantKey attribute
		NamedNodeMap attr = page.getAttributes();
		Node nodeAttr = attr.getNamedItem( "tenantKey" );
		nodeAttr.setTextContent( tenantKey );

		Node pageHashKey = attr.getNamedItem( "hashKey" );
		pageHashKey.setTextContent( "" );

		//loop the Part child nodes
		NodeList list = page.getChildNodes();
		for( int i = 0; i < list.getLength(); i++ ) 
		{
			Node node = list.item( i );

			// get the supplierKey element, and update the value
			if( "Part".equals( node.getNodeName() ) ) 
			{
				//update supplierKey attribute
				NamedNodeMap attr1 = node.getAttributes();
				Node nodeAttr1 = attr1.getNamedItem( "supplierKey" );
				nodeAttr1.setTextContent( supplierKey );
				
				for( int x = 0; x < node.getChildNodes().getLength(); x++ )
				{
					if( "Attachment".equals( node.getNodeName() ) )
					{
						//update userName attribute
						NamedNodeMap attr2 = node.getAttributes();
						Node nodeAttr2 = attr2.getNamedItem( "userName" );
						nodeAttr2.setTextContent( userName );
					}
				}
			}
		}
	}

	public void updateMediaXML(Document xmlDocument)
	{
		Node page = xmlDocument.getFirstChild();

		//update tenantKey attribute
		NamedNodeMap attr = page.getAttributes();
		Node nodeAttr = attr.getNamedItem( "tenantKey" );
		nodeAttr.setTextContent( tenantKey );

		NodeList list = page.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) 
		{
			Node node = list.item( i );

			//get all chapter nodes
			if ( "Chapter".equals( node.getNodeName() ) ) 
			{
				NodeList chapterNodes = node.getChildNodes();

				for( int y = 0; y < chapterNodes.getLength(); y++ )
				{
					Node pageNode = chapterNodes.item( y );

					//get each chapter page to update
					if( "Page".equals( pageNode.getNodeName() ) )
					{	
						NodeList partNodes = pageNode.getChildNodes();
						
						NamedNodeMap attr1 = pageNode.getAttributes();
						Node nodeAttr1 = attr1.getNamedItem( "hashKey" );
						nodeAttr1.setTextContent( "" );
						
						for( int z = 0; z < partNodes.getLength(); z++ )
						{
							Node partNode = partNodes.item( z );
							
							//deletes all Part elements from Media XML (they don't need to be here)
							if( "Part".equals( partNode.getNodeName() ) )
							{
								pageNode.removeChild( partNode );
							}
							else if( "Attachment".equals( partNode.getNodeName() ) )
							{
								NamedNodeMap attachmentAttr = partNode.getAttributes();
								Node attachmentNodeAttr = attachmentAttr.getNamedItem( "userName" );
								attachmentNodeAttr.setTextContent( userName );
							}
						}
					}
					else if( "Attachment".equals( pageNode.getNodeName() ) )
					{
						NamedNodeMap attachmentAttr = pageNode.getAttributes();
						Node attachmentNodeAttr = attachmentAttr.getNamedItem( "userName" );
						attachmentNodeAttr.setTextContent( userName );
					}
				}
			}
			//get all book pages
			else if( "Page".equals( node.getNodeName() ) )
			{
				NamedNodeMap pageAttr = node.getAttributes();
				Node pageNodeAttr = pageAttr.getNamedItem( "hashKey" );
				pageNodeAttr.setTextContent( "" );
				
				NodeList pageNodeList = node.getChildNodes();
				
				for( int x = 0; x < pageNodeList.getLength(); x++ )
				{
					Node attachmentNode = pageNodeList.item( x );
					
					if( "Attachment".equals( attachmentNode.getNodeName() ) )
					{
						NamedNodeMap attachmentAttr = attachmentNode.getAttributes();
						Node attachmentNodeAttr = attachmentAttr.getNamedItem( "userName" );
						attachmentNodeAttr.setTextContent( userName );
					}
				}
			}
			//gets Media Attachment and updates userName attribute
			else if( "Attachment".equals( node.getNodeName() ) )
			{
				NamedNodeMap attachmentAttr = node.getAttributes();
				Node attachmentNodeAttr = attachmentAttr.getNamedItem( "userName" );
				attachmentNodeAttr.setTextContent( userName );
			}
		}
	}

	//writes Document to XML file
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
