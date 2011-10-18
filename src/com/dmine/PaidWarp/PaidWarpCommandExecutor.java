package com.dmine.PaidWarp;

import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.nijikokun.register.payment.Methods;
import com.nijikokun.register.payment.Method.MethodAccount;

public class PaidWarpCommandExecutor implements CommandExecutor {
	
	private PaidWarp plugin;
	
	public PaidWarpCommandExecutor(PaidWarp plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = null;
		
		if (sender instanceof Player) {
			player = (Player)sender;
		}
		else {
			sender.sendMessage("This command can only be run by a player.");
			return true;
		}
		
		String cmdName = cmd.getName();
		
		if (cmdName.equalsIgnoreCase("setwarp")) {
			return setWarp(player, args);
		}
		else if (cmdName.equalsIgnoreCase("delwarp")) {
			return delWarp(player, args);
		}
		else if (cmdName.equalsIgnoreCase("warp")) {
			return warp(player, args);
		}
		else if(cmd.getName().equalsIgnoreCase("warps")) {
			return listWarps(player, args);
		}
		
		return false;
	}

	private boolean listWarps(Player player, String[] args) {
		int page = 1;
		int perPage = this.plugin.config.getInt("paidwarp.warpsperpage", 5);
		
		if (args.length > 0) {
			try {
				page = Integer.parseInt(args[0]);
			}
			catch (Exception e) {
				page = 1;
			}
		}
		
		int start = (page - 1)*perPage;
		int end = page*perPage;
		
		if (this.plugin.warps.size() > 0 && start >= 0 && start <= this.plugin.warps.size()) {
			int i = 0;
			Iterator<String> it = this.plugin.warps.keySet().iterator();
			
			player.sendMessage(String.format("Listing warp points %d to %d (Total: %d).", start + 1, end, this.plugin.warps.size()));
			while (it.hasNext()) {
				String warpName = it.next();
				
				if (i >= start && i < end) {
					player.sendMessage("> " + warpName);
				}
				i++;
			}
		}
		else {
			player.sendMessage("No warp points to list.");
		}
		
		return true;
	}

	private boolean warp(Player player, String[] args) {
		if (args.length != 1) {
			return false;
		}
		
		if (this.plugin.method == null) {
			if (Methods.hasMethod()) {
				this.plugin.method = Methods.getMethod();
			}
			else {
				player.sendMessage("Economy plugin is not loaded - no warping possible.");
				return true;
			}
		}
		
		String warpName = args[0];
		
		if (this.plugin.warps.containsKey(warpName)) {
			SerializableLocation warpLocation = this.plugin.warps.get(warpName);
			Location location = player.getLocation();
			warpLocation.updateLocation(location);
			
			if (location.getBlock().getType().equals(Material.AIR) && location.add(0.0d, 1.0d, 0.0d).getBlock().getType().equals(Material.AIR)) {
				if (location.getWorld().getUID().equals(warpLocation.getUUID())) {
					if (this.plugin.method.hasAccount(player.getName())) {
						MethodAccount account = this.plugin.method.getAccount(player.getName());
						
						double warpCost = 0;
						if (!player.hasPermission("paidwarp.free")) {
							warpCost = this.plugin.config.getDouble("paidwarp.warpcost", 10);
						}
						
						if (account.hasEnough(warpCost)) {
							account.subtract(warpCost);
							player.teleport(location);
							player.sendMessage("Warped you to the destination.");
							this.plugin.logger.info(String.format("[%s] Warped %s to %s for %.2f.", this.plugin.pdf.getName(), player.getName(), warpName, warpCost));
						}
						else {
							player.sendMessage("You dont have enough funds for the warp.");
						}
					}
					else {
						player.sendMessage("Could not find your account.");
					}
				}
				else {
					player.sendMessage("The warp point is not in your dimension.");
				}
			}
			else {
				player.sendMessage("The warp point has been destroyed.");
			}
		}
		else {
			player.sendMessage("The warp point could not be found.");
		}
		
		return true;
	}

	private boolean delWarp(Player player, String[] args) {
		if (args.length != 1) {
			return false;
		}
		
		String warpName = args[0];
		
		if (this.plugin.warps.containsKey(warpName)) {
			this.plugin.warps.remove(warpName);
			this.plugin.saveWarps();
			player.sendMessage("The warp has been removed.");
			this.plugin.logger.info(String.format("[%s] %s deleted warp %s.", this.plugin.pdf.getName(), player.getName(), warpName));
		}
		else {
			player.sendMessage("The warp does not exist.");
		}
		
		return true;
	}

	private boolean setWarp(Player player, String[] args) {
		if (args.length != 1) {
			return false;
		}
		
		String warpName = args[0];
		
		if (!this.plugin.warps.containsKey(warpName)) {
			SerializableLocation sLocation = new SerializableLocation(player.getLocation());
			this.plugin.warps.put(warpName, sLocation);
			this.plugin.saveWarps();
			player.sendMessage("The warp has been created.");
			this.plugin.logger.info(String.format("[%s] %s set new warp point %s", this.plugin.pdf.getName(), player.getName(), warpName));
		}
		else {
			player.sendMessage("The warp name is already in use.");
		}
		
		return true;
	}
}
