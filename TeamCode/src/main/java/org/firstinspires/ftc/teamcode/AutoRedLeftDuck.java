package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.teamcode.core.Auto;
import org.firstinspires.ftc.teamcode.core.Drive;

//@Disabled
@Autonomous(name = "AA-AutoRedLeftDuck", group = "A")
public class AutoRedLeftDuck extends LinearOpMode {

  final Auto auto = new Auto();
  final Drive drive = new Drive();

  // @Override
  public void runOpMode() {
    drive.init(hardwareMap, telemetry);
    auto.init(hardwareMap, telemetry, drive, Auto.SIDE.RED);

    waitForStart();
    auto.recordTeamScorePos();

    // move a bit forward
    drive.moveY = 0.5;
    drive.apply();
    sleep(450);

    // stop moving and rotate to right
    drive.moveY = 0;
    drive.rot = 0.5;
    drive.apply();
    sleep(795);

    // stop rotating, go back, and pull out the spinner
    drive.rot = 0;
    drive.moveY = -0.5;
    drive.apply();
    sleep(750);

    // stop moving backwards
    drive.moveY = 0;
    drive.spinnerJointPos = 1;
    sleep(500);

    // spin the spinner
    drive.spinnerSpeed = -0.2;
    drive.apply();
    sleep(2500);

    // bring back in the spinner, move left, and move the arm to the correct height
    drive.spinnerJointPos = -0.3;
    drive.spinnerSpeed = 0.49;
    drive.moveX = -0.5;
    drive.apply();
    sleep(2150);

    // stop moving left, move forward, and stop pulling the spinner joint in
    drive.rot = 0;
    drive.spinnerJointPos = 0;
    drive.moveX = 0;
    drive.moveY = 0.5;
    auto.moveArmToCorrectPosition();
    drive.apply();
    sleep(1420);

    // stop moving forward
    drive.moveY = 0;
    drive.apply();
    sleep(550);

    // drop
    drive.clawTargetState = 0;
    drive.apply();
    sleep(500);

    // move back left a bit
    drive.moveY = -0.5;
    drive.moveX = -0.5;
    drive.apply();
    sleep(250);

    // stop moving back left, turn left, and move arm to default
    drive.moveX = 0;
    drive.moveY = 0;
    drive.rot = -0.5;
    drive.setState(Drive.ARM_STATE.DEFAULT);
    sleep(750);

    // stop turning and move left
    drive.rot = 0;
    drive.moveX = -0.5;
    drive.apply();
    sleep(1800);

    // stop moving left and go backwards
    drive.moveX = 0;
    drive.moveY = -0.5;
    drive.apply();
    sleep(1020);

    // stop moving backwards and go left
    drive.moveY = 0;
    drive.moveX = -0.5;
    drive.apply();
    sleep(800);

    // stop moving
    drive.moveX = 0;
    drive.apply();
  }
}
