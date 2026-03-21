package com.jagex.map;

import com.displee.cache.CacheLibrary;
import com.jagex.io.Buffer;

public class WorldMapEncoder {

    public static void save(CacheLibrary cache, int archiveId, WorldMapData data) {
        cache.put(23, archiveId, buildDetails(data));
        for (WorldMapElement e : data.elements) {
            cache.put(23, archiveId, buildElement(e));
        }
        for (WorldMapLabel l : data.labels) {
            cache.put(23, archiveId, buildLabel(l));
        }
        cache.update();
    }


    public static byte[] buildDetails(WorldMapData data) {
        Buffer buffer = Buffer.create();
        buffer.writeJString(data.debugName);
        buffer.writeJString(data.name);
        buffer.writeInt(data.origin);
        buffer.writeInt(data.background);
        buffer.writeByte(data.listed ? 1 : 0);
        buffer.writeByte(data.zoom == -1 ? 255 : data.zoom);
        buffer.writeByte(data.buildArea);
        buffer.writeByte(0);
        return buffer.getPayload();
    }

    public static byte[] buildElement(WorldMapElement e) {
        Buffer buffer = Buffer.create();
        buffer.writeInt(e.coord);
        buffer.writeShort(e.mapElement);
        buffer.writeByte(e.flag);
        return buffer.getPayload();
    }

    public static byte[] buildLabel(WorldMapLabel l) {
        Buffer buffer = Buffer.create();
        buffer.writeInt(l.coord);
        buffer.writeShort(l.mapElement);
        buffer.writeByte(l.flag);
        return buffer.getPayload();
    }
}