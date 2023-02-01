package frc.team3128.commands;

import edu.wpi.first.math.geometry.Pose2d;

public class CmdMoveScore extends CmdMove {

    public static int SELECTED_GRID = 0;
    private Pose2d[][] positions;

    public CmdMoveScore(Type type, boolean joystickOverride, Pose2d[]... positions) {
        super(type, joystickOverride, positions[0]);
        this.positions = positions;
    }

    @Override
    public void initialize() {
        var newPoses = new Pose2d[positions.length];
        for (int i = 0; i < positions.length; i++) {
            newPoses[i] = positions[i][SELECTED_GRID];
        }
        poses = newPoses;
        super.initialize();
    }
    
}