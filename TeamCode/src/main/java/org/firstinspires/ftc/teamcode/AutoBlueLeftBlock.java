package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.teamcode.core.Auto;
import org.firstinspires.ftc.teamcode.core.Drive;

//@Disabled
@Autonomous(name = "AutoBlueLeftBlock", group = "A")
public class AutoBlueLeftBlock extends LinearOpMode {

  final Auto auto = new Auto();
  final Drive drive = new Drive();

  // @Override
  public void runOpMode() {
    drive.init(hardwareMap);
    auto.init(hardwareMap, telemetry, drive);

    waitForStart();
    auto.recordTeamScorePos();

    // go forward
    drive.moveY = 0.5f;
    drive.apply();
    sleep(300);

    // stop moving forward and move right
    drive.moveY = 0;
    drive.moveX = 0.5f;
    drive.apply();
    sleep(1400);

    // move arm to position
    auto.moveArmToCorrectPosition();
    sleep(500);

    // stop moving right and go forward
    drive.moveX = 0;
    drive.moveY = 0.5f;
    drive.apply();
    sleep(1100);

    // stop moving
    drive.moveY = 0;
    drive.apply();
    sleep(500);

    // drop block
    drive.clawTargetState = 0;
    drive.apply();
    sleep(500);

    // go back
    drive.moveY = -0.5f;
    drive.apply();
    sleep(700);

    // stop moving, turn left, set to low state, and move the arm back
    drive.moveY = 0;
    drive.rot = -0.5f;
    drive.setState(Drive.ARM_STATE.DEFAULT);
    drive.wristTargetAngle = 1;
    sleep(730);

    // stop turning
    drive.rot = 0;
    drive.apply();
    sleep(200);

    // move forward
    drive.moveY = 1;
    drive.apply();
    sleep(1900);

    // stop moving
    drive.moveY = 0;
    drive.apply();
    sleep(200);
  }
}
