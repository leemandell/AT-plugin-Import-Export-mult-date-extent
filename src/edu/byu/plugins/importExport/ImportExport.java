
package edu.byu.plugins.importExport;

import org.java.plugin.Plugin;
import org.archiviststoolkit.plugin.ATPlugin;
import org.archiviststoolkit.ApplicationFrame;
import org.archiviststoolkit.exceptions.DefaultExceptionHandler;
import org.archiviststoolkit.model.Resources;
import org.archiviststoolkit.model.DigitalObjects;
import org.archiviststoolkit.util.LookupListUtils;
import org.archiviststoolkit.util.FileUtils;
import org.archiviststoolkit.importer.ImportException;
import org.archiviststoolkit.importer.ImportHandler;
import org.archiviststoolkit.dialog.ATFileChooser;
import org.archiviststoolkit.dialog.ErrorDialog;
import org.archiviststoolkit.editor.*;
import org.archiviststoolkit.swing.*;
import org.archiviststoolkit.mydomain.*;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Vector;
import java.io.File;

import edu.byu.plugins.importExport.marcxmlImport.BYU_MARCImportHandler;
import edu.byu.plugins.importExport.marcxmlExport.BYU_MARCExportHandler;
import edu.byu.plugins.importExport.eadImport.BYU_EADImportHandler;
import edu.byu.plugins.importExport.eadExport.BYU_EADExportHandler;
import edu.byu.plugins.importExport.dcExport.BYU_DCExportHandler;
import edu.byu.plugins.importExport.modsExport.BYU_MODSExportHandler;

/**
 * Archivists' Toolkit(TM) Copyright � 2005-2007 Regents of the University of California, New York University, & Five Colleges, Inc.
 * All rights reserved.
 *
 * This software is free. You can redistribute it and / or modify it under the terms of the Educational Community License (ECL)
 * version 1.0 (http://www.opensource.org/licenses/ecl1.php)
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the ECL license for more details about permissions and limitations.
 *
 *
 * Archivists' Toolkit(TM)
 * http://www.archiviststoolkit.org
 * info@archiviststoolkit.org
 *
 * A simple plugin to test the functionality of 
 *
 * Created by IntelliJ IDEA.
 *
 * @author: Nathan Stevens
 * Date: Feb 10, 2009
 * Time: 1:07:45 PM
 */

public class ImportExport extends Plugin implements ATPlugin {

	public static final String IMPORT_ACCESSIONS = "Import Accessions";
	public static final String IMPORT_MARC = "Import MARCXML";
	public static final String EXPORT_MARC = "Export MARCXML";
	public static final String IMPORT_EAD = "Import EAD";
	public static final String EXPORT_EAD = "Export EAD";
	public static final String EXPORT_DC = "Export DC";
	public static final String EXPORT_MODS = "Export MODS";

	protected ApplicationFrame mainFrame;
	protected DomainEditorFields editorField;
//	protected DomainObject record;


	// the default constructor
	public ImportExport() {
		System.out.println("Editor Panels instantiated");
	}

	// get the category this plugin belongs to
	public String getCategory() {
		return ATPlugin.IMPORT_CATEGORY;
		//return ATPlugin.DEFAULT_CATEGORY + " " + ATPlugin.EMBEDDED_EDITOR_CATEGORY;
		//return ATPlugin.IMPORT_CATEGORY;
	}

	// get the name of this plugin
	public String getName() {
		return "Custom Import/Export";
	}

	// Method to set the main frame
	public void setApplicationFrame(ApplicationFrame mainFrame) {
		this.mainFrame = mainFrame;
	}

	// Method that display the window
	public void showPlugin() {
	}

	// method to display a plugin that needs a parent frame
	public void showPlugin(Frame owner) {
	}

	// method to display a plugin that needs a parent dialog
	public void showPlugin(Dialog owner) {
	}

	// Method to return the jpanels for plugins that are in an AT editor
	public HashMap getEmbeddedPanels() {

		return null;
	}

	public void setEditorField(ArchDescriptionFields archDescriptionFields) {
	}

	// Method to set the editor field component
	public void setEditorField(DomainEditorFields domainEditorFields) {
	}

	/**
	 * Method to set the domain object for this plugin
	 */
	public void setModel(DomainObject domainObject, InfiniteProgressPanel monitor) {
	}

	/**
	 * Method to get the table from which the record was selected
	 * @param callingTable The table containing the record
	 */
	public void setCallingTable(JTable callingTable) {
	}

	/**
	 * Method to set the selected row of the calling table
	 * @param selectedRow
	 */
	public void setSelectedRow(int selectedRow) {
	}

	/**
	 * Method to set the current record number along with the total number of records
	 * @param recordNumber The current record number
	 * @param totalRecords The total number of records
	 */
	public void setRecordPositionText(int recordNumber, int totalRecords) { }

	// Method to do a specific task in the plugin
	public void doTask(String task) {

		if (task.equals(IMPORT_ACCESSIONS)) {
			final ATFileChooser filechooser = new ATFileChooser(new ImportOptionsAccessions(ImportOptionsAccessions.SUPPRESS_DATE_FORMAT), new SimpleFileFilter(".xml"));

			if (filechooser.showOpenDialog(ApplicationFrame.getInstance(), "Import") == JFileChooser.APPROVE_OPTION) {
				final DomainImportController controller = new DomainImportController();
				final ImportHandler handler = new BYU_AccessionImportXMLHandler((ImportOptionsAccessions) filechooser.getAccessory());
				Thread performer = new Thread(new Runnable() {
					public void run() {
						InfiniteProgressPanel monitor = ATProgressUtil.createModalProgressMonitor(ApplicationFrame.getInstance(), 1000, true);
						monitor.start("Importing accessions...");
						try {
							handler.importFile(filechooser.getSelectedFile(), controller, monitor);
						} catch (ImportException e) {
							monitor.close();
							new ErrorDialog(ApplicationFrame.getInstance(), "Import Problem", e).showDialog();
						} finally {
							monitor.close();
						}

						monitor = ATProgressUtil.createModalProgressMonitor(ApplicationFrame.getInstance(), 1000);
						monitor.start("Loading lookup lists...");
						try {
							LookupListUtils.loadLookupLists();
							monitor.setTextLine("Loading editors", 1);
							DomainEditorFactory.getInstance().updateDomainEditors();
						} finally {
							monitor.close();
						}
					}
				}, "ImportAccessionsXML");
				performer.start();
			}

		} else if (task.equals(IMPORT_MARC)) {
			final ATFileChooser filechooser = new ATFileChooser(new ImportOptionsMARC(), new SimpleFileFilter(".xml"));

			if (filechooser.showOpenDialog(ApplicationFrame.getInstance(), "Import") == JFileChooser.APPROVE_OPTION) {
				final DomainImportController controller = new DomainImportController();
				final BYU_MARCImportHandler handler = new BYU_MARCImportHandler((ImportOptionsMARC) filechooser.getAccessory());
				Thread performer = new Thread(new Runnable() {
					public void run() {
						InfiniteProgressPanel monitor = ATProgressUtil.createModalProgressMonitor(ApplicationFrame.getInstance(), 1000, true);
						monitor.start("Importing MARCXML...");
						try {
							handler.importFile(filechooser.getSelectedFile(), controller, monitor);
						} catch (Exception e) {
							monitor.close();
							new ErrorDialog(ApplicationFrame.getInstance(), "", e).showDialog();
						} finally {
							monitor.close();
						}
						monitor = ATProgressUtil.createModalProgressMonitor(ApplicationFrame.getInstance(), 1000);
						monitor.start("Loading lookup lists...");
						try {
							LookupListUtils.loadLookupLists();
							monitor.setTextLine("Loading editors", 1);
//							DomainEditorFactory.getInstance().updateDomainEditors();
						} finally {
							monitor.close();
						}
					}
				}, "ImportMARC");
				performer.start();
			}

		} else if (task.equals(EXPORT_MARC)) {
			DomainTableWorkSurface workSurface= ApplicationFrame.getInstance().getWorkSurfaceContainer().getCurrentWorkSurface();
			final DomainSortableTable worksurfaceTable = (DomainSortableTable)workSurface.getTable();
			if (workSurface.getClazz() != Resources.class) {
				JOptionPane.showMessageDialog(ApplicationFrame.getInstance(), "This function only works for the resources module");
			} else if (worksurfaceTable.getSelectedRowCount() == 0) {
				JOptionPane.showMessageDialog(ApplicationFrame.getInstance(), "You must select at least one resource record");
			} else {

				final int[] selectedIndexes = worksurfaceTable.getSelectedRows();
				final ExportOptionsMARC exportOptions = new ExportOptionsMARC();
				final File selectedFileOrDirectory = FileUtils.chooseFileOrDirectory(selectedIndexes.length, exportOptions);

				if (selectedFileOrDirectory != null) {
					Thread performer = new Thread(new Runnable() {
						public void run() {
							// see whether to show the cancel button
							boolean allowCancel = false;
							if(selectedIndexes.length > 1) {
								allowCancel = true;
							}

							InfiniteProgressPanel monitor = ATProgressUtil.createModalProgressMonitor(ApplicationFrame.getInstance(), 1000, allowCancel);
							monitor.start("Exporting...");
							BYU_MARCExportHandler batchMARCHandler = new BYU_MARCExportHandler(exportOptions);
							try {
								int[] selectedIndexes = worksurfaceTable.getSelectedRows();
								Vector<DomainObject> resources = new Vector<DomainObject>();
								DomainObject domainObject = null;
								DomainObject fullDomainObject = null;

								for (int loop = 0; loop < selectedIndexes.length; loop++) {
									// check to see if this operation wasnt cancelled
									if(monitor.isProcessCancelled()) {
										break;
									}

									domainObject = (worksurfaceTable.getFilteredList().get(selectedIndexes[loop]));
									resources.add(domainObject);
								}

								if(!monitor.isProcessCancelled()) {
									Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler());
									batchMARCHandler.export(selectedFileOrDirectory, resources, monitor);
								}
							} catch (Exception e) {
								monitor.close();
								new ErrorDialog("", e).showDialog();
							} finally {
								monitor.close();
							}
						}
					}, "ExportMarc");
					performer.start();
				}

			}

		} else if (task.equals(IMPORT_EAD)) {
			final ATFileChooser filechooser = new ATFileChooser(new ImportOptionsEAD(), new SimpleFileFilter(".xml"));

			if (filechooser.showOpenDialog(ApplicationFrame.getInstance(), "Import") == JFileChooser.APPROVE_OPTION) {
				final DomainImportController controller = new DomainImportController();
				final BYU_EADImportHandler handler = new BYU_EADImportHandler((ImportOptionsEAD) filechooser.getAccessory());
				Thread performer = new Thread(new Runnable() {
					public void run() {
						InfiniteProgressPanel monitor = ATProgressUtil.createModalProgressMonitor(ApplicationFrame.getInstance(), 1000, true);
						monitor.start("Importing EAD...");
						System.out.println("importing ead");
						try {
							handler.importFile(filechooser.getSelectedFile(), controller, monitor);
						} catch (Exception e) {
							monitor.close();
							new ErrorDialog(ApplicationFrame.getInstance(), "", e).showDialog();
						} finally {
							monitor.close();
						}
						monitor = ATProgressUtil.createModalProgressMonitor(ApplicationFrame.getInstance(), 1000);
						monitor.start("Loading lookup lists...");
						System.out.println("Loading lookup lists...");
						try {
							LookupListUtils.loadLookupLists();
							monitor.setTextLine("Loading editors", 1);
							System.out.println("Loading editors");
							DomainEditorFactory.getInstance().updateDomainEditors();
						} finally {
							monitor.close();
						}
					}
				}, "ImportEAD");
				performer.start();
			}
		} else if (task.equals(EXPORT_EAD)) {
			DomainTableWorkSurface workSurface= ApplicationFrame.getInstance().getWorkSurfaceContainer().getCurrentWorkSurface();
			final DomainSortableTable worksurfaceTable = (DomainSortableTable)workSurface.getTable();
			if (workSurface.getClazz() != Resources.class) {
				JOptionPane.showMessageDialog(ApplicationFrame.getInstance(), "This function only works for the resources module");
			} else if (worksurfaceTable.getSelectedRowCount() == 0) {
				JOptionPane.showMessageDialog(ApplicationFrame.getInstance(), "You must select at least one resource record");
			} else {

				final int[] selectedIndexes = worksurfaceTable.getSelectedRows();
				final ExportOptionsEAD exportOptions = new ExportOptionsEAD();
				final File selectedFileOrDirectory = FileUtils.chooseFileOrDirectory(selectedIndexes.length, exportOptions);

				if (selectedFileOrDirectory != null) {
					Thread performer = new Thread(new Runnable() {
						public void run() {
							// see whether to show the cancel button
							boolean allowCancel = false;
							if(selectedIndexes.length > 1) {
								allowCancel = true;
							}

							InfiniteProgressPanel monitor = ATProgressUtil.createModalProgressMonitor(ApplicationFrame.getInstance(), 1000, allowCancel);
							monitor.start("Exporting...");
							BYU_EADExportHandler batchEADHandler = new BYU_EADExportHandler(exportOptions);
							try {
								int[] selectedIndexes = worksurfaceTable.getSelectedRows();
								Vector<DomainObject> resources = new Vector<DomainObject>();
								DomainObject domainObject = null;
								DomainObject fullDomainObject = null;

								for (int loop = 0; loop < selectedIndexes.length; loop++) {
									// check to see if this operation wasnt cancelled
									if(monitor.isProcessCancelled()) {
										break;
									}

									domainObject = (worksurfaceTable.getFilteredList().get(selectedIndexes[loop]));
									resources.add(domainObject);
								}

								if(!monitor.isProcessCancelled()) {
									Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler());
									batchEADHandler.export(selectedFileOrDirectory, resources, monitor);
								}
							} catch (Exception e) {
								monitor.close();
								new ErrorDialog("", e).showDialog();
							} finally {
								monitor.close();
							}
						}
					}, "ExportEAD");
					performer.start();
				}

			}
		} else if (task.equals(EXPORT_DC)) {
			DomainTableWorkSurface workSurface= ApplicationFrame.getInstance().getWorkSurfaceContainer().getCurrentWorkSurface();
			final DomainSortableTable worksurfaceTable = (DomainSortableTable)workSurface.getTable();
			if (workSurface.getClazz() != DigitalObjects.class) {
				JOptionPane.showMessageDialog(ApplicationFrame.getInstance(), "This function only works for the Digital Objects module");
			} else if (worksurfaceTable.getSelectedRowCount() == 0) {
				JOptionPane.showMessageDialog(ApplicationFrame.getInstance(), "You must select at least one Digital Object record");
			} else {
				final int[] selectedIndexes = worksurfaceTable.getSelectedRows();
				final ExportOptionsMARC exportOptions = new ExportOptionsMARC();
				final File selectedFileOrDirectory = FileUtils.chooseFileOrDirectory(selectedIndexes.length, exportOptions);

				if (selectedFileOrDirectory != null) {
					Thread performer = new Thread(new Runnable() {
						public void run() {
							// see whether to show the cancel button
							boolean allowCancel = false;
							if(selectedIndexes.length > 1) {
								allowCancel = true;
							}

							InfiniteProgressPanel monitor = ATProgressUtil.createModalProgressMonitor(ApplicationFrame.getInstance(), 1000, allowCancel);
							monitor.start("Exporting...");
							BYU_DCExportHandler batchDCHandler = new BYU_DCExportHandler(exportOptions);

							// open a long session to the database for loading full digital object
							DigitalObjectDAO access = new DigitalObjectDAO();
							access.getLongSession();
							try {
								int[] selectedIndexes = worksurfaceTable.getSelectedRows();
								Vector<DigitalObjects> digitalObjects = new Vector<DigitalObjects>();
								DigitalObjects digitalObject = null;

								for (int loop = 0; loop < selectedIndexes.length; loop++) {
									// check to see if this operation wasn't cancelled
									if(monitor.isProcessCancelled()) {
										break;
									}

									digitalObject = (DigitalObjects)(worksurfaceTable.getFilteredList().get(selectedIndexes[loop]));
									digitalObjects.add((DigitalObjects)access.findByPrimaryKeyLongSession(digitalObject.getIdentifier()));
								}

								if(!monitor.isProcessCancelled()) {
									Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler());
									batchDCHandler.export(selectedFileOrDirectory, digitalObjects, monitor);
								}

								access.closeLongSession();
							} catch (Exception e) {
								monitor.close();
								new ErrorDialog("", e).showDialog();
							} finally {
								monitor.close();
							}
						}
					}, "ExportDC");
					performer.start();
				}

			}

		} else if (task.equals(EXPORT_MODS)) {
			DomainTableWorkSurface workSurface= ApplicationFrame.getInstance().getWorkSurfaceContainer().getCurrentWorkSurface();
			final DomainSortableTable worksurfaceTable = (DomainSortableTable)workSurface.getTable();
			if (workSurface.getClazz() != DigitalObjects.class) {
				JOptionPane.showMessageDialog(ApplicationFrame.getInstance(), "This function only works for the Digital Objects module");
			} else if (worksurfaceTable.getSelectedRowCount() == 0) {
				JOptionPane.showMessageDialog(ApplicationFrame.getInstance(), "You must select at least one Digital Object record");
			} else {
				final int[] selectedIndexes = worksurfaceTable.getSelectedRows();
				final ExportOptionsMARC exportOptions = new ExportOptionsMARC();
				final File selectedFileOrDirectory = FileUtils.chooseFileOrDirectory(selectedIndexes.length, exportOptions);

				if (selectedFileOrDirectory != null) {
					Thread performer = new Thread(new Runnable() {
						public void run() {
							// see whether to show the cancel button
							boolean allowCancel = false;
							if(selectedIndexes.length > 1) {
								allowCancel = true;
							}

							InfiniteProgressPanel monitor = ATProgressUtil.createModalProgressMonitor(ApplicationFrame.getInstance(), 1000, allowCancel);
							monitor.start("Exporting...");
							BYU_MODSExportHandler batchMODSHandler = new BYU_MODSExportHandler(exportOptions);

							// open a long session to the database for loading full digital object
							DigitalObjectDAO access = new DigitalObjectDAO();
							access.getLongSession();
							try {
								int[] selectedIndexes = worksurfaceTable.getSelectedRows();
								Vector<DigitalObjects> digitalObjects = new Vector<DigitalObjects>();
								DigitalObjects digitalObject = null;

								for (int loop = 0; loop < selectedIndexes.length; loop++) {
									// check to see if this operation wasn't cancelled
									if(monitor.isProcessCancelled()) {
										break;
									}

									digitalObject = (DigitalObjects)(worksurfaceTable.getFilteredList().get(selectedIndexes[loop]));
									digitalObjects.add((DigitalObjects)access.findByPrimaryKeyLongSession(digitalObject.getIdentifier()));
								}

								if(!monitor.isProcessCancelled()) {
									Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler());
									batchMODSHandler.export(selectedFileOrDirectory, digitalObjects, monitor);
								}
								access.closeLongSession();
							} catch (Exception e) {
								monitor.close();
								new ErrorDialog("", e).showDialog();
							} finally {
								monitor.close();
							}
						}
					}, "ExportMODS");
					performer.start();
				}
			}

		}
	}

	// Method to get the list of specific task the plugin can perform
	public String[] getTaskList() {
		String[] tasks = {IMPORT_ACCESSIONS, IMPORT_MARC, EXPORT_MARC, IMPORT_EAD, EXPORT_EAD, EXPORT_DC, EXPORT_MODS};
		return tasks;
	}

	// Method to return the editor type for this plugin
	public String getEditorType() {
		return null;
	}

	// code that is executed when plugin starts. not used here
	protected void doStart()  {
	}

	// code that is executed after plugin stops. not used here
	protected void doStop()  { }

	// main method for testing only
	public static void main(String[] args) {
		ImportExport demo = new ImportExport();
		demo.showPlugin();
	}
}
