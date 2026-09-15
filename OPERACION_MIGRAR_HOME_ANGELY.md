# Estado: migración de `/home` a `Angely`

## Estado final

- La copia original de `Angely` está resguardada en
  `/media/yannier/39A7FE8A2172FB36/DISCO ANGELY/` y fue validada antes del
  formateo.
- `/dev/nvme0n1p3` es ahora `ext4`, etiqueta `HOME_DATA`, UUID
  `13357232-5271-4f59-8405-9f124daa5f2c`.
- Se copiaron y sincronizaron 13 GiB de `/home`, conservando propietarios,
  ACL, atributos extendidos y enlaces duros.
- `/etc/fstab` ya monta ese UUID como `/home`; la configuración anterior está
  guardada en `/etc/fstab.pre-home-20260915`.
- Tras el reinicio, `systemd-fsck` comprobó `HOME_DATA` sin errores y el
  sistema lo montó en lectura/escritura como `/home`.
- La copia antigua de `/home`, que quedaba oculta en la partición raíz, fue
  eliminada el 2026-09-15 tras validar el nuevo montaje. La raíz pasó de
  47 GiB usados y 7.7 GiB libres a 34 GiB usados y 21 GiB libres.

## Respaldo disponible

La copia original de Angely en
`/media/yannier/39A7FE8A2172FB36/DISCO ANGELY/` se conserva sin cambios.

## Verificación futura

Para comprobar el montaje en un futuro:

```bash
findmnt /home
df -h /home /
```

`/home` debe provenir de `/dev/nvme0n1p3`. No queda una copia antigua de
`/home` en la partición del sistema que liberar.
