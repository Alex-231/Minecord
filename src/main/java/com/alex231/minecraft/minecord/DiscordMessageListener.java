package com.alex231.minecraft.minecord;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class DiscordMessageListener extends ListenerAdapter
{	
    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
    	if(event.getAuthor().getId().equals(MinecordPlugin.getInstance().jda.getSelfUser().getId()))
            return;
    	if(event.getTextChannel().getName().equals(MinecordPlugin.getInstance().config.getString("DiscordConsoleChannel")))
    	{
            MinecraftConsoleController.SendCommand(event.getMessage().getContentRaw());
            event.getChannel().deleteMessageById(event.getMessageIdLong()).queue();
            if(event.getMember().getNickname() != null)
                event.getChannel().sendMessage("`** USER " + event.getMember().getUser().getName() + "#" + event.getMember().getUser().getDiscriminator() + " (" + event.getMember().getNickname() + ") ISSUED COMMAND " + event.getMessage().getContentRaw() +" **`").queue();
            else
                event.getChannel().sendMessage("`** USER " + event.getMember().getUser().getName() + "#" + event.getMember().getUser().getDiscriminator() + " ISSUED COMMAND " + event.getMessage().getContentRaw() +" **`").queue();;
            
            return;
    	}
    	else if(event.isFromType(ChannelType.PRIVATE))
    	{
            if(event.getMessage().getContentRaw().equals("/list"))
            {
                String returnMessage = "Online Players: ";

                for(Player player : Bukkit.getServer().getOnlinePlayers())
                {
                    returnMessage = returnMessage + player.getPlayer().getName() + " ";
                }
                event.getChannel().sendMessage(returnMessage).queue();
            }
    	}
    	else if(event.getTextChannel().getName().equals(MinecordPlugin.getInstance().config.getString("DiscordChatChannel")))
    	{
            if(event.getMessage().getContentRaw().equals("/list"))
            {
                String message = "Online Players: ";

                for(Player player : Bukkit.getServer().getOnlinePlayers())
                {
                    message = message + player.getPlayer().getName() + " ";
                }
                final String returnMessage = message;
                
                event.getChannel().deleteMessageById(event.getMessageIdLong()).queue();
                event.getAuthor().openPrivateChannel().queue(privateChannel -> { 
                    privateChannel.sendMessage(returnMessage).queue();
                });
                return;
            }

            String prefix = MinecordPlugin.getInstance().config.getString("RolePrefixes.Everyone");

            for(Role role : event.getMember().getRoles())
            {    		
                if(MinecordPlugin.getInstance().config.getString("RolePrefixes." + role.getName()) != null)
                {
                    prefix = MinecordPlugin.getInstance().config.getString("RolePrefixes." + role.getName());
                    break;
                }
            }

            event.getChannel().deleteMessageById(event.getMessageIdLong()).queue();

            if(event.getMessage().getContentRaw().length() > 256)
            {
                event.getAuthor().openPrivateChannel().queue(privateChannel -> { 
                    privateChannel.sendMessage("**ERROR: The message you just tried to send it over 256 characters! You sent:**").queue();
                    privateChannel.sendMessage(event.getMessage().getContentRaw()).queue();
                });
            }
            else
            {
                String nicknamePrefix = "";
                if(event.getMember().getNickname() != null)
                    nicknamePrefix = MinecordPlugin.getInstance().config.getString("NicknamePrefix");

                char firstChar = event.getMessage().getContentRaw().charAt(0);
                char lastChar = event.getMessage().getContentRaw().charAt(event.getMessage().getContentRaw().length() - 1);
                if((firstChar == '*' && lastChar == '*') || (firstChar == '_' && lastChar == '_'))
                {
                    Bukkit.broadcastMessage("§9> " + nicknamePrefix + event.getMember().getEffectiveName() + " " + event.getMessage().getContentStripped() + " <");
                    event.getChannel().sendMessage("> **" + nicknamePrefix + event.getMember().getEffectiveName() + "** " + event.getMessage().getContentStripped() + " <").queue();
                }
                else
                {
                    //Bukkit.broadcastMessage("§9<" + prefix + nicknamePrefix + event.getMember().getEffectiveName() + "> " + event.getMessage().getContentRaw());
                    Bukkit.broadcastMessage("§9<" + prefix + nicknamePrefix + event.getMember().getEffectiveName() + "> " + event.getMessage().getContentStripped());
                    event.getChannel().sendMessage("<" + "**" + nicknamePrefix + event.getMember().getEffectiveName() + "**> " + event.getMessage().getContentRaw()).queue();
                }
            }
    	}
    }
}