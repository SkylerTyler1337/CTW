package com.falconetwork.ctw.teams.events;

import org.bukkit.entity.Player;

import com.falconetwork.ctw.teams.Team;

/**
 * Called when a player leaves a team.
 * @author Jatboy
 */
public class TeamLeaveEvent extends TeamEvent {

	public TeamLeaveEvent(Team team, Player player) {
		super(team, team.getType(), player);
	}
	
}