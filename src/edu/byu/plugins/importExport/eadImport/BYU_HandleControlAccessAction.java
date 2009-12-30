package edu.byu.plugins.importExport.eadImport;

import java.util.List;

import javax.xml.bind.JAXBElement;

import org.archiviststoolkit.model.ArchDescription;
import org.archiviststoolkit.structure.EAD.Controlaccess;
import org.archiviststoolkit.swing.InfiniteProgressPanel;
import org.archiviststoolkit.model.Resources;
import org.archiviststoolkit.model.ResourcesComponents;
import org.archiviststoolkit.structure.EAD.Head;
import org.archiviststoolkit.importer.Action;
import org.archiviststoolkit.importer.EADInfo;
import org.archiviststoolkit.importer.EADHelper;

public class BYU_HandleControlAccessAction implements Action {
    public  void processElement(ArchDescription archDescription, Object o, InfiniteProgressPanel progressPanel){
        if(o instanceof JAXBElement)
            o = ((JAXBElement)o).getValue();
//        ApplicationFrame.getInstance().updateProgressMessageSecondLine("Import controlaccess element");
        BYU_EADInfo eadInfo = new BYU_EADInfo();
        for(Object eadElem:getChildren(o)){
            Action action = eadInfo.getActionFromClass(eadElem);
            if(null!=action){
                action.processElement(archDescription,eadElem, progressPanel);
            }
        else{
            if(archDescription instanceof Resources)
                ((Resources)archDescription).setEadIngestProblem(((Resources)archDescription).getEadIngestProblem()+EADHelper.ObjectNodetoString(eadElem));
            else if(archDescription instanceof Resources)
                ((ResourcesComponents)archDescription).setEadIngestProblem(((ResourcesComponents)archDescription).getEadIngestProblem()+EADHelper.ObjectNodetoString(eadElem));
        }
        }
    }
    public List getChildren(Object element){
        Controlaccess ca = (Controlaccess)element;
        List list = ca.getAddressOrChronlistOrList();
        Head h = ca.getHead();
        if(h!=null)
            list.add(h);

        return list;
    }
}