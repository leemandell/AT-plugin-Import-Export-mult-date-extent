package edu.byu.plugins.importExport.eadImport;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.archiviststoolkit.model.ArchDescription;
import org.archiviststoolkit.structure.EAD.Profiledesc;
import org.archiviststoolkit.swing.InfiniteProgressPanel;
import org.archiviststoolkit.importer.Action;
import org.archiviststoolkit.importer.EADInfo;

public class BYU_HandleProfiledescAction implements Action
{
    public void processElement(ArchDescription archDescription, Object o, InfiniteProgressPanel progressPanel){
        if(o instanceof JAXBElement)
            o = ((JAXBElement)o).getValue();
        BYU_EADInfo eadInfo = new BYU_EADInfo();
        for (Object eadElem: getChildren(o)) {
            Action a =
                eadInfo.getActionFromClass(eadElem);
            if (a != null) {
                a.processElement(archDescription, eadElem, progressPanel);
            }
        }
    }
    public List getChildren(Object element)
    {
        Profiledesc profileDesc = (Profiledesc)element;
        List list = new ArrayList();
        if(profileDesc.getDescrules()!=null)
            list.add(profileDesc.getDescrules());
        if(profileDesc.getLangusage()!=null)
            list.add(profileDesc.getLangusage());
        if(profileDesc.getCreation()!=null)
            list.add(profileDesc.getCreation());
        return list;
    }
}