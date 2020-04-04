package ru.buseso.spigot.free.reputation.updates;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ru.buseso.spigot.free.reputation.Reputation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class Updater {
    //Base GitHubAPI URL
    public static final String BASE_URL = "https://api.github.com/";
    //Using for parsing input responses
    public static final Gson GSON = new Gson();

    //Plugin version
    private int currentVersion = 0;
    //GitHub repo
    private final String repositoryUrl;

    public Updater(String user, String repository) {
        this.repositoryUrl = "/".concat(user.concat("/").concat(repository).concat("/"));
    }

    public UpdateResult checkUpdates() throws UpdateException {
        if(this.currentVersion == 0) currentVersion = parseVersion();

        try {
            URL url = new URL(BASE_URL.concat("repos").concat(repositoryUrl).concat("commits"));
            JsonObject[] objects = GSON.fromJson(
                    new BufferedReader(
                            new InputStreamReader(url.openStream(), Charsets.UTF_8.name())
                    ),
                    JsonObject[].class);

            if(objects.length == 0) {
                throw new UpdateException("commits is empty", this);
            }

            JsonObject object = objects[0];

            String[] toParse = object.get("commit").getAsJsonObject().get("message").getAsString().replace(".","").split("\\n");
            if(toParse.length == 0) {
                throw new UpdateException("commits is empty", this);
            }

            int version = Integer.parseInt(toParse[0]);

            if(version != currentVersion) {
                return UpdateResult.UPDATE_FOUND;
            }
        }catch (IOException | NumberFormatException ignore) { }

        return UpdateResult.UPDATE_NOT_FOUND;
    }

    private int parseVersion() {
        try {
            return Reputation.getVersion();
        }catch (NumberFormatException ignored) { }
        return -1;
    }

    public int getCurrentVersion() {
        return currentVersion;
    }
}
