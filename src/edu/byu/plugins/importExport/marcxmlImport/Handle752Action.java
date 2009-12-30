package edu.byu.plugins.importExport.marcxmlImport;

import java.util.List;
import java.util.Vector;
import org.archiviststoolkit.exceptions.UnknownLookupListException;
import org.archiviststoolkit.exceptions.ValidationException;
import org.archiviststoolkit.model.Resources;
import org.archiviststoolkit.mydomain.PersistenceException;
import org.archiviststoolkit.structure.MARCXML.DataFieldType;
import org.archiviststoolkit.swing.InfiniteProgressPanel;
import org.archiviststoolkit.importer.MARCXML.MARCXMLAction;

public class Handle752Action implements MARCXMLAction
{
    public void processElement(Resources resource, Object o, InfiniteProgressPanel progressPanel)
    {
    DataFieldType dataField = (DataFieldType) o;
    String subjects[] = {"a","b","c","d","f","g","h"};
    Vector <String> subjectsV;
    subjectsV = BYU_MARCIngest.arrayToVector(subjects);
    String subjectS = BYU_MARCIngest.getSpecificSubCodeValuesAsDelimitedString(dataField,subjectsV,"--");
    String source = BYU_MARCIngest.getSubCodeValue(dataField,"2");

    try{
        BYU_MARCIngest.addSubjects(resource,subjectS,"Geographic Name (651)",source);
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