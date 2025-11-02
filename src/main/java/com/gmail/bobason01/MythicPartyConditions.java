package com.gmail.bobason01;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.ISkillCondition;
import io.lumine.mythic.bukkit.events.MythicConditionLoadEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class MythicPartyConditions extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onConditionLoad(MythicConditionLoadEvent event) {
        if (event.getConditionName().equalsIgnoreCase("partywithin")) {
            MythicLineConfig config = event.getConfig();
            ISkillCondition cond = new PartyWithinSkillCondition(event.getConditionName(), config);
            event.register(cond);
        }
        if (event.getConditionName().equalsIgnoreCase("partyowner")) {
            MythicLineConfig config = event.getConfig();
            ISkillCondition cond = new PartyOwnerSkillCondition(event.getConditionName(), config);
            event.register(cond);
        }
    }
}

