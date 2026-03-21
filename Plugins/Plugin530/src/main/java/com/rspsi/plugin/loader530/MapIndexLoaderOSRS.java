package com.rspsi.plugin.loader530;

import com.displee.cache.index.Index;
import com.displee.cache.index.archive.Archive;
import com.jagex.cache.loader.map.MapIndexLoader;
import com.jagex.cache.loader.map.MapType;
import com.jagex.io.Buffer;
import com.rspsi.misc.RegionData;
import org.apache.commons.compress.utils.Lists;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class MapIndexLoaderOSRS extends MapIndexLoader {


	private int[] mapHashes, objects, landscapes;

	@Override
	public void init(Archive archive) {
		byte[] indices = archive.file("map_index").getData();
		Buffer buffer = new Buffer(indices);
		int count = buffer.readUShort();
		mapHashes = new int[count];
		landscapes = new int[count];
		objects = new int[count];
		int pos = 0;
		for (int region = 0; region < count; region++) {
			mapHashes[region] = buffer.readUShort();
			landscapes[region] = buffer.readUShort();
			objects[region] = buffer.readUShort();
			pos++;
		}
		System.out.println("expected regions " + count + " - actual " + pos);
	}

	@Override
	public void init(Buffer buffer) {
		int count = buffer.readUShort();
		mapHashes = new int[count];
		landscapes = new int[count];
		objects = new int[count];
		int pos = 0;
		for (int region = 0; region < count; region++) {
			mapHashes[region] = buffer.readUShort();
			landscapes[region] = buffer.readUShort();
			objects[region] = buffer.readUShort();
			pos++;
		}
		System.out.println("expected regions " + count + " - actual " + pos);
	}

	@Override
	public int getFileId(int hash, MapType type) {
		int index = IntStream.range(0, mapHashes.length)
				.filter(i -> hash == mapHashes[i])
				.findFirst()
				.orElse(-1);
		if(index >= 0) {
			return type == MapType.LANDSCAPE ? landscapes[index] : objects[index];
		}

		return -1;
	}

	@Override
	public boolean landscapePresent(int id) {
		return IntStream.range(0, mapHashes.length)
				.filter(i -> id == landscapes[i])
				.findFirst()
				.orElse(-1) >= 0;
	}

	@Override
	public boolean objectPresent(int id) {
		return IntStream.range(0, mapHashes.length)
				.filter(i -> id == objects[i])
				.findFirst()
				.orElse(-1) >= 0;
	}

	@Override
	public byte[] encode() {
		ByteBuffer buffer = ByteBuffer.allocate((mapHashes.length * 6) + 2);
		buffer.putShort((short) mapHashes.length);
		for(int index = 0;index<mapHashes.length;index++) {
			buffer.putShort((short) mapHashes[index]);
			buffer.putShort((short) landscapes[index]);
			buffer.putShort((short) objects[index]);
		}
		return buffer.array();
	}

	@Override
	public void set(int regionX, int regionY, int landscapeId, int objectsId) {
		int hash = (regionX << 8) | regionY;

		int index = IntStream.range(0, mapHashes.length)
				.filter(i -> hash == mapHashes[i])
				.findFirst()
				.orElse(-1);

		if (index >= 0) {
			landscapes[index] = landscapeId;
			objects[index] = objectsId;
			return;
		}

		int newSize = mapHashes.length + 1;

		int[] newMapHashes = Arrays.copyOf(mapHashes, newSize);
		int[] newLandscapes = Arrays.copyOf(landscapes, newSize);
		int[] newObjects = Arrays.copyOf(objects, newSize);

		int newIndex = newSize - 1;

		newMapHashes[newIndex] = hash;
		newLandscapes[newIndex] = landscapeId;
		newObjects[newIndex] = objectsId;

		this.mapHashes = newMapHashes;
		this.landscapes = newLandscapes;
		this.objects = newObjects;
	}

	public void init(Index mapIndex) {
		List<RegionData> regionData = Lists.newArrayList();
		for (int i = 0; i < 32768; i++) {
			int x = (i >> 8);
			int y = (i & 0xFF);

			Archive map = mapIndex.archive("m" + x + "_" + y);
			Archive land = mapIndex.archive("l" + x + "_" + y);

			RegionData data = new RegionData(i, map != null ? map.getId() : -1, land != null ? land.getId() : -1);

			if (data.getLoc() != -1 && data.getObj() != -1) {
				regionData.add(data);
			}
		}
		mapHashes = new int[regionData.size()];
		landscapes = new int[regionData.size()];
		objects = new int[regionData.size()];
		int index = 0;
		for (RegionData data : regionData) {
			mapHashes[index] = data.getHash();
			landscapes[index] = data.getLoc();
			objects[index] = data.getObj();
			index++;
		}
	}

}
