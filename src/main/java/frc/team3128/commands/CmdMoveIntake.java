package frc.team3128.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.team3128.subsystems.Intake;

public class CmdMoveIntake extends CommandBase{

    private double desiredAngle;
    private Intake intake = Intake.getInstance();

    public CmdMoveIntake(double angle) {
        desiredAngle = angle;
    }

    public CmdMoveIntake(Intake.IntakeState state) {
        desiredAngle = state.getAngleSetpoint();
        addRequirements(intake);
    }

    @Override
    public void initialize(){
        intake.startPID(desiredAngle);
    }

    @Override
    public void end(boolean interrupted){
        intake.stop();
    }

    @Override
    public boolean isFinished(){
        return intake.atSetpoint();
    }
}