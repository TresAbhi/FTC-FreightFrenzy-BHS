package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;

//@Disabled
@Autonomous(name = "AutoBlueRightDuck", group = "A")
public class AutoBlueRightDuck extends LinearOpMode {

  AutonomousAPI autonomousAPI;
  DriverControlAPI driverControlAPI;
  SampleMecanumDrive drive;

  // @Override
  public void runOpMode() {
    autonomousAPI = new AutonomousAPI();
    driverControlAPI = new DriverControlAPI();
    drive = new SampleMecanumDrive(hardwareMap);

    autonomousAPI.init(hardwareMap);
    driverControlAPI.init(hardwareMap);

    // Positions
    Pose2d startPosition = new Pose2d(-39, -62, Math.toRadians(90));

    // Trajectories
    // move to the duck spinner
    Trajectory step1 = drive
      .trajectoryBuilder(startPosition)
      .forward(50)
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
