package com.walhalla.compose.model

import com.walhalla.appextractor.activity.assets.ResItem
import com.walhalla.appextractor.activity.assets.ResType

data class AssetTreeNode(
    val asset: ResItem,
    val level: Int,
    val children: List<AssetTreeNode> = emptyList()
) {
    val isBranch: Boolean
        get() = asset.type == ResType.Dir || children.isNotEmpty()

    val name: String
        get() = asset.fullPath.substringAfterLast('/').ifEmpty { asset.fullPath }
}

fun buildAssetTree(assets: List<ResItem>): List<AssetTreeNode> {
    val pathMap = mutableMapOf<String, MutableList<ResItem>>()

    assets.forEach { asset ->
        val path = asset.fullPath.substringBeforeLast('/', "")
        pathMap.getOrPut(path) { mutableListOf() }.add(asset)
    }

    fun buildNode(asset: ResItem, level: Int): AssetTreeNode {
        val children = pathMap[asset.fullPath]
            ?.sortedWith(compareBy({ it.type != ResType.Dir }, { it.fullPath.lowercase() }))
            ?.map { buildNode(it, level + 1) }
            ?: emptyList()
        return AssetTreeNode(asset, level, children)
    }

    return pathMap[""]
        ?.sortedWith(compareBy({ it.type != ResType.Dir }, { it.fullPath.lowercase() }))
        ?.map { buildNode(it, 0) }
        ?: emptyList()
}

/** Depth-first flatten of currently expanded branches for LazyColumn. */
fun List<AssetTreeNode>.flattenVisible(expandedPaths: Set<String>): List<AssetTreeNode> {
    val out = ArrayList<AssetTreeNode>()
    fun walk(node: AssetTreeNode) {
        out += node
        if (node.isBranch && node.asset.fullPath in expandedPaths) {
            node.children.forEach(::walk)
        }
    }
    forEach(::walk)
    return out
}

fun List<AssetTreeNode>.allBranchPaths(): Set<String> {
    val out = mutableSetOf<String>()
    fun walk(node: AssetTreeNode) {
        if (node.isBranch) {
            out += node.asset.fullPath
            node.children.forEach(::walk)
        }
    }
    forEach(::walk)
    return out
}
