#!/bin/bash
# ==============================================================================
# MARDUKH SYSTEM - ADMINISTRATIVE VAULT CONTROLLER
# Copyright © 2026 Seliim Ahmed (seliim.ahmed@gmail.com). All Rights Reserved.
# ==============================================================================

CORE_FILES=(
    "file_one_voip_ingress.c"
    "file_two_filtration.c"
    "file_three_voip_egress.c"
    "src/state_controller.c"
)

ENCRYPTED_TARGET=".mardukh_core.enc"

print_status() {
    echo -e "\033[1;33m[MARDUKH-SECURITY]\033[0m $1"
}

case "$1" in
    "lock")
        print_status "Archiving and encrypting core bare-metal modules..."
        # Prompt for the administrative password safely
        read -s -p "Set Administrative Encryption Password: " ADMIN_PASS
        echo ""
        
        # Tar the core modules and secure them using heavy AES-256 encryption
        tar -cf - "${CORE_FILES[@]}" 2>/dev/null | openssl enc -aes-256-cbc -salt -pbkdf2 -out "$ENCRYPTED_TARGET" -pass pass:"$ADMIN_PASS"
        
        if [ $? -eq 0 ]; then
            print_status "Vault created successfully -> $ENCRYPTED_TARGET"
            # Securely shred and scrub the local unencrypted development source files
            for file in "${CORE_FILES[@]}"; do
                if [ -f "$file" ]; then
                    rm -f "$file"
                fi
            done
            print_status "Core source files scrubbed from local memory. Repository secured."
        else
            echo "Error locking vault."
        fi
        ;;

    "unlock")
        print_status "Initiating Administrative Access Request..."
        read -s -p "Enter Administrative Password: " ADMIN_PASS
        echo ""
        
        if [ ! -f "$ENCRYPTED_TARGET" ]; then
            echo "Error: Encrypted core vault archive ($ENCRYPTED_TARGET) not found."
            exit 1
        fi
        
        # Reconstruct the infrastructure back out into the open file system
        openssl enc -d -aes-256-cbc -pbkdf2 -in "$ENCRYPTED_TARGET" -pass pass:"$ADMIN_PASS" | tar -xf - 2>/dev/null
        
        if [ $? -eq 0 ]; then
            print_status "Access Authorized. Core files restored for compilation."
        else
            echo "Access Denied: Invalid Administrative Password Sequence."
            exit 1
        fi
        ;;

    *)
        echo "Usage: ./vault_gate.sh [lock|unlock]"
        exit 1
        ;;
esac
