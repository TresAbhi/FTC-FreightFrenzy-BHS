package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.teamcode.core.Auto;
import org.firstinspires.ftc.teamcode.core.Drive;

//@Disabled
@Autonomous(name = "AB-PROTO-AutoRedLeftDuck", group = "A")
public class AutoRedLeftDuck extends LinearOpMode {

  final Auto auto = new Auto();
  final Drive drive = new Drive();

  // @Override
  public void runOpMode() {
    drive.init(hardwareMap);
    auto.init(hardwareMap, telemetry, drive);

    waitForStart();
    auto.recordTeamScorePos();

    // move a bit forward
    drive.moveY = 0.5f;
    drive.apply();
    sleep(450);

    // stop moving and rotate to right
    drive.moveY = 0;
    drive.rot = 0.5f;
    drive.apply();
    sleep(800);

    // stop rotating, go back, and pull out the spinner
    drive.rot = 0;
    drive.moveY = -0.5f;
    drive.apply();
    sleep(600);

    // stop moving backwards
    drive.moveY = 0;
    drive.spinnerJointPos = 0.75f;
    sleep(500);

    // spin the spinner
    drive.spinnerSpeed = -0.2f;
    drive.apply();
    sleep(2500);

    // bring back in the spinner, move left, and move the arm to the correct height
    drive.spinnerJointPos = -0.3f;
    drive.spinnerSpeed = 0.49f;
    drive.moveX = -0.5f;
    drive.apply();
    sleep(2200);

    // stop moving left, move forward, and stop pulling the spinner joint in
    drive.rot = 0;
    drive.spinnerJointPos = 0;
    drive.moveX = 0;
    drive.moveY = 0.5f;
    auto.moveArmToCorrectPosition();
    drive.apply();
    sleep(1348);

    // stop moving forward
    drive.moveY = 0;
    drive.apply();
    sleep(500);

    // let go of the block
    drive.clawTargetState = 0;
    drive.apply();
    sleep(500);

    // move back a bit
    drive.moveY = -0.5f;
    drive.apply();
    sleep(250);

    // stop moving back and move left
    drive.moveY = 0;
    drive.moveX = -0.5f;
    drive.apply();
    sleep(200);

    // stop moving left, turn left, and move arm to default
    drive.moveX = 0;
    drive.rot = -0.5f;
    drive.setState(Drive.ARM_STATE.DEFAULT);
    sleep(690);

    // stop turning and move left
    drive.rot = 0;
    drive.moveX = -0.5f;
    drive.apply();
    sleep(1800);

    // stop moving left and go backwards
    drive.moveX = 0;
    drive.moveY = -0.5f;
    drive.apply();
    sleep(1380);

    // stop moving forward and go left
    drive.moveY = 0;
    drive.moveX = -0.5f;
    drive.apply();
    sleep(800);

    // stop moving
    drive.moveX = 0;
    drive.apply();
  }
}
