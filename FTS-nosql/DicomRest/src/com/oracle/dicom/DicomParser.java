package com.oracle.dicom;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import oracle.kv.KVStore;
import oracle.kv.KVStoreConfig;
import oracle.kv.KVStoreFactory;
import oracle.kv.table.Table;
import oracle.kv.table.TableAPI;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.TagFromName;

/**
 * The DicomParser is a Rest Class.
 * 
 * @Path("/dicom")
 * @author Vishal Settipalli
 * @version 1.0
 * @since March 2016
 */
@Path("/dicom")
public class DicomParser {

    private static AttributeList list;

    @javax.ws.rs.core.Context
    public ServletContext context;

    private static LinkedHashMap<String, DicomMetadata> dicomMap;
    private static TableAPI tableH = null;
    private static Table dicomTable = null;
    private static DicomMetadata dicomMetaData = null;
    public static DataInsert dataInsert = new DataInsert();
    private static KVStoreConfig kvconfig;
    private static KVStore kvstore;
    private File[] listOfFiles;
    private File direc;

    /**
     * load is a method which will list the number of file in a given directory
     * specified in /WEB-INF/DicomParser.properties
     * 
     * @Path("/es/load")
     * @return String - JSON Response
     * @exception IOException
     * @see IOException
     */
    @GET
    @Path("/load")
    @Produces("application/json")
    public String load() {

	Properties prop = new Properties();
	InputStream input = null;
	String imageDetails = "";

	try {
	    input = context
		    .getResourceAsStream("/WEB-INF/DicomParser.properties");
	    prop.load(input);
	    System.out.println("properties :" + prop.toString());

	    final String directory = prop.getProperty("directory");

	    direc = new File(directory);
	    System.out.println("Directory Name :" + direc.getName());

	    // initTableHandlers(hosts, store, tableName);
	    listOfFiles = new File[0];
	    if (direc.isDirectory()) {
		listOfFiles = direc.listFiles();
	    } else {
		System.out
			.println("Please specify a Directory of Dicom Images");
		System.exit(0);
	    }

	    System.out.println("Number of Dicom Files :" + listOfFiles.length);
	    imageDetails = "{\"no\":" + listOfFiles.length + ",\"name\":\""
		    + directory + "\"}";
	    System.out.println("details :" + imageDetails);

	} catch (IOException io) {
	    io.printStackTrace();
	}
	return imageDetails;
    }

    /**
     * displayTags is a method which will retrieve all the Dicom Attributes and
     * insert them into KV table - dicom
     * 
     * @Path("/es/loadDicom")
     * @return void/Success
     * @exception IOException
     *                , Exception
     * @see IOException, Exception
     */
    @GET
    @Path("/loadDicom")
    public void displayTags() {

	Properties prop = new Properties();
	InputStream input = null;

	try {
	    input = context
		    .getResourceAsStream("/WEB-INF/DicomParser.properties");
	    prop.load(input);
	    System.out.println("properties :" + prop.toString());
	    final String hosts = prop.getProperty("host");
	    final String store = prop.getProperty("store");
	    final String tableName = prop.getProperty("tablename");
	    final String directory = prop.getProperty("directory");

	    direc = new File(directory);

	    initTableHandlers(hosts, store, tableName);
	    listOfFiles = new File[0];
	    if (direc.isDirectory()) {
		listOfFiles = direc.listFiles();
	    } else {
		System.out
			.println("Please specify a Directory of Dicom Images");
		System.exit(0);
	    }

	} catch (IOException io) {
	    io.printStackTrace();
	}

	// if (listOfFiles.length > 0){
	for (File file : listOfFiles) {
	    list = new AttributeList();
	    dicomMetaData = new DicomMetadata();
	    dicomMap = new LinkedHashMap<String, DicomMetadata>();
	    String filename = file.getName();
	    String extension = filename.substring(
		    filename.lastIndexOf(".") + 1, filename.length());
	    String dicomExtn = "dcm";
	    if (!extension.equals(dicomExtn)) {
		System.out
			.println("Please ensure only Dicom images with .dcm extension are present in the Directory");
		System.exit(0);
	    }

	    System.out.println("File Read :" + file.getName()
		    + "======================================Started");
	    try {
		list.read(file);
		dicomMetaData = getDicomMetaData(list);
		dicomMap.put(direc + "/" + filename, dicomMetaData);
		System.out.println("File Read :" + file.getName()
			+ "======================================Ended");
		dataInsert
			.insertWikiData(dicomTable, tableH, dicomMap, kvstore);
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
    }

    private static String getTagInformation(AttributeTag attrTag) {
	return Attribute.getDelimitedStringValuesOrEmptyString(list, attrTag);
    }

    private static DicomMetadata getDicomMetaData(AttributeList l)
	    throws ParseException {

	DicomMetadata dmd = new DicomMetadata();
	dmd.setDicomMetadata(l.toString());
	if (!getTagInformation(TagFromName.ImageType).equals(null)
		&& !getTagInformation(TagFromName.ImageType).equals("")) {
	    dmd.setImageType(getTagInformation(TagFromName.ImageType));
	}
	if (!getTagInformation(TagFromName.InstanceCreationDate).equals(null)
		&& !getTagInformation(TagFromName.InstanceCreationDate).equals(
			"")) {
	    dmd.setInstanceCreationDate(getTagInformation(TagFromName.InstanceCreationDate));
	}
	if (!getTagInformation(TagFromName.InstanceCreationTime).equals(null)
		&& !getTagInformation(TagFromName.InstanceCreationTime).equals(
			"")) {
	    dmd.setInstanceCreationTime(getTagInformation(TagFromName.InstanceCreationTime));
	}
	if (!getTagInformation(TagFromName.PatientID).equals(null)
		&& !getTagInformation(TagFromName.PatientID).equals("")) {
	    dmd.setPatientID(getTagInformation(TagFromName.PatientID));
	}
	if (!getTagInformation(TagFromName.PatientName).equals(null)
		&& !getTagInformation(TagFromName.PatientName).equals("")) {
	    dmd.setPatientName(getTagInformation(TagFromName.PatientName));
	}
	if (!getTagInformation(TagFromName.SOPInstanceUID).equals(null)
		&& !getTagInformation(TagFromName.SOPInstanceUID).equals("")) {
	    dmd.setSOPInstanceUID(getTagInformation(TagFromName.SOPInstanceUID));
	}
	if (!getTagInformation(TagFromName.StudyDate).equals(null)
		&& !getTagInformation(TagFromName.StudyDate).equals("")) {
	    dmd.setStudyDate(getTagInformation(TagFromName.StudyDate));
	}
	if (!getTagInformation(TagFromName.StudyDescription).equals(null)
		&& !getTagInformation(TagFromName.StudyDescription).equals("")) {
	    dmd.setStudyDescription(getTagInformation(TagFromName.StudyDescription));
	}
	if (!getTagInformation(TagFromName.AccessionNumber).equals(null)
		&& !getTagInformation(TagFromName.AccessionNumber).equals("")) {
	    dmd.setAccessionNumber(getTagInformation(TagFromName.AccessionNumber));
	}
	if (!getTagInformation(TagFromName.BitsAllocated).equals(null)
		&& !getTagInformation(TagFromName.BitsAllocated).equals("")) {
	    dmd.setBitsAllocated(getTagInformation(TagFromName.BitsAllocated));
	}
	if (!getTagInformation(TagFromName.BitsStored).equals(null)
		&& !getTagInformation(TagFromName.BitsStored).equals("")) {
	    dmd.setBitsStored(getTagInformation(TagFromName.BitsStored));
	}
	if (!getTagInformation(TagFromName.Columns).equals(null)
		&& !getTagInformation(TagFromName.Columns).equals("")) {
	    dmd.setColumns(getTagInformation(TagFromName.Columns));
	}
	if (!getTagInformation(TagFromName.FileMetaInformationGroupLength)
		.equals(null)
		&& !getTagInformation(
			TagFromName.FileMetaInformationGroupLength).equals("")) {
	    dmd.setFileMetaInformationGroupLength(getTagInformation(TagFromName.FileMetaInformationGroupLength));
	}
	if (!getTagInformation(TagFromName.FileMetaInformationVersion).equals(
		null)
		&& !getTagInformation(TagFromName.FileMetaInformationVersion)
			.equals("")) {
	    dmd.setFileMetaInformationVersion(getTagInformation(TagFromName.FileMetaInformationVersion));
	}
	if (!getTagInformation(TagFromName.FrameOfReferenceUID).equals(null)
		&& !getTagInformation(TagFromName.FrameOfReferenceUID).equals(
			"")) {
	    dmd.setFrameOfReferenceUID(getTagInformation(TagFromName.FrameOfReferenceUID));
	}
	if (!getTagInformation(TagFromName.HighBit).equals(null)
		&& !getTagInformation(TagFromName.HighBit).equals("")) {
	    dmd.setHighBit(getTagInformation(TagFromName.HighBit));
	}
	if (!getTagInformation(TagFromName.ImageOrientationPatient)
		.equals(null)
		&& !getTagInformation(TagFromName.ImageOrientationPatient)
			.equals("")) {
	    dmd.setImageOrientationPatient(getTagInformation(TagFromName.ImageOrientationPatient));
	}
	if (!getTagInformation(TagFromName.ImagePositionPatient).equals(null)
		&& !getTagInformation(TagFromName.ImagePositionPatient).equals(
			"")) {
	    dmd.setImagePositionPatient(getTagInformation(TagFromName.ImagePositionPatient));
	}
	if (!getTagInformation(TagFromName.ImplementationClassUID).equals(null)
		&& !getTagInformation(TagFromName.ImplementationClassUID)
			.equals("")) {
	    dmd.setImplementationClassUID(getTagInformation(TagFromName.ImplementationClassUID));
	}
	if (!getTagInformation(TagFromName.InstanceNumber).equals(null)
		&& !getTagInformation(TagFromName.InstanceNumber).equals("")) {
	    dmd.setInstanceNumber(getTagInformation(TagFromName.InstanceNumber));
	}
	if (!getTagInformation(TagFromName.InstitutionAddress).equals(null)
		&& !getTagInformation(TagFromName.InstitutionAddress)
			.equals("")) {
	    dmd.setInstitutionAddress(getTagInformation(TagFromName.InstitutionAddress));
	}
	if (!getTagInformation(TagFromName.InstitutionName).equals(null)
		&& !getTagInformation(TagFromName.InstitutionName).equals("")) {
	    dmd.setInstitutionName(getTagInformation(TagFromName.InstitutionName));
	}
	if (!getTagInformation(TagFromName.Manufacturer).equals(null)
		&& !getTagInformation(TagFromName.Manufacturer).equals("")) {
	    dmd.setManufacturer(getTagInformation(TagFromName.Manufacturer));
	}
	if (!getTagInformation(TagFromName.MediaStorageSOPClassUID)
		.equals(null)
		&& !getTagInformation(TagFromName.MediaStorageSOPClassUID)
			.equals("")) {
	    dmd.setMediaStorageSOPClassUID(getTagInformation(TagFromName.MediaStorageSOPClassUID));
	}
	if (!getTagInformation(TagFromName.MediaStorageSOPInstanceUID).equals(
		null)
		&& !getTagInformation(TagFromName.MediaStorageSOPInstanceUID)
			.equals("")) {
	    dmd.setMediaStorageSOPInstanceUID(getTagInformation(TagFromName.MediaStorageSOPInstanceUID));
	}
	if (!getTagInformation(TagFromName.Modality).equals(null)
		&& !getTagInformation(TagFromName.Modality).equals("")) {
	    dmd.setModality(getTagInformation(TagFromName.Modality));
	}
	if (!getTagInformation(TagFromName.PatientBirthDate).equals(null)
		&& !getTagInformation(TagFromName.PatientBirthDate).equals("")) {
	    dmd.setPatientBirthDate(getTagInformation(TagFromName.PatientBirthDate));
	}
	if (!getTagInformation(TagFromName.PatientBirthTime).equals(null)
		&& !getTagInformation(TagFromName.PatientBirthTime).equals("")) {
	    dmd.setPatientBirthTime(getTagInformation(TagFromName.PatientBirthTime));
	}
	if (!getTagInformation(TagFromName.PatientPosition).equals(null)
		&& !getTagInformation(TagFromName.PatientPosition).equals("")) {
	    dmd.setPatientPosition(getTagInformation(TagFromName.PatientPosition));
	}
	if (!getTagInformation(TagFromName.PatientSex).equals(null)
		&& !getTagInformation(TagFromName.PatientSex).equals("")) {
	    dmd.setPatientSex(getTagInformation(TagFromName.PatientSex));
	}
	if (!getTagInformation(TagFromName.PhotometricInterpretation).equals(
		null)
		&& !getTagInformation(TagFromName.PhotometricInterpretation)
			.equals("")) {
	    dmd.setPhotometricInterpretation(getTagInformation(TagFromName.PhotometricInterpretation));
	}
	if (!getTagInformation(TagFromName.PixelData).equals(null)
		&& !getTagInformation(TagFromName.PixelData).equals("")) {
	    dmd.setPixelData(getTagInformation(TagFromName.PixelData));
	}
	if (!getTagInformation(TagFromName.PixelRepresentation).equals(null)
		&& !getTagInformation(TagFromName.PixelRepresentation).equals(
			"")) {
	    dmd.setPixelRepresentation(getTagInformation(TagFromName.PixelRepresentation));
	}
	if (!getTagInformation(TagFromName.PixelSpacing).equals(null)
		&& !getTagInformation(TagFromName.PixelSpacing).equals("")) {
	    dmd.setPixelSpacing(getTagInformation(TagFromName.PixelSpacing));
	}
	if (!getTagInformation(TagFromName.PositionReferenceIndicator).equals(
		null)
		&& !getTagInformation(TagFromName.PositionReferenceIndicator)
			.equals("")) {
	    dmd.setPositionReferenceIndicator(getTagInformation(TagFromName.PositionReferenceIndicator));
	}
	if (!getTagInformation(TagFromName.ReferringPhysicianName).equals(null)
		&& !getTagInformation(TagFromName.ReferringPhysicianName)
			.equals("")) {
	    dmd.setReferringPhysicianName(getTagInformation(TagFromName.ReferringPhysicianName));
	}
	if (!getTagInformation(TagFromName.RescaleIntercept).equals(null)
		&& !getTagInformation(TagFromName.RescaleIntercept).equals("")) {
	    dmd.setRescaleIntercept(getTagInformation(TagFromName.RescaleIntercept));
	}
	if (!getTagInformation(TagFromName.RescaleSlope).equals(null)
		&& !getTagInformation(TagFromName.RescaleSlope).equals("")) {
	    dmd.setRescaleSlope(getTagInformation(TagFromName.RescaleSlope));
	}
	if (!getTagInformation(TagFromName.Rows).equals(null)
		&& !getTagInformation(TagFromName.Rows).equals("")) {
	    dmd.setRows(getTagInformation(TagFromName.Rows));
	}
	if (!getTagInformation(TagFromName.SamplesPerPixel).equals(null)
		&& !getTagInformation(TagFromName.SamplesPerPixel).equals("")) {
	    dmd.setSamplesPerPixel(getTagInformation(TagFromName.SamplesPerPixel));
	}
	if (!getTagInformation(TagFromName.SeriesDescription).equals(null)
		&& !getTagInformation(TagFromName.SeriesDescription).equals("")) {
	    dmd.setSeriesDescription(getTagInformation(TagFromName.SeriesDescription));
	}
	if (!getTagInformation(TagFromName.SeriesInstanceUID).equals(null)
		&& !getTagInformation(TagFromName.SeriesInstanceUID).equals("")) {
	    dmd.setSeriesInstanceUID(getTagInformation(TagFromName.SeriesInstanceUID));
	}
	if (!getTagInformation(TagFromName.SeriesNumber).equals(null)
		&& !getTagInformation(TagFromName.SeriesNumber).equals("")) {
	    dmd.setSeriesNumber(getTagInformation(TagFromName.SeriesNumber));
	}
	if (!getTagInformation(TagFromName.SliceThickness).equals(null)
		&& !getTagInformation(TagFromName.SliceThickness).equals("")) {
	    dmd.setSliceThickness(getTagInformation(TagFromName.SliceThickness));
	}
	if (!getTagInformation(TagFromName.SOPClassUID).equals(null)
		&& !getTagInformation(TagFromName.SOPClassUID).equals("")) {
	    dmd.setSOPClassUID(getTagInformation(TagFromName.SOPClassUID));
	}
	if (!getTagInformation(TagFromName.SourceApplicationEntityTitle)
		.equals(null)
		&& !getTagInformation(TagFromName.SourceApplicationEntityTitle)
			.equals("")) {
	    dmd.setSourceApplicationEntityTitle(getTagInformation(TagFromName.SourceApplicationEntityTitle));
	}
	if (!getTagInformation(TagFromName.StudyID).equals(null)
		&& !getTagInformation(TagFromName.StudyID).equals("")) {
	    dmd.setStudyID(getTagInformation(TagFromName.StudyID));
	}
	if (!getTagInformation(TagFromName.StudyInstanceUID).equals(null)
		&& !getTagInformation(TagFromName.StudyInstanceUID).equals("")) {
	    dmd.setStudyInstanceUID(getTagInformation(TagFromName.StudyInstanceUID));
	}
	if (!getTagInformation(TagFromName.StudyTime).equals(null)
		&& !getTagInformation(TagFromName.StudyTime).equals("")) {
	    dmd.setStudyTime(getTagInformation(TagFromName.StudyTime));
	}
	if (!getTagInformation(TagFromName.TransferSyntaxUID).equals(null)
		&& !getTagInformation(TagFromName.TransferSyntaxUID).equals("")) {
	    dmd.setTransferSyntaxUID(getTagInformation(TagFromName.TransferSyntaxUID));
	}
	SimpleDateFormat formatter = new SimpleDateFormat("yyyymmdd");
	if (dmd.getPatientBirthDate() == null) {
	    dmd.setAge(0);
	} else {
	    dmd.setAge((getAge(formatter.parse(dmd.getPatientBirthDate()))));
	}

	return dmd;
    }

    public static int getAge(Date birthday) {
	GregorianCalendar today = new GregorianCalendar();
	GregorianCalendar bday = new GregorianCalendar();
	GregorianCalendar bdayThisYear = new GregorianCalendar();

	bday.setTime(birthday);
	bdayThisYear.setTime(birthday);
	bdayThisYear.set(Calendar.YEAR, today.get(Calendar.YEAR));

	int age = today.get(Calendar.YEAR) - bday.get(Calendar.YEAR);

	if (today.getTimeInMillis() < bdayThisYear.getTimeInMillis()) {
	    age--;
	}
	System.out.println("Calculated Age :" + age);
	return age;
    }

    public static void initTableHandlers(String hosts, String store,
	    String tableName) {

	kvconfig = new KVStoreConfig(store, hosts);

	kvstore = KVStoreFactory.getStore(kvconfig);
	tableH = kvstore.getTableAPI();

	dicomTable = tableH.getTable(tableName);
    }
}
