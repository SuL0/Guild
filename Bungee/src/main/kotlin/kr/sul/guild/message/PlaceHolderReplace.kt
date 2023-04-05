package kr.sul.guild.message

class PlaceHolderReplace(
    private val targetPlaceHolder: String,
    private val to: String
) {
    fun apply(str: String): String {
        return str.replace(targetPlaceHolder, to)
    }
}
