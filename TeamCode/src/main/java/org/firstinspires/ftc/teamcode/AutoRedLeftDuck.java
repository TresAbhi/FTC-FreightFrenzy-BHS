package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.teamcode.core.Auto;
import org.firstinspires.ftc.teamcode.core.Drive;

//@Disabled
@Autonomous(name = "A-PROTO-AutoRedLeftDuck", group = "A")
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
    drive.spinnerJointSpeed = 0.75f;
    sleep(500);

    // spin the spinner
    drive.spinnerSpeed = -0.2f;
    drive.apply();
    sleep(2500);

    // bring back in the spinner, move left, and move the arm to the correct height
    drive.spinnerJointSpeed = -0.3f;
    drive.spinnerSpeed = 0.49f;
    drive.moveX = -0.5f;
    drive.apply();
    sleep(2500);

    // turn a bit and stop moving
    drive.moveX = 0;
    drive.rot = 0.5f;
    drive.apply();
    sleep(20);

    // stop rotating and move forward
    drive.rot = 0;
    drive.moveY = 0.5f;
    auto.moveArmToCorrectPosition();
    drive.apply();
    sleep(1200);

    // move forward but slower and stop pulling the spinner joint in
    drive.spinnerJointSpeed = 0;
    drive.moveY = 0.2f;
    drive.apply();
    sleep(370);

    // stop moving forward
    drive.moveY = 0;
    drive.apply();
    sleep(500);

    // let go of the block
    drive.clawTargetState = 0;
    drive.apply();
    sleep(500);

    // go back
    drive.moveY = -0.5f;
    drive.apply();
    sleep(1450);

    // stop moving back and move right
    drive.moveY = 0;
    drive.moveX = 0.5f;
    drive.setState(Drive.ARM_STATE.DEFAULT);
    sleep(1020);

    // stop moving
    drive.moveX = 0;
    drive.apply();
    sleep(200);
  }
}
