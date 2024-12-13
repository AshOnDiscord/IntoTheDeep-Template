## File Structure
### Common
Commonly shared code, ex: Subsystems, Actions, Util, etc
#### Subsystems
Robot subsystems, organized by folders/modules
#### Commands
Subsystem commands, also organized by folders/modules.
DrivePIDCommand is a simple p2p PID.
### OpModes
All the opmodes for the robot, seperated by folders/modules.
### GoBilda
3rd party files from GoBilda (ie: Pinpoint Driver)

### PathPlanner
External library for util and pure pursuit (likely to be remade), also has a nice planner GUI

## System
[Subsystems](https://docs.ftclib.org/ftclib/command-base/command-system/subsystems). - Represents a subsystem or one
individual part of the robot or a group of other subsystems. Extend `utils.Subsystem` instead of `SubsystemBase`.
All subsystems receive a `Robot` object in their constructor that provides access to other subsystems and also the
hardware map.

[Commands](https://docs.ftclib.org/ftclib/command-base/command-system/command) - Represents a command or action that the robot can perform. No extra changes from the docs.

For OpModes, extend `utils.OpMode` instead of `CommandOpMode` as it'll handle creating and initializing the robot,
automatically calling `robot.telemetry.update`, and calling the Command base scheduler.

## Util
`Vec2d` - 2d vector class from PathPlanner
`Pose2d` - 2d pose class that uses Vec2d, heading is in degrees, use `Pose.fromRadians` if you want radians
`EdgeDetector` - Detects when a button is pressed (rising edge) or released (falling edge)
`DeltaTime` - Measures the time between cycles. Helpful to measuring loop time and creating time-linked code.