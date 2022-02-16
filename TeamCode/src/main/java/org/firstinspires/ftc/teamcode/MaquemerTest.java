package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

//@Disabled
@Autonomous(name = "AA-PROTO-MaquemerTest", group = "A")
public class MaquemerTest extends LinearOpMode {

  public DcMotor motor;

  // @Override
  public void runOpMode() {
    motor = hardwareMap.get(DcMotor.class, "right_rear"); // slot 3

    waitForStart();

    motor.setPower(0.5);

    while (true) {
      motor.setTargetPosition(0);
      motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
      sleep(4000);

      motor.setTargetPosition(538);
      motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
      sleep(4000);
    }
  }
}
