package de.thkoeln.syp.mtc.gui.view;

import java.awt.Font;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

/**
 * Visualisierung des About-Panels
 * @author Allen Kletinitch
 *
 */
public class AboutView extends JFrame {
	
	private static final long serialVersionUID = -8398864952376075615L;
	private JPanel panel;
	private JLabel lblHeader;
	private JLabel lblDevelopers;
	private JLabel lblKletinitch;
	private JLabel lblPooth;
	private JLabel lblSchmitz;
	private JLabel lblUckermann;

	public AboutView() {
		// Panel
		panel = new JPanel();
		panel.setLayout(new MigLayout("", "[][][100px]", "[][][][][][]"));
		this.add(panel);

		// Ueberschrift
		lblHeader = new JLabel("MTC: MultiTextCompare");
		lblHeader.setFont(new Font("Tahoma", Font.BOLD, 13));
		panel.add(lblHeader, "cell 1 0");

		// Developer Spalte
		lblDevelopers = new JLabel("Developers:");
		lblDevelopers.setFont(new Font("Tahoma", Font.BOLD, 11));
		panel.add(lblDevelopers, "cell 0 1");

		lblKletinitch = new JLabel("Allen Kletinitch");
		panel.add(lblKletinitch, "cell 0 2");

		lblPooth = new JLabel("Matthias Pooth");
		panel.add(lblPooth, "cell 0 3");

		lblSchmitz = new JLabel("Alexander Schmitz");
		panel.add(lblSchmitz, "cell 0 4");

		lblUckermann = new JLabel("Luca Uckermann");
		panel.add(lblUckermann, "cell 0 5");

		// Frame
		this.setTitle("About");
		this.pack();
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		try {
			this.setIconImage(ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("icon.png")));
		} catch (IOException e) {
		}

	}
}
