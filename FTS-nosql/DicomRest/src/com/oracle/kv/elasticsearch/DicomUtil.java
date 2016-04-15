package com.oracle.kv.elasticsearch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;

import oracle.kv.Consistency;
import oracle.kv.KVStore;
import oracle.kv.KVStoreConfig;
import oracle.kv.KVStoreFactory;
import oracle.kv.Key;
import oracle.kv.lob.InputStreamVersion;
import oracle.kv.table.PrimaryKey;
import oracle.kv.table.ReadOptions;
import oracle.kv.table.Row;
import oracle.kv.table.Table;
import oracle.kv.table.TableAPI;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.json.JSONObject;

import com.oracle.dicom.DicomAttributes;
import com.oracle.kv.elasticsearch.FetchRecords.QueryTypes;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.display.ConsumerFormatImageMaker;

/**
 * The DicomUtil is a Utility Class.
 *
 * @author Vishal Settipalli
 * @version 1.0
 * @since March 2016
 */
public class DicomUtil {

    private DicomAttributes da;
    private List<DicomAttributes> listOfDa;
    private Set<Integer> keyAttributes = new HashSet<Integer>();
    private final String pKey = "_pkey";
    private static String prefix = "dicom";
    private static String suffix = "dicomImage.lob";
    @javax.ws.rs.core.Context
    public ServletContext context;

    private static TableAPI tableH = null;
    private static Table dicomTable = null;
    private static KVStoreConfig kvconfig;
    private static KVStore kvstore;
    private Row row;

    /**
     * parseSearch is the method which will parse the SearchHits returned as
     * Response from ElasticSearch and build the List of Dicom Attributes
     * Object. This method will also download the dcm file for the given Primary
     * Key from a Search hit and convert to Jpeg.
     * 
     * @param SearchHits
     *            hits, ServletContext context.
     * @return List<DicomAttributes>
     * @exception DicomException.
     * @see DicomException
     */
    @SuppressWarnings({ "unchecked", "deprecation" })
    public List<DicomAttributes> parseSearch(SearchHits hits,
	    ServletContext context) throws DicomException {
	Integer k = null;
	Properties prop = new Properties();
	InputStream input = null;
	listOfDa = new ArrayList<DicomAttributes>();

	for (SearchHit hit : hits) {
	    da = new DicomAttributes();
	    Map<String, Object> data = hit.getSource();
	    // System.out.println("Data from hit.getSource :"+data);
	    if (data.containsKey("ImageType")) {
		if (!data.get("ImageType").toString().equals(null)
			&& !data.get("ImageType").toString().equals("")) {
		    da.setImageType(data.get("ImageType").toString());
		} else {
		    da.setImageType("");
		}
	    }
	    if (data.containsKey("InstanceCreationDate")) {
		if (!data.get("InstanceCreationDate").toString().equals(null)
			&& !data.get("InstanceCreationDate").toString()
				.equals("")) {
		    da.setInstanceCreationDate(data.get("InstanceCreationDate")
			    .toString());
		} else {
		    da.setInstanceCreationDate("");
		}
	    }
	    if (data.containsKey("InstanceCreationTime")) {
		if (!data.get("InstanceCreationTime").toString().equals(null)
			&& !data.get("InstanceCreationTime").toString()
				.equals("")) {
		    da.setInstanceCreationTime(data.get("InstanceCreationTime")
			    .toString());
		} else {
		    da.setInstanceCreationTime("");
		}
	    }
	    if (data.containsKey("PatientID")) {
		if (!data.get("PatientID").toString().equals(null)
			&& !data.get("PatientID").toString().equals("")) {
		    da.setPatientID(data.get("PatientID").toString());
		} else {
		    da.setPatientID("");
		}
	    }
	    if (data.containsKey("PatientName")) {
		if (!data.get("PatientName").toString().equals(null)
			&& !data.get("PatientName").toString().equals("")) {
		    da.setPatientName(data.get("PatientName").toString());
		} else {
		    da.setPatientName("");
		}
	    }
	    if (data.containsKey(pKey)) {
		if (!data.get(pKey).toString().equals(null)
			&& !data.get(pKey).toString().equals("")) {
		    Map<String, String> pkey = new HashMap<String, String>();
		    pkey = (Map<String, String>) data.get(pKey);
		    k = Integer.parseInt(pkey.get("id"));
		    da.setPkey(k);
		} else {
		    da.setPkey(0);
		}
	    }
	    if (data.containsKey("SOPInstanceUID")) {
		if (!data.get("SOPInstanceUID").toString().equals(null)
			&& !data.get("SOPInstanceUID").toString().equals("")) {
		    da.setSOPInstanceUID(data.get("SOPInstanceUID").toString());
		} else {
		    da.setSOPInstanceUID("");
		}
	    }
	    if (data.containsKey("StudyDate")) {
		if (!data.get("StudyDate").toString().equals(null)
			&& !data.get("StudyDate").toString().equals("")) {
		    da.setStudyDate(data.get("StudyDate").toString());
		} else {
		    da.setStudyDate("");
		}
	    }
	    if (data.containsKey("StudyDescription")) {
		if (!data.get("StudyDescription").toString().equals(null)
			&& !data.get("StudyDescription").toString().equals("")) {
		    da.setStudyDescription(data.get("StudyDescription")
			    .toString());
		} else {
		    da.setStudyDescription("");
		}
	    }
	    listOfDa.add(da);
	    keyAttributes.add(k);
	}

	try {
	    input = context
		    .getResourceAsStream("/WEB-INF/DicomParser.properties");
	    prop.load(input);
	    // System.out.println("properties :"+prop.toString());
	    final String hosts = prop.getProperty("host");
	    final String store = prop.getProperty("store");
	    final String tableName = prop.getProperty("tablename");
	    OutputStream outputStream;

	    initTableHandlers(hosts, store, tableName);

	    // retrieve all the Dicom Images from KV
	    Iterator<?> itr = keyAttributes.iterator();
	    while (itr.hasNext()) {
		Integer dicomKey = (Integer) itr.next();
		PrimaryKey key = dicomTable.createPrimaryKey();
		key.put("id", dicomKey);

		row = tableH.get(key, new ReadOptions(null, 0, null));
		String dicomFileName = row.get("dicomFileName").asString()
			.toString();
		System.out.println("The file Name for id :" + dicomKey + "is :"
			+ dicomFileName);

		// Construct the key to retrieve Dicom File from KV
		final Key fileKey = Key.createKey(Arrays.asList(prefix,
			dicomKey.toString(), dicomFileName, suffix));

		System.out.println("The file Name for id :" + dicomKey + "is :"
			+ dicomFileName);

		InputStreamVersion istreamVersion = kvstore.getLOB(fileKey,
			Consistency.NONE_REQUIRED, 5, TimeUnit.SECONDS);
		InputStream stream = istreamVersion.getInputStream();

		String outputPath = context
			.getRealPath("/WEB-INF/DicomImages/");

		// write the inputStream to a FileOutputStream
		outputStream = new FileOutputStream(new File(outputPath + "/"
			+ dicomKey + ".dcm"));

		System.out.println("New File name :" + outputPath + "/"
			+ dicomKey);
		String dcmFileName = outputPath + "/" + dicomKey + ".dcm";
		String jpegFileName = outputPath + "/" + dicomKey + ".jpg";

		int read = 0;
		byte[] bytes = new byte[1024];

		while ((read = stream.read(bytes)) != -1) {
		    outputStream.write(bytes, 0, read);
		}

		ConsumerFormatImageMaker.convertFileToEightBitImage(
			dcmFileName, jpegFileName, "jpeg", 0);

		File f = new File(jpegFileName);
		File f1 = new File(dcmFileName);
		if (f.exists()) {
		    if (f1.exists())
			System.out.println("File exists");
		} else {
		    System.out.println("There is something wrong");
		}
		outputStream.close();
	    }
	} catch (IOException io) {
	    io.printStackTrace();
	}

	return listOfDa;
    }

    public Set<Integer> getKeyAttributes() {
	return keyAttributes;
    }

    /**
     * This method reads the the required configuration's to access KV
     * 
     * @param String
     *            hosts, String store, String tableName.
     * @return void
     */
    public static void initTableHandlers(String hosts, String store,
	    String tableName) {

	kvconfig = new KVStoreConfig(store, hosts);

	kvstore = KVStoreFactory.getStore(kvconfig);
	tableH = kvstore.getTableAPI();

	dicomTable = tableH.getTable(tableName);
    }

    /**
     * This method builds the Elasticsearch BoolQuery dynamically. The input to
     * this method should be a map of Query Parameters and their values and the
     * JSON representation of the Query String An Example Query String -
     * {"PatientName": { "Term": "Adam" }, "Age": { "Range": { "gte": "10",
     * "lte": "20" }}, "StudyDescription": {"Match": "Human"},"Modality":
     * {"Term": "CT"}}
     * 
     * @param Map
     *            of Query Parameters
     * @return void
     */
    public BoolQueryBuilder buildBoolQuery(Map<String, Object> qp,
	    JSONObject jobject) {
	Set<String> queryKeys = qp.keySet();
	Iterator<String> it = queryKeys.iterator();
	BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
	Map<String, Object> ageMap = new HashMap<String, Object>();

	while (it.hasNext()) {
	    String key = it.next();
	    if (!qp.get(key).equals("")) {
		if (key.equals("Age")) {
		    JSONObject ageJson = new JSONObject(qp.get(key).toString());
		    JSONObject ageQueryType = new JSONObject(ageJson.get(
			    "Range").toString());
		    System.out.println("Done taking ageJson and ageQueryType");
		    Iterator<?> ageValues = ageQueryType.keys();
		    System.out.println("ageQueryType Iterator :"
			    + ageValues.toString());
		    while (ageValues.hasNext()) {
			String agek = (String) ageValues.next();
			System.out.println("Age Key :" + agek);
			String ageValue = ageQueryType.getString(agek);
			System.out.println("Age Value :" + ageValue);
			ageMap.put(agek, ageValue);
		    }
		    System.out.println("ageMap :" + ageMap);
		    if (!ageMap.get("gte").equals("")
			    && !ageMap.get("lte").equals("")) {
			boolQuery.should(new RangeQueryBuilder("Age").gte(
				ageMap.get("gte")).lte(ageMap.get("lte")));
		    }
		} else {
		    JSONObject qType = new JSONObject(qp.get(key).toString());
		    Iterator<?> qKeys = qType.keys();
		    while (qKeys.hasNext()) {
			String qKey = (String) qKeys.next();
			System.out.println("Key :" + key + "value :"
				+ jobject.get(key));
			String value = qType.getString(qKey);

			QueryTypes qTypes = QueryTypes.valueOf(qKey);
			switch (qTypes) {
			case Match: {
			    System.out.println("Match Type Query :");
			    if (!value.equals("")) {
				boolQuery.should(new MatchQueryBuilder(key,
					value));
			    }
			    break;
			}
			case Term: {
			    if (!value.equals("")) {
				boolQuery.should(new TermQueryBuilder(key,
					value));
			    }
			    break;
			}
			case WildCard: {
			    if (!value.equals("")) {
				boolQuery.should(new WildcardQueryBuilder(key,
					value));
			    }
			    break;
			}
			}

		    }
		}
	    }
	}
	return boolQuery;
    }
}
