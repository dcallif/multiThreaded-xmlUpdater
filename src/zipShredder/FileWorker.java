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

	private final String supplierKey = "IEC-SN";
	private final String tenantKey = "IEC-SN";
	private final String userName = "digabitadmin@iec-sn.com";

	public FileWorker(String directoryName, String unzipPath, String updatedPath, boolean isPlz)
	{
		if( isPlz == true )
		{
			//Creates object that is the unzipPath folder
			unzipDir = new File( unzipPath );
			//Creates object that is the updatedPath folder (where we are storing the files)
			updatedDir = new File( updatedPath );
			//Method that returns file array that is set on fileList object
			fileList = getFileList( directoryName );

			for( File file : fileList )
			{
				//If file is a directory stop
				if( file.isDirectory() )
					break;
				
				unzipFile( file );

				// Get File Name
				String fileName = file.getName(); // Has .plz on it
				fileName = fileName.substring( 0, fileName.lastIndexOf( '.' ) ); // remove extension

				//Getting XML file inside the unzipped directory
				File xmlFile = new File( unzipDir.getAbsolutePath() + File.separator + fileName + ".xml" );
				try 
				{
					//Convert XML file to an XML document which is an object
					Document doc = getXMLDocument(xmlFile);

					if( doc != null )
					{
						updateXML( doc );
						writeXMLDocument( doc, xmlFile );
						zipFile( file );
					}
				} 
				catch(Exception e) 
				{
					e.printStackTrace();
				}

				// Remove Files from Unzip Dir
				for( File f : unzipDir.listFiles() )
				{
					f.delete();
				}
			}
		}
		else
		{
			//Creates object that is the updatedPath folder (where we are storing the files)
			updatedDir = new File( updatedPath );
			//Method that returns file array that is set on fileList object
			fileList = getFileList( directoryName );

			for( File file : fileList )
			{
				//If file is a directory stop
				if( file.isDirectory() )
					break;

				// Get File Name
				String fileName = file.getName(); // Has .mdz on it
				fileName = fileName.substring( 0, fileName.lastIndexOf( '.' ) ); // remove extension

				//Getting XML file inside the unzipped directory
				File xmlFile = new File( updatedDir.getAbsolutePath() + File.separator + fileName + ".xml" );
				try 
				{
					//Convert XML file to an XML document which is an object
					Document doc = getXMLDocument( file );

					if( doc != null )
					{
						//updateMediaXML( doc );
						Node media = doc.getFirstChild();

						//update tenantKey attribute
						NamedNodeMap attr = media.getAttributes();
						Node nodeAttr = attr.getNamedItem( "tenantKey" );
						nodeAttr.setTextContent( tenantKey );
						
						updateAllMediaAttachments( media );
						updateAllMediaPages( media );

						writeXMLDocument( doc, xmlFile );
					}
				} 
				catch(Exception e) 
				{
					e.printStackTrace();
				}
			}
		}
	}

	public FileWorker() { }

	//Retrieves array of files
	private File[] getFileList(String directoryName)
	{	 
		File directory = new File( directoryName );

		if( directory.exists() )
		{
			return directory.listFiles();
		}
		return new File[0];
	}

	//Unzip all files in unzipDir
	private void unzipFile(File file)
	{
		if( file.exists() )
		{
			System.out.println( "Updated file: " + file.getName() );

			try 
			{
				ZipFile zipFile = new ZipFile( file );
				zipFile.extractAll( unzipDir.getAbsolutePath() );
			} 
			catch(Exception e) 
			{
				e.printStackTrace();
			} 
		}
	}

	//Zips all files in unzipDir
	private void zipFile(File file)
	{
		try 
		{
			File newFile = new File( updatedDir.getAbsolutePath() + File.separator + file.getName() );

			ZipFile zipFile = new ZipFile( newFile );

			for( File f : unzipDir.listFiles() )
			{
				zipFile.addFile( f, new ZipParameters() );
			}

		} 
		catch(Exception e) 
		{
			e.printStackTrace();
		} 
	}

	//Retrieves XML file
	private Document getXMLDocument(File file)
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		Document doc = null;

		try 
		{
			db = dbf.newDocumentBuilder();
			doc = db.parse( file );
		} 
		catch(Exception e) 
		{
			e.printStackTrace();
		} 
		return doc;
	}

	//Updates tenantKey node in source XML and all supplierKey values
	private void updateXML(Document xmlDocument)
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

			if( "Part".equals( node.getNodeName() ) ) 
			{
				updateParts( node );
			}
			else if( "Attachment".equals( node.getNodeName() ) )
			{
				//update userName attribute
				NamedNodeMap attachmentAttr = node.getAttributes();
				Node attachmentNode = attachmentAttr.getNamedItem( "userName" );
				attachmentNode.setTextContent( userName );
			}
		}
	}
	
	private void updateParts( Node partNode )
	{
		NamedNodeMap partAttr1 = partNode.getAttributes();
		Node partNode1 = partAttr1.getNamedItem( "supplierKey" );
		partNode1.setTextContent( supplierKey );
		
		NodeList partNodeList = partNode.getChildNodes();
		for( int i = 0; i < partNodeList.getLength(); i++ )
		{
			Node node = partNodeList.item( i );
			if( "Attachment".equals( node.getNodeName() ) )
			{
				NamedNodeMap partAttr = node.getAttributes();
				Node newPartNode = partAttr.getNamedItem( "userName" );
				newPartNode.setTextContent( userName );
			}
			else if( "Part".equals( node.getNodeName() ) )
			{
				updateParts( node );
			}
		}
	}
	
	private void updateAllMediaPages( Node mediaNode )
	{
		NodeList mediaNodeList = mediaNode.getChildNodes();
		for( int i = 0; i < mediaNodeList.getLength(); i++ )
		{
			Node node = mediaNodeList.item( i );
			if( "Chapter".equals( node.getNodeName() ) )
			{
				NodeList chapterNodes = node.getChildNodes();
				for( int y = 0; y < chapterNodes.getLength(); y++ )
				{
					Node chapterNode = chapterNodes.item( y );
					if( "Page".equals( chapterNode.getNodeName() ) )
					{
						NamedNodeMap pageAttr = chapterNode.getAttributes();
						Node pageNodeAttr = pageAttr.getNamedItem( "hashKey" );
						pageNodeAttr.setTextContent( "" );
						
						NodeList pageNodes = chapterNode.getChildNodes();
						for( int z = 0; z < pageNodes.getLength(); z++ )
						{
							Node pageNode = pageNodes.item( z );
							if( "Part".equals( pageNode.getNodeName() ) )
							{
								chapterNode.removeChild( pageNode );
							}
						}
					}
					else if( "Chapter".equals( chapterNode.getNodeName() ) )
					{
						updateAllMediaPages( chapterNode );
					}
				}
			}
			else if( "Page".equals( node.getNodeName() ) )
			{
				NamedNodeMap pageAttr = node.getAttributes();
				Node pageNodeAttr = pageAttr.getNamedItem( "hashKey" );
				pageNodeAttr.setTextContent( "" );
				
				NodeList pageNodes = node.getChildNodes();
				for( int y = 0; y < pageNodes.getLength(); y++ )
				{
					Node pageNode = pageNodes.item( y );
					if( "Part".equals( pageNode.getNodeName() ) )
					{
						node.removeChild( pageNode );
					}
				}
			}
		}
	}

	private void updateAllMediaAttachments( Node mediaNode )
	{
		NodeList mediaNodeList = mediaNode.getChildNodes();
		for( int i = 0; i < mediaNodeList.getLength(); i++ )
		{
			Node node = mediaNodeList.item( i );
			if( "Chapter".equals( node.getNodeName() ) )
			{
				NodeList chapterNodes = node.getChildNodes();
				for( int y = 0; y < chapterNodes.getLength(); y++ )
				{
					Node chapterNode = chapterNodes.item( y );
					if( "Attachment".equals( chapterNode.getNodeName() ) )
					{
						NamedNodeMap attachmentAttr = chapterNode.getAttributes();
						Node attachmentNodeAttr = attachmentAttr.getNamedItem( "userName" );
						attachmentNodeAttr.setTextContent( userName );
					}
					else if( "Page".equals( chapterNode.getNodeName() ) )
					{
						NodeList pageNodes = chapterNode.getChildNodes();
						for( int x = 0; x < pageNodes.getLength(); x++ )
						{
							Node pageNode = pageNodes.item( x );
							if( "Attachment".equals( pageNode.getNodeName() ) )
							{
								NamedNodeMap attachmentAttr = pageNode.getAttributes();
								Node attachmentNodeAttr = attachmentAttr.getNamedItem( "userName" );
								attachmentNodeAttr.setTextContent( userName );
							}
						}
					}
					else if( "Chapter".equals( chapterNode.getNodeName() ) )
					{
						updateAllMediaAttachments( chapterNode );
					}
				}
			}
			
			//update all book page attachments
			else if( "Page".equals( node.getNodeName() ) )
			{
				NodeList pageNodes = node.getChildNodes();
				for( int y = 0; y < pageNodes.getLength(); y++ )
				{
					Node pageNode = pageNodes.item( y );
					if( "Attachment".equals( pageNode.getNodeName() ) )
					{
						NamedNodeMap attachmentAttr = pageNode.getAttributes();
						Node attachmentNodeAttr = attachmentAttr.getNamedItem( "userName" );
						attachmentNodeAttr.setTextContent( userName );
					}
				}
			}
			else if( "Attachment".equals( node.getNodeName() ) )
			{
				NamedNodeMap attachmentAttr = node.getAttributes();
				Node attachmentNode = attachmentAttr.getNamedItem( "userName" );
				attachmentNode.setTextContent( userName );
				
				NodeList attachmentNodes = node.getChildNodes();
				for( int z = 0; z < attachmentNodes.getLength(); z++ )
				{
					Node attachmentchildNode = attachmentNodes.item( z );
					if( "Attachment".equals( attachmentchildNode.getNodeName() ) )
					{
						NamedNodeMap subAttachmentAttr = attachmentchildNode.getAttributes();
						Node subAttachmentNode = subAttachmentAttr.getNamedItem( "userName" );
						subAttachmentNode.setTextContent( userName );
					}
				}
			}
		}
	}

	//writes Document to XML file
	private void writeXMLDocument(Document xmlDocument, File xmlFile)
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