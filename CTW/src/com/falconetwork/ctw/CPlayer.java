package com.falconetwork.ctw;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import com.falconetwork.ctw.teams.Team;
import com.falconetwork.ctw.util.TeamType;
import com.falconetwork.fca.jnbt.CompoundTag;
import com.falconetwork.fca.jnbt.DoubleTag;
import com.falconetwork.fca.jnbt.IntTag;
import com.falconetwork.fca.jnbt.NBTInputStream;
import com.falconetwork.fca.jnbt.NBTOutputStream;
import com.falconetwork.fca.jnbt.Tag;

public class CPlayer {
	private int cash = 0;
	private int wins = 0;
	private int kills = 0;
	private int deaths = 0;
	private Team team = null;
	private double kdr = 0.0D;
	private VIPShop shop = null;
	private Player player = null;
	private File dataFile = null;
	private double donated = 0.0D;
	private boolean carrying = false;
	private TeamType teamType = null;
	private List<String> perks = null;
	
	public CPlayer(Player player) {
		this.player = player;
		this.shop = new VIPShop(this);
		this.teamType = TeamType.UNKNOWN;
		this.perks = new ArrayList<String>();
		this.dataFile = new File(CTW.playersFolder, player.getName() + ".dat");
		
		// Getting donated amount from donator Database.
		{
			try {
				/*ResultSet set = CTW.donators.querySQL("SELECT * FROM [Donators] WHERE Player='" + player.getName() + "';");
				if(set.next()) {
					donated = set.getDouble("Donated");
				}*/
				loadPerks();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		try {
			if(!dataFile.exists()) {
				dataFile.createNewFile();
				save();
			} else
				load();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void loadPerks() {
		if(donated >= 2.50) {
			perks.add("vipArmor");
			perks.add("vipNametag");
			perks.add("vipWeapons");
		}
		if(donated >= 3.50)
			perks.add("invisiblity");
	}
	
	public void load() {
		try {
			FileInputStream fin = new FileInputStream(dataFile);
			NBTInputStream in = new NBTInputStream(new GZIPInputStream(fin));
			CompoundTag tag = (CompoundTag) in.readTag();
			in.close();
			Map<String, Tag> tags = tag.getValue();
			if(tags.containsKey("KDR")) kdr = ((DoubleTag) tags.get("KDR")).getValue();
			if(tags.containsKey("Cash")) cash = ((IntTag) tags.get("Cash")).getValue();
			if(tags.containsKey("Wins")) wins = ((IntTag) tags.get("Wins")).getValue();
			if(tags.containsKey("Kills")) kills = ((IntTag) tags.get("Kills")).getValue();
			if(tags.containsKey("Deaths")) deaths = ((IntTag) tags.get("Deaths")).getValue();
		} catch (Exception ex) {
			System.err.println("[CTW] LOAD ERROR: " + ex.getMessage());
		}
	}
	
	public void save() {
		try {
			Map<String, Tag> tags = new HashMap<String, Tag>();
			tags.put("Cash", new IntTag("Cash", cash));
			tags.put("Wins", new IntTag("Wins", wins));
			tags.put("KDR", new DoubleTag("KDR", kdr));
			tags.put("Kills", new IntTag("Kills", kills));
			tags.put("Deaths", new IntTag("Deaths", deaths));
			CompoundTag tag = new CompoundTag("Player", tags);
			FileOutputStream fos = new FileOutputStream(dataFile);
			NBTOutputStream out = new NBTOutputStream(fos);
			out.writeTag(tag);
			out.close();
		} catch (Exception ex) {
			System.err.println("[CTW] SAVE ERROR: " + ex.getMessage());
		}
	}
	
	public void openShop() {
		shop.setup();
		shop.open(player);
	}
	
	public void addPerk(String perk) {
		if(!perks.contains(perk))
			perks.add(perk);
	}
	
	public void removePerk(String perk) {
		if(perks.contains(perk))
			perks.remove(perk);
	}
	
	public boolean hasPerk(String perk) {
		boolean flag = false;
		for(String p : perks)
			if(p.equalsIgnoreCase(perk)) {
				flag = true;
				break;
			}
				
		return flag;
	}
	
	public int getCash() {
		return cash;
	}

	public void setCash(int cash) {
		this.cash = cash;
	}
	
	public void addCash(int cash) {
		this.cash += cash;
		if(this.cash < 0)
			this.cash = 0;
	}

	public int getWins() {
		return wins;
	}

	public void setWins(int wins) {
		this.wins = wins;
	}
	
	public void addWins(int wins) {
		this.wins += wins;
		if(this.wins < 0)
			this.wins = 0;
	}

	public int getKills() {
		return kills;
	}

	public void setKills(int kills) {
		this.kills = kills;
	}
	
	public void addKills(int kills) {
		this.kills += kills;
		if(this.kills < 0)
			this.kills = 0;
	}

	public int getDeaths() {
		return deaths;
	}

	public void setDeaths(int deaths) {
		this.deaths = deaths;
	}
	
	public void addDeaths(int deaths) {
		this.deaths += deaths;
		if(this.deaths < 0)
			this.deaths = 0;
	}

	public double getKdr() {
		return kdr;
	}

	public void setKdr(double kdr) {
		this.kdr = kdr;
	}
	
	public void calcKDR() {
		this.kdr = (double) (kills / deaths);
	}
	
	public boolean isCarrying() {
		return carrying;
	}
	
	public void setCarrying(boolean carrying) {
		this.carrying = carrying;
		if(carrying == false)
			for(PotionEffect e : player.getActivePotionEffects())
				player.removePotionEffect(e.getType());
	}

	public Team getTeam() {
		return team;
	}
	
	public void setTeam(Team team) {
		this.team = team;
		if(team == null)
			this.teamType = TeamType.UNKNOWN;
		else
			this.teamType = team.getType();
	}
	
	public TeamType getTeamType() {
		return teamType;
	}
	
	public void setTeamType(TeamType teamType) {
		this.teamType = teamType;
	}
	
	public void setPlayer(Player player) {
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

	public boolean isInTeam() {
		return !(team == null);
	}
	
	public void setDonated(double donated) {
		this.donated = donated;
	}
	
	public double getDonated() {
		return donated;
	}
	
	public VIPShop getShop() {
		return shop;
	}
	
	public List<String> getPerks() {
		return perks;
	}
	
	public void setPerks(List<String> perks) {
		this.perks = perks;
	}
	
}