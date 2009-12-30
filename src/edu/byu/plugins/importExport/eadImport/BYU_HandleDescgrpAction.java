package edu.byu.plugins.importExport.eadImport;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.archiviststoolkit.model.ArchDescription;
import org.archiviststoolkit.structure.EAD.Descgrp;
import org.archiviststoolkit.swing.InfiniteProgressPanel;
import org.archiviststoolkit.importer.Action;
import org.archiviststoolkit.importer.EADInfo;

public class BYU_HandleDescgrpAction implements Action
{
    public void processElement(ArchDescription archDescription, Object o, InfiniteProgressPanel progressPanel)
    {
        if(o instanceof JAXBElement)
            o = ((JAXBElement)o).getValue();
        BYU_EADInfo eadInfo = new BYU_EADInfo();
        Iterator it;
        Object eadElem = null;
        Action action = null;
        ArrayList descGrpChildren = (ArrayList) getChildren(o);
        it = descGrpChildren.iterator();

        while(it.hasNext()){
            eadElem = it.next();
            action = eadInfo.getActionFromClass(eadElem);
            if(null!=action){
                action.processElement(archDescription,eadElem, progressPanel);
            }
        }
    }
    public List getChildren(Object element)
    {
        return ((Descgrp)element).getAddressOrChronlistOrList();
    }
}