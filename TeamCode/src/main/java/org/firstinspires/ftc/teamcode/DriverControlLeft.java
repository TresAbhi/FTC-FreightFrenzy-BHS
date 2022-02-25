package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "DriverControlLeft", group = "Linear Opmode")
// @Disabled
public class DriverControlLeft extends LinearOpMode {

  DriverControlBase driverControlBase = new DriverControlBase();

  public void runOpMode() {
    driverControlBase.hardwareMap = hardwareMap;
    driverControlBase.telemetry = telemetry;
    driverControlBase.gamepad1 = gamepad1;
    driverControlBase.gamepad2 = gamepad2;

    driverControlBase.init(hardwareMap, telemetry, gamepad1, gamepad2);

    driverControlBase.drive.headingOffset = -Math.PI / 2;

    waitForStart();

    while (opModeIsActive()) {
      driverControlBase.iterate();
    }
  }
}
