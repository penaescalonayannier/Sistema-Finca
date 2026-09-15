#!/usr/bin/env bash
# Elimina la copia antigua de /home que quedó oculta tras montar HOME_DATA.
# Debe ejecutarse únicamente después de validar la nueva sesión de usuario.

set -euo pipefail

if [[ ${1:-} != "--delete" ]]; then
  echo "Uso: sudo $0 --delete" >&2
  exit 64
fi

if [[ $(id -u) -ne 0 ]]; then
  echo "Este proceso requiere privilegios de administrador." >&2
  exit 1
fi

temporary_root=/mnt/root-before-home-cleanup
bound_root=0

cleanup() {
  if [[ $bound_root -eq 1 ]]; then
    umount "$temporary_root" || true
  fi
  rmdir "$temporary_root" 2>/dev/null || true
}
trap cleanup EXIT INT TERM

mkdir -p "$temporary_root"
mount --bind / "$temporary_root"
bound_root=1

root_source=$(findmnt -nro SOURCE --target /)
new_home_source=$(findmnt -nro SOURCE --target /home)
old_home_source=$(findmnt -nro SOURCE --target "$temporary_root/home")

# Un bind normal de / no incluye el montaje hijo /home. Estas condiciones
# impiden borrar datos si el comportamiento del sistema no es el esperado.
if [[ "$new_home_source" == "$root_source" || "$old_home_source" != "$root_source" ]]; then
  echo "ABORTADO: no se pudo identificar con seguridad el /home antiguo." >&2
  echo "Raíz: $root_source | /home actual: $new_home_source | /home antiguo: $old_home_source" >&2
  exit 1
fi

if [[ ! -d "$temporary_root/home" ]]; then
  echo "ABORTADO: no existe el directorio /home antiguo." >&2
  exit 1
fi

echo "Se eliminará el /home antiguo en $root_source."
echo "El /home activo permanece en $new_home_source."
du -sh --one-file-system "$temporary_root/home"

find "$temporary_root/home" -xdev -mindepth 1 -maxdepth 1 \
  -exec rm -rf --one-file-system -- {} +
sync

echo "Limpieza terminada. Espacio disponible en la raíz:"
df -h /
