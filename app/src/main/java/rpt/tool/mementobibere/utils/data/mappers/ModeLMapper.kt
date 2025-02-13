package rpt.tool.mementobibere.utils.data.mappers

interface ModelMapper<Source, Destination> {
    val destination: Class<Destination>

    fun map(source: Source): Destination
}