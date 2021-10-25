/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;


/**
 * This file contains an minimal example of a Linear "OpMode". An OpMode is a 'program' that runs in either
 * the autonomous or the teleop period of an FTC match. The names of OpModes appear on the menu
 * of the FTC Driver Station. When an selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 *
 * This particular OpMode just executes a basic Tank Drive Teleop for a two wheeled robot
 * It includes all the skeletal structure that all linear OpModes contain.
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@TeleOp(name="DriverControl_original", group="Linear Opmode")
@Disabled
public class DriverControl_original extends LinearOpMode {

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftFront = null;       //left front wheel
    private DcMotor leftRear = null;        //left rear wheel
    private DcMotor rightFront = null;      //right front wheel
    private DcMotor rightRear = null;       //right rear wheel
    private DcMotor conveyor1 = null;
    private DcMotor conveyor2 = null;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        leftFront  = hardwareMap.get(DcMotor.class, "left_front");
        leftRear   = hardwareMap.get(DcMotor.class, "left_rear");
        rightFront = hardwareMap.get(DcMotor.class, "right_front");
        rightRear  = hardwareMap.get(DcMotor.class, "right_rear");
        conveyor1  = hardwareMap.get(DcMotor.class, "conveyor_1");
        conveyor2  = hardwareMap.get(DcMotor.class, "conveyor_2");


        // Most robots need the motor on one side to be reversed to drive forward
        // Reverse the motor that runs backwards when connected directly to the battery
        //leftFront.setDirection(DcMotor.Direction.REVERSE);          // Need to check for reverse
        //leftRear.setDirection(DcMotor.Direction.REVERSE);
        //rightFront.setDirection(DcMotor.Direction.REVERSE);
        rightRear.setDirection(DcMotor.Direction.REVERSE);

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            double r = Math.hypot(gamepad1.left_stick_y, gamepad1.left_stick_x); // final vector based on the offsets on both x and y axis (using pythogorus theromum)
            double robotAngle = Math.atan2(gamepad1.left_stick_y, - gamepad1.left_stick_x) - Math.PI / 4;
            double v1 = r * Math.cos(robotAngle); // y part of the vector
            double v2 = r * Math.sin(robotAngle); // x part of the vector
            double v3 = r * Math.sin(robotAngle); // x part of the vector
            double v4 = r * Math.cos(robotAngle); // y part of the vector

            /*
            v1  = Range.clip(v1, -1.0, 1.0) ;
            v2  = Range.clip(v2, -1.0, -1.0) ;
            v3  = Range.clip(v3, 1.0, 1.0) ;
            v4  = Range.clip(v4, 1.0, -1.0) ;
            */

            double speedControl = 1.5;          // drives at half speed when left bumper is pressed
            if (gamepad1.left_bumper)
            {
                speedControl = 2.5;
            }
            else
            {
                speedControl = 1.5;
            }
            leftFront.setPower((-v1 + gamepad1.right_stick_x) / speedControl);
            leftRear.setPower((-v2 + gamepad1.right_stick_x) / speedControl);
            rightFront.setPower((-v3 - gamepad1.right_stick_x) / speedControl);
            rightRear.setPower((-v4 - gamepad1.right_stick_x) / speedControl);

            /* OLD CODE
            leftFront.setPower((-v1 + gamepad1.right_stick_x) / speedControl);      // maybe flip the signs if it doesn't work
            leftRear.setPower((-v2 + gamepad1.right_stick_x) / speedControl);       // v1 - gamepad1 to -v1 + gamepad1
            rightFront.setPower((-v3 - gamepad1.right_stick_x) / speedControl);
            rightRear.setPower((-v4 - gamepad1.right_stick_x) / speedControl);
    */
            if(gamepad2.right_bumper) {
                conveyor1.setPower(1);
                conveyor2.setPower(-1);
            }
            else
            {
                conveyor1.setPower(0);
                conveyor2.setPower(0);
            }
            if(gamepad2.left_bumper)
            {
                conveyor1.setPower(-1);
                conveyor2.setPower(1);
            }
            else
            {
                conveyor1.setPower(0);
                conveyor2.setPower(0);
            }

            // conveyor1.setPower(gamepad2.right_bumper ? 1 : 0);     Look into this code

            double drive = -gamepad1.left_stick_y;
            double turn = gamepad1.right_stick_x;


            // Show the elapsed game time and wheel power.
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("LF Power", leftFront.getPower());
            telemetry.addData("LR Power", leftRear.getPower());
            telemetry.addData("RF Power", rightFront.getPower());
            telemetry.addData("RR Power", rightRear.getPower());
            telemetry.addData("Left Joystick X", gamepad1.left_stick_x);
            telemetry.addData("Left Joystick Y", gamepad1.left_stick_y);
            telemetry.addData("Right Joystick X", gamepad1.right_stick_x);
            telemetry.addData("Right Joystick Y", gamepad1.right_stick_y);
            telemetry.update();
        }
    }
}
