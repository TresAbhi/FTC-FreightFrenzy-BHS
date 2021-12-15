package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;

@Autonomous(name = "AutoBlueRightDuck", group = "A")
// @Disabled
public class AutoBlueRightDuck extends LinearOpMode {

  private ElapsedTime runtime = new ElapsedTime();

  OpenCvWebcam webcam;
  TeamScoreDetector teamScoreDetector = new TeamScoreDetector(telemetry);
  DriverControlAPI driverControlAPI = new DriverControlAPI(hardwareMap);

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
    sleep(200);

    TeamScoreDetector.LOCATION camResult = teamScoreDetector.getAnalysis();

    telemetry.addData("Status", "Initialized");
    telemetry.addData("Location", camResult);
    telemetry.update();

    waitForStart();

    runtime.reset();

    // Move right and a bit up
    driverControlAPI.spinnerSpeed = 1;
    driverControlAPI.moveX = 1;
    driverControlAPI.moveY = -0.2;
    driverControlAPI.apply();
    sleep(810);

    // stop moving
    driverControlAPI.moveX = 0;
    driverControlAPI.moveY = 0;
    driverControlAPI.apply();
    sleep(8500);

    // wait for ducks to fall off and go back to original position
    driverControlAPI.spinnerSpeed = 0.49f;
    driverControlAPI.moveX = -1;
    driverControlAPI.moveY = 0;
    driverControlAPI.apply();
    sleep(1300);

    // stop moving to the left
    driverControlAPI.moveX = 0;
    driverControlAPI.apply();

    /**
     * move the arm to the level
     *
     * left = low
     * middle = middle
     * right = high
     */
    if (camResult == TeamScoreDetector.LOCATION.LEFT) {
      driverControlAPI.setState(DriverControlAPI.STATE.LOW);
    } else if (camResult == TeamScoreDetector.LOCATION.MIDDLE) {
      driverControlAPI.setState(DriverControlAPI.STATE.MIDDLE);
    } else {
      // right or none... hope it's right it if doesn't figure it out
      driverControlAPI.setState(DriverControlAPI.STATE.HIGH);
    }
    sleep(2000);

    // move forward
    driverControlAPI.moveY = -0.2;
    driverControlAPI.apply();
    sleep(500);

    // stop moving and let go of the block
    driverControlAPI.moveY = 0;
    driverControlAPI.clawTargetState = 0;
    driverControlAPI.apply();

    /*

    driverControlAPI.moveX = 0;
    driverControlAPI.moveY = 0;
    driverControlAPI.clawTargetState = 0;
    driverControlAPI.apply();
    */

    sleep(1000000);
  }
}
