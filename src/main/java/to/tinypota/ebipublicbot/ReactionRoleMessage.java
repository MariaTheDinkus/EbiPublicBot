package to.tinypota.ebipublicbot;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;

public class ReactionRoleMessage {
    private long messageId;
    private long channelId;
    private String description;
    private Map<String, Long> emojiRoleMap;

    // Default constructor for Jackson
    public ReactionRoleMessage() {
        this.emojiRoleMap = new HashMap<>();
    }

    @JsonCreator
    public ReactionRoleMessage(
            @JsonProperty("messageId") long messageId,
            @JsonProperty("channelId") long channelId,
            @JsonProperty("description") String description,
            @JsonProperty("emojiRoleMap") Map<String, Long> emojiRoleMap) {
        this.messageId = messageId;
        this.channelId = channelId;
        this.description = description;
        this.emojiRoleMap = emojiRoleMap != null ? emojiRoleMap : new HashMap<>();
    }

    // Getters and setters
    public long getMessageId() { return messageId; }
    public void setMessageId(long messageId) { this.messageId = messageId; }

    public long getChannelId() { return channelId; }
    public void setChannelId(long channelId) { this.channelId = channelId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Map<String, Long> getEmojiRoleMap() { return emojiRoleMap; }
    public void setEmojiRoleMap(Map<String, Long> emojiRoleMap) { this.emojiRoleMap = emojiRoleMap; }

    public void addReactionRole(String emoji, long roleId) {
        emojiRoleMap.put(emoji, roleId);
    }

    public void removeReactionRole(String emoji) {
        emojiRoleMap.remove(emoji);
    }
}