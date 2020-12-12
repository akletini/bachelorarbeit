package de.thkoeln.syp.mtc.datenhaltung.api;

import java.io.File;

public interface IXMLParseError {
	
	void setFile(File file);
	
	File getFile();
	
	void setMessage(String eMessage);
	
	String getMessage();
	
	void setCol(int ecol);
	
	int getCol();
	
	void setLine(int eline);
	
	int getLine();
}
