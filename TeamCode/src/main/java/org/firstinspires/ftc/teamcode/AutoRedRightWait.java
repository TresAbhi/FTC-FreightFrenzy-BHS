package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.teamcode.core.AutonomousAPI;
import org.firstinspires.ftc.teamcode.core.CameraPipeline;
import org.firstinspires.ftc.teamcode.core.DriverControlAPI;

//@Disabled
@Autonomous(name = "AutoRedRightWait", group = "A")
public class AutoRedRightWait extends LinearOpMode {

  final AutoRedRightBlock autoRedRightBlock = new AutoRedRightBlock();

  // @Override
  public void runOpMode() {
    sleep(15000);
    autoRedRightBlock.runOpMode();
  }
}
