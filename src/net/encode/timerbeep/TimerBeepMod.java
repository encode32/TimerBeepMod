package net.encode.timerbeep;

import java.util.Properties;
import org.gotti.wurmunlimited.modloader.ReflectionUtil;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.interfaces.Configurable;
import org.gotti.wurmunlimited.modloader.interfaces.Initable;
import org.gotti.wurmunlimited.modloader.interfaces.WurmClientMod;

import com.wurmonline.client.game.PlayerPosition;
import com.wurmonline.client.renderer.cell.CellRenderable;
import com.wurmonline.client.sound.FixedSoundSource;
import com.wurmonline.client.sound.SoundSource;

public class TimerBeepMod implements WurmClientMod, Initable, Configurable{

	private boolean enabled = true;
	private String sound = "sound.bell.handbell";
	
	@Override
	public void configure(Properties properties) {
		this.enabled = Boolean.valueOf(properties.getProperty("enabled", Boolean.toString(this.enabled)));
		this.sound = properties.getProperty("sound", this.sound);
	}

	@Override
	public void init() {
		
		HookManager.getInstance().registerHook("com.wurmonline.client.renderer.gui.CustomTimerComponent$Timer", "tick", "()V",
				() -> (proxy, method, args) -> {
					Class<?> cls = proxy.getClass();
        			
					boolean preFinished = ReflectionUtil.getPrivateField(proxy, ReflectionUtil.getField(cls, "finished"));
					method.invoke(proxy, args);
					boolean postFinished = ReflectionUtil.getPrivateField(proxy, ReflectionUtil.getField(cls, "finished"));
					
					if(!preFinished && postFinished && this.enabled)
					{
						playSound(this.sound);
					}
					
					
					return null;
				});
	}
	
	private void playSound(String sound) {
		PlayerPosition pos = CellRenderable.world.getPlayer().getPos();
		CellRenderable.world.getSoundEngine().play(sound, (SoundSource)new FixedSoundSource(pos.getX(), pos.getY(), 2.0f), 1.0f, 5.0f, 1.0f, false, false);
	}

}
