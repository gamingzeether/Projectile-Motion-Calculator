/*
 * https://stackoverflow.com/a/299555
 */

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.JPanel;

public class ImagePanel extends JPanel{

    private BufferedImage image;
    private Graphics2D graphics;
    private ArrayList<Shape> shapes = new ArrayList<>();
    int pixelWidth;
    int pixelHeight;

    public ImagePanel(int x, int y) {
        image = new BufferedImage(x, y, BufferedImage.TYPE_INT_RGB);
        graphics = image.createGraphics();
        drawRectangle(new Vector2Int(0, 0), new Vector2Int(x, y), Color.white);
        pixelWidth = x;
        pixelHeight = y;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, this); // see javadoc for more info on the parameters            
    }

    public void clearShapes() {
        shapes.clear();
    }
    public void addShape(Shape s) {
        shapes.add(s);
    }
    public void redraw() {
        int w = image.getWidth();
        int h = image.getHeight();
        drawRectangle(new Vector2Int(0, 0), new Vector2Int(w, h), Color.white);
        for (Shape s : shapes) {
            s.drawShape(this);
        }
        this.updateUI();
    }
    public void drawLine(Vector2Int p1, Vector2Int p2, double width, Color c) {
        graphics.setColor(c);
        graphics.setStroke(new BasicStroke((float) width));
        graphics.drawLine(p1.x, pixelHeight - p1.y, p2.x, pixelHeight - p2.y);
        graphics.setStroke(new BasicStroke());
    }
    public void drawRectangle(Vector2Int p1, Vector2Int p2, Color color) {
        graphics.setColor(color);
        graphics.fillRect(p1.x, p1.y, p2.x - p1.x, p2.y - p1.y);
    }
    public void drawCircle(Vector2Int center, double radius, double radiusInner, Color color) {
        graphics.setColor(color);
        float strokeWidth = (float)(radius - radiusInner);
        int realRadius =  (int)(radius - strokeWidth / 2);
        graphics.setStroke(new BasicStroke(strokeWidth));
        graphics.drawOval(center.x - realRadius, pixelHeight - center.y - realRadius, realRadius * 2, realRadius * 2);
        graphics.setStroke(new BasicStroke());
    }
    public void drawText(Vector2Int position, String text, Font font, Color color) {
        graphics.setFont(font);
        graphics.setColor(color);
        graphics.drawChars(text.toCharArray(), 0, text.length(), position.x, pixelHeight - position.y);
    }
    public void onMouseMove(int mouseX, int mouseY) {
        mouseY = pixelHeight - mouseY;
        Trajectory selectedTrajectory = null;
        int selectedDist = 20;
        for (Shape s : shapes) {
            if (s instanceof  Trajectory) {
                Trajectory trajectory = (Trajectory) s;
                FiringSolution fs = trajectory.firingSolution;
                double x = (mouseX - trajectory.launchPos.x) * trajectory.metersPerPixel;
                double maxX = fs.speed * fs.travelTime * Math.cos(fs.elevation);
                if (x >= 0 && x <= maxX) {
                    int trajectoryY = (int) (fs.yAtX(x) / trajectory.metersPerPixel) + trajectory.launchPos.y;
                    int dist = Math.abs(trajectoryY - mouseY);
                    if (dist < selectedDist) {
                        selectedDist = dist;
                        selectedTrajectory = trajectory;
                    }
                }
            }
        }
        if (selectedTrajectory == null) {
            Main.outputLabelSolution.setText("Firing Solution:");
        } else {
            Main.outputLabelSolution.setText("Firing Solution: " + selectedTrajectory.firingSolution.toString());
        }
    }
}

class Shape {
    public Shape() {}

    public void drawShape(ImagePanel panel) {}
}
class Line extends Shape {
    Vector2Int point1;
    Vector2Int point2;
    double width;
    Color color;

    public Line(Vector2Int p1, Vector2Int p2, double w, Color c) {
        point1 = p1;
        point2 = p2;
        width = w;
        color = c;
    }

    public void drawShape(ImagePanel panel) {
        panel.drawLine(point1, point2, width, color);
    }
}

class Circle extends Shape {
    Vector2Int center;
    double radius;
    double radiusInner;
    Color color;

    public Circle(Vector2Int p1, double r, Color c) {
        center = p1;
        radius = r;
        radiusInner = 0;
        color = c;
    }public Circle(Vector2Int p1, double r, double ir, Color c) {
        center = p1;
        radius = r;
        radiusInner = ir;
        color = c;
    }

    public void drawShape(ImagePanel panel) {
        panel.drawCircle(center, radius, radiusInner, color);
    }
}
class Trajectory extends Shape {
    FiringSolution firingSolution;
    Vector2Int launchPos;
    double metersPerPixel;

    public Trajectory(FiringSolution fs, Vector2Int lp, double mp) {
        firingSolution = fs;
        launchPos = lp;
        metersPerPixel = mp;
    }

    public void drawShape(ImagePanel panel) {
        //TODO: redo drawing

        // Draw arc using equations:
        // x = v * t * cos e
        // y = s * t * sin e - 1/2 * g * t^2

        double timeStep = 0.05;
        int steps = (int) (Math.ceil(firingSolution.travelTime) / timeStep);
        Vector2Int prevPt = launchPos;
        for (int i = 1; i <= steps; i++) {
            double t = Math.min(firingSolution.travelTime, timeStep * i);
            double x = firingSolution.speed * t * Math.cos(firingSolution.elevation);
            double y = (firingSolution.speed * t * Math.sin(firingSolution.elevation)) - ((firingSolution.gravity * t * t) / 2);

            Vector2Int curPt = new Vector2Int(launchPos.x + (int) (x / metersPerPixel), launchPos.y + (int) (y / metersPerPixel));
            panel.drawLine(prevPt, curPt, 2, Color.red);
            prevPt = curPt;
        }
    }
}
class Text extends Shape {
    Vector2Int position;
    String text;
    Font font;
    Color color;

    public Text(Vector2Int pos, String t, Font f, Color c) {
        position = pos;
        text = t;
        font = f;
        color = c;
    }
    public void drawShape(ImagePanel panel) {
        panel.drawText(position, text, font, color);
    }
}
