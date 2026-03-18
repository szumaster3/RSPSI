package com.rspsi.plugin.loader530;

import com.displee.cache.CacheLibrary;
import com.displee.cache.index.Index;
import com.displee.cache.index.archive.Archive;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import com.rspsi.misc.XTEAManager;
import org.apache.commons.compress.utils.Lists;

import com.jagex.cache.loader.map.MapIndexLoader;
import com.jagex.cache.loader.map.MapType;
import com.jagex.io.Buffer;
import com.rspsi.misc.RegionData;

public class MapIndexLoaderOSRS extends MapIndexLoader {


	private int[] mapHashes, objects, landscapes;

	@Override
	public void init(Archive archive) {
		com.displee.cache.index.archive.file.File mapIndexFile = archive.file("map_index");
		if (mapIndexFile == null) {
			throw new RuntimeException("map_index not found in archive " + archive.getId());
		}
		byte[] indices = mapIndexFile.getData().clone();
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
		int hash = (regionX << 8) + regionY;
		int index = IntStream.range(0, mapHashes.length)
				.filter(i -> hash == mapHashes[i])
				.findFirst()
				.orElse(-1);
		if(index >= 0) {
			System.out.println("Setting index " + index);
			landscapes[index] = landscapeId;
			objects[index] = objectsId;
		} else {
			System.out.println("Adding new index");
			int[] mapHashes = Arrays.copyOf(this.mapHashes, this.landscapes.length + 1);
			int[] landscapes = Arrays.copyOf(this.landscapes, this.landscapes.length + 1);
			int[] objects = Arrays.copyOf(this.objects, this.landscapes.length + 1);
			index = mapHashes.length - 1;
			mapHashes[index] = hash;
			landscapes[index] = landscapeId;
			objects[index] = objectsId;

			this.mapHashes = mapHashes;
			this.landscapes = landscapes;
			this.objects = objects;
		}

	}

	public void init(CacheLibrary cache) {

		List<RegionData> regionData = new ArrayList<>();
		Index mapIndex = cache.index(5);

		for (int i = 0; i < 32768; i++) {

			int x = i >> 8;
			int y = i & 0xFF;

			int mapId = -1;
			int landId = -1;

			try {
				Archive map = mapIndex.archive("m" + x + "_" + y);
				if (map != null)
					mapId = map.getId();
			} catch (Exception ignored) {}

			try {
				Archive land = mapIndex.archive("l" + x + "_" + y);
				if (land != null)
					landId = land.getId();
			} catch (Exception ignored) {}

			if (mapId == -1 && landId == -1)
				continue;

			regionData.add(new RegionData(i, mapId, landId));
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

		System.out.println("Loaded " + regionData.size() + " regions from Index 5");
	}

}
