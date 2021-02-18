package de.thkoeln.syp.mtc.datenhaltung.api;

public interface IConfig {
	void setKeepWhitespaces(boolean keepWhitespaces);

	void setKeepPuctuation(boolean keepPuctuation);

	void setKeepCapitalization(boolean keepCapitalization);

	void setRootDir(String rootDir);

	void setPath(String path);

	void setCompareLines(boolean compareLines);

	void setFilename(String filename);

	void setFiletype(String filetype);

	void setKeepBlankLines(boolean keepBlankLines);

	void setLineMatch(boolean lineMatch);

	boolean getKeepWhitespaces();

	boolean getKeepPuctuation();

	boolean getKeepCapitalization();

	String getRootDir();

	String getPath();

	boolean getCompareLines();

	String getFilename();

	String getFiletype();

	boolean getKeepBlankLines();

	boolean getLineMatch();

	// XML spezifische Parameter
	void setXmlSortElements(boolean xmlSortElements);

	void setXmlSortAttributes(boolean xmlSortAttributes);

	void setXmlDeleteAttributes(boolean xmlDeleteAttributes);

	void setXmlDeleteComments(boolean xmlDeleteComments);

	void setXmlOnlyTags(boolean xmlOnlyTags);

	void setXmlValidation(int xmlValidation);
	
	void setXmlPrint(int xmlPrint);

	boolean getXmlSortElements();

	boolean getXmlSortAttributes();

	boolean getXmlDeleteAttributes();

	boolean getXmlDeleteComments();

	boolean getXmlOnlyTags();

	int getXmlValidation();
	
	int getXmlPrint();

	// JSON spezifische Parameter
	void setJsonSortKeys(boolean jsonSortKeys);

	void setJsonDeleteValues(boolean jsonDeleteValues);

	boolean getJsonSortKeys();

	boolean getJsonDeleteValues();
}
