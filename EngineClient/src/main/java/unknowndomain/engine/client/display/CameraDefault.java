package unknowndomain.engine.client.display;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.lwjgl.glfw.GLFW;

import unknowndomain.engine.api.client.display.Camera;

public class CameraDefault implements Camera {

    public static final Vector3f UP_VECTOR = new Vector3f(0, 1, 0);
    private Vector3f pos = new Vector3f();
    private float yaw, pitch, roll;
    private double zoomRate;

    private int width = 854, height = 480;

    public CameraDefault() {
        zoomRate = 1;
    }

    @Override
    public void move(float x, float y, float z) {
        pos.add(x, y, z);
    }

    @Override
    public void moveTo(float x, float y, float z) {
        pos = new Vector3f(x, y, z);
    }

    @Override
    public void forward() {
        float moveC = 0.05f;
        Vector3f tmp = new Vector3f();
        getFrontVector().mul(moveC, tmp);
        pos.add(tmp);
    }

    @Override
    public void backward() {
        float moveC = 0.05f;
        Vector3f tmp = new Vector3f();
        getFrontVector().mul(moveC, tmp);
        pos.sub(tmp);
    }

    @Override
    public void left() {
        float moveC = 0.05f;
        Vector3f tmp = new Vector3f();
        getFrontVector().cross(UP_VECTOR, tmp);
        tmp.mul(moveC);
        pos.sub(tmp);
    }

    @Override
    public void right() {
        float moveC = 0.05f;
        Vector3f tmp = new Vector3f();
        getFrontVector().cross(UP_VECTOR, tmp);
        tmp.mul(moveC);
        pos.add(tmp);
    }

    private double lastX, lastY;
    private boolean setupLast = false;

    private static final float SENSIBILITY = 0.05f;

    @Override
    public void rotate(float x, float y) {
        double yaw = (x - lastX) * SENSIBILITY;
        double pitch = (lastY - y) * SENSIBILITY;
        lastX = x;
        lastY = y;
        if (setupLast) {
            this.pitch += pitch;
            this.pitch = Math.min(89.0f, Math.max(-89.0f, this.pitch));
            this.yaw += yaw;
        } else setupLast = true;
    }

    @Override
    public Matrix4f view() {
        Vector3fc front = getFrontVector();
        Vector3f center = new Vector3f();
        pos.add(front, center);
        Vector3f up = new Vector3f(0, 1, 0);
        //up.mulDirection(mat);
        return new Matrix4f().lookAt(pos, center, up).rotateZ((float) Math.toRadians(roll));
    }

    @Override
    public Matrix4f projection() {
        return new Matrix4f().perspective((float) (Math.toRadians(Math.max(1.0, Math.min(90.0, 60.0f * zoomRate)))), width / height, 0.01f, 1000f);
    }

    public void rotateTo(float yaw, float pitch) {
        this.pitch = pitch;
        this.pitch = Math.min(89.0f, Math.max(-89.0f, this.pitch));
        this.yaw = yaw;
    }


    public void zoom(double ratio) {
        zoomRate *= ratio;
    }

    public void zoomTo(double ratio) {
        zoomRate = ratio;
    }


    private Vector3fc getFrontVector() {
        return new Vector3f((float) (Math.cos(Math.toRadians(pitch)) * Math.cos(Math.toRadians(yaw))), (float) Math.sin(Math.toRadians(pitch)), (float) (Math.cos(Math.toRadians(pitch)) * Math.sin(Math.toRadians(yaw)))).normalize();
    }

}
