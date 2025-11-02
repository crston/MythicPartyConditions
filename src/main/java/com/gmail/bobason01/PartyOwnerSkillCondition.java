package com.gmail.bobason01;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.conditions.ICasterCondition;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import net.playavalon.mythicdungeons.api.MythicDungeonsService;
import net.playavalon.mythicdungeons.api.party.IDungeonParty;
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
    public boolean check(SkillCaster caster) {
        AbstractEntity ae = caster.getEntity();
        return ae != null && checkEntity(ae);
    }

    @Override
    public boolean check(AbstractEntity entity) {
        return entity != null && checkEntity(entity);
    }

    private boolean checkEntity(AbstractEntity ae) {
        Entity bukkit = ae.getBukkitEntity();
        if (bukkit == null) return false;

        if (bukkit instanceof Player player) {
            return inParty(player);
        }

        if (bukkit instanceof Tameable tameable) {
            if (tameable.getOwner() instanceof Player player) {
                return inParty(player);
            }
        }

        if (bukkit instanceof Projectile projectile) {
            if (projectile.getShooter() instanceof Player player) {
                return inParty(player);
            }
        }

        ActiveMob am = MythicBukkit.inst().getMobManager().getActiveMob(ae.getUniqueId()).orElse(null);
        if (am != null && am.getParent().isPresent()) {
            AbstractEntity parent = am.getParent().get();
            if (parent != null && parent.isPlayer()) {
                return inParty((Player) parent.getBukkitEntity());
            }
        }

        return false;
    }

    private boolean inParty(Player player) {
        if (svc == null) return false;
        IDungeonParty party = svc.getParty(player);
        return party != null;
    }
}
