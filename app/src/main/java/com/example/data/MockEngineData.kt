package com.example.data

import com.example.data.model.ActorModel
import com.example.data.model.ActorType
import com.example.data.model.AssetResource
import com.example.data.model.BlockCategory
import com.example.data.model.BlockShape
import com.example.data.model.BlockTemplate
import com.example.data.model.PlacedBlock
import com.example.data.model.ProjectModel
import com.example.data.model.ProjectOrientation
import com.example.data.model.SpriteCategory
import com.example.data.model.SpriteItem

/**
 * Datos de demostración y catálogo inicial para las interfaces de Pocket Engine.
 * Proporciona plantillas de bloques visuales donde el usuario ensamblará scripts
 * en fases posteriores, así como actores y proyectos de muestra.
 */
object MockEngineData {

    // Proyectos de muestra para el hub principal
    val sampleProjects = listOf(
        ProjectModel(
            id = "proj_space_fighter",
            title = "Aventura Espacial 2D",
            description = "Juego estilo arcade de naves, disparos láser y esquiva de meteoros.",
            orientation = ProjectOrientation.PORTRAIT,
            actorsCount = 4,
            scenesCount = 2,
            lastModified = "Editado hoy 10:45 AM",
            accentHex = 0xFF58A6FF
        ),
        ProjectModel(
            id = "proj_pixel_runner",
            title = "Pixel Jump Runner",
            description = "Plataformas con scroll automático, monedas doradas y obstáculos con pinchos.",
            orientation = ProjectOrientation.LANDSCAPE,
            actorsCount = 6,
            scenesCount = 3,
            lastModified = "Ayer 18:20 PM",
            accentHex = 0xFFA371F7
        ),
        ProjectModel(
            id = "proj_flappy_clone",
            title = "Tap Flying Bird",
            description = "Toca la pantalla para aletear y pasar entre las tuberías sin chocar.",
            orientation = ProjectOrientation.PORTRAIT,
            actorsCount = 3,
            scenesCount = 1,
            lastModified = "12 Septiembre",
            accentHex = 0xFF3FB950
        )
    )

    // Actores que pertenecen al proyecto seleccionado ("Aventura Espacial 2D")
    val sampleActors = listOf(
        ActorModel(
            id = "act_hero_ship",
            name = "Nave Jugador",
            type = ActorType.PLAYER,
            posX = 0,
            posY = -280,
            rotation = 0f,
            scale = 1.2f,
            costumeName = "nave_azul_propulsor.png",
            scriptsCount = 8,
            soundsCount = 2,
            isVisible = true,
            isPlayer = true,
            spriteId = "spr_player_ship",
            speed = 12f
        ),
        ActorModel(
            id = "act_meteor_1",
            name = "Asteroide Gigante",
            type = ActorType.ENEMY,
            posX = -80,
            posY = 320,
            rotation = 45f,
            scale = 1.5f,
            costumeName = "meteoro_rocoso.png",
            scriptsCount = 4,
            soundsCount = 1,
            isVisible = true,
            isPlayer = false,
            spriteId = "spr_enemy_meteor",
            speed = 4f
        ),
        ActorModel(
            id = "act_laser_beam",
            name = "Láser Fotónico",
            type = ActorType.PROP,
            posX = 0,
            posY = -200,
            rotation = 0f,
            scale = 0.8f,
            costumeName = "laser_cyan.png",
            scriptsCount = 3,
            soundsCount = 1,
            isVisible = true,
            isPlayer = false,
            spriteId = "spr_fx_sparks",
            speed = 20f
        ),
        ActorModel(
            id = "act_score_ui",
            name = "Marcador de Puntos",
            type = ActorType.UI,
            posX = 0,
            posY = 550,
            rotation = 0f,
            scale = 1.0f,
            costumeName = "texto_puntuacion.png",
            scriptsCount = 2,
            soundsCount = 0,
            isVisible = true,
            isPlayer = false,
            spriteId = "spr_ui_bar"
        )
    )

    // Catálogo completo de Sprites de Pocket Engine clasificados por rol de motor
    val sampleSprites: MutableList<SpriteItem> = mutableListOf(
        // Fondos (Backgrounds)
        SpriteItem(
            id = "spr_bg_space",
            name = "Fondo Galaxia Profunda",
            category = SpriteCategory.BACKGROUND,
            width = 1080,
            height = 1920,
            tintColorHex = 0xFF0B132B,
            tags = listOf("espacio", "estrellas", "fondo", "cosmos"),
            isDefault = true,
            shapeType = "square"
        ),
        SpriteItem(
            id = "spr_bg_sunset",
            name = "Cielo Atardecer Pixel",
            category = SpriteCategory.BACKGROUND,
            width = 1080,
            height = 1920,
            tintColorHex = 0xFF3D1B4E,
            tags = listOf("cielo", "atardecer", "paisaje"),
            isDefault = true,
            shapeType = "square"
        ),
        SpriteItem(
            id = "spr_bg_cyber",
            name = "Matriz Cyber Neón",
            category = SpriteCategory.BACKGROUND,
            width = 1080,
            height = 1920,
            tintColorHex = 0xFF0D253A,
            tags = listOf("cyberpunk", "cuadricula", "neon"),
            isDefault = true,
            shapeType = "square"
        ),

        // Jugador (Player)
        SpriteItem(
            id = "spr_player_ship",
            name = "Nave Halcón Azul",
            category = SpriteCategory.PLAYER,
            width = 128,
            height = 128,
            tintColorHex = 0xFF58A6FF,
            tags = listOf("nave", "heroe", "jugador", "arcade"),
            isDefault = true,
            shapeType = "ship"
        ),
        SpriteItem(
            id = "spr_player_hero",
            name = "Aventurero Pixel",
            category = SpriteCategory.PLAYER,
            width = 64,
            height = 64,
            tintColorHex = 0xFF3FB950,
            tags = listOf("heroe", "plataformas", "personaje"),
            isDefault = true,
            shapeType = "circle"
        ),

        // Iconos (Icons)
        SpriteItem(
            id = "spr_ico_pause",
            name = "Botón Pausa HUD",
            category = SpriteCategory.ICONS,
            width = 48,
            height = 48,
            tintColorHex = 0xFFF0883E,
            tags = listOf("icono", "hud", "pausa"),
            isDefault = true,
            shapeType = "square"
        ),
        SpriteItem(
            id = "spr_ico_heart",
            name = "Corazón Vidas",
            category = SpriteCategory.ICONS,
            width = 48,
            height = 48,
            tintColorHex = 0xFFFF5252,
            tags = listOf("salud", "vidas", "icono"),
            isDefault = true,
            shapeType = "heart"
        ),

        // Ítems (Items)
        SpriteItem(
            id = "spr_item_coin",
            name = "Moneda de Oro",
            category = SpriteCategory.ITEMS,
            width = 48,
            height = 48,
            tintColorHex = 0xFFFFD166,
            tags = listOf("moneda", "tesoro", "puntos"),
            isDefault = true,
            shapeType = "circle"
        ),
        SpriteItem(
            id = "spr_item_gem",
            name = "Gema Cristalina",
            category = SpriteCategory.ITEMS,
            width = 48,
            height = 48,
            tintColorHex = 0xFF06D6A0,
            tags = listOf("gema", "cristal", "bonus"),
            isDefault = true,
            shapeType = "gem"
        ),
        SpriteItem(
            id = "spr_item_potion",
            name = "Poción de Hipervelocidad",
            category = SpriteCategory.ITEMS,
            width = 48,
            height = 48,
            tintColorHex = 0xFFA371F7,
            tags = listOf("pocion", "powerup", "velocidad"),
            isDefault = true,
            shapeType = "star"
        ),

        // Enemigos (Enemies)
        SpriteItem(
            id = "spr_enemy_meteor",
            name = "Asteroide Rocoso",
            category = SpriteCategory.ENEMY,
            width = 96,
            height = 96,
            tintColorHex = 0xFF8E9297,
            tags = listOf("asteroide", "obstaculo", "meteoro"),
            isDefault = true,
            shapeType = "circle"
        ),
        SpriteItem(
            id = "spr_enemy_alien",
            name = "Caza Alienígena",
            category = SpriteCategory.ENEMY,
            width = 96,
            height = 96,
            tintColorHex = 0xFFFF5252,
            tags = listOf("alien", "enemigo", "nave"),
            isDefault = true,
            shapeType = "ship"
        ),

        // Props / Escenario
        SpriteItem(
            id = "spr_prop_platform",
            name = "Plataforma de Acero",
            category = SpriteCategory.PROP,
            width = 160,
            height = 48,
            tintColorHex = 0xFF565F6E,
            tags = listOf("plataforma", "suelo", "metal"),
            isDefault = true,
            shapeType = "square"
        ),
        SpriteItem(
            id = "spr_prop_crate",
            name = "Caja de Suministros",
            category = SpriteCategory.PROP,
            width = 64,
            height = 64,
            tintColorHex = 0xFFD29922,
            tags = listOf("caja", "madera", "obstaculo"),
            isDefault = true,
            shapeType = "square"
        ),

        // Interfaz / HUD
        SpriteItem(
            id = "spr_ui_bar",
            name = "Marco Barra de Energía",
            category = SpriteCategory.UI_HUD,
            width = 200,
            height = 32,
            tintColorHex = 0xFF58A6FF,
            tags = listOf("hud", "energia", "ui"),
            isDefault = true,
            shapeType = "square"
        ),

        // Efectos / FX
        SpriteItem(
            id = "spr_fx_explosion",
            name = "Explosión de Plasma",
            category = SpriteCategory.FX,
            width = 80,
            height = 80,
            tintColorHex = 0xFFFF7B72,
            tags = listOf("explosion", "fuego", "particulas"),
            isDefault = true,
            shapeType = "star"
        ),
        SpriteItem(
            id = "spr_fx_sparks",
            name = "Chispas de Láser",
            category = SpriteCategory.FX,
            width = 40,
            height = 40,
            tintColorHex = 0xFFFFDF5D,
            tags = listOf("chispas", "impacto", "laser"),
            isDefault = true,
            shapeType = "star"
        )
    )

    // Catálogo completo de bloques visuales preparados para el motor (Pocket Code style)
    val availableBlockTemplates = listOf(
        // EVENTOS
        BlockTemplate(
            id = "evt_start",
            category = BlockCategory.EVENTOS,
            label = "Al iniciar la escena",
            shape = BlockShape.CAP_HEADER,
            description = "Se ejecuta tan pronto como comienza el nivel."
        ),
        BlockTemplate(
            id = "evt_tap_actor",
            category = BlockCategory.EVENTOS,
            label = "Al tocar este objeto",
            shape = BlockShape.CAP_HEADER,
            description = "Se activa cuando el dedo del jugador presiona este sprite."
        ),
        BlockTemplate(
            id = "evt_broadcast_recv",
            category = BlockCategory.EVENTOS,
            label = "Al recibir mensaje [",
            shape = BlockShape.CAP_HEADER,
            defaultParam = "Game_Over",
            description = "Reacciona a eventos globales emitidos por otros actores."
        ),

        // MOVIMIENTO
        BlockTemplate(
            id = "mot_move_steps",
            category = BlockCategory.MOVIMIENTO,
            label = "Mover pasos: ",
            shape = BlockShape.STACK_COMMAND,
            defaultParam = "10",
            description = "Avanza hacia la dirección actual del actor."
        ),
        BlockTemplate(
            id = "mot_set_position",
            category = BlockCategory.MOVIMIENTO,
            label = "Fijar posición en X: [ ] Y: [ ]",
            shape = BlockShape.STACK_COMMAND,
            defaultParam = "X: 0, Y: 100",
            description = "Coloca el actor en coordenadas precisas de pantalla."
        ),
        BlockTemplate(
            id = "mot_point_in_dir",
            category = BlockCategory.MOVIMIENTO,
            label = "Girar hacia ángulo: ",
            shape = BlockShape.STACK_COMMAND,
            defaultParam = "90°",
            description = "Alinea la orientación del sprite."
        ),
        BlockTemplate(
            id = "mot_bounce_edge",
            category = BlockCategory.MOVIMIENTO,
            label = "Rebotar si toca el borde",
            shape = BlockShape.STACK_COMMAND,
            description = "Invierte la velocidad al tocar los límites de la pantalla."
        ),

        // CONTROL
        BlockTemplate(
            id = "ctrl_wait",
            category = BlockCategory.CONTROL,
            label = "Esperar segundos: ",
            shape = BlockShape.STACK_COMMAND,
            defaultParam = "1.0",
            description = "Pausa la ejecución del script por un tiempo determinado."
        ),
        BlockTemplate(
            id = "ctrl_forever",
            category = BlockCategory.CONTROL,
            label = "Por siempre {",
            shape = BlockShape.WRAP_LOOP,
            description = "Bucle infinito principal del ciclo de juego."
        ),
        BlockTemplate(
            id = "ctrl_repeat_n",
            category = BlockCategory.CONTROL,
            label = "Repetir veces: [ ] {",
            shape = BlockShape.WRAP_LOOP,
            defaultParam = "10",
            description = "Ejecuta los bloques interiores un número exacto de veces."
        ),
        BlockTemplate(
            id = "ctrl_if_then",
            category = BlockCategory.CONTROL,
            label = "Si <condición> entonces {",
            shape = BlockShape.WRAP_LOOP,
            defaultParam = "¿Toca asteroide?",
            description = "Evalúa una condición lógica antes de continuar."
        ),

        // APARIENCIA
        BlockTemplate(
            id = "looks_switch_costume",
            category = BlockCategory.APARIENCIA,
            label = "Cambiar disfraz a: ",
            shape = BlockShape.STACK_COMMAND,
            defaultParam = "nave_fuego.png",
            description = "Cambia el sprite actual del actor."
        ),
        BlockTemplate(
            id = "looks_set_size",
            category = BlockCategory.APARIENCIA,
            label = "Establecer tamaño al: ",
            shape = BlockShape.STACK_COMMAND,
            defaultParam = "120%",
            description = "Escala el tamaño visual del actor."
        ),
        BlockTemplate(
            id = "looks_show",
            category = BlockCategory.APARIENCIA,
            label = "Mostrar este actor",
            shape = BlockShape.STACK_COMMAND,
            description = "Vuelve visible el objeto."
        ),
        BlockTemplate(
            id = "looks_hide",
            category = BlockCategory.APARIENCIA,
            label = "Ocultar este actor",
            shape = BlockShape.STACK_COMMAND,
            description = "Desvanece el objeto de la pantalla."
        ),

        // SONIDO
        BlockTemplate(
            id = "snd_play",
            category = BlockCategory.SONIDO,
            label = "Reproducir sonido: ",
            shape = BlockShape.STACK_COMMAND,
            defaultParam = "disparo_laser.wav",
            description = "Emite un efecto de sonido sin bloquear el código."
        ),
        BlockTemplate(
            id = "snd_stop_all",
            category = BlockCategory.SONIDO,
            label = "Detener todos los sonidos",
            shape = BlockShape.STACK_COMMAND,
            description = "Silencia cualquier pista o efecto sonoro activo."
        ),

        // SENSORES
        BlockTemplate(
            id = "sens_touch_screen",
            category = BlockCategory.SENSORES,
            label = "¿Pantalla tocada?",
            shape = BlockShape.BOOLEAN_VALUE,
            description = "Detecta si el usuario tiene un dedo sobre el cristal."
        ),
        BlockTemplate(
            id = "sens_touch_actor",
            category = BlockCategory.SENSORES,
            label = "¿Tocando a [Asteroide]?",
            shape = BlockShape.BOOLEAN_VALUE,
            description = "Detecta colisión geométrica con otro actor."
        ),

        // DATOS / VARIABLES
        BlockTemplate(
            id = "data_set_var",
            category = BlockCategory.DATOS,
            label = "Fijar variable [puntos] a: ",
            shape = BlockShape.STACK_COMMAND,
            defaultParam = "0",
            description = "Asigna un valor a una variable global o local."
        ),
        BlockTemplate(
            id = "data_change_var",
            category = BlockCategory.DATOS,
            label = "Sumar a [puntos]: ",
            shape = BlockShape.STACK_COMMAND,
            defaultParam = "10",
            description = "Incrementa o decrementa la variable del marcador."
        )
    )

    // Bloques actualmente ensamblados en el workspace de ejemplo de la "Nave Jugador"
    val samplePlacedBlocks = listOf(
        PlacedBlock(
            id = "pl_1",
            templateId = "evt_start",
            category = BlockCategory.EVENTOS,
            text = "Al iniciar la escena",
            shape = BlockShape.CAP_HEADER,
            indentLevel = 0
        ),
        PlacedBlock(
            id = "pl_2",
            templateId = "mot_set_position",
            category = BlockCategory.MOVIMIENTO,
            text = "Fijar posición en",
            paramValue = "X: 0, Y: -280",
            shape = BlockShape.STACK_COMMAND,
            indentLevel = 0
        ),
        PlacedBlock(
            id = "pl_3",
            templateId = "looks_set_size",
            category = BlockCategory.APARIENCIA,
            text = "Establecer tamaño al",
            paramValue = "120%",
            shape = BlockShape.STACK_COMMAND,
            indentLevel = 0
        ),
        PlacedBlock(
            id = "pl_4",
            templateId = "ctrl_forever",
            category = BlockCategory.CONTROL,
            text = "Por siempre {",
            shape = BlockShape.WRAP_LOOP,
            indentLevel = 0
        ),
        PlacedBlock(
            id = "pl_5",
            templateId = "mot_move_steps",
            category = BlockCategory.MOVIMIENTO,
            text = "Mover hacia el toque",
            paramValue = "Velocidad: 8",
            shape = BlockShape.STACK_COMMAND,
            indentLevel = 1
        ),
        PlacedBlock(
            id = "pl_6",
            templateId = "mot_bounce_edge",
            category = BlockCategory.MOVIMIENTO,
            text = "Rebotar si toca el borde",
            shape = BlockShape.STACK_COMMAND,
            indentLevel = 1
        ),
        PlacedBlock(
            id = "pl_7",
            templateId = "ctrl_wait",
            category = BlockCategory.CONTROL,
            text = "Esperar segundos",
            paramValue = "0.02s",
            shape = BlockShape.STACK_COMMAND,
            indentLevel = 1
        ),
        PlacedBlock(
            id = "pl_8",
            templateId = "evt_tap_actor",
            category = BlockCategory.EVENTOS,
            text = "Al tocar este objeto",
            shape = BlockShape.CAP_HEADER,
            indentLevel = 0
        ),
        PlacedBlock(
            id = "pl_9",
            templateId = "snd_play",
            category = BlockCategory.SONIDO,
            text = "Reproducir sonido",
            paramValue = "disparo_laser.wav",
            shape = BlockShape.STACK_COMMAND,
            indentLevel = 0
        )
    )

    // Biblioteca de recursos visuales y auditivos
    val sampleAssets = listOf(
        AssetResource("res_1", "nave_azul_propulsor.png", "Sprite Principal", "256x256 px", "Disfraz 1 (Predeterminado)"),
        AssetResource("res_2", "nave_azul_fuego.png", "Sprite Animación", "256x256 px", "Disfraz 2 (En movimiento)"),
        AssetResource("res_3", "meteoro_rocoso.png", "Sprite Enemigo", "180x180 px", "Disfraz Asteroide"),
        AssetResource("res_4", "fondo_espacio_estrellas.png", "Fondo Escena", "1080x1920 px", "Capa de fondo 60fps"),
        AssetResource("res_5", "disparo_laser.wav", "Efecto Sonoro", "44.1 kHz, 0.4s", "Canal de efectos SFX"),
        AssetResource("res_6", "explosion_impacto.wav", "Efecto Sonoro", "44.1 kHz, 1.2s", "Canal de efectos SFX")
    )
}
