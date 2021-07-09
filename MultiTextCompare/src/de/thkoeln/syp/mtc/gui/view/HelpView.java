package de.thkoeln.syp.mtc.gui.view;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;

import de.thkoeln.syp.mtc.gui.control.Management;
import de.thkoeln.syp.mtc.logging.Logger;

/**
 * Handler um die Anleitung zu oeffnen
 * @author Allen Kletinitch
 *
 */
public class HelpView extends JFrame {
	
	private static final long serialVersionUID = -8187518366010915651L;
	private Management management;
	private Logger logger;
	
	public HelpView() {
		management = Management.getInstance();
		logger = management.getLogger();
		File helpFile = new File(System.getProperty("user.dir")
				+ File.separator + "help_docs" + File.separator + "help.pdf");
		try {
			Desktop d = Desktop.getDesktop();
			d.open(helpFile);
		} catch (IOException e) {
			logger.setMessage(e.toString(), Logger.LEVEL_ERROR);
		}
	}
}
