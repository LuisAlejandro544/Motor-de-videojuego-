/**
 * ==============================================================================
 * POCKET ENGINE - PUENTE C++ HACIA RUST (RustBridge.h)
 * ==============================================================================
 * Este archivo de cabecera define las funciones exportadas por el núcleo en Rust
 * (`pocketengine_core`) usando la interfaz binaria C estándar (extern "C").
 *
 * Permite que el motor en C++ y la máquina virtual de Lua invoquen la simulación
 * física y el gestor de entidades seguro en memoria programado en Rust.
 * ==============================================================================
 */

#ifndef POCKET_ENGINE_RUST_BRIDGE_H
#define POCKET_ENGINE_RUST_BRIDGE_H

#include <cstdint>

#ifdef __cplusplus
extern "C" {
#endif

// Inicializa el estado del mundo en Rust
int32_t pocket_rust_init();

// Retorna la versión y descripción del núcleo en Rust
const char* pocket_rust_version();

// Avanza un tick en la simulación física (dt en segundos)
uint64_t pocket_rust_step_simulation(float dt);

// Registra un nuevo actor en el mundo de Rust
int32_t pocket_rust_spawn_actor(const char* actor_id, float x, float y);

// Desplaza las coordenadas del actor en el motor de Rust
int32_t pocket_rust_move_actor(const char* actor_id, float dx, float dy);

// Consulta las coordenadas actuales del actor
int32_t pocket_rust_get_actor_coords(const char* actor_id, float* out_x, float* out_y);

// Retorna cadena con estadísticas y métricas del motor Rust
const char* pocket_rust_get_stats();

// Libera la memoria de la cadena retornada por pocket_rust_get_stats
void pocket_rust_free_string(char* s);

#ifdef __cplusplus
}
#endif

#endif // POCKET_ENGINE_RUST_BRIDGE_H
