package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;

@Autonomous(name = "AutoRedLeftDuck", group = "A")
// @Disabled
public class AutoRedLeftDuck extends LinearOpMode {

  private ElapsedTime runtime = new ElapsedTime();

  OpenCvWebcam webcam;
  TeamScoreDetector teamScoreDetector = new TeamScoreDetector(telemetry);
  DriverControlAPI driverControlAPI = new DriverControlAPI();

  // @Override
  public void runOpMode() {
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
    webcam.setPipeline(teamScoreDetector);
    webcam.startStreaming(432, 240, OpenCvCameraRotation.UPRIGHT);

    driverControlAPI.init(hardwareMap);

    telemetry.update();

    // don't burn CPU cycles busy-looping in this sample
    sleep(1000);

    TeamScoreDetector.LOCATION camResult = teamScoreDetector.getAnalysis();

    telemetry.addData("Status", "Initialized");
    telemetry.addData("Location", camResult);
    telemetry.update();

    waitForStart();

    runtime.reset();

    // move a bit up
    driverControlAPI.moveY = -1;
    driverControlAPI.apply();
    sleep(120);

    // stop moving and rotate 90
    driverControlAPI.moveY = 0;
    driverControlAPI.rotX = 1;
    driverControlAPI.apply();
    sleep(420);

    // stop rotating, move back, swing the spinner out, and start the spinner
    driverControlAPI.moveY = 1;
    driverControlAPI.spinnerJointSpeed = 1;
    driverControlAPI.spinnerSpeed = 1;
    driverControlAPI.rotX = 0;
    driverControlAPI.apply();
    sleep(550);

    // stop moving and wait for ducks to fall
    driverControlAPI.moveY = 0;
    driverControlAPI.apply();
    sleep(8500);

    // move to the left and bring back in the spinner
    driverControlAPI.moveX = -1;
    driverControlAPI.spinnerJointSpeed = 0;
    driverControlAPI.spinnerSpeed = 0.5f;
    driverControlAPI.apply();
    sleep(960);

    // stop moving, move the arm to correct position, and stop the spinner joint
    driverControlAPI.moveX = 0;
    driverControlAPI.spinnerJointSpeed = 0;
    if (camResult == TeamScoreDetector.LOCATION.RIGHT) {
      driverControlAPI.setState(DriverControlAPI.STATE.HIGH);
    } else if (camResult == TeamScoreDetector.LOCATION.MIDDLE) {
      driverControlAPI.setState(DriverControlAPI.STATE.MIDDLE);
    } else {
      driverControlAPI.setState(DriverControlAPI.STATE.LOW);
    }
    sleep(2000);

    // move forward
    driverControlAPI.moveY = -0.4f;
    driverControlAPI.apply();
    sleep(1200);

    // stop and drop the box
    driverControlAPI.moveY = 0;
    driverControlAPI.clawTargetState = 0;
    driverControlAPI.apply();
    sleep(200);

    // go back
    driverControlAPI.moveY = 1;
    driverControlAPI.clawTargetState = 1;
    driverControlAPI.apply();
    sleep(450);

    // stop moving back and move right
    driverControlAPI.moveY = 0;
    driverControlAPI.moveX = 1;
    driverControlAPI.setState(DriverControlAPI.STATE.BACK);
    sleep(500);

    // stop moving
    driverControlAPI.moveX = 0;
    driverControlAPI.apply();
    sleep(200);

    sleep(100000);
  }
}
