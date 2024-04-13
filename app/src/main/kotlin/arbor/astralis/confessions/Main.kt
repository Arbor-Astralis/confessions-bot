package arbor.astralis.confessions

import dev.kord.common.Color
import dev.kord.common.entity.ButtonStyle
import dev.kord.common.entity.CommandArgument
import dev.kord.common.entity.Permission
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.edit
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.cache.data.InteractionData
import dev.kord.core.cache.data.OptionData
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.entity.interaction.ApplicationCommandInteraction
import dev.kord.core.entity.interaction.ButtonInteraction
import dev.kord.core.event.interaction.ApplicationCommandInteractionCreateEvent
import dev.kord.core.event.interaction.ButtonInteractionCreateEvent
import dev.kord.core.on
import dev.kord.rest.builder.message.actionRow
import dev.kord.rest.builder.message.embed

suspend fun main(args: Array<String>) {
    Settings.initialize()
    
    val kord = Kord(Settings.BOT_TOKEN!!)

    registerCommands(kord);
    registerCommandHandlers(kord);
    
    kord.login()
}

suspend fun registerCommandHandlers(kord: Kord) {
    kord.on<ApplicationCommandInteractionCreateEvent> {
        handleCommandReceived(interaction.invokedCommandName, interaction.data.data.options, interaction, kord)
    }

    kord.on<ButtonInteractionCreateEvent> {
        handleButtonInteraction(interaction, kord)
    }
}

suspend fun handleCommandReceived(
    name: String,
    options: Optional<List<OptionData>>,
    interaction: ApplicationCommandInteraction,
    kord: Kord
) {
    if (name == COMMAND_CONFESS) {
        handleConfession(options, interaction, kord)
    } else if (name == COMMAND_DEFINE_CONFESSION_CHANNEL && options.value != null) {
        handleDefineConfessionChannel(options, interaction, kord)
    } else if (name == COMMAND_DEFINE_CONFESSION_MOD_CHANNEL) {
        handleDefineConfessionModChannel(options, interaction, kord)
    } else if (name == COMMAND_UNDEFINE_CONFESSION_MOD_CHANNEL) {
        handleUndefineConfessionModChannel(options, interaction, kord)
    }
}

suspend fun handleUndefineConfessionModChannel(
    options: Optional<List<OptionData>>,
    interaction: ApplicationCommandInteraction,
    kord: Kord
) {
    val guildId = interaction.data.guildId.value!!

    // Validate user has permissions
    val userPermissions = kord.getGuild(guildId).getMember(interaction.user.id).getPermissions()
    if (!userPermissions.contains(Permission.Administrator)) {
        interaction.respondPublic {
            content = DEFINE_CONFESSIONS_CHANNEL_NO_PERMS_RESPONSES.random()
        }

        return
    }

    val guildSettings = Settings.getForGuild(guildId)
    guildSettings.confessionsModChannelId = null
    Settings.persistForGuild(guildSettings)

    interaction.respondPublic {
        content = UNDEFINE_CONFESSIONS_MOD_CHANNEL_SUCCESSFUL_RESPONSES.random()
    }
}

suspend fun handleDefineConfessionModChannel(
    options: Optional<List<OptionData>>,
    interaction: ApplicationCommandInteraction,
    kord: Kord
) {
    val channelArgument = options.value!![0].value.value as CommandArgument.ChannelArgument
    val channelId = channelArgument.value.value

    val guildId = interaction.data.guildId.value!!
    
    // Validate user has permissions
    val userPermissions = kord.getGuild(guildId).getMember(interaction.user.id).getPermissions()
    if (!userPermissions.contains(Permission.Administrator)) {
        interaction.respondPublic { 
            content = DEFINE_CONFESSIONS_CHANNEL_NO_PERMS_RESPONSES.random()
        }

        return
    }

    val guildSettings = Settings.getForGuild(guildId)
    guildSettings.confessionsModChannelId = channelId.toString()
    Settings.persistForGuild(guildSettings)

    interaction.respondPublic {
        val message = DEFINE_CONFESSIONS_MOD_CHANNEL_RESPONSES.random()
        content = message.format("<#${channelId}>")
    }
}

suspend fun handleDefineConfessionChannel(
    options: Optional<List<OptionData>>,
    interaction: ApplicationCommandInteraction,
    kord: Kord
) {
    val channelArgument = options.value!![0].value.value as CommandArgument.ChannelArgument
    val channelId = channelArgument.value.value

    val guildId = interaction.data.guildId.value!!
    
    // Validate user has permissions
    val userPermissions = kord.getGuild(guildId).getMember(interaction.user.id).getPermissions()
    if (!userPermissions.contains(Permission.Administrator)) {
        interaction.respondPublic {
            content = DEFINE_CONFESSIONS_CHANNEL_NO_PERMS_RESPONSES.random()
        }
        
        return
    }
    
    val guildSettings = Settings.getForGuild(guildId)
    guildSettings.confessionsChannelId = channelId.toString()
    Settings.persistForGuild(guildSettings)
    
    interaction.respondPublic {
        val message = DEFINE_CONFESSIONS_CHANNEL_SUCCESSFUL_RESPONSES.random()
        content = message.format("<#${channelId}>")
    }
}

suspend fun handleConfession(options: Optional<List<OptionData>>, interaction: ApplicationCommandInteraction, kord: Kord) {
    val guildId = interaction.data.guildId.value!!
    val guildSettings = Settings.getForGuild(guildId)
    
    if (guildSettings.confessionsChannelId == null) {
        interaction.respondPublic {
            content = CONFESSION_FAIL_NO_SETUP.random()
        }
        
        return
    }
    
    val confessionMessageArgument = options.value!!.get(0).value.value as CommandArgument.StringArgument
    val confessionMessage = confessionMessageArgument.value
    
    if (guildSettings.confessionsModChannelId == null) {
        getConfessionsChannel(guildSettings, kord)!!.createEmbed {
            description = confessionMessage
            color = createRandomColor()
        }

        interaction.respondEphemeral { content = CONFESSION_SUCCESS_NO_MOD.random() }
    } else {
        getConfessionsModChannel(guildSettings, kord)!!.createMessage {
            content = CONFESSION_MOD_REQUEST_GREETINGS.random()
            embed {
                description = confessionMessage
            }
            actionRow {
                interactionButton(ButtonStyle.Primary, BUTTON_ACCEPT_CONFESSION_ID) {
                    label = "Accept"
                }
                interactionButton(ButtonStyle.Danger, BUTTON_REJECT_CONFESSION_ID) {
                    label = "Reject"
                }
            }
        }

        interaction.respondEphemeral { content = CONFESSION_SUCCESS_REQUIRE_MOD.random() }
    }
}

suspend fun handleButtonInteraction(interaction: ButtonInteraction, kord: Kord) {
    if (interaction.componentId == BUTTON_ACCEPT_CONFESSION_ID) {
        handleAcceptConfession(interaction.data, interaction, kord)
        completeConfessionMod("accept", interaction)
    } else if (interaction.componentId == BUTTON_REJECT_CONFESSION_ID) {
        handleRejectConfession(interaction.data, interaction, kord)
        completeConfessionMod("reject", interaction)
    }
}

suspend fun handleAcceptConfession(data: InteractionData, interaction: ButtonInteraction, kord: Kord) {
    val guildId = data.guildId.value!!
    val guildSettings = Settings.getForGuild(guildId)
    
    getConfessionsChannel(guildSettings, kord)!!.createMessage { 
        embed { 
            description = interaction.message.embeds.get(0).description
            color = createRandomColor()
        }
    }

    interaction.respondEphemeral {
        content = CONFESSION_MOD_ACCEPT_RESPONSE.random()
    }
}


suspend fun handleRejectConfession(data: InteractionData, interaction: ButtonInteraction, kord: Kord) {
    // TODO: FIXME this is being sent to the person interacting with the button and not the confessor
    //       Possibly need to track the confessor in a database.
    
//    val userDmChannel = kord.getGuild(data.guildId.value!!).getMember(interaction.user.id).getDmChannelOrNull()
//    
//    if (userDmChannel != null) {
//        val confessionMessage = interaction.message.embeds.get(0).description
//        val introLine = CONFESSION_USER_REJECT_RESPONSE.random()
//        
//        userDmChannel.createMessage {
//            content = CONFESSION_USER_DM_TEMPLATE.format(introLine, confessionMessage)
//        }
//    }

    interaction.respondEphemeral {
        content = CONFESSION_MOD_REJECT_RESPONSE.random()
    }
}

suspend fun completeConfessionMod(resolution: String, interaction: ButtonInteraction) {
    interaction.message.edit {
        content = "<@${interaction.user.id}> told me to $resolution this:"
        
        actionRow {
            interactionButton(ButtonStyle.Primary, BUTTON_ACCEPT_CONFESSION_ID) {
                label = "Accept"
                disabled = true
            }
            interactionButton(ButtonStyle.Danger, BUTTON_REJECT_CONFESSION_ID) {
                label = "Reject"
                disabled = true
            }
        }
    }
}

suspend fun getConfessionsChannel(guildSettings: GuildSettings, kord: Kord) : MessageChannel? {
    return getMessageChannel(guildSettings.confessionsChannelId, guildSettings.guildId, kord)
}

suspend fun getConfessionsModChannel(guildSettings: GuildSettings, kord: Kord) : MessageChannel? {
    return getMessageChannel(guildSettings.confessionsModChannelId, guildSettings.guildId, kord) 
}

suspend fun getMessageChannel(channelId: String?, guildId: Snowflake, kord: Kord): MessageChannel? {
    if (channelId == null)
        return null;

    return (kord.getGuild(guildId).getChannel(Snowflake(channelId)) as MessageChannel)
}

fun createRandomColor(): Color {
    return Color(
        (Math.random() * 255).toInt(),
        (Math.random() * 255).toInt(),
        (Math.random() * 255).toInt()
    )
}
