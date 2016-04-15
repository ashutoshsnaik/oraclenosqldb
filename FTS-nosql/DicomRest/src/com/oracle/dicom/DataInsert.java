package com.oracle.dicom;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import oracle.kv.Consistency;
import oracle.kv.Direction;
import oracle.kv.Durability;
import oracle.kv.KVStore;
import oracle.kv.Key;
import oracle.kv.RequestTimeoutException;
import oracle.kv.table.PrimaryKey;
import oracle.kv.table.Row;
import oracle.kv.table.Table;
import oracle.kv.table.TableAPI;
import oracle.kv.table.TableIterator;
import oracle.kv.table.TableIteratorOptions;

/**
 * The DataInsert class is a utility class which will insert data into a KV
 * table.
 * 
 * @author Vishal Settipalli
 * @version 1.0
 * @since March 2016
 */
public class DataInsert {

    public Row row;
    public KVStore store;
    public Integer i = 0;

    private static String prefix = "dicom";
    private static String suffix = "dicomImage.lob";

    /**
     * insertWiki is the method which will insert data into KV table for the
     * given DicomMetaData.
     * 
     * @param Table
     *            dicomTable, TableAPI tableH, LinkedHashMap<String ,
     *            DicomMetadata> dicomMap, KVStore kvstore.
     * @return void
     */
    public void insertWikiData(Table dicomTable, TableAPI tableH,
	    LinkedHashMap<String, DicomMetadata> dicomMap, KVStore kvstore) {
	store = kvstore;
	Iterator<?> iterator = dicomMap.entrySet().iterator();
	i = getTableRowCount(dicomTable, tableH).intValue();
	System.out.println("Total Row Count :" + i);
	while (iterator.hasNext()) {
	    i++;
	    @SuppressWarnings("unchecked")
	    Entry<String, DicomMetadata> entry = (Entry<String, DicomMetadata>) iterator
		    .next();
	    String fileName = entry.getKey().toString();
	    DicomMetadata dicomMetaData = (DicomMetadata) entry.getValue();
	    // String uniqueId = getUniqueId();
	    String uniqueId = i.toString();

	    String key = loadBlob(uniqueId, fileName);

	    row = dicomTable.createRow();
	    row.put("id", i);
	    row.put("lobkey", key);
	    row.put("dicomFileName", fileName);
	    row.put("dicomMetadata", dicomMetaData.getDicomMetadata());
	    if (dicomMetaData.getSOPInstanceUID() != null) {
		row.put("SOPInstanceUID", dicomMetaData.getSOPInstanceUID());
	    } else {
		row.put("SOPInstanceUID", "");
	    }
	    if (dicomMetaData.getPatientID() != null) {
		row.put("PatientID", dicomMetaData.getPatientID());
	    } else {
		row.put("PatientID", "");
	    }
	    if (dicomMetaData.getStudyDescription() != null) {
		row.put("StudyDescription", dicomMetaData.getStudyDescription());
	    } else {
		row.put("StudyDescription", "");
	    }
	    if (dicomMetaData.getPatientName() != null) {
		row.put("PatientName", dicomMetaData.getPatientName());
	    } else {
		row.put("PatientName", "");
	    }
	    if (dicomMetaData.getImageType() != null) {
		row.put("ImageType", dicomMetaData.getImageType());
	    } else {
		row.put("ImageType", "");
	    }
	    if (dicomMetaData.getInstanceCreationDate() != null) {
		row.put("InstanceCreationDate",
			dicomMetaData.getInstanceCreationDate());
	    } else {
		row.put("InstanceCreationDate", "");
	    }
	    if (dicomMetaData.getInstanceCreationTime() != null) {
		row.put("InstanceCreationTime",
			dicomMetaData.getInstanceCreationTime());
	    } else {
		row.put("InstanceCreationTime", "");
	    }
	    if (dicomMetaData.getStudyDate() != null) {
		row.put("StudyDate", dicomMetaData.getStudyDate());
	    } else {
		row.put("StudyDate", "");
	    }
	    if (dicomMetaData.getAccessionNumber() != null) {
		row.put("AccessionNumber", dicomMetaData.getAccessionNumber());
	    } else {
		row.put("AccessionNumber", "");
	    }
	    if (dicomMetaData.getBitsAllocated() != null) {
		row.put("BitsAllocated", dicomMetaData.getBitsAllocated());
	    } else {
		row.put("BitsAllocated", "");
	    }
	    if (dicomMetaData.getBitsStored() != null) {
		row.put("BitsScored", dicomMetaData.getBitsStored());
	    } else {
		row.put("BitsScored", "");
	    }
	    if (dicomMetaData.getColumns() != null) {
		row.put("Columns", dicomMetaData.getColumns());
	    } else {
		row.put("Columns", "");
	    }
	    if (dicomMetaData.getFileMetaInformationGroupLength() != null) {
		row.put("FileMetaInformationGroupLength",
			dicomMetaData.getFileMetaInformationGroupLength());
	    } else {
		row.put("FileMetaInformationGroupLength", "");
	    }
	    if (dicomMetaData.getFileMetaInformationVersion() != null) {
		row.put("FileMetaInformationVersion",
			dicomMetaData.getFileMetaInformationVersion());
	    } else {
		row.put("FileMetaInformationVersion", "");
	    }
	    if (dicomMetaData.getFrameOfReferenceUID() != null) {
		row.put("FrameOfReferenceUID",
			dicomMetaData.getFrameOfReferenceUID());
	    } else {
		row.put("FrameOfReferenceUID", "");
	    }
	    if (dicomMetaData.getHighBit() != null) {
		row.put("HighBit", dicomMetaData.getHighBit());
	    } else {
		row.put("HighBit", "");
	    }
	    if (dicomMetaData.getImageOrientationPatient() != null) {
		row.put("ImageOrientationPatient",
			dicomMetaData.getImageOrientationPatient());
	    } else {
		row.put("ImageOrientationPatient", "");
	    }
	    if (dicomMetaData.getImagePositionPatient() != null) {
		row.put("ImagePositionPatient",
			dicomMetaData.getImagePositionPatient());
	    } else {
		row.put("ImagePositionPatient", "");
	    }
	    if (dicomMetaData.getImplementationClassUID() != null) {
		row.put("ImplementationClassUID",
			dicomMetaData.getImplementationClassUID());
	    } else {
		row.put("ImplementationClassUID", "");
	    }
	    if (dicomMetaData.getInstanceNumber() != null) {
		row.put("InstanceNumber", dicomMetaData.getInstanceNumber());
	    } else {
		row.put("InstanceNumber", "");
	    }
	    if (dicomMetaData.getInstitutionAddress() != null) {
		row.put("InstitutionAddress",
			dicomMetaData.getInstitutionAddress());
	    } else {
		row.put("InstitutionAddress", "");
	    }
	    if (dicomMetaData.getInstitutionName() != null) {
		row.put("InstitutionName", dicomMetaData.getInstitutionName());
	    } else {
		row.put("InstitutionName", "");
	    }
	    if (dicomMetaData.getManufacturer() != null) {
		row.put("Manufacturer", dicomMetaData.getManufacturer());
	    } else {
		row.put("Manufacturer", "");
	    }
	    if (dicomMetaData.getMediaStorageSOPClassUID() != null) {
		row.put("MediaStorageSOPClassUID",
			dicomMetaData.getMediaStorageSOPClassUID());
	    } else {
		row.put("MediaStorageSOPClassUID", "");
	    }
	    if (dicomMetaData.getMediaStorageSOPInstanceUID() != null) {
		row.put("MediaStorageSOPInstanceUID",
			dicomMetaData.getMediaStorageSOPInstanceUID());
	    } else {
		row.put("MediaStorageSOPInstanceUID", "");
	    }
	    if (dicomMetaData.getModality() != null) {
		row.put("Modality", dicomMetaData.getModality());
	    } else {
		row.put("Modality", "");
	    }
	    if (dicomMetaData.getPatientBirthDate() != null) {
		row.put("PatientBirthDate", dicomMetaData.getPatientBirthDate());
	    } else {
		row.put("PatientBirthDate", "");
	    }
	    if (dicomMetaData.getPatientBirthTime() != null) {
		row.put("PatientBirthTime", dicomMetaData.getPatientBirthTime());
	    } else {
		row.put("PatientBirthTime", "");
	    }
	    if (dicomMetaData.getPatientPosition() != null) {
		row.put("PatientPosition", dicomMetaData.getPatientPosition());
	    } else {
		row.put("PatientPosition", "");
	    }
	    if (dicomMetaData.getPatientSex() != null) {
		row.put("PatientSex", dicomMetaData.getPatientSex());
	    } else {
		row.put("PatientSex", "");
	    }
	    if (dicomMetaData.getPhotometricInterpretation() != null) {
		row.put("PhotometricInterpretation",
			dicomMetaData.getPhotometricInterpretation());
	    } else {
		row.put("PhotometricInterpretation", "");
	    }
	    if (dicomMetaData.getPixelData() != null) {
		row.put("PixelData", dicomMetaData.getPixelData());
	    } else {
		row.put("PixelData", "");
	    }
	    if (dicomMetaData.getPixelRepresentation() != null) {
		row.put("PixelRepresentation",
			dicomMetaData.getPixelRepresentation());
	    } else {
		row.put("PixelRepresentation", "");
	    }
	    if (dicomMetaData.getPixelSpacing() != null) {
		row.put("PixelSpacing", dicomMetaData.getPixelSpacing());
	    } else {
		row.put("PixelSpacing", "");
	    }
	    if (dicomMetaData.getPositionReferenceIndicator() != null) {
		row.put("PositionReferenceIndicator",
			dicomMetaData.getPositionReferenceIndicator());
	    } else {
		row.put("PositionReferenceIndicator", "");
	    }
	    if (dicomMetaData.getReferringPhysicianName() != null) {
		row.put("ReferringPhysicianName",
			dicomMetaData.getReferringPhysicianName());
	    } else {
		row.put("ReferringPhysicianName", "");
	    }
	    if (dicomMetaData.getRescaleIntercept() != null) {
		row.put("RescaleIntercept", dicomMetaData.getRescaleIntercept());
	    } else {
		row.put("RescaleIntercept", "");
	    }
	    if (dicomMetaData.getRescaleSlope() != null) {
		row.put("RescaleSlope", dicomMetaData.getRescaleSlope());
	    } else {
		row.put("RescaleSlope", "");
	    }
	    if (dicomMetaData.getRows() != null) {
		row.put("Rows", dicomMetaData.getRows());
	    } else {
		row.put("Rows", "");
	    }
	    if (dicomMetaData.getSamplesPerPixel() != null) {
		row.put("SamplesPerPixel", dicomMetaData.getSamplesPerPixel());
	    } else {
		row.put("SamplesPerPixel", "");
	    }
	    if (dicomMetaData.getSeriesDescription() != null) {
		row.put("SeriesDescription",
			dicomMetaData.getSeriesDescription());
	    } else {
		row.put("SeriesDescription", "");
	    }
	    if (dicomMetaData.getSeriesInstanceUID() != null) {
		row.put("SeriesInstanceUID",
			dicomMetaData.getSeriesInstanceUID());
	    } else {
		row.put("SeriesInstanceUID", "");
	    }
	    if (dicomMetaData.getSeriesNumber() != null) {
		row.put("SeriesNumber", dicomMetaData.getSeriesNumber());
	    } else {
		row.put("SeriesNumber", "");
	    }
	    if (dicomMetaData.getSliceThickness() != null) {
		row.put("SliceThickness", dicomMetaData.getSliceThickness());
	    } else {
		row.put("SliceThickness", "");
	    }
	    if (dicomMetaData.getSOPClassUID() != null) {
		row.put("SOPClassUID", dicomMetaData.getSOPClassUID());
	    } else {
		row.put("SOPClassUID", "");
	    }
	    if (dicomMetaData.getSourceApplicationEntityTitle() != null) {
		row.put("SourceApplicationEntityTitle",
			dicomMetaData.getSourceApplicationEntityTitle());
	    } else {
		row.put("SourceApplicationEntityTitle", "");
	    }
	    if (dicomMetaData.getStudyID() != null) {
		row.put("StudyID", dicomMetaData.getStudyID());
	    } else {
		row.put("StudyID", "");
	    }
	    if (dicomMetaData.getStudyInstanceUID() != null) {
		row.put("StudyInstanceUID", dicomMetaData.getStudyInstanceUID());
	    } else {
		row.put("StudyInstanceUID", "");
	    }
	    if (dicomMetaData.getStudyTime() != null) {
		row.put("StudyTime", dicomMetaData.getStudyTime());
	    } else {
		row.put("StudyTime", "");
	    }
	    // row.put("TransferSyntaxUID",
	    // dicomMetaData.getTransferSyntaxUID());
	    if (dicomMetaData.getAge() != null) {
		row.put("Age", dicomMetaData.getAge());
	    } else {
		row.put("Age", 0);
	    }
	    tableH.put(row, null, null);
	}
    }

    private Long getTableRowCount(Table tableH, TableAPI tableAPI) {
	PrimaryKey pkey = null;
	Long rowCount = new Long(0);
	TableIterator<Row> tIterator = null;
	TableIteratorOptions tIteratorOptions = null;

	try {
	    pkey = tableH.createPrimaryKey();
	    tIteratorOptions = new TableIteratorOptions(Direction.UNORDERED,
		    Consistency.NONE_REQUIRED, 30L, TimeUnit.SECONDS);
	    tIterator = tableAPI.tableIterator(pkey, null, tIteratorOptions);
	    while (tIterator.hasNext()) {
		rowCount++;
		tIterator.next();
	    }

	    tIterator.close();
	    return (rowCount);
	} catch (Throwable t) {
	    throw t;
	}
    }

    private String loadBlob(String uniqueId, String filename) {
	// Construct the key.

	final Key key = Key.createKey(Arrays.asList(prefix, uniqueId, filename,
		suffix));
	File lobFile = new File(filename);
	try {
	    FileInputStream fis = new FileInputStream(lobFile);

	    store.putLOB(key, fis, Durability.COMMIT_WRITE_NO_SYNC, 5,
		    TimeUnit.SECONDS);
	} catch (FileNotFoundException fnf) {
	    System.err.println("Input file not found.");
	    System.err.println("FileNotFoundException: " + fnf.toString());
	    fnf.printStackTrace();
	    System.exit(-1);
	} catch (RequestTimeoutException rte) {
	    System.err.println("A LOB chunk was either not read or");
	    System.err.println("not written in the alloted time.");
	    System.err.println("RequestTimeoutException: " + rte.toString());
	    rte.printStackTrace();
	    System.exit(-1);
	} catch (IOException e) {
	    System.err.println("IO Exception: " + e.toString());
	    e.printStackTrace();
	    System.exit(-1);
	}

	return key.toString();
    }
}
