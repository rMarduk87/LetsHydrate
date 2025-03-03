package rpt.tool.mementobibere.utils.data.appmodel

class SoundModel {
    var id: Int = 0
    var name: String? = null
    var isSelected: Boolean = false
        private set

    fun isSelected(isSelected: Boolean) {
        this.isSelected = isSelected
    }
}