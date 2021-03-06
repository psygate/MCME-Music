package com.mcmiddleearth.mcmemusic.util;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.mcmiddleearth.mcmemusic.Main;
import com.mcmiddleearth.mcmemusic.file.JSONFile;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class LoadRegion {

    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private Main main;
    private JSONFile jsonFile;
    public Logger log = Bukkit.getLogger();
    public HashMap<Polygonal2DRegion, Integer> allRegions = new HashMap<>();

    public LoadRegion(Main main, JSONFile jsonFile){
        this.main = main;
        this.jsonFile = jsonFile;
    }

    public void loadRegion() throws Exception {
        File dataFolder = new File(String.valueOf(main.getDataFolder()));
        File[] files = dataFolder.listFiles();

        for(File f: files){
            if(f.getName().contains(".json")){
                ArrayList<String> pointList = new ArrayList<String>();
                JsonObject json = (JsonObject) jsonFile.readJson(f.getName());
                JsonPrimitive jsonID = json.getAsJsonPrimitive("musicID");

                //Get points
                JsonArray points = json.getAsJsonArray("points");

                if(points != null){
                    for(int i=0;i<points.size();i++){
                        pointList.add(String.valueOf(points.get(i)));
                    }
                }

                //Get musicID
                int musicID = jsonID.getAsInt();

                //Log
                log.info("Test log");
                log.info("File:" + f.getName());
                log.info("Points:" + pointList);
                log.info("Music ID:" + musicID);

                //Create Region
                Polygonal2DRegion region = new Polygonal2DRegion();

                for(int i=0;i<pointList.size();i++){
                    //Add point region
                    int x;
                    int z;

                    //Deserialize
                    int firstIndex = pointList.get(i).indexOf(":");
                    int lastIndex = pointList.get(i).lastIndexOf(":");
                    int secondIndex = pointList.get(i).indexOf(":", firstIndex + 1);
                    String world = pointList.get(i).substring(1, firstIndex);
                    String stringX = pointList.get(i).substring(firstIndex+1, secondIndex);
                    String stringZ = pointList.get(i).substring(lastIndex+1, pointList.get(i).length()-1);

                    log.info(world);

                    //Set variables to final values
                    x = Integer.parseInt(stringX);
                    z = Integer.parseInt(stringZ);
                    World bukkitWorld = Bukkit.getServer().getWorld(world);
                    com.sk89q.worldedit.world.World regionWorld = BukkitAdapter.adapt(bukkitWorld);
                    BlockVector2 pointLoc = BlockVector2.at(x,z);

                    //Add Point and Set World
                    region.setWorld(regionWorld);
                    region.addPoint(pointLoc);
                }

                allRegions.put(region, musicID);

            }
        }
        log.info(String.valueOf(allRegions));
    }

    public HashMap<Polygonal2DRegion, Integer> getRegionsMap(){
        return allRegions;
    }

}