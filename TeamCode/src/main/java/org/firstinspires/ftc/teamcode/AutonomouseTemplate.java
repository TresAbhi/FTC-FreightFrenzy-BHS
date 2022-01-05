package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;

@Autonomous(name = "AutoBRDuck", group = "A")
// @Disabled
public class AutoBRDuck extends LinearOpMode {

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

    /**
     * move the arm to the level
     *
     * left = low
     * middle = middle
     * right = high
     */
    if (camResult == TeamScoreDetector.LOCATION.RIGHT) {
      driverControlAPI.setState(DriverControlAPI.STATE.HIGH);
    } else if (camResult == TeamScoreDetector.LOCATION.MIDDLE) {
      driverControlAPI.setState(DriverControlAPI.STATE.MIDDLE);
    } else {
      driverControlAPI.setState(DriverControlAPI.STATE.LOW);
    }
  }
}
