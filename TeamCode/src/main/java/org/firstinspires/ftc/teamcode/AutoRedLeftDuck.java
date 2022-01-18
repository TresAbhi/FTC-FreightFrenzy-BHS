package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.teamcode.core.AutonomousAPI;
import org.firstinspires.ftc.teamcode.core.CameraPipeline;
import org.firstinspires.ftc.teamcode.core.DriverControlAPI;

//@Disabled
@Autonomous(name = "AutoRedLeftDuck", group = "A")
public class AutoRedLeftDuck extends LinearOpMode {

  final AutonomousAPI auto = new AutonomousAPI();
  final DriverControlAPI drive = new DriverControlAPI();

  // @Override
  public void runOpMode() {
    drive.init(hardwareMap);
    auto.init(hardwareMap);

    drive.compensateForVoltage();

    waitForStart();
    auto.recordTeamScorePos();

    // move a bit forward
    drive.moveY = -1;
    drive.apply();
    sleep(175);

    // stop moving and rotate to right
    drive.moveY = 0;
    drive.rot = 1;
    drive.apply();
    sleep(400);

    // stop rotating, go back, and pull out the spinner
    drive.rot = 0;
    drive.moveY = 0.5f;
    drive.spinnerJointSpeed = 0.6f;
    drive.apply();
    sleep(960);

    // stop moving backwards
    drive.moveY = 0;
    sleep(500);

    // spin the spinner
    drive.spinnerSpeed = -0.2f;
    drive.apply();
    sleep(2500);

    // bring back in the spinner, move left, and move the arm to the correct height
    drive.spinnerJointSpeed = -0.3f;
    drive.spinnerSpeed = 0.49f;
    drive.moveX = -1;
    drive.apply();
    auto.moveArmToCorrectPosition();
    sleep(1150);

    // turn a bit and stop moving
    drive.moveX = 0;
    drive.rot = 0.5f;
    drive.apply();
    sleep(95);

    // stop rotating and move forward
    drive.rot = 0;
    drive.moveY = -1;
    drive.apply();
    sleep(500);

    // move forward but slower and stop pulling the spinner joint in
    drive.spinnerJointSpeed = 0;
    drive.moveY = -0.2f;
    drive.apply();
    sleep(370);

    // stop moving forward
    drive.moveY = 0;
    drive.apply();
    sleep(500);

    // let go of the block
    drive.clawTargetState = 0;
    drive.apply();
    sleep(200);

    // go back
    drive.moveY = 1;
    drive.apply();
    sleep(510);

    // stop moving back and move right
    drive.moveY = 0;
    drive.moveX = 1;
    drive.setState(DriverControlAPI.STATE.LOW);
    sleep(560);

    // stop moving
    drive.moveX = 0;
    drive.apply();
    sleep(200);
  }
}
