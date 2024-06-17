package net.themcbrothers.lib.client.model.fluid;

import net.minecraft.core.Direction;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;

public class FluidCuboid {
    public static final Map<Direction, FluidFace> DEFAULT_FACES;

    static {
        DEFAULT_FACES = new EnumMap<>(Direction.class);
        for (Direction direction : Direction.values()) {
            DEFAULT_FACES.put(direction, FluidFace.NORMAL);
        }
    }

    /**
     * Fluid start, scaled for block models
     */
    private final Vector3f from;

    /**
     * Fluid end, scaled for block models
     */
    private final Vector3f to;

    /**
     * Block faces for the fluid
     */
    private final Map<Direction, FluidFace> faces;

    /**
     * Cache for scaled from
     */
    @Nullable
    private Vector3f fromScaled;
    /**
     * Cache for scaled to
     */
    @Nullable
    private Vector3f toScaled;

    public FluidCuboid(Vector3f from, Vector3f to, Map<Direction, FluidFace> faces) {
        this.from = from;
        this.to = to;
        this.faces = faces;
    }

    /**
     * Checks if the fluid has the given face
     *
     * @param face Face to check
     * @return True if the face is present
     */
    @Nullable
    public FluidFace getFace(Direction face) {
        return faces.get(face);
    }

    /**
     * Gets fluid from, scaled for renderer
     *
     * @return Scaled from
     */
    public Vector3f getFromScaled() {
        if (fromScaled == null) {
            fromScaled = new Vector3f(from);
            fromScaled.mul(1 / 16f);
        }
        return fromScaled;
    }

    /**
     * Gets fluid to, scaled for renderer
     *
     * @return Scaled from
     */
    public Vector3f getToScaled() {
        if (toScaled == null) {
            toScaled = new Vector3f(to);
            toScaled.mul(1 / 16f);
        }
        return toScaled;
    }

    public Vector3f getFrom() {
        return from;
    }

    public Vector3f getTo() {
        return to;
    }

    public Map<Direction, FluidFace> getFaces() {
        return faces;
    }

    /**
     * Represents a single fluid face in the model
     */
    public record FluidFace(boolean isFlowing, int rotation) {
        public static final FluidFace NORMAL = new FluidFace(false, 0);
    }
}