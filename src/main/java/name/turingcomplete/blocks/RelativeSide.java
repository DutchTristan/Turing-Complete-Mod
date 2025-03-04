package name.turingcomplete.blocks;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;

public enum RelativeSide {
    FRONT,
    BACK,
    LEFT,
    RIGHT;
    
    public Direction onDirection(Direction direction){
        if (direction.getAxis() == Axis.Y) {
            throw new IllegalArgumentException("direction must not be vertical");
        }
        switch(this) {
            case FRONT:
                return direction;
            case BACK:
                return direction.getOpposite();
            case LEFT:
                if (direction.getAxis() == Axis.Z) {
                    return Direction.from(Axis.X, direction.getDirection());
                }
                else {
                    return Direction.from(Axis.Z,direction.getDirection().getOpposite());
                }
            case RIGHT:
                if (direction.getAxis() == Axis.Z) {
                    return Direction.from(Axis.X, direction.getDirection().getOpposite());
                }
                else {
                    return Direction.from(Axis.Z,direction.getDirection());
                }
            default:
                //Java should be smart enough to know this is unreachable. Because it's not, we won't get an error if the above code
                //stops being comprehensive
                throw new IllegalStateException();
        }
    }
}
