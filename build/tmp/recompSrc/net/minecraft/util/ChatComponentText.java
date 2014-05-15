package net.minecraft.util;

import java.util.Iterator;

public class ChatComponentText extends ChatComponentStyle
{
    private final String text;
    private static final String __OBFID = "CL_00001269";

    public ChatComponentText(String p_i45159_1_)
    {
        this.text = p_i45159_1_;
    }

    /**
     * Gets the text value of this ChatComponentText.  TODO: what are getUnformattedText and getUnformattedTextForChat
     * missing that made someone decide to create a third equivalent method that only ChatComponentText can implement?
     */
    public String getChatComponentText_TextValue()
    {
        return this.text;
    }

    /**
     * Gets the text of this component, without any special formatting codes added, for chat.  TODO: why is this two
     * different methods?
     */
    public String getUnformattedTextForChat()
    {
        return this.text;
    }

    /**
     * Creates a copy of this component.  Almost a deep copy, except the style is shallow-copied.
     */
    public ChatComponentText createCopy()
    {
        ChatComponentText chatcomponenttext = new ChatComponentText(this.text);
        chatcomponenttext.setChatStyle(this.getChatStyle().createShallowCopy());
        Iterator iterator = this.getSiblings().iterator();

        while (iterator.hasNext())
        {
            IChatComponent ichatcomponent = (IChatComponent)iterator.next();
            chatcomponenttext.appendSibling(ichatcomponent.createCopy());
        }

        return chatcomponenttext;
    }

    public boolean equals(Object par1Obj)
    {
        if (this == par1Obj)
        {
            return true;
        }
        else if (!(par1Obj instanceof ChatComponentText))
        {
            return false;
        }
        else
        {
            ChatComponentText chatcomponenttext = (ChatComponentText)par1Obj;
            return this.text.equals(chatcomponenttext.getChatComponentText_TextValue()) && super.equals(par1Obj);
        }
    }

    public String toString()
    {
        return "TextComponent{text=\'" + this.text + '\'' + ", siblings=" + this.siblings + ", style=" + this.getChatStyle() + '}';
    }
}