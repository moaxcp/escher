#!/bin/sh

# Decrypt the file
mkdir $HOME/secrets || true
# --batch to prevent interactive command --yes to assume "yes" for questions
gpg --quiet --batch --yes --decrypt --passphrase="${ENCRYPTED_SIGNING_PASSPHRASE}" \
--output $HOME/secrets/signingkey.gpg signingkey.gpg.gpg