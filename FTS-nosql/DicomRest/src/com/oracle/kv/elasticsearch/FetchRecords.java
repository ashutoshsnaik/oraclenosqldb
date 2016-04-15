package com.oracle.kv.elasticsearch;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.SearchHits;
import org.json.JSONObject;

import com.oracle.dicom.DicomAttributes;

/**
 * The FetchRecords is a Rest Class.
 * 
 * @Path("/es")
 * @author Vishal Settipalli
 * @version 1.0
 * @since March 2016
 */
@Path("/es")
public class FetchRecords {

    @javax.ws.rs.core.Context
    public ServletContext context;

    public SearchResponse response;
    private List<DicomAttributes> listOfDa;

    private DicomUtil du = new DicomUtil();

    /**
     * fetchRecords is a method which will retrieve all indexed records from
     * ElasticSearch
     * 
     * @Path("/es/fetchRecords")
     * @return String - JSON Response
     * @exception IOException
     *                , JsonMappingExceptio
     * @see IOException, JsonMappingExceptio
     */
    @GET
    @Path("/fetchRecords")
    @Produces("application/json")
    public String fetchRecords() throws Exception, JsonMappingException,
	    IOException {

	Properties prop = new Properties();
	InputStream input = null;

	ObjectMapper mapper = new ObjectMapper();

	try {
	    input = context.getResourceAsStream("/WEB-INF/KVES.properties");
	    prop.load(input);
	    System.out.println("properties :" + prop.toString());
	    final String hosts = prop.getProperty("host");
	    final int transportPort = Integer.parseInt(prop
		    .getProperty("transportPort"));
	    final String store = prop.getProperty("storename");
	    final String tableName = prop.getProperty("tablename");
	    final String indexName = prop.getProperty("indexname");

	    TransportClient client = TransportClient.builder().build();

	    client.addTransportAddress(new InetSocketTransportAddress(
		    InetAddress.getByName(hosts), transportPort));

	    final String index = "ondb." + store.toLowerCase() + "."
		    + tableName + "." + indexName;
	    // System.out.println(index);
	    SearchRequestBuilder sb = client.prepareSearch(index);

	    response = sb.execute().actionGet();

	    SearchHits hits = response.getHits();

	    listOfDa = new ArrayList<DicomAttributes>();
	    listOfDa = du.parseSearch(hits, context);

	} catch (IOException io) {
	    io.printStackTrace();
	}

	return mapper.writeValueAsString(listOfDa);
    }

    public enum QueryTypes {
	Match, Term, WildCard
    }

    /**
     * buildQuery is a method which will retrieve indexed records for attributes
     * and values with a defined Query Type supported query types are -
     * match,term, wild card and range
     * 
     * @Path("/es/buildQuery")
     * @param JSON
     *            representation of Query String.
     * @return String - JSON Response
     * @exception IOException
     *                , JsonMappingExceptio
     * @see DicomUtil
     */
    @POST
    @Path("/buildQuery")
    @Consumes({ "application/json" })
    @Produces("application/json")
    public String buildQuery(String query) throws Exception,
	    JsonMappingException, IOException {
	Properties prop = new Properties();
	InputStream input = null;

	System.out.println("Query Parameters :" + query.toString());

	ObjectMapper mapper = new ObjectMapper();

	try {
	    input = context.getResourceAsStream("/WEB-INF/KVES.properties");
	    prop.load(input);
	    System.out.println("properties :" + prop.toString());
	    final String hosts = prop.getProperty("host");
	    final int transportPort = Integer.parseInt(prop
		    .getProperty("transportPort"));
	    final String store = prop.getProperty("storename");
	    final String tableName = prop.getProperty("tablename");
	    final String indexName = prop.getProperty("indexname");

	    JSONObject jobject = new JSONObject(query);
	    Iterator<?> ks = jobject.keys();

	    Map<String, Object> qp = new HashMap<String, Object>();
	    ;

	    while (ks.hasNext()) {
		String key = (String) ks.next();
		System.out
			.println("Key :" + key + "value :" + jobject.get(key));
		Object value = jobject.get(key);
		qp.put(key, value);
	    }
	    System.out.println("Map :" + qp);

	    TransportClient client = TransportClient.builder().build();

	    client.addTransportAddress(new InetSocketTransportAddress(
		    InetAddress.getByName(hosts), transportPort));

	    final String index = "ondb." + store.toLowerCase() + "."
		    + tableName + "." + indexName;

	    BoolQueryBuilder boolQuery = du.buildBoolQuery(qp, jobject);
	    System.out.println("qb :" + boolQuery.toString());

	    response = client.prepareSearch(index).setQuery(boolQuery)
		    .execute().get();

	    SearchHits hits = response.getHits();

	    listOfDa = new ArrayList<DicomAttributes>();
	    listOfDa = du.parseSearch(hits, context);

	} catch (IOException io) {
	    io.printStackTrace();
	}

	return mapper.writeValueAsString(listOfDa);
    }

    /**
     * getRecords is a method which will retrieve indexed records for the given
     * valid ES Query
     * 
     * @Path("/es/getQuery")
     * @param JSON
     *            representation of valid of Query String.
     * @return String - JSON Response
     * @exception IOException
     *                , JsonMappingExceptio
     * @see DicomUtil
     */
    @POST
    @Path("/getRecords")
    @Consumes({ "application/json" })
    @Produces("application/json")
    public String getRecords(String q) throws Exception, JsonMappingException,
	    IOException {

	Properties prop = new Properties();
	InputStream input = null;

	ObjectMapper mapper = new ObjectMapper();

	listOfDa = new ArrayList<DicomAttributes>();

	try {
	    input = context.getResourceAsStream("/WEB-INF/KVES.properties");
	    prop.load(input);
	    // System.out.println("properties :"+prop.toString());
	    final String hosts = prop.getProperty("host");
	    final int transportPort = Integer.parseInt(prop
		    .getProperty("transportPort"));
	    final String store = prop.getProperty("storename");
	    final String tableName = prop.getProperty("tablename");
	    final String indexName = prop.getProperty("indexname");

	    TransportClient client = TransportClient.builder().build();

	    client.addTransportAddress(new InetSocketTransportAddress(
		    InetAddress.getByName(hosts), transportPort));

	    final String index = "ondb." + store.toLowerCase() + "."
		    + tableName + "." + indexName;
	    JSONObject queryStringObject = new JSONObject(q);
	    SearchResponse response = client.prepareSearch(index)
		    .setSource(queryStringObject.toString()).execute()
		    .actionGet();
	    System.out.println(response);

	    SearchHits hits = response.getHits();

	    listOfDa = du.parseSearch(hits, context);

	} catch (IOException io) {
	    io.printStackTrace();
	}

	return mapper.writeValueAsString(listOfDa);
    }

    /**
     * listIndexes is a method which will list all indexes for the Cluster
     * defined in the WEB-INF/KVES.properties.
     * 
     * @Path("/es/listIndexes")
     * @return String - JSON Response
     */
    @GET
    @Path("/listIndexes")
    public Response listIndexes() {

	Properties prop = new Properties();
	InputStream input = null;
	String[] listOfIndexes = new String[0];

	try {
	    input = context.getResourceAsStream("/WEB-INF/KVES.properties");
	    prop.load(input);
	    System.out.println("properties :" + prop.toString());
	    final String hosts = prop.getProperty("host");
	    final int transportPort = Integer.parseInt(prop
		    .getProperty("transportPort"));
	    final String store = prop.getProperty("storename");
	    final String tableName = prop.getProperty("tablename");
	    final String indexName = prop.getProperty("indexname");

	    TransportClient client = TransportClient.builder().build();

	    client.addTransportAddress(new InetSocketTransportAddress(
		    InetAddress.getByName(hosts), transportPort));

	    final String index = "ondb." + store.toLowerCase() + "."
		    + tableName + "." + indexName;
	    System.out.println(index);
	    listOfIndexes = client.admin().indices()
		    .getIndex(new GetIndexRequest()).actionGet().getIndices();
	    System.out.println(Arrays.toString(listOfIndexes));

	} catch (IOException io) {
	    io.printStackTrace();
	}

	return Response
		.status(200)
		.entity("Search Complete, No Of Indexes are :"
			+ Arrays.toString(listOfIndexes)).build();
    }

    /**
     * listAttributes is a method which will retrieve all the attributes for a
     * given Index specified in WEB-INF/KVES.properties
     *
     * @Path("/es/listAttributes")
     * @return String - JSON Response
     * @exception IOException
     *                , JsonMappingExceptio
     * @see IOException, JsonMappingExceptio
     */
    @GET
    @Path("/listAttributes")
    @Produces("application/json")
    public String listAttributes() throws Exception, JsonMappingException,
	    IOException {

	Properties prop = new Properties();
	InputStream input = null;
	List<String> propertyNames = new ArrayList<String>();
	ObjectMapper mapper = new ObjectMapper();

	try {
	    input = context.getResourceAsStream("/WEB-INF/KVES.properties");
	    prop.load(input);
	    System.out.println("properties :" + prop.toString());
	    final String hosts = prop.getProperty("host");
	    final int transportPort = Integer.parseInt(prop
		    .getProperty("transportPort"));
	    final String store = prop.getProperty("storename");
	    final String tableName = prop.getProperty("tablename");
	    final String indexName = prop.getProperty("indexname");

	    TransportClient client = TransportClient.builder().build();

	    client.addTransportAddress(new InetSocketTransportAddress(
		    InetAddress.getByName(hosts), transportPort));

	    final String index = "ondb." + store.toLowerCase() + "."
		    + tableName + "." + indexName;
	    System.out.println(index);

	    GetMappingsResponse res = client.admin().indices()
		    .getMappings(new GetMappingsRequest().indices(index)).get();
	    ImmutableOpenMap<String, MappingMetaData> mapping = res.mappings()
		    .get(index);
	    for (Iterator<String> iterator = mapping.keysIt(); iterator
		    .hasNext();) {
		String typeName = iterator.next();
		MappingMetaData mappingMetadata = mapping.get(typeName);

		// Get mapping content for the type
		Map<String, Object> source = mappingMetadata.sourceAsMap();
		@SuppressWarnings("unchecked")
		Map<String, Object> properties = (Map<String, Object>) source
			.get("properties");
		System.out.println("properties :" + properties.size()
			+ " are :" + properties.toString());
		propertyNames = handleTypeMappingProperties(properties);
		System.out.println(mapper.writeValueAsString(propertyNames));
	    }
	} catch (IOException io) {
	    io.printStackTrace();
	} catch (Exception e) {
	    e.printStackTrace();
	}

	return mapper.writeValueAsString(propertyNames);
    }

    private List<String> handleTypeMappingProperties(
	    Map<String, Object> properties) {
	if (properties == null || properties.isEmpty()) {
	    return new ArrayList<String>();
	}
	System.out.println("handle individual Properties :"
		+ properties.keySet());
	List<String> propertyNames = new ArrayList<String>();
	for (String propertyName : properties.keySet()) {
	    if (!propertyName.equals("_pkey"))
		propertyNames.add(propertyName);
	}
	return propertyNames;
    }

    /**
     * getImageFile returns the Base64 encoded representation of an Image file
     * 
     * @Path("/es/getImageFile/{id ")
     * @param String
     *            - id of the Image File
     * @return Base64 encoded image representation
     */
    @GET
    @Path("/getImageFile/{id}")
    @Produces({ "image/jpg" })
    public Response getImageFile(@PathParam("id") String id) {

	final String keyId = id;
	StreamingOutput fileStream = new StreamingOutput() {
	    @Override
	    public void write(OutputStream outputStream) throws IOException,
		    WebApplicationException {
		System.out.println("Image File Name :" + keyId);
		// set file (and path) to be download
		String outputPath = context
			.getRealPath("/WEB-INF/DicomImages/");

		java.nio.file.Path path = Paths
			.get(outputPath + keyId + ".jpg");
		System.out.println("File exists :" + Files.exists(path));
		byte[] data = Files.readAllBytes(path);
		outputStream.write(data);
		outputStream.flush();

		outputStream.flush();
		outputStream.close();
		// inputStream.close();
	    }
	};
	return Response
		.ok(fileStream, MediaType.APPLICATION_OCTET_STREAM)
		.header("content-disposition",
			"attachment; filename=" + keyId + ".jpg").build();
    }

    /**
     * downloadImageFile returns the Jpeg Image
     * 
     * @Path("/es/getImage/{id ")
     * @param String
     *            - id of the Image File
     * @return Image File
     */
    @GET
    @Path("/getImage/{id}")
    @Produces({ "image/jpg" })
    public Response downloadImageFile(@PathParam("id") String id) {
	System.out.println("Image File Name :" + id);
	// set file (and path) to be download
	String outputPath = context.getRealPath("/WEB-INF/DicomImages/");
	File file = new File(outputPath + id + ".jpg");
	// System.out.println("file exists :"+file.exists()+"Where is the file :"+outputPath+id+".jpg");
	ResponseBuilder responseBuilder = Response.ok((Object) file);
	responseBuilder.header("Content-Disposition", "attachment; filename="
		+ id + ".jpg");
	return responseBuilder.build();
    }

    /**
     * downloadDcmFile returns the Dicom Image
     * 
     * @Path("/es/getDcm/{id ")
     * @param String
     *            - id of the Dicom File
     * @return Dicom File
     */
    @GET
    @Path("/getDcm/{id}")
    @Produces({ "image/dcm" })
    public Response downloadDcmFile(@PathParam("id") String id) {
	System.out.println("Image File Name :" + id);
	// set file (and path) to be download
	String outputPath = context.getRealPath("/WEB-INF/DicomImages/");
	File file = new File(outputPath + id + ".jpg");
	// System.out.println("file exists :"+file.exists()+"Where is the file :"+outputPath+id+".dcm");
	ResponseBuilder responseBuilder = Response.ok((Object) file);
	responseBuilder.header("Content-Disposition", "attachment; filename="
		+ id + ".dcm");
	return responseBuilder.build();
    }

    /**
     * getImage returns the Jpeg Image Path of the File
     * 
     * @Path("/es/getJpeg/{id ")
     * @param String
     *            - id of the Image File
     * @return String - Path of the Image File
     */
    @GET
    @Path("/getJpeg/{id}")
    @Produces({ "application/json" })
    public String getImage(@PathParam("id") String id)
	    throws JsonGenerationException, JsonMappingException, IOException {
	System.out.println("Image File Name :" + id);
	// set file (and path) to be download
	String outputPath = context.getRealPath("/WEB-INF/DicomImages/");

	String imageString = null;
	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	BufferedImage img = ImageIO.read(new File(outputPath + id + ".jpg"));
	try {
	    ImageIO.write(img, "jpg", bos);
	    byte[] imageBytes = bos.toByteArray();

	    imageString = Base64.encodeBase64String(imageBytes);

	    bos.close();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	ObjectMapper mapper = new ObjectMapper();
	System.out.println("Base 64 image string :" + imageString);
	return mapper.writeValueAsString(imageString);
    }
}
