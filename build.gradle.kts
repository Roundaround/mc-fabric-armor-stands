plugins {
  id("roundalib") version "0.3.6"
}

repositories {
  maven("https://jitpack.io")
}

dependencies {
  implementation("com.github.LlamaLad7:MixinExtras:0.1.1")
  annotationProcessor("com.github.LlamaLad7:MixinExtras:0.1.1")
  include("com.github.LlamaLad7:MixinExtras:0.1.1")
}
