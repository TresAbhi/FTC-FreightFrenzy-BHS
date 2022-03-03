package org.firstinspires.ftc.teamcode.core;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

public class TeamScoreAlignmentChecker extends OpenCvPipeline {

  // ROI: Region of intrest
  static final int SLIM_ROI_WIDTH = 14;
  static final int SLIM_ROI_HEIGHT = 125;
  static final int THICK_ROI_WIDTH = 150;

  // The minimum and maximum colors that we will accept
  final Scalar LOW_ACCEPTANCE_COLOR = new Scalar(20, 50, 70); // in HSV
  final Scalar HIGH_ACCEPTANCE_COLOR = new Scalar(35, 255, 255); // in HSV

  // colors for either the team score is there or not
  final Scalar trueColor = new Scalar(0, 255, 0); // in RGB
  final Scalar falseColor = new Scalar(255, 0, 0); // in RGB

  // enumerations: the only possible results
  public enum LOCATION {
    UNKNOWN,
    TOO_LEFT,
    TOO_RIGHT,
    LEFT,
    RIGHT,
    MIDDLE,
  }

  // the value that stores either LEFT, MIDDLE, RIGHT, or NONE
  public LOCATION location = LOCATION.UNKNOWN;

  //  static final int ORIGIN_X = 167;
  static final int ORIGIN_X = 175;
  static final int ORIGIN_Y = 100;

  static final Rect MIDDLE_ROI = new Rect(
    new Point(ORIGIN_X, ORIGIN_Y),
    new Point(ORIGIN_X + SLIM_ROI_WIDTH, ORIGIN_Y + SLIM_ROI_HEIGHT)
  );
  static final Rect RIGHT_ROI = new Rect(
    new Point(ORIGIN_X + SLIM_ROI_WIDTH, ORIGIN_Y),
    new Point(ORIGIN_X + SLIM_ROI_WIDTH * 2, ORIGIN_Y + SLIM_ROI_HEIGHT)
  );
  static final Rect LEFT_ROI = new Rect(
    new Point(ORIGIN_X, ORIGIN_Y),
    new Point(ORIGIN_X - SLIM_ROI_WIDTH, ORIGIN_Y + SLIM_ROI_HEIGHT)
  );
  static final Rect TOO_RIGHT_ROI = new Rect(
    new Point(ORIGIN_X + SLIM_ROI_WIDTH * 2, ORIGIN_Y),
    new Point(
      ORIGIN_X + SLIM_ROI_WIDTH * 2 + THICK_ROI_WIDTH,
      ORIGIN_Y + SLIM_ROI_HEIGHT
    )
  );
  static final Rect TOO_LEFT_ROI = new Rect(
    new Point(ORIGIN_X - SLIM_ROI_WIDTH, ORIGIN_Y),
    new Point(
      ORIGIN_X - SLIM_ROI_WIDTH - THICK_ROI_WIDTH,
      ORIGIN_Y + SLIM_ROI_HEIGHT
    )
  );

  /**
   * this percentage or above of the box will have to be covered with yellow
   * in order to be considered `true`
   */
  static final double TOO_FAR_COVERAGE = 0.2;
  static final double FAR_COVERAGE = 0.1;
  static final double MIDDLE_COVERAGE = 0.4;

  // the whole screen's region/matrix
  final Mat mat = new Mat();

  @Override
  public Mat processFrame(Mat input) {
    // converts to HSV colors (google it!)
    Imgproc.cvtColor(input, mat, Imgproc.COLOR_RGB2HSV);

    // checks for colors in range
    Core.inRange(mat, LOW_ACCEPTANCE_COLOR, HIGH_ACCEPTANCE_COLOR, mat);

    // "cut-outs" of the regions of intrests
    Mat middle = mat.submat(MIDDLE_ROI);
    Mat right = mat.submat(RIGHT_ROI);
    Mat left = mat.submat(LEFT_ROI);
    Mat tooRight = mat.submat(TOO_RIGHT_ROI);
    Mat tooLeft = mat.submat(TOO_LEFT_ROI);

    // calculate coverage for cutouts (from 0 to 1)
    double middleCoverage =
      Core.sumElems(middle).val[0] / MIDDLE_ROI.area() / 255;
    double rightCoverage =
      Core.sumElems(right).val[0] / MIDDLE_ROI.area() / 255;
    double leftCoverage = Core.sumElems(left).val[0] / MIDDLE_ROI.area() / 255;
    double tooRightCoverage =
      Core.sumElems(tooRight).val[0] / MIDDLE_ROI.area() / 255;
    double tooLeftCoverage =
      Core.sumElems(tooLeft).val[0] / MIDDLE_ROI.area() / 255;

    // release it back to the parent map
    middle.release();
    right.release();
    left.release();
    tooRight.release();
    tooLeft.release();

    // booleans to figure out which one is actually covered
    boolean isMiddleCovered = middleCoverage >= MIDDLE_COVERAGE;
    boolean isRightCovered = rightCoverage >= FAR_COVERAGE;
    boolean isLeftCovered = leftCoverage >= FAR_COVERAGE;
    boolean isTooRightCovered = tooRightCoverage >= TOO_FAR_COVERAGE;
    boolean isTooLeftCovered = tooLeftCoverage >= TOO_FAR_COVERAGE;

    if (isLeftCovered) {
      location = LOCATION.LEFT;
    } else if (isRightCovered) {
      location = LOCATION.RIGHT;
    } else if (isTooLeftCovered) {
      location = LOCATION.TOO_LEFT;
    } else if (isTooRightCovered) {
      location = LOCATION.TOO_RIGHT;
    } else if (isMiddleCovered) {
      location = LOCATION.MIDDLE;
    } else {
      location = LOCATION.UNKNOWN;
    }

    // convert back to RGB
    Imgproc.cvtColor(mat, mat, Imgproc.COLOR_GRAY2RGB);

    // draw three rectangles based on which one has the team score
    Imgproc.rectangle(
      mat,
      MIDDLE_ROI,
      isMiddleCovered ? trueColor : falseColor
    );
    Imgproc.rectangle(mat, RIGHT_ROI, isRightCovered ? trueColor : falseColor);
    Imgproc.rectangle(mat, LEFT_ROI, isLeftCovered ? trueColor : falseColor);
    Imgproc.rectangle(
      mat,
      TOO_LEFT_ROI,
      isTooLeftCovered ? trueColor : falseColor
    );
    Imgproc.rectangle(
      mat,
      TOO_RIGHT_ROI,
      isTooRightCovered ? trueColor : falseColor
    );

    // return mat to let the bot render it on screen
    return mat;
  }

  public LOCATION getAnalysis() {
    return location;
  }
}
