package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;

@Autonomous(name = "Autonomous1", group = "A")
// @Disabled
public class Autonomous1 extends LinearOpMode {

  private ElapsedTime runtime = new ElapsedTime();

  private DcMotor LEFT_FRONT = null;
  private DcMotor LEFT_REAR = null;
  private DcMotor RIGHT_FRONT = null;
  private DcMotor RIGHT_REAR = null;

  private DcMotorEx ARM_JOINT_LEFT = null;
  private DcMotorEx ARM_JOINT_RIGHT = null;

  private DcMotor EXTENDER = null;

  private Servo CLAW = null;
  private Servo WRIST = null;

  private Servo SPINNER = null;

  private Gamepad player1 = gamepad1;
  private Gamepad player2 = gamepad2;

  // Constants
  public float MOVEMENT_PRECISION = 2f;

  public int ARM_JOINT_MIN_ANGLE = 55;
  public int ARM_JOINT_MAX_ANGLE = ARM_JOINT_MIN_ANGLE + 430;
  public float ARM_JOINT_POWER = 0.2f;
  public int ARM_JOINT_INPUT_SPEED = 4;

  public int EXTENDER_MIN_POS = 40;
  public int EXTENDER_MAX_POS = EXTENDER_MIN_POS + 1490;
  public float EXTENDER_POWER = 0.4f;
  public float ARM_JOINT_MAX_VELOCITY = 320;
  public float ARM_JOINT_VELOCITY_DAMPING = 4;
  public int EXTENDER_INPUT_SPEED = 24;

  public float WRIST_INPUT_SPEED = 0.03f;

  public float SPEED_LOW_POWER = 0.4f;
  public float SPEED_HIGH_POWER = 0.8f;

  // Preset states

  // b: lower tower layer
  public int ARM_JOINT_LOW_ANGLE = ARM_JOINT_MIN_ANGLE + 310;
  public int EXTENDER_LOW_POS = EXTENDER_MIN_POS;
  public float WRIST_LOW_ANGLE = 0.8f;

  // x: middle tower layer
  public int ARM_JOINT_MIDDLE_ANGLE = ARM_JOINT_MIN_ANGLE + 245;
  public int EXTENDER_MIDDLE_POS = EXTENDER_MIN_POS + 110;
  public float WRIST_MIDDLE_ANGLE = 0.75f;

  // y: top tower layer
  public int ARM_JOINT_HIGH_ANGLE = ARM_JOINT_MIN_ANGLE + 190;
  public int EXTENDER_HIGH_POS = EXTENDER_MIN_POS + 630;
  public float WRIST_HIGH_ANGLE = 0.65f;

  // x: ground
  public int ARM_JOINT_GROUND_ANGLE = ARM_JOINT_MIN_ANGLE + 430;
  public int EXTENDER_GROUND_POS = EXTENDER_MIN_POS;
  public float WRIST_GROUND_ANGLE = 1f;

  // Mutables
  public int armJointTargetAngle = ARM_JOINT_MIN_ANGLE;
  public int extenderTargetPos = EXTENDER_MIN_POS;
  public float wristTargetAngle = 0f;

  String driveMode = "normal";
  boolean isModeSwitched = false;

  OpenCvWebcam webcam;
  TeamScoreDeterminationPipeline pipeline = new TeamScoreDeterminationPipeline();

  // @Override
  public void runOpMode() {
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

    // TEMPORARY
    // ARM_JOINT_LEFT.setDirection(DcMotor.Direction.FORWARD);
    // ARM_JOINT_RIGHT.setDirection(DcMotor.Direction.REVERSE);
    // ARM_JOINT_LEFT.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    // ARM_JOINT_RIGHT.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    // ARM_JOINT_LEFT.setTargetPosition(ARM_JOINT_MIN_ANGLE);
    // ARM_JOINT_RIGHT.setTargetPosition(ARM_JOINT_MIN_ANGLE);
    // ARM_JOINT_LEFT.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    // ARM_JOINT_RIGHT.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    // ARM_JOINT_LEFT.setPower(ARM_JOINT_POWER);
    // ARM_JOINT_RIGHT.setPower(ARM_JOINT_POWER);

    // EXTENDER.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    // EXTENDER.setTargetPosition(EXTENDER_MIN_POS);
    // EXTENDER.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    // EXTENDER.setPower(EXTENDER_POWER);

    // WRIST.setPosition(0);

    // One time compute
    telemetry.addData("Status", "Initialized");
    telemetry.update();

    int cameraMonitorViewId = hardwareMap.appContext
      .getResources()
      .getIdentifier(
        "cameraMonitorViewId",
        "id",
        hardwareMap.appContext.getPackageName()
      );
    webcam =
      OpenCvCameraFactory
        .getInstance()
        .createWebcam(
          hardwareMap.get(WebcamName.class, "webcam"),
          cameraMonitorViewId
        );

    webcam.openCameraDevice();
    webcam.setPipeline(pipeline);
    webcam.startStreaming(432, 240, OpenCvCameraRotation.UPRIGHT);

    telemetry.addData("Analysis", pipeline.getAnalysis());
    telemetry.update();

    // Don't burn CPU cycles busy-looping in this sample
    sleep(50);

    waitForStart();

    runtime.reset();

    while (opModeIsActive()) {
      telemetry.addData("Status", "Run Time: " + runtime.toString());
      telemetry.addData("Middle", pipeline.getAnalysis()[0]);
      telemetry.addData("Left", pipeline.getAnalysis()[1]);
      telemetry.addData("Right", pipeline.getAnalysis()[2]);

      telemetry.update();
    }
  }
}