package org.firstinspires.ftc.teamcode.core;

import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.opencv.engine.OpenCVEngineInterface;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;

public class Auto {

  OpenCvWebcam webcam;
  Drive drive;
  Telemetry telemetry;
  HardwareMap hardwareMap;

  final InitialTeamScoreChecker initialTeamScoreChecker = new InitialTeamScoreChecker();
  final TeamScoreAlignmentChecker teamScoreAlignmentChecker = new TeamScoreAlignmentChecker();

  public InitialTeamScoreChecker.LOCATION teamscoreLocation;
  public TeamScoreAlignmentChecker.LOCATION teamscoreAlignment;

  private final double TOO_FAR_SPEED = 0.3;
  private final double FAR_SPEED = 0.2;

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
    teamscoreLocation = initialTeamScoreChecker.getAnalysis();
    telemetry.addData("Position", teamscoreLocation);
    telemetry.update();
  }

  public void moveArmToCorrectPosition (boolean switchPipelineToAligner) {
    if (teamscoreLocation == InitialTeamScoreChecker.LOCATION.RIGHT) {
      drive.setState(Drive.ARM_STATE.TOP);
    } else if (teamscoreLocation == InitialTeamScoreChecker.LOCATION.MIDDLE) {
      drive.setState(Drive.ARM_STATE.MIDDLE);
    } else {
      drive.setState(Drive.ARM_STATE.BOTTOM);
    }

    if (switchPipelineToAligner) {
      webcam.setPipeline(teamScoreAlignmentChecker);
    }
  }
  public void moveArmToCorrectPosition () {
    moveArmToCorrectPosition(true);
  }

  public void startAlignment() {
    while (teamscoreAlignment != TeamScoreAlignmentChecker.LOCATION.MIDDLE) {
      teamscoreAlignment = teamScoreAlignmentChecker.getAnalysis();

      if (teamscoreAlignment == TeamScoreAlignmentChecker.LOCATION.TOO_LEFT) {
        drive.moveX = -TOO_FAR_SPEED;
      } else if (teamscoreAlignment == TeamScoreAlignmentChecker.LOCATION.TOO_RIGHT) {
        drive.moveX = TOO_FAR_SPEED;
      } else if (teamscoreAlignment == TeamScoreAlignmentChecker.LOCATION.LEFT) {
        drive.moveX = -FAR_SPEED;
      } else if (teamscoreAlignment == TeamScoreAlignmentChecker.LOCATION.RIGHT) {
        drive.moveX = FAR_SPEED;
      }

      drive.apply();
    }

    drive.moveX = 0;
    drive.apply();
  }
}
