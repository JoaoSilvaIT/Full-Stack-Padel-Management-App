package pt.isel.ls.data.models

data class ClubUpdate(val name: String) {
    init {
        require(name.isNotBlank()) { "name must not be blank" }
    }
}
