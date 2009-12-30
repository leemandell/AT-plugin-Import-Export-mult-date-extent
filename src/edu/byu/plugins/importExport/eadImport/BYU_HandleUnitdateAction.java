package edu.byu.plugins.importExport.eadImport;

import java.util.List;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormatSymbols;

import javax.xml.bind.JAXBElement;

import org.archiviststoolkit.swing.InfiniteProgressPanel;
import org.archiviststoolkit.model.ArchDescription;
import org.archiviststoolkit.model.ArchDescriptionDates;
import org.archiviststoolkit.structure.EAD.Unitdate;
import org.archiviststoolkit.util.StringHelper;
import org.archiviststoolkit.importer.Action;
import org.archiviststoolkit.importer.EADHelper;
import org.archiviststoolkit.importer.ImportUtils;
import org.archiviststoolkit.exceptions.UnknownLookupListException;

public class BYU_HandleUnitdateAction implements Action{

	private boolean debug = false;

	public void processElement(ArchDescription archDescription, Object o, InfiniteProgressPanel progressPanel) {
		System.out.println("Using custom date importer");
		if(o instanceof JAXBElement)
			o = ((JAXBElement)o).getValue();
//        ApplicationFrame.getInstance().updateProgressMessageSecondLine("Importing unitdate element");
		Unitdate unitdate;
		if(o instanceof JAXBElement)
			unitdate = (Unitdate)((JAXBElement)o).getValue();
		else
			unitdate = (Unitdate) o;

		ArchDescriptionDates newDate = new ArchDescriptionDates(archDescription);
		archDescription.addArchdescriptionDate(newDate);

		String unitDateText =null;
		if(unitdate!=null) {
			unitDateText = (String) EADHelper.getClassFromList(unitdate.getContent(),String.class);
			if(unitDateText !=null){
				EADHelper.setProperty(newDate,ArchDescriptionDates.PROPERTYNAME_DATE_EXPRESSION, unitDateText,null);
			}
		}

		//set calendar, era and certainty
		EADHelper.setProperty(newDate, ArchDescriptionDates.PROPERTYNAME_CALENDAR, unitdate.getCalendar(), null);
		EADHelper.setProperty(newDate, ArchDescriptionDates.PROPERTYNAME_ERA, unitdate.getEra(), null);
		if (unitdate.getCertainty() != null) {
			newDate.setCertainty(true);
		}


		String type = unitdate.getType();
		String normal= unitdate.getNormal();
		if (normal != null && normal.length() > 0) {
			String dateStart;
			String dateEnd;
			String[] parts = normal.split("/");
			if (parts.length == 1) {
				//not a date range
				dateStart = normal;
				dateEnd = normal;
			} else {
				dateStart = parts[0];
				dateEnd = parts[1];
			}
			//System.out.println("normal1"+normal);
			if(type!=null && type.equals("bulk")){
				if (debug) {
					System.out.println("hereInc");
				}
				EADHelper.setProperty(newDate, ArchDescriptionDates.PROPERTYNAME_ISOBULK_DATE_BEGIN, dateStart, false);
				EADHelper.setProperty(newDate, ArchDescriptionDates.PROPERTYNAME_ISOBULK_DATE_END, dateEnd, false);
//				if(normal!=null && normal.length()>0){
//					Integer start = archDescription.getDateBegin();
//					Integer end = archDescription.getDateEnd();
//					StringHelper.simpleParseDate(archDescription,normal, true);
//
//					if(archDescription.getDateBegin()!=null && start !=null){
//						if(start<archDescription.getDateBegin())
//							archDescription.setDateBegin(start);
//					}
//					if(archDescription.getDateEnd()!=null && end!=null){
//						if(end>archDescription.getDateEnd())
//							archDescription.setDateEnd(end);
//					}
//				}
			}
			else {
				System.out.println("hereBulk");
				EADHelper.setProperty(newDate, ArchDescriptionDates.PROPERTYNAME_ISODATE_BEGIN, dateStart, false);
				EADHelper.setProperty(newDate, ArchDescriptionDates.PROPERTYNAME_ISODATE_END, dateEnd, false);
//				if(normal!=null && normal.length()>0){
//					StringHelper.simpleParseDate(archDescription,normal, false);
//				}
			}
		}


	}

	public List getChildren(Object element){
		return null;
	}
}