package tech.tenamen.client;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import tech.tenamen.Main;
import tech.tenamen.property.OS;
import tech.tenamen.util.NetUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class Client implements Downloadable {

    private String VERSION_NAME;
    private File JSON_FILE, JAR_FILE;

    private final List<Library> LIBRARIES = new ArrayList<>();
    public Asset asset;

    private int javaMajorVersion = -1;

    public Client(final File folder) throws Exception {
        if (!folder.isFile()) {
            final List<File> JSON_CANDIDATE = new ArrayList<>(), JAR_CANDIDATE = new ArrayList<>();
            // collect json and jar candidates
            for (final File f : Objects.requireNonNull(folder.listFiles())) {
                if (!f.isFile()) continue;
                final String upperFileName = f.getName().toUpperCase();
                if (upperFileName.endsWith(".JSON")) {
                    JSON_CANDIDATE.add(f);
                } else if (upperFileName.endsWith(".JAR")) {
                    JAR_CANDIDATE.add(f);
                }
            }
            for (File jsonCandidate : JSON_CANDIDATE) {
                final String jsonFileRawName = jsonCandidate.getName();
                final String jsonFileName = jsonFileRawName.substring(0, jsonFileRawName.length() - ".json".length());
                for (File jarCandidate : JAR_CANDIDATE) {
                    final String jarFileRawName = jarCandidate.getName();
                    final String jarFileName = jarFileRawName.substring(0, jarFileRawName.length() - ".jar".length());
                    if (jsonFileName.equalsIgnoreCase(jarFileName)) {
                        this.VERSION_NAME = jsonFileName;
                        this.JAR_FILE = jarCandidate;
                        this.JSON_FILE = jsonCandidate;
                        break;
                    }
                }
                if (JSON_FILE != null && JAR_FILE != null && VERSION_NAME != null) break;
            }
            if (JSON_FILE == null) throw new Exception("The folder doesn't have json");
            if (JAR_FILE == null) throw new Exception("The folder doesn't have jar");
            if (VERSION_NAME == null) throw new Exception("The name is null");
        } else {
            throw new Exception("The folder is not folder");
        }
    }

    public void parseDependencies() {
        JsonObject object = null;
        try {
            object = new GsonBuilder().setPrettyPrinting().create().fromJson(new FileReader(this.JSON_FILE), JsonObject.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (object == null) return;
        // arguments
        if (object.has("arguments")) {
            // TODO make launch property from json
            final JsonObject arguments = object.getAsJsonObject("arguments");
        }
        // asset index
        if (object.has("assetIndex")) {
            final JsonObject assetIndex = object.getAsJsonObject("assetIndex");
            String assetId = null, assetSha1 = null, assetUrl = null;
            long assetSize = -1, assetTutorialSize = -1;
            if (assetIndex.has("id")) assetId = assetIndex.get("id").getAsString();
            if (assetIndex.has("sha1")) assetSha1 = assetIndex.get("sha1").getAsString();
            if (assetIndex.has("url")) assetUrl = assetIndex.get("url").getAsString();
            if (assetIndex.has("size")) assetSize = assetIndex.get("size").getAsLong();
            if (assetIndex.has("totalSize")) assetTutorialSize = assetIndex.get("totalSize").getAsLong();
            if (assetId != null && assetSha1 != null && assetUrl != null && assetSize != -1 && assetTutorialSize != -1) {
                asset = new Asset(assetId, assetSha1, assetSize, assetTutorialSize, assetUrl);
            }
        }
        // downloads
        if (object.has("downloads")) {
            final JsonObject downloads = object.getAsJsonObject("downloads");
            if (downloads.has("client")) {
                final JsonObject client = downloads.getAsJsonObject("client");
                new Thread(() -> {
                    NetUtil.download(client.get("url").getAsString(), new File(Main.LIBRARIES_DIR, "client.jar"));
                }).start();
                Main.IDE.getLibraryNames().add("client.jar");
            }
            if (downloads.has("client_mappings")) {
                final JsonObject clientMappings = downloads.getAsJsonObject("client_mappings");
            }
            if (downloads.has("server")) {
                final JsonObject server = downloads.getAsJsonObject("server");
            }
            if (downloads.has("server_mappings")) {
                final JsonObject serverMappings = downloads.getAsJsonObject("server_mappings");
            }
        }
        // java version
        if (object.has("javaVersion")) {
            final JsonObject javaVersion = object.getAsJsonObject("javaVersion");
            // if (javaVersion.has("component"))
            if (javaVersion.has("majorVersion")) this.javaMajorVersion = javaVersion.get("majorVersion").getAsInt();
        }
        if (object.has("libraries")) {
            final JsonArray libraries = object.getAsJsonArray("libraries");
            for (JsonElement e : libraries) {
                String libName = null, libPath = null, libSha1 = null, libUrl = null;
                long libSize = -1;
                final OSRule osRule = new OSRule();
                final List<NativeLibrary> nativeLibraries = new ArrayList<>();
                final JsonObject library = e.getAsJsonObject();
                if (library.has("downloads")) {
                    final JsonObject downloads = library.getAsJsonObject("downloads");
                    if (downloads.has("artifact")) {
                        final JsonObject artifact = downloads.getAsJsonObject("artifact");
                        if (artifact.has("path")) libPath = artifact.get("path").getAsString();
                        if (artifact.has("sha1")) libSha1 = artifact.get("sha1").getAsString();
                        if (artifact.has("url")) libUrl = artifact.get("url").getAsString();
                        if (artifact.has("size")) libSize = artifact.get("size").getAsLong();
                    }
                    if (downloads.has("classifiers")) {
                        final JsonObject classifiers = downloads.getAsJsonObject("classifiers");
                        final Map<String, JsonObject> nativeObjects = new HashMap<>();
                        if (classifiers.has("natives-windows")) {
                            nativeObjects.put("natives-windows", classifiers.getAsJsonObject("natives-windows"));
                        }
                        if (classifiers.has("natives-linux")) {
                            nativeObjects.put("natives-linux", classifiers.getAsJsonObject("natives-linux"));
                        }
                        if (classifiers.has("natives-macos")) {
                            nativeObjects.put("natives-macos", classifiers.getAsJsonObject("natives-macos"));
                        }
                        nativeObjects.forEach((k, v) -> {
                            String nativePath = null, nativeSha1 = null, nativeUrl = null;
                            long nativeSize = -1;
                            if (v.has("path")) nativePath = v.get("path").getAsString();
                            if (v.has("sha1")) nativeSha1 = v.get("sha1").getAsString();
                            if (v.has("url")) nativeUrl = v.get("url").getAsString();
                            if (v.has("size")) nativeSize = v.get("size").getAsLong();
                            if (nativePath != null && nativeSha1 != null && nativeUrl != null && nativeSize != -1) {
                                nativeLibraries.add(new NativeLibrary(k, nativePath, nativeSha1, nativeSize, nativeUrl));
                            }
                        });
                    }
                }
                if (library.has("name")) {
                    libName = library.get("name").getAsString();
                }
                if (library.has("rules")) {
                    for (JsonElement r : library.getAsJsonArray("rules")) {
                        // os
                        final JsonObject rule = r.getAsJsonObject();
                        if (rule.has("action")) {
                            final String action = rule.get("action").getAsString();
                            osRule.setAllow(action.equalsIgnoreCase("allow"));
                        }
                        if (rule.has("os")) {
                            final JsonObject os = rule.getAsJsonObject("os");
                            if (os.has("name")) osRule.setOSName(os.get("name").getAsString());
                        }
                    }
                }
                if (libName != null && libPath != null && libSha1 != null && libUrl != null && libSize != -1) {
                    this.LIBRARIES.add(new Library(libName, libPath, libSha1, libSize, libUrl, osRule, nativeLibraries));
                }
            }
        }
        // release time
        if (object.has("releaseTime")) {
            final String releaseTime = object.get("releaseTime").getAsString();
        }
        // time
        if (object.has("time")) {
            final String time = object.get("time").getAsString();
        }
        // type
        if (object.has("type")) {
            final String type = object.get("type").getAsString();
        }
        Main.IDE.getLibraryNames().add("jsr305-3.0.2.jar");
    }

    public final String getVersionName() { return this.VERSION_NAME; }

    @Override
    public void download(OS os) {
        final List<Thread> threads = new ArrayList<>();
        this.LIBRARIES.forEach(l -> {
            threads.add(new Thread(() -> {
                l.download(os);
            }));
        });
        this.asset.download(os);
        threads.addAll(this.asset.threads);
        for (Thread thread : threads) {
            thread.run();
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public int getJavaMajorVersion() { return this.javaMajorVersion; }

}
