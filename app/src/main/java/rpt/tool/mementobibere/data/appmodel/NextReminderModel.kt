package rpt.tool.mementobibere.data.appmodel

class NextReminderModel(val millesecond: Long, val time: String) :
    Comparable<NextReminderModel?> {


    override fun compareTo(f: NextReminderModel?): Int {
        return if (millesecond > f!!.millesecond) {
            1
        } else if (millesecond < f.millesecond) {
            -1
        } else {
            0
        }
    }

    override fun toString(): String {
        return this.time
    }
}