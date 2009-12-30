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

public class Handle655Action implements MARCXMLAction
{
    public void processElement(Resources resource, Object o, InfiniteProgressPanel progressPanel)
    {
    DataFieldType dataField = (DataFieldType) o;
    String indicator2 = dataField.getInd2();
    String thesaurus;
    String subjects[] = {"3","a","b","c","v","x","y","z"};
    Vector <String> subjectsV;
    subjectsV = BYU_MARCIngest.arrayToVector(subjects);
    String subjectS = BYU_MARCIngest.getSpecificSubCodeValuesAsDelimitedString(dataField,subjectsV,"--");

    if(!indicator2.equals("7"))
    thesaurus = (String) BYU_MARCIngest.thesaurusMappings.get(indicator2);
    else
    thesaurus = BYU_MARCIngest.getSubCodeValue(dataField,"2");
    try{
        BYU_MARCIngest.addSubjects(resource,subjectS,"Genre / Form (655)",thesaurus);
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