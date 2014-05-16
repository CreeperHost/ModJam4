package net.creeperhost.modjam4.voice;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import net.minecraft.client.settings.KeyBinding;

import java.util.List;

/**
 * Created by Minion 2 on 16/05/2014.
 *
 * A imposter keybind we can force to be held down for so long
 */
public class ImposterKeyBinding extends KeyBinding {

    private static List<KeyBinding> existingSet = ObfuscationReflectionHelper.getPrivateValue(KeyBinding.class, null, "keybindArray");
    
    public ImposterKeyBinding(KeyBinding keybinding) {
        super(keybinding.getKeyDescription(), keybinding.getKeyCode(), keybinding.getKeyCategory());

    }
}
