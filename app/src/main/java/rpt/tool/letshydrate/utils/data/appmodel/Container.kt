package rpt.tool.letshydrate.utils.data.appmodel

class Container {
    var containerId: String? = null
    var containerValue: String? = null
    var containerValueOZ: String? = null
    var isSelected: Boolean = false
        private set
    var isOpen: Boolean = false
        private set
    var isCustom: Boolean = false
        private set

    constructor()

    constructor(
        containerId: String?,
        containerValue: String?,
        isSelected: Boolean,
        isOpen: Boolean,
        isCustom: Boolean
    ) {
        this.isSelected = isSelected
        this.containerValue = containerValue
        this.containerId = containerId
        this.isOpen = isOpen
        this.isCustom = isCustom
    }

    fun isCustom(isCustom: Boolean) {
        this.isCustom = isCustom
    }

    fun isSelected(isSelected: Boolean) {
        this.isSelected = isSelected
    }

    fun isOpen(isOpen: Boolean) {
        this.isOpen = isOpen
    }
}