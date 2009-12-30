package edu.byu.plugins.importExport.marcxmlImport;

import java.util.List;

import java.util.Vector;

import org.archiviststoolkit.model.ArchDescriptionNotes;
import org.archiviststoolkit.model.Resources;
import org.archiviststoolkit.model.UnsupportedRepeatingDataTypeException;
import org.archiviststoolkit.structure.MARCXML.DataFieldType;
import org.archiviststoolkit.structure.NotesEtcTypes;
import org.archiviststoolkit.util.NoteEtcTypesUtils;
import org.archiviststoolkit.swing.InfiniteProgressPanel;
import org.archiviststoolkit.importer.MARCXML.MARCXMLAction;

public class Handle222Action implements MARCXMLAction
{
    public static int titlePrecedence = 2;

    public void processElement(Resources resource, Object o, InfiniteProgressPanel progressPanel) throws UnsupportedRepeatingDataTypeException {
		DataFieldType dataField = (DataFieldType) o;
		String titles[] = {"a","b"};
		Vector <String> titlesV;
		titlesV = BYU_MARCIngest.arrayToVector(titles);
		String title = BYU_MARCIngest.getSpecificSubCodeValuesAsDelimitedString(dataField,titlesV,",");
		if(BYU_MARCIngest.resourceTitle==null){
			resource.setTitle(title);
			BYU_MARCIngest.resourceTitle = (DataFieldType)o;
			BYU_MARCIngest.resourceTitlePriority=this.titlePrecedence;
		}
		else if(BYU_MARCIngest.resourceTitle!=null && BYU_MARCIngest.resourceTitlePriority<this.titlePrecedence){
			DataFieldType resTitle = BYU_MARCIngest.getResourceTitleTag();
			BYU_MARCIngest.resourceTitle = (DataFieldType)o;
			BYU_MARCIngest.resourceTitlePriority=this.titlePrecedence;
			BYU_MARCIngest.processElement(resTitle,resource,true);
			resource.setTitle(title);
		}
		else{
			ArchDescriptionNotes adn = new ArchDescriptionNotes(resource,
					"Key Title",
					NotesEtcTypes.DATA_TYPE_NOTE,resource.getRepeatingData().size()+1,
					NoteEtcTypesUtils.lookupNoteEtcTypeByCannonicalName(NotesEtcTypes.CANNONICAL_NAME_GENERAL_NOTE),
					title);
			resource.addRepeatingData(adn);
		}

	}
    public List getChildren(Object element)
    {
        return null;
    }
}
