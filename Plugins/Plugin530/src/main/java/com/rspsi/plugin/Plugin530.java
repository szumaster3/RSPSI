package com.rspsi.plugin;

import com.displee.cache.CacheLibrary;
import com.displee.cache.index.Index;

import com.jagex.Client;
import com.jagex.cache.loader.anim.FrameLoader;
import com.jagex.cache.loader.anim.GraphicLoader;
import com.jagex.cache.loader.config.RSAreaLoader;
import com.jagex.cache.loader.config.VariableBitLoader;
import com.jagex.cache.loader.map.MapIndexLoader;
import com.jagex.cache.loader.object.ObjectDefinitionLoader;
import com.jagex.cache.loader.textures.TextureLoader;
import com.jagex.net.ResourceResponse;
import com.rspsi.cache.CacheFileType;
import com.rspsi.plugin.loader530.AnimationDefLoader;
import com.rspsi.plugin.loader530.FloorDefLoader;
import com.rspsi.plugin.loader530.AnimationSkinLoader;
import com.rspsi.plugin.loader530.AnimationFrameLoader;
import com.rspsi.plugin.loader530.SpotAnimationLoader;
import com.rspsi.plugin.loader530.MapIndexLoaderOSRS;
import com.rspsi.plugin.loader530.ObjectDefLoader;
import com.rspsi.plugin.loader530.RSAreaLoaderOSRS;
import com.rspsi.plugin.loader530.TextureLoaderOSRS;
import com.rspsi.plugin.loader530.VarbitLoader;
import com.rspsi.plugins.ClientPlugin;

import java.util.Objects;
public class Plugin530 implements ClientPlugin {

    private AnimationFrameLoader frameLoader;
    private FloorDefLoader floorLoader;
    private ObjectDefLoader objLoader;
    private AnimationDefLoader animDefLoader;
    private SpotAnimationLoader graphicLoader;
    private VarbitLoader varbitLoader;
    private MapIndexLoaderOSRS mapIndexLoader;
    private TextureLoaderOSRS textureLoader;
    private AnimationSkinLoader skeletonLoader;
    private RSAreaLoaderOSRS areaLoader;

    @Override
    public void initializePlugin() {
        objLoader = new ObjectDefLoader();
        floorLoader = new FloorDefLoader();
        frameLoader = new AnimationFrameLoader();
        animDefLoader = new AnimationDefLoader();

        mapIndexLoader = new MapIndexLoaderOSRS();
        textureLoader = new TextureLoaderOSRS();
        skeletonLoader = new AnimationSkinLoader();
        graphicLoader = new SpotAnimationLoader();
        varbitLoader = new VarbitLoader();
        areaLoader = new RSAreaLoaderOSRS();

        MapIndexLoader.instance = mapIndexLoader;
        GraphicLoader.instance = graphicLoader;
        VariableBitLoader.instance = varbitLoader;
        FrameLoader.instance = frameLoader;
        ObjectDefinitionLoader.instance = objLoader;
        com.jagex.cache.loader.floor.FloorDefinitionLoader.instance = floorLoader;
        com.jagex.cache.loader.anim.FrameBaseLoader.instance = skeletonLoader;
        TextureLoader.instance = textureLoader;
        com.jagex.cache.loader.anim.AnimationDefinitionLoader.instance = animDefLoader;
        RSAreaLoader.instance = areaLoader;
    }

    @Override
    public void onGameLoaded(Client client) {

        frameLoader.init(2500);

        Index configIndex = client.getCache().readFile(CacheFileType.CONFIG);

        floorLoader.decodeUnderlays(Objects.requireNonNull(configIndex.archive(1)));
        floorLoader.decodeOverlays(Objects.requireNonNull(configIndex.archive(4)));
        Index varbitIndex = client.getCache().readFile(CacheFileType.VARBIT);
        varbitLoader.decodeVarbits(varbitIndex);
        objLoader.decodeObjects(client.getCache().getCacheLibrary().index(16));
		animDefLoader.init(configIndex.archive(12));
		graphicLoader.init(configIndex.archive(13));
//      areaLoader.init(configIndex.archive(35));
//		Index skeletonIndex = client.getCache().readFile(CacheFileType.SKELETON);
//		skeletonLoader.init(skeletonIndex);

        CacheLibrary cache = client.getCache().getCacheLibrary();
        mapIndexLoader.init(cache);
    }

    @Override
    public void onResourceDelivered(ResourceResponse arg0) {
        // TODO Auto-generated method stub

    }

}
