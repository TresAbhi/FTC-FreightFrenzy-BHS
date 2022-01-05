package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;

//@Disabled
@Autonomous(name = "AutoBlueRightDuck", group = "A")
public class AutoBlueRightDuck extends LinearOpMode {

  AutonomousAPI autonomousAPI = new AutonomousAPI();
  DriverControlAPI driverControlAPI = new DriverControlAPI();
  SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);

  // @Override
  public void runOpMode() {
    autonomousAPI.init(hardwareMap);
    driverControlAPI.init(hardwareMap);

    // Positions
    Pose2d startPosition = new Pose2d(-63, 45, Math.toRadians(90));

    // Trajectories
    // move to the duck spinner
    Trajectory step1 = drive
      .trajectoryBuilder(startPosition)
      .strafeTo(new Vector2d(-58, 60))
      .build();

    // One-time executions
    drive.setPoseEstimate(startPosition);

    waitForStart();

    drive.followTrajectory(step1);

    if (autonomousAPI.camResult == CameraPipeline.LOCATION.RIGHT) {
      driverControlAPI.setState(DriverControlAPI.STATE.HIGH);
    } else if (autonomousAPI.camResult == CameraPipeline.LOCATION.MIDDLE) {
      driverControlAPI.setState(DriverControlAPI.STATE.MIDDLE);
    } else {
      driverControlAPI.setState(DriverControlAPI.STATE.LOW);
    }
  }
}
