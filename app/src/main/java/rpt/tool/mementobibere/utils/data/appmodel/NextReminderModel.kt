package rpt.tool.mementobibere.utils.data.appmodel

class NextReminderModel(val millesecond: Long, val time: String) :
    Comparable<NextReminderModel?> {


    override fun compareTo(other: NextReminderModel?): Int {
        return if (millesecond > other!!.millesecond) {
            1
        } else if (millesecond < other.millesecond) {
            -1
        } else {
            0
        }
    }

    override fun toString(): String {
        return this.time
    }
}