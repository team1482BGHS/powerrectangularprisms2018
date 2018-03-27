package org.usfirst.frc.team1482.robot;

import edu.wpi.first.wpilibj.Joystick;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.regex.Pattern;
import java.util.List;

class PlaybackAuto {
  private String filename;
  
  public PlaybackAuto(String fileInput) {
    filename = fileInput;
  }
  
  /**
   * Initialize the recording by clearing the file.
   * @throws IOException Thrown on file write error.
   */
  public void startRecord() throws IOException {
    Files.write(Paths.get(filename), "".getBytes());
  }
  
  /**
   * Record a time frame of controller inputs into a file.
   * @param time The time frame to record to, starting at zero, incrementing by one.
   * @param stick The Joystick to record from.
   * @throws IOException Thrown on file write error.
   */
  public void recordFrame(int time, Joystick stick) throws IOException {
    /*
     * Format:
     * [time]:[button...]:[[axis],[axis]...]\n
     */
     
     String line = "";
    
     line += Integer.toString(time) + ":";
    
    for (int i = 1; i <= stick.getButtonCount(); i++) {
      line += stick.getRawButton(i) ? "1" : "0";
    }
    
    line += ":";
    
    for (int i = 0; i < stick.getAxisCount(); i++) {
      line += Double.toString(stick.getRawAxis(i)) + (i >= stick.getAxisCount() - 1 ? "" : ",");
    }

    line += "\n";
    
    Files.write(Paths.get(filename), line.getBytes(), StandardOpenOption.APPEND);
  }
  
  /**
   * Get a pre-recorded button press at a time frame.
   * @param time The time frame to use.
   * @param button The button id, starting at zero, to check for.
   * @return Whether the recording had the button pressed.
   * @throws IOException Thrown on file read error.
   * @throws IndexOutOfBoundsException Thrown if time is bigger than recording length or for an invalid button id.
   */
  public boolean getButton(int time, int button) throws IOException, IndexOutOfBoundsException {
    List<String> lines = Files.readAllLines(Paths.get(filename));
    String line = lines.get(time);
    
    Pattern segmentSplit = Pattern.compile(":");
    String[] segments = segmentSplit.split(line, 0); // [time, buttons, axises]; throws IndexOutOfBoundsException with invalid line
    
    if (Integer.parseInt(segments[0]) == time) {
      return segments[1].substring(button - 1, button - 1 + 1).equals("1"); // throws IndexOutOfBoundsException with invalid button
    } else {
      throw new IndexOutOfBoundsException(); // throws IndexOutOfBoundsException with invalid line
    }
  }
  
  /**
   * Get a pre-recorded axis position at a time frame.
   * @param time The time frame to use.
   * @param axis The axis id, starting at one, to check for.
   * @return The axis position on the recording.
   * @throws IOException Thrown on file read error.
   * @throws IndexOutOfBoundsException Thrown if time is bigger than recording length.
   */
  public double getAxis(int time, int axis) throws IOException, IndexOutOfBoundsException {
    List<String> lines = Files.readAllLines(Paths.get(filename));
    String line = lines.get(time);
    
    Pattern segmentSplit = Pattern.compile(":");
    String[] segments = segmentSplit.split(line, 0); // [time, buttons, axises]; throws IndexOutOfBoundsException with invalid line
    
    if (Integer.parseInt(segments[0]) == time) {
      Pattern axisesSplit = Pattern.compile(",");
      
      String[] axises = axisesSplit.split(segments[2], 0);
      
      return Double.parseDouble(axises[axis]); // throws IndexOutOfBoundsException with invalid button
    } else {
      throw new IndexOutOfBoundsException(); // throws IndexOutOfBoundsException with invalid line
    }
  }
}
