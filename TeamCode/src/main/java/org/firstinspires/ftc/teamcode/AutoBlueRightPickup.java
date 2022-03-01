package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.teamcode.core.Auto;
import org.firstinspires.ftc.teamcode.core.Drive;

//@Disabled
@Autonomous(name = "AutoBlueRightPickup", group = "A")
public class AutoBlueRightPickup extends LinearOpMode {

  final Auto auto = new Auto();
  final Drive drive = new Drive();

  // @Override
  public void runOpMode() {
    drive.init(hardwareMap, telemetry);
    auto.init(hardwareMap, telemetry, drive);

    waitForStart();
    auto.recordTeamScorePos();

    // go forward
    drive.moveY = 0.5;
    drive.apply();
    sleep(240);

    // stop moving forward, move right
    drive.moveY = 0;
    drive.moveX = 0.5;
    drive.apply();
    sleep(1400);

    // pull out the spinner, and spin the spinner, and stop moving right
    drive.spinnerJointPos = 0.7;
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
    sleep(500);

    // move right
    drive.moveX = 0.5;
    drive.apply();
    sleep(400);

    // stop moving right and move forward
    drive.moveX = 0;
    drive.moveY = 0.5;
    drive.apply();
    sleep(1900);

    // stop moving forward and move arm to correct height
    drive.moveY = 0;
    drive.apply();
    auto.moveArmToCorrectPosition();
    sleep(500);

    // and turn left
    drive.rot = -0.5;
    drive.apply();
    sleep(690);

    // stop turning and move forward
    drive.rot = 0;
    drive.moveY = 0.5;
    drive.apply();
    sleep(1350);

    // stop moving
    drive.moveY = 0;
    drive.apply();
    sleep(500);

    // let go
    drive.clawTargetState = 0;
    drive.apply();
    sleep(500);

    // move back
    drive.moveY = -0.5;
    drive.apply();
    sleep(2050);

    // stop moving back, move arm to default, and move capper down
    drive.moveY = 0;
    drive.setState(Drive.ARM_STATE.DEFAULT);
    drive.capperTargetAngle = 1;
    drive.apply();
    sleep(800);

    // slide to the left
    drive.moveX = -0.5;
    drive.apply();
    sleep(1100);

    // stop moving right
    drive.moveX = 0;
    drive.apply();
    sleep(200);

    // move forward, slowly
    drive.moveY = 0.25;
    drive.apply();
    sleep(1900);

    // stop moving
    drive.moveY = 0;
    drive.apply();
    sleep(200);
  }
}
