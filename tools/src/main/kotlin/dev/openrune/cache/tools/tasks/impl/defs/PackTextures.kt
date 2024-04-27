package dev.openrune.cache.tools.tasks.impl.defs

import cc.ekblad.toml.decode
import cc.ekblad.toml.tomlMapper
import com.displee.cache.CacheLibrary
import dev.openrune.cache.SPRITES
import dev.openrune.cache.TEXTURES
import dev.openrune.cache.filestore.buffer.BufferWriter
import dev.openrune.cache.filestore.definition.data.TextureDefinition
import dev.openrune.cache.filestore.definition.decoder.TextureDecoder
import dev.openrune.cache.filestore.definition.encoder.TextureEncoder
import dev.openrune.cache.tools.tasks.CacheTask
import dev.openrune.cache.tools.tasks.impl.PackSprites.Companion.customSprites
import dev.openrune.cache.tools.tasks.impl.sprites.SpriteSet
import dev.openrune.cache.tools.tasks.impl.sprites.SpriteSet.Companion.averageColorForPixels
import dev.openrune.cache.util.getFiles
import dev.openrune.cache.util.progress
import io.github.oshai.kotlinlogging.KotlinLogging
import io.netty.buffer.Unpooled
import java.io.File
import java.lang.reflect.Modifier

class PackTextures(private val textureDir : File) : CacheTask() {

    val logger = KotlinLogging.logger {}

    val mapper = tomlMapper {}

    override fun init(library: CacheLibrary) {
        val size = getFiles(textureDir,"toml").size
        val progress = progress("Packing Textures", size)
        if (size != 0) {

            getFiles(textureDir,"toml").forEach {

                var def = mapper.decode<TextureDefinition>(it.toPath())

                val defId = def.id

                if (def.inherit != -1) {
                    val data = library.data(TEXTURES, 0,def.inherit)
                    data.let {
                        val inheritedDef = TextureDecoder().loadSingleFlat(def.inherit,data!!)
                        def = mergeDefinitions(inheritedDef!!, def)
                    }
                }

                if (def.fileIds.isNotEmpty() && defId != -1) {
                    val spriteID = def.fileIds.first()
                    if (customSprites.containsKey(spriteID)) {
                        def.averageRgb = customSprites[spriteID]?.averageColor ?: 0
                    } else {
                        val sprite = SpriteSet.decode(spriteID, Unpooled.wrappedBuffer(library.data(SPRITES, spriteID))).sprites.first()
                        def.averageRgb = averageColorForPixels(sprite.image)
                    }
                    val encoder = TextureEncoder()
                    val writer = BufferWriter(4096)
                    with(encoder) { writer.encode(def) }

                    library.put(TEXTURES,0,defId,writer.toArray())
                    progress.step()
                } else {
                    logger.info { "Unable to Pack Texture ID is -1 or no fileIds has been defined" }
                }
            }

            progress.close()

        }
    }

    private fun mergeDefinitions(baseDef: TextureDefinition, inheritedDef: TextureDefinition): TextureDefinition {
        val defaultDef = TextureDefinition()
        val newDef = baseDef.copy()

        val ignoreFields = setOf("inherit")

        TextureDefinition::class.java.declaredFields.forEach { field ->
            if (!Modifier.isStatic(field.modifiers) && !ignoreFields.contains(field.name)) {
                field.isAccessible = true
                val baseValue = field.get(baseDef)
                val inheritedValue = field.get(inheritedDef)
                val defaultValue = field.get(defaultDef)

                if (inheritedValue != baseValue && inheritedValue != defaultValue) {
                    field.set(newDef, inheritedValue)
                }
            }
        }

        return newDef
    }

}