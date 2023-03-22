package frc.team3128.commands;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.StartEndCommand;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;
import frc.team3128.RobotContainer;
import frc.team3128.Constants.ArmConstants.ArmPosition;
import frc.team3128.common.swerve.SwerveModule;
import frc.team3128.common.utility.NAR_Shuffleboard;
import frc.team3128.subsystems.Intake;
import frc.team3128.subsystems.Manipulator;
import frc.team3128.subsystems.Pivot;
import frc.team3128.subsystems.Swerve;
import frc.team3128.subsystems.Telescope;
import frc.team3128.subsystems.Intake.IntakeState;

import static frc.team3128.Constants.ManipulatorConstants.*;

import java.util.Arrays;

import static frc.team3128.Constants.IntakeConstants.*;

public class CmdSystemCheckFancy extends CommandBase {
    public static double systemCheck = 0;
    public static boolean repeat = true;

    private Swerve swerve;
    private Telescope tele;
    private Pivot pivot;
    private Manipulator manip;
    private Intake intake;

    private double driveVelocity = 1;

    private static boolean swerveSystemCheck, armSystemCheck, manipulatorSystemCheck, intakeSystemCheck = false;

    public CmdSystemCheckFancy() {
        swerve = Swerve.getInstance();
        tele = Telescope.getInstance();
        pivot = Pivot.getInstance();
        manip = Manipulator.getInstance();
        intake = Intake.getInstance();
    }

    @Override
    public void initialize() {
        systemCheck = 0;
        repeat = true;
    }

    @Override
    public void execute() {
        if (!repeat) return;
        repeat = false;
        if (systemCheck == 1) {
            swerveSystemCheck = false;
            swerveCheck(driveVelocity);
            swerveSystemCheck = true;
        }
        else if (systemCheck == 2) {
            armSystemCheck = false;
            var arm1 = new CmdMoveArm(ArmPosition.NEUTRAL).withTimeout(3);
            arm1.schedule();
            while (arm1.isScheduled()) Timer.delay(0.1);
            var arm2 = new CmdMoveArm(ArmPosition.TOP_CONE.pivotAngle, 25).withTimeout(3);
            arm2.schedule();
            while (arm2.isScheduled()) Timer.delay(0.1);
            arm1.schedule();
            while (arm1.isScheduled()) Timer.delay(0.1);
            armSystemCheck = true;
        }
        else if (systemCheck == 3) {
            intakeSystemCheck = false;
            var cmdIntake = new CmdIntake();
            cmdIntake.schedule();
            while (cmdIntake.isScheduled()) Timer.delay(0.1);
            Timer.delay(1);
            CommandBase outtake = new StartEndCommand(()-> intake.outtake(), ()-> intake.stopRollers(), intake).withTimeout(1);
            outtake.schedule();
            while (outtake.isScheduled()) Timer.delay(0.1);
            intakeSystemCheck = true;
        }
        else if (systemCheck == 4) {
            manipulatorSystemCheck = false;
            var manipGrabCone = new CmdManipGrab(true);
            manipGrabCone.schedule();
            while (manipGrabCone.isScheduled()) Timer.delay(0.1);
            Timer.delay(1);
            CommandBase manipOuttake = new StartEndCommand(()-> manip.outtake(), ()-> manip.stopRoller(), manip).withTimeout(1);
            manipOuttake.schedule();
            while (manipOuttake.isScheduled()) Timer.delay(0.1);
            var manipGrabCube = new CmdManipGrab(false);
            manipGrabCube.schedule();
            while (manipGrabCube.isScheduled()) Timer.delay(0.1);
            Timer.delay(1);
            manipOuttake.schedule();
            while (manipOuttake.isScheduled()) Timer.delay(0.1);
            manipulatorSystemCheck = true;
        }
    }

    @Override
    public boolean isFinished() {
        return systemCheck > 4;
    }

    public void swerveCheck(double velocity) {
        for (int i = 0; i < 8; i++) {
            double angle = i * 45;
            SwerveModuleState desiredTestState = new SwerveModuleState(velocity * (angle > 180 ? -1 : 1),
                    Rotation2d.fromDegrees(angle));
            SwerveModuleState[] desiredTestStates = new SwerveModuleState[4];
            Arrays.fill(desiredTestStates, desiredTestState);
            swerve.setModuleStates(desiredTestStates);
            Timer.delay(0.2);
            System.out.println("Angle: " + i);
            for (SwerveModule module : swerve.modules) {
                System.out.println(
                        "Module " + module.moduleNumber + ": " + swerve.compare(module.getState(), desiredTestState));
            }
        }
        swerveSystemCheck = true;
    }

    public static void initShuffleboard() {
        NAR_Shuffleboard.addData("System Check", "Swerve", () -> swerveSystemCheck, 0, 0);
        NAR_Shuffleboard.addData("System Check", "Arm", () -> armSystemCheck, 0, 1);
        NAR_Shuffleboard.addData("System Check", "Intake", () -> intakeSystemCheck, 0, 2);
        NAR_Shuffleboard.addData("System Check", "Manipulator", () -> manipulatorSystemCheck, 0, 3);
    }
}
