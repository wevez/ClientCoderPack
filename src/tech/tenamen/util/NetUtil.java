package tech.tenamen.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class NetUtil {

    public static boolean checkExist(final File file, final long size) {
        return file.length() == size;
    }

    public static void download(final String urly, final File file) {
        System.out.printf("Downloading %s -> %s\n", urly, file.getAbsolutePath());
        try {
            URL url = new URL(urly);
            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setAllowUserInteraction(false);
            conn.setInstanceFollowRedirects(true);
            conn.setRequestMethod("GET");
            conn.connect();
            int httpStatusCode = conn.getResponseCode();
            if (httpStatusCode != HttpURLConnection.HTTP_OK) {
                throw new Exception("HTTP Status " + httpStatusCode);
            }
            final DataInputStream dataInStream = new DataInputStream(conn.getInputStream());
            final DataOutputStream dataOutStream = new DataOutputStream(new BufferedOutputStream(Files.newOutputStream(file.toPath())));
            final byte[] b = new byte[4096];
            int readByte = 0;
            while (-1 != (readByte = dataInStream.read(b))) {
                dataOutStream.write(b, 0, readByte);
            }
            dataInStream.close();
            dataOutStream.close();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public static String download(final String urly) {
        final StringBuilder buffer = new StringBuilder();
        URL url;
        try {
            url = new URL(urly);
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            final InputStreamReader reader = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8);
            final BufferedReader in = new BufferedReader(reader);
            String line = null;
            while ((line = in.readLine()) != null) {
                buffer.append(line);
                buffer.append('\n');
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }

    public static String clip(String target, String first, String last) {
        final int startIndex = target.indexOf(first) + first.length();
        return target.substring(startIndex, target.indexOf(last, startIndex));
    }

    public static String clip(String target, String first, int last) {
        final int startIndex = target.indexOf(first) + first.length();
        return target.substring(startIndex, last);
    }

}
