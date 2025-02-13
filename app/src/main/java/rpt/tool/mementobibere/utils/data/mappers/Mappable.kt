package rpt.tool.mementobibere.utils.data.mappers

import rpt.tool.mementobibere.utils.data.AppModel
import rpt.tool.mementobibere.utils.data.DbModel

abstract class Mappable {

    open var mappers: ArrayList<ModelMapper<Mappable, *>> = arrayListOf()


    inline fun <reified T> map(): T {
        return mappers.singleOrNull { it.destination == T::class.java }?.map(this) as? T
            ?: throw IllegalArgumentException("Mapper not found!")
    }

    open fun <T : AppModel> toAppModel(): T {
        return mappers.singleOrNull { it.destination.superclass == AppModel::class.java }
            ?.map(this) as? T
            ?: throw IllegalArgumentException("Mapper not found!")
    }

    open fun <T : DbModel> toDBModel(): T {
        return mappers.singleOrNull { it.destination.superclass == DbModel::class.java }
            ?.map(this) as? T
            ?: throw IllegalArgumentException("Mapper not found!")
    }
}

fun <T : Mappable> T.addMapper(mapper: ModelMapper<T, *>) {
    if (this.mappers == null) {
        this.mappers = arrayListOf()
    }
    this.mappers.add(mapper as ModelMapper<Mappable, *>)
}