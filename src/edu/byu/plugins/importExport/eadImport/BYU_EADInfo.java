package edu.byu.plugins.importExport.eadImport;
import java.util.HashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.archiviststoolkit.model.ArchDescriptionNotes;
import org.archiviststoolkit.model.Resources;
import org.archiviststoolkit.importer.*;

public class BYU_EADInfo {
    private static HashMap elementTypes = new HashMap();
    private static HashMap actions = new HashMap();
    public static int sequence = 0;
    private static ArchDescriptionNotes referenceTOParentNotes = null;
    private static Resources referenceTOResources = null;
	private static JAXBContext context=null;
	private static String report = "";
	public BYU_EADInfo()
    {
        setEADInfo();
    }

    public void setEADInfo(){
        setElementTypes();
        setActions();
    }

    public void setElementTypes(){
        elementTypes.put("abstract","abstract");
        elementTypes.put("archdesc","archdesc");
        elementTypes.put("accessrestrict","note");
        elementTypes.put("accruals","note");
        elementTypes.put("acqinfo","note");
        elementTypes.put("altformavail","note");
        elementTypes.put("appraisal","note");
        elementTypes.put("arrangement","note");
        this.elementTypes.put("bibliography","bibliography");
        elementTypes.put("bioghist","note");
        elementTypes.put("c","container");
        elementTypes.put("chronlist","chronlist");
        elementTypes.put("famname","famname");

        elementTypes.put("corpname","corpname");
        elementTypes.put("controlaccess","control access");
        elementTypes.put("custodhist","note");
        elementTypes.put("dao","dao");
        elementTypes.put("daodesc","note");

        elementTypes.put("daoloc","daoloc");

        elementTypes.put("daogrp","daogrp");
        //elementTypes.put("descgrp","other");
        elementTypes.put("did","did");
        elementTypes.put("dimensions","note");

        elementTypes.put("dsc","dsc");
        elementTypes.put("descgrp","descgrp");
        elementTypes.put("eadid","eadid");
        elementTypes.put("eadheader","eadheader");
        elementTypes.put("filedesc","filedesc");
        elementTypes.put("profiledesc","profiledesc");
        elementTypes.put("revisiondesc","revisiondesc");
        elementTypes.put("geogname","geogname");
        elementTypes.put("occupation","occupation");
        elementTypes.put("function","function");

        elementTypes.put("editionstmt","editionstmt");
        elementTypes.put("notestmt","notestmt");
        elementTypes.put("publicationstmt","publicationstmt");
        elementTypes.put("seriesstmt","seriesstmt");
        elementTypes.put("titlestmt","titlestmt");


        elementTypes.put("fileplan","note");
        elementTypes.put("genreform","genreform");
        //elementTypes.put("geogname","geogname");

        elementTypes.put("index","index");
        elementTypes.put("langmaterial","langmaterial");
        elementTypes.put("list","list");
        elementTypes.put("langusage","langusage");
        elementTypes.put("legalstatus","note");

        elementTypes.put("materialspec","materialspec");
        elementTypes.put("otherfindaid","otherfindaid");

        elementTypes.put("note","note");
        elementTypes.put("origination","origination");
        elementTypes.put("odd","note");
        elementTypes.put("originalsloc","note");
        elementTypes.put("name","persname");

        elementTypes.put("persname","persname");
        elementTypes.put("physdesc","physdesc");
        elementTypes.put("physloc","physloc");
        elementTypes.put("descrules","descrules");

        elementTypes.put("phystech","note");
        elementTypes.put("prefercite","note");
        elementTypes.put("processinfo","note");
        elementTypes.put("relatedmaterial","note");
        elementTypes.put("runner","note");
        elementTypes.put("scopecontent","note");
        elementTypes.put("separatedmaterial","note");
        elementTypes.put("subject","subject");
        elementTypes.put("separatedmaterial","note");
        elementTypes.put("unitdate","unitdate");
        elementTypes.put("unitid","unitid");
        elementTypes.put("unittitle","unittitle");
        elementTypes.put("userestrict","note");
    }

    public void setActions(){
        actions.put("note",new BYU_HandleNotesAction());
        //actions.put("structured data",new HandleStructuredDataAction());

        actions.put("archdesc",new BYU_HandleArchDescAction());
        actions.put("did",new BYU_HandleDidAction());
        actions.put("container",new BYU_HandleComponentAction());
        actions.put("corpname",new HandleCorpnameAction());
        actions.put("otherfindaid",new BYU_HandleNotesAction());

        actions.put("physdesc",new BYU_HandlePhysdescAction());
        actions.put("descrules",new HandleDescrulesAction());
        actions.put("control access",new BYU_HandleControlAccessAction());
        actions.put("persname",new HandlePersnameAction());
        actions.put("subject",new HandleSubjectAction());
        actions.put("genreform",new HandleGenreAction());
        actions.put("geogname",new HandleGeognameAction());
        actions.put("famname",new HandleFamname());

        actions.put("function",new HandleFunction());
        actions.put("occupation",new HandleOccupation());

        actions.put("bibliography",new BYU_HandleBibliographyAction());
        actions.put("index",new BYU_HandleIndexAction());
        actions.put("list",new BYU_HandleListAction());

        actions.put("chronlist",new BYU_HandleChronListAction());
        actions.put("dsc",new BYU_HandleDscAction());
        actions.put("descgrp",new BYU_HandleDescgrpAction());
        actions.put("eadid",new HandleEadidAction());
        actions.put("eadheader",new BYU_HandleEadheaderAction());


        actions.put("unittitle",new BYU_HandleUnittitleAction());
        actions.put("unitid",new HandleUnitidAction());
        actions.put("unitdate",new BYU_HandleUnitdateAction());
        actions.put("langmaterial",new HandleLangmaterialAction());
        actions.put("materialspec",new BYU_HandleNotesAction());
        actions.put("origination",new HandleOriginationAction());
        actions.put("physloc",new BYU_HandlePhyslocAction());
        actions.put("abstract",new BYU_HandleAbstractAction());
        actions.put("dao",new HandleDaoAction());
        actions.put("daoloc",new BYU_HandleDaolocAction());
        actions.put("daogrp",new BYU_HandleDaogrpAction());

        actions.put("filedesc",new BYU_HandleFiledescAction());
        actions.put("profiledesc",new BYU_HandleProfiledescAction());
        actions.put("revisiondesc",new HandleRevisiondescAction());
        actions.put("langusage",new HandleLangusageAction());

        actions.put("editionstmt",new HandleEditionstmtAction());
        actions.put("notestmt",new HandleNotestmtAction());
        actions.put("publicationstmt",new HandlePublicationstmtAction());
        actions.put("seriesstmt",new HandleSeriesstmtAction());
        actions.put("titlestmt",new HandleTitlestmtAction());

    }

    public static String getElementType(String elemName){
        return (String) elementTypes.get(elemName);
    }

    public static Action getActionbyElementName(String elemName){

        String elementType = (String) elementTypes.get(elemName);
        Action action = (Action)getActionbyElementType(elementType);

        return action;
    }

    public static Action getActionbyElementType(String type){
        return (Action) actions.get(type);
    }

    public static String getElementNameFromClassName(String className){
        int a = className.lastIndexOf(".");
        if(a>=0){
            return className.substring(a+1);
        }
        else return null;
    }

    public static Action getActionFromClass(Object elem){
        if(elem==null)
            return null;
        String className;
        if(elem instanceof JAXBElement){
            elem = ((JAXBElement)elem).getValue();
        }
        className = elem.getClass().getName();
        //System.out.println("cname"+className);
        return getActionbyElementName((getElementNameFromClassName(className.toLowerCase())));
    }

    public static void addIdPairs(String before, String after){
        if(before==null || before.length()==0 || after == null)
            return;
        EADIngest2.idPairs.put(before,after);
    }

	public static void setReferenceTOParentNotes(ArchDescriptionNotes referenceTOParentNotes) {
		BYU_EADInfo.referenceTOParentNotes = referenceTOParentNotes;
	}

	public static ArchDescriptionNotes getReferenceTOParentNotes() {
		return referenceTOParentNotes;
	}

	public static void setReferenceTOResources(Resources referenceTOResources) {
		BYU_EADInfo.referenceTOResources = referenceTOResources;
	}

	public static Resources getReferenceTOResources() {
		return referenceTOResources;
	}

	public static void setContext(JAXBContext context) {
		BYU_EADInfo.context = context;
	}

	public static JAXBContext getContext() throws JAXBException{
		if(context==null){
				context = JAXBContext.newInstance("org.archiviststoolkit.structure.EAD");
		}
		return context;
	}

	static void setReport(String report) {
		BYU_EADInfo.report = report;
	}

	static String getReport() {
		return report;
	}
}