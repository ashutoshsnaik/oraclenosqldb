package com.oracle.dicom;

import java.util.HashMap;
import java.util.Map;

public class DicomAttributes {

    private Integer pkey;
    private String SOPInstanceUID;
    private String PatientID;
    private String StudyDescription;
    private String PatientName;
    private String ImageType;
    private String InstanceCreationDate;
    private String InstanceCreationTime;
    private String StudyDate;

    public String getSOPInstanceUID() {
	return SOPInstanceUID;
    }

    public void setSOPInstanceUID(String sOPInstanceUID) {
	SOPInstanceUID = sOPInstanceUID;
    }

    public String getPatientID() {
	return PatientID;
    }

    public void setPatientID(String patientID) {
	PatientID = patientID;
    }

    public String getStudyDescription() {
	return StudyDescription;
    }

    public void setStudyDescription(String studyDescription) {
	StudyDescription = studyDescription;
    }

    public String getPatientName() {
	return PatientName;
    }

    public void setPatientName(String patientName) {
	PatientName = patientName;
    }

    public String getImageType() {
	return ImageType;
    }

    public void setImageType(String imageType) {
	ImageType = imageType;
    }

    public String getInstanceCreationDate() {
	return InstanceCreationDate;
    }

    public void setInstanceCreationDate(String instanceCreationDate) {
	InstanceCreationDate = instanceCreationDate;
    }

    public String getInstanceCreationTime() {
	return InstanceCreationTime;
    }

    public void setInstanceCreationTime(String instanceCreationTime) {
	InstanceCreationTime = instanceCreationTime;
    }

    public String getStudyDate() {
	return StudyDate;
    }

    public void setStudyDate(String studyDate) {
	StudyDate = studyDate;
    }

    public Integer getPkey() {
	return pkey;
    }

    public void setPkey(Integer pkey) {
	this.pkey = pkey;
    }

    public Map<String, String> toMap() {
	Map<String, String> mapOfAttributes = new HashMap<String, String>();
	mapOfAttributes.put("SOPInstanceUID", getSOPInstanceUID());
	mapOfAttributes.put("PatientID", getPatientID());
	mapOfAttributes.put("StudyDescription", getStudyDescription());
	mapOfAttributes.put("PatientName", getPatientName());
	mapOfAttributes.put("ImageType", getImageType());
	mapOfAttributes.put("InstanceCreationDate", getInstanceCreationDate());
	mapOfAttributes.put("InstanceCreationTime", getInstanceCreationTime());
	mapOfAttributes.put("StudyDate", getStudyDate());
	mapOfAttributes.put("_pkey", getPkey().toString());

	return mapOfAttributes;
    }
}
