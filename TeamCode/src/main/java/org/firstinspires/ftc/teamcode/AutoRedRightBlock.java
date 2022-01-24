package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.teamcode.core.Auto;
import org.firstinspires.ftc.teamcode.core.Drive;

//@Disabled
@Autonomous(name = "AutoRedRightBlock", group = "A")
public class AutoRedRightBlock extends LinearOpMode {

  final Auto auto = new Auto();
  final Drive drive = new Drive();

  // @Override
  public void runOpMode() {
    drive.init(hardwareMap);
    auto.init(hardwareMap, telemetry, drive);

    drive.compensateForVoltage();

    waitForStart();
    auto.recordTeamScorePos();

    // go forward
    drive.moveY = 1;
    drive.apply();
    sleep(150);

    // stop moving forward and move left
    drive.moveY = 0;
    drive.moveX = -1;
    drive.apply();
    sleep(250);

    // move arm to position
    auto.moveArmToCorrectPosition();
    sleep(500);

    // stop going left and go forward
    drive.moveX = 0;
    drive.moveY = 0.5f;
    drive.apply();
    sleep(480);

    // stop moving
    drive.moveY = 0;
    drive.apply();
    sleep(200);

    // drop block
    drive.clawTargetState = 0;
    drive.apply();
    sleep(200);

    // go back
    drive.moveY = -1;
    drive.apply();
    sleep(140);

    // stop moving, turn right, set to low state, and mvoe the wrist up
    drive.moveY = 0;
    drive.rot = 0.5f;
    drive.setState(Drive.ARM_STATE.DEFAULT);
    drive.wristTargetAngle = 1;
    sleep(850);

    // stop turning
    drive.rot = 0;
    drive.apply();
    sleep(200);

    // move forward
    drive.moveY = 1;
    drive.apply();
    sleep(1500);

    // stop moving forward
    drive.moveY = 0;
    drive.apply();
    sleep(200);

    //move to the left
    drive.moveX = -1;
    drive.apply();
    sleep(400);

    //stop moving left
    drive.moveX = 0;
    drive.apply();
    sleep(200);

    //forward a bit
    drive.moveY = 1;
    drive.apply();
    sleep(300);

    // stop moving
    drive.moveY = 0;
    drive.apply();
    sleep(200);
  }
}
