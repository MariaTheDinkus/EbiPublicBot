package to.tinypota.ebipublicbot;

import java.util.HashMap;
import java.util.Map;

public class GuildReactionRoles {
    private long guildId;
    private Map<Long, ReactionRoleMessage> reactionRoleMessages;

    public GuildReactionRoles(long guildId) {
        this.guildId = guildId;
        this.reactionRoleMessages = new HashMap<>();
    }

    public long getGuildId() {
        return guildId;
    }

    public Map<Long, ReactionRoleMessage> getReactionRoleMessages() {
        return reactionRoleMessages;
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