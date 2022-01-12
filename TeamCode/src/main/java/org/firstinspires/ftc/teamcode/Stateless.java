package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "Stateless", group = "Linear Opmode")
// @Disabled
public class Stateless extends LinearOpMode {

  private ElapsedTime runtime = new ElapsedTime();

  private DcMotor LEFT_FRONT = null;
  private DcMotor LEFT_REAR = null;
  private DcMotor RIGHT_FRONT = null;
  private DcMotor RIGHT_REAR = null;

  private DcMotor EXTENDER = null;

  private Servo CLAW = null;
  private Servo WRIST = null;

  private Servo SPINNER = null;

  @Override
  public void runOpMode() {
    // Components
    LEFT_FRONT = hardwareMap.get(DcMotor.class, "left_front");
    LEFT_REAR = hardwareMap.get(DcMotor.class, "left_rear");
    RIGHT_FRONT = hardwareMap.get(DcMotor.class, "right_front");
    RIGHT_REAR = hardwareMap.get(DcMotor.class, "right_rear");

    EXTENDER = hardwareMap.get(DcMotor.class, "extender");
    CLAW = hardwareMap.get(Servo.class, "claw");
    WRIST = hardwareMap.get(Servo.class, "wrist");

    SPINNER = hardwareMap.get(Servo.class, "spinner");

    // One time executions
    LEFT_FRONT.setDirection(DcMotor.Direction.REVERSE);
    RIGHT_REAR.setDirection(DcMotor.Direction.REVERSE);

    telemetry.addData("Status", "Initialized");
    telemetry.update();

    waitForStart();

    runtime.reset();

    while (opModeIsActive()) {
      telemetry.addData("Status", "Run Time: " + runtime.toString());

      telemetry.addData("Extender position", EXTENDER.getCurrentPosition());
      telemetry.addData("Wrist angle", WRIST.getPosition());

      telemetry.update();
    }
  }
}
