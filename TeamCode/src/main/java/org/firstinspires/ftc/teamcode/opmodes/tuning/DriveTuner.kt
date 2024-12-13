package org.firstinspires.ftc.teamcode.opmodes.tuning

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.common.Robot
import org.firstinspires.ftc.teamcode.common.subsystems.drive.Drive
import org.firstinspires.ftc.teamcode.common.utils.OpMode
import org.firstinspires.ftc.teamcode.common.utils.Subsystem

class DriveOnly(opmode: OpMode) : Robot(opmode) {
    override val drive: Drive by lazy { Drive(this) }
    override val subsystems: List<Subsystem> by lazy { listOf(drive) }
}

@TeleOp(name = "Drive Tuner")
class DriveTuner : OpMode() {
    override fun exec() {
        robot.drive.robotCentric(
            gamepad1.left_stick_y.toDouble(),
            -gamepad1.left_stick_x.toDouble(),
            -gamepad1.right_stick_x.toDouble()
        )

        val pose = robot.drive.pose
        val velocity = robot.drive.velocity
        robot.telemetry.addLine("Pose: ${pose.position}")
        robot.telemetry.addLine("Velocity: ${velocity.position}")
        robot.telemetry.addData("Status", robot.drive.odometry.pinpoint.deviceStatus);
        robot.telemetry.addData("Pinpoint Frequency", robot.drive.odometry.pinpoint.frequency);
        robot.telemetry.update()
    }
}