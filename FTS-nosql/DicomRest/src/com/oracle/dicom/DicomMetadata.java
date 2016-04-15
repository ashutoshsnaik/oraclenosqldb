package com.oracle.dicom;

/**
 * The DicomMetadata is a standard POJO class which is used to carry Dicom
 * Attributes
 *
 * @author Vishal Settipalli
 * @version 1.0
 * @since March 2016
 */
public class DicomMetadata {

    private String id;
    private String lobKey;
    private String dicomFileName;
    private String dicomMetadata;
    private String SOPInstanceUID;
    private String PatientID;
    private String StudyDescription;
    private String PatientName;
    private String ImageType;
    private String InstanceCreationDate;
    private String InstanceCreationTime;
    private String StudyDate;
    private String FileMetaInformationGroupLength;
    private String FileMetaInformationVersion;
    private String MediaStorageSOPClassUID;
    private String MediaStorageSOPInstanceUID;
    private String TransferSyntaxUID;
    private String ImplementationClassUID;
    private String SourceApplicationEntityTitle;
    private String SOPClassUID;
    private String StudyTime;
    private String AccessionNumber;
    private String Modality;
    private String Manufacturer;
    private String InstitutionAddress;
    private String InstitutionName;
    private String ReferringPhysicianName;
    private String SeriesDescription;
    private String PatientBirthDate;
    private String PatientBirthTime;
    private String PatientSex;
    private String SliceThickness;
    private String PatientPosition;
    private String StudyInstanceUID;
    private String SeriesInstanceUID;
    private String StudyID;
    private String SeriesNumber;
    private String InstanceNumber;
    private String ImagePositionPatient;
    private String ImageOrientationPatient;
    private String FrameOfReferenceUID;
    private String PositionReferenceIndicator;
    private String SamplesPerPixel;
    private String PhotometricInterpretation;
    private String Rows;
    private String Columns;
    private String PixelSpacing;
    private String BitsAllocated;
    private String BitsStored;
    private String HighBit;
    private String PixelRepresentation;
    private String RescaleIntercept;
    private String RescaleSlope;
    private String PixelData;
    private Integer Age;

    public Integer getAge() {
	return Age;
    }

    public void setAge(Integer age) {
	Age = age;
    }

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getLobKey() {
	return lobKey;
    }

    public void setLobKey(String lobKey) {
	this.lobKey = lobKey;
    }

    public String getDicomFileName() {
	return dicomFileName;
    }

    public void setDicomFileName(String dicomFileName) {
	this.dicomFileName = dicomFileName;
    }

    public String getDicomMetadata() {
	return dicomMetadata;
    }

    public void setDicomMetadata(String dicomMetadata) {
	this.dicomMetadata = dicomMetadata;
    }

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

    public String getFileMetaInformationGroupLength() {
	return FileMetaInformationGroupLength;
    }

    public void setFileMetaInformationGroupLength(
	    String fileMetaInformationGroupLength) {
	FileMetaInformationGroupLength = fileMetaInformationGroupLength;
    }

    public String getFileMetaInformationVersion() {
	return FileMetaInformationVersion;
    }

    public void setFileMetaInformationVersion(String fileMetaInformationVersion) {
	FileMetaInformationVersion = fileMetaInformationVersion;
    }

    public String getMediaStorageSOPClassUID() {
	return MediaStorageSOPClassUID;
    }

    public void setMediaStorageSOPClassUID(String mediaStorageSOPClassUID) {
	MediaStorageSOPClassUID = mediaStorageSOPClassUID;
    }

    public String getMediaStorageSOPInstanceUID() {
	return MediaStorageSOPInstanceUID;
    }

    public void setMediaStorageSOPInstanceUID(String mediaStorageSOPInstanceUID) {
	MediaStorageSOPInstanceUID = mediaStorageSOPInstanceUID;
    }

    public String getTransferSyntaxUID() {
	return TransferSyntaxUID;
    }

    public void setTransferSyntaxUID(String transferSyntaxUID) {
	TransferSyntaxUID = transferSyntaxUID;
    }

    public String getImplementationClassUID() {
	return ImplementationClassUID;
    }

    public void setImplementationClassUID(String implementationClassUID) {
	ImplementationClassUID = implementationClassUID;
    }

    public String getSourceApplicationEntityTitle() {
	return SourceApplicationEntityTitle;
    }

    public void setSourceApplicationEntityTitle(
	    String sourceApplicationEntityTitle) {
	SourceApplicationEntityTitle = sourceApplicationEntityTitle;
    }

    public String getSOPClassUID() {
	return SOPClassUID;
    }

    public void setSOPClassUID(String sOPClassUID) {
	SOPClassUID = sOPClassUID;
    }

    public String getStudyTime() {
	return StudyTime;
    }

    public void setStudyTime(String studyTime) {
	StudyTime = studyTime;
    }

    public String getAccessionNumber() {
	return AccessionNumber;
    }

    public void setAccessionNumber(String accessionNumber) {
	AccessionNumber = accessionNumber;
    }

    public String getModality() {
	return Modality;
    }

    public void setModality(String modality) {
	Modality = modality;
    }

    public String getManufacturer() {
	return Manufacturer;
    }

    public void setManufacturer(String manufacturer) {
	Manufacturer = manufacturer;
    }

    public String getInstitutionAddress() {
	return InstitutionAddress;
    }

    public void setInstitutionAddress(String institutionAddress) {
	InstitutionAddress = institutionAddress;
    }

    public String getInstitutionName() {
	return InstitutionName;
    }

    public void setInstitutionName(String institutionName) {
	InstitutionName = institutionName;
    }

    public String getReferringPhysicianName() {
	return ReferringPhysicianName;
    }

    public void setReferringPhysicianName(String referringPhysicianName) {
	ReferringPhysicianName = referringPhysicianName;
    }

    public String getSeriesDescription() {
	return SeriesDescription;
    }

    public void setSeriesDescription(String seriesDescription) {
	SeriesDescription = seriesDescription;
    }

    public String getPatientBirthDate() {
	return PatientBirthDate;
    }

    public void setPatientBirthDate(String patientBirthDate) {
	PatientBirthDate = patientBirthDate;
    }

    public String getPatientBirthTime() {
	return PatientBirthTime;
    }

    public void setPatientBirthTime(String patientBirthTime) {
	PatientBirthTime = patientBirthTime;
    }

    public String getPatientSex() {
	return PatientSex;
    }

    public void setPatientSex(String patientSex) {
	PatientSex = patientSex;
    }

    public String getSliceThickness() {
	return SliceThickness;
    }

    public void setSliceThickness(String sliceThickness) {
	SliceThickness = sliceThickness;
    }

    public String getPatientPosition() {
	return PatientPosition;
    }

    public void setPatientPosition(String patientPosition) {
	PatientPosition = patientPosition;
    }

    public String getStudyInstanceUID() {
	return StudyInstanceUID;
    }

    public void setStudyInstanceUID(String studyInstanceUID) {
	StudyInstanceUID = studyInstanceUID;
    }

    public String getSeriesInstanceUID() {
	return SeriesInstanceUID;
    }

    public void setSeriesInstanceUID(String seriesInstanceUID) {
	SeriesInstanceUID = seriesInstanceUID;
    }

    public String getStudyID() {
	return StudyID;
    }

    public void setStudyID(String studyID) {
	StudyID = studyID;
    }

    public String getSeriesNumber() {
	return SeriesNumber;
    }

    public void setSeriesNumber(String seriesNumber) {
	SeriesNumber = seriesNumber;
    }

    public String getInstanceNumber() {
	return InstanceNumber;
    }

    public void setInstanceNumber(String instanceNumber) {
	InstanceNumber = instanceNumber;
    }

    public String getImagePositionPatient() {
	return ImagePositionPatient;
    }

    public void setImagePositionPatient(String imagePositionPatient) {
	ImagePositionPatient = imagePositionPatient;
    }

    public String getImageOrientationPatient() {
	return ImageOrientationPatient;
    }

    public void setImageOrientationPatient(String imageOrientationPatient) {
	ImageOrientationPatient = imageOrientationPatient;
    }

    public String getFrameOfReferenceUID() {
	return FrameOfReferenceUID;
    }

    public void setFrameOfReferenceUID(String frameOfReferenceUID) {
	FrameOfReferenceUID = frameOfReferenceUID;
    }

    public String getPositionReferenceIndicator() {
	return PositionReferenceIndicator;
    }

    public void setPositionReferenceIndicator(String positionReferenceIndicator) {
	PositionReferenceIndicator = positionReferenceIndicator;
    }

    public String getSamplesPerPixel() {
	return SamplesPerPixel;
    }

    public void setSamplesPerPixel(String samplesPerPixel) {
	SamplesPerPixel = samplesPerPixel;
    }

    public String getPhotometricInterpretation() {
	return PhotometricInterpretation;
    }

    public void setPhotometricInterpretation(String photometricInterpretation) {
	PhotometricInterpretation = photometricInterpretation;
    }

    public String getRows() {
	return Rows;
    }

    public void setRows(String rows) {
	Rows = rows;
    }

    public String getColumns() {
	return Columns;
    }

    public void setColumns(String columns) {
	Columns = columns;
    }

    public String getPixelSpacing() {
	return PixelSpacing;
    }

    public void setPixelSpacing(String pixelSpacing) {
	PixelSpacing = pixelSpacing;
    }

    public String getBitsAllocated() {
	return BitsAllocated;
    }

    public void setBitsAllocated(String bitsAllocated) {
	BitsAllocated = bitsAllocated;
    }

    public String getBitsStored() {
	return BitsStored;
    }

    public void setBitsStored(String bitsStored) {
	BitsStored = bitsStored;
    }

    public String getHighBit() {
	return HighBit;
    }

    public void setHighBit(String highBit) {
	HighBit = highBit;
    }

    public String getPixelRepresentation() {
	return PixelRepresentation;
    }

    public void setPixelRepresentation(String pixelRepresentation) {
	PixelRepresentation = pixelRepresentation;
    }

    public String getRescaleIntercept() {
	return RescaleIntercept;
    }

    public void setRescaleIntercept(String rescaleIntercept) {
	RescaleIntercept = rescaleIntercept;
    }

    public String getRescaleSlope() {
	return RescaleSlope;
    }

    public void setRescaleSlope(String rescaleSlope) {
	RescaleSlope = rescaleSlope;
    }

    public String getPixelData() {
	return PixelData;
    }

    public void setPixelData(String pixelData) {
	PixelData = pixelData;
    }
}
