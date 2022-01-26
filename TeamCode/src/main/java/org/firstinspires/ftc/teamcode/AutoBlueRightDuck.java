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
    drive.init(hardwareMap);
    auto.init(hardwareMap, telemetry, drive);

    waitForStart();
    auto.recordTeamScorePos();

    // go forward
    drive.moveY = 0.5f;
    drive.apply();
    sleep(240);

    // stop moving forward, move right,
    drive.moveY = 0;
    drive.moveX = 0.5f;
    drive.apply();
    sleep(1400);

    // pull out the spinner, and spin the spinner, and stop moving right
    drive.spinnerJointSpeed = 0.6f;
    drive.spinnerSpeed = 0.8f;
    drive.moveX = 0;
    drive.apply();
    sleep(300);

    // stop moving and let the duck fall off
    drive.moveX = 0;
    drive.apply();
    sleep(2500);

    // pull back spinner, stop spinner and move right and a bit forward
    drive.spinnerJointSpeed = -0.2f;
    drive.spinnerSpeed = 0.49f;
    drive.moveX = 0.5f;
    drive.moveY = 0.1f;
    drive.apply();
    sleep(200);

    // stop moving right and move forward
    drive.moveX = 0;
    drive.moveY = 0.5f;
    drive.apply();
    sleep(1800);

    // stop moving forward and move arm to correct height
    drive.moveY = 0;
    drive.apply();
    auto.moveArmToCorrectPosition();
    sleep(500);

    // and rotate left
    drive.rot = -0.5f;
    drive.apply();
    sleep(670);

    // stop rotating and move forward
    drive.rot = 0;
    drive.moveY = 0.5f;
    drive.apply();
    sleep(1150);

    // stop moving
    drive.moveY = 0;
    drive.apply();
    sleep(500);

    // let go
    drive.clawTargetState = 0;
    drive.apply();
    sleep(500);

    // move back
    drive.moveY = -0.5f;
    drive.apply();
    sleep(1650);

    // stop moving back and move to the left
    drive.moveY = 0;
    drive.setState(Drive.ARM_STATE.DEFAULT);
    drive.moveX = -0.5f;
    drive.apply();
    sleep(920);

    // stop moving left
    drive.moveX = 0;
    drive.apply();
    sleep(220);
  }
}
