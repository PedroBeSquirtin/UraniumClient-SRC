package com.uranium.utils.webhook;

import javax.net.ssl.HttpsURLConnection;
import java.awt.*;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.*;

public class DiscordWebhook {
    private final String url;
    private String content;
    private String username;
    private String avatarUrl;
    private boolean tts;
    private final List<EmbedObject> embeds = new ArrayList<>();

    public DiscordWebhook(String webhookUrl) {
        this.url = webhookUrl;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void setTts(boolean tts) {
        this.tts = tts;
    }

    public void addEmbed(EmbedObject embed) {
        this.embeds.add(embed);
    }

    public void execute() throws Exception {
        if (content == null && embeds.isEmpty()) {
            throw new IllegalArgumentException("Set content or add at least one EmbedObject");
        }

        JSONObject json = new JSONObject();
        json.put("content", content);
        json.put("username", username);
        json.put("avatar_url", avatarUrl);
        json.put("tts", tts);

        if (!embeds.isEmpty()) {
            List<JSONObject> embedObjects = new ArrayList<>();
            
            for (EmbedObject embed : embeds) {
                JSONObject jsonEmbed = new JSONObject();
                jsonEmbed.put("title", embed.title);
                jsonEmbed.put("description", embed.description);
                jsonEmbed.put("url", embed.url);

                if (embed.color != null) {
                    Color color = embed.color;
                    int rgb = (color.getRed() << 16) + (color.getGreen() << 8) + color.getBlue();
                    jsonEmbed.put("color", rgb);
                }

                if (embed.footer != null) {
                    JSONObject jsonFooter = new JSONObject();
                    jsonFooter.put("text", embed.footer.text);
                    jsonFooter.put("icon_url", embed.footer.iconUrl);
                    jsonEmbed.put("footer", jsonFooter);
                }

                if (embed.image != null) {
                    JSONObject jsonImage = new JSONObject();
                    jsonImage.put("url", embed.image.url);
                    jsonEmbed.put("image", jsonImage);
                }

                if (embed.thumbnail != null) {
                    JSONObject jsonThumbnail = new JSONObject();
                    jsonThumbnail.put("url", embed.thumbnail.url);
                    jsonEmbed.put("thumbnail", jsonThumbnail);
                }

                if (embed.author != null) {
                    JSONObject jsonAuthor = new JSONObject();
                    jsonAuthor.put("name", embed.author.name);
                    jsonAuthor.put("url", embed.author.url);
                    jsonAuthor.put("icon_url", embed.author.iconUrl);
                    jsonEmbed.put("author", jsonAuthor);
                }

                List<JSONObject> jsonFields = new ArrayList<>();
                for (Field field : embed.fields) {
                    JSONObject jsonField = new JSONObject();
                    jsonField.put("name", field.name);
                    jsonField.put("value", field.value);
                    jsonField.put("inline", field.inline);
                    jsonFields.add(jsonField);
                }
                jsonEmbed.put("fields", jsonFields.toArray());

                embedObjects.add(jsonEmbed);
            }

            json.put("embeds", embedObjects.toArray());
        }

        URLConnection connection = new URL(url).openConnection();
        connection.addRequestProperty("Content-Type", "application/json");
        connection.addRequestProperty("User-Agent", "Uranium-Client/1.0");
        connection.setDoOutput(true);
        
        HttpsURLConnection httpsConnection = (HttpsURLConnection) connection;
        httpsConnection.setRequestMethod("POST");

        try (OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write(json.toString().getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        }

        connection.getInputStream().close();
        httpsConnection.disconnect();
    }

    static class JSONObject {
        private final Map<String, Object> map = new HashMap<>();

        void put(String key, Object value) {
            if (value != null) {
                map.put(key, value);
            }
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            
            int count = 0;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                sb.append(quote(entry.getKey())).append(":");
                
                Object value = entry.getValue();
                if (value instanceof String) {
                    sb.append(quote((String) value));
                } else if (value instanceof Integer || value instanceof Boolean) {
                    sb.append(value);
                } else if (value instanceof JSONObject) {
                    sb.append(value);
                } else if (value != null && value.getClass().isArray()) {
                    sb.append("[");
                    int length = Array.getLength(value);
                    for (int i = 0; i < length; i++) {
                        sb.append(Array.get(value, i).toString());
                        if (i < length - 1) sb.append(",");
                    }
                    sb.append("]");
                }
                
                count++;
                sb.append(count == map.size() ? "}" : ",");
            }
            
            return sb.toString();
        }

        private String quote(String s) {
            return "\"" + s + "\"";
        }
    }

    public static class EmbedObject {
        private String title;
        private String description;
        private String url;
        private Color color;
        private Footer footer;
        private Thumbnail thumbnail;
        private Image image;
        private Author author;
        private final List<Field> fields = new ArrayList<>();

        public EmbedObject setTitle(String title) {
            this.title = title;
            return this;
        }

        public EmbedObject setDescription(String description) {
            this.description = description;
            return this;
        }

        public EmbedObject setUrl(String url) {
            this.url = url;
            return this;
        }

        public EmbedObject setColor(Color color) {
            this.color = color;
            return this;
        }

        public EmbedObject setFooter(String text, String iconUrl) {
            this.footer = new Footer(text, iconUrl);
            return this;
        }

        public EmbedObject setImage(String url) {
            this.image = new Image(url);
            return this;
        }

        public EmbedObject setThumbnail(String url) {
            this.thumbnail = new Thumbnail(url);
            return this;
        }

        public EmbedObject setAuthor(String name, String url, String iconUrl) {
            this.author = new Author(name, url, iconUrl);
            return this;
        }

        public EmbedObject addField(String name, String value, boolean inline) {
            this.fields.add(new Field(name, value, inline));
            return this;
        }
    }

    private record Image(String url) {}
    private record Footer(String text, String iconUrl) {}
    private record Field(String name, String value, boolean inline) {}
    private record Author(String name, String url, String iconUrl) {}
    private record Thumbnail(String url) {}
}
