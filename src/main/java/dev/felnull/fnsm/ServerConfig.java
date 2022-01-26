package dev.felnull.fnsm;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.*;
import java.nio.file.Files;

public class ServerConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static String JMS_URL;
    private static String MONOCRAFT_URL;

    public static void init() {
        File cf = FelNullServerMOD.getModFolder().resolve("config.json").toFile();
        if (!cf.exists()) {
            JsonObject jo = new JsonObject();

            JsonObject vss = new JsonObject();
            vss.addProperty("JMS", "");
            vss.addProperty("Monocraft", "");
            jo.add("VoteServiceURL", vss);
            try {
                cf.getParentFile().mkdirs();
                Files.write(cf.toPath(), GSON.toJson(jo).getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            JsonObject jo;
            try {
                jo = GSON.fromJson(new BufferedReader(new FileReader(cf)), JsonObject.class);
            } catch (FileNotFoundException e) {
                return;
            }
            JsonObject vss = jo.getAsJsonObject("VoteServiceURL");
            JMS_URL = vss.get("JMS").getAsString();
            MONOCRAFT_URL = vss.get("Monocraft").getAsString();
        }
    }

    public static String getJmsUrl() {
        return JMS_URL;
    }

    public static String getMonocraftUrl() {
        return MONOCRAFT_URL;
    }
}
