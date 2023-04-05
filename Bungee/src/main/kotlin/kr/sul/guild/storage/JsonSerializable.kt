package kr.sul.guild.storage

interface JsonSerializable<T> {
    fun load(jsonStr: String): T
    fun save(): String
}