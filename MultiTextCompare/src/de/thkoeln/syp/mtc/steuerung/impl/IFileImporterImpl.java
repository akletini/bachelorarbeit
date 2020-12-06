package de.thkoeln.syp.mtc.steuerung.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import de.thkoeln.syp.mtc.datenhaltung.impl.IConfigImpl;
import de.thkoeln.syp.mtc.steuerung.services.IFileImporter;

public class IFileImporterImpl implements IFileImporter {

	private IConfigImpl iConfigImpl;
	private List<File> textdateien;
	private Properties prop;

	public IFileImporterImpl() {
		iConfigImpl = null;
		textdateien = new ArrayList<>();
		prop = new Properties();

		prop.setProperty(PROP_LEERZEICHEN, "false");
		prop.setProperty(PROP_SATZZEICHEN, "false");
		prop.setProperty(PROP_GROSSSCHREIBUNG, "false");
		prop.setProperty(PROP_ROOT, System.getProperty("user.dir"));
	}

	@Override
	public IConfigImpl getConfig() {
		if (iConfigImpl == null)
			importConfigdatei(DEFAULT_CONFIG);

		return iConfigImpl;
	}

	@Override
	public List<File> getTextdateien() {
		return textdateien;
	}

	@Override
	public boolean importConfigdatei(File config) {
		iConfigImpl = new IConfigImpl();
		OutputStream outputStream;
		InputStream inputStream;

		if (!config.exists()) {
			if (!DEFAULT_CONFIG.exists()) {
				try {
					outputStream = new FileOutputStream(
							System.getProperty("user.dir") + File.separator
									+ "config.properties");

					prop.store(outputStream, null);

					iConfigImpl.setPath(System.getProperty("user.dir")
							+ File.separator + "config.properties");

					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			} else {
				try {
					inputStream = new FileInputStream(DEFAULT_CONFIG);

					prop.load(inputStream);

					iConfigImpl.setPath(DEFAULT_CONFIG.getAbsolutePath());

					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			}
		} else {
			try {
				inputStream = new FileInputStream(config);

				prop.load(inputStream);

				iConfigImpl.setPath(config.getAbsolutePath());

				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}

		iConfigImpl.setBeachteLeerzeichen(Boolean.parseBoolean(prop
				.getProperty(PROP_LEERZEICHEN)));
		iConfigImpl.setBeachteSatzzeichen(Boolean.parseBoolean(prop
				.getProperty(PROP_SATZZEICHEN)));
		iConfigImpl.setBeachteGrossschreibung(Boolean.parseBoolean(prop
				.getProperty(PROP_GROSSSCHREIBUNG)));
		iConfigImpl.setRootDir(prop.getProperty(PROP_ROOT));

		return true;
	}

	@Override
	public boolean exportConfigdatei(IConfigImpl iConfigImpl) {
		Properties prop = new Properties();
		OutputStream outputStream;

		try {
			outputStream = new FileOutputStream(iConfigImpl.getPath());

			prop.setProperty(PROP_LEERZEICHEN,
					Boolean.toString(iConfigImpl.getBeachteLeerzeichen()));
			prop.setProperty(PROP_SATZZEICHEN,
					Boolean.toString(iConfigImpl.getBeachteSatzzeichen()));
			prop.setProperty(PROP_GROSSSCHREIBUNG,
					Boolean.toString(iConfigImpl.getBeachteGrossschreibung()));
			prop.setProperty(PROP_ROOT, iConfigImpl.getRootDir());

			prop.store(outputStream, null);

			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	@Override
	public boolean importTextdateien(List<String> textdateien) {
		for (String str : textdateien) {
			File file = new File(str);

			if (file.exists())
				this.textdateien.add(file);
		}

		return true;
	}

	@Override
	public boolean importTextRoot() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean createTempFiles() {
		BufferedReader reader;
		BufferedWriter writer;

		new File(System.getProperty("user.dir") + File.separator + "TempFiles")
				.mkdirs();

		for (File f : this.textdateien) {
			File temp = new File(System.getProperty("user.dir")
					+ File.separator + "TempFiles" + File.separator + "temp_"
					+ f.getName());

			try {
				if (temp.exists()) {
					temp.delete();
				}
				temp.createNewFile();

				reader = new BufferedReader(new InputStreamReader(
						new FileInputStream(f)));
				writer = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(temp)));

				String line;
				while ((line = reader.readLine()) != null)
					writer.write(line.replaceAll("\\s", "\n") + "\n");

				reader.close();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
}
