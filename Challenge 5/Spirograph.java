package spirograph;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class Spirograph {

    private ArrayList<Point2D> points;
    private double x, y, r, apt, a;
    private Spirograph child;
    private int dir = 1;

    public Spirograph(double r, Spirograph child) { //child+parent
        this.r = r;
        this.child = child;
        apt = child.r / r * child.apt;
        dir = -child.dir;
        points = new ArrayList<>();
    }

    public Spirograph(double x, double y, double r, Spirograph child) { //parent
        setPos(x, y);
        this.r = r;
        this.child = child;
        apt = child.r / r * child.apt;
        dir = -child.dir;
        points = new ArrayList<>();
        child.setX(getPoint(x, y, r - child.getR(), a).x);
        child.setY(getPoint(x, y, r - child.getR(), a).y);
        child.setR(child.getR());
    }

    public Spirograph(double r, double apt) { //child
        this.r = r;
        this.apt = apt / r;
        points = new ArrayList<>();
    }

    public void progress() {
        a += dir * apt;
        if (child != null) {
            points.add(getPoint(x, y, 0, a));
            child.setX(getPoint(x, y, r - child.getR(), a).x);
            child.setY(getPoint(x, y, r - child.getR(), a).y);
            child.progress();
        } else {
            points.add(getPoint(x, y, r - 10, a));
        }
    }

    public void render(Graphics g, boolean show) {
        if (child == null) {
            g.setColor(Color.white);
            for (int i = 0; i < points.size() - 1; i++) {
                Point2D p0 = points.get(i);
                Point2D p1 = points.get(i + 1);
                g.drawLine((int) p0.x, (int) p0.y, (int) p1.x, (int) p1.y);
            }
            g.fillOval((int) getPoint(x, y, r - 10, a).x, (int) getPoint(x, y, r - 10, a).y, 10, 10);
        } else {
            child.render(g, show);
        }
        if (show) {
            g.setColor(Color.white.darker().darker());
            g.drawOval((int) (x - r), (int) (y - r), (int) (r * 2), (int) (r * 2));
        }
    }

    private Point2D getPoint(double x, double y, double r, double a) {
        double px = x + r * Math.cos(a);
        double py = y + r * Math.sin(a);
        return new Point2D(px, py);
    }

    public double getX() {
        return x;
    }

    public void setPos(double x, double y) {
        this.x = x;
        this.y = y;
        if (child != null) {
            double cx = getPoint(x, y, r - child.getR(), a).x;
            double cy = getPoint(x, y, r - child.getR(), a).y;
            child.setR(child.getR());
            child.setPos(cx, cy);
        }
    }

    public int getHeight() {
        if (child == null) {
            return 0;
        } else {
            return child.getHeight() + 1;
        }
    }

    public void reset() {
        points = new ArrayList<>();
        if (child != null) {
            child.setX(getPoint(x, y, r - child.getR(), a).x);
            child.setY(getPoint(x, y, r - child.getR(), a).y);
            child.reset();
        }
    }

    public void setX(double x) {
        this.x = x;
        if (child != null) {
            child.setX(getPoint(x, y, r - child.getR(), a).x);
        }
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
        if (child != null) {
            child.setY(getPoint(x, y, r - child.getR(), a).y);
        }
    }

    public double getR() {
        return r;
    }

    public void setR(double r) {
        this.r = r;
        if (child != null) {
            apt = child.r / r * child.apt;
        }
    }

    public double getApt() {
        return apt;
    }

    public void setApt(double apt) {
        this.apt = apt;
    }
    
    public void setApt(double apt, Spirograph s) {
        this.apt = apt;
        s.setR(s.getR());
    }

    public void setDir(int dir) {
        this.dir = dir;
    }

}
