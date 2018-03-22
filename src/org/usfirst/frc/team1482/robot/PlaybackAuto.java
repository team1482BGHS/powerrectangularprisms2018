package org.usfirst.frc.team1482.robot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.wpi.first.wpilibj.Joystick;

class PlaybackAuto {
  // 10 buttons, 6 axiseses
  // private Map<Integer, List<Double>> timeline; // {20: [1,2,3,4]}
  private BufferedWriter writer;
  private BufferedReader reader;
  
  public PlaybackAuto(String filename) {
    try {
      writer = new BufferedWriter(new FileWriter(new File(filename)));
      reader = new BufferedReader(new FileReader(new File(filename)));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  public void startRecord(String filename) {
    try {
      writer = new BufferedWriter(new FileWriter(new File(filename)));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  public void recordFrame(int time, Joystick stick) {
    /*
     * Format:
     * [time]:[button...]:[[axis],[axis]...]\n
     */
    
    try {
      writer.write(Integer.toString(time) + ":");
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    
    for (int i = 1; i <= stick.getButtonCount(); i++) {
      try {
        writer.write(stick.getRawButton(i) ? "1" : "0");
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    
    try {
      writer.write(":");
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    
    for (int i = 0; i < stick.getAxisCount(); i++) {
      try {
        writer.write(Double.toString(stick.getRawAxis(i)) + ",");
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    try {
      writer.write("\n");
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
  }
  
  public void recordStop() {
    try {
      writer.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  public void start(String filename) {
    try {
      reader = new BufferedReader(new FileReader(new File(filename)));
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  /*
   * getButton
   * int time - current time frame
   * int button - button id (starting at 1)
   * boolean @return - true if pressed false if not
   */
  public boolean getButton(int time, int button) {
    String line;
    while (true) {
      try {
        line = reader.readLine();
      
        if (line == null) {
          throw new Error("EOF");
        }
        
        if (line.substring(0, Integer.toString(time).length()) == Integer.toString(time)) { // Check if we're on the right line
          int offset = Integer.toString(time).length() + 1 + button; // Locate the string offset
          return (line.substring(offset, offset + 1) == "1");
        }
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }
  
  /*
   * getButton
   * int time - current time frame
   * int button - button id (starting at 1)
   * boolean @return - true if pressed false if not
   */
  public double getAxis(int time, int axis) {
    String line;
    while (true) {
      try {
        line = reader.readLine();
      
        if (line == null) {
          throw new Error("EOF");
        }
        
        System.out.println(line);
        
        if (line.substring(0, Integer.toString(time).length()) == Integer.toString(time)) { // Check if we're on the right line
          int offset = Integer.toString(time).length() + 1; // Default to after [time]:
          offset += line.substring(offset).indexOf(":") + 1; // Set the offset to where the axises start
          for (int i = 0; i < axis; i++) {
            offset += line.substring(offset).indexOf(",") + 1;
          }
          
          return Double.parseDouble(line.substring(offset, line.substring(offset).indexOf(",")));
        }
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }
}