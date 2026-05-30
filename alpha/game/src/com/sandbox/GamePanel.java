package com.sandbox;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel implements Runnable, KeyListener, MouseListener, MouseMotionListener {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final double FOV = 500;

    private World world;
    private Camera camera;
    private boolean running;
    private boolean[] keys;
    private boolean mouseCaptured;
    private Block selectedBlock;
    private int targetX, targetY, targetZ;
    private boolean targetValid;

    private BufferedImage backBuffer;
    private int debugDx, debugDy;
    private Cursor transparentCursor;
    private int prevMouseX = -1;
    private int prevMouseY = -1;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);

        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);

        transparentCursor = getToolkit().createCustomCursor(
            new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB),
            new Point(), null);
        setCursor(transparentCursor);

        world = new World();
        camera = new Camera();
        keys = new boolean[256];
        selectedBlock = Block.GRASS;
        targetX = -1;
        targetY = -1;
        targetZ = -1;
        backBuffer = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    }

    public void start() {
        running = true;
        SwingUtilities.invokeLater(() -> {
            captureMouse();
        });
        new Thread(this).start();
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();

        while (running) {
            long now = System.nanoTime();
            double dt = (now - lastTime) / 1_000_000_000.0;
            lastTime = now;
            if (dt > 0.05) dt = 0.05;

            update(dt);
            renderToBuffer();
            repaint();

            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backBuffer != null) {
            g.drawImage(backBuffer, 0, 0, null);
        }
    }

    private void renderToBuffer() {
        Graphics2D g = backBuffer.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(new Color(100, 180, 255));
        g.fillRect(0, 0, WIDTH, HEIGHT / 2);
        g.setColor(new Color(90, 140, 90));
        g.fillRect(0, HEIGHT / 2, WIDTH, HEIGHT / 2);

        renderBlocks(g);
        renderBlockOutline(g);
        renderCrosshair(g);
        renderHUD(g);

        g.dispose();
    }

    private void update(double dt) {
        if (keys[KeyEvent.VK_W]) camera.moveForward(dt);
        if (keys[KeyEvent.VK_S]) camera.moveBackward(dt);
        if (keys[KeyEvent.VK_A]) camera.moveLeft(dt);
        if (keys[KeyEvent.VK_D]) camera.moveRight(dt);
        if (keys[KeyEvent.VK_SPACE]) camera.moveUp(dt);
        if (keys[KeyEvent.VK_SHIFT]) camera.moveDown(dt);
        if (keys[KeyEvent.VK_UP]) camera.pitch -= 1.5 * dt;
        if (keys[KeyEvent.VK_DOWN]) camera.pitch += 1.5 * dt;
        if (keys[KeyEvent.VK_LEFT]) camera.yaw -= 2.0 * dt;
        if (keys[KeyEvent.VK_RIGHT]) camera.yaw += 2.0 * dt;
        camera.pitch = Math.min(Math.PI / 2.2, Math.max(-Math.PI / 2.2, camera.pitch));

        camera.yaw %= 2 * Math.PI;
        if (camera.yaw < 0) camera.yaw += 2 * Math.PI;

        updateTarget();
    }

    private void updateTarget() {
        double dirX = Math.sin(camera.yaw) * Math.cos(camera.pitch);
        double dirY = -Math.sin(camera.pitch);
        double dirZ = Math.cos(camera.yaw) * Math.cos(camera.pitch);

        double step = 0.05;
        double maxDist = 8.0;

        targetX = -1;
        targetY = -1;
        targetZ = -1;
        targetValid = false;

        for (double d = 0.1; d < maxDist; d += step) {
            double px = camera.x + dirX * d;
            double py = camera.y + dirY * d;
            double pz = camera.z + dirZ * d;

            int bx = (int) Math.floor(px);
            int by = (int) Math.floor(py);
            int bz = (int) Math.floor(pz);

            Block block = world.getBlock(bx, by, bz);
            if (block != null && block.solid) {
                targetX = bx;
                targetY = by;
                targetZ = bz;
                targetValid = true;
                return;
            }
        }
    }

    private void renderBlocks(Graphics2D g) {
        List<Face> faces = new ArrayList<>();

        for (int x = 0; x < World.WIDTH; x++) {
            for (int y = 0; y < World.HEIGHT; y++) {
                for (int z = 0; z < World.DEPTH; z++) {
                    Block block = world.getBlock(x, y, z);
                    if (block == null || !block.solid) continue;
                    addBlockFaces(faces, block, x, y, z);
                }
            }
        }

        faces.sort((a, b) -> Double.compare(b.depth, a.depth));

        for (Face face : faces) {
            drawFace(g, face);
        }
    }

    private void addBlockFaces(List<Face> faces, Block block, int bx, int by, int bz) {
        double cx = bx + 0.5;
        double cy = by + 0.5;
        double cz = bz + 0.5;
        double h = 0.5;

        double[][][] faceVerts = {
            {{-h, h, -h}, {-h, h, h}, {h, h, h}, {h, h, -h}},
            {{-h, -h, -h}, {h, -h, -h}, {h, -h, h}, {-h, -h, h}},
            {{-h, -h, h}, {h, -h, h}, {h, h, h}, {-h, h, h}},
            {{-h, -h, -h}, {-h, h, -h}, {h, h, -h}, {h, -h, -h}},
            {{-h, -h, -h}, {-h, -h, h}, {-h, h, h}, {-h, h, -h}},
            {{h, -h, -h}, {h, h, -h}, {h, h, h}, {h, -h, h}}
        };

        int[][] faceDirs = {{0, 1, 0}, {0, -1, 0}, {0, 0, 1}, {0, 0, -1}, {-1, 0, 0}, {1, 0, 0}};
        Color[] faceColors = {block.topColor, block.bottomColor, block.sideColor, block.sideColor, block.sideColor, block.sideColor};

        for (int fi = 0; fi < 6; fi++) {
            int dx = faceDirs[fi][0];
            int dy = faceDirs[fi][1];
            int dz = faceDirs[fi][2];

            if (world.hasNeighbor(bx, by, bz, dx, dy, dz)) continue;

            double[][] worldVerts = new double[4][3];
            double avgZ = 0;
            boolean valid = true;

            for (int vi = 0; vi < 4; vi++) {
                double[] vert = faceVerts[fi][vi];
                double wx = cx + vert[0];
                double wy = cy + vert[1];
                double wz = cz + vert[2];

                double[] cam = toCameraSpace(wx, wy, wz);
                if (cam[2] <= 0.1) {
                    valid = false;
                    break;
                }
                worldVerts[vi] = cam;
                avgZ += cam[2];
            }

            if (!valid) continue;
            avgZ /= 4;

            int[] sx = new int[4];
            int[] sy = new int[4];

            for (int vi = 0; vi < 4; vi++) {
                double[] cam = worldVerts[vi];
                sx[vi] = (int) (cam[0] / cam[2] * FOV + WIDTH / 2);
                sy[vi] = (int) (-cam[1] / cam[2] * FOV + HEIGHT / 2);
            }

            faces.add(new Face(sx, sy, 4, faceColors[fi], avgZ));
        }
    }

    private double[] toCameraSpace(double wx, double wy, double wz) {
        double dx = wx - camera.x;
        double dy = wy - camera.y;
        double dz = wz - camera.z;

        double cosYaw = Math.cos(camera.yaw);
        double sinYaw = Math.sin(camera.yaw);
        double x1 = dx * cosYaw - dz * sinYaw;
        double y1 = dy;
        double z1 = dx * sinYaw + dz * cosYaw;

        double cosPitch = Math.cos(camera.pitch);
        double sinPitch = Math.sin(camera.pitch);
        double x2 = x1;
        double y2 = y1 * cosPitch - z1 * sinPitch;
        double z2 = y1 * sinPitch + z1 * cosPitch;

        return new double[]{x2, y2, z2};
    }

    private void drawFace(Graphics2D g, Face face) {
        Color base = face.color;
        double shade = face.depth > 0 ? Math.min(1.0, 1.5 / (face.depth * 0.3 + 1.0)) : 1.0;
        shade = Math.max(0.4, Math.min(1.0, shade));

        int r = (int) (base.getRed() * shade);
        int gr = (int) (base.getGreen() * shade);
        int b = (int) (base.getBlue() * shade);
        r = Math.min(255, Math.max(0, r));
        gr = Math.min(255, Math.max(0, gr));
        b = Math.min(255, Math.max(0, b));

        g.setColor(new Color(r, gr, b));
        g.fillPolygon(face.xs, face.ys, face.count);

        g.setColor(new Color(0, 0, 0, 50));
        g.setStroke(new BasicStroke(1f));
        g.drawPolygon(face.xs, face.ys, face.count);
    }

    private void renderBlockOutline(Graphics2D g) {
        if (!targetValid) return;

        double cx = targetX + 0.5;
        double cy = targetY + 0.5;
        double cz = targetZ + 0.5;
        double h = 0.505;

        double[][][] edges = {
            {{-h, -h, -h}, {h, -h, -h}},
            {{h, -h, -h}, {h, h, -h}},
            {{h, h, -h}, {-h, h, -h}},
            {{-h, h, -h}, {-h, -h, -h}},
            {{-h, -h, h}, {h, -h, h}},
            {{h, -h, h}, {h, h, h}},
            {{h, h, h}, {-h, h, h}},
            {{-h, h, h}, {-h, -h, h}},
            {{-h, -h, -h}, {-h, -h, h}},
            {{h, -h, -h}, {h, -h, h}},
            {{h, h, -h}, {h, h, h}},
            {{-h, h, -h}, {-h, h, h}}
        };

        for (double[][] edge : edges) {
            double[] p1 = toCameraSpace(cx + edge[0][0], cy + edge[0][1], cz + edge[0][2]);
            double[] p2 = toCameraSpace(cx + edge[1][0], cy + edge[1][1], cz + edge[1][2]);

            if (p1[2] <= 0.1 || p2[2] <= 0.1) continue;

            int x1 = (int) (p1[0] / p1[2] * FOV + WIDTH / 2);
            int y1 = (int) (-p1[1] / p1[2] * FOV + HEIGHT / 2);
            int x2 = (int) (p2[0] / p2[2] * FOV + WIDTH / 2);
            int y2 = (int) (-p2[1] / p2[2] * FOV + HEIGHT / 2);

            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(2.5f));
            g.drawLine(x1, y1, x2, y2);
        }
    }

    private void renderCrosshair(Graphics2D g) {
        int cx = WIDTH / 2;
        int cy = HEIGHT / 2;
        int size = 10;

        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(2f));
        g.drawLine(cx - size, cy, cx + size, cy);
        g.drawLine(cx, cy - size, cx, cy + size);
    }

    private void renderHUD(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        if (targetValid) {
            Block targetBlock = world.getBlock(targetX, targetY, targetZ);
            if (targetBlock != null) {
                g.drawString(targetBlock.name + " [" + targetX + ", " + targetY + ", " + targetZ + "]", 14, 24);
            }
        }
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g.setColor(new Color(200, 200, 200));
        g.drawString(String.format("Pos: %.1f, %.1f, %.1f", camera.x, camera.y, camera.z), 14, 44);

        g.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g.setColor(new Color(150, 150, 150));
        g.drawString("WASD:Move  Spc:Up  Shft:Down  ESC:Release", 14, HEIGHT - 74);
        g.drawString("LMB:Break  RMB:Place", 14, HEIGHT - 58);

        renderHotbar(g);
    }

    private void renderHotbar(Graphics2D g) {
        Block[] blocks = {Block.GRASS, Block.DIRT, Block.STONE, Block.WOOD,
                          Block.LEAVES, Block.PLANKS, Block.SAND, Block.BRICK};

        int slotSize = 52;
        int gap = 4;
        int barW = blocks.length * (slotSize + gap) - gap;
        int barH = 78;
        int barX = (WIDTH - barW) / 2;
        int barY = HEIGHT - barH - 14;

        g.setColor(new Color(0, 0, 0, 120));
        g.fillRoundRect(barX - 10, barY - 8, barW + 20, barH + 16, 18, 18);
        g.setColor(new Color(22, 22, 28, 230));
        g.fillRoundRect(barX, barY, barW, barH, 12, 12);

        for (int i = 0; i < blocks.length; i++) {
            int sx = barX + i * (slotSize + gap);
            int sy = barY + 6;

            if (blocks[i] == selectedBlock) {
                g.setColor(new Color(100, 160, 255, 60));
                g.fillRoundRect(sx, sy, slotSize, slotSize, 8, 8);
                g.setColor(new Color(100, 160, 255, 180));
                g.setStroke(new BasicStroke(2f));
                g.drawRoundRect(sx, sy, slotSize, slotSize, 8, 8);
            } else {
                g.setColor(new Color(45, 45, 52, 180));
                g.fillRoundRect(sx, sy, slotSize, slotSize, 8, 8);
            }

            int ps = 22;
            int px = sx + (slotSize - ps) / 2;
            int py = sy + 6;
            g.setColor(blocks[i].topColor);
            g.fillRect(px, py, ps, ps);

            g.setColor(new Color(0, 0, 0, 80));
            g.drawRect(px, py, ps, ps);

            g.setColor(Color.WHITE);
            g.setFont(new Font("SansSerif", Font.PLAIN, 9));
            String name = blocks[i].name;
            int nw = g.getFontMetrics().stringWidth(name);
            g.drawString(name, sx + (slotSize - nw) / 2, sy + slotSize - 6);

            if (blocks[i] == selectedBlock) {
                g.setColor(new Color(130, 180, 255));
            } else {
                g.setColor(new Color(120, 120, 130));
            }
            g.setFont(new Font("SansSerif", Font.PLAIN, 9));
            String key = String.valueOf(i + 1);
            int kw = g.getFontMetrics().stringWidth(key);
            g.drawString(key, sx + (slotSize - kw) / 2, sy + 54);
        }
    }

    public void breakBlock() {
        if (targetValid) {
            world.setBlock(targetX, targetY, targetZ, Block.AIR);
        }
    }

    public void placeBlock() {
        if (!targetValid) return;

        double dirX = Math.sin(camera.yaw) * Math.cos(camera.pitch);
        double dirY = -Math.sin(camera.pitch);
        double dirZ = Math.cos(camera.yaw) * Math.cos(camera.pitch);

        double step = 0.02;
        double maxDist = 8.0;

        for (double d = 0.1; d < maxDist; d += step) {
            double px = camera.x + dirX * d;
            double py = camera.y + dirY * d;
            double pz = camera.z + dirZ * d;

            int bx = (int) Math.floor(px);
            int by = (int) Math.floor(py);
            int bz = (int) Math.floor(pz);

            Block block = world.getBlock(bx, by, bz);
            if (block != null && block.solid) {
                if (bx == targetX && by == targetY && bz == targetZ) {
                    int nx = (int) Math.floor(px - dirX * step * 2);
                    int ny = (int) Math.floor(py - dirY * step * 2);
                    int nz = (int) Math.floor(pz - dirZ * step * 2);

                    Block existing = world.getBlock(nx, ny, nz);
                    if (existing == null || !existing.solid) {
                        world.setBlock(nx, ny, nz, selectedBlock);
                    }
                }
                return;
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() < 256) {
            keys[e.getKeyCode()] = true;
        }

        if (e.getKeyCode() >= KeyEvent.VK_1 && e.getKeyCode() <= KeyEvent.VK_8) {
            Block[] blocks = {Block.GRASS, Block.DIRT, Block.STONE, Block.WOOD,
                            Block.LEAVES, Block.PLANKS, Block.SAND, Block.BRICK};
            int index = e.getKeyCode() - KeyEvent.VK_1;
            if (index < blocks.length) {
                selectedBlock = blocks[index];
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            releaseMouse();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() < 256) {
            keys[e.getKeyCode()] = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {
        if (!mouseCaptured) return;
        if (e.getButton() == MouseEvent.BUTTON1) {
            breakBlock();
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            placeBlock();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (!mouseCaptured) {
            captureMouse(e);
        } else {
            if (e.getButton() == MouseEvent.BUTTON1) {
                breakBlock();
            } else if (e.getButton() == MouseEvent.BUTTON3) {
                placeBlock();
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (!mouseCaptured) {
            prevMouseX = -1;
            prevMouseY = -1;
            return;
        }
        int curX = e.getXOnScreen();
        int curY = e.getYOnScreen();
        if (prevMouseX >= 0) {
            int rawDx = curX - prevMouseX;
            int rawDy = curY - prevMouseY;
            debugDx = rawDx;
            debugDy = rawDy;
            if (rawDx != 0 || rawDy != 0) {
                camera.rotateYaw(rawDx);
                camera.rotatePitch(rawDy * 2);
            }
        }
        prevMouseX = curX;
        prevMouseY = curY;
    }

    private void captureMouse() {
        mouseCaptured = true;
        requestFocusInWindow();
    }

    private void captureMouse(MouseEvent e) {
        captureMouse();
    }

    private void releaseMouse() {
        mouseCaptured = false;
    }

    private static class Face {
        int[] xs, ys;
        int count;
        Color color;
        double depth;

        Face(int[] xs, int[] ys, int count, Color color, double depth) {
            this.xs = xs;
            this.ys = ys;
            this.count = count;
            this.color = color;
            this.depth = depth;
        }
    }
}