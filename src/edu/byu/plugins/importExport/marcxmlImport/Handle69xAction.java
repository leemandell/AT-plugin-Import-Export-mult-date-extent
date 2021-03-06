package edu.byu.plugins.importExport.marcxmlImport;

import java.util.List;

import org.archiviststoolkit.exceptions.UnknownLookupListException;
import org.archiviststoolkit.exceptions.ValidationException;
import org.archiviststoolkit.model.Resources;
import org.archiviststoolkit.mydomain.PersistenceException;
import org.archiviststoolkit.structure.MARCXML.DataFieldType;
import org.archiviststoolkit.swing.InfiniteProgressPanel;
import org.archiviststoolkit.importer.MARCXML.MARCXMLAction;

public class Handle69xAction implements MARCXMLAction
{
    public void processElement(Resources resource, Object o, InfiniteProgressPanel progressPanel)
    {
    DataFieldType dataField = (DataFieldType) o;
    String subjectS = BYU_MARCIngest.getAllSubCodeValuesAsDelimitedString(dataField,"--");
    String source = "local";

    try{
        BYU_MARCIngest.addSubjects(resource,subjectS,"Topical Term (650)",source);
    }
    catch (UnknownLookupListException ulle)
    {
        ulle.printStackTrace();   
    }
    catch (PersistenceException pe)
    {
        pe.printStackTrace();
    }
    catch (ValidationException ve)
    {
        ve.printStackTrace();
    }        
    }
    public List getChildren(Object element)
    {
        return null;
    }
}