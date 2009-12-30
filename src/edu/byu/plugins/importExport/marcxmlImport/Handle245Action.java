package edu.byu.plugins.importExport.marcxmlImport;

import java.util.List;

import java.util.Vector;

import org.archiviststoolkit.model.Resources;
import org.archiviststoolkit.model.ArchDescriptionDates;
import org.archiviststoolkit.structure.MARCXML.DataFieldType;
import org.archiviststoolkit.swing.InfiniteProgressPanel;
import org.archiviststoolkit.importer.MARCXML.MARCXMLAction;

public class Handle245Action implements MARCXMLAction
{
    public static int titlePrecedence = 4;
    public void processElement(Resources resource, Object o, InfiniteProgressPanel progressPanel){

		System.out.println("using custom importer");
		
        DataFieldType dataField = (DataFieldType) o;
        String titles[] = {"a","b","h","k","n","p","s"};
        Vector <String> titlesV;
        titlesV = BYU_MARCIngest.arrayToVector(titles);
        String title = BYU_MARCIngest.getSpecificSubCodeValuesAsDelimitedString(dataField,titlesV,",");

        String dates[] = {"f","g"};
        Vector <String> datesV;
        datesV = BYU_MARCIngest.arrayToVector(dates);
        String dateExp = BYU_MARCIngest.getSpecificSubCodeValuesAsDelimitedString(dataField,datesV,",");
        
//        resource.setDateExpression(dateExp);
		ArchDescriptionDates dateRecord = BYU_MARCIngest.getDateRecord(resource);
		dateRecord.setDateExpression(dateExp);
		resource.addArchdescriptionDate(dateRecord);
        
        if(BYU_MARCIngest.resourceTitle==null){
            resource.setTitle(title);
            BYU_MARCIngest.resourceTitle = (DataFieldType)o;
            BYU_MARCIngest.resourceTitlePriority=this.titlePrecedence;System.out.println("here1");
        }
        else{            
            DataFieldType resTitle = BYU_MARCIngest.getResourceTitleTag();
            System.out.println("here2");
            BYU_MARCIngest.resourceTitle = (DataFieldType)o;
            BYU_MARCIngest.resourceTitlePriority=this.titlePrecedence;
            BYU_MARCIngest.processElement(resTitle,resource,true);
            resource.setTitle(title);
        }
    
    }
    public List getChildren(Object element)
    {
        return null;
    }
}
