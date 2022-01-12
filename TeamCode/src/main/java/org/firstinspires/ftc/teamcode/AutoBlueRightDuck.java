package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

//@Disabled
@Autonomous(name = "AutoBlueRightDuck", group = "A")
public class AutoBlueRightDuck extends LinearOpMode {

  AutonomousAPI autonomousAPI;
  DriverControlAPI driverControlAPI;

  // @Override
  public void runOpMode() {
    autonomousAPI = new AutonomousAPI();
    driverControlAPI = new DriverControlAPI();

    autonomousAPI.init(hardwareMap);
    driverControlAPI.init(hardwareMap);

    waitForStart();

    // go forward
    driverControlAPI.moveY = -1;
    driverControlAPI.apply();
    sleep(150);

    // stop moving forward, move right,
    driverControlAPI.moveY = 0;
    driverControlAPI.moveX = 1;
    driverControlAPI.apply();
    sleep(700);

    //pull out the spinner, and spin the spinner, and stop moving right
    driverControlAPI.spinnerJointSpeed = 0.6f;
    driverControlAPI.spinnerSpeed = 0.8f;
    driverControlAPI.moveX = 0;
    driverControlAPI.apply();
    sleep(300);

    // stop moving and let the duck fall off
    driverControlAPI.moveX = 0;
    driverControlAPI.apply();
    sleep(2500);

    // pull back spinner and move right
    driverControlAPI.spinnerJointSpeed = -0.2f;
    driverControlAPI.moveX = 1;
    driverControlAPI.apply();
    sleep(100);

    //stop moving right and move forward
    driverControlAPI.moveX = 0;
    driverControlAPI.moveY = -1;
    driverControlAPI.apply();
    sleep(900);

    // stop moving forward and move arm to correct height
    driverControlAPI.moveY = 0;
    if (autonomousAPI.camResult == CameraPipeline.LOCATION.RIGHT) {
      driverControlAPI.setState(DriverControlAPI.STATE.HIGH);
    } else if (autonomousAPI.camResult == CameraPipeline.LOCATION.MIDDLE) {
      driverControlAPI.setState(DriverControlAPI.STATE.MIDDLE);
    } else {
      driverControlAPI.setState(DriverControlAPI.STATE.LOW);
    }
    driverControlAPI.apply();
    sleep(500);

    //and rotate left
    driverControlAPI.rot = -1;
    driverControlAPI.apply();
    sleep(400);

    //stop rotating and move forward
    driverControlAPI.rot = 0;
    driverControlAPI.moveY = -1;
    driverControlAPI.apply();
    sleep(650);

    //open claw
    driverControlAPI.moveY = 0;
    driverControlAPI.clawTargetState = 0;
    driverControlAPI.apply();
    sleep(200);

    //move back
    driverControlAPI.moveY = 1;
    driverControlAPI.apply();
    sleep(600);

    //stop moving back and move to the left
    driverControlAPI.moveY = 0;
    driverControlAPI.setState(DriverControlAPI.STATE.LOW);
    driverControlAPI.moveX = -1;
    driverControlAPI.apply();
    sleep(800);

    //turn a bit to the right
    driverControlAPI.moveX = 0;
    driverControlAPI.rot = 1;
    driverControlAPI.apply();
    sleep(20);

    //stop moving
    driverControlAPI.rot = 0;
    driverControlAPI.apply();
    sleep(200);
  }
}
