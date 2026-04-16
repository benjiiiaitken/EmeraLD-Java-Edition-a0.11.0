//importz :o
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.File;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;

public class EmeraLD_Java_edition_a1_1_1_1 extends JFrame {
    //the version
    private static final String VERSION = "a1.1.1_1";
    private final GameCanvas canvas;
    private final String splash;
    public EmeraLD_Java_edition_a1_1_1_1() {
        //the name enter thing
        String name = JOptionPane.showInputDialog(this, "Enter your name:");
        if (name == null || name.trim().isEmpty()) {
        name = "veLcro";
        }
       //the welcome stuff & splashes
        String welcomeMsg = "Welcome, " + name + " Welcome to EmeraLD - Java Edition " + VERSION;
        welcomeMsg += "\nControls:\nWASD - Move\nSpace - Jump\nShift - Crouch\nArrow Keys + Mouse - Look Around\nR - Hellmode\nT - RAINBOW MODE :3 ";
        JOptionPane.showMessageDialog(this, welcomeMsg, "Welcome to EmeraLD!", JOptionPane.INFORMATION_MESSAGE);
        String[] splashes = {"Work of fennicx!", "This Ruby is Dung", "alt + f4!", "thanks for helping " + name + "!", "Copycat!", "May be buggy :D", "Java!", "insert splash text here :D", "gun", "twitter.",
        "youtube", "tiktok", "www.skipitstudios.org", "R to Reset", "Cave game 2","spunchbob", "too many splashes D:", "...", "try the python edition :O", "I HAVE THE POWER OF COPYWRIGHT ON MY SIDE!", "hello " + name + "!",
        "EmeraLD is the best!", "EmeraLD is the worst!", "EmeraLD is aight.", "EmeraLD is a masterpiece!", "EmeraLD is a disaster!", "EmeraLD is a work of art!", "EmeraLD is a work of garbage!", "EmeraLD is a work of genius!",
        "EmeraLD is a work of stupidity!", "this was added version a0.12.0!", "its " +VERSION+ " time!", "hang on... " +VERSION+ " loading", "only a few lines of code!", "Open source without the source engine!",
        "( ͡° ͜ʖ ͡°) (╯°□°）╯︵ ┻━┻ ¯\\_(ツ)_/¯ ಠ_ಠ (ง'̀-'́)ง ┬─┬ノ( º _ ºノ)", "it kinda lags while loading the ASCII art one XD", "you're welcome here!", "dont even joke mate.", "!£$%^&*()_+-=}{]{~@:?></|`¬", "Bro looks like an egg",
        "Creepah, aww man", "possibly a copy of a blocky game", "Half-Life > Quake", "habatalabaubalamtum", "Capes were a no go - SalC1", "M to change splash!", "R = AHHHHH T = :D", "First comment to see this replaces this :o", "Absolute bliss!"};
        splash = splashes[new Random().nextInt(splashes.length)];

        setTitle("EmeraLD Engine - Java Edition " + VERSION);
setResizable(true);

canvas = new GameCanvas(1280, 720, splash);
add(canvas);
pack();

setLocationRelativeTo(null);
        setVisible(true);
        canvas.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EmeraLD_Java_edition_a1_1_1_1::new);
    }
}
class GameCanvas extends Canvas implements Runnable {
    private float rotX = 0;
private boolean isMouseLocked = true;
  private Robot mouseRobot;
    private boolean isMouseLookEnabled = true;
    private boolean running;
    private BufferedImage screenBuffer;
    private int[] pixels;
    //... dont get rid of zbuffer... trust me.
    private float[] zBuffer;
    private int width, height;
    private String splashText;

    private BufferedImage playerSkin;

    private int skyColor =  0x6699cc;
    private float fogNear = 90;
    private float fogFar = 190;

    private float camX = 0, camY = 8, camZ = -15; 
    private float rotY = 0;
    private boolean[] keys = new boolean[65536];

    private int[] texture;
    private int texW, texH;
    private List<Block> world;

    public GameCanvas(int w, int h, String splash) {
        try { mouseRobot = new Robot(); } catch (AWTException e) { e.printStackTrace(); }

addKeyListener(new KeyAdapter() {
    public void keyPressed(KeyEvent e) { 
        if(e.getKeyCode() < keys.length) keys[e.getKeyCode()] = true; 
        
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            isMouseLocked = !isMouseLocked;
            if (isMouseLocked) {
                setCursor(getToolkit().createCustomCursor(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "invisible"));
            } else {
                setCursor(Cursor.getDefaultCursor());
            }
        }
    }
    public void keyReleased(KeyEvent e) { if(e.getKeyCode() < keys.length) keys[e.getKeyCode()] = false; }
});
addMouseMotionListener(new MouseMotionAdapter() {
    @Override
    public void mouseMoved(MouseEvent e) {
        if (!isMouseLocked) return; 

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int dx = e.getX() - centerX;
        int dy = e.getY() - centerY;

        if (dx != 0 || dy != 0) {
            float mouseSensitivity = 0.003f;
            rotY -= dx * mouseSensitivity; 
            rotX -= dy * mouseSensitivity; 
            
            rotX = Math.max(-1.57f, Math.min(1.57f, rotX)); 

            Point screenLoc = getLocationOnScreen();
            mouseRobot.mouseMove(screenLoc.x + centerX, screenLoc.y + centerY);
        }
    }
});
        this.width = w;
        this.height = h;
        this.splashText = splash;
        setPreferredSize(new Dimension(w, h));
        setCursor(getToolkit().createCustomCursor(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "invisible"));
        try {
            mouseRobot = new Robot();
        } catch (Exception e) {
            e.printStackTrace();
        }
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (!isMouseLookEnabled) return;
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                int dx = e.getX() - centerX;
                if (dx != 0) {
                    float mouseSensitivity = 0.003f;
                    rotY -= dx * mouseSensitivity;
                    Point screenLocation = getLocationOnScreen();
                    mouseRobot.mouseMove(screenLocation.x + centerX, screenLocation.y + centerY);
                }
            }
        });
        setFocusable(true);


        screenBuffer = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        pixels = ((DataBufferInt) screenBuffer.getRaster().getDataBuffer()).getData();
        zBuffer = new float[w * h];

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) { if(e.getKeyCode() < keys.length) keys[e.getKeyCode()] = true; }
            public void keyReleased(KeyEvent e) { if(e.getKeyCode() < keys.length) keys[e.getKeyCode()] = false; }
        });
//the main texture (Stone)
   loadTexture("C:\\Program Files\\EmeraLD\\Stone.jpeg.png");
        generateWorld();
    }

    private void loadTexture(String path) {
        try {
            BufferedImage img = ImageIO.read(new File(path));
            texW = img.getWidth();
            texH = img.getHeight();
            texture = new int[texW * texH];
            img.getRGB(0, 0, texW, texH, texture, 0, texW);
        } catch (Exception e) {
            texW = texH = 16;
            texture = new int[256];
            Arrays.fill(texture, 0x88888888);
        }
        try { playerSkin = ImageIO.read(new File("C:\\Users\\aitke\\Downloads\\OG BEINZ.png")); } catch (Exception e) {}
    }

    private void generateWorld() {
        world = new ArrayList<>();
        for (int x = -100; x < 100; x++) {
            for (int z = -100; z < 100; z++) {
 
                int h = (int) (Math.sin(x * 0.2) * Math.cos(z * 0.2) * 5) + 3;
                for (int y = 0; y < h; y++) {
                    world.add(new Block(x, y, z));
                }
            }
        }
    }

    public void start() {
        running = true;
        new Thread(this).start();
    }

    @Override
    public void run() {
        while (running) {
            handleInput();
            render();
            try { Thread.sleep(10); } catch (InterruptedException e) {}
        }
    }

    private void handleInput() {
        float speed = 0.2f;
        float rotSpeed = 0.05f;


//the computer key whisperer (james) :D
        if (keys[KeyEvent.VK_W]) { camX -= Math.sin(rotY) * speed; camZ += Math.cos(rotY) * speed; }
        if (keys[KeyEvent.VK_S]) { camX += Math.sin(rotY) * speed; camZ -= Math.cos(rotY) * speed; }
        if (keys[KeyEvent.VK_A]) { camX -= Math.cos(rotY) * speed; camZ -= Math.sin(rotY) * speed; }
        if (keys[KeyEvent.VK_D]) { camX += Math.cos(rotY) * speed; camZ += Math.sin(rotY) * speed; }
        if (keys[KeyEvent.VK_SPACE]) camY += speed;
        if (keys[KeyEvent.VK_SHIFT]) camY -= speed;
        if (keys[KeyEvent.VK_LEFT]) rotY += rotSpeed;
        if (keys[KeyEvent.VK_RIGHT]) rotY -= rotSpeed;
        if (keys[KeyEvent.VK_J]) loadTexture("C:\\Program Files\\EmeraLD\\Grass.jpeg.png");
        if (keys[KeyEvent.VK_K]) loadTexture("C:\\Program Files\\EmeraLD\\Stone.jpeg.png");
        if (keys[KeyEvent.VK_ENTER]) loadTexture("puttexturehere:D");
        if (keys[KeyEvent.VK_I]) loadTexture("puttexturehere:D");
        if (keys[KeyEvent.VK_E]) loadTexture("puttexturehere:D");
        if (keys[KeyEvent.VK_M]) splashText = "get tricked bozo";
        //Hellmode :(
        if (keys[KeyEvent.VK_R]) {
            setName("AHHHHHHHHHHHH");
            splashText = "Welcome to HELL!";
            skyColor = 0x8B0000;
            fogNear = 5;
            fogFar = 30;
            camY = 5;
            loadTexture("C:\\Program Files\\EmeraLD\\Stone.jpeg.png");
        }
        //pinkfluffyunicorns mode :)
        if (keys[KeyEvent.VK_T]) {
            setName("prettyunicorn321");
            splashText = "Hiya!, W3lc0m 2 my w0ndewfuw w0wld!";
            skyColor = 0x93CAED;
            fogNear = 9999;
            fogFar = 9999;
            camY = 15;
            loadTexture("C:\\Program Files\\EmeraLD\\Grass.jpeg.png");
        }
     }

    


    private void render() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) { createBufferStrategy(2); return; }

        Arrays.fill(pixels, skyColor);
        Arrays.fill(zBuffer, Float.MAX_VALUE);

        for (Block b : world) {
            drawVoxel(b.x, b.y, b.z);
            
        }

        Graphics g = bs.getDrawGraphics();
        g.drawImage(screenBuffer, 0, 0, null);
        
        Graphics2D g2d = (Graphics2D) g;
        Font customFont = new Font("Arial", Font.BOLD, 15);
        g.setColor(Color.white);
        g2d.setFont(customFont);
        g2d.drawString("+", 640, 360);
        //must be a minecraft skin OR you could put the image in the top left corner and have the rest of the image be transparent :D
        if (playerSkin != null) {
    BufferedImage face = playerSkin.getSubimage(8, 8, 8, 8); 
    
    g.drawImage(face, width - 100, height - 100, 64, 64, null);
    
    g.setColor(Color.WHITE);
    g.drawString("Developer", width - 110, height - 20);
}


        g.setColor(Color.YELLOW);
        g.drawString(splashText, 500, 40);
        

        g.setColor(Color.WHITE);
       if (keys[KeyEvent.VK_F5]) g.drawString(String.format("XYZ: %.3f / %.3f / %.3f | RotX: %.2f | RotY: %.1f | Blocks: %d | %h | %o | Java: %s | Brightness: %.2f", -camX, -camY, camZ, Math.toDegrees(rotY), Math.toDegrees(rotX) % 360, world.size(), (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024, Runtime.getRuntime().maxMemory() / 1024 / 1024, System.getProperty("java.version"), 1.0f), 20, 100);
        g.drawString("EmeraLD a1.1.1_1", 20, 60);
        //booooorrrriiiiinnnnngggg ¬ ¬
        //                          -
        g.setColor(Color.orange);
        g.drawString("Press F5 for stats", 20, 700);
        
        //da logo
        try {
            BufferedImage logo = ImageIO.read(new File("C:\\Program Files\\EmeraLD\\EmeraLD logo alpha.png"));
            g.drawImage(logo, 20, 120, null);
        } catch (Exception e) {

        }
        
        g.dispose();
        bs.show();
    }

    private void drawVoxel(float bx, float by, float bz) {
 float tx = bx - camX - (keys[KeyEvent.VK_F3] ? (float)Math.sin(rotY) * 7.0f : 0);
float ty = by - camY - (keys[KeyEvent.VK_F3] ? 2.5f : 0);
float tz = bz - camZ + (keys[KeyEvent.VK_F3] ? (float)Math.cos(rotY) * 7.0f : 0);


    float cosY = (float) Math.cos(-rotY);
    float sinY = (float) Math.sin(-rotY);
    float rx = tx * cosY - tz * sinY;
    float rz1 = tx * sinY + tz * cosY;

    float cosX = (float) Math.cos(rotX);
    float sinX = (float) Math.sin(rotX);
    float ry = ty * cosX - rz1 * sinX;
    float rz = ty * sinX + rz1 * cosX;

    if (rz < 0.2f) return;

    float fov = 800.0f;
    int sx = (int) (rx * fov / rz) + width / 2;
    int sy = (int) (-ry * fov / rz) + height / 2; 
    int size = (int) (fov / rz);


        if (rz < 0.2f) return;

        if (sx + size < 0 || sx >= width || sy + size < 0 || sy >= height) return;


        float fogFactor = (rz - fogNear) / (fogFar - fogNear);
        if (fogFactor < 0) fogFactor = 0;
        if (fogFactor > 1) fogFactor = 1;

        for (int y = 0; y < size; y++) {
            int py = sy + y;
            if (py < 0 || py >= height) continue;
            for (int x = 0; x < size; x++) {
                int px = sx + x;
                if (px < 0 || px >= width) continue;

                if (rz < zBuffer[px + py * width]) {
                    zBuffer[px + py * width] = rz;
                    
                    int txP = (int) ((float) x / size * (texW - 1));
                    int tyP = (int) ((float) y / size * (texH - 1));
                    int color = texture[txP + tyP * texW];
                    
        
                    if (fogFactor > 0) {
                        color = lerpColor(color, skyColor, fogFactor);
                    }
                    
                    pixels[px + py * width] = color;
                }
            }
        }
    }
//im too lazy to explain what this is go figure out urself.
    private int lerpColor(int c1, int c2, float t) {
        int r1 = (c1 >> 16) & 0xFF, g1 = (c1 >> 8) & 0xFF, b1 = c1 & 0xFF;
        int r2 = (c2 >> 16) & 0xFF, g2 = (c2 >> 8) & 0xFF, b2 = c2 & 0xFF;
        int r = (int) (r1 + (r2 - r1) * t);
        int g = (int) (g1 + (g2 - g1) * t);
        int b = (int) (b1 + (b2 - b1) * t);
        return (r << 16) | (g << 8) | b;
    }

    static class Block {
        float x, y, z;
        Block(float x, float y, float z) { this.x = x; this.y = y; this.z = z; }
    }

    public Color getWorldColor(int id, float timeOfDay) {
    int hex = (id);
    
    int r = (hex >> 16) & 0xFF;
    int g = (hex >> 8) & 0xFF;
    int b = hex & 0xFF;
    float brightness = Math.max(0.0f, timeOfDay); 
    
    int finalR = (int)(r * brightness);
    int finalG = (int)(g * brightness);
    int finalB = (int)(b * (brightness + 0.1f));

    return new Color(Math.min(255, finalR), Math.min(255, finalG), Math.min(255, finalB));
}

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public BufferedImage getScreenBuffer() {
        return screenBuffer;
    }

    public void setScreenBuffer() {
        this.screenBuffer = screenBuffer;
    }

    public int[] getPixels() {
        return pixels;
    }

    public void setPixels(int[] pixels) {
        this.pixels = pixels;
    }

    public float[] getzBuffer() {
        return zBuffer;
    }

    public void setzBuffer(float[] zBuffer) {
        this.zBuffer = zBuffer;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getSplashText() {
        return splashText;
    }

    public void setSplashText(String splashText) {
        this.splashText = splashText;
    }

    public int getSkyColor() {
        return skyColor;
    }

    public float getFogNear() {
        return fogNear;
    }

    public float getFogFar() {
        return fogFar;
    }

    public float getCamX() {
        return camX;
    }

    public void setCamX(float camX) {
        this.camX = camX;
    }

    public float getCamY() {
        return camY;
    }

    public void setCamY(float camY) {
        this.camY = camY;
    }

    public float getCamZ() {
        return camZ;
    }

    public void setCamZ(float camZ) {
        this.camZ = camZ;
    }

    public float getRotY() {
        return rotY;
    }

    public void setRotY(float rotY) {
        this.rotY = rotY;
    }

    public boolean[] getKeys() {
        return keys;
    }

    public void setKeys(boolean[] keys) {
        this.keys = keys;
    }

    public int[] getTexture() {
        return texture;
    }

    public void setTexture(int[] texture) {
        this.texture = texture;
    }

    public int getTexW() {
        return texW;
    }

    public void setTexW(int texW) {
        this.texW = texW;
    }

    public int getTexH() {
        return texH;
    }

    public void setTexH(int texH) {
        this.texH = texH;
    }

    public List<Block> getWorld() {
        return world;
    }

    public void setWorld(List<Block> world) {
        this.world = world;
    }
}