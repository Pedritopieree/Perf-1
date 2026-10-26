# GCUnlock — LSPosed Module for Vivo GameCube

## Requirements
- Vivo device with GameCube (com.vivo.gamecube)
- LSPosed framework installed (LSPosed / LSPatch)
- Android 12+ (SDK 31+), OriginOS 4 ou 5

## Build
```bash
./gradlew assembleRelease
```
APK em: `app/build/outputs/apk/release/app-release.apk`

## Install
1. Instalar APK no device
2. Activar o módulo em LSPosed Manager
3. Scope: `com.vivo.gamecube` + `com.vivo.gamewatch` + `system`
4. Reboot

## Features
| Feature | Classe alvo |
|---|---|
| Thermal Bypass | TemperatureHelper, ThermalFpsHooks, ThermalGraphicsHooks |
| Monster+ Unlock | ModeShiftBar, MonsterAccess, EdgeDockView |
| 144Hz Hold | RrmDisplayHooks, DisplayRateManager |
| FPS Override | FrameHooks (OS4/OS5), framedisplay.a/d/e |
| Super Resolution | SrHooks, VSRController, superresolution.* |
| SR Algorithm 1-6 | VSRController.setVSR, algorithm getters |
| MEMC | ThermalGraphicsHooks (bypass memc thermal) |
| Audio Unlock | GameSoundSupportGame, gamecustomsound.* |
| Gyro Anti-Shake | utils.z0 |

## Re-derivação após update GameCube
Classes ofuscadas (a, b, d, h, o...) mudam com cada update.
```bash
# Extrair vivo-services.jar do device
adb pull /system/framework/vivo-services.jar

# Procurar classe com o método alvo em jadx ou:
jadx -d out/ vivo-services.jar
grep -r "setVSR\|requestVSR" out/sources/

# Para offsets nativos:
strings libgamecube_tuning.so | grep -E "fps|sr|thermal|algorithm"
```

## Architecture
```
ModuleEntry (LSPosed entry point)
├── hooks/backend/   — VSR, RRM, Thermal FPS/Graphics
├── hooks/thermal/   — CubeThermal, ThermalSessions
├── hooks/profiles/  — FrameHooks, SrHooks, FrameTuning
├── hooks/overlay/   — Monster+, FPS counter, GameCenter
├── hooks/general/   — Audio, Gyro, SyncSwitch
└── core/            — ConfigBridge, ConfigSnapshot, AppRule, DevicePolicy
```
