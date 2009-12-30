package edu.byu.plugins.importExport.eadImport;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.archiviststoolkit.swing.InfiniteProgressPanel;
import org.archiviststoolkit.model.ArchDescription;
import org.archiviststoolkit.model.ResourcesCommon;
import org.archiviststoolkit.model.ArchDescriptionPhysicalDescriptions;
import org.archiviststoolkit.model.AccessionsResourcesCommon;
import org.archiviststoolkit.structure.EAD.Dimensions;
import org.archiviststoolkit.structure.EAD.Extent;
import org.archiviststoolkit.structure.EAD.Physdesc;
import org.archiviststoolkit.structure.EAD.Physfacet;
import org.archiviststoolkit.util.StringHelper;
import org.archiviststoolkit.importer.Action;
import org.archiviststoolkit.importer.EADInfo;
import org.archiviststoolkit.importer.EADHelper;
import org.archiviststoolkit.importer.HandlePhysdescAction;

public class BYU_HandlePhysdescAction implements Action {

    public void processElement(ArchDescription archDescription, Object o, InfiniteProgressPanel progressPanel)
	{
		System.out.println("using custom phys desc importer");
		if(o instanceof JAXBElement)
			o = ((JAXBElement)o).getValue();
//        ApplicationFrame.getInstance().updateProgressMessageSecondLine("Importing physdesc element");
		ArrayList physDescChildren = (ArrayList) getChildren(o);

		Iterator it;
		Object eadElem = null;
		Action action = null;
		String level="";
		it = physDescChildren.iterator();
		//EADInfo eadInfo = new EADInfo();
		StringBuffer containerSummary = new StringBuffer();
		Object ob = null;

		ArchDescriptionPhysicalDescriptions newPhysicalDescription = new ArchDescriptionPhysicalDescriptions(archDescription);
		((AccessionsResourcesCommon)archDescription).addPhysicalDesctiptions(newPhysicalDescription);
		while (it.hasNext()) {
			ob = it.next();
			if(ob instanceof JAXBElement){
				ob = ((JAXBElement)ob).getValue();
			}

			if (ob instanceof String){
				containerSummary.append((String)ob);
			} else if(ob instanceof Dimensions){
				newPhysicalDescription.setDimensions(EADHelper.ObjectNodetoString(ob));
//				action = BYU_EADInfo.getActionFromClass(ob);
//				action.processElement(archDescription, ob, progressPanel);
			} else if(ob instanceof Physfacet){
				newPhysicalDescription.setPhysicalDetail(EADHelper.ObjectNodetoString(ob));
//				action = BYU_EADInfo.getActionFromClass(ob);
//				action.processElement(archDescription, ob, progressPanel);
			} else{
				boolean parseExtent=false;
				if(ob instanceof Extent){
					parseExtent = parseExtentInformation(newPhysicalDescription, (Extent)ob);
				}
				if(!parseExtent)
					newPhysicalDescription.setContainerSummary(EADHelper.ObjectNodetoString(ob));
			}
		}

//		((ResourcesCommon)archDescription).setContainerSummary(containerSummary.toString());

	}
	public static boolean parseExtentInformation(ArchDescriptionPhysicalDescriptions archDescriptionPhysicalDescriptions, Extent extent){
		String extentString = (String)EADHelper.getClassFromList(extent.getContent(), String.class);
		if(extentString ==null)
			return false;
		if(!(extentString.contains(" ")))
			return false;

		int firstSpace = extentString.indexOf(" ");
		String partOne = null;
		String partTwo = null;

		partOne = extentString.substring(0,firstSpace);
		partOne = partOne.trim();
		if(extentString.length()>firstSpace+1){
			partTwo = extentString.substring(firstSpace+1,extentString.length());
			partTwo = partTwo.trim();
		}
		else return false;



		if(StringHelper.isNotEmpty(partOne) && StringHelper.isNotEmpty(partTwo)){
			if(partTwo.length()>20)
				return false;
			Double partOneDouble = null;
			try{
				partOneDouble = Double.parseDouble(partOne);
				EADHelper.setProperty(archDescriptionPhysicalDescriptions,"extentNumber",partOneDouble);

				EADHelper.setProperty(archDescriptionPhysicalDescriptions,"extentType",partTwo);

			}
			catch (NumberFormatException pe){
				pe.printStackTrace();
				return false;
			}
			return true;
		}
		return false;
	}
	public static void main(String args[]){
		Extent extent = new Extent();
		extent.getContent().add("1.0 linear feet");
		boolean parse = BYU_HandlePhysdescAction.parseExtentInformation(null,extent);
	}
	public List getChildren(Object element)
	{
		return ((Physdesc)element).getContent();
	}
}