# 🚀 Booster
The networking implementation for MCK

This repository contains some of the basic implementation code for the Booster networking system how-ever it does not contain code for the packets or packet processors because they include too much internal game code that is not yet able to be published

## 🔨 Improvements

- All packets are now immutable so no weird empty constructors and massive files full of assignments and annoying messing around
- Far smaller this implementation when included in MCK is far far smaller than the original networking while still achieving fast performance

## ⚙️ Building

You cannot build this code directly out of the box as its missing some internal components from the client however these can be expected to be published later on as the project is more complete

## 📜 Documentation

I have not yet documented this code expect full documentation to come in future commits when the client is closer to completion