package org.firstinspires.ftc.teamcode.core;

import android.os.SystemClock;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;

public class AutonomousAPI {

  OpenCvWebcam webcam;
  CameraPipeline cameraPipeline = new CameraPipeline();
  DriverControlAPI drive = new DriverControlAPI();

  public CameraPipeline.LOCATION camResult;

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
    webcam.setPipeline(cameraPipeline);
    webcam.startStreaming(432, 240, OpenCvCameraRotation.UPRIGHT);
  }

  public void recordTeamScorePos() {
    camResult = cameraPipeline.getAnalysis();
  }

  public void moveArmToCorrectPosition() {
    if (camResult == CameraPipeline.LOCATION.RIGHT) {
      drive.setState(DriverControlAPI.STATE.HIGH);
    } else if (camResult == CameraPipeline.LOCATION.MIDDLE) {
      drive.setState(DriverControlAPI.STATE.MIDDLE);
    } else {
      drive.setState(DriverControlAPI.STATE.LOW);
    }
  }
}
