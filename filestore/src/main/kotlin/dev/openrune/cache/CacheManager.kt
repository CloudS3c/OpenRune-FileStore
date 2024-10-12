package dev.openrune.cache

import dev.openrune.cache.filestore.Cache
import dev.openrune.cache.filestore.definition.data.*
import dev.openrune.cache.filestore.definition.decoder.*
import java.nio.file.Path

object CacheManager {

    lateinit var cache: Cache
    private var cacheRevision = -1

    private val npcs = mutableMapOf<Int, NpcType>()
    private val objects = mutableMapOf<Int, ObjectType>()
    private val items = mutableMapOf<Int, ItemType>()
    private val varbits = mutableMapOf<Int, VarBitType>()
    private val varps = mutableMapOf<Int, VarpType>()
    private val anims = mutableMapOf<Int, SequenceType>()
    private val enums = mutableMapOf<Int, EnumType>()
    private val healthBars = mutableMapOf<Int, HealthBarType>()
    private val hitsplats = mutableMapOf<Int, HitSplatType>()
    private val structs = mutableMapOf<Int, StructType>()

    fun init(cachePath: Path, cacheRevision: Int) {
        init(Cache.load(cachePath, false), cacheRevision)
    }

    @JvmStatic
    fun init(cache: Cache, cacheRevision: Int) {
        this.cacheRevision = cacheRevision
        this.cache = cache
        npcs.putAll(NPCDecoder().load(cache))
        objects.putAll(ObjectDecoder().load(cache))
        items.putAll(ItemDecoder().load(cache))
        varbits.putAll(VarBitDecoder().load(cache))
        varps.putAll(VarDecoder().load(cache))
        anims.putAll(SequenceDecoder().load(cache))
        enums.putAll(EnumDecoder().load(cache))
        healthBars.putAll(HealthBarDecoder().load(cache))
        hitsplats.putAll(HitSplatDecoder().load(cache))
        structs.putAll(StructDecoder().load(cache))
    }

    private inline fun <T> getOrDefault(map: Map<Int, T>, id: Int, default: T, typeName: String): T {
        return map.getOrDefault(id, default).also {
            if (it == default) println("$typeName with id $id is missing.")
        }
    }

    fun getNpc(id: Int) = npcs[id]
    fun getObject(id: Int) = objects[id]
    fun getItem(id: Int) = items[id]
    fun getVarbit(id: Int) = varbits[id]
    fun getVarp(id: Int) = varps[id]
    fun getAnim(id: Int) = anims[id]
    fun getEnum(id: Int) = enums[id]
    fun getHealthBar(id: Int) = healthBars[id]
    fun getHitsplat(id: Int) = hitsplats[id]
    fun getStruct(id: Int) = structs[id]

    fun getNpcOrDefault(id: Int) = getOrDefault(npcs, id, NpcType(id), "Npc")
    fun getObjectOrDefault(id: Int) = getOrDefault(objects, id, ObjectType(id), "Object")
    fun getItemOrDefault(id: Int) = getOrDefault(items, id, ItemType(id), "Item")
    fun getVarbitOrDefault(id: Int) = getOrDefault(varbits, id, VarBitType(id), "Varbit")
    fun getVarpOrDefault(id: Int) = getOrDefault(varps, id, VarpType(id), "Varp")
    fun getAnimOrDefault(id: Int) = getOrDefault(anims, id, SequenceType(id), "Anim")
    fun getEnumOrDefault(id: Int) = getOrDefault(enums, id, EnumType(id), "Enum")
    fun getHealthBarOrDefault(id: Int) = getOrDefault(healthBars, id, HealthBarType(id), "HealthBar")
    fun getHitsplatOrDefault(id: Int) = getOrDefault(hitsplats, id, HitSplatType(id), "Hitsplat")
    fun getStructOrDefault(id: Int) = getOrDefault(structs, id, StructType(id), "Struct")

    fun findScriptId(name: String): Int {
        val cacheName = "[clientscript,$name]"
        return cache.archiveId(CLIENTSCRIPT, cacheName).also { id ->
            if (id == -1) println("Unable to find script: $cacheName")
        }
    }

    // Size methods
    fun npcSize() = npcs.size
    fun objectSize() = objects.size
    fun itemSize() = items.size
    fun varbitSize() = varbits.size
    fun varpSize() = varps.size
    fun animSize() = anims.size
    fun enumSize() = enums.size
    fun healthBarSize() = healthBars.size
    fun hitsplatSize() = hitsplats.size
    fun structSize() = structs.size

    // Bulk getters
    fun getNpcs() = npcs.toMap()
    fun getObjects() = objects.toMap()
    fun getItems() = items.toMap()
    fun getVarbits() = varbits.toMap()
    fun getVarps() = varps.toMap()
    fun getAnims() = anims.toMap()
    fun getEnums() = enums.toMap()
    fun getHealthBars() = healthBars.toMap()
    fun getHitsplats() = hitsplats.toMap()
    fun getStructs() = structs.toMap()

    // Cache revision methods
    fun revisionIsOrAfter(rev: Int) = rev <= cacheRevision
    fun revisionIsOrBefore(rev: Int) = rev >= cacheRevision
    
}
