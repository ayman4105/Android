# Android Boot Sequence for Automotive

## 1. Big Picture

Android boot sequence means what happens from **Power ON** until the user sees the Android UI or the infotainment screen.

```text
Power ON
   |
   v
Boot ROM
   |
   v
Bootloader
   |
   v
Linux Kernel
   |
   v
init
   |
   v
Zygote
   |
   v
system_server
   |
   v
System Services
   |
   v
Launcher / System UI
```

In Android Automotive:

```text
Power ON
   |
   v
Boot ROM
   |
   v
Bootloader
   |
   v
Linux Kernel
   |
   v
init
   |
   v
Zygote
   |
   v
system_server
   |
   v
CarService / Vehicle Services
   |
   v
System UI
   |
   v
Car Launcher / Infotainment UI
```

---

# 2. Boot ROM

## What is Boot ROM?

Boot ROM is the **first code that runs after Power ON**.

It is stored inside the SoC by the chip manufacturer.

Examples of SoC vendors:

```text
Qualcomm
NXP
MediaTek
TI
Renesas
```

Boot ROM is not part of Android itself.

---

## Boot ROM Responsibilities

```text
1. Start CPU execution
2. Search for the bootloader
3. Load bootloader from storage into RAM
4. Verify bootloader if secure boot is enabled
5. Jump to bootloader
```

Bootloader can be stored in:

```text
eMMC
UFS
SD Card
SPI Flash
NAND
```

---

## Boot ROM Flow

```text
+-----------+
| Power ON  |
+-----------+
      |
      v
+-----------+
| Boot ROM  |
+-----------+
      |
      v
+---------------------+
| Find Bootloader     |
| in boot storage     |
+---------------------+
      |
      v
+---------------------+
| Verify Bootloader   |
| if secure boot used |
+---------------------+
      |
      v
+---------------------+
| Load Bootloader     |
| into RAM            |
+---------------------+
      |
      v
+---------------------+
| Jump to Bootloader  |
+---------------------+
```

---

## Chain of Trust

Boot ROM can start the security chain.

```text
Boot ROM checks bootloader
Bootloader checks next stage
Next stage checks kernel / system
```

This is important in automotive because the infotainment system should not boot untrusted software.

---

## Key Point

```text
Boot ROM is the first code that runs after power on.
```

```text
Boot ROM finds, verifies, and loads the bootloader.
```

---

# 3. Bootloader

## What is Bootloader?

Bootloader is the software that runs after Boot ROM.

```text
Boot ROM loads Bootloader
Bootloader prepares system
Bootloader loads Linux Kernel
```

Simple meaning:

```text
Bootloader prepares the device before starting the Kernel.
```

---

## Bootloader Responsibilities

```text
1. Initialize basic hardware
2. Check boot mode
3. Verify boot images
4. Load Linux Kernel
5. Load ramdisk
6. Pass boot parameters to Kernel
7. Jump to Kernel
```

---

## 3.1 Initialize Basic Hardware

Bootloader prepares basic hardware needed to start the Kernel.

Examples:

```text
RAM
Clocks
Power rails
Storage controller
Basic display logo sometimes
UART debug sometimes
```

Flow:

```text
Bootloader
    |
    v
Prepare RAM and storage
    |
    v
Kernel can be loaded
```

---

## 3.2 Check Boot Mode

Bootloader decides which mode to enter.

Examples:

```text
Normal Boot
Recovery Mode
Fastboot Mode
Download Mode
Factory Mode
```

### Normal Boot

```text
Boot Android normally
```

### Recovery Mode

Used for:

```text
Factory reset
OTA update recovery
Repair system
```

### Fastboot Mode

Used with PC flashing tools.

Examples:

```bash
fastboot flash boot boot.img
fastboot flash system system.img
fastboot reboot
```

---

## 3.3 Verify Boot Images

Bootloader verifies important boot images.

Examples:

```text
boot.img
vendor_boot.img
dtbo.img
vbmeta.img
```

Modern Android uses:

```text
Android Verified Boot - AVB
```

Main idea:

```text
Bootloader verifies images before running them.
```

If the image is modified without a valid signature, the device may refuse to boot or show a warning/error mode.

---

## 3.4 Chain of Trust

```text
Boot ROM
   |
   v
Verifies Bootloader
   |
   v
Bootloader
   |
   v
Verifies boot.img / vbmeta
   |
   v
Kernel starts
```

This is very important in automotive systems for security.

---

## 3.5 Load Linux Kernel

Bootloader loads the Kernel into RAM.

```text
Storage
   |
   v
boot.img
   |
   v
Bootloader extracts Kernel
   |
   v
Kernel loaded into RAM
```

---

## 3.6 Load Ramdisk

In Android, `boot.img` often contains:

```text
Linux Kernel
Ramdisk
Boot parameters
```

The ramdisk is a small temporary filesystem loaded into RAM.

The most important file inside it is:

```text
/init
```

---

## 3.7 Pass Boot Parameters to Kernel

Bootloader passes important information to the Kernel.

Examples:

```text
Kernel command line
Device tree address
Ramdisk address
Memory layout
Boot slot A/B
Verified boot state
Hardware information
```

For A/B OTA systems, bootloader may pass active slot info:

```text
active_slot=a
```

or:

```text
active_slot=b
```

---

## 3.8 Jump to Kernel

After preparation, bootloader transfers control to the Kernel.

```text
Bootloader
   |
   v
Jump to Kernel entry point
   |
   v
Linux Kernel starts running
```

---

## Bootloader Flow

```text
+----------------+
| Boot ROM       |
+----------------+
        |
        v
+----------------+
| Bootloader     |
+----------------+
        |
        v
+----------------------------+
| Init RAM / Clocks / Storage|
+----------------------------+
        |
        v
+----------------+
| Check boot mode|
+----------------+
        |
        v
+----------------+
| Verify images  |
+----------------+
        |
        v
+----------------+
| Load Kernel    |
+----------------+
        |
        v
+----------------+
| Load Ramdisk   |
+----------------+
        |
        v
+----------------------------+
| Pass boot args to Kernel   |
+----------------------------+
        |
        v
+----------------+
| Jump to Kernel |
+----------------+
```

---

## Automotive Note: A/B OTA

In automotive, bootloader is important for OTA rollback.

Example:

```text
Current system: Slot A
New update written to: Slot B
Bootloader boots Slot B
If Slot B works → mark successful
If Slot B fails → rollback to Slot A
```

This helps avoid a broken infotainment system after a failed update.

---

## Boot ROM vs Bootloader

```text
Boot ROM:
- Inside SoC
- First code to run
- Fixed by SoC manufacturer
- Loads bootloader

Bootloader:
- Stored in boot storage
- Can be changed/updated
- Prepares hardware
- Verifies boot images
- Loads Kernel
```

---

## Key Point

```text
Bootloader prepares the hardware, verifies boot images, loads the Linux Kernel, and passes control to it.
```

---

# 4. Linux Kernel Boot

## What happens when Kernel starts?

After the bootloader jumps to the Kernel, the Linux Kernel begins booting.

```text
Bootloader
   |
   v
Linux Kernel
```

---

## Kernel Boot Responsibilities

```text
1. Decompress itself
2. Initialize CPU and memory
3. Read Device Tree
4. Initialize kernel subsystems
5. Initialize drivers
6. Mount ramdisk / early filesystem
7. Start first user-space process: init
```

---

## 4.1 Kernel Decompression

The Kernel image is usually compressed.

First step:

```text
Decompress kernel image
```

Flow:

```text
Compressed Kernel
        |
        v
Decompressed Kernel in RAM
        |
        v
Kernel starts executing
```

---

## 4.2 Initialize CPU and Memory

Kernel initializes low-level system parts:

```text
CPU cores
MMU
Virtual memory
RAM management
Interrupt system
Scheduler
Timers
```

---

## MMU

MMU means:

```text
Memory Management Unit
```

It allows the Kernel to provide virtual memory.

Each process can have isolated memory:

```text
Process A virtual memory
Process B virtual memory
Process C virtual memory
```

This is important for security and process isolation.

---

## 4.3 Read Device Tree

In embedded and automotive systems, the Kernel needs to know the board hardware.

This information usually comes from:

```text
Device Tree
```

or:

```text
.dtb
```

Bootloader passes the Device Tree Blob to the Kernel.

```text
Bootloader
   |
   v
Pass Device Tree Blob - DTB
   |
   v
Linux Kernel reads hardware description
```

---

## Device Tree describes things like:

```text
UART address
I2C address
SPI controller
GPIO pins
Display controller
Touch controller
CAN controller
Memory map
Interrupt numbers
```

Example:

```text
There is a UART controller at address 0x30860000
There is a CAN controller at address 0x30A00000
There is a display controller
There is 4GB RAM
```

Then the Kernel can initialize the right drivers.

---

## 4.4 Initialize Kernel Subsystems

Examples of kernel subsystems:

```text
Process scheduler
Memory manager
Interrupt controller
File systems
Block device layer
Network stack
Power management
Security modules
```

Example:

```text
Network stack
   |
   v
Allows Wi-Fi / Ethernet / CAN networking later
```

Example:

```text
File system layer
   |
   v
Allows Android to mount partitions
```

---

## 4.5 Initialize Drivers

After reading Device Tree, the Kernel initializes drivers.

Examples:

```text
Storage driver
Display driver
Touch driver
Audio driver
USB driver
Wi-Fi driver
Bluetooth driver
CAN driver
GPU driver
Power management driver
```

Automotive important drivers:

```text
Display
Touch
Audio
CAN / Ethernet
Bluetooth
Wi-Fi
Camera
Storage
```

Example:

```text
Device Tree says CAN controller exists
        |
        v
Kernel loads CAN driver
        |
        v
CAN hardware becomes available to Linux
```

---

## 4.6 Mount Ramdisk / Early Filesystem

The Kernel mounts the ramdisk loaded by the bootloader.

The important file inside the ramdisk is:

```text
/init
```

Flow:

```text
Kernel
   |
   v
Mount ramdisk
   |
   v
Find /init
```

---

## 4.7 Start init Process

After Kernel initialization, it starts the first user-space process:

```text
/init
```

This process has:

```text
PID 1
```

Flow:

```text
Linux Kernel
     |
     v
starts /init
     |
     v
init process PID 1
```

---

## Kernel Boot Flow

```text
+----------------------+
| Bootloader           |
+----------------------+
           |
           v
+----------------------+
| Jump to Kernel       |
+----------------------+
           |
           v
+----------------------+
| Decompress Kernel    |
+----------------------+
           |
           v
+----------------------+
| Init CPU / Memory    |
+----------------------+
           |
           v
+----------------------+
| Read Device Tree     |
+----------------------+
           |
           v
+----------------------+
| Init Subsystems      |
+----------------------+
           |
           v
+----------------------+
| Init Drivers         |
+----------------------+
           |
           v
+----------------------+
| Mount Ramdisk        |
+----------------------+
           |
           v
+----------------------+
| Start /init PID 1    |
+----------------------+
```

---

## Kernel Space vs User Space

```text
+----------------------------------+
| User Space                       |
| init, zygote, system_server, apps|
+----------------------------------+
| Kernel Space                     |
| Linux Kernel + Drivers           |
+----------------------------------+
| Hardware                         |
+----------------------------------+
```

### Kernel Space

Contains:

```text
Linux Kernel
Drivers
Memory manager
Scheduler
File system
Network stack
```

### User Space

Contains:

```text
init
Zygote
system_server
System Services
Apps
Native daemons
```

---

## Automotive Note

Kernel boot affects:

```text
Boot time
Display startup
Touch readiness
Audio readiness
CAN / Ethernet availability
Power management
Suspend / resume
```

Fast infotainment startup depends on fast driver initialization.

---

## Key Point

```text
Linux Kernel initializes CPU, memory, drivers, mounts the ramdisk, then starts the first Android user-space process: init.
```

---

# 5. Android init Process

## What is init?

`init` is the first user-space process in Android.

It is started by the Kernel.

```text
/init
```

It has:

```text
PID 1
```

Simple meaning:

```text
Kernel starts init
init starts Android system
```

---

## init Responsibilities

```text
1. Read init.rc files
2. Mount file systems
3. Setup permissions
4. Start native daemons
5. Start property service
6. Load SELinux policy
7. Start Zygote
```

---

## 5.1 Read init.rc Files

Android `init` reads configuration files called:

```text
init.rc
```

Examples:

```text
/init.rc
/system/etc/init/*.rc
/vendor/etc/init/*.rc
/odm/etc/init/*.rc
```

These files tell init what to do.

Examples:

```text
Mount partitions
Start services
Set permissions
Create directories
Start Zygote
```

Example:

```text
service zygote /system/bin/app_process64 ...
    class main
    user root
    group root
```

Meaning:

```text
There is a service called zygote.
Start it using this command.
Put it in class main.
```

---

## 5.2 Mount File Systems

init mounts Android partitions.

Examples:

```text
/system
/vendor
/product
/data
/metadata
```

Flow:

```text
Storage partitions
       |
       v
init mounts them
       |
       v
Android can read system files and apps
```

---

## Why /data is important

`/data` contains user and app data.

Examples:

```text
Installed apps data
Settings
User accounts
Databases
Caches
```

If `/data` is not mounted correctly, Android will not work normally.

---

## 5.3 Setup Permissions

init sets permissions for files and device nodes.

Examples:

```text
/dev/binder
/dev/hwbinder
/dev/vndbinder
/dev/input/event0
/dev/graphics/fb0
```

It controls:

```text
Who can read?
Who can write?
Who can use this device?
```

---

## 5.4 Start Native Daemons

init starts native daemons, usually written in C/C++.

Examples:

```text
ueventd
logd
servicemanager
hwservicemanager
vndservicemanager
surfaceflinger
audioserver
cameraserver
netd
vold
```

---

## ueventd

Responsible for device nodes.

Example:

```text
Touch driver ready
      |
      v
Kernel sends uevent
      |
      v
ueventd creates /dev/input/eventX
```

---

## logd

Responsible for Android logs.

When you use:

```bash
adb logcat
```

You read logs managed by `logd`.

---

## servicemanager

Important for Binder.

`servicemanager` is like a registry for Binder services.

Example:

```text
AudioService registers with servicemanager
App asks servicemanager for audio service
App gets Binder reference
```

Flow:

```text
System Service
      |
      v
Register service name
      |
      v
servicemanager
      |
      v
App can find service
```

---

## hwservicemanager

Like `servicemanager`, but used for HAL services.

```text
HAL service registers
       |
       v
hwservicemanager
```

---

## surfaceflinger

Responsible for composing UI layers on the screen.

Automotive example:

```text
Navigation layer
Media layer
Climate popup
System bar
Rear camera layer
        |
        v
SurfaceFlinger composes final screen
        |
        v
Display
```

---

## audioserver

Responsible for Android audio system.

```text
Music App
   |
   v
Audio Framework
   |
   v
audioserver
   |
   v
Audio HAL
   |
   v
Speaker
```

---

## 5.5 Start Property Service

Android has system properties.

They are key-value pairs.

Examples:

```text
ro.build.version.release
ro.product.model
sys.boot_completed
vendor.some.property
```

Read properties:

```bash
getprop
```

Set some properties:

```bash
setprop
```

`init` starts and manages property service.

---

## 5.6 Load SELinux Policy

Android uses SELinux for security.

SELinux controls:

```text
Which process can access which file?
Which process can talk to which service?
Which process can access which device?
```

Example automotive rule idea:

```text
App process should not access raw CAN device directly.
Only allowed service/HAL can access it.
```

---

## 5.7 Start Zygote

After init prepares the system, it starts:

```text
Zygote
```

Flow:

```text
init
 |
 v
starts Zygote
 |
 v
Zygote prepares app runtime
```

---

## init Flow

```text
+----------------------+
| Linux Kernel         |
+----------------------+
           |
           v
+----------------------+
| Start /init PID 1    |
+----------------------+
           |
           v
+----------------------+
| Read init.rc files   |
+----------------------+
           |
           v
+----------------------+
| Mount partitions     |
+----------------------+
           |
           v
+----------------------+
| Setup permissions    |
+----------------------+
           |
           v
+----------------------+
| Start native daemons |
+----------------------+
           |
           v
+----------------------+
| Start property svc   |
+----------------------+
           |
           v
+----------------------+
| Load SELinux policy  |
+----------------------+
           |
           v
+----------------------+
| Start Zygote         |
+----------------------+
```

---

## Automotive Note

In Android Automotive, init can start vendor or vehicle-specific services.

Examples:

```text
Vehicle HAL service
CAN daemon
Audio vendor service
Display vendor service
Power management service
Camera service
```

Vendor init files are often located in:

```text
/vendor/etc/init/
```

Example:

```text
init
 |
 v
starts vehicle HAL service
 |
 v
vehicle HAL connects to CAN / Ethernet
 |
 v
CarService can read vehicle properties
```

---

## Key Point

```text
init is the first user-space process in Android.
```

```text
init reads init.rc files, mounts partitions, starts native daemons, sets security, and starts Zygote.
```

---

# 6. Zygote Process

## What is Zygote?

Zygote is one of the most important Android processes.

Simple meaning:

```text
Zygote = parent process for Android Java/Kotlin apps
```

Most Android app processes are created from Zygote.

---

## Why the name Zygote?

In biology, zygote means the first cell that creates other cells.

Android uses the same idea:

```text
Zygote starts early
Then it creates child processes for apps
```

---

## Zygote Responsibilities

```text
1. Start Android Runtime - ART
2. Preload common Java classes
3. Preload common resources
4. Wait for requests to start apps
5. Fork new app processes
6. Start system_server
```

---

## 6.1 Start ART

ART means:

```text
Android Runtime
```

ART runs Java/Kotlin Android code.

Flow:

```text
init
 |
 v
starts Zygote
 |
 v
Zygote starts ART
```

---

## 6.2 Preload Common Java Classes

Zygote preloads classes used by most apps.

Examples:

```text
Activity
View
TextView
Button
Intent
Resources
Bitmap
System classes
```

Why?

```text
Faster app startup
Less repeated work
Better memory usage
```

---

## 6.3 Preload Common Resources

Zygote may preload common resources.

Examples:

```text
Default themes
System drawables
Common resources
```

---

## 6.4 Wait for Requests to Start Apps

Zygote waits for requests from:

```text
ActivityManagerService
```

Example:

```text
User clicks Music App
        |
        v
Launcher asks ActivityManagerService
        |
        v
ActivityManagerService asks Zygote
        |
        v
Zygote creates new app process
```

---

## 6.5 Fork New App Processes

When Android needs a new app process, Zygote calls:

```text
fork()
```

Flow:

```text
Zygote
  |
  | fork()
  v
New App Process
```

Then the child process loads and runs the app-specific code.

---

## Why Fork from Zygote?

Because Zygote is already loaded with:

```text
ART
Common classes
Common resources
```

Without Zygote:

```text
Start ART
Load classes
Load resources
Start app
```

With Zygote:

```text
Fork from ready Zygote
Start app code
```

This is faster.

---

## 6.6 Start system_server

During boot, Zygote also forks:

```text
system_server
```

Flow:

```text
Zygote
   |
   | fork()
   v
system_server
```

`system_server` starts many Android system services.

---

## Zygote Flow

```text
+----------------------+
| init                 |
+----------------------+
           |
           v
+----------------------+
| Start Zygote         |
+----------------------+
           |
           v
+----------------------+
| Start ART            |
+----------------------+
           |
           v
+----------------------+
| Preload classes      |
+----------------------+
           |
           v
+----------------------+
| Preload resources    |
+----------------------+
           |
           v
+----------------------+
| Fork system_server   |
+----------------------+
           |
           v
+----------------------+
| Wait for app starts  |
+----------------------+
           |
           v
+----------------------+
| Fork app processes   |
+----------------------+
```

---

## Zygote Diagram

```text
                         +----------------+
                         |     init       |
                         +----------------+
                                  |
                                  v
                         +----------------+
                         |    Zygote      |
                         +----------------+
                         /       |        \
                        /        |         \
                       v         v          v
              system_server   Music App   Settings App
```

---

## Android Automotive Note

In Android Automotive, Zygote starts/forks:

```text
Launcher / Home UI
Settings app
Media app
Climate app
Navigation app
System UI
```

Example:

```text
Infotainment boots
        |
        v
Zygote ready
        |
        v
system_server starts services
        |
        v
Launcher process created from Zygote
        |
        v
Car UI appears
```

---

## Native Daemons vs Zygote Apps

Not every process comes from Zygote.

Native daemons are started by init.

Examples:

```text
surfaceflinger
audioserver
cameraserver
logd
netd
vold
```

Apps and Java framework processes usually come from Zygote.

```text
init starts native daemons
Zygote starts Java/Kotlin app processes
```

---

## init vs Zygote

```text
init:
- First user-space process
- PID 1
- Starts native daemons
- Starts Zygote
- Reads init.rc

Zygote:
- Starts ART
- Preloads Java classes/resources
- Forks system_server
- Forks app processes
```

---

## Key Point

```text
Zygote is a preloaded Android runtime process that forks system_server and app processes.
```

---

# 7. Why Zygote is Better for Android Apps

## Linux Normal Process Creation

In normal Linux, when you run a program:

```bash
firefox
```

or:

```bash
ls
```

Usually the flow is:

```text
Shell
  |
  v
fork()
  |
  v
exec()
  |
  v
New program starts from zero
```

---

## fork + exec

### fork()

Creates a copy of the current process.

```text
Shell
  |
  v
fork()
  |
  v
Child copy of shell
```

### exec()

Replaces the child process code with a new program.

```text
Child shell copy
  |
  v
exec("firefox")
  |
  v
Firefox program
```

So normal Linux process creation is usually:

```text
fork()
then
exec()
```

The new program loads its libraries and runtime from the beginning.

---

## Android App Process Creation

Android apps are usually written in:

```text
Java / Kotlin
```

So every app needs:

```text
ART runtime
Java classes
Android framework classes
Resources
App environment
```

If Android started every app from zero:

```text
fork()
exec()
Start ART from zero
Load framework classes
Load resources
Start app
```

This would be slow.

---

## Android Solution: Zygote

Android keeps a ready process called Zygote.

Zygote already loaded:

```text
ART
Common Java classes
Common Android framework classes
Common resources
```

When Android needs a new app:

```text
Zygote
  |
  v
fork()
  |
  v
New App Process
```

The app process inherits many prepared things from Zygote.

---

## Linux vs Android Process Creation

### Linux Normal Process Creation

```text
Shell
  |
  v
fork()
  |
  v
Child Process
  |
  v
exec(program)
  |
  v
Load program from disk
  |
  v
Load libraries
  |
  v
Initialize runtime
  |
  v
Program starts
```

### Android App Process Creation

```text
Zygote already running
  |
  | already loaded ART + common classes
  v
fork()
  |
  v
App Process
  |
  v
Load app-specific code
  |
  v
Start MainActivity
```

---

## Copy-On-Write

Linux `fork()` uses:

```text
Copy-On-Write
```

Meaning:

```text
Read shared page  → no copy
Write to page     → copy this page only
```

When Zygote forks a new app:

```text
Zygote memory pages
        |
        v
Shared with child process
```

As long as the app only reads shared data, memory stays shared.

If the app modifies memory, only that page is copied.

---

## Example

Zygote loads this class once:

```text
android.view.View
```

Without Zygote:

```text
Music App loads View
Settings App loads View
Maps App loads View
```

With Zygote:

```text
Zygote loads View once
      |
      v
Music App shares it
Settings App shares it
Maps App shares it
```

This saves:

```text
Startup time
RAM usage
CPU work
Disk reads
```

---

## Why Normal Linux Does Not Need Zygote Like Android

Linux runs many different program types:

```text
bash
ls
gcc
firefox
python
vim
docker
```

They do not all share one common runtime.

Android apps are more similar.

Most apps share:

```text
ART
Android Framework
Java/Kotlin classes
Resources
App lifecycle
```

So Android benefits from Zygote.

---

## Key Point

```text
Linux creates processes using fork + exec.
Each program loads its own runtime and libraries.
```

```text
Android creates app processes by forking from Zygote.
Zygote already has ART and common framework classes loaded.
```

```text
Zygote makes Android app startup faster by preloading the runtime and common framework classes, then forking app processes using Copy-On-Write.
```

---

# 8. system_server

## What is system_server?

After Zygote starts, it forks a very important process:

```text
system_server
```

Flow:

```text
Zygote
   |
   | fork()
   v
system_server
```

`system_server` hosts most core Java Android system services.

---

## Why system_server is important

Apps do not directly control the Android system.

Apps request services from system services.

Examples:

```text
App wants to start Activity
        |
        v
ActivityManagerService

App wants audio volume
        |
        v
AudioService

App wants vehicle speed
        |
        v
CarService
```

---

## system_server Starts Services

Examples of services inside `system_server`:

```text
ActivityManagerService
PackageManagerService
WindowManagerService
PowerManagerService
InputManagerService
DisplayManagerService
AudioService
LocationManagerService
NotificationManagerService
CarService
```

---

## ServiceManager

`servicemanager` is started earlier by init.

It is a registry for Binder services.

System services register themselves with it.

Examples:

```text
ActivityManagerService registers as "activity"
PackageManagerService registers as "package"
WindowManagerService registers as "window"
AudioService registers as "audio"
```

Flow:

```text
system_server
   |
   v
Starts ActivityManagerService
   |
   v
Registers it in ServiceManager
   |
   v
Apps can find it using Binder
```

---

## Example: Open Music App

```text
User clicks Music icon
        |
        v
Launcher
        |
        v
ActivityManagerService
        |
        v
Zygote
        |
        v
Music App process
        |
        v
MainActivity
```

ActivityManagerService organizes app startup.

---

## Important System Services

### ActivityManagerService

Responsible for:

```text
Starting apps
Stopping apps
Activity lifecycle
Task stack
Process priority
```

---

### PackageManagerService

Responsible for:

```text
Installed apps
APK information
Permissions
App signatures
Manifest parsing
```

---

### WindowManagerService

Responsible for:

```text
Windows
Screen layout
Focus
Which window is on top
Input target
```

Automotive example:

```text
Rear camera view should appear above navigation.
```

---

### PowerManagerService

Responsible for:

```text
Sleep
Wakeup
Screen on/off
Power states
Suspend/resume
```

Automotive power states:

```text
Ignition ON
Ignition OFF
Sleep
Wakeup
Deep sleep
```

---

### InputManagerService

Responsible for input devices:

```text
Touch
Keyboard
Buttons
Rotary controller
Steering wheel buttons
```

Automotive inputs:

```text
Touch screen
Physical buttons
Rotary knob
Steering controls
```

---

### DisplayManagerService

Responsible for displays.

Automotive can have multiple displays:

```text
Center infotainment display
Instrument cluster display
Rear seat display
HUD display
```

---

### AudioService

Responsible for:

```text
Audio routing
Volume
Audio focus
```

Automotive audio sources:

```text
Music
Navigation guidance
Phone call
Warning chimes
Voice assistant
Parking sensor sounds
```

Example:

```text
Music playing
Navigation voice starts
AudioService lowers music volume
Navigation instruction plays
Music returns
```

---

### CarService

Important for Android Automotive.

CarService is the bridge between apps and vehicle features.

Flow:

```text
Automotive App
      |
      v
Car API
      |
      v
Binder IPC
      |
      v
CarService
      |
      v
Vehicle HAL
      |
      v
CAN / Ethernet / Vehicle Network
```

Example: Read vehicle speed

```text
Speed App
   |
   v
CarPropertyManager
   |
   v
Binder IPC
   |
   v
CarService
   |
   v
Vehicle HAL
   |
   v
CAN Driver / Vehicle Network
   |
   v
ECU sends speed
```

The app does not read CAN directly.

---

## Why many services run inside system_server

Reasons:

```text
Performance
Shared framework state
Less IPC overhead between services
Easier coordination
```

But `system_server` is sensitive.

If it crashes badly, Android may restart or soft reboot.

---

## Native Services vs System Services

### Native Services

Usually C/C++ processes started by init.

Examples:

```text
surfaceflinger
audioserver
cameraserver
netd
vold
logd
```

### System Services

Usually Java services running inside system_server.

Examples:

```text
ActivityManagerService
PackageManagerService
WindowManagerService
PowerManagerService
CarService
```

Diagram:

```text
init
 ├── native daemons
 │    ├── surfaceflinger
 │    ├── audioserver
 │    ├── netd
 │    └── logd
 │
 └── zygote
      └── system_server
           ├── ActivityManagerService
           ├── PackageManagerService
           ├── WindowManagerService
           ├── PowerManagerService
           └── CarService
```

---

## system_server Flow

```text
+----------------------+
| Zygote               |
+----------------------+
           |
           v
+----------------------+
| Fork system_server   |
+----------------------+
           |
           v
+----------------------+
| Start core services  |
+----------------------+
           |
           v
+-------------------------+
| Register with Binder    |
| ServiceManager          |
+-------------------------+
           |
           v
+----------------------+
| Start other services |
+----------------------+
           |
           v
+----------------------+
| System ready         |
+----------------------+
           |
           v
+----------------------+
| Start Launcher / UI  |
+----------------------+
```

---

## Automotive Boot Note

In Android Automotive, after `system_server` starts:

```text
CarService
Vehicle HAL
Power policy
Audio routing
Display management
Input from steering/buttons
Rear camera handling
```

must become ready.

Example:

```text
system_server starts
        |
        v
CarService starts
        |
        v
CarService connects to Vehicle HAL
        |
        v
Vehicle properties become available
        |
        v
Launcher / Car UI starts
        |
        v
Infotainment screen ready
```

---

## Key Point

```text
system_server is the main Android process that hosts core Java system services.
```

```text
It manages apps, windows, permissions, power, input, audio, and automotive services.
```

---

# 9. Launcher and System UI

After `system_server` starts the core system services, Android starts the user interface.

```text
System Services Ready
   |
   v
System UI
   |
   v
Launcher
```

---

## What is Launcher?

Launcher is the Home Screen app.

In normal Android:

```text
Home Screen
App icons
Widgets
App drawer
```

In Android Automotive:

```text
Infotainment Home Screen
Media shortcut
Navigation shortcut
Phone shortcut
Climate controls
Vehicle settings
```

---

## Who starts Launcher?

Usually:

```text
ActivityManagerService
```

When the system is ready, ActivityManagerService finds the Home Activity.

Conceptual manifest idea:

```xml
<intent-filter>
    <action android:name="android.intent.action.MAIN" />
    <category android:name="android.intent.category.HOME" />
</intent-filter>
```

Meaning:

```text
This Activity can act as Home / Launcher screen.
```

---

## Launcher Start Flow

```text
system_server
      |
      v
ActivityManagerService becomes ready
      |
      v
Find Home Activity / Launcher
      |
      v
Ask Zygote to fork Launcher process
      |
      v
Launcher app process starts
      |
      v
Launcher Activity opens
      |
      v
User sees Home Screen
```

Diagram:

```text
System Services Ready
        |
        v
ActivityManagerService
        |
        v
Find Launcher Activity
        |
        v
Zygote forks Launcher process
        |
        v
Launcher onCreate()
        |
        v
Home screen appears
```

---

## What is System UI?

System UI controls persistent system interface elements.

In normal Android:

```text
Status bar
Quick settings
Navigation buttons
Notifications
Lock screen parts
```

In Android Automotive:

```text
Top status bar
Bottom navigation bar
HVAC quick controls
Volume UI
System alerts
Rear camera overlay
Power state UI
```

---

## Launcher vs System UI

### Launcher

Home screen.

```text
Main infotainment screen
App shortcuts
Navigation tile
Media tile
Phone tile
```

### System UI

System elements that appear above or around apps.

```text
Status bar
Navigation bar
Volume overlay
Warning popup
Climate quick panel
```

Visual idea:

```text
+------------------------------------------------+
| System UI: Status Bar                          |
+------------------------------------------------+
|                                                |
| Launcher / Current App                         |
|                                                |
|                                                |
+------------------------------------------------+
| System UI: Navigation / HVAC Controls          |
+------------------------------------------------+
```

---

## Automotive Example Screen

```text
+------------------------------------------------+
| Time | Network | Bluetooth | Profile           |
+------------------------------------------------+
|                                                |
|   Navigation Tile       Media Tile             |
|                                                |
|   Phone Tile            Vehicle Settings       |
|                                                |
+------------------------------------------------+
| Climate: 22°C | Fan 2 | Home | Apps | Volume   |
+------------------------------------------------+
```

---

## Launcher is also an App

Launcher starts from Zygote like other apps.

```text
Zygote
  |
  | fork()
  v
Launcher process
  |
  v
Launcher Activity
```

---

## Launcher uses Binder

Launcher communicates with system services using Binder.

Example: get installed apps.

```text
Launcher wants list of installed apps
        |
        v
PackageManager API
        |
        v
Binder IPC
        |
        v
PackageManagerService
```

Example: start Music App.

```text
Launcher wants to start Music App
        |
        v
ActivityManager API
        |
        v
Binder IPC
        |
        v
ActivityManagerService
```

---

## BOOT_COMPLETED

After Android finishes booting, it sends a broadcast:

```text
BOOT_COMPLETED
```

Flow:

```text
System boot completed
        |
        v
Send BOOT_COMPLETED broadcast
        |
        v
Allowed apps/services receive it
```

Automotive examples after boot completed:

```text
Media indexing
Bluetooth auto-connect
Navigation preparation
User profile loading
Vehicle data monitoring
```

---

# 10. Full Android Automotive Boot Flow

```text
Driver opens car / ignition ON
        |
        v
SoC powers up
        |
        v
Boot ROM loads Bootloader
        |
        v
Bootloader verifies boot image
        |
        v
Linux Kernel starts
        |
        v
Kernel initializes display, touch, storage, CAN/Ethernet drivers
        |
        v
init starts native daemons and Zygote
        |
        v
Zygote starts system_server
        |
        v
system_server starts ActivityManager, WindowManager, PowerManager
        |
        v
CarService starts and connects to Vehicle HAL
        |
        v
System UI starts
        |
        v
Car Launcher starts
        |
        v
Infotainment screen is ready
```

---

# 11. Full Boot Diagram

```text
+-----------------------------+
| Power ON / Ignition ON      |
+-----------------------------+
              |
              v
+-----------------------------+
| Boot ROM                    |
| First code in SoC           |
+-----------------------------+
              |
              v
+-----------------------------+
| Bootloader                  |
| Init hardware + verify boot |
+-----------------------------+
              |
              v
+-----------------------------+
| Linux Kernel                |
| Drivers + memory + init     |
+-----------------------------+
              |
              v
+-----------------------------+
| init PID 1                  |
| rc files + daemons + SELinux|
+-----------------------------+
              |
              v
+-----------------------------+
| Zygote                      |
| ART + preload + fork        |
+-----------------------------+
              |
              v
+-----------------------------+
| system_server               |
| Core Java system services   |
+-----------------------------+
              |
              v
+-----------------------------+
| CarService / System Services|
| Vehicle + Android services  |
+-----------------------------+
              |
              v
+-----------------------------+
| System UI + Launcher        |
| Infotainment screen ready   |
+-----------------------------+
```

---

# 12. Final Summary

```text
Boot ROM:
First code after power on. Finds and loads bootloader.

Bootloader:
Prepares hardware, verifies images, loads Kernel and ramdisk.

Linux Kernel:
Initializes CPU, memory, drivers, and starts /init.

init:
First user-space process. Reads init.rc, mounts partitions, starts native daemons, loads SELinux, starts Zygote.

Zygote:
Starts ART, preloads common classes/resources, forks system_server and app processes.

system_server:
Hosts core Android Java system services like ActivityManager, PackageManager, WindowManager, PowerManager, AudioService, and CarService.

System Services:
Provide Android and automotive features to apps through Binder IPC.

Launcher / System UI:
Final user interface shown to the user.
```

---

# 13. One-Line Memory

```text
Power ON → Boot ROM → Bootloader → Kernel → init → Zygote → system_server → System Services → Launcher
```

For Android Automotive:

```text
Ignition ON → Boot ROM → Bootloader → Kernel → init → Zygote → system_server → CarService → System UI → Car Launcher
```

---

# 14. Most Important Idea

```text
Bootloader starts the Kernel.
Kernel starts init.
init starts Zygote.
Zygote starts system_server and apps.
system_server starts Android services.
ActivityManager starts the Launcher.
```