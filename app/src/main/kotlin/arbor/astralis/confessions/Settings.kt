package arbor.astralis.confessions

import co.touchlab.stately.concurrency.synchronize
import dev.kord.common.entity.Snowflake
import java.io.BufferedWriter
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import kotlin.collections.HashMap
import kotlin.system.exitProcess

sealed class Settings {
    
    companion object {
        
        var SETTINGS_DIRECTORY: Path = Paths.get(System.getProperty("user.dir")).resolve("confessions-bot")  
        var BOT_TOKEN: String? = null
        
        // TODO: need upper bound if bot is to be mass-distributed
        private val SETTINGS_CACHE = HashMap<Snowflake, GuildSettings>()
        
        
        fun initialize() {
            if (!Files.exists(SETTINGS_DIRECTORY)) {
                Files.createDirectories(SETTINGS_DIRECTORY)
            }
            
            // Initialize bot token
            BOT_TOKEN = System.getenv("ARBOR_CONFESSIONS_BOT_TOKEN");
            
            if (BOT_TOKEN == null) {
                val botKeyFile = SETTINGS_DIRECTORY.resolve("bot-token.txt")

                if (!Files.exists(botKeyFile)) {
                    println("Missing bot token! Please configure: ${botKeyFile.toAbsolutePath()}")
                    exitProcess(0)
                }

                val scanner = Scanner(botKeyFile.toAbsolutePath())
                
                if (!scanner.hasNextLine()) {
                    println("Missing bot token! Please configure: ${botKeyFile.toAbsolutePath()}")
                    exitProcess(0)
                }
                
                BOT_TOKEN = scanner.nextLine().replace("\n", "")
            }
        }
        
        fun persistForGuild(settings: GuildSettings) {
            val guildSettingsFile = getGuildSettingsFile(settings.guildId)
            
            if (!Files.exists(guildSettingsFile) || Files.isDirectory(guildSettingsFile)) {
                Files.createFile(guildSettingsFile)
            }
            
            val writer = Files.newBufferedWriter(guildSettingsFile)
            
            writeSetting(settings.confessionsChannelId, writer)
            writeSetting(settings.confessionsModChannelId, writer)
            
            writer.flush()
            writer.close()
        }

        private fun writeSetting(channelId: String?, writer: BufferedWriter) {
            if (channelId != null) {
                writer.write(channelId)
            } else {
                writer.write("")
            }

            writer.newLine()
        }


        fun getForGuild(guildId: Snowflake) : GuildSettings {
            var settings: GuildSettings? = null
            
            synchronize { 
                settings = SETTINGS_CACHE.compute(guildId) { key, initialValue ->
                    loadGuildSettings(guildId)
                }!!
            }
            
            return settings!!
        }

        private fun loadGuildSettings(guildId: Snowflake): GuildSettings {
            val guildSettings = GuildSettings(guildId)
            val guildSettingsFile = getGuildSettingsFile(guildId)
            
            if (Files.exists(guildSettingsFile) && !Files.isDirectory(guildSettingsFile)) {
                val scanner = Scanner(guildSettingsFile)

                if (scanner.hasNextLine()) {
                    val channelId = scanner.nextLine()
                    guildSettings.confessionsChannelId = channelId.ifBlank { null }
                }

                if (scanner.hasNextLine()) {
                    val channelId = scanner.nextLine()
                    guildSettings.confessionsModChannelId = channelId.ifBlank { null }
                }
            }
            
            return guildSettings
        }

        private fun getGuildSettingsFile(guildId: Snowflake): Path {
            return SETTINGS_DIRECTORY.resolve("guild_${guildId.value}.txt")
        }
    }
}

class GuildSettings constructor(
    val guildId: Snowflake,
    var confessionsChannelId: String?, 
    var confessionsModChannelId: String?
) {
    constructor(guildId: Snowflake) : this(guildId, null, null) {
    }
}