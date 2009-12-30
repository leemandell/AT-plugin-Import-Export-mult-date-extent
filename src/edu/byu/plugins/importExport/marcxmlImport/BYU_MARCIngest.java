package edu.byu.plugins.importExport.marcxmlImport;
import java.io.*;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import java.util.List;

import java.util.Vector;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;


import org.archiviststoolkit.swing.InfiniteProgressPanel;
import org.archiviststoolkit.dialog.ErrorDialog;
import org.archiviststoolkit.util.LookupListUtils;
import org.archiviststoolkit.util.NameUtils;
import org.archiviststoolkit.exceptions.DuplicateLinkException;
import org.archiviststoolkit.exceptions.UnknownLookupListException;
import org.archiviststoolkit.importer.ImportException;
import org.archiviststoolkit.importer.ImportExportLogDialog;
import org.archiviststoolkit.importer.MARCXML.MARCXMLAction;
import org.archiviststoolkit.model.*;
import org.archiviststoolkit.exceptions.ValidationException;
import org.archiviststoolkit.mydomain.NamesDAO;
import org.archiviststoolkit.mydomain.PersistenceException;
import org.archiviststoolkit.mydomain.SubjectsDAO;
import org.archiviststoolkit.structure.FieldNotFoundException;
import org.archiviststoolkit.structure.MARCXML.CollectionType;
import org.archiviststoolkit.structure.MARCXML.ControlFieldType;
import org.archiviststoolkit.structure.MARCXML.DataFieldType;
import org.archiviststoolkit.structure.MARCXML.RecordType;
import org.archiviststoolkit.structure.MARCXML.SubfieldatafieldType;
import org.archiviststoolkit.util.StringHelper;


public class BYU_MARCIngest {
	static JAXBContext context = null;
	//static String report = "";
	public static HashMap actionMappings;
	public static DataFieldType resourceTitle = null;
	public static int resourceTitlePriority = 0;
	public static HashMap thesaurusMappings = null;
	public static String report;
	public static boolean error;
	private static ArchDescriptionDates dateRecord = null;

	public boolean doIngest(File fileName, Resources res,boolean importAll, InfiniteProgressPanel progressPanel) throws ImportException {
		try{
			//Resources res = new Resources();
			if(thesaurusMappings==null){
				thesaurusMappings = new HashMap();
				setThesaurusMappings(thesaurusMappings);
			}
			BYU_MARCIngest.error=false;
			report = "";
			resourceTitle=null;
			dateRecord = null;
			resourceTitlePriority=0;
			progressPanel.setTextLine("Loading MARCXML document into memory", 1);
			context = JAXBContext.newInstance("org.archiviststoolkit.structure.MARCXML");
			JAXBElement o = (JAXBElement)context.createUnmarshaller().unmarshal(fileName);

			CollectionType collectionElem = (CollectionType)o.getValue();

			List records = collectionElem.getRecord();
			RecordType record = (RecordType)records.get(0);
			//RecordTypeType type = record.getType();
			//String typeS = "";
			//if(type!= null)
			//{
			//typeS = type.value();
			//}
			//if((type==null) || (!typeS.equalsIgnoreCase("Bibliographic")))
			//{
			//BYU_MARCIngest.importError("Record type attribute must have a value of Bibliographic");
			//return false;
			//}

			List<ControlFieldType> controlFields = record.getControlfield();
			for(ControlFieldType controlField:controlFields)
			{
				// if process was cancelled then return
				if(progressPanel.isProcessCancelled()) {
					return false;
				}
				if(importAll)
					processControlElement(controlField,res, progressPanel);
				if(BYU_MARCIngest.error==true)
					return false;
			}

			List<DataFieldType> dataFields = record.getDatafield();
			for(DataFieldType dataField:dataFields){
				// if process was cancelled then return
				if(progressPanel.isProcessCancelled()) {
					return false;
				}

				boolean a = processElement(dataField,res,importAll);
				if(a==false){
					importError("Presence of 773 tag indicates this is a child record and cannot be imported", progressPanel);
					return false;
				}
			}

			return true;
		}

		catch (JAXBException jabe){
			//throw new ImportException("Error loading finding aid into memory", jabe);
			jabe.printStackTrace();
			if(jabe.getMessage().startsWith("unexpected element")){
				importError("The file you are importing does not appear to be an MARCXML document.", progressPanel);
				return false;
			}
			else{
				importError("There appears to be a problem with the MARCXML document", progressPanel);
				return false;
			}
		}
		catch (ClassCastException cce){
			//throw new ImportException("Error loading finding aid into memory", jabe);
			cce.printStackTrace();
			importError("There appears to be a problem with the MARCXML document", progressPanel);
			return false;
		}
	}



	private void setThesaurusMappings(HashMap thesaurusMappings)
	{
		thesaurusMappings.put("0","Library of Congress Subject Headings");
		thesaurusMappings.put("1","LC subject headings for children's literature");
		thesaurusMappings.put("2","Medical Subject Headings");
		thesaurusMappings.put("3","National Agricultural Library subject authority file");
		thesaurusMappings.put("4","Source not specified");
		thesaurusMappings.put("5","Canadian Subject Headings");
		thesaurusMappings.put("6","Répertoire de vedettes-matière");
		thesaurusMappings.put("7","Source specified in subfield $2");

	}
	public static void importError(String message, InfiniteProgressPanel progressPanel){
		progressPanel.close();
		ImportExportLogDialog dialog = new ImportExportLogDialog(message, ImportExportLogDialog.DIALOG_TYPE_IMPORT);
		dialog.showDialog();
		BYU_MARCIngest.error=true;
	}


	public static DataFieldType getResourceTitleTag(){
		return BYU_MARCIngest.resourceTitle;
	}

	public static int getResourceTitleTagPriority(){
		return BYU_MARCIngest.resourceTitlePriority;
	}

	public static boolean processElement(DataFieldType dataField,Resources res,boolean importAll){
		String tag = dataField.getTag();
		String className = "edu.byu.plugins.importExport.marcxmlImport."+"Handle"+tag+"Action";
		if(tag.equals("773")){
			return false;
		}
		if(importAll || tag.startsWith("1") || tag.startsWith("6") || tag.startsWith("7")){

			try{
				System.out.println("Class: " + className);
				getHandleClass(tag).processElement(res,dataField, null);
//				if (tag.equals("008")) {
//					new Handle008Action().processElement(res,dataField, null);
//
//				} else if (tag.equals("100")) {
//					new Handle100Action().processElement(res,dataField, null);
//
//				} else if (tag.equals("110")) {
//					new Handle110Action().processElement(res,dataField, null);
//
//				} else if (tag.equals("111")) {
//					new Handle111Action().processElement(res,dataField, null);
//
//				} else if (tag.equals("130")) {
//					new Handle130Action().processElement(res,dataField, null);
//
//				} else if (tag.equals("210")) {
//					new Handle210Action().processElement(res,dataField, null);
//
//				} else if (tag.equals("222")) {
//					new Handle222Action().processElement(res,dataField, null);
//
//				} else if (tag.equals("240")) {
//					new Handle240Action().processElement(res,dataField, null);
//
//				} else if (tag.equals("242")) {
//					new Handle242Action().processElement(res,dataField, null);
//
//				} else if (tag.equals("243")) {
//					new Handle243Action().processElement(res,dataField, null);
//
//				} else if (tag.equals("245")) {
//					new Handle245Action().processElement(res,dataField, null);
//
//				}
				return true;
			}
			catch (ClassCastException cce){
				//cnfe.printStackTrace();
				System.out.println(className+ " not cast to Action");
			} catch (UnsupportedRepeatingDataTypeException e) {
				System.out.println(e.getMessage());
			} catch (ClassNotFoundException e) {
				System.out.println(className+ " not found");
			}
		}
		return true;
	}

	private static MARCXMLAction getHandleClass(String tag) throws ClassNotFoundException {

		if (tag.equals("008")) {
			return new Handle008Action();

		} else if (tag.equals("100")) {
			return new Handle100Action();

		} else if (tag.equals("110")) {
			return new Handle110Action();

		} else if (tag.equals("111")) {
			return new Handle111Action();

		} else if (tag.equals("130")) {
			return new Handle130Action();

		} else if (tag.equals("210")) {
			return new Handle210Action();

		} else if (tag.equals("222")) {
			return new Handle222Action();

		} else if (tag.equals("240")) {
			return new Handle240Action();

		} else if (tag.equals("242")) {
			return new Handle242Action();

		} else if (tag.equals("243")) {
			return new Handle243Action();

		} else if (tag.equals("245")) {
			return new Handle245Action();

		} else if (tag.equals("246")) {
			return new Handle246Action();

		} else if (tag.equals("247")) {
			return new Handle247Action();

		} else if (tag.equals("250")) {
			return new Handle250Action();

		} else if (tag.equals("254")) {
			return new Handle254Action();

		} else if (tag.equals("255")) {
			return new Handle255Action();

		} else if (tag.equals("256")) {
			return new Handle256Action();

		} else if (tag.equals("257")) {
			return new Handle257Action();

		} else if (tag.equals("258")) {
			return new Handle258Action();

		} else if (tag.equals("260")) {
			return new Handle260Action();

		} else if (tag.equals("263")) {
			return new Handle263Action();

		} else if (tag.equals("270")) {
			return new Handle270Action();

		} else if (tag.equals("300")) {
			return new Handle300Action();

		} else if (tag.equals("306")) {
			return new Handle306Action();

		} else if (tag.equals("307")) {
			return new Handle307Action();

		} else if (tag.equals("310")) {
			return new Handle310Action();

		} else if (tag.equals("321")) {
			return new Handle321Action();

		} else if (tag.equals("340")) {
			return new Handle340Action();

		} else if (tag.equals("342")) {
			return new Handle342Action();

		} else if (tag.equals("343")) {
			return new Handle343Action();

		} else if (tag.equals("351")) {
			return new Handle351Action();

		} else if (tag.equals("352")) {
			return new Handle352Action();

		} else if (tag.equals("355")) {
			return new Handle355Action();

		} else if (tag.equals("357")) {
			return new Handle357Action();

		} else if (tag.equals("362")) {
			return new Handle362Action();

		} else if (tag.equals("365")) {
			return new Handle365Action();

		} else if (tag.equals("366")) {
			return new Handle366Action();

		} else if (tag.equals("440")) {
			return new Handle440Action();

		} else if (tag.equals("590")) {
			return new Handle490Action();

		} else if (tag.equals("500")) {
			return new Handle500Action();

		} else if (tag.equals("501")) {
			return new Handle501Action();

		} else if (tag.equals("502")) {
			return new Handle502Action();

		} else if (tag.equals("504")) {
			return new Handle504Action();

		} else if (tag.equals("505")) {
			return new Handle505Action();

		} else if (tag.equals("506")) {
			return new Handle506Action();

		} else if (tag.equals("507")) {
			return new Handle507Action();

		} else if (tag.equals("508")) {
			return new Handle508Action();

		} else if (tag.equals("510")) {
			return new Handle510Action();

		} else if (tag.equals("511")) {
			return new Handle511Action();

		} else if (tag.equals("513")) {
			return new Handle513Action();

		} else if (tag.equals("514")) {
			return new Handle514Action();

		} else if (tag.equals("515")) {
			return new Handle515Action();

		} else if (tag.equals("516")) {
			return new Handle516Action();

		} else if (tag.equals("518")) {
			return new Handle518Action();

		} else if (tag.equals("520")) {
			return new Handle520Action();

		} else if (tag.equals("521")) {
			return new Handle521Action();

		} else if (tag.equals("522")) {
			return new Handle522Action();

		} else if (tag.equals("524")) {
			return new Handle524Action();

		} else if (tag.equals("525")) {
			return new Handle525Action();

		} else if (tag.equals("526")) {
			return new Handle526Action();

		} else if (tag.equals("530")) {
			return new Handle530Action();

		} else if (tag.equals("533")) {
			return new Handle533Action();

		} else if (tag.equals("534")) {
			return new Handle534Action();

		} else if (tag.equals("535")) {
			return new Handle535Action();

		} else if (tag.equals("536")) {
			return new Handle536Action();

		} else if (tag.equals("538")) {
			return new Handle538Action();

		} else if (tag.equals("540")) {
			return new Handle540Action();

		} else if (tag.equals("541")) {
			return new Handle541Action();

		} else if (tag.equals("544")) {
			return new Handle544Action();

		} else if (tag.equals("545")) {
			return new Handle545Action();

		} else if (tag.equals("546")) {
			return new Handle546Action();

		} else if (tag.equals("547")) {
			return new Handle547Action();

		} else if (tag.equals("550")) {
			return new Handle550Action();

		} else if (tag.equals("552")) {
			return new Handle522Action();

		} else if (tag.equals("555")) {
			return new Handle555Action();

		} else if (tag.equals("556")) {
			return new Handle556Action();

		} else if (tag.equals("561")) {
			return new Handle561Action();

		} else if (tag.equals("562")) {
			return new Handle562Action();

		} else if (tag.equals("563")) {
			return new Handle563Action();

		} else if (tag.equals("565")) {
			return new Handle565Action();

		} else if (tag.equals("567")) {
			return new Handle567Action();

		} else if (tag.equals("580")) {
			return new Handle580Action();

		} else if (tag.equals("581")) {
			return new Handle581Action();

		} else if (tag.equals("583")) {
			return new Handle583Action();

		} else if (tag.equals("584")) {
			return new Handle584Action();

		} else if (tag.equals("585")) {
			return new Handle585Action();

		} else if (tag.equals("586")) {
			return new Handle586Action();

		} else if (tag.equals("59x")) {
			return new Handle59xAction();

		} else if (tag.equals("600")) {
			return new Handle600Action();

		} else if (tag.equals("610")) {
			return new Handle610Action();

		} else if (tag.equals("611")) {
			return new Handle611Action();

		} else if (tag.equals("630")) {
			return new Handle630Action();

		} else if (tag.equals("648")) {
			return new Handle648Action();

		} else if (tag.equals("650")) {
			return new Handle650Action();

		} else if (tag.equals("651")) {
			return new Handle651Action();

		} else if (tag.equals("653")) {
			return new Handle653Action();

		} else if (tag.equals("654")) {
			return new Handle654Action();

		} else if (tag.equals("655")) {
			return new Handle655Action();

		} else if (tag.equals("656")) {
			return new Handle656Action();

		} else if (tag.equals("657")) {
			return new Handle657Action();

		} else if (tag.equals("658")) {
			return new Handle658Action();

		} else if (tag.equals("662")) {
			return new Handle662Action();

		} else if (tag.equals("69x")) {
			return new Handle69xAction();

		} else if (tag.equals("700")) {
			return new Handle700Action();

		} else if (tag.equals("710")) {
			return new Handle710Action();

		} else if (tag.equals("711")) {
			return new Handle711Action();

		} else if (tag.equals("720")) {
			return new Handle720Action();

		} else if (tag.equals("730")) {
			return new Handle730Action();

		} else if (tag.equals("740")) {
			return new Handle740Action();

		} else if (tag.equals("752")) {
			return new Handle752Action();

		} else if (tag.equals("753")) {
			return new Handle753Action();

		} else if (tag.equals("754")) {
			return new Handle754Action();

		} else if (tag.equals("760")) {
			return new Handle760Action();

		} else if (tag.equals("762")) {
			return new Handle762Action();

		} else if (tag.equals("765")) {
			return new Handle765Action();

		} else if (tag.equals("767")) {
			return new Handle767Action();

		} else if (tag.equals("770")) {
			return new Handle770Action();

		} else if (tag.equals("772")) {
			return new Handle772Action();

		} else if (tag.equals("773")) {
			return new Handle773Action();

		} else if (tag.equals("774")) {
			return new Handle774Action();

		} else if (tag.equals("775")) {
			return new Handle775Action();

		} else if (tag.equals("776")) {
			return new Handle776Action();

		} else if (tag.equals("777")) {
			return new Handle777Action();

		} else if (tag.equals("780")) {
			return new Handle780Action();

		} else if (tag.equals("785")) {
			return new Handle785Action();

		} else if (tag.equals("786")) {
			return new Handle786Action();

		} else if (tag.equals("787")) {
			return new Handle787Action();

		} else {
			throw new ClassNotFoundException("Handle" + tag + "Action");
		}
	}

	public static boolean processControlElement(ControlFieldType controlField,Resources res, InfiniteProgressPanel progressPanel){
		String tag = controlField.getTag();
		String className = "edu.byu.plugins.importExport.marcxmlImport."+"Handle"+tag+"Action";

		try{
			getHandleClass(tag).processElement(res,controlField, progressPanel);
//			Class a = Class.forName(className,true,MARCXMLAction.class.getClassLoader());
//			MARCXMLAction ob = (MARCXMLAction)a.newInstance();
//			ob.processElement(res,controlField, progressPanel);
			return true;
		}
		catch (ClassNotFoundException cnfe){
			//cnfe.printStackTrace();
			//System.out.println(className+ " not found");
		}
		catch (ClassCastException cce){
			//cnfe.printStackTrace();
			System.out.println(className+ " not cast to Action");
		}
//		catch (InstantiationException cce){
//			//cnfe.printStackTrace();
//			System.out.println(className+ " instantiation exception");
//		}
//		catch (IllegalAccessException cce){
//			//cnfe.printStackTrace();
//			System.out.println(className+ " illegal access exception");
//		}
		catch (UnsupportedRepeatingDataTypeException e) {
			//todo handle error better
			new ErrorDialog("", e).showDialog();
		}

		return false;
	}

	public static String getSubCodeValue(DataFieldType dataField,String subCode)
	{
		String value="";
		List<SubfieldatafieldType> subFields = dataField.getSubfield();
		for(SubfieldatafieldType subField:subFields)
		{
			String code = subField.getCode();
			if(code.equals(subCode)){
				value = subField.getValue();
				if(null!=value)
					return value;
				else
					return "";
			}
		}
		return value;
	}

	public static String getSpecificSubCodeValuesAsDelimitedString(DataFieldType dataField, String subCode, String delimiter){
		String stringArray[] = {subCode};
		return getSpecificSubCodeValuesAsDelimitedString(dataField, BYU_MARCIngest.arrayToVector(stringArray), delimiter);

	}

	public static String getSpecificSubCodeValuesAsDelimitedString(DataFieldType dataField,Vector<String> subCodes,String delimiter){
		String values="";

		boolean firstPass = true;
		for(String subCode:subCodes){
			Vector<String> vals = getSubCodeValues(dataField,subCode);
			for(String val:vals){
				if(firstPass && val.length()>0){
					values = val;
					firstPass=false;
				}
				else{
					if(val.length()>0)
						values = values+delimiter+val;
				}
			}
		}
		return values;
	}

	public static String getAllSubCodeValuesAsDelimitedString(DataFieldType dataField,String delimiter){
		String values="";
		Vector<String> subCodes = BYU_MARCIngest.getAllSubCodeValues(dataField);
		boolean firstPass = true;
		for(String subCode:subCodes){
			if(firstPass && subCode.length()>0){
				values = subCode;
				firstPass=false;
			}
			else{
				if(subCode.length()>0)
					values = values+delimiter+subCode;
			}
		}
		return values;
	}

	public static Vector<String> getSubCodeValues(DataFieldType dataField,String subCode)
	{
		Vector <String> values = new Vector<String>();
		List<SubfieldatafieldType> subFields = dataField.getSubfield();
		for(SubfieldatafieldType subField:subFields)
		{
			String code = subField.getCode();
			if(code.equals(subCode)){
				String value = subField.getValue();
				values.add(value);
			}
		}
		return values;
	}

	public static Vector<String> getAllSubCodeValues(DataFieldType dataField)
	{
		Vector <String> values = new Vector();
		List<SubfieldatafieldType> subFields = dataField.getSubfield();
		for(SubfieldatafieldType subField:subFields){
			values.add(subField.getValue());
		}
		return values;
	}
	public static String getDelimitedString(Vector<String> strings,String delim)
	{
		if(strings==null||strings.size()==0)
			return "";
		String value="";
		boolean first=true;
		for(String string:strings)
		{
			if(!first)
				value=value+delim;
			value=value+string;
			first=false;
		}
		return value;
	}
	public static Vector<String> arrayToVector(String[] values){
		Vector<String> valuesV = new Vector<String>();
		if(values==null || values.length==0)
			return valuesV;

		for(int a=0;a<values.length;a++){
			valuesV.add(values[a]);
		}
		return valuesV;
	}
	public static void addSubjects(ArchDescription archDescription,
								   String subjectS,
								   String subjectTermType,
								   String subjectSource) throws PersistenceException, ValidationException, UnknownLookupListException {
		SubjectsDAO subjectDao = new SubjectsDAO();
		Subjects subject;
		ArchDescriptionSubjects accessionSubject;

		subject = subjectDao.lookupSubject(subjectS, subjectTermType, subjectSource, true);
		accessionSubject = new ArchDescriptionSubjects(subject, archDescription);
		archDescription.addSubject(accessionSubject);

	}

	public static void addName(ArchDescription archDescription, Names name, String function,String role,String form) throws PersistenceException, UnknownLookupListException, NoSuchAlgorithmException, UnsupportedEncodingException {
		NamesDAO nameDao = new NamesDAO();
		ArchDescriptionNames archDescriptionName;
		function = BYU_MARCIngest.addItemToList(ArchDescriptionNames.class,"nameLinkFunction",function);
		role = StringHelper.cleanUpWhiteSpace(role);
		if(function!=null && (function.equalsIgnoreCase("Creator") || function.equalsIgnoreCase("Subject"))){
			LookupListItems lli = LookupListUtils.getLookupListItem("Name link creator / subject role", role);
			if(lli!=null)
				role = lli.toString();
		}
		else if(function!=null && (function.equalsIgnoreCase("Source"))){
			LookupListItems lli = LookupListUtils.getLookupListItem("Name link source role", role);
			if(lli!=null)
				role = lli.toString();
		}

		name.createSortName();

		if (name.getNameSource().length() == 0) {
			name.setNameSource("ingest");
		}

		NameUtils.setMd5Hash(name);

		name = nameDao.lookupName(name, true);

		archDescriptionName = new ArchDescriptionNames(name, archDescription);
		archDescriptionName.setNameLinkFunction(function);
		if(role!=null)
			archDescriptionName.setRole(role);
		if(StringHelper.isNotEmpty(form)){
			form = BYU_MARCIngest.addItemToList(ArchDescriptionNames.class,"form",form);
			archDescriptionName.setForm(form);
		}
		try {
			archDescription.addName(archDescriptionName);
		} catch (DuplicateLinkException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
	}

	public static String addItemToList(Class clazz,String fieldName,String value){
		String valueS = "";
		try{
			valueS = LookupListUtils.addItemToListByField(clazz,fieldName,value);
		}
		catch (FieldNotFoundException fnfe){
			fnfe.printStackTrace();
		}
		catch (UnknownLookupListException ulle){
			ulle.printStackTrace();
		}
		if(null!=valueS)
			return valueS;
		else return "";
	}

	public static ArchDescriptionDates getDateRecord(Resources resource) {
		if (dateRecord == null) {
			dateRecord = new ArchDescriptionDates(resource);
			resource.addArchdescriptionDate(dateRecord);
		}

		return dateRecord;
	}
}