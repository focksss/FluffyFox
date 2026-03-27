package com.ff.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import static com.ff.FluffyFoxClient.MC;

public final class RenderingUtil {

    private RenderingUtil() {}

    public static void drawTracer(
        MatrixStack ms,
        VertexConsumerProvider.Immediate vcp,
        Vec3d cam, Vec3d target,
        float r, float g, float b, float a
    ) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        float yaw   = (float) Math.toRadians(mc.player.getYaw());
        float pitch = (float) Math.toRadians(mc.player.getPitch());
        float ox = (float)(-Math.sin(yaw)   * Math.cos(pitch) * 0.5);
        float oy = (float)(-Math.sin(pitch) * 0.5);
        float oz = (float)( Math.cos(yaw)   * Math.cos(pitch) * 0.5);

        float ex = (float)(target.x - cam.x);
        float ey = (float)(target.y - cam.y);
        float ez = (float)(target.z - cam.z);

        drawLine(ms, vcp, ox, oy, oz, ex, ey, ez, r, g, b, a);
    }

    public static void drawLine(
        MatrixStack ms,
        VertexConsumerProvider.Immediate vcp,
        float x0, float y0, float z0,
        float x1, float y1, float z1,
        float r, float g, float b, float a
    ) {
        VertexConsumer buf  = vcp.getBuffer(RenderLayers.lines());
        Matrix4f       mat  = ms.peek().getPositionMatrix();
        int            col  = argb(r, g, b, a);

        float nx = x1 - x0, ny = y1 - y0, nz = z1 - z0;
        float len = (float) Math.sqrt(nx*nx + ny*ny + nz*nz);
        if (len > 0) { nx /= len; ny /= len; nz /= len; }

        buf.vertex(mat, x0, y0, z0).color(col).lineWidth(1.5f).normal(ms.peek(), nx, ny, nz);
        buf.vertex(mat, x1, y1, z1).color(col).lineWidth(1.5f).normal(ms.peek(), nx, ny, nz);
    }

    public static void drawOutlineBox(
        MatrixStack ms,
        VertexConsumerProvider.Immediate vcp,
        double wx,  double wy,  double wz,
        double wx2, double wy2, double wz2,
        float r, float g, float b, float a
    ) {
        Vec3d cam = MC.gameRenderer.getCamera().getCameraPos();
        float x0 = (float)(wx  - cam.x), y0 = (float)(wy  - cam.y), z0 = (float)(wz  - cam.z);
        float x1 = (float)(wx2 - cam.x), y1 = (float)(wy2 - cam.y), z1 = (float)(wz2 - cam.z);

        VertexConsumer buf  = vcp.getBuffer(RenderLayers.lines());
        int            col  = argb(r, g, b, a);

        MatrixStack.Entry mat = ms.peek();

        // bottom ring
        edge(buf, mat, col, x0,y0,z0, x1,y0,z0);
        edge(buf, mat, col,  x1,y0,z0, x1,y0,z1);
        edge(buf, mat, col,  x1,y0,z1, x0,y0,z1);
        edge(buf, mat, col,  x0,y0,z1, x0,y0,z0);
        // top ring
        edge(buf, mat, col,  x0,y1,z0, x1,y1,z0);
        edge(buf, mat, col,  x1,y1,z0, x1,y1,z1);
        edge(buf, mat, col,  x1,y1,z1, x0,y1,z1);
        edge(buf, mat, col,  x0,y1,z1, x0,y1,z0);
        // verticals
        edge(buf, mat, col,  x0,y0,z0, x0,y1,z0);
        edge(buf, mat, col,  x1,y0,z0, x1,y1,z0);
        edge(buf, mat, col,  x1,y0,z1, x1,y1,z1);
        edge(buf, mat, col,  x0,y0,z1, x0,y1,z1);
    }

    public static void drawFilledBox(
        MatrixStack ms,
        VertexConsumerProvider.Immediate vcp,
        double wx,  double wy,  double wz,
        double wx2, double wy2, double wz2,
        float r, float g, float b, float a
    ) {
        Vec3d cam = MinecraftClient.getInstance().gameRenderer.getCamera().getCameraPos();
        float x0 = (float)(wx  - cam.x), y0 = (float)(wy  - cam.y), z0 = (float)(wz  - cam.z);
        float x1 = (float)(wx2 - cam.x), y1 = (float)(wy2 - cam.y), z1 = (float)(wz2 - cam.z);

        VertexConsumer buf = vcp.getBuffer(RenderLayers.debugFilledBox());
        Matrix4f       mat = ms.peek().getPositionMatrix();
        int            col = argb(r, g, b, a);

        quad(buf, mat, col,  x0,y0,z0, x0,y0,z1, x1,y0,z1, x1,y0,z0); // -Y
        quad(buf, mat, col,  x0,y1,z0, x1,y1,z0, x1,y1,z1, x0,y1,z1); // +Y
        quad(buf, mat, col,  x0,y0,z0, x1,y0,z0, x1,y1,z0, x0,y1,z0); // -Z
        quad(buf, mat, col,  x0,y0,z1, x0,y1,z1, x1,y1,z1, x1,y0,z1); // +Z
        quad(buf, mat, col,  x0,y0,z0, x0,y1,z0, x0,y1,z1, x0,y0,z1); // -X
        quad(buf, mat, col,  x1,y0,z0, x1,y0,z1, x1,y1,z1, x1,y1,z0); // +X
    }

    public static void drawBeam(
        MatrixStack ms,
        VertexConsumerProvider.Immediate vcp,
        Vec3d cam, Vec3d worldPos,
        float height, float halfWidth,
        float r, float g, float b, float a
    ) {
        float dx = (float)(worldPos.x - cam.x);
        float dy = (float)(worldPos.y - cam.y);
        float dz = (float)(worldPos.z - cam.z);

        float x0 = dx - halfWidth, x1 = dx + halfWidth;
        float z0 = dz - halfWidth, z1 = dz + halfWidth;
        float yBot = dy, yTop = dy + height;

        VertexConsumer buf = vcp.getBuffer(RenderLayers.debugQuads());
        Matrix4f       mat = ms.peek().getPositionMatrix();
        int            col = argb(r, g, b, a);

        quad(buf, mat, col,  x0,yBot,z1, x1,yBot,z1, x1,yTop,z1, x0,yTop,z1); // +Z
        quad(buf, mat, col,  x1,yBot,z0, x0,yBot,z0, x0,yTop,z0, x1,yTop,z0); // -Z
        quad(buf, mat, col,  x1,yBot,z1, x1,yBot,z0, x1,yTop,z0, x1,yTop,z1); // +X
        quad(buf, mat, col,  x0,yBot,z0, x0,yBot,z1, x0,yTop,z1, x0,yTop,z0); // -X
    }

    private static void edge(
        VertexConsumer buf, MatrixStack.Entry mat, int col,
        float x0, float y0, float z0,
        float x1, float y1, float z1
    ) {
        float nx = x1-x0, ny = y1-y0, nz = z1-z0;
        float len = (float) Math.sqrt(nx*nx + ny*ny + nz*nz);
        if (len > 0) { nx /= len; ny /= len; nz /= len; }
        buf.vertex(mat, x0, y0, z0).color(col).normal(mat, nx, ny, nz);
        buf.vertex(mat, x1, y1, z1).color(col).normal(mat, nx, ny, nz);
    }

    private static void quad(
        VertexConsumer buf, Matrix4f mat, int col,
        float x0, float y0, float z0,
        float x1, float y1, float z1,
        float x2, float y2, float z2,
        float x3, float y3, float z3
    ) {
        buf.vertex(mat, x0, y0, z0).color(col);
        buf.vertex(mat, x1, y1, z1).color(col);
        buf.vertex(mat, x2, y2, z2).color(col);
        buf.vertex(mat, x3, y3, z3).color(col);
    }

    public static int argb(float r, float g, float b, float a) {
        return ((int)(a * 255) << 24) | ((int)(r * 255) << 16)
                | ((int)(g * 255) <<  8) |  (int)(b * 255);
    }
}