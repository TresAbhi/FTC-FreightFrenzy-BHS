package org.firstinspires.ftc.teamcode;

import android.os.SystemClock;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;

public class DriverControlAPI {

  // Components
  public DcMotor LEFT_FRONT, LEFT_REAR, RIGHT_FRONT, RIGHT_REAR;

  public DcMotor EXTENDER;
  public Servo WRIST, CLAW;

  public Servo SPINNER, SPINNER_JOINT;

  // Constants
  public float EXTENDER_POWER = 0.8f;

  public int EXTENDER_MIN_POS;

  // Preset states

  public enum STATE {
    LOW,
    MIDDLE,
    HIGH,
    GROUND,
    BACK,
  }

  // a: lower tower layer
  public int EXTENDER_LOW_POS = EXTENDER_MIN_POS + 48;
  public float WRIST_LOW_ANGLE = 0.57f;

  // x: middle tower layer
  public int EXTENDER_MIDDLE_POS = EXTENDER_MIN_POS + 730;
  public float WRIST_MIDDLE_ANGLE = 0.57f;

  // y: top tower layer
  public int EXTENDER_HIGH_POS = EXTENDER_MIN_POS + 1509;
  public float WRIST_HIGH_ANGLE = 0.57f;

  // b: ground
  public int EXTENDER_GROUND_POS = EXTENDER_MIN_POS;
  public float WRIST_GROUND_ANGLE = 0.425f;

  // NONE: back
  public int EXTENDER_BACK_POS = EXTENDER_MIN_POS;
  public float WRIST_BACK_ANGLE = 1f;

  // Mutables
  public int extenderTargetPos = EXTENDER_MIN_POS;

  public float wristTargetAngle = 1;
  public float clawTargetState = 0;

  public float spinnerSpeed = 0.49f;
  public float spinnerJointSpeed = 0f;

  public double moveX = 0;
  public double moveY = 0;
  public double rotX = 0;

  public float movementPower = 1f;

  public void init(HardwareMap hardwareMap) {
    // Components
    LEFT_FRONT = hardwareMap.get(DcMotor.class, "left_front");
    LEFT_REAR = hardwareMap.get(DcMotor.class, "left_rear");
    RIGHT_FRONT = hardwareMap.get(DcMotor.class, "right_front");
    RIGHT_REAR = hardwareMap.get(DcMotor.class, "right_rear");

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

    EXTENDER.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    EXTENDER.setTargetPosition(EXTENDER_MIN_POS);
    EXTENDER.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    EXTENDER.setPower(EXTENDER_POWER);

    WRIST.setPosition(1);
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
    LEFT_FRONT.setPower((-vector1 + rotX) * movementPower);
    LEFT_REAR.setPower((-vector2 + rotX) * movementPower);
    RIGHT_FRONT.setPower((-vector3 - rotX) * movementPower);
    RIGHT_REAR.setPower((-vector4 - rotX) * movementPower);

    // Apply all targets
    EXTENDER.setTargetPosition(extenderTargetPos);

    WRIST.setPosition(wristTargetAngle);

    CLAW.setPosition(clawTargetState);

    SPINNER.setPosition(spinnerSpeed);
    SPINNER_JOINT.setPosition(spinnerJointSpeed);
  }
}
