package edu.byu.plugins.importExport.marcxmlImport;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.archiviststoolkit.exceptions.UnknownLookupListException;
import org.archiviststoolkit.model.Names;
import org.archiviststoolkit.model.Resources;
import org.archiviststoolkit.mydomain.PersistenceException;
import org.archiviststoolkit.structure.MARCXML.DataFieldType;
import org.archiviststoolkit.swing.InfiniteProgressPanel;
import org.archiviststoolkit.importer.MARCXML.MARCXMLAction;

public class Handle720Action implements MARCXMLAction
{
    public void processElement(Resources resource, Object o, InfiniteProgressPanel progressPanel)
    {
        DataFieldType dataField = (DataFieldType) o;
        String indicator1 = dataField.getInd1();
        String a = BYU_MARCIngest.getSubCodeValue(dataField,"a");
        String e = BYU_MARCIngest.getSubCodeValue(dataField,"e");
        String four = BYU_MARCIngest.getSubCodeValue(dataField,"4");
        Names name = new Names();
        String nameType = "";
        if(indicator1.equals("2")){
            nameType = Names.CORPORATE_BODY_TYPE;
            name.setCorporatePrimaryName(a);
            //name.setSortName(a);
        }
        else{
            nameType = Names.PERSON_TYPE;
            name.setPersonalPrimaryName(a);
            //name.setSortName(a);
        }
        String function = "Creator";
        String role = "unknown";

        String type = BYU_MARCIngest.addItemToList(Names.class,"nameType",nameType);
        name.setNameType(type);

        
        try{
            BYU_MARCIngest.addName(resource,name,function,role,null);
        }
        catch (UnknownLookupListException ulle){
            ulle.printStackTrace();
        }
        catch (PersistenceException pe){
            pe.printStackTrace();
        } catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}        
        
    }
    public List getChildren(Object element)
    {
        return null;
    }
}