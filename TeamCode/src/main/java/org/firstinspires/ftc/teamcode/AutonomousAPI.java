package org.firstinspires.ftc.teamcode;

import android.os.SystemClock;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;

public class AutonomousAPI {

  OpenCvWebcam webcam;

  CameraAPI cameraAPI = new CameraAPI();

  CameraAPI.LOCATION camResult;

  public void init(HardwareMap hardwareMap) {
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
    webcam.setPipeline(cameraAPI);
    webcam.startStreaming(432, 240, OpenCvCameraRotation.UPRIGHT);

    // don't burn CPU cycles busy-looping in this sample
    SystemClock.sleep(1000);

    camResult = cameraAPI.getAnalysis();
  }
}
