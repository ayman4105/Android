# Android Build System

## 1. Big Picture

Android Build System is the system that takes **AOSP source code** and converts it into Android images that can be flashed to a device.

```text
Source Code
   |
   v
Build System
   |
   v
Android Images
   |
   v
Flash to Device
```

Examples of output images:

```text
boot.img
vendor_boot.img
system.img
vendor.img
product.img
odm.img
userdata.img
vbmeta.img
```

---

# 2. Main Build System Tools

Modern Android mainly uses:

```text
Soong
```

Soong works with:

```text
Kati
Ninja
```

Simple meaning:

```text
Soong  → Main Android build system
Kati   → Handles old Android.mk files
Ninja  → Executes the actual build commands
```

---

# 3. Android Build Flow

```text
Android.bp / Android.mk
        |
        v
Soong + Kati
        |
        v
Ninja files
        |
        v
Ninja executes build commands
        |
        v
Output images
```

More detailed:

```text
AOSP Source Code
      |
      v
Android.bp files  ----> Soong
Android.mk files  ----> Kati
      |                  |
      +--------+---------+
               |
               v
          Ninja build rules
               |
               v
          Compile / Link / Package
               |
               v
          Android images
```

---

# 4. Soong

Soong is the main build system used in modern Android.

It reads files called:

```text
Android.bp
```

Soong understands Android modules such as:

```text
C/C++ library
Java library
Android app
HAL service
Native binary
Rust module
Prebuilt file
```

Simple idea:

```text
Soong reads Android.bp files and generates Ninja build rules.
```

---

# 5. Android.bp

`Android.bp` is the build configuration file used by Soong.

It describes modules.

Example:

```bp
cc_binary {
    name: "hello_android",
    srcs: ["main.cpp"],
    shared_libs: ["liblog"],
}
```

Meaning:

```text
Build C/C++ executable
Name = hello_android
Source file = main.cpp
Link with liblog
```

---

# 6. Android.mk

`Android.mk` is the old Android Make-based build file.

It is considered legacy, but still exists in many old projects and vendor code.

```text
Android.mk → Old build file
Android.bp → Modern build file
```

Kati handles old `Android.mk` files.

```text
Android.mk
   |
   v
Kati
   |
   v
Ninja rules
```

---

# 7. Kati

Kati is used to process old Make files.

It helps Android support legacy build files like:

```text
Android.mk
```

Simple idea:

```text
Kati converts old Make logic into Ninja build rules.
```

---

# 8. Ninja

Ninja is the backend build executor.

Soong and Kati generate Ninja files, then Ninja runs the real commands.

```text
Soong / Kati
     |
     v
Generate Ninja files
     |
     v
Ninja runs commands
```

Ninja executes things like:

```text
Compile
Link
Copy files
Package modules
Generate images
```

---

# 9. Comparison with CMake

In normal C/C++ projects:

```text
CMakeLists.txt
   |
   v
CMake generates Make/Ninja files
   |
   v
Make/Ninja builds project
```

In Android platform build:

```text
Android.bp
   |
   v
Soong generates Ninja files
   |
   v
Ninja builds modules/images
```

Simple comparison:

```text
CMake → common in normal C/C++ projects
Soong → used in AOSP / Android platform
```

---

# 10. Why Android Needs a Big Build System

Android is not one small app.

AOSP contains thousands of modules.

Examples:

```text
Kernel-related files
Native libraries
Java framework
System apps
HAL services
Vendor modules
Permissions XML
init rc files
SELinux policies
APEX packages
Images and partitions
```

The build system must know:

```text
How to build each module
Where to install each output
Which partition should contain it
What dependencies it needs
How to sign it
Whether it belongs to system, vendor, product, or odm
```

---

# 11. AOSP Source Tree Structure

Typical AOSP tree:

```text
aosp/
├── build/
├── system/
├── frameworks/
├── packages/
├── hardware/
├── device/
├── vendor/
├── kernel/
├── bootable/
├── external/
├── prebuilts/
└── out/
```

---

# 12. Important Folders

## build/

Contains Android build system files and logic.

```text
build/
├── make/
├── soong/
└── blueprint/
```

Used for:

```text
lunch
build rules
product config
Soong build system
Make compatibility
```

Key idea:

```text
build/ = core build system logic
```

---

## frameworks/

Contains Android Framework code.

Examples:

```text
frameworks/base/
frameworks/native/
frameworks/av/
frameworks/opt/
```

Important folder:

```text
frameworks/base/
```

Contains core framework and services like:

```text
ActivityManager
PackageManager
WindowManager
PowerManager
SystemServer
```

---

## system/

Contains core system components.

Examples:

```text
system/core/
system/sepolicy/
system/libbase/
system/logging/
```

Important folder:

```text
system/core/init/
```

This contains Android `init` source code.

Other examples in `system/core`:

```text
init
adb
logcat
libcutils
healthd
```

---

## packages/

Contains Android apps and app-related services.

Examples:

```text
packages/apps/
packages/services/
packages/providers/
```

Examples of apps:

```text
Settings
Launcher
Music
Bluetooth
Camera
```

---

## hardware/

Contains HAL interfaces and hardware abstraction code.

Examples:

```text
hardware/interfaces/
hardware/libhardware/
```

Important for automotive:

```text
hardware/interfaces/automotive/
```

This can include automotive HAL interfaces such as Vehicle HAL.

---

## device/

Contains board/device-specific configuration.

Example:

```text
device/<vendor>/<board>/
```

Usually contains:

```text
BoardConfig.mk
device.mk
product.mk
init rc files
fstab
device sepolicy
permissions
overlays
```

Simple meaning:

```text
device/ = device and board configuration
```

It answers questions like:

```text
What partitions does this device use?
What packages are included?
What kernel is used?
How are images generated?
What init files are needed?
```

---

## vendor/

Contains vendor-specific files.

Example:

```text
vendor/<vendor>/<board>/
```

Usually contains:

```text
Vendor HAL implementations
Proprietary binaries
Vendor libraries
Vendor init rc
Vendor SELinux policy
Vendor apps
```

In Android Automotive, Vehicle HAL implementation or CAN services may be placed here.

---

## kernel/

Can contain Linux kernel source or kernel configuration.

Not every AOSP tree contains kernel source directly.

Sometimes the kernel is in a separate repository.

Simple meaning:

```text
kernel/ = Linux kernel source/config/build outputs if included
```

---

## bootable/

Contains boot and recovery related code.

Example:

```text
bootable/recovery/
```

Used for:

```text
Recovery mode
Factory reset
OTA recovery
```

---

## external/

Contains third-party open-source libraries used by Android.

Examples:

```text
external/boringssl
external/sqlite
external/libpng
external/expat
external/zlib
```

---

## prebuilts/

Contains prebuilt tools or binaries.

Examples:

```text
prebuilts/clang/
prebuilts/jdk/
prebuilts/build-tools/
```

Used for:

```text
Compiler
JDK
Build tools
Prebuilt binaries
```

---

## out/

Contains build output.

This folder is created after building.

Important path:

```text
out/target/product/<device_name>/
```

Example:

```text
out/target/product/cuttlefish/
```

Contains images like:

```text
boot.img
vendor_boot.img
system.img
vendor.img
product.img
odm.img
vbmeta.img
ramdisk.img
```

Simple meaning:

```text
out/ = build output directory
```

---

# 13. Source Tree Summary

```text
aosp/
├── build/        → build system logic
├── frameworks/   → Android Framework and SystemServer code
├── system/       → init, adb, logcat, sepolicy, core system tools
├── packages/     → Android apps and providers
├── hardware/     → HAL interfaces and hardware abstraction code
├── device/       → board/device configuration
├── vendor/       → vendor-specific implementation and binaries
├── kernel/       → Linux kernel source/config if included
├── bootable/     → recovery and boot related code
├── external/     → third-party open-source libraries
├── prebuilts/    → prebuilt compilers/tools/JDK
└── out/          → build output
```

---

# 14. Automotive Build Examples

In Android Automotive, common modules can include:

```text
Vehicle HAL service
CarService changes
Custom Launcher
Custom System UI
Vendor native daemon
Audio HAL
CAN gateway service
SELinux policy
init.rc service file
```

Example: Vehicle HAL build flow

```text
Vehicle HAL implementation
        |
        v
Build with Soong
        |
        v
Install into vendor partition
        |
        v
Started by init rc
        |
        v
Used by CarService
```

---

# 15. Common Automotive Source Locations

## CarService

Usually found in:

```text
packages/services/Car/
```

Used for Android Automotive car framework services.

---

## Vehicle HAL

May be found in places like:

```text
hardware/interfaces/automotive/
vendor/<vendor>/
device/<vendor>/<board>/
```

---

## Custom Launcher or System UI

May be found in:

```text
packages/apps/
frameworks/base/packages/SystemUI/
```

---

## Device and Vendor Configuration

Usually found in:

```text
device/<vendor>/<board>/
vendor/<vendor>/<board>/
```

---

## SELinux Policy

Can be found in:

```text
system/sepolicy/
device/<vendor>/<board>/sepolicy/
vendor/<vendor>/<board>/sepolicy/
```

---

# 16. How Build System Finds Modules

The build system scans the source tree for files like:

```text
Android.bp
Android.mk
```

Each module defines itself.

Examples:

```text
packages/apps/MyCarLauncher/Android.bp
hardware/mycompany/vehicle/Android.bp
vendor/mycompany/can_service/Android.bp
```

Then the build system decides:

```text
What to build
Where to install it
Which partition it belongs to
What dependencies it needs
```

---

# 17. Module Installation Partitions

Android build outputs can go into different partitions.

Common partitions:

```text
system
vendor
product
odm
system_ext
data
```

Simple idea:

```text
system     → Android framework and core system
vendor     → vendor-specific HALs and binaries
product    → product-specific apps/configs
odm        → device/manufacturer-specific files
system_ext → system extensions
data       → user/app data
```

---

# 18. Simple Full Flow

```text
Developer writes code
        |
        v
Adds Android.bp
        |
        v
Selects product/device
        |
        v
Runs build command
        |
        v
Soong/Kati generate Ninja rules
        |
        v
Ninja compiles and packages modules
        |
        v
Images are generated in out/
        |
        v
Images are flashed to device
```

---

# 19. Key Commands Preview

Common Android build commands:

```bash
source build/envsetup.sh
lunch
m
```

Meaning:

```text
source build/envsetup.sh → Load Android build environment
lunch                    → Select target product/device
m                        → Build
```

Example:

```bash
source build/envsetup.sh
lunch aosp_cf_x86_64_auto-userdebug
m
```

---

# 20. Key Summary

```text
Android Build System:
Converts AOSP source code and module definitions into bootable Android images.

Soong:
Main modern Android build system.

Android.bp:
Build file used by Soong.

Android.mk:
Old Make-based Android build file.

Kati:
Handles Android.mk legacy Make files.

Ninja:
Executes the real build commands.

out/:
Directory that contains build outputs and images.

device/:
Board/device configuration.

vendor/:
Vendor-specific implementation, binaries, HALs, policies, and configs.

frameworks/:
Android framework and system_server code.

packages/:
Android apps and services.

hardware/:
HAL interfaces and hardware abstraction code.
```

---

# 21. One-Line Memory

```text
Android.bp goes to Soong, Android.mk goes to Kati, both generate Ninja rules, and Ninja builds Android modules and images.
```

Another important line:

```text
AOSP source tree is divided by responsibility: framework, system, apps, hardware, device, vendor, and output.
```