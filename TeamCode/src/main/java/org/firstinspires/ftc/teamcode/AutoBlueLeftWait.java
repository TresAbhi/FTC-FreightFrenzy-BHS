package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.teamcode.core.AutonomousAPI;
import org.firstinspires.ftc.teamcode.core.CameraPipeline;
import org.firstinspires.ftc.teamcode.core.DriverControlAPI;

//@Disabled
@Autonomous(name = "AutoBlueLeftWait", group = "A")
public class AutoBlueLeftWait extends LinearOpMode {

  final AutoBlueLeftBlock autoBlueLeftBlock = new AutoBlueLeftBlock();

  // @Override
  public void runOpMode() {
    sleep(15000);
    autoBlueLeftBlock.runOpMode();
  }
}
