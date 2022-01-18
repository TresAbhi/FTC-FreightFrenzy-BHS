package org.firstinspires.ftc.teamcode.core;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.VoltageSensor;

public class DriverControlAPI {

  // Components
  public DcMotor leftFront, leftRear, rightFront, rightRear;

  public DcMotor extender;
  public Servo wrist, claw;

  public Servo spinner, spinnerJoint;

  // Constants
  public final float EXTENDER_POWER = 0.8f;
  public final int EXTENDER_MIN_POS = 0;

  public final float NORMAL_VOLTAGE = 13;

  // Preset states
  public enum STATE {
    LOW,
    MIDDLE,
    HIGH,
    GROUND,
    BACK,
  }

  // a: lower tower layer
  public final int EXTENDER_LOW_POS = EXTENDER_MIN_POS + 55;
  public final float WRIST_LOW_ANGLE = 0.57f;

  // x: middle tower layer
  public final int EXTENDER_MIDDLE_POS = EXTENDER_MIN_POS + 730;
  public final float WRIST_MIDDLE_ANGLE = 0.57f;

  // y: top tower layer
  public final int EXTENDER_HIGH_POS = EXTENDER_MIN_POS + 1590;
  public final float WRIST_HIGH_ANGLE = 0.57f;

  // b: ground
  public final int EXTENDER_GROUND_POS = EXTENDER_MIN_POS;
  public final float WRIST_GROUND_ANGLE = 0.425f;

  // NONE: back
  public final int EXTENDER_BACK_POS = EXTENDER_MIN_POS;
  public final float WRIST_BACK_ANGLE = 1f;

  // Mutables
  public int extenderTargetPos = EXTENDER_MIN_POS;

  public float wristTargetAngle = 1;
  public float clawTargetState = 1;

  public float spinnerSpeed = 0.49f;
  public float spinnerJointSpeed = 0f;

  public float moveX = 0;
  public float moveY = 0;
  public float rot = 0;

  public float movementPower = 1f;
  public float voltageCompensatedPower = 1f;

  public HardwareMap hardwareMap;

  public void init(HardwareMap hm) {
    hardwareMap = hm;

    // Components
    leftFront = hardwareMap.get(DcMotor.class, "left_front");
    leftRear = hardwareMap.get(DcMotor.class, "left_rear");
    rightFront = hardwareMap.get(DcMotor.class, "right_front");
    rightRear = hardwareMap.get(DcMotor.class, "right_rear");

    extender = hardwareMap.get(DcMotor.class, "extender");
    claw = hardwareMap.get(Servo.class, "claw");
    wrist = hardwareMap.get(Servo.class, "wrist");

    spinner = hardwareMap.get(Servo.class, "spinner");
    spinnerJoint = hardwareMap.get(Servo.class, "spinner_joint");

    // One time executions
    leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    leftRear.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    rightRear.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    leftFront.setDirection(DcMotor.Direction.REVERSE);
    rightRear.setDirection(DcMotor.Direction.REVERSE);

    extender.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    extender.setTargetPosition(EXTENDER_MIN_POS);
    extender.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    extender.setPower(EXTENDER_POWER);

    wrist.setPosition(1);
    claw.setPosition(clawTargetState);
  }

  public void setState(STATE state) {
    if (state == STATE.LOW) {
      wristTargetAngle = WRIST_LOW_ANGLE;
      extenderTargetPos = EXTENDER_LOW_POS;
      apply();
    } else if (state == STATE.MIDDLE) {
      wristTargetAngle = WRIST_MIDDLE_ANGLE;
      extenderTargetPos = EXTENDER_MIDDLE_POS;
      apply();
    } else if (state == STATE.HIGH) {
      wristTargetAngle = WRIST_HIGH_ANGLE;
      extenderTargetPos = EXTENDER_HIGH_POS;
      apply();
    } else if (state == STATE.GROUND) {
      wristTargetAngle = WRIST_GROUND_ANGLE;
      extenderTargetPos = EXTENDER_GROUND_POS;
      apply();
    } else if (state == STATE.BACK) {
      wristTargetAngle = WRIST_BACK_ANGLE;
      extenderTargetPos = EXTENDER_BACK_POS;
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
    leftFront.setPower(
      (-vector1 + rot) * movementPower * voltageCompensatedPower
    );
    leftRear.setPower(
      (-vector2 + rot) * movementPower * voltageCompensatedPower
    );
    rightFront.setPower(
      (-vector3 - rot) * movementPower * voltageCompensatedPower
    );
    rightRear.setPower(
      (-vector4 - rot) * movementPower * voltageCompensatedPower
    );

    // Apply all targets
    extender.setTargetPosition(extenderTargetPos);

    wrist.setPosition(wristTargetAngle);

    claw.setPosition(clawTargetState);

    spinner.setPosition(spinnerSpeed);
    spinnerJoint.setPosition(spinnerJointSpeed);
  }

  public float getBatteryVoltage() {
    double result = Double.POSITIVE_INFINITY;

    for (VoltageSensor sensor : hardwareMap.voltageSensor) {
      double voltage = sensor.getVoltage();

      if (voltage > 0) {
        result = Math.min(result, voltage);
      }
    }

    return (float) result;
  }

  public void compensateForVoltage() {
    voltageCompensatedPower = NORMAL_VOLTAGE / getBatteryVoltage();
  }
}
