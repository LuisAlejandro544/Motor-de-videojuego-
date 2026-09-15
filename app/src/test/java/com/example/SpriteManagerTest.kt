package com.example

import com.example.data.MockEngineData
import com.example.data.model.ActorModel
import com.example.data.model.ActorType
import com.example.data.model.BackgroundMode
import com.example.data.model.SceneBackgroundConfig
import com.example.data.model.SpriteCategory
import com.example.data.model.SpriteItem
import com.example.data.model.VirtualControlsConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Suite de pruebas unitarias para el Gestor de Sprites, Configuración de Fondo
 * y Control de Jugador (Player) de Pocket Engine.
 * 
 * Cumple con el rol "El Escudo" (verificación de casos felices, casos borde y estabilidad).
 */
class SpriteManagerTest {

    @Test
    fun spriteCategories_haveRequiredEngineRoles() {
        val categories = SpriteCategory.values()
        
        // Verifica que existan los roles solicitados
        assertTrue(categories.contains(SpriteCategory.BACKGROUND))
        assertTrue(categories.contains(SpriteCategory.PLAYER))
        assertTrue(categories.contains(SpriteCategory.ICONS))
        assertTrue(categories.contains(SpriteCategory.ITEMS))
        assertTrue(categories.contains(SpriteCategory.ENEMY))
        assertTrue(categories.contains(SpriteCategory.PROP))
        assertTrue(categories.contains(SpriteCategory.UI_HUD))
        assertTrue(categories.contains(SpriteCategory.FX))
    }

    @Test
    fun sampleSprites_containDefaultClassifiedSprites() {
        val sprites = MockEngineData.sampleSprites
        assertTrue("Debe contener sprites de fondo", sprites.any { it.category == SpriteCategory.BACKGROUND })
        assertTrue("Debe contener sprites de jugador", sprites.any { it.category == SpriteCategory.PLAYER })
        assertTrue("Debe contener sprites de iconos", sprites.any { it.category == SpriteCategory.ICONS })
        assertTrue("Debe contener sprites de ítems", sprites.any { it.category == SpriteCategory.ITEMS })
    }

    @Test
    fun spriteItem_supportsNativeFileAndGalleryUri() {
        val localSprite = SpriteItem(
            id = "spr_custom_saf",
            name = "mi_nave_archivo",
            category = SpriteCategory.PLAYER,
            imageUri = "content://com.android.providers.media.documents/document/image%3A1024",
            width = 128,
            height = 128
        )

        assertNotNull(localSprite.imageUri)
        assertTrue(localSprite.imageUri!!.startsWith("content://"))
        assertEquals(SpriteCategory.PLAYER, localSprite.category)
    }

    @Test
    fun actorModel_playerDesignationAndSpeed() {
        val player = ActorModel(
            id = "act_player",
            name = "Heroe",
            type = ActorType.PLAYER,
            posX = 0,
            posY = 0,
            rotation = 0f,
            scale = 1.0f,
            costumeName = "heroe.png",
            scriptsCount = 2,
            soundsCount = 1,
            isPlayer = true,
            speed = 12f
        )

        assertTrue(player.isPlayer)
        assertEquals(ActorType.PLAYER, player.type)
        assertEquals(12f, player.speed, 0.01f)
    }

    @Test
    fun playerMovement_dpadClampingWithinBounds() {
        var hero = ActorModel(
            id = "act_hero",
            name = "Player",
            type = ActorType.PLAYER,
            posX = 475,
            posY = -710,
            rotation = 0f,
            scale = 1.0f,
            costumeName = "player.png",
            scriptsCount = 0,
            soundsCount = 0,
            isPlayer = true,
            speed = 10f
        )

        // Movimiento a la derecha que sobrepasa el límite 480
        val stepX = 1 * hero.speed.toInt()
        val newX = (hero.posX + stepX).coerceIn(-480, 480)
        hero = hero.copy(posX = newX)

        assertEquals(480, hero.posX)

        // Movimiento hacia abajo que sobrepasa el límite -720
        val stepY = -1 * hero.speed.toInt()
        val newY = (hero.posY + stepY).coerceIn(-720, 720)
        hero = hero.copy(posY = newY)

        assertEquals(-720, hero.posY)
    }

    @Test
    fun backgroundConfig_defaultsAndModes() {
        val defaultBg = SceneBackgroundConfig()
        assertEquals(BackgroundMode.GRADIENT_SPACE, defaultBg.mode)

        val skyBg = defaultBg.copy(mode = BackgroundMode.GRADIENT_SKY)
        assertEquals(BackgroundMode.GRADIENT_SKY, skyBg.mode)

        val spriteBg = defaultBg.copy(
            mode = BackgroundMode.SPRITE_IMAGE,
            spriteId = "spr_bg_space"
        )
        assertEquals(BackgroundMode.SPRITE_IMAGE, spriteBg.mode)
        assertEquals("spr_bg_space", spriteBg.spriteId)
    }

    @Test
    fun virtualControlsConfig_enablesDpadAndButtons() {
        val config = VirtualControlsConfig(
            isEnabled = true,
            showDpad = true,
            showActionA = true,
            labelA = "Disparo (A)",
            showActionB = true,
            labelB = "Turbo (B)"
        )

        assertTrue(config.isEnabled)
        assertTrue(config.showDpad)
        assertTrue(config.showActionA)
        assertTrue(config.showActionB)
    }
}
