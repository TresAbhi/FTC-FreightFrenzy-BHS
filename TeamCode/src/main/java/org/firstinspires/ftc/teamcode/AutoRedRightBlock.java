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
    drive.init(hardwareMap, telemetry);
    auto.init(hardwareMap, telemetry, drive);

    waitForStart();
    auto.recordTeamScorePos();

    // go forward
    drive.moveY = 0.5f;
    drive.apply();
    sleep(300);

    // stop moving forward and move left
    drive.moveY = 0;
    drive.moveX = -0.5f;
    drive.apply();
    sleep(750);

    // move arm to position
    auto.moveArmToCorrectPosition();
    sleep(500);

    // stop going left and go forward
    drive.moveX = 0;
    drive.moveY = 0.5f;
    drive.apply();
    sleep(750);

    // stop moving
    drive.moveY = 0;
    drive.apply();
    sleep(500);

    // drop block
    drive.clawTargetAngle = 0;
    drive.apply();
    sleep(500);

    // go back
    drive.moveY = -0.5f;
    drive.apply();
    sleep(280);

    // stop moving, turn right, set to default state, and move the wrist up
    drive.moveY = 0;
    drive.rot = 0.5f;
    drive.wristTargetAngle = 1;
    drive.setState(Drive.ARM_STATE.DEFAULT);
    sleep(900);

    // stop turning
    drive.rot = 0;
    drive.apply();
    sleep(200);

    // move forward
    drive.moveY = 1;
    drive.apply();
    sleep(1900);

    // stop moving forward
    drive.moveY = 0;
    drive.apply();
    sleep(200);
  }
}
