package org.samcrow.markedantrecorder.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.samcrow.markedantrecorder.data.ColorDataSet;
import org.samcrow.markedantrecorder.util.Storage;

/**
 * Records ant events to a file
 * @author Sam Crow
 *
 */
public class RecorderFileInterface {

	/**
	 * Writes an event to the correct file
	 * @param experimentId
	 * @param direction
	 * @param colors
	 * @throws IOException If an IO error occurred
	 */
	public static void writeEvent(String experimentId, InOut direction, ColorDataSet colors) throws IOException {
		final File cardDir = Storage.getMemoryCard();
		File csvFile = new File(cardDir, "color_events_"+experimentId+".csv");
		
		//If the file header should be printed
		boolean printHeader = false;
		if(!csvFile.exists()) {
			printHeader = true;
			boolean success = csvFile.createNewFile();
			if(!success) {
				throw new IOException("Could not create CSV file "+csvFile.getAbsolutePath());
			}
		}
		//Open the file to append
		FileOutputStream stream = new FileOutputStream(csvFile, true);
		PrintStream out = new PrintStream(stream);
		try {
			if(printHeader) {
				//Print the header
				out.println("Time,InOut,Colors");
			}
			
			//Assemble the line
			//Date/Time
			DateTimeFormatter formatter = ISODateTimeFormat.dateTime();
			out.print(formatter.print(System.currentTimeMillis()));
			out.print(',');
			//Direction
			out.print(direction.name());
			out.print(',');
			//Colors
			int i = 0;
			for(ColorDataSet.Color color : colors) {
				out.print(color.toString());
				if (i < 2) {
					out.print(' ');
				}
				i++;
			}
			//End the line
			out.println();

		}
		finally {
			out.close();
		}
	}
	
	/**
	 * Definitions of whether a colony is going in or out
	 */
	public enum InOut {
		In,
		Out
	}

	private RecorderFileInterface() {
	}
}
