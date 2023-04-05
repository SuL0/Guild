package kr.sul.guild.message

enum class EnumMessage(private val msg: String) {
    // CreateGuildCommand
    GUILD_NAME_OVERLAP("길드 이름이 중복됩니다."),
    CREATED_GUILD1("§b▣ §f혼자서는 거의 아무 것도 못한다. 함께 하면 우리는 그렇게 많은 것을 할 수 있다 §7 - 헬렌 켈러\n§b▣ §f왕관을 쓰려는 자, 그 무게를 견뎌라. §7- 셰익스피어\n§b▣ §f그래도 틈은 있다. 절대 강자, 약자는 없는 것이다."),
    CREATED_GUILD2("§2길드§f가 §c창설§f되었습니다. §a<{guildName}>"),
    YOU_ARE_NOT_IN_GUILD("당신은 길드에 속해있지 않습니다."),
    YOU_ARE_NOT_AN_OWNER_OF_GUILD("당신은 길드의 소유자가 아닙니다."),
    YOU_DONT_HAVE_PERMISSION_FOR_KICK("당신은 강퇴를 할 수 있는 권한을 가지고 있지 않습니다."),
    YOU_DONT_HAVE_PERMISSION_FOR_INVITE("당신은 초대를 할 수 있는 권한을 가지고 있지 않습니다."),
    GUILD_HAS_BEEN_DISBANDED("길드를 해산시켰습니다."),
    YOUR_GUILD_HAS_BEEN_DISBANDED("당신이 속한 길드가 해산되었습니다."),
    LEFT_FROM_GUILD("길드에서 탈퇴하였습니다."),
    YOU_ALREADY_BELONG_IN_GUILD("당신은 길드에 이미 속해있습니다."),
    PLAYER_WHO_HAVE_THAT_NAME_NOT_EXISTS_OR_IN_OFFLINE("현재 서버에 해당 닉네임을 가진 플레이어가 존재하지 않거나 온라인이 아닙니다."),
    GUILD_MEMBERS_EXCEEDED("길드의 최대 인원이 초과하였습니다."),
    KICKED_PLAYER("{player} 님을 길드에서 추방하였습니다."),
    KICKED_FROM_GUILD("{player} 님에 의해 길드에서 추방되었습니다."),
    THIS_COMMAND_CAN_BE_PERFORMED_TO_WHO_HAS_LOWER_RANK("해당 명령어는 당신과 같거나 더 높은 계급을 가진 구성원에게 적용할 수 없습니다."),
    GUILD_OWNER_IS_BLOCKED_FROM_LEAVING_GUILD("길드 소유자는 소유권을 넘긴 후 탈퇴하거나, 길드를 해산하여야 합니다."),
    DENY_INVITATION("초대를 거절하였습니다"),
//    ACCEPT_INVITATION("초대를 수락하였습니다"),
    INVITATION_ARRIVED("§a{guildName} §2길드§f로부터 §c초대§f받았습니다. "),
    CANNOT_FIND_THAT_INVITATION("해당 초대장을 찾을 수 없습니다"),
    YOU_ARE_NOW_A_GUILD_MEMBER("{guildName} 길드에 가입하였습니다."),
    NEW_GUILD_MEMBER_IS_ARRIVED("{player} 님이 길드에 들어왔습니다."),
    SENT_INVITATION("{player} 님에게 길드 초대를 보냈습니다."),
    GUILD_NAME_SHOULD_NOT_CONTAIN_BLANK_OR_SPECIAL_CHARACTER("길드 이름에는 특수문자나 공백이 포함되면 안됩니다."),
    GUILD_NAME_MUST_NOT_BE_MORE_THAN_FOUR_LETTERS("길드 이름의 길이는 4글자를 넘지 않아야 합니다."),
    SOMEONE_CREATED_GUILD("새로운 §c오피니언 §7({player}) §f이(가) 나타났습니다 §a<{guildName}> 길드"),
    COMMAND_COOLDOWN("해당 명령어는 {cooldown}초 뒤에 다시 사용할 수 있습니다."),
    GUILD_CHAT_ON("§2길드 채팅 모드§f로 §c변경§f되었습니다."),
    GUILD_CHAT_OFF("§e전체 채팅 모드§f로 §c변경§f되었습니다."),
    YOU_DONT_HAVE_ENOUGH_MONEY_FOR_CREATE_GUILD("§c길드 창설에 필요한 돈({money})이 부족합니다. ");

    companion object {
        private const val PREFIX = "§f"
        private const val PREFIX_TO_ALL = "§b[모두에게]"
    }

    fun getProcessedMessage(vararg placeHolderReplaces: PlaceHolderReplace): String {
        var modifiedMessage = msg
        placeHolderReplaces.forEach {
            modifiedMessage = it.apply(modifiedMessage)
        }
        return modifiedMessage
    }
}