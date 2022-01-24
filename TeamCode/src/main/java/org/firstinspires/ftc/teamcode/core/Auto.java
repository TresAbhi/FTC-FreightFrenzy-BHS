package org.firstinspires.ftc.teamcode.core;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;

public class Auto {

  OpenCvWebcam webcam;
  final Cam cam = new Cam();
  Drive drive;

  public Cam.LOCATION camResult;

  Telemetry telemetry;

  public void init(HardwareMap hardwareMap, Telemetry tl, Drive dr) {
    drive = dr;

    drive.compensateForVoltage(5);

    telemetry = tl;

    telemetry.addData("Cam", "Starting");
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
    webcam.setPipeline(cam);
    webcam.startStreaming(432, 240, OpenCvCameraRotation.UPRIGHT);

    telemetry.addData("Cam", "Initialized");
    telemetry.update();
  }

  public void recordTeamScorePos() {
    camResult = cam.getAnalysis();
    telemetry.addData("Position", camResult);
    telemetry.update();
  }

  public void moveArmToCorrectPosition() {
    if (camResult == Cam.LOCATION.RIGHT) {
      drive.setState(Drive.ARM_STATE.HIGH);
    } else if (camResult == Cam.LOCATION.MIDDLE) {
      drive.setState(Drive.ARM_STATE.MIDDLE);
    } else {
      drive.setState(Drive.ARM_STATE.LOW);
    }
  }
}
