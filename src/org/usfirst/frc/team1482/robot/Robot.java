/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team1482.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Timer;

import java.lang.String;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */
public class Robot extends IterativeRobot {
  
  int stickID = 0;
  int stick1ID = 1;
  
  int axisThrottleID = 1; // stick 1, l, y
  int axisSteerID = 0; // stick 1, l, x
  int axisRaiseID = 1; // stick 2, l, y
  int axisRaise1ID = 5; // stick 2, r, y
  int buttonGrabID = 6; // 2, r bumper
  int buttonReleaseID = 5; // 2, l bumper
  int buttonLockID = 1; // 2, a button
  int buttonTransmissionID = 1; // 1, a button
  
  SendableChooser<String> chooser;
  SendableChooser<Double> motorChooser;
  Timer autoTimer;
  
  String selectedAuto;
  String gameData;
  
  Joystick stick;
  Joystick stick1;
  
  DifferentialDrive drive;
  DifferentialDrive winch;
  DifferentialDrive winch1;
  DifferentialDrive grab;
  
  DoubleSolenoid lock;
  DoubleSolenoid transmission;
  
  char SelectedAuto;
  String GameData;
  
  PlaybackAuto recording1; // for joystick 1
  PlaybackAuto recording2; // for joystick 2
  int recordingTime = 0;
  
  /**
   * This function is run when the Robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    chooser = new SendableChooser<String>();
    // chooser.addDefault("Center", "C");
    // chooser.addObject("Left", "L");
    // chooser.addObject("Right", "R");
    chooser.addDefault("Drive Forward (L/R only)", "d");
    // chooser.addObject("Do Switch (C only)", "s");
    chooser.addObject("Sit Still (all positions)", "n");
    chooser.addObject("Play Recording (all positions)", "r");
    chooser.addObject("Overwrite Recording (test mode only)", "R");
    SmartDashboard.putData("Autonomous", chooser);

    autoTimer = new Timer();

    stick = new Joystick(stickID);
    stick1 = new Joystick(stick1ID);
    
    WPI_TalonSRX motorLeft = new WPI_TalonSRX(0);
    WPI_TalonSRX motorLeft1 = new WPI_TalonSRX(1);
    WPI_TalonSRX motorRight = new WPI_TalonSRX(2);
    WPI_TalonSRX motorRight1 = new WPI_TalonSRX(3);
    WPI_TalonSRX motorWinch = new WPI_TalonSRX(4);
    WPI_TalonSRX motorWinch1 = new WPI_TalonSRX(5);
    WPI_TalonSRX motorGrab = new WPI_TalonSRX(6);
    WPI_TalonSRX motorGrab1 = new WPI_TalonSRX(7);
    
    SpeedControllerGroup railLeft = new SpeedControllerGroup(motorLeft, motorLeft1);
    SpeedControllerGroup railRight = new SpeedControllerGroup(motorRight, motorRight1);
    
    // railLeft.setInverted(true);
    // railRight.setInverted(true);
    
    drive = new DifferentialDrive(railLeft, railRight);
    winch = new DifferentialDrive(motorWinch, motorWinch);
    winch1 = new DifferentialDrive(motorWinch1, motorWinch1);
    grab = new DifferentialDrive(motorGrab, motorGrab1);
    
    //lock = new DoubleSolenoid(2, 3);
    transmission = new DoubleSolenoid(0, 1);
    
    CameraServer.getInstance().startAutomaticCapture();
    
    try {
      // MAKE SURE TO BACKUP THESE 2 FILES WITH SSH
      recording1 = new PlaybackAuto("/home/lvuser/timeline1.dat");
      recording2 = new PlaybackAuto("/home/lvuser/timeline2.dat");
    } catch (IOException ex) {
      ex.printStackTrace();
      System.out.println("PlaybackAuto failed to start and will not run in match. Restart robot code to try loading again.");
    }
    recordingTime = 0;
	}

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable
   * chooser code works with the Java SmartDashboard. If you prefer the
   * LabVIEW Dashboard, remove all of the chooser code and uncomment the
   * getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to
   * the switch structure below with additional strings. If using the
   * SendableChooser make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    selectedAuto = chooser.getSelected(); // R(ight) C(enter) or L(eft)
    gameData = DriverStation.getInstance().getGameSpecificMessage(); // LRL or RLR or LLL etc.
    
    autoTimer.reset();
    autoTimer.start();
    
    timePlayback = 0;
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    // System.out.println("Selected: " + selectedAuto);

      if (selectedAuto == "d") { // drive to auto line
      
        // No fuck that, just drive forwards
        if (autoTimer.get() < 7) { // For 3 seconds, drive forward (make sure to check timings and to make sure drive train doesn't curve)
          drive.arcadeDrive(-0.5, 0);
        } else {
          drive.arcadeDrive(0, 0);
        }
        
      } else if (selectedAuto == "s") { // do actual auto
      
        /* 
         * Lower cube picker - async 1s - 0-1s
         * Lower first rail - async 1s - 0-1s
         * Drive forward - 2s - 0-2
         * Intake motors inwards - 1s - 2-3s
         * Raise rail - 1s - 3-4s
         * Turn left/right - 0.5s - 4-4.5s
         * Drive forward - 2s - 4.5-6.5s
         * Turn to face switch - 0.5s - 6.5-7s
         * Intake motors outwards - 1s - 7-8s
         *
         * Reset all outputs execpt drive, winch & grab every tick
         */

        /*if (autoTimer.get() < 1) {
          winch.arcadeDrive(-0.5, 0);
          // insert servo/other motor to lower picker
        }*/
        
        /*if (autoTimer.get() < 2) {
          drive.arcadeDrive(0.5, 0);
          grab.arcadeDrive(0, 0);
        }

        if (autoTimer.get() > 2 && autoTimer.get() < 3) {
          grab.arcadeDrive(1, 0);
          drive.arcadeDrive(0, 0);
          winch.arcadeDrive(0, 0);
        }

        if (autoTimer.get() > 3 && autoTimer.get() < 4) {
          grab.arcadeDrive(0, 0);
          drive.arcadeDrive(0, 0);
          winch.arcadeDrive(-0.5, 0);
        }

        if (autoTimer.get() > 4 && autoTimer.get() < 4.5) {
          grab.arcadeDrive(0, 0);
          drive.arcadeDrive(0.5, gameData.charAt(0) == 'L' ? -1 : 1);
          winch.arcadeDrive(0, 0);
        }

        if (autoTimer.get() > 4.5 && autoTimer.get() < 6.5) {
          grab.arcadeDrive(0, 0);
          drive.arcadeDrive(0.5, 0);
          winch.arcadeDrive(0, 0);
        }	

        if (autoTimer.get() > 6.5 && autoTimer.get() < 7) {
          grab.arcadeDrive(0, 0);
          drive.arcadeDrive(0.5, gameData.charAt(0) == 'L' ? 1 : -1);
          winch.arcadeDrive(0, 0);
        }

        if (autoTimer.get() > 7 && autoTimer.get() < 8) {
          grab.arcadeDrive(-1, 0);
          drive.arcadeDrive(0, 0);
          winch.arcadeDrive(0, 0);
        }

        winch1.arcadeDrive(0, 0);*/
        
      } else if (selectedAuto == "r") {// use recording
        
        // Yeah I fucking copy and pasted our teleop code.
        drive.arcadeDrive(recording1.getAxis(recordingTime, axisThrottleID), -recording1.getRawAxis(axisSteerID));
        winch.arcadeDrive(recording2.getAxis(recordingTime, axisRaiseID), 0);

        grab.arcadeDrive(recording2.getButton(recordingTime, buttonGrabID) ? 0.85 : (recording2.getButton(recordingTime, buttonReleaseID) ? -0.85 : 0), 0);

        transmission.set(recording1.getButton(recordingTime, buttonTransmissionID) ? DoubleSolenoid.Value.kForward : DoubleSolenoid.Value.kReverse);
        recordingTime++;
      
      } else {
        // Okay fuck that further, because we can't even drive forwards, so do nothing.
      }
    
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    drive.arcadeDrive(stick.getRawAxis(axisThrottleID), -stick.getRawAxis(axisSteerID));
    winch.arcadeDrive(stick1.getRawAxis(axisRaiseID) * 0.6, 0);
    
    grab.arcadeDrive(stick1.getRawButton(buttonGrabID) ? 0.85 : (stick1.getRawButton(buttonReleaseID) ? -0.85 : 0), 0);
    
    transmission.set(stick.getRawButton(buttonTransmissionID) ? DoubleSolenoid.Value.kForward : DoubleSolenoid.Value.kReverse);
    
    /*
     * Climber broke
    if (stick1.getRawButton(buttonLockID)) {
        winch1.arcadeDrive(stick1.getRawAxis(axisRaise1ID), 0);
        lock.set(DoubleSolenoid.Value.kForward);
    } else {
      winch1.arcadeDrive(0, 0);
      lock.set(DoubleSolenoid.Value.kReverse);
    }
    
    lock.set(stick1.getRawButton(buttonLockID) ? DoubleSolenoid.Value.kForward : DoubleSolenoid.Value.kReverse);
    */
  }
  

  /**
     * This function is called periodically during test mode.
     */
    @Override
    public void testInit() {
      recordingTime = 0;
      
      selectedAuto = chooser.getSelected();
      
      if (selectedAuto == "R") {// overwrite recording
        recording1.startRecord();
        recording2.startRecord();
        
        // IF YOU DON'T GET THIS MESSAGE, SELECT THE OPTION FIRST, THEN SELECT TEST MODE (OR SWITCH MODES)
        System.out.println("Recording has started for timeline1.dat and timeline2.dat. Disable test mode to stop recording.");
      }
    }
  
  
  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
    // stick.setRumble(RumbleType.kLeftRumble, stick.getRawButton(1) ? 1 : 0);
    // stick.setRumble(RumbleType.kRightRumble, stick.getRawButton(2) ? 1 : 0);
    
    recording1.recordFrame(recordingTime, stick);
    recording2.recordFrame(recordingTime, stick1);
    recordingTime++;
    
    drive.arcadeDrive(stick.getRawAxis(axisThrottleID), -stick.getRawAxis(axisSteerID));
    winch.arcadeDrive(stick1.getRawAxis(axisRaiseID) * 0.6, 0);
    
    grab.arcadeDrive(stick1.getRawButton(buttonGrabID) ? 0.85 : (stick1.getRawButton(buttonReleaseID) ? -0.85 : 0), 0);
    
    transmission.set(stick.getRawButton(buttonTransmissionID) ? DoubleSolenoid.Value.kForward : DoubleSolenoid.Value.kReverse);
    
    /*
     * POV Layout
     * 315   0  45
     * 270  -1  90
     * 225 180 135
     */
  }
}
