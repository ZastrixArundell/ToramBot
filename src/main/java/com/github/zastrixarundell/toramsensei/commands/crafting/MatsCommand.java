package com.github.zastrixarundell.toramsensei.commands.crafting;

import com.github.zastrixarundell.toramsensei.Parser;
import com.github.zastrixarundell.toramsensei.commands.DiscordCommand;
import com.github.zastrixarundell.toramsensei.commands.search.items.DiscordItemCommand;
import com.github.zastrixarundell.toramsensei.objects.toram.items.Item;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;

public class MatsCommand extends DiscordCommand
{

    public MatsCommand()
    {
        super("recipe", "mats");
    }

    @Override
    protected void runCommand(MessageCreateEvent event)
    {
        ArrayList<String> arguments = Parser.argumentsParser(event);

        if (arguments.isEmpty())
        {
            emptySearch(event);
            return;
        }

        String data = String.join(" ", arguments);

        Runnable runnable = () ->
        {
            ArrayList<Item> allItems = new ArrayList<>();

            try
            {
                Document document = Jsoup.connect("http://coryn.club/item.php")
                        .data("name", data)
                        .data("special", "nalch")
                        .get();

                Element cardContainer = document.getElementsByClass("card-container").first();

                allItems.addAll(DiscordItemCommand.getItems(cardContainer));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            try
            {
                Document document = Jsoup.connect("http://coryn.club/item.php")
                        .data("name", data)
                        .data("special", "nsmith")
                        .get();

                Element cardContainer = document.getElementsByClass("card-container").first();

                allItems.addAll(DiscordItemCommand.getItems(cardContainer));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            if (allItems.isEmpty())
            {
                sendErrorMessage(event);
                return;
            }

            for (int i = 0; i < allItems.size() && i < 5; i++)
                sendItemEmbed(allItems.get(i), event);

        };

        executeRunnable(event, runnable);
    }

    private void emptySearch(MessageCreateEvent messageCreateEvent)
    {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Empty search!")
                .setDescription("You can not find the recipe of an item without specifying the item!");

        Parser.parseThumbnail(embed, messageCreateEvent);
        Parser.parseFooter(embed, messageCreateEvent);
        Parser.parseColor(embed, messageCreateEvent);

        messageCreateEvent.getChannel().sendMessage(embed);
    }

    private void sendErrorMessage(MessageCreateEvent messageCreateEvent)
    {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Error while getting item mats!")
                .setDescription("An error happened! Does the specified!mats aaaa" +
                        "item even exist? The item may not be added yet.");

        Parser.parseThumbnail(embed, messageCreateEvent);
        Parser.parseFooter(embed, messageCreateEvent);
        Parser.parseColor(embed, messageCreateEvent);

        messageCreateEvent.getChannel().sendMessage(embed);
    }

    private void sendItemEmbed(Item item, MessageCreateEvent messageCreateEvent)
    {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(item.getName());

        String mats = String.join("\n", item.getMats());

        embed.addField("Recipe:", mats);

        if (item.getApp() != null)
            embed.setThumbnail(item.getApp());
        else
            Parser.parseThumbnail(embed, messageCreateEvent);

        Parser.parseFooter(embed, messageCreateEvent);
        Parser.parseColor(embed, messageCreateEvent);

        messageCreateEvent.getChannel().sendMessage(embed);
    }
}
