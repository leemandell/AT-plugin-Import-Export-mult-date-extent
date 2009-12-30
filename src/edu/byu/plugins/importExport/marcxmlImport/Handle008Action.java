package edu.byu.plugins.importExport.marcxmlImport;
import java.util.List;

import org.archiviststoolkit.importer.MARCXML.MARCXMLAction;
import org.archiviststoolkit.model.Resources;
import org.archiviststoolkit.model.ArchDescriptionDates;
import org.archiviststoolkit.structure.MARCXML.ControlFieldType;
import org.archiviststoolkit.util.LookupListUtils;
import org.archiviststoolkit.swing.InfiniteProgressPanel;

public class Handle008Action implements MARCXMLAction
{
	public void processElement(Resources resource, Object o, InfiniteProgressPanel progressPanel)
	{
		System.out.println("in 008");
		ControlFieldType controlFieldType = (ControlFieldType)o;
		String controlCharacters = controlFieldType.getValue();
		if(controlCharacters.length()<40)
		{
			BYU_MARCIngest.importError("Invalid Control Field", progressPanel);
			return;
		}

		String dateType = controlCharacters.substring(6,7);
		String date1 = controlCharacters.substring(7,11);
		String date2 = controlCharacters.substring(11,15);
		String lang = controlCharacters.substring(35,38);
		ArchDescriptionDates dateRecord;
		try{
			if(dateType.equals("s"))
			{
				dateRecord = BYU_MARCIngest.getDateRecord(resource);
				dateRecord.setDateBegin(Integer.parseInt(date1));
				dateRecord.setDateEnd(Integer.parseInt(date1));
			}
			else if(dateType.equals("i"))
			{
				dateRecord = BYU_MARCIngest.getDateRecord(resource);
				dateRecord.setDateBegin(Integer.parseInt(date1));
				dateRecord.setDateEnd(Integer.parseInt(date2));

			}
			else if(dateType.equals("k"))
			{
				dateRecord = BYU_MARCIngest.getDateRecord(resource);
				dateRecord.setBulkDateBegin(Integer.parseInt(date1));
				dateRecord.setBulkDateEnd(Integer.parseInt(date2));
			}
		}
		catch (NumberFormatException nfe)
		{
			nfe.printStackTrace();
		}

		lang = LookupListUtils.getLookupListItemFromCode(Resources.class,"languageCode",lang);

		resource.setLanguageCode(lang);
	}


	public List getChildren(Object element)
	{
		return null;
	}
}