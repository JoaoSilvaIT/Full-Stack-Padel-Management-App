package pt.isel.ls.utils

import kotlinx.serialization.Serializable

@Serializable
class PaginatedList<T> private constructor(
    val hasNext: Boolean,
    val hasPrevious: Boolean,
    val list: List<T>,
) : AbstractList<T>() {
    private var skip: Int = 0

    private var limit: Int = 0

    override val size = list.size

    override fun get(index: Int): T = list[index]

    override fun isEmpty(): Boolean = list.isEmpty()

    fun <R> map(transform: (T) -> R) = PaginatedList<R>(hasNext, hasPrevious, list.map(transform))

    companion object {
        fun <T> fromFullList(
            list: List<T>,
            skip: Int,
            limit: Int,
        ): PaginatedList<T> {
            val hasNext = skip + limit < list.size
            val hasPrevious = skip > 0
            val paginatedList = PaginatedList(hasNext, hasPrevious, list.paginate(skip, limit))
            paginatedList.skip = skip
            paginatedList.limit = limit
            return paginatedList
        }

        fun <T> fromList(
            list: List<T>,
            skip: Int,
            limit: Int,
        ): PaginatedList<T> {
            println("fromList: $skip, $limit, with list: $list")
            val hasNext = limit < list.size
            val hasPrevious = skip > 0
            val paginatedList = PaginatedList(hasNext, hasPrevious, list.subList(0, minOf(limit, list.size)))
            paginatedList.skip = skip
            paginatedList.limit = limit
            return paginatedList
        }

        private fun <T> List<T>.paginate(
            skip: Int,
            limit: Int,
        ): List<T> {
            require(skip >= 0) { "skip must be non-negative" }
            require(limit >= 0) { "limit must be non-negative" }
            if (isEmpty() || skip >= size) return emptyList()
            return subList(skip, minOf(skip + limit, size))
        }
    }
}
