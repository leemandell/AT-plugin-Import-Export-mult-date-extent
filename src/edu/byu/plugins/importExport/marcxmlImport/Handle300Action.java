package edu.byu.plugins.importExport.marcxmlImport;

import java.util.List;

import org.archiviststoolkit.model.Resources;
import org.archiviststoolkit.model.UnsupportedRepeatingDataTypeException;
import org.archiviststoolkit.model.ArchDescriptionPhysicalDescriptions;
import org.archiviststoolkit.structure.MARCXML.DataFieldType;
import org.archiviststoolkit.util.StringHelper;
import org.archiviststoolkit.swing.InfiniteProgressPanel;
import org.archiviststoolkit.importer.MARCXML.MARCXMLAction;

public class Handle300Action implements MARCXMLAction
{
    public void processElement(Resources resource, Object o, InfiniteProgressPanel progressPanel) throws UnsupportedRepeatingDataTypeException {
		DataFieldType dataField = (DataFieldType) o;
//		String title = BYU_MARCIngest.getAllSubCodeValuesAsDelimitedString(dataField,",");
		String subfield_a = BYU_MARCIngest.getSpecificSubCodeValuesAsDelimitedString(dataField, "a", " ");
		String subfield_e = BYU_MARCIngest.getSubCodeValue(dataField, "e");
		String subfield_f = BYU_MARCIngest.getSubCodeValue(dataField, "f");
		String subfield_3 = BYU_MARCIngest.getSubCodeValue(dataField, "3");
		String subfield_g = BYU_MARCIngest.getSubCodeValue(dataField, "g");

		ArchDescriptionPhysicalDescriptions physDesc = new ArchDescriptionPhysicalDescriptions(resource);
		physDesc.setPhysicalDetail(BYU_MARCIngest.getSubCodeValue(dataField, "b"));
		physDesc.setDimensions(BYU_MARCIngest.getSubCodeValue(dataField, "c"));

		String af = StringHelper.concat(" ", subfield_a, subfield_e);
		String eg;
		if (subfield_g.length() == 0) {
			eg = subfield_e;
		} else {
			eg = StringHelper.concat(" ", subfield_e, "(" + subfield_g + ")");
		}

		String afeg = StringHelper.concat(" +", af, eg);
		physDesc.setContainerSummary(StringHelper.concat(": ", subfield_3, afeg));
		resource.addPhysicalDesctiptions(physDesc);

//		if(resource.getContainerSummary()==null || resource.getContainerSummary().length()==0 ){
//			resource.setContainerSummary(title);
//		}
//		else
//		{
//			NotesEtcTypes noteType = NoteEtcTypesUtils.lookupNoteEtcTypeByCannonicalName("General note");
//			ArchDescriptionNotes adn = new ArchDescriptionNotes(resource,"Additonal Extent Statement",NotesEtcTypes.DATA_TYPE_NOTE,resource.getRepeatingData().size()+1,noteType,title);
//	        adn.setPersistentId(resource.getNextPersistentIdAndIncrement());
//			resource.addRepeatingData(adn);
//		}
	}
    public List getChildren(Object element)
    {
        return null;
    }
}