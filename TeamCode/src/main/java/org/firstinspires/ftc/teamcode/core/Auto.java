package org.firstinspires.ftc.teamcode.core;

import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;

public class Auto {

  OpenCvWebcam webcam;
  final Cam cam = new Cam();
  final Drive drive = new Drive();

  public Cam.LOCATION camResult;

  public void init(HardwareMap hardwareMap) {
    drive.init(hardwareMap);

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
  }

  public void recordTeamScorePos() {
    camResult = cam.getAnalysis();
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
