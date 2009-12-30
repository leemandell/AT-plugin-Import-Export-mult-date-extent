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

public class Handle522Action implements MARCXMLAction
{
    public void processElement(Resources resource, Object o, InfiniteProgressPanel progressPanel) throws UnsupportedRepeatingDataTypeException {
		DataFieldType dataField = (DataFieldType) o;
		String titles[] = {"a"};
		Vector <String> titlesV;
		titlesV = BYU_MARCIngest.arrayToVector(titles);
		String noteTitle = "Geographic Coverage";
		String noteType = "General note";
		String title = BYU_MARCIngest.getSpecificSubCodeValuesAsDelimitedString(dataField,titlesV,",");
		ArchDescriptionNotes adn = new ArchDescriptionNotes(resource,
				noteTitle,
				NotesEtcTypes.DATA_TYPE_NOTE,resource.getRepeatingData().size()+1,
				NoteEtcTypesUtils.lookupNoteEtcTypeByCannonicalName(noteType),
				title);
		
        adn.setPersistentId(resource.getNextPersistentIdAndIncrement());
        resource.addRepeatingData(adn);
	}
    public List getChildren(Object element)
    {
        return null;
    }
}