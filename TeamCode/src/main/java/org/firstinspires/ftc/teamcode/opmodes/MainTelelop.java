package org.firstinspires.ftc.teamcode.opmodes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.command.CommandOpMode;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.common.subsystems.DriveSubsystem;
import org.firstinspires.ftc.teamcode.common.utils.Telemetry;

@Config
@TeleOp(name = "MainTeleOp")
public class MainTelelop extends CommandOpMode {
    private DriveSubsystem drive;
    Telemetry tel;
    GamepadEx gamepad;
    Pose2d pos;
    FtcDashboard dash;

    @Override
    public void initialize() {
        drive = new DriveSubsystem(hardwareMap, -1);
//        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        tel = new Telemetry();
        dash = FtcDashboard.getInstance();
    }


    @Override
    public void run() {
        super.run();

        double power = -gamepad1.left_stick_y;
        double strafe = gamepad1.left_stick_x * 1.1;
        double turn = gamepad1.right_stick_x;

        drive.robotCentric(power, strafe, turn);
        drive.updatePos();

        pos = drive.getPos();

        telemetry.addData("left", drive.leftOdom.getPosition());
        telemetry.addData("center", drive.centerOdom.getPosition());
        telemetry.addData("right", drive.rightOdom.getPosition());
        tel.drawField(pos, dash);
        telemetry.update();
    }
}
