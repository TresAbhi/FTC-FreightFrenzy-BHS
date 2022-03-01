package org.firstinspires.ftc.teamcode.core;

import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;

public class Auto {

  OpenCvWebcam webcam;
  Drive drive;
  Telemetry telemetry;
  HardwareMap hardwareMap;

  final InitialTeamScoreChecker initialTeamScoreChecker = new InitialTeamScoreChecker();

  public InitialTeamScoreChecker.LOCATION camResult;

  public void init(HardwareMap hm, Telemetry tl, Drive dr) {
    // get access to runtime APIs
    hardwareMap = hm;
    telemetry = tl;
    drive = dr;

    telemetry.addData("Compensation Voltage", "calculating...");
    telemetry.addData("Cam", "starting...");
    telemetry.update();

    telemetry.addData("Compensation Voltage", drive.compensateForVoltage(10));

    // start the cam
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
    webcam.setPipeline(initialTeamScoreChecker);
    webcam.startStreaming(432, 240, OpenCvCameraRotation.UPRIGHT);

    // at this point, it's ready
    telemetry.addData("Cam", "Initialized");
    telemetry.addData("Position", "waiting...");
    telemetry.update();
  }

  public void recordTeamScorePos() {
    camResult = initialTeamScoreChecker.getAnalysis();
    telemetry.addData("Position", camResult);
    telemetry.update();
  }

  public void moveArmToCorrectPosition() {
    if (camResult == InitialTeamScoreChecker.LOCATION.RIGHT) {
      drive.setState(Drive.ARM_STATE.TOP);
    } else if (camResult == InitialTeamScoreChecker.LOCATION.MIDDLE) {
      drive.setState(Drive.ARM_STATE.MIDDLE);
    } else {
      drive.setState(Drive.ARM_STATE.BOTTOM);
    }
  }
}
