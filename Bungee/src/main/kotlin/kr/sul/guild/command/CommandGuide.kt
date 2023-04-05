package kr.sul.guild.command

/*
class CommandGuide(rootNode: String) {
    private val rootNode = TreeNode(rootNode)

    fun register(command: GuildCommand) {
        val paths = CommandParser.getCommandPath(command::class.java, command::class.java.methods[0], AnnotationReader.create(
            BungeeHandler(plugin), command::class.java.methods[0]
        ))
        if (paths[0].name != rootNode.key) return
        paths.removeFirst()
        var current = rootNode
        for (path in paths.map { it.name }) {
            current = current.addChildIfNotExistAndGet(path)
            plugin.proxy.broadcast("${current.key} + $path")
        }
    }


    private fun sendCommandGuide(actor: BungeeActor, command: ExecutableCommand) {
        if (!actor.isPlayer) return
        val p = actor.requirePlayer()
        if (command.path.parent != rootNode.key) return
        var current = rootNode
        for (path in command.path.subcommandPath) {
            current = current.get(path) ?: return
        }
        for (matching in current)
//        val missingArgIndex = exception.parameter.methodIndex // TODO 고정 파라미터와 직접 넣는 <> 파라미터를 구분해야 함
//        val helpLine = (exception.command as GuildCommandWithHelp).getHelpLine(missingArgIndex)
//        p.sendMessage(helpLine)
    }






    class TreeNode(
        val key: String
    ) {
        private val childNode = hashMapOf<String, TreeNode>()
        fun isLeafNode(): Boolean {
            return childNode.isEmpty()
        }
        fun addChildIfNotExistAndGet(key: String): TreeNode {
            if (!childNode.containsKey(key)) {
                childNode[key] = TreeNode(key)
            }
            return childNode[key]!!
        }
        fun get(key: String): TreeNode? {
            return childNode[key]
        }
        fun print() {
            val parent = let {
                var str = ""
                var
                while() {

                }
            }
        }
    }
}*/