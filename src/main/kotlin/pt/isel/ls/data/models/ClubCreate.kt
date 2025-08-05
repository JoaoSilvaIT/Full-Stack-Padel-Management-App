package pt.isel.ls.data.models

data class ClubCreate(val name: String, val ownerId: Int) {
    init {
        require(ownerId > 0) { "ownerid must be greater than zero" }
        require(name.isNotBlank()) { "name must not be blank" }
    }
}
