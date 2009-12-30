package edu.byu.plugins.importExport.eadImport;

import java.util.HashMap;
import javax.xml.bind.JAXBException;
import org.archiviststoolkit.model.Resources;
import org.archiviststoolkit.structure.EAD.Archdesc;
import org.archiviststoolkit.structure.EAD.Ead;

import org.archiviststoolkit.structure.EAD.Eadheader;
import org.archiviststoolkit.swing.InfiniteProgressPanel;
import org.archiviststoolkit.importer.EADInfo;
import org.archiviststoolkit.importer.Action;
import org.archiviststoolkit.importer.HandleEadheaderAction;
import org.archiviststoolkit.importer.HandleArchDescAction;

public class BYU_EADImport {

	public static HashMap idPairs;

	public Resources convertFileToResourceNew(Ead ead,Resources resource, InfiniteProgressPanel progressPanel) throws JAXBException{

		idPairs = new HashMap();
		BYU_EADInfo.sequence = 0;
		BYU_EADInfo.setReferenceTOResources(resource);

		Archdesc archDesc = null;
		Eadheader eadHeader = null;

		archDesc = ead.getArchdesc();
		eadHeader = ead.getEadheader();
		Action a = new HandleEadheaderAction();
		a.processElement(resource, eadHeader, progressPanel);
		a = new BYU_HandleArchDescAction();
		a.processElement(resource, archDesc, progressPanel);

		return resource;
	}
}