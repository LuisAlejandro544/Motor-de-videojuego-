//! ==============================================================================
//! POCKET ENGINE - NÚCLEO CENTRAL EN RUST (pocketengine_core)
//! ==============================================================================
//! Este módulo implementa la lógica central, de alto rendimiento y segura en memoria
//! para el motor de videojuegos móvil.
//!
//! Responsabilidades clave de este módulo Rust:
//! 1. Bucle de Simulación de Física y Ticks de Juego deterministas sin data-races.
//! 2. Registro y estado posicional de Actores en memoria nativa (evita overhead de GC).
//! 3. Cola de eventos de alta velocidad para comunicar los scripts de Lua con el motor.
//! 4. Interfaz binaria C (ABI `extern "C"`) compatible con C++ y JNI de Android.
//! ==============================================================================

use std::collections::HashMap;
use std::ffi::{CStr, CString};
use std::os::raw::c_char;
use std::sync::Mutex;

/// Estructura que almacena el estado de un actor dentro del mundo de simulación de Rust.
#[derive(Debug, Clone)]
pub struct ActorState {
    pub x: f32,
    pub y: f32,
    pub vx: f32,
    pub vy: f32,
    pub active: bool,
}

/// Estado global del motor encapsulado en un Singleton seguro para concurrencia.
pub struct EngineWorld {
    pub tick_count: u64,
    pub actors: HashMap<String, ActorState>,
    pub last_dt: f32,
}

impl EngineWorld {
    pub fn new() -> Self {
        Self {
            tick_count: 0,
            actors: HashMap::new(),
            last_dt: 0.0,
        }
    }

    pub fn step(&mut self, dt: f32) {
        self.tick_count += 1;
        self.last_dt = dt;

        // Actualizar física básica de todos los actores registrados
        for actor in self.actors.values_mut() {
            if actor.active {
                actor.x += actor.vx * dt;
                actor.y += actor.vy * dt;
            }
        }
    }
}

// Contenedor global estático protegido con Mutex para seguridad en hilos concurrentes
static ENGINE_WORLD: Mutex<Option<EngineWorld>> = Mutex::new(None);

/// Inicializa el núcleo de simulación de Rust.
/// Retorna 1 si tuvo éxito, o 0 en caso de error.
#[no_mangle]
pub extern "C" fn pocket_rust_init() -> i32 {
    let mut guard = match ENGINE_WORLD.lock() {
        Ok(g) => g,
        Err(_) => return 0,
    };
    *guard = Some(EngineWorld::new());
    1
}

/// Devuelve la cadena de versión y arquitectura del núcleo Rust.
/// La memoria es estática y segura para lectura desde C/C++ y Kotlin.
#[no_mangle]
pub extern "C" fn pocket_rust_version() -> *const c_char {
    static VERSION: &[u8] = b"PocketEngine Rust Core v0.1.0 (Memory Safe Simulation)\0";
    VERSION.as_ptr() as *const c_char
}

/// Avanza un tick en la simulación del juego.
/// Retorna el número total acumulado de ticks ejecutados.
#[no_mangle]
pub extern "C" fn pocket_rust_step_simulation(dt: f32) -> u64 {
    let mut guard = match ENGINE_WORLD.lock() {
        Ok(g) => g,
        Err(_) => return 0,
    };

    if let Some(world) = guard.as_mut() {
        world.step(dt);
        world.tick_count
    } else {
        0
    }
}

/// Registra o actualiza un actor dentro de la simulación de Rust.
#[no_mangle]
pub extern "C" fn pocket_rust_spawn_actor(actor_id: *const c_char, x: f32, y: f32) -> i32 {
    if actor_id.is_null() {
        return 0;
    }

    let id_str = unsafe {
        match CStr::from_ptr(actor_id).to_str() {
            Ok(s) => s.to_string(),
            Err(_) => return 0,
        }
    };

    let mut guard = match ENGINE_WORLD.lock() {
        Ok(g) => g,
        Err(_) => return 0,
    };

    if let Some(world) = guard.as_mut() {
        world.actors.insert(
            id_str,
            ActorState {
                x,
                y,
                vx: 0.0,
                vy: 0.0,
                active: true,
            },
        );
        1
    } else {
        0
    }
}

/// Mueve un actor sumando un desplazamiento (dx, dy).
#[no_mangle]
pub extern "C" fn pocket_rust_move_actor(actor_id: *const c_char, dx: f32, dy: f32) -> i32 {
    if actor_id.is_null() {
        return 0;
    }

    let id_str = unsafe {
        match CStr::from_ptr(actor_id).to_str() {
            Ok(s) => s,
            Err(_) => return 0,
        }
    };

    let mut guard = match ENGINE_WORLD.lock() {
        Ok(g) => g,
        Err(_) => return 0,
    };

    if let Some(world) = guard.as_mut() {
        if let Some(actor) = world.actors.get_mut(id_str) {
            actor.x += dx;
            actor.y += dy;
            1
        } else {
            0
        }
    } else {
        0
    }
}

/// Obtiene las coordenadas actuales (x, y) de un actor en Rust.
#[no_mangle]
pub extern "C" fn pocket_rust_get_actor_coords(
    actor_id: *const c_char,
    out_x: *mut f32,
    out_y: *mut f32,
) -> i32 {
    if actor_id.is_null() || out_x.is_null() || out_y.is_null() {
        return 0;
    }

    let id_str = unsafe {
        match CStr::from_ptr(actor_id).to_str() {
            Ok(s) => s,
            Err(_) => return 0,
        }
    };

    let guard = match ENGINE_WORLD.lock() {
        Ok(g) => g,
        Err(_) => return 0,
    };

    if let Some(world) = guard.as_ref() {
        if let Some(actor) = world.actors.get(id_str) {
            unsafe {
                *out_x = actor.x;
                *out_y = actor.y;
            }
            1
        } else {
            0
        }
    } else {
        0
    }
}

/// Devuelve un reporte resumido en formato texto con estadísticas del motor Rust.
#[no_mangle]
pub extern "C" fn pocket_rust_get_stats() -> *const c_char {
    let guard = match ENGINE_WORLD.lock() {
        Ok(g) => g,
        Err(_) => return std::ptr::null(),
    };

    let (ticks, count) = if let Some(world) = guard.as_ref() {
        (world.tick_count, world.actors.len())
    } else {
        (0, 0)
    };

    let report = format!(
        "Rust Engine: Activo | Ticks: {} | Actores gestionados: {} | Memoria segura (Zero-cost)",
        ticks, count
    );

    // Retorna una cadena asignada de forma segura
    match CString::new(report) {
        Ok(c_str) => c_str.into_raw(),
        Err(_) => std::ptr::null(),
    }
}

/// Libera la memoria de una cadena previamente entregada por `pocket_rust_get_stats`.
#[no_mangle]
pub extern "C" fn pocket_rust_free_string(s: *mut c_char) {
    if !s.is_null() {
        unsafe {
            let _ = CString::from_raw(s);
        }
    }
}
