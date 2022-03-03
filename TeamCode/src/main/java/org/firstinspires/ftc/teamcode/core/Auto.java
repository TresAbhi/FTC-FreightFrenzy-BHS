package org.firstinspires.ftc.teamcode.core;

import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;

public class Auto {

  public enum SIDE {
    BLUE,
    RED
  }

  OpenCvWebcam webcam;
  Drive drive;
  Telemetry telemetry;
  HardwareMap hardwareMap;
  SIDE side;

  final InitialTeamScoreChecker initialTeamScoreChecker = new InitialTeamScoreChecker();
  final TeamScoreAlignmentChecker teamScoreAlignmentChecker = new TeamScoreAlignmentChecker();

  public InitialTeamScoreChecker.LOCATION teamscoreLocation = InitialTeamScoreChecker.LOCATION.UNKNOWN;
  public TeamScoreAlignmentChecker.LOCATION teamscoreAlignment = TeamScoreAlignmentChecker.LOCATION.UNKNOWN;

  private final double TOO_FAR_SPEED = 0.3;
  private final double FAR_SPEED = 0.25;

  public void init(HardwareMap hardwareMap, Telemetry telemetry, Drive drive, SIDE side) {
    // get access to runtime APIs
    this.hardwareMap = hardwareMap;
    this.telemetry = telemetry;
    this.drive = drive;
    this.side = side;

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

  public int getToAlignmentPositionTime () {
    if (side == SIDE.BLUE) {
      if (teamscoreLocation == InitialTeamScoreChecker.LOCATION.RIGHT) {
        return 0;
      } else if (teamscoreAlignment == TeamScoreAlignmentChecker.LOCATION.MIDDLE) {
        return 900;
      } else {
        return 0;
      }
    } else {
      return 0;
    }
  }
  public void alignToTeamscore() {
//    while (teamscoreAlignment != TeamScoreAlignmentChecker.LOCATION.MIDDLE) {
//      teamscoreAlignment = teamScoreAlignmentChecker.getAnalysis();
//
//      if (teamscoreAlignment == TeamScoreAlignmentChecker.LOCATION.TOO_LEFT) {
//        drive.moveX = -TOO_FAR_SPEED;
//      } else if (teamscoreAlignment == TeamScoreAlignmentChecker.LOCATION.TOO_RIGHT) {
//        drive.moveX = TOO_FAR_SPEED;
//      } else if (teamscoreAlignment == TeamScoreAlignmentChecker.LOCATION.LEFT) {
//        drive.moveX = -FAR_SPEED;
//      } else if (teamscoreAlignment == TeamScoreAlignmentChecker.LOCATION.RIGHT) {
//        drive.moveX = FAR_SPEED;
//      }
//
//      drive.apply();
//    }
//
//    drive.moveX = 0;
//    drive.apply();
//  }
    while (teamscoreAlignment != TeamScoreAlignmentChecker.LOCATION.MIDDLE) {
      teamscoreAlignment = teamScoreAlignmentChecker.getAnalysis();


    }
  }
}
