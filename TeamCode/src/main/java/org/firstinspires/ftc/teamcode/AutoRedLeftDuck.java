package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;

//@Disabled
@Autonomous(name = "AutoRedLeftDuck", group = "A")
public class AutoRedLeftDuck extends LinearOpMode {

  AutonomousAPI autonomousAPI;
  DriverControlAPI driverControlAPI;

  // @Override
  public void runOpMode() {
    autonomousAPI = new AutonomousAPI();
    driverControlAPI = new DriverControlAPI();

    autonomousAPI.init(hardwareMap);
    driverControlAPI.init(hardwareMap);

    waitForStart();

    // move a bit forward
    driverControlAPI.moveY = -1;
    driverControlAPI.apply();
    sleep(175);

    // stop moving and rotate to right
    driverControlAPI.moveY = 0;
    driverControlAPI.rot = 1;
    driverControlAPI.apply();
    sleep(400);

    // stop rotating, go back, and pull out the spinner
    driverControlAPI.rot = 0;
    driverControlAPI.moveY = 0.5;
    driverControlAPI.spinnerJointSpeed = 0.6f;
    driverControlAPI.apply();
    sleep(960);

    // stop moving backwards
    driverControlAPI.moveY = 0;
    sleep(500);

    // spin the spinner
    driverControlAPI.spinnerSpeed = -0.2f;
    driverControlAPI.apply();
    sleep(2200);

    // bring back in the spinner, move left, and move the arm to the correct height
    driverControlAPI.spinnerJointSpeed = -0.3f;
    driverControlAPI.spinnerSpeed = 0.49f;
    driverControlAPI.moveX = -1;
    if (autonomousAPI.camResult == CameraPipeline.LOCATION.RIGHT) {
      driverControlAPI.setState(DriverControlAPI.STATE.HIGH);
    } else if (autonomousAPI.camResult == CameraPipeline.LOCATION.MIDDLE) {
      driverControlAPI.setState(DriverControlAPI.STATE.MIDDLE);
    } else {
      driverControlAPI.setState(DriverControlAPI.STATE.LOW);
    }
    sleep(1150);

    // turn a bit and stop moving
    driverControlAPI.moveX = 0;
    driverControlAPI.rot = 0.5f;
    driverControlAPI.apply();
    sleep(125);

    // stop rotating and move forward
    driverControlAPI.rot = 0;
    driverControlAPI.moveY = -1;
    driverControlAPI.apply();
    sleep(500);

    // move forward but slower and stop pulling the spinner joint in
    driverControlAPI.spinnerJointSpeed = 0;
    driverControlAPI.moveY = -0.2;
    driverControlAPI.apply();
    sleep(370);

    // stop moving forward
    driverControlAPI.moveY = 0;
    driverControlAPI.apply();
    sleep(500);

    // let go of the block
    driverControlAPI.clawTargetState = 0;
    driverControlAPI.apply();
    sleep(200);

    // go back
    driverControlAPI.moveY = 1;
    driverControlAPI.apply();
    sleep(510);

    // stop moving back and move right
    driverControlAPI.moveY = 0;
    driverControlAPI.moveX = 1;
    driverControlAPI.apply();
    sleep(610);

    // stop moving
    driverControlAPI.moveX = 0;
    driverControlAPI.apply();
    sleep(200);
  }
}
