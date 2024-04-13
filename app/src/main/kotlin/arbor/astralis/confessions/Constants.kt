package arbor.astralis.confessions

// Commands
val COMMAND_CONFESS = "confess"
val COMMAND_CONFESS_OPTION_MESSAGE = "message"

val COMMAND_DEFINE_CONFESSION_CHANNEL = "set-confession-channel"
val COMMAND_DEFINE_CONFESSION_CHANNEL_OPTION_CHANNEL = "channel"

val COMMAND_DEFINE_CONFESSION_MOD_CHANNEL = "set-confession-mod-channel"
val COMMAND_DEFINE_CONFESSION_MOD_CHANNEL_OPTION_CHANNEL = "channel"

val COMMAND_UNDEFINE_CONFESSION_MOD_CHANNEL = "unset-confession-mod-channel"

// Button IDs
val BUTTON_ACCEPT_CONFESSION_ID = "accept_confession"
val BUTTON_REJECT_CONFESSION_ID = "reject_confession"

// Branding
val BRAND_NAME = "Tsukasa"

val DEFINE_CONFESSIONS_CHANNEL_SUCCESSFUL_RESPONSES = listOf(
    "M'kay, confessions will now go to %s",
    "Okie, I'll say confessions in %s from n'eow on",
    "Aye aye, %s? I gotchu",
    "Oowooah %s?? I can't wait!",
    "%s? You can count on me!",
    "Ehihi, %s, this will be fun...!",
)

val DEFINE_CONFESSIONS_CHANNEL_NO_PERMS_RESPONSES = listOf(
    "Aye-aye naughty boy? Only admins can use this! Or perhaps you should be admin first?"
)

val DEFINE_CONFESSIONS_MOD_CHANNEL_RESPONSES = listOf(
    "M'kay, from now on I'll forward confessions to %s for approvals",
    "Okie, I'll ask in %s before announcing them from now on",
    "Aye aye, file an approval in %s? I gotchu",
    "Members been naughty? No more freedom of speech? I love it. %s it is!",
    "%s? You can count on me!",
)

val UNDEFINE_CONFESSIONS_MOD_CHANNEL_SUCCESSFUL_RESPONSES = listOf(
    "M'kay, confessions will no longer require approvals n'eow~",
    "If you say so. Freedom of speech hooray!!"
)

val CONFESSION_SUCCESS_NO_MOD = listOf(
    "No problem!",
    "All done!",
    "There ya go!!",
    "You can count on me!",
    "Right as rain~"
)

val CONFESSION_SUCCESS_REQUIRE_MOD = listOf(
    "No problem! It will be reviewed soon~",
    "All done -- allow some time for it to be reviewed!"
)

val CONFESSION_MOD_REQUEST_GREETINGS = listOf(
    "Hai hai mina-san~ got a new confession for y'all",
    "Well well? Does this look okay to you?",
    "I think this is okay, but I donno...",
)

val CONFESSION_MOD_ACCEPT_RESPONSE = listOf(
    "Aweshome! I'll post it now~",
    "Wooah, time to confess!",
    "Okay, I'll go post it",
    "I thought it's a good one too~",
    "Eheh, I love this one too"
)

val CONFESSION_MOD_REJECT_RESPONSE = listOf(
    "Nawh, I thought that was okay...",
    "Awww that's a shame....",
    "Naurrr, that's too bad...",
    "Weally? Was it that bad?",
    "Uh-huh.... I guess it wasn't okay after all.",
    "Wahh, they're gonna be sad! But it is whatever."
)

val CONFESSION_USER_REJECT_RESPONSE = listOf(
    "Hey hey, so I was told that we cannot post this confession you made.",
    "Yoohoo, I wasn't allowed to submit this recent confession you made.",
    "Nee nee~ I tried my best but your recent confession was still turned down:"
)

val CONFESSION_USER_DM_TEMPLATE = "" +
        "%s " +
        "```%s```\n" +
        "Word goes that the council does not endorse confessions that are:\n" +
        "- Vent, hate speech or other overly negative sentiments -- these go to the vent channel so members can respond to you properly.\n" +
        "- Heavy topics such as rape, suicide, self-harm -- these will not be accepted by any means.\n" +
        "- (Suspected of) making references to other members in the server, especially if these are putting them in negative light.\n" +
        "\n" +
        "... in other words, they're trying to keep the confessions channel for more wholesome things! Better luck next time~"

val CONFESSION_FAIL_NO_SETUP = listOf(
    "Oopsie, the confessions channel is not setup. Ask an administrator to set one up with the `/$COMMAND_DEFINE_CONFESSION_CHANNEL` command",
)