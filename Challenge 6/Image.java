package circledetector;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Image {

    private float[][] r, g, b;
    private int[][] finalCValues;
    private double[][][] cValues;
    private BufferedImage image, bw, edges, circles;
    public static int MIN_R = 8, MAX_R = 200, SCALE = 2;

    public Image(BufferedImage image) {
        this.image = image;
        MAX_R = Math.min(image.getHeight() / 2 / SCALE, image.getWidth() / 2 / SCALE);
        bw = copyImage(image);
        int[][] values = new int[image.getWidth()][image.getHeight()];
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color pixel = new Color(image.getRGB(x, y));
                values[x][y] = (pixel.getRed() + pixel.getGreen() + pixel.getBlue()) / 3;
                Color newP = new Color(values[x][y], values[x][y], values[x][y]);
                bw.setRGB(x, y, newP.getRGB());
            }
        }
        edges = findEdges(scaleImage(image,0.5));
        circles = findCircles(scaleImage(image,1.0/SCALE));
    }

    public BufferedImage findEdges(BufferedImage image) {
        //Black and White
        BufferedImage bw = copyImage(image);
        int[][] values = new int[image.getWidth()][image.getHeight()];
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color pixel = new Color(image.getRGB(x, y));
                values[x][y] = (pixel.getRed() + pixel.getGreen() + pixel.getBlue()) / 3;
                Color newP = new Color(values[x][y], values[x][y], values[x][y]);
                bw.setRGB(x, y, newP.getRGB());
            }
        }
        BufferedImage edges = copyImage(bw);
        //Verticle changes
        int[][] vValues = new int[image.getWidth()][image.getHeight()];
        int[][] m = new int[][]{new int[]{1, 0, -1}, new int[]{2, 0, -2}, new int[]{1, 0, -1}};
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color pixel = new Color(image.getRGB(x, y));
                int[][] vs = new int[3][3];
                for (int mx = -1; mx < 2; mx++) {
                    for (int my = -1; my < 2; my++) {
                        vs[mx + 1][my + 1] = values[Math.max(0, Math.min(image.getWidth() - 1, x + mx))][Math.max(0, Math.min(image.getHeight() - 1, y + my))];
                    }
                }
                for (int mx = 0; mx < 3; mx++) {
                    for (int my = 0; my < 3; my++) {
                        vValues[x][y] += vs[mx][my] * m[mx][my];
                    }
                }
                vValues[x][y] /= 4.5;
            }
        }
        //horizontal changes
        int[][] hValues = new int[image.getWidth()][image.getHeight()];
        m = new int[][]{new int[]{1, 2, 1}, new int[]{0, 0, 0}, new int[]{-1, -2, -1}};
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color pixel = new Color(image.getRGB(x, y));
                int[][] vs = new int[3][3];
                for (int mx = -1; mx < 2; mx++) {
                    for (int my = -1; my < 2; my++) {
                        vs[mx + 1][my + 1] = values[Math.max(0, Math.min(image.getWidth() - 1, x + mx))][Math.max(0, Math.min(image.getHeight() - 1, y + my))];
                    }
                }
                for (int mx = 0; mx < 3; mx++) {
                    for (int my = 0; my < 3; my++) {
                        hValues[x][y] += vs[mx][my] * m[mx][my];
                    }
                }
                hValues[x][y] /= 4.5;
            }
        }
        //combine
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                values[x][y] = (int) Math.sqrt(vValues[x][y] * vValues[x][y] + hValues[x][y] * hValues[x][y]);
            }
        }
        //set colors
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color newP = new Color(values[x][y], values[x][y], values[x][y]);
                edges.setRGB(x, y, newP.getRGB());
            }
        }
        return edges;
    }

    public BufferedImage findCircles(BufferedImage image) {
        finalCValues = new int[image.getWidth()][image.getHeight()];
        BufferedImage circles = copyImage(findEdges(image));
        System.out.println(MIN_R + " -> " + MAX_R);
        double[][][] values = new double[image.getWidth()][image.getHeight()][MAX_R - MIN_R];
        float[][] pixel = new float[image.getWidth()][image.getHeight()];
        for (int r = MIN_R; r < MAX_R; r++) {
            System.out.println(r);
            for (int x = r; x < image.getWidth() - r; x++) {
                for (int y = r; y < image.getHeight() - r; y++) {
                    //midpoint algorithm
                    int total = 0;
                    int count = 0;
                    int nx = r - 1;
                    int ny = 0;
                    int dx = 1, dy = 1;
                    int err = dx - r * 2;
                    while (nx > ny) {
                        count++;
                        total += getColorValue(circles.getRGB(x + nx, y + ny));
                        total += getColorValue(circles.getRGB(x + nx, y - ny));
                        total += getColorValue(circles.getRGB(x - nx, y + ny));
                        total += getColorValue(circles.getRGB(x - nx, y - ny));
                        total += getColorValue(circles.getRGB(x + ny, y + nx));
                        total += getColorValue(circles.getRGB(x + ny, y - nx));
                        total += getColorValue(circles.getRGB(x - ny, y + nx));
                        total += getColorValue(circles.getRGB(x - ny, y - nx));
                        if (err <= 0) {
                            ny++;
                            err += dy;
                            dy += 2;
                        }
                        if (err > 0) {
                            nx--;
                            dx += 2;
                            err += dx - r * 2;
                        }
                    }
                    //average value
                    values[x][y][r - MIN_R] = total / count / 255.0 / 4.0;
                    if (values[x][y][r - MIN_R] > 0.7 && finalCValues[x][y] == 0 && finalCValues[x + 1][y] != r && finalCValues[x + 1][y + 1] != r && finalCValues[x][y + 1] != r) {
                        //circle
                        pixel[x][y] = 1f;
                        finalCValues[x][y] = r;
                    }
                }
            }
        }
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                circles.setRGB(x, y, new Color(pixel[x][y], pixel[x][y], pixel[x][y]).getRGB());
            }
        }
        cValues = values;
        return circles;
    }

    private int getColorValue(int rgb) {
        return new Color(rgb).getBlue();
    }

    public static BufferedImage copyImage(BufferedImage source) {
        BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        Graphics g = b.getGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return b;
    }

    public static BufferedImage scaleImage(BufferedImage source, double scale) {
        BufferedImage b = new BufferedImage((int) (source.getWidth() * scale), (int) (source.getHeight() * scale), source.getType());
        Graphics g = b.getGraphics();
        g.drawImage(source, 0, 0, (int) (source.getWidth() * scale), (int) (source.getHeight() * scale), null);
        g.dispose();
        return b;
    }

    public BufferedImage getImage() {
        return image;
    }

    public BufferedImage getBw() {
        return bw;
    }

    public BufferedImage getEdges() {
        return edges;
    }

    public BufferedImage getCircles() {
        return circles;
    }

    public double[][][] getcValues() {
        return cValues;
    }

    public int[][] getFinalCValues() {
        return finalCValues;
    }

}
