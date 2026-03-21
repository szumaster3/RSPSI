package com.jagex.map;

import java.util.ArrayList;
import java.util.List;

public class WorldMapData {

    public String debugName;
    public String name;

    public int origin;
    public int background;
    public boolean listed;

    public int zoom = -1;
    public int buildArea = 0;

    public List<WorldMapLabel> labels = new ArrayList<>();
    public List<WorldMapElement> elements = new ArrayList<>();
}