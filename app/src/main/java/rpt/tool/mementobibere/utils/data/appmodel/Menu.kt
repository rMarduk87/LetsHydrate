package rpt.tool.mementobibere.utils.data.appmodel

class Menu
    (menuName: String, isSelected: Boolean) {
    var menuName: String
    var isSelected: Boolean = false
        private set

    init {
        this.isSelected = isSelected
        this.menuName = menuName
    }

    fun isSelected(isSelected: Boolean) {
        this.isSelected = isSelected
    }
}