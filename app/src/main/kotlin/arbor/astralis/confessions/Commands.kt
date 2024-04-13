package arbor.astralis.confessions

import dev.kord.common.entity.ChannelType
import dev.kord.core.Kord
import dev.kord.rest.builder.interaction.ChannelBuilder
import dev.kord.rest.builder.interaction.StringChoiceBuilder

suspend fun registerCommands(kord: Kord) {
    createConfessionCommand(kord);
    createModerationCommands(kord);
}

private suspend fun createConfessionCommand(kord: Kord) {
    val messageInput = StringChoiceBuilder(COMMAND_CONFESS_OPTION_MESSAGE, "The message to be shown");
    messageInput.minLength = 1;
    messageInput.maxLength = 2048;
    messageInput.required = true;

    kord.createGlobalChatInputCommand(COMMAND_CONFESS, "Confess a secret to $BRAND_NAME") {
        options = mutableListOf(messageInput)
    }
}

private suspend fun createModerationCommands(kord: Kord) {
    // set-confession-channel
    val channelList = ChannelBuilder(COMMAND_DEFINE_CONFESSION_CHANNEL_OPTION_CHANNEL, "The channel for $BRAND_NAME to broadcast confessions");
    channelList.channelTypes = listOf(ChannelType.GuildText)
    channelList.required = true;
    
    kord.createGlobalChatInputCommand(COMMAND_DEFINE_CONFESSION_CHANNEL, "Set the confession channel for $BRAND_NAME") {
        options = mutableListOf(channelList)
    }
    
    // set-confession-mod-channel
    val modChannelList = ChannelBuilder(COMMAND_DEFINE_CONFESSION_MOD_CHANNEL_OPTION_CHANNEL, "The channel for $BRAND_NAME to submit confessions for approval");
    modChannelList.channelTypes = listOf(ChannelType.GuildText)
    modChannelList.required = true;
    
    kord.createGlobalChatInputCommand(COMMAND_DEFINE_CONFESSION_MOD_CHANNEL, "(Optional) Set the channel for $BRAND_NAME to post confessions for approval first") {
        options = mutableListOf(modChannelList)
    }
}