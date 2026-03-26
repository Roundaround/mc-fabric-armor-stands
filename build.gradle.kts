import me.roundaround.gradle.extension.library.LibModule

plugins {
  id("roundalib-gradle") version "1.1.0"
}

roundalib {
  library {
    local = true
    version = "3.5.0"
    modules.addAll(LibModule.CORE, LibModule.GUI, LibModule.CONFIG, LibModule.CONFIG_GUI, LibModule.OBSERVABLES)
  }
}
