package pt.isel.ls.data.models

data class CourtCreate(val name: String, val club: Int, val uid: Int) {
    init {
        require(name.isNotBlank()) { "name must not be blank" }
        require(uid > 0) { "uid must be greater than zero" }
        require(club > 0) { "club must be greater than zero" }
    }
}
