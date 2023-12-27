package net.rainbowcreation.extension.server.utils;

import net.kyori.adventure.text.Component;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import java.awt.*;
import java.util.Random;

@SuppressWarnings("unused")
public class Item {
    private ItemStack itemStack;
    private net.minecraft.item.Item material;
    private int n = 1;
    private net.kyori.adventure.text.Component displayName;
    private NBTTagList lore;
    private NBTTagCompound itemMeta;
    private int color = 0;
    private int i = 0;
    private static final Color[] COLOR_LIST = {
            Color.red, Color.black, Color.blue, Color.cyan, Color.darkGray, Color.gray, Color.green, Color.lightGray, Color.magenta, Color.orange, Color.pink,
            Color.white, Color.yellow
    };

    public Item material(net.minecraft.item.Item material) {
        this.material = material;
        return this;
    }

    public Item material(Block material) {
        this.material = net.minecraft.item.Item.getItemFromBlock(material);
        return this;
    }

    public Item color(int color) {
        this.color = color;
        return this;
    }

    public net.minecraft.item.Item getMaterial() {
        return material;
    }

    public Item amount(int n) {
        this.n = n;
        return this;
    }

    public int getAmount() {
        return n;
    }

    public Item displayName(String mimnimessage) {
        displayName = Chat.minimessageComponent(mimnimessage);
        return this;
    }

    public Component getName() {
        return displayName;
    }

    public Item lore(NBTTagList loreListColored) {
        lore = loreListColored;
        return this;
    }

    public Item lore(String minimessage) {
        if (lore == null)
            lore  = new NBTTagList();
        lore.appendTag(new NBTTagString(Chat.minimessageLegacy(minimessage)));
        return this;
    }

    public NBTTagList getLore() {
        return lore;
    }

    public Item customModelData(int modelNumber) {
        i = modelNumber;
        return this;
    }

    public int getCustomModelData() {
        return i;
    }

    public ItemStack get() {
        itemStack = new ItemStack(material, n, color);
        itemMeta = itemStack.getTagCompound();
        if (itemMeta == null)
            itemMeta = new NBTTagCompound();
        if (displayName != null)
            itemMeta.setString("display", Chat.componentColored(displayName));
        if (lore != null) {
            NBTTagCompound displayname = itemMeta.getCompoundTag("display");
            displayname.setTag("Lore", lore);
            itemMeta.setTag("display", displayname);
        }
        itemStack.setTagCompound(itemMeta);
        return itemStack;
    }

    public ItemStack getrandomFirework() {
        ItemStack fireworkStack = new ItemStack(Items.FIREWORKS);
        NBTTagCompound fireworkTag = new NBTTagCompound();
        NBTTagCompound explosionTag = new NBTTagCompound();
        // Randomly set explosion properties
        explosionTag.setByte("Type", (byte) new Random().nextInt(5)); // Random explosion type
        explosionTag.setIntArray("Colors", getRandomColors()); // Random colors
        explosionTag.setIntArray("FadeColors", getRandomColors()); // Random fade colors
        if (new Random().nextBoolean()) {
            explosionTag.setBoolean("Flicker", true); // 50% chance of adding flicker
        }
        if (new Random().nextBoolean()) {
            explosionTag.setBoolean("Trail", true); // 50% chance of adding trail
        }
        // Set explosion tag
        NBTTagList explosionsList = new NBTTagList();
        explosionsList.appendTag(explosionTag);
        fireworkTag.setTag("Explosions", explosionsList);
        // Set flight duration
        fireworkTag.setByte("Flight", (byte) (1 + new Random().nextInt(3))); // Random flight duration
        // Set fireworks tag
        fireworkStack.getOrCreateSubCompound("Fireworks").setTag("Fireworks", fireworkTag);
        return fireworkStack;
    }

    private static int[] getRandomColors() {
        int numColors = 1 + new Random().nextInt(5); // Random number of colors (1 to 5)
        int[] colors = new int[numColors];

        for (int i = 0; i < numColors; i++) {
            colors[i] = ItemDye.DYE_COLORS[new Random().nextInt(ItemDye.DYE_COLORS.length)]; // Using DYE_COLORS array for 1.12.2
        }

        return colors;
    }
}
