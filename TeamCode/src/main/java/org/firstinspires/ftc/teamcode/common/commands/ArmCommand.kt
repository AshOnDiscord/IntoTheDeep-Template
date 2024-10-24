package org.firstinspires.ftc.teamcode.common.commands

import com.arcrobotics.ftclib.command.CommandBase
import org.firstinspires.ftc.teamcode.common.subsystems.Arm
import kotlin.math.abs

class ArmCommand(val arm: Arm, val target: Int, val threshold: Int = 20) : CommandBase() {
    init {
        addRequirements(arm)
    }

    override fun initialize() {
        arm.target = target
    }

    // no execute needed since arm will already manage running

    override fun isFinished(): Boolean {
        val diff = abs(arm.position - target)
        return diff < threshold
    }
}