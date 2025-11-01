package com.markabot.bot;

import java.util.Objects;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

/**
 * Provides helper checks for privileged MarkaBot operations.
 */
public class SecurityGuards {

    private final String ownerId;

    public SecurityGuards(String ownerId) {
        this.ownerId = ownerId != null && !ownerId.isBlank() ? ownerId : null;
    }

    public String ownerId() {
        return ownerId;
    }

    public boolean isOwner(String userId) {
        if (ownerId == null) {
            return false;
        }
        return Objects.equals(ownerId, userId);
    }

    public boolean isOwner(User user) {
        return user != null && isOwner(user.getId());
    }

    public boolean isOwner(Member member) {
        return member != null && isOwner(member.getId());
    }
}
