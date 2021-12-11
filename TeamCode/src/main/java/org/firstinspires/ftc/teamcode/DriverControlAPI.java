package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Hardware;

public class DriverControlAPI {

  HardwareMap hardwareMap;

  // Components
  private DcMotor LEFT_FRONT;
  private DcMotor LEFT_REAR;
  private DcMotor RIGHT_FRONT;
  private DcMotor RIGHT_REAR;

  private DcMotorEx ARM_JOINT_LEFT;
  private DcMotorEx ARM_JOINT_RIGHT;

  private DcMotor EXTENDER;

  private Servo CLAW;
  private Servo WRIST;

  private Servo SPINNER;

  // Constants

  public float ARM_JOINT_POWER = 0.2f;

  public float EXTENDER_POWER = 0.4f;
  public float ARM_JOINT_MAX_VELOCITY = 320;
  public float ARM_JOINT_VELOCITY_DAMPING = 4;

  public int ARM_JOINT_MIN_ANGLE;
  public int EXTENDER_MIN_POS;

  // Mutables
  public int armJointTargetAngle = ARM_JOINT_MIN_ANGLE;
  public int extenderTargetPos = EXTENDER_MIN_POS;
  public float wristTargetAngle = 0f;
  public float clawTargetState = 0;
  public float spinnerSpeed = 0.49f;

  public double moveX = 0;
  public double moveY = 0;
  public double rotX = 0;
  public double rotY = 0;

  public float movementPower = 1f;

  public DriverControlAPI(HardwareMap h) {
    hardwareMap = h;
  }

  public void init(HardwareMap hardwareMap) {
    // Components
    LEFT_FRONT = hardwareMap.get(DcMotor.class, "left_front");
    LEFT_REAR = hardwareMap.get(DcMotor.class, "left_rear");
    RIGHT_FRONT = hardwareMap.get(DcMotor.class, "right_front");
    RIGHT_REAR = hardwareMap.get(DcMotor.class, "right_rear");

    ARM_JOINT_LEFT = hardwareMap.get(DcMotorEx.class, "conveyor_left");
    ARM_JOINT_RIGHT = hardwareMap.get(DcMotorEx.class, "conveyor_right");
    EXTENDER = hardwareMap.get(DcMotor.class, "extender");
    CLAW = hardwareMap.get(Servo.class, "claw");
    WRIST = hardwareMap.get(Servo.class, "wrist");

    SPINNER = hardwareMap.get(Servo.class, "spinner");

    // One time executions
    LEFT_FRONT.setDirection(DcMotor.Direction.REVERSE);
    RIGHT_REAR.setDirection(DcMotor.Direction.REVERSE);

    ARM_JOINT_LEFT.setDirection(DcMotor.Direction.FORWARD);
    ARM_JOINT_RIGHT.setDirection(DcMotor.Direction.REVERSE);
    ARM_JOINT_LEFT.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    ARM_JOINT_RIGHT.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    ARM_JOINT_LEFT.setTargetPosition(ARM_JOINT_MIN_ANGLE);
    ARM_JOINT_RIGHT.setTargetPosition(ARM_JOINT_MIN_ANGLE);
    ARM_JOINT_LEFT.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    ARM_JOINT_RIGHT.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    ARM_JOINT_LEFT.setPower(ARM_JOINT_POWER);
    ARM_JOINT_RIGHT.setPower(ARM_JOINT_POWER);

    EXTENDER.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    EXTENDER.setTargetPosition(EXTENDER_MIN_POS);
    EXTENDER.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    EXTENDER.setPower(EXTENDER_POWER);

    WRIST.setPosition(0);
  }

  public void iterate() {
    // Trig to find out partial offsets in axes (plural of axis)
    // Don't mess with this unless you know what you're doing!!!
    double vectorNormal = Math.hypot(moveX, moveY);
    double robotAngle = Math.atan2(moveY, -moveX) - Math.PI / 4;
    double vector1 = vectorNormal * Math.cos(robotAngle);
    double vector2 = vectorNormal * Math.sin(robotAngle);
    double vector3 = vectorNormal * Math.sin(robotAngle);
    double vector4 = vectorNormal * Math.cos(robotAngle);

    // Apply wheel motor powers
    LEFT_FRONT.setPower((-vector1 + rotX) * movementPower);
    LEFT_REAR.setPower((-vector2 + rotX) * movementPower);
    RIGHT_FRONT.setPower((-vector3 - rotX) * movementPower);
    RIGHT_REAR.setPower((-vector4 - rotX) * movementPower);

    // Tweak powers based on velocities
    double armJointPowerCoefficient =
      1 -
      Math.pow(
        Math.min(
          Math.abs(ARM_JOINT_LEFT.getVelocity()) / ARM_JOINT_MAX_VELOCITY,
          1
        ),
        ARM_JOINT_VELOCITY_DAMPING
      );
    ARM_JOINT_LEFT.setPower(armJointPowerCoefficient * ARM_JOINT_POWER);
    ARM_JOINT_RIGHT.setPower(armJointPowerCoefficient * ARM_JOINT_POWER);

    // Apply all targets
    ARM_JOINT_LEFT.setTargetPosition(armJointTargetAngle);
    ARM_JOINT_RIGHT.setTargetPosition(armJointTargetAngle);

    EXTENDER.setTargetPosition(extenderTargetPos);

    WRIST.setPosition(wristTargetAngle);

    CLAW.setPosition(clawTargetState);

    SPINNER.setPosition(spinnerSpeed);
  }
}
