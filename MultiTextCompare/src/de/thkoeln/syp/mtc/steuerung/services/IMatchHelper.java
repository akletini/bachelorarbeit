package de.thkoeln.syp.mtc.steuerung.services;

import java.io.File;
import java.io.IOException;
import java.util.List;

import de.thkoeln.syp.mtc.datenhaltung.api.IMatch;

public interface IMatchHelper {

	void matchEqualLines(File a, File b) throws IOException;

	File[] createMatchFiles(File[] files) throws IOException;

	List<IMatch> getMatches();

}
