# ==============================================================================
# MARDUKH SYSTEM - ROOT AUTOMATION ENGINE
# Copyright © 2026 Seliim Ahmed (seliim.ahmed@gmail.com). All Rights Reserved.
# ==============================================================================

CC=gcc
CFLAGS=-O3 -march=native -Wall -pthread
TARGET=mardukh_core_coordinator

all: unlock compile lock

# Decrypt the hidden core files via your secret password
unlock:
	@echo "[MARDUKH-BUILD] Unlocking core cryptographic modules..."
	chmod +x vault_gate.sh
	./vault_gate.sh unlock

# Compile the standalone bare-metal filtering layer
compile: main_coordinator.c
	@echo "[MARDUKH-BUILD] Compiling C-layer low-latency socket filter..."
	$(CC) $(CFLAGS) main_coordinator.c -o $(TARGET)
	@echo "[MARDUKH-BUILD] Bare-metal core binary successfully assembled."

# Clean build structures and scrub open raw source arrays to lock security
lock:
	@echo "[MARDUKH-BUILD] Re-encrypting source modules and purging memory..."
	./vault_gate.sh lock

# Clean temporary compilation artifacts
clean:
	@echo "[MARDUKH-BUILD] Clearing localized terminal caches..."
	rm -rf *.o $(TARGET) app/build/ .gradle/
	@echo "[MARDUKH-BUILD] Clean operations complete."
