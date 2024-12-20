package org.firstinspires.ftc.teamcode.common.subsystems.drive

import com.acmerobotics.dashboard.config.Config
import com.millburnx.utils.Vec2d
import com.qualcomm.robotcore.hardware.DcMotorEx
import org.firstinspires.ftc.teamcode.common.Robot
import org.firstinspires.ftc.teamcode.common.utils.Pose2d
import org.firstinspires.ftc.teamcode.common.utils.Subsystem
import org.firstinspires.ftc.teamcode.common.utils.init
import kotlin.math.absoluteValue
import kotlin.math.max

@Config
open class Drive(val robot: Robot) : Subsystem() {
    val frontLeft: DcMotorEx = (robot.hardware["frontLeft"] as DcMotorEx).apply { init() }
    val frontRight: DcMotorEx = (robot.hardware["frontRight"] as DcMotorEx).apply { init(false) }
    val backLeft: DcMotorEx = (robot.hardware["backLeft"] as DcMotorEx).apply { init() }
    val backRight: DcMotorEx = (robot.hardware["backRight"] as DcMotorEx).apply { init(false) }
    val motors = listOf<DcMotorEx>(frontLeft, frontRight, backLeft, backRight)
    val odometry = Pinpoint(robot)
    val pose: Pose2d = odometry.pose
    val velocity: Pose2d = odometry.velocity

    fun robotCentric(forward: Double, strafe: Double, rotate: Double) = fieldCentric(forward, strafe, rotate, 0.0)

    open fun fieldCentric(x: Double, y: Double, rotate: Double, heading: Double) {
        val relativeVector = Vec2d(x, y).rotate(-heading) * Vec2d(1.0, strafeMultiplier)

        val forward = relativeVector.x
        val strafe = relativeVector.y

        val denominator = max(forward.absoluteValue + strafe.absoluteValue + rotate.absoluteValue, 1.0)
        frontLeft.power = (forward + strafe + rotate) / denominator
        backLeft.power = (forward - strafe + rotate) / denominator
        frontRight.power = (forward - strafe - rotate) / denominator
        backRight.power = (forward + strafe - rotate) / denominator
    }

    companion object {
        @JvmField
        var strafeMultiplier: Double = 1.1
    }
}