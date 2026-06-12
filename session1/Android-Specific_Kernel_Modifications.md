# Android-Specific Kernel Modifications

## 1. Big Picture

Android uses the Linux Kernel, but Android does **not** use a normal Linux kernel exactly as it is.

Android needs extra kernel features and configurations to support:

```text
Apps
Security
Binder IPC
Power management
Memory management
Verified boot
Vendor hardware separation
Automotive hardware drivers
```

Simple idea:

```text
Android Kernel = Linux Kernel + Android-specific features/configuration
```

Architecture view:

```text
+--------------------------------------+
| Android Apps                         |
+--------------------------------------+
| Android Framework / System Services  |
+--------------------------------------+
| HAL / Native Services                |
+--------------------------------------+
| Android Kernel Features              |
| Binder, SELinux, Power, GKI, etc.    |
+--------------------------------------+
| Hardware                             |
+--------------------------------------+
```

---

# 2. Why Android Needs Kernel Modifications

A normal Linux kernel is designed for many systems:

```text
Servers
Desktops
Embedded Linux devices
Networking devices
```

Android has different needs:

```text
Each app runs in its own sandbox
Apps talk to services using Binder IPC
Power must be controlled carefully
Memory pressure must be handled quickly
System partitions must be verified
Vendor drivers must be separated from Android framework
```

In Android Automotive, this is even more important because the system may interact with vehicle-related hardware.

Examples:

```text
CAN
Ethernet
Display
Touch
Audio
Camera
Power management
Vehicle sensors
```

---

# 3. Main Android-Specific Kernel Features

Important Android kernel features:

```text
1. Binder Driver
2. Power Management / Wake Locks
3. Low Memory Handling / LMKD Support
4. Shared Memory Support
5. SELinux Support
6. Android Verified Boot / dm-verity
7. cgroups / Process Control
8. GKI / KMI
```

---

# 4. Binder Driver

## What is Binder?

Binder is Android's main IPC mechanism.

IPC means:

```text
Inter-Process Communication
```

Binder allows one process to call another process safely.

Example:

```text
App Process
    |
    v
Binder Driver in Kernel
    |
    v
System Service Process
```

Binder device nodes:

```text
/dev/binder
/dev/hwbinder
/dev/vndbinder
```

---

## Why Binder Needs Kernel Support

The Binder driver inside the kernel helps with:

```text
Passing messages between processes
Knowing the caller UID/PID
Managing remote object references
Handling blocking and wakeup
Supporting Android permissions/security model
```

Example: Music App talks to AudioService

```text
Music App
   |
   v
AudioManager API
   |
   v
Binder Driver
   |
   v
AudioService inside system_server
   |
   v
Audio HAL
   |
   v
Audio Driver
   |
   v
Speaker
```

---

## Binder in Android Automotive

Example: App reads vehicle speed.

```text
Vehicle App
   |
   v
CarPropertyManager
   |
   v
Binder Driver
   |
   v
CarService
   |
   v
Vehicle HAL
   |
   v
CAN / Ethernet Driver
   |
   v
Vehicle ECU
```

Key point:

```text
Android kernel must support Binder.
```

---

# 5. Power Management / Wake Locks

Android needs strong power management.

A wake lock means:

```text
Do not suspend the system now.
Something important is running.
```

Examples:

```text
Music is playing
Navigation is running
Bluetooth call is active
OTA update is running
Rear camera is active
```

Wake lock flow:

```text
App / System Service
       |
       v
Requests Wake Lock
       |
       v
PowerManagerService
       |
       v
Kernel Power Management
       |
       v
System stays awake
```

---

## Automotive Example

Rear camera must stay active when reversing.

```text
Rear Camera active
       |
       v
System must not suspend
       |
       v
Wake lock is held
```

If ignition is OFF:

```text
Ignition OFF
   |
   v
System should sleep or enter low-power mode
```

But some services may still wake the system:

```text
Remote wakeup
OTA background task
Alarm
Vehicle monitoring
```

---

# 6. Low Memory Handling / LMKD

Android runs many apps, so memory pressure must be handled carefully.

Old Android used a kernel Low Memory Killer.

Modern Android uses:

```text
lmkd = Low Memory Killer Daemon
```

The kernel helps notify userspace about memory pressure.

Flow:

```text
RAM pressure
    |
    v
Kernel memory pressure signals
    |
    v
lmkd
    |
    v
Kill cached/background process
    |
    v
Free memory
```

---

## Example

Important processes:

```text
Navigation App
Music App
System UI
CarService
Rear Camera
```

Less important process:

```text
Old background app
```

When RAM is low:

```text
Kernel detects pressure
        |
        v
lmkd chooses low-priority process
        |
        v
Kills background app
        |
        v
Keeps critical services alive
```

In Automotive, this is important because critical components should not be killed easily:

```text
System UI
Navigation
Rear camera
CarService
Audio
```

---

# 7. Shared Memory Support

Binder is good for small commands.

But Binder is not good for very large data.

Examples of large data:

```text
Image buffers
Audio buffers
Video frames
Graphics data
```

So Android needs shared memory mechanisms.

Simple idea:

```text
Binder:
Send control command

Shared Memory:
Share large data buffer
```

Example:

```text
Camera Service
   |
   v
Shares image buffer
   |
   v
App reads frame data
```

Key point:

```text
Android needs shared memory support for efficient large data sharing.
```

---

# 8. SELinux Support

Android uses SELinux for strong security.

SELinux means:

```text
Security-Enhanced Linux
```

It adds mandatory access control.

Normal Linux permissions are based on:

```text
User
Group
Other
```

SELinux is more strict and can control:

```text
Which process can access which file
Which process can talk to which service
Which process can access which device node
Which HAL can access hardware
```

---

## Automotive SELinux Example

Normal app should not access raw CAN device directly.

```text
Normal App
   |
   v
Tries to access /dev/can0
   |
   v
SELinux checks policy
   |
   v
Denied
```

But Vehicle HAL may be allowed:

```text
Vehicle HAL
   |
   v
Access /dev/can0
   |
   v
SELinux policy allows it
```

Key point:

```text
SELinux protects vehicle-related hardware from direct access by normal apps.
```

---

# 9. Android Verified Boot / dm-verity

Android needs to make sure system partitions are trusted.

Important partitions:

```text
boot
system
vendor
product
vbmeta
```

Verified Boot idea:

```text
Bootloader verifies images
Kernel can use dm-verity to verify block devices at runtime
```

Simple flow:

```text
Bootloader
   |
   v
Verifies boot image and vbmeta
   |
   v
Kernel starts
   |
   v
dm-verity verifies partitions while reading
```

Why this matters:

```text
Prevents modified system partitions
Prevents malicious boot images
Improves platform security
```

In Automotive:

```text
Verified boot helps ensure trusted software is running.
```

---

# 10. cgroups / Process Control

Android has different process types:

```text
Foreground app
Background app
Cached app
System service
Critical service
```

The kernel helps Android control resources using:

```text
cgroups
cpusets
scheduler tuning
```

Android can control:

```text
CPU usage
Memory usage
I/O priority
Process groups
Foreground/background scheduling
```

Example:

```text
Foreground Navigation App
        |
        v
Higher priority

Cached Background App
        |
        v
Lower priority
```

Automotive important processes:

```text
Rear camera
Navigation
Audio
CarService
System UI
```

These should have better priority than normal background apps.

---

# 11. GKI / Generic Kernel Image

## What is GKI?

GKI means:

```text
Generic Kernel Image
```

The idea:

```text
Keep the Android core kernel as generic and unified as possible.
Move vendor/device-specific drivers outside the core kernel as modules.
```

---

# 12. The Problem Before GKI: Kernel Fragmentation

Before GKI, Android kernels were often built like this:

```text
Linux Upstream Kernel
        |
        v
Android Common Kernel
        |
        v
SoC Vendor Kernel
        |
        v
Device / Board Kernel
```

Example:

```text
Linux Kernel
   |
   v
Android changes
   |
   v
Qualcomm / NXP / MediaTek changes
   |
   v
Board-specific drivers
   |
   v
Final device kernel
```

This created many different kernel versions.

```text
Device A Kernel
Device B Kernel
Device C Kernel
Device D Kernel
```

This problem is called:

```text
Kernel Fragmentation
```

---

## What Does Kernel Fragmentation Mean?

Kernel fragmentation means:

```text
Each vendor or device has its own heavily modified kernel.
```

Example:

```text
Linux Kernel original
      |
      v
Android Common Kernel
      |
      +----> Qualcomm Kernel + Samsung device changes
      |
      +----> NXP Kernel + Automotive board changes
      |
      +----> MediaTek Kernel + another device changes
      |
      +----> Renesas Kernel + another board changes
```

So instead of one common kernel, we get many different kernels.

```text
Kernel A
Kernel B
Kernel C
Kernel D
```

Each one has different patches and drivers.

---

## Why Kernel Fragmentation is a Problem

When a security update appears:

```text
Security Patch
```

Each vendor must apply it to their own modified kernel.

But because every kernel is different:

```text
Patch works on NXP kernel
Patch conflicts on Qualcomm kernel
Patch breaks Renesas kernel
```

This causes:

```text
Delayed updates
Delayed security patches
Harder testing
Higher maintenance cost
Driver compatibility problems
```

---

# 13. GKI Solution

GKI reduces fragmentation by separating:

```text
Generic Android Kernel Core
```

from:

```text
Vendor-specific drivers
```

Instead of putting everything inside one kernel image:

```text
Core kernel + vendor drivers + board hacks
```

Android tries to make it:

```text
GKI Kernel Core + Vendor Kernel Modules
```

---

## Before GKI

```text
+--------------------------------------+
| Device-specific Kernel               |
|                                      |
| Core Linux                           |
| Android changes                      |
| SoC vendor changes                   |
| Board drivers                        |
| Vendor modifications                 |
+--------------------------------------+
```

Problems:

```text
Hard to update
Hard to maintain
Hard to test
Different for every device
```

---

## After GKI

```text
+--------------------------------------+
| Generic Kernel Image - GKI           |
| Core Linux + Android core features   |
+--------------------------------------+

+--------------------------------------+
| Vendor Kernel Modules                |
| Display driver                       |
| Audio driver                         |
| Camera driver                        |
| CAN / Ethernet driver                |
| Touch driver                         |
| Power driver                         |
+--------------------------------------+
```

Benefits:

```text
Core kernel is more unified
Vendor drivers are separated
Security updates are easier
Maintenance is cleaner
```

---

# 14. Kernel Modules

A kernel module is a piece of kernel code that can be loaded separately.

Its extension is usually:

```text
.ko
```

Examples:

```text
can_driver.ko
audio_codec.ko
display_panel.ko
touch_driver.ko
camera_sensor.ko
```

Flow:

```text
Kernel boots
    |
    v
init loads modules
    |
    v
Drivers become available
```

In Android, vendor modules may be stored in partitions like:

```text
vendor_boot
vendor_dlkm
odm_dlkm
```

Example path:

```text
/vendor/lib/modules/
    can_driver.ko
    audio_driver.ko
    display_driver.ko
```

or:

```text
/vendor_dlkm/lib/modules/
    vendor drivers
```

---

# 15. KMI / Kernel Module Interface

## What is KMI?

KMI means:

```text
Kernel Module Interface
```

It is the interface between:

```text
GKI Core Kernel
```

and:

```text
Vendor Kernel Modules
```

Diagram:

```text
+-------------------------------+
| GKI Kernel Core               |
| exports allowed symbols       |
+-------------------------------+
              |
              | KMI
              v
+-------------------------------+
| Vendor Kernel Modules         |
| use exported KMI symbols      |
+-------------------------------+
              |
              v
+-------------------------------+
| Hardware                      |
+-------------------------------+
```

---

## Simple Meaning of KMI

A vendor module needs to use some functions from the kernel.

Example imaginary kernel functions:

```c
register_driver();
request_irq();
devm_kmalloc();
gpio_get_value();
```

The module needs these functions to exist and stay compatible.

KMI defines:

```text
Which kernel symbols/functions vendor modules are allowed to use.
```

If the GKI kernel changes these symbols badly:

```text
Vendor module may fail to load.
```

So stable KMI means:

```text
Vendor modules can keep working with updated GKI kernel inside the same branch.
```

---

# 16. KMI is Not Stable Forever

KMI is stable only inside a specific Android version and kernel branch.

Examples:

```text
android14-6.1
android15-6.6
android16-6.12
```

Meaning:

```text
A module built for android15-6.6 should stay compatible with android15-6.6 updates.
```

But not necessarily with:

```text
android16-6.12
```

---

# 17. GKI Automotive Example

Imagine Android Automotive running on an NXP board.

## GKI Kernel Core

Contains general kernel features:

```text
Scheduler
Memory management
Binder driver
SELinux support
Networking stack
File systems
Power management core
```

## Vendor Modules

Contain board-specific drivers:

```text
NXP CAN driver
NXP Ethernet driver
NXP audio driver
NXP display driver
NXP touch driver
NXP camera driver
NXP power driver
```

Diagram:

```text
Android Automotive Framework
        |
        v
CarService / System Services
        |
        v
Vehicle HAL
        |
        v
GKI Kernel Core
        |
        v
Vendor Module: CAN driver .ko
        |
        v
CAN Controller
        |
        v
Vehicle ECU
```

---

# 18. GKI and Boot Flow

During boot:

```text
Bootloader loads GKI kernel
        |
        v
Kernel starts
        |
        v
First-stage init loads needed vendor modules
        |
        v
Hardware drivers become ready
        |
        v
Android services continue boot
```

Simplified image relationship:

```text
boot.img
   |
   v
GKI kernel

vendor_boot.img / vendor_dlkm
   |
   v
Vendor modules
```

Simple meaning:

```text
Kernel core is in one place.
Vendor-specific drivers are in another place.
```

---

# 19. GKI vs Embedded Linux Normal Kernel

## Embedded Linux Normal Style

```text
Linux kernel source
   |
   v
Add board patches
Add drivers directly
Change configs
Build zImage/Image
```

Usually everything is built into one custom board kernel.

---

## Android GKI Style

```text
Use GKI core kernel
Build vendor drivers as modules
Keep KMI compatibility
Put modules in vendor partitions
Boot Android with GKI + vendor modules
```

---

# 20. Simple Analogy

Think of GKI like a standard motherboard.

```text
GKI = stable main board / core
KMI = fixed connector
Vendor module = part connected to the connector
Hardware = real device
```

If the connector shape stays the same:

```text
Vendor module still works
```

If the connector changes:

```text
Vendor module may not work
```

So:

```text
Stable KMI = vendor modules still work
Broken KMI = vendor modules may fail
```

---

# 21. Full Android Kernel Feature View

```text
Android Apps
    |
    v
Application Framework
    |
    v
Binder IPC
    |
    v
System Services
    |
    v
HAL
    |
    v
Android Kernel Features
    |
    +--> Binder Driver
    +--> Power Management / Wake Locks
    +--> SELinux
    +--> cgroups
    +--> Memory Pressure Support
    +--> Verified Boot / dm-verity
    +--> Vendor Modules / GKI
    |
    v
Hardware
```

---

# 22. Full Automotive Example

Example: App displays vehicle speed.

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
SELinux allows Vehicle HAL to access CAN
   |
   v
CAN Kernel Driver
   |
   v
CAN Controller
   |
   v
Vehicle ECU
```

Kernel-related Android parts in this flow:

```text
Binder:
App talks to CarService

SELinux:
App cannot access CAN directly

Kernel Driver:
Vehicle HAL accesses CAN hardware

cgroups:
Keeps critical services responsive

Power management:
Handles sleep/wakeup correctly

GKI/KMI:
Keeps core kernel separated from vendor CAN driver
```

---

# 23. Final Summary

```text
Android Kernel:
Linux Kernel configured and extended for Android requirements.
```

Important Android-specific kernel features:

```text
Binder Driver:
Supports Android IPC.

Wake Locks / Power Management:
Prevents suspend during important tasks.

LMKD Support:
Helps Android kill low-priority apps under memory pressure.

Shared Memory:
Efficiently shares large buffers between processes.

SELinux:
Enforces strict security policies.

Verified Boot / dm-verity:
Verifies boot and system partitions.

cgroups:
Controls CPU, memory, and process priority.

GKI:
Generic Kernel Image that keeps the core kernel more unified.

KMI:
Stable interface between GKI core kernel and vendor kernel modules.

Kernel Fragmentation:
Problem where each vendor/device has its own heavily modified kernel.
```

---

# 24. One-Line Memory

```text
Android Kernel = Linux Kernel + Binder + SELinux + Power + Memory control + Verified Boot + GKI/KMI + Vendor driver support
```

Another important line:

```text
GKI separates the generic Android kernel core from vendor hardware-specific drivers using loadable modules and a stable KMI.
```
