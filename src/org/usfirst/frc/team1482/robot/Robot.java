/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team1482.Robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
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
  
  int axisThrottleID = 1; // stick 1, y
  int axisSteerID = 0; // stick 1, x
  int axisRaiseID = 2;
  int axisRaise1ID = 3;
  int buttonGrabID = ; // don't forget to map this later
  int buttonReleaseID = ;
  int buttonLockID = ;
  int buttonTransmissionID = 1; // a
  
  SendableChooser<String> chooser;
  Timer autoTimer;
  
  String selectedAuto;
  String gameData;
  
  Joystick stick;
  
  DifferentialDrive drive;
  DifferentialDrive winch;
  DifferentialDrive winch1;
  DifferentialDrive grab;
  
  DoubleSolenoid lock;
  DoubleSolenoid transmission;

	/**
	 * This function is run when the Robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void RobotInit() {
	  chooser = new SendableChooser<String>();
	  chooser.addDefault("Center", "C");
    chooser.addObject("Left", "L");
    chooser.addObject("Right", "R");
    SmartDashboard.putData("Autonomouse Position", chooser);
    
    autoTimer = new Timer();
	  
	  stick = new Joystick(stickID);
	  
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
	  winch1 = new DifferentialDrive(motorWinch1, motorWinch);
	  grab = new DifferentialDrive(motorGrab, motorGrab1);
	  
	  lock = new DoubleSolenoid(2, 3);
	  transmission = new DoubleSolenoid(0, 1);
	  
	  CameraServer.getInstance().startAutomaticCapture();
	  
	  
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
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
	  
	  
	  
	  switch (selectedAuto) {
      case "R":
        
        switch (gameData.charAt(0)) {
          case "L":
            // No fuck that, just drive forwards
            if (autoTimer.get() < 2) {
              drive.arcadeDrive(1, 0);
            }
            break;
            
          case "R":
            break;
        }
        
        break;
      case "C":
        
        break;
      case "L":
        
        switch (gameData.charAt(0)) {
          case "L":
            break;
            
          case "R":
            // No fuck that, just drive forwards
            drive.arcadeDrive(1, 0);
            Timer.delay(5000);
            drive.arcadeDrive(0, 0);
            break;
        }
        
        break;
      default:
        
        break;
    }
	  
	}

	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
	  drive.arcadeDrive(stick.getRawAxis(axisThrottleID), -stick.getRawAxis(axisSteerID));
	  winch.arcadeDrive(stick.getRawAxis(axisRaiseID), 0);
	  winch1.arcadeDrive(stick.getRawAxis(axisRaise1ID), 0);
	  grab.arcadeDrive(stick.getRawButton(buttonGrabID) ? 1 : (stick.getRawButton(buttonReleaseID) ? -1 : 0), 0);
	  
	  lock.set(stick.getRawButton(buttonLockID) ? DoubleSolenoid.Value.kReverse : DoubleSolenoid.Value.kForward);
	  transmission.set(stick.getRawButton(buttonTransmissionID) ? DoubleSolenoid.Value.kForward : DoubleSolenoid.Value.kReverse);
	}
	
	
	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {}
}
