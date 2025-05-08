import javax.inject.Inject

class Param
    @Inject
    constructor()

class VeryImportedImplementation
    @Inject
    constructor(val param: Param)
