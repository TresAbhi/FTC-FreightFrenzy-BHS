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
    webcam.setPipeline(teamScoreDetector);
    webcam.startStreaming(432, 240, OpenCvCameraRotation.UPRIGHT);

    driverControlAPI.init(hardwareMap);

    telemetry.update();

    // don't burn CPU cycles busy-looping in this sample
    sleep(50);

    waitForStart();

    runtime.reset();

    // Move to carousel
    driverControlAPI.moveX = 1;
    driverControlAPI.apply();

    sleep(500);

    driverControlAPI.moveX = 0.2;
    driverControlAPI.moveY = -0.1;
    driverControlAPI.apply();

    sleep(1000);

    // Start spinning
    driverControlAPI.spinnerSpeed = 1;
    driverControlAPI.apply();

    sleep(500);

    driverControlAPI.moveY = 0;
    driverControlAPI.apply();

    sleep(120);

    driverControlAPI.moveX = -0.05;
    driverControlAPI.moveY = 0;
    driverControlAPI.apply();

    sleep(100);

    driverControlAPI.moveX = 0;
    driverControlAPI.apply();

    sleep(8000);

    driverControlAPI.spinnerSpeed = 0.5f;

    // Move back left
    driverControlAPI.moveX = -1;
    driverControlAPI.apply();

    sleep(800);

    driverControlAPI.moveX = 0;
    driverControlAPI.apply();

    TeamScoreDetector.LOCATION camResult = teamScoreDetector.getAnalysis();

    /**
     * left = low
     * middle = middle
     * right = high
     */
    if (camResult == TeamScoreDetector.LOCATION.LEFT) {
      // bottom
    }
    // while (opModeIsActive()) {
    //   telemetry.addData("Status", "Run Time: " + runtime.toString());
    //   telemetry.addData("Location", TeamScoreDetector.location);
    //   telemetry.update();
    // }
  }
}
