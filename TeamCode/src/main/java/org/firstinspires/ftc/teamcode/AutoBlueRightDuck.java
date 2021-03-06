package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.teamcode.core.Auto;
import org.firstinspires.ftc.teamcode.core.Drive;

//@Disabled
@Autonomous(name = "AutoBlueRightDuck", group = "A")
public class AutoBlueRightDuck extends LinearOpMode {

  final Auto auto = new Auto();
  final Drive drive = new Drive();

  // @Override
  public void runOpMode() {

    drive.init(hardwareMap, telemetry);
    auto.init(hardwareMap, telemetry, drive, Auto.SIDE.BLUE);

    waitForStart();
    auto.recordTeamScorePos();

    // go forward
    drive.moveY = 0.5;
    drive.apply();
    sleep(240);

    // stop moving forward and move right
    drive.moveY = 0;
    drive.moveX = 0.5;
    drive.apply();
    sleep(1600);

    // pull out the spinner, and spin the spinner, and stop moving right
    drive.spinnerJointPos = 0.8;
    drive.spinnerSpeed = 0.8;
    drive.moveX = 0;
    drive.apply();
    sleep(300);

    // stop moving and let the duck fall off
    drive.moveX = 0;
    drive.apply();
    sleep(2500);

    // pull back spinner and stop spinner
    drive.spinnerJointPos = -0.2;
    drive.spinnerSpeed = 0.49;
    drive.apply();
    sleep(350);

    // move right
    drive.moveX = 0.5;
    drive.apply();
    sleep(500);

    // stop moving right and move forward
    drive.moveX = 0;
    drive.moveY = 0.5;
    drive.apply();
    sleep(1920);

    // stop moving forward and move arm to correct height
    drive.moveY = 0;
    drive.apply();
    auto.moveArmToCorrectPosition();
    sleep(500);

    // rotate left
    drive.rot = -0.5;
    drive.apply();
    sleep(720);

    // stop rotating and move forward
    drive.rot = 0;
    drive.moveY = 0.5;
    drive.apply();
    sleep(1480);

    // stop moving
    drive.moveY = 0;
    drive.apply();
    sleep(500);

    // drop
    drive.clawTargetState = 0;
    drive.apply();
    sleep(500);

    // move back
    drive.moveY = -0.5;
    drive.apply();
    sleep(1580);

    // stop moving back, move arm to default and turn left
    drive.moveY = 0;
    drive.rot = -0.5;
    drive.setState(Drive.ARM_STATE.DEFAULT);
    drive.apply();
    sleep(720);

    // stop rotating and move forwards
    drive.rot = 0;
    drive.moveY = 0.5;
    drive.apply();
    sleep(650);

    // stop moving forwards and move left
    drive.moveY = 0;
    drive.moveX = -0.5;
    drive.apply();
    sleep(750);

    // stop moving
    drive.moveX = 0;
    drive.apply();
    sleep(20000);
  }
}
