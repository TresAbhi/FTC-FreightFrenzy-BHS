package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "Stateless", group = "Linear Opmode")
// @Disabled
public class Stateless extends LinearOpMode {

  private final ElapsedTime runtime = new ElapsedTime();

  private DcMotor leftFront = null;
  private DcMotor leftRear = null;
  private DcMotor rightFront = null;
  private DcMotor rightRear = null;

  private DcMotor extender = null;

  private Servo claw = null;
  private Servo wrist = null;

  private Servo spinner = null;

  @Override
  public void runOpMode() {
    // Components
    leftFront = hardwareMap.get(DcMotor.class, "left_front");
    leftRear = hardwareMap.get(DcMotor.class, "left_rear");
    rightFront = hardwareMap.get(DcMotor.class, "right_front");
    rightRear = hardwareMap.get(DcMotor.class, "right_rear");

    extender = hardwareMap.get(DcMotor.class, "extender");
    claw = hardwareMap.get(Servo.class, "claw");
    wrist = hardwareMap.get(Servo.class, "wrist");

    spinner = hardwareMap.get(Servo.class, "spinner");

    // One time executions
    leftFront.setDirection(DcMotor.Direction.REVERSE);
    rightRear.setDirection(DcMotor.Direction.REVERSE);

    telemetry.addData("Status", "Initialized");
    telemetry.update();

    waitForStart();

    runtime.reset();

    while (opModeIsActive()) {
      telemetry.addData("Status", "Run Time: " + runtime.toString());

      telemetry.addData("Extender position", extender.getCurrentPosition());
      telemetry.addData("Wrist angle", wrist.getPosition());

      telemetry.addData("Left front", leftFront.getCurrentPosition());
      telemetry.addData("Left rear", leftRear.getCurrentPosition());
      telemetry.addData("Right rear", rightRear.getCurrentPosition());
      telemetry.addData("Right front", rightFront.getCurrentPosition());

      telemetry.update();
    }
  }
}
