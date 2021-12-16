package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class DriverControlAPI {

  HardwareMap hardwareMap;

  // Components
  public DcMotor LEFT_FRONT;
  public DcMotor LEFT_REAR;
  public DcMotor RIGHT_FRONT;
  public DcMotor RIGHT_REAR;

  public DcMotorEx ARM_JOINT_LEFT;
  public DcMotorEx ARM_JOINT_RIGHT;

  public DcMotor EXTENDER;
  public Servo WRIST;
  public Servo CLAW;

  public Servo SPINNER;
  public Servo SPINNER_JOINT;

  // Constants

  public float ARM_JOINT_POWER = 0.4f;
  public float ARM_JOINT_MAX_VELOCITY = 320;
  public int ARM_JOINT_MIN_ANGLE;

  public float EXTENDER_POWER = 0.4f;

  public int EXTENDER_MIN_POS;

  // Preset states

  public enum STATE {
    LOW,
    MIDDLE,
    HIGH,
    GROUND,
    BACK,
  }

  // b: lower tower layer
  public int ARM_JOINT_LOW_ANGLE = ARM_JOINT_MIN_ANGLE + 360;
  public int EXTENDER_LOW_POS = EXTENDER_MIN_POS;
  public float WRIST_LOW_ANGLE = 0.8f;

  // x: middle tower layer
  public int ARM_JOINT_MIDDLE_ANGLE = ARM_JOINT_MIN_ANGLE + 290;
  public int EXTENDER_MIDDLE_POS = EXTENDER_MIN_POS + 110;
  public float WRIST_MIDDLE_ANGLE = 0.75f;

  // y: top tower layer
  public int ARM_JOINT_HIGH_ANGLE = ARM_JOINT_MIN_ANGLE + 190;
  public int EXTENDER_HIGH_POS = EXTENDER_MIN_POS + 630;
  public float WRIST_HIGH_ANGLE = 0.65f;

  // a: ground
  public int ARM_JOINT_GROUND_ANGLE = ARM_JOINT_MIN_ANGLE + 430;
  public int EXTENDER_GROUND_POS = EXTENDER_MIN_POS;
  public float WRIST_GROUND_ANGLE = 1;

  // NONE: back
  public int ARM_JOINT_BACK_ANGLE = ARM_JOINT_MIN_ANGLE;
  public int EXTENDER_BACK_POS = EXTENDER_MIN_POS;
  public float WRIST_BACK_ANGLE = 0;

  // Mutables
  public int armJointTargetAngle = ARM_JOINT_MIN_ANGLE;
  public int extenderTargetPos = EXTENDER_MIN_POS;

  public float wristTargetAngle = 0f;
  public float clawTargetState = 1;

  public float spinnerSpeed = 0.49f;
  public float spinnerJointSpeed = 0.5f;

  public double moveX = 0;
  public double moveY = 0;
  public double rotX = 0;

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

    ARM_JOINT_LEFT = hardwareMap.get(DcMotorEx.class, "arm_joint_left");
    ARM_JOINT_RIGHT = hardwareMap.get(DcMotorEx.class, "arm_joint_right");
    EXTENDER = hardwareMap.get(DcMotor.class, "extender");
    CLAW = hardwareMap.get(Servo.class, "claw");
    WRIST = hardwareMap.get(Servo.class, "wrist");

    SPINNER = hardwareMap.get(Servo.class, "spinner");
    SPINNER_JOINT = hardwareMap.get(Servo.class, "spinner_joint");

    // One time executions
    LEFT_FRONT.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    LEFT_REAR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    RIGHT_FRONT.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    RIGHT_REAR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    LEFT_FRONT.setDirection(DcMotor.Direction.REVERSE);
    RIGHT_REAR.setDirection(DcMotor.Direction.REVERSE);

    ARM_JOINT_LEFT.setDirection(DcMotor.Direction.FORWARD);
    ARM_JOINT_RIGHT.setDirection(DcMotor.Direction.REVERSE);
    ARM_JOINT_LEFT.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    ARM_JOINT_RIGHT.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    ARM_JOINT_LEFT.setTargetPosition(ARM_JOINT_MIN_ANGLE);
    ARM_JOINT_RIGHT.setTargetPosition(ARM_JOINT_MIN_ANGLE);
    ARM_JOINT_LEFT.setTargetPositionTolerance(0);
    ARM_JOINT_RIGHT.setTargetPositionTolerance(0);
    ARM_JOINT_LEFT.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    ARM_JOINT_RIGHT.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    ARM_JOINT_LEFT.setPower(ARM_JOINT_POWER);
    ARM_JOINT_RIGHT.setPower(ARM_JOINT_POWER);
    ARM_JOINT_LEFT.setVelocityPIDFCoefficients(1, 0.1, 0, 10);
    ARM_JOINT_RIGHT.setVelocityPIDFCoefficients(1, 0.1, 0, 10);

    EXTENDER.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    EXTENDER.setTargetPosition(EXTENDER_MIN_POS);
    EXTENDER.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    EXTENDER.setPower(EXTENDER_POWER);

    WRIST.setPosition(0);
  }

  public void setState(STATE state) {
    if (state == STATE.LOW) {
      armJointTargetAngle = ARM_JOINT_LOW_ANGLE;
      extenderTargetPos = EXTENDER_LOW_POS;
      wristTargetAngle = WRIST_LOW_ANGLE;
      apply();
    } else if (state == STATE.MIDDLE) {
      armJointTargetAngle = ARM_JOINT_MIDDLE_ANGLE;
      extenderTargetPos = EXTENDER_MIDDLE_POS;
      wristTargetAngle = WRIST_MIDDLE_ANGLE;
      apply();
    } else if (state == STATE.HIGH) {
      armJointTargetAngle = ARM_JOINT_HIGH_ANGLE;
      extenderTargetPos = EXTENDER_HIGH_POS;
      wristTargetAngle = WRIST_HIGH_ANGLE;
      apply();
    } else if (state == STATE.GROUND) {
      armJointTargetAngle = ARM_JOINT_GROUND_ANGLE;
      extenderTargetPos = EXTENDER_GROUND_POS;
      wristTargetAngle = WRIST_GROUND_ANGLE;
      apply();
    } else if (state == STATE.BACK) {
      armJointTargetAngle = ARM_JOINT_BACK_ANGLE;
      extenderTargetPos = EXTENDER_BACK_POS;
      wristTargetAngle = WRIST_BACK_ANGLE;
      apply();
    }
  }

  public void apply() {
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

    // Apply all targets
    ARM_JOINT_LEFT.setTargetPosition(armJointTargetAngle);
    ARM_JOINT_RIGHT.setTargetPosition(armJointTargetAngle);

    EXTENDER.setTargetPosition(extenderTargetPos);

    WRIST.setPosition(wristTargetAngle);

    CLAW.setPosition(clawTargetState);

    SPINNER.setPosition(spinnerSpeed);
    SPINNER_JOINT.setPosition(spinnerJointSpeed);
  }
}
