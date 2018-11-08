package spirograph.state;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import spirograph.Handler;
import spirograph.Spirograph;
import spirograph.ui.ClickListener;
import spirograph.ui.UIButton;
import spirograph.ui.UIManager;
import spirograph.ui.UISlider;

public class GameState extends State {

    private Spirograph s;
    private float speed = 10;
    private boolean running = false, hidden = false;
    private UIManager uiManager;
    private UISlider speedSlider;
    private ArrayList<UISlider> newSliders, sliders;
    private ArrayList<Spirograph> spirographs, newSpirographs;

    public GameState(Handler handler) {
        super(handler);
        speedSlider = new UISlider(handler, handler.getWidth() - handler.getWidth() / 4, 100, handler.getWidth() / 8, handler.getHeight() / 32);
        newSliders = new ArrayList();
        newSpirographs = new ArrayList();
        Spirograph s0 = new Spirograph(100, 1);
        newSpirographs.add(s0);
        s = new Spirograph(handler.getWidth() / 2, handler.getHeight() / 2, handler.getHeight() / 2, s0);
        newSpirographs.add(s);
    }

    @Override
    public void tick() {
        spirographs = (ArrayList<Spirograph>) newSpirographs.clone();
        sliders = (ArrayList<UISlider>) newSliders.clone();
        if (handler.getKeyManager().getJustPressed()[KeyEvent.VK_SPACE]) {
            running = !running;
        }
        if (handler.getKeyManager().getJustPressed()[KeyEvent.VK_BACK_SPACE]) {
            s.reset();
        }
        if (handler.getKeyManager().getJustPressed()[KeyEvent.VK_H]) {
            hidden = !hidden;
        }
        for (int i = 0; i < sliders.size(); i++) {
            spirographs.get(i).setR((sliders.get(i).getValue() / 2) * spirographs.get(i + 1).getR());
            spirographs.get(i + 1).setX(spirographs.get(i + 1).getX());
        }
        speed = (float) (1000 * speedSlider.getValue());
        spirographs.get(0).setApt(newSpirographs.get(0).getApt(), newSpirographs.get(newSpirographs.size() - 1));
        if (running) {
            for (int i = 0; i < speed; i++) {
                s.progress();
            }
        }
        uiManager.tick();
    }

    @Override
    public void render(Graphics g) {
        s.render(g, !hidden);
        uiManager.render(g);
    }

    @Override
    public void init() {
        uiManager = new UIManager(handler);
        UISlider slider = new UISlider(handler, handler.getWidth() / 32, handler.getHeight() / 2 - handler.getHeight() / 128, handler.getWidth() / 8, handler.getHeight() / 64);
        newSliders.add(slider);
        uiManager.addUIObject(slider);
        uiManager.addUIObject(new UIButton(handler.getWidth() / 32, handler.getHeight() - handler.getHeight() / 8, handler.getWidth() / 8, handler.getHeight() / 16, "+", new ClickListener() {
            @Override
            public void onClick() {
                UISlider slider = new UISlider(handler, handler.getWidth() / 32, 0, handler.getWidth() / 8, handler.getHeight() / 64);
                newSliders.add(slider);
                for (int i = 0; i < newSliders.size(); i++) {
                    newSliders.get(i).setY(handler.getHeight() / 2 - newSliders.size() * handler.getHeight() / 128 + i * handler.getHeight() / 64);
                }
                newSpirographs.remove(s);
                newSpirographs.add(new Spirograph(1, newSpirographs.get(newSpirographs.size() - 1)));
                s = new Spirograph(handler.getWidth() / 2, handler.getHeight() / 2, handler.getHeight() / 2, newSpirographs.get(newSpirographs.size() - 1));
                newSpirographs.add(s);
                for (int i = 0; i < newSliders.size(); i++) {
                    newSpirographs.get(i).setR((newSliders.get(i).getValue()) * newSpirographs.get(i + 1).getR());
                    newSpirographs.get(i + 1).setX(newSpirographs.get(i + 1).getX());
                }
                uiManager.addUIObject(slider);
                newSpirographs.get(0).setApt(newSpirographs.get(0).getApt(), newSpirographs.get(newSpirographs.size() - 1));
            }
        }));
        uiManager.addUIObject(new UIButton(handler.getWidth() / 32, handler.getHeight() - handler.getHeight() / 16, handler.getWidth() / 8, handler.getHeight() / 16, "-", new ClickListener() {
            @Override
            public void onClick() {
                if (newSpirographs.size() > 2) {
                    uiManager.removeUIObject(newSliders.get(newSliders.size() - 1));
                    newSliders.remove(newSliders.size() - 1);
                    for (int i = 0; i < newSliders.size(); i++) {
                        newSliders.get(i).setY(handler.getHeight() / 2 - newSliders.size() * handler.getHeight() / 128 + i * handler.getHeight() / 64);
                    }
                    newSpirographs.remove(s);
                    newSpirographs.remove(newSpirographs.size() - 1);
                    s = new Spirograph(handler.getWidth() / 2, handler.getHeight() / 2, handler.getHeight() / 2, newSpirographs.get(newSpirographs.size() - 1));
                    newSpirographs.add(s);
                    for (int i = 0; i < newSliders.size(); i++) {
                        newSpirographs.get(i).setR((newSliders.get(i).getValue()) * newSpirographs.get(i + 1).getR());
                        newSpirographs.get(i + 1).setX(newSpirographs.get(i + 1).getX());
                    }
                    newSpirographs.get(0).setApt(newSpirographs.get(0).getApt(), newSpirographs.get(newSpirographs.size() - 1));
                }
            }
        }));
        uiManager.addUIObject(speedSlider);
        handler.getMouseManager().setUIManager(uiManager);
    }

    public double getAngle(double x, double y) {
        double a;
        if (x == 0) {
            if (y > 0) {
                a = Math.PI / 2;
            } else {
                a = -Math.PI / 2;
            }
        } else if (y == 0) {
            if (x > 0) {
                a = 0;
            } else {
                a = Math.PI;
            }
        } else {
            a = Math.atan(y / x);
            if (x < 0) {
                a += Math.PI;
            }
        }
        return a;
    }

}
