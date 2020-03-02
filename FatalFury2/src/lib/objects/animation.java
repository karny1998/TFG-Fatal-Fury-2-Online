package lib.objects;

import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

//Animación representada por sus frames, cambios de posición
//y tiempos de transición.
public class animation {
    List<Image> frames = new ArrayList<Image>();
    List<Pair<Integer, Integer>> coords = new ArrayList<Pair<Integer, Integer>>();
    List<Image> times = new ArrayList<Image>();
    Boolean unstoppable = false;

    public List<Image> getFrames() {
        return frames;
    }

    public void setFrames(List<Image> frames) {
        this.frames = frames;
    }

    public List<Pair<Integer, Integer>> getCoords() {
        return coords;
    }

    public void setCoords(List<Pair<Integer, Integer>> coords) {
        this.coords = coords;
    }

    public List<Image> getTimes() {
        return times;
    }

    public void setTimes(List<Image> times) {
        this.times = times;
    }

    public Boolean getUnstoppable() {
        return unstoppable;
    }

    public void setUnstoppable(Boolean unstoppable) {
        this.unstoppable = unstoppable;
    }
}
