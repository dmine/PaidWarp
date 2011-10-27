package com.dmine.PaidWarp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import com.nijikokun.register.payment.Method;
import com.nijikokun.register.payment.Methods;

public class PaidWarp extends JavaPlugin {
	
	public final Logger logger = Logger.getLogger("Minecraft");
	public PluginDescriptionFile pdf;
	public HashMap<String,SerializableLocation> warps;
	public Method method;
	public Configuration config;
	private PaidWarpCommandExecutor executor;
	
	@Override
	public void onEnable() {
		this.pdf = this.getDescription();
		this.logger.info(String.format("[%s] Plugin enabled.", this.pdf.getName()));
		this.executor = new PaidWarpCommandExecutor(this);
		
		this.config = new Configuration(new File("bukkit.yml"));
		this.config.load();
		this.config.setProperty("paidwarp.cost_base", this.config.getDouble("paidwarp.cost_base", 10.0));
		this.config.setProperty("paidwarp.cost_distance", this.config.getDouble("paidwarp.cost_distance", 10.0));
		this.config.setProperty("paidwarp.warpsperpage", this.config.getInt("paidwarp.warpsperpage", 5));
		this.config.save();
		
		registerCommands();
		this.loadWarps();
		
		if (Methods.hasMethod()) {
			this.method = Methods.getMethod();
		}
	}
	
	@Override
	public void onDisable() {
		this.logger.info(String.format("[%s] Plugin disabled.", this.pdf.getName()));
		this.saveWarps();
	}
	
	private void registerCommands() {
		getCommand("setwarp").setExecutor(executor);
		getCommand("delwarp").setExecutor(executor);
		getCommand("warps").setExecutor(executor);
		getCommand("warp").setExecutor(executor);
	}
	
	@SuppressWarnings("unchecked")
	public boolean loadWarps() {
		try {
			if (!this.getDataFolder().exists()) {
				try {
					this.getDataFolder().mkdir();
				}
				catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
			File warpFile = new File(this.getDataFolder(), "warps.bin");
			if (!warpFile.exists()) {
				try {
					warpFile.createNewFile();
				}
				catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
			
			try {
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(warpFile));
				Object result = ois.readObject();
				ois.close();
				this.warps = (HashMap<String,SerializableLocation>)result;
				if (this.warps == null) {
					throw new Exception();
				}
				this.logger.info(String.format("[%s] %d warp points loaded.", this.pdf.getName(), this.warps.size()));
			}
			catch (Exception e) {
				//e.printStackTrace();
				warps = new HashMap<String,SerializableLocation>();
				this.logger.info(String.format("[%s] Created empty warp list.", this.pdf.getName()));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public boolean saveWarps() {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(getDataFolder(), "warps.bin")));
			oos.writeObject(warps);
			oos.flush();
			oos.close();
			this.logger.info(String.format("[%s] %d warp points saved.", this.pdf.getName(), this.warps.size()));
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
}
