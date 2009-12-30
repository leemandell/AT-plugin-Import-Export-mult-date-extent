package edu.byu.plugins.importExport.eadImport;

import java.lang.reflect.InvocationTargetException;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.apache.commons.beanutils.PropertyUtils;

import org.archiviststoolkit.swing.InfiniteProgressPanel;
import org.archiviststoolkit.model.*;
import org.archiviststoolkit.structure.EAD.Abstract;
import org.archiviststoolkit.structure.EAD.Chronlist;
import org.archiviststoolkit.structure.EAD.Daodesc;
import org.archiviststoolkit.structure.EAD.Dimensions;
import org.archiviststoolkit.structure.EAD.Extent;
import org.archiviststoolkit.structure.EAD.Head;
import org.archiviststoolkit.structure.EAD.Legalstatus;
import org.archiviststoolkit.structure.EAD.Materialspec;
import org.archiviststoolkit.structure.EAD.Note;
import org.archiviststoolkit.structure.EAD.Physdesc;
import org.archiviststoolkit.structure.NotesEtcTypes;
import org.archiviststoolkit.util.StringHelper;
import org.archiviststoolkit.util.NoteEtcTypesUtils;
import org.archiviststoolkit.importer.*;

public class BYU_HandleNotesAction implements Action {

    public void processElement(ArchDescription res, Object note, InfiniteProgressPanel progressPanel) {
        if(note instanceof JAXBElement)
            note = ((JAXBElement)note).getValue();
//        ApplicationFrame.getInstance().updateProgressMessageSecondLine("Importing notes");
        ArchDescriptionNotes notes = new ArchDescriptionNotes(res);
        notes.setSequenceNumber(res.getRepeatingData().size());
        Head h = null;
        String head = "";
        List list = new ArrayList();
        StringBuffer nodeValue = new StringBuffer();
        String scope = "";
        Action action;
        BYU_EADInfo eadInfo = new BYU_EADInfo();
        String typeS = NoteEtcTypesUtils.getNoteTypeFromEAD(note);
        try {
            NotesEtcTypes type = NoteEtcTypesUtils.lookupNoteEtcTypeByCannonicalName(typeS);
            PropertyUtils.setProperty(notes, "notesEtcType", type);
            if (note instanceof Abstract || note instanceof Materialspec || note instanceof Physdesc || note instanceof Legalstatus || note instanceof Dimensions) {
                list = (List)PropertyUtils.getProperty(note, "content");
            } else {
                if((!(note instanceof Note))&&(!(note instanceof Daodesc))){
                    h = (Head)PropertyUtils.getProperty(note, "head");
                    list = (List)PropertyUtils.getProperty(note, "addressOrChronlistOrList");}
                else
                    list = (List)PropertyUtils.getProperty(note, "MBlocks");
            }

            String audience = "";
            audience = (String)PropertyUtils.getProperty(note, "audience");
            if(StringHelper.isNotEmpty(audience) && audience.equalsIgnoreCase("internal")){
                notes.setInternalOnly(true);
            }


        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return;
        }
        catch (UnsupportedRepeatingDataTypeException e){
            e.printStackTrace();
            return;
        }

        if (h != null)
            head = (String)EADHelper.getClassFromList(h.getContent(), String.class);
        if (head != null &&  head.length()>0){
            EADHelper.setProperty(notes,"title",head);
            }
        else if(StringHelper.isNotEmpty(typeS))
            notes.setTitle(typeS);

        EADHelper.setProperty(notes,"repeatingDataType",NotesEtcTypes.DATA_TYPE_NOTE);



        BYU_EADInfo.setReferenceTOParentNotes(notes);
        notes.setPersistentId(BYU_EADInfo.getReferenceTOResources().getNextPersistentIdAndIncrement());
        notes.setSequenceNumber(res.getRepeatingData().size());
        String id = (String)EADHelper.getProperty(note,"id");
        BYU_EADInfo.addIdPairs(id,notes.getPersistentId());
        //res.addRepeatingData(notes);

        for (Object o: list) {
            action = eadInfo.getActionFromClass(o);
            if (o instanceof Chronlist) {
                notes.setMultiPart(true);
                makeSubNote(nodeValue,notes);
                nodeValue = new StringBuffer();
                action.processElement(res, o, progressPanel);
            }
            else if(o instanceof org.archiviststoolkit.structure.EAD.List){
                notes.setMultiPart(true);
                makeSubNote(nodeValue,notes);
                nodeValue = new StringBuffer();
                action.processElement(res,o, progressPanel);
            }
            else if (action != null && action instanceof BYU_HandleNotesAction)
                action.processElement(res, o, progressPanel);
            else {
            	if(o instanceof JAXBElement)
            		o = ((JAXBElement)o).getValue();
            	if(o instanceof Extent){
					ArchDescriptionPhysicalDescriptions newPhysicalDescription = new ArchDescriptionPhysicalDescriptions(res);
					((AccessionsResourcesCommon)res).addPhysicalDesctiptions(newPhysicalDescription);
					BYU_HandlePhysdescAction.parseExtentInformation(newPhysicalDescription, (Extent)o);
            		// extent already parsed
            	}
            	else{
                scope = EADHelper.ObjectNodetoStringWithTags(o);
                scope = StringHelper.cleanUpWhiteSpace(scope);
            	scope =  scope.replaceAll("\n", "").replaceAll("<p>", "").replaceAll("</p>","\n\n");
                nodeValue.append(scope);
            	}
            }
        }

        if (notes.getMultiPart()) {
        	res.addRepeatingData(notes);
        	makeSubNote(nodeValue,notes);
        }
        else {
            EADHelper.setProperty(notes,"noteContent",nodeValue.toString(),false);
            if (nodeValue.toString().length()>0)
            	res.addRepeatingData(notes);
        }
    }

    private void makeSubNote(StringBuffer nodeValue, ArchDescriptionNotes notes){
        if(nodeValue==null || nodeValue.length()==0)
            return;
        ArchDescriptionNotes subNote = new ArchDescriptionNotes(notes, NotesEtcTypes.DATA_TYPE_NOTE);
        subNote.setBasic(true);
        EADHelper.setProperty(subNote,"noteContent",nodeValue.toString(),false);
        subNote.setPersistentId(BYU_EADInfo.getReferenceTOResources().getNextPersistentIdAndIncrement());
        subNote.setSequenceNumber(notes.getChildren().size());
        notes.addRepeatingData(subNote);
    }



    public List getChildren(Object element) {
        return null;
    }


}