package tech.tenamen.client;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import tech.tenamen.Main;
import tech.tenamen.property.OS;
import tech.tenamen.util.NetUtil;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class Asset implements Downloadable {

    private final String ID;
    private final String SHA1;
    private final long SIZE;
    private final long TOTAL_SIZE;
    private final String URL;

    public Asset(final String ID, final String SHA1, final long SIZE, final long TOTAL_SIZE, final String URL) {
        this.ID = ID;
        this.SHA1 = SHA1;
        this.SIZE = SIZE;
        this.TOTAL_SIZE = TOTAL_SIZE;
        this.URL = URL;
    }

    @Override
    public void download(OS os) {
        JsonObject object = null;
        object = new GsonBuilder().setPrettyPrinting().create().fromJson(new StringReader(NetUtil.download(this.URL)), JsonObject.class);
        if (object == null) return;
        List<Resource> resources = new ArrayList<>();
        if (object.has("objects")) {
            final JsonObject objects = object.getAsJsonObject("objects");
            int tabIndex = 0;
            final StringBuilder tempBuilder = new StringBuilder();
            final String raw = objects.toString();
            for (int i = 0, l = raw.length(); i < l; i++) {
                final char c = raw.charAt(i);
                switch (c) {
                    case '{':
                        tabIndex++;
                        break;
                    case '}':
                        if (tabIndex == 2) {
                            resources.add(new Resource(NetUtil.clip(tempBuilder.toString(), "\"hash\":\"", "\","),
                                    Long.parseLong(NetUtil.clip(tempBuilder.toString(), "\"size\":", tempBuilder.length()))));
                            tempBuilder.setLength(0);
                        }
                        tabIndex--;
                        break;
                    default:
                        if (tabIndex == 2) {
                            tempBuilder.append(c);
                        }
                        break;
                }
            }
        }
        resources.forEach(r -> {
            threads.add(new Thread(() -> r.download(os)));
        });
        threads.add(new Thread(() -> NetUtil.download(this.URL, new File(Main.INDEXES_DIR, String.format("%s.json", this.ID)))));
    }

    public final List<Thread> threads = new ArrayList<>();

    private static class Resource implements Downloadable {

        private static final String RESOURCE_URL = "https://resources.download.minecraft.net/";
        private final String HASH;
        private final long SIZE;

        public Resource(final String HASH, final long SIZE) {
            this.HASH = HASH;
            this.SIZE = SIZE;
        }

        @Override
        public void download(OS os) {
            final String head = this.HASH.substring(0, 2);
            final StringBuilder url = new StringBuilder(RESOURCE_URL);
            url.append(head);
            url.append('/');
            url.append(this.HASH);
            final File downloadFile = new File(Main.OBJECTS_DIR, head);
            downloadFile.mkdirs();
            final File desFile = new File(downloadFile, HASH);
            if (!NetUtil.checkExist(desFile, this.SIZE)) {
                NetUtil.download(url.toString(), desFile);
            }
        }

    }

}
