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

  // @Override
  public void runOpMode() {
    autonomousAPI = new AutonomousAPI();
    driverControlAPI = new DriverControlAPI();

    autonomousAPI.init(hardwareMap);
    driverControlAPI.init(hardwareMap);

    waitForStart();

    /*
    if (autonomousAPI.camResult == CameraPipeline.LOCATION.RIGHT) {
      driverControlAPI.setState(DriverControlAPI.STATE.HIGH);
    } else if (autonomousAPI.camResult == CameraPipeline.LOCATION.MIDDLE) {
      driverControlAPI.setState(DriverControlAPI.STATE.MIDDLE);
    } else {
      driverControlAPI.setState(DriverControlAPI.STATE.LOW);
    }
    */
  }
}
