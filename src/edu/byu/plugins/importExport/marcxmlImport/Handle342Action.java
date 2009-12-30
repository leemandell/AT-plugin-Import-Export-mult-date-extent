package edu.byu.plugins.importExport.marcxmlImport;

import java.util.List;

import org.archiviststoolkit.model.ArchDescriptionNotes;
import org.archiviststoolkit.model.Resources;
import org.archiviststoolkit.model.UnsupportedRepeatingDataTypeException;
import org.archiviststoolkit.structure.MARCXML.DataFieldType;
import org.archiviststoolkit.structure.NotesEtcTypes;
import org.archiviststoolkit.util.NoteEtcTypesUtils;
import org.archiviststoolkit.swing.InfiniteProgressPanel;
import org.archiviststoolkit.importer.MARCXML.MARCXMLAction;

public class Handle342Action implements MARCXMLAction
{
    public void processElement(Resources resource, Object o, InfiniteProgressPanel progressPanel) throws UnsupportedRepeatingDataTypeException {
		DataFieldType dataField = (DataFieldType) o;
		String title = BYU_MARCIngest.getAllSubCodeValuesAsDelimitedString(dataField,",");
		NotesEtcTypes noteType = NoteEtcTypesUtils.lookupNoteEtcTypeByCannonicalName("General note");
		ArchDescriptionNotes adn = new ArchDescriptionNotes(resource,"Geospatial Reference Dimension",NotesEtcTypes.DATA_TYPE_NOTE,resource.getRepeatingData().size()+1,noteType,title);
        adn.setPersistentId(resource.getNextPersistentIdAndIncrement());
		resource.addRepeatingData(adn);
	}
    public List getChildren(Object element)
    {
        return null;
    }
}