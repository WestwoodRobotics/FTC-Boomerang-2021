

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;



@TeleOp(name="BoomerangTeleOp")
public class TeleOpFirstComp extends OpMode
{
    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftFrontDrive = null;
    private DcMotor rightFrontDrive = null;
    private DcMotor leftBackDrive = null;
    private DcMotor rightBackDrive = null;
    private DcMotor carousel = null;
    private DcMotor intake = null;

    private double leftFrontPower;
    private double rightFrontPower;
    private double leftBackPower;
    private double rightBackPower;
    private double carouselPower;
    boolean dpadPushed = false;
    boolean speedMode = false;
    private double intakePower;

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        telemetry.addData("Status", "Initialized");

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        leftFrontDrive  = hardwareMap.get(DcMotor.class, "left_Front_drive");
        rightFrontDrive = hardwareMap.get(DcMotor.class, "right_Front_drive");
        leftBackDrive = hardwareMap.get(DcMotor.class, "left_Back_drive" );
        rightBackDrive = hardwareMap.get(DcMotor.class, "right_Back_drive");
        carousel = hardwareMap.get(DcMotor.class, "carousel");
        intake = hardwareMap.get(DcMotor.class, "intake");

        // Most robots need the motor on one side to be reversed to drive forward
        // Reverse the motor that runs when connected directly to the battery
        leftFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        rightFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        leftBackDrive.setDirection(DcMotor.Direction.FORWARD);
        rightBackDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        carousel.setDirection(DcMotorSimple.Direction.FORWARD);
        intake.setDirection(DcMotorSimple.Direction.FORWARD);

        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        carousel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        // Tell the driver that initialization is complete.
        telemetry.addData("Status", "Initialized");

    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {
    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
        runtime.reset();
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        // Setup a variable for each drive wheel to save power level for telemetry


        // Choose to drive using either Tank Mode, or POV Mode
        // Comment out the method that's not used.  The default below is POV.

        // POV Mode uses left stick to go forward, and right stick to turn.
        // - This uses basic math to combine motions and is easier to drive straight.

        double forward = (-gamepad1.left_stick_y);
        double strafe = gamepad1.left_stick_x;
        // Mulitiplied by .75 to get a slower turn
        double turn = (gamepad1.right_stick_x);

        leftFrontPower = (forward + strafe + turn);
        rightFrontPower = (forward - strafe - turn);
        leftBackPower = (forward - strafe + turn);
        rightBackPower = (forward + strafe - turn);
        double maxDrivetrainPower = Math.abs(Math.max(Math.max(Math.max(leftFrontPower, rightFrontPower), leftBackPower), rightBackPower));

        carouselPower = 0;

        if(gamepad1.dpad_right){
            carouselPower = 1;
        }
        else if(gamepad1.dpad_left){
            carouselPower = -1;
        }
        else{
            carouselPower = 0;
        }

        intakePower = 0;

        if(gamepad2.dpad_up){
            intakePower = 1;
        }
        else intakePower = 0;

        // Tank Mode uses one stick to control each wheel.
        // - This requires no math, but it is hard to drive forward slowly and keep straight.
        // leftPower  = -gamepad1.left_stick_y ;
        // rightPower = -gamepad1.right_stick_y ;

        // Send calculated power to wheels

        if(!dpadPushed && gamepad1.dpad_up)
        {
            speedMode = !speedMode;
            dpadPushed = !dpadPushed;
        }
        else if (!gamepad1.dpad_up){
            dpadPushed = !dpadPushed;
        }
        leftFrontPower /= maxDrivetrainPower;
        rightFrontPower /= maxDrivetrainPower;
        leftBackPower /= maxDrivetrainPower;
        rightBackPower /= maxDrivetrainPower;
        // Set motors
        if(speedMode){
        }
        else {
            leftFrontPower *= 0.7;
            rightFrontPower *= 0.7;
            leftBackPower *= 0.7 ;
            rightBackPower *= 0.7;
        }
        leftFrontDrive.setPower(leftFrontPower);
        rightFrontDrive.setPower(rightFrontPower);
        leftBackDrive.setPower(leftBackPower);
        rightBackDrive.setPower(rightBackPower);



        carousel.setPower(carouselPower);
        intake.setPower(intakePower);

        // Show the elapsed game time and wheel power.
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addData("Motors", "leftFront (%.2f), rightFront (%.2f) leftBack (%.2f), rightBack (%.2f)", leftFrontPower, rightFrontPower, leftBackPower, rightBackPower);
        telemetry.addData("Controller", "Forward (%.2f), Strafe (%.2f) Turn (%.2f)", forward, strafe, turn);
        telemetry.addData("Carousel", "Carousel: " + carouselPower);
        telemetry.addData("Intake", "Intake", +intakePower);
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
    }

}
