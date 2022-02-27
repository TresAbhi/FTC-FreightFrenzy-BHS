package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "DriverControlNormal", group = "Linear Opmode")
// @Disabled
public class DriverControlNormal extends LinearOpMode {

  DriverControlBase driverControlBase = new DriverControlBase();

  public void runOpMode() {
    driverControlBase.hardwareMap = hardwareMap;
    driverControlBase.telemetry = telemetry;
    driverControlBase.gamepad1 = gamepad1;
    driverControlBase.gamepad2 = gamepad2;
    driverControlBase.drive.useGyro = false;

    driverControlBase.init(hardwareMap, telemetry, gamepad1, gamepad2);

    waitForStart();

    while (opModeIsActive()) {
      driverControlBase.iterate();
    }
  }
}
