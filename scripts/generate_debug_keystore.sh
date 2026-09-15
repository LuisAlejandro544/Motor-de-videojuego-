#!/usr/bin/env bash
# ==============================================================================
# Script: generate_debug_keystore.sh
# Proyecto: Pocket Engine
# 
# Descripción:
# Genera un almacén de claves (keystore) y certificado de depuración (debug.keystore)
# completamente nuevo desde cero, de forma 100% no interactiva y desatendida.
# 
# Propósito:
# Diseñado para entornos de Integración Continua (CI/CD como GitHub Actions)
# y compilaciones locales directas en teléfonos móviles o servidores, obligando
# a regenerar una firma válida desde 0 sin requerir intervención humana,
# contraseñas interactivas ni secrets externos preconfigurados.
#
# Entradas:
# - [Opcional] Argumento $1: Ruta absoluta o relativa del keystore de salida.
#   Por defecto: ./debug.keystore (en la raíz del proyecto) y ~/.android/debug.keystore
#
# Salidas:
# - Archivo binario debug.keystore con alias 'androiddebugkey' y contraseña 'android'
# - Código de salida: 0 (Éxito), 1 (Fallo en generación)
# ==============================================================================

set -euo pipefail

echo "======================================================================"
echo "🔧 [Pocket Engine] Iniciando generación forzada de firma Debug desde 0"
echo "======================================================================"

# Determinar directorio raíz del proyecto Pocket Engine
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"

# Ubicación de destino del keystore de depuración
TARGET_KEYSTORE="${1:-${ROOT_DIR}/debug.keystore}"
FALLBACK_ANDROID_DIR="${HOME}/.android"
FALLBACK_KEYSTORE="${FALLBACK_ANDROID_DIR}/debug.keystore"

# Parámetros estándar de firma Debug de Android
KEY_ALIAS="androiddebugkey"
KEY_PASS="android"
STORE_PASS="android"
DNAME="CN=Android Debug,O=Android,C=US"
KEY_ALG="RSA"
KEY_SIZE=2048
VALIDITY_DAYS=10000

# 1. Verificar presencia de la herramienta keytool (incluida en OpenJDK)
if ! command -v keytool >/dev/null 2>&1; then
    echo "❌ ERROR: No se encontró la herramienta 'keytool' en el PATH del sistema."
    echo "   Asegúrate de tener instalado Java Development Kit (OpenJDK 17 o superior)."
    exit 1
fi

echo "✅ Herramienta keytool detectada: $(which keytool)"

# 2. Obligar a regenerar desde 0: eliminar cualquier keystore previo
if [ -f "${TARGET_KEYSTORE}" ]; then
    echo "⚠️  Detectado keystore previo en: ${TARGET_KEYSTORE}"
    echo "🧹 Eliminando keystore anterior para forzar generación limpia desde 0..."
    rm -f "${TARGET_KEYSTORE}"
fi

# Asegurar que el directorio contenedor exista
mkdir -p "$(dirname "${TARGET_KEYSTORE}")"

# 3. Generación del par de claves con keytool de forma no interactiva
echo "🔨 Generando nuevo par de claves RSA ${KEY_SIZE} bits (Validez: ${VALIDITY_DAYS} días)..."
keytool -genkeypair \
    -v \
    -keystore "${TARGET_KEYSTORE}" \
    -storepass "${STORE_PASS}" \
    -alias "${KEY_ALIAS}" \
    -keypass "${KEY_PASS}" \
    -keyalg "${KEY_ALG}" \
    -keysize "${KEY_SIZE}" \
    -validity "${VALIDITY_DAYS}" \
    -dname "${DNAME}"

# 4. Verificación rigurosa de la firma recién creada
echo "🔍 Verificando integridad de la firma generada en: ${TARGET_KEYSTORE}"
if ! keytool -list -v -keystore "${TARGET_KEYSTORE}" -storepass "${STORE_PASS}" -alias "${KEY_ALIAS}" >/dev/null 2>&1; then
    echo "❌ ERROR: La verificación del keystore generado ha fallado."
    exit 1
fi

# 5. Replicar adicionalmente en ~/.android/debug.keystore si es posible
if [ -d "${FALLBACK_ANDROID_DIR}" ] || mkdir -p "${FALLBACK_ANDROID_DIR}" 2>/dev/null; then
    cp -f "${TARGET_KEYSTORE}" "${FALLBACK_KEYSTORE}" 2>/dev/null || true
    echo "📋 Copia de seguridad de firma sincronizada en: ${FALLBACK_KEYSTORE}"
fi

# Resumen de confirmación
KEYSTORE_SIZE=$(stat -c%s "${TARGET_KEYSTORE}" 2>/dev/null || stat -f%z "${TARGET_KEYSTORE}" 2>/dev/null || wc -c < "${TARGET_KEYSTORE}")

echo "======================================================================"
echo "🎉 ¡Firma Debug generada con éxito desde cero!"
echo "   - Archivo: ${TARGET_KEYSTORE} (${KEYSTORE_SIZE} bytes)"
echo "   - Alias:   ${KEY_ALIAS}"
echo "   - Clave:   ${STORE_PASS}"
echo "   - Alg:     ${KEY_ALG} ${KEY_SIZE} bits"
echo "   - Estado:  Listo para firmar el APK Debug en CI/CD"
echo "======================================================================"

exit 0
