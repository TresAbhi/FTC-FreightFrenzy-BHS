package org.firstinspires.ftc.teamcode.core;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

public class InitialTeamScoreChecker extends OpenCvPipeline {

  // ROI: Region of intrest
  static final int ROI_WIDTH = 80;
  static final int ROI_HEIGHT = 80;

  // The minimum and maximum colors that we will accept
  final Scalar LOW_ACCEPTANCE_COLOR = new Scalar(20, 50, 70); // in HSV
  final Scalar HIGH_ACCEPTANCE_COLOR = new Scalar(35, 255, 255); // in HSV

  // colors for either the team score is there or not
  final Scalar TEAM_SCORE_VISUAL_COLOR = new Scalar(0, 255, 0); // in RGB
  final Scalar NO_TEAM_SCORE_VISUAL_COLOR = new Scalar(255, 0, 0); // in RGB

  // enumerations: the only possible results
  public enum LOCATION {
    LEFT,
    MIDDLE,
    RIGHT,
    UNKNOWN,
  }

  // the value that stores either LEFT, MIDDLE, RIGHT, or NONE
  public LOCATION location = LOCATION.UNKNOWN;

  static final Rect LEFT_ROI = new Rect( // Left region of intrest
    new Point(55, 110),
    new Point(55 + ROI_WIDTH, 110 + ROI_HEIGHT)
  );
  static final Rect MIDDLE_ROI = new Rect( // middle region of intrest
    new Point(155, 110),
    new Point(155 + ROI_WIDTH, 110 + ROI_HEIGHT)
  );
  static final Rect RIGHT_ROI = new Rect( // right region of intrest
    new Point(265, 110),
    new Point(265 + ROI_WIDTH, 110 + ROI_HEIGHT)
  );

  /**
   * this percentage or above of the box will have to be covered with yellow
   * in order to be considered `true`
   */
  static final double PERCENT_COLOR_COVERAGE_THRESHOLD = 0.1;

  // the whole screen's region/matrix
  final Mat mat = new Mat();

  @Override
  public Mat processFrame(Mat input) {
    // converts to HSV colors (google it!)
    Imgproc.cvtColor(input, mat, Imgproc.COLOR_RGB2HSV);

    // checks for colors in range
    Core.inRange(mat, LOW_ACCEPTANCE_COLOR, HIGH_ACCEPTANCE_COLOR, mat);

    // "cut-outs" of the regions of intrests
    Mat left = mat.submat(LEFT_ROI);
    Mat middle = mat.submat(MIDDLE_ROI);
    Mat right = mat.submat(RIGHT_ROI);

    // calculate coverage for cutouts (from 0 to 1)
    double leftCoverage = Core.sumElems(left).val[0] / LEFT_ROI.area() / 255;
    double middleCoverage =
      Core.sumElems(middle).val[0] / MIDDLE_ROI.area() / 255;
    double rightCoverage = Core.sumElems(right).val[0] / RIGHT_ROI.area() / 255;

    // release it back to the parent map
    left.release();
    middle.release();
    right.release();

    // booleans to figure out which one is actually covered
    boolean leftCovered = leftCoverage > PERCENT_COLOR_COVERAGE_THRESHOLD;
    boolean middleCovered = middleCoverage > PERCENT_COLOR_COVERAGE_THRESHOLD;
    boolean rightCovered = rightCoverage > PERCENT_COLOR_COVERAGE_THRESHOLD;

    /**
     * do stuff with what is actually covered; if two are properly covered, the
     * left-most is preferred
     */
    if (leftCovered) {
      location = LOCATION.LEFT;
    } else if (middleCovered) {
      location = LOCATION.MIDDLE;
    } else if (rightCovered) {
      location = LOCATION.RIGHT;
    } else {
      location = LOCATION.UNKNOWN;
    }

    // convert back to RGB
    Imgproc.cvtColor(mat, mat, Imgproc.COLOR_GRAY2RGB);

    // draw three rectangles based on which one has the team score
    Imgproc.rectangle(
      mat,
      LEFT_ROI,
      location == LOCATION.LEFT
        ? TEAM_SCORE_VISUAL_COLOR
        : NO_TEAM_SCORE_VISUAL_COLOR
    );
    Imgproc.rectangle(
      mat,
      MIDDLE_ROI,
      location == LOCATION.MIDDLE
        ? TEAM_SCORE_VISUAL_COLOR
        : NO_TEAM_SCORE_VISUAL_COLOR
    );
    Imgproc.rectangle(
      mat,
      RIGHT_ROI,
      location == LOCATION.RIGHT
        ? TEAM_SCORE_VISUAL_COLOR
        : NO_TEAM_SCORE_VISUAL_COLOR
    );

    // return mat to let the bot render it on screen
    return mat;
  }

  public LOCATION getAnalysis() {
    return location;
  }
}
