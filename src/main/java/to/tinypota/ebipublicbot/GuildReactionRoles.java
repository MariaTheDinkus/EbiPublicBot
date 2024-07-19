package to.tinypota.ebipublicbot;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;

public class GuildReactionRoles {
    private long guildId;
    private Map<Long, ReactionRoleMessage> reactionRoleMessages;

    // Default constructor for Jackson
    public GuildReactionRoles() {
        this.reactionRoleMessages = new HashMap<>();
    }

    @JsonCreator
    public GuildReactionRoles(
            @JsonProperty("guildId") long guildId,
            @JsonProperty("reactionRoleMessages") Map<Long, ReactionRoleMessage> reactionRoleMessages) {
        this.guildId = guildId;
        this.reactionRoleMessages = reactionRoleMessages != null ? reactionRoleMessages : new HashMap<>();
    }

    public long getGuildId() {
        return guildId;
    }

    public void setGuildId(long guildId) {
        this.guildId = guildId;
    }

    public Map<Long, ReactionRoleMessage> getReactionRoleMessages() {
        return reactionRoleMessages;
    }

    public void setReactionRoleMessages(Map<Long, ReactionRoleMessage> reactionRoleMessages) {
        this.reactionRoleMessages = reactionRoleMessages;
    }

    public void addReactionRoleMessage(ReactionRoleMessage message) {
        reactionRoleMessages.put(message.getMessageId(), message);
    }

    public void removeReactionRoleMessage(long messageId) {
        reactionRoleMessages.remove(messageId);
    }

    public ReactionRoleMessage getReactionRoleMessage(long messageId) {
        return reactionRoleMessages.get(messageId);
    }
}