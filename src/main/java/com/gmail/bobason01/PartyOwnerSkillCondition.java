package com.gmail.bobason01;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.ICasterCondition;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import net.Indyuce.mmocore.party.provided.Party;
import net.playavalon.mythicdungeons.api.MythicDungeonsService;
import net.playavalon.mythicdungeons.api.party.IDungeonParty;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Tameable;

public class PartyOwnerSkillCondition implements ICasterCondition, IEntityCondition {

    private static final MythicDungeonsService svc =
            Bukkit.getServicesManager().load(MythicDungeonsService.class);

    public PartyOwnerSkillCondition(String conditionName, MythicLineConfig config) {
    }

    @Override
    public boolean check(AbstractEntity entity) {
        return entity != null && checkEntity(entity);
    }

    @Override
    public boolean check(io.lumine.mythic.api.skills.SkillCaster caster) {
        AbstractEntity ae = caster.getEntity();
        return ae != null && checkEntity(ae);
    }

    private boolean checkEntity(AbstractEntity ae) {
        Entity bukkit = ae.getBukkitEntity();
        if (bukkit == null) return false;

        if (bukkit instanceof Player player) {
            return inAnyParty(player);
        }

        if (bukkit instanceof Tameable tameable) {
            if (tameable.getOwner() instanceof Player player) {
                return inAnyParty(player);
            }
        }

        if (bukkit instanceof Projectile projectile) {
            if (projectile.getShooter() instanceof Player player) {
                return inAnyParty(player);
            }
        }

        ActiveMob am = MythicBukkit.inst().getMobManager().getActiveMob(ae.getUniqueId()).orElse(null);
        if (am != null && am.getParent().isPresent()) {
            AbstractEntity parent = am.getParent().get();
            if (parent != null && parent.isPlayer()) {
                return inAnyParty((Player) parent.getBukkitEntity());
            }
        }

        return false;
    }

    private boolean inAnyParty(Player player) {

        if (svc != null) {
            IDungeonParty md = svc.getParty(player);
            if (md != null) return true;
        }

        PlayerData data = PlayerData.get(player);
        if (data != null) {
            Party mmocoreParty = (Party) data.getParty();
            if (mmocoreParty != null) return true;
        }

        return false;
    }
}
