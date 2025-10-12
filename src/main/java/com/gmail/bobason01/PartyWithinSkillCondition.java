package com.gmail.bobason01;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.conditions.ICasterCondition;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import net.playavalon.mythicdungeons.api.MythicDungeonsService;
import net.playavalon.mythicdungeons.api.party.IDungeonParty;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PartyWithinSkillCondition implements ICasterCondition, IEntityCondition {

    private final double distanceSquared;
    private final int minMembers;
    private final boolean asCaster;

    public PartyWithinSkillCondition(String conditionName, MythicLineConfig config) {
        this.distanceSquared = Math.pow(config.getDouble(new String[]{"d","distance"}, 10.0), 2);
        this.minMembers = config.getInteger(new String[]{"n","min","members"}, 1);
        this.asCaster = config.getBoolean(new String[]{"ascaster"}, true);
    }

    @Override
    public boolean check(SkillCaster caster) {
        if (!asCaster) return false;
        AbstractEntity ae = caster.getEntity();
        if (ae == null || !ae.isPlayer()) return false;
        return checkPlayer((Player) ae.getBukkitEntity());
    }

    @Override
    public boolean check(AbstractEntity entity) {
        if (asCaster) return false;
        if (entity == null || !entity.isPlayer()) return false;
        return checkPlayer((Player) entity.getBukkitEntity());
    }

    private boolean checkPlayer(Player base) {
        MythicDungeonsService svc = Bukkit.getServicesManager().load(MythicDungeonsService.class);
        if (svc == null) return false;
        IDungeonParty party = svc.getParty(base);
        if (party == null) return false;
        int count = 0;
        var baseLoc = base.getLocation();
        var baseWorld = baseLoc.getWorld();
        for (Player member : party.getPlayers()) {
            if (member == null) continue;
            if (member.equals(base)) continue;
            if (!member.isOnline()) continue;
            if (member.getWorld() != baseWorld) continue;
            if (member.getLocation().distanceSquared(baseLoc) <= distanceSquared) {
                count++;
                if (count >= minMembers) return true;
            }
        }
        return false;
    }
}
