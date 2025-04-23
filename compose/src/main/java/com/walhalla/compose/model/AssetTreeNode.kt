package com.walhalla.compose.model

import com.walhalla.appextractor.activity.assets.ResItem

data class AssetTreeNode(
    val asset: ResItem,
    val level: Int,
    val children: List<AssetTreeNode> = emptyList(),
    val isExpanded: Boolean = false
)

fun buildAssetTree(assets: List<ResItem>): List<AssetTreeNode> {
    val rootNodes = mutableListOf<AssetTreeNode>()
    val pathMap = mutableMapOf<String, MutableList<ResItem>>()
    
    // Группируем ассеты по директориям
    assets.forEach { asset ->
        val path = asset.fullPath.substringBeforeLast('/', "")
        pathMap.getOrPut(path) { mutableListOf() }.add(asset)
    }
    
    // Строим дерево рекурсивно
    fun buildNode(asset: ResItem, level: Int): AssetTreeNode {
        val children = pathMap[asset.fullPath]?.map { buildNode(it, level + 1) } ?: emptyList()
        return AssetTreeNode(asset, level, children)
    }
    
    // Создаем корневые узлы
    pathMap[""]?.forEach { asset ->
        rootNodes.add(buildNode(asset, 0))
    }
    
    return rootNodes
} 