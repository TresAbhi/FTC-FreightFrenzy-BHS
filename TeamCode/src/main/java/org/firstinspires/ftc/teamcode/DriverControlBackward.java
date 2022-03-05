package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "DriverControlBackward", group = "Linear Opmode")
@Disabled
public class DriverControlBackward extends LinearOpMode {

  DriverControlBase driverControlBase = new DriverControlBase();

  public void runOpMode() {
    driverControlBase.hardwareMap = hardwareMap;
    driverControlBase.telemetry = telemetry;
    driverControlBase.gamepad1 = gamepad1;
    driverControlBase.gamepad2 = gamepad2;

    driverControlBase.init(hardwareMap, telemetry, gamepad1, gamepad2);

    driverControlBase.drive.headingOffset = Math.PI;

    waitForStart();

    while (opModeIsActive()) {
      driverControlBase.iterate();
    }
  }
}
